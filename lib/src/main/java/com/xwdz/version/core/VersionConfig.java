package com.xwdz.version.core;

import android.app.Application;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.xwdz.version.R;
import com.xwdz.version.callback.OnCheckVersionRules;
import com.xwdz.version.callback.OnUIDialogNotify;

/**
 * @author 黄兴伟 (xwdz9989@gamil.com)
 * @since 2018/9/15
 */
public class VersionConfig {

    private static final String TAG = VersionConfig.class.getSimpleName();

    private OnCheckVersionRules onCheckVersionRules;
    private boolean             forceDownload;
    private boolean             deleteApk;
    private OnUIDialogNotify    onUIDialogNotify;
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

    public OnUIDialogNotify getOnUIDialogNotify() {
        return onUIDialogNotify;
    }

    public VersionConfig setOnUIDialogNotify(OnUIDialogNotify onUIDialogNotify) {
        this.onUIDialogNotify = onUIDialogNotify;
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
