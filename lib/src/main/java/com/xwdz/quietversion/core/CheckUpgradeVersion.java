package com.xwdz.quietversion.core;


import com.xingwei.checkupdate.BuildConfig;

/**
 * @author huangxingwei(xwdz9989 @ gmail.com)
 */
public class CheckUpgradeVersion {

    private static final CheckUpgradeVersion INSTANCE = new CheckUpgradeVersion();


    private CheckUpgradeVersion() {
    }

    public static synchronized CheckUpgradeVersion get() {
        return INSTANCE;
    }


    /**
     * 对比code是否需要更新
     *
     * @param remoteVersionCode 服务器返回版本code
     */
    public boolean check(int remoteVersionCode) {
        return remoteVersionCode > BuildConfig.VERSION_CODE;
    }

}
