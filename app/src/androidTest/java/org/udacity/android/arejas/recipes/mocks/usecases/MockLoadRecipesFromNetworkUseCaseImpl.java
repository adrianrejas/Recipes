package org.udacity.android.arejas.recipes.mocks.usecases;

import android.arch.lifecycle.LiveData;
import android.support.annotation.Nullable;

import org.udacity.android.arejas.recipes.data.interfaces.repositories.RecipesRepository;
import org.udacity.android.arejas.recipes.domain.business.usecases.LoadRecipesFromNetworkUseCase;
import org.udacity.android.arejas.recipes.mocks.MockRecipesApplication;
import org.udacity.android.arejas.recipes.utils.SimpleIdlingResource;
import org.udacity.android.arejas.recipes.utils.entities.Resource;

/*
 * Mock use case with the same implementation as the original but taking care of annooncind de idling
 * resources as not idle during async tasks
 */
public class MockLoadRecipesFromNetworkUseCaseImpl implements LoadRecipesFromNetworkUseCase {

    private final RecipesRepository repository;

    // The Idling Resource which will be used.
    @Nullable
    private static SimpleIdlingResource mIdlingResource;

    public MockLoadRecipesFromNetworkUseCaseImpl(RecipesRepository repository) {
        this.repository = repository;
        mIdlingResource = MockRecipesApplication.getIdlingResource();
    }

    @Override
    public void loadAndSaveRecipesFromNetwork(String url, String file) {
        LiveData<Resource> action = repository.requestRecipesFromNetworkAndSaveThemOnDatabase(url, file);
        action.observeForever(resource -> {
            if (resource.getStatus() == Resource.Status.SUCCESS) {
                mIdlingResource.setIdleState(true);
            }
        });
    }

}
