package com.penguinstudios.fantomguardian.di;

import com.penguinstudios.fantomguardian.viewmodel.ContractInfoViewModel;

import dagger.assisted.AssistedFactory;

@AssistedFactory
public interface ContractInfoViewModelFactory {
    ContractInfoViewModel create(String contractAddress);
}
