package com.maan.eway.endorsment.util;

import java.math.BigDecimal;
import java.util.function.Function;

import com.maan.eway.bean.PolicyCoverData;
import com.maan.eway.res.calc.Loading;

public class LoadingFromPolicy  implements Function<PolicyCoverData,Loading>{

	 
	@Override
	public Loading apply(PolicyCoverData t) {
		try {
			 if("L".equalsIgnoreCase(t.getCoverageType())) {
				 String calctype=t.getCalcType()==null?"":t.getCalcType();
				 Loading d=Loading.builder()
						 	.loadingDesc(t.getCoverName()==null?"":t.getCoverName())
						 	.loadingId(t.getDiscLoadId()==null?"":t.getDiscLoadId().toString())
						 	.loadingRate("F".equals(calctype)?"0": t.getRate()==null?"0":t.getRate().toString())
						 	.loadingCalcType(calctype)
						 	.loadingforId(t.getDiscountCoverId()==null?"":t.getDiscountCoverId().toString())
						 	.maxAmount(t.getMinimumPremium()==null?BigDecimal.ZERO:t.getMinimumPremium())
						 	.factorTypeId(t.getFactorTypeId()==null?"":t.getFactorTypeId().toString())
						 	.regulatoryCode(t.getRegulatoryCode()==null?"N/A":t.getRegulatoryCode())	
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
