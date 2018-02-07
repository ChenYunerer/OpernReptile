package com.opern.reptile.model.qupu123;

public class OpernListInfo {
    private String opernName;
    private String opernOriginHtmlUrl;

    public String getOpernName() {
        return opernName;
    }

    public void setOpernName(String opernName) {
        this.opernName = opernName;
    }

    public String getOpernOriginHtmlUrl() {
        return opernOriginHtmlUrl;
    }

    public void setOpernOriginHtmlUrl(String opernOriginHtmlUrl) {
        this.opernOriginHtmlUrl = opernOriginHtmlUrl;
    }

    @Override
    public String toString() {
        return "OpernListInfo{" +
                "opernName='" + opernName + '\'' +
                ", opernOriginHtmlUrl='" + opernOriginHtmlUrl + '\'' +
                '}';
    }
}
