/**
 * FileServer
 * @title MD.java
 * @package com.chn.fs.util
 * @author lzxz1234<lzxz1234@gmail.com>
 * @date 2014年11月26日-下午6:13:50
 * @version V1.0
 * Copyright (c) 2014 ChineseAll.com All Right Reserved
 */
package com.chn.fs.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @class MD
 * @author lzxz1234
 * @description 
 * @version v1.0
 */
public class MD {
    
    private static final char[] HEX_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};
    
    /**
     * 获得字节数组的摘要信息。
     * 
     * @param   algorithm       摘要算法
     * @param   bytes           字节数组
     * 
     * @return  字节数组的摘要信息。如果bytes是null，则返回null。
     * 
     * @throws  UnsupportedOperationException   如果算法是null、空字符串或者不支持，则抛出。
     */
    public static byte[] digest(String algorithm, byte[] bytes) {
        
        if ((algorithm == null) || "".equals(algorithm)) 
            throw new UnsupportedOperationException("Algorithm is null!");
        
        try {
            MessageDigest md = MessageDigest.getInstance(algorithm);
            if (bytes == null) return null;
            md.update(bytes);
            return md.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new UnsupportedOperationException("Algorithm[" + algorithm + "] is not supported!", e);
        }
    }
    
    /**
     * 获得字符串的摘要信息。
     * 
     * @param   algorithm       摘要算法
     * @param   string          字符串
     * 
     * @return  字符串的摘要信息。如果bytes是null，则返回null。
     * 
     * @throws  UnsupportedOperationException   如果算法是null、空字符串或者不支持，则抛出。
     */
    public static byte[] digest(String algorithm, String string) {
        
        return digest(algorithm, (string == null) ? null : string.getBytes());
    }
    
    /**
     * 获得字节数组的摘要字符串。
     * 
     * @param   algorithm       摘要算法
     * @param   bytes           字节数组
     * 
     * @return  字节数组的摘要字符串。如果bytes是null，则返回null。
     * 
     * @throws  UnsupportedOperationException   如果算法是null、空字符串或者不支持，则抛出。
     */
    public static String digestString(String algorithm, byte[] bytes) {
        
        return getHexString(digest(algorithm, bytes));
    }
    
    /**
     * 获得字符串的摘要字符串。
     * 
     * @param   algorithm       摘要算法
     * @param   bytes           字节数组
     * 
     * @return  字符串的摘要字符串。如果bytes是null，则返回null。
     * 
     * @throws  UnsupportedOperationException   如果算法是null、空字符串或者不支持，则抛出。
     */
    public static String digestString(String algorithm, String string) {
        
        return getHexString(digest(algorithm, string));
    }
    
    public static String getHexString(byte[] bytes) {
        
        if (bytes == null) return null;
        
        int len = bytes.length;
        StringBuilder result = new StringBuilder(len * 2);
        
        for (int i = 0; i < len; i++) {
            result.append(HEX_DIGITS[(bytes[i] >> 4) & 0x0f]);
            result.append(HEX_DIGITS[bytes[i] & 0x0f]);
        }
        
        return result.toString();
    }
    
    public static byte[] decodeHexString(String hex) {
        
        if(hex == null) return new byte[0];
        byte[] result = new byte[hex.length() / 2];
        for(int i = 0; i < hex.length(); i = i + 2) {
            result[i / 2] = (byte)(decodeHex(hex.charAt(i)) << 4 | decodeHex(hex.charAt(i+1)));
        }
        return result;
    }
    
    private static int decodeHex(char c) {
        
        if(c >= '0' && c <= '9') return c - '0';
        if(c >= 'a' && c <= 'f') return c - 'a' + 10;
        throw new RuntimeException("未知符号：" + c);
    }
    
}
