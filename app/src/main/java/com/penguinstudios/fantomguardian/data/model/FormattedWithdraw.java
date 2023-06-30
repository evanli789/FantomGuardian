package com.penguinstudios.fantomguardian.data.model;

import com.penguinstudios.fantomguardian.util.Constants;
import com.penguinstudios.fantomguardian.util.DateConverter;
import com.penguinstudios.fantomguardian.util.WalletUtil;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class FormattedWithdraw {

    private final String formattedDateWithdrawn;
    private final String contractAddress;
    private final String formattedAmount;

    public FormattedWithdraw(long dateWithdrawnSeconds, String contractAddress, BigInteger amountWei) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_PATTERN, Locale.US);

        this.formattedDateWithdrawn = dateFormat.format(
                DateConverter.secondsToMilliseconds(BigInteger.valueOf(dateWithdrawnSeconds)));
        this.contractAddress = contractAddress;
        this.formattedAmount = WalletUtil.formatBalance(amountWei);
    }

    public String getFormattedDateWithdrawn() {
        return formattedDateWithdrawn;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public String getFormattedAmount() {
        return formattedAmount;
    }
}
