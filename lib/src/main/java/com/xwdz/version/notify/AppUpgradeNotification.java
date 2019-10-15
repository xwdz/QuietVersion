package com.xwdz.version.notify;

import android.content.Context;
import android.support.v4.app.NotificationCompat;

import com.xwdz.version.entry.ApkSource;

/**
 * @author xingwei.huang (xwdz9989@gmail.com)
 * @since v1.0.0
 */
public interface AppUpgradeNotification {

    NotificationCompat.Builder createNotification(ApkSource apkSource, Context context);

    int getNotificationId();
}
