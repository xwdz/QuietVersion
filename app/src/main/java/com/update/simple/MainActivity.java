package com.update.simple;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.xwdz.version.core.QuietVersion;
import com.xwdz.version.callback.NetworkParser;
import com.xwdz.version.callback.ErrorListener;
import com.xwdz.version.entry.ApkSource;
import com.xwdz.version.utils.LOG;


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
    }

    public void onClickUpdated(View view) {
        QuietVersion.
                get(REQUEST_URL)
//                .addParams()
//                .addHeaders()
                .onNetworkParser(new NetworkParser() {
                    @Override
                    public ApkSource parser(String response) {
                        return ApkSource.simpleParser(response);
                    }
                })
                .error(new ErrorListener() {
                    @Override
                    public void listener(Throwable throwable) {
                        LOG.e(TAG, "Updated error:" + throwable);
                    }
                }).
                check();

    }

    public void onOtherClickUpdated(View view) {

    }
}
