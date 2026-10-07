package com.maan.eway.calculator.util;

import java.util.function.Function;

import com.maan.eway.res.calc.RatingInfo;

import jakarta.persistence.Tuple;

public class RatingFieldUtil implements Function<Tuple,RatingInfo > {

	@Override
	public RatingInfo apply(Tuple t) { 
		try {
	 
			RatingInfo r=RatingInfo.builder().ratingFieldId(t.get("ratingId")==null?"":t.get("ratingId").toString())
					.ratingField(t.get("ratingField")==null?"":t.get("ratingField").toString())
					.inputTableName(t.get("inputTableName")==null?"":t.get("inputTableName").toString())
					.inputColumName(t.get("inputColumnName")==null?"":t.get("inputColumnName").toString())
					.build();
			
			return r;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	 

}
