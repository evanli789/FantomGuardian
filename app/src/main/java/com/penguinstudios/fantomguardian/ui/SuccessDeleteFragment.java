package com.penguinstudios.fantomguardian.ui;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.fragment.app.DialogFragment;

import com.penguinstudios.fantomguardian.R;
import com.penguinstudios.fantomguardian.data.model.Network;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SuccessDeleteFragment extends DialogFragment {

    @BindView(R.id.tv_tx_hash)
    TextView tvTxHash;

    private final String txHash, contractAddress;

    public SuccessDeleteFragment(String txHash, String contractAddress) {
        this.txHash = txHash;
        this.contractAddress = contractAddress;
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

        View view = inflater.inflate(R.layout.success_delete_fragment, container, false);
        ButterKnife.bind(this, view);

        tvTxHash.setText(txHash);

        return view;
    }

    @OnClick(R.id.btn_view_explorer)
    void onViewIconTrackerBtnClick() {
        this.dismiss();
        String url = Network.MAIN_NET.getExplorerUrl() + contractAddress;
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(requireContext(), Uri.parse(url));
    }
}
