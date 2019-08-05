package com.xwdz.version.strategy;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

import com.xwdz.version.entry.ApkSource;

/**
 * @author xingwei.huang (xwdz9989@gmail.com)
 * @since v1.0.0
 */
public interface AppUpgradeStrategy {

    /**
     * 升级策略
     */
    UpgradeStrategy getAppUpgradeStrategy(ApkSource source);

    /**
     * 执行升级的必要条件.
     * <p>
     * 默认： apkSource.getRemoteVersionCode() > BuildConfig.VERSION_CODE
     */
    boolean check(ApkSource apkSource, Context context);


    AppUpgradeStrategy sDefaultAppUpgradeStrategy = new AppUpgradeStrategy() {
        @Override
        public UpgradeStrategy getAppUpgradeStrategy(ApkSource source) {
            return UpgradeStrategy.NORMAL;
        }

        @Override
        public boolean check(ApkSource apkSource, Context context) {
            PackageManager pm          = context.getPackageManager();
            int            versioncode = 0;
            try {
                PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
                versioncode = pi.versionCode;
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
            }
            return apkSource.getRemoteVersionCode() > versioncode;
        }
    };

    enum UpgradeStrategy {
        /**
         * 直接下载最新apk包，下载完成后直接进入安装界面，没有 `提示有新版本更新Dialog` 界面
         * 适用所有网络条件
         */
        SILENT,

        /**
         * 正常升级流程， 有`提示有新版本更新Dialog` 界面，下载完成后进入安装界面
         */
        NORMAL

    }

}



