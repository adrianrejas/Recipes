package org.udacity.android.arejas.recipes.data.interfaces.repositories.mappers;

import org.udacity.android.arejas.recipes.data.sources.database.model.RecipeDb;
import org.udacity.android.arejas.recipes.data.sources.database.model.RecipeFullInfoDb;
import org.udacity.android.arejas.recipes.data.sources.database.model.RecipeIngredientDb;
import org.udacity.android.arejas.recipes.data.sources.database.model.RecipeStepDb;
import org.udacity.android.arejas.recipes.data.sources.network.model.RecipeIngredientNet;
import org.udacity.android.arejas.recipes.data.sources.network.model.RecipeNet;
import org.udacity.android.arejas.recipes.data.sources.network.model.RecipeStepNet;
import org.udacity.android.arejas.recipes.domain.entities.Recipe;
import org.udacity.android.arejas.recipes.domain.entities.RecipeIngredient;
import org.udacity.android.arejas.recipes.domain.entities.RecipeListItem;
import org.udacity.android.arejas.recipes.domain.entities.RecipeStep;

import java.util.ArrayList;
import java.util.List;

public class RecipesRepositoryMapper {

    public static RecipeListItem mapRecipeListItemDatabaseToDomain(RecipeDb recipedb) {
        RecipeListItem resultRecipe = new RecipeListItem();
        resultRecipe.setId(recipedb.getId());
        resultRecipe.setImage(recipedb.getImage());
        resultRecipe.setName(recipedb.getName());
        resultRecipe.setServings(recipedb.getServings());
        return resultRecipe;
    }

    public static Recipe mapRecipeDatabaseToDomain(RecipeFullInfoDb recipedb) {
        Recipe resultRecipe = new Recipe();
        resultRecipe.setId(recipedb.recipe.getId());
        resultRecipe.setImage(recipedb.recipe.getImage());
        resultRecipe.setName(recipedb.recipe.getName());
        resultRecipe.setServings(recipedb.recipe.getServings());
        List<RecipeIngredient> ingredients = new ArrayList<>();
        for (RecipeIngredientDb ingredient : recipedb.ingredients ) {
            RecipeIngredient resultIngredient = new RecipeIngredient();
            resultIngredient.setIngredient(ingredient.getIngredient());
            resultIngredient.setMeasure(ingredient.getMeasure());
            resultIngredient.setQuantity(ingredient.getQuantity());
            ingredients.add(resultIngredient);
        }
        resultRecipe.setIngredients(ingredients);
        List<RecipeStep> steps = new ArrayList<>();
        for (RecipeStepDb step : recipedb.steps ) {
            RecipeStep resultStep = new RecipeStep();
            resultStep.setDescription(step.getDescription());
            resultStep.setId(step.getId());
            resultStep.setShortDescription(step.getShortDescription());
            resultStep.setThumbnailURL(step.getThumbnailURL());
            resultStep.setVideoURL(step.getVideoURL());
            steps.add(resultStep);
        }
        resultRecipe.setSteps(steps);
        return resultRecipe;
    }

    public static List<RecipeFullInfoDb> mapRecipeListDomainToDatabase(List<Recipe> recipes) {
        List<RecipeFullInfoDb> recipesDb = new ArrayList<>();
        for (Recipe recipe: recipes) {
            recipesDb.add(mapRecipeDomainToDatabase(recipe));
        }
        return recipesDb;
    }

    private static RecipeFullInfoDb mapRecipeDomainToDatabase(Recipe recipe) {
        RecipeFullInfoDb resultRecipe = new RecipeFullInfoDb();
        resultRecipe.recipe = new RecipeDb();
        resultRecipe.steps = new ArrayList<>();
        resultRecipe.ingredients = new ArrayList<>();
        resultRecipe.recipe.setImage(recipe.getImage());
        resultRecipe.recipe.setName(recipe.getName());
        resultRecipe.recipe.setServings(recipe.getServings());
        for (RecipeIngredient ingredient : recipe.getIngredients() ) {
            RecipeIngredientDb resultIngredient = new RecipeIngredientDb();
            resultIngredient.setRecipeId(recipe.getId());
            resultIngredient.setIngredient(ingredient.getIngredient());
            resultIngredient.setMeasure(ingredient.getMeasure());
            resultIngredient.setQuantity(ingredient.getQuantity());
            resultRecipe.ingredients.add(resultIngredient);
        }
        for (RecipeStep step : recipe.getSteps() ) {
            RecipeStepDb resultStep = new RecipeStepDb();
            resultStep.setDescription(step.getDescription());
            resultStep.setRecipeId(recipe.getId());
            resultStep.setId(step.getId());
            resultStep.setShortDescription(step.getShortDescription());
            resultStep.setThumbnailURL(step.getThumbnailURL());
            resultStep.setVideoURL(step.getVideoURL());
            resultRecipe.steps.add(resultStep);
        }
        return resultRecipe;
    }

    public static List<Recipe> mapRecipeListNetworkToDomain(List<RecipeNet> recipesNet) {
        List<Recipe> recipes = new ArrayList<>();
        for (RecipeNet recipeNet: recipesNet) {
            recipes.add(mapRecipeNetworkToDomain(recipeNet));
        }
        return recipes;
    }

    private static Recipe mapRecipeNetworkToDomain(RecipeNet recipeNet) {
        Recipe resultRecipe = new Recipe();
        resultRecipe.setImage(recipeNet.getImage());
        resultRecipe.setName(recipeNet.getName());
        resultRecipe.setServings(recipeNet.getServings());
        List<RecipeIngredient> ingredients = new ArrayList<>();
        for (RecipeIngredientNet ingredient : recipeNet.getIngredients() ) {
            RecipeIngredient resultIngredient = new RecipeIngredient();
            resultIngredient.setIngredient(ingredient.getIngredient());
            resultIngredient.setMeasure(ingredient.getMeasure());
            resultIngredient.setQuantity(ingredient.getQuantity());
            ingredients.add(resultIngredient);
        }
        resultRecipe.setIngredients(ingredients);
        List<RecipeStep> steps = new ArrayList<>();
        for (RecipeStepNet step : recipeNet.getSteps() ) {
            RecipeStep resultStep = new RecipeStep();
            resultStep.setDescription(step.getDescription());
            resultStep.setId(step.getId());
            resultStep.setShortDescription(step.getShortDescription());
            resultStep.setThumbnailURL(step.getThumbnailURL());
            resultStep.setVideoURL(step.getVideoURL());
            steps.add(resultStep);
        }
        resultRecipe.setSteps(steps);
        return resultRecipe;
    }

    public static List<RecipeIngredient> mapRecipeIngredientsListDatabaseToDomain(
            List<RecipeIngredientDb> recipeIngredientsDb) {
        List<RecipeIngredient> ingredients = new ArrayList<>();
        for (RecipeIngredientDb ingredient : recipeIngredientsDb ) {
            RecipeIngredient resultIngredient = new RecipeIngredient();
            resultIngredient.setIngredient(ingredient.getIngredient());
            resultIngredient.setMeasure(ingredient.getMeasure());
            resultIngredient.setQuantity(ingredient.getQuantity());
            ingredients.add(resultIngredient);
        }
        return ingredients;
    }

}
