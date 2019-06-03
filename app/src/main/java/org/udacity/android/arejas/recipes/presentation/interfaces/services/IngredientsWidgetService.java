package org.udacity.android.arejas.recipes.presentation.interfaces.services;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v4.app.JobIntentService;

import org.udacity.android.arejas.recipes.R;
import org.udacity.android.arejas.recipes.domain.business.usecases.GetRecipeInfoUseCase;
import org.udacity.android.arejas.recipes.presentation.ui.widget.IngredientsWidget;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

/**
 * An {@link IntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class IngredientsWidgetService extends JobIntentService {

    public static final int UNKNOWN_RECIPE_ID = -1;
    private static final String WIDGET_PREFERENCES = "widgetPreferences";

    private static final int JOB_ID_UPDATE = 1;
    private static final int JOB_ID_SET_RECIPE = 1;
    private static final int JOB_ID_REMOVE_WIDGET = 1;

    private static final String ACTION_UPDATE_WIDGETS = "org.udacity.android.arejas.recipes.presentation.interfaces.services.action.WIDGETS";
    private static final String ACTION_SET_RECIPE_FOR_WIDGET = "org.udacity.android.arejas.recipes.presentation.interfaces.services.action.SET_RECIPE_FOR_WIDGET";
    private static final String ACTION_REMOVE_WIDGET = "org.udacity.android.arejas.recipes.presentation.interfaces.services.action.REMOVE_WIDGET";

    private static final String EXTRA_WIDGET_ID = "org.udacity.android.arejas.recipes.presentation.interfaces.services.extra.WIDGET_ID";
    private static final String EXTRA_RECIPE_ID = "org.udacity.android.arejas.recipes.presentation.interfaces.services.extra.RECIPE_ID";

    @Inject
    public GetRecipeInfoUseCase getRecipesUseCase;

    public IngredientsWidgetService() {
        super();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        // Dependency injection
        AndroidInjection.inject(this);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_UPDATE_WIDGETS.equals(action)) {
                handleActionUpdateWidgets();
            } else if (ACTION_SET_RECIPE_FOR_WIDGET.equals(action)) {
                final int widgetId = intent.getIntExtra(EXTRA_WIDGET_ID, 0);
                final int recipeId = intent.getIntExtra(EXTRA_RECIPE_ID, UNKNOWN_RECIPE_ID);
                handleActionSetRecipeForWidget(widgetId, recipeId);
            } else if (ACTION_REMOVE_WIDGET.equals(action)) {
                final int widgetId = intent.getIntExtra(EXTRA_WIDGET_ID, 0);
                handleActionRemoveWidget(widgetId);
            }
        }
    }

    /**
     * Handle action UpdateWidgets in the provided background thread with the provided
     * parameters.
     */
    private void handleActionUpdateWidgets() {
        // Get app widget manager and the IDs of the widget instances deployed
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(new ComponentName(this, IngredientsWidget.class));
        // For each of the widget instances
        for (int widgetId: appWidgetIds) {
            // Get recipe Id for widget from preferences
            int recipeId = getRecipeIdForWidgetId(this, widgetId);
            // Request data of the new recipe and update the widget with it
            getRecipesUseCase.getRecipeInfoForWidget(recipeId, recipeWidgetItem -> {
                //Now update all widgets
                IngredientsWidget.updateAppWidget(this, appWidgetManager, widgetId, recipeWidgetItem);
            });
        }
    }

    /**
     * Handle action SetRecipeForWidget in the provided background thread with the provided
     * parameters.
     */
    private void handleActionSetRecipeForWidget(int widgetId, int recipeId) {
        // get app widget manager
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);
        // Save preferences for widget Id
        saveRecipeIdForWidgetId(getApplicationContext(), widgetId, recipeId);
        //Set now widget as loading
        IngredientsWidget.updateAppWidgetAsLoading(this, appWidgetManager, widgetId);
        // Request data of the new recipe and update the widget with it
        getRecipesUseCase.getRecipeInfoForWidget(recipeId, recipeWidgetItem -> {
            //Now update all widgets
            IngredientsWidget.updateAppWidget(this, appWidgetManager, widgetId, recipeWidgetItem);
        });
    }

    /**
     * Handle action RemoveWidget in the provided background thread with the provided
     * parameters.
     */
    private void handleActionRemoveWidget(int widgetId) {
        // Remove preferences for widget Id
        removeRecipeIdForWidgetId(getApplicationContext(), widgetId);
    }

    /**
     * Starts this service to perform action UpdateWidgets with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionUpdateWidgets(Context context) {
        Intent intent = new Intent(context, IngredientsWidgetService.class);
        intent.setAction(ACTION_UPDATE_WIDGETS);
        enqueueWork(context, IngredientsWidgetService.class, JOB_ID_UPDATE, intent);
    }

    /**
     * Starts this service to perform action SetRecipeForWidget with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionSetRecipeForWidget(Context context, int widgetId, int recipeId) {
        Intent intent = new Intent(context, IngredientsWidgetService.class);
        intent.setAction(ACTION_SET_RECIPE_FOR_WIDGET);
        intent.putExtra(EXTRA_WIDGET_ID, widgetId);
        intent.putExtra(EXTRA_RECIPE_ID, recipeId);
        enqueueWork(context, IngredientsWidgetService.class, JOB_ID_SET_RECIPE, intent);
    }

    /**
     * Starts this service to perform action RemoveWidget with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see IntentService
     */
    public static void startActionRemoveWidget(Context context, int widgetId) {
        Intent intent = new Intent(context, IngredientsWidgetService.class);
        intent.setAction(ACTION_REMOVE_WIDGET);
        intent.putExtra(EXTRA_WIDGET_ID, widgetId);
        enqueueWork(context, IngredientsWidgetService.class, JOB_ID_REMOVE_WIDGET, intent);
    }

    private static int getRecipeIdForWidgetId(Context context, int widgetId) {
        return context.getSharedPreferences(WIDGET_PREFERENCES, 0)
                .getInt(context.getString(R.string.preference_recipe_id_for_widget, widgetId),
                        UNKNOWN_RECIPE_ID);
    }

    private static void saveRecipeIdForWidgetId(Context context, int widgetId, int recipeId) {
        SharedPreferences.Editor editor = context.getSharedPreferences(WIDGET_PREFERENCES, 0).edit();
        editor.putInt(context.getString(R.string.preference_recipe_id_for_widget, widgetId), recipeId);
        editor.apply();
        editor.clear();
    }

    private static void removeRecipeIdForWidgetId(Context context, int widgetId) {
        SharedPreferences.Editor editor = context.getSharedPreferences(WIDGET_PREFERENCES, 0).edit();
        editor.remove(context.getString(R.string.preference_recipe_id_for_widget, widgetId));
        editor.apply();
        editor.clear();
    }

}
