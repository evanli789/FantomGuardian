package com.penguinstudios.fantomguardian.data.model;

import com.penguinstudios.fantomguardian.util.Constants;
import com.penguinstudios.fantomguardian.util.DateConverter;
import com.penguinstudios.fantomguardian.util.WalletUtil;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class FormattedWithdrawInfo {

    private final String formattedDateWithdrawn;
    private final String formattedAmount;

    public FormattedWithdrawInfo(Withdrawal withdrawal) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_PATTERN, Locale.US);
        long dateWithdrawn = DateConverter.secondsToMilliseconds(BigInteger.valueOf(withdrawal.getDateWithdrawn()));

        this.formattedDateWithdrawn = dateFormat.format(dateWithdrawn);
        this.formattedAmount = WalletUtil.formatBalance(BigInteger.valueOf(withdrawal.getAmount()));
    }

    public String getFormattedDateWithdrawn() {
        return formattedDateWithdrawn;
    }

    public String getFormattedAmount() {
        return formattedAmount;
    }

}
