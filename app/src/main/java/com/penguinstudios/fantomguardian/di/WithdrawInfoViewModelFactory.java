package com.penguinstudios.fantomguardian.di;

import com.penguinstudios.fantomguardian.viewmodel.WithdrawInfoViewModel;

import dagger.assisted.AssistedFactory;

@AssistedFactory
public interface WithdrawInfoViewModelFactory {
    WithdrawInfoViewModel create(String contractAddress);
}
