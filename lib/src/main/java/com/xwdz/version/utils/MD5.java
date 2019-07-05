package com.xwdz.version.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DecimalFormat;
import java.util.Locale;

/**
 * @author xingwei.huang (xwdz9989@gmail.com)
 * @since v1.0.0
 */
public class MD5 {

    /**
     * 获取指定文件内容对应的MD5码
     *
     * @param file 文件
     * @return 文件内容对应的MD5码
     */
    public static String getMD5(File file)
            throws Exception {
        return getMD5(loadFromFile(file));
    }

    /**
     * 获取指定文件内容对应的MD5码
     *
     * @param file 文件
     * @return 文件内容对应的MD5码
     */
    public static byte[] getByteArray(File file)
            throws Exception {
        return getByteArray(loadFromFile(file));
    }

    private static byte[] loadFromFile(File file) throws Exception {
        FileInputStream fis = null;

        try {
            fis = new FileInputStream(file);

            ByteArrayOutputStream output = new ByteArrayOutputStream();
            int                   length = 0;
            byte[]                buffer = new byte[64 * 1024];

            while (-1 != (length = fis.read(buffer))) {
                output.write(buffer, 0, length);
            }

            return output.toByteArray();
        } finally {
            try {
                if (null != fis) {
                    fis.close();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 获取指定文本内容对应的MD5码
     *
     * @param plain 文本内容
     * @return 文本内容对应的MD5码
     * @throws Exception 异常定义
     */
    public static String getMD5(byte[] plain) {
        byte[] buffer = getByteArray(plain);
        return bufferToHex(buffer, 0, buffer.length);
    }

    /**
     * 获取指定文本内容对应的MD5码
     *
     * @param plain 文本内容
     * @return 文本内容对应的MD5码
     * @throws Exception 异常定义
     */
    public static byte[] getByteArray(byte[] plain) {
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.update(plain);
            return digest.digest();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return new byte[0];

    }


    /**
     * 将数组转换成十六进制字符串
     *
     * @param b 数组
     * @param m 起始位置
     * @param n 个数
     * @return 转化后的十六进制字符串
     */
    private static String bufferToHex(byte[] b, int m, int n) {
        String md5 = "";
        int    k   = m + n;

        if (k > b.length) {
            k = b.length;
        }

        for (int i = m; i < k; i++) {
            md5 += Integer.toHexString((b[i] & 0x000000FF) | 0xFFFFFF00).substring(6);
        }

        return md5.toLowerCase(Locale.getDefault());
    }

    public static String formatNetFileSizeDescription(long size) {
        StringBuffer  bytes  = new StringBuffer();
        DecimalFormat format = new DecimalFormat("###.0");
        if (size >= 1024 * 1024 * 1024) {
            double i = (size / (1024.0 * 1024.0 * 1024.0));
            bytes.append(format.format(i)).append("G");
        } else if (size >= 1024 * 1024) {
            double i = (size / (1024.0 * 1024.0));
            bytes.append(format.format(i)).append("M");
        } else if (size >= 1024) {
            double i = (size / (1024.0));
            bytes.append(format.format(i)).append("KB");
        } else if (size < 1024) {
            if (size <= 0) {
                bytes.append("0B");
            } else {
                bytes.append((int) size).append("B");
            }
        }
        return bytes.toString();
    }


}
