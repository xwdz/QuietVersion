package com.xingwei.checkupdate.core;


import com.xingwei.checkupdate.Utils;
import com.xingwei.checkupdate.callback.OnProgressListener;
import com.xwdz.okhttpgson.OkHttpRun;
import com.xwdz.okhttpgson.callback.FileCallBack;

import java.io.File;

import okhttp3.Call;

public class DownloadApkHelper {

    private static final String TAG = DownloadApkHelper.class.getSimpleName();

    private String mApkUrl;
    private String mFilePath;
    private OnProgressListener mOnProgressListener;

    public void setUrl(String url) {
        this.mApkUrl = url;
    }

    public void setFilePath(String filePath) {
        this.mFilePath = filePath;
    }

    public boolean checkApkExits(String url) {
        return checkFileExists(url);
    }

    public void setOnProgressListener(OnProgressListener onProgressListener) {
        mOnProgressListener = onProgressListener;
    }


    public void download() throws Exception {
        OkHttpRun.get(mApkUrl)
                .setCallBackToMainUIThread(true)
                .execute(new FileCallBack(createBrokenFile(mFilePath)) {

                    @Override
                    protected void onProgressListener(float percent, long currentLength, long total) {
                        if (mOnProgressListener != null) {
                            mOnProgressListener.onTransfer(percent, currentLength, total);
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
                        if (mOnProgressListener != null) {
                            mOnProgressListener.onStart();
                        }
                    }

                    @Override
                    public void onFailure(Call call, Exception e) {
                        if (mOnProgressListener != null) {
                            mOnProgressListener.onError(e);
                        }
                    }
                });
    }

    /**
     * 判断本地有无文件
     *
     * @param url 文件路径
     */
    private boolean checkFileExists(String url) {
        // 如果文件存在，则直接安装
        if (new File(url).exists()) {
            Utils.LOG.i(TAG, url + "exist, install ...");
            return true;
        } else {
            return false;
        }
    }

    /**
     * 创建APK文件
     */
    private File createBrokenFile(String localUrl) throws Exception {
        File file = new File(localUrl);

        // 如果文件不存在，那么继续判断
        // 如果父目录不存在，首先创建父目录，然后再创建文件
        // 如果父目录存在，直接创建文件
        if (!file.exists()) {
            if (!file.getParentFile().exists()) {
                if (!file.getParentFile().mkdirs()) {
                    throw new Exception("mkdirs failed");
                }
            }

            if (!file.createNewFile()) {
                throw new Exception("create file failed");
            }
        }

        return file;
    }
}
