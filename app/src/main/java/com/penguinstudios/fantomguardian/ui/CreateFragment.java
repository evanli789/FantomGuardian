package com.penguinstudios.fantomguardian.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.penguinstudios.fantomguardian.R;
import com.penguinstudios.fantomguardian.adapters.SendToAdapter;
import com.penguinstudios.fantomguardian.data.model.ResetDuration;
import com.penguinstudios.fantomguardian.viewmodel.CreateViewModel;
import com.penguinstudios.fantomguardian.viewmodel.SharedViewModel;
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
public class CreateFragment extends Fragment implements SendToAdapter.AdapterCallback {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;

    @BindView(R.id.spinner_days_to_reset)
    Spinner spinnerDaysToReset;

    private CreateViewModel viewModel;
    private SendToAdapter adapter;
    private AlertDialog progressDeploySwitch;

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.create_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        SharedViewModel sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        viewModel = new ViewModelProvider(this).get(CreateViewModel.class);
        viewModel.getLiveData().observe(getViewLifecycleOwner(), state -> {
            switch (state.getState()) {
                case RECIPIENT_ADDED:
                    adapter.notifyItemInserted(viewModel.getList().size() - 1);
                    break;

                case PROGRESS_DEPLOYING_SWITCH:
                    showProgressDialog();
                    break;

                case SUCCESS_DEPLOY_SWITCH:
                    hideProgressDialog();
                    spinnerDaysToReset.setSelection(0);
                    adapter.notifyDataSetChanged();

                    initSuccessCreateFragment(state.getTxHash(), state.getContractAddress());
                    sharedViewModel.updateContractsList();
                    break;

                case NO_RESET_DURATION_SELECTED:
                    Toast.makeText(requireContext(), R.string.no_reset_duration_selected, Toast.LENGTH_LONG).show();
                    break;

                case NO_WALLET_ADDED:
                    Toast.makeText(requireContext(), R.string.no_wallet_added, Toast.LENGTH_LONG).show();
                    break;

                case ERROR:
                    hideProgressDialog();
                    Toast.makeText(requireContext(), state.getErrorMsg(), Toast.LENGTH_LONG).show();
                    break;

                case SCANNED_QR_CODE:
                    adapter.notifyItemChanged(state.getScannedAdapterPosition());
                    break;
            }
        });

        adapter = new SendToAdapter(viewModel.getList(), this);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);

        initDaysSpinner();
    }

    private void initDaysSpinner() {
        List<String> spinnerItems = new ArrayList<>();
        for (ResetDuration resetDuration : ResetDuration.values()) {
            spinnerItems.add(resetDuration.getName());
        }

        ArrayAdapter<String> spinnerAdapter = new ArrayAdapter<>(
                requireContext(), R.layout.spinner_network_drop_down, spinnerItems);

        spinnerAdapter.setDropDownViewResource(R.layout.spinner_network_drop_down);
        spinnerDaysToReset.setAdapter(spinnerAdapter);
    }

    private void initSuccessCreateFragment(String txHash, String contractAddress) {
        DialogFragment successFragment = new SuccessCreateFragment(txHash, contractAddress);
        successFragment.show(getParentFragmentManager(), null);
    }

    private void showProgressDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext(), R.style.alertDialogTheme);
        builder.setView(R.layout.progress_deploy_switch);
        progressDeploySwitch = builder.create();
        progressDeploySwitch.setCancelable(false);
        progressDeploySwitch.setCanceledOnTouchOutside(false);
        progressDeploySwitch.show();
    }

    private void hideProgressDialog() {
        if (progressDeploySwitch != null) {
            progressDeploySwitch.hide();
        }
    }

    @OnClick(R.id.btn_add)
    void onAdd() {
        viewModel.onBtnClickAddRecipient();
    }

    @OnClick(R.id.btn_create)
    void onCreateSwitch() {
        viewModel.onCreateSwitchBtnClick(spinnerDaysToReset.getSelectedItemPosition());
    }

    @Override
    public void onPause() {
        super.onPause();
        viewModel.dispose();
    }

    @Override
    public void onRecipientAddressEditTextChange(int adapterPosition, CharSequence s) {
        viewModel.onRecipientAddressEditTextChange(adapterPosition, s);
    }

    @Override
    public void onAmountEditTextChange(int adapterPosition, CharSequence s) {
        viewModel.onAmountEditTextChange(adapterPosition, s);
    }

    @Override
    public void onCommentEditTextChange(int adapterPosition, CharSequence s) {
        viewModel.onCommentEditTextChange(adapterPosition, s);
    }

    @Override
    public void onScanBtnClick(int adapterPosition) {
        viewModel.onScanBtnClick(adapterPosition);
        initScanner();
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
                viewModel.onScannedQrCode(s);
            } catch (Exception e) {
                Timber.e(e);
            }
        }
    }
}
