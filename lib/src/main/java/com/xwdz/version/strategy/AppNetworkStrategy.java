package com.xwdz.version.strategy;

import android.content.Context;

import com.xwdz.version.entry.ApkSource;
import com.xwdz.version.entry.AppNetwork;

/**
 * 执行App升级的网络条件
 *
 * @author xingwei.huang (xwdz9989@gmail.com)
 * @since v1.0.0
 */
public interface AppNetworkStrategy {


    AppNetwork getAppUpgradeStrategy(ApkSource source, Context context);

    AppNetworkStrategy sDefault = new AppNetworkStrategy() {
        @Override
        public AppNetwork getAppUpgradeStrategy(ApkSource source, Context context) {
            return AppNetwork.ALL;
        }
    };

}
