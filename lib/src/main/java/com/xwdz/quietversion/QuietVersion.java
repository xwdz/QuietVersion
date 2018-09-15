package com.xwdz.quietversion;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;

import com.xwdz.quietversion.callback.NetworkParser;
import com.xwdz.quietversion.callback.OnUINotify;
import com.xwdz.quietversion.core.VersionHandler;
import com.xwdz.quietversion.entry.ApkSource;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

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

    private static final String GET = "GET";
    private static final String POST = "POST";

    private static final LinkedHashMap<String, String> PARAMS = new LinkedHashMap<>();
    private static final LinkedHashMap<String, String> HEADER = new LinkedHashMap<>();

    private String mMethod;
    private FragmentActivity mFragmentActivity;
    private Activity mActivity;
    private String mUrl;
    private VersionHandler mVersionHandler;
    private NetworkParser mNetworkParser;
    private QuiteEntry mQuiteEntry;
    private Interceptor mNetworkInterceptor;
    private Interceptor mInterceptor;

    private QuietVersion(FragmentActivity fragmentActivity) {
        this.mFragmentActivity = fragmentActivity;
    }

    private QuietVersion(Activity activity) {
        this.mActivity = activity;
        this.mQuiteEntry = new QuiteEntry(activity.getApplicationContext());
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

    public QuietVersion GET(String url) {
        this.mMethod = GET;
        this.mUrl = url;
        return this;
    }

    public QuietVersion setNetworkParser(NetworkParser networkParser) {
        this.mNetworkParser = networkParser;
        return this;
    }

    public QuietVersion POST(String url) {
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

    public QuietVersion setShowUIActivity(Class<?> cls) {
        mQuiteEntry.setShowUIActivityClass(cls);
        return this;
    }

    public QuietVersion addInterceptor(Interceptor interceptor) {
        mInterceptor = interceptor;
        return this;
    }

    public QuietVersion addNetworkInterceptor(Interceptor interceptor) {
        mNetworkInterceptor = interceptor;
        return this;
    }

    public QuietVersion setApkPath(String path) {
        mQuiteEntry.setApkPath(path);
        return this;
    }

    public QuietVersion setApkName(String name) {
        mQuiteEntry.setApkName(name);
        return this;
    }

    public QuietVersion setForceDownload(boolean isDownload) {
        mQuiteEntry.setForceDownload(isDownload);
        return this;
    }

    public QuietVersion setNotifyHandler(OnUINotify notifyHandler) {
        mQuiteEntry.setOnUINotify(notifyHandler);
        return this;
    }


    public void apply() {
        try {
            Utils.LOG.i(TAG, "appUpgrade apply ... ");

            if (mNetworkParser != null) {
                OkHttpClient.Builder builder = new OkHttpClient.Builder();
                builder.addInterceptor(mInterceptor)
                        .addNetworkInterceptor(mNetworkInterceptor);

                OkHttpClient okHttpClient = builder.build();
                Call call = okHttpClient.newCall(buildRequest());
                call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                    }
                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        ApkSource apkSource = mNetworkParser.parser(response.body().string());
                        if (apkSource != null) {
                            final Context context = mFragmentActivity != null ? mFragmentActivity.getBaseContext() : mActivity.getBaseContext();
                            mQuiteEntry.setRemoteVersionCode(apkSource.getRemoteVersionCode());
                            mQuiteEntry.setUrl(apkSource.getUrl());
                            mQuiteEntry.setLevel(apkSource.getLevel());
                            mQuiteEntry.setNote(apkSource.getNote());
                            mQuiteEntry.setFileSize(apkSource.getFileSize());
                            mVersionHandler = VersionHandler.get(context, mQuiteEntry);
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

    private Request buildRequest() {
        if (TextUtils.isEmpty(mUrl)) {
            throw new NullPointerException("url = " + mUrl);
        }

        final Request.Builder requestBuilder = new Request.Builder();
        FormBody.Builder params = new FormBody.Builder();

        for (Map.Entry<String, String> map : HEADER.entrySet()) {
            requestBuilder.addHeader(map.getKey(), map.getValue());
        }

        if (GET.equals(mMethod)) {
            requestBuilder.url(mUrl + com.xwdz.http.Utils.appendHttpParams(PARAMS));
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


    public static class QuiteEntry {

        private String mApkName;
        private String mApkPath;
        private boolean mForceDownload;
        private boolean mDeleteApk;
        private OnUINotify mOnUINotify;
        private Class<?> mClass;

        private long mFileSize;
        private String mNote;
        private int mLevel;
        private String mUrl;
        private int mRemoteVersionCode;
        private Context mContext;

        public QuiteEntry(Context context) {
            mContext = context;
        }


        public boolean isForceDownload() {
            return mForceDownload;
        }

        public String getApkName() {
            if (mApkName == null) {
                try {
                    int index = mUrl.lastIndexOf("/");
                    if (index != -1) {
                        String name = mUrl.substring(index + 1, mUrl.length());
                        mApkName = Utils.getApkFilename(name);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    mApkName = null;
                }
            }
            return mApkName;
        }

        public String getApkPath() {
            if (mApkPath == null) {
                mApkPath = Utils.getApkLocalUrl(mContext.getApplicationContext(), getApkName());
            }
            return mApkPath;
        }

        public boolean checkApkExits() {
            try {
                File file = new File(getApkPath());
                return file.exists();
            } catch (Exception e) {
                e.printStackTrace();
                Utils.LOG.e(TAG, "check local apk failure = " + e);
            }
            return false;
        }

        public void setApkName(String apkName) {
            mApkName = apkName;
        }

        public void setApkPath(String apkPath) {
            mApkPath = apkPath;
        }

        public void setForceDownload(boolean forceDownload) {
            mForceDownload = forceDownload;
        }

        public void setDeleteApk(boolean deleteApk) {
            mDeleteApk = deleteApk;
        }

        public void setOnUINotify(OnUINotify onUINotify) {
            mOnUINotify = onUINotify;
        }

        public void setShowUIActivityClass(Class<?> aClass) {
            mClass = aClass;
        }

        public long getFileSize() {
            return mFileSize;
        }

        public void setFileSize(long fileSize) {
            mFileSize = fileSize;
        }

        public String getNote() {
            return mNote;
        }

        public void setNote(String note) {
            mNote = note;
        }

        public int getLevel() {
            return mLevel;
        }

        public void setLevel(int level) {
            mLevel = level;
        }

        public String getUrl() {
            return mUrl;
        }

        public void setUrl(String url) {
            mUrl = url;
        }

        public int getRemoteVersionCode() {
            return mRemoteVersionCode;
        }

        public void setRemoteVersionCode(int remoteVersionCode) {
            this.mRemoteVersionCode = remoteVersionCode;
        }

        public OnUINotify getOnUINotify() {
            return mOnUINotify;
        }

        public boolean isDeleteApk() {
            return mDeleteApk;
        }

        public Class<?> getActivityClass() {
            return mClass;
        }

    }
}
