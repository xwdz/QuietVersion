package com.xingwei.checkupdate.ui;

import android.content.Context;

public class UIAdapter {


    private Context mContext;

    public UIAdapter(Context context) {
        mContext = context;
    }


    public void showUpgradeDialog(String note) {
        ProgressDialogActivity.startActivity(mContext, note);
    }
}
