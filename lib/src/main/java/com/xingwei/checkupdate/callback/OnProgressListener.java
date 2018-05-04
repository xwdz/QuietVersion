package com.xingwei.checkupdate.callback;

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
    void onTransfer(float percent, long currentLength, long total);

    /**
     * 返回结果回调
     *
     * @param e 错误对象
     */
    void onError(Exception e);

    /**
     * 传输完成
     *
     * @param file 文件
     */
    void onFinished(File file);

    /**
     * 传输开始
     */
    void onStart();
}
