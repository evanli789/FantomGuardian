package com.penguinstudios.fantomguardian.data.model;

public class SuccessfulWithdrawal {

    private final String txHash;
    private final String contractAddress;
    private final String formattedAmount;

    public SuccessfulWithdrawal(String txHash, String contractAddress, String formattedAmount) {
        this.txHash = txHash;
        this.contractAddress = contractAddress;
        this.formattedAmount = formattedAmount;
    }

    public String getTxHash() {
        return txHash;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public String getFormattedAmount() {
        return formattedAmount;
    }
}
