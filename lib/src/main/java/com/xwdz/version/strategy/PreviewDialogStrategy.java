package com.xwdz.version.strategy;

import android.content.Context;

import com.xwdz.version.core.AppConfig;
import com.xwdz.version.entry.ApkSource;
import com.xwdz.version.ui.UIAdapter;

/**
 * App弹出升级框策略
 *
 * @author xingwei.huang (xwdz9989@gmail.com)
 * @since v1.0.0
 */
public interface PreviewDialogStrategy extends BaseStrategy {

    /**
     * @param context context
     * @param config  自定义的升级配置
     * @param source  服务器返回的信息
     * @return true  消耗事件
     * false 不处理事件
     */
    boolean handler(Context context, AppConfig config, ApkSource source);


    PreviewDialogStrategy sDefault = new PreviewDialogStrategy() {
        @Override
        public boolean handler(Context context, AppConfig config, ApkSource source) {
            UIAdapter.showUpgradeDialog(context, source, config.getUiClass());
            return true;
        }

        @Override
        public int priority() {
            return PRIORITY_10;
        }

        @Override
        public String getName() {
            return "Default:"+toString();
        }
    };
}
