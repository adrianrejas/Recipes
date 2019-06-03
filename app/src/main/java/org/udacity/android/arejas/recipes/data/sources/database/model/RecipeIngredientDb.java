package org.udacity.android.arejas.recipes.data.sources.database.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.ForeignKey;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.Index;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import static android.arch.persistence.room.ForeignKey.CASCADE;

@Entity(tableName = "recipes_ingredients",
        foreignKeys = @ForeignKey(entity = RecipeDb.class,
                parentColumns = "id",
                childColumns = "recipeId",
                onDelete = CASCADE),
        indices = @Index(value = "recipeId"),
        primaryKeys = {"recipeId","ingredient"})
public class RecipeIngredientDb implements Parcelable
{

    @NonNull
    private Integer recipeId;
    private Float quantity;
    private String measure;
    @NonNull
    private String ingredient;

    @Ignore
    public final static Parcelable.Creator<RecipeIngredientDb> CREATOR = new Creator<RecipeIngredientDb>() {

        @SuppressWarnings({
                "unchecked"
        })
        public RecipeIngredientDb createFromParcel(Parcel in) {
            return new RecipeIngredientDb(in);
        }

        public RecipeIngredientDb[] newArray(int size) {
            return (new RecipeIngredientDb[size]);
        }

    };

    @Ignore
    protected RecipeIngredientDb(Parcel in) {
        this.quantity = ((Float) in.readValue((Float.class.getClassLoader())));
        this.measure = ((String) in.readValue((String.class.getClassLoader())));
        this.ingredient = ((String) in.readValue((String.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     *
     */
    public RecipeIngredientDb() {
    }

    /**
     *
     * @param measure
     * @param ingredient
     * @param quantity
     */
    @Ignore
    public RecipeIngredientDb(Float quantity, String measure, String ingredient) {
        super();
        this.quantity = quantity;
        this.measure = measure;
        this.ingredient = ingredient;
    }

    public Integer getRecipeId() {
        return recipeId;
    }

    public void setRecipeId(Integer recipeId) {
        this.recipeId = recipeId;
    }

    public Float getQuantity() {
        return quantity;
    }

    public void setQuantity(Float quantity) {
        this.quantity = quantity;
    }

    public String getMeasure() {
        return measure;
    }

    public void setMeasure(String measure) {
        this.measure = measure;
    }

    public String getIngredient() {
        return ingredient;
    }

    public void setIngredient(String ingredient) {
        this.ingredient = ingredient;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(quantity);
        dest.writeValue(measure);
        dest.writeValue(ingredient);
    }

    public int describeContents() {
        return 0;
    }

}
