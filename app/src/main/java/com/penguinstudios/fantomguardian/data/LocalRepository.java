package com.penguinstudios.fantomguardian.data;

import com.penguinstudios.fantomguardian.data.model.Contract;
import com.penguinstudios.fantomguardian.data.model.Withdrawal;

import org.web3j.tuples.generated.Tuple3;

import java.math.BigInteger;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

public class LocalRepository {

    private final AppDatabase appDatabase;

    @Inject
    public LocalRepository(AppDatabase appDatabase) {
        this.appDatabase = appDatabase;
    }

    public Single<Long> insertContract(Contract contract) {
        return appDatabase.contractDao().insertContract(contract);
    }

    public Single<Long> insertWithdrawal(Withdrawal withdrawal){
        return appDatabase.withdrawalDao().insertWithdrawal(withdrawal);
    }

    public Single<List<Contract>> getContracts(String ownerAddress) {
        return appDatabase.contractDao().getContracts(ownerAddress);
    }

    public Completable updateContracts(
            List<Contract> listContracts,
            List<Tuple3<BigInteger, BigInteger, BigInteger>> statusList) {

        for (int i = 0; i < listContracts.size(); i++) {
            Tuple3<BigInteger, BigInteger, BigInteger> status = statusList.get(i);
            Contract contract = listContracts.get(i);
            long dateExpiration = status.component3().longValue();
            contract.setDateOfExpiration(dateExpiration);
        }

        return appDatabase.contractDao().updateContracts(listContracts);
    }

    public Single<Integer> deleteContract(String contractAddress) {
        return appDatabase.contractDao().deleteContract(contractAddress);
    }

    public Single<String> getDecryptionPhrase(String contractAddress){
        return appDatabase.contractDao().getDecryptionPhrase(contractAddress);
    }

    public Single<List<Withdrawal>> getWithdrawals(String ownerAddress){
        return appDatabase.withdrawalDao().getWithdrawals(ownerAddress);
    }

    public Single<Withdrawal> getWithdrawal(String contractAddress, String ownerAddress){
        return appDatabase.withdrawalDao().getWithdrawal(contractAddress, ownerAddress);
    }

    public Completable updateDecryptionPhrase(Withdrawal withdrawal){
        return appDatabase.withdrawalDao().updateDecryptionPhrase(withdrawal);
    }
}
