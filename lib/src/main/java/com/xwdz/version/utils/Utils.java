package com.xwdz.version.utils;

import android.content.Context;
import android.os.Environment;
import android.text.TextUtils;

import java.io.File;
import java.net.URLEncoder;
import java.util.Iterator;
import java.util.Map;

public class Utils {

    /**
     * 获取MD5Apk名称
     */
    public static String getApkFilename(String apkUrl) throws Exception {
        String sApkMd5 = (TextUtils.isEmpty(apkUrl) ? MD5.getMD5(apkUrl.getBytes("UTF-8")) : apkUrl);
        if (sApkMd5.endsWith(".apk")) {
            return sApkMd5;
        } else {
            return (sApkMd5 + ".apk");
        }
    }

    /**
     * 获取APK本地存储路径地址
     */
    public static File getApkPath(Context context, String uniqueName) {
        String cachePath;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            cachePath = context.getExternalCacheDir().getPath();
        } else {
            cachePath = context.getCacheDir().getPath();
        }
        return new File(cachePath + File.separator + uniqueName);
    }

    public static String appendHttpParams(Map<String, String> sLinkedHashMap) {
        Iterator<String> keys         = sLinkedHashMap.keySet().iterator();
        Iterator<String> values       = sLinkedHashMap.values().iterator();
        StringBuffer     stringBuffer = new StringBuffer();
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
}
