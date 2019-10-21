package com.xwdz.version.core;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.RequiresApi;

import com.xwdz.version.entry.AppNetwork;
import com.xwdz.version.network.NetworkUtils;
import com.xwdz.version.strategy.AppUpgradeStrategy;
import com.xwdz.version.strategy.VerifyApkStrategy;
import com.xwdz.version.ui.DefaultDialogActivity;
import com.xwdz.version.ui.OnNotifyUIListener;
import com.xwdz.version.utils.LOG;
import com.xwdz.version.callback.OnProgressListener;
import com.xwdz.version.entry.ApkSource;

import java.io.File;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;


/**
 * @author huangxingwei (xwdz9989@gmail.com)
 * @since 2018/5/3
 */
public class UpgradeHandler {


    private static final String TAG = UpgradeHandler.class.getSimpleName();

    private volatile static UpgradeHandler sUpgradeHandler;

    private Handler         mHandler = new Handler(Looper.getMainLooper());
    private ExecutorService mExecutorService;

    private OnNotifyUIListener mOnNotifyUIListener;
    private DownloadTask       mDownloadTask;

    private Context              mContext;
    private ApkSource            mSource;
    private AppConfig            mAppConfig;
    private QuietVersion.Builder mBuilder;


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


    private UpgradeHandler() {
        mExecutorService = Executors.newCachedThreadPool();
    }


    void initConfig(AppConfig config) {
        mAppConfig = config;
        mContext = mAppConfig.getContext();
    }


    void launcherUpgrade(ApkSource source, OkHttpClient okHttpClient, QuietVersion.Builder builder) {
        mSource = source;
        mBuilder = builder;


        mDownloadTask = new DownloadTask(okHttpClient, mContext);
        mDownloadTask.setUrl(mSource.getUrl());
        mDownloadTask.setOnProgressListener(mDownloadProgressListener);


        mHandler.post(new Runnable() {
            @Override
            public void run() {

                boolean hasNewVersion = mAppConfig.getCheckUpgradeStrategy().check(mSource, mContext);
                if (hasNewVersion) {
                    LOG.i(TAG, "检测到服务器发布新版本。远程版本号为:" + mSource.toString());
                    postNewVersionRunnable();
                } else {
                    LOG.i(TAG, "没有发现新版本! " + mSource.toString());
                }
            }
        });
    }


    private void postNewVersionRunnable() {
        if (checkNetwork()) {
            checkUpgrade();
        }
    }


    private boolean checkNetwork() {
        AppNetwork appNetwork = mAppConfig.getAppNetworkStrategy();
        LOG.w(TAG, "指定升级网络类型为:" + appNetwork);
        if (AppNetwork.ALL == appNetwork) {
            return true;
        } else if (AppNetwork.MOBILE == appNetwork) {
            // appNetwork mobile type
            if (NetworkUtils.isMobileAvailable(mContext)) {
                return true;
            } else {
                LOG.w(TAG, "当前网络类型不匹配无法进行升级.");
            }
        } else if (AppNetwork.WIFI == appNetwork) {
            // wifi
            if (NetworkUtils.isWIFIAvailable(mContext)) {
                return true;
            } else {
                LOG.w(TAG, "当前网络类型不匹配无法进行升级。");
            }
        }
        return false;
    }


    private void checkUpgrade() {
        final AppUpgradeStrategy upgradeStrategy = mBuilder.upgradeStrategy;
        LOG.w(TAG, "当前升级策略是:" + upgradeStrategy);

        // 如果是正常升级流程，则执行弹框策略
        if (upgradeStrategy == AppUpgradeStrategy.NORMAL) {
            showUpgradeDialog(mContext, mSource, mAppConfig.getUiClass());
        } else if (upgradeStrategy == AppUpgradeStrategy.FORCE_SILENT_DOWNLOAD_NOTIFICATION) {
            doDownloadUpgradeApp();
        }
    }

    private void doDownloadUpgradeApp() {
        if (mBuilder.upgradeStrategy == AppUpgradeStrategy.FORCE_SILENT_DOWNLOAD_NOTIFICATION
                && mDownloadTask.hasCacheApp()
                && !mAppConfig.isForceDownload()) {
            postNotifyInstall(mDownloadTask.getAppPath(), true);
            return;
        }

        LOG.i(TAG, "从服务器下载[" + mSource.getUrl() + "]文件.");
        mExecutorService.execute(mDownloadTask);
    }

    private final OnProgressListener mDownloadProgressListener = new OnProgressListener() {

        @Override
        public void onTransfer(int percent, long currentLength, long total) {
            LOG.i(TAG, "下载中,当前进度[" + percent + "]");
            if (mOnNotifyUIListener != null) {
                mOnNotifyUIListener.onUpdateProgress(percent, currentLength, total);
            }

        }


        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onFinished(File file) {
            LOG.i(TAG, "下载[" + mSource.getUrl() + "]文件是否完成:" + file.exists());
            if (mOnNotifyUIListener != null) {
                mOnNotifyUIListener.onFinished(file);
            }


            final AppUpgradeStrategy upgradeStrategy = mBuilder.upgradeStrategy;

            if (upgradeStrategy == AppUpgradeStrategy.NORMAL) {
                if (callbackVerify(file.getAbsolutePath())) {
                    AppInstallUtils.doInstall(mContext, file.getAbsolutePath(), mAppConfig.getOnErrorListener());
                }
            } else {
                if (mBuilder.baseNotification != null) {
                    mBuilder.baseNotification.initNotification(mContext, file.getAbsolutePath(), false);
                    mBuilder.baseNotification.sendNotify();
                }
            }
        }

        @Override
        public void onError(Throwable e) {
            if (mAppConfig.getOnErrorListener() != null) {
                mAppConfig.getOnErrorListener().listener(e);
            }

            if (mOnNotifyUIListener != null) {
                mOnNotifyUIListener.onUpgradeFailure(e);
            }

        }
    };


    /**
     * 检测安装校验流程
     *
     * @return true 通过，反之不通过
     */
    private boolean callbackVerify(String path) {
        try {

            if (mAppConfig.getVerify().isEmpty()) {
                mAppConfig.getVerify().add(VerifyApkStrategy.sDefault);
            }

            Collections.sort(mAppConfig.getVerify(), new Comparator<VerifyApkStrategy>() {
                @Override
                public int compare(VerifyApkStrategy o1, VerifyApkStrategy o2) {
                    return o2.priority() - o1.priority();
                }
            });

            LOG.i(TAG, "开始安装校验流程.");
            for (VerifyApkStrategy verifyApkStrategy : mAppConfig.getVerify()) {
                if (verifyApkStrategy.verify(mContext, mSource, new File(path), mAppConfig)) {
                    LOG.i(TAG, "名称是:[" + verifyApkStrategy.getName() + "]的校验器,校验成功!");
                    return true;
                } else {
                    LOG.e(TAG, "名称是:[" + verifyApkStrategy.getName() + "]的校验器,校验失败!");
                }
            }
        } catch (Throwable e) {
            mAppConfig.getOnErrorListener().listener(e);
        }
        return false;
    }


    void startDownloaderApk() {
        doDownloadUpgradeApp();
    }


    void registerProgressListener(OnNotifyUIListener listener) {
        mOnNotifyUIListener = listener;
    }

    void unRegisterProgressListener() {
        if (mOnNotifyUIListener != null) {
            mOnNotifyUIListener = null;
        }
    }

    void postNotifyInstall(String path, boolean isLocalCacheApp) {
        if (mBuilder.baseNotification != null) {
            mBuilder.baseNotification.initNotification(mContext, path, isLocalCacheApp);
            mBuilder.baseNotification.sendNotify();
        }
    }


    void callbackRequestUpgradeError(Throwable e) {
        if (mAppConfig.getOnErrorListener() != null) {
            mAppConfig.getOnErrorListener().listener(e);
        }
    }

    public static void showUpgradeDialog(Context context, ApkSource source, Class<?> activityClass) {
        if (activityClass == null) {
            DefaultDialogActivity.startActivity(context, source);
        } else {
            Intent intent = new Intent(context, activityClass);
            intent.putExtra("note", source);
            context.startActivity(intent);
        }
    }

}
