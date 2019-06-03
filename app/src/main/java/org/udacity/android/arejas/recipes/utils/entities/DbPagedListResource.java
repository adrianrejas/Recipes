package org.udacity.android.arejas.recipes.utils.entities;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MediatorLiveData;
import android.arch.paging.DataSource;
import android.arch.paging.LivePagedListBuilder;
import android.arch.paging.PagedList;
import android.support.annotation.MainThread;

import java.util.concurrent.Executor;

@SuppressWarnings("ALL")
public abstract class DbPagedListResource<KeyType, DomainType, DatabaseType> {

    private MediatorLiveData<Resource<PagedList<DomainType>>> resultLD;

    public DbPagedListResource(Integer resultsPerPage, Executor executor) {
        DataSource.Factory<KeyType, DatabaseType> dataFactory = getDbDataSource();
        DataSource.Factory<KeyType, DomainType> domainDataFactory =
                dataFactory.map(this::transformDatabaseToDomain);
        //noinspection unchecked
        LiveData<PagedList<DomainType>> dataListLD =
                new LivePagedListBuilder(domainDataFactory, resultsPerPage).
                        setFetchExecutor(executor)
                        .build();
        resultLD = new MediatorLiveData<>();
        resultLD.setValue(Resource.loading(null));
        resultLD.addSource(dataListLD, items -> {
            Resource<PagedList<DomainType>> newData = resultLD.getValue();
            try {
                transformResult(items);
                newData.setData(items);
                if (checkIfSuccess(items)) {
                    newData.setStatus(Resource.Status.SUCCESS);
                } else {
                    newData.setStatus(Resource.Status.ERROR);
                    newData.setError(new VerifyError("There was a problem with the DB collection"));
                }
            } catch (Exception error) {
                newData.setStatus(Resource.Status.ERROR);
                newData.setError(error);
            }
            resultLD.setValue(newData);
        });
    }

    public LiveData<Resource<PagedList<DomainType>>> getLiveData() {
        return resultLD;
    }

    @MainThread
    public abstract DataSource.Factory<KeyType, DatabaseType> getDbDataSource ();

    @MainThread
    public abstract DomainType transformDatabaseToDomain (DatabaseType data);

    @MainThread
    public abstract void transformResult(PagedList<DomainType> result) throws Exception;

    @MainThread
    public abstract boolean checkIfSuccess (PagedList<DomainType> result) throws Exception;

}
