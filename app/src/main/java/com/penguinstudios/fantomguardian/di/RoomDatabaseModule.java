package com.penguinstudios.fantomguardian.di;

import android.content.Context;

import androidx.room.Room;

import com.penguinstudios.fantomguardian.data.AppDatabase;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.android.qualifiers.ApplicationContext;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class RoomDatabaseModule {

    @Provides
    @Singleton
    AppDatabase provideAppDatabase(@ApplicationContext Context context){
        return Room.databaseBuilder(context, AppDatabase.class, "dms-db")
                .fallbackToDestructiveMigration()
                .build();
    }
}
