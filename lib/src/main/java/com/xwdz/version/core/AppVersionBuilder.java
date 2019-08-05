package com.xwdz.version.core;

import android.content.Context;

import com.xwdz.version.callback.OnErrorListener;
import com.xwdz.version.notify.AppUpgradeNotification;
import com.xwdz.version.strategy.AppNetworkStrategy;
import com.xwdz.version.strategy.AppUpgradeStrategy;
import com.xwdz.version.strategy.AppVerifyStrategy;

/**
 * @author 黄兴伟 (xwdz9989@gamil.com)
 * @since 2018/9/15
 */
public class AppVersionBuilder {

    private static final String TAG = AppVersionBuilder.class.getSimpleName();


    public boolean                forceDownload      = true;
    public Class<?>               uiClass;
    public AppUpgradeStrategy     appUpgradeStrategy = AppUpgradeStrategy.sDefaultAppUpgradeStrategy;
    public AppVerifyStrategy      appVerifyStrategy  = AppVerifyStrategy.sDefault;
    public AppNetworkStrategy     appNetworkStrategy = AppNetworkStrategy.sDefault;
    public AppUpgradeNotification appUpgradeNotification;
    public OnErrorListener        errorListener;
    public Context                context;

    public AppVersionBuilder(Context context) {
        this.context = context.getApplicationContext();
    }

    public AppVersionBuilder setAppUpdatedStrategy(AppUpgradeStrategy strategy) {
        appUpgradeStrategy = strategy;
        return this;
    }

    public AppVersionBuilder setAppVerifyStrategy(AppVerifyStrategy strategy) {
        appVerifyStrategy = strategy;
        return this;
    }

    public AppVersionBuilder setAppNetworkStrategy(AppNetworkStrategy strategy) {
        appNetworkStrategy = strategy;
        return this;
    }


    /**
     * 是否强制每次都从网络上下载apk
     * true：是
     * false：当本地有Apk时，读取本地APK
     */
    public AppVersionBuilder setForceDownload(boolean forceDownload) {
        this.forceDownload = forceDownload;
        return this;
    }

    public AppVersionBuilder setErrorListener(OnErrorListener errorListener) {
        this.errorListener = errorListener;
        return this;
    }


    public AppVersionBuilder setUIActivityClass(Class<?> UIClass) {
        uiClass = UIClass;
        return this;
    }

    public AppVersionBuilder setAppUpgradeNotification(AppUpgradeNotification appUpgradeNotification){
        this.appUpgradeNotification = appUpgradeNotification;
        return this;
    }
}
