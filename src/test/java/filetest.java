import org.apache.hadoop.conf.Configuration;

import static com.Util.IOUtil.HDFSHelper.uploadFileFromLocalToHDFS;
import static com.Util.SparkInit.setSystemProperty;

public class filetest {
    public static void main(String[] args){
        setSystemProperty();
        Configuration conf = new Configuration();
        conf.set("fs.defaultFS", "hdfs://master:9000");
        conf.set("mapreduce.app-submission.cross-platform", "true");//意思是跨平台提交，在windows下如果没有这句代码会报错 "/bin/bash: line 0: fg: no job control"，去网上搜答案很多都说是linux和windows环境不同导致的一般都是修改YarnRunner.java，但是其实添加了这行代码就可以了。
        conf.set("mapreduce.framework.name", "yarn");//集群的方式运行，非本地运行。
        conf.set("yarn.resourcemanager.address", "master:8032");

        String fileRemotePath = "/home/cloud/cloud/haninput/test100_100.txt";
        //String fileRemotePath = "D:\\test100_100.txt";
        String fileHDFSPath = "hdfs://master:9000/haninput";
        uploadFileFromLocalToHDFS(fileRemotePath, fileHDFSPath, conf);
    }
}
