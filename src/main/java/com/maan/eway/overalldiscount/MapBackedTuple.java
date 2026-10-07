package com.maan.eway.overalldiscount;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.persistence.Tuple;
import jakarta.persistence.TupleElement;

public class MapBackedTuple implements Tuple {

    private final Map<String, Object> map;

    public MapBackedTuple(Map<String, Object> map) {
        this.map = map;
    }

    @Override
    public <X> X get(String alias, Class<X> type) {
        return type.cast(map.get(alias));
    }

    @Override
    public Object get(String alias) {
        return map.get(alias);
    }

    @Override
    public <X> X get(int i, Class<X> type) {
        return type.cast(map.values().toArray()[i]);
    }

    @Override
    public Object get(int i) {
        return map.values().toArray()[i];
    }

    @Override
    public List<TupleElement<?>> getElements() {
        return new ArrayList<>();
    }

    @Override
    public Object[] toArray() {
        return map.values().toArray();
    }

    @Override
    public String toString() {
        return map.toString();
    }

	@Override
	public <X> X get(TupleElement<X> tupleElement) {
		// TODO Auto-generated method stub
		return null;
	}
}

