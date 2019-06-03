package org.udacity.android.arejas.recipes.utils.entities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.support.annotation.MainThread;
import android.support.annotation.Nullable;

public abstract class DbBoundResource<DomainType, DatabaseType> {

    private final MediatorLiveData<Resource<DomainType>> result = new MediatorLiveData<>();

    public DbBoundResource() {
        result.postValue(Resource.<DomainType>loading(null));
        LiveData<DatabaseType> dbSource = loadFromDb();
        result.addSource(dbSource, data -> {
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

    @MainThread
    protected abstract LiveData<DatabaseType> loadFromDb();

    protected abstract DomainType transformDatabaseToDomain(@Nullable DatabaseType data) throws Exception;

    public final LiveData<Resource<DomainType>> getAsLiveData() {
        return result;
    }
}
