package com.penguinstudios.fantomguardian.util;

import static org.web3j.crypto.Bip32ECKeyPair.HARDENED_BIT;

import org.web3j.crypto.Bip32ECKeyPair;
import org.web3j.crypto.MnemonicUtils;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.security.SecureRandom;

public class WalletUtil {
    //FTM uses same path as Ethereum
    public static Bip32ECKeyPair deriveKeyPairFromMnemonic(String mnemonic) {
        Bip32ECKeyPair masterKeyPair = Bip32ECKeyPair.generateKeyPair(
                MnemonicUtils.generateSeed(mnemonic, null));
        int[] path = {44 | HARDENED_BIT, 60 | HARDENED_BIT, 0 | HARDENED_BIT, 0, 0};
        return Bip32ECKeyPair.deriveKeyPair(masterKeyPair, path);
    }

    public static String formatBalance(BigInteger balance) {
        BigDecimal divisor = BigDecimal.TEN.pow(18);
        BigDecimal result = new BigDecimal(balance).divide(divisor, 5, BigDecimal.ROUND_HALF_UP);
        return result + " FTM";
    }

    public static String generate12WordMnemonic() {
        byte[] entropy = new byte[16];
        new SecureRandom().nextBytes(entropy);
        return MnemonicUtils.generateMnemonic(entropy);
    }
}
