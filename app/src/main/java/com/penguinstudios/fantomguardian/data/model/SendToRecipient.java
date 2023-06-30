package com.penguinstudios.fantomguardian.data.model;

import java.math.BigDecimal;
import java.math.BigInteger;

public class SendToRecipient {

    private CharSequence recipientAddress = "";
    private CharSequence amountFTM = "";
    private CharSequence comments = "";

    private SendToRecipient() {
    }

    public static SendToRecipient createEmptyRecipient() {
        return new SendToRecipient();
    }

    public String getRecipientAddress() {
        return recipientAddress.toString();
    }

    public void setRecipientAddress(CharSequence recipientAddress) {
        this.recipientAddress = recipientAddress;
    }

    public String getAmountFTM() {
        return amountFTM.toString();
    }

    public BigInteger getAmountFtmAsBigInt() {
        BigDecimal amountDecimal = new BigDecimal(amountFTM.toString());
        return amountDecimal.multiply(BigDecimal.valueOf(10).pow(18)).toBigInteger();
    }

    public void setAmountFTM(CharSequence amountFTM) {
        this.amountFTM = amountFTM;
    }

    public String getComments() {
        return comments.toString();
    }

    public void setComments(CharSequence comments) {
        this.comments = comments;
    }
}
