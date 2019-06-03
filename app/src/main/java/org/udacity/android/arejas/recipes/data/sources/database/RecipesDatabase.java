package org.udacity.android.arejas.recipes.data.sources.database;

import android.arch.persistence.room.Database;
import android.arch.persistence.room.RoomDatabase;

import org.udacity.android.arejas.recipes.data.sources.database.dao.RecipesDao;
import org.udacity.android.arejas.recipes.data.sources.database.dao.RecipesIngredientsDao;
import org.udacity.android.arejas.recipes.data.sources.database.dao.RecipesStepsDao;
import org.udacity.android.arejas.recipes.data.sources.database.model.RecipeDb;
import org.udacity.android.arejas.recipes.data.sources.database.model.RecipeIngredientDb;
import org.udacity.android.arejas.recipes.data.sources.database.model.RecipeStepDb;

@Database(entities = {RecipeDb.class, RecipeIngredientDb.class, RecipeStepDb.class}, version = 1)
public abstract class RecipesDatabase extends RoomDatabase {

    public static final String RECIPES_DB_NAME = "recipe.db";

    public abstract RecipesDao getRecipesDao();

    public abstract RecipesStepsDao getRecipesStepsDao();

    public abstract RecipesIngredientsDao getRecipesIngredientsDao();

}
