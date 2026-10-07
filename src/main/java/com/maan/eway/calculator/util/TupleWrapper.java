package com.maan.eway.calculator.util;

import jakarta.persistence.Tuple;
import jakarta.persistence.TupleElement;
import java.util.List;
import java.util.Map;

/**
 * Wraps a JPA Tuple and allows specific keys to be overridden.
 * Used when we need to patch values (e.g. rate) before passing to processCoverDependents.
 */
public class TupleWrapper implements Tuple {

    private final Tuple delegate;
    private final Map<String, Object> overrides;

    public TupleWrapper(Tuple delegate, Map<String, Object> overrides) {
        this.delegate = delegate;
        this.overrides = overrides;
    }

    @Override
    public <X> X get(TupleElement<X> tupleElement) {
        return delegate.get(tupleElement);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <X> X get(String alias, Class<X> type) {
        if (overrides.containsKey(alias)) {
            return (X) overrides.get(alias);
        }
        return delegate.get(alias, type);
    }

    @Override
    @SuppressWarnings("unchecked")
    public Object get(String alias) {
        if (overrides.containsKey(alias)) {
            return overrides.get(alias);
        }
        return delegate.get(alias);
    }

    @Override
    public <X> X get(int i, Class<X> type) {
        return delegate.get(i, type);
    }

    @Override
    public Object get(int i) {
        return delegate.get(i);
    }

    @Override
    public Object[] toArray() {
        return delegate.toArray();
    }

    @Override
    public List<TupleElement<?>> getElements() {
        return delegate.getElements();
    }
}