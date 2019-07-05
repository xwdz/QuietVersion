package com.xwdz.version.core;

import android.app.Application;

import com.xwdz.version.utils.LOG;
import com.xwdz.version.utils.Utils;
import com.xwdz.version.callback.OnCheckVersionRules;
import com.xwdz.version.callback.OnUIDialogNotify;

import java.io.File;

/**
 * @author 黄兴伟 (xwdz9989@gamil.com)
 * @since 2018/9/15
 */
public class VersionConfig {

    private static final String TAG = VersionConfig.class.getSimpleName();

    private OnCheckVersionRules mOnCheckVersionRules;
    private boolean             mForceDownload;
    private boolean             mDeleteApk;
    private OnUIDialogNotify    mOnUIDialogNotify;
    private Class<?>            mUIClass;

    private Application mApplication;


    public static VersionConfig with(Application application) {
        return new VersionConfig(application);
    }

    private VersionConfig(Application application) {
        mApplication = application;
    }


    public Application getApplication() {
        return mApplication;
    }

    public OnCheckVersionRules getOnCheckVersionRules() {
        return mOnCheckVersionRules;
    }

    public VersionConfig setOnCheckVersionRules(OnCheckVersionRules onCheckVersionRules) {
        mOnCheckVersionRules = onCheckVersionRules;
        return this;
    }

    public boolean isForceDownload() {
        return mForceDownload;
    }

    public VersionConfig setForceDownload(boolean forceDownload) {
        mForceDownload = forceDownload;
        return this;
    }

    public boolean isDeleteApk() {
        return mDeleteApk;
    }

    public VersionConfig setIsDeleteApk(boolean deleteApk) {
        mDeleteApk = deleteApk;
        return this;
    }

    public OnUIDialogNotify getOnUIDialogNotify() {
        return mOnUIDialogNotify;
    }

    public VersionConfig setOnUIDialogNotify(OnUIDialogNotify onUIDialogNotify) {
        mOnUIDialogNotify = onUIDialogNotify;
        return this;
    }

    public Class<?> getUIActivityClass() {
        return mUIClass;
    }

    public VersionConfig setUIActivityClass(Class<?> UIClass) {
        mUIClass = UIClass;
        return this;
    }
}
