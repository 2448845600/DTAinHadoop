import org.apache.hadoop.hbase.util.Bytes;
import org.apache.log4j.PropertyConfigurator;

import java.nio.ByteBuffer;

import static com.Util.ByteHelper.*;
import static java.lang.System.arraycopy;
import static java.util.Arrays.copyOfRange;

public class MyByteTest {
    private byte[] key;

    MyByteTest(int i) {
        this.key = IntToBytes(i);
    }

    public static String bytesStrToString (){
        String res = "31e24594";
        res = res.getBytes().toString();
        System.out.println(res);
        return res;
    }

    public static void main(String[] args) {
        PropertyConfigurator.configure("D:\\DTAinHadoop\\src\\log4j.properties");
        MyByteTest mbt = new MyByteTest(9);
        String keystr = new String(mbt.key);
        System.out.println("mbt.key=" + mbt.key + ", toString=" + mbt.key.toString()
                + ", len=" + mbt.key.length + ", \nString(mbt.key)=" + keystr);
        
        System.out.println("\nBytes.toBytes(9)=" + Bytes.toBytes(9));
        System.out.println("Bytes.toBytes(keystr)=" + Bytes.toBytes(keystr));
        System.out.println("Bytes.toBytes(mbt.key.toString()=" + Bytes.toBytes(mbt.key.toString()));
        System.out.println("Bytes.toBytes(ByteBuffer.wrap(mbt.key))=" + Bytes.toBytes(ByteBuffer.wrap(mbt.key)));


        byte[] bytes = new byte[16];
        byte[] bt11 = LongToBytes(1);
        byte[] bt1 = copyOfRange(bt11, 0, 4);
        byte[] bt2 = LongToBytes(9);
        byte[] bt3 = IntToBytes(9);
        arraycopy(bt1, 0, bytes, 0, 4);
        arraycopy(bt2, 0, bytes, 4, 8);
        arraycopy(bt3, 0, bytes, 12, 4);
        System.out.println("\nbt1=" + bt1 + ", bt1.len=" + bt1.length
                + ", bt2=" + bt2 + ", bt2.len=" + bt2.length
                + ", bt3=" + bt3 + ", bt3.len=" + bt3.length
                + ", bytes=" + bytes + ", bytes.len=" + bytes.length
        );
        System.out.print("bt1=");
        for (byte a : bt1) {
            System.out.print(ByteToBit(a));
        }

        System.out.print("\n" + "bt2=");
        for (byte a : bt2) {
            System.out.print(ByteToBit(a));
        }

        System.out.print("\n" + "bt2=");
        for (byte a : bt3) {
            System.out.print(ByteToBit(a));
        }

        System.out.print("\n" + "bytes=");
        for (byte a : bytes) {
            System.out.print(ByteToBit(a));
        }


        System.out.println("\nsss");
        bytesStrToString();
    }
}
