package com.xwdz.version.core;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.support.v4.app.NotificationCompat;

import com.xwdz.version.QuietVersion;
import com.xwdz.version.R;
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

    private static final String CHANNEL_ID   = "msg";
    private static final String CHANNEL_NAME = "下载管理";
    private static       int    sNotifyId    = 10012;


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
            createNotify();
            mExecutorService.execute(mDownloadTask);
            LOG.i(TAG, "downloading apk:" + mSource.getUrl());
        }
    }

    private NotificationCompat.Builder mNotifyBuilder;
    private NotificationManager        mNotifyManager;


    private void createNotify() {
        if (!mVersionConfig.isUseNotify()) {
            return;
        }


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH);
        }


        final Notification factoryNotification = mVersionBuilder.notificationFactory.created(mContext);
        if (factoryNotification == null) {
            mNotifyManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
            mNotifyBuilder = new NotificationCompat.Builder(mContext, CHANNEL_ID);
            Notification notification = mNotifyBuilder
                    .setContentTitle("正在更新,请稍候")
                    .setContentText("下载中")
                    .setProgress(100, 0, false)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(mVersionConfig.getSmallIcon())
                    .setLargeIcon(mVersionConfig.getLargeIcon())
                    .build();
            mNotifyManager.notify(sNotifyId, notification);
        }


    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(
                Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }

    private final OnProgressListener mOnProgressListener = new OnProgressListener() {

        @Override
        public void onTransfer(int percent, long currentLength, long total) {
            mNotifyBuilder.setProgress(100, percent, false);
            mNotifyBuilder.setContentText("下载完成");

            mNotifyManager.notify(sNotifyId, mNotifyBuilder.build());
            updateProgress(mContext, total, currentLength, percent);
        }

        @Override
        public void onFinished(File file) {
            LOG.i(TAG, "File Download complete! exist:" + file.exists());
            mNotifyBuilder.setAutoCancel(true);

            Intent intent = new Intent(mContext, ClickBroadcast.class);
            intent.putExtra("path", file.getAbsolutePath());
            intent.putExtra("md5", mSource.getMd5());
            PendingIntent pi = PendingIntent.getBroadcast(mContext, 10, intent, PendingIntent.FLAG_CANCEL_CURRENT);

            mNotifyBuilder.setContentIntent(pi);
            mNotifyManager.notify(sNotifyId, mNotifyBuilder.build());
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

    public static void setNotifyID(int id) {
        sNotifyId = id;
    }
}
