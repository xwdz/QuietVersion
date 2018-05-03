package com.xingwei.checkupdate.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.text.TextUtils;

import com.xingwei.checkupdate.entry.ApkResultSource;
import com.xingwei.checkupdate.LOG;
import com.xingwei.checkupdate.callback.OnProgressListener;

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
            mApkInstall.install(file.getAbsolutePath());
        }
    };


    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            ApkResultSource source = intent.getParcelableExtra(KEY);
            if (source != null) {
                checkURLNotNull(source.url);

                //配置下载信息
                mDownloadApkHelper.setFileName(source.apkName);
                mDownloadApkHelper.setOnProgressListener(mOnProgressListener);
                mDownloadApkHelper.setUrl(source.url);
                mDownloadApkHelper.setFilePath(source.apkPath);
                LOG.i(TAG, source.toString());

                //如果APk 本地存在则直接安装
                if (mDownloadApkHelper.checkApkExits(source.apkPath)) {
                    mApkInstall.install(source.apkPath);
                }

                mDownloadApkHelper.download();
            }

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
