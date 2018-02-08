package com.opern.reptile.qupu123;

import com.opern.reptile.model.qupu123.OpernListInfo;
import com.opern.reptile.net.HttpUtil;
import com.opern.reptile.utils.FileUtil;
import com.opern.reptile.utils.LogUtil;
import com.opern.reptile.utils.StringUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 获取所有曲谱列表数据
 */
public class GetAllOpernListInfo {
    private static CountDownLatch countDownLatch = new CountDownLatch(3);
    private static String[] opernCategorys1 = new String[]{"yuanchuang", "jipu", "xiqu"};
    private static String[] opernCategorys2 = new String[]{"minge", "tongsu", "meisheng"};
    private static String[] opernCategorys3 = new String[]{"hechang", "shaoer", "waiguo", "qiyue"};
    /*private static String[] opernCategorys1 = new String[]{"waiguo"};
    private static String[] opernCategorys2 = new String[]{"shaoer"};
    private static String[] opernCategorys3 = new String[]{"hechang"};*/
    private static ArrayList<OpernListInfo> list = new ArrayList<OpernListInfo>();
    private static ArrayList<OpernListInfo> list1 = new ArrayList<OpernListInfo>();
    private static ArrayList<OpernListInfo> list2 = new ArrayList<OpernListInfo>();
    private static ArrayList<OpernListInfo> list3 = new ArrayList<OpernListInfo>();

    public static void main(String[] strings) throws Exception {
        new WorkThread(opernCategorys1, list1).start();
        new WorkThread(opernCategorys2, list2).start();
        new WorkThread(opernCategorys3, list3).start();
        countDownLatch.await();
        list.addAll(list1);
        list.addAll(list2);
        list.addAll(list3);
        writeData2File(list, Config.TEMP_TILE_PATH, Config.OPERN_LIST_TEMP_FILE_NAME);
    }

    public static class WorkThread extends Thread {
        private String[] opernCategorys;
        private ArrayList<OpernListInfo> list;

        public WorkThread(String[] opernCategorys, ArrayList<OpernListInfo> list) {
            this.opernCategorys = opernCategorys;
            this.list = list;
        }

        @Override
        public void run() {
            super.run();
            for (String opernCategory : opernCategorys) {
                String url = Config.QUPU123_BASE_URL + opernCategory;
                while (StringUtil.isNotNull(url)) {
                    String responseStr = HttpUtil.get(url);
                    if (StringUtil.isNotNull(responseStr)) {
                        Document responseDoc = Jsoup.parse(responseStr);
                        String nextPageUrl = getNextPageUrl(responseDoc);
                        url = nextPageUrl;
                        List<OpernListInfo> opernListInfoList = getOpernListInfo(responseDoc);
                        list.addAll(opernListInfoList);
                    } else {
                        url = null;
                    }
                }
            }
            countDownLatch.countDown();
        }
    }

    /**
     * 获取下一页URL
     *
     * @return 下一页URL全路径 没有下一页则为空字符串
     */
    private static String getNextPageUrl(Document document) {
        String nextPageUrl = "";
        if (document.getElementsByClass("pageHtml").isEmpty()) {
            nextPageUrl = "";
        } else {
            for (int index = 0; index < document.getElementsByClass("pageHtml").first().children().size(); index++) {
                Element element = document.getElementsByClass("pageHtml").first().children().get(index);
                if (element.text().equals("下一页")) {
                    String href = element.attr("href");
                    if (StringUtil.isNotNull(href)) {
                        href = href.startsWith("/") ? href.substring(1) : href;
                        nextPageUrl = Config.QUPU123_BASE_URL + href;
                        break;
                    }
                }
            }
        }
        LogUtil.i("解析到下一页URL", nextPageUrl);
        return nextPageUrl;
    }

    /**
     * 获取列表中的曲谱信息
     *
     * @return 列表中的曲谱信息
     */
    private static List<OpernListInfo> getOpernListInfo(Document document) {
        List<OpernListInfo> opernListInfoList = new ArrayList<>();
        Elements opernListEles = document.getElementsByClass("opern_list");
        if (opernListEles == null || opernListEles.isEmpty()) {
            return opernListInfoList;
        }
        Elements tbodyEles = opernListEles.first().getElementsByTag("tbody");
        if (tbodyEles == null || tbodyEles.isEmpty()) {
            return opernListInfoList;
        }
        tbodyEles.first().getElementsByTag("tr").forEach(element -> {
            Elements f1Eles = element.getElementsByClass("f1");
            if (f1Eles != null && !f1Eles.isEmpty()) {
                OpernListInfo opernListInfo = new OpernListInfo();
                String href = f1Eles.first().getElementsByTag("a").first().attr("href");
                href = href.startsWith("/") ? href.substring(1) : href;
                String opernOriginHtmlUrl = Config.QUPU123_BASE_URL + href;
                String opernName = f1Eles.first().getElementsByTag("a").first().text();
                opernListInfo.setOpernName(opernName);
                opernListInfo.setOpernOriginHtmlUrl(opernOriginHtmlUrl);
                opernListInfoList.add(opernListInfo);
                LogUtil.i("解析到一条列表数据", opernName + " " + opernOriginHtmlUrl);
            }
        });
        return opernListInfoList;
    }

    /**
     * 将曲谱列表数据写入文件
     */
    private static void writeData2File(List<OpernListInfo> opernListInfoList, String path, String fileName) {
        boolean success = FileUtil.createFile(path, fileName);
        if (!success) {
            return;
        }
        File file = new File(path + fileName);
        try {
            FileWriter fileWriter = new FileWriter(file, true);
            PrintWriter printWriter = new PrintWriter(fileWriter);
            opernListInfoList.forEach(opernListInfo -> printWriter.println(opernListInfo.getOpernName() + Config.SEPARATOR + opernListInfo.getOpernOriginHtmlUrl()));
            printWriter.flush();
            fileWriter.flush();
            printWriter.close();
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
