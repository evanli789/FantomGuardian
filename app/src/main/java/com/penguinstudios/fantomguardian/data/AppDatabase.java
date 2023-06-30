package com.penguinstudios.fantomguardian.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.penguinstudios.fantomguardian.data.model.Contract;
import com.penguinstudios.fantomguardian.data.model.Withdrawal;

@Database(entities = {Contract.class, Withdrawal.class}, version = 1, exportSchema = false)
public abstract class AppDatabase extends RoomDatabase {

    public abstract ContractDao contractDao();

    public abstract WithdrawalDao withdrawalDao();
}
