package com.xwdz.version.core;

import com.xwdz.version.callback.DownloadProgressListener;
import com.xwdz.version.callback.NetworkParser;
import com.xwdz.version.callback.OnErrorListener;
import com.xwdz.version.entry.ApkSource;
import com.xwdz.version.utils.LOG;
import com.xwdz.version.utils.Utils;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class QuietVersion {

    private static final String TAG = QuietVersion.class.getSimpleName();


    private static final String GET  = "created";
    private static final String POST = "post";

    private static OkHttpClient sOkHttpClient;

    public static void initialize(AppVersionBuilder appVersionBuilder) {
        sOkHttpClient = new OkHttpClient();
        UpgradeHandler.getInstance().initBuilder(appVersionBuilder);
    }

    public static void setOkHttpClient(OkHttpClient okHttpClient) {
        sOkHttpClient = okHttpClient;
    }


    public static Builder get(String url) {
        return new Builder().get(url);
    }

    public static Builder post(String url) {
        return new Builder().post(url);
    }

    public static void startDownloaderApk() {
        UpgradeHandler.getInstance().startDownloaderApk();
    }


    public static void registerProgressListener(DownloadProgressListener listener) {
        UpgradeHandler.getInstance().registerProgressListener(listener);
    }

    public static void unRegisterProgressListener() {
        UpgradeHandler.getInstance().unRegisterProgressListener();
    }

    public static final class Builder {

        HashMap<String, String> HEADERS = new HashMap<>();
        HashMap<String, String> PARAMS  = new HashMap<>();

        String          url;
        NetworkParser   networkParser;
        String          method;
        OnErrorListener errorListener;


        public Builder get(String url) {
            this.method = GET;
            this.url = url;
            return this;
        }

        public Builder post(String url) {
            this.method = POST;
            this.url = url;
            return this;
        }


        public Builder onNetworkParser(NetworkParser networkParser) {
            this.networkParser = networkParser;
            return this;
        }

        public Builder addParams(String key, String value) {
            PARAMS.put(key, value);
            return this;
        }

        public Builder error(OnErrorListener listener) {
            errorListener = listener;
            return this;
        }

        public Builder addHeaders(String key, String value) {
            HEADERS.put(key, value);
            return this;
        }

        public Builder url(String url) {
            this.url = url;
            return this;
        }

        public Builder method(String method) {
            this.method = method;
            return this;
        }

        public void apply() {
            try {
                LOG.i(TAG, "appUpgrade apply ... ");
                if (networkParser != null) {

                    Call call = sOkHttpClient.newCall(buildRequest());
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(Call call, IOException e) {
                            if (errorListener != null) {
                                errorListener.listener(e);
                            }
                        }

                        @Override
                        public void onResponse(Call call, Response response) throws IOException {
                            ApkSource apkSource = networkParser.parser(response.body().string());
                            if (apkSource != null) {
                                UpgradeHandler.getInstance().launcherUpgrade(apkSource, sOkHttpClient, errorListener);

                            } else {
                                LOG.i(TAG, "not New Version");
                            }
                        }
                    });
                }
            } catch (Throwable e) {
                e.printStackTrace();

                if (errorListener != null) {
                    errorListener.listener(e);
                }
            }
        }

        private Request buildRequest() {
            final Request.Builder requestBuilder = new Request.Builder();
            FormBody.Builder      params         = new FormBody.Builder();

            for (Map.Entry<String, String> map : HEADERS.entrySet()) {
                requestBuilder.addHeader(map.getKey(), map.getValue());
            }

            requestBuilder.cacheControl(CacheControl.FORCE_NETWORK);
            if (GET.equals(method)) {
                requestBuilder.url(url + Utils.appendHttpParams(PARAMS));
            } else if (POST.equals(method)) {
                for (Map.Entry<String, String> map : PARAMS.entrySet()) {
                    params.add(map.getKey(), map.getValue());
                }
                requestBuilder.url(url);
                requestBuilder.post(params.build());
            }
            return requestBuilder.build();
        }
    }
}