package org.udacity.android.arejas.recipes.presentation.interfaces.viewmodels;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;

import org.udacity.android.arejas.recipes.domain.business.usecases.GetRecipeInfoUseCase;
import org.udacity.android.arejas.recipes.domain.business.usecases.LoadRecipesFromNetworkUseCase;
import org.udacity.android.arejas.recipes.domain.entities.RecipeListItem;
import org.udacity.android.arejas.recipes.utils.entities.Resource;

public class RecipesListViewModel extends AndroidViewModel {

    private LiveData<Resource<PagedList<RecipeListItem>>> recipesList;

    private final GetRecipeInfoUseCase getRecipeInfoUseCase;
    private final LoadRecipesFromNetworkUseCase loadRecipesFromNetworkUseCase;

    public RecipesListViewModel(@NonNull Application application,
                                GetRecipeInfoUseCase getRecipeInfoUseCase,
                                LoadRecipesFromNetworkUseCase loadRecipesFromNetworkUseCase) {
        super(application);
        this.getRecipeInfoUseCase = getRecipeInfoUseCase;
        this.loadRecipesFromNetworkUseCase = loadRecipesFromNetworkUseCase;
    }

    public LiveData<Resource<PagedList<RecipeListItem>>> getRecipesList(boolean reload) {
        if (recipesList == null) {
            recipesList = getRecipeInfoUseCase.getRecipes(reload);
        }
        return recipesList;
    }

    public void loadRecipesFromNetworkUrl(String url, String file) {
        loadRecipesFromNetworkUseCase.loadAndSaveRecipesFromNetwork(url, file);
    }

}
