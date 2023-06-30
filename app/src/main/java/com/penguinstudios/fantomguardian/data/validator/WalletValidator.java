package com.penguinstudios.fantomguardian.data.validator;

import org.web3j.crypto.MnemonicUtils;
import org.web3j.crypto.WalletUtils;

import java.util.Map;

public class WalletValidator {

    public void isAllMnemonicFieldsFilled(int numWordsMnemonic, Map<Integer, CharSequence> map) {
        if (map.values().size() < numWordsMnemonic) {
            throw new IllegalArgumentException("Please fill out all fields.");
        }
    }

    public void isValidMnemonic(String mnemonic) {
        if (!MnemonicUtils.validateMnemonic(mnemonic)) {
            throw new IllegalArgumentException("Invalid mnemonic. Please try again.");
        }
    }

    public void isPrivateKeyFieldFilled(String privateKey) {
        if (privateKey.equals("")) {
            throw new IllegalArgumentException("Please add your private key to load the wallet");
        }
    }

    public void isPrivateKeyValid(String privateKey) {
        if (!WalletUtils.isValidPrivateKey(privateKey)) {
            throw new IllegalArgumentException("The private key you entered is invalid. Please try again.");
        }
    }
}
