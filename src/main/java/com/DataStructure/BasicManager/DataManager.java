package com.DataStructure.BasicManager;

import com.Globals;
import com.DataStructure.Model.CellId;
import com.DataStructure.Model.Curve;
import com.DataStructure.Model.Point;
import com.DataStructure.Model.PointSet;
import com.mongodb.client.FindIterable;
import org.bson.Document;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;


import static com.Util.MongoDBJDBC.queryDocument;

/**
 * 从多种媒介读取原始数据，建立分布式数据库
 * <p>
 * 以README.md中“长途货车穿越哪些乡镇”为例，这里就是处理乡镇数据，将其划分到p台机器中
 * 没有大量数据，没有多台机器，咋整 -> 从mongoDB中读取数据，放到本机内存里面，List<List<PointSet>> dataAfterMap，dataAfterMap中每一项模拟一台机器
 * 先假设 p = 2^4 = 16
 */
public class DataManager {
    private List<Curve> curves;//线段数据的索引
    private List<List<PointSet>> dataAfterMap;//数据库已有数据进行划分映射后，得到分布式数据库。
    private int pointSetNumMax = 0;

    public DataManager() {
        this.curves = new ArrayList<Curve>();
        this.dataAfterMap = new ArrayList<List<PointSet>>();
    }

    public List<List<PointSet>> getDataAfterMap() {
        return dataAfterMap;
    }

    public void setDataAfterMap(List<List<PointSet>> dataAfterMap) {
        this.dataAfterMap = dataAfterMap;
    }

    public long getPointSetNumMax() {
        return pointSetNumMax;
    }

    public void setPointSetNumMax(int pointSetNumMax) {
        this.pointSetNumMax = pointSetNumMax;
    }

    public int getNewPointSetMax() {
        return ++this.pointSetNumMax;
    }


    //在内存中模拟分布式数据库
    //从数据库读取曲线数据，存储到List<PointSet>中
    //将List<PointSet>中的每个PointSet划分为HashMap<Long, PointSet>
    //将每个HashMap<Long, PointSet>映射到List<List<PointSet>>，得到的结果就可以赋给private List<List<PointSet>> dataAfterMap;

    /**
     * 在内存中模拟分布式数据库
     * <p>
     * example:
     * DataManager dataManager = new DataManager();
     * List<List<PointSet>> data = simulateDistributedDBInMemory();
     * dataManager.setDataAfterMap(data);
     */
    public List<List<PointSet>> simulateDistributedDBInMemory() {
        List<List<PointSet>> resData = new ArrayList<List<PointSet>>();
        for (int i = 0; i < Globals.MACSHINE_NUM; i++) {
            List<PointSet> pointSetList = new ArrayList<PointSet>();
            resData.add(pointSetList);
        }

        List<PointSet> rawData = getRawDataFromMongoDB();
        for (int i = 0; i < rawData.size(); i++) {
            PointSet temp = rawData.get(i);
            HashMap<Long, PointSet> divisionPointSetMap = divideIntoPointSets(temp, Globals.MACSHINE_BIT);

            Iterator iter = divisionPointSetMap.entrySet().iterator();
            while (iter.hasNext()) {
                HashMap.Entry entry = (HashMap.Entry) iter.next();
                long key = Long.valueOf(entry.getKey().toString());
                PointSet value = divisionPointSetMap.get(key);
                int k = (int) (key % (Globals.MACSHINE_NUM - 1));
                //System.out.print("\n\nin simulateDistributedDBInMemory(), key="+key+", k="+ k);
                //System.out.println("value" + value);
                resData.get(k).add(value);
            }
        }
        return resData;
    }

    /**
     * 从MongoDB中获取原始数据
     * {curveNum:v ,cellId:v ,points: {{x:vx, y:vy},{x:vx, y:vy}}}
     * <p>
     * 功能：
     * this.pointSetNumMax = (long) i;
     * this.curves = curves;
     * return pointSets;
     */
    public List<PointSet> getRawDataFromMongoDB() {
        List<PointSet> pointSets = new ArrayList<PointSet>();
        List<Curve> curves = new ArrayList<Curve>();

        FindIterable<Document> queryRes = queryDocument(Globals.RawDataCollectionName, null);
        int i = 0;
        for (Document document : queryRes) {
            i++;
            long curveNum = document.getLong("curveNum");
            CellId cellId = new CellId(document.getLong("cellId"));
            List<Point> points = new ArrayList<Point>();
            List<Document> pointRes = (List<Document>) document.get("points");
            for (Document d : pointRes) {
                Point point = new Point(d.getLong("pointNum"), d.getDouble("x"), d.getDouble("y"));
                points.add(point);
            }

            PointSet pointSet = new PointSet(i, curveNum, points, cellId);
            pointSets.add(pointSet);
            Curve curve = new Curve(curveNum, cellId);
            curves.add(curve);
        }

        this.pointSetNumMax = (int) i;
        this.curves = curves;
        return pointSets;
    }

    /**
     * 将pointSet划分为HashMap<Long, pointSet>，就是MP中的 Map() 函数
     */
    public HashMap<Long, PointSet> divideIntoPointSets(PointSet pointSet, int bitNum) {
        HashMap<Long, PointSet> pointSetMap = new HashMap<Long, PointSet>();

        long inheritNum = pointSet.getInheritNum();
        List<Point> points = pointSet.getPoints();
        //将点集按照cellId划分为多个子点集
        for (Point point : points) {
            long key = CellIdManager.getLongPreBit(point.getCellId().getId(), bitNum);
            if (pointSetMap.get(key) != null) {
                pointSetMap.get(key).getPoints().add(point);
            } else {
                PointSet newPointSet = new PointSet(getNewPointSetMax(), inheritNum);
                newPointSet.getPoints().add(point);
                pointSetMap.put(key, newPointSet);
            }
        }

        Iterator iter = pointSetMap.entrySet().iterator();
        while (iter.hasNext()) {
            HashMap.Entry entry = (HashMap.Entry) iter.next();
            long key = Long.valueOf(entry.getKey().toString());
            PointSet value = pointSetMap.get(key);
        }
        return pointSetMap;
    }

}
