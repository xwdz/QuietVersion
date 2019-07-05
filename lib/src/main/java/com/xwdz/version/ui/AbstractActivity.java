package com.xwdz.version.ui;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.xwdz.version.core.UpgradeHandler;

public abstract class AbstractActivity extends AppCompatActivity {

    protected static final int MAX = 100;

    private UpgradeHandler.ProgressReceiver mProgressReceiver = new UpgradeHandler.ProgressReceiver() {
        @Override
        public void onUpdateProgress(long total, long currentLength, int percent) {
            if (total > 0 && currentLength > 0 && percent >= 0) {
                if (percent == MAX) {
                    finish();
                    return;
                }

                AbstractActivity.this.onUpdateProgress(percent, currentLength, total);
            }
        }
    };


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UpgradeHandler.registerProgressbarReceiver(this, mProgressReceiver);
        setContentView(getContentLayoutId());
        onViewCreated();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UpgradeHandler.unregisterProgressbarReceiver(this, mProgressReceiver);
    }


    public abstract int getContentLayoutId();

    public abstract void onViewCreated();

    protected void onUpdateProgress(int percent, long currentLength, long total){

    }

}
