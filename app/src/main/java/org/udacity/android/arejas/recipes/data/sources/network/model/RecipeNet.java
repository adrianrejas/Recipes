package org.udacity.android.arejas.recipes.data.sources.network.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class RecipeNet implements Parcelable
{

    private String name;
    private List<RecipeIngredientNet> ingredients = null;
    private List<RecipeStepNet> steps = null;
    private Integer servings;
    private String image;

    public final static Creator<RecipeNet> CREATOR = new Creator<RecipeNet>() {

        @SuppressWarnings({
                "unchecked"
        })
        public RecipeNet createFromParcel(Parcel in) {
            return new RecipeNet(in);
        }

        public RecipeNet[] newArray(int size) {
            return (new RecipeNet[size]);
        }

    };

    private RecipeNet(Parcel in) {
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.ingredients, (RecipeIngredientNet.class.getClassLoader()));
        in.readList(this.steps, (RecipeStepNet.class.getClassLoader()));
        this.servings = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.image = ((String) in.readValue((String.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     *
     */
    public RecipeNet() {
    }

    /**
     *
     * @param ingredients
     * @param servings
     * @param name
     * @param image
     * @param steps
     */
    public RecipeNet(String name, List<RecipeIngredientNet> ingredients, List<RecipeStepNet> steps, Integer servings, String image) {
        super();
        this.name = name;
        this.ingredients = ingredients;
        this.steps = steps;
        this.servings = servings;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<RecipeIngredientNet> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<RecipeIngredientNet> recipeIngredients) {
        this.ingredients = recipeIngredients;
    }

    public List<RecipeStepNet> getSteps() {
        return steps;
    }

    public void setSteps(List<RecipeStepNet> steps) {
        this.steps = steps;
    }

    public Integer getServings() {
        return servings;
    }

    public void setServings(Integer servings) {
        this.servings = servings;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(name);
        dest.writeList(ingredients);
        dest.writeList(steps);
        dest.writeValue(servings);
        dest.writeValue(image);
    }

    public int describeContents() {
        return 0;
    }

}