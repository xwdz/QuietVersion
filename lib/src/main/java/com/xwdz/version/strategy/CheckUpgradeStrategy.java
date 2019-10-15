package com.xwdz.version.strategy;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.xwdz.version.entry.ApkSource;

/**
 * @author xingwei.huang (xwdz9989@gmail.com)
 * @since v1.0.0
 */
public interface CheckUpgradeStrategy extends BaseStrategy {

    /**
     * 执行升级的必要条件.
     * <p>
     * 默认： apkSource.getRemoteVersionCode() > BuildConfig.VERSION_CODE
     */
    boolean check(ApkSource apkSource, Context context);

    CheckUpgradeStrategy sDefault = new CheckUpgradeStrategy() {
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

        @Override
        public int priority() {
            return PRIORITY_10;
        }

        @Override
        public String getName() {
            return "Default:" + toString();
        }
    };
}
