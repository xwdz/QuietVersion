package com.xwdz.version.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.xwdz.version.core.QuietVersion;
import com.xwdz.version.entry.ApkSource;

import java.io.File;

public abstract class AbstractActivity extends AppCompatActivity {

    protected static final int MAX = 100;

    final Handler mHandler = new Handler(Looper.getMainLooper());


    private OnNotifyUIListener mOnNotifyUIListenerListener = new OnNotifyUIListener() {
        @Override
        public void onUpdateProgress(final int percent, final long currentLength, final long total) {
            if (total > 0 && currentLength > 0 && percent >= 0) {
                if (percent == MAX) {
                    finish();
                    return;
                }

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        AbstractActivity.this.onUpdateProgress(percent, currentLength, total);
                    }
                });
            }
        }

        @Override
        public void onFinished(File file) {
            AbstractActivity.this.onDownloadCompleted(file);
        }

        @Override
        public void onUpgradeFailure(Throwable error) {
            AbstractActivity.this.onUpgradeFailure(error);
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        QuietVersion.registerProgressListener(mOnNotifyUIListenerListener);
        super.onCreate(savedInstanceState);
        setContentView(getContentLayoutId());
        onViewCreated();
    }


    public abstract int getContentLayoutId();

    public abstract void onViewCreated();

    protected void onUpdateProgress(int percent, long currentLength, long total) {

    }

    /**
     * 成功下载APK文件
     *
     * @param file
     */
    protected void onDownloadCompleted(File file) {

    }

    /**
     * 更新失败
     *
     * @param throwable
     */
    protected void onUpgradeFailure(Throwable throwable) {

    }


}
