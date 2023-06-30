package com.penguinstudios.fantomguardian.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.penguinstudios.fantomguardian.data.model.Withdrawal;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface WithdrawalDao {

    @Insert
    Single<Long> insertWithdrawal(Withdrawal withdrawal);

    @Query("SELECT * FROM withdrawals WHERE owner_address = :ownerAddress ORDER BY id DESC")
    Single<List<Withdrawal>> getWithdrawals(String ownerAddress);

    @Query("SELECT * FROM withdrawals WHERE contract_address = :contractAddress AND owner_address = :ownerAddress")
    Single<Withdrawal> getWithdrawal(String contractAddress, String ownerAddress);

    @Update
    Completable updateDecryptionPhrase(Withdrawal withdrawal);
}
