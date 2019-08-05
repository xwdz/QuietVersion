package com.xwdz.version.strategy;

import android.content.Context;

import com.xwdz.version.entry.ApkSource;
import com.xwdz.version.utils.SignatureUtil;

import java.io.File;

/**
 * App 安全校验
 *
 * @author xingwei.huang (xwdz9989@gmail.com)
 * @since v1.0.0
 */
public interface AppVerifyStrategy {

    boolean verify(Context context, ApkSource apkSource, File file);

    AppVerifyStrategy sDefault = new AppVerifyStrategy() {

        @Override
        public boolean verify(Context context, ApkSource apkSource, File file) {
            String md5 = SignatureUtil.getAppSignatureMD5(context);
            return apkSource.getMd5().toLowerCase().equals(md5.toLowerCase());
        }
    };

}
