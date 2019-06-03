package org.udacity.android.arejas.recipes.presentation.ui.activities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModelProviders;
import android.arch.paging.PagedList;
import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.view.View;

import org.udacity.android.arejas.recipes.R;
import org.udacity.android.arejas.recipes.databinding.ActivityRecipesListBinding;
import org.udacity.android.arejas.recipes.domain.entities.RecipeListItem;
import org.udacity.android.arejas.recipes.presentation.interfaces.viewmodels.RecipesListViewModel;
import org.udacity.android.arejas.recipes.presentation.interfaces.viewmodels.factories.ViewModelFactory;
import org.udacity.android.arejas.recipes.presentation.ui.adapters.RecipesListAdapter;
import org.udacity.android.arejas.recipes.utils.Utils;
import org.udacity.android.arejas.recipes.utils.entities.Resource;

import java.io.IOException;

import javax.inject.Inject;

import dagger.android.AndroidInjection;

public class RecipesListActivity extends AppCompatActivity {

    private static final String LOADFROMNETWORK_PENDING_SHOW_KEY = "LOADFROMNETWORK_PENDING_SHOW_KEY";

    @Inject
    public ViewModelFactory viewModelFactory;

    private RecipesListViewModel recipesListViewModel;
    private ActivityRecipesListBinding uiBinding;
    private RecipesListAdapter mAdapter;

    private LiveData<Resource<PagedList<RecipeListItem>>> resourceBeingShown;

    private boolean bLoadFromNetworkDialogPendingToShow = true;
    private AlertDialog noRecipesDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        /* Inflate main layout and get UI element references */
        uiBinding = DataBindingUtil.setContentView(this, R.layout.activity_recipes_list);

        // Set title according to the font required
        setTitle(Utils.getFontedString(getString(R.string.title_recipes_list), R.font.tillana_bold));

        // Dependency injection
        AndroidInjection.inject(this);

        // Request viewmodel
        recipesListViewModel = ViewModelProviders.of(this, this.viewModelFactory).get(RecipesListViewModel.class);

        // Get the status aboit if the load from network noRecipesDialog has been shown in order
        // not to show for the rest of the live of the activity
        if ((savedInstanceState != null) && (savedInstanceState.containsKey(LOADFROMNETWORK_PENDING_SHOW_KEY)))
            bLoadFromNetworkDialogPendingToShow = savedInstanceState.getBoolean(LOADFROMNETWORK_PENDING_SHOW_KEY);

        // Configure adapter for recycler view
        configureListAdapter();

        // Set action of refreshing list when refreshing gesture detected
        uiBinding.srlRefreshLayout.setOnRefreshListener(() -> loadRecipesList(true));

        // Load recipe list
        loadRecipesList(false);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (noRecipesDialog != null) {
            try {
                noRecipesDialog.dismiss();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(LOADFROMNETWORK_PENDING_SHOW_KEY, bLoadFromNetworkDialogPendingToShow);
    }

    /**
     * Function for configure data list adapter
     */
    private void configureListAdapter() {

        /* Get number of items in a row*/
        int iElementsPerRow = getResources().getInteger(R.integer.recipes_list_items_column);

        // Configure recycler view with a grid layout
        GridLayoutManager glm_grid = new GridLayoutManager(this, iElementsPerRow);
        uiBinding.recipesList.setLayoutManager(glm_grid);

        // Configure adapter for recycler view
         mAdapter = new RecipesListAdapter(mItemClickListener);
        uiBinding.recipesList.setAdapter(mAdapter);
    }

    /**
     * Function called for setting a new movie list, requesting it to the REST API
     *
     * @param reload true if wanted to reload the data, false if it's enough with cached data.
     *
     */
    private void loadRecipesList(boolean reload) {
        if (resourceBeingShown != null) {
            resourceBeingShown.removeObservers(this);
            resourceBeingShown = null;
        }
        resourceBeingShown = recipesListViewModel.getRecipesList(reload);
        if (resourceBeingShown != null) {
            resourceBeingShown.observe(this, pagedListResource -> {
                if (pagedListResource == null) {
                    showError(new NullPointerException("No data was loaded."));
                } else {
                    if (pagedListResource.getStatus() == Resource.Status.ERROR) {
                        showError(pagedListResource.getError());
                    } else if (pagedListResource.getStatus() == Resource.Status.LOADING) {
                        showLoading();
                    } else {
                        updateList(pagedListResource.getData());
                    }
                }
            });
        }
    }

    /**
     * Updates the list used by the activity. We set the list in the list adapter and notify it.
     * We cancel both loading more and swipe to refresh refreshing methods.
     *
     * @param newList New list to set
     */
    private void updateList(PagedList<RecipeListItem> newList) {
        if ((newList != null) && (!newList.isEmpty())) {
            uiBinding.recipesList.setVisibility(View.VISIBLE);
            uiBinding.recipesListLoadingLayout.loadingLayout.setVisibility(View.GONE);
            uiBinding.recipesListErrorLayout.errorLayout.setVisibility(View.GONE);
            uiBinding.recipesListNoElementsLayout.noElementsLayout.setVisibility(View.GONE);
            mAdapter.submitList(newList);
        } else {
            uiBinding.recipesList.setVisibility(View.GONE);
            uiBinding.recipesListLoadingLayout.loadingLayout.setVisibility(View.GONE);
            uiBinding.recipesListErrorLayout.errorLayout.setVisibility(View.GONE);
            uiBinding.recipesListNoElementsLayout.noElementsLayout.setVisibility(View.VISIBLE);
            if (bLoadFromNetworkDialogPendingToShow) {
                requestLoadFromNetworkDialog();
            }
        }
        uiBinding.srlRefreshLayout.setRefreshing(false);
    }

    private void requestLoadFromNetworkDialog () {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(Utils.getFontedString(getString(R.string.request_default_recipes), R.font.tillana))
                    .setTitle(Utils.getFontedString(getString(R.string.no_recipes_title), R.font.tillana_bold))
                    .setPositiveButton(Utils.getFontedString(getString(R.string.yes), R.font.tillana_bold),
                            (dialog, which) -> {
                                String url = getResources().getString(R.string.base_url_default_recipes);
                                String file = getResources().getString(R.string.file_url_default_recipes);
                                recipesListViewModel.loadRecipesFromNetworkUrl(url, file);
                                bLoadFromNetworkDialogPendingToShow = false;
                                dialog.cancel();
                            })
                    .setNegativeButton(Utils.getFontedString(getString(R.string.no), R.font.tillana_bold),
                            (dialog, which) -> {
                                bLoadFromNetworkDialogPendingToShow = false;
                                dialog.cancel();
                            });
            noRecipesDialog = builder.create();
            noRecipesDialog.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Show an error on the activity. The idea has been to escalate errors to the UI classes by the
     * exception mechanisms and let these classes decide how to show the error.
     *
     * @param error Exception with the error appeared.
     */
    private void showError(Throwable error) {
        uiBinding.recipesList.setVisibility(View.GONE);
        uiBinding.recipesListLoadingLayout.loadingLayout.setVisibility(View.GONE);
        uiBinding.recipesListErrorLayout.errorLayout.setVisibility(View.VISIBLE);
        uiBinding.recipesListNoElementsLayout.noElementsLayout.setVisibility(View.GONE);
        if (error instanceof NullPointerException)
            uiBinding.recipesListErrorLayout.tvError.setText(getString(R.string.error_data));
        if (error instanceof IOException)
            uiBinding.recipesListErrorLayout.tvError.setText(getString(R.string.error_connection));
        else
            uiBinding.recipesListErrorLayout.tvError.setText(getString(R.string.error_ui));
        uiBinding.srlRefreshLayout.setRefreshing(false);
    }

    /**
     * Show loading data on the activity. If Swipe refresh layout is yet refreshing no actions have
     * to be taken.
     *
     */
    private void showLoading() {
        if (!uiBinding.srlRefreshLayout.isRefreshing()) {
            uiBinding.recipesList.setVisibility(View.GONE);
            uiBinding.recipesListLoadingLayout.loadingLayout.setVisibility(View.VISIBLE);
            uiBinding.recipesListErrorLayout.errorLayout.setVisibility(View.GONE);
            uiBinding.recipesListNoElementsLayout.noElementsLayout.setVisibility(View.GONE);
        }
    }

    /**
     * Listener called when recipe list item is clicked. Now we open the details activity for the
     * recipe by passing it's ID.
     */
    private final RecipesListAdapter.OnRecipesListItemClickListener mItemClickListener =
            recipeId -> {
                Intent recipeIntent = new Intent(getApplicationContext(),
                        RecipeDetailsListActivity.class);
                if (recipeId != null)
                    recipeIntent.putExtra(RecipeDetailsListActivity.ARG_RECIPE_ID, recipeId.intValue());
                startActivity(recipeIntent);
            };
}
