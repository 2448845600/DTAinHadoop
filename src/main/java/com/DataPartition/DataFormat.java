package com.DataPartition;

import com.DataStructure.Model.Curve;
import com.DataStructure.Model.Point;
import com.DataStructure.Model.PointSet;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class DataFormat {

    /**
     * @param data double double double ......
     * @return
     */
    private static Logger logger = Logger.getLogger(DataFormat.class);

    public static List<Point> getPointListByLineRDD(String data) {
        List<Point> res = new ArrayList<>();
        String[] temp = data.split(" ");
        for (int i = 0; i < temp.length - 1; i += 2) {
            double x = Double.parseDouble(temp[i]);
            double y = Double.parseDouble(temp[i + 1]);
            Point p = new Point(x, y);
            res.add(p);
        }
        return res;
    }

    public static PointSet setPointSetByVal(String rk, byte[] val) {
        List<Point> points = new ArrayList<>();

        String[] valStr = new String(val).split(" ");

        List<Double> valDouble = new ArrayList<>();
        for (int i = 0; i < valStr.length -1; i++) {
            logger.debug("valStr[i]=" + valStr[i]);
            if (valStr[i] != "") {
                valDouble.add(Double.parseDouble(valStr[i]));
            }
        }
        for (int i = 0; i < valDouble.size() - 1; i += 2) {
            Point p = new Point(valDouble.get(i), valDouble.get(i + 1));
            points.add(p);
        }
        PointSet pointSet = new PointSet(rk, points);
        return pointSet;
    }
}
