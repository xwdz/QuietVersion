package com.xwdz.version.notify;

import android.app.Notification;
import android.content.Context;

import com.xwdz.version.entry.ApkSource;

/**
 * @author xingwei.huang (xwdz9989@gmail.com)
 * @since v1.0.0
 */
public interface AppUpgradeNotification {

    Notification createNotification(ApkSource apkSource, Context context);

    int getNotificationId();
}
