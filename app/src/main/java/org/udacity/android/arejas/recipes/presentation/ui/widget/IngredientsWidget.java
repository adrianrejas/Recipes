package org.udacity.android.arejas.recipes.presentation.ui.widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.RemoteViews;

import org.udacity.android.arejas.recipes.R;
import org.udacity.android.arejas.recipes.domain.entities.RecipeWidgetItem;
import org.udacity.android.arejas.recipes.presentation.interfaces.receivers.IngredientWidgetButtonBroadcast;
import org.udacity.android.arejas.recipes.presentation.interfaces.services.IngredientsWidgetService;
import org.udacity.android.arejas.recipes.presentation.interfaces.services.RemoteViewListAdapterService;
import org.udacity.android.arejas.recipes.utils.Utils;

/**
 * Implementation of App Widget functionality.
 */
public class IngredientsWidget extends AppWidgetProvider {

    public static void updateAllAppWidgets(Context context, AppWidgetManager appWidgetManager,
                                          int[] appWidgetIds, RecipeWidgetItem[] recipeInfoList) {
        if (appWidgetIds.length != recipeInfoList.length)
            return;
        for (int i = 0; i < appWidgetIds.length; i++) {
            int appWidgetId = appWidgetIds[i];
            RecipeWidgetItem recipeInfo = recipeInfoList[i];
            updateAppWidget(context, appWidgetManager, appWidgetId, recipeInfo);
        }
    }

    public static void updateAppWidgetAsLoading(Context context, AppWidgetManager appWidgetManager,
                                       int appWidgetId) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_ingredients);

        // Hide loading screen
        views.setViewVisibility(R.id.rl_widget_loading, View.VISIBLE);

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    public static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId, RecipeWidgetItem recipeInfo) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.widget_ingredients);

        // Hide loading screen
        views.setViewVisibility(R.id.rl_widget_loading, View.GONE);

        // If recipe info set
        if (recipeInfo != null) {
            // Set the recipe name at the remote view
            views.setTextViewText(R.id.tv_widget_ingredients_title,
                    Utils.fromHtml("<u>" + recipeInfo.getName() + "</u>"));

            // Construct the ingredient list and set it at the remote view
            Intent intent = new Intent(context, RemoteViewListAdapterService.class);
            intent.putExtra(RemoteViewListAdapterService.EXTRA_RECIPE_ID, recipeInfo.getId());
            views.setRemoteAdapter(R.id.lv_widget_ingredients_listed, intent);

            // Set previous Button (creating pending intent if defined or hiding it if not defined)
            if (recipeInfo.getPrevId() != null) {
                views.setViewVisibility(R.id.bt_widget_recipe_previous, View.VISIBLE);
                PendingIntent pendingIntentPrevious = PendingIntent.getBroadcast(context, appWidgetId*2,
                        IngredientWidgetButtonBroadcast.createActionSetRecipeForWidget(context, appWidgetId,
                                recipeInfo.getPrevId()), PendingIntent.FLAG_UPDATE_CURRENT);
                views.setOnClickPendingIntent(R.id.bt_widget_recipe_previous, pendingIntentPrevious);
            } else {
                views.setViewVisibility(R.id.bt_widget_recipe_previous, View.GONE);
            }

            // Set next Button (creating pending intent if defined or hiding it if not defined)
            if (recipeInfo.getNextId() != null) {
                views.setViewVisibility(R.id.bt_widget_recipe_next, View.VISIBLE);
                PendingIntent pendingIntentNext = PendingIntent.getBroadcast(context, (appWidgetId*2)+1,
                        IngredientWidgetButtonBroadcast.createActionSetRecipeForWidget(context, appWidgetId,
                                recipeInfo.getNextId()), PendingIntent.FLAG_UPDATE_CURRENT);
                views.setOnClickPendingIntent(R.id.bt_widget_recipe_next, pendingIntentNext);
            } else {
                views.setViewVisibility(R.id.bt_widget_recipe_next, View.GONE);
            }
        } else { // If recipe info not set
            // Set title to unknown
            views.setTextViewText(R.id.tv_widget_ingredients_title,
                    Utils.fromHtml("<u>" + context.getString(R.string.unknown_recipe) + "</u>"));

            // Hide previous button
            views.setViewVisibility(R.id.bt_widget_recipe_previous, View.GONE);

            // Show next button with the idea of loading the first recipe available
            views.setViewVisibility(R.id.bt_widget_recipe_next, View.VISIBLE);
            PendingIntent pendingIntentNext = PendingIntent.getBroadcast(context, (appWidgetId*2)+1,
                    IngredientWidgetButtonBroadcast.createActionSetRecipeForWidget(context, appWidgetId,
                            IngredientsWidgetService.UNKNOWN_RECIPE_ID), PendingIntent.FLAG_UPDATE_CURRENT);
            views.setOnClickPendingIntent(R.id.bt_widget_recipe_next, pendingIntentNext);
        }

        // Instruct the widget manager to update the widget
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // Request an update to interface service
        IngredientsWidgetService.startActionUpdateWidgets(context);
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        // Notify the interface widget to remove widget IDs
        for (int widgetId: appWidgetIds) {
            IngredientsWidgetService.startActionRemoveWidget(context, widgetId);
        }
    }
}

