/**
 * 点基本操作
 */
package com.DataStructure.BasicManager;

import com.Globals;
import com.DataStructure.Model.Point;
import com.DataStructure.Model.Points;
import com.mongodb.client.FindIterable;
import org.bson.Document;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import static com.Util.MongoDBJDBC.queryDocument;


public class PointManager {

    /**
     * 得到所有点
     */
    public static Points getAllPoints() {
        Points points = new Points();
        FindIterable<Document> queryRes = queryDocument(Globals.PointCollectionName, null);
        for (Document document : queryRes) {
            Point pt = new Point(document.getLong("pointNum"), document.getDouble("x"), document.getDouble("y"));
            points.add(pt);
        }
        return points;
    }

    /**
     * 得到给定点的HashMapKey
     */
    public static long getPointHashMapKey(Point point) {
        return point.getCellId().getId();
    }

    /**
     * 返回一个HashMap，里面包含按照CellId分组的所有点
     */
    public static HashMap<Long, Points> getAllPointsGroupByCellId() {
        HashMap<Long, Points> pointsHashMap = new HashMap<Long, Points>();
        Points allPoints = getAllPoints();

        for(Point point : allPoints.getPoints()) {
            Long key = getPointHashMapKey(point);
            if(pointsHashMap.get(key) != null ){
                pointsHashMap.get(getPointHashMapKey(point)).add(point);
            } else {
                Points points = new Points();
                points.add(point);
                pointsHashMap.put(key, points);
            }
        }
        return pointsHashMap;
    }

    /**
     * 返回给定点集中相同cellId的子集
     */
    public static Points getSameCellIdPoints(Point point, Map<Long, Points> allPointsGroupByCellId) {
        return allPointsGroupByCellId.get(getPointHashMapKey(point));
    }

    public static Point getPointByPointNum(long pointNum) {
        Document query = new Document("pointNum", pointNum);
        FindIterable<Document> queryRes = queryDocument(Globals.PointCollectionName, query);
        Document document = queryRes.first();
        Point point = new Point(document.getLong("pointNum"), document.getDouble("x"), document.getDouble("y"));
        return point;
    }

    public static void printPointsMap(HashMap<Long, Points> pointsMap) {
        Iterator iter = pointsMap.entrySet().iterator();
        System.out.println("点集按照cellId分组：");
        int i = 0;
        while(iter.hasNext()) {
            HashMap.Entry entry = (HashMap.Entry) iter.next();
            Long key = Long.valueOf(entry.getKey().toString());
            Points val = pointsMap.get(key);
            System.out.println(i + ":(key=" + key + ", value.size=" + val.getPoints().size() + ")");
            i++;
        }
    }

}
