package com.penguinstudios.fantomguardian.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.penguinstudios.fantomguardian.data.SharedPrefManager;
import com.penguinstudios.fantomguardian.data.WalletRepository;
import com.penguinstudios.fantomguardian.data.state.WalletMnemonicUIState;
import com.penguinstudios.fantomguardian.util.MapToStringConverter;
import com.penguinstudios.fantomguardian.util.WalletUtil;
import com.penguinstudios.fantomguardian.data.validator.WalletValidator;

import org.web3j.crypto.Bip32ECKeyPair;
import org.web3j.crypto.Credentials;

import java.util.Map;
import java.util.TreeMap;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;

@HiltViewModel
public class WalletMnemonicViewModel extends ViewModel {

    public static final int NUM_WORDS_MNEMONIC = 12;
    private final WalletRepository walletRepository;
    private final SharedPrefManager sharedPrefManager;
    private final MutableLiveData<WalletMnemonicUIState> liveData = new MutableLiveData<>();
    private final Map<Integer, CharSequence> map = new TreeMap<>();

    @Inject
    public WalletMnemonicViewModel(
            WalletRepository walletRepository,
            SharedPrefManager sharedPrefManager) {

        this.walletRepository = walletRepository;
        this.sharedPrefManager = sharedPrefManager;
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

    public void onLoadWalletBtnClick() {
        String mnemonic = MapToStringConverter.convert(map).toLowerCase();

        WalletValidator validator = new WalletValidator();
        try {
            validator.isAllMnemonicFieldsFilled(NUM_WORDS_MNEMONIC, map);
            validator.isValidMnemonic(mnemonic);
        } catch (IllegalArgumentException e) {
            liveData.setValue(WalletMnemonicUIState.error(e.getMessage()));
            return;
        }

        Bip32ECKeyPair derivedKeyPair = WalletUtil.deriveKeyPairFromMnemonic(mnemonic);
        Credentials credentials = Credentials.create(derivedKeyPair);

        walletRepository.setCredentials(credentials);
        sharedPrefManager.setMnemonic(mnemonic);
        sharedPrefManager.setIsMnemonicAdded(true);

        liveData.setValue(WalletMnemonicUIState.addCredentialsSuccess());
    }

    public MutableLiveData<WalletMnemonicUIState> getLiveData() {
        return liveData;
    }
}
