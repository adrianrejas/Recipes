package org.udacity.android.arejas.recipes.domain.entities;

import android.arch.persistence.room.Ignore;
import android.os.Parcel;
import android.os.Parcelable;

public class RecipeListItem implements Parcelable
{

    private Integer id;
    private String name;
    private Integer servings;
    private String image;

    public final static Creator<RecipeListItem> CREATOR = new Creator<RecipeListItem>() {

        @SuppressWarnings({
                "unchecked"
        })
        public RecipeListItem createFromParcel(Parcel in) {
            return new RecipeListItem(in);
        }

        public RecipeListItem[] newArray(int size) {
            return (new RecipeListItem[size]);
        }

    };

    @Ignore
    private RecipeListItem(Parcel in) {
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.servings = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.image = ((String) in.readValue((String.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     *
     */
    public RecipeListItem() {
    }

    /**
     *
     * @param id
     * @param servings
     * @param name
     * @param image
     */
    @Ignore
    public RecipeListItem(Integer id, String name, Integer servings, String image) {
        super();
        this.id = id;
        this.name = name;
        this.servings = servings;
        this.image = image;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        dest.writeValue(id);
        dest.writeValue(name);
        dest.writeValue(servings);
        dest.writeValue(image);
    }

    public int describeContents() {
        return 0;
    }

}