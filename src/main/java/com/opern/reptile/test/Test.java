package com.opern.reptile.test;

import com.opern.reptile.model.OpernPicInfo;
import com.opern.reptile.net.HttpUtil;
import com.opern.reptile.qupu123.Config;
import com.opern.reptile.utils.LogUtil;
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

public class Test {
    private static ScriptEngineManager manager = new ScriptEngineManager();
    private static ScriptEngine engine = manager.getEngineByName("javascript");
    private static Invocable inv;
    private static ScriptEngineManager scriptEngineManager = new ScriptEngineManager();
    private static ScriptEngine scriptEngine = scriptEngineManager.getEngineByName("javascript");

    /**
     * 初始化JS
     */
    private static void initJS() throws Exception {
        engine.eval(Config.commonjs);
        inv = (Invocable) engine;
    }

    public static void main(String[] args) throws Exception {
        initJS();
        String mobileHtml = HttpUtil.get(Config.QUPU123_MOBILE_URL + "311211" + ".html");
        if (mobileHtml.equals("")) {
            return;
        }
        if (mobileHtml.contains("此曲谱不存在或已被删除")) {
            return;
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
                    String k0_p = (String) scriptEngine.get(k0);
                    String imgUrl = (String) inv.invokeFunction("showopern", k0_p, k1);
                    imgUrl = imgUrl.startsWith("/") ? imgUrl.substring(1) : imgUrl;
                    imgs.add(Config.QUPU123_BASE_URL + imgUrl);
                } catch (Exception e) {

                }
            }
        }
        List<OpernPicInfo> opernPicInfos = new ArrayList<>();
        for (int i = 1; i <= imgs.size(); i++) {
            OpernPicInfo opernPicInfo = new OpernPicInfo();
            opernPicInfo.setOpernId(123);
            opernPicInfo.setOpernPicIndex(i);
            opernPicInfo.setOpernPicUrl(imgs.get(i - 1));
            opernPicInfos.add(opernPicInfo);
            LogUtil.i(opernPicInfo.toString());
        }
        //opernPicInfoList.addAll(opernPicInfos);
    }
}

