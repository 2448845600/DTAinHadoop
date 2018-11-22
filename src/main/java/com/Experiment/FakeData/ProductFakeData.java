package com.Experiment.FakeData;

import com.DataStructure.Model.Point;
import org.apache.commons.lang3.RandomUtils;
import org.apache.hadoop.conf.Configuration;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.*;
import java.util.Date;

import static com.StartUpRemote.setFileName;
import static com.Util.IOUtil.HDFSHelper.showFs;
import static com.Util.IOUtil.HDFSHelper.uploadFileFromLocalToHDFS;
import static com.Util.IOUtil.LocalFileHelper.createFile;

public class ProductFakeData {
    // 生成假数据文件
    // 假数据格式：
    // curveNum : point_1_x point_1_y point_2_x point_2_y ......
    // long:double double double double
    // 1072341:100.100 63.001 102.122 64.017 104.102 64.012 105.861 64.871

    private static Logger logger = Logger.getLogger(ProductFakeData.class);

    public static double randomDouble(double min, double max) {
        return RandomUtils.nextDouble(min, max);
    }

    public static Point createFirstPoint() {
        double min = 0.000;
        double max = 180.000;
        double x = randomDouble(min, max);
        double y = randomDouble(min, max);
        return new Point(x, y);
    }

    /**
     * @param curveCount
     * @param pointCount
     * @param path
     * @return 文件绝对路径
     * @throws IOException
     */
    public static String productFakeData(int curveCount, int pointCount, String path) throws IOException {
        String filename = "test_" + curveCount + "_" + pointCount + ".txt";
        String url = path + filename;//文件路径+名称+文件类型
        createFile(url);

        FileWriter fw = new FileWriter(url);
        for (int i = 0; i < curveCount; i++) {
            FakeData fk = new FakeData();
            Point p = createFirstPoint();
            double x = p.getX();
            double y = p.getY();
            for (int j = 0; j < pointCount; j++) {
                x = x + randomDouble(0.000, 0.010);
                y = y + randomDouble(0.000, 0.010);
                if (x >= 180) x = x - 180;
                if (y >= 180) y = y - 180;
                fk.xys.add(x);
                fk.xys.add(y);
            }
            fk.curveNo = i;
            fw.write(fk.toString());
        }
        fw.close();
        return filename;
    }

    public static String productFakeDataQuick(int temp, int curveNumbegin, int curveCount, int pointCount, String path) throws IOException {
        double delt = 0.00001;
        double deltx = 0.005;//0.005 * 10000 < 180
        double delty = 0.0025;//0.0025 * 10000 < 90
        String filename = setFileName(curveCount, pointCount, temp);
        String url = path + filename;//文件路径+名称+文件类型
        createFile(url);

        FileWriter fw = new FileWriter(url);
        for (int i = curveNumbegin; i < curveCount + curveNumbegin; i++) {
            String s = i + ":";
            double x = i * delt;
            double y = i * delt;
            for (int j = 0; j < pointCount; j++) {
                x = x + deltx;
                y = y + delty;
                s += x + " " + y + " ";
            }
            fw.write(s);
        }
        fw.close();
        return filename;
    }

    public static String productFakeDataQuick2(int temp, int curveNumbegin, int curveCount, int pointCount, String path) {

        double delt = 0.00001;
        double deltx = 0.005;//0.005 * 10000 < 180
        double delty = 0.0025;//0.0025 * 10000 < 90
        String filename = setFileName(curveCount, pointCount, temp);
        String url = path + filename;//文件路径+名称+文件类型
        createFile(url);

        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(url), 4194304);
            for (int i = curveNumbegin; i < curveCount + curveNumbegin; i++) {

                Date ST = new Date();
                String s = i + ":";
                double x = i * delt;
                double y = i * delt;
                for (int j = 0; j < pointCount; j++) {
                    x = x + deltx;
                    y = y + delty;
                    s += x + " " + y + " ";
                }
                Date ET = new Date();
                System.out.println("生成一条数据的时间：" + (ET.getTime() - ST.getTime()));

                Date ST2 = new Date();
                bufferedWriter.write(s);
                Date ET2 = new Date();
                System.out.println("一条数据写入缓存的时间：" + (ET2.getTime() - ST2.getTime()));

            }
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return filename;
    }

    public static String productFakeDataQuick3(int temp, int curveNumbegin, int curveCount, int pointCount, String path) {
        int pice = 10;
        int piceNum = pointCount / pice;

        String filename = setFileName(curveCount, pointCount, temp);
        String url = path + filename;//文件路径+名称+文件类型
        createFile(url);

        try {
            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(url), 4194304);
            for (int i = curveNumbegin; i < curveCount + curveNumbegin; i++) {
                String s = i + ":";
                int x1 = (((i * 131 + 19) % 203) * 131) % (180 - pice - 2); // 经度的整数部分， 保证 x2 + pice < 180
                int x2 = (i * 131 + 19) % 100; // 经度的小数部分，保证 x2 + 10000 < INT_MAX

                int y1 = (((i * 19 + 131) % 107) * 131) % (90 - pice - 2);
                int y2 = (i * 19 + 131) % 100;

                for (int j = 0; j < pice; j++) {
                    x1++;
                    y1++;
                    for (int k = 0; k < piceNum; k++) {
                        x2++;
                        y2++;
                        s += x1 + "." + x2 + " " + y1 + "." + y2 + " ";
                    }
                }
                s += "\n";
                bufferedWriter.write(s);
            }
            bufferedWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return filename;
    }

    //生成数据，存储在本地，返回运行时间
    public static long getLocalData(String localSrcPath, int curveNum, int pointNum, int tempNum) {
        Date StartTime = new Date();
        for (int i = 0; i < tempNum; i++) {
            productFakeDataQuick3(i, i * curveNum, curveNum, pointNum, localSrcPath);
            System.out.println((i + 1) + "/" + tempNum);
        }
        Date EndTime = new Date();
        return EndTime.getTime() - StartTime.getTime();
    }

    //将本地数据上传到服务器端HDFS
    public static long dataFromLocalToHDFS(String srcPath, int curveNum, int pointNum, int tempNum, String dstPath) {
        Date StartTime = new Date();
        Configuration conf = new Configuration();
        String filename = null;
        for (int i = 0; i < tempNum; i++) {
            filename = srcPath + setFileName(curveNum, pointNum, i);
            logger.info("上传文件" + filename + "到HDFS");
            uploadFileFromLocalToHDFS(filename, dstPath, conf);
        }
        showFs(dstPath, conf);
        Date EndTime = new Date();
        return EndTime.getTime() - StartTime.getTime();
    }

    public static void main(String[] args) {
        PropertyConfigurator.configure("D:\\DTAinHadoop\\src\\log4j.properties");
        String path = "D:\\experiment\\";
        int curveCount = 600; // 每个文件生产line行
        int pointCount = 10000;
        int count = 200;
        for (int i = 7; i < count + 7; i++) {
            Date start = new Date();
            productFakeDataQuick3(i, i * curveCount, curveCount, pointCount, path);
            Date end = new Date();
            System.out.print("耗时：" + (end.getTime() - start.getTime()) + "\n");
        }
    }
}
