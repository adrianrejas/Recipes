package org.udacity.android.arejas.recipes.presentation.ui.fragments;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.udacity.android.arejas.recipes.R;
import org.udacity.android.arejas.recipes.databinding.FragmentRecipeIngredientsBinding;
import org.udacity.android.arejas.recipes.domain.entities.Recipe;
import org.udacity.android.arejas.recipes.domain.entities.RecipeIngredient;
import org.udacity.android.arejas.recipes.presentation.interfaces.viewmodels.RecipeDetailsViewModel;
import org.udacity.android.arejas.recipes.presentation.interfaces.viewmodels.factories.ViewModelFactory;
import org.udacity.android.arejas.recipes.presentation.ui.activities.RecipeDetailsListActivity;
import org.udacity.android.arejas.recipes.presentation.ui.activities.RecipeDetailInfoActivity;
import org.udacity.android.arejas.recipes.presentation.ui.interfaces.NavigationInterface;
import org.udacity.android.arejas.recipes.utils.Utils;
import org.udacity.android.arejas.recipes.utils.entities.Resource;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

/**
 * A fragment representing a single RecipeDb detail screen.
 * This fragment is either contained in a {@link RecipeDetailsListActivity}
 * in two-pane mode (on tablets) or a {@link RecipeDetailInfoActivity}
 * on handsets.
 */
public class RecipeIngredientsFragment extends Fragment implements NavigationInterface {

    @Inject
    public ViewModelFactory viewModelFactory;

    private RecipeDetailsViewModel recipeDetailsViewModel;

    private FragmentRecipeIngredientsBinding uiBinding;

    private final DecimalFormat measureDecimalFormat = new DecimalFormat("#.##");

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RecipeIngredientsFragment() {}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Get the recipe details activity view model and observe the changes in the recipe
        recipeDetailsViewModel = ViewModelProviders.of(
                Objects.requireNonNull(getActivity()), viewModelFactory).get(RecipeDetailsViewModel.class);
        recipeDetailsViewModel.getRecipeDetails().observe(this, new Observer<Resource<Recipe>>() {
            @Override
            public void onChanged(@Nullable Resource<Recipe> recipeResource) {
                if (recipeResource == null) {
                    showError(new NullPointerException("No data was loaded."));
                } else {
                    if (recipeResource.getStatus() == Resource.Status.ERROR) {
                        showError(recipeResource.getError());
                    } else if (recipeResource.getStatus() == Resource.Status.LOADING) {
                        showLoading();
                    } else {
                        updateData(recipeResource.getData());
                    }
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout corresponding to the ingredients
        uiBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_recipe_ingredients,
                container, false);
        // Set navigator listener
        uiBinding.setNavigator(this);
        // Set activity title accordingly JUST IF WE'RE in a one-pane view (in a two-pane view the
        //        // activity decides the title)
        if (getActivity() instanceof RecipeDetailInfoActivity)
            getActivity().setTitle(Utils.getFontedString(getString(R.string.title_recipe_ingredients), R.font.tillana_bold));
        return uiBinding.getRoot();
    }


    /**
     * Update the UI with the recipe got from repository
     *
     * @param recipe Recipe obtained from repository.
     */
    private void updateData(Recipe recipe) {
        if ((recipe != null) && (recipe.getIngredients() != null)) {
            List<String> recipeListString = new ArrayList<>();
            if (!recipe.getIngredients().isEmpty()) {
                for (RecipeIngredient ingredient: recipe.getIngredients()) {
                    String ingredientString = ingredient.getIngredient();
                    if (ingredientString != null) {
                        if (ingredient.getQuantity() != null) {
                            ingredientString = ingredientString.concat(": ")
                                    .concat(measureDecimalFormat.format(ingredient.getQuantity()));
                        }
                        if (ingredient.getMeasure() != null) {
                            ingredientString = ingredientString.concat(" ").concat(ingredient.getMeasure());
                        }
                        recipeListString.add(ingredientString);
                    }
                }
            }
            if (recipeListString.isEmpty()) {
                recipeListString.add(getString(R.string.no_ingredients));
            }
            uiBinding.ingredientsLayout.setVisibility(View.VISIBLE);
            uiBinding.ingredientsLoadingLayout.loadingLayout.setVisibility(View.GONE);
            uiBinding.ingredientsErrorLayout.errorLayout.setVisibility(View.GONE);
            uiBinding.setIngredients(recipeListString);
            uiBinding.setNextDetailId(0);
        } else {
            showError(new NullPointerException("No valid data."));
        }
    }

    /**
     * Show an error on the activity. The idea has been to escalate errors to the UI classes by the
     * exception mechanisms and let these classes decide how to show the error.
     *
     * @param error Exception with the error appeared.
     */
    private void showError(Throwable error) {
        uiBinding.ingredientsLayout.setVisibility(View.GONE);
        uiBinding.ingredientsLoadingLayout.loadingLayout.setVisibility(View.GONE);
        uiBinding.ingredientsErrorLayout.errorLayout.setVisibility(View.VISIBLE);
        if (error instanceof NullPointerException)
            uiBinding.ingredientsErrorLayout.tvError.setText(getString(R.string.error_data));
        if (error instanceof IOException)
            uiBinding.ingredientsErrorLayout.tvError.setText(getString(R.string.error_connection));
        else
            uiBinding.ingredientsErrorLayout.tvError.setText(getString(R.string.error_ui));
    }

    /**
     * Show loading data on the activity. If Swipe refresh layout is yet refreshing no actions have
     * to be taken.
     *
     */
    private void showLoading() {
        uiBinding.ingredientsLayout.setVisibility(View.GONE);
        uiBinding.ingredientsLoadingLayout.loadingLayout.setVisibility(View.VISIBLE);
        uiBinding.ingredientsErrorLayout.errorLayout.setVisibility(View.GONE);
    }

    @Override
    public void onLoadFragment(Integer fragmentId) {
        if (fragmentId != null) {
            uiBinding.getRoot().getParent();
        }
        if (fragmentId == null) {
            RecipeIngredientsFragment fragment = new RecipeIngredientsFragment();
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.recipe_detail_container, fragment).commit();
        } else {
            Bundle arguments = new Bundle();
            arguments.putInt(RecipeStepFragment.ARG_STEP_POSITION_ID, fragmentId.intValue());
            RecipeStepFragment fragment = new RecipeStepFragment();
            fragment.setArguments(arguments);
            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.recipe_detail_container, fragment).commit();
        }
    }

}
