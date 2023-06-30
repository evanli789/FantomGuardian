package com.penguinstudios.fantomguardian.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.penguinstudios.fantomguardian.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ConfirmDeleteFragment extends DialogFragment {

    public interface DeleteCallback {
        void onConfirm();
    }

    private final DeleteCallback listener;

    public ConfirmDeleteFragment(DeleteCallback listener) {
        this.listener = listener;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.WRAP_CONTENT;
            dialog.getWindow().setLayout(width, height);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.confirm_delete_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @OnClick(R.id.btn_confirm)
    void onConfirm(){
        listener.onConfirm();
        dismiss();
    }

    @OnClick(R.id.btn_cancel)
    void onDismiss(){
        dismiss();
    }
}
