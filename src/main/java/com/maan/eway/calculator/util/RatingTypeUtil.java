package com.maan.eway.calculator.util;

import java.util.function.Function;

import com.maan.eway.res.calc.RatingInfo;

import jakarta.persistence.Tuple;

public class RatingTypeUtil implements Function<Tuple,RatingInfo > {

	@Override
	public RatingInfo apply(Tuple t) { 
		try {
	 
			final RatingInfo r=
					RatingInfo.builder().companyId(t.get("companyId")==null?"":t.get("companyId").toString())
					.discretCol(t.get("discreteColumn")==null?"":t.get("discreteColumn").toString())
					.factorRangeYn(t.get("rangeYn")==null?"":t.get("rangeYn").toString())
					.factortypeId(t.get("factorTypeId")==null?"":t.get("factorTypeId").toString())
					.factortypeName(t.get("factorTypeName")==null?"":t.get("factorTypeName").toString())
					.productId(t.get("productId")==null?"":t.get("productId").toString())
					.rangeFromCol(t.get("rangeFromColumn")==null?"":t.get("rangeFromColumn").toString())
					.rangeToCol(t.get("rangeToColumn")==null?"":t.get("rangeToColumn").toString())
					.ratingFieldId(t.get("ratingFieldId")==null?"":t.get("ratingFieldId").toString())
					.build();
		 
			return r;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	 

}
