package com.xingwei.checkupdate;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;

public class Utils {

    /**
     * 获取MD5Apk名称
     */
    public static String getApkFilename(String apkMd5, String apkUrl) throws Exception {
        String sApkMd5 = (TextUtils.isEmpty(apkMd5) ? MD5.getString(apkUrl.getBytes("UTF-8")) : apkMd5);
        return (sApkMd5 + ".apk");
    }

    /**
     * 获取APK本地存储路径地址
     */
    public static String getApkLocalUrl(Context context, String apkFilename) {
        String extFileDir = context.getExternalFilesDir("apk").getAbsolutePath();
        return (extFileDir + File.separator + apkFilename);
    }

}
