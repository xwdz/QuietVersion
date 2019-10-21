package com.xwdz.version.strategy;


/**
 * @author xingwei.huang (xwdz9989@gmail.com)
 * @since v1.0.0
 */
public enum AppUpgradeStrategy {

    /**
     * 直接静默下载最新apk包，下载完成以后直接发通知消息，点击通知消息拉起系统安装
     */
    FORCE_SILENT_DOWNLOAD_NOTIFICATION,

    /**
     * 最普通正常的升级流程， 有`提示有新版本更新Dialog` 界面，下载完成后进入安装界面
     */
    NORMAL,

}



