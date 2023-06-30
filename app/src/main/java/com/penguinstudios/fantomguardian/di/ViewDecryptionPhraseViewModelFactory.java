package com.penguinstudios.fantomguardian.di;

import com.penguinstudios.fantomguardian.viewmodel.ViewDecryptionPhraseViewModel;

import dagger.assisted.AssistedFactory;

@AssistedFactory
public interface ViewDecryptionPhraseViewModelFactory {
    ViewDecryptionPhraseViewModel create(String contractAddress);
}
