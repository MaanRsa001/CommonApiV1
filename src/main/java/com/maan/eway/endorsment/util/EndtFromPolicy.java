package com.maan.eway.endorsment.util;

import java.math.BigDecimal;
import java.util.function.Function;

import com.maan.eway.bean.PolicyCoverData;
import com.maan.eway.res.calc.Endorsement; 

public class EndtFromPolicy  implements Function<PolicyCoverData,Endorsement>{

	 
	@Override
	public Endorsement apply(PolicyCoverData t) {
		try {
			 if("E".equalsIgnoreCase(t.getCoverageType())) {
				 String calctype=t.getCalcType()==null?"":t.getCalcType();
				 Endorsement d=Endorsement.builder()
						 	.endorsementDesc(t.getCoverName()==null?"":t.getCoverName())
						 	.endorsementId(t.getDiscLoadId()==null?"":t.getDiscLoadId().toString())
						 	.endorsementRate("F".equals(calctype)?0D: t.getRate()==null?0D:t.getRate().doubleValue())
						 	.endorsementCalcType(calctype)
						 	.endorsementforId(t.getDiscountCoverId()==null?"":t.getDiscountCoverId().toString())
						 	.maxAmount(t.getMinimumPremium()==null?BigDecimal.ZERO:t.getMinimumPremium())
						 	.factorTypeId(t.getFactorTypeId()==null?"":t.getFactorTypeId().toString())
						 	.regulatoryCode(t.getRegulatoryCode()==null?"N/A":t.getRegulatoryCode())
						 	.premiumAfterDiscount(t.getPremiumAfterDiscountFc())
						    .premiumAfterDiscountLC(t.getPremiumAfterDiscountLc())
						    .premiumBeforeDiscount(t.getPremiumBeforeDiscountFc())
						    .premiumBeforeDiscountLC(t.getPremiumBeforeDiscountLc())
						    .premiumExcluedTax(t.getPremiumExcludedTaxFc())
						    .premiumExcluedTaxLC(t.getPremiumExcludedTaxLc())
						    .premiumIncludedTax(t.getPremiumIncludedTaxFc())
						    .premiumIncludedTaxLC(t.getPremiumIncludedTaxLc())
						    .endtCount(t.getEndtCount())
						 	.build();
				 return d;
			 }
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

 

}
