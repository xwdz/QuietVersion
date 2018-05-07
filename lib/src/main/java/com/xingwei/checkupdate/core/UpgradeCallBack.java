package com.xingwei.checkupdate.core;

import com.xingwei.checkupdate.entry.ApkSource;
import com.xwdz.okhttpgson.callback.AbstractCallBack;
import com.xwdz.okhttpgson.model.Parser;

import java.io.IOException;
import java.lang.reflect.Type;

import okhttp3.Call;
import okhttp3.Response;

public abstract class UpgradeCallBack<T> extends AbstractCallBack<ApkSource> {

    private volatile ApkSource mApkSource;

    @Override
    protected ApkSource parser(final Call call, Response response, boolean isMainUIThread) throws IOException {
        final String json = response.body().string();
        Type type = Parser.getInstance().getSuperclassTypeParameter(getClass());
        final Object object = Parser.getInstance().parser(json, type);
        mApkSource = onNetworkParser(call, (T) object);
        return mApkSource;
    }

    public ApkSource getResult() {
        return mApkSource;
    }

    protected abstract ApkSource onNetworkParser(Call call, T response);
}
