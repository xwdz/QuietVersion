package com.update.testabc;

import android.app.Application;

import com.xwdz.version.callback.DefaultCheckVersionRules;
import com.xwdz.version.core.VersionConfigs;
import com.xwdz.version.ui.DefaultProgressDialogActivity;


public class TestApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        VersionConfigs.getImpl()
                .setUIActivityClass(DefaultProgressDialogActivity.class)
                .setOnCheckVersionRules(new DefaultCheckVersionRules());
    }
}
