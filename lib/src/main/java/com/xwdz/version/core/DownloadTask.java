package com.xwdz.version.core;


import com.xwdz.version.Utils;
import com.xwdz.version.callback.OnProgressListener;

import java.io.File;
import java.io.IOException;

import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
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
public class DownloadTask implements Runnable {

    private static final String TAG = DownloadTask.class.getSimpleName();

    /**
     * 下载URL
     */
    private String mApkUrl;
    /**
     * 存放apk路径
     */
    private String mFilePath;

    private OnProgressListener mOnProgressListener;

    private OkHttpClient.Builder mBuilder = new OkHttpClient.Builder();
    private OkHttpClient mOkHttpClient;


    DownloadTask() {
        mOkHttpClient = mBuilder.addNetworkInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                final Response interceptor = chain.proceed(chain.request());
                return interceptor.newBuilder()
                        .body(new ProgressBody(interceptor.body(), mOnProgressListener))
                        .build();
            }
        }).build();
    }


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
        File file = null;
        try {
            file = createBrokenFile(mFilePath);

            Call call = mOkHttpClient.newCall(new Request.Builder().url(mApkUrl).build());
            Response response = call.execute();


            final BufferedSink sink = Okio.buffer(Okio.sink(file));
            sink.writeAll(response.body().source());
            sink.close();

            if (mOnProgressListener != null) {
                mOnProgressListener.onFinished(file);
            }
        } catch (Exception e) {
            if (file != null) {
                file.deleteOnExit();
            }
            Utils.LOG.e(TAG, "download file error= " + e);
        }
    }

    /**
     * 创建APK文件
     */
    private File createBrokenFile(String localUrl) throws Exception {
        File file = new File(localUrl);

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
