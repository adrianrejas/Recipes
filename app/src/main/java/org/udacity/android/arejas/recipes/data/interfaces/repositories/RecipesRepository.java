package org.udacity.android.arejas.recipes.data.interfaces.repositories;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.paging.DataSource;
import android.arch.paging.PagedList;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.udacity.android.arejas.recipes.data.interfaces.repositories.mappers.RecipesRepositoryMapper;
import org.udacity.android.arejas.recipes.data.sources.database.RecipesDatabase;
import org.udacity.android.arejas.recipes.data.sources.database.model.RecipeDb;
import org.udacity.android.arejas.recipes.data.sources.database.model.RecipeFullInfoDb;
import org.udacity.android.arejas.recipes.data.sources.database.model.RecipeIngredientDb;
import org.udacity.android.arejas.recipes.data.sources.network.RecipeNetworkApi;
import org.udacity.android.arejas.recipes.data.sources.network.model.RecipeNet;
import org.udacity.android.arejas.recipes.domain.entities.Recipe;
import org.udacity.android.arejas.recipes.domain.entities.RecipeIngredient;
import org.udacity.android.arejas.recipes.domain.entities.RecipeListItem;
import org.udacity.android.arejas.recipes.domain.entities.RecipeWidgetItem;
import org.udacity.android.arejas.recipes.utils.entities.DbPagedListResource;
import org.udacity.android.arejas.recipes.utils.entities.DbBoundResource;
import org.udacity.android.arejas.recipes.utils.entities.NetworkAndDbBoundResource;
import org.udacity.android.arejas.recipes.utils.entities.Resource;
import org.udacity.android.arejas.recipes.utils.functional.Consumer;

import java.util.List;
import java.util.concurrent.Executor;

import javax.inject.Inject;
import javax.inject.Named;
import javax.inject.Singleton;

import retrofit2.Call;

@Singleton
public class RecipesRepository {

    private final static int RESULTS_PER_PAGE_RECIPES_DB = 20;

    private final RecipesDatabase recipesDatabase;
    private final Executor dbExecutor;
    private final Executor networkExecutor;

    // Cached data
    private LiveData<Resource<PagedList<RecipeListItem>>> recipes;

    // Synchronization lock objects
    private final Object recipesLock = new Object();

    @Inject
    public RecipesRepository(RecipesDatabase recipesDatabase,
                             @Named("dbExecutor") Executor dbExecutor,
                             @Named("networkExecutor") Executor networkExecutor) {
        this.recipesDatabase = recipesDatabase;
        this.dbExecutor = dbExecutor;
        this.networkExecutor = networkExecutor;
    }

    public LiveData<Resource<PagedList<RecipeListItem>>> getRecipes(boolean forceReload) {
        synchronized (recipesLock) {
            if (forceReload) recipes = null;
            if (recipes == null) {
                recipes = new DbPagedListResource<Integer, RecipeListItem, RecipeDb>(
                                RESULTS_PER_PAGE_RECIPES_DB,
                                dbExecutor) {
                    @Override
                    public DataSource.Factory<Integer, RecipeDb> getDbDataSource() {
                        return recipesDatabase.getRecipesDao().getAllDataSource();
                    }

                    @Override
                    public RecipeListItem transformDatabaseToDomain(RecipeDb data) {
                        return RecipesRepositoryMapper.mapRecipeListItemDatabaseToDomain(data);
                    }

                    @Override
                    public void transformResult(PagedList<RecipeListItem> result) throws Exception {}

                    @Override
                    public boolean checkIfSuccess(PagedList<RecipeListItem> result) throws Exception {
                        return result != null;
                    }
                }.getLiveData();
            }
            return recipes;
        }
    }

    public LiveData<Resource<Recipe>> getRecipe(int recipeId) {
        return new DbBoundResource<Recipe, RecipeFullInfoDb>() {

            @Override
            protected LiveData<RecipeFullInfoDb> loadFromDb() {
                return recipesDatabase.getRecipesDao().getRecipeFullInfoLiveData(recipeId);
            }

            @Override
            public Recipe transformDatabaseToDomain(@Nullable RecipeFullInfoDb data) throws Exception {
                return RecipesRepositoryMapper.mapRecipeDatabaseToDomain(data);
            }

        }.getAsLiveData();
    }

    public LiveData<Resource> requestRecipesFromNetworkAndSaveThemOnDatabase(String url, String file) {
        MutableLiveData<Resource> resultLD = new MutableLiveData<>();
        resultLD.postValue(Resource.loading(null));
        new NetworkAndDbBoundResource<List<Recipe>, List<RecipeNet>, List<RecipeFullInfoDb>>() {
            @Override
            protected LiveData<List<RecipeFullInfoDb>> loadFromDb() {
                return null;
            }

            @Override
            public List<Recipe> transformDatabaseToDomain(@Nullable List<RecipeFullInfoDb> data) throws Exception {
                return null;
            }

            @Override
            protected Call<List<RecipeNet>> createNetworkCall() {
                return RecipeNetworkApi.requestRecipesCallToNetwork(url, file);
            }

            @Override
            protected List<Recipe> transformNetworkToDomain(@NonNull List<RecipeNet> item) throws Exception {
                return RecipesRepositoryMapper.mapRecipeListNetworkToDomain(item);
            }

            @Override
            protected void saveNetworkResult(@Nullable List<Recipe> data) {
                dbExecutor.execute(() -> {
                    try {
                        List<RecipeFullInfoDb> dataInDbStyle =
                                RecipesRepositoryMapper.mapRecipeListDomainToDatabase(data);
                        recipesDatabase.getRecipesDao().insertRecipeListFullInfo(
                                dataInDbStyle.toArray(new RecipeFullInfoDb[dataInDbStyle.size()]));
                        resultLD.postValue(Resource.success(null));
                    } catch (Exception e) {
                        e.printStackTrace();
                        resultLD.postValue(Resource.error(e, null));
                    }
                });
            }

            @Override
            protected boolean shouldRequestToDb() {
                return false;
            }

            @Override
            protected boolean shouldRequestToNetwork(@Nullable List<Recipe> data) {
                return true;
            }

            @Override
            protected boolean shouldSaveToDb(@Nullable List<Recipe> data) {
                return true;
            }
        };
        return resultLD;
    }

    public void getRecipeInfoForWidget(int recipeId, Consumer<RecipeWidgetItem> consumer) {
        dbExecutor.execute(() -> {
            RecipeWidgetItem resultedItem = null;
            RecipeDb recipeDb = recipesDatabase.getRecipesDao().getRecipeBasicInfo(recipeId);
            if (recipeDb != null) {
                List<RecipeIngredientDb> recipeIngredientsDb =
                        recipesDatabase.getRecipesIngredientsDao().getAllFromSameRecipe(recipeId);
                List<RecipeIngredient> recipeIngredients =
                        RecipesRepositoryMapper.mapRecipeIngredientsListDatabaseToDomain(recipeIngredientsDb);
                Integer prevRecipeId = recipesDatabase.getRecipesDao().getPreviousRecipeId(recipeId);
                Integer nextRecipeId = recipesDatabase.getRecipesDao().getNextRecipeId(recipeId);
                resultedItem = new RecipeWidgetItem(recipeId, nextRecipeId, prevRecipeId,
                        recipeDb.getName(), recipeIngredients);
            }
            if (consumer != null) consumer.accept(resultedItem);
        });
    }

    public void getFirstRecipeInfoForWidget(Consumer<RecipeWidgetItem> consumer) {
        dbExecutor.execute(() -> {
            RecipeWidgetItem resultedItem = null;
            RecipeDb recipeDb = recipesDatabase.getRecipesDao().getFirstRecipeBasicInfo();
            if (recipeDb != null) {
                int recipeId = (recipeDb.getId() != null) ? recipeDb.getId() : 0;
                List<RecipeIngredientDb> recipeIngredientsDb =
                        recipesDatabase.getRecipesIngredientsDao().getAllFromSameRecipe(recipeId);
                List<RecipeIngredient> recipeIngredients =
                        RecipesRepositoryMapper.mapRecipeIngredientsListDatabaseToDomain(recipeIngredientsDb);
                Integer prevRecipeId = recipesDatabase.getRecipesDao().getPreviousRecipeId(recipeId);
                Integer nextRecipeId = recipesDatabase.getRecipesDao().getNextRecipeId(recipeId);
                resultedItem = new RecipeWidgetItem(recipeId, nextRecipeId, prevRecipeId,
                        recipeDb.getName(), recipeIngredients);
            }
            if (consumer != null) consumer.accept(resultedItem);
        });
    }

    public List<RecipeIngredient> getRecipeIngredients(int recipeId) {
        List<RecipeIngredientDb> recipeIngredientsDb =
                recipesDatabase.getRecipesIngredientsDao().getAllFromSameRecipe(recipeId);
        return RecipesRepositoryMapper.mapRecipeIngredientsListDatabaseToDomain(recipeIngredientsDb);
    }

    public List<RecipeIngredient> getFirstRecipeIngredients() {
        List<RecipeIngredientDb> recipeIngredientsDb =
                recipesDatabase.getRecipesIngredientsDao().getAllFromFirstRecipe();
        return RecipesRepositoryMapper.mapRecipeIngredientsListDatabaseToDomain(recipeIngredientsDb);
    }

}
