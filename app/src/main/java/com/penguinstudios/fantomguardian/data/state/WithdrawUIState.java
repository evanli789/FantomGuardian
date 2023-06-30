package com.penguinstudios.fantomguardian.data.state;

import com.penguinstudios.fantomguardian.data.model.SuccessfulWithdrawal;

public class WithdrawUIState {

    public enum State {
        SUCCESS_WITHDRAW,
        PROGRESS_WITHDRAW,
        WALLET_CLEARED,
        NEW_WALLET_ADDED,
        SUCCESS_GET_WITHDRAWALS,
        ENTER_DECRYPTION_PHRASE_MNEMONIC_ADDED,
        NO_WALLET_ADDED,
        ERROR
    }

    private final State state;
    private SuccessfulWithdrawal successfulWithdrawal;
    private String errorMsg;
    private String mnemonic;

    private WithdrawUIState(State state) {
        this.state = state;
    }

    private WithdrawUIState(State state, String mnemonic, String errorMsg) {
        this.state = state;
        this.mnemonic = mnemonic;
        this.errorMsg = errorMsg;
    }

    private WithdrawUIState(State state, SuccessfulWithdrawal successfulWithdrawal){
        this.state = state;
        this.successfulWithdrawal = successfulWithdrawal;
    }

    public static WithdrawUIState progressWithdraw() {
        return new WithdrawUIState(State.PROGRESS_WITHDRAW);
    }

    public static WithdrawUIState successWithdraw(SuccessfulWithdrawal successfulWithdrawal) {
        return new WithdrawUIState(State.SUCCESS_WITHDRAW, successfulWithdrawal);
    }

    public static WithdrawUIState successGetListWithdrawals(){
        return new WithdrawUIState(State.SUCCESS_GET_WITHDRAWALS);
    }

    public static WithdrawUIState newWalletAdded(){
        return new WithdrawUIState(State.NEW_WALLET_ADDED);
    }

    public static WithdrawUIState walletCleared(){
        return new WithdrawUIState(State.WALLET_CLEARED);
    }

    public static WithdrawUIState decryptionPhraseAddedInWithdrawFunds(String mnemonic){
        return new WithdrawUIState(State.ENTER_DECRYPTION_PHRASE_MNEMONIC_ADDED, mnemonic, null);
    }

    public static WithdrawUIState error(String errorMsg) {
        return new WithdrawUIState(State.ERROR, null, errorMsg);
    }

    public static WithdrawUIState noWalletAdded() {
        return new WithdrawUIState(State.NO_WALLET_ADDED);
    }

    public State getState() {
        return state;
    }

    public SuccessfulWithdrawal getSuccessfulWithdrawal() {
        return successfulWithdrawal;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public String getMnemonic() {
        return mnemonic;
    }
}
