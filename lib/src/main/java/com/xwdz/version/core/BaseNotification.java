package com.xwdz.version.core;

import android.content.Context;


/**
 * @author xingwei.huang (xwdz9989@gmail.com)
 * @since v1.0.0
 */
public abstract class BaseNotification {

    public abstract void initNotification(Context context, String path,boolean isLocalCacheApp);

    public abstract void sendNotify();


}
