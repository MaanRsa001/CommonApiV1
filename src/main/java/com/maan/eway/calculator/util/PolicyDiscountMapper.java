package com.maan.eway.calculator.util;

import java.math.BigDecimal;
import java.util.Date;
import java.util.function.Function;

import com.maan.eway.res.calc.Cover;

import jakarta.persistence.Tuple;

public class PolicyDiscountMapper implements Function<Tuple,Cover> {
	private Date effectiveDate;
	private Date policyEndDate;
	
	public PolicyDiscountMapper(Date effectiveDate, Date policyEndDate) {
		super();
		this.effectiveDate = effectiveDate;
		this.policyEndDate = policyEndDate;
	}

	@Override
	public Cover apply(Tuple t) {

		try {
			
			 if(t.get("coverageType")!=null  ) {
				 String subc=t.get("subCoverYn")==null?"N":t.get("subCoverYn").toString();
				 
				 // Referral 
				 String referal=(t.get("status")==null?"N":t.get("status").toString()).equals("R")?"Y":"N";
				 Cover c = Cover.builder()						 
					.calcType(t.get("calcType")==null?"":t.get("calcType").toString())
					.coverId(t.get("coverId")==null?"":t.get("coverId").toString())
					.coverDesc(t.get("coverDesc")==null?"":t.get("coverDesc").toString())
					.coverName(t.get("coverName")==null?"":t.get("coverName").toString())
					.minimumPremium(t.get("minPremium")==null?BigDecimal.ZERO:new BigDecimal(t.get("minPremium").toString()))
					.coverToolTip(t.get("toolTip")==null?"":t.get("toolTip").toString())
					.isSubCover( t.get("subCoverYn")==null?"N":t.get("subCoverYn").toString())
					.sumInsuredLc(BigDecimal.ZERO)
					.sumInsured(BigDecimal.ZERO)
					.rate(t.get("baseRate")==null?0D:Double.parseDouble(t.get("baseRate").toString()))
					.subCoverId( (t.get("subCoverId")==null || "N".equals(subc) )?null:t.get("subCoverId").toString())
					.subCoverDesc("Y".equals(subc)?(t.get("subCoverDesc")==null?"":t.get("subCoverDesc").toString()):null)
					.subCoverName("Y".equals(subc)?(t.get("subCoverName")==null?"":t.get("subCoverName").toString()):null)
					.factorTypeId(t.get("factorTypeId")==null?"":t.get("factorTypeId").toString())
					.dependentCoveryn(t.get("dependentCoverYn")==null?"N":t.get("dependentCoverYn").toString())
					.dependentCoverId(t.get("discountCoverId")==null?"":t.get("discountCoverId").toString())
					.coverageType(t.get("coverageType")==null?"NA":t.get("coverageType").toString())
					.isselected(t.get("isSelectedYn")==null?"N":t.get("isSelectedYn").toString())
					.isReferral(referal)
					.referalDescription("Y".equals(referal)?(t.get("coverDesc")==null?"":t.get("coverDesc").toString()+" Referral"):"")
					.coverBasedOn(t.get("coverBasedOn")==null?"sumInsured":t.get("coverBasedOn").toString())
					.sectionId(t.get("sectionId")==null?"":t.get("sectionId").toString())
					//.exchangeRate(t.get("isSelectedYn")==null?0D:t.get("isSelectedYn").toString())
					/*	.premiumBeforeDiscount(new BigDecimal(t.get("coverId").toString()))
					.premiumAfterDiscount(new BigDecimal(t.get("coverId").toString()))
					.premiumExcluedTax(new BigDecimal(t.get("coverId").toString()))
					.premiumIncludedTax(new BigDecimal(t.get("coverId").toString()))*/
					.regulatoryCode(t.get("regulatoryCode")==null?"N/A":t.get("regulatoryCode").toString())
					.multiSelectYn(t.get("multiSelectYn")==null?"N":t.get("multiSelectYn").toString())
					.excessAmount(t.get("excessAmount")==null?BigDecimal.ZERO:new BigDecimal(t.get("excessAmount").toString()))
					.excessDesc(t.get("excessDesc")==null?"N":t.get("excessDesc").toString())
					.excessPercent(t.get("excessPercent")==null?BigDecimal.ZERO:new BigDecimal(t.get("excessPercent").toString()))
					.minimumPremiumYn("N")
					.proRataYn(t.get("proRataYn")==null?"N":t.get("proRataYn").toString())
					.endtCount(BigDecimal.ZERO)
					.effectiveDate(effectiveDate)
					.policyEndDate(policyEndDate)
					.coverageLimit(t.get("coverageLimit")==null?BigDecimal.ZERO:new BigDecimal(t.get("coverageLimit").toString()))
					.status("Y")
					.minSumInsured(t.get("minSuminsured")==null?BigDecimal.ZERO:new BigDecimal(t.get("minSuminsured").toString()))
					.isTaxExcempted(t.get("isTaxExcempted")==null?"N":t.get("isTaxExcempted").toString())					
					.build();
				return c;
			 }			
		}catch (Exception e) {
			
			System.out.println("cccccccc"+t.get("coverId").toString());
			e.printStackTrace();
		}
		return null;
	
	}

}
