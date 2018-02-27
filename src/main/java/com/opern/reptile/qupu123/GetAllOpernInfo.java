package com.opern.reptile.qupu123;

import com.opern.reptile.dao.OpernDao;
import com.opern.reptile.db.MyBatis;
import com.opern.reptile.model.OpernInfo;
import com.opern.reptile.model.qupu123.OpernListInfo;
import com.opern.reptile.net.HttpUtil;
import com.opern.reptile.utils.FileUtil;
import com.opern.reptile.utils.LogUtil;
import com.opern.reptile.utils.MultiThreadUtil;
import com.opern.reptile.utils.StringUtil;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * 获取曲谱信息
 */
public class GetAllOpernInfo {

    public static void main(String[] strings) {
        List<OpernListInfo> list = getOpernListInfoFromFile(Config.TEMP_TILE_PATH, Config.OPERN_LIST_TEMP_FILE_NAME);

        MultiThreadUtil<OpernListInfo, OpernInfo> multiThreadUtil = new MultiThreadUtil<>(new Worker(), resultList -> {
            System.out.println(resultList.size());
            writeData2File(resultList, Config.TEMP_TILE_PATH, Config.OPERN_INFO_TEMP_FILE_NAME);
            saveToDB(resultList);
        }, list);
        multiThreadUtil.start();
    }

    private static void saveToDB(List<OpernInfo> list) {
        int dataSize = 10000;
        SqlSession sqlSession = MyBatis.getSqlSessionFactory().openSession(ExecutorType.BATCH);
        OpernDao dao = sqlSession.getMapper(OpernDao.class);
        List<OpernInfo> subList = list.subList(0, dataSize);
        try {
            dao.insertOpernInfos(subList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        sqlSession.commit();
        sqlSession.close();
        if (subList.size() == dataSize) {
            list = list.subList(dataSize, list.size());
            saveToDB(list);
        }
    }

    public static class Worker implements MultiThreadUtil.Worker<OpernListInfo, OpernInfo> {

        @Override
        public List<OpernInfo> work(List<OpernListInfo> list) {
            ArrayList<OpernInfo> opernInfoArrayList = new ArrayList<>();
            int a = 0;
            for (OpernListInfo info : list) {
                LogUtil.i(Thread.currentThread().getName() + " " + a++ + " " + info.getOpernOriginHtmlUrl());
                OpernInfo opernInfo = new OpernInfo();
                opernInfo.setOriginName(Config.ORIGIN_NAME);
                opernInfo.setOpernName(info.getOpernName());
                opernInfo.setOpernOriginHtmlUrl(info.getOpernOriginHtmlUrl());
                String html = "";
                try {
                    html = HttpUtil.get(info.getOpernOriginHtmlUrl());
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtil.e("error", info.getOpernOriginHtmlUrl());
                }
                if (html.contains("页面错误！请稍后再试")) {
                    continue;
                }
                Document document = Jsoup.parse(html.replace("&nbsp;", ""));
                String parameter;
                try {
                    parameter = document.getElementById("look_all").attr("onclick");
                } catch (Exception e) {
                    continue;
                }
                int index1 = parameter.indexOf("'");
                int index2 = parameter.indexOf("'", index1 + 1);

                int id = Integer.parseInt(parameter.substring(index1 + 1, index2));
                opernInfo.setOriginId(String.valueOf(id));
                String title = document.getElementsByClass("content_head").get(0).child(0).text();
                opernInfo.setOpernName(title);
                Elements headInfo = document.getElementsByClass("content_head").get(0).getElementsByClass("info").get(0).getElementsByTag("span");
                int[] indexs = new int[headInfo.size() - 1];
                for (int i = 0; i < headInfo.size() - 1; i++) {
                    indexs[i] = document.getElementsByClass("content_head").get(0).child(1).text().indexOf(headInfo.get(i).text());
                }
                String[] strs = new String[headInfo.size() - 2];
                for (int i = 0; i < indexs.length - 1; i++) {
                    try {
                        strs[i] = document.getElementsByClass("content_head").get(0).child(1).text().substring(indexs[i] + headInfo.get(i).text().length(), indexs[i + 1]);
                    } catch (Exception e) {
                        strs[i] = "解析错误";
                    }
                }
                for (int i = 0; i < strs.length; i++) {
                    String s = headInfo.get(i).text();
                    if (s.equals("作词：")) {
                        opernInfo.setOpernWordAuthor(strs[i]);
                    } else if (s.equals("作曲：")) {
                        opernInfo.setOpernSongAuthor(strs[i]);
                    } else if (s.equals("演唱(奏)：")) {

                    } else if (s.equals("格式：")) {
                        if (strs[i].contains("简谱")) {
                            opernInfo.setOpernFormat(1);
                        } else if (strs[i].contains("五线谱")) {
                            opernInfo.setOpernFormat(2);
                        } else if (strs[i].contains("简线")) {
                            opernInfo.setOpernFormat(3);
                        } else {
                            opernInfo.setOpernFormat(4);
                        }
                    } else if (s.equals("来源：")) {

                    } else if (s.equals("上传：")) {
                        opernInfo.setOpernUploader(strs[i]);
                    } else if (s.equals("日期：")) {
                        opernInfo.setOpernUploadTime(strs[i]);
                    }
                }
                String viewCountStr = HttpUtil.get(Config.VIEW_COUNT_URL + opernInfo.getOriginId() + ".html");
                if (StringUtil.isNull(viewCountStr)) {
                    viewCountStr = "0";
                }
                int viewCount = Integer.parseInt(viewCountStr);
                opernInfo.setOpernViews(viewCount);

                opernInfoArrayList.add(opernInfo);
                LogUtil.i(opernInfo.toString());
            }
            LogUtil.i(Thread.currentThread().getName() + " over");
            return opernInfoArrayList;
        }
    }

    /**
     * 从文件获取opernListInfo
     */
    private static List<OpernListInfo> getOpernListInfoFromFile(String filePath, String fileName) {
        List<OpernListInfo> opernListInfoList = new ArrayList<>();
        FileReader fileReader;
        try {
            File file = new File(filePath + fileName);
            if (!file.exists()) {
                LogUtil.i("从文件获取opernListInfo", "文件不存在");
                return opernListInfoList;
            }
            fileReader = new FileReader(filePath + fileName);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String lineStr;
            while (StringUtil.isNotNull(lineStr = bufferedReader.readLine())) {
                OpernListInfo opernListInfo = new OpernListInfo();
                String opernName = lineStr.split(Config.SEPARATOR)[0];
                String opernOriginHtmlUrl = lineStr.split(Config.SEPARATOR)[1];
                opernListInfo.setOpernName(opernName);
                opernListInfo.setOpernOriginHtmlUrl(opernOriginHtmlUrl);
                opernListInfoList.add(opernListInfo);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return opernListInfoList;
    }

    /**
     * 将曲谱数据写入文件
     */
    private static void writeData2File(List<OpernInfo> opernInfoList, String path, String fileName) {
        boolean success = FileUtil.createFile(path, fileName);
        if (!success) {
            return;
        }
        try {
            File file = new File(path + fileName);
            FileWriter fileWriter = new FileWriter(file, true);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            opernInfoList.forEach(opernInfo -> printWriter.println(
                    opernInfo.getId() + Config.SEPARATOR
                            + opernInfo.getOpernName() + Config.SEPARATOR
                            + opernInfo.getOriginId() + Config.SEPARATOR
                            + opernInfo.getOpernName() + Config.SEPARATOR
                            + opernInfo.getOpernWordAuthor() + Config.SEPARATOR
                            + opernInfo.getOpernSongAuthor() + Config.SEPARATOR
                            + opernInfo.getOpernViews() + Config.SEPARATOR
                            + opernInfo.getOpernUploader() + Config.SEPARATOR
                            + opernInfo.getOpernOriginHtmlUrl() + Config.SEPARATOR
                            + opernInfo.getOpernPicNum() + Config.SEPARATOR
                            + opernInfo.getOpernFirstPicUrl() + Config.SEPARATOR
                            + opernInfo.getOpernFormat() + Config.SEPARATOR
                            + opernInfo.getOpernCategoryOne() + Config.SEPARATOR
                            + opernInfo.getOpernCategoryTwo() + Config.SEPARATOR
                            + opernInfo.getOpernUploadTime() + Config.SEPARATOR
                            + opernInfo.getAddTime()
                    )
            );
            printWriter.flush();
            fileWriter.flush();
            printWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
