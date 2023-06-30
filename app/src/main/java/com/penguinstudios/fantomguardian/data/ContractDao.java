package com.penguinstudios.fantomguardian.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.penguinstudios.fantomguardian.data.model.Contract;

import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;

@Dao
public interface ContractDao {

    @Insert
    Single<Long> insertContract(Contract contract);

    @Query("SELECT * FROM contracts WHERE owner_address = :ownerAddress ORDER BY id DESC")
    Single<List<Contract>> getContracts(String ownerAddress);

    @Query("DELETE FROM contracts WHERE contract_address = :contractAddress")
    Single<Integer> deleteContract(String contractAddress);

    @Query("SELECT decryption_phrase FROM contracts WHERE contract_address = :contractAddress")
    Single<String> getDecryptionPhrase(String contractAddress);

    @Update
    Completable updateContracts(List<Contract> listContracts);
}
