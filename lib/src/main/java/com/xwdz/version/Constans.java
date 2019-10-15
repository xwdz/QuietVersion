package com.xwdz.version;

/**
 * @author xingwei.huang (xwdz9989@gmail.com)
 * @since v1.0.0
 */
public interface Constans {


    /**
     * 最普通正常的升级流程
     *
     *  检查到新版本更新Dialog ==> 点击下载安装包,展示下载界面UI ==> 下载完成后自动调起系统安装器
     *
     */
    int TYPE_NORMAL = 0;

    /**
     * 后台下载apk
     *
     *  点击升级以后后面没有
     */
    int TYPE_BACKGROUND_DOWNLOAD_INSTALL = 1;


}
