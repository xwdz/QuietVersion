package com.xwdz.version.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.xwdz.version.core.VersionHandler;

public abstract class AbstractActivity extends AppCompatActivity {

    protected static final int MAX = 100;

    private VersionHandler.ProgressReceiver mProgressReceiver = new VersionHandler.ProgressReceiver() {
        @Override
        public void onUpdateProgress(long total, long currentLength, int percent) {
            if (total > 0 && currentLength > 0 && percent >= 0) {
                if (percent == MAX) {
                    finish();
                    return;
                }

                updateProgress(percent, currentLength, total);
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        VersionHandler.registerProgressbarReceiver(this, mProgressReceiver);
        setContentView(getContentLayoutId());
        setUpData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        VersionHandler.unregisterProgressbarReceiver(this, mProgressReceiver);
    }


    public abstract int getContentLayoutId();

    public abstract void setUpData();

    public abstract void updateProgress(int percent, long currentLength, long total);

}
