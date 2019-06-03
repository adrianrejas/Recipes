package org.udacity.android.arejas.recipes.utils.functional;

@FunctionalInterface
public interface BiConsumer<T, V> {
    void accept(T var1, V var2);

    default BiConsumer<T, V> andThen(BiConsumer<? super T, ? super V> after) {
        throw new RuntimeException("Stub!");
    }
}