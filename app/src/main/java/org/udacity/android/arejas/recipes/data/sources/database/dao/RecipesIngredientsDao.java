package org.udacity.android.arejas.recipes.data.sources.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import org.udacity.android.arejas.recipes.data.sources.database.model.RecipeIngredientDb;

import java.util.List;

@Dao
public abstract class RecipesIngredientsDao {

    @Query("SELECT * FROM recipes_ingredients")
    public abstract List<RecipeIngredientDb> getAll();

    @Query("SELECT * FROM recipes_ingredients")
    public abstract LiveData<List<RecipeIngredientDb>> getAllLiveData();

    @Query("SELECT * FROM recipes_ingredients where recipeId = :recipeId")
    public abstract List<RecipeIngredientDb> getAllFromSameRecipe(Integer recipeId);

    @Query("SELECT * FROM recipes_ingredients where recipeId = :recipeId")
    public abstract LiveData<List<RecipeIngredientDb>> getAllFromSameRecipeLiveData(Integer recipeId);

    @Query("SELECT * FROM recipes_ingredients ORDER BY recipeId ASC LIMIT 1")
    public abstract List<RecipeIngredientDb> getAllFromFirstRecipe();

    @Query("SELECT * FROM recipes_ingredients ORDER BY recipeId ASC LIMIT 1")
    public abstract LiveData<List<RecipeIngredientDb>> getAllFromFirstRecipeLiveData();
    
    @Insert
    public abstract void insert(RecipeIngredientDb recipeIngredient);

    @Insert
    public abstract void insertAll(RecipeIngredientDb... recipeIngredients);

    @Delete
    public abstract void delete(RecipeIngredientDb recipeIngredient);

    @Delete
    public abstract void deteleAll(RecipeIngredientDb... recipeIngredients);

    @Update
    public abstract void update(RecipeIngredientDb recipeIngredient);

}
