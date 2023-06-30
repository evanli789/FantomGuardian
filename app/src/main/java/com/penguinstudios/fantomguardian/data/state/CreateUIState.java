package com.penguinstudios.fantomguardian.data.state;

/**
 * Different ui states for CreateFragment
 */
public class CreateUIState {

    public enum State {
        RECIPIENT_ADDED,
        PROGRESS_DEPLOYING_SWITCH,
        SUCCESS_DEPLOY_SWITCH,
        NO_RESET_DURATION_SELECTED,
        NO_WALLET_ADDED,
        SCANNED_QR_CODE,
        ERROR
    }

    private final State state;
    private String errorMsg;
    private int scannedAdapterPosition;
    private String txHash, contractAddress;

    private CreateUIState(State state) {
        this.state = state;
    }

    private CreateUIState(State state, String errorMsg) {
        this.state = state;
        this.errorMsg = errorMsg;
    }

    private CreateUIState(State state, int scannedAdapterPosition) {
        this.state = state;
        this.scannedAdapterPosition = scannedAdapterPosition;
    }

    private CreateUIState(State state, String txHash, String contractAddress) {
        this.state = state;
        this.txHash = txHash;
        this.contractAddress = contractAddress;
    }

    public static CreateUIState recipientAdded() {
        return new CreateUIState(State.RECIPIENT_ADDED);
    }

    public static CreateUIState progressDeployingSwitch() {
        return new CreateUIState(State.PROGRESS_DEPLOYING_SWITCH);
    }

    public static CreateUIState noWalletAdded() {
        return new CreateUIState(State.NO_WALLET_ADDED);
    }

    public static CreateUIState error(String errorMsg) {
        return new CreateUIState(State.ERROR, errorMsg);
    }

    public static CreateUIState successDeploySwitch(String txHash, String contractAddress) {
        return new CreateUIState(State.SUCCESS_DEPLOY_SWITCH, txHash, contractAddress);
    }

    public static CreateUIState noResetDurationSelected() {
        return new CreateUIState(State.NO_RESET_DURATION_SELECTED);
    }

    public static CreateUIState scannedQRCode(int scannedAdapterPosition) {
        return new CreateUIState(State.SCANNED_QR_CODE, scannedAdapterPosition);
    }

    public String getTxHash() {
        return txHash;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public State getState() {
        return state;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public int getScannedAdapterPosition() {
        return scannedAdapterPosition;
    }
}
