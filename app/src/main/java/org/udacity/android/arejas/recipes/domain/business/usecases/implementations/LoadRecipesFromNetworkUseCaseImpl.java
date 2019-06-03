package org.udacity.android.arejas.recipes.domain.business.usecases.implementations;

import org.udacity.android.arejas.recipes.data.interfaces.repositories.RecipesRepository;
import org.udacity.android.arejas.recipes.domain.business.usecases.LoadRecipesFromNetworkUseCase;

public class LoadRecipesFromNetworkUseCaseImpl implements LoadRecipesFromNetworkUseCase {

    private final RecipesRepository repository;

    public LoadRecipesFromNetworkUseCaseImpl(RecipesRepository repository) {
        this.repository = repository;
    }

    @Override
    public void loadAndSaveRecipesFromNetwork(String url, String file) {
        repository.requestRecipesFromNetworkAndSaveThemOnDatabase(url, file);
    }

}
