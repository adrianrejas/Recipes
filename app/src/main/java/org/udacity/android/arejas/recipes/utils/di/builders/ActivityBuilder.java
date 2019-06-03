package org.udacity.android.arejas.recipes.utils.di.builders;

import org.udacity.android.arejas.recipes.presentation.ui.activities.RecipeDetailsListActivity;
import org.udacity.android.arejas.recipes.presentation.ui.activities.RecipesListActivity;
import org.udacity.android.arejas.recipes.presentation.ui.activities.RecipeDetailInfoActivity;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ActivityBuilder {

    @ContributesAndroidInjector
    abstract RecipesListActivity bindRecipeListActivity();

    @ContributesAndroidInjector
    abstract RecipeDetailsListActivity bindRecipeDetailsListActivity();

    @ContributesAndroidInjector
    abstract RecipeDetailInfoActivity bindRecipeStepActivity();

}
