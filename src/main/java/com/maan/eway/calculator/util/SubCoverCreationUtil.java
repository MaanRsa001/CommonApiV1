package com.maan.eway.calculator.util;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import com.maan.eway.res.calc.Cover;

public class SubCoverCreationUtil implements Function<List<Cover>, List<Cover>> {

	 

	@Override
	public List<Cover> apply(List<Cover> c) {
		try {
			List<Cover> d = c.stream().filter(distinctByKey(Cover::getCoverId)).collect(Collectors.toList());
			for (Cover cover : d) {
				 List<Cover> subcover = c.stream().filter(cv-> cv.getCoverId().equals(cover.getCoverId())).collect(Collectors.toList());
				 cover.setSubcovers(subcover);
			}
			return d;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public static <T> Predicate<T> distinctByKey(Function<? super T,Object> keyExtractor) {
	    Map<Object,Boolean> seen = new ConcurrentHashMap<>();
	    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}
}
