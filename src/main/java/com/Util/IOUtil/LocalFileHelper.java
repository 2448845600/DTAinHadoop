package com.Util.IOUtil;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class LocalFileHelper {
    private static Logger logger = Logger.getLogger(LocalFileHelper.class);

    /**
     * 创建文件
     *
     * @param fileName 文件名
     * @param path     路径
     * @return 是否创建成功
     */
    public static boolean createFile(String fileName, String path) {
        Boolean bool = false;
        String url = path + fileName + ".txt";//文件路径+名称+文件类型
        File file = new File(url);
        try {
            //如果文件不存在，则创建新的文件
            if (!file.exists()) {
                file.createNewFile();
                bool = true;
                logger.info("[成功] 已创建文件:" + url);
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("[失败] 创建文件失败");
        }
        return bool;
    }

    /**
     * 创建文件
     *
     * @param url 文件地址
     * @return
     */
    public static boolean createFile(String url) {
        Boolean bool = false;
        File file = new File(url);
        try {
            //如果文件不存在，则创建新的文件
            if (!file.exists()) {
                file.createNewFile();
                bool = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
            logger.error("创建文件失败");
        }
        return bool;
    }

    public static boolean writeStringToFile(String s, String file) {
        try {
            FileWriter fw = new FileWriter(file);
            fw.write(s);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    public static void main(String[] args) {
    }
}
