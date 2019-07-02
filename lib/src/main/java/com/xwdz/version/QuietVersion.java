package com.xwdz.version;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.xwdz.version.callback.NetworkParser;
import com.xwdz.version.core.VersionHandler;
import com.xwdz.version.entry.ApkSource;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class QuietVersion {

    private static final String TAG = QuietVersion.class.getSimpleName();

    private static QuietVersion sQuietVersion;

    private static final String GET = "get";
    private static final String POST = "post";

    private static final LinkedHashMap<String, String> PARAMS = new LinkedHashMap<>();
    private static final LinkedHashMap<String, String> HEADER = new LinkedHashMap<>();

    private String mMethod;
    private FragmentActivity mFragmentActivity;
    private Activity mActivity;
    private String mUrl;
    private VersionHandler mVersionHandler;
    private NetworkParser mNetworkParser;
    private List<Interceptor> mNetworkInterceptors;
    private List<Interceptor> mInterceptors;
    private OkHttpClient mOkHttpClient;

    private QuietVersion(FragmentActivity fragmentActivity) {
        this.mFragmentActivity = fragmentActivity;
    }

    private QuietVersion(Activity activity) {
        this.mActivity = activity;
    }

    public static QuietVersion getInstance(FragmentActivity context) {
        if (sQuietVersion == null) {
            synchronized (QuietVersion.class) {
                if (sQuietVersion == null) {
                    sQuietVersion = new QuietVersion(context);
                }
            }
        }
        return sQuietVersion;
    }

    public static QuietVersion getInstance(Activity activity) {
        if (sQuietVersion == null) {
            synchronized (QuietVersion.class) {
                if (sQuietVersion == null) {
                    sQuietVersion = new QuietVersion(activity);
                }
            }
        }
        return sQuietVersion;
    }

    public QuietVersion initOkHttpClient(OkHttpClient okHttpClient) {
        this.mOkHttpClient = okHttpClient;
        return this;
    }

    public QuietVersion get(String url) {
        this.mMethod = GET;
        this.mUrl = url;
        return this;
    }

    public QuietVersion onNetworkParser(NetworkParser networkParser) {
        this.mNetworkParser = networkParser;
        return this;
    }


    public QuietVersion post(String url) {
        this.mMethod = POST;
        this.mUrl = url;
        return this;
    }

    public QuietVersion addParams(String key, String value) {
        PARAMS.put(key, value);
        return this;
    }

    public QuietVersion addHeader(String key, String value) {
        HEADER.put(key, value);
        return this;
    }

    public QuietVersion addInterceptor(Interceptor interceptor) {
        mInterceptors.add(interceptor);
        return this;
    }

    public QuietVersion addNetworkInterceptor(Interceptor interceptor) {
        mNetworkInterceptors.add(interceptor);
        return this;
    }


    public void apply() {
        try {
            Utils.LOG.i(TAG, "appUpgrade apply ... ");

            if (mNetworkParser != null) {
                OkHttpClient.Builder builder = new OkHttpClient.Builder();

                if (mNetworkInterceptors != null && !mNetworkInterceptors.isEmpty()) {
                    for (Interceptor mNetworkInterceptor : mNetworkInterceptors) {
                        builder.addNetworkInterceptor(mNetworkInterceptor);
                    }

                }

                if (mInterceptors != null && !mInterceptors.isEmpty()) {
                    for (Interceptor mInterceptor : mInterceptors) {
                        builder.addInterceptor(mInterceptor);
                    }
                }

                OkHttpClient okHttpClient = builder.build();
                Call call = okHttpClient.newCall(buildRequest());
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        e.printStackTrace();
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        ApkSource apkSource = mNetworkParser.parser(response.body().string());
                        if (apkSource != null) {
                            final Context context = mFragmentActivity != null ? mFragmentActivity.getBaseContext() : mActivity.getBaseContext();
                            mVersionHandler = VersionHandler.get(context, apkSource);
                        } else {
                            Utils.LOG.i(TAG, "not New Version");
                        }
                    }
                });
            }
        } catch (Throwable e) {
            e.printStackTrace();
            Utils.LOG.e(TAG, "app apply error = " + e);
        }
    }

    private Request buildRequest() {
        if (TextUtils.isEmpty(mUrl)) {
            throw new NullPointerException("url = " + mUrl);
        }

        final Request.Builder requestBuilder = new Request.Builder();
        FormBody.Builder params = new FormBody.Builder();

        for (Map.Entry<String, String> map : HEADER.entrySet()) {
            requestBuilder.addHeader(map.getKey(), map.getValue());
        }

        requestBuilder.cacheControl(CacheControl.FORCE_NETWORK);
        if (GET.equals(mMethod)) {
            requestBuilder.url(mUrl + Utils.appendHttpParams(PARAMS));
        } else if (POST.equals(mMethod)) {
            for (Map.Entry<String, String> map : PARAMS.entrySet()) {
                params.add(map.getKey(), map.getValue());
            }
            requestBuilder.url(mUrl);
            requestBuilder.post(params.build());
        }
        return requestBuilder.build();
    }


    public void recycle() {
        if (mVersionHandler != null) {
            mVersionHandler.recycle();
        }
    }
}
