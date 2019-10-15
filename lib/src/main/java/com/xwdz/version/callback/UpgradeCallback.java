package com.xwdz.version.callback;

import com.xwdz.version.entry.ApkSource;

import java.io.File;

/**
 * @author xingwei.huang (xwdz9989@gmail.com)
 * @since v1.0.0
 */
public interface UpgradeCallback {


    /**
     * 开始检查是否有新版本
     */
    void onBeginCheckVersion();


    /**
     * 当前不包含新版本
     */
    void onNoVersion();

    /**
     * 包含新版本回调
     */
    void onHasNewVersion(ApkSource source);

    /**
     * 开始下载
     */
    void onBeginDownload(ApkSource source);

    /**
     * 下载进度回调
     *
     * @param total            总长度
     * @param percent          当前占比
     * @param downloaderLength 当前下载长度
     */
    void onDownloadProgress(long total, int percent, long downloaderLength);

    /**
     * 下载新Apk完成
     */
    void onDownloadCompleted(ApkSource source, File file);


    UpgradeCallback sDefCallback = new UpgradeCallback() {
        @Override
        public void onBeginCheckVersion() {

        }

        @Override
        public void onNoVersion() {

        }

        @Override
        public void onHasNewVersion(ApkSource source) {

        }

        @Override
        public void onBeginDownload(ApkSource source) {

        }

        @Override
        public void onDownloadProgress(long total, int percent, long downloaderLength) {

        }

        @Override
        public void onDownloadCompleted(ApkSource source, File file) {

        }
    };

}
