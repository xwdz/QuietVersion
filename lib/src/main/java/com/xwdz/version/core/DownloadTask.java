package com.xwdz.version.core;


import android.content.Context;
import android.os.Environment;

import com.xwdz.version.callback.OnProgressListener;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

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
    private String mURL;

    private OnProgressListener mOnProgressListener;
    private OkHttpClient       mOkHttpClient;
    private Context            mContext;
    private String             mDownloadPath;

    DownloadTask(OkHttpClient okHttpClient, Context context) {
        mContext = context;


        OkHttpClient.Builder builder = okHttpClient.newBuilder();
        builder.connectTimeout(QuietVersion.DEFAULT_TIMEOUT_CONNECT, TimeUnit.SECONDS)
                .readTimeout(QuietVersion.DEFAULT_TIMEOUT_READ, TimeUnit.SECONDS)
                .writeTimeout(QuietVersion.DEFAULT_TIMEOUT_WRITE, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true);

        builder.addNetworkInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                final Response interceptor = chain.proceed(chain.request());
                return interceptor.newBuilder()
                        .body(new ProgressBody(interceptor.body(), mOnProgressListener))
                        .build();
            }
        });
        mOkHttpClient = builder.build();

        //
    }


    void setUrl(String url) {
        mURL = url;
        mDownloadPath = getDownloadPath(mContext) + getDownloadName(url);
    }

    boolean hasCacheApp() {
        return new File(mDownloadPath).exists();
    }

    String getAppPath() {
        return mDownloadPath;
    }

    void setOnProgressListener(OnProgressListener onProgressListener) {
        mOnProgressListener = onProgressListener;
    }

    private void download() {
        File file;
        try {
            file = new File(mDownloadPath);

            Call     call     = mOkHttpClient.newCall(new Request.Builder().url(mURL).build());
            Response response = call.execute();

            final BufferedSink sink = Okio.buffer(Okio.sink(file));
            sink.writeAll(response.body().source());
            sink.close();

            if (mOnProgressListener != null) {
                mOnProgressListener.onFinished(file);
            }
        } catch (Exception e) {
            if (mDownloadPath != null) {
                new File(mDownloadPath).deleteOnExit();
            }
            mOnProgressListener.onError(e);
        }
    }

    public static class ProgressBody extends ResponseBody {

        private final ResponseBody       mResponseBody;
        private       OnProgressListener mOnProgressListener;
        private       BufferedSource     mBufferedSource;

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
                    float length  = mCurrentRead * 1.0f / mTotalLength;
                    int   percent = (int) (length * 100);
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

    public static File getDownloadDir(Context context) {
        String path;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())
                || !Environment.isExternalStorageRemovable()) {
            path = context.getExternalCacheDir().getPath();
        } else {
            path = context.getCacheDir().getPath();
        }
        return new File(path + File.separator + "QuietVersion");
    }

    public static String getDownloadPath(Context context) {
        File root = getDownloadDir(context);
        if (!root.exists()) {
            root.mkdir();
        }
        return root.getAbsolutePath();
    }

    public static String getDownloadName(String url) {
        final int index = url.lastIndexOf('/');
        return url.substring(index);
    }
}
