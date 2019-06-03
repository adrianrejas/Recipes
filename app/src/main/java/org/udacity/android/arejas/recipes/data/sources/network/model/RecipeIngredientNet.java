package org.udacity.android.arejas.recipes.data.sources.network.model;

import android.os.Parcel;
import android.os.Parcelable;

public class RecipeIngredientNet implements Parcelable
{

    private Float quantity;
    private String measure;
    private String ingredient;

    public final static Creator<RecipeIngredientNet> CREATOR = new Creator<RecipeIngredientNet>() {

        @SuppressWarnings({
                "unchecked"
        })
        public RecipeIngredientNet createFromParcel(Parcel in) {
            return new RecipeIngredientNet(in);
        }

        public RecipeIngredientNet[] newArray(int size) {
            return (new RecipeIngredientNet[size]);
        }

    };

    RecipeIngredientNet(Parcel in) {
        this.quantity = ((Float) in.readValue((Float.class.getClassLoader())));
        this.measure = ((String) in.readValue((String.class.getClassLoader())));
        this.ingredient = ((String) in.readValue((String.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     *
     */
    public RecipeIngredientNet() {
    }

    /**
     *
     * @param measure
     * @param ingredient
     * @param quantity
     */
    public RecipeIngredientNet(Float quantity, String measure, String ingredient) {
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
