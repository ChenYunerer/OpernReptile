package com.opern.reptile.zhanmeishi;

import com.opern.reptile.model.zanmeishi.OpernListInfo;
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

/**
 * 获取所有曲谱列表数据
 */
public class GetAllOpernListInfo {
    private static List<OpernListInfo> opernListInfoList = new ArrayList<>();

    public static void main(String[] args) {
        String url = Config.FIRST_PAGE_URL;
        while (StringUtil.isNotNull(url)) {
            String responseStr = HttpUtil.get(url);
            if (StringUtil.isNotNull(responseStr)) {
                Document responseDoc = Jsoup.parse(responseStr);
                String nextPageUrl = getNextPageUrl(responseDoc);
                url = nextPageUrl;
                List<OpernListInfo> opernListInfos = getOpernListInfo(responseDoc);
                opernListInfoList.addAll(opernListInfos);
            }
        }
        writeData2File(opernListInfoList, Config.TEMP_TILE_PATH, Config.OPERN_LIST_TEMP_FILE_NAME);
    }

    /**
     * 获取下一页访问地址
     *
     * @return 下一页访问地址 如果没有下一页则返回空字符串
     */
    private static String getNextPageUrl(Document document) {
        String nextPageUrl = "";
        Elements pagerEles = document.getElementsByClass("pager");
        if (pagerEles == null || pagerEles.isEmpty()) {
            return nextPageUrl;
        }
        for (int index = 0; index < pagerEles.first().getElementsByTag("a").size(); index++) {
            Element aEle = pagerEles.first().getElementsByTag("a").get(index);
            if (aEle.text().contains("下一页")) {
                String href = aEle.attr("href");
                href = href.startsWith("/") ? href.substring(1) : href;
                nextPageUrl = Config.ZAN_MEI_SHI_BASE_URL + href;
                break;
            }
        }
        return nextPageUrl;
    }

    /**
     * 获取曲谱列表信息
     */
    private static List<OpernListInfo> getOpernListInfo(Document document) {
        List<OpernListInfo> opernListInfoList = new ArrayList<>();
        Elements tabsListEles = document.getElementsByClass("tabslist");
        if (tabsListEles == null || tabsListEles.isEmpty()) {
            return opernListInfoList;
        }
        Elements dlEles = tabsListEles.first().getElementsByTag("dl");
        if (dlEles == null || dlEles.isEmpty()) {
            return opernListInfoList;
        }
        for (int index = 0; index < dlEles.size(); index++) {
            Elements h3Eles = dlEles.get(index).getElementsByTag("h3");
            if (h3Eles != null && !h3Eles.isEmpty()) {
                Elements aEles = h3Eles.first().getElementsByTag("a");
                if (aEles != null && !aEles.isEmpty()) {
                    String opernName = aEles.first().text();
                    String href = aEles.first().attr("href");
                    href = href.startsWith("/") ? href.substring(1) : href;
                    String opernOriginHtmlUrl = Config.ZAN_MEI_SHI_BASE_URL + href;
                    OpernListInfo opernListInfo = new OpernListInfo();
                    opernListInfo.setOpernName(opernName);
                    opernListInfo.setOpernOriginHtmlUrl(opernOriginHtmlUrl);
                    opernListInfoList.add(opernListInfo);
                    LogUtil.i("解析到一条列表数据", opernName + " " + opernOriginHtmlUrl);
                    continue;
                }
            }
        }
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
