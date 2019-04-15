package com.xwdz.version.core;

import com.xwdz.version.BuildConfig;
import com.xwdz.version.callback.OnCheckVersionRules;
import com.xwdz.version.entry.ApkSource;

/**
 * @author 黄兴伟 (xwdz9989@gamil.com)
 * @since 2018/9/15
 */
public class DefaultCheckVersionRules implements OnCheckVersionRules {

    @Override
    public boolean check(ApkSource apkSource) {
        return apkSource.getRemoteVersionCode() > BuildConfig.VERSION_CODE;
    }
}
