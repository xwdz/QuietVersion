package com.xingwei.checkupdate;

import android.support.v4.app.FragmentActivity;

import com.xingwei.checkupdate.callback.OnNetworkParserListener;
import com.xingwei.checkupdate.callback.OnUINotify;
import com.xingwei.checkupdate.core.VersionHandler;
import com.xingwei.checkupdate.entry.ApkSource;
import com.xwdz.okhttpgson.OkHttpRun;
import com.xwdz.okhttpgson.OkRun;
import com.xwdz.okhttpgson.callback.StringCallBack;
import com.xwdz.okhttpgson.method.Request;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import okhttp3.Call;
import okhttp3.Interceptor;

public class Quite {

    private static final String TAG = Quite.class.getSimpleName();

    private static Quite sQuite;

    private static final String GET = "GET";
    private static final String POST = "POST";

    private static final LinkedHashMap<String, String> PARAMS = new LinkedHashMap<>();
    private static final LinkedHashMap<String, String> HEADER = new LinkedHashMap<>();

    private String mMethod;
    private FragmentActivity mFragmentActivity;
    private String mUrl;
    private VersionHandler mVersionHandler;
    private String mApkName;
    private String mApkPath;
    private boolean mForceDownload;
    private OnNetworkParserListener mOnNetworkParserListener;
    private OnUINotify mNotifyUIHandler;
    private final List<Interceptor> mInterceptors = new ArrayList<>();
    private final List<Interceptor> mNetworkInterceptors = new ArrayList<>();

    private Quite(FragmentActivity fragmentActivity) {
        this.mFragmentActivity = fragmentActivity;
    }

    public static Quite getInstance(FragmentActivity context) {
        if (sQuite == null) {
            synchronized (Quite.class) {
                if (sQuite == null) {
                    sQuite = new Quite(context);
                }
            }
        }
        return sQuite;
    }

    public Quite GET(String url) {
        this.mMethod = GET;
        this.mUrl = url;
        return this;
    }

    public Quite setOnNetworkParserListener(OnNetworkParserListener onNetworkParserListener) {
        this.mOnNetworkParserListener = onNetworkParserListener;
        return this;
    }

    public Quite POST(String url) {
        this.mMethod = POST;
        this.mUrl = url;
        return this;
    }

    public Quite addParams(String key, String value) {
        PARAMS.put(key, value);
        return this;
    }

    public Quite addHeader(String key, String value) {
        HEADER.put(key, value);
        return this;
    }

    public Quite addInterceptor(Interceptor interceptor) {
        mInterceptors.add(interceptor);
        return this;
    }

    public Quite addNetworkInterceptor(Interceptor interceptor) {
        mNetworkInterceptors.add(interceptor);
        return this;
    }

    public Quite setApkPath(String path) {
        this.mApkPath = path;
        return this;
    }

    public Quite setApkName(String name) {
        this.mApkName = name;
        return this;
    }

    public Quite setForceDownload(boolean isDownload) {
        this.mForceDownload = isDownload;
        return this;
    }

    public Quite setNotifyHandler(OnUINotify notifyHandler) {
        this.mNotifyUIHandler = notifyHandler;
        return this;
    }


    public void apply() {
        try {
            Utils.LOG.i(TAG, "appUpgrade apply ... ");
            final QuiteEntry entry = new QuiteEntry
                    (
                            mApkName,
                            mApkPath,
                            mForceDownload,
                            false,
                            mNotifyUIHandler
                    );

            initClient();

            if (mOnNetworkParserListener != null) {
                final Request request = GET.equals(mMethod) ? OkHttpRun.get(mUrl) : OkHttpRun.post(mUrl);
                request.addParams(PARAMS)
                        .addHeaders(HEADER)
                        .execute(new StringCallBack() {
                            @Override
                            public void onFailure(Call call, Exception e) {
                                Utils.LOG.e(TAG, "请求url = " + mUrl + " 失败! error = " + e);
                            }

                            @Override
                            protected void onSuccess(Call call, String response) {
                                ApkSource apkSource = mOnNetworkParserListener.parser(response);
                                if (apkSource != null) {
                                    mVersionHandler = VersionHandler.get(mFragmentActivity, apkSource, entry);
                                } else {
                                    Utils.LOG.i(TAG, "当前暂未发现新版本...");
                                }
                            }
                        });
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.LOG.e(TAG, "app apply error = " + e);
        }
    }

    private void initClient() {
        final OkRun manager = OkRun.getInstance().newBuilder();
        for (Interceptor interceptor : mInterceptors) {
            manager.addInterceptor(interceptor);
        }

        for (Interceptor networkInterceptor : mNetworkInterceptors) {
            manager.addNetworkInterceptor(networkInterceptor);
        }
        manager.attachTag(Utils.LOG.TAG);
        manager.build();
    }


    public void recycle() {
        if (mVersionHandler != null) {
            mVersionHandler.recycle();
        }
    }


    public static class QuiteEntry {

        private String mApkName;
        private String mApkPath;
        private boolean mForceDownload;
        private boolean mDeleteApk;
        private OnUINotify mOnUINotify;

        private QuiteEntry(String apkName, String apkPath, boolean forceDownload, boolean deleteApk, OnUINotify onUINotify) {
            this.mApkName = apkName;
            this.mApkPath = apkPath;
            this.mForceDownload = forceDownload;
            this.mOnUINotify = onUINotify;
            this.mDeleteApk = deleteApk;
        }

        public boolean isForceDownload() {
            return mForceDownload;
        }

        public String getApkName() {
            return mApkName;
        }

        public String getApkPath() {
            return mApkPath;
        }

        public OnUINotify getOnUINotify() {
            return mOnUINotify;
        }

        public boolean isDeleteApk() {
            return mDeleteApk;
        }
    }
}
