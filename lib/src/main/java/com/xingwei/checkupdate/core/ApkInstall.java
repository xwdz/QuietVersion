package com.xingwei.checkupdate.core;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.FileProvider;
import android.text.TextUtils;

import com.xingwei.checkupdate.Utils;
import com.xingwei.checkupdate.Utils.LOG;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;

/**
 * 执行Apk安装操作行为
 *
 * @author huangxingwei (xwdz9989@gmail.com)
 * @since v0.0.1
 */
public class ApkInstall {

    private static final String TAG = ApkInstall.class.getSimpleName();

    private FragmentActivity mFragmentActivity;

    ApkInstall(FragmentActivity fragmentActivity) {
        this.mFragmentActivity = fragmentActivity;

    }

    public void install(String apkPath) {
        doInstall(apkPath);
    }

    private void doInstall(String apkPath) {
        if (!apkPath.endsWith(".apk")) {
            Utils.LOG.e(TAG, "install error path = " + apkPath);
            return;
        }

        try {
            File file = new File(apkPath);
            Intent intent = new Intent(Intent.ACTION_VIEW);

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                Uri contentUri = FileProvider.getUriForFile(mFragmentActivity.getBaseContext(), mFragmentActivity.getBaseContext().getPackageName() + ".fileProvider", file);
                intent.setDataAndType(contentUri, "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            } else {
                intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            }
            mFragmentActivity.startActivity(intent);

        } catch (Exception e) {
            e.printStackTrace();
            Utils.LOG.e(TAG, "install error = " + e.toString());
        }

    }

    public void installSilently(String localUrl)
            throws Exception {
        if (TextUtils.isEmpty(localUrl)) {
            throw new IllegalArgumentException("empty local url");
        }

        File file = new File(localUrl);
        if (!file.exists()) {
            throw new IllegalArgumentException("file not exist");
        }

        String[] args = {"pm", "install", "-r", localUrl};
        exeCmdArgs(args);
    }

    private void exeCmdArgs(String[] args) throws Exception {
        ByteArrayOutputStream errorBuffer = new ByteArrayOutputStream();
        ByteArrayOutputStream resultBuffer = new ByteArrayOutputStream();
        ProcessBuilder processBuilder = null;
        Process process = null;
        InputStream errorInput = null;
        InputStream resultInput = null;
        int byteOfRead = 0;
        byte[] buffer = new byte[1024];

        try {
            processBuilder = new ProcessBuilder(args);
            process = processBuilder.start();

            errorInput = process.getErrorStream();
            while (-1 != (byteOfRead = errorInput.read(buffer))) {
                errorBuffer.write(buffer, 0, byteOfRead);
            }

            resultInput = process.getInputStream();
            while (-1 != (byteOfRead = resultInput.read(buffer))) {
                resultBuffer.write(buffer, 0, byteOfRead);
            }

            String error = errorBuffer.toString("UTF-8");
            String result = resultBuffer.toString("UTF-8");
            validateResult(error, result);
        } finally {
            close(errorInput, resultInput);
            destroy(process);
        }
    }

    private void validateResult(String error, String result)
            throws Exception {
        if (error.contains("Failure")) {
            throw new Exception("e=" + error + ", r=" + result);
        } else {
            if (!result.contains("Success")) {
                throw new Exception("e=" + error + ", r=" + result);
            }
        }
    }

    private void close(InputStream is1, InputStream is2) {
        try {
            if (null != is1) {
                is1.close();
            }
        } catch (Throwable t) {
            LOG.e(TAG, "close input stream failed: " + t);
        }

        try {
            if (null != is2) {
                is2.close();
            }
        } catch (Throwable t) {
            LOG.e(TAG, "close input stream failed: " + t);
        }
    }

    private void destroy(Process process) {
        try {
            if (null != process) {
                process.exitValue();
            }
        } catch (IllegalThreadStateException e) {
            try {
                process.destroy();
                process.waitFor();
            } catch (Throwable t) {
                LOG.e(TAG, "close process failed: " + t);
            }
        }
    }
}
