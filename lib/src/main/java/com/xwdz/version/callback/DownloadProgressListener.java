package com.xwdz.version.callback;


import java.io.File;

/**
 * 内层使用的接口下载进度监听器
 */
public interface DownloadProgressListener {
    /**
     * 进度发生变化时回调
     *
     * @param percent 进度百分比
     * @param total   总大小
     */
    void onUpdateProgress(int percent, long currentLength, long total);

    /**
     * 下载完成
     * @param file
     */
    void onFinished(File file);

    /**
     *  升级过程中发生错误
     * @param error
     */
    void onError(Throwable error);
}
