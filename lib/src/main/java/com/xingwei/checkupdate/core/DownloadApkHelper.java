package com.xingwei.checkupdate.core;


import com.xingwei.checkupdate.Utils;
import com.xingwei.checkupdate.callback.OnProgressListener;
import com.xwdz.okhttpgson.HttpManager;
import com.xwdz.okhttpgson.OkHttpRun;

import java.io.File;
import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.ForwardingSource;
import okio.Okio;
import okio.Source;

/**
 * apk下载器
 */
public class DownloadApkHelper implements Runnable {

    private static final String TAG = DownloadApkHelper.class.getSimpleName();

    /**
     * 下载URL
     */
    private String mApkUrl;
    /**
     * 存放apk路径
     */
    private String mFilePath;

    private OnProgressListener mOnProgressListener;


    void setUrl(String url) {
        this.mApkUrl = url;
    }

    void setFilePath(String filePath) {
        this.mFilePath = filePath;
    }

    void setOnProgressListener(OnProgressListener onProgressListener) {
        mOnProgressListener = onProgressListener;
    }

    private void download() {
        initHttpClint();

        try {
            File file = createBrokenFile(mFilePath);
            Response response = OkHttpRun.get(mApkUrl)
                    .execute();

            final BufferedSink sink = Okio.buffer(Okio.sink(file));
            sink.writeAll(response.body().source());
            sink.close();

            if (mOnProgressListener != null) {
                mOnProgressListener.onFinished(file);
            }
        } catch (Exception e) {
            Utils.LOG.e(TAG, "download file = " + e);
        }
    }

    private void initHttpClint() {
        HttpManager.getInstance().addNetworkInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                final Response interceptor = chain.proceed(chain.request());
                return interceptor.newBuilder()
                        .body(new ProgressBody(interceptor.body(), mOnProgressListener))
                        .build();
            }
        });

        HttpManager.getInstance().build();
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

    /**
     * 判断本地是否有缓存apk
     */
    boolean checkApkExits(String apkPath) {
        try {
            File file = new File(apkPath);
            return file.exists();
        } catch (Exception e) {
            e.printStackTrace();
            Utils.LOG.e(TAG, "check local apk failure = " + e);
        }
        return false;
    }

    public static class ProgressBody extends ResponseBody {

        private final ResponseBody mResponseBody;
        private OnProgressListener mOnProgressListener;
        private BufferedSource mBufferedSource;

        ProgressBody(ResponseBody responseBody, OnProgressListener progressListener) {
            this.mResponseBody = responseBody;
            this.mOnProgressListener = progressListener;
        }

        @Override
        public MediaType contentType() {
            return mResponseBody.contentType();
        }

        @Override
        public long contentLength() {
            return mResponseBody.contentLength();
        }

        @Override
        public BufferedSource source() {
            if (mBufferedSource == null) {
                mBufferedSource = Okio.buffer(source(mResponseBody.source()));
            }
            return mBufferedSource;
        }

        private Source source(Source source) {
            return new ForwardingSource(source) {

                private long mTotalLength = mResponseBody.contentLength();
                private long mCurrentRead = 0L;
                private int mPercent;

                private boolean isControlCallback(int percent) {
                    return ((percent - mPercent) >= 1);
                }

                @Override
                public long read(Buffer sink, long byteCount) throws IOException {
                    final long read = super.read(sink, byteCount);
                    mCurrentRead += read != -1 ? read : 0;
                    float length = mCurrentRead * 1.0f / mTotalLength;
                    int percent = (int) (length * 100);
                    if (isControlCallback(percent)) {
                        mPercent = percent;
                        mOnProgressListener.onTransfer(percent, mCurrentRead, mTotalLength);
                    }
                    return read;
                }
            };
        }
    }

    @Override
    public void run() {
        download();
    }
}
