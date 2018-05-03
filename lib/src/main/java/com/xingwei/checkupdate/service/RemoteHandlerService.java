package com.xingwei.checkupdate.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.xingwei.checkupdate.LOG;
import com.xingwei.checkupdate.Utils;
import com.xingwei.checkupdate.callback.OnProgressListener;
import com.xingwei.checkupdate.entry.ApkResultSource;

import java.io.File;

/**
 * @author huangxingwei (xwdz9989@gmail.com)
 * @since 2018/5/3
 */
public class RemoteHandlerService extends IntentService {

    private static final String TAG = RemoteHandlerService.class.getSimpleName();
    private static final String KEY = "source";


    private ApkInstall mApkInstall;
    private DownloadApkHelper mDownloadApkHelper;

    private String mApkName;
    private String mApkPath;

    public RemoteHandlerService() {
        super("RemoteHandlerService");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mApkInstall = new ApkInstall(this);
        mDownloadApkHelper = new DownloadApkHelper();
    }

    private final OnProgressListener mOnProgressListener = new OnProgressListener() {
        @Override
        public void onTransfer(float progress, long total) {
            //todo 进度条更新
            LOG.i(TAG, "progress = " + progress);

        }

        @Override
        public void onError(Exception e) {
            LOG.e(TAG, "progress exception = " + e.toString());
        }

        @Override
        public void onFinished(File file) {
            LOG.i(TAG, "install done ...");
            mApkInstall.install(file.getAbsolutePath());
        }
    };


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            LOG.i(TAG, "onHandleIntent ... ");
            ApkResultSource source = intent.getParcelableExtra(KEY);
            if (source != null) {
                checkURLNotNull(source.url);
                checkApkNameAndLocalIsNull(source.apkPath, source.apkName, source.url);

                //配置远程下载信息
                mDownloadApkHelper.setUrl(source.url);
                mDownloadApkHelper.setFilePath(mApkPath);
                mDownloadApkHelper.setOnProgressListener(mOnProgressListener);

                //配置系统打开apk信息
                LOG.i(TAG, "mApkName = " + mApkName);
                LOG.i(TAG, "mApkPath = " + mApkPath);

                //如果APk 本地存在则直接安装
                if (mDownloadApkHelper.checkApkExits(mApkPath)) {
                    LOG.i(TAG, "install local apk ...");
                    mApkInstall.install(mApkPath);
                    return;
                }

                try {
                    mDownloadApkHelper.download();
                    LOG.i(TAG, "start download apk ...");
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }
    }

    /**
     * 给apkPath,apkName 配置默认属性
     *
     * @param apkPath 配置的apkPath
     * @param apkName 配置的apkName
     * @param url     配置的url
     */
    private void checkApkNameAndLocalIsNull(String apkPath, String apkName, String url) {
        if (TextUtils.isEmpty(apkName)) {
            try {
                int index = url.lastIndexOf("/");
                if (index != -1) {
                    String name = url.substring(index + 1, url.length());
                    mApkName = Utils.getApkFilename(name);
                }
            } catch (Exception e) {
                e.printStackTrace();
                mApkName = null;
            }
        } else {
            mApkName = apkName;
        }

        if (TextUtils.isEmpty(apkPath)) {
            mApkPath = Utils.getApkLocalUrl(this, mApkName);
        } else {
            mApkPath = apkPath;
        }

    }


    private void checkURLNotNull(String url) {
        if (TextUtils.isEmpty(url)) {
            throw new NullPointerException("remote apk url cannot be null !");
        }
    }


    public static void start(Context context, ApkResultSource apkResultSource) {
        Intent intent = new Intent(context, RemoteHandlerService.class);
        intent.putExtra(KEY, apkResultSource);
        context.startService(intent);
    }
}
