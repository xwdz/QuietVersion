package com.xingwei.checkupdate;

import android.content.Context;

import com.xingwei.checkupdate.callback.OnCheckUpgradeRuleListener;
import com.xingwei.checkupdate.callback.OnNetworkParserListener;
import com.xingwei.checkupdate.core.VersionHandler;
import com.xingwei.checkupdate.entry.ApkResultSource;
import com.xwdz.okhttpgson.OkHttpRun;
import com.xwdz.okhttpgson.callback.StringCallBack;
import com.xwdz.okhttpgson.method.Request;

import java.util.LinkedHashMap;

import okhttp3.Call;

public class XCheck {

    private static final String TAG = XCheck.class.getSimpleName();

    private static XCheck sCheck;

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


    private XCheck(Context applicationContext) {
        this.mContext = applicationContext;
    }

    public static XCheck getInstance(Context context) {
        if (sCheck == null) {
            synchronized (XCheck.class) {
                if (sCheck == null) {
                    sCheck = new XCheck(context.getApplicationContext());
                }
            }
        }
        return sCheck;
    }

    public XCheck GET(String url) {
        this.mMethod = GET;
        this.mUrl = url;
        return this;
    }

    public XCheck POST(String url) {
        this.mMethod = POST;
        this.mUrl = url;
        return this;
    }

    public XCheck addParams(String key, String value) {
        PARAMS.put(key, value);
        return this;
    }

    public XCheck addHeader(String key, String value) {
        HEADER.put(key, value);
        return this;
    }


    public XCheck setOnNetworkParserListener(OnNetworkParserListener listener) {
        this.mParserListener = listener;
        return this;
    }


    public XCheck setOnCheckUpgradeRuleListener(OnCheckUpgradeRuleListener listener) {
        this.mCheckUpgradeRuleListener = listener;
        return this;
    }


    public void apply() {
        Utils.LOG.i(TAG, "appUpgrade apply ... ");
        Request request = GET.equals(mMethod) ? OkHttpRun.get(mUrl) : OkHttpRun.post(mUrl);
        request.addParams(PARAMS)
                .addHeaders(HEADER)
                .setCallBackToMainUIThread(true)
                .execute(new StringCallBack() {

                    @Override
                    public void onFailure(Call call, Exception e) {
                        if (mParserListener != null) {
                            ApkResultSource apkResultSource = mParserListener.parser(null);
                            mVersionHandler = VersionHandler.get(mContext, apkResultSource);
                        }
                        Utils.LOG.e(TAG, e.toString());
                    }

                    @Override
                    protected void onSuccess(Call call, String response) {
                        if (mParserListener != null) {
                            ApkResultSource apkResultSource = mParserListener.parser(response);
                            mVersionHandler = VersionHandler.get(mContext, apkResultSource);

                        }
                    }

                });
    }


    public void recycle() {
        if (mVersionHandler != null) {
            mVersionHandler.recycle();
        }
    }
}
