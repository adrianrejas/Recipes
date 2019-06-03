package org.udacity.android.arejas.recipes.presentation.ui.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.ActionBar;
import android.view.MenuItem;

import org.udacity.android.arejas.recipes.R;
import org.udacity.android.arejas.recipes.presentation.interfaces.viewmodels.RecipeDetailsViewModel;
import org.udacity.android.arejas.recipes.presentation.interfaces.viewmodels.factories.ViewModelFactory;
import org.udacity.android.arejas.recipes.presentation.ui.fragments.RecipeIngredientsFragment;
import org.udacity.android.arejas.recipes.presentation.ui.fragments.RecipeStepFragment;
import org.udacity.android.arejas.recipes.utils.Utils;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

/**
 * An activity representing a single RecipeDb detail screen. This
 * activity is only used on narrow width devices. On tablet-size devices,
 * item details are presented side-by-side with a list of items
 * in a {@link RecipeDetailsListActivity}.
 */
public class RecipeDetailInfoActivity extends AppCompatActivity {

    /**
     * The argument representing the recipe ID that this fragment
     * represents.
     */
    public static final String ARG_RECIPE_ID = "recipe_id";

    /**
     * The argument representing the recipe step ID that this fragment
     * represents.
     */
    public static final String ARG_RECIPE_STEP_POSITION_ID = "recipe_step_id";

    @Inject
    public ViewModelFactory viewModelFactory;

    private Integer recipeId;
    private Integer stepPositionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Get ID of the recipe to load */
        if (getIntent().hasExtra(ARG_RECIPE_ID)) {
            int recipe = getIntent().getIntExtra(ARG_RECIPE_ID, -1);
            this.recipeId = (recipe >= 0) ? recipe : null;
        }

        /* Get ID of the step to load */
        if (getIntent().hasExtra(ARG_RECIPE_STEP_POSITION_ID)) {
            int step = getIntent().getIntExtra(ARG_RECIPE_STEP_POSITION_ID, -1);
            stepPositionId = (step >= 0) ? step : null;
        }

        /* Inflate main layout and get UI element references */
        setContentView(R.layout.activity_recipe_detail_info);

        // Dependency injection
        AndroidInjection.inject(this);

        // Show the Up button in the action bar.
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Set title (later changed by the ruling recipe)
        setTitle(Utils.getFontedString(getString(R.string.title_recipe_detail_info), R.font.tillana_bold));

        // If the recipe ID is defined load the suitable viewmodel.
        if (recipeId != null) {
            // Configure the viewmodel provider
            this.viewModelFactory.setRecipeIdToLoad(recipeId);
            // Get the viewmodel
            RecipeDetailsViewModel recipeDetailsViewModel = ViewModelProviders.of(this, this.viewModelFactory).get(RecipeDetailsViewModel.class);
        }

        // savedInstanceState is non-null when there is fragment state
        // saved from previous configurations of this activity
        // (e.g. when rotating the screen from portrait to landscape).
        // In this case, the fragment will automatically be re-added
        // to its container so we don't need to manually add it.
        // For more information, see the Fragments API guide at:
        //
        // http://developer.android.com/guide/components/fragments.html
        //
        if (savedInstanceState == null) {
            // Create the detail info fragment and add it to the activity
            // using a fragment transaction.
            // If a recipe step has been set, the fragment should show the step info
            // If not, the fragment should show the list of ingredients
            if (stepPositionId == null) {
                RecipeIngredientsFragment fragment = new RecipeIngredientsFragment();
                getSupportFragmentManager().beginTransaction().add(R.id.recipe_detail_container, fragment).commit();
            } else {
                Bundle arguments = new Bundle();
                arguments.putInt(RecipeStepFragment.ARG_STEP_POSITION_ID, stepPositionId.intValue());
                RecipeStepFragment fragment = new RecipeStepFragment();
                fragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction().add(R.id.recipe_detail_container, fragment).commit();
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home) {
            // This ID represents the Home or Up button. In the case of this
            // activity, the Up button is shown. For
            // more details, see the Navigation pattern on Android Design:
            //
            // http://developer.android.com/design/patterns/navigation.html#up-vs-back
            //
            Intent intent = new Intent(this, RecipeDetailsListActivity.class);
            if (recipeId != null)
                intent.putExtra(RecipeDetailsListActivity.ARG_RECIPE_ID, recipeId.intValue());
            navigateUpTo(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    
}