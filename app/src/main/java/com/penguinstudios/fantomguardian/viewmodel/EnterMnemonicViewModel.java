package com.penguinstudios.fantomguardian.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.penguinstudios.fantomguardian.data.state.WithdrawDecryptionPhraseUIState;
import com.penguinstudios.fantomguardian.util.MapToStringConverter;
import com.penguinstudios.fantomguardian.data.validator.WithdrawValidator;

import java.util.Map;
import java.util.TreeMap;

public class EnterMnemonicViewModel extends ViewModel {

    public static final int NUM_WORDS_MNEMONIC = 12;
    private final MutableLiveData<WithdrawDecryptionPhraseUIState> liveData = new MutableLiveData<>();
    private final Map<Integer, CharSequence> map = new TreeMap<>();

    public MutableLiveData<WithdrawDecryptionPhraseUIState> getLiveData() {
        return liveData;
    }

    public Map<Integer, CharSequence> getMap() {
        return map;
    }

    public void onMnemonicEditTextChange(int adapterPosition, CharSequence s) {
        CharSequence currentValue = map.get(adapterPosition);
        if (currentValue != null && currentValue.length() == 0) {
            map.remove(adapterPosition);
        } else {
            map.put(adapterPosition, s);
        }
    }

    public void onConfirmBtnClick() {
        String mnemonic = MapToStringConverter.convert(map).toLowerCase();

        WithdrawValidator validator = new WithdrawValidator();

        try {
            validator.isAllMnemonicFieldsFilled(NUM_WORDS_MNEMONIC, map);
            validator.isValidMnemonic(mnemonic);
        } catch (IllegalArgumentException e) {
            liveData.setValue(WithdrawDecryptionPhraseUIState.error(e.getMessage()));
            return;
        }

        String decryptionPhrase = MapToStringConverter.convert(map).toLowerCase();
        liveData.setValue(WithdrawDecryptionPhraseUIState.mnemonicValid(decryptionPhrase));
    }
}
