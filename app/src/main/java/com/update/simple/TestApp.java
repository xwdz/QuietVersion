package com.update.simple;

import android.app.Application;

import com.xwdz.version.QuietVersion;
import com.xwdz.version.core.DefaultCheckVersionRules;
import com.xwdz.version.core.VersionConfigs;
import com.xwdz.version.ui.DefaultDialogActivity;


public class TestApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        VersionConfigs.getImpl()
                .setForceDownload(true)
                .setUIActivityClass(DefaultDialogActivity.class)
                .setOnCheckVersionRules(new DefaultCheckVersionRules());


        QuietVersion.initializeUpdater();
    }
}
