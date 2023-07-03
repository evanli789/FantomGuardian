package com.penguinstudios.fantomguardian.viewmodel;

import android.util.Pair;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.penguinstudios.fantomguardian.data.LocalRepository;
import com.penguinstudios.fantomguardian.data.RemoteRepository;
import com.penguinstudios.fantomguardian.data.WalletRepository;
import com.penguinstudios.fantomguardian.data.model.FormattedWithdraw;
import com.penguinstudios.fantomguardian.data.model.SuccessfulWithdrawal;
import com.penguinstudios.fantomguardian.data.model.Withdrawal;
import com.penguinstudios.fantomguardian.data.state.WithdrawUIState;
import com.penguinstudios.fantomguardian.data.validator.WithdrawValidator;
import com.penguinstudios.fantomguardian.util.WalletUtil;

import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.tuples.generated.Tuple3;

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
public class WithdrawViewModel extends ViewModel {

    private final MutableLiveData<WithdrawUIState> liveData = new MutableLiveData<>();
    private final RemoteRepository remoteRepository;
    private final LocalRepository localRepository;
    private final WalletRepository walletRepository;
    private final List<FormattedWithdraw> withdrawalsList = new ArrayList<>();
    private Disposable withdrawDisposable, getWithdrawalsDisposable;
    private String decryptionPhrase;

    @Inject
    public WithdrawViewModel(
            RemoteRepository remoteRepository,
            LocalRepository localRepository,
            WalletRepository walletRepository) {

        this.remoteRepository = remoteRepository;
        this.localRepository = localRepository;
        this.walletRepository = walletRepository;

        if (walletRepository.getCredentials() != null) {
            getWithdrawals();
        }
    }

    public void onClaimBtnClick(CharSequence text) {
        String contractAddress = text.toString();

        if (walletRepository.getCredentials() == null) {
            liveData.setValue(WithdrawUIState.noWalletAdded());
            return;
        }

        WithdrawValidator validator = new WithdrawValidator();

        try {
            validator.isValidContractAddress(contractAddress);
        } catch (IllegalArgumentException err) {
            Timber.e(err);
            liveData.setValue(WithdrawUIState.error(err.getMessage()));
            return;
        }

        withdraw(contractAddress);
    }

    //Retrieves list of withdrawals from Room using the user's wallet address
    public void getWithdrawals() {
        getWithdrawalsDisposable = localRepository.getWithdrawals(walletRepository.getAddress())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(list -> {
                    withdrawalsList.clear();
                    List<FormattedWithdraw> formattedWithdraws = createFormattedWithdrawalList(list);
                    withdrawalsList.addAll(formattedWithdraws);
                    liveData.setValue(WithdrawUIState.successGetListWithdrawals());
                }, err -> {
                    liveData.setValue(WithdrawUIState.error(err.getMessage()));
                    Timber.e(err);
                });
    }

    private static List<FormattedWithdraw> createFormattedWithdrawalList(List<Withdrawal> list) {
        List<FormattedWithdraw> newList = new ArrayList<>();
        for (Withdrawal withdrawal : list) {
            long dateWithdrawnSeconds = withdrawal.getDateWithdrawn();
            FormattedWithdraw formattedWithdraw = new FormattedWithdraw(
                    dateWithdrawnSeconds,
                    withdrawal.getContractAddress(),
                    BigInteger.valueOf(withdrawal.getAmount()));
            newList.add(formattedWithdraw);
        }
        return newList;
    }

    //Calls withdraw method from smart contract
    //Stores a receipt of withdrawal if successfully done
    public void withdraw(String contractAddress) {
        liveData.setValue(WithdrawUIState.progressWithdraw());

        withdrawDisposable = Single.fromCallable(() -> {
            BigInteger currentTime = remoteRepository.getTimestamp();
            Tuple3<BigInteger, BigInteger, String> withdrawStatus = remoteRepository.getWithdrawStatus(contractAddress);

            BigInteger expirationDate = withdrawStatus.component1();
            if (!(currentTime.compareTo(expirationDate) > 0)) {
                throw new IllegalStateException("The switch is not expired");
            }

            BigInteger userAmount = withdrawStatus.component2();

            if (!(userAmount.compareTo(BigInteger.ZERO) > 0)) {
                throw new IllegalStateException("User has empty balance");
            }

            String formattedAmount = WalletUtil.formatBalance(userAmount);
            TransactionReceipt receipt = remoteRepository.withdraw(contractAddress);
            String comment = withdrawStatus.component3();

            Withdrawal withdrawal = new Withdrawal.Builder()
                    .ownerAddress(walletRepository.getAddress())
                    .contractAddress(contractAddress)
                    .dateWithdrawn(currentTime)
                    .amount(userAmount)
                    .encryptedComments(comment)
                    .decryptionPhrase(decryptionPhrase)
                    .build();

            SuccessfulWithdrawal successfulWithdrawal = new SuccessfulWithdrawal(
                    receipt.getTransactionHash(), contractAddress, formattedAmount);

            return new Pair<>(withdrawal, successfulWithdrawal);
        }).flatMap(pair -> {
            return localRepository.insertWithdrawal(pair.first).map(rowId -> pair.second);
        })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(successfulWithdrawal -> {
                    decryptionPhrase = null;
                    liveData.setValue(WithdrawUIState.successWithdraw(successfulWithdrawal));
                }, err -> {
                    liveData.setValue(WithdrawUIState.error(err.getMessage()));
                    Timber.e(err);
                });
    }

    public void dispose() {
        if (withdrawDisposable != null) {
            withdrawDisposable.dispose();
        }

        if (getWithdrawalsDisposable != null) {
            getWithdrawalsDisposable.dispose();
        }
    }

    public MutableLiveData<WithdrawUIState> getLiveData() {
        return liveData;
    }

    public void setDecryptionPhrase(String decryptionPhrase) {
        this.decryptionPhrase = decryptionPhrase;
    }

    public List<FormattedWithdraw> getFormattedWithdrawalList() {
        return withdrawalsList;
    }

    public void clearWithdrawalsList() {
        withdrawalsList.clear();
    }
}
