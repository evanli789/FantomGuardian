package com.penguinstudios.fantomguardian.data.model;

import com.penguinstudios.fantomguardian.util.Constants;
import com.penguinstudios.fantomguardian.util.DateConverter;

import org.web3j.tuples.generated.Tuple3;

import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class FormattedContractInfo {

    private final String formattedDateExpiration;
    private final String formattedDateCreated;
    private final boolean isContractExpired;

    ////Tuple3 values: contractCreationDate, dateExpiration, contractBalance
    public FormattedContractInfo(BigInteger timestampSeconds, Tuple3<BigInteger, BigInteger, BigInteger> contractStatus) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(Constants.DATE_PATTERN, Locale.US);
        BigInteger contractCreationDateSeconds = contractStatus.component1();
        BigInteger expirationDateSeconds = contractStatus.component2();

        this.formattedDateExpiration = DateConverter.getDateExpirationAsString(
                timestampSeconds, expirationDateSeconds);

        this.formattedDateCreated = dateFormat.format(DateConverter.secondsToMilliseconds(contractCreationDateSeconds));
        this.isContractExpired = DateConverter.isContractExpired(timestampSeconds, expirationDateSeconds);
    }

    public String getFormattedDateExpiration() {
        return formattedDateExpiration;
    }

    public String getFormattedDateCreated() {
        return formattedDateCreated;
    }

    public boolean isContractExpired() {
        return isContractExpired;
    }
}
