package utils;

public class StringUtil {

    /**
     * 判断string是否为空
     *
     * @param string 待判断字符串
     * @return true 为空 false 不为空
     */
    public static boolean isNull(String string) {
        if (string == null || string.isEmpty() || string.toLowerCase().equals("null")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * 判断string是否不为空
     *
     * @param string 待判断字符串
     * @return false 为空 true 不为空
     */
    public static boolean isNotNull(String string) {
        return !isNull(string);
    }
}
