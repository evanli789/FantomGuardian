package com.penguinstudios.fantomguardian.viewmodel;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;

import com.penguinstudios.fantomguardian.data.LocalRepository;
import com.penguinstudios.fantomguardian.data.state.EnterMnemonicUIState;
import com.penguinstudios.fantomguardian.di.ViewDecryptionPhraseViewModelFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import dagger.assisted.Assisted;
import dagger.assisted.AssistedInject;
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.disposables.Disposable;
import io.reactivex.rxjava3.schedulers.Schedulers;
import timber.log.Timber;

public class ViewDecryptionPhraseViewModel extends ViewModel {

    private final LocalRepository localRepository;
    private final List<String> wordList = new ArrayList<>();
    private final MutableLiveData<EnterMnemonicUIState> liveData = new MutableLiveData<>();
    private final String contractAddress;
    private String decryptionPhrase;
    private Disposable disposable;

    @SuppressWarnings("unchecked")
    public static ViewModelProvider.Factory provideFactory(
            ViewDecryptionPhraseViewModelFactory assistedFactory, String contractAddress) {

        return new ViewModelProvider.Factory() {
            @NonNull
            @Override
            public <T extends ViewModel> T create(@NonNull Class<T> modelClass) {
                return (T) assistedFactory.create(contractAddress);
            }
        };
    }

    @AssistedInject
    public ViewDecryptionPhraseViewModel(@Assisted String contractAddress, LocalRepository localRepository) {
        this.contractAddress = contractAddress;
        this.localRepository = localRepository;
    }

    public void queryDecryptionPhrase() {
        disposable = localRepository.getDecryptionPhrase(contractAddress)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(decryptionPhrase -> {
                    this.decryptionPhrase = decryptionPhrase;
                    wordList.addAll(createWordList(decryptionPhrase));
                    liveData.setValue(EnterMnemonicUIState.successGetDecryptionPhrase());
                }, err -> {
                    liveData.setValue(EnterMnemonicUIState.error());
                    Timber.e(err);
                });
    }

    private List<String> createWordList(String decryptionPhrase) {
        String[] words = decryptionPhrase.split(" ");
        return Arrays.asList(words);
    }

    public List<String> getWordList() {
        return wordList;
    }

    public MutableLiveData<EnterMnemonicUIState> getLiveData() {
        return liveData;
    }

    public void dispose() {
        if (disposable != null) {
            disposable.dispose();
        }
    }

    public String getContractAddress() {
        return contractAddress;
    }

    public String getDecryptionPhrase() {
        return decryptionPhrase;
    }
}
