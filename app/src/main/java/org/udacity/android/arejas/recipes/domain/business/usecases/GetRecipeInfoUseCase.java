package org.udacity.android.arejas.recipes.domain.business.usecases;

import android.arch.lifecycle.LiveData;
import android.arch.paging.PagedList;

import org.udacity.android.arejas.recipes.domain.business.usecases.base.BaseUseCase;
import org.udacity.android.arejas.recipes.domain.entities.Recipe;
import org.udacity.android.arejas.recipes.domain.entities.RecipeIngredient;
import org.udacity.android.arejas.recipes.domain.entities.RecipeListItem;
import org.udacity.android.arejas.recipes.domain.entities.RecipeWidgetItem;
import org.udacity.android.arejas.recipes.utils.entities.Resource;
import org.udacity.android.arejas.recipes.utils.functional.Consumer;

import java.util.List;

public interface GetRecipeInfoUseCase extends BaseUseCase {

    LiveData<Resource<PagedList<RecipeListItem>>> getRecipes(boolean forceReload);

    LiveData<Resource<Recipe>> getRecipe(int recipeId);

    void getRecipeInfoForWidget(Integer recipeId, Consumer<RecipeWidgetItem> consumer);

    List<RecipeIngredient> getRecipeIngredients(Integer recipeId);
}
