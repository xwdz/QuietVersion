package com.xwdz.version.ui;

import android.content.Context;
import android.content.Intent;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.daimajia.numberprogressbar.NumberProgressBar;
import com.xwdz.version.R;
import com.xwdz.version.core.QuietVersion;
import com.xwdz.version.entry.ApkSource;

import java.io.File;

/**
 * 透明主题Activity, 提供更新信息
 */
public class DefaultDialogActivity extends AbstractActivity {

    private static final String KEY_NOTE = "note";

    private NumberProgressBar mProgressBar;
    private TextView          mSizeNote;
    private Button            mSubmit;
    private TextView          mReleaseNote;


    @Override
    public int getContentLayoutId() {
        return R.layout.quiet_version_activity_note_layout;
    }

    @Override
    public void onViewCreated() {
        mReleaseNote = findViewById(R.id.release_note);
        mSubmit = findViewById(R.id.upgrade);

        mProgressBar = findViewById(R.id.progressBar);
        mSizeNote = findViewById(R.id.percent);

        ApkSource source = getIntent().getParcelableExtra(KEY_NOTE);
        if (source != null && source.getNote() != null) {
            mReleaseNote.setText(source.getNote());
        }

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                QuietVersion.startDownloaderApk();
                mSubmit.setEnabled(false);
                mSubmit.setText("正在更新");
            }
        });
    }

    @Override
    public void onNewVersion(boolean isNewVersion, ApkSource apkSource) {
        mSubmit.setText(isNewVersion ? "已在WIFI下载好新版本,点击安装" : "立即更新");
    }

    @Override
    public void onUpdateProgress(int percent, long currentLength, long total) {
        update(percent, percent + "/" + 100);
    }

    public void update(int percent, String text) {
        mProgressBar.setProgress(percent);
        mSizeNote.setText(text);

        if (percent == MAX) {
            finish();
        }
    }


    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return true;
    }

    @Override
    protected void onDownloadCompleted(File file) {
    }

    public static void startActivity(Context context, ApkSource note) {
        Intent starter = new Intent(context, DefaultDialogActivity.class);
        starter.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        starter.putExtra(KEY_NOTE, note);
        context.startActivity(starter);
    }
}
