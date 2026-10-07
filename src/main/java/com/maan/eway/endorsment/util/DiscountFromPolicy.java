package com.maan.eway.endorsment.util;

import java.math.BigDecimal;
import java.util.function.Function;

import com.maan.eway.bean.PolicyCoverData;
import com.maan.eway.res.calc.Discount;

public class DiscountFromPolicy  implements Function<PolicyCoverData,Discount>{

	 

	@Override
	public Discount apply(PolicyCoverData t) {
		try {
			 if(t.getCoverageType()!=null && ("D".equalsIgnoreCase(t.getCoverageType()) || "P".equalsIgnoreCase(t.getCoverageType()))) {
				 String calctype=t.getCalcType()==null?"":t.getCalcType();
				 Discount d=Discount.builder()
						 	.discountDesc(t.getCoverName()==null?"":t.getCoverName())
						 	.discountId(t.getDiscLoadId()==null?"":t.getDiscLoadId().toString())
						 	.discountRate("F".equals(calctype)?"0": t.getRate()==null?"0":t.getRate().toString())
						 	.discountCalcType(calctype)
						 	.discountforId(t.getDiscountCoverId()==null?"":t.getDiscountCoverId().toString())
						 	.maxAmount(t.getMinimumPremium()==null?BigDecimal.ZERO:t.getMinimumPremium())
						 	.factorTypeId(t.getFactorTypeId()==null?"":t.getFactorTypeId().toString())
						 	.regulatoryCode(t.getRegulatoryCode()==null?"N/A":t.getRegulatoryCode())
						 	.coverAgeType(t.getCoverageType())
						 	.minrate(t.getMinimumRate()==null?0D: t.getMinimumRate().doubleValue())
						 	.build();
				 return d;
			 }
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

 

}
