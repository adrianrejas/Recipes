package org.udacity.android.arejas.recipes.mocks.di;

import org.udacity.android.arejas.recipes.RecipesApplication;
import org.udacity.android.arejas.recipes.data.interfaces.repositories.RecipesRepository;
import org.udacity.android.arejas.recipes.data.sources.database.RecipesDatabase;
import org.udacity.android.arejas.recipes.domain.business.usecases.GetRecipeInfoUseCase;
import org.udacity.android.arejas.recipes.domain.business.usecases.LoadRecipesFromNetworkUseCase;
import org.udacity.android.arejas.recipes.mocks.MockRecipesApplication;
import org.udacity.android.arejas.recipes.mocks.usecases.MockGetRecipeInfoUseCaseImpl;
import org.udacity.android.arejas.recipes.mocks.usecases.MockLoadRecipesFromNetworkUseCaseImpl;
import org.udacity.android.arejas.recipes.utils.di.modules.AppModule;

import dagger.Module;

/*
 * Mock applicacion module used for injecting mock database and use cases.
 */
@Module
public class MockAppModule extends AppModule {

    public MockAppModule() {
        super();
    }

    @Override
    public RecipesDatabase provideDatabase(RecipesApplication application) {
        return MockRecipesApplication.getRecipesDatabase();
    }

    @Override
    public GetRecipeInfoUseCase provideGetRecipeInfoUseCase(RecipesRepository repository) {
        return new MockGetRecipeInfoUseCaseImpl(repository);
    }

    @Override
    public LoadRecipesFromNetworkUseCase provideLoadRecipesFromNetworkUseCase(RecipesRepository repository) {
        return new MockLoadRecipesFromNetworkUseCaseImpl(repository);
    }
}
