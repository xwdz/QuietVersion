package com.update.simple;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xwdz.version.utils.LOG;
import com.xwdz.version.utils.Utils;
import com.xwdz.version.callback.OnUIDialogNotify;
import com.xwdz.version.core.UpgradeHandler;
import com.xwdz.version.entry.ApkSource;

public class DialogTest extends DialogFragment implements OnUIDialogNotify {


    private final UpgradeHandler.ProgressReceiver mProgressReceiver = new UpgradeHandler.ProgressReceiver() {
        @Override
        public void onUpdateProgress(long total, long currentLength, int percent) {
            LOG.i("tag", "current = " + currentLength);
        }
    };


    public static DialogTest newInstance() {
        Bundle args = new Bundle();
        DialogTest fragment = new DialogTest();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.test_dialog, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        UpgradeHandler.startDownloaderApk(getContext());
        UpgradeHandler.registerProgressbarReceiver(getContext(), mProgressReceiver);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        UpgradeHandler.unregisterProgressbarReceiver(getContext(), mProgressReceiver);
    }

    @Override
    public void show(ApkSource apkSource, FragmentManager fragmentManager) {
        fragmentManager.beginTransaction().add(this, getTag()).commitAllowingStateLoss();
    }
}
