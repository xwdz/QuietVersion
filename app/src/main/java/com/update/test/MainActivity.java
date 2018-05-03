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
                .GET("http://www.baidu.com")
                .setNetworkParserListener(new OnNetworkParserListener() {
                    @Override
                    public ApkResultSource parser(String response) {
                        ApkResultSource apkResultSource = new ApkResultSource();
                        apkResultSource.level = 0;
                        apkResultSource.appPackage = getPackageName();
                        apkResultSource.fileSize = 102121;
                        apkResultSource.note = "this is Test";
                        apkResultSource.url = "http://shouji.360tpcdn.com/180427/9050ba38f3138d9895f619389241c0c7/com.ss.android.article.video_250.apk";
                        return apkResultSource;
                    }
                })
                .request();


    }
}
