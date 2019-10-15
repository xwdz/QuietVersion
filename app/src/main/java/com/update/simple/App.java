package com.update.simple;

import android.annotation.TargetApi;
import android.app.Application;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import com.xwdz.version.core.AppConfig;
import com.xwdz.version.core.QuietVersion;
import com.xwdz.version.entry.ApkSource;
import com.xwdz.version.entry.AppNetwork;
import com.xwdz.version.strategy.AppUpgradeStrategy;
import com.xwdz.version.strategy.VerifyApkStrategy;
import com.xwdz.version.ui.DefaultDialogActivity;
import com.xwdz.version.utils.SignatureUtil;

import java.io.File;


public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();


        AppConfig appConfig = new AppConfig(getApplicationContext());
        appConfig.setForceDownload(true);
        appConfig.setUIActivityClass(DefaultDialogActivity.class);
        appConfig.setUpgradeNetworkStrategy(AppNetwork.ALL);
        appConfig.setAppUpdatedStrategy(AppUpgradeStrategy.NORMAL);

//        appConfig.addPreviewDialogStrategy(new PreviewDialogStrategy() {
//            @Override
//            public boolean handler(Context context, AppConfig config, ApkSource source) {
//                UIAdapter.showUpgradeDialog(context, source, config.getUiClass());
//                return true;
//            }
//        });

        appConfig.addVerifyApkStrategy(new VerifyApkStrategy() {
            @Override
            public boolean handler(Context context, ApkSource apkSource, File file, AppConfig appConfig) {
                String md5 = SignatureUtil.getAppSignatureMD5(context);
                return apkSource.getMd5().toLowerCase().equals(md5.toLowerCase());
            }

            @Override
            public int priority() {
                return PRIORITY_8;
            }

            @Override
            public String getName() {
                return "MD5校验器";
            }
        });

        appConfig.addVerifyApkStrategy(new VerifyApkStrategy() {
            @Override
            public boolean handler(Context context, ApkSource apkSource, File file, AppConfig appConfig) {
                // 其他自定义安装策略
                return false;
            }

            @Override
            public int priority() {
                return PRIORITY_10;
            }

            @Override
            public String getName() {
                return "自定义策略校验器";
            }
        });

        QuietVersion.initialize(appConfig);



//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            String channelId = "chat";
//            String channelName = "聊天消息";
//            int importance = NotificationManager.IMPORTANCE_HIGH;
//            createNotificationChannel(channelId, channelName, importance);
//
//            channelId = "subscribe";
//            channelName = "订阅消息";
//            importance = NotificationManager.IMPORTANCE_DEFAULT;
//            createNotificationChannel(channelId, channelName, importance);
//        }


    }

    @TargetApi(Build.VERSION_CODES.O)
    private void createNotificationChannel(String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        NotificationManager notificationManager = (NotificationManager) getSystemService(
                NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
    }

}
