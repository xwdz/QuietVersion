package com.xwdz.version.core;

import android.app.Application;

import com.xwdz.version.callback.OnCheckVersionRules;

/**
 * @author 黄兴伟 (xwdz9989@gamil.com)
 * @since 2018/9/15
 */
public class VersionConfig {

    private static final String TAG = VersionConfig.class.getSimpleName();

    private OnCheckVersionRules onCheckVersionRules;
    private boolean             forceDownload = true;
    private boolean             deleteApk;
    private Class<?>            uIClass;
    private Application         application;


    public static VersionConfig with(Application application) {
        return new VersionConfig(application);
    }

    private VersionConfig(Application application) {
        this.application = application;
    }


    public Application getApplication() {
        return application;
    }

    public OnCheckVersionRules getOnCheckVersionRules() {
        return onCheckVersionRules;
    }

    public VersionConfig setOnCheckVersionRules(OnCheckVersionRules onCheckVersionRules) {
        this.onCheckVersionRules = onCheckVersionRules;
        return this;
    }

    public boolean isForceDownload() {
        return forceDownload;
    }

    public VersionConfig setForceDownload(boolean forceDownload) {
        this.forceDownload = forceDownload;
        return this;
    }

    public boolean isDeleteApk() {
        return deleteApk;
    }

    public VersionConfig setIsDeleteApk(boolean deleteApk) {
        this.deleteApk = deleteApk;
        return this;
    }

    public Class<?> getUIActivityClass() {
        return uIClass;
    }

    public VersionConfig setUIActivityClass(Class<?> UIClass) {
        uIClass = UIClass;
        return this;
    }
}
