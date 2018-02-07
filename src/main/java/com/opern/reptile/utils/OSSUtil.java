package com.opern.reptile.utils;

import com.aliyun.oss.OSSClient;

import java.io.InputStream;

/**
 * Created by Yun on 2017/7/28 0028.
 * OSS工具类
 */
public class OSSUtil {
    private final String bucketName = "opern";
    private final String endpoint = "http://oss-cn-shanghai.aliyuncs.com";
    private final String accessKeyId = "LTAIWoAN6Iea779u";
    private final String accessKeySecret = "sPWkPYSMK1rRCRtIGW2XDiTqj7Z3RA";
    private OSSClient client;


    public String getUrl(String key) {
        return "https://opern.oss-cn-shanghai.aliyuncs.com/" + key;
    }

    /**
     * oss上传
     *
     * @param key
     * @param inputStream
     */
    public void upLoad(String key, InputStream inputStream) throws Exception {
        client = new OSSClient(endpoint, accessKeyId, accessKeySecret);
        client.putObject(bucketName, key, inputStream);
        client.shutdown();
    }

}
