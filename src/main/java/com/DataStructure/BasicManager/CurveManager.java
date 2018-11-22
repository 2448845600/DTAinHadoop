package com.DataStructure.BasicManager;

import com.Globals;
import com.DataStructure.Model.*;
import com.mongodb.client.FindIterable;
import org.bson.Document;
import org.omg.PortableServer.POA;

import java.util.ArrayList;
import java.util.List;

import static com.Util.MongoDBJDBC.insertDocumentList;
import static com.Util.MongoDBJDBC.queryDocument;
import static com.DataStructure.BasicManager.CellIdManager.isSameCellId;

public class CurveManager {

    /**
     * 返回所有曲线的BBox信息
     */
    public static List<Curve> getAllCurve() {
        List<Curve> curves = new ArrayList<Curve>();
        /*
        FindIterable<Document> queryRes = queryDocument(Globals.CurveCollectionName, null);
        for (Document document : queryRes) {
            BBox bbox = new BBox(document.getLong("BBox"));
            Curve curve = new Curve(document.getLong("curveNum"), bbox);
            curves.add(curve);
        }
         */
        return curves;
    }

    /**
     * 向数据库中插入曲线索引信息curves
     */
    public static boolean insertCurvesToDB(List<Curve> curves) {
        List<Document> documents = new ArrayList<Document>();
        for (Curve curve : curves) {
            documents.add(curve.toDocumet());
        }
        return insertDocumentList(Globals.CurveCollectionName, documents);
    }

    public static List<Long> getPointNumsByCurveNum(long curveNum) {
        List<Long> pointNums = new ArrayList<Long>();

        Document query = new Document("curveNum", curveNum);
        FindIterable<Document> queryRes = queryDocument(Globals.CurveDetailCollectionName, query);
        for (Document res : queryRes) {
            pointNums = (List<Long>) res.get("pointNums");
        }
        return pointNums;
    }

    public static Points getPointsByCurveNum(long curveNum) {
        Points points = new Points();
        List<Long> pointNums = getPointNumsByCurveNum(curveNum);
        for (long pointNum : pointNums) {
            Point point = PointManager.getPointByPointNum(pointNum);
            points.add(point);
        }

        System.out.println();
        return points;
    }

    /**
     * 将曲线划分为有序点集
     *
     * @param curveNum  曲线序号。
     * @param pointList 点集。
     * @param level     划分颗粒度。即cellId前bNum位相同的point划分到一个pointSet。
     * @return
     */
    public static List<PointSet> curveToPointSet(long curveNum, List<Point> pointList, int level) {
        if (pointList.size() < 2) {
            return null;
        }

        List<PointSet> resPointSets = new ArrayList<>();
        List<Point> tempPoints = new ArrayList<>();
        Point p1 = pointList.get(0);
        Point p2 = pointList.get(1);

        for (int i = 1; i < pointList.size(); i++) {
            p2 = pointList.get(i);

            tempPoints.add(p1);
            if (!isSameCellId(p1.getCellId().getId(), p2.getCellId().getId(), level)) {
                tempPoints.add(p2);//每个cell多保存一个点
                List<Point> ps = new ArrayList<>();
                ps.addAll(tempPoints);
                PointSet pointSet = new PointSet(i, curveNum, ps, new CellId(p1.getCellId().getId()));
                resPointSets.add(pointSet);
                tempPoints.clear();
            }
            p1 = p2;
        }
        PointSet pointSet = new PointSet(pointList.size(), curveNum, tempPoints, new CellId(p2.getCellId().getId()));
        resPointSets.add(pointSet);

        return resPointSets;
    }

}

