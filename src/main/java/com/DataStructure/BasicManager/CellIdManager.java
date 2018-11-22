package com.DataStructure.BasicManager;

import com.DataStructure.Model.Point;
import com.google.common.geometry.S2CellId;

import java.util.List;
import java.util.Random;

import static com.Util.ByteHelper.longToBit;

public class CellIdManager {

    /**
     * 返回两个点的公共cellId
     */
    public static S2CellId twoPointsToCellId(Point leftPoint, Point rightPoint) {
        long l = LCPStringToLong(LCP(leftPoint.getCellId().getId(), rightPoint.getCellId().getId()));
        return new S2CellId(l);
    }


    /**
     * 返回最长公共前缀
     */
    public static String LCP(S2CellId id1, S2CellId id2) {
        String s = "";
        String s1 = longToBit(id1.id());
        String s2 = longToBit(id2.id());

        for (int i = 0; i < 64; i++) {
            if (s1.charAt(i) == s2.charAt(i)) s += s1.charAt(i);
            else break;
        }
        return s;
    }

    public static String LCP(long id1, long id2) {
        String s = "";
        String s1 = longToBit(id1);
        String s2 = longToBit(id2);

        for (int i = 0; i < 64; i++) {
            if (s1.charAt(i) == s2.charAt(i)) s += s1.charAt(i);
            else break;
        }
        return s;
    }

    public static String LCP(String s1, String s2) {
        String s = "";

        for (int i = 0; i < min(s1.length(), s2.length()); i++) {
            if (s1.charAt(i) == s2.charAt(i)) s += s1.charAt(i);
            else break;
        }
        return s;
    }

    public static String LCP(List<Long> longList) {
        if (longList.size() == 0) return "";

        String s = longToBit(longList.get(0));
        if (longList.size() == 1) return s;
        else {
            for (int i = 1; i < longList.size(); i++) {
                s = LCP(s, longToBit(longList.get(i)));
            }
        }
        return s;
    }

    public static long LCPStringToLong(String s) {
        long resl = 0;
        long temp = 1;
        int slen = s.length();
        //System.out.print("String:"+s+", leng:"+s.length());
        for (int i = 1; i <= 64 - slen; i++) temp *= 2;
        for (int i = slen - 1; i >= 0; i--, temp *= 2) {
            if (s.charAt(i) == '1') {
                resl += temp;
            }
        }
        //System.out.println(", LCPStringToLong="+resl);
        return resl;
    }

    public static int MinMaxRandom(int min, int max) {
        Random random = new Random();
        return random.nextInt(max) % (max - min + 1) + min;
    }

    public static int min(int a, int b) {
        return a < b ? a : b;
    }

    public static long getLongPreBit(long longNum, int bitNum) {
        String s = longToBit(longNum);
        long res = LCPStringToLong(s.substring(0, bitNum));
        return res;
    }

    /**
     * id1和id2转换位二进制后，前b位上是否相同
     */
    public static boolean isSameCellId(long id1, long id2, int b) {
        boolean isSame = true;
        String id1s = longToBit(id1);
        String id2s = longToBit(id2);
        for(int i = 0; i<b; i++){
            if(id1s.charAt(i) != id2s.charAt(i)){
                isSame = false;
                break;
            }
        }
        return isSame;
    }
}
