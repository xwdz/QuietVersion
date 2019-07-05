package com.xwdz.version.callback;

import android.app.Notification;
import android.content.Context;

/**
 * @author xingwei.huang (xwdz9989@gmail.com)
 * @since v1.0.0
 */
public interface NotificationFactory {

    Notification created(Context context);
}
