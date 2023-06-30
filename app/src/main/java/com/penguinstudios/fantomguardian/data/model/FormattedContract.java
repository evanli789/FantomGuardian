package com.penguinstudios.fantomguardian.data.model;

import com.penguinstudios.fantomguardian.util.Constants;
import com.penguinstudios.fantomguardian.util.DateConverter;
import com.penguinstudios.fantomguardian.util.WalletUtil;

import java.math.BigInteger;

public class FormattedContract {

    private static final double RED_PROGRESS_BAR_THRESHOLD = 0.33;
    private static final int AMOUNT_TO_SCALE_PROGRESS_BAR = 1000;
    private final String formattedAmount;
    private final String dateToReset;
    private final String numRecipients;
    private final String contractAddress;
    private final boolean isContractExpired;
    private final int progress;
    private final boolean isProgressLessThan25Percent;

    public FormattedContract(
            String contractAddress, int numRecipients, BigInteger amount, BigInteger currentTimeSeconds,
            BigInteger dateOfExpirationSeconds, ResetDuration resetDuration) {

        this.contractAddress = contractAddress;
        this.numRecipients = String.valueOf(numRecipients);
        this.formattedAmount = WalletUtil.formatBalance(amount);

        this.dateToReset = DateConverter.getDateExpirationAsString(currentTimeSeconds, dateOfExpirationSeconds);
        this.isContractExpired = DateConverter.isContractExpired(currentTimeSeconds, dateOfExpirationSeconds);

        long timeLeftSeconds = dateOfExpirationSeconds.longValue() - currentTimeSeconds.longValue();

        double percentLeft;

        if (resetDuration == ResetDuration.ONE_HOUR) {
            percentLeft = (double) timeLeftSeconds / Constants.SECONDS_PER_HOUR;
        } else {
            percentLeft = (double) timeLeftSeconds / (resetDuration.getNumDays() * Constants.SECONDS_PER_DAY);
        }

        double percentScaledUp = percentLeft * AMOUNT_TO_SCALE_PROGRESS_BAR;
        this.progress = (int) percentScaledUp;

        this.isProgressLessThan25Percent = percentLeft < RED_PROGRESS_BAR_THRESHOLD;
    }

    public String getFormattedAmount() {
        return formattedAmount;
    }

    public String getDateToReset() {
        return dateToReset;
    }

    public String getNumRecipients() {
        return numRecipients;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public int getProgress() {
        return progress;
    }

    public boolean isContractExpired() {
        return isContractExpired;
    }

    public boolean isProgressLessThan33Percent() {
        return isProgressLessThan25Percent;
    }
}
