package com.penguinstudios.fantomguardian.data.state;

/**
 * Different ui states for WalletFragment
 */
public class WalletUIState {

    public enum State {
        DEFAULT,
        CLEAR_WALLET,
        ADD_CREDENTIALS_MNEMONIC_SUCCESS,
        PROGRESS_RESET_SWITCH,
        SUCCESS_ADD_WALLET,
        PROGRESS_ADD_WALLET,
        SUCCESS_GET_WALLET_BALANCE,
        SUCCESS_GET_USER_CONTRACTS,
        SUCCESS_RESET_SWITCH,
        FAILED_RESET_SWITCH,
        HIDE_SWIPE_REFRESH_PROGRESS,
        UPDATE_CONTRACTS_LIST,
        ERROR
    }

    private final State state;
    private String formattedWalletBalance;
    private String errorMsg;
    private String address;

    private WalletUIState(State state) {
        this.state = state;
    }

    private WalletUIState(State state, String formattedWalletBalance, String address) {
        this.state = state;
        this.formattedWalletBalance = formattedWalletBalance;
        this.address = address;
    }

    private WalletUIState(State state, String errorMsg) {
        this.state = state;
        this.errorMsg = errorMsg;
    }

    public static WalletUIState defaultState() {
        return new WalletUIState(State.DEFAULT);
    }

    public static WalletUIState clearWallet() {
        return new WalletUIState(State.CLEAR_WALLET);
    }

    public static WalletUIState successGetWalletBalance(String walletBalance, String address) {
        return new WalletUIState(State.SUCCESS_GET_WALLET_BALANCE, walletBalance, address);
    }

    public static WalletUIState successAddWallet() {
        return new WalletUIState(State.SUCCESS_ADD_WALLET);
    }

    public static WalletUIState addCredentialsMnemonicSuccess() {
        return new WalletUIState(State.ADD_CREDENTIALS_MNEMONIC_SUCCESS);
    }

    public static WalletUIState successGetUserContracts() {
        return new WalletUIState(State.SUCCESS_GET_USER_CONTRACTS);
    }

    public static WalletUIState progressResetSwitch() {
        return new WalletUIState(State.PROGRESS_RESET_SWITCH);
    }

    public static WalletUIState progressAddWallet() {
        return new WalletUIState(State.PROGRESS_ADD_WALLET);
    }

    public static WalletUIState successResetSwitch() {
        return new WalletUIState(State.SUCCESS_RESET_SWITCH);
    }

    public static WalletUIState failedResetSwitch(String errorMsg) {
        return new WalletUIState(State.FAILED_RESET_SWITCH, errorMsg);
    }

    public static WalletUIState error(String errorMsg) {
        return new WalletUIState(State.ERROR, errorMsg);
    }

    public static WalletUIState hideSwipeRefresh() {
        return new WalletUIState(State.HIDE_SWIPE_REFRESH_PROGRESS);
    }

    public static WalletUIState updateContractsList() {
        return new WalletUIState(State.UPDATE_CONTRACTS_LIST);
    }

    public State getState() {
        return state;
    }

    public String getFormattedWalletBalance() {
        return formattedWalletBalance;
    }

    public String getAddress() {
        return address;
    }

    public String getErrorMsg() {
        return errorMsg;
    }


}
