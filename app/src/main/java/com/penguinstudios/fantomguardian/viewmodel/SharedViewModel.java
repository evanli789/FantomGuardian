package com.penguinstudios.fantomguardian.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.penguinstudios.fantomguardian.data.state.WalletUIState;
import com.penguinstudios.fantomguardian.data.state.WithdrawInfoUIState;
import com.penguinstudios.fantomguardian.data.state.WithdrawUIState;

public class SharedViewModel extends ViewModel {

    private final MutableLiveData<WalletUIState> walletFragmentLiveData = new MutableLiveData<>();
    private final MutableLiveData<WithdrawUIState> withdrawFragmentLiveData = new MutableLiveData<>();
    private final MutableLiveData<WithdrawInfoUIState> withdrawInfoFragmentLiveData = new MutableLiveData<>();

    //Called from WalletMnemonicFragment
    public void addCredentialsMnemonicSuccess() {
        walletFragmentLiveData.setValue(WalletUIState.addCredentialsMnemonicSuccess());
    }

    //Called from EnterMnemonicFragment that's created from WithdrawFragment
    public void decryptionPhraseAddedInWithdrawFunds(String mnemonic) {
        withdrawFragmentLiveData.setValue(WithdrawUIState.decryptionPhraseAddedInWithdrawFunds(mnemonic));
    }

    //Called from EnterMnemonicFragment that's created from WithdrawInfoFragment
    public void decryptCommentsDecryptionPhraseAdded(String mnemonic) {
        withdrawInfoFragmentLiveData.setValue(WithdrawInfoUIState.decryptCommentsDecryptionPhraseAdded(mnemonic));
    }

    //Called from WalletFragment
    public void newWalletAddedGetWithdrawals() {
        withdrawFragmentLiveData.setValue(WithdrawUIState.newWalletAdded());
    }

    //Called from WalletFragment
    public void clearWithdrawalsList(){
        withdrawFragmentLiveData.setValue(WithdrawUIState.walletCleared());
    }

    //Called from CreateFragment
    public void updateContractsList(){
        walletFragmentLiveData.setValue(WalletUIState.updateContractsList());
    }

    public MutableLiveData<WalletUIState> getWalletFragmentLiveData() {
        return walletFragmentLiveData;
    }

    public MutableLiveData<WithdrawInfoUIState> getWithdrawInfoFragmentLiveData() {
        return withdrawInfoFragmentLiveData;
    }

    public MutableLiveData<WithdrawUIState> getWithdrawFragmentLiveData() {
        return withdrawFragmentLiveData;
    }
}
