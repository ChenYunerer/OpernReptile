package com.opern.reptile.utils;

import java.io.File;
import java.io.IOException;

public class FileUtil {

    /**
     * 创建文件,如果文件存在则覆盖
     *
     * @param path     文件路径
     * @param fileName 文件名称
     * @return 成功与否
     */
    public static boolean createFile(String path, String fileName) {
        File filePath = new File(path);
        if (!filePath.exists()) {
            boolean success = filePath.mkdirs();
            LogUtil.i("创建文件路径", "创建" + (success ? "成功" : "失败"));
        }
        File tempFile = new File(path + fileName);
        if (!tempFile.exists()) {
            try {
                boolean success = tempFile.createNewFile();
                LogUtil.i("创建文件", "创建" + (success ? "成功" : "失败"));
                return success;
            } catch (IOException e) {
                e.printStackTrace();
                return false;
            }
        } else {
            boolean success = tempFile.delete();
            LogUtil.i("删除文件", "创建" + (success ? "成功" : "失败"));
            if (success) {
                return createFile(path, fileName);
            } else {
                return false;
            }
        }
    }

}
