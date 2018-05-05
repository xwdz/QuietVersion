package com.xingwei.checkupdate;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;

import com.xingwei.checkupdate.callback.OnCheckUpgradeRuleListener;
import com.xingwei.checkupdate.callback.OnNetworkParserListener;
import com.xingwei.checkupdate.core.VersionHandler;
import com.xingwei.checkupdate.entry.ApkSource;
import com.xwdz.okhttpgson.OkHttpRun;
import com.xwdz.okhttpgson.callback.StringCallBack;
import com.xwdz.okhttpgson.method.Request;

import java.util.LinkedHashMap;

import okhttp3.Call;

public class Quite {

    private static final String TAG = Quite.class.getSimpleName();

    private static Quite sCheck;

    private static final String GET = "GET";
    private static final String POST = "POST";


    private static final LinkedHashMap<String, String> PARAMS = new LinkedHashMap<>();
    private static final LinkedHashMap<String, String> HEADER = new LinkedHashMap<>();


    private String mMethod;
    private Context mContext;
    private String mUrl;
    private OnNetworkParserListener mParserListener;
    private OnCheckUpgradeRuleListener mCheckUpgradeRuleListener;
    private VersionHandler mVersionHandler;
    private String mApkName;
    private String mApkPath;
    private boolean mForceDownload;
    private boolean mSilentInstall;

    private Quite(Context applicationContext) {
        this.mContext = applicationContext;
    }

    public static Quite getInstance(Context context) {
        if (sCheck == null) {
            synchronized (Quite.class) {
                if (sCheck == null) {
                    sCheck = new Quite(context.getApplicationContext());
                }
            }
        }
        return sCheck;
    }

    public Quite GET(String url) {
        this.mMethod = GET;
        this.mUrl = url;
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

    public Quite setOnNetworkParserListener(OnNetworkParserListener listener) {
        this.mParserListener = listener;
        return this;
    }


    public Quite setOnCheckUpgradeRuleListener(OnCheckUpgradeRuleListener listener) {
        this.mCheckUpgradeRuleListener = listener;
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

    public Quite setSilentInstall(boolean install) {
        this.mSilentInstall = install;
        return this;
    }


    public void apply() {
        Utils.LOG.i(TAG, "appUpgrade apply ... ");
        final QuiteEntry entry = new QuiteEntry
                (
                        mApkName,
                        mApkPath,
                        mForceDownload
                );

        Request request = GET.equals(mMethod) ? OkHttpRun.get(mUrl) : OkHttpRun.post(mUrl);
        request.addParams(PARAMS)
                .addHeaders(HEADER)
                .setCallBackToMainUIThread(true)
                .execute(new StringCallBack() {

                    @Override
                    public void onFailure(Call call, Exception e) {
                        Utils.LOG.e(TAG, e.toString());
                    }

                    @Override
                    protected void onSuccess(Call call, String response) {
                        if (mParserListener != null) {
                            ApkSource apkSource = mParserListener.parser(response);
                            mVersionHandler = VersionHandler.get(mContext, apkSource, entry);

                        }
                    }

                });
    }


    public void recycle() {
        if (mVersionHandler != null) {
            mVersionHandler.recycle();
        }
    }


    public static class QuiteEntry implements Parcelable {

        private String mApkName;
        private String mApkPath;
        private boolean mForceDownload;

        private QuiteEntry(String apkName, String apkPath, boolean forceDownload) {
            mApkName = apkName;
            mApkPath = apkPath;
            mForceDownload = forceDownload;
        }

        public boolean isForceDownload() {
            return mForceDownload;
        }

        public void setForceDownload(boolean forceDownload) {
            mForceDownload = forceDownload;
        }

        public String getApkName() {
            return mApkName;
        }

        public void setApkName(String apkName) {
            mApkName = apkName;
        }

        public String getApkPath() {
            return mApkPath;
        }

        public void setApkPath(String apkPath) {
            mApkPath = apkPath;
        }


        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(this.mApkName);
            dest.writeString(this.mApkPath);
            dest.writeByte(this.mForceDownload ? (byte) 1 : (byte) 0);
        }

        protected QuiteEntry(Parcel in) {
            this.mApkName = in.readString();
            this.mApkPath = in.readString();
            this.mForceDownload = in.readByte() != 0;
        }

        public static final Parcelable.Creator<QuiteEntry> CREATOR = new Parcelable.Creator<QuiteEntry>() {
            @Override
            public QuiteEntry createFromParcel(Parcel source) {
                return new QuiteEntry(source);
            }

            @Override
            public QuiteEntry[] newArray(int size) {
                return new QuiteEntry[size];
            }
        };
    }
}
