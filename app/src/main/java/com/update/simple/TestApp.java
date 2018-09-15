package com.update.simple;

import android.app.Application;

import com.xwdz.version.core.DefaultCheckVersionRules;
import com.xwdz.version.core.VersionConfigs;
import com.xwdz.version.ui.DefaultProgressDialogActivity;


public class TestApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        VersionConfigs.getImpl()
                .setForceDownload(true)
                .setApkPath(String apkPath)
                .setApkName(String apkName)
                .setUIActivityClass(DefaultProgressDialogActivity.class)
                .setOnCheckVersionRules(new DefaultCheckVersionRules());
    }
}
