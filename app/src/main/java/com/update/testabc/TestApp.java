package com.update.testabc;

import android.app.Application;

import com.xwdz.version.callback.DefaultCheckVersionRules;
import com.xwdz.version.core.VersionConfigs;


public class TestApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        VersionConfigs.getImpl()
                .setOnCheckVersionRules(new DefaultCheckVersionRules());
    }
}
