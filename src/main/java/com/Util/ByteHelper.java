package com.Util;

import com.DataPartition.DivisionByJava;
import org.apache.log4j.Logger;

import java.util.Arrays;

import static java.util.Arrays.copyOfRange;

public class ByteHelper {
    // 1 long = 8 byte; 1 int = 4 byte; 1 byte = 8 bit;
    // byte 就是 B

    private static Logger logger = Logger.getLogger(DivisionByJava.class);

    //将byte转换为一个长度为8的byte数组，数组每个值代表bit
    public static byte[] getBooleanArray(byte b) {
        byte[] array = new byte[8];
        for (int i = 7; i >= 0; i--) {
            array[i] = (byte) (b & 1);
            b = (byte) (b >> 1);
        }
        return array;
    }

    //把byte转为字符串的bit
    public static String ByteToBit(byte b) {
        return ""
                + (byte) ((b >> 7) & 0x1) + (byte) ((b >> 6) & 0x1)
                + (byte) ((b >> 5) & 0x1) + (byte) ((b >> 4) & 0x1)
                + (byte) ((b >> 3) & 0x1) + (byte) ((b >> 2) & 0x1)
                + (byte) ((b >> 1) & 0x1) + (byte) ((b >> 0) & 0x1);
    }

    public static String BytesToBit(byte[] bs) {
        String s = "";
        for (byte b : bs) {
            s = s + ByteToBit(b);
        }
        return s;
    }

    //将long转为byte[]
    public static byte[] LongToBytes(long number) {
        long temp = number;
        byte[] b = new byte[8];
        for (int i = 7; i >= 0; i--) {
            b[i] = new Long(temp & 0xff).byteValue();//将最低位保存在最低位
            temp = temp >> 8;// 向右移8位
        }

        return b;
    }

    public static byte[] LongToBytesN(long number, int n) {
        byte[] bs = copyOfRange(LongToBytes(number), 0, n);
        return bs;
    }

    //将long转为字符串的bit, 64位
    public static String longToBit(long number) {
        byte[] bytes = LongToBytes(number);
        String bits = "";
        for (int i = 0; i < 8; i++) {
            bits += ByteToBit(bytes[i]);
        }
        return bits;
    }

    //将long转为字符串的bit，返回前n位
    public static String longToBitN(long number, int n) {
        String bits = longToBit(number);

        logger.debug("bits=" + bits);

        String bitsN = bits.substring(0, n);
        return bitsN;
    }

    //将int转为字符串的bit，32位
    public static String intToBit(int number) {
        byte[] bytes = new byte[8];
        bytes = LongToBytes(number);
        String bits = "";
        for (int i = 3; i >= 0; i--) {
            bits += ByteToBit(bytes[i]);
        }
        return bits;
    }

    public static byte[] IntToBytes(int num) {
        byte[] result = new byte[4];
        result[0] = (byte) ((num >>> 24) & 0xff);//说明一
        result[1] = (byte) ((num >>> 16) & 0xff);
        result[2] = (byte) ((num >>> 8) & 0xff);
        result[3] = (byte) ((num >>> 0) & 0xff);
        return result;
    }

    //
    public static long BitToLong(String bits) {
        long ll = 0;
        long twoN = 1;

        for (int i = bits.length() - 1; i >= 0; i--) {
            ll = ll + (bits.charAt(i) - '0') * twoN;
            twoN *= 2;
        }
        return ll;
    }

    public static int BitToInt(String bits) {
        int ll = 0;
        int twoN = 1;

        for (int i = bits.length() - 1; i >= 0; i--) {
            ll = ll + (bits.charAt(i) - '0') * twoN;
            twoN *= 2;
        }
        return ll;
    }

    public static void main(String[] args) {
        byte b = 0x35; // 0011 0101
        // 输出 [0, 0, 1, 1, 0, 1, 0, 1]
        System.out.println(Arrays.toString(getBooleanArray(b)));
        System.out.println(getBooleanArray(b).length);
        // 输出 00110101
        System.out.println(ByteToBit(b).length());
        // JDK自带的方法，会忽略前面的 0
        System.out.println(Integer.toBinaryString(0x35));

        System.out.println("longToBit(1)"+longToBit(1));
        System.out.println(BitToInt(intToBit(1)));

        byte[] bytes = LongToBytes(10);
        System.out.print("LongToBytes(10)=");
        for (byte a : bytes) {
            System.out.print(ByteToBit(a));
        }

    }


}
