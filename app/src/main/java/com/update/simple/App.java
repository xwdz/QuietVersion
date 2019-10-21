package com.update.simple;

import android.app.Application;
import android.content.Context;

import com.xwdz.version.callback.onErrorListener;
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

        appConfig.addVerifyApkStrategy(new VerifyApkStrategy() {
            @Override
            public boolean verify(Context context, ApkSource apkSource, File file, AppConfig appConfig) {
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
            public boolean verify(Context context, ApkSource apkSource, File file, AppConfig appConfig) {
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
    }


}
