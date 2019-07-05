package com.xwdz.version.ui;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.xwdz.version.R;
import com.xwdz.version.core.UpgradeHandler;
import com.xwdz.version.entry.ApkSource;

/**
 * 透明主题Activity, 提供更新信息
 */
public class DefaultDialogActivity extends AbstractActivity {

    private static final String KEY_NOTE = "note";

    @Override
    public int getContentLayoutId() {
        return R.layout.quiet_version_activity_note_layout;
    }

    @Override
    public void onViewCreated() {
        TextView releaseNote = findViewById(R.id.release_note);
        Button submit = findViewById(R.id.upgrade);

        ApkSource source = getIntent().getParcelableExtra(KEY_NOTE);
        if (source != null && source.getNote() != null) {
            releaseNote.setText(source.getNote());
        }

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UpgradeHandler.startDownloaderApk(DefaultDialogActivity.this.getApplicationContext());
                DefaultProgressDialogActivity.start(DefaultDialogActivity.this.getApplicationContext());
                finish();
            }
        });
    }

    @Override
    public void onUpdateProgress(int percent, long currentLength, long total) {

    }

    public static void startActivity(Context context, ApkSource note) {
        Intent starter = new Intent(context, DefaultDialogActivity.class);
        starter.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        starter.putExtra(KEY_NOTE, note);
        context.startActivity(starter);
    }
}
