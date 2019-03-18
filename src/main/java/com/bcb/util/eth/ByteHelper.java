package com.bcb.util.eth;

/**
 * Created by liq on 2018/1/15.
 */
public class ByteHelper {

    /**
     * 把byte转为bit字符串
     */
    public static String byteToBitString(byte b) {
        return ""
                + (byte) ((b >> 7) & 0x1) + (byte) ((b >> 6) & 0x1)
                + (byte) ((b >> 5) & 0x1) + (byte) ((b >> 4) & 0x1)
                + (byte) ((b >> 3) & 0x1) + (byte) ((b >> 2) & 0x1)
                + (byte) ((b >> 1) & 0x1) + (byte) ((b >> 0) & 0x1);
    }

    /**
     * byte[]转换二进制字符串
     */
    public static String bytesToBitString(byte[] b) {
        String temp = "";
        for (int i = 0; i < b.length; i++) {
            temp += byteToBitString(b[i]);
        }
        return temp;
    }

    /**
     * 将字节转换成int
     */
    public static int getIntByByte(byte b) {
        return Integer.parseInt(byteToBitString(b), 2);
    }

    //高位在前，低位在后
    public static byte[] int2bytes(int num) {
        byte[] result = new byte[4];
        result[0] = (byte) ((num >>> 24) & 0xff);
        result[1] = (byte) ((num >>> 16) & 0xff);
        result[2] = (byte) ((num >>> 8) & 0xff);
        result[3] = (byte) ((num >>> 0) & 0xff);
        return result;
    }

    //高位在前，低位在后
    public static int bytes2int(byte[] bytes) {
        int result = 0;
        if (bytes.length == 4) {
            int a = (bytes[0] & 0xff) << 24;
            int b = (bytes[1] & 0xff) << 16;
            int c = (bytes[2] & 0xff) << 8;
            int d = (bytes[3] & 0xff);
            result = a | b | c | d;
        } else if (bytes.length == 2) {
            int c = (bytes[0] & 0xff) << 8;
            int d = (bytes[1] & 0xff);
            result = c | d;
        }
        return result;
    }
}
