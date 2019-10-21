package com.update.simple;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.xwdz.version.core.DefaultNotification;
import com.xwdz.version.core.QuietVersion;
import com.xwdz.version.callback.ResponseNetworkParser;
import com.xwdz.version.entry.ApkSource;
import com.xwdz.version.strategy.AppUpgradeStrategy;

import org.json.JSONException;
import org.json.JSONObject;


public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String REQUEST_URL = "https://raw.githubusercontent.com/weaponbay/Test-Version/master/config.json";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toast.makeText(MainActivity.this, "v:" + BuildConfig.VERSION_CODE, Toast.LENGTH_SHORT).show();


        QuietVersion.
                get(REQUEST_URL)
                .setUpgradeStrategy(AppUpgradeStrategy.NORMAL)
                .setResponseNetworkParser(new ResponseNetworkParser() {
                    @Override
                    public ApkSource parser(String response) {
                        try {
                            JSONObject jsonObject        = new JSONObject(response);
                            String     note              = jsonObject.getString("note");
                            String     fileSize          = jsonObject.getString("fileSize");
                            String     url               = jsonObject.getString("url");
                            String     remoteVersionCode = jsonObject.getString("remoteVersionCode");
                            String     remoteVersionName = jsonObject.getString("remoteVersionName");
                            String     md5               = jsonObject.getString("md5");
                            return new ApkSource(url, note,
                                    Long.parseLong(fileSize),
                                    Integer.parseInt(remoteVersionCode),
                                    remoteVersionName,
                                    md5);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                })
                .check();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void onClickUpdated(View view) {
        QuietVersion.
                get(REQUEST_URL)
                .setUpgradeStrategy(AppUpgradeStrategy.NORMAL)
                .setResponseNetworkParser(new ResponseNetworkParser() {
                    @Override
                    public ApkSource parser(String response) {
                        return ApkSource.simpleParser(response);
                    }
                })
                .check();

    }

    public void onOtherClickUpdated(View view) {
        QuietVersion.
                get(REQUEST_URL)
                .setUpgradeStrategy(AppUpgradeStrategy.FORCE_SILENT_DOWNLOAD_NOTIFICATION)
                .setResponseNetworkParser(new ResponseNetworkParser() {
                    @Override
                    public ApkSource parser(String response) {
                        return ApkSource.simpleParser(response);
                    }
                })
                .setNotification(new DefaultNotification())
                .check();
    }
}
