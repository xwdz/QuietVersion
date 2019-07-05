package com.xwdz.version.callback;


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
    void onProgress(int percent, long currentLength, long total);
}
