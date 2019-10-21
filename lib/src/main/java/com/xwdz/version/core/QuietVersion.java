package com.xwdz.version.core;


import android.annotation.TargetApi;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;
import android.support.annotation.NonNull;

import com.xwdz.version.callback.ResponseNetworkParser;
import com.xwdz.version.entry.ApkSource;
import com.xwdz.version.strategy.AppUpgradeStrategy;
import com.xwdz.version.ui.OnNotifyUIListener;
import com.xwdz.version.utils.LOG;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.CacheControl;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class QuietVersion {

    private static final String TAG = QuietVersion.class.getSimpleName();

    public static final long DEFAULT_TIMEOUT_CONNECT = 30;
    public static final long DEFAULT_TIMEOUT_READ    = 30;
    public static final long DEFAULT_TIMEOUT_WRITE   = 30;


    private static final String GET  = "created";
    private static final String POST = "post";

    private static OkHttpClient sOkHttpClient;

    public static void initialize(AppConfig appConfig) {
        sOkHttpClient = new OkHttpClient.Builder()
                .connectTimeout(DEFAULT_TIMEOUT_CONNECT, TimeUnit.SECONDS)
                .readTimeout(DEFAULT_TIMEOUT_READ, TimeUnit.SECONDS)
                .writeTimeout(DEFAULT_TIMEOUT_WRITE, TimeUnit.SECONDS)
                .retryOnConnectionFailure(true)
                .build();
        UpgradeHandler.getInstance().initConfig(appConfig);
        //

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            String channelId   = "install";
            String channelName = "安装消息";
            int    importance  = NotificationManager.IMPORTANCE_HIGH;
            createNotificationChannel(appConfig.getContext(), channelId, channelName, importance);
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    private static void createNotificationChannel(Context context, String channelId, String channelName, int importance) {
        NotificationChannel channel = new NotificationChannel(channelId, channelName, importance);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(
                Context.NOTIFICATION_SERVICE);
        notificationManager.createNotificationChannel(channel);
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


    public static void registerProgressListener(OnNotifyUIListener listener) {
        UpgradeHandler.getInstance().registerProgressListener(listener);
    }

    public static void unRegisterProgressListener() {
        UpgradeHandler.getInstance().unRegisterProgressListener();
    }

    public static final class Builder {

        private HashMap<String, String> HEADERS = new HashMap<>();
        private HashMap<String, String> PARAMS  = new HashMap<>();
        private String                  url;
        private ResponseNetworkParser   mResponseNetworkParser;
        private String                  method;

        public AppUpgradeStrategy upgradeStrategy;
        public BaseNotification   baseNotification;


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

        public Builder setUpgradeStrategy(AppUpgradeStrategy upgradeStrategy) {
            this.upgradeStrategy = upgradeStrategy;
            return this;
        }

        public Builder setNotification(BaseNotification notification) {
            this.baseNotification = notification;
            return this;
        }

        public Builder setResponseNetworkParser(ResponseNetworkParser responseNetworkParser) {
            this.mResponseNetworkParser = responseNetworkParser;
            return this;
        }

        public Builder addParams(String key, String value) {
            PARAMS.put(key, value);
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


        public void check() {
            try {
                LOG.i(TAG, "检测程序升级开始.");
                if (mResponseNetworkParser != null) {

                    Call call = sOkHttpClient.newCall(buildRequest());
                    call.enqueue(new Callback() {
                        @Override
                        public void onFailure(@NonNull Call call, @NonNull IOException e) {
                            UpgradeHandler.getInstance().callbackRequestUpgradeError(e);
                        }

                        @Override
                        public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                            ApkSource apkSource = mResponseNetworkParser.parser(response.body().string());
                            if (apkSource != null) {
                                UpgradeHandler.getInstance().launcherUpgrade(apkSource, sOkHttpClient, Builder.this);
                            } else {
                                LOG.i(TAG, "not New Version! ");
                            }
                        }
                    });
                }
            } catch (Throwable e) {
                e.printStackTrace();
                UpgradeHandler.getInstance().callbackRequestUpgradeError(e);
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
                requestBuilder.url(url + appendHttpParams(PARAMS));
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

    private static String appendHttpParams(Map<String, String> sLinkedHashMap) {
        Iterator<String> keys         = sLinkedHashMap.keySet().iterator();
        Iterator<String> values       = sLinkedHashMap.values().iterator();
        StringBuffer     stringBuffer = new StringBuffer();
        stringBuffer.append("?");

        for (int i = 0; i < sLinkedHashMap.size(); i++) {
            String value = null;
            try {
                value = URLEncoder.encode(values.next(), "utf-8");
            } catch (Exception e) {
                e.printStackTrace();
            }

            stringBuffer.append(keys.next() + "=" + value);
            if (i != sLinkedHashMap.size() - 1) {
                stringBuffer.append("&");
            }
        }

        return stringBuffer.toString();
    }
}