package com.xwdz.version.core;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;

import com.xwdz.version.R;
import com.xwdz.version.utils.LOG;

/**
 * @author xingwei.huang (xwdz9989@gmail.com)
 * @since v1.0.0
 */
public class DefaultNotification extends BaseNotification {

    private static final String TAG = DefaultNotification.class.getSimpleName();

    NotificationManager mManager;
    Notification        mNotification;


    @Override
    public void initNotification(Context context, String path, boolean isLocalCacheApp) {
        mManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        LOG.i(TAG, "是否是缓存App:" + isLocalCacheApp);
        Intent intent = AppInstallUtils.getSystemInstallIntent(context, path, null);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mNotification = new NotificationCompat.Builder(context, "install")
                .setContentTitle("自定义:已WIFI预下载完成")
                .setContentText("自定义:点击进行安装更新")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.drawable.ic_launcher_background)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .build();
    }

    @Override
    public void sendNotify() {
        mManager.notify(1, mNotification);
    }
}
