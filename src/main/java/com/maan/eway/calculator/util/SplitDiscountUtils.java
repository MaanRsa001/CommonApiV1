package com.maan.eway.calculator.util;

import java.math.BigDecimal;
import java.util.Date;
import java.util.function.Function;

import com.maan.eway.res.calc.Discount;

import jakarta.persistence.Tuple;

public class SplitDiscountUtils  implements Function<Tuple,Discount>{

	private Date effectiveDate;
	private Date policyEndDate;
	private String promoCode;
	public SplitDiscountUtils(Date effectiveDate, Date policyEndDate,String promoCode) {
		this.effectiveDate=effectiveDate;
		this.policyEndDate=policyEndDate;
		this.promoCode=promoCode;
				
	}

	@Override
	public Discount apply(Tuple t) {
		try {
			
			System.out.println(
					   "Discount generated → coverId=" + t.get("coverId") 
					   + " discountId=" + t.get("coverId")
					);

			
			 if(t.get("coverageType")!=null && ("D".equalsIgnoreCase(t.get("coverageType").toString()) || ("P".equalsIgnoreCase(t.get("coverageType").toString()) && promoCode.equals(t.get("coverName")==null?"":t.get("coverName").toString()) ) )) {
				 
				 String calctype=t.get("calcType")==null?"":t.get("calcType").toString();
				 Discount d=Discount.builder()
						 	.discountDesc(t.get("coverDesc")==null?"":t.get("coverDesc").toString())
						 	.discountId(t.get("coverId")==null?"":t.get("coverId").toString())
						 	.discountRate("F".equals(calctype)?"0": t.get("baseRate")==null?"0":t.get("baseRate").toString())
						 	.discountCalcType(calctype)
						 	.discountforId(t.get("discountCoverId")==null?"":t.get("discountCoverId").toString())
						 	.maxAmount(t.get("minPremium")==null?BigDecimal.ZERO:new BigDecimal(t.get("minPremium").toString()))
						 	.factorTypeId(t.get("factorTypeId")==null?"":t.get("factorTypeId").toString())
						 	.coverAgeType(t.get("coverageType").toString())
						 	.regulatoryCode(t.get("regulatoryCode")==null?"N/A":t.get("regulatoryCode").toString())
						 	.effectiveDate(effectiveDate)
						 	.policyEndDate(policyEndDate)
						 	.minrate(t.get("minimumRate")==null?0D:Double.parseDouble(t.get("minimumRate").toString()))
						 	.build();
				 
				 // Underwriter manual discount override if present
	                if ("Y".equalsIgnoreCase(getSafe(t, "uwDiscountYn"))) {
	                    String uwType = getSafe(t, "uwDiscountType");
	                    String uwDesc = getSafe(t, "uwDiscountDesc") == null ? "Underwriter Discount"
	                            : getSafe(t, "uwDiscountDesc");
	                    BigDecimal uwValue = getBigDecimalSafe(t, "uwDiscountValue");

	                    d.setDiscountDesc(uwDesc);
	                    d.setDiscountCalcType(uwType == null ? "P" : uwType);

	                    // percentage discount
	                    if ("P".equalsIgnoreCase(uwType)) {
	                        d.setDiscountRate(uwValue.toPlainString());
	                        d.setDiscountAmount(BigDecimal.ZERO);

	                    // amount discount
	                    } else if ("A".equalsIgnoreCase(uwType)) {
	                        d.setDiscountRate(uwValue.toPlainString());
	                        d.setDiscountAmount(uwValue);

	                    // Default case
	                    } else {
	                        d.setDiscountRate("0");
	                        d.setDiscountAmount(BigDecimal.ZERO);
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
	            
	            return null;
	        }
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
