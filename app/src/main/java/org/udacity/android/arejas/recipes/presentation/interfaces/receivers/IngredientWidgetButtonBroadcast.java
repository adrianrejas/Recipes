package org.udacity.android.arejas.recipes.presentation.interfaces.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import org.udacity.android.arejas.recipes.presentation.interfaces.services.IngredientsWidgetService;

public class IngredientWidgetButtonBroadcast extends BroadcastReceiver {

    private static final String ACTION_SET_RECIPE_FOR_WIDGET = "org.udacity.android.arejas.recipes.presentation.interfaces.broadcast.action.SET_RECIPE_FOR_WIDGET";

    private static final String EXTRA_WIDGET_ID = "org.udacity.android.arejas.recipes.presentation.interfaces.broadcast.extra.WIDGET_ID";
    private static final String EXTRA_RECIPE_ID = "org.udacity.android.arejas.recipes.presentation.interfaces.broadcast.extra.RECIPE_ID";

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SET_RECIPE_FOR_WIDGET.equals(action)) {
                final int widgetId = intent.getIntExtra(EXTRA_WIDGET_ID, 0);
                final int recipeId = intent.getIntExtra(EXTRA_RECIPE_ID, IngredientsWidgetService.UNKNOWN_RECIPE_ID);
                IngredientsWidgetService.startActionSetRecipeForWidget(context, widgetId, recipeId);
            }
        }
    }

    public static Intent createActionSetRecipeForWidget(Context context, int widgetId, int recipeId) {
        Intent intent = new Intent(context, IngredientWidgetButtonBroadcast.class);
        intent.setAction(ACTION_SET_RECIPE_FOR_WIDGET);
        intent.putExtra(EXTRA_WIDGET_ID, widgetId);
        intent.putExtra(EXTRA_RECIPE_ID, recipeId);
        return intent;
    }
}
