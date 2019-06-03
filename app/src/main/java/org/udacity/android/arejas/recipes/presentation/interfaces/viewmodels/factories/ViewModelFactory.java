package org.udacity.android.arejas.recipes.presentation.interfaces.viewmodels.factories;

import android.app.Application;
import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.annotation.NonNull;

import org.udacity.android.arejas.recipes.domain.business.usecases.GetRecipeInfoUseCase;
import org.udacity.android.arejas.recipes.domain.business.usecases.LoadRecipesFromNetworkUseCase;
import org.udacity.android.arejas.recipes.presentation.interfaces.viewmodels.RecipeDetailsViewModel;
import org.udacity.android.arejas.recipes.presentation.interfaces.viewmodels.RecipesListViewModel;

import java.security.InvalidParameterException;

import javax.inject.Inject;
import javax.inject.Singleton;

@SuppressWarnings("unchecked")
@Singleton
public class ViewModelFactory extends ViewModelProvider.NewInstanceFactory {

    private final Application application;

    private final GetRecipeInfoUseCase getRecipeInfoUseCase;
    private final LoadRecipesFromNetworkUseCase loadRecipesFromNetworkUseCase;

    private RecipesListViewModel listViewModel;
    private RecipeDetailsViewModel detailsViewModel;

    private Integer recipeIdToLoad;

    @Inject
    public ViewModelFactory(@NonNull Application application,
                            GetRecipeInfoUseCase getRecipeInfoUseCase,
                            LoadRecipesFromNetworkUseCase loadRecipesFromNetworkUseCase) {
        this.application = application;
        this.getRecipeInfoUseCase = getRecipeInfoUseCase;
        this.loadRecipesFromNetworkUseCase = loadRecipesFromNetworkUseCase;
        listViewModelSingleton();
        this.detailsViewModel = null;
    }

    public void setRecipeIdToLoad (int recipeId) {
        if ((recipeIdToLoad == null) || (recipeId != recipeIdToLoad)) {
            recipeIdToLoad = recipeId;
            this.detailsViewModel = new RecipeDetailsViewModel(application, recipeIdToLoad,
                    getRecipeInfoUseCase);
        }
    }

    @Override
    public <T extends ViewModel> T create(Class<T> modelClass) {
        //noinspection unchecked
        if (modelClass.isAssignableFrom(RecipesListViewModel.class))
            return (T) listViewModelSingleton();
        else if (modelClass.isAssignableFrom(RecipeDetailsViewModel.class)) {
            if (recipeIdToLoad != null) {
                return (T) detailsViewModelSingleton();
            } else {
                throw new InvalidParameterException("Need to specify recipe ID before");
            }
        }else {
            throw new ClassCastException("No view model class recognized");
        }
    }

    private RecipesListViewModel listViewModelSingleton() {
        if (listViewModel == null)
            listViewModel = new RecipesListViewModel(application,
                    getRecipeInfoUseCase,
                    loadRecipesFromNetworkUseCase);
        return listViewModel;
    }

    private RecipeDetailsViewModel detailsViewModelSingleton() {
        if (detailsViewModel == null)
            detailsViewModel = new RecipeDetailsViewModel(application, recipeIdToLoad,
                    getRecipeInfoUseCase);
        return detailsViewModel;
    }

}
