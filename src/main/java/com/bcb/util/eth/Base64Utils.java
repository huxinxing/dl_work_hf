package com.bcb.util.eth;

import org.apache.commons.codec.binary.Base64;

/**
 * Created by liq on 2018/1/16.
 */
public class Base64Utils {

    /**
     * Base64编码.
     */
    public static String encode(byte[] input) {
        return Base64.encodeBase64String(input);
    }

    /**
     * Base64编码, URL安全(将Base64中的URL非法字符'+'和'/'转为'-'和'_', 见RFC3548).
     */
    public static String encodeUrlSafe(byte[] input) {
        return Base64.encodeBase64URLSafeString(input);
    }

    /**
     * Base64解码.
     */
    public static byte[] decode(String input) {
        return Base64.decodeBase64(input);
    }

}
