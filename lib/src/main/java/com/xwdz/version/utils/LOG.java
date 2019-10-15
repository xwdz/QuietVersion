package com.xwdz.version.utils;

import android.util.Log;

import com.xwdz.version.core.QuietVersion;

/**
 * @author xingwei.huang (xwdz9989@gmail.com)
 * @since v1.0.0
 */
public class LOG {

    public static final String TAG = QuietVersion.class.getSimpleName();

    public static void i(String tag, String msg) {
        Log.i(TAG, "[" + tag + "]" + msg);
    }

    public static void w(String tag, String msg) {
        Log.w(TAG, "[" + tag + "]" + msg);
    }

    public static void e(String tag, String msg) {
        Log.e(TAG, "[" + tag + "]" + msg);
    }
}
