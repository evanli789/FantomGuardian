package com.penguinstudios.fantomguardian.data.validator;

import org.web3j.crypto.MnemonicUtils;
import org.web3j.crypto.WalletUtils;

import java.util.Map;

public class WithdrawValidator {

    public void isValidContractAddress(String address) {
        if (address.equals("")) {
            throw new IllegalArgumentException("Please enter the contract address.");
        }

        if (!WalletUtils.isValidAddress(address)) {
            throw new IllegalArgumentException("Invalid contract address, please try again.");
        }
    }

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
}
