package com.penguinstudios.fantomguardian.data.state;

import com.penguinstudios.fantomguardian.data.model.FormattedWithdrawInfo;

public class WithdrawInfoUIState {

    public enum State {
        SUCCESS_GET_WITHDRAWAL,
        NO_DECRYPTION_PHRASE_AVAILABLE,
        DECRYPTION_PHRASE_AVAILABLE,
        DECRYPTION_PHRASE_ADDED_FROM_ENTER_DECRYPTION_PHRASE,
        SUCCESSFULLY_DECRYPTED_COMMENTS,
        PROGRESS_GET_WITHDRAWAL,
        ERROR
    }

    private final State state;
    private FormattedWithdrawInfo formattedWithdrawInfo;
    private String errorMsg;
    private String decryptedComments;
    private String decryptionPhrase;

    private WithdrawInfoUIState(State state) {
        this.state = state;
    }

    private WithdrawInfoUIState(State state, FormattedWithdrawInfo formattedWithdrawInfo) {
        this.state = state;
        this.formattedWithdrawInfo = formattedWithdrawInfo;
    }

    private WithdrawInfoUIState(State state, String decryptedComments, String decryptionPhrase, String errorMsg) {
        this.state = state;
        this.decryptedComments = decryptedComments;
        this.decryptionPhrase = decryptionPhrase;
        this.errorMsg = errorMsg;
    }

    public static WithdrawInfoUIState showProgress() {
        return new WithdrawInfoUIState(State.PROGRESS_GET_WITHDRAWAL);
    }

    public static WithdrawInfoUIState successGetWithdrawInfo(FormattedWithdrawInfo formattedWithdrawInfo) {
        return new WithdrawInfoUIState(State.SUCCESS_GET_WITHDRAWAL, formattedWithdrawInfo);
    }

    public static WithdrawInfoUIState error(String errorMsg) {
        return new WithdrawInfoUIState(State.ERROR, null, null, errorMsg);
    }

    public static WithdrawInfoUIState noDecryptionPhraseAvailable() {
        return new WithdrawInfoUIState(State.NO_DECRYPTION_PHRASE_AVAILABLE);
    }

    public static WithdrawInfoUIState decryptionPhraseAvailable(String decryptedComments) {
        return new WithdrawInfoUIState(State.DECRYPTION_PHRASE_AVAILABLE, decryptedComments, null, null);
    }

    public static WithdrawInfoUIState decryptCommentsDecryptionPhraseAdded(String mnemonic) {
        return new WithdrawInfoUIState(State.DECRYPTION_PHRASE_ADDED_FROM_ENTER_DECRYPTION_PHRASE, null, mnemonic, null);
    }

    public static WithdrawInfoUIState successFullyDecryptedComments(String decryptedComments) {
        return new WithdrawInfoUIState(State.SUCCESSFULLY_DECRYPTED_COMMENTS, decryptedComments, null, null);
    }

    public State getState() {
        return state;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public String getDecryptedComments() {
        return decryptedComments;
    }

    public String getDecryptionPhrase() {
        return decryptionPhrase;
    }

    public FormattedWithdrawInfo getFormattedWithdrawInfo() {
        return formattedWithdrawInfo;
    }
}

