package com.xwdz.version.callback;


import android.support.v4.app.FragmentManager;

import com.xwdz.version.entry.ApkSource;

public interface OnUIDialogNotify {

    void show(ApkSource apkSource, FragmentManager fragmentManager);
}
