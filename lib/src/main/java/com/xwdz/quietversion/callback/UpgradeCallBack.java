package com.xwdz.quietversion.callback;


import com.xwdz.okhttpgson.callback.AbstractCallBack;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Response;

public abstract class UpgradeCallBack extends AbstractCallBack<Object> {

    @Override
    protected Object parser(final Call call, Response response, boolean isMainUIThread) throws IOException {
        final String text = response.body().string();
        onSuccess(call, text);
        return null;
    }

    protected abstract void onSuccess(Call call, String response);
}
