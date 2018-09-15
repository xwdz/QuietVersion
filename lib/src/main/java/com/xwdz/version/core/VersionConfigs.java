package com.xwdz.version.core;

import android.content.Context;

import com.xwdz.version.Utils;
import com.xwdz.version.callback.OnCheckVersionRules;
import com.xwdz.version.callback.OnUINotify;

import java.io.File;

/**
 * @author 黄兴伟 (xwdz9989@gamil.com)
 * @since 2018/9/15
 */
public class VersionConfigs {

    private static final String TAG = VersionConfigs.class.getSimpleName();

    public static class Holder {
        static VersionConfigs INSTANCE = new VersionConfigs();
    }

    public static VersionConfigs getImpl() {
        return Holder.INSTANCE;
    }


    private OnCheckVersionRules mOnCheckVersionRules;
    private String mApkName;
    private String mApkPath;
    private boolean mForceDownload;
    private boolean mDeleteApk;
    private OnUINotify mOnUINotify;
    private Class<?> mUIClass;

    private Context mContext;
    private String mDownloaderUrl;

    void initContext(Context context, String url) {
        this.mContext = context;
        this.mDownloaderUrl = url;
    }


    public OnCheckVersionRules getOnCheckVersionRules() {
        return mOnCheckVersionRules;
    }

    public void setOnCheckVersionRules(OnCheckVersionRules onCheckVersionRules) {
        mOnCheckVersionRules = onCheckVersionRules;
    }

    public String getApkName() {
        if (mApkName == null) {
            try {
                int index = mDownloaderUrl.lastIndexOf("/");
                if (index != -1) {
                    String name = mDownloaderUrl.substring(index + 1, mDownloaderUrl.length());
                    mApkName = Utils.getApkFilename(name);
                }
            } catch (Exception e) {
                e.printStackTrace();
                mApkName = null;
            }
        }
        return mApkName;
    }

    public boolean checkApkExits() {
        try {
            File file = new File(getApkPath());
            return file.exists();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.LOG.e(TAG, "check local apk failure = " + e);
        }
        return false;
    }

    public void setApkName(String apkName) {
        mApkName = apkName;
    }

    public String getApkPath() {
        if (mApkPath == null) {
            mApkPath = Utils.getApkLocalUrl(mContext.getApplicationContext(), getApkName());
        }
        return mApkPath;
    }

    public void setApkPath(String apkPath) {
        mApkPath = apkPath;
    }

    public boolean isForceDownload() {
        return mForceDownload;
    }

    public void setForceDownload(boolean forceDownload) {
        mForceDownload = forceDownload;
    }

    public boolean isDeleteApk() {
        return mDeleteApk;
    }

    public void setIsDeleteApk(boolean deleteApk) {
        mDeleteApk = deleteApk;
    }

    public OnUINotify getOnUINotify() {
        return mOnUINotify;
    }

    public void setOnUINotify(OnUINotify onUINotify) {
        mOnUINotify = onUINotify;
    }

    public Class<?> getUIActivityClass() {
        return mUIClass;
    }

    public void setUIActivityClass(Class<?> UIClass) {
        mUIClass = UIClass;
    }
}
