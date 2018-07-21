package com.xingwei.checkupdate;

import android.app.Activity;
import android.content.Context;
import android.support.v4.app.FragmentActivity;

import com.xingwei.checkupdate.callback.NetworkParser;
import com.xingwei.checkupdate.callback.OnUINotify;
import com.xingwei.checkupdate.core.VersionHandler;
import com.xingwei.checkupdate.entry.ApkSource;
import com.xwdz.http.OkHttpManager;
import com.xwdz.http.callback.StringCallBack;

import java.io.File;
import java.util.LinkedHashMap;

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
    private Activity mActivity;
    private String mUrl;
    private VersionHandler mVersionHandler;
    private NetworkParser mNetworkParser;
    private QuiteEntry mQuiteEntry;
    private Interceptor mNetwordInterceptor;
    private Interceptor mInterceptor;

    private Quite(FragmentActivity fragmentActivity) {
        this.mFragmentActivity = fragmentActivity;
    }

    private Quite(Activity activity) {
        this.mActivity = activity;
        this.mQuiteEntry = new QuiteEntry(activity.getApplicationContext());
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

    public static Quite getInstance(Activity activity) {
        if (sQuite == null) {
            synchronized (Quite.class) {
                if (sQuite == null) {
                    sQuite = new Quite(activity);
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

    public Quite setNetworkParser(NetworkParser networkParser) {
        this.mNetworkParser = networkParser;
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

    public Quite setShowUIActivity(Class<?> cls) {
        mQuiteEntry.setShowUIActivityClass(cls);
        return this;
    }

    public Quite addInterceptor(Interceptor interceptor) {
        mInterceptor = interceptor;
        return this;
    }

    public Quite addNetworkInterceptor(Interceptor interceptor) {
        mNetwordInterceptor = interceptor;
        return this;
    }

    public Quite setApkPath(String path) {
        mQuiteEntry.setApkPath(path);
        return this;
    }

    public Quite setApkName(String name) {
        mQuiteEntry.setApkName(name);
        return this;
    }

    public Quite setForceDownload(boolean isDownload) {
        mQuiteEntry.setForceDownload(isDownload);
        return this;
    }

    public Quite setNotifyHandler(OnUINotify notifyHandler) {
        mQuiteEntry.setOnUINotify(notifyHandler);
        return this;
    }


    public void apply() {
        try {
            Utils.LOG.i(TAG, "appUpgrade apply ... ");

            if (mNetworkParser != null) {
                OkHttpManager okHttpManager = new OkHttpManager.Builder()
                        .addInterceptor(mInterceptor)
                        .addNetworkInterceptor(mNetwordInterceptor)
                        .build();

                if (GET.equals(mMethod)) {
                    okHttpManager.get(mUrl);
                } else {
                    okHttpManager.post(mUrl);
                }
                okHttpManager.addParams(PARAMS);
                okHttpManager.addHeader(HEADER);
                okHttpManager.execute(new StringCallBack() {
                    @Override
                    protected void onSuccess(Call call, String response) {
                        ApkSource apkSource = mNetworkParser.parser(response);
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

                    @Override
                    public void onFailure(Call call, Exception e) {

                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.LOG.e(TAG, "app apply error = " + e);
        }
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
