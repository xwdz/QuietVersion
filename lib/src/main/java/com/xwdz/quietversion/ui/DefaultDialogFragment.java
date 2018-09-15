package com.xwdz.quietversion.ui;

import android.app.DialogFragment;
import android.app.FragmentManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.xingwei.checkupdate.R;

public class DefaultDialogFragment extends DialogFragment {

    public static DefaultDialogFragment newInstance() {
        Bundle args = new Bundle();
        DefaultDialogFragment fragment = new DefaultDialogFragment();
        fragment.setArguments(args);
        return fragment;
    }


    private ProgressBar mProgressBar;
    private TextView mSizeNote;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.widget_progress_dialog_layout, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mProgressBar = view.findViewById(R.id.progressBar);
        mSizeNote = view.findViewById(R.id.percent);

    }

    public void show(FragmentManager manager) {
        manager.beginTransaction().add(this, getTag()).commitAllowingStateLoss();
    }

    public void update(int percent, String text) {
        mProgressBar.setProgress(percent);
        mSizeNote.setText(text);
    }
}
