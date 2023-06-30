package com.penguinstudios.fantomguardian.data.state;

public class EnterMnemonicUIState {

    public enum State {
        SUCCESS_GET_DECRYPTION_PHRASE,
        ERROR
    }

    private final State state;

    private EnterMnemonicUIState(State state) {
        this.state = state;
    }

    public static EnterMnemonicUIState successGetDecryptionPhrase() {
        return new EnterMnemonicUIState(State.SUCCESS_GET_DECRYPTION_PHRASE);
    }

    public static EnterMnemonicUIState error() {
        return new EnterMnemonicUIState(State.ERROR);
    }

    public State getState() {
        return state;
    }
}
