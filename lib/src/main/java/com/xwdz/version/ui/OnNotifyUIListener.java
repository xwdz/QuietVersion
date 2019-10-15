package com.xwdz.version.ui;

import com.xwdz.version.entry.ApkSource;

import java.io.File;

/**
 * @author xingwei.huang (xwdz9989@gmail.com)
 * @since v1.0.0
 */
public interface OnNotifyUIListener {

    /**
     * 进度发生变化时回调
     *
     * @param percent 进度百分比
     * @param total   总大小
     */
    void onUpdateProgress(int percent, long currentLength, long total);

    /**
     * 下载完成
     *
     * @param file
     */
    void onFinished(File file);

    /**
     * 升级过程中发生错误
     *
     * @param error
     */
    void onUpgradeFailure(Throwable error);

    /**
     * 是否有新版本
     */
    void onHadNewVersion(boolean isNewVersion, ApkSource apkSource);
}
