package org.udacity.android.arejas.recipes;

import android.app.Activity;
import android.app.Application;
import android.app.Service;
import android.support.v7.app.AppCompatDelegate;

import org.udacity.android.arejas.recipes.utils.di.components.DaggerAppComponent;

import javax.inject.Inject;

import dagger.android.DispatchingAndroidInjector;
import dagger.android.HasActivityInjector;
import dagger.android.HasServiceInjector;

public class RecipesApplication extends Application implements HasActivityInjector, HasServiceInjector {

    public static Application application;

    public static Application getApplication() {
        return application;
    }

    @Inject
    public DispatchingAndroidInjector<Activity> dispatchingActivityInjector;

    @Inject
    public DispatchingAndroidInjector<Service> dispatchingServiceInjector;

    @Override
    public void onCreate() {
        super.onCreate();
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);
        application = this;
        this.initDagger();
    }

    protected void initDagger(){
        DaggerAppComponent.builder()
                .application(this)
                .build()
                .inject(this);
    }

    @Override
    public DispatchingAndroidInjector<Activity> activityInjector() {
        return dispatchingActivityInjector;
    }

    @Override
    public DispatchingAndroidInjector<Service> serviceInjector() {
        return dispatchingServiceInjector;
    }

}
