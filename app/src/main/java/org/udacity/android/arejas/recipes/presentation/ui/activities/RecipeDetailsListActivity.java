package org.udacity.android.arejas.recipes.presentation.ui.activities;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;

import org.udacity.android.arejas.recipes.R;

import org.udacity.android.arejas.recipes.domain.entities.Recipe;
import org.udacity.android.arejas.recipes.presentation.interfaces.viewmodels.RecipeDetailsViewModel;
import org.udacity.android.arejas.recipes.presentation.interfaces.viewmodels.factories.ViewModelFactory;
import org.udacity.android.arejas.recipes.presentation.ui.adapters.RecipeDetailsListAdapter;

import org.udacity.android.arejas.recipes.databinding.ActivityRecipeDetailsListBinding;

import org.udacity.android.arejas.recipes.presentation.ui.fragments.RecipeIngredientsFragment;
import org.udacity.android.arejas.recipes.presentation.ui.fragments.RecipeStepFragment;
import org.udacity.android.arejas.recipes.utils.Utils;
import org.udacity.android.arejas.recipes.utils.entities.Resource;

import java.io.IOException;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

/**
 * An activity representing a list of Recipes. This activity
 * has different presentations for handset and tablet-size devices. On
 * handsets, the activity presents a list of items, which when touched,
 * lead to a {@link RecipeDetailInfoActivity} representing
 * item details. On tablets, the activity presents the list of items and
 * item details side-by-side using two vertical panes.
 */
public class RecipeDetailsListActivity extends AppCompatActivity
        implements RecipeDetailsListAdapter.OnRecipesDetailsItemClickListener {

    /**
     * The argument representing the recipe ID that this fragment
     * represents.
     */
    public static final String ARG_RECIPE_ID = "recipe_id";

    @Inject
    public ViewModelFactory viewModelFactory;

    private Integer recipeId;

    private ActivityRecipeDetailsListBinding uiBinding;
    private RecipeDetailsListAdapter mAdapter;

    /**
     * Whether or not the activity is in two-pane mode, i.e. running on a tablet
     * device.
     */
    private boolean mTwoPane;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Inflate main layout and get UI element references */
        uiBinding = DataBindingUtil.setContentView(this, R.layout.activity_recipe_details_list);

        // Set title according to the font required
        setTitle(Utils.getFontedString(getString(R.string.title_recipe_details_list), R.font.tillana_bold));

        // Dependency injection
        AndroidInjection.inject(this);

        // MASTER-DETAIL FLOW: if the recipe detail container is present, it means the app is being
        // executed from a two panel device.
        if (uiBinding.recipeDetailContainer != null) {
            mTwoPane = true;
        }

        // Configure list adapter
        configureRecipeListAdapter();

        /* Get ID of the recipe to load */
        if (getIntent().hasExtra(ARG_RECIPE_ID)) {
            int recipe = getIntent().getIntExtra(ARG_RECIPE_ID, -1);
            this.recipeId = (recipe >= 0) ? recipe : null;
        }

        // If the recipe ID is null stop the function here, showing error before.
        if (recipeId == null) {
            showError(new NullPointerException("No ID was specified."));
            return;
        } else { // If the recipe ID is defined
            // Configure the viewmodel provider
            this.viewModelFactory.setRecipeIdToLoad(recipeId);

            // Get the viewmodel
            RecipeDetailsViewModel recipeDetailsViewModel = ViewModelProviders.of(this, this.viewModelFactory).get(RecipeDetailsViewModel.class);

            // Request info about the recipe and observe the result
            recipeDetailsViewModel.getRecipeDetails().observe(this, recipeResource -> {
                if (recipeResource == null) {
                    showError(new NullPointerException("No data was loaded."));
                } else {
                    if (recipeResource.getStatus() == Resource.Status.ERROR) {
                        showError(recipeResource.getError());
                    } else if (recipeResource.getStatus() == Resource.Status.LOADING) {
                        showLoading();
                    } else {
                        updateDetailsList(recipeResource.getData());
                    }
                }
            });
        }

    }

    /**
     * Function for setting the list of details of the recipe
     */
    private void updateDetailsList(Recipe data) {
        if (data != null) {
            if (((data.getIngredients() == null) || data.getIngredients().isEmpty()) &&
                    ((data.getSteps() == null) || data.getSteps().isEmpty())) {
                uiBinding.recipeDetailsList.setVisibility(View.GONE);
                if (mTwoPane)
                    uiBinding.recipeDetailContainer.setVisibility(View.GONE);
                uiBinding.recipeDetailsListLoadingLayout.loadingLayout.setVisibility(View.GONE);
                uiBinding.recipeDetailsListErrorLayout.errorLayout.setVisibility(View.GONE);
                uiBinding.recipeDetailsListNoElementsLayout.noElementsLayout.setVisibility(View.VISIBLE);
            } else {
                // Set title according to the font required
                setTitle(Utils.getFontedString(data.getName(), R.font.tillana_bold));
                uiBinding.recipeDetailsList.setVisibility(View.VISIBLE);
                if (mTwoPane)
                    uiBinding.recipeDetailContainer.setVisibility(View.VISIBLE);
                uiBinding.recipeDetailsListLoadingLayout.loadingLayout.setVisibility(View.GONE);
                uiBinding.recipeDetailsListErrorLayout.errorLayout.setVisibility(View.GONE);
                uiBinding.recipeDetailsListNoElementsLayout.noElementsLayout.setVisibility(View.GONE);
                mAdapter.setData(data);
                mAdapter.notifyDataSetChanged();
            }
        } else {
            showError(new NullPointerException("No data available."));
        }
    }

    /**
     * Function for configure data list adapter
     */
    private void configureRecipeListAdapter() {
        mAdapter = new RecipeDetailsListAdapter(this);
        uiBinding.recipeDetailsList.setAdapter(mAdapter);
    }

    /**
     * Show an error on the activity. The idea has been to escalate errors to the UI classes by the
     * exception mechanisms and let these classes decide how to show the error.
     *
     * @param error Exception with the error appeared.
     */
    private void showError(Throwable error) {
        uiBinding.recipeDetailsList.setVisibility(View.GONE);
        if (mTwoPane)
            uiBinding.recipeDetailContainer.setVisibility(View.GONE);
        uiBinding.recipeDetailsListLoadingLayout.loadingLayout.setVisibility(View.GONE);
        uiBinding.recipeDetailsListErrorLayout.errorLayout.setVisibility(View.VISIBLE);
        uiBinding.recipeDetailsListNoElementsLayout.noElementsLayout.setVisibility(View.GONE);
        if (error instanceof NullPointerException)
            uiBinding.recipeDetailsListErrorLayout.tvError.setText(getString(R.string.error_data));
        if (error instanceof IOException)
            uiBinding.recipeDetailsListErrorLayout.tvError.setText(getString(R.string.error_connection));
        else
            uiBinding.recipeDetailsListErrorLayout.tvError.setText(getString(R.string.error_ui));
    }

    /**
     * Show loading data on the activity. If Swipe refresh layout is yet refreshing no actions have
     * to be taken.
     *
     */
    private void showLoading() {
        uiBinding.recipeDetailsList.setVisibility(View.GONE);
        if (mTwoPane)
            uiBinding.recipeDetailContainer.setVisibility(View.GONE);
        uiBinding.recipeDetailsListLoadingLayout.loadingLayout.setVisibility(View.VISIBLE);
        uiBinding.recipeDetailsListErrorLayout.errorLayout.setVisibility(View.GONE);
        uiBinding.recipeDetailsListNoElementsLayout.noElementsLayout.setVisibility(View.GONE);
    }

    @Override
    public void onIngredientsClicked() {
        if (recipeId != null) { // If recipe ID not defined, ignore
            if (mTwoPane) { // If two panel model, load the ingredients fragment in the details panel
                RecipeIngredientsFragment fragment = new RecipeIngredientsFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.recipe_detail_container, fragment).commit();
            } else { // If not, launch an Ingredients activity passing it the ID of the recipe
                Intent intent = new Intent(getApplicationContext(), RecipeDetailInfoActivity.class);
                intent.putExtra(RecipeDetailInfoActivity.ARG_RECIPE_ID, recipeId.intValue());
                startActivity(intent);
            }
        }
    }

    @Override
    public void onStepClicked(Integer stepPositionId) {
        if ((recipeId != null) && stepPositionId != null) { // If recipe ID not defined, ignore
            if (mTwoPane) { // If two panel model, load the ingredients fragment in the details panel
                Bundle arguments = new Bundle();
                arguments.putInt(RecipeStepFragment.ARG_STEP_POSITION_ID, stepPositionId.intValue());
                RecipeStepFragment fragment = new RecipeStepFragment();
                fragment.setArguments(arguments);
                getSupportFragmentManager().beginTransaction().replace(R.id.recipe_detail_container, fragment).commit();
            } else { // If not, launch an Ingredients activity passing it the ID of the recipe
                Intent intent = new Intent(getApplicationContext(), RecipeDetailInfoActivity.class);
                intent.putExtra(RecipeDetailInfoActivity.ARG_RECIPE_ID, recipeId.intValue());
                intent.putExtra(RecipeDetailInfoActivity.ARG_RECIPE_STEP_POSITION_ID, stepPositionId.intValue());
                startActivity(intent);
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
            navigateUpTo(new Intent(this, RecipesListActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
