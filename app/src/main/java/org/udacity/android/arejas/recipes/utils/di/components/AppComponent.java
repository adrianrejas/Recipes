package org.udacity.android.arejas.recipes.utils.di.components;

import org.udacity.android.arejas.recipes.RecipesApplication;
import org.udacity.android.arejas.recipes.utils.di.builders.ActivityBuilder;
import org.udacity.android.arejas.recipes.utils.di.builders.ServiceBuilder;
import org.udacity.android.arejas.recipes.utils.di.modules.AppModule;

import javax.inject.Singleton;

import dagger.BindsInstance;
import dagger.Component;
import dagger.android.AndroidInjectionModule;
import dagger.android.AndroidInjector;

@Singleton
@Component (modules={AndroidInjectionModule.class, AppModule.class, ActivityBuilder.class, ServiceBuilder.class})
public interface AppComponent extends AndroidInjector<RecipesApplication> {

    @Component.Builder
    interface Builder {

        @BindsInstance
        Builder application(RecipesApplication application);

        Builder appModule(AppModule module);

        AppComponent build();

    }

    void inject(RecipesApplication app);

}
