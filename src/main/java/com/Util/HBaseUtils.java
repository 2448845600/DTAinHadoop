package com.Util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.hbase.Cell;
import org.apache.hadoop.hbase.CellUtil;
import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.TableName;
import org.apache.hadoop.hbase.client.Admin;
import org.apache.hadoop.hbase.client.Connection;
import org.apache.hadoop.hbase.client.ConnectionFactory;
import org.apache.hadoop.hbase.client.Get;
import org.apache.hadoop.hbase.client.Result;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.client.Table;
import org.apache.hadoop.hbase.filter.BinaryComparator;
import org.apache.hadoop.hbase.filter.ColumnCountGetFilter;
import org.apache.hadoop.hbase.filter.ColumnPaginationFilter;
import org.apache.hadoop.hbase.filter.ColumnPrefixFilter;
import org.apache.hadoop.hbase.filter.ColumnRangeFilter;
import org.apache.hadoop.hbase.filter.DependentColumnFilter;
import org.apache.hadoop.hbase.filter.FamilyFilter;
import org.apache.hadoop.hbase.filter.Filter;
import org.apache.hadoop.hbase.filter.FilterList;
import org.apache.hadoop.hbase.filter.FirstKeyOnlyFilter;
import org.apache.hadoop.hbase.filter.FuzzyRowFilter;
import org.apache.hadoop.hbase.filter.InclusiveStopFilter;
import org.apache.hadoop.hbase.filter.KeyOnlyFilter;
import org.apache.hadoop.hbase.filter.MultipleColumnPrefixFilter;
import org.apache.hadoop.hbase.filter.PageFilter;
import org.apache.hadoop.hbase.filter.PrefixFilter;
import org.apache.hadoop.hbase.filter.QualifierFilter;
import org.apache.hadoop.hbase.filter.RandomRowFilter;
import org.apache.hadoop.hbase.filter.RegexStringComparator;
import org.apache.hadoop.hbase.filter.RowFilter;
import org.apache.hadoop.hbase.filter.SingleColumnValueExcludeFilter;
import org.apache.hadoop.hbase.filter.SingleColumnValueFilter;
import org.apache.hadoop.hbase.filter.SkipFilter;
import org.apache.hadoop.hbase.filter.SubstringComparator;
import org.apache.hadoop.hbase.filter.TimestampsFilter;
import org.apache.hadoop.hbase.filter.ValueFilter;
import org.apache.hadoop.hbase.filter.WhileMatchFilter;
import org.apache.hadoop.hbase.filter.CompareFilter.CompareOp;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.hadoop.hbase.util.Pair;

/**
 * https://blog.csdn.net/m0_37739193/article/details/73615016
 */
public class HBaseUtils {

    public static Admin admin = null;
    public static Connection conn = null;

    public HBaseUtils() {
        Configuration conf = HBaseConfiguration.create();
        conf.set("hbase.zookeeper.property.clientPort", "2181");
        conf.set("hbase.zookeeper.quorum", "master,slave1,slave2");
        try {
            conn = ConnectionFactory.createConnection(conf);
            admin = conn.getAdmin();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws Exception {
        HBaseUtils hbase = new HBaseUtils();
        //1，FamilyFilter：基于“列族”来过滤数据；
//		hbase.FamilyFilter("scores");
        //2，QualifierFilter：基于“列名”来过滤数据；
//		hbase.QualifierFilter("scores");
        //3.RowFilter：基于rowkey来过滤数据；
//		hbase.RowFilter("scores","zhangsan01");
        //4.PrefixFilter：基于rowkey前缀来过滤数据；
//		hbase.PrefixFilter("scores","zhang");
        //后缀过滤数据
//		hbase.HouZui("scores");
        //5，ColumnPrefixFilter：基于列名前缀来过滤数据；
//		hbase.ColumnPrefixFilter("scores");
        //6，MultipleColumnPrefixFilter：ColumnPrefixFilter的加强版；
//		hbase.MultipleColumnPrefixFilter("scores");
        //7，ColumnCountGetFilter：限制每行返回多少列；
//		hbase.columnCountGetFilter();
        //8，ColumnPaginationFilter：对一行的所有列分页，只返回[limit, offset]范围内的列；
//		hbase.ColumnPaginationFilter("scores");
        //9，ColumnRangeFilter：可用于获得一个范围的列
//		hbase.ColumnRangeFilter("scores");
        //10，DependentColumnFilter：返回（与（符合条件[列族，列名]或[列族，列名，值]的参考列）具有相同的时间戳）的所有列，即：基于比较器过滤参考列，基于参考列的时间戳过滤其他列；
//		hbase.DependentColumnFilter("scores");
        //11，FirstKeyOnlyFilter：结果只返回每行的第一个值对；
//		hbase.FirstKeyOnlyFilter("scores");
        //12，FuzzyRowFilter：模糊row查询；
//		hbase.FuzzyRowFilter("scores");
        //13，InclusiveStopFilter：将stoprow也一起返回；
//		hbase.InclusiveStopFilter("scores");
        //14，KeyOnlyFilter：只返回行键；
//		hbase.KeyOnlyFilter("scores");
        //15，PageFilter： 取回XX条数据  ；
//		hbase.PageFilter("scores");
        //16，RandomRowFilter：随机获取一定比例（比例为参数）的数据；
//		hbase.RandomRowFilter("scores");
        //17，SingleColumnValueFilter：基于参考列的值来过滤数据；
//		hbase.SingleColumnValueFilter("scores");
        //18，ValueFilter：基于值来过滤数据；
//		hbase.ValueFilter("scores");
        //19，SkipFilter：当过滤器发现某一行中的一列要过滤时，就将整行数据都过滤掉；
//		hbase.SkipFilter("scores");
        //20，TimestampsFilter：基于时间戳来过滤数据；
//		hbase.TimestampsFilter("scores");
        //21，WhileMatchFilter：一旦遇到一条符合过滤条件的数据，就停止扫描；
//		hbase.WhileMatchFilter("scores");
        //22，FilterList：多个过滤器组合过滤。
//		hbase.FilterList("scores");
    }

    /**
     * 1,FamilyFilter
     * a,按family（列族）查找，取回所有符合条件的“family”
     * b,构造方法第一个参数为compareOp
     * c,第二个参数为WritableByteArrayComparable，有BinaryComparator, BinaryPrefixComparator,
     * BitComparator, NullComparator, RegexStringComparator, SubstringComparator这些类，
     * 最常用的为BinaryComparator
     */
    public void FamilyFilter(String tableName) throws Exception {
        Table table = conn.getTable(TableName.valueOf(tableName));
        Scan scan = new Scan();
        Filter filter = new FamilyFilter(CompareOp.LESS_OR_EQUAL, new BinaryComparator(Bytes.toBytes("grc")));
        scan.setFilter(filter);
        ResultScanner scanner = table.getScanner(scan);
        for (Result r : scanner) {
            for (Cell cell : r.rawCells()) {
                System.out.println(
                        "Rowkey-->" + Bytes.toString(r.getRow()) + "  " +
                                "Familiy:Quilifier-->" + Bytes.toString(CellUtil.cloneQualifier(cell)) + "  " +
                                "Value-->" + Bytes.toString(CellUtil.cloneValue(cell)));
            }
        }
    }

    /**
     * 2，QualifierFilter
     * 类似于FamilyFilter，取回所有符合条件的“列”
     * 构造方法第一个参数   compareOp
     * 第二个参数为WritableByteArrayComparable
     */
    public void QualifierFilter(String tableName) throws Exception {
        Table table = conn.getTable(TableName.valueOf(tableName));
        Scan scan = new Scan();
        Filter filter = new QualifierFilter(CompareOp.LESS_OR_EQUAL, new BinaryComparator(Bytes.toBytes("grc")));
        //这里输的参数是相应位置比大小，及当输入ms的时候，所有列名的第一位小于等于m，如果第一位相等则比较第二位的大小。一开始没理解，所以一开始参数输入math或course的时候把我整懵了。
        scan.setFilter(filter);
        ResultScanner scanner = table.getScanner(scan);
        for (Result r : scanner) {
            for (Cell cell : r.rawCells()) {
                System.out.println(
                        "Rowkey-->" + Bytes.toString(r.getRow()) + "  " +
                                "Familiy:Quilifier-->" + Bytes.toString(CellUtil.cloneQualifier(cell)) + "  " +
                                "Value-->" + Bytes.toString(CellUtil.cloneValue(cell)));
            }
        }
    }

    /**
     * 3，RowFilter
     * 构造方法参数设置类似于FamilyFilter，符合条件的row都返回
     * 但是通过row查询时，如果知道开始结束的row，还是用scan的start和end方法更直接并且经测试速度快一半以上
     */
    public void RowFilter(String tableName, String reg) throws Exception {
        Table table = conn.getTable(TableName.valueOf(tableName));
        Scan scan = new Scan();
        //这个参数EQUAL很重要，如果参数不同，查询的结果也会不同
//		RowFilter filter = new RowFilter(CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes(reg)));//这样写也行
//		Filter filter = new RowFilter(CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes(reg)));
        Filter filter = new RowFilter(CompareOp.LESS_OR_EQUAL, new BinaryComparator(Bytes.toBytes(reg)));
        scan.setFilter(filter);
        ResultScanner scanner = table.getScanner(scan);
        for (Result r : scanner) {
            for (Cell cell : r.rawCells()) {
                System.out.println(
                        "Rowkey-->" + Bytes.toString(r.getRow()) + "  " +
                                "Familiy:Quilifier-->" + Bytes.toString(CellUtil.cloneQualifier(cell)) + "  " +
                                "Value-->" + Bytes.toString(CellUtil.cloneValue(cell)));
            }
        }
        /**
         * 更推荐用下面的方法直接指定起止行，因为filter本质上还是会遍历全部数据，而设定起止行后会直接从指定行开始，指定行结束，效率高很多。
         */
        // scan.setStartRow(Bytes.toBytes("AAAAAAAAAAAA"));
        // scan.setStopRow(Bytes.toBytes( "AAAAAAAAABBB"));
    }

    /**
     * 4，PrefixFilter
     * 取回rowkey以指定prefix开头的所有行
     */
    public void PrefixFilter(String tableName, String reg) throws Exception {
        Table table = conn.getTable(TableName.valueOf(tableName));
        Scan scan = new Scan();
        Filter filter = new PrefixFilter(Bytes.toBytes("zhang"));
        scan.setFilter(filter);
        ResultScanner scanner = table.getScanner(scan);
        for (Result r : scanner) {
            for (Cell cell : r.rawCells()) {
                System.out.println(
                        "Rowkey-->" + Bytes.toString(r.getRow()) + "  " +
                                "Familiy:Quilifier-->" + Bytes.toString(CellUtil.cloneQualifier(cell)) + "  " +
                                "Value-->" + Bytes.toString(CellUtil.cloneValue(cell)));
            }
        }
    }

    /**
     * 由于其原生带有PrefixFilter这种对ROWKEY的前缀过滤查询，因此想着实现的后缀查询的过程中，发现这一方面相对来说还是空白。
     * 因此，只能采用一些策略来实现，主要还是采用正则表达式的方式。
     */
    public void HouZui(String tableName) throws Exception {
        Table table = conn.getTable(TableName.valueOf(tableName));
        Scan scan = new Scan();
        Filter filter = new RowFilter(CompareOp.EQUAL, new RegexStringComparator(".*n01"));
        scan.setFilter(filter);
        ResultScanner scanner = table.getScanner(scan);
        for (Result r : scanner) {
            for (Cell cell : r.rawCells()) {
                System.out.println(
                        "Rowkey-->" + Bytes.toString(r.getRow()) + "  " +
                                "Familiy:Quilifier-->" + Bytes.toString(CellUtil.cloneQualifier(cell)) + "  " +
                                "Value-->" + Bytes.toString(CellUtil.cloneValue(cell)));
            }
        }
    }

    /**
     * 5，ColumnPrefixFilter
     */
    public void ColumnPrefixFilter(String tableName) throws Exception {
        Table table = conn.getTable(TableName.valueOf(tableName));
        Scan scan = new Scan();
        byte[] prefix = Bytes.toBytes("ar");
        Filter filter = new ColumnPrefixFilter(prefix);
        scan.setFilter(filter);
        ResultScanner scanner = table.getScanner(scan);
        for (Result r : scanner) {
            for (Cell cell : r.rawCells()) {
                System.out.println(
                        "Rowkey-->" + Bytes.toString(r.getRow()) + "  " +
                                "Familiy:Quilifier-->" + Bytes.toString(CellUtil.cloneQualifier(cell)) + "  " +
                                "Value-->" + Bytes.toString(CellUtil.cloneValue(cell)));
            }
        }
    }

    /**
     * 6，MultipleColumnPrefixFilter
     * a,返回有此前缀的所有列，
     * b,在byte[][]中定义所有需要的列前缀，只要满足其中一条约束就会被返回（ColumnPrefixFilter的加强版），
     */
    public void MultipleColumnPrefixFilter(String tableName) throws Exception {
        Table table = conn.getTable(TableName.valueOf(tableName));
        Scan scan = new Scan();
        byte[][] prefix = {Bytes.toBytes("ar"), Bytes.toBytes("ma")};
        Filter filter = new MultipleColumnPrefixFilter(prefix);
        scan.setFilter(filter);
        ResultScanner scanner = table.getScanner(scan);
        for (Result r : scanner) {
            for (Cell cell : r.rawCells()) {
                System.out.println(
                        "Rowkey-->" + Bytes.toString(r.getRow()) + "  " +
                                "Familiy:Quilifier-->" + Bytes.toString(CellUtil.cloneQualifier(cell)) + "  " +
                                "Value-->" + Bytes.toString(CellUtil.cloneValue(cell)));
            }
        }
    }

    /**
     * 7，ColumnCountGetFilter
     * a,无法再scan中使用，只能在Get中
     * b,若设为0，则无法返回数据，设为几就按服务器中存储位置取回几列
     * c,可用size()取到列数，观察效果
     */
    public void columnCountGetFilter() throws Exception {
        Table table = conn.getTable(TableName.valueOf("scores"));
        Get get = new Get(Bytes.toBytes("zhangsan01"));
        get.setFilter(new ColumnCountGetFilter(2));
        Result result = table.get(get);
        //输出结果size，观察效果
        System.out.println(result.size());
//        byte[] value1 = result.getValue("course".getBytes(), "art".getBytes());
//        byte[] value2 = result.getValue("course".getBytes(), "math".getBytes());
//        System.out.println("course:art"+"-->"+new String(value1)+"  "
//                +"course:math"+"-->"+new String(value2));
    }

    /**
     * 8，ColumnPaginationFilter
     * a,limit 表示返回列数
     * b,offset 表示返回列的偏移量，如果为0，则全部取出，如果为1，则返回第二列及以后
     */
    public void ColumnPaginationFilter(String tableName) throws Exception {
        Table table = conn.getTable(TableName.valueOf(tableName));
        Scan scan = new Scan();
        Filter filter = new ColumnPaginationFilter(2, 1);
        scan.setFilter(filter);
//		用addFamily增加列族后，会只返回指定列族的数据
        scan.addFamily(Bytes.toBytes("course"));
        ResultScanner scanner = table.getScanner(scan);
        for (Result r : scanner) {
            for (Cell cell : r.rawCells()) {
                System.out.println(
                        "Rowkey-->" + Bytes.toString(r.getRow()) + "  " +
                                "Familiy:Quilifier-->" + Bytes.toString(CellUtil.cloneQualifier(cell)) + "  " +
                                "Value-->" + Bytes.toString(CellUtil.cloneValue(cell)));
            }
        }
    }

    /**
     * 9，ColumnRangeFilter
     * 构造函数：
     * ColumnRangeFilter(byte[] minColumn, boolean minColumnInclusive, byte[] maxColumn, boolean maxColumnInclusive)
     * 可用于获得一个范围的列，例如，如果你的一行中有百万个列，但是你只希望查看列名为bbbb到dddd的范围
     * 该过滤器可以进行高效的列名内部扫描。（为何是高效呢？？？因为列名是已经按字典排序好的）HBase-0.9.2 版本引入该功能。
     * 一个列名是可以出现在多个列族中的，该过滤器将返回所有列族中匹配的列
     */
    public void ColumnRangeFilter(String tableName) throws Exception {
        Table table = conn.getTable(TableName.valueOf(tableName));
        Scan scan = new Scan();
        Filter filter = new ColumnRangeFilter(Bytes.toBytes("a"), true, Bytes.toBytes("n"), true);
        scan.setFilter(filter);
        ResultScanner scanner = table.getScanner(scan);
        for (Result r : scanner) {
            for (Cell cell : r.rawCells()) {
                System.out.println(
                        "Rowkey-->" + Bytes.toString(r.getRow()) + "  " +
                                "Familiy:Quilifier-->" + Bytes.toString(CellUtil.cloneQualifier(cell)) + "  " +
                                "Value-->" + Bytes.toString(CellUtil.cloneValue(cell)));
            }
        }
    }

    /**
     * 10, DependentColumnFilter （该过滤器有两个参数：family和Qualifier,尝试找到该列所在的每一行，
     * 并返回该行具有相同时间戳的全部键值对。如果某一行不包含指定的列，则该行的任何键值对都不返回，
     * 该过滤器还可以有一个可选的布尔参数-如果为true,从属的列不返回；
     * 该过滤器还可以有两个可选的参数--一个比较操作符和一个值比较器，用于family和Qualifier
     * 的进一步检查，如果从属的列找到，其值还必须通过值检查，然后就是时间戳必须考虑）
     */
    public void DependentColumnFilter(String tableName) throws Exception {
        Table table = conn.getTable(TableName.valueOf(tableName));
        Scan scan = new Scan();
//		Filter filter = new DependentColumnFilter(Bytes.toBytes("course"), Bytes.toBytes("art"),false);
//		Filter filter = new DependentColumnFilter(Bytes.toBytes("course"), Bytes.toBytes("art"),true);
        Filter filter = new DependentColumnFilter(Bytes.toBytes("course"), Bytes.toBytes("art"), false, CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes("90")));
//		Filter filter = new DependentColumnFilter(Bytes.toBytes("course"), Bytes.toBytes("art"),true,CompareOp.EQUAL,new BinaryComparator(Bytes.toBytes("90")));
        //上面这四种情况输出的for循环中的内容也不一样，要做相应的修改，否则会报java.lang.NullPointerException
        scan.setFilter(filter);
        ResultScanner scanner = table.getScanner(scan);
        for (Result r : scanner) {
            for (Cell cell : r.rawCells()) {
                System.out.println(
                        "Rowkey-->" + Bytes.toString(r.getRow()) + "  " +
                                "Familiy:Quilifier-->" + Bytes.toString(CellUtil.cloneQualifier(cell)) + "  " +
                                "Value-->" + Bytes.toString(CellUtil.cloneValue(cell)));
            }
        }
    }

    /**
     * 11，FirstKeyOnlyFilter
     * 如名字所示，结果只返回每行的第一个值对
     */
    public void FirstKeyOnlyFilter(String tableName) throws Exception {
        Table table = conn.getTable(TableName.valueOf(tableName));
        Scan scan = new Scan();
        Filter filter = new FirstKeyOnlyFilter();
        scan.setFilter(filter);
        ResultScanner scanner = table.getScanner(scan);
        for (Result r : scanner) {
            for (Cell cell : r.rawCells()) {
                System.out.println(
                        "Rowkey-->" + Bytes.toString(r.getRow()) + "  " +
                                "Familiy:Quilifier-->" + Bytes.toString(CellUtil.cloneQualifier(cell)) + "  " +
                                "Value-->" + Bytes.toString(CellUtil.cloneValue(cell)));
            }
        }
    }

    /**
     * 12，FuzzyRowFilter
     * 模糊row查询
     * pair中第一个参数为模糊查询的string
     * 第二个参数为byte[]其中装与string位数相同的数值0或1,0表示该位必须与string中值相同，1表示可以不同
     */
    public void FuzzyRowFilter(String tableName) throws Exception {
        Table table = conn.getTable(TableName.valueOf(tableName));
        Scan scan = new Scan();
        Filter filter = new FuzzyRowFilter(Arrays.asList(new Pair<byte[], byte[]>(Bytes.toBytes("zhangsan01"),
                new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 1})));
        scan.setFilter(filter);
        ResultScanner scanner = table.getScanner(scan);
        for (Result r : scanner) {
            for (Cell cell : r.rawCells()) {
                System.out.println(
                        "Rowkey-->" + Bytes.toString(r.getRow()) + "  " +
                                "Familiy:Quilifier-->" + Bytes.toString(CellUtil.cloneQualifier(cell)) + "  " +
                                "Value-->" + Bytes.toString(CellUtil.cloneValue(cell)));
            }
        }
    }

    /**
     * 13，InclusiveStopFilter
     * 指定stopRow，程序在scan时从头扫描全部返回，直到stopRow停止（stopRow这行也会返回，然后scan停止）
     */
    public void InclusiveStopFilter(String tableName) throws Exception {
        Table table = conn.getTable(TableName.valueOf(tableName));
        Scan scan = new Scan();
        Filter filter = new InclusiveStopFilter(Bytes.toBytes("zhangsan01"));
        scan.setFilter(filter);
        ResultScanner scanner = table.getScanner(scan);
        for (Result r : scanner) {
            for (Cell cell : r.rawCells()) {
                System.out.println(
                        "Rowkey-->" + Bytes.toString(r.getRow()) + "  " +
                                "Familiy:Quilifier-->" + Bytes.toString(CellUtil.cloneQualifier(cell)) + "  " +
                                "Value-->" + Bytes.toString(CellUtil.cloneValue(cell)));
            }
        }
    }

    /**
     * 14，KeyOnlyFilter
     * 只取key值，size正常，说明value不是没取而是在取的时候被重写为空（能打印，不是null）
     * lenAsVal这个值没大搞明白，当设为false时打印为空，如果设为true时打印的将会是“口口口口”
     */
    public void KeyOnlyFilter(String tableName) throws Exception {
        Table table = conn.getTable(TableName.valueOf(tableName));
        Scan scan = new Scan();
        Filter filter = new KeyOnlyFilter(true);
        scan.setFilter(filter);
        ResultScanner scanner = table.getScanner(scan);
        for (Result r : scanner) {
            for (Cell cell : r.rawCells()) {
                System.out.println(
                        "Rowkey-->" + Bytes.toString(r.getRow()) + "  " +
                                "Familiy:Quilifier-->" + Bytes.toString(CellUtil.cloneQualifier(cell)) + "  " +
                                "Value-->" + Bytes.toString(CellUtil.cloneValue(cell)));
            }
        }
    }

    /**
     * 15，PageFilter
     * 取回XX条数据
     */
    public void PageFilter(String tableName) throws Exception {
        Table table = conn.getTable(TableName.valueOf(tableName));
        Scan scan = new Scan();
        Filter filter = new PageFilter(2);
        scan.setFilter(filter);
        ResultScanner scanner = table.getScanner(scan);
        for (Result r : scanner) {
            for (Cell cell : r.rawCells()) {
                System.out.println(
                        "Rowkey-->" + Bytes.toString(r.getRow()) + "  " +
                                "Familiy:Quilifier-->" + Bytes.toString(CellUtil.cloneQualifier(cell)) + "  " +
                                "Value-->" + Bytes.toString(CellUtil.cloneValue(cell)));
            }
        }
    }

    /**
     * 16，RandomRowFilter
     * 参数小于0时一条查不出大于1值会返回所有，而想取随机行的话有效区间为0~1，值代表取到的几率
     */
    public void RandomRowFilter(String tableName) throws Exception {
        Table table = conn.getTable(TableName.valueOf(tableName));
        Scan scan = new Scan();
        Filter filter = new RandomRowFilter((float) 0.5);
        //即使是0.5有时候也一条查不出来，有时候却全出来了，是几率并不是一定，那我就不知道这个具体有什么实际运用了。。。根据rowkey随机而不是根据列随机
        scan.setFilter(filter);
        ResultScanner scanner = table.getScanner(scan);
        for (Result r : scanner) {
            for (Cell cell : r.rawCells()) {
                System.out.println(
                        "Rowkey-->" + Bytes.toString(r.getRow()) + "  " +
                                "Familiy:Quilifier-->" + Bytes.toString(CellUtil.cloneQualifier(cell)) + "  " +
                                "Value-->" + Bytes.toString(CellUtil.cloneValue(cell)));
            }
        }
    }

    /**
     * 17，SingleColumnValueFilter和SingleColumnValueExcludeFilter
     * 用来查找并返回指定条件的列的数据
     * a，如果查找时没有该列，两种filter都会把该行所有数据返回
     * b，如果查找时有该列，但是不符合条件，则该行所有列都不返回
     * c，如果找到该列，并且符合条件，前者返回所有列，后者返回除该列以外的所有列
     */
    public void SingleColumnValueFilter(String tableName) throws Exception {
        Table table = conn.getTable(TableName.valueOf(tableName));
        Scan scan = new Scan();
        //完整匹配字节数组
//		Filter filter = new SingleColumnValueFilter(Bytes.toBytes("course"), Bytes.toBytes("art"),CompareOp.EQUAL,new BinaryComparator(Bytes.toBytes("90")));
        //匹配正则表达式
//		Filter filter = new SingleColumnValueFilter(Bytes.toBytes("course"), Bytes.toBytes("art"),CompareOp.EQUAL,new RegexStringComparator("8"));
        //匹配是否包含子串,大小写不敏感
//		Filter filter = new SingleColumnValueFilter(Bytes.toBytes("course"), Bytes.toBytes("art"),CompareOp.EQUAL,new SubstringComparator("9"));
        Filter filter = new SingleColumnValueExcludeFilter(Bytes.toBytes("course"), Bytes.toBytes("art"), CompareOp.EQUAL, new SubstringComparator("9"));
        scan.setFilter(filter);
        ResultScanner scanner = table.getScanner(scan);
        for (Result r : scanner) {
            for (Cell cell : r.rawCells()) {
                System.out.println(
                        "Rowkey-->" + Bytes.toString(r.getRow()) + "  " +
                                "Familiy:Quilifier-->" + Bytes.toString(CellUtil.cloneQualifier(cell)) + "  " +
                                "Value-->" + Bytes.toString(CellUtil.cloneValue(cell)));
            }
        }
    }

    /**
     * 18，ValueFilter
     * 按value全数据库搜索（全部列的value均会被检索）
     */
    public void ValueFilter(String tableName) throws Exception {
        Table table = conn.getTable(TableName.valueOf(tableName));
        Scan scan = new Scan();
        Filter filter = new ValueFilter(CompareOp.NOT_EQUAL, new BinaryComparator(Bytes.toBytes("102")));
        scan.setFilter(filter);
        ResultScanner scanner = table.getScanner(scan);
        for (Result r : scanner) {
            for (Cell cell : r.rawCells()) {
                System.out.println(
                        "Rowkey-->" + Bytes.toString(r.getRow()) + "  " +
                                "Familiy:Quilifier-->" + Bytes.toString(CellUtil.cloneQualifier(cell)) + "  " +
                                "Value-->" + Bytes.toString(CellUtil.cloneValue(cell)));
            }
        }
    }

    /**
     * 19，SkipFilter
     * 根据整行中的每个列来做过滤，只要存在一列不满足条件，整行都被过滤掉。
     * 例如，如果一行中的所有列代表的是不同物品的重量，则真实场景下这些数值都必须大于零，我们希望将那些包含任意列值为0的行都过滤掉。
     * 在这个情况下，我们结合ValueFilter和SkipFilter共同实现该目的：
     * scan.setFilter(new SkipFilter(new ValueFilter(CompareOp.NOT_EQUAL,new BinaryComparator(Bytes.toBytes(0))));
     */
    public void SkipFilter(String tableName) throws Exception {
        Table table = conn.getTable(TableName.valueOf(tableName));
        Scan scan = new Scan();
        Filter filter = new SkipFilter(new ValueFilter(CompareOp.NOT_EQUAL, new BinaryComparator(Bytes.toBytes("102"))));
//		Filter filter = new SkipFilter(new DependentColumnFilter(Bytes.toBytes("course"), Bytes.toBytes("art"),false,CompareOp.NOT_EQUAL,new BinaryComparator(Bytes.toBytes("90"))));
        //该过滤器需要配合其他过滤器来使用
        scan.setFilter(filter);
        ResultScanner scanner = table.getScanner(scan);
        for (Result r : scanner) {
            for (Cell cell : r.rawCells()) {
                System.out.println(
                        "Rowkey-->" + Bytes.toString(r.getRow()) + "  " +
                                "Familiy:Quilifier-->" + Bytes.toString(CellUtil.cloneQualifier(cell)) + "  " +
                                "Value-->" + Bytes.toString(CellUtil.cloneValue(cell)));
            }
        }
    }

    /**
     * 20，TimestampsFilter
     * a，按时间戳搜索数据库
     * b，需设定List<Long> 存放所有需要检索的时间戳，
     */
    public void TimestampsFilter(String tableName) throws Exception {
        Table table = conn.getTable(TableName.valueOf(tableName));
        Scan scan = new Scan();
        //ls中存放所有需要查找匹配的时间戳
        List<Long> ls = new ArrayList<Long>();
        ls.add((long) 1498003561726L);
        ls.add((long) 1498003601365L);
        //java语言的整型常量默认为int型，声明long型常量可以后加”l“或”L“
        Filter filter = new TimestampsFilter(ls);
        scan.setFilter(filter);
        ResultScanner scanner = table.getScanner(scan);
//		for (Result result : scanner) {
//			if(new String(result.getRow()).equals("zhangsan01")){
//				System.out.println(new String(result.getRow())+"  "
//					+"course:art"+"-->"+new String(result.getValue(Bytes.toBytes("course"), Bytes.toBytes("art")))+"  "
//					+"course:math"+"-->"+new String(result.getValue(Bytes.toBytes("course"), Bytes.toBytes("math"))));
//			}else if(new String(result.getRow()).equals("zhangsan02")){
//				System.out.println(new String(result.getRow())+"  "
//					+"course:art"+"-->"+new String(result.getValue(Bytes.toBytes("course"), Bytes.toBytes("art")))+"  "
//					+"course:math"+"-->"+new String(result.getValue(Bytes.toBytes("course"), Bytes.toBytes("math")))+"  "
//					+"grade:"+"-->"+new String(result.getValue(Bytes.toBytes("grade"), Bytes.toBytes(""))));
//			}else{
//				System.out.println(new String(result.getRow())+"  "
//					+"course:math"+"-->"+new String(result.getValue(Bytes.toBytes("course"), Bytes.toBytes("math")))+"  "
//					+"grade:"+"-->"+new String(result.getValue(Bytes.toBytes("grade"), Bytes.toBytes(""))));
//			}
//		}
        for (Result r : scanner) {
            for (Cell cell : r.rawCells()) {
                System.out.println(
                        "Rowkey-->" + Bytes.toString(r.getRow()) + "  " +
                                "Familiy:Quilifier-->" + Bytes.toString(CellUtil.cloneQualifier(cell)) + "  " +
                                "Value-->" + Bytes.toString(CellUtil.cloneValue(cell)));
            }
        }
    }

    /**
     * 21，WhileMatchFilter
     * 相当于while执行，直到不match就break了返回了。
     */
    public void WhileMatchFilter(String tableName) throws Exception {
        Table table = conn.getTable(TableName.valueOf(tableName));
        Scan scan = new Scan();
        Filter filter = new WhileMatchFilter(new ValueFilter(CompareOp.NOT_EQUAL, new BinaryComparator(Bytes.toBytes("101"))));
        scan.setFilter(filter);
        ResultScanner scanner = table.getScanner(scan);
        for (Result r : scanner) {
            for (Cell cell : r.rawCells()) {
                System.out.println(
                        "Rowkey-->" + Bytes.toString(r.getRow()) + "  " +
                                "Familiy:Quilifier-->" + Bytes.toString(CellUtil.cloneQualifier(cell)) + "  " +
                                "Value-->" + Bytes.toString(CellUtil.cloneValue(cell)));
            }
        }
    }

    /**
     * 22，FilterList
     * 代表一个过滤器链，它可以包含一组即将应用于目标数据集的过滤器，过滤器间具有“与”FilterList.Operator.MUST_PASS_ALL和“或”FilterList.Operator.MUST_PASS_ONE关系。
     * 官网实例代码，两个“或”关系的过滤器的写法：
     */
    public void FilterList(String tableName) throws Exception {
        Table table = conn.getTable(TableName.valueOf(tableName));
        FilterList list = new FilterList(FilterList.Operator.MUST_PASS_ONE);   //数据只要满足一组过滤器中的一个就可以
        SingleColumnValueFilter filter1 = new SingleColumnValueFilter(Bytes.toBytes("course"), Bytes.toBytes("math"), CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes("89")));
        list.addFilter(filter1);
        SingleColumnValueFilter filter2 = new SingleColumnValueFilter(Bytes.toBytes("course"), Bytes.toBytes("math"), CompareOp.EQUAL, new BinaryComparator(Bytes.toBytes("66")));
        list.addFilter(filter2);
        Scan scan = new Scan();
        scan.setFilter(list);
        ResultScanner scanner = table.getScanner(scan);
        for (Result r : scanner) {
            for (Cell cell : r.rawCells()) {
                System.out.println(
                        "Rowkey-->" + Bytes.toString(r.getRow()) + "  " +
                                "Familiy:Quilifier-->" + Bytes.toString(CellUtil.cloneQualifier(cell)) + "  " +
                                "Value-->" + Bytes.toString(CellUtil.cloneValue(cell)));
            }
        }
    }

}