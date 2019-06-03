package org.udacity.android.arejas.recipes.presentation.interfaces.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import org.udacity.android.arejas.recipes.domain.business.usecases.GetRecipeInfoUseCase;
import org.udacity.android.arejas.recipes.domain.entities.Recipe;
import org.udacity.android.arejas.recipes.utils.entities.Resource;

public class RecipeDetailsViewModel extends AndroidViewModel {

    private LiveData<Resource<Recipe>> recipeDetails;

    private final GetRecipeInfoUseCase getRecipeInfoUseCase;

    private final Integer recipeId;

    public RecipeDetailsViewModel(@NonNull Application application, Integer recipeIdToLoad, GetRecipeInfoUseCase getRecipeInfoUseCase) {
        super(application);
        this.recipeId = recipeIdToLoad;
        this.getRecipeInfoUseCase = getRecipeInfoUseCase;
    }

    public LiveData<Resource<Recipe>> getRecipeDetails() {
        if (recipeDetails == null) {
            recipeDetails = getRecipeInfoUseCase.getRecipe(recipeId);
        }
        return recipeDetails;
    }

}
