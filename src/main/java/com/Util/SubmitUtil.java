package com.Util;

import com.DataStructure.Model.CurveInfo;
import com.DataStructure.Model.IntersectionPoint;
import com.DataStructure.Model.Point;
import com.DataStructure.Model.PointSet;
import org.apache.log4j.Logger;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.DistributeTopologicalAnalysis.IntersectionHelper.getPreciseIntersectionPointWithoutContactPoint;

public class SubmitUtil {
    private static Logger logger = Logger.getLogger(SubmitUtil.class);
    public static String localResult = "D:\\experiment\\localResult.txt";
    public static String ReportResultFile = "D:\\experiment\\report.txt";

    // 一条记录 pointNum * 2 * 8 B
    // curveNum
    public static int curveNum = 500;
    public static int pointNum = 1000;
    public static int tempNum = 5;
    public static int dataSize = (int) (pointNum * 2 * 8 * curveNum * tempNum) / (1024 * 1024); // 单位 MB
    public static String dataSizeUnit = "MB";

    public static boolean writeResultToFile(long time, String job, String info) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 EEE HH:mm:ss");
        String dt = sdf.format(date);
        String s = "[时间]" + dt + "\n[任务]" + job + "\n[耗时]" + time + "ms\n" +
                "[参数]" + "<曲线数量," + curveNum + ">" + ", <点个数," + pointNum + ">" + ", <文件数量," + tempNum + ">\n" +
                "[信息]" + info +
                "\n-------------------------------------------------------------\n";
        try {
            FileWriter fw = new FileWriter(localResult, true);

            fw.write(s);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    public static boolean writeResultToFile(long time, String job) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 EEE HH:mm:ss");
        String dt = sdf.format(date);
        String info = "[时间]" + dt + "\n[任务]" + job + "\n[耗时]" + time + "ms\n" +
                "[参数]" + "<曲线数量," + curveNum + ">" + ", <点个数," + pointNum + ">" + ", <文件数量," + tempNum + ">" +
                "\n-------------------------------------------------------------\n";
        try {
            FileWriter fw = new FileWriter(localResult, true);

            fw.write(info);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    public static CurveInfo strToCurveInfo(String str) {
        CurveInfo curveInfo = new CurveInfo();
        String[] temp = str.split(":");
        curveInfo.setCurveNum(Long.parseLong(temp[0]));
        String[] t1 = temp[1].split(" ");
        for (int i = 0; i < t1.length - 1; i += 2) {
            double x = Double.parseDouble(t1[i]);
            double y = Double.parseDouble(t1[i + 1]);
            Point p = new Point(x, y);
            curveInfo.getPoints().add(p);
        }
        return curveInfo;
    }

    public static List<CurveInfo> loadData(String fileName) {
        List<CurveInfo> curves = new ArrayList<>();

        logger.info("读取文件" + fileName);
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String str = null;
            while ((str = reader.readLine()) != null) {
                CurveInfo curve = strToCurveInfo(str);
                logger.debug(curve.toString());
                curves.add(curve);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return curves;
    }

    public static List<CurveInfo> loadData(List<String> fileNames) {
        List<CurveInfo> curves = new ArrayList<>();
        for (int i = 0; i < fileNames.size(); i++) {
            logger.info("读取文件" + fileNames.get(i));
            File file = new File(fileNames.get(i));
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(file));
                String str = null;
                while ((str = reader.readLine()) != null) {
                    CurveInfo curve = strToCurveInfo(str);
                    logger.debug(curve.toString());
                    curves.add(curve);
                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return curves;
    }

    public static List<IntersectionPoint> getIntersection(CurveInfo curves1, CurveInfo curve2) {
        PointSet ps1 = new PointSet();
        PointSet ps2 = new PointSet();
        ps1.setPoints(curves1.getPoints());
        ps1.setInheritNum(curves1.getCurveNum());
        ps2.setPoints(curve2.getPoints());
        ps2.setInheritNum(curve2.getCurveNum());

        List<IntersectionPoint> resIntersectionPoint = getPreciseIntersectionPointWithoutContactPoint(ps1, ps2);
        return resIntersectionPoint;
    }

    public static List<IntersectionPoint> getReport() {
        logger.info("start report");

        List<CurveInfo> allCurves = loadAllData();

        Date ST = new Date();
        logger.info("calculate  Intersection-Point");
        List<IntersectionPoint> res = new ArrayList<>();
        for (int i = 0; i < allCurves.size(); i++) {
            for (int j = 0; j < allCurves.size(); j++) {
                res.addAll(getIntersection(allCurves.get(i), allCurves.get(j)));
            }
        }

        //为了公平公正，结果写入文件
        logger.info("write result to file");
        try {
            int size = 1024 * 1024 * 4; // 4M
            BufferedWriter bw = new BufferedWriter(new FileWriter(ReportResultFile), size);
            bw.write(ST.getTime() + "\n");
            for (int i = 0; i < res.size(); i++) {
                bw.write(res.get(i).toString());
            }
            bw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //统计运行时间
        Date ET = new Date();
        String info = "数据总量" + dataSize + dataSizeUnit + "，得到交点" + res.size() + "个,写入文件" + ReportResultFile;
        writeResultToFile(ET.getTime() - ST.getTime(), "全局报表", info);

        logger.info(info + ", end report");
        return res;
    }

    public static List<IntersectionPoint> getIntersectionByCurve(List<CurveInfo> allCurves, List<CurveInfo> curves) {
        List<IntersectionPoint> res = new ArrayList<>();
        for (int i = 0; i < curves.size(); i++) {
            for (int j = 0; j < allCurves.size(); j++) {
                res.addAll(getIntersection(curves.get(i), allCurves.get(j)));
            }
        }
        return res;
    }

    public static List<CurveInfo> loadAllData() {
        logger.info("load all data");
        List<CurveInfo> allCurves = new ArrayList<>();
        List<String> fileNames = new ArrayList<>();
        String path = "D:\\experiment" + "\\test_" + curveNum + "_" + pointNum + "_";
        for (int i = 0; i < tempNum; i++) {
            String filename = path + i + ".txt";
            fileNames.add(filename);
        }
        allCurves = loadData(fileNames);
        logger.info("load data successfully, data size " + dataSize + dataSizeUnit);
        return allCurves;
    }

    public static void main(String[] args) {
        //从文件读取全局信息
        //List<CurveInfo> allCurves = loadAllData();
        //logger.info("allCurves.size()=" + allCurves.size());

        //1 报表，暴力循环
        //2 线段交点查询
        getReport();

        //写入文件
    }
}

