package com.update.test;

import android.app.Application;

import com.xingwei.checkupdate.LOG;
import com.xwdz.okhttpgson.HttpManager;

public class TestApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        HttpManager.getInstance().addTAGNameProvide(new HttpManager.LogListener() {
            @Override
            public String getHttpLogTAG() {
                return LOG.TAG;
            }
        }).build();
    }
}
