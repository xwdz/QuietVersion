package com.xwdz.version.ui;

import android.content.Context;
import android.content.Intent;

import com.xwdz.version.entry.ApkSource;

public class UIAdapter {


    private Context mContext;

    public UIAdapter(Context context) {
        mContext = context;
    }


    public void showUpgradeDialog(ApkSource source, Class<?> activityClass) {
        if (activityClass == null) {
            DefaultDialogActivity.startActivity(mContext, source);
        } else {
            Intent intent = new Intent(mContext, activityClass);
            intent.putExtra("note", source);
            mContext.startActivity(intent);
        }
    }
}
