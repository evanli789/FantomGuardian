package com.penguinstudios.fantomguardian.data.state;

public class WalletMnemonicUIState {

    public enum State {
        ADD_CREDENTIALS_SUCCESS,
        ERROR
    }

    private final State state;
    private String errorMsg;

    private WalletMnemonicUIState(State state) {
        this.state = state;
    }

    private WalletMnemonicUIState(State state, String errorMsg) {
        this.state = state;
        this.errorMsg = errorMsg;
    }

    public static WalletMnemonicUIState addCredentialsSuccess() {
        return new WalletMnemonicUIState(State.ADD_CREDENTIALS_SUCCESS);
    }

    public static WalletMnemonicUIState error(String errorMsg) {
        return new WalletMnemonicUIState(State.ERROR, errorMsg);
    }

    public State getState() {
        return state;
    }

    public String getErrorMsg() {
        return errorMsg;
    }
}
