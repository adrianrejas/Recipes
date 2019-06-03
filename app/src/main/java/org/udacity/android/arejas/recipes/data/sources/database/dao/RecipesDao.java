package org.udacity.android.arejas.recipes.data.sources.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.paging.DataSource;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Transaction;
import android.arch.persistence.room.Update;

import org.udacity.android.arejas.recipes.data.sources.database.model.RecipeDb;
import org.udacity.android.arejas.recipes.data.sources.database.model.RecipeFullInfoDb;
import org.udacity.android.arejas.recipes.data.sources.database.model.RecipeIngredientDb;
import org.udacity.android.arejas.recipes.data.sources.database.model.RecipeStepDb;

import java.util.List;

@SuppressWarnings("ToArrayCallWithZeroLengthArrayArgument")
@Dao
public abstract class RecipesDao {

    @Query("SELECT * FROM recipes_list")
    public abstract List<RecipeDb> getAll();

    @Query("SELECT * FROM recipes_list")
    public abstract LiveData<List<RecipeDb>> getAllLiveData();

    @Query("SELECT * FROM recipes_list")
    public abstract DataSource.Factory<Integer, RecipeDb> getAllDataSource();

    @SuppressWarnings("SyntaxError")
    @Query("SELECT * FROM recipes_list WHERE rowid = :rowId")
    public abstract RecipeDb getRecipeByRowId(long rowId);

    @Query("SELECT * FROM recipes_list ORDER BY id ASC LIMIT 1")
    public abstract RecipeDb getFirstRecipeBasicInfo();

    @Query("SELECT * FROM recipes_list ORDER BY id ASC LIMIT 1")
    public abstract LiveData<RecipeDb> getFirstRecipeBasicInfoLiveData();

    @Transaction
    @Query("SELECT * FROM recipes_list WHERE id = :recipeId")
    public abstract RecipeDb getRecipeBasicInfo(int recipeId);

    @Query("SELECT * FROM recipes_list WHERE id = :recipeId")
    public abstract LiveData<RecipeDb> getRecipeBasicInfoLiveData(int recipeId);

    @Transaction
    @Query("SELECT * FROM recipes_list WHERE id = :recipeId")
    public abstract RecipeFullInfoDb getRecipeFullInfo(int recipeId);

    @Transaction
    @Query("SELECT * FROM recipes_list WHERE id = :recipeId")
    public abstract LiveData<RecipeFullInfoDb> getRecipeFullInfoLiveData(int recipeId);

    @Query("SELECT id FROM recipes_list WHERE id < :recipeId ORDER BY id DESC LIMIT 1")
    public abstract Integer getPreviousRecipeId(int recipeId);

    @Query("SELECT id FROM recipes_list WHERE id > :recipeId ORDER BY id ASC LIMIT 1")
    public abstract Integer getNextRecipeId(int recipeId);

    @Insert
    public abstract long insert(RecipeDb recipe);

    @Insert
    public abstract long[] insertAll(RecipeDb... recipes);

    @Insert
    public abstract long[] insertIngredients(RecipeIngredientDb... recipeIngredients);

    @Insert
    public abstract long[] insertSteps(RecipeStepDb... recipeSteps);

    @Transaction
    public void insertRecipeFullInfo(RecipeFullInfoDb recipe) {
        long rowId = insert(recipe.recipe);
        RecipeDb recipeDb = getRecipeByRowId(rowId);
        for (RecipeIngredientDb ingredient : recipe.ingredients) {
            ingredient.setRecipeId(recipeDb.getId());
        }
        for (RecipeStepDb step : recipe.steps) {
            step.setRecipeId(recipeDb.getId());
        }
        insertIngredients((RecipeIngredientDb[]) recipe.ingredients.toArray(new RecipeIngredientDb[recipe.ingredients.size()]));
        insertSteps((RecipeStepDb[]) recipe.steps.toArray(new RecipeStepDb[recipe.steps.size()]));
    }

    @Transaction
    public void insertRecipeListFullInfo(RecipeFullInfoDb... recipes) {
        for (RecipeFullInfoDb recipe: recipes) {
            insertRecipeFullInfo(recipe);
        }
    }

    @Delete
    public abstract void delete(RecipeDb recipe);

    @Delete
    public abstract void deteleAll(RecipeDb... recipes);

    @Query("DELETE FROM recipes_list")
    public abstract void deleteWholeDatabase();

    @Update
    public abstract void update(RecipeDb recipe);

}
