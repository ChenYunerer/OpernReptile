package com.opern.reptile.model;

public class OpernPicInfo {
    private int opernId;
    private int opernPicIndex;
    private String opernPicUrl;

    public int getOpernId() {
        return opernId;
    }

    public void setOpernId(int opernId) {
        this.opernId = opernId;
    }

    public int getOpernPicIndex() {
        return opernPicIndex;
    }

    public void setOpernPicIndex(int opernPicIndex) {
        this.opernPicIndex = opernPicIndex;
    }

    public String getOpernPicUrl() {
        return opernPicUrl;
    }

    public void setOpernPicUrl(String opernPicUrl) {
        this.opernPicUrl = opernPicUrl;
    }

    @Override
    public String toString() {
        return "OpernPicInfo{" +
                "opernId=" + opernId +
                ", opernPicIndex=" + opernPicIndex +
                ", opernPicUrl='" + opernPicUrl + '\'' +
                '}';
    }
}
