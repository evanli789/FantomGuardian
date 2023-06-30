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
import com.penguinstudios.fantomguardian.data.model.SuccessfulWithdrawal;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class SuccessWithdrawFragment extends DialogFragment {

    @BindView(R.id.tv_tx_hash)
    TextView txHash;

    @BindView(R.id.tv_contract_address)
    TextView tvContractAddress;

    @BindView(R.id.tv_amount)
    TextView tvAmount;

    private final SuccessfulWithdrawal successfulWithdrawal;

    public SuccessWithdrawFragment(SuccessfulWithdrawal successfulWithdrawal) {
        this.successfulWithdrawal = successfulWithdrawal;
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

        View view = inflater.inflate(R.layout.success_withdraw_fragment, container, false);
        ButterKnife.bind(this, view);

        tvContractAddress.setText(successfulWithdrawal.getContractAddress());
        tvAmount.setText(successfulWithdrawal.getFormattedAmount());
        txHash.setText(successfulWithdrawal.getTxHash());

        return view;
    }

    @OnClick(R.id.btn_view_explorer)
    void onViewExplorer() {
        this.dismiss();
        String url = Network.MAIN_NET.getExplorerUrl() + successfulWithdrawal.getContractAddress();
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(requireContext(), Uri.parse(url));
    }
}
