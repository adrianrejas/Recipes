package org.udacity.android.arejas.recipes.mocks;

import android.arch.persistence.room.Room;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.udacity.android.arejas.recipes.RecipesApplication;
import org.udacity.android.arejas.recipes.data.sources.database.RecipesDatabase;
import org.udacity.android.arejas.recipes.mocks.di.MockAppModule;
import org.udacity.android.arejas.recipes.utils.SimpleIdlingResource;
import org.udacity.android.arejas.recipes.utils.di.components.DaggerAppComponent;

/*
 * Mock applicacion class used for injecting mock database and use cases.
*/
public class MockRecipesApplication extends RecipesApplication {

    private static final String RECIPES_DB_NAME = "recipe_test.db";

    // The Idling Resource which will be used.
    @Nullable
    private static SimpleIdlingResource mIdlingResource;

    // The test database (different from the production one)
    private static RecipesDatabase mRecipesDatabase;

    @NonNull
    public static SimpleIdlingResource getIdlingResource() {
        if (mIdlingResource == null) {
            mIdlingResource = new SimpleIdlingResource();
        }
        return mIdlingResource;
    }

    @NonNull
    public static RecipesDatabase getRecipesDatabase() {
        if (mRecipesDatabase == null) {
            mRecipesDatabase = Room.databaseBuilder(application,
                    RecipesDatabase .class, RECIPES_DB_NAME)
                    .build();
        }
        return mRecipesDatabase;
    }

    @Override
    public void initDagger(){
        DaggerAppComponent.builder()
                .application(this)
                .appModule(new MockAppModule())
                .build()
                .inject(this);
    }
}
