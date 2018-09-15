package com.xwdz.version.ui;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xingwei.checkupdate.R;
import com.xwdz.version.Utils;
import com.xwdz.version.core.VersionHandler;

/**
 * 透明主题Activity
 * 下载进度条
 */
public class ProgressDialogActivity extends AbstractActivity {


    private static final String KEY_NOTE = "note";
    private DefaultDialogFragment mDefaultDialogFragment = DefaultDialogFragment.newInstance();

    @Override
    public int getContentLayoutId() {
        return R.layout.activity_note_dialog;
    }

    @Override
    public void setUpData() {
        mDefaultDialogFragment.setCancelable(false);
        TextView releaseNote = findViewById(R.id.release_note);
        Button submit = findViewById(R.id.upgrade);

        String note = getIntent().getStringExtra(KEY_NOTE);
        if (!TextUtils.isEmpty(note)) {
            releaseNote.setText(note);
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                VersionHandler.startDownloadApk(ProgressDialogActivity.this.getApplicationContext());
                mDefaultDialogFragment.show(getFragmentManager());
            }
        });
    }

    @Override
    public void updateProgress(int percent, long currentLength, long total) {
        mDefaultDialogFragment.update(percent, format(currentLength) + "/" + format(total));
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
}
