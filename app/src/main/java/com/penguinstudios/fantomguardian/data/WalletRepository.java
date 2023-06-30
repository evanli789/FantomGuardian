package com.penguinstudios.fantomguardian.data;

import org.web3j.crypto.Credentials;

import javax.inject.Inject;
import javax.inject.Singleton;



@Singleton
public class WalletRepository {

    private Credentials credentials;

    @Inject
    public WalletRepository() {
    }

    public Credentials getCredentials() {
        return credentials;
    }

    public String getAddress(){
        return credentials.getAddress();
    }

    public void setCredentials(Credentials credentials) {
        this.credentials = credentials;
    }
}
