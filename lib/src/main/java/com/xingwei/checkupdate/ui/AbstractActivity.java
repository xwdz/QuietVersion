package com.xingwei.checkupdate.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public abstract class AbstractActivity extends AppCompatActivity {

    protected static final String ACTION = "com.xingwei.checkupdate.ui.ProgressDialogActivity";
    protected static final String KEY_NOTE = "note";
    protected static final String KEY_TOTAL = "total";
    protected static final String KEY_CURRENT_LENGTH = "currentlength";
    protected static final String KEY_PERCENT = "percent";
    protected static final int MAX = 100;


    protected class ProgressReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            long total = intent.getLongExtra(KEY_TOTAL, 0);
            long currentLength = intent.getLongExtra(KEY_CURRENT_LENGTH, 0);
            int percent = intent.getIntExtra(KEY_PERCENT, 0);

            if (total > 0 && currentLength > 0 && percent >= 0) {
                if (percent == MAX) {
                    finish();
                    return;
                }

                updateProgress(percent, currentLength, total);
            }
        }
    }

    protected ProgressReceiver mProgressReceiver;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mProgressReceiver = new ProgressReceiver();
        getApplicationContext().registerReceiver(mProgressReceiver, new IntentFilter(ACTION));
        setContentView(getContentLayoutId());
        setUpData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getApplicationContext().unregisterReceiver(mProgressReceiver);
    }

    public static void updateProgress(Context context, long total, long currentLength, int percent) {
        Intent intent = new Intent(ACTION);
        intent.putExtra(KEY_TOTAL, total);
        intent.putExtra(KEY_CURRENT_LENGTH, currentLength);
        intent.putExtra(KEY_PERCENT, percent);
        context.sendBroadcast(intent);
    }

    public abstract int getContentLayoutId();

    public abstract void setUpData();

    public abstract void updateProgress(int percent, long currentLength, long total);

}
