package com.penguinstudios.fantomguardian.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.penguinstudios.fantomguardian.data.LocalRepository;
import com.penguinstudios.fantomguardian.data.RemoteRepository;
import com.penguinstudios.fantomguardian.data.model.ContractInfoRecipient;
import com.penguinstudios.fantomguardian.data.model.FormattedContractInfo;
import com.penguinstudios.fantomguardian.data.state.ContractInfoUIState;
import com.penguinstudios.fantomguardian.di.ContractInfoViewModelFactory;
import com.penguinstudios.fantomguardian.util.EncryptionUtil;

import org.web3j.tuples.generated.Tuple3;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

public class ContractInfoViewModel extends ViewModel {

    private final MutableLiveData<ContractInfoUIState> liveData = new MutableLiveData<>();
    private final RemoteRepository remoteRepository;
    private final LocalRepository localRepository;
    private final List<ContractInfoRecipient> recipientList = new ArrayList<>();
    private final String contractAddress;
    private Disposable getContractInfoDisposable, deleteSwitchDisposable;

    @SuppressWarnings("unchecked")
    public static ViewModelProvider.Factory provideFactory(
            ContractInfoViewModelFactory assistedFactory, String contractAddress) {

        return new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) assistedFactory.create(contractAddress);
            }
        };
    }

    @AssistedInject
    public ContractInfoViewModel(
            @Assisted String contractAddress,
            RemoteRepository remoteRepository,
            LocalRepository localRepository) {

        this.contractAddress = contractAddress;
        this.remoteRepository = remoteRepository;
        this.localRepository = localRepository;

        getContractInfo(contractAddress);
    }

    /**
     * Retrieves the information for a single switch
     * Gets the decryption phrase from Room
     * Retrieves the timestamp, contract status, and the distribution details
     * Creates a formatted list of distribution details as recipients to display in recycler
     */
    public void getContractInfo(String contractAddress) {
        getContractInfoDisposable = localRepository.getDecryptionPhrase(contractAddress)
                .map(decryptionPhrase -> {
                    BigInteger timestamp = remoteRepository.getTimestamp();

                    Tuple3<BigInteger, BigInteger, BigInteger> contractStatus =
                            remoteRepository.getContractStatus(contractAddress);

                    Tuple3<List<String>, List<BigInteger>, List<String>> distributionDetails =
                            remoteRepository.getDistributionDetails(contractAddress);

                    List<ContractInfoRecipient> newList = createListRecipients(distributionDetails, decryptionPhrase);

                    recipientList.clear();
                    recipientList.addAll(newList);

                    return new FormattedContractInfo(timestamp, contractStatus);
                }).subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(formattedContractInfo -> {
                    liveData.setValue(ContractInfoUIState.successGetContractInfo(formattedContractInfo));
                }, err -> {
                    liveData.setValue(ContractInfoUIState.failedGetContractInfo());
                    Timber.e(err);
                });
    }

    /**
     * Creates a formatted list of recipients to display in recycler
     * Uses decryption phrase to decrypt comments
     */
    private static List<ContractInfoRecipient> createListRecipients(
            Tuple3<List<String>, List<BigInteger>, List<String>> distributionDetails,
            String decryptionPhrase) throws Exception {

        List<String> recipientWalletAddress = distributionDetails.component1();
        List<BigInteger> amountEachRecipientReceives = distributionDetails.component2();
        List<String> commentsForEachRecipient = distributionDetails.component3();

        List<ContractInfoRecipient> list = new ArrayList<>();

        for (int i = 0; i < recipientWalletAddress.size(); i++) {
            String comment = commentsForEachRecipient.get(i);
            String decryptedComment = EncryptionUtil.decryptString(comment, decryptionPhrase);
            ContractInfoRecipient recipient = new ContractInfoRecipient(
                    recipientWalletAddress.get(i),
                    decryptedComment,
                    amountEachRecipientReceives.get(i));
            list.add(recipient);
        }
        return list;
    }

    /**
     * Deletes the contract on chain and removes it from Room
     */
    public void deleteSwitch() {
        liveData.setValue(ContractInfoUIState.progressDeleteSwitch());

        deleteSwitchDisposable = Single.fromCallable(() -> remoteRepository.deleteSwitch(contractAddress))
                .flatMap(txReceipt -> localRepository.deleteContract(contractAddress)
                        .map(status -> txReceipt.getTransactionHash()))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(txHash -> {
                    liveData.setValue(ContractInfoUIState.successDeleteSwitch(
                            txHash, contractAddress));
                }, err -> {
                    liveData.setValue(ContractInfoUIState.failedDeleteSwitch());
                    Timber.e(err);
                });
    }

    public void dispose() {
        if (getContractInfoDisposable != null) {
            getContractInfoDisposable.dispose();
        }

        if (deleteSwitchDisposable != null) {
            deleteSwitchDisposable.dispose();
        }
    }

    public List<ContractInfoRecipient> getRecipientList() {
        return recipientList;
    }

    public MutableLiveData<ContractInfoUIState> getLiveData() {
        return liveData;
    }

    public String getContractAddress() {
        return contractAddress;
    }
}
