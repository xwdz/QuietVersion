package com.update.simple;

import android.app.Application;

import com.xwdz.version.BuildConfig;
import com.xwdz.version.QuietVersion;
import com.xwdz.version.callback.NetworkParser;
import com.xwdz.version.callback.OnCheckVersionRules;
import com.xwdz.version.core.VersionConfig;
import com.xwdz.version.entry.ApkSource;
import com.xwdz.version.ui.DefaultDialogActivity;


public class TestApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        VersionConfig versionConfig = VersionConfig.with(this);
        versionConfig.setForceDownload(true)
                .setUIActivityClass(DefaultDialogActivity.class)
                .setOnCheckVersionRules(new OnCheckVersionRules() {
                    @Override
                    public boolean check(ApkSource apkSource) {
                        return apkSource.getRemoteVersionCode() > BuildConfig.VERSION_CODE;
                    }
                });
        QuietVersion.initializeUpdater(versionConfig);


    }
}
