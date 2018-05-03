package com.xingwei.checkupdate.service;


import com.xingwei.checkupdate.LOG;
import com.xingwei.checkupdate.callback.OnProgressListener;
import com.xwdz.okhttpgson.OkHttpRun;
import com.xwdz.okhttpgson.callback.FileCallBack;

import java.io.File;

import okhttp3.Call;

public class DownloadApkHelper {

    private static final String TAG = DownloadApkHelper.class.getSimpleName();

    private String mApkUrl;
    private String mFileName;
    private String mFilePath;
    private OnProgressListener mOnProgressListener;

    public void setUrl(String url) {
        this.mApkUrl = url;
    }

    public void setFileName(String fileName) {
        this.mFileName = fileName;
    }

    public void setFilePath(String filePath) {
        this.mFilePath = filePath;
    }

    public boolean checkApkExits(String url){
        return checkFileExists(url);
    }

    public void setOnProgressListener(OnProgressListener onProgressListener) {
        mOnProgressListener = onProgressListener;
    }


    public void download() {
        OkHttpRun.get(mApkUrl)
                .execute(new FileCallBack(mFilePath, mFileName, 0) {
                    @Override
                    protected void onProgressListener(float current, long total) {
                        if (mOnProgressListener != null) {
                            mOnProgressListener.onTransfer(current, total);
                        }
                    }

                    @Override
                    protected void onFinish(File file) {
                        if (mOnProgressListener != null) {
                            mOnProgressListener.onFinished(file);
                        }
                    }

                    @Override
                    protected void onStart() {

                    }

                    @Override
                    protected void onPause() {

                    }

                    @Override
                    public void onFailure(Call call, Exception e) {
                        if (mOnProgressListener != null) {
                            mOnProgressListener.onError(e);
                        }
                    }
                });
    }

    private boolean checkFileExists(String url) {
        // 如果文件存在，则直接安装
        if (new File(url).exists()) {
            LOG.i(TAG, " apk(" + url + ") exist, install ...");
            return true;
        } else {
            return false;
        }
    }
}
