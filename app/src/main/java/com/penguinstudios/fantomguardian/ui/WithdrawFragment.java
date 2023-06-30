package com.penguinstudios.fantomguardian.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.penguinstudios.fantomguardian.R;
import com.penguinstudios.fantomguardian.adapters.WithdrawnFundsAdapter;
import com.penguinstudios.fantomguardian.data.model.SuccessfulWithdrawal;
import com.penguinstudios.fantomguardian.util.Constants;
import com.penguinstudios.fantomguardian.viewmodel.SharedViewModel;
import com.penguinstudios.fantomguardian.viewmodel.WithdrawViewModel;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.hilt.android.AndroidEntryPoint;
import timber.log.Timber;

@AndroidEntryPoint
public class WithdrawFragment extends Fragment implements WithdrawnFundsAdapter.WithdrawnFundsClickCallback {

    @BindView(R.id.til_contract_address)
    TextInputLayout tilContractAddress;

    @BindView(R.id.et_contract_address)
    TextInputEditText etContractAddress;

    @BindView(R.id.tv_static_decryption_phrase)
    TextView tvDecryptionBtnName;

    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;

    private WithdrawViewModel viewModel;
    private AlertDialog progressWithdraw;
    private WithdrawnFundsAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.withdraw_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);
        sharedViewModel.getWithdrawFragmentLiveData().observe(getViewLifecycleOwner(), state -> {
            switch (state.getState()){
                case NEW_WALLET_ADDED:
                    viewModel.getWithdrawals();
                    break;

                case WALLET_CLEARED:
                    viewModel.clearWithdrawalsList();
                    adapter.notifyDataSetChanged();

                    clearContractEditText();
                    resetEnterDecryptionPhraseBtn();
                    break;

                case ENTER_DECRYPTION_PHRASE_MNEMONIC_ADDED:
                    dismissDecryptionPhraseFragment();
                    viewModel.setDecryptionPhrase(state.getMnemonic());
                    tvDecryptionBtnName.setText(R.string.decryption_phrase_added);
                    tvDecryptionBtnName.setTextColor(ContextCompat.getColor(requireContext(), R.color.green_400));
                    break;
            }
        });

        viewModel = new ViewModelProvider(this).get(WithdrawViewModel.class);
        viewModel.getLiveData().observe(getViewLifecycleOwner(), state -> {
            switch (state.getState()) {
                case SUCCESS_WITHDRAW:
                    hideProgressDialog();
                    initSuccessWithdrawFragment(state.getSuccessfulWithdrawal());

                    clearContractEditText();
                    resetEnterDecryptionPhraseBtn();

                    viewModel.getWithdrawals();
                    break;

                case PROGRESS_WITHDRAW:
                    showProgressDialog();
                    break;

                case ERROR:
                    hideProgressDialog();
                    Toast.makeText(requireContext(), state.getErrorMsg(), Toast.LENGTH_LONG).show();
                    break;

                case SUCCESS_GET_WITHDRAWALS:
                    adapter.notifyDataSetChanged();
                    break;

                case NO_WALLET_ADDED:
                    Toast.makeText(requireContext(), R.string.no_wallet_added_claim, Toast.LENGTH_LONG).show();
                    break;
            }
        });

        tilContractAddress.setEndIconOnClickListener(icon -> {
            initScanner();
        });

        adapter = new WithdrawnFundsAdapter(viewModel.getFormattedWithdrawalList(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(
                requireContext(), LinearLayoutManager.HORIZONTAL, false));
        recyclerView.setAdapter(adapter);
    }

    private void clearContractEditText(){
        CharSequence text = etContractAddress.getText();
        if (text != null) {
            etContractAddress.getText().clear();
        }
    }

    private void resetEnterDecryptionPhraseBtn(){
        tvDecryptionBtnName.setText(R.string.enter_decryption_phrase);
        tvDecryptionBtnName.setTextColor(ContextCompat.getColor(requireContext(), R.color.default_text_color));
    }

    private void dismissDecryptionPhraseFragment() {
        DialogFragment decryptionPhraseFragment = (EnterMnemonicFragment) getParentFragmentManager()
                .findFragmentByTag(Constants.ENTER_DECRYPTION_PHRASE_FRAGMENT_WITHDRAW_FUNDS);

        if (decryptionPhraseFragment != null) {
            decryptionPhraseFragment.dismiss();
        }
    }

    private void initSuccessWithdrawFragment(SuccessfulWithdrawal successfulWithdrawal) {
        DialogFragment successWithdraw = new SuccessWithdrawFragment(successfulWithdrawal);
        successWithdraw.show(getParentFragmentManager(), null);
    }

    private void showProgressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.alertDialogTheme);
        builder.setView(R.layout.progress_withdraw);
        progressWithdraw = builder.create();
        progressWithdraw.setCancelable(false);
        progressWithdraw.setCanceledOnTouchOutside(false);
        progressWithdraw.show();
    }

    private void hideProgressDialog() {
        if (progressWithdraw != null) {
            progressWithdraw.hide();
        }
    }

    @OnClick(R.id.btn_submit)
    void onSubmit() {
        CharSequence text = etContractAddress.getText();
        if (text != null) {
            viewModel.onClaimBtnClick(text);
        }
    }

    @OnClick(R.id.btn_decryption_phrase)
    void onEnterPhrase() {
        DialogFragment fragment = new EnterMnemonicFragment();
        fragment.show(getParentFragmentManager(), Constants.ENTER_DECRYPTION_PHRASE_FRAGMENT_WITHDRAW_FUNDS);
    }

    @Override
    public void onPause() {
        super.onPause();
        viewModel.dispose();
    }

    private void initScanner() {
        IntentIntegrator integrator = IntentIntegrator.forSupportFragment(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.QR_CODE);
        integrator.setPrompt("Send to address");
        integrator.setBeepEnabled(false);
        integrator.setOrientationLocked(true);
        integrator.initiateScan();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null && result.getContents() != null) {
            try {
                String s = result.getContents();
                etContractAddress.setText(s);
            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }

    @Override
    public void onWithdrawnFundsClick(int adapterPosition) {
        DialogFragment withdrawInfoFragment = new WithdrawInfoFragment(
                viewModel.getFormattedWithdrawalList().get(adapterPosition).getContractAddress());
        withdrawInfoFragment.show(getParentFragmentManager(), null);
    }
}
