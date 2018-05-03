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
                    protected void onProgressListener(float current, long total) {
                        if (mOnProgressListener != null) {
                            mOnProgressListener.onTransfer(current, total);
                        }
                    }

                    @Override
                    protected void onFinish(File file) {
                        LOG.i(TAG, "finish");
                        if (mOnProgressListener != null) {
                            try {
                                completeBrokenFile(mFilePath);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            mOnProgressListener.onFinished(file);
                        }
                    }

                    @Override
                    protected void onStart() {

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
            LOG.i(TAG, " apk(" + url + ") exist, install ...");
            return true;
        } else {
            return false;
        }
    }

    /**
     * 创建断点续传中间文件，格式为
     * [localUrl].part
     * 下载完毕后，将中间文件修改为最终文件，即localUrl
     *
     * @param localUrl 指定下载文件
     * @return 断点续传中间文件，如果失败为NULL
     * @throws Exception 异常定义
     */
    private File createBrokenFile(String localUrl) throws Exception {
        String brokenUrl = (localUrl + ".part");
        File file = new File(brokenUrl);

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

    /**
     * 成功完成文件下载后的处理过程
     * 把.part的中间下载文件重命名为正式指定的下载文件名
     *
     * @param localUrl 下载文件的全路径名
     *                 Exception 异常定义
     */
    private void completeBrokenFile(String localUrl) throws Exception {
        String brokenUrl = (localUrl + ".part");
        File file = new File(localUrl);
        File brokenFile = new File(brokenUrl);

        if (!brokenFile.exists()) {
            throw new Exception("broken file not exist");
        }

        // 删除掉原来文件
        if (file.exists()) {
            file.delete();
        }

        if (!brokenFile.renameTo(file)) {
            throw new Exception("broken file rename failed");
        }
    }
}
