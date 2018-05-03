package com.xingwei.checkupdate.service;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

import com.xingwei.checkupdate.LOG;

import java.io.File;

/**
 * 执行Apk安装操作行为
 *
 * @author huangxingwei (xwdz9989@gmail.com)
 * @since v0.0.1
 */
public class ApkInstall {

    private static final String TAG = ApkInstall.class.getSimpleName();

    private Context mContext;

    public ApkInstall(Context context) {
        this.mContext = context;
    }


    public void install(String apkPath) {
        doInstall(apkPath);
    }

    private void doInstall(String apkPath) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(new File(apkPath)),
                "application/vnd.android.package-archive");
        mContext.startActivity(intent);

        LOG.i(TAG, "install success ");
    }


}
