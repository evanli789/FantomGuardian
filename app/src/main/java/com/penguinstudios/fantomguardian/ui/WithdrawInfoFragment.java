package com.penguinstudios.fantomguardian.ui;

import android.app.Dialog;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.penguinstudios.fantomguardian.R;
import com.penguinstudios.fantomguardian.data.model.FormattedWithdrawInfo;
import com.penguinstudios.fantomguardian.data.model.Network;
import com.penguinstudios.fantomguardian.data.state.WithdrawInfoUIState;
import com.penguinstudios.fantomguardian.di.WithdrawInfoViewModelFactory;
import com.penguinstudios.fantomguardian.util.ClipboardUtil;
import com.penguinstudios.fantomguardian.util.Constants;
import com.penguinstudios.fantomguardian.viewmodel.SharedViewModel;
import com.penguinstudios.fantomguardian.viewmodel.WithdrawInfoViewModel;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WithdrawInfoFragment extends DialogFragment {

    @BindView(R.id.tv_contract_address)
    TextView tvContractAddress;

    @BindView(R.id.tv_date_withdrawn)
    TextView tvDateWithdrawn;

    @BindView(R.id.tv_amount)
    TextView tvAmount;

    @BindView(R.id.tv_comments)
    TextView tvComments;

    @BindView(R.id.progressbar)
    ProgressBar progressBar;

    @BindView(R.id.layout_wrapper)
    ViewGroup layoutWrapper;

    @Inject
    WithdrawInfoViewModelFactory viewModelFactory;

    private WithdrawInfoViewModel withdrawInfoViewModel;
    private final String contractAddress;

    public WithdrawInfoFragment(String contractAddress) {
        this.contractAddress = contractAddress;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_FantomGuardian);

        final Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().getAttributes().windowAnimations = R.style.FragmentEnterFromLeft;

        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.withdraw_info_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getWithdrawInfoFragmentLiveData().observe(this, state -> {
            if (state.getState() == WithdrawInfoUIState.State.DECRYPTION_PHRASE_ADDED_FROM_ENTER_DECRYPTION_PHRASE) {
                withdrawInfoViewModel.setDecryptionPhrase(state.getDecryptionPhrase());
                withdrawInfoViewModel.decryptComments();
            }
        });

        withdrawInfoViewModel = new ViewModelProvider(this,
                WithdrawInfoViewModel.provideFactory(viewModelFactory, contractAddress))
                .get(WithdrawInfoViewModel.class);

        withdrawInfoViewModel.getLiveData().observe(getViewLifecycleOwner(), state -> {
            FormattedWithdrawInfo withdrawInfo = state.getFormattedWithdrawInfo();
            switch (state.getState()) {
                case SUCCESS_GET_WITHDRAWAL:
                    tvDateWithdrawn.setText(withdrawInfo.getFormattedDateWithdrawn());
                    tvAmount.setText(withdrawInfo.getFormattedAmount());
                    progressBar.setVisibility(View.GONE);
                    layoutWrapper.setVisibility(View.VISIBLE);
                    break;

                case NO_DECRYPTION_PHRASE_AVAILABLE:
                    tvComments.setText("Encrypted");
                    tvComments.setTextColor(ContextCompat.getColor(requireContext(), R.color.yellow_400));
                    break;

                case DECRYPTION_PHRASE_AVAILABLE:
                    tvComments.setText(state.getDecryptedComments());
                    tvComments.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
                    break;

                case SUCCESSFULLY_DECRYPTED_COMMENTS:
                    tvComments.setText(state.getDecryptedComments());
                    tvComments.setTextColor(ContextCompat.getColor(requireContext(), R.color.white));
                    dismissEnterDecryptionPhraseFragment();
                    break;

                case PROGRESS_GET_WITHDRAWAL:
                    progressBar.setVisibility(View.VISIBLE);
                    break;

                case ERROR:
                    Toast.makeText(requireContext(), state.getErrorMsg(), Toast.LENGTH_SHORT).show();
                    break;
            }
        });

        tvContractAddress.setText(withdrawInfoViewModel.getContractAddress());
    }

    private void dismissEnterDecryptionPhraseFragment() {
        DialogFragment enterDecryptionPhraseFragment = (DialogFragment) getParentFragmentManager()
                .findFragmentByTag(Constants.ENTER_DECRYPTION_PHRASE_FRAGMENT_DECRYPT_COMMENTS);

        if (enterDecryptionPhraseFragment != null) {
            enterDecryptionPhraseFragment.dismiss();
        }
    }

    @OnClick(R.id.btn_back)
    void onClose() {
        dismiss();
    }

    @Override
    public void onPause() {
        super.onPause();
        withdrawInfoViewModel.dispose();
    }

    @OnClick(R.id.btn_decrypt_comments)
    void onDecryptComments() {
        DialogFragment enterMnemonicFragment = new EnterMnemonicFragment();
        enterMnemonicFragment.show(getParentFragmentManager(), Constants.ENTER_DECRYPTION_PHRASE_FRAGMENT_DECRYPT_COMMENTS);
    }


    @OnClick(R.id.btn_copy)
    void onCopy() {
        Toast.makeText(requireContext(), "Address copied", Toast.LENGTH_SHORT).show();
        ClipboardUtil.copyText(requireContext(), contractAddress);
    }

    @OnClick(R.id.btn_view_on_explorer)
    void onViewExplorer() {
        String url = Network.MAIN_NET.getExplorerUrl() + contractAddress;
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(requireContext(), Uri.parse(url));
    }
}
