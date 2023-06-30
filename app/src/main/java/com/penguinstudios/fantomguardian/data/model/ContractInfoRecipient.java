package com.penguinstudios.fantomguardian.data.model;

import com.penguinstudios.fantomguardian.util.WalletUtil;

import java.math.BigInteger;

public class ContractInfoRecipient {

    private final String walletAddress;
    private final String comments;
    private final String formattedAmount;

    public ContractInfoRecipient(String walletAddress, String comments, BigInteger amount) {
        this.walletAddress = walletAddress;
        this.comments = comments;
        this.formattedAmount = WalletUtil.formatBalance(amount);
    }

    public String getWalletAddress() {
        return walletAddress;
    }

    public String getComments() {
        return comments;
    }

    public String getFormattedAmount() {
        return formattedAmount;
    }
}
