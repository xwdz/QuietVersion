package com.xingwei.checkupdate.core;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.content.FileProvider;

import com.xingwei.checkupdate.Utils;

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
        if (!apkPath.endsWith(".apk")) {
            Utils.LOG.e(TAG, "install error path = " + apkPath);
            return;
        }

        try {
            File file = new File(apkPath);
            Intent intent = new Intent(Intent.ACTION_VIEW);

            //判断是否是AndroidN以及更高的版本
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                Uri contentUri = FileProvider.getUriForFile(mContext.getApplicationContext(), mContext.getPackageName() + ".fileProvider", file);
                intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
            } else {
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            mContext.startActivity(intent);
            Utils.LOG.i(TAG, "open apk success ");
        } catch (Exception e) {
            e.printStackTrace();
            Utils.LOG.e(TAG, "install error = " + e.toString());
        }

    }
}
