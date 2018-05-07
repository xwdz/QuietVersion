package com.xingwei.checkupdate.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.text.TextUtils;

import com.xingwei.checkupdate.Quite;
import com.xingwei.checkupdate.Utils;
import com.xingwei.checkupdate.callback.OnProgressListener;
import com.xingwei.checkupdate.callback.OnUINotify;
import com.xingwei.checkupdate.entry.ApkSource;
import com.xingwei.checkupdate.ui.AbstractActivity;
import com.xingwei.checkupdate.ui.UIAdapter;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


/**
 * @author huangxingwei (xwdz9989@gmail.com)
 * @since 2018/5/3
 */
public class VersionHandler {

    private static final String TAG = VersionHandler.class.getSimpleName();

    private ApkInstall mApkInstall;
    private DownloadApkHelper mDownloadApkHelper;
    private UIAdapter mUIAdapter;
    private ExecutorService mExecutorService;

    private String mApkName;
    private String mApkPath;
    private ApkSource mApkSource;

    private StartDownloadReceiver mDownloadReceiver;
    private FragmentActivity mFragmentActivity;
    private Quite.QuiteEntry mQuiteEntry;
    /**
     * 本地是否存在缓存Apk
     */
    private boolean mApkLocalIsExist;


    public static VersionHandler get(FragmentActivity context, ApkSource apkSource, Quite.QuiteEntry entry) {
        return new VersionHandler(context, apkSource, entry);
    }

    private VersionHandler(FragmentActivity fragmentActivity, ApkSource apkSource, Quite.QuiteEntry entry) {
        mExecutorService = Executors.newFixedThreadPool(3);
        mFragmentActivity = fragmentActivity;
        checkNouNull(apkSource);
        checkURLNotNull(apkSource.getUrl());

        mApkSource = apkSource;
        mQuiteEntry = entry;

        createModule();

        mDownloadApkHelper.setUrl(mApkSource.getUrl());
        mDownloadApkHelper.setOnProgressListener(mOnProgressListener);
        checkApkNameAndLocalIsNullAndInit(mQuiteEntry.getApkPath(), mQuiteEntry.getApkName(), mApkSource.getUrl());

        //apkName，ApkPath 配置之后再set入真正的ApkPath
        mDownloadApkHelper.setFilePath(mApkPath);

        Utils.LOG.i(TAG, "handlerApk info:" + "url = " + mApkSource.getUrl() + " \napkName = " + mApkName);

        mApkLocalIsExist = mDownloadApkHelper.checkApkExits(mApkPath);
        handlerApk();
    }

    private void createModule() {
        mApkInstall = new ApkInstall(mFragmentActivity);
        mUIAdapter = new UIAdapter(mFragmentActivity);
        mDownloadApkHelper = new DownloadApkHelper();

        mDownloadReceiver = new StartDownloadReceiver();
        mFragmentActivity.getApplication().registerReceiver(mDownloadReceiver, new IntentFilter(ACTION));
        Utils.LOG.i(TAG, "组件初始化完毕 ...");
    }


    private void handlerApk() {
        if (CheckUpgradeVersion.get().check(mApkSource.getRemoteVersionCode())) {
            if (mApkLocalIsExist && !mQuiteEntry.isForceDownload()) {
                Utils.LOG.i(TAG, "读取到本地缓存APk = " + mApkPath + " 开始安装...");
                mApkInstall.install(mApkPath);
            } else {
                final OnUINotify onUINotify = mQuiteEntry.getOnUINotify();
                if (onUINotify != null) {
                    final String note = mApkSource.getNote();
                    onUINotify.show(note);


                    try {
                        FragmentManager fragmentManager = mFragmentActivity.getSupportFragmentManager();
                        if (fragmentManager != null) {
                            onUINotify.show(note, fragmentManager);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Utils.LOG.e(TAG, "get fragmentManager error = " + e);
                    }
                } else {
                    mUIAdapter.showUpgradeDialog(mApkSource.getNote());
                }

            }
        } else {
            Utils.LOG.i(TAG, "未发现最新Apk版本 " + mApkSource.toString());
        }
    }

    /**
     * 执行下载Apk操作
     */
    private void doDownload() {
        if (CheckUpgradeVersion.get().check(mApkSource.getRemoteVersionCode())) {
            /* 是否强制每次都从网络上下载最新apk */
            if (!mQuiteEntry.isForceDownload()) {
                if (mApkLocalIsExist) {
                    Utils.LOG.i(TAG, "读取到本地缓存APk = " + mApkPath + " 开始安装...");
                    mApkInstall.install(mApkPath);
                    return;
                }
            }

            try {
                mExecutorService.execute(mDownloadApkHelper);
                Utils.LOG.i(TAG, "开始下载服务器apk ...");
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            Utils.LOG.i(TAG, "未发现最新Apk版本 " + mApkSource.toString());
        }
    }

    /**
     * 给apkPath,apkName 配置默认属性
     *
     * @param apkPath 配置的apkPath
     * @param apkName 配置的apkName
     * @param url     配置的url
     */
    private void checkApkNameAndLocalIsNullAndInit(String apkPath, String apkName, String url) {
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
                Utils.getApkLocalUrl(mFragmentActivity.getApplicationContext(), mApkName) : apkPath;

    }


    private void checkURLNotNull(String url) {
        if (TextUtils.isEmpty(url)) {
            throw new NullPointerException("remote apk url cannot be null !");
        }
    }

    public static void startDownloadApk(Context context) {
        Intent intent = new Intent(ACTION);
        intent.putExtra(KEY_START_DOWN, FLAG_START_DOWN);
        context.sendBroadcast(intent);
    }


    private final OnProgressListener mOnProgressListener = new OnProgressListener() {

        @Override
        public void onTransfer(int percent, long currentLength, long total) {
            AbstractActivity.updateProgress(mFragmentActivity, total, currentLength, percent);
        }

        @Override
        public void onFinished(File file) {
            Utils.LOG.i(TAG, "install done ...");
            mApkInstall.install(file.getAbsolutePath());
        }
    };


    private static final String ACTION = "com.xwdz.checkupdate.core.VersionHandler";
    private static final String KEY_START_DOWN = "start_download";
    private static final int FLAG_START_DOWN = 1;

    private class StartDownloadReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int flag = intent.getIntExtra(KEY_START_DOWN, 0);
            if (flag == FLAG_START_DOWN) {
                doDownload();
            }
        }
    }

    public void recycle() {
        if (mDownloadReceiver != null) {
            mFragmentActivity.getApplication().unregisterReceiver(mDownloadReceiver);
        }
    }

    private void checkNouNull(ApkSource apkSource) {
        if (apkSource == null) {
            throw new NullPointerException("apkSource cannot be null");
        }
    }
}
