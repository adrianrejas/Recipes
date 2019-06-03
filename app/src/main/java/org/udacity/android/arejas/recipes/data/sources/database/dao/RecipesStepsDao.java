package org.udacity.android.arejas.recipes.data.sources.database.dao;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import org.udacity.android.arejas.recipes.data.sources.database.model.RecipeStepDb;

import java.util.List;

@Dao
public abstract class RecipesStepsDao {

    @Query("SELECT * FROM recipes_steps")
    public abstract List<RecipeStepDb> getAll();

    @Query("SELECT * FROM recipes_steps")
    public abstract LiveData<List<RecipeStepDb>> getAllLiveData();

    @Query("SELECT * FROM recipes_steps where recipeId = :recipeId")
    public abstract List<RecipeStepDb> getAllFromSameRecipe(Integer recipeId);

    @Query("SELECT * FROM recipes_steps where recipeId = :recipeId")
    public abstract LiveData<List<RecipeStepDb>> getAllFromSameRecipeLiveData(Integer recipeId);
    
    @Insert
    public abstract void insert(RecipeStepDb recipeStep);

    @Insert
    public abstract void insertAll(RecipeStepDb... recipeSteps);

    @Delete
    public abstract void delete(RecipeStepDb recipeStep);

    @Delete
    public abstract void deteleAll(RecipeStepDb... recipeSteps);

    @Update
    public abstract void update(RecipeStepDb recipeStep);

}
