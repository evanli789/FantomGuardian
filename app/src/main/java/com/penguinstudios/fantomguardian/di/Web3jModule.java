package com.penguinstudios.fantomguardian.di;

import com.penguinstudios.fantomguardian.data.model.Network;

import org.web3j.protocol.Web3j;
import org.web3j.protocol.http.HttpService;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import dagger.hilt.InstallIn;
import dagger.hilt.components.SingletonComponent;

@Module
@InstallIn(SingletonComponent.class)
public class Web3jModule {

    @Singleton
    @Provides
    Web3j provideWeb3j() {
        return Web3j.build(new HttpService(Network.MAIN_NET.getBaseUrl()));
    }
}
