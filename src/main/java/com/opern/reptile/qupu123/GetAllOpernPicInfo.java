package com.opern.reptile.qupu123;

import com.opern.reptile.dao.OpernDao;
import com.opern.reptile.db.MyBatis;
import com.opern.reptile.model.OpernInfo;
import com.opern.reptile.model.OpernPicInfo;
import com.opern.reptile.net.HttpUtil;
import com.opern.reptile.utils.LogUtil;
import org.apache.ibatis.session.ExecutorType;
import org.apache.ibatis.session.SqlSession;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

/**
 * 获取曲谱图片信息
 */
public class GetAllOpernPicInfo {
    private static ScriptEngineManager manager = new ScriptEngineManager();
    private static ScriptEngine engine = manager.getEngineByName("javascript");
    private static Invocable inv;
    private static ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
    private static ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("javascript");

    private static CountDownLatch countDownLatch;
    private static ArrayList<OpernPicInfo> opernPicInfoList = new ArrayList<>();
    private static ArrayList<OpernPicInfo> opernPicInfoList1 = new ArrayList<>();
    private static ArrayList<OpernPicInfo> opernPicInfoList2 = new ArrayList<>();
    private static ArrayList<OpernPicInfo> opernPicInfoList3 = new ArrayList<>();
    private static ArrayList<OpernPicInfo> opernPicInfoList4 = new ArrayList<>();
    private static ArrayList<OpernPicInfo> opernPicInfoList5 = new ArrayList<>();

    public static void main(String[] args) throws Exception {
        initJS();
        SqlSession sqlSession = MyBatis.getSqlSessionFactory().openSession(ExecutorType.BATCH);
        OpernDao dao = sqlSession.getMapper(OpernDao.class);
        List<OpernInfo> opernInfoList = dao.listOpernInfo();
        sqlSession.close();
        countDownLatch = new CountDownLatch(5);
        new WorkThread(opernInfoList.subList(0, opernInfoList.size() / 5), opernPicInfoList1).start();
        new WorkThread(opernInfoList.subList(opernInfoList.size() / 5, opernInfoList.size() / 5 * 2), opernPicInfoList2).start();
        new WorkThread(opernInfoList.subList(opernInfoList.size() / 5 * 2, opernInfoList.size() / 5 * 3), opernPicInfoList3).start();
        new WorkThread(opernInfoList.subList(opernInfoList.size() / 5 * 3, opernInfoList.size() / 5 * 4), opernPicInfoList4).start();
        new WorkThread(opernInfoList.subList(opernInfoList.size() / 5 * 4, opernInfoList.size()), opernPicInfoList5).start();
        countDownLatch.await();
        opernPicInfoList.addAll(opernPicInfoList1);
        opernPicInfoList.addAll(opernPicInfoList2);
        opernPicInfoList.addAll(opernPicInfoList3);
        opernPicInfoList.addAll(opernPicInfoList4);
        opernPicInfoList.addAll(opernPicInfoList5);
        save2DB(opernPicInfoList);
    }

    private static void save2DB(List<OpernPicInfo> picInfoList) {
        int size = 5000;
        size = picInfoList.size() < size ? picInfoList.size() : size;
        SqlSession sqlSession = MyBatis.getSqlSessionFactory().openSession(ExecutorType.BATCH);
        OpernDao dao = sqlSession.getMapper(OpernDao.class);
        List<OpernPicInfo> subList = picInfoList.subList(0, size);
        try {
            dao.insertOpernPicInfos(subList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        sqlSession.commit();
        sqlSession.close();
        picInfoList.removeAll(subList);
        if (picInfoList.isEmpty()) {
            return;
        } else {
            save2DB(picInfoList);
        }
    }

    /**
     * 初始化JS
     */
    private static void initJS() throws Exception {
        engine.eval(Config.commonjs);
        inv = (Invocable) engine;
    }

    private static class WorkThread extends Thread {
        List<OpernInfo> opernInfoList;
        List<OpernPicInfo> opernPicInfoList;

        public WorkThread(List<OpernInfo> opernInfoList, List<OpernPicInfo> opernPicInfoList) {
            this.opernInfoList = opernInfoList;
            this.opernPicInfoList = opernPicInfoList;
        }

        @Override
        public void run() {
            super.run();
            for (int index = 0; index < opernInfoList.size(); index++) {
                LogUtil.d("GetAllOpernPicInfo", Thread.currentThread().getName() + " " + index + "of" + opernInfoList.size() + " " + opernInfoList.get(index).getOriginId());
                OpernInfo opernInfo = opernInfoList.get(index);
                String mobileHtml = HttpUtil.get(Config.QUPU123_MOBILE_URL + opernInfo.getOriginId() + ".html");
                if (mobileHtml.equals("")) {
                    continue;
                }
                if (mobileHtml.contains("此曲谱不存在或已被删除")) {
                    continue;
                }
                Document mobileDocument = Jsoup.parse(mobileHtml);
                Elements elements = mobileDocument.getElementsByTag("head").get(0).getElementsByTag("script");
                String x = elements.get(elements.size() - 1).toString();
                String js = x.replace("<script>", "");
                js = js.replace("</script>", "");
                try {
                    scriptEngine.eval(js);
                } catch (ScriptException e) {
                    //LogUtil.e("解析JS", "解析失败" + opernInfo.getOpernName() + " " + opernInfo.getOpernOriginHtmlUrl());
                }
                ArrayList<String> imgs = new ArrayList<String>();
                for (Element l : mobileDocument.getElementsByClass("image_list").get(0).children()) {
                    if (l.tagName().equals("a")) {
                        String href = l.attr("href");
                        href = href.startsWith("/") ? href.substring(1) : href;
                        imgs.add(Config.QUPU123_BASE_URL + href);
                    }
                    if (l.tagName().equals("script")) {
                        try {
                            int b = l.toString().indexOf("(");
                            int c = l.toString().indexOf(",");
                            String k0 = l.toString().substring(b + 1, c);
                            int d = l.toString().indexOf("\"");
                            int e = l.toString().indexOf("\"", d + 1);
                            String k1 = l.toString().substring(d + 1, e);
                            String k0_p;
                            String imgUrl;
                            synchronized (GetAllOpernPicInfo.class) {
                                k0_p = (String) scriptEngine.get(k0);
                                imgUrl = (String) inv.invokeFunction("showopern", k0_p, k1);
                            }
                            LogUtil.i("xxx" + imgUrl);
                            imgUrl = imgUrl.startsWith("/") ? imgUrl.substring(1) : imgUrl;
                            imgs.add(Config.QUPU123_BASE_URL + imgUrl);
                        } catch (Exception e) {

                        }
                    }
                }
                List<OpernPicInfo> opernPicInfos = new ArrayList<>();
                for (int i = 1; i <= imgs.size(); i++) {
                    OpernPicInfo opernPicInfo = new OpernPicInfo();
                    opernPicInfo.setOpernId(opernInfo.getId());
                    opernPicInfo.setOpernPicIndex(i);
                    opernPicInfo.setOpernPicUrl(imgs.get(i - 1));
                    opernPicInfos.add(opernPicInfo);
                    LogUtil.i(opernPicInfo.toString());
                }
                opernPicInfoList.addAll(opernPicInfos);
            }
            countDownLatch.countDown();
            LogUtil.d("GetAllOpernPicInfo", Thread.currentThread().getName() + "结束");
        }
    }
}
