package com.xwdz.quietversion.callback;

import java.io.File;

/**
 * 下载进度监听器
 */
public interface OnProgressListener {
    /**
     * 进度发生变化时回调
     *
     * @param percent 进度百分比
     * @param total    总大小
     */
    void onTransfer(int percent, long currentLength, long total);

    /**
     * 传输完成
     *
     * @param file 文件
     */
    void onFinished(File file);
}
