package com.Experiment;

import com.DataStructure.BasicManager.CurveManager;
import com.DataStructure.Model.CurveInfo;
import com.DataStructure.Model.Point;
import com.DataStructure.Model.PointSet;
import org.apache.log4j.Logger;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DivisionTest {
    private static Logger logger = Logger.getLogger(DivisionTest.class);
    public static String file = "D:\\experiment\\test_1_10000_0.txt";
    public static String localResult = "D:\\experiment\\A.txt";
    public static int level = 28;

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

    public static List<CurveInfo> getCurveFromFile(String fileName) {
        logger.info("读取文件" + fileName);

        List<CurveInfo> curves = new ArrayList<>();
        File file = new File(fileName);
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(file));
            String str = null;
            while ((str = reader.readLine()) != null) {
                CurveInfo curve = strToCurveInfo(str);
                curves.add(curve);
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return curves;
    }

    public static void writeResultToFile(long curveNum, List<PointSet> pointSets) {
        String s = "[曲线号]" + curveNum + "\n" + "" + "[划分等级]" + level + "\n" +
                "[点集]" + "有点集" + pointSets.size() + "个" + "\n";
        for (int i = 0; i < pointSets.size(); i++) {
            s += "点集" + i + "" + "的点个数=" + pointSets.get(i).getPoints().size();
            String ps = "";
            for (int k = 0; k < pointSets.get(i).getPoints().size(); k++) {
                ps += "(" + pointSets.get(i).getPoints().get(k).getX() + "," + pointSets.get(i).getPoints().get(k).getY() + ") ";
            }
            s += ps + "\n";
        }
        s += "\n----------------------------------------------------------------------------------------\n";

        try {
            FileWriter fw = new FileWriter(localResult, false);
            fw.write(s);
            fw.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        //get curves from file
        List<CurveInfo> curves = getCurveFromFile(file);

        //divide curves to pointSets
        for (int i = 0; i < curves.size(); i++) {
            logger.info(curves.get(i).toString());
            List<PointSet> pointSetList = CurveManager.curveToPointSet(curves.get(i).getCurveNum(), curves.get(i).getPoints(), level);
            //writeResultToFile(curves.get(i).getCurveNum(), pointSetList);
            logger.info(pointSetList.size());
        }
    }
}
