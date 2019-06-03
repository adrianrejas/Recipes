package org.udacity.android.arejas.recipes.utils.di.builders;

import org.udacity.android.arejas.recipes.presentation.interfaces.services.IngredientsWidgetService;
import org.udacity.android.arejas.recipes.presentation.interfaces.services.RemoteViewListAdapterService;

import dagger.Module;
import dagger.android.ContributesAndroidInjector;

@Module
public abstract class ServiceBuilder {

    @ContributesAndroidInjector
    abstract IngredientsWidgetService bindIngredientsWidgetService();

    @ContributesAndroidInjector
    abstract RemoteViewListAdapterService bindRemoteViewListAdapterService();

}
