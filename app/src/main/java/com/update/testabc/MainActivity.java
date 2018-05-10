package com.update.testabc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.xingwei.checkupdate.Quite;
import com.xingwei.checkupdate.callback.OnNetworkParserListener;
import com.xingwei.checkupdate.entry.ApkSource;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    String A = "zhushou360://type=apk&marketid=10000001&refer=thirdlink&name=360%E6%89%8B%E6%9C%BA%E5%8D%AB%E5%A3%AB&icon=http://p18.qhimg.com/t0168f384a0b6a971c2.png&appmd5=78ef176d24b7de2272bf8d88e9da5035&softid=77208&appadb=&url=http://shouji.360tpcdn.com/180503/78ef176d24b7de2272bf8d88e9da5035/com.qihoo360.mobilesafe_260.apk";
    String c = "http://shouji.360tpcdn.com/180427/9050ba38f3138d9895f619389241c0c7/com.ss.android.article.video_250.apk";
    String d = "http://openbox.mobilem.360.cn/url/r/k/std_1525405075";

    String weixin = "http://dlc2.pconline.com.cn/filedown_359554_6972055/kvssBJkn/weixin665android1280.apk";
    String kugou = "http://download.kugou.com/download/kugou_android";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Quite.getInstance(this)
                //or POST
                .GET("http://www.baidu.com")
                //强制每次更新下载最新Apk
                .setForceDownload(true)
                .setShowUIActivity(DialogTest.class)
                .setOnNetworkParserListener(new OnNetworkParserListener() {
                    @Override
                    public ApkSource parser(String response) {
                        return new ApkSource(
                                c,
                                "更新内容如下\n1.你好\n2.我不好",
                                123123123,
                                123,
                                9999
                        );
                    }
                })
                .apply();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Quite.getInstance(this).recycle();
    }
}
