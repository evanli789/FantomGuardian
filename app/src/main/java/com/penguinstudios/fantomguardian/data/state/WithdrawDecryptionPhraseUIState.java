package com.penguinstudios.fantomguardian.data.state;

public class WithdrawDecryptionPhraseUIState {

    public enum State {
        ERROR,
        VALID_DECRYPTION_PHRASE
    }

    private final State state;
    private final String decryptionPhrase;
    private final String errorMsg;

    private WithdrawDecryptionPhraseUIState(State state, String decryptionPhrase, String errorMsg) {
        this.state = state;
        this.decryptionPhrase = decryptionPhrase;
        this.errorMsg = errorMsg;
    }

    public static WithdrawDecryptionPhraseUIState mnemonicValid(String mnemonic) {
        return new WithdrawDecryptionPhraseUIState(State.VALID_DECRYPTION_PHRASE, mnemonic, null);
    }

    public static WithdrawDecryptionPhraseUIState error(String errorMsg) {
        return new WithdrawDecryptionPhraseUIState(State.ERROR, null, errorMsg);
    }

    public State getState() {
        return state;
    }

    public String getDecryptionPhrase() {
        return decryptionPhrase;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
