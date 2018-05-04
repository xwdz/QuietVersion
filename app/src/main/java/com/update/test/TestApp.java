package com.update.test;

import android.app.Application;

import com.xingwei.checkupdate.Utils;
import com.xwdz.okhttpgson.HttpManager;

public class TestApp extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        HttpManager.getInstance().addTAGNameProvide(new HttpManager.LogListener() {
            @Override
            public String getHttpLogTAG() {
                return Utils.LOG.TAG;
            }
        }).build();
    }
}
