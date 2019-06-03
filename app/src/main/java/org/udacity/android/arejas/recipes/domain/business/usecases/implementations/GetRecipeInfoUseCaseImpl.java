package org.udacity.android.arejas.recipes.domain.business.usecases.implementations;

import android.arch.lifecycle.LiveData;
import android.arch.paging.PagedList;

import org.udacity.android.arejas.recipes.data.interfaces.repositories.RecipesRepository;
import org.udacity.android.arejas.recipes.domain.business.usecases.GetRecipeInfoUseCase;
import org.udacity.android.arejas.recipes.domain.entities.Recipe;
import org.udacity.android.arejas.recipes.domain.entities.RecipeIngredient;
import org.udacity.android.arejas.recipes.domain.entities.RecipeListItem;
import org.udacity.android.arejas.recipes.domain.entities.RecipeWidgetItem;
import org.udacity.android.arejas.recipes.utils.entities.Resource;
import org.udacity.android.arejas.recipes.utils.functional.Consumer;

import java.util.List;

public class GetRecipeInfoUseCaseImpl implements GetRecipeInfoUseCase {

    private final RecipesRepository repository;

    public GetRecipeInfoUseCaseImpl(RecipesRepository repository) {
        this.repository = repository;
    }

    @Override
    public LiveData<Resource<PagedList<RecipeListItem>>> getRecipes(boolean forceReload) {
        return repository.getRecipes(forceReload);
    }

    @Override
    public LiveData<Resource<Recipe>> getRecipe(int recipeId) {
        return repository.getRecipe(recipeId);
    }

    @Override
    public void getRecipeInfoForWidget(Integer recipeId, Consumer<RecipeWidgetItem> consumer) {
        if ((recipeId != null) && (recipeId >= 0)) {
            repository.getRecipeInfoForWidget(recipeId, consumer);
        } else {
            repository.getFirstRecipeInfoForWidget(consumer);
        }
    }

    @Override
    public List<RecipeIngredient> getRecipeIngredients(Integer recipeId) {
        if ((recipeId != null) && (recipeId >= 0)) {
            return repository.getRecipeIngredients(recipeId);
        } else {
            return repository.getFirstRecipeIngredients();
        }
    }

}
