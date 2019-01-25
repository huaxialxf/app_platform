package com.marksmile.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 
 * @Title:
 * @Description:
 * @Author : LuXinFeng
 * @Since : 2015年10月14日
 * @Version : 1.0.0
 */
public class MD5Util {
    private static final char[] DIGITS = { '0', '1', '2', '3', '4', '5', '6',
            '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };

    public static String encode(String text) {

        try {
            return encode(text.getBytes("utf-8"));
        } catch (UnsupportedEncodingException e1) {
            throw new IllegalStateException(
                    "System doesn't support your  EncodingException.");
        }

    }

    public static String encode(File file) {

        FileInputStream fis = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        int n = -1;
        byte[] bytes = new byte[1024];
        try {
            fis = new FileInputStream(file);
            while ((n = fis.read(bytes)) > -1) {
                bos.write(bytes, 0, n);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                }
            }
        }
        return encode(bos.toByteArray());
    }

    public static String encode(byte[] bodys) {

        MessageDigest msgDigest = null;

        try {

            msgDigest = MessageDigest.getInstance("MD5");

        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(
                    "System doesn't support MD5 algorithm.");
        }

        msgDigest.update(bodys);

        byte[] bytes = msgDigest.digest();

        String md5Str = new String(encodeHex(bytes));

        return md5Str;
    }

    public static char[] encodeHex(byte[] data) {

        int l = data.length;

        char[] out = new char[l << 1];
        // two characters form the hex value.
        for (int i = 0, j = 0; i < l; i++) {
            out[j++] = DIGITS[(0xF0 & data[i]) >>> 4];
            out[j++] = DIGITS[0x0F & data[i]];
        }

        return out;
    }

    public static void main(String[] args) throws IOException {
        
        String s1 = MD5Util
                .encode(new File("e:/s3tmp/20151224/cn_base_task/task_done/106_done.txt"));
        System.out.println("s1:" + s1.toUpperCase());

    }
}
