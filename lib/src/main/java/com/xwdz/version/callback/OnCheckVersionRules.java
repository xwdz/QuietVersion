package com.xwdz.version.callback;

import com.xwdz.version.entry.ApkSource;

/**
 * @author 黄兴伟 (xwdz9989@gamil.com)
 * @since 2018/9/15
 */
public interface OnCheckVersionRules {

    /**
     * 开发者可自定义版本检查规则
     *
     * @param remoteVersionCode 远程版本号
     * @return true 进行升级,false 则不升级
     */
    boolean check(ApkSource remoteVersionCode);
}
