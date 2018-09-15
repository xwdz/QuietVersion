package com.xwdz.quietversion.callback;


import android.support.v4.app.FragmentManager;

public interface OnUINotify {

    void show(String note);

    void show(String note, FragmentManager fragmentManager);
}
