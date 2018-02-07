package com.opern.reptile.model;

public class OpernInfo {
    private int id;  //自增id
    private String originName = "";  //原平台名称 如中国曲谱网
    private String originId = "";  //曲谱在原平台的id
    private String opernName = "";  //曲谱名称
    private String opernWordAuthor = "";  //曲谱词作者
    private String opernSongAuthor = "";  //曲谱曲作者
    private int opernViews;  //曲谱在原平台上的浏览量
    private String opernUploader = "";  //曲谱在原平台的上传者
    private String opernOriginHtmlUrl = "";  //曲谱在原平台的访问地址全路径
    private int opernPicNum;  //曲谱图片数量
    private String opernFirstPicUrl = "";  //曲谱首张曲谱图片地址
    private int opernFormat;  //曲谱格式 1简谱 2五线谱 3简线混排 4其它
    private String opernCategoryOne = "";  //一级类目
    private String opernCategoryTwo = "";  //二级类目
    private String opernUploadTime = "";  //曲谱在原平台的上传时间
    private String addTime;  //记录添加时间

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getOriginName() {
        return originName;
    }

    public void setOriginName(String originName) {
        this.originName = originName;
    }

    public String getOriginId() {
        return originId;
    }

    public void setOriginId(String originId) {
        this.originId = originId;
    }

    public String getOpernName() {
        return opernName;
    }

    public void setOpernName(String opernName) {
        this.opernName = opernName;
    }

    public String getOpernWordAuthor() {
        return opernWordAuthor;
    }

    public void setOpernWordAuthor(String opernWordAuthor) {
        this.opernWordAuthor = opernWordAuthor;
    }

    public String getOpernSongAuthor() {
        return opernSongAuthor;
    }

    public void setOpernSongAuthor(String opernSongAuthor) {
        this.opernSongAuthor = opernSongAuthor;
    }

    public int getOpernViews() {
        return opernViews;
    }

    public void setOpernViews(int opernViews) {
        this.opernViews = opernViews;
    }

    public String getOpernUploader() {
        return opernUploader;
    }

    public void setOpernUploader(String opernUploader) {
        this.opernUploader = opernUploader;
    }

    public String getOpernOriginHtmlUrl() {
        return opernOriginHtmlUrl;
    }

    public void setOpernOriginHtmlUrl(String opernOriginHtmlUrl) {
        this.opernOriginHtmlUrl = opernOriginHtmlUrl;
    }

    public int getOpernPicNum() {
        return opernPicNum;
    }

    public void setOpernPicNum(int opernPicNum) {
        this.opernPicNum = opernPicNum;
    }

    public String getOpernFirstPicUrl() {
        return opernFirstPicUrl;
    }

    public void setOpernFitstPicUrl(String opernFirstPicUrl) {
        this.opernFirstPicUrl = opernFirstPicUrl;
    }

    public String getOpernCategoryOne() {
        return opernCategoryOne;
    }

    public void setOpernCategoryOne(String opernCategoryOne) {
        this.opernCategoryOne = opernCategoryOne;
    }

    public String getOpernCategoryTwo() {
        return opernCategoryTwo;
    }

    public void setOpernCategoryTwo(String opernCategoryTwo) {
        this.opernCategoryTwo = opernCategoryTwo;
    }

    public String getOpernUploadTime() {
        return opernUploadTime;
    }

    public void setOpernUploadTime(String opernUploadTime) {
        this.opernUploadTime = opernUploadTime;
    }

    public String getAddTime() {
        return addTime;
    }

    public void setAddTime(String addTime) {
        this.addTime = addTime;
    }

    public void setOpernFirstPicUrl(String opernFirstPicUrl) {
        this.opernFirstPicUrl = opernFirstPicUrl;
    }

    public int getOpernFormat() {
        return opernFormat;
    }

    public void setOpernFormat(int opernFormat) {
        this.opernFormat = opernFormat;
    }

    @Override
    public String toString() {
        return "OpernInfo{" +
                "id=" + id +
                ", originName='" + originName + '\'' +
                ", originId='" + originId + '\'' +
                ", opernName='" + opernName + '\'' +
                ", opernWordAuthor='" + opernWordAuthor + '\'' +
                ", opernSongAuthor='" + opernSongAuthor + '\'' +
                ", opernViews=" + opernViews +
                ", opernUploader='" + opernUploader + '\'' +
                ", opernOriginHtmlUrl='" + opernOriginHtmlUrl + '\'' +
                ", opernPicNum=" + opernPicNum +
                ", opernFirstPicUrl='" + opernFirstPicUrl + '\'' +
                ", opernFormat=" + opernFormat +
                ", opernCategoryOne='" + opernCategoryOne + '\'' +
                ", opernCategoryTwo='" + opernCategoryTwo + '\'' +
                ", opernUploadTime='" + opernUploadTime + '\'' +
                ", addTime='" + addTime + '\'' +
                '}';
    }
}
