package org.udacity.android.arejas.recipes.domain.entities;

import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.util.List;

public class RecipeWidgetItem implements Parcelable
{

    @NonNull
    private Integer id;
    private Integer nextId;
    private Integer prevId;
    private String name;
    private List<RecipeIngredient> ingredients = null;

    public final static Creator<RecipeWidgetItem> CREATOR = new Creator<RecipeWidgetItem>() {

        @SuppressWarnings({
                "unchecked"
        })
        public RecipeWidgetItem createFromParcel(Parcel in) {
            return new RecipeWidgetItem(in);
        }

        public RecipeWidgetItem[] newArray(int size) {
            return (new RecipeWidgetItem[size]);
        }

    };

    private RecipeWidgetItem(Parcel in) {
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.nextId = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.prevId = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        in.readList(this.ingredients, (RecipeIngredient.class.getClassLoader()));
    }

    /**
     * No args constructor for use in serialization
     *
     */
    public RecipeWidgetItem() {
    }

    /**
     *
     * @param ingredients
     * @param id
     * @param nextId
     * @param prevId
     * @param name
     */
    public RecipeWidgetItem(Integer id, Integer nextId, Integer prevId, String name, List<RecipeIngredient> ingredients) {
        super();
        this.id = id;
        this.name = name;
        this.ingredients = ingredients;
        this.nextId = nextId;
        this.prevId = prevId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNextId() {
        return nextId;
    }

    public void setNextId(Integer nextId) {
        this.nextId = nextId;
    }

    public Integer getPrevId() {
        return prevId;
    }

    public void setPrevId(Integer prevId) {
        this.prevId = prevId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<RecipeIngredient> getIngredients() {
        return ingredients;
    }

    public void setIngredients(List<RecipeIngredient> recipeIngredients) {
        this.ingredients = recipeIngredients;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(nextId);
        dest.writeValue(prevId);
        dest.writeValue(name);
        dest.writeList(ingredients);
    }

    public int describeContents() {
        return 0;
    }

}