package com.penguinstudios.fantomguardian.data;

import android.content.SharedPreferences;

import javax.inject.Inject;

public class SharedPrefManager {

    private static final String KEY_MNEMONIC = "KEY_MNEMONIC";
    private static final String KEY_PRIVATE_KEY = "KEY_PRIVATE_KEY";
    private static final String KEY_IS_PRIVATE_KEY_WALLET_ADDED = "KEY_IS_PRIVATE_KEY_WALLET_ADDED";
    private static final String KEY_IS_MNEMONIC_ADDED = "KEY_IS_MNEMONIC_ADDED";

    private static final String DEFAULT_MNEMONIC = null;
    private static final String DEFAULT_PRIVATE_KEY = null;
    private static final boolean DEFAULT_IS_PRIVATE_KEY_WALLET_ADDED = false;
    private static final boolean DEFAULT_IS_MNEMONIC_ADDED = false;

    private final SharedPreferences sharedPreferences;

    @Inject
    public SharedPrefManager(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }

    public String getPrivateKey() {
        return sharedPreferences.getString(KEY_PRIVATE_KEY, DEFAULT_PRIVATE_KEY);
    }

    public void setPrivateKey(String privateKey) {
        sharedPreferences.edit().putString(KEY_PRIVATE_KEY, privateKey).apply();
    }

    public String getMnemonic() {
        return sharedPreferences.getString(KEY_MNEMONIC, DEFAULT_MNEMONIC);
    }

    public void setMnemonic(String mnemonic) {
        sharedPreferences.edit().putString(KEY_MNEMONIC, mnemonic).apply();
    }

    public boolean getIsPrivateKeyAdded() {
        return sharedPreferences.getBoolean(KEY_IS_PRIVATE_KEY_WALLET_ADDED, DEFAULT_IS_PRIVATE_KEY_WALLET_ADDED);
    }

    public void setIsPrivateKeyAdded(boolean isPrivateKeyWalletAdded) {
        sharedPreferences.edit().putBoolean(KEY_IS_PRIVATE_KEY_WALLET_ADDED, isPrivateKeyWalletAdded).apply();
    }

    public boolean getIsMnemonicAdded() {
        return sharedPreferences.getBoolean(KEY_IS_MNEMONIC_ADDED, DEFAULT_IS_MNEMONIC_ADDED);
    }

    public void setIsMnemonicAdded(boolean isMnemonicAdded) {
        sharedPreferences.edit().putBoolean(KEY_IS_MNEMONIC_ADDED, isMnemonicAdded).apply();
    }

    public void resetWalletSharedPreferences() {
        sharedPreferences.edit()
                .putString(KEY_MNEMONIC, DEFAULT_MNEMONIC)
                .putString(KEY_PRIVATE_KEY, DEFAULT_PRIVATE_KEY)
                .putBoolean(KEY_IS_PRIVATE_KEY_WALLET_ADDED, DEFAULT_IS_PRIVATE_KEY_WALLET_ADDED)
                .putBoolean(KEY_IS_MNEMONIC_ADDED, DEFAULT_IS_MNEMONIC_ADDED)
                .apply();
    }
}
