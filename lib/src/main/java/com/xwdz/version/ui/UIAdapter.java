package com.xwdz.version.ui;

import android.content.Context;
import android.content.Intent;

import com.xwdz.version.entry.ApkSource;

public class UIAdapter {


    public static void showUpgradeDialog(Context context, ApkSource source, Class<?> activityClass) {
        if (activityClass == null) {
            DefaultDialogActivity.startActivity(context, source);
        } else {
            Intent intent = new Intent(context, activityClass);
            intent.putExtra("note", source);
            context.startActivity(intent);
        }
    }
}
