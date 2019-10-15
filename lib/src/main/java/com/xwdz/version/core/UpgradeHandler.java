package com.xwdz.version.core;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.xwdz.version.callback.ErrorListener;
import com.xwdz.version.entry.AppNetwork;
import com.xwdz.version.network.NetworkUtils;
import com.xwdz.version.strategy.PreviewDialogStrategy;
import com.xwdz.version.strategy.AppUpgradeStrategy;
import com.xwdz.version.strategy.VerifyApkStrategy;
import com.xwdz.version.ui.OnNotifyUIListener;
import com.xwdz.version.utils.LOG;
import com.xwdz.version.utils.Utils;
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

    private Context   mContext;
    private ApkSource mSource;
    private AppConfig mAppConfig;


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

    void launcherUpgrade(ApkSource source, OkHttpClient okHttpClient, ErrorListener errorListener) {
        mSource = source;
        mAppConfig.setErrorListener(errorListener);

        mDownloadTask = new DownloadTask(okHttpClient);

        final String url = mSource.getUrl();
        mDownloadTask.setUrl(url);
        mDownloadTask.setOnProgressListener(mDownloadProgressListener);


        final int    index    = url.lastIndexOf('/');
        final String fileName = url.substring(index);
        final File   root     = Utils.getApkPath(mContext, "QuietVersion");
        if (!root.exists()) {
            root.mkdir();
        }
        mDownloadTask.setFilePath(root.getAbsolutePath() + fileName);


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


    public final void postNewVersionRunnable() {
        if (checkNetwork()) {
            checkUpgrade();
        }
    }


//    public boolean checkLocalHanNewVersion(String url) {
//        final int    index    = url.lastIndexOf('/');
//        final String fileName = url.substring(index);
//        final File   root     = Utils.getApkPath(mContext, "QuietVersion");
//        if (!root.exists()) {
//            root.mkdir();
//        }
//        return new File(root.getAbsolutePath() + fileName).exists();
//    }


    public boolean checkNetwork() {
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


    public void checkUpgrade() {
        final AppUpgradeStrategy upgradeStrategy = mAppConfig.getAppUpgradeStrategy();

        LOG.w(TAG, "当前升级策略是:" + upgradeStrategy);

        // 如果是正常升级流程，则执行弹框策略
        if (upgradeStrategy == AppUpgradeStrategy.NORMAL) {

            if (mAppConfig.getPreviews().isEmpty()) {
                mAppConfig.addPreviewDialogStrategy(PreviewDialogStrategy.sDefault);
            }

            Collections.sort(mAppConfig.getPreviews(), new Comparator<PreviewDialogStrategy>() {
                @Override
                public int compare(PreviewDialogStrategy o1, PreviewDialogStrategy o2) {
                    return o1.priority() - o2.priority();
                }
            });

            for (PreviewDialogStrategy appShowDialogStrategy : mAppConfig.getPreviews()) {
                if (appShowDialogStrategy.handler(mContext, mAppConfig, mSource)) {
                    break;
                }
            }
        }
//        else if (upgradeStrategy == AppUpgradeStrategy.FORCE_SILENT_DOWNLOAD) {
//            doDownloadUpgradeApp();
//        }

        //
//        if (mDownloadTask.hasLocalApk() && !mAppConfig.isForceDownload()) {
//            String path = mDownloadTask.getDownloadPath();
//            LOG.i(TAG, "read apk for cache:" + path + " start install!");
//            callbackVerify(path);
//        } else {
//            // 正常升级策略
//            if (AppUpgradeStrategy.UpgradeStrategy.NORMAL == upgradeStrategy) {
//
//            } else if (AppUpgradeStrategy.UpgradeStrategy.SILENT == upgradeStrategy) {
//                // 静默下载策略, 下载完成后调用安装界面
//                doDownloadUpgradeApp();
//            }
//        }
    }

    private void doDownloadUpgradeApp() {
        LOG.i(TAG, "从服务器下载[" + mSource.getUrl() + "]文件.");
        mExecutorService.execute(mDownloadTask);
    }

    private final OnProgressListener mDownloadProgressListener = new OnProgressListener() {

        @Override
        public void onTransfer(int percent, long currentLength, long total) {
            if (mOnNotifyUIListener != null) {
                mOnNotifyUIListener.onUpdateProgress(percent, currentLength, total);
            }

        }

        @Override
        public void onFinished(File file) {
            LOG.i(TAG, "下载[" + mSource.getUrl() + "]文件是否完成:" + file.exists());
            if (mOnNotifyUIListener != null) {
                mOnNotifyUIListener.onFinished(file);
            }


            if (mAppConfig.getAppUpgradeStrategy() == AppUpgradeStrategy.NORMAL) {
                if (callbackVerify(file.getAbsolutePath())) {
                    ApkInstallUtils.doInstall(mContext, file.getAbsolutePath(), mAppConfig.getErrorListener());
                }
            } else {

            }


        }

        @Override
        public void onError(Throwable e) {
            if (mAppConfig.getErrorListener() != null) {
                mAppConfig.getErrorListener().listener(e);
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

            if (mAppConfig.getVerifys().isEmpty()) {
                mAppConfig.getVerifys().add(VerifyApkStrategy.sDefault);
            }

            Collections.sort(mAppConfig.getVerifys(), new Comparator<VerifyApkStrategy>() {
                @Override
                public int compare(VerifyApkStrategy o1, VerifyApkStrategy o2) {
                    return o2.priority() - o1.priority();
                }
            });

            LOG.i(TAG, "开始安装校验流程.");
            for (VerifyApkStrategy verifyApkStrategy : mAppConfig.getVerifys()) {
                if (verifyApkStrategy.handler(mContext, mSource, new File(path), mAppConfig)) {
                    LOG.i(TAG, "名称是:[" + verifyApkStrategy.getName() + "]的校验器,校验成功!");
                    return true;
                } else {
                    LOG.e(TAG, "名称是:[" + verifyApkStrategy.getName() + "]的校验器,校验失败!");
                }
            }
        } catch (Throwable e) {
            mAppConfig.getErrorListener().listener(e);
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
}
