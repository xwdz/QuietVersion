package com.xwdz.version.core;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

import com.xwdz.version.callback.DownloadProgressListener;
import com.xwdz.version.callback.OnErrorListener;
import com.xwdz.version.entry.Network;
import com.xwdz.version.network.NetworkUtils;
import com.xwdz.version.notify.AppUpgradeNotification;
import com.xwdz.version.strategy.AppNetworkStrategy;
import com.xwdz.version.strategy.AppUpgradeStrategy;
import com.xwdz.version.utils.LOG;
import com.xwdz.version.utils.Utils;
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

    private volatile static UpgradeHandler sUpgradeHandler;

    static UpgradeHandler getInstance() {
        if (sUpgradeHandler == null) {
            synchronized (UpgradeHandler.class) {
                if (sUpgradeHandler == null) {
                    sUpgradeHandler = new UpgradeHandler();
                }
            }
        }
        return sUpgradeHandler;
    }

    private ExecutorService mExecutorService;

    private UpgradeHandler() {
        mExecutorService = Executors.newCachedThreadPool();
    }


    private static final String TAG = UpgradeHandler.class.getSimpleName();

    private DownloadProgressListener mDownloadProgressListener;

    private DownloadTask mDownloadTask;
    private UIAdapter    mUIAdapter;

    private Context mContext;

    private ApkSource         mSource;
    private AppVersionBuilder mAppVersionBuilder;

    private Notification mNotification;
    private int          mId = 1;


    void initBuilder(AppVersionBuilder builder) {
        mAppVersionBuilder = builder;
        mContext = mAppVersionBuilder.context;
    }


    void launcherUpgrade(ApkSource source, OkHttpClient okHttpClient, OnErrorListener errorListener) {
        mSource = source;
        mAppVersionBuilder.errorListener = errorListener;

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

        //
        prepare();
    }

    private void initModule(OkHttpClient okHttpClient) {
        mUIAdapter = new UIAdapter(mContext);
        mDownloadTask = new DownloadTask(okHttpClient, mAppVersionBuilder.errorListener);
        LOG.i(TAG, "init module complete!");
    }


    private void prepare() {
        AppUpgradeStrategy appUpgradeStrategy = mAppVersionBuilder.appUpgradeStrategy;

        AppUpgradeStrategy.UpgradeStrategy upgradeStrategy = appUpgradeStrategy.getAppUpgradeStrategy(mSource);
        boolean                            hasNewVersion   = appUpgradeStrategy.check(mSource, mContext);

        if (hasNewVersion) {

            final AppNetworkStrategy networkStrategy = mAppVersionBuilder.appNetworkStrategy;
            Network                  network         = networkStrategy.getAppUpgradeStrategy(mSource, mContext);


            if (Network.ALL == network) {
                doUpgrade(upgradeStrategy);
            } else if (Network.MOBILE == network) {
                // network mobile type
                if (NetworkUtils.isMobileAvailable(mContext)) {
                    doUpgrade(upgradeStrategy);
                } else {
                    LOG.w(TAG, "当前网络类型不匹配无法进行升级。指定升级网络类型为:" + network);
                }
            } else if (Network.WIFI == network) {
                // wifi
                if (NetworkUtils.isWIFIAvailable(mContext)) {
                    doUpgrade(upgradeStrategy);
                } else {
                    LOG.w(TAG, "当前网络类型不匹配无法进行升级。指定升级网络类型为:" + network);
                }
            }

        } else {
            LOG.i(TAG, "not New Version!");
        }
    }


    private void doUpgrade(AppUpgradeStrategy.UpgradeStrategy upgradeStrategy) {
        if (mDownloadTask.hasLocalApk() && !mAppVersionBuilder.forceDownload) {
            String path = mDownloadTask.getDownloadPath();
            LOG.i(TAG, "read apk for cache:" + path + " start install!");
            doInstallApp(path);
        } else {
            // 正常升级策略
            if (AppUpgradeStrategy.UpgradeStrategy.NORMAL == upgradeStrategy) {
                mUIAdapter.showUpgradeDialog(mSource, mAppVersionBuilder.uiClass);
            } else if (AppUpgradeStrategy.UpgradeStrategy.SILENT == upgradeStrategy) {
                // 静默下载策略, 下载完成后调用安装界面
                doDownloadUpgradeApp();
            }
        }
    }

    private void doDownloadUpgradeApp() {
        final AppUpgradeNotification appUpgradeNotification = mAppVersionBuilder.appUpgradeNotification;
        if (appUpgradeNotification != null) {
            mNotification = appUpgradeNotification.createNotification(mSource, mContext);
            mId = appUpgradeNotification.getNotificationId();
            if (mNotification != null) {
                NotificationManager manager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
                if (manager != null) {
                    manager.notify(mId, mNotification);
                }
            }
        }

        mExecutorService.execute(mDownloadTask);
        LOG.i(TAG, "downloading apk:" + mSource.getUrl());
    }

    private final OnProgressListener mOnProgressListener = new OnProgressListener() {

        @Override
        public void onTransfer(int percent, long currentLength, long total) {
            notifyProgress(total, currentLength, percent);
        }

        @Override
        public void onFinished(File file) {
            LOG.i(TAG, "File Download complete! exist:" + file.exists());
            doInstallApp(file.getAbsolutePath());
        }
    };


    private void doInstallApp(String path) {
        try {

            boolean result = mAppVersionBuilder.appVerifyStrategy.verify(mContext, mSource, new File(path));
            if (result) {
                LOG.i(TAG, "verify success!");
                ApkInstallUtils.doInstall(mContext, path, mAppVersionBuilder.errorListener);
            } else {
                throw new IllegalStateException("verify signature failed");
            }
        } catch (Throwable e) {
            mAppVersionBuilder.errorListener.listener(e);
        }
    }


    void startDownloaderApk() {
        doDownloadUpgradeApp();
    }


    void registerProgressListener(DownloadProgressListener listener) {
        mDownloadProgressListener = listener;
    }

    void unRegisterProgressListener() {
        if (mDownloadProgressListener != null) {
            mDownloadProgressListener = null;
        }
    }


    private void notifyProgress(long total, long currentLength, int percent) {
        if (mDownloadProgressListener != null) {
            mDownloadProgressListener.onUpdateProgress(percent, currentLength, total);
        }
    }
}
