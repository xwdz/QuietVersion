package com.xingwei.checkupdate;

import android.util.Log;

public class LOG {

    public static final String TAG = "UpdateCORE";

    public static void i(String tag, String msg) {
        Log.i(TAG, "[" + tag + "] " + msg);
    }

    public static void e(String tag, String msg) {
        Log.e(TAG, "[" + tag + "] " + msg);
    }
}
