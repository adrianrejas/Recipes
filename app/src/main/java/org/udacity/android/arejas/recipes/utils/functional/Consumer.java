package org.udacity.android.arejas.recipes.utils.functional;

@FunctionalInterface
public interface Consumer<T> {
    void accept(T var1);

    default Consumer<T> andThen(Consumer<? super T> after) {
        throw new RuntimeException("Stub!");
    }
}