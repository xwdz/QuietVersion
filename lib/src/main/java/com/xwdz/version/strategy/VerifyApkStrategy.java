package com.xwdz.version.strategy;

import android.content.Context;

import com.xwdz.version.core.ApkInstallUtils;
import com.xwdz.version.core.AppConfig;
import com.xwdz.version.entry.ApkSource;
import com.xwdz.version.utils.SignatureUtil;

import java.io.File;

/**
 * App 安全校验
 *
 * @author xingwei.huang (xwdz9989@gmail.com)
 * @since v1.0.0
 */
public interface VerifyApkStrategy extends BaseStrategy {

    /**
     *
     * @param context context
     * @param apkSource 服务器返回的更新资源实体类
     * @param file 下载好的apk文件
     * @param appConfig 配置
     * @return true校验通过，反之不通过
     */
    boolean handler(Context context, ApkSource apkSource, File file, AppConfig appConfig);

    VerifyApkStrategy sDefault = new VerifyApkStrategy() {

        @Override
        public String getName() {
            return "Default:" + toString();
        }

        @Override
        public boolean handler(Context context, ApkSource apkSource, File file, AppConfig appConfig) {
            String md5 = SignatureUtil.getAppSignatureMD5(context);
            return apkSource.getMd5().toLowerCase().equals(md5.toLowerCase());
        }

        @Override
        public int priority() {
            return PRIORITY_10;
        }
    };

}
