package org.udacity.android.arejas.recipes.utils.entities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.MainThread;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.WorkerThread;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public abstract class NetworkAndDbBoundResource<DomainType, NetworkType, DatabaseType> {

    private final MediatorLiveData<Resource<DomainType>> result = new MediatorLiveData<>();

    public NetworkAndDbBoundResource() {
        result.postValue(Resource.<DomainType>loading(null));
        if (shouldRequestToDb()) {
            LiveData<DatabaseType> dbSource = loadFromDb();
            result.addSource(dbSource, data -> {
                result.removeSource(dbSource);
                DomainType domainData = null;
                try {
                    domainData = transformDatabaseToDomain(data);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (shouldRequestToNetwork(domainData)) {
                    loadFromNetwork(dbSource);
                } else {
                    result.addSource(dbSource, newData -> {
                        try {
                            DomainType domData = transformDatabaseToDomain(data);
                            Resource<DomainType> newResource = result.getValue();
                            newResource.setStatus(Resource.Status.SUCCESS);
                            newResource.setData(domData);
                            result.postValue(newResource);
                        } catch (Exception error) {
                            Resource<DomainType> newResource = result.getValue();
                            newResource.setStatus(Resource.Status.ERROR);
                            newResource.setError(error);
                            result.postValue(newResource);
                        }
                    });
                }
            });
        } else {
            loadFromNetwork(null);
        }
    }

    private void loadFromNetwork(final LiveData<DatabaseType> dbSource) {
        if (dbSource != null)  result.addSource(dbSource, newData -> {
            Resource<DomainType> newResource = result.getValue();
            newResource.setStatus(Resource.Status.LOADING);
            result.postValue(newResource);
        });
        Call<NetworkType> call = createNetworkCall();
        if (call != null) {
            call.enqueue(new Callback<NetworkType>() {
                @Override
                public void onResponse(Call<NetworkType> call, Response<NetworkType> response) {
                    if (dbSource != null)  result.removeSource(dbSource);
                    if ((response != null) && (response.body() != null)) {
                        try {
                            DomainType newData = transformNetworkToDomain(response.body());
                            if (shouldSaveToDb(newData)) {
                                saveNetworkResult(newData);
                                if (dbSource != null)  result.addSource(dbSource, data -> {
                                    try {
                                        DomainType domData = transformDatabaseToDomain(data);
                                        Resource<DomainType> newResource = result.getValue();
                                        newResource.setStatus(Resource.Status.SUCCESS);
                                        newResource.setData(domData);
                                        result.postValue(newResource);
                                    } catch (Exception error) {
                                        Resource<DomainType> newResource = result.getValue();
                                        newResource.setStatus(Resource.Status.ERROR);
                                        newResource.setError(error);
                                        result.postValue(newResource);
                                    }
                                });
                            } else {
                                Resource<DomainType> newResource = result.getValue();
                                newResource.setStatus(Resource.Status.SUCCESS);
                                newResource.setData(newData);
                                result.postValue(newResource);
                            }
                        } catch (Exception error) {
                            Resource<DomainType> newResource = result.getValue();
                            newResource.setStatus(Resource.Status.ERROR);
                            newResource.setError(error);
                            result.postValue(newResource);
                        }
                    } else {
                        result.postValue(Resource.error(new NullPointerException("No result from REST call"), null));
                    }
                }

                @Override
                public void onFailure(Call<NetworkType> call, Throwable error) {
                    result.removeSource(dbSource);
                    result.addSource(dbSource, newData -> {
                        Resource<DomainType> newResource = result.getValue();
                        newResource.setStatus(Resource.Status.ERROR);
                        newResource.setError(error);
                        result.postValue(newResource);
                    });
                }
            });
        }
    }

    @MainThread
    protected abstract LiveData<DatabaseType> loadFromDb();

    protected abstract DomainType transformDatabaseToDomain(@Nullable DatabaseType data) throws Exception;

    @MainThread
    protected abstract Call<NetworkType> createNetworkCall();

    @WorkerThread
    protected abstract DomainType transformNetworkToDomain(@NonNull NetworkType item) throws Exception;

    @WorkerThread
    protected abstract void saveNetworkResult(@Nullable DomainType data);

    @MainThread
    protected abstract boolean shouldRequestToDb();

    @MainThread
    protected abstract boolean shouldRequestToNetwork(@Nullable DomainType data);

    @WorkerThread
    protected abstract boolean shouldSaveToDb(@Nullable DomainType data);

    public final LiveData<Resource<DomainType>> getAsLiveData() {
        return result;
    }
}
