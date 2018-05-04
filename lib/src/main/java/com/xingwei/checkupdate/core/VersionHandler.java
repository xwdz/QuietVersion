package com.xingwei.checkupdate.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.xingwei.checkupdate.Utils;
import com.xingwei.checkupdate.callback.OnProgressListener;
import com.xingwei.checkupdate.entry.ApkResultSource;
import com.xingwei.checkupdate.ui.ProgressDialogActivity;
import com.xingwei.checkupdate.ui.UIAdapter;

import java.io.File;

/**
 * @author huangxingwei (xwdz9989@gmail.com)
 * @since 2018/5/3
 */
public class VersionHandler {

    private static final String TAG = VersionHandler.class.getSimpleName();

    private ApkInstall mApkInstall;
    private DownloadApkHelper mDownloadApkHelper;
    private NotifyControlled mNotifyControlled;
    private UIAdapter mUIAdapter;

    private String mApkName;
    private String mApkPath;

    private StartDownloadReceiver mDownloadReceiver;

    private Context mContext;
    private ApkResultSource mApkResultSource;

    private VersionHandler(Context context, ApkResultSource apkResultSource) {
        mContext = context.getApplicationContext();
        mApkResultSource = apkResultSource;
        create();
        handlerApk();
    }

    private void create() {
        mApkInstall = new ApkInstall(mContext);
        mUIAdapter = new UIAdapter(mContext);
        mDownloadApkHelper = new DownloadApkHelper();
        mNotifyControlled = new NotifyControlled();

        mDownloadReceiver = new StartDownloadReceiver();
        mContext.registerReceiver(mDownloadReceiver, new IntentFilter(ACTION));
        Utils.LOG.i(TAG, "service created ...");
    }

    private final OnProgressListener mOnProgressListener = new OnProgressListener() {

        @Override
        public void onTransfer(float percent, long currentLength, long total) {
            ProgressDialogActivity.update(mContext, total, currentLength, (int) percent);
        }

        @Override
        public void onError(Exception e) {
            Utils.LOG.e(TAG, "progress exception = " + e.toString());
        }

        @Override
        public void onFinished(File file) {
            Utils.LOG.i(TAG, "install done ...");
            mApkInstall.install(file.getAbsolutePath());
        }

        @Override
        public void onStart() {

        }
    };


    private static final String ACTION = "com.xingwei.checkupdate.core.VersionHandler";
    private static final String KEY_START_DOWN = "start_download";

    public static final int FLAG_START_DOWN = 1;

    private class StartDownloadReceiver extends BroadcastReceiver {

        private final String TAG = StartDownloadReceiver.class.getSimpleName();

        @Override
        public void onReceive(Context context, Intent intent) {
            Utils.LOG.i(TAG, "receiver download request ...");
            int flag = intent.getIntExtra(KEY_START_DOWN, 0);
            if (flag == FLAG_START_DOWN) {
                doDownload();
            }
        }
    }


    private void handlerApk() {
        if (mApkResultSource != null) {
            Utils.LOG.i(TAG, "handlerApk ... ");
            final ApkResultSource source = mApkResultSource;
            if (source != null) {
                checkURLNotNull(source.url);
                checkApkNameAndLocalIsNull(source.apkPath, source.apkName, source.url);

                //配置远程下载信息
                mDownloadApkHelper.setUrl(source.url);
                mDownloadApkHelper.setFilePath(mApkPath);
                mDownloadApkHelper.setOnProgressListener(mOnProgressListener);
                Utils.LOG.i(TAG, "doDownload apk info : path = " + mApkPath + " \nurl = " + source.url + " \napkName = " + mApkName);

                ProgressDialogActivity.update(mContext, 1, 1, (int) 1);
                int flag = mNotifyControlled.checkUpgradeRule(source.level);
                //正常下载
                if (flag == NotifyControlled.NORMAL) {
                    mUIAdapter.showUpgradeDialog(source.note);
                }

                //强制下载
                if (flag == NotifyControlled.FORCE) {
                    doDownload();
                }

            }

        }
    }

    /**
     * 执行下载Apk操作
     */
    private void doDownload() {
        //如果APk 本地存在则直接安装
        if (mDownloadApkHelper.checkApkExits(mApkPath)) {
            Utils.LOG.i(TAG, "install local apk ...");
            mApkInstall.install(mApkPath);
            return;
        }


        try {
            mDownloadApkHelper.download();
            Utils.LOG.i(TAG, "startActivity doDownload apk ...");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 给apkPath,apkName 配置默认属性
     *
     * @param apkPath 配置的apkPath
     * @param apkName 配置的apkName
     * @param url     配置的url
     */
    private void checkApkNameAndLocalIsNull(String apkPath, String apkName, String url) {
        if (TextUtils.isEmpty(apkName)) {
            try {
                int index = url.lastIndexOf("/");
                if (index != -1) {
                    String name = url.substring(index + 1, url.length());
                    mApkName = Utils.getApkFilename(name);
                }
            } catch (Exception e) {
                e.printStackTrace();
                mApkName = null;
            }
        } else {
            mApkName = apkName;
        }

        mApkPath = TextUtils.isEmpty(apkPath) ?
                Utils.getApkLocalUrl(mContext, mApkName) : apkPath;

    }


    private void checkURLNotNull(String url) {
        if (TextUtils.isEmpty(url)) {
            throw new NullPointerException("remote apk url cannot be null !");
        }
    }


    public static VersionHandler get(Context context, ApkResultSource apkResultSource) {
        return new VersionHandler(context, apkResultSource);
    }

    public static void notifyDownload(Context context, int flag) {
        Intent intent = new Intent(ACTION);
        intent.putExtra(KEY_START_DOWN, flag);
        context.sendBroadcast(intent);
        Utils.LOG.i(TAG, "notify ... ");
    }

    public void destroy() {
        if (mDownloadReceiver != null) {
            mContext.unregisterReceiver(mDownloadReceiver);
        }
    }
}
