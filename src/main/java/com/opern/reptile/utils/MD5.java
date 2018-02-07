package com.opern.reptile.utils;

import java.security.MessageDigest;

public class MD5 {

    public static String getMD5(String str) throws Exception {
        MessageDigest md5 = MessageDigest.getInstance("MD5");
        md5.update((str).getBytes("UTF-8"));
        byte b[] = md5.digest();

        int i;
        StringBuffer buf = new StringBuffer("");

        for (int offset = 0; offset < b.length; offset++) {
            i = b[offset];
            if (i < 0) {
                i += 256;
            }
            if (i < 16) {
                buf.append("0");
            }
            buf.append(Integer.toHexString(i));
        }

        return buf.toString();
    }

}
