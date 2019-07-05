package com.xwdz.version.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import com.xwdz.version.callback.OnErrorListener;
import com.xwdz.version.utils.LOG;
import com.xwdz.version.utils.SignatureUtil;
import com.xwdz.version.utils.Utils;
import com.xwdz.version.callback.OnCheckVersionRules;
import com.xwdz.version.callback.OnProgressListener;
import com.xwdz.version.entry.ApkSource;
import com.xwdz.version.ui.UIAdapter;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;


/**
 * @author huangxingwei (xwdz9989@gmail.com)
 * @since 2018/5/3
 */
public class UpgradeHandler {

    private static final String TAG = UpgradeHandler.class.getSimpleName();

    private DownloadTask    mDownloadTask;
    private UIAdapter       mUIAdapter;
    private ExecutorService mExecutorService;

    private StartDownloadReceiver mDownloadReceiver;
    private Context               mContext;

    private ApkSource       mSource;
    private VersionConfig   mVersionConfig;
    private OnErrorListener mOnErrorListener;


    public static UpgradeHandler create(VersionConfig context, ApkSource entry, OkHttpClient okHttpClient, OnErrorListener listener) {
        return new UpgradeHandler(context, entry, okHttpClient, listener);
    }


    private UpgradeHandler(VersionConfig versionConfig, ApkSource source, OkHttpClient okHttpClient, OnErrorListener listener) {
        mVersionConfig = versionConfig;
        mOnErrorListener = listener;
        mContext = versionConfig.getApplication().getApplicationContext();
        mExecutorService = Executors.newFixedThreadPool(3);
        mSource = source;

        initModule(okHttpClient);

        final String url = mSource.getUrl();
        mDownloadTask.setUrl(url);
        mDownloadTask.setOnProgressListener(mOnProgressListener);

        final int    index    = url.lastIndexOf('/');
        final String fileName = url.substring(index);
        final File   root     = Utils.getApkPath(mContext, "QuietVersion");
        if (!root.exists()) {
            root.mkdir();
        }
        mDownloadTask.setFilePath(root.getAbsolutePath() + fileName);

        handler();
    }


    private void initModule(OkHttpClient okHttpClient) {
        mUIAdapter = new UIAdapter(mContext);
        mDownloadTask = new DownloadTask(okHttpClient, mOnErrorListener);
        mDownloadReceiver = new StartDownloadReceiver();
        mContext.registerReceiver(mDownloadReceiver, new IntentFilter(START_DOWNLOAD_ACTION));
        LOG.i(TAG, "init module complete!");
    }


    private void handler() {
        OnCheckVersionRules onCheckVersionRules = mVersionConfig.getOnCheckVersionRules();
        if (onCheckVersionRules != null) {
            boolean handler = onCheckVersionRules.check(mSource);
            if (handler) {
                if (mDownloadTask.hasLocalApk() && !mVersionConfig.isForceDownload()) {
                    String path = mDownloadTask.getDownloadPath();
                    LOG.i(TAG, "read apk for cache:" + path + " start install!");
                    doInstall(path);
                } else {
                    mUIAdapter.showUpgradeDialog(mSource, mVersionConfig.getUIActivityClass());
                }
            } else {
                LOG.i(TAG, "not New Version!");
            }
        } else {
            LOG.i(TAG, "not New Version " + mSource.getUrl());
        }
    }

    /**
     * 执行下载Apk操作
     */
    private void doDownload() {
        OnCheckVersionRules onCheckVersionRules = mVersionConfig.getOnCheckVersionRules();
        if (onCheckVersionRules != null) {
            if (!mVersionConfig.isForceDownload()) {
                if (mDownloadTask.hasLocalApk()) {
                    LOG.i(TAG, "real local APk = " + mDownloadTask.getDownloadPath() + " start install!");
                    doInstall(mDownloadTask.getDownloadPath());
                    return;
                }
            }

            mExecutorService.execute(mDownloadTask);
            LOG.i(TAG, "downloading apk:" + mSource.getUrl());
        }
    }


    private void checkURLNotNull(String url) {
        if (TextUtils.isEmpty(url)) {
            throw new NullPointerException("remote apk url cannot be null !");
        }
    }

    private final OnProgressListener mOnProgressListener = new OnProgressListener() {

        @Override
        public void onTransfer(int percent, long currentLength, long total) {
            LOG.i(TAG, "fse:" + percent);
            updateProgress(mContext, total, currentLength, percent);
        }

        @Override
        public void onFinished(File file) {
            LOG.i(TAG, "File Download complete! exist:" + file.exists());
            doInstall(file.getAbsolutePath());
        }
    };

    private boolean checkMD5() {
        String md5 = SignatureUtil.getAppSignatureMD5(mContext);
        return mSource.getMd5().equals(md5);
    }


    private void doInstall(String path) {
        try {
            if (checkMD5()) {
                ApkInstallUtils.doInstall(mContext, path, mOnErrorListener);
            } else {
                throw new IllegalStateException("verify signature failed");
            }
        } catch (Throwable e) {
            mOnErrorListener.listener(e);
        }
    }


    private static final String START_DOWNLOAD_ACTION = "com.xwdz.version.core.UpgradeHandler";
    private static final String KEY_START_DOWN        = "start_download";
    private static final int    FLAG_START_DOWN       = 1;

    public class StartDownloadReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            int flag = intent.getIntExtra(KEY_START_DOWN, 0);
            if (flag == FLAG_START_DOWN) {
                doDownload();
            }
        }
    }


    private static final String UPDATE_PROGRESSBAR_ACTION = "com.xwdz.qversion.ui.DefaultDialogActivity";
    private static final String KEY_TOTAL                 = "total";
    private static final String KEY_CURRENT_LENGTH        = "current.length";
    private static final String KEY_PERCENT               = "percent";

    public abstract static class ProgressReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            long total         = intent.getLongExtra(KEY_TOTAL, 0);
            long currentLength = intent.getLongExtra(KEY_CURRENT_LENGTH, 0);
            int  percent       = intent.getIntExtra(KEY_PERCENT, 0);

            onUpdateProgress(total, currentLength, percent);

        }

        public abstract void onUpdateProgress(long total, long currentLength, int percent);
    }


    public static void startDownloaderApk(Context context) {
        Intent intent = new Intent(START_DOWNLOAD_ACTION);
        intent.putExtra(KEY_START_DOWN, FLAG_START_DOWN);
        context.sendBroadcast(intent);
    }

    public static void registerProgressbarReceiver(Context context, ProgressReceiver progressReceiver) {
        if (progressReceiver != null) {
            context.getApplicationContext().registerReceiver(progressReceiver, new IntentFilter(UpgradeHandler.UPDATE_PROGRESSBAR_ACTION));
        }
    }

    public static void unregisterProgressbarReceiver(Context context, ProgressReceiver progressReceiver) {
        if (progressReceiver != null) {
            context.getApplicationContext().unregisterReceiver(progressReceiver);
        }
    }

    private static void updateProgress(Context context, long total, long currentLength, int percent) {
        Intent intent = new Intent(UPDATE_PROGRESSBAR_ACTION);
        intent.putExtra(KEY_TOTAL, total);
        intent.putExtra(KEY_CURRENT_LENGTH, currentLength);
        intent.putExtra(KEY_PERCENT, percent);
        context.sendBroadcast(intent);
    }


    public void recycle() {
        if (mDownloadReceiver != null) {
            mContext.unregisterReceiver(mDownloadReceiver);
        }
    }
}
