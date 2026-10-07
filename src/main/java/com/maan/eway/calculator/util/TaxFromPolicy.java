package com.maan.eway.calculator.util;

import java.util.function.Function;

import com.maan.eway.bean.PolicyCoverData;
import com.maan.eway.res.calc.Tax;

public class TaxFromPolicy  implements Function<PolicyCoverData,Tax>{

	@Override
	public Tax apply(PolicyCoverData t) {
		try {
			if("T".equalsIgnoreCase(t.getCoverageType())) {
				Tax d=Tax.builder()
						.isTaxExempted(t.getIsTaxExtempted())
						.taxAmount(t.getTaxAmount())
						.taxDesc(t.getTaxDesc())
						.taxExemptCode(t.getTaxExemptCode())
						.taxExemptType(t.getTaxExemptType())
						.taxId(t.getTaxId()==null?"":t.getTaxId().toString())
						.taxRate(t.getTaxRate()==null?0D: t.getTaxRate().doubleValue() )				 	
						.calcType(t.getCalcType()==null?"":t.getCalcType())
						.endtTypeId(t.getDiscLoadId()==null?"0":t.getDiscLoadId().toString())
						.endtTypeCount(t.getEndtCount())
						.build();
				return d;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

 

}
