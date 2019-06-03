package org.udacity.android.arejas.recipes.utils.di.modules;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Context;

import org.udacity.android.arejas.recipes.RecipesApplication;
import org.udacity.android.arejas.recipes.data.interfaces.repositories.RecipesRepository;
import org.udacity.android.arejas.recipes.data.sources.database.RecipesDatabase;
import org.udacity.android.arejas.recipes.domain.business.usecases.GetRecipeInfoUseCase;
import org.udacity.android.arejas.recipes.domain.business.usecases.LoadRecipesFromNetworkUseCase;
import org.udacity.android.arejas.recipes.domain.business.usecases.implementations.GetRecipeInfoUseCaseImpl;
import org.udacity.android.arejas.recipes.domain.business.usecases.implementations.LoadRecipesFromNetworkUseCaseImpl;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class AppModule {

    private static final int THREADS_FOR_GENERAL_EXECUTOR = 3;

    @Provides
    Application provideApplication() {
        return RecipesApplication.application;
    }

    @Provides
    Context provideApplicationContext() {
        return RecipesApplication.application.getApplicationContext();
    }

    @Provides
    @Named("dbExecutor")
    Executor provideDbExecutor() {
        return Executors.newSingleThreadExecutor();
    }

    @Provides
    @Named("networkExecutor")
    Executor provideNetworkExecutor() {
        return Executors.newSingleThreadExecutor();
    }

    @Provides
    @Named("generalExecutor")
    Executor provideGeneralExecutor() {
        return Executors.newFixedThreadPool(THREADS_FOR_GENERAL_EXECUTOR);
    }

    @Provides
    @Singleton
    public RecipesDatabase provideDatabase(RecipesApplication application) {
        return Room.databaseBuilder(application,
                RecipesDatabase.class, RecipesDatabase.RECIPES_DB_NAME)
                .build();
    }

    @Provides
    @Singleton
    public GetRecipeInfoUseCase provideGetRecipeInfoUseCase (RecipesRepository repository) {
        return new GetRecipeInfoUseCaseImpl(repository);
    }

    @Provides
    @Singleton
    public LoadRecipesFromNetworkUseCase provideLoadRecipesFromNetworkUseCase (RecipesRepository repository) {
        return new LoadRecipesFromNetworkUseCaseImpl(repository);
    }

}
