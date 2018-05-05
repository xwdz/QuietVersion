package com.xingwei.checkupdate.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import com.xingwei.checkupdate.Quite;
import com.xingwei.checkupdate.Utils;
import com.xingwei.checkupdate.callback.OnProgressListener;
import com.xingwei.checkupdate.entry.ApkSource;
import com.xingwei.checkupdate.ui.ProgressDialogActivity;
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
    private NotifyControlled mNotifyControlled;
    private UIAdapter mUIAdapter;
    private ExecutorService mExecutorService;

    private String mApkName;
    private String mApkPath;
    private ApkSource mApkSource;

    private StartDownloadReceiver mDownloadReceiver;
    private Context mContext;
    private Quite.QuiteEntry mQuiteEntry;
    /**
     * 本地是否存在缓存Apk
     */
    private boolean mApkLocalIsExist;


    public static VersionHandler get(Context context, ApkSource apkSource, Quite.QuiteEntry entry) {
        return new VersionHandler(context, apkSource, entry);
    }

    private VersionHandler(Context context, ApkSource apkSource, Quite.QuiteEntry entry) {
        mExecutorService = Executors.newFixedThreadPool(3);
        mContext = context.getApplicationContext();
        checkNouNull(apkSource);
        mApkSource = apkSource;
        mQuiteEntry = entry;
        createModule();

        mDownloadApkHelper.setUrl(mApkSource.url);
        mDownloadApkHelper.setOnProgressListener(mOnProgressListener);
        checkApkNameAndLocalIsNullAndInit(mQuiteEntry.getApkPath(), mQuiteEntry.getApkName(), mApkSource.url);

        //apkName，ApkPath 配置之后再set入真正的ApkPath
        mDownloadApkHelper.setFilePath(mApkPath);

        Utils.LOG.i(TAG, "handlerApk apk info : path = " + mApkPath + " \nurl = " + mApkSource.url + " \napkName = " + mApkName);

        mApkLocalIsExist = mDownloadApkHelper.checkApkExits(mApkPath);
        handlerApk();
    }

    private void createModule() {
        mApkInstall = new ApkInstall(mContext);
        mUIAdapter = new UIAdapter(mContext);
        mDownloadApkHelper = new DownloadApkHelper();
        mNotifyControlled = new NotifyControlled();

        mDownloadReceiver = new StartDownloadReceiver();
        mContext.registerReceiver(mDownloadReceiver, new IntentFilter(ACTION));
        Utils.LOG.i(TAG, "service created finished ...");
    }

    private void handlerApk() {
        Utils.LOG.i(TAG, "handlerApk ... ");
        if (mApkLocalIsExist && !mQuiteEntry.isForceDownload()) {
            Utils.LOG.i(TAG, "apkLocalExist do install local apk ...");
            mApkInstall.install(mApkPath);
        } else {
            mUIAdapter.showUpgradeDialog(mApkSource.note);
        }

//                int flag = mNotifyControlled.checkUpgradeRule(source.level);
//                //正常下载
//                if (flag == NotifyControlled.NORMAL) {
//
//                }
//                //强制下载
//                if (flag == NotifyControlled.FORCE) {
//                    doDownload();
//                }
    }

    /**
     * 执行下载Apk操作
     */
    private void doDownload() {
        /* 是否强制每次都从网络上下载最新apk */
        if (!mQuiteEntry.isForceDownload()) {
            if (mApkLocalIsExist) {
                Utils.LOG.i(TAG, "install local apk ...");
                mApkInstall.install(mApkPath);
                return;
            }
        }

        try {
            mExecutorService.execute(mDownloadApkHelper);
            Utils.LOG.i(TAG, "start download apk ...");
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
                Utils.getApkLocalUrl(mContext, mApkName) : apkPath;

    }


    private void checkURLNotNull(String url) {
        if (TextUtils.isEmpty(url)) {
            throw new NullPointerException("remote apk url cannot be null !");
        }
    }

    public static void notifyDownload(Context context) {
        Intent intent = new Intent(ACTION);
        intent.putExtra(KEY_START_DOWN, FLAG_START_DOWN);
        context.sendBroadcast(intent);
    }


    private final OnProgressListener mOnProgressListener = new OnProgressListener() {

        @Override
        public void onTransfer(float percent, long currentLength, long total) {
            ProgressDialogActivity.update(mContext, total, currentLength, (int) percent);
        }

        @Override
        public void onFinished(File file) {
            Utils.LOG.i(TAG, "install done ...");
            mApkInstall.install(file.getAbsolutePath());
        }
    };


    private static final String ACTION = "com.xingwei.checkupdate.core.VersionHandler";
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
            mContext.unregisterReceiver(mDownloadReceiver);
        }
    }

    private void checkNouNull(ApkSource apkSource) {
        if (apkSource == null) {
            throw new NullPointerException("apkSource cannot be null");
        }
    }
}
