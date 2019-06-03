package org.udacity.android.arejas.recipes.domain.business.usecases;

import org.udacity.android.arejas.recipes.domain.business.usecases.base.BaseUseCase;

public interface LoadRecipesFromNetworkUseCase extends BaseUseCase {

    void loadAndSaveRecipesFromNetwork(String url, String file);
}
