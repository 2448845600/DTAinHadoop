package com;

import com.DataStructure.Model.CurveInfo;
import org.apache.log4j.Logger;
import scala.tools.cmd.gen.AnyVals;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static com.DataPartition.DivisionByJava.divideFromFileToHBase;
import static com.DataPartition.HBaseOperator.resetTable;
import static com.DistributeTopologicalAnalysis.GetAllIntersectReport.getAllIntersectReport;
import static com.DistributeTopologicalAnalysis.GetIntersectionByCurves.getIntersectionByCurves;
import static com.DistributeTopologicalAnalysis.OriginalDTA.originalDTA;
import static com.Experiment.IntersectionTest.getCurveFromFile;

public class StartUpRemote {
    private static Logger logger = Logger.getLogger(StartUpRemote.class);

    public static String localResult = "res.txt";
    public static String hdfsSrcPath = "hdfs://master:9000/haninput/test/";
    public static String hdfsDstPath = "hdfs://master:9000/hanoutput/";

    // (600,10000) = 100MB; (600, 10000, 10) = 1G;
    // (600, 10000, 100) = 10G; (600, 10000, 200) = 20G
    static int curveNum;
    static int pointNum;
    static int fileTempStart;
    static int fileTempEnd;
    static int tempNum;
    static int level;
    static int ReportLevel;
    static String curveAnalysisFile;
    static String srcfileRE;
    static int dataSize;
    static String dataSizeUnit = "MB";

    public static void main(String[] args) {
        if (args.length > 0) {
            curveNum = Integer.parseInt(args[0]);
            pointNum = Integer.parseInt(args[1]);
            fileTempStart = Integer.parseInt(args[2]);
            fileTempEnd = Integer.parseInt(args[3]);
            level = Integer.parseInt(args[4]);
            ReportLevel = Integer.parseInt(args[5]);
            curveAnalysisFile = args[6];
            srcfileRE = args[7];
        } else {
            curveNum = 600;
            pointNum = 10000;
            fileTempStart = 0;
            fileTempEnd = 2;
            level = 28;
            ReportLevel = 24;//数据量100MB，level = 28划分后1517条数据的情况下，reportLevel的参数设置在22-26之间都还不错
            curveAnalysisFile = "/home/cloud/cloud/haninput/data/curveAnalysisFile.txt";
            //curveAnalysisFile = "D:\\experiment\\test_4_100_0.txt";
            //srcfileRE = "hdfs://master:9000/haninput/test/test_20_200_[0].txt";
            srcfileRE = "hdfs://master:9000/haninput/test/test_600_10000_[0-9].txt";
        }
        dataSize = (pointNum * 2 * 8 * curveNum * (fileTempEnd - fileTempStart)) / (1024 * 1024);
        tempNum = fileTempEnd - fileTempStart;

        //resetHBaseTable(false);
        //divideFileFromHDFSToHBase(hdfsSrcPath, curveNum, pointNum, fileTempStart, fileTempEnd, hdfsDstPath, level);
        report(ReportLevel);
        curveAnalysisTest(curveAnalysisFile, level);
        originDTATest(curveAnalysisFile, srcfileRE);
    }

    public static String setFileName(int curveNum, int pointNum, int tempNum) {
        return "test_" + curveNum + "_" + pointNum + "_" + tempNum + ".txt";
    }

    public static boolean writeResultToFile(long time, String job, String info) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 EEE HH:mm:ss");
        String dt = sdf.format(date);

        String s = "[时间]" + dt + "\n[任务]" + job + "\n[耗时]" + time + "ms\n" +
                "[参数]" + "曲线数量:" + curveNum + ", 点个数:" + pointNum + ", 文件数量:" + tempNum + "\n" +
                (info != "" ? info : "") +
                "---------------------------------------------------------------------------------\n";
        try {
            FileWriter fw = new FileWriter(localResult, true);
            fw.write(s);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return true;
    }

    public static void resetHBaseTable(boolean isResetTable) {
        if (isResetTable == true) {
            resetTable(Globals.tbl);
            resetTable(Globals.tblCurve);
            logger.info("HBase已清空");
        }
    }

    //将HDFS中的数据划分，送到HBase
    public static long divideFileFromHDFSToHBase(String srcPath, int curveNum, int pointNum, int fileTempStart, int fileTempEnd, String dstPath, int level) {
        Date ST = new Date();
        String info = "[数据总量]" + dataSize + dataSizeUnit + ",写入文件" + localResult + "\n";
        logger.info("开始划分操作,共有" + (fileTempEnd - fileTempStart) + "个文件");
        for (int i = fileTempStart; i < fileTempEnd; i++) {
            String inputFile = srcPath + setFileName(curveNum, pointNum, i);
            String outputFolder = dstPath + "division" + i;
            logger.debug("划分文件" + inputFile);
            divideFromFileToHBase(inputFile, outputFolder, level);
        }
        logger.info("结束划分操作");
        Date ET = new Date();
        info += "[具体时间]" + (ET.getTime() - ST.getTime()) + "ms\n";
        writeResultToFile(ET.getTime() - ST.getTime(), "数据划分", info);
        return ET.getTime() - ST.getTime();
    }

    //报表操作
    public static long report(int level) {
        Date ST = new Date();
        logger.info("开始报表");
        String res = getAllIntersectReport(level);
        Date ET = new Date();
        logger.info("结束报表");

        writeResultToFile(ET.getTime() - ST.getTime(), "报表操作", res);
        return ET.getTime() - ST.getTime();
    }

    // 1 10 50 100 条曲线
    public static void curveAnalysisTest(String url, int level) {
        List<CurveInfo> curves = getCurveFromFile(url);

        //calculate intersection between 1 curve/10 curves/50 curves/100curves and data from HBase
        for (int i : new int[]{1, 10, 20, 50, 100, 200}) {
            List<CurveInfo> curvesTest = curves.subList(0, i);

            Date ST = new Date();
            String s = getIntersectionByCurves(curvesTest, level, Globals.ns, Globals.tbl);
            Date ET = new Date();

            String info = "[操作详情]计算" + i + "条曲线与数据库的交点，" + "\n" + s;
            writeResultToFile(ET.getTime() - ST.getTime(), "曲线分析", info);
        }
    }

    public static void originDTATest(String testfile, String srcfileRE) {
        Date ST = new Date();
        String timeInfo = originalDTA(testfile, srcfileRE);
        Date ET = new Date();
        writeResultToFile(ET.getTime() - ST.getTime(), "原始拓扑算法", timeInfo);
    }

}
