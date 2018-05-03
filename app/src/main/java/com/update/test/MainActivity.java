package com.update.test;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.xingwei.checkupdate.XCheck;
import com.xingwei.checkupdate.callback.OnNetworkParserListener;
import com.xingwei.checkupdate.entry.ApkResultSource;

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
                        ApkResultSource apkResultSource = new ApkResultSource();
                        apkResultSource.apkName = "weixin.apk";
                        apkResultSource.level = 0;
                        apkResultSource.fileSize = 102121;
                        apkResultSource.note = "this is Test";
                        apkResultSource.url = "http://acj3.pc6.com/pc6_soure/2018-4/com.tencent.mm_1280.apk";
                        return apkResultSource;
                    }
                })
                .request();


    }
}
