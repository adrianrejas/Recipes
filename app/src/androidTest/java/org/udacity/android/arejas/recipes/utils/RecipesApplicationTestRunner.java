package org.udacity.android.arejas.recipes.utils;

import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.test.runner.AndroidJUnitRunner;

import org.udacity.android.arejas.recipes.mocks.MockRecipesApplication;

/*
 * This class will be used as test runner for using the mock as Application class in order to use
 * dagger for injecting test database and mock use cases
 */
class RecipesApplicationTestRunner extends AndroidJUnitRunner {

    @Override
    public void onCreate(Bundle arguments) {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder().permitAll().build());
        super.onCreate(arguments);
    }

    @Override
    public Application newApplication(ClassLoader cl, String className, Context context)
            throws InstantiationException, IllegalAccessException, ClassNotFoundException {
        return super.newApplication(cl, MockRecipesApplication.class.getName(), context);
    }

}
