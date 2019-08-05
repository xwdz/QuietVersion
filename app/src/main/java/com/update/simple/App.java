package com.update.simple;

import android.app.Application;
import android.content.Context;

import com.xwdz.version.BuildConfig;
import com.xwdz.version.core.AppVersionBuilder;
import com.xwdz.version.core.QuietVersion;
import com.xwdz.version.entry.ApkSource;
import com.xwdz.version.entry.Network;
import com.xwdz.version.strategy.AppNetworkStrategy;
import com.xwdz.version.strategy.AppUpgradeStrategy;
import com.xwdz.version.strategy.AppVerifyStrategy;
import com.xwdz.version.ui.DefaultDialogActivity;
import com.xwdz.version.utils.SignatureUtil;

import java.io.File;


public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();


        AppVersionBuilder appVersionBuilder = new AppVersionBuilder(getApplicationContext());

        appVersionBuilder
//                .setAppUpdatedStrategy(new AppUpgradeStrategy() {
//                    @Override
//                    public UpgradeStrategy getAppUpgradeStrategy(ApkSource source) {
//                        return UpgradeStrategy.NORMAL;
//                    }
//
//                    @Override
//                    public boolean check(ApkSource apkSource,Context context) {
//                        return apkSource.getRemoteVersionCode() > BuildConfig.VERSION_CODE;
//                    }
//                })
//                .setAppVerifyStrategy(new AppVerifyStrategy() {
//                    @Override
//                    public boolean verify(Context context, ApkSource apkSource, File file) {
//                        String md5 = SignatureUtil.getAppSignatureMD5(context);
//                        return apkSource.getMd5().toLowerCase().equals(md5.toLowerCase());
//                    }
//                })
                .setForceDownload(false)
                .setUIActivityClass(DefaultDialogActivity.class);
//                .setAppNetworkStrategy(new AppNetworkStrategy() {
//                    @Override
//                    public Network getAppUpgradeStrategy(ApkSource source, Context context) {
//                        return Network.WIFI;
//                    }
//                });
        QuietVersion.initialize(appVersionBuilder);


    }

}
