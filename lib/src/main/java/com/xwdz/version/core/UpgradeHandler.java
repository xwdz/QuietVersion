package com.xwdz.version.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.xwdz.version.QuietVersion;
import com.xwdz.version.callback.DownloadProgressListener;
import com.xwdz.version.utils.LOG;
import com.xwdz.version.utils.SignatureUtil;
import com.xwdz.version.utils.Utils;
import com.xwdz.version.callback.OnCheckVersionRules;
import com.xwdz.version.callback.OnProgressListener;
import com.xwdz.version.entry.ApkSource;
import com.xwdz.version.ui.UIAdapter;

import java.io.File;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;


/**
 * @author huangxingwei (xwdz9989@gmail.com)
 * @since 2018/5/3
 */
public class UpgradeHandler {

    private static final String TAG = UpgradeHandler.class.getSimpleName();

    private static final HashMap<String, DownloadProgressListener> MAP     = new HashMap<>();
    private static final String                                    DEF_KEY = "def.key";

    private DownloadTask    mDownloadTask;
    private UIAdapter       mUIAdapter;
    private ExecutorService mExecutorService;

    private StartDownloadReceiver mDownloadReceiver;
    private Context               mContext;

    private ApkSource            mSource;
    private VersionConfig        mVersionConfig;
    private QuietVersion.Builder mVersionBuilder;


    public static UpgradeHandler create(VersionConfig context, ApkSource entry, OkHttpClient okHttpClient, QuietVersion.Builder builder) {
        return new UpgradeHandler(context, entry, okHttpClient, builder);
    }


    private UpgradeHandler(VersionConfig versionConfig, ApkSource source, OkHttpClient okHttpClient, QuietVersion.Builder versionBuilder) {
        mVersionConfig = versionConfig;
        mVersionBuilder = versionBuilder;
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
        mDownloadTask = new DownloadTask(okHttpClient, mVersionBuilder.errorListener);
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

    private final OnProgressListener mOnProgressListener = new OnProgressListener() {

        @Override
        public void onTransfer(int percent, long currentLength, long total) {
            notifyProgress(total, currentLength, percent);
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
                ApkInstallUtils.doInstall(mContext, path, mVersionBuilder.errorListener);
            } else {
                throw new IllegalStateException("verify signature failed");
            }
        } catch (Throwable e) {
            mVersionBuilder.errorListener.listener(e);
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


    public static void startDownloaderApk(Context context) {
        Intent intent = new Intent(START_DOWNLOAD_ACTION);
        intent.putExtra(KEY_START_DOWN, FLAG_START_DOWN);
        context.sendBroadcast(intent);
    }


    public static void registerProgressListener(DownloadProgressListener listener) {
        MAP.put(DEF_KEY, listener);
    }

    public static void recycle() {
        MAP.clear();
    }

    private void notifyProgress(long total, long currentLength, int percent) {
        final DownloadProgressListener listener = MAP.get(DEF_KEY);
        if (listener != null) {
            listener.onUpdateProgress(percent, currentLength, total);
        }
    }
}
