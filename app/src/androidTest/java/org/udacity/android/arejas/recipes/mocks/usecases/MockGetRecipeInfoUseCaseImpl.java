package org.udacity.android.arejas.recipes.mocks.usecases;

import android.arch.lifecycle.LiveData;
import android.arch.paging.PagedList;
import android.support.annotation.Nullable;

import org.udacity.android.arejas.recipes.data.interfaces.repositories.RecipesRepository;
import org.udacity.android.arejas.recipes.domain.business.usecases.GetRecipeInfoUseCase;
import org.udacity.android.arejas.recipes.domain.entities.Recipe;
import org.udacity.android.arejas.recipes.domain.entities.RecipeIngredient;
import org.udacity.android.arejas.recipes.domain.entities.RecipeListItem;
import org.udacity.android.arejas.recipes.domain.entities.RecipeWidgetItem;
import org.udacity.android.arejas.recipes.mocks.MockRecipesApplication;
import org.udacity.android.arejas.recipes.utils.SimpleIdlingResource;
import org.udacity.android.arejas.recipes.utils.entities.Resource;
import org.udacity.android.arejas.recipes.utils.functional.Consumer;

import java.util.List;

/*
 * Mock use case with the same implementation as the original but taking care of annooncind de idling
 * resources as not idle during async tasks
 */
public class MockGetRecipeInfoUseCaseImpl implements GetRecipeInfoUseCase {

    private final RecipesRepository repository;

    // The Idling Resource which will be used.
    @Nullable
    private static SimpleIdlingResource mIdlingResource;

    public MockGetRecipeInfoUseCaseImpl(RecipesRepository repository) {
        this.repository = repository;
        mIdlingResource = MockRecipesApplication.getIdlingResource();
    }

    @Override
    public LiveData<Resource<PagedList<RecipeListItem>>> getRecipes(boolean forceReload) {
        mIdlingResource.setIdleState(false);
        LiveData<Resource<PagedList<RecipeListItem>>> toReturn = repository.getRecipes(forceReload);
        toReturn.observeForever(pagedListResource -> {
            if (pagedListResource.getStatus() == Resource.Status.SUCCESS) {
                mIdlingResource.setIdleState(true);
            }
        });
        return toReturn;
    }

    @Override
    public LiveData<Resource<Recipe>> getRecipe(int recipeId) {
        mIdlingResource.setIdleState(false);
        LiveData<Resource<Recipe>> toReturn = repository.getRecipe(recipeId);
        toReturn.observeForever(resource -> {
            if (resource.getStatus() == Resource.Status.SUCCESS) {
                mIdlingResource.setIdleState(true);
            }
        });
        return toReturn;
    }

    @Override
    public void getRecipeInfoForWidget(Integer recipeId, Consumer<RecipeWidgetItem> consumer) {
        mIdlingResource.setIdleState(false);
        Consumer<RecipeWidgetItem> wrappingConsumer = recipeWidgetItem -> {
            mIdlingResource.setIdleState(true);
            consumer.accept(recipeWidgetItem);
        };
        if ((recipeId != null) && (recipeId >= 0)) {
            repository.getRecipeInfoForWidget(recipeId, consumer);
        } else {
            repository.getFirstRecipeInfoForWidget(consumer);
        }
    }

    @Override
    public List<RecipeIngredient> getRecipeIngredients(Integer recipeId) {
        List<RecipeIngredient> toReturn;
        mIdlingResource.setIdleState(false);
        if ((recipeId != null) && (recipeId >= 0)) {
            toReturn = repository.getRecipeIngredients(recipeId);
        } else {
            toReturn = repository.getFirstRecipeIngredients();
        }
        mIdlingResource.setIdleState(true);
        return toReturn;
    }

}
