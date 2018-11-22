package com.Util.IOUtil;

import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.*;

import java.io.IOException;

import static com.Util.SparkInit.setSystemProperty;

public class HDFSHelper {
    static Configuration conf = new Configuration();

    //创建新文件
    public static void createFile(String dst, byte[] contents, Configuration conf) throws IOException {
        FileSystem fs = FileSystem.get(conf);
        Path dstPath = new Path(dst); //目标路径
        //打开一个输出流
        FSDataOutputStream outputStream = fs.create(dstPath);
        outputStream.write(contents);
        outputStream.close();
        fs.close();
        System.out.println(dst + "成功！");
    }

    //删除文件
    public static boolean rmFile(String hdfsFile, Configuration conf) throws IOException {
        if (StringUtils.isBlank(hdfsFile)) {
            return false;
        }
        FileSystem hdfs = FileSystem.get(conf);
        Path path = new Path(hdfsFile);
        boolean isDeleted = hdfs.delete(path, true);
        hdfs.close();
        return isDeleted;
    }

    //读取文件的内容
    public static byte[] readFile(String dst, Configuration conf) throws Exception {
        if (StringUtils.isBlank(dst)) {
            return null;
        }
        FileSystem fs = FileSystem.get(conf);
        // check if the file exists
        Path path = new Path(dst);
        if (fs.exists(path)) {
            FSDataInputStream is = fs.open(path);
            // get the file info to create the buffer
            FileStatus stat = fs.getFileStatus(path);
            // create the buffer
            byte[] buffer = new byte[Integer.parseInt(String.valueOf(stat.getLen()))];
            is.readFully(0, buffer);
            is.close();
            fs.close();
            return buffer;
        } else {
            throw new Exception("the file is not found .");
        }
    }

    //上传本地文件
    public static void uploadFileFromLocalToHDFS(String src, String dst, Configuration conf) {
        FileSystem fs = null;
        Path srcPath = new Path(src); //原路径
        Path dstPath = new Path(dst); //目标路径
        try {
            fs = FileSystem.get(conf);
            fs.copyFromLocalFile(false, srcPath, dstPath); //调用文件系统的文件复制函数,前面参数是指是否删除原文件，true为删除，默认为false
            fs.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    //显示HDFS文件夹包含的文件
    public static void showFs(String dst, Configuration conf) {
        FileSystem fs = null;
        Path dstPath = new Path(dst); //目标路径
        try {
            fs = FileSystem.get(conf);
            FileStatus[] fileStatus = fs.listStatus(dstPath);
            for (FileStatus file : fileStatus) {
                System.out.println(file.getPath());
            }
            fs.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //创建新文件夹
    public static void mkDir(String dir, Configuration conf) throws IOException {
        FileSystem fileSystem = FileSystem.get(conf);

        Path path = new Path(dir);
        if (fileSystem.exists(path)) {
            System.out.println("Dir " + dir + " already exists");
            return;
        }

        fileSystem.mkdirs(path);

        fileSystem.close();
    }

    public static void main(String[] args) throws Exception {
        setSystemProperty();
        Configuration conf = new Configuration();
        String srcLinux = "/home/cloud/cloud/haninput/uploadtohdfs.sh";
        String srcWindows = "D:\\experiment\\a.txt";
        String dst = "hdfs://master:9000/haninput/";
        String filename = "/haninput/test100_100.txt";

        byte[] f = readFile(filename, conf);
        System.out.println("数据大小" + f.length);

        uploadFileFromLocalToHDFS(srcWindows, dst, conf);
    }

}
