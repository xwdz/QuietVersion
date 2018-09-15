package com.xwdz.version;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.net.URLEncoder;
import java.security.MessageDigest;
import java.text.DecimalFormat;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Locale;

public class Utils {

    /**
     * 获取MD5Apk名称
     */
    public static String getApkFilename(String apkUrl) throws Exception {
        String sApkMd5 = (TextUtils.isEmpty(apkUrl) ? MD5.getString(apkUrl.getBytes("UTF-8")) : apkUrl);
        if (sApkMd5.endsWith(".apk")) {
            return sApkMd5;
        } else {
            return (sApkMd5 + ".apk");
        }
    }

    /**
     * 获取APK本地存储路径地址
     */
    public static String getApkLocalUrl(Context context, String apkFilename) {
        final File file = context.getExternalFilesDir("apk");
        String extFileDir = file.getAbsolutePath();
        return (extFileDir + File.separator + apkFilename);
    }

    /**
     * 获取MD5码
     */
    public static class MD5 {
        /**
         * 日志标签
         */
        private final static String TAG = "MD5";

        /**
         * 获取指定文件内容对应的MD5码
         *
         * @param file 文件
         * @return 文件内容对应的MD5码
         * @throws Exception 异常定义
         */
        public static String getString(File file)
                throws Exception {
            return getString(loadFromFile(file));
        }

        /**
         * 获取指定文件内容对应的MD5码
         *
         * @param file 文件
         * @return 文件内容对应的MD5码
         * @throws Exception 异常定义
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
                int length = 0;
                byte[] buffer = new byte[64 * 1024];

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
                    LOG.e(TAG, "close file stream failed(Exception): "
                            + e.getMessage());
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
        public static String getString(byte[] plain) throws Exception {
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
        public static byte[] getByteArray(byte[] plain) throws Exception {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            digest.update(plain);
            return digest.digest();
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
            int k = m + n;

            if (k > b.length) {
                k = b.length;
            }

            for (int i = m; i < k; i++) {
                md5 += Integer.toHexString((b[i] & 0x000000FF) | 0xFFFFFF00).substring(6);
            }

            return md5.toLowerCase(Locale.getDefault());
        }
    }

    public static String formatNetFileSizeDescription(long size) {
        StringBuffer bytes = new StringBuffer();
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

    public static String appendHttpParams(LinkedHashMap<String, String> sLinkedHashMap) {
        Iterator<String> keys = sLinkedHashMap.keySet().iterator();
        Iterator<String> values = sLinkedHashMap.values().iterator();
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("?");

        for (int i = 0; i < sLinkedHashMap.size(); i++) {
            String value = null;
            try {
                value = URLEncoder.encode(values.next(), "utf-8");
            } catch (Exception e) {
                e.printStackTrace();
            }

            stringBuffer.append(keys.next() + "=" + value);
            if (i != sLinkedHashMap.size() - 1) {
                stringBuffer.append("&");
            }
        }

        return stringBuffer.toString();
    }

    public static class LOG {

        public static final String TAG = "QuietVersion";

        public static void i(String tag, String msg) {
            Log.i(TAG, "[" + tag + "] " + msg);
        }

        public static void e(String tag, String msg) {
            Log.e(TAG, "[" + tag + "] " + msg);
        }
    }
}
