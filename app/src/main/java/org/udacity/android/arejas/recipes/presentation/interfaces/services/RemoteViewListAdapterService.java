package org.udacity.android.arejas.recipes.presentation.interfaces.services;


import android.content.Context;
import android.content.Intent;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import org.udacity.android.arejas.recipes.R;
import org.udacity.android.arejas.recipes.domain.business.usecases.GetRecipeInfoUseCase;
import org.udacity.android.arejas.recipes.domain.entities.RecipeIngredient;

import java.text.DecimalFormat;
import java.util.List;

import javax.inject.Inject;

import dagger.android.AndroidInjection;


public class RemoteViewListAdapterService extends RemoteViewsService {

    public static final String EXTRA_RECIPE_ID = "RECIPE_ID";

    @Inject
    public GetRecipeInfoUseCase getRecipesUseCase;

    @Override
    public void onCreate() {
        super.onCreate();

        // Dependency injection
        AndroidInjection.inject(this);
    }

    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        int recipeId = intent.getIntExtra(EXTRA_RECIPE_ID, IngredientsWidgetService.UNKNOWN_RECIPE_ID);
        return new RemoteViewListView(this.getApplicationContext(), getRecipesUseCase, recipeId);
    }
}

class RemoteViewListView implements RemoteViewsService.RemoteViewsFactory {

    private final Context mContext;

    private final GetRecipeInfoUseCase getRecipesUseCase;

    private final int recipeId;

    private List<RecipeIngredient> ingredients;

    public RemoteViewListView(Context applicationContext, GetRecipeInfoUseCase getRecipesUseCase, int recipeId) {
        mContext = applicationContext;
        this.getRecipesUseCase = getRecipesUseCase;
        this.recipeId = recipeId;
    }

    @Override
    public void onCreate() {

    }

    //called on start and when notifyAppWidgetViewDataChanged is called
    @Override
    public void onDataSetChanged() {
        // Get ingredients list for recipe ID
        ingredients = getRecipesUseCase.getRecipeIngredients(recipeId);
    }

    @Override
    public void onDestroy() {
        if (ingredients != null)
            ingredients.clear();
    }

    @Override
    public int getCount() {
        if ((ingredients == null) || (ingredients.size() < 1))
            return 1; // Show at leas a no ingredients waring
        return ingredients.size();
    }

    /**
     * This method acts like the onBindViewHolder method in an Adapter
     *
     * @param position The current position of the item in the GridView to be displayed
     * @return The RemoteViews object to display for the provided postion
     */
    @Override
    public RemoteViews getViewAt(int position) {
        DecimalFormat measureDecimalFormat = new DecimalFormat("#.##");
        String recipeIngredientString = "";
        if ((ingredients != null) && (ingredients.size() > position)) {
            RecipeIngredient ingredient = ingredients.get(position);
            String ingredientString = "- " + ingredient.getIngredient();
            if (ingredientString != null) {
                if (ingredient.getQuantity() != null) {
                    ingredientString = ingredientString.concat(": ")
                            .concat(measureDecimalFormat.format(ingredient.getQuantity()));
                }
                if (ingredient.getMeasure() != null) {
                    ingredientString = ingredientString.concat(" ").concat(ingredient.getMeasure());
                }
                recipeIngredientString = ingredientString;
            }
        }
        if (recipeIngredientString.isEmpty()) {
            recipeIngredientString = mContext.getString(R.string.no_ingredients);
        }

        RemoteViews views = new RemoteViews(mContext.getPackageName(), R.layout.widget_ingredient_item);

        // Set text for recipe ingredient string
        views.setTextViewText(R.id.tv_widget_item_text, recipeIngredientString);

        return views;

    }

    @Override
    public RemoteViews getLoadingView() {
        return null;
    }

    @Override
    public int getViewTypeCount() {
        return 1;
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }
}

