package com.penguinstudios.fantomguardian.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.penguinstudios.fantomguardian.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class ReadMoreFragment extends DialogFragment {

    @BindView(R.id.tv_comments)
    TextView tvComments;

    private final String comments;

    public ReadMoreFragment(String comments) {
        this.comments = comments;
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

        View view = inflater.inflate(R.layout.read_more_fragment, container, false);
        ButterKnife.bind(this, view);

        tvComments.setText(comments);

        return view;
    }

    @OnClick(R.id.btn_close)
    void onClose() {
        dismiss();
    }
}
