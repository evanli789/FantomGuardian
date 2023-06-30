package com.penguinstudios.fantomguardian.ui;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.penguinstudios.fantomguardian.R;
import com.penguinstudios.fantomguardian.adapters.MnemonicAdapter;
import com.penguinstudios.fantomguardian.util.MnemonicItemDecoration;
import com.penguinstudios.fantomguardian.util.SpacingUtils;
import com.penguinstudios.fantomguardian.viewmodel.SharedViewModel;
import com.penguinstudios.fantomguardian.viewmodel.WalletMnemonicViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class WalletMnemonicFragment extends DialogFragment implements MnemonicAdapter.EditTextCallback {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;

    private static final int GRID_VIEW_SPACING = 16;
    private static final int GRID_VIEW_COLUMN_COUNT = 3;
    private WalletMnemonicViewModel walletMnemonicViewModel;
    private SharedViewModel sharedViewModel;

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

        View view = inflater.inflate(R.layout.wallet_mnemonic_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        walletMnemonicViewModel = new ViewModelProvider(this).get(WalletMnemonicViewModel.class);
        walletMnemonicViewModel.getLiveData().observe(getViewLifecycleOwner(), state -> {
            switch (state.getState()) {
                case ADD_CREDENTIALS_SUCCESS:
                    sharedViewModel.addCredentialsMnemonicSuccess();
                    break;

                case ERROR:
                    Toast.makeText(requireContext(), state.getErrorMsg(), Toast.LENGTH_LONG).show();
                    break;
            }
        });

        MnemonicAdapter adapter = new MnemonicAdapter(walletMnemonicViewModel.getMap(),
                WalletMnemonicViewModel.NUM_WORDS_MNEMONIC, this);

        int spacing = SpacingUtils.convertIntToDP(requireContext(), GRID_VIEW_SPACING);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(requireContext(), GRID_VIEW_COLUMN_COUNT);

        recyclerView.addItemDecoration(new MnemonicItemDecoration(GRID_VIEW_COLUMN_COUNT, spacing, true));
        recyclerView.setLayoutManager(gridLayoutManager);
        recyclerView.setAdapter(adapter);
    }

    @OnClick(R.id.btn_back)
    void onClose() {
        dismiss();
    }

    @OnClick(R.id.btn_load_wallet)
    void onLoadWallet() {
        walletMnemonicViewModel.onLoadWalletBtnClick();
    }

    @Override
    public void onEditTextChange(int adapterPosition, CharSequence s) {
        walletMnemonicViewModel.onMnemonicEditTextChange(adapterPosition, s);
    }
}
