package net;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import utils.LogUtil;

import java.util.concurrent.TimeUnit;

/**
 * Created by Yun on 2017/7/31 0031.
 * OKHttp网络请求工具类
 */
public class HttpUtil {

    private static OkHttpClient client;

    static {
        client = new OkHttpClient.Builder()
                .sslSocketFactory(TrustAllCerts.createSSLSocketFactory())
                .hostnameVerifier(new TrustAllCerts.TrustAllHostnameVerifier())
                .connectTimeout(180, TimeUnit.SECONDS)
                .readTimeout(180, TimeUnit.SECONDS)
                .writeTimeout(180, TimeUnit.SECONDS)
                //.proxy(new Proxy(Proxy.Type.HTTP,new InetSocketAddress("127.0.0.1", 8888)))
                .build();
    }

    /**
     * 发送GET请求
     *
     * @param url 请求URL
     * @return 请求成功返回response, 否则返回空字符串
     */
    public static String get(String url) {
        try {
            Request request = new Request.Builder().get().url(url).build();
            String responseStr = null;
            Response response = client.newCall(request).execute();
            if (response.isSuccessful()) {
                responseStr = response.body().string();
                response.body().close();
                return responseStr == null ? "" : responseStr;
            } else {
                LogUtil.i("网络请求异常", "code : " + response.code() + " 请求地址: " + url);
                return "";
            }
        } catch (Exception e) {
            e.printStackTrace();
            LogUtil.e("网络请求异常", url);
            return "";
        }
    }

}
