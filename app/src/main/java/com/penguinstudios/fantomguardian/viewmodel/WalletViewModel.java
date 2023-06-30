package com.penguinstudios.fantomguardian.viewmodel;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.penguinstudios.fantomguardian.data.LocalRepository;
import com.penguinstudios.fantomguardian.data.RemoteRepository;
import com.penguinstudios.fantomguardian.data.SharedPrefManager;
import com.penguinstudios.fantomguardian.data.WalletRepository;
import com.penguinstudios.fantomguardian.data.model.Contract;
import com.penguinstudios.fantomguardian.data.model.FormattedContract;
import com.penguinstudios.fantomguardian.data.model.ResetDuration;
import com.penguinstudios.fantomguardian.data.state.WalletUIState;
import com.penguinstudios.fantomguardian.util.WalletUtil;
import com.penguinstudios.fantomguardian.data.validator.WalletValidator;

import org.web3j.crypto.Bip32ECKeyPair;
import org.web3j.crypto.Credentials;
import org.web3j.tuples.generated.Tuple3;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import dagger.hilt.android.lifecycle.HiltViewModel;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

@HiltViewModel
public class WalletViewModel extends ViewModel {

    private final SharedPrefManager sharedPrefManager;
    private final RemoteRepository remoteRepository;
    private final WalletRepository walletRepository;
    private final LocalRepository localRepository;
    private final MutableLiveData<WalletUIState> liveData = new MutableLiveData<>();
    private final List<FormattedContract> formattedContractsList = new ArrayList<>();
    private final List<Contract> contractsList = new ArrayList<>();
    private Disposable addWalletDisposable, resetSwitchDisposable;

    @Inject
    public WalletViewModel(
            SharedPrefManager sharedPrefManager,
            RemoteRepository remoteRepository,
            WalletRepository walletRepository,
            LocalRepository localRepository) {

        this.sharedPrefManager = sharedPrefManager;
        this.remoteRepository = remoteRepository;
        this.walletRepository = walletRepository;
        this.localRepository = localRepository;

        //Retrieves wallet credentials from encrypted shared preferences
        if (sharedPrefManager.getIsMnemonicAdded()) {
            createCredentialsMnemonic(sharedPrefManager.getMnemonic());
        } else if (sharedPrefManager.getIsPrivateKeyAdded()) {
            createCredentialsPrivateKey(sharedPrefManager.getPrivateKey());
        } else {
            liveData.setValue(WalletUIState.defaultState());
        }
    }

    //Creates credentials from mnemonic phrase
    private void createCredentialsMnemonic(String mnemonic) {
        liveData.setValue(WalletUIState.progressAddWallet());

        Single<Credentials> credentialsSingle = Single.fromCallable(() -> {
            Bip32ECKeyPair derivedKeyPair = WalletUtil.deriveKeyPairFromMnemonic(mnemonic);
            return Credentials.create(derivedKeyPair);
        })
                .doOnSuccess(keyWallet -> {
                    sharedPrefManager.setMnemonic(mnemonic);
                    sharedPrefManager.setIsMnemonicAdded(true);
                });

        loadWallet(credentialsSingle);
    }

    //Creates credentials from private key
    private void createCredentialsPrivateKey(String privateKey) {
        liveData.setValue(WalletUIState.progressAddWallet());

        Single<Credentials> credentialsSingle = Single.fromCallable(() -> Credentials.create(privateKey))
                .doOnSuccess(keyWallet -> {
                    sharedPrefManager.setPrivateKey(privateKey);
                    sharedPrefManager.setIsPrivateKeyAdded(true);
                });

        loadWallet(credentialsSingle);
    }

    /**
     *  Uses user credentials to get wallet balance, the list of contracts from Room, and syncs each of them
     *  for the updated expiration date
     */
    private void loadWallet(Single<Credentials> credentialsSingle) {
        addWalletDisposable = credentialsSingle
                .flatMap(credentials -> {
                    walletRepository.setCredentials(credentials);

                    //Clear withdrawals list because new wallet is set
                    liveData.postValue(WalletUIState.successAddWallet());

                    BigInteger balance = remoteRepository.getWalletBalance(credentials.getAddress()).getBalance();
                    String formattedWalletBalance = WalletUtil.formatBalance(balance);

                    liveData.postValue(WalletUIState.successGetWalletBalance(
                            formattedWalletBalance, credentials.getAddress()));

                    return localRepository.getContracts(credentials.getAddress());
                })
                .flatMap(contracts -> {
                    contractsList.clear();
                    contractsList.addAll(contracts);

                    //Retrieves all contracts concurrently and retains order
                    return Observable.fromIterable(contracts)
                            .concatMapEager(contract -> Observable.fromCallable(() -> {
                                        return remoteRepository.getContractStatus(contract.getContractAddress());
                                    })
                                            .subscribeOn(Schedulers.io())
                            )
                            .toList();
                })
                .flatMapCompletable(contractStatusList -> {
                    BigInteger timestampSeconds = remoteRepository.getTimestamp();

                    List<FormattedContract> newFormattedContractList =
                            createFormattedContractList(timestampSeconds, contractsList, contractStatusList);

                    formattedContractsList.clear();
                    formattedContractsList.addAll(newFormattedContractList);

                    return localRepository.updateContracts(contractsList, contractStatusList);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(() -> {
                    liveData.setValue(WalletUIState.successGetUserContracts());
                    liveData.setValue(WalletUIState.hideSwipeRefresh());
                }, err -> {
                    Timber.e(err);
                    liveData.setValue(WalletUIState.error(err.getMessage()));
                    liveData.setValue(WalletUIState.hideSwipeRefresh());
                });
    }

    //Creates a list on contracts with formatted data to display in recycler
    private static List<FormattedContract> createFormattedContractList(
            BigInteger timeStampSeconds,
            List<Contract> contracts,
            List<Tuple3<BigInteger, BigInteger, BigInteger>> statusList) {

        List<FormattedContract> formattedContractsList = new ArrayList<>();

        for (int i = 0; i < contracts.size(); i++) {
            Tuple3<BigInteger, BigInteger, BigInteger> status = statusList.get(i);
            Contract contract = contracts.get(i);

            BigInteger expirationDateSeconds = status.component2();
            BigInteger contractBalanceWei = status.component3();

            ResetDuration resetDuration = ResetDuration.of(contract.getNumDaysToReset()).orElseThrow(() ->
                    new IllegalStateException("Stored value in database incorrectly mapped."));

            FormattedContract formattedContract = new FormattedContract(
                    contract.getContractAddress(),
                    contract.getNumRecipients(),
                    contractBalanceWei,
                    timeStampSeconds,
                    expirationDateSeconds,
                    resetDuration);

            formattedContractsList.add(formattedContract);
        }

        return formattedContractsList;
    }

    //Calls contract to reset the expiration date of the switch
    public void resetSwitch(int adapterPosition) {
        liveData.setValue(WalletUIState.progressResetSwitch());
        Contract contract = contractsList.get(adapterPosition);

        resetSwitchDisposable = Single.fromCallable(() -> {
            return remoteRepository.resetSwitch(contract.getContractAddress());
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(txReceipt -> {
                    liveData.setValue(WalletUIState.successResetSwitch());
                }, err -> {
                    liveData.setValue(WalletUIState.failedResetSwitch(err.getMessage()));
                    Timber.e(err);
                });
    }

    public void onSwipeRefresh() {
        if (walletRepository.getCredentials() == null) {
            liveData.setValue(WalletUIState.hideSwipeRefresh());
            return;
        }

        loadWallet(Single.just(walletRepository.getCredentials()));
    }

    //Called when user creates new contract
    public void updateContractsList(){
        loadWallet(Single.just(walletRepository.getCredentials()));
    }

    public void clearWallet() {
        formattedContractsList.clear();
        walletRepository.setCredentials(null);
        sharedPrefManager.resetWalletSharedPreferences();
        liveData.setValue(WalletUIState.clearWallet());
    }

    public void onMnemonicAdded() {
        loadWallet(Single.just(walletRepository.getCredentials()));
    }

    public MutableLiveData<WalletUIState> getLiveData() {
        return liveData;
    }

    public void dispose() {
        if (addWalletDisposable != null) {
            addWalletDisposable.dispose();
        }

        if (resetSwitchDisposable != null) {
            resetSwitchDisposable.dispose();
        }
    }

    public List<FormattedContract> getFormattedContractsList() {
        return formattedContractsList;
    }

    public List<Contract> getContractsList() {
        return contractsList;
    }

    public String getWalletAddress() {
        return walletRepository.getAddress();
    }

    public void onLoadWalletBtnClick(CharSequence text) {
        String privateKey = text.toString();
        WalletValidator walletValidator = new WalletValidator();

        try {
            walletValidator.isPrivateKeyFieldFilled(privateKey);
            walletValidator.isPrivateKeyValid(privateKey);
        } catch (IllegalArgumentException e) {
            liveData.setValue(WalletUIState.error(e.getMessage()));
            return;
        }

        createCredentialsPrivateKey(privateKey);
    }
}
