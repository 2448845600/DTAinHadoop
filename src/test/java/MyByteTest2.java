import org.apache.hadoop.hbase.util.Bytes;

import java.nio.ByteBuffer;

public class MyByteTest2 {

    public static void main(String[] args) {
        ByteBuffer byteBuffer = ByteBuffer.allocate(8);
        byteBuffer.putInt(1234);
        byteBuffer.putInt(5678);

        byte[] rowkey = byteBuffer.array();
        System.out.print("rowkey=");
        for (byte b : rowkey) {
            System.out.print(b);
        }

        String sb = Bytes.toStringBinary(rowkey);
        String s = rowkey.toString();
        rowkey = s.getBytes();
        System.out.print("\nrowkey=");
        for (byte b : rowkey) {
            System.out.print(b);
        }
        System.out.println("\n" + sb);
        rowkey = "test".getBytes();
        System.out.println(Bytes.toString(rowkey));
        System.out.println(Bytes.toStringBinary(rowkey));
    }

}
