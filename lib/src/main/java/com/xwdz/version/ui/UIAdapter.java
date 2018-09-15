package com.xwdz.version.ui;

import android.content.Context;
import android.content.Intent;

public class UIAdapter {


    private Context mContext;

    public UIAdapter(Context context) {
        mContext = context;
    }


    public void showUpgradeDialog(String note, Class<?> activityClass) {
        if (activityClass == null) {
            ProgressDialogActivity.startActivity(mContext, note);
        } else {
            Intent intent = new Intent(mContext, activityClass);
            intent.putExtra("note", note);
            mContext.startActivity(intent);
        }
    }
}
