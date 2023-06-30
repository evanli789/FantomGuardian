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
import com.penguinstudios.fantomguardian.util.Constants;
import com.penguinstudios.fantomguardian.util.MnemonicItemDecoration;
import com.penguinstudios.fantomguardian.util.SpacingUtils;
import com.penguinstudios.fantomguardian.viewmodel.EnterMnemonicViewModel;
import com.penguinstudios.fantomguardian.viewmodel.SharedViewModel;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import dagger.hilt.android.AndroidEntryPoint;

@AndroidEntryPoint
public class EnterMnemonicFragment extends DialogFragment implements MnemonicAdapter.EditTextCallback {

    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;

    private static final int GRID_VIEW_SPACING = 16;
    private static final int GRID_VIEW_COLUMN_COUNT = 3;
    private EnterMnemonicViewModel enterMnemonicViewModel;
    private SharedViewModel sharedViewModel;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_FantomGuardian);

        final Dialog dialog = super.onCreateDialog(savedInstanceState);
        dialog.getWindow().getAttributes().windowAnimations = R.style.FragmentSlideUpAnim;

        return dialog;
    }

    @Nullable
    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater,
            @Nullable ViewGroup container,
            @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.enter_mnemonic_fragment, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        enterMnemonicViewModel = new ViewModelProvider(this).get(EnterMnemonicViewModel.class);
        enterMnemonicViewModel.getLiveData().observe(getViewLifecycleOwner(), state -> {
            switch (state.getState()) {
                case ERROR:
                    Toast.makeText(requireContext(), state.getErrorMsg(), Toast.LENGTH_SHORT).show();
                    break;

                case VALID_DECRYPTION_PHRASE:
                    String decryptionPhrase = state.getDecryptionPhrase();
                    String tag = getTag();

                    if (tag == null) {
                        throw new IllegalStateException("No tag available");
                    }

                    if (tag.equals(Constants.ENTER_DECRYPTION_PHRASE_FRAGMENT_WITHDRAW_FUNDS)) {
                        sharedViewModel.decryptionPhraseAddedInWithdrawFunds(decryptionPhrase);
                    } else if (tag.equals(Constants.ENTER_DECRYPTION_PHRASE_FRAGMENT_DECRYPT_COMMENTS)) {
                        sharedViewModel.decryptCommentsDecryptionPhraseAdded(decryptionPhrase);
                    }

                    break;
            }
        });

        MnemonicAdapter adapter = new MnemonicAdapter(enterMnemonicViewModel.getMap(),
                EnterMnemonicViewModel.NUM_WORDS_MNEMONIC, this);

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

    @OnClick(R.id.btn_confirm)
    void onConfirm() {
        enterMnemonicViewModel.onConfirmBtnClick();
    }

    @Override
    public void onEditTextChange(int adapterPosition, CharSequence s) {
        enterMnemonicViewModel.onMnemonicEditTextChange(adapterPosition, s);
    }
}
