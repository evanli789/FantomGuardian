package com.penguinstudios.fantomguardian.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.penguinstudios.fantomguardian.data.LocalRepository;
import com.penguinstudios.fantomguardian.data.WalletRepository;
import com.penguinstudios.fantomguardian.data.model.FormattedWithdrawInfo;
import com.penguinstudios.fantomguardian.data.model.Withdrawal;
import com.penguinstudios.fantomguardian.data.state.WithdrawInfoUIState;
import com.penguinstudios.fantomguardian.di.WithdrawInfoViewModelFactory;
import com.penguinstudios.fantomguardian.util.EncryptionUtil;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

public class WithdrawInfoViewModel extends ViewModel {

    private final MutableLiveData<WithdrawInfoUIState> liveData = new MutableLiveData<>();
    private final LocalRepository localRepository;
    private final WalletRepository walletRepository;
    private final String contractAddress;
    private String decryptionPhrase;
    private Withdrawal withdrawal;
    private Disposable getWithdrawalDisposable, updateDecryptionPhraseDisposable;

    @SuppressWarnings("unchecked")
    public static ViewModelProvider.Factory provideFactory(
            WithdrawInfoViewModelFactory assistedFactory, String contractAddress) {

        return new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) assistedFactory.create(contractAddress);
            }
        };
    }

    @AssistedInject
    public WithdrawInfoViewModel(
            @Assisted String contractAddress,
            LocalRepository localRepository,
            WalletRepository walletRepository) {

        this.contractAddress = contractAddress;
        this.localRepository = localRepository;
        this.walletRepository = walletRepository;

        getWithdrawalInfo(contractAddress);
    }

    //Gets the withdrawal info from room of a single contract
    private void getWithdrawalInfo(String contractAddress) {
        liveData.setValue(WithdrawInfoUIState.showProgress());

        getWithdrawalDisposable = localRepository.getWithdrawal(contractAddress, walletRepository.getAddress())
                .map(withdrawal -> {
                    this.withdrawal = withdrawal;
                    decryptionPhrase = withdrawal.getDecryptionPhrase();

                    if (decryptionPhrase == null) {
                        liveData.postValue(WithdrawInfoUIState.noDecryptionPhraseAvailable());
                    } else {
                        String decryptedComments = EncryptionUtil.decryptString(
                                withdrawal.getEncryptedComments(), decryptionPhrase);

                        liveData.postValue(WithdrawInfoUIState.decryptionPhraseAvailable(decryptedComments));
                    }

                    return new FormattedWithdrawInfo(withdrawal);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(formattedWithdrawInfo -> {
                    liveData.setValue(WithdrawInfoUIState.successGetWithdrawInfo(formattedWithdrawInfo));
                }, err -> {
                    Timber.e(err);
                    liveData.setValue(WithdrawInfoUIState.error(err.getMessage()));
                });
    }

    //Tries decrypting comments and stores the decryption phrase in room, if successful
    public void decryptComments() {
        try {
            String decryptedComments = EncryptionUtil.decryptString(
                    withdrawal.getEncryptedComments(), decryptionPhrase);
            liveData.setValue(WithdrawInfoUIState.successFullyDecryptedComments(decryptedComments));
        } catch (Exception e) {
            liveData.setValue(WithdrawInfoUIState.error("Failed to decrypt comments. Please try again."));
        }

        withdrawal.setDecryptionPhrase(decryptionPhrase);
        saveDecryptionPhrase();
    }

    private void saveDecryptionPhrase() {
        updateDecryptionPhraseDisposable = localRepository.updateDecryptionPhrase(withdrawal)
                .subscribeOn(Schedulers.io())
                .subscribe(() -> {
                    Timber.d("Successfully updated decryption phrase");
                }, err -> {
                    Timber.e(err);
                });
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public void dispose() {
        if (getWithdrawalDisposable != null) {
            getWithdrawalDisposable.dispose();
        }

        if (updateDecryptionPhraseDisposable != null) {
            updateDecryptionPhraseDisposable.dispose();
        }
    }

    public void setDecryptionPhrase(String decryptionPhrase) {
        this.decryptionPhrase = decryptionPhrase;
    }

    public MutableLiveData<WithdrawInfoUIState> getLiveData() {
        return liveData;
    }
}
