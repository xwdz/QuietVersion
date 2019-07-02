package com.update.simple;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.xwdz.version.QuietVersion;
import com.xwdz.version.callback.NetworkParser;
import com.xwdz.version.entry.ApkSource;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String REQUEST_URL = "https://raw.githubusercontent.com/TINNO-Sugar/GameVersionPlatform/master/Config.json";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        QuietVersion.getInstance(this)
                //or post
                .get(REQUEST_URL)
                //强制每次更新下载最新Apk
                .onNetworkParser(new NetworkParser() {
                    @Override
                    public ApkSource parser(String response) {
                        Log.i("QuietVersion", "response:" + response);

                        return ApkSource.simpleParser(response);
                    }
                })
                .apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        QuietVersion.getInstance(this).recycle();
    }
}
