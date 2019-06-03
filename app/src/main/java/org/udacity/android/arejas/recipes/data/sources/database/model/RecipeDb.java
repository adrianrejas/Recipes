package org.udacity.android.arejas.recipes.data.sources.database.model;

import android.arch.persistence.room.Entity;
import android.arch.persistence.room.Ignore;
import android.arch.persistence.room.PrimaryKey;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

@Entity(tableName = "recipes_list")
public class RecipeDb implements Parcelable
{

    @PrimaryKey(autoGenerate = true)
    @NonNull
    private Integer id;
    private String name;
    private Integer servings;
    private String image;

    @Ignore
    public final static Parcelable.Creator<RecipeDb> CREATOR = new Creator<RecipeDb>() {

        @SuppressWarnings({
                "unchecked"
        })
        public RecipeDb createFromParcel(Parcel in) {
            return new RecipeDb(in);
        }

        public RecipeDb[] newArray(int size) {
            return (new RecipeDb[size]);
        }

    };

    @Ignore
    protected RecipeDb(Parcel in) {
        this.id = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.name = ((String) in.readValue((String.class.getClassLoader())));
        this.servings = ((Integer) in.readValue((Integer.class.getClassLoader())));
        this.image = ((String) in.readValue((String.class.getClassLoader())));
    }

    /**
     * No args constructor for use in serialization
     *
     */
    public RecipeDb() {
    }

    /**
     *
     * @param id
     * @param servings
     * @param name
     * @param image
     */
    @Ignore
    public RecipeDb(Integer id, String name, Integer servings, String image) {
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