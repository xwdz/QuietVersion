package com.xingwei.checkupdate.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xingwei.checkupdate.R;
import com.xingwei.checkupdate.Utils;
import com.xingwei.checkupdate.core.VersionHandler;

/**
 * 透明主题Activity
 * 下载进度条
 */
public class ProgressDialogActivity extends AppCompatActivity {

    private static final String TAG = ProgressDialogActivity.class.getSimpleName();

    private static final String ACTION = "com.xingwei.checkupdate.ui.ProgressDialogActivity";
    public static final String KEY_NOTE = "note";
    public static final String KEY_TOTAL = "total";
    public static final String KEY_CURRENT_LENGTH = "currentlength";
    public static final String KEY_PERCENT = "percent";
    public static final int MAX = 100;


    public class ProgressReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            long total = intent.getLongExtra(KEY_TOTAL, 0);
            long currentLength = intent.getLongExtra(KEY_CURRENT_LENGTH, 0);
            int percent = intent.getIntExtra(KEY_PERCENT, 0);

            if (total > 0 && currentLength > 0 && percent >= 0) {
                if (percent == MAX) {
                    finish();
                    mDefaultDialogFragment = null;
                    return;
                }

                mDefaultDialogFragment.update(percent,
                        format(currentLength) + "/" + format(total));
            }
        }
    }


    private DefaultDialogFragment mDefaultDialogFragment = DefaultDialogFragment.newInstance();

    private ProgressReceiver mProgressReceiver;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_dialog);

        TextView releaseNote = findViewById(R.id.release_note);
        Button submit = findViewById(R.id.upgrade);
        mProgressReceiver = new ProgressReceiver();
        getApplicationContext().registerReceiver(mProgressReceiver, new IntentFilter(ACTION));

        String note = getIntent().getStringExtra(KEY_NOTE);
        if (!TextUtils.isEmpty(note)) {
            releaseNote.setText(note);
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VersionHandler.notifyDownload(ProgressDialogActivity.this.getApplicationContext());
                mDefaultDialogFragment.show(getFragmentManager());
            }
        });

    }


    private String format(long currentLength) {
        return Utils.formatNetFileSizeDescription(currentLength);
    }

    public static void startActivity(Context context, String note) {
        Intent starter = new Intent(context, ProgressDialogActivity.class);
        starter.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        starter.putExtra(KEY_NOTE, note);
        context.startActivity(starter);
    }

    public static void update(Context context, long total, long currentLength, int percent) {
        Intent intent = new Intent(ACTION);
        intent.putExtra(KEY_TOTAL, total);
        intent.putExtra(KEY_CURRENT_LENGTH, currentLength);
        intent.putExtra(KEY_PERCENT, percent);
        context.sendBroadcast(intent);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mProgressReceiver != null) {
            getApplicationContext().unregisterReceiver(mProgressReceiver);
        }
    }
}
