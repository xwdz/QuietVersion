package com.xwdz.version.core;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.xwdz.version.utils.SignatureUtil;

/**
 * @author xingwei.huang (xwdz9989@gmail.com)
 * @since v1.0.0
 */
public class ClickBroadcast extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        final String path = intent.getStringExtra("path");
        final String md5  = intent.getStringExtra("md5");
        if (SignatureUtil.getAppSignatureMD5(context).equals(md5)) {
            ApkInstallUtils.doInstall(context, path, null);
        }


    }
}
