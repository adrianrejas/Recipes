package org.udacity.android.arejas.recipes.utils.entities;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

public class Resource<T> {
    @NonNull
    private Status status;
    @Nullable
    private T data;
    @Nullable
    private Throwable error;

    private Resource(@NonNull Status status, @Nullable T data,
                     @Nullable Throwable error) {
        this.status = status;
        this.data = data;
        this.error = error;
    }

    public static <T> Resource<T> success(@Nullable T data) {
        return new Resource<>(Status.SUCCESS, data, null);
    }

    public static <T> Resource<T> error(Throwable error, @Nullable T data) {
        return new Resource<>(Status.ERROR, data, error);
    }

    public static <T> Resource<T> loading(@Nullable T data) {
        return new Resource<>(Status.LOADING, data, null);
    }

    @NonNull
    public Status getStatus() {
        return status;
    }

    @NonNull
    public T getData() {
        return data;
    }

    @Nullable
    public Throwable getError() {
        return error;
    }

    public void setStatus(@Nullable Status status) {
        this.status = status;
    }

    public void setData(@Nullable T data) {
        this.data = data;
    }

    public void setError(@Nullable Throwable error) {
        this.error = error;
    }

    public enum Status { SUCCESS, ERROR, LOADING }
}
