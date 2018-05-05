package com.xingwei.checkupdate.core;

import com.xwdz.okhttpgson.callback.AbstractCallBack;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Response;

/**
 * @author 黄兴伟 (xwd9989@gamil.com)
 * @since 2018/5/5
 */
public abstract class StreamCallBack extends AbstractCallBack<InputStream> {


    /**
     * 目标路径
     */
    private String mPath;
    /**
     * 目标文件名
     */
    private String mFileName;

    private File mFile;

    private int mPercent;

    public StreamCallBack(String path, String fileName) {
        this.mPath = path;
        this.mFileName = fileName;
    }


    public StreamCallBack(File file) {
        this.mFile = file;
    }

    @Override
    protected InputStream parser(Call call, Response response, boolean isMainUIThread) throws IOException {
        return response.body().byteStream();
    }


    protected File saveFile(InputStream is,long total) throws IOException {
        File resultFile = null;
        FileOutputStream fos = null;

        try {
            long sum = 0;

            if (mFile == null) {
                File dir = new File(mPath);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                resultFile = new File(dir, mFileName);
            } else {
                resultFile = mFile;
            }
            fos = new FileOutputStream(resultFile);

            int len;
            byte[] buffer = new byte[2 * 1024];
            onStart();
            while ((len = is.read(buffer)) != -1) {
                sum += len;
                fos.write(buffer, 0, len);
                float length = sum * 1.0f / total;
                int percent = (int) (length * 100);

                if (isControlCallback(percent)) {
                    mPercent = percent;
                    onProgressListener(percent, sum, total);
                } else {
                    onProgressListener(percent, sum, total);
                }
            }
            return resultFile;
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fos != null) {
                    fos.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 控制回调，一定是下载进度大于1才回调上层
     *
     * @param percent 下载百分比
     */
    protected boolean isControlCallback(int percent) {
        return ((percent - mPercent) >= 1);
    }

    protected abstract void onProgressListener(float percent, long currentLength, long total);

    protected abstract void onFinish(File file);

    protected abstract void onStart();
}
