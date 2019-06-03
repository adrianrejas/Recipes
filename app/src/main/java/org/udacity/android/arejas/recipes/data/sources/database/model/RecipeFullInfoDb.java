package org.udacity.android.arejas.recipes.data.sources.database.model;

import android.arch.persistence.room.Embedded;
import android.arch.persistence.room.Relation;

import java.util.List;

public class RecipeFullInfoDb {

    @Embedded
    public RecipeDb recipe;

    @Relation(parentColumn = "id", entityColumn = "recipeId", entity = RecipeIngredientDb.class)
    public List<RecipeIngredientDb> ingredients;

    @Relation(parentColumn = "id", entityColumn = "recipeId", entity = RecipeStepDb.class)
    public List<RecipeStepDb> steps;

}
