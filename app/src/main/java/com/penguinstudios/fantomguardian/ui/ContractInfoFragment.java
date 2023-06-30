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
import androidx.appcompat.app.AlertDialog;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.penguinstudios.fantomguardian.R;
import com.penguinstudios.fantomguardian.adapters.RecipientsAdapter;
import com.penguinstudios.fantomguardian.data.model.FormattedContractInfo;
import com.penguinstudios.fantomguardian.data.model.Network;
import com.penguinstudios.fantomguardian.di.ContractInfoViewModelFactory;
import com.penguinstudios.fantomguardian.util.ClipboardUtil;
import com.penguinstudios.fantomguardian.viewmodel.ContractInfoViewModel;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class ContractInfoFragment extends DialogFragment implements
        ConfirmDeleteFragment.DeleteCallback, RecipientsAdapter.SelectedRecipientCallback {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;

    @BindView(R.id.tv_contract_address)
    TextView tvContractAddress;

    @BindView(R.id.tv_date_expiration)
    TextView tvDateExpiration;

    @BindView(R.id.progress_contract_info)
    ProgressBar progressContractInfo;

    @BindView(R.id.tv_static_date_created)
    TextView tvStaticDateCreated;

    @BindView(R.id.tv_date_created)
    TextView tvDateCreated;

    @BindView(R.id.tv_static_recipients)
    TextView tvStaticRecipients;

    @BindView(R.id.layout_btns)
    ViewGroup btnLayout;

    @Inject
    ContractInfoViewModelFactory viewModelFactory;

    private ContractInfoViewModel viewModel;
    private final String contractAddress;
    private AlertDialog progressDeleteSwitch;
    private RecipientsAdapter adapter;

    public ContractInfoFragment(String contractAddress) {
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

        View view = inflater.inflate(R.layout.contract_info_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this,
                ContractInfoViewModel.provideFactory(viewModelFactory, contractAddress))
                .get(ContractInfoViewModel.class);

        viewModel.getLiveData().observe(getViewLifecycleOwner(), state -> {
            switch (state.getState()) {
                case PROGRESS_DELETE_SWITCH:
                    showProgressDialog();
                    break;

                case SUCCESS_DELETE_SWITCH:
                    hideProgressDialog();
                    initSuccessDeleteFragment(state.getTxHash(), state.getContractAddress());
                    dismiss();
                    break;

                case FAILED_DELETE_SWITCH:
                    hideProgressDialog();
                    Toast.makeText(requireContext(), R.string.failed_delete_switch, Toast.LENGTH_LONG).show();
                    break;

                case SUCCESS_GET_CONTRACT_INFO:
                    FormattedContractInfo info = state.getFormattedContractInfo();
                    tvDateExpiration.setText(info.getFormattedDateExpiration());
                    tvDateCreated.setText(info.getFormattedDateCreated());
                    progressContractInfo.setVisibility(View.GONE);

                    int redColor = ContextCompat.getColor(requireContext(), R.color.red_500);

                    if (info.isContractExpired()) {
                        tvDateExpiration.setTextColor(redColor);
                    }

                    tvStaticDateCreated.setVisibility(View.VISIBLE);
                    tvDateCreated.setVisibility(View.VISIBLE);
                    tvStaticRecipients.setVisibility(View.VISIBLE);
                    btnLayout.setVisibility(View.VISIBLE);
                    adapter.notifyDataSetChanged();
                    break;

                case FAILED_GET_CONTRACT_INFO:
                    Toast.makeText(requireContext(), R.string.failed_get_contract_info, Toast.LENGTH_LONG).show();
                    break;
            }
        });

        tvContractAddress.setText(viewModel.getContractAddress());

        adapter = new RecipientsAdapter(viewModel.getRecipientList(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(
                requireContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);
    }

    private void showProgressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.alertDialogTheme);
        builder.setView(R.layout.progress_delete_switch);
        progressDeleteSwitch = builder.create();
        progressDeleteSwitch.setCancelable(false);
        progressDeleteSwitch.setCanceledOnTouchOutside(false);
        progressDeleteSwitch.show();
    }

    private void hideProgressDialog() {
        if (progressDeleteSwitch != null) {
            progressDeleteSwitch.hide();
        }
    }

    private void initSuccessDeleteFragment(String txHash, String contractAddress) {
        DialogFragment successDeleteFragment = new SuccessDeleteFragment(txHash, contractAddress);
        successDeleteFragment.show(getParentFragmentManager(), null);
    }

    @Override
    public void onPause() {
        super.onPause();
        viewModel.dispose();
    }

    @OnClick(R.id.btn_back)
    void onClose() {
        dismiss();
    }

    @OnClick(R.id.btn_delete_contract)
    void onDelete() {
        DialogFragment confirmDeleteFragment = new ConfirmDeleteFragment(this);
        confirmDeleteFragment.show(getParentFragmentManager(), null);
    }

    @OnClick(R.id.btn_copy)
    void onCopy() {
        Toast.makeText(requireContext(), "Address copied", Toast.LENGTH_SHORT).show();
        ClipboardUtil.copyText(requireContext(), contractAddress);
    }

    @OnClick(R.id.btn_decryption_phrase)
    void onDecryptionPhrase() {
        DialogFragment viewDecryptionPhraseFragment =
                new ViewDecryptionPhraseFragment(viewModel.getContractAddress());
        viewDecryptionPhraseFragment.show(getParentFragmentManager(), null);
    }

    @OnClick(R.id.btn_view_on_explorer)
    void onViewExplorer() {
        String url = Network.MAIN_NET.getExplorerUrl() + contractAddress;
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(requireContext(), Uri.parse(url));
    }

    @Override
    public void onConfirm() {
        viewModel.deleteSwitch();
    }

    @Override
    public void onRecipientClick(int adapterPosition) {
        DialogFragment readMoreFragment = new ReadMoreFragment(
                viewModel.getRecipientList().get(adapterPosition).getComments());
        readMoreFragment.show(getParentFragmentManager(), null);
    }
}
