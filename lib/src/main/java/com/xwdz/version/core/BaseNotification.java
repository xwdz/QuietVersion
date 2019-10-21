package com.xwdz.version.core;

import android.content.Context;

import com.xwdz.version.entry.ApkSource;


/**
 * @author xingwei.huang (xwdz9989@gmail.com)
 * @since v1.0.0
 */
public abstract class BaseNotification {

    public abstract void initNotification(Context context, ApkSource source, String path, boolean isLocalCacheApp);

    public abstract void sendNotify();


}
