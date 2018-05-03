package com.update.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.xingwei.checkupdate.LOG;
import com.xingwei.checkupdate.entry.ApkResultSource;
import com.xingwei.checkupdate.XCheck;
import com.xingwei.checkupdate.callback.OnNetworkParserListener;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        XCheck.getInstance(this)
                .GET("http://acj3.pc6.com/pc6_soure/2018-4/com.tencent.mm_1280.apk")
                .setNetworkParserListener(new OnNetworkParserListener() {
                    @Override
                    public ApkResultSource parser(String response) {
                        LOG.i(TAG, "response = " + response);
                        ApkResultSource apkResultSource = new ApkResultSource();
                        return apkResultSource;
                    }
                })
                .request();


    }
}
