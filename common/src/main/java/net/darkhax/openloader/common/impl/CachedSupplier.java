package net.darkhax.openloader.common.impl;

import org.jetbrains.annotations.Nullable;

import java.util.function.Supplier;

// Minified version adapted from Bookshelf
public class CachedSupplier<T> implements Supplier<T> {

    private final Supplier<T> delegate;
    private boolean cached = false;
    @Nullable
    private T cachedValue;

    protected CachedSupplier(Supplier<T> delegate) {
        this.delegate = delegate;
    }

    @Override
    public T get() {
        if (!this.isCached()) {
            this.cachedValue = this.delegate.get();
            this.cached = true;
        }
        return cachedValue;
    }

    public boolean isCached() {
        return this.cached;
    }

    public static <T> CachedSupplier<T> cache(Supplier<T> delegate) {
        return new CachedSupplier<>(delegate);
    }
}