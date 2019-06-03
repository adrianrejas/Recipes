package org.udacity.android.arejas.recipes.testcases;


import android.content.Intent;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.IdlingResource;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.v7.widget.Toolbar;
import android.widget.TextView;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.udacity.android.arejas.recipes.R;
import org.udacity.android.arejas.recipes.mocks.MockRecipesApplication;
import org.udacity.android.arejas.recipes.presentation.ui.activities.RecipesListActivity;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.hasDescendant;
import static android.support.test.espresso.matcher.ViewMatchers.isAssignableFrom;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withParent;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class PopulateRecipeListFromNetworkTest {

    @Rule
    public final ActivityTestRule<RecipesListActivity> mActivityTestRule =
            new ActivityTestRule<>(RecipesListActivity.class, false, false);

    private IdlingResource mIdlingResource;

    @Before
    /* Request idling resources from mock application and register them before finishing tests */
    public void registerIdlingResource() {
        mIdlingResource = MockRecipesApplication.getIdlingResource();
        IdlingRegistry.getInstance().register(mIdlingResource);
    }

    @Before
    /* Make sure the test database is empty before finishing tests */
    public void populateTestDatabase() {
        MockRecipesApplication.getRecipesDatabase().getRecipesDao().deleteWholeDatabase();
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
    public void populateRecipeListFromNetworkTest() {
        // Launch activity manually at start of the test (to make sure the database has been cleared)
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
        // Check the alert dialog is shown
        onView(withText(mActivityTestRule.getActivity().getString(R.string.no_recipes_title))).check(matches(isDisplayed()));
        // Perform a click on the alert dialog OK button
        onView(withId(android.R.id.button1)).perform(click());
        // Wait 2 seconds for getting recipes from network
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        // Check at least a recipe card has been shown
        onView(withId(R.id.recipes_list)).check(matches(hasDescendant(withId(R.id.cv_recipe_card))));
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
