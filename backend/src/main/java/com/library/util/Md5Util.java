package com.library.util;

import java.security.MessageDigest;

/**
 * MD5 加密工具
 */
public class Md5Util {

    public static String encrypt(String input) {
        if (input == null) {
            return null;
        }
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] bytes = md.digest(input.getBytes("UTF-8"));
            StringBuilder sb = new StringBuilder();
            for (byte b : bytes) {
                String hex = Integer.toHexString(b & 0xff);
                if (hex.length() == 1) {
                    sb.append("0");
                }
                sb.append(hex);
            }
            return sb.toString();
        } catch (Exception e) {
            throw new RuntimeException("MD5加密失败", e);
        }
    }

    public static void main(String[] args) {
        System.out.println("admin123 -> " + encrypt("admin123"));
        System.out.println("123456   -> " + encrypt("123456"));
    }
}
