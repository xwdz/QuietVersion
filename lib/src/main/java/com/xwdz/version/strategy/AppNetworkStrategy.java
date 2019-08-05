package com.xwdz.version.strategy;

import android.content.Context;

import com.xwdz.version.entry.ApkSource;
import com.xwdz.version.entry.Network;

/**
 * 执行App升级的网络条件
 *
 * @author xingwei.huang (xwdz9989@gmail.com)
 * @since v1.0.0
 */
public interface AppNetworkStrategy {


    Network getAppUpgradeStrategy(ApkSource source, Context context);

    AppNetworkStrategy sDefault = new AppNetworkStrategy() {
        @Override
        public Network getAppUpgradeStrategy(ApkSource source, Context context) {
            return Network.ALL;
        }
    };

}
