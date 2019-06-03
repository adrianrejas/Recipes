package org.udacity.android.arejas.recipes.domain.entities;

import android.os.Parcel;
import android.os.Parcelable;

public class RecipeIngredient implements Parcelable
{

    private Float quantity;
    private String measure;
    private String ingredient;

    public final static Creator<RecipeIngredient> CREATOR = new Creator<RecipeIngredient>() {

        @SuppressWarnings({
                "unchecked"
        })
        public RecipeIngredient createFromParcel(Parcel in) {
            return new RecipeIngredient(in);
        }

        public RecipeIngredient[] newArray(int size) {
            return (new RecipeIngredient[size]);
        }

    };

    RecipeIngredient(Parcel in) {
        this.quantity = ((Float) in.readValue((Float.class.getClassLoader())));
        this.measure = ((String) in.readValue((String.class.getClassLoader())));
        this.ingredient = ((String) in.readValue((String.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     *
     */
    public RecipeIngredient() {
    }

    /**
     *
     * @param measure
     * @param ingredient
     * @param quantity
     */
    public RecipeIngredient(Float quantity, String measure, String ingredient) {
        super();
        this.quantity = quantity;
        this.measure = measure;
        this.ingredient = ingredient;
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
