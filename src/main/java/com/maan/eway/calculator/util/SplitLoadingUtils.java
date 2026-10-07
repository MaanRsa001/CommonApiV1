package com.maan.eway.calculator.util;

import java.math.BigDecimal;
import java.util.Date;
import java.util.function.Function;

import com.maan.eway.res.calc.Loading;

import jakarta.persistence.Tuple;

public class SplitLoadingUtils  implements Function<Tuple,Loading>{

	private Date effectiveDate;
	private Date policyEndDate;
	
	public SplitLoadingUtils(Date effectiveDate, Date policyEndDate) {
		this.effectiveDate=effectiveDate;
		this.policyEndDate=policyEndDate;
	}

	@Override
	public Loading apply(Tuple t) {
		try {
			 if(t.get("coverageType")!=null && "L".equalsIgnoreCase(t.get("coverageType").toString())) {
				 String calctype=t.get("calcType")==null?"":t.get("calcType").toString();
				 Loading d=Loading.builder()
						 	.loadingDesc(t.get("coverName")==null?"":t.get("coverName").toString())
						 	.loadingId(t.get("coverId")==null?"":t.get("coverId").toString())
						 	.loadingRate("F".equals(calctype)?"0": t.get("baseRate")==null?"0":t.get("baseRate").toString())
						 	.loadingCalcType(calctype)
						 	.loadingforId(t.get("discountCoverId")==null?"":t.get("discountCoverId").toString())
						 	.maxAmount(t.get("minPremium")==null?BigDecimal.ZERO:new BigDecimal(t.get("minPremium").toString()))
						 	.factorTypeId(t.get("factorTypeId")==null?"":t.get("factorTypeId").toString())
							.regulatoryCode(t.get("regulatoryCode")==null?"N/A":t.get("regulatoryCode").toString())
							.effectiveDate(effectiveDate)
							.policyEndDate(policyEndDate)
							.minrate(t.get("minimumRate")==null?0D:Double.parseDouble(t.get("minimumRate").toString()))
						 	.build();
				 
				// Underwriter Manual Loading override if present
	                if ("Y".equalsIgnoreCase(getSafe(t, "uwLoadingYn"))) {

	                    String uwType = getSafe(t, "uwLoadingType");
	                    String uwDesc = getSafe(t, "uwLoadingDesc") == null ? "Underwriter Loading"
	                            : getSafe(t, "uwLoadingDesc");
	                    BigDecimal uwValue = getBigDecimalSafe(t, "uwLoadingValue");

	                    d.setLoadingDesc(uwDesc);
	                    d.setLoadingCalcType(uwType == null ? "P" : uwType);

	                    // Percentage loading 
	                    if ("P".equalsIgnoreCase(uwType)) {
	                        d.setLoadingRate(uwValue.toPlainString());
	                        d.setLoadingAmount(BigDecimal.ZERO);

	                    // Absolute loading
	                    } else if ("A".equalsIgnoreCase(uwType)) {
	                        d.setLoadingRate(uwValue.toPlainString());
	                        d.setLoadingAmount(uwValue);

	                    // Default fallback 
	                    } else {
	                        d.setLoadingRate("0");
	                        d.setLoadingAmount(BigDecimal.ZERO);
	                    }
	                }

	                return d;
	            }

	        } catch (Exception e) {
	            e.printStackTrace();
	        }
	        return null;
	    }

	    
	    private String getSafe(Tuple t, String alias) {
	        try {
	            Object val = t.get(alias);
	            return val == null ? null : val.toString();
	        } catch (IllegalArgumentException ex) {
	            return null; 	        }
	    }

	    private BigDecimal getBigDecimalSafe(Tuple t, String alias) {
	        try {
	            Object val = t.get(alias);
	            if (val == null) return BigDecimal.ZERO;
	            return new BigDecimal(val.toString());
	        } catch (IllegalArgumentException ex) {
	            return BigDecimal.ZERO;
	        }
	    }


 

}
