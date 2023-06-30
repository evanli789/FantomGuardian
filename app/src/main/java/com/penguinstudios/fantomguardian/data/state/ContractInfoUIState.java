package com.penguinstudios.fantomguardian.data.state;

import com.penguinstudios.fantomguardian.data.model.FormattedContractInfo;

public class ContractInfoUIState {

    public enum State {
        PROGRESS_DELETE_SWITCH,
        SUCCESS_DELETE_SWITCH,
        FAILED_DELETE_SWITCH,
        SUCCESS_GET_CONTRACT_INFO,
        FAILED_GET_CONTRACT_INFO
    }

    private final State state;
    private FormattedContractInfo formattedContractInfo;
    private String txHash;
    private String contractAddress;

    private ContractInfoUIState(State state) {
        this.state = state;
    }

    private ContractInfoUIState(State state, FormattedContractInfo formattedContractInfo) {
        this.state = state;
        this.formattedContractInfo = formattedContractInfo;
    }

    private ContractInfoUIState(State state, String txHash, String contractAddress) {
        this.state = state;
        this.txHash = txHash;
        this.contractAddress = contractAddress;
    }

    public static ContractInfoUIState successGetContractInfo(FormattedContractInfo formattedContractInfo) {
        return new ContractInfoUIState(State.SUCCESS_GET_CONTRACT_INFO, formattedContractInfo);
    }

    public static ContractInfoUIState failedGetContractInfo() {
        return new ContractInfoUIState(State.FAILED_GET_CONTRACT_INFO);
    }

    public static ContractInfoUIState progressDeleteSwitch() {
        return new ContractInfoUIState(State.PROGRESS_DELETE_SWITCH);
    }

    public static ContractInfoUIState successDeleteSwitch(String txHash, String contractAddress) {
        return new ContractInfoUIState(State.SUCCESS_DELETE_SWITCH, txHash, contractAddress);
    }

    public static ContractInfoUIState failedDeleteSwitch() {
        return new ContractInfoUIState(State.FAILED_DELETE_SWITCH);
    }

    public State getState() {
        return state;
    }

    public String getTxHash() {
        return txHash;
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public FormattedContractInfo getFormattedContractInfo() {
        return formattedContractInfo;
    }
}
