package com.bcb.util;

import org.apache.commons.codec.binary.Hex;

import java.security.MessageDigest;
/**
 * Created by kx on 2018/1/12.
 */
public class PasswordUtil {
	/**
	 * 获取十六进制字符串形式的MD5摘要
	 */
	private static String md5Hex(String src) {
		try {
			MessageDigest md5 = MessageDigest.getInstance("MD5");
			byte[] bs = md5.digest(src.getBytes());
			return new String(new Hex().encode(bs));
		} catch (Exception e) {
			return null;
		}
	}

	public static String MD5Salt(String password) {
		String psalt = md5Hex(password) + "cvYl8U";
		return md5Hex(psalt);
	}
	public static String MD5Salt(String password,String salt) {
		String psalt = md5Hex(password) + salt;
		return md5Hex(psalt);
	}
}
