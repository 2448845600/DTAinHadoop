package com;

import com.DataStructure.Model.CurveInfo;
import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import static com.DataPartition.DivisionByJava.divideFromFileToHBase;
import static com.DataPartition.HBaseOperator.resetTable;
import static com.DistributeTopologicalAnalysis.GetAllIntersectReport.getAllIntersectReport;
import static com.DistributeTopologicalAnalysis.GetIntersectionByCurves.getIntersectionByCurves;
import static com.Experiment.IntersectionTest.getCurveFromFile;

public class StartUp {
    private static Logger logger = Logger.getLogger(StartUp.class);

    public static String localResult = "D:\\experiment\\C.txt";
    public static String hdfsSrcPath = "hdfs://master:9000/haninput/test/";
    public static String hdfsDstPath = "hdfs://master:9000/hanoutput/";
    public static String curveAnalysisFile = "D:\\experiment\\test_10_10000_0.txt";
    public static int curveNum = 20;
    public static int pointNum = 10000;
    public static int tempNum = 1;
    public static int dataSize = (int) (pointNum * 2 * 8 * curveNum * tempNum) / (1024 * 1024); // 单位 MB
    public static String dataSizeUnit = "MB";

    public static void main(String[] args) {
        PropertyConfigurator.configure("D:\\DTAinHadoop\\src\\log4j.properties");

        int curveNum = 20;
        int pointNum = 10000;
        int fileTempStart = 0;
        int fileTempEnd = 1;
        int DivisionLevel = 28; //可以将10000个点的curve划分位80左右个pointSets
        int ReportLevel = 26;//数据量100MB，level = 28划分后1517条数据的情况下，reportLevel的参数设置在22-26之间都还不错

        //resetHBaseTable(true);
        //divideFileFromHDFSToHBase(hdfsSrcPath, curveNum, pointNum, fileTempStart, fileTempEnd, hdfsDstPath, DivisionLevel);


        //report(ReportLevel);

        curveAnalysisTest(curveAnalysisFile, DivisionLevel);
    }

    public static String setFileName(int curveNum, int pointNum, int tempNum) {
        return "test_" + curveNum + "_" + pointNum + "_" + tempNum + ".txt";
    }

    public static boolean writeResultToFile(long time, String job, String info) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy年MM月dd日 EEE HH:mm:ss");
        String dt = sdf.format(date);

        String s = "[时间]" + dt + "\n[任务]" + job + "\n[耗时]" + time + "ms\n" +
                "[参数]" + "<曲线数量," + curveNum + ">" + ", <点个数," + pointNum + ">" + ", <文件数量," + tempNum + ">\n" +
                "[信息]" + (info != "" ? info : "无") +
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
        logger.info("开始划分操作,共有" + (fileTempEnd - fileTempStart) + "个文件");
        for (int i = fileTempStart; i < fileTempEnd; i++) {
            String inputFile = srcPath + setFileName(curveNum, pointNum, i);
            String outputFolder = dstPath + "division" + i;
            logger.info("划分文件" + inputFile);
            divideFromFileToHBase(inputFile, outputFolder, level);
        }
        logger.info("结束划分操作");
        Date ET = new Date();
        String info = "[操作详情]划分" + dataSize + dataSizeUnit + "\n";
        writeResultToFile(ET.getTime() - ST.getTime(), "数据划分", info);
        return ET.getTime() - ST.getTime();
    }

    //报表操作
    public static long report(int reportLevel) {
        Date ST = new Date();
        logger.info("开始报表");
        String timeInfo = getAllIntersectReport(reportLevel);
        Date ET = new Date();
        logger.info("结束报表");

        writeResultToFile(ET.getTime() - ST.getTime(), "报表操作", timeInfo);
        return ET.getTime() - ST.getTime();
    }

    // 1 10 50 100 条曲线
    public static void curveAnalysisTest(String url, int level) {
        //应当特别生成一个500条curve的文件，读取该测试文件url
        List<CurveInfo> curves = getCurveFromFile(url);
        logger.info("curves.size()=" + curves.size());

        //calculate intersection between 1 curve/10 curves/50 curves/100curves and data from HBase
        for (int i : new int[]{1, 5, 10}) {
            List<CurveInfo> curvesTest = curves.subList(0, i);

            Date ST = new Date();
            String res = getIntersectionByCurves(curvesTest, level, Globals.ns, Globals.tbl);
            Date ET = new Date();

            String info = "计算" + i + "条曲线与数据库的交点\n[具体时间消耗]" + res;
            writeResultToFile(ET.getTime() - ST.getTime(), "曲线分析", info);
        }
    }

}

