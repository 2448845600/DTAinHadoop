# DTAinHadoop

Accepted by NDBC 2018

高效的空间大数据分布式拓扑分析算法
Efficient Distributed Big Spatial Data Topology Analysis Algorithm

Abstract: Rapidly growing spatial data and the increasing demand for application have put forward higher requirements for spatial topology analysis algorithms, such as high concurrent real-time request processing and massive data topology analysis. The existing stand-alone or multi-cores space topology analysis algorithm to deal with spatial data is inefficient. We propose an efficient big spatial data distributed topology analysis algorithm. The space-filling curve is used to divide the complex spatial objects into subspace objects. Each sub-space-object has a unique number, CellId. A pre-judgment algorithm is proposed that uses the CellId’ spatial information to filter the redundant data and reduce the data in topological analysis. Experiments show that this algorithm can efficiently process the topological analysis of massive spatial data. It is superior to existing stand-alone and multi-core algorithms in most cases, and has a linear acceleration ratio.

Key words: spatial database; topology analysis; distributed computing; data division; space fill curve

摘 要: 迅猛增长的空间数据和日趋旺盛的应用需求，对空间拓扑分析算法提出了更高的要求， 如高并发
请求实时处理、 海量数据拓扑分析。 现有空间拓扑分析算法针对单机或多核设计，处理空间大数据效率不
高。本文提出了一种高效的空间大数据分布式拓扑分析算法。 利用空间填充曲线将复杂的空间对象划分为
子空间对象，每个子空间对象拥有唯一网格编号； 提出一种预先判断机制，利用网格编号包含的局部空间
信息过滤冗余数据，减少拓扑分析的数据量。 实验表明，该算法可以高效的处理海量空间数据拓扑分析，
在绝大多数情况下优于现有单机和多核算法，且具有线性加速比。

关键词: 空间数据库； 拓扑分析; 分布式计算; 数据划分; 空间填充曲线 

## 总体构架

BBox（边缘矩形，粗略表示曲线和区域），Segment（线段，详细表示曲线与区域），这两个数据结构类似

Curve（曲线），Region（区域，可以看作封闭曲线）先不管Region

LineDetail，RegionDetail
L1.Points L2.points -> Map -> p台机器，线段L1，L2相同cellId的点(也就是可能交点)就在同一个机器上 -> Reduce -> 交点/null

如何分布式：
1 以求两条线段L1,L2交点为例：
读取curveDetail.pointNums，根据cellId，将两条线段的点映射到p台机器上，
每台机器上就有Map<cellId_n, list<Point>> L1Map 和 L2Map,判断后返回交点或者null。long 8B，double 8B，一个点32B；1000KM长的线段，按照1m一个点，1000 000个点，1000 000 * 32 B = 32MB，这种情况可以不需要分布式

2 以一条线段与多条已知线段求交点集合为例:
长途货车穿越哪些乡镇
乡镇的位置信息是提前确定的，货车路线每时每刻不同。乡镇的信息数据量大，需要分割后存储在p台机器中，货车的路线分割后放入不同台机器，就真分布式了。

只有2这种情况，多条线/区域求交点，数据量大，才需要分布式

[//]:<以下是base64编码后的图片，这种方法插入图片很骚，也可以在html中实现这方法（base64可以直接在浏览器中解析）>
![image][image1]


## 数据结构：

rawData -> curve/region -> pointSets

### point
rawData1 {
​    List<(double x, double y)}>
​    type // 曲线还是区域还是其他
}

### curve, region
curve/region {
​    public long curveNum;
​    public CellId cellId;
​    public List<Long> divisionPointSetNums; // 第一次划分
}

### pointSet
pointSet {
​    private long pointSetNum;
​    private long inheritNum; 
​    private CellId cellId;
​    public List<Long> divisionPointSetNums; // 可以继续划分，多次划分（pointSet划分为多个子pointSet，这里存储pointSetNum）
​    private List<Point> points; // 可以为空，即具体点的信息存储在divisionPointSetNums的points里面
}


## 功能模块

### 存储数据

原始数据利用 cellId 划分到一个个 pointSet，存储在机器中

1 建立数据库

2 对于输入的线段，分割存储在内存/数据库中

3 输入curveNum，得到这条线段的所有信息

### 拓扑分析

1 全库分析，输出报表（交点个数）
```
long getReportIntersection()
{
    //计算交点，将所有点写入文件intersection.txt
    //返回交点个数
}
```

2 输入线段，分析该线段与数据库信息的拓扑关系（相交，包含等）
```$xslt
//单个处理
List<Point> calculateIntersection(Curve curve)
{
    List<Point> res = new ArrayList<>();
    
    //划分curve为多个PointSet
    //对每个pointSet，求交点
    
    return res;
}

//批处理
List<List<Point>> calculateIntersection(List<Curve> curves)
{
    List<List<Point>> res = new ArrayList<>();
    
    //划分curve为多个PointSet
    //对每个pointSet，求交点
    
    return res;
}

Boolean calculateIntersectionToFile(List<Curve> curves)
{
    //划分curve为多个PointSet
    //对每个pointSet，求交点
    //写入文件
}
```

3 进入某个区域的点（已知点的轨迹和区域的范围，找出所有轨迹进入到区域内的点 -> 现实场景：某城市净流入人口，净流出人口）
```
public static void getPointInRegion(Region region)
{
    //划分region
    //查找与之相交的线段（轨迹）
    //判断交点状态（流入，流出，相切）
    //统计保存结果
}
```

### 结果展示

1 全库展示

2 输入curveNum，展示该线段

3 展示拓扑分析结果（显示交点，包含关系等）










[image1]:data:image/png;base64,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ir/4i7+Qv1y+fLn43//9X3HiiSeKWbNmAVkCBG6//Xa54lh1SHPChAni3nvvFW+++WYCNLrtIsREt+PXaesvvfRSMWXKFDmZnHLKKdIXHK7sdEjZxtPqBK020V76l770JVnv008/FQMGDBB0t4pVCTbKKApWHdK86KKL5IrkeeedJ04++eQofI3VCYiJWCMbuF//9V//JXbccUd5Zzpp0iQpKLI/Z5xxhpxA6MKCn3gJTJ48WVx77bVi2bJl0sn1119f/PM//7M4//zz43UanlUSKB7SpDmA/syZM0esXLlS/O53vxN//dd/DYKBEoCYCDQwsZu11157iYULF8pJYsSIEeKVV17B4crYg17wL786Qb/CqkRiCVDhbv6Q5rbbbivP0Ky99tpil112EU8//TQgBUoAYiLQwMRsFq1AXHDBBeKDDz4Qffr0kasTdBCP9srxkxYBWp3IzsvQ/jhWJdKKf523dKaGDuTSOZpVq1aJ9dZbT/4/naHCT3gEICbCi0nUFj3//PNijz32EJ9//nmPn3TXQX9oiZvOUeAnHQK0OjF06FDp8B//+EeclUgn9LWennTSSWLevHly5fKzzz7rKbvWWmuJZ599VvzN3/wNSAVGAGIisIDEbg59P4NWJPr16yddpZUJmiDoz0cffSQfGaSLCn7SIXDwwQdLZ++66650nIanlQQ22mgjOUess8468qaD/tDKBP2QuKAVirfffhsEAyMAMRFYQGI3h5a1t9xySzlRDB48uPTPFltsETsG+AcCIFBB4I033hB//vOfS//QDcfrr7+O7bAAswdiIsCgwCQQAAEQAAEQ6BIBiIkuRQu2ggAIgAAIgECABCAmAgwKTAIBEAABEACBLhGAmOhStGArCIAACIAACARIAGIiwKDAJBAAARAAARDoEgGIiS5FC7aCAAiAAAiAQIAEICYCDApMAgEQAAEQAIEuEYCY6FK0YCsIgAAIgAAIBEgAYiLAoMAkEAABEAABEOgSAYiJLkULtoIACIAACIBAgAQgJgIMCkwCARAAARAAgS4RgJjoUrRgKwiAAAiAAAgESABiIsCgwCQQAAEQAAEQ6BIBiIkuRQu2ggAIgAAIgECABCAmAgwKTAIBEAABEACBLhGAmOhStGArCIAACIAACARIAGIiwKDAJBAAARAAARDoEgGIiS5FC7aCAAiAAAiAQIAE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



