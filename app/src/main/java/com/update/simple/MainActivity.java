package com.update.simple;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.xwdz.version.QuietVersion;
import com.xwdz.version.callback.NetworkParser;
import com.xwdz.version.callback.OnErrorListener;
import com.xwdz.version.entry.ApkSource;
import com.xwdz.version.utils.LOG;

import java.util.logging.Logger;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String REQUEST_URL = "https://raw.githubusercontent.com/weaponbay/Test-Version/master/config.json";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(MainActivity.this, "v:" + BuildConfig.VERSION_CODE, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        QuietVersion.getInstance(this).recycle();
    }

    public void onClickUpdated(View view) {
        QuietVersion
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
                .error(new OnErrorListener() {
                    @Override
                    public void listener(Throwable throwable) {
                        LOG.e(TAG,"Updated error:"+throwable);
                    }
                })
                .apply();
    }
}
