package org.udacity.android.arejas.recipes.testcases;


import android.content.Intent;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import com.google.android.exoplayer2.ui.PlaybackControlView;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.udacity.android.arejas.recipes.R;
import org.udacity.android.arejas.recipes.data.sources.database.model.RecipeDb;
import org.udacity.android.arejas.recipes.data.sources.database.model.RecipeFullInfoDb;
import org.udacity.android.arejas.recipes.data.sources.database.model.RecipeIngredientDb;
import org.udacity.android.arejas.recipes.data.sources.database.model.RecipeStepDb;
import org.udacity.android.arejas.recipes.mocks.MockRecipesApplication;
import org.udacity.android.arejas.recipes.presentation.ui.activities.RecipeDetailsListActivity;
import org.udacity.android.arejas.recipes.utils.RecyclerViewAssertions;

import java.util.ArrayList;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.not;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class RecipeDetailsTestPhoneDevicesPortrait {

    @Rule
    public final ActivityTestRule<RecipeDetailsListActivity> mActivityTestRule =
            new ActivityTestRule<>(RecipeDetailsListActivity.class, false, false);

    private IdlingResource mIdlingResource;

    private RecipeFullInfoDb recipe;

    @Before
    /* Request idling resources from mock application and register them before finishing tests */
    public void registerIdlingResource() {
        mIdlingResource = MockRecipesApplication.getIdlingResource();
        IdlingRegistry.getInstance().register(mIdlingResource);
    }

    @Before
    /* Make sure the test database is populated with recipes before finishing tests */
    public void populateTestDatabase() {
        MockRecipesApplication.getRecipesDatabase().getRecipesDao().deleteWholeDatabase();
        recipe = new RecipeFullInfoDb();
        recipe.recipe = new RecipeDb(1, "Brownies", 4, "http://recetasdecocina.elmundo.es/wp-content/uploads/2016/11/brownie-de-chocolate.jpg");
        recipe.ingredients = new ArrayList<>();
        recipe.ingredients.add(new RecipeIngredientDb(150.0f, "G", "Chocolate"));
        recipe.ingredients.add(new RecipeIngredientDb(4f, "Units", "Eggs"));
        recipe.ingredients.add(new RecipeIngredientDb(3f, "TLBPS", "Oil and sugar"));
        recipe.steps = new ArrayList<>();
        recipe.steps.add(new RecipeStepDb(1, "Introduction", "Easy recipe", "https://d17h27t6h515a5.cloudfront.net/topher/2017/April/58ffdc33_-intro-brownies/-intro-brownies.mp4", "http://recetasdecocina.elmundo.es/wp-content/uploads/2016/11/brownie-de-chocolate.jpg"));
        recipe.steps.add(new RecipeStepDb(2, "Prepare recipe", "Prepare recipe by getting the ingredients", "", ""));
        recipe.steps.add(new RecipeStepDb(3, "Cook recipe", "Cook recipe in the kitchen", "", "http://recetasdecocina.elmundo.es/wp-content/uploads/2016/11/brownie-de-chocolate.jpg"));
        recipe.steps.add(new RecipeStepDb(4, "Serve recipe", "Enjoy the brownies with your friends", "https://d17h27t6h515a5.cloudfront.net/topher/2017/April/58ffdc33_-intro-brownies/-intro-brownies.mp4", ""));
        recipe.steps.add(new RecipeStepDb(5, "Time To Eat", "Let's go to eat!", "", "notvalid"));
        MockRecipesApplication.getRecipesDatabase().getRecipesDao().insertRecipeFullInfo(recipe);
    }

    @Test
    /*
     * This test will get an empty database, check that an alert dialog is shown (reporting the emptyness
     * of the database and offering to populate it from web), check that the alert dialog can be accepted
     * and check that if the alert dialog is accepted the recipes list is populated with at least one item.
     */
    /*
     * During the test there are som sleeps to wait for actions (not necessary because of idle resources
     * are managed, but with this sleep the test can be followed by sight).
     */
    public void recipesListActivityTest() {
        // Work out recipe details number and if there are ingredients to show
        boolean thereAreIngredients = ((recipe.ingredients != null) && (!recipe.ingredients.isEmpty()));
        int numberRecipeDetails = recipe.steps.size() + (thereAreIngredients ? 1 : 0);
        // Launch activity manually at start of the test (to make sure the database has been populated)
        Intent launchIntent = new Intent();
        launchIntent.putExtra(RecipeDetailsListActivity.ARG_RECIPE_ID, 1);
        mActivityTestRule.launchActivity(launchIntent);
        // Wait 2 seconds for the activity to be started
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Check proper action bar title is correct
        onView(allOf(isAssignableFrom(TextView.class), withParent(isAssignableFrom(Toolbar.class))))
                .check(matches(withText(recipe.recipe.getName())));
        // Check the recipes details list exists and has the expected number of elements
        onView(withId(R.id.recipe_details_list)).check(RecyclerViewAssertions.hasItemsCount(numberRecipeDetails));
        // For each recipe detail
        for (int i = 0; i < numberRecipeDetails; i++) {
            // Work out recipe detail title, step position and if it's ingredients detail or step detail
            boolean isIngredientsDetail = (i == 0) && thereAreIngredients;
            int stepPosition = thereAreIngredients ? (i-1) : i;
            String recipeDetailTitle = isIngredientsDetail ?
                    mActivityTestRule.getActivity().getString(R.string.ingredients_list) :
                    recipe.steps.get(stepPosition).getShortDescription();
            // Scroll to recipe detail
            onView(withId(R.id.recipe_details_list)).perform(RecyclerViewActions.scrollToPosition(i));
            // Check recipe detail name is correct
            onView(withId(R.id.recipe_details_list))
                    .check(RecyclerViewAssertions.hasViewWithTextAtPosition(i, recipeDetailTitle));
            // Click on recipe detail
            onView(withId(R.id.recipe_details_list)).perform(RecyclerViewActions.actionOnItemAtPosition(i, click()));
            // Wait until new activity opened
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Check activity for recipe detail info has been opened depending on if a step or and igredients
            // list has been openeted
            if (isIngredientsDetail) {
                // Check the action bar title
                onView(allOf(isAssignableFrom(TextView.class), withParent(isAssignableFrom(Toolbar.class))))
                        .check(matches(withText(mActivityTestRule.getActivity().getResources().getString(R.string.title_recipe_ingredients))));
                // Check the displaying of ingredients title, list and next button
                onView(withId(R.id.tv_ingredients_title)).check(matches(isDisplayed()));
                onView(withId(R.id.tv_ingredients_listed)).check(matches(isDisplayed()));
                if ((recipe.steps != null) && (!recipe.steps.isEmpty()))
                    onView(withId(R.id.bt_ingredients_next)).check(matches(isDisplayed()));
            } else {
                RecipeStepDb recipeStep = recipe.steps.get(stepPosition);
                // Check the displaying of the player or it's thumbnail or it's no preview view
                // (the elements we're sure it can appear no matter we're in portrait or landscape)
                if ((recipeStep.getVideoURL() != null) && (!recipeStep.getVideoURL().isEmpty())) {
                    onView(allOf(isAssignableFrom(PlaybackControlView.class), withParent(withId(R.id.step_player))))
                            .check(matches(isDisplayed()));
                } else if ((recipeStep.getThumbnailURL() != null) && (!recipeStep.getThumbnailURL().isEmpty())) {
                    onView(withId(R.id.step_thumbnail)).check(matches(isDisplayed()));
                } else {
                    onView(withId(R.id.step_nopreview)).check(matches(isDisplayed()));
                }
            }
            // press back key for returning to recipe list
            onView(isRoot()).perform(pressBack());
            // Wait until came back to old activity
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Check we're back on recipes list activity by checking action bar
            onView(allOf(isAssignableFrom(TextView.class), withParent(isAssignableFrom(Toolbar.class))))
                    .check(matches(withText(recipe.recipe.getName())));
        }
        // Scroll to first recipe detail
        onView(withId(R.id.recipe_details_list)).perform(RecyclerViewActions.scrollToPosition(0));
        // Click on first recipe detail
        onView(withId(R.id.recipe_details_list)).perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));
        // For each recipe detail
        for (int i = 0; i < numberRecipeDetails; i++) {
            // Work out recipe detail title, step position and if it's ingredients detail or step detail
            boolean isIngredientsDetail = (i == 0) && thereAreIngredients;
            int stepPosition = thereAreIngredients ? (i-1) : i;
            // Wait until new fragment is loaded
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Check fragment for recipe detail info has been opened depending on if a step or and igredients
            // list has been opened and depending on fragment chosen, press the suitable next button
            if (isIngredientsDetail) {
                // Click on next button if there are next details
                if (i < (numberRecipeDetails - 1))
                    onView(withId(R.id.bt_ingredients_next)).perform(click());
                else
                    onView(withId(R.id.bt_ingredients_next)).check(matches(not(isDisplayed())));
            } else {
                // Click on next button if there are next details
                if (i < (numberRecipeDetails - 1))
                    onView(withId(R.id.bt_step_next)).perform(click());
                else
                    onView(withId(R.id.bt_step_next)).check(matches(not(isDisplayed())));
            }
        }
        // press back key for returning to recipe list
        onView(isRoot()).perform(pressBack());
        // Wait until came back to old activity
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Check we're back on recipes list activity by checking action bar
        onView(allOf(isAssignableFrom(TextView.class), withParent(isAssignableFrom(Toolbar.class))))
                .check(matches(withText(recipe.recipe.getName())));
        // Scroll to last recipe detail
        onView(withId(R.id.recipe_details_list)).perform(RecyclerViewActions.scrollToPosition(numberRecipeDetails - 1));
        // Click on last recipe detail
        onView(withId(R.id.recipe_details_list)).perform(RecyclerViewActions.actionOnItemAtPosition(numberRecipeDetails - 1, click()));
        // For each recipe detail
        for (int i = (numberRecipeDetails -1); i >= 0; i--) {
            // Work out recipe detail title, step position and if it's ingredients detail or step detail
            boolean isIngredientsDetail = (i == 0) && thereAreIngredients;
            int stepPosition = thereAreIngredients ? (i-1) : i;
            // Wait until new fragment is loaded
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Check fragment for recipe step has been opened and press the suitable previous button
            if (isIngredientsDetail) {
                // Check the displaying of ingredients title for knowing we are in ingredients
                onView(withId(R.id.tv_ingredients_title)).check(matches(isDisplayed()));
            } else {
                // Click on previous button if there are previous details
                if (i > 0)
                    onView(withId(R.id.bt_step_previous)).perform(click());
            }
        }
        // press back key for returning to recipe list
        onView(isRoot()).perform(pressBack());
        // Wait until came back to old activity
        try {
            Thread.sleep(500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Check we're back on recipes list activity by checking action bar
        onView(allOf(isAssignableFrom(TextView.class), withParent(isAssignableFrom(Toolbar.class))))
                .check(matches(withText(recipe.recipe.getName())));
    }

    @After
    /* Empty database after finishing tests */
    public void emptyTestDatabase() {
        MockRecipesApplication.getRecipesDatabase().getRecipesDao().deleteWholeDatabase();
    }

    @After
    /* Unregister used idling resources after finishing tests */
    public void unregisterIdlingResource() {
        if (mIdlingResource != null) {
            IdlingRegistry.getInstance().unregister(mIdlingResource);
        }
    }

}
