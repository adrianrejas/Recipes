package org.udacity.android.arejas.recipes.testcases;


import android.content.Intent;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

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
import org.udacity.android.arejas.recipes.presentation.ui.activities.RecipesListActivity;
import org.udacity.android.arejas.recipes.utils.RecyclerViewAssertions;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.pressBack;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.isRoot;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.widget.TextView;
import  android.support.v7.widget.Toolbar;

import java.util.ArrayList;
import java.util.List;

import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class RecipeListStoredAtDbTest {

    @Rule
    public final ActivityTestRule<RecipesListActivity> mActivityTestRule =
            new ActivityTestRule<>(RecipesListActivity.class, false, false);

    private IdlingResource mIdlingResource;

    private List<RecipeFullInfoDb> recipes;

    @Before
    /* Request idling resources from mock application and register them before finishing tests */
    public void registerIdlingResource() {
        mIdlingResource = MockRecipesApplication.getIdlingResource();
        IdlingRegistry.getInstance().register(mIdlingResource);
    }

    @Before
    /* Make sure the test database is populated with recipes before finishing tests. We save as a field
     * the data populated in order to be used for assertions during test */
    public void populateTestDatabase() {
        MockRecipesApplication.getRecipesDatabase().getRecipesDao().deleteWholeDatabase();
        RecipeFullInfoDb recipe1 = new RecipeFullInfoDb();
        recipe1.recipe = new RecipeDb(1, "Brownies", 4, "http://recetasdecocina.elmundo.es/wp-content/uploads/2016/11/brownie-de-chocolate.jpg");
        recipe1.ingredients = new ArrayList<>();
        recipe1.ingredients.add(new RecipeIngredientDb(150.0f, "G", "Chocolate"));
        recipe1.ingredients.add(new RecipeIngredientDb(4f, "Units", "Eggs"));
        recipe1.ingredients.add(new RecipeIngredientDb(3f, "TLBPS", "Oil and sugar"));
        recipe1.steps = new ArrayList<>();
        recipe1.steps.add(new RecipeStepDb(1, "Introduction", "Easy recipe", "https://d17h27t6h515a5.cloudfront.net/topher/2017/April/58ffdc33_-intro-brownies/-intro-brownies.mp4", "http://recetasdecocina.elmundo.es/wp-content/uploads/2016/11/brownie-de-chocolate.jpg"));
        recipe1.steps.add(new RecipeStepDb(2, "Prepare recipe", "Prepare recipe by getting the ingredients", "", ""));
        recipe1.steps.add(new RecipeStepDb(3, "Cook recipe", "Cook recipe in the kitchen", "", "http://recetasdecocina.elmundo.es/wp-content/uploads/2016/11/brownie-de-chocolate.jpg"));
        recipe1.steps.add(new RecipeStepDb(4, "Serve recipe", "Enjoy the brownies with your friends", "https://d17h27t6h515a5.cloudfront.net/topher/2017/April/58ffdc33_-intro-brownies/-intro-brownies.mp4", ""));
        recipe1.steps.add(new RecipeStepDb(5, "Time To Eat", "Let's go to eat!", "", "notvalid"));
        MockRecipesApplication.getRecipesDatabase().getRecipesDao().insertRecipeFullInfo(recipe1);
        RecipeFullInfoDb recipe2 = new RecipeFullInfoDb();
        recipe2.recipe = new RecipeDb(2, "Cheesecake", 4, "https://www.pequerecetas.com/wp-content/uploads/2014/09/tarta-de-queso.jpg");
        recipe2.ingredients = new ArrayList<>();
        recipe2.ingredients.add(new RecipeIngredientDb(150.0f, "G", "Cheese"));
        recipe2.ingredients.add(new RecipeIngredientDb(4f, "Units", "Eggs"));
        recipe2.ingredients.add(new RecipeIngredientDb(3f, "TLBPS", "Oil and sugar"));
        recipe2.steps = new ArrayList<>();
        recipe2.steps.add(new RecipeStepDb(1, "Introduction", "Easy recipe", "https://d17h27t6h515a5.cloudfront.net/topher/2017/April/58ffdae8_-intro-cheesecake/-intro-cheesecake.mp4", "https://www.pequerecetas.com/wp-content/uploads/2014/09/tarta-de-queso.jpg"));
        recipe2.steps.add(new RecipeStepDb(2, "Prepare recipe", "Prepare recipe by getting the ingredients", "", ""));
        recipe2.steps.add(new RecipeStepDb(3, "Cook recipe", "Cook recipe in the kitchen", "", "https://www.pequerecetas.com/wp-content/uploads/2014/09/tarta-de-queso.jpg"));
        recipe2.steps.add(new RecipeStepDb(4, "Serve recipe", "Enjoy the cheesecake with your friends", "https://d17h27t6h515a5.cloudfront.net/topher/2017/April/58ffdae8_-intro-cheesecake/-intro-cheesecake.mp4", ""));
        recipe2.steps.add(new RecipeStepDb(5, "Time To Eat", "Let's go to eat!", "", "notvalid"));
        MockRecipesApplication.getRecipesDatabase().getRecipesDao().insertRecipeFullInfo(recipe2);
        RecipeFullInfoDb recipe3 = new RecipeFullInfoDb();
        recipe3.recipe = new RecipeDb(3, "Yellow cake", 4, "");
        recipe3.ingredients = new ArrayList<>();
        recipe3.ingredients.add(new RecipeIngredientDb(150.0f, "G", "Chocolate"));
        recipe3.ingredients.add(new RecipeIngredientDb(4f, "Units", "Eggs"));
        recipe3.ingredients.add(new RecipeIngredientDb(3f, "TLBPS", "Oil and sugar"));
        recipe3.steps = new ArrayList<>();
        recipe3.steps.add(new RecipeStepDb(1, "Introduction", "Easy recipe", "https://d17h27t6h515a5.cloudfront.net/topher/2017/April/58ffddf0_-intro-yellow-cake/-intro-yellow-cake.mp4", "https://hips.hearstapps.com/hmg-prod.s3.amazonaws.com/images/190404-yellow-cake-horizontal-5-1555423519.png"));
        recipe3.steps.add(new RecipeStepDb(2, "Prepare recipe", "Prepare recipe by getting the ingredients", "", ""));
        recipe3.steps.add(new RecipeStepDb(3, "Cook recipe", "Cook recipe in the kitchen", "", "https://hips.hearstapps.com/hmg-prod.s3.amazonaws.com/images/190404-yellow-cake-horizontal-5-1555423519.png"));
        recipe3.steps.add(new RecipeStepDb(4, "Serve recipe", "Enjoy the Yellow cake with your friends", "https://d17h27t6h515a5.cloudfront.net/topher/2017/April/58ffddf0_-intro-yellow-cake/-intro-yellow-cake.mp4", ""));
        recipe3.steps.add(new RecipeStepDb(5, "Time To Eat", "Let's go to eat!", "", "notvalid"));
        MockRecipesApplication.getRecipesDatabase().getRecipesDao().insertRecipeFullInfo(recipe3);
        recipes = new ArrayList<>();
        recipes.add(recipe1);
        recipes.add(recipe2);
        recipes.add(recipe3);
    }

    @Test
    /*
     * This test will get a populated database, check that the appropiate number of elements is shown
     * and check that navigation between recipe list activity and recipe details activity works in a
     * suitable way.
     */
    /*
     * During the test there are som sleeps to wait for actions (not necessary because of idle resources
     * are managed, but with this sleep the test can be followed by sight).
     */
    public void recipesListActivityTest() {
        // Launch activity manually at start of the test (to make sure the database has been populated)
        mActivityTestRule.launchActivity(new Intent());
        // Wait 2 seconds for the activity to be started
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Check proper action bar title is correct
        onView(allOf(isAssignableFrom(TextView.class), withParent(isAssignableFrom(Toolbar.class))))
                .check(matches(withText(mActivityTestRule.getActivity().getString(R.string.title_recipes_list))));
        // Check the recipes list exists and has the expected number of elements
        onView(withId(R.id.recipes_list)).check(RecyclerViewAssertions.hasItemsCount(recipes.size()));
        // For each recipe
        for (int i = 0; i < recipes.size(); i++) {
            // Scroll to recipe card
            onView(withId(R.id.recipes_list)).perform(RecyclerViewActions.scrollToPosition(i));
            // Check recipe card name is correct
            onView(withId(R.id.recipes_list))
                    .check(RecyclerViewAssertions.hasViewWithTextAtPosition(i, recipes.get(i).recipe.getName()));
            // Click on recipe card
            onView(withId(R.id.recipes_list)).perform(RecyclerViewActions.actionOnItemAtPosition(i, click()));
            // Wait until new activity opened
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // Check activity for recipe details has been opened by checking the action bar title
            onView(allOf(isAssignableFrom(TextView.class), withParent(isAssignableFrom(Toolbar.class))))
                    .check(matches(withText(recipes.get(i).recipe.getName())));
            // And checking a view of recipe details list is being displayed
            onView(withId(R.id.recipe_details_list)).check(matches(isDisplayed()));
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
                    .check(matches(withText(mActivityTestRule.getActivity().getString(R.string.title_recipes_list))));
        }
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
