package com.update.testabc;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.xwdz.quietversion.Utils;
import com.xwdz.quietversion.callback.OnUINotify;
import com.xwdz.quietversion.core.VersionHandler;

public class DialogTest extends DialogFragment implements OnUINotify {


    private final VersionHandler.ProgressReceiver mProgressReceiver = new VersionHandler.ProgressReceiver() {
        @Override
        public void onUpdateProgress(long total, long currentLength, int percent) {
            Utils.LOG.i("tag", "current = " + currentLength);
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
        VersionHandler.startDownloadApk(getContext());
        VersionHandler.registerProgressbarReceiver(getContext(), mProgressReceiver);
    }

    @Override
    public void show(String note) {

    }

    @Override
    public void show(String note, FragmentManager fragmentManager) {
        fragmentManager.beginTransaction().add(this, getTag()).commitAllowingStateLoss();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        VersionHandler.unregisterProgressbarReceiver(getContext(), mProgressReceiver);
    }
}
