package com.penguinstudios.fantomguardian.util;

import java.math.BigInteger;

public class DateConverter {

    public static String getDateExpirationAsString(BigInteger timestampSeconds, BigInteger dateExpirationSeconds) {
        long timeLeft = dateExpirationSeconds.longValue() - timestampSeconds.longValue();
        if (timeLeft < 0) {
            return "Contract Expired";
        } else if (timeLeft < Constants.SECONDS_PER_HOUR) {
            long minutesLeft = timeLeft / Constants.SECONDS_PER_MINUTE;
            return minutesLeft + " mins left";
        } else if (timeLeft < Constants.SECONDS_PER_DAY) {
            long hoursLeft = timeLeft / Constants.SECONDS_PER_HOUR;
            long minutesLeft = (timeLeft % Constants.SECONDS_PER_HOUR) / Constants.SECONDS_PER_MINUTE;
            return hoursLeft + " hours and " + minutesLeft + " mins left";
        } else {
            long daysLeft = timeLeft / Constants.SECONDS_PER_DAY;
            long hoursLeft = (timeLeft % Constants.SECONDS_PER_DAY) / Constants.SECONDS_PER_HOUR;
            return daysLeft + " days and " + hoursLeft + " hours left";
        }
    }

    public static boolean isContractExpired(BigInteger timestampSeconds, BigInteger dateExpirationSeconds) {
        long timeLeft = dateExpirationSeconds.longValue() - timestampSeconds.longValue();
        return timeLeft <= 0;
    }

    public static long secondsToMilliseconds(BigInteger timeSeconds) {
        return timeSeconds.longValue() * Constants.MILLISECONDS_PER_SECOND;
    }
}
