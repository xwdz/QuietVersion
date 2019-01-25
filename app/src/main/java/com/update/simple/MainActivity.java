package com.update.simple;

import android.app.Activity;
import android.os.Bundle;
import android.widget.Toast;

import com.xwdz.version.QuietVersion;
import com.xwdz.version.callback.NetworkParser;
import com.xwdz.version.entry.ApkSource;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    private static final String URL = "https://github.com/xwdzProxy/version/raw/master/app-release-unsigned.apk";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toast.makeText(this, "This is New Version", Toast.LENGTH_SHORT).show();

        QuietVersion.getInstance(this)
                //or post
                .get("http://www.baidu.com")
                //强制每次更新下载最新Apk
                .setNetworkParser(new NetworkParser() {
                    @Override
                    public ApkSource parser(String response) {
                        return new ApkSource(
                                URL,
                                "更新内容如下\n1.你好\n2.我不好",
                                123123123,
                                123,
                                "v/1.2.3"
                        );
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
