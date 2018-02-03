package utils;

/**
 * Created by Yun on 2017/7/31 0031.
 */
public class LogUtil {

    public static void i(String msg) {
        System.out.println("[INFO:] " + msg);
    }

    public static void i(Object object, String msg) {
        System.out.println("[INFO:] " + object.getClass().getName() + " " + msg);
    }

    public static void i(String tag, String msg) {
        System.out.println("[INFO:] " + tag + "-->" + msg);
    }

    public static void d(String tag, String msg) {
        System.out.println("[DEBUG:] " + tag + "-->" + msg);
    }

    public static void e(String tag, String msg) {
        System.out.println("[ERROR:] " + tag + "-->" + msg);
    }
}
