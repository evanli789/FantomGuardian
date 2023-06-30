package com.penguinstudios.fantomguardian.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.penguinstudios.fantomguardian.data.LocalRepository;
import com.penguinstudios.fantomguardian.data.RemoteRepository;
import com.penguinstudios.fantomguardian.data.WalletRepository;
import com.penguinstudios.fantomguardian.data.model.Contract;
import com.penguinstudios.fantomguardian.data.model.ResetDuration;
import com.penguinstudios.fantomguardian.data.model.SendToRecipient;
import com.penguinstudios.fantomguardian.data.state.CreateUIState;
import com.penguinstudios.fantomguardian.data.validator.CreateSwitchValidator;
import com.penguinstudios.fantomguardian.util.WalletUtil;

import org.web3j.protocol.core.methods.response.TransactionReceipt;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

@HiltViewModel
public class CreateViewModel extends ViewModel {

    private final RemoteRepository remoteRepository;
    private final WalletRepository walletRepository;
    private final LocalRepository localRepository;
    private final MutableLiveData<CreateUIState> liveData = new MutableLiveData<>();
    private final List<SendToRecipient> list = new ArrayList<>();
    private Disposable deployContractDisposable;
    private int positionOfClickedScanBtn;

    @Inject
    public CreateViewModel(
            RemoteRepository remoteRepository,
            WalletRepository walletRepository,
            LocalRepository localRepository) {

        this.remoteRepository = remoteRepository;
        this.walletRepository = walletRepository;
        this.localRepository = localRepository;

        list.add(SendToRecipient.createEmptyRecipient());
    }

    public void onCreateSwitchBtnClick(int selectedDaysToResetSpinnerPosition) {
        if (walletRepository.getCredentials() == null) {
            liveData.setValue(CreateUIState.noWalletAdded());
            return;
        }

        ResetDuration selectedSpinnerItem = ResetDuration.values()[selectedDaysToResetSpinnerPosition];

        if (selectedSpinnerItem == ResetDuration.NO_DATE_SELECTED) {
            liveData.setValue(CreateUIState.noResetDurationSelected());
            return;
        }

        CreateSwitchValidator validator = new CreateSwitchValidator();

        try {
            validator.isValidListOfRecipients(list, walletRepository.getAddress());
        } catch (IllegalArgumentException err) {
            Timber.e(err);
            liveData.setValue(CreateUIState.error(err.getMessage()));
            return;
        }

        deployContract(list, selectedSpinnerItem);
    }

    /**
     * Deploys the smart contract and stores a copy locally in Room
     * @param sendToRecipients List of recipients to send to
     * @param resetDuration The duration in which switch must be reset
     */
    private void deployContract(List<SendToRecipient> sendToRecipients, ResetDuration resetDuration) {
        liveData.setValue(CreateUIState.progressDeployingSwitch());

        deployContractDisposable = Single.fromCallable(() -> {
            String mnemonic = WalletUtil.generate12WordMnemonic();

            TransactionReceipt receipt = remoteRepository.createSwitch(sendToRecipients, resetDuration, mnemonic)
                    .getTransactionReceipt().orElseThrow(() -> new IllegalStateException("Transaction receipt not present"));

            String txHash = receipt.getTransactionHash();
            String contractAddress = receipt.getContractAddress();

            BigInteger expirationDate = remoteRepository.getDateExpiration(contractAddress);

            return new Contract.Builder()
                    .ownerAddress(walletRepository.getAddress())
                    .contractAddress(contractAddress)
                    .numRecipients(sendToRecipients)
                    .resetDuration(resetDuration)
                    .dateOfExpiration(expirationDate)
                    .contractTxHash(txHash)
                    .decryptionPhrase(mnemonic)
                    .build();
        })
                .flatMap(contract -> localRepository.insertContract(contract).map(rowId -> contract))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(contract -> {
                    list.clear();
                    list.add(SendToRecipient.createEmptyRecipient());
                    liveData.postValue(CreateUIState.successDeploySwitch(
                            contract.getContractTxHash(), contract.getContractAddress()));
                }, err -> {
                    liveData.setValue(CreateUIState.error(err.getMessage()));
                    Timber.e(err);
                });
    }

    public void onRecipientAddressEditTextChange(int adapterPosition, CharSequence s) {
        list.get(adapterPosition).setRecipientAddress(s);
    }

    public void onAmountEditTextChange(int adapterPosition, CharSequence s) {
        list.get(adapterPosition).setAmountFTM(s);
    }

    public void onCommentEditTextChange(int adapterPosition, CharSequence s) {
        list.get(adapterPosition).setComments(s);
    }

    public List<SendToRecipient> getList() {
        return list;
    }

    public void onBtnClickAddRecipient() {
        list.add(SendToRecipient.createEmptyRecipient());
        liveData.setValue(CreateUIState.recipientAdded());
    }

    public MutableLiveData<CreateUIState> getLiveData() {
        return liveData;
    }

    public void dispose() {
        if (deployContractDisposable != null) {
            deployContractDisposable.dispose();
        }
    }

    //Saves the position of the viewholder the user clicked on for the scan button
    public void onScanBtnClick(int adapterPosition) {
        this.positionOfClickedScanBtn = adapterPosition;
    }

    public void onScannedQrCode(String s) {
        list.get(positionOfClickedScanBtn).setRecipientAddress(s);
        liveData.setValue(CreateUIState.scannedQRCode(positionOfClickedScanBtn));
    }
}
