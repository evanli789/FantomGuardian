package com.penguinstudios.fantomguardian.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.penguinstudios.fantomguardian.R;
import com.penguinstudios.fantomguardian.adapters.ContractsAdapter;
import com.penguinstudios.fantomguardian.data.model.Network;
import com.penguinstudios.fantomguardian.util.Constants;
import com.penguinstudios.fantomguardian.viewmodel.SharedViewModel;
import com.penguinstudios.fantomguardian.viewmodel.WalletViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.hilt.android.AndroidEntryPoint;
import timber.log.Timber;

@AndroidEntryPoint
public class WalletFragment extends Fragment implements
        ContractsAdapter.ContractClickCallback,
        WalletPopupWindow.ClearWalletCallback {

    @BindView(R.id.spinner_network)
    Spinner spinnerNetwork;

    @BindView(R.id.tv_select_keystore)
    TextView tvSelectKeystore;

    @BindView(R.id.et_private_key)
    TextInputEditText etPrivateKey;

    @BindView(R.id.layout_keys)
    ViewGroup layoutKeys;

    @BindView(R.id.layout_spinner)
    ViewGroup layoutSpinner;

    @BindView(R.id.layout_progress_fetch_balance)
    ViewGroup layoutProgressWalletBalance;

    @BindView(R.id.layout_wallet_balance)
    ViewGroup layoutWalletBalance;

    @BindView(R.id.tv_wallet_balance)
    TextView tvWalletBalance;

    @BindView(R.id.tv_wallet_address)
    TextView tvWalletAddress;

    @BindView(R.id.tv_static_fetching_wallet)
    TextView tvFetchingBalance;

    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;

    @BindView(R.id.btn_wallet_more_options)
    ImageView btnWalletMoreOptions;

    @BindView(R.id.swipe_refresh_layout)
    SwipeRefreshLayout swipeRefreshLayout;

    @BindView(R.id.til_private_key)
    TextInputLayout tilPrivateKey;

    @BindView(R.id.parent_layout)
    ViewGroup parentLayout;

    private WalletViewModel walletViewModel;
    private ContractsAdapter adapter;
    private AlertDialog progressResetSwitch;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.wallet_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getWalletFragmentLiveData().observe(getViewLifecycleOwner(), state -> {
            switch (state.getState()){
                case ADD_CREDENTIALS_MNEMONIC_SUCCESS:
                    dismissMnemonicFragment();
                    walletViewModel.onMnemonicAdded();
                    break;

                case UPDATE_CONTRACTS_LIST:
                    walletViewModel.updateContractsList();
                    break;
            }
        });

        walletViewModel = new ViewModelProvider(this).get(WalletViewModel.class);
        walletViewModel.getLiveData().observe(getViewLifecycleOwner(), state -> {
            switch (state.getState()) {
                case DEFAULT:
                    layoutKeys.setVisibility(View.VISIBLE);
                    break;

                case CLEAR_WALLET:
                    resetUI();
                    sharedViewModel.clearWithdrawalsList();
                    break;

                case PROGRESS_RESET_SWITCH:
                    showProgressDialog();
                    break;

                case SUCCESS_ADD_WALLET:
                    sharedViewModel.newWalletAddedGetWithdrawals();
                    break;

                case PROGRESS_ADD_WALLET:
                    layoutKeys.setVisibility(View.GONE);
                    layoutSpinner.setVisibility(View.VISIBLE);
                    layoutProgressWalletBalance.setVisibility(View.VISIBLE);
                    break;

                case SUCCESS_GET_WALLET_BALANCE:
                    layoutProgressWalletBalance.setVisibility(View.GONE);
                    tvWalletAddress.setText(state.getAddress());
                    tvWalletBalance.setText(state.getFormattedWalletBalance());
                    layoutWalletBalance.setVisibility(View.VISIBLE);
                    break;

                case SUCCESS_GET_USER_CONTRACTS:
                    adapter.notifyDataSetChanged();
                    recyclerView.setVisibility(View.VISIBLE);
                    break;

                case SUCCESS_RESET_SWITCH:
                    hideProgressDialog();
                    Toast.makeText(requireContext(), R.string.success_reset_switch, Toast.LENGTH_LONG).show();
                    break;

                case FAILED_RESET_SWITCH:
                    progressResetSwitch.hide();
                    Toast.makeText(requireContext(), R.string.failed_reset_switch, Toast.LENGTH_LONG).show();
                    break;

                case HIDE_SWIPE_REFRESH_PROGRESS:
                    swipeRefreshLayout.setRefreshing(false);
                    break;

                case ERROR:
                    Toast.makeText(requireContext(), state.getErrorMsg(), Toast.LENGTH_LONG).show();
                    break;
            }
        });

        initNetworkSpinner();

        adapter = new ContractsAdapter(walletViewModel.getFormattedContractsList(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(
                requireContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(() -> {
            walletViewModel.onSwipeRefresh();
        });

        tilPrivateKey.setEndIconOnClickListener(icon -> {
            initScanner();
        });
    }

    private void dismissMnemonicFragment() {
        WalletMnemonicFragment walletMnemonicFragment = (WalletMnemonicFragment) getParentFragmentManager().
                findFragmentByTag(Constants.MNEMONIC_FRAGMENT);
        if (walletMnemonicFragment != null) {
            walletMnemonicFragment.dismiss();
        }
    }

    private void initNetworkSpinner() {
        List<String> spinnerItems = new ArrayList<>();
        spinnerItems.add(Network.MAIN_NET.getNetworkName());

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                requireContext(), R.layout.spinner_network_drop_down, spinnerItems);

        spinnerAdapter.setDropDownViewResource(R.layout.spinner_network_drop_down);
        spinnerNetwork.setAdapter(spinnerAdapter);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null && result.getContents() != null) {
            try {
                String s = result.getContents();
                etPrivateKey.setText(s);
            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }

    @OnClick(R.id.btn_enter_recovery_phrase)
    void onAddWithMnemonic() {
        DialogFragment mnemonicFragment = new WalletMnemonicFragment();
        mnemonicFragment.show(getParentFragmentManager(), Constants.MNEMONIC_FRAGMENT);
    }

    @OnClick(R.id.btn_load_wallet)
    void onLoadWallet() {
        CharSequence text = etPrivateKey.getText();
        if (text != null) {
            walletViewModel.onLoadWalletBtnClick(text);
        }
    }

    @OnClick(R.id.btn_wallet_more_options)
    void onClearWalletBtnClick() {
        PopupWindow popupWindow = new WalletPopupWindow(requireContext(), this);
        popupWindow.showAsDropDown(btnWalletMoreOptions, -100, 0);
    }

    @Override
    public void onPause() {
        super.onPause();
        walletViewModel.dispose();
    }

    @Override
    public void onContractClick(int adapterPosition) {
        DialogFragment contractInfoFragment = new ContractInfoFragment(
                walletViewModel.getContractsList().get(adapterPosition).getContractAddress());
        contractInfoFragment.show(getParentFragmentManager(), null);
    }

    @Override
    public void onBtnResetSwitchClick(int adapterPosition) {
        walletViewModel.resetSwitch(adapterPosition);
    }

    private void showProgressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.alertDialogTheme);
        builder.setView(R.layout.progress_reset_switch);
        progressResetSwitch = builder.create();
        progressResetSwitch.setCancelable(false);
        progressResetSwitch.setCanceledOnTouchOutside(false);
        progressResetSwitch.show();
    }

    private void hideProgressDialog() {
        if (progressResetSwitch != null) {
            progressResetSwitch.hide();
        }
    }

    @Override
    public void onClearWallet() {
        walletViewModel.clearWallet();
    }

    public void resetUI() {
        CharSequence text = etPrivateKey.getText();
        if (text != null) {
            etPrivateKey.getText().clear();
        }

        layoutWalletBalance.setVisibility(View.GONE);
        layoutSpinner.setVisibility(View.GONE);
        layoutKeys.setVisibility(View.VISIBLE);
        recyclerView.setVisibility(View.GONE);
    }

    @Override
    public void onWalletQr() {
        DialogFragment walletQrDialog = new QRWalletFragment(walletViewModel.getWalletAddress());
        walletQrDialog.show(getParentFragmentManager(), null);
    }

    @OnClick(R.id.btn_github)
    void onGithub() {
        String url = Constants.GITHUB_URL;
        CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
        CustomTabsIntent customTabsIntent = builder.build();
        customTabsIntent.launchUrl(requireContext(), Uri.parse(url));
    }

    private void initScanner() {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Add private key");
        integrator.setBeepEnabled(false);
        integrator.setOrientationLocked(true);
        integrator.initiateScan();
    }
}
