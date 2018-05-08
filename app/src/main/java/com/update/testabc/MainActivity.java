package com.update.testabc;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.widget.Toast;

import com.jac.android.common.http.HTTPBuilder;
import com.jac.android.common.id.CUID;
import com.jac.android.common.utils.Device;
import com.jac.android.common.utils.SystemUtils;
import com.jac.android.common.utils.TextUtils;
import com.jac.android.common.utils.ZipUtils;
import com.xingwei.checkupdate.Quite;
import com.xingwei.checkupdate.Utils;
import com.xingwei.checkupdate.callback.OnNetworkParserListener;
import com.xingwei.checkupdate.entry.ApkSource;
import com.xwdz.okhttpgson.callback.JsonCallBack;
import com.xwdz.okhttpgson.model.Parser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.security.MessageDigest;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.TreeMap;

import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    String A = "zhushou360://type=apk&marketid=10000001&refer=thirdlink&name=360%E6%89%8B%E6%9C%BA%E5%8D%AB%E5%A3%AB&icon=http://p18.qhimg.com/t0168f384a0b6a971c2.png&appmd5=78ef176d24b7de2272bf8d88e9da5035&softid=77208&appadb=&url=http://shouji.360tpcdn.com/180503/78ef176d24b7de2272bf8d88e9da5035/com.qihoo360.mobilesafe_260.apk";
    String c = "http://shouji.360tpcdn.com/180427/9050ba38f3138d9895f619389241c0c7/com.ss.android.article.video_250.apk";
    String d = "http://openbox.mobilem.360.cn/url/r/k/std_1525405075";

    String weixin = "http://dlc2.pconline.com.cn/filedown_359554_6972055/kvssBJkn/weixin665android1280.apk";
    String kugou = "http://download.kugou.com/download/kugou_android";


    public final static String SIGN_PRIVKEY = "788c14bbbe0eaf5d1120bcd5a013bdd8";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        Toast.makeText(this, "This is new app", Toast.LENGTH_LONG).show();


        final Interceptor interceptor = new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Utils.LOG.e(TAG, "intercept");
                JSONObject jo = null;
                try {
                    jo = new JSONObject();
                    jo.put(HTTPBuilder.P_CHANNEL, "app");
                    jo.put(HTTPBuilder.P_CUID, CUID.str(MainActivity.this));
                    jo.put("app_pkg", SystemUtils.getMyPackageName(MainActivity.this));
                    jo.put("app_ver", ("" + SystemUtils.getMyVersionCode(MainActivity.this)));
                    jo.put("app_vern", SystemUtils.getMyVersionName(MainActivity.this));
                    jo.put("os_release", SystemUtils.getOSRelease());
                    jo.put("os_sdk", SystemUtils.getOSSDKInt());
                    jo.put("rom_release", SystemUtils.getROMRelease());
                    jo.put("dev_model", Device.getModel());
                    jo.put("dev_brand", Device.getBrand());
                    jo.put("dev_mfr", Device.getManufacturer());
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                Request request = chain.request();
                final Request.Builder requestBuilder = request.newBuilder();
                String result = get(jo.toString());
                Utils.LOG.e(TAG, "result = " + result);
                requestBuilder.url(request.url().newBuilder()
                        .addQueryParameter("p", get(jo.toString()))
                        .addQueryParameter("lang", "zhcn")
                        .addQueryParameter("gz", "1")
                        .build()
                );
                return chain.proceed(requestBuilder.build());
            }
        };


        Quite.getInstance(this)
                .GET("http://appcfg.5hbb.com:8066/rest/v1/checkupdate")
                .setForceDownload(true)
                .addInterceptor(interceptor)
                .setInstallLaterDeleteApk(true)
                .setOnNetworkParserListener(new OnNetworkParserListener() {
                    @Override
                    public ApkSource parser(String response) {
                        com.update.testabc.Response resule = Parser.getInstance().parser(response, com.update.testabc.Response.class);
                        Utils.LOG.i(TAG, "response = " + resule.toString());
                        return new ApkSource(
                                "http://tools.9fens.com/sdk/upload/app-debug_201805081607.apk",
                                "本次更新内容如下:\n1.瘦身200k\n2.朋友圈功能\n3.abc",
                                123123123,
                                1,
                                99999
                        );
                    }
                })
                .apply();
    }

    private String getSignature(Map<String, String> signs, String bodyMd5, String signKey) throws Exception {
        StringBuilder builder = new StringBuilder();
        TreeMap<String, String> t = new TreeMap(signs);
        Set<Map.Entry<String, String>> entries = t.entrySet();
        Iterator var7 = entries.iterator();

        while (var7.hasNext()) {
            Map.Entry<String, String> entry = (Map.Entry) var7.next();
            builder.append((String) entry.getKey()).append("=").append((String) entry.getValue());
        }

        if (!TextUtils.empty(bodyMd5)) {
            builder.append(bodyMd5);
        }

        if (!TextUtils.empty(signKey)) {
            builder.append(signKey);
        }

        String matrix = builder.toString();
        return this.getMD5String(matrix.getBytes("UTF-8"));
    }

    private String getMD5String(byte[] plain) throws Exception {
        MessageDigest digest = MessageDigest.getInstance("MD5");
        byte[] buffer = digest.digest(plain);
        return this.byteToHex(buffer, 0, buffer.length);
    }

    private String byteToHex(byte[] b, int m, int n) {
        String md5 = "";
        int k = m + n;
        if (k > b.length) {
            k = b.length;
        }

        for (int i = m; i < k; ++i) {
            md5 = md5 + Integer.toHexString(b[i] & 255 | -256).substring(6);
        }

        return md5.toLowerCase(Locale.getDefault());
    }

    public String get(String jo) {
        try {
            byte[] buffer = ZipUtils.gz(jo.toString().getBytes("UTF-8"));
            return new String(Base64.encode(buffer, Base64.NO_WRAP));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        Quite.getInstance(this).recycle();
    }
}
