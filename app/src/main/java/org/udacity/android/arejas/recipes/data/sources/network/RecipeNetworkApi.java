package org.udacity.android.arejas.recipes.data.sources.network;

import org.udacity.android.arejas.recipes.data.sources.network.model.RecipeNet;

import java.util.List;

import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Path;

public class RecipeNetworkApi {

    interface MovieDbApService {

        @GET("{file}")
        Call<List<RecipeNet>> requestRecipes(@Path("file") String file);

    }

    public static Call<List<RecipeNet>> requestRecipesCallToNetwork(String url, String file) {
        try {
            return new Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .build()
                    .create(MovieDbApService.class)
                    .requestRecipes(file);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
