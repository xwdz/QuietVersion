package com.xwdz.version.core;

import android.content.Context;

import com.xwdz.version.callback.ErrorListener;
import com.xwdz.version.entry.AppNetwork;
import com.xwdz.version.strategy.PreviewDialogStrategy;
import com.xwdz.version.strategy.AppUpgradeStrategy;
import com.xwdz.version.strategy.CheckUpgradeStrategy;
import com.xwdz.version.strategy.VerifyApkStrategy;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * @author 黄兴伟 (xwdz9989@gamil.com)
 * @since 2018/9/15
 */
public class AppConfig {

    private static final String TAG = AppConfig.class.getSimpleName();


    private boolean              forceDownload         = true;
    private Class<?>             uiClass;
    private AppUpgradeStrategy   appUpgradeStrategy    = AppUpgradeStrategy.NORMAL;
    //    private VerifyApkStrategy    mVerifyApkStrategy    = VerifyApkStrategy.sDefault;
    private AppNetwork           mAppNetworkStrategy   = AppNetwork.ALL;
    private ErrorListener        errorListener         = ErrorListener.sDef;
    private CheckUpgradeStrategy mCheckUpgradeStrategy = CheckUpgradeStrategy.sDefault;

    private List<PreviewDialogStrategy> mAppShowDialogStrategies = new CopyOnWriteArrayList<>();
    private List<VerifyApkStrategy>     mVerifyApkStrategies     = new CopyOnWriteArrayList<>();

    private Context context;


    public AppConfig(Context context) {
        this.context = context.getApplicationContext();
    }

    public void setAppUpdatedStrategy(AppUpgradeStrategy strategy) {
        appUpgradeStrategy = strategy;
    }

    public void addVerifyApkStrategy(VerifyApkStrategy strategy) {
        mVerifyApkStrategies.add(strategy);
    }

    public void setUpgradeNetworkStrategy(AppNetwork strategy) {
        mAppNetworkStrategy = strategy;
    }

    public void addPreviewDialogStrategy(PreviewDialogStrategy showDialogStrategy) {
        mAppShowDialogStrategies.add(showDialogStrategy);
    }


    public boolean isForceDownload() {
        return forceDownload;
    }

    public Class<?> getUiClass() {
        return uiClass;
    }

    public AppUpgradeStrategy getAppUpgradeStrategy() {
        return appUpgradeStrategy;
    }


    public AppNetwork getAppNetworkStrategy() {
        return mAppNetworkStrategy;
    }

    public ErrorListener getErrorListener() {
        return errorListener;
    }

    public CheckUpgradeStrategy getCheckUpgradeStrategy() {
        return mCheckUpgradeStrategy;
    }

    public Context getContext() {
        return context;
    }

    /**
     * 是否强制每次都从网络上下载apk
     * true：是
     * false：当本地有Apk时，读取本地APK
     */
    public void setForceDownload(boolean forceDownload) {
        this.forceDownload = forceDownload;
    }

    public void setErrorListener(ErrorListener errorListener) {
        this.errorListener = errorListener;
    }


    public void setUIActivityClass(Class<?> UIClass) {
        uiClass = UIClass;
    }

    public void setCheckUpgradeStrategy(CheckUpgradeStrategy checkUpgradeStrategy) {
        mCheckUpgradeStrategy = checkUpgradeStrategy;
    }

    public List<VerifyApkStrategy> getVerifys() {
        return mVerifyApkStrategies;
    }

    public List<PreviewDialogStrategy> getPreviews() {
        return mAppShowDialogStrategies;
    }
}
