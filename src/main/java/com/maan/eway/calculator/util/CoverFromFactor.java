package com.maan.eway.calculator.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.function.Function;

import com.maan.eway.bean.FactorRateRequestDetails;
import com.maan.eway.res.calc.Cover;

public class CoverFromFactor implements Function<FactorRateRequestDetails,Cover> {

	private String filterBy;
	
	
	public CoverFromFactor(String filterBy) {
		super();
		this.filterBy = filterBy;
	}


	@Override
	public Cover apply(FactorRateRequestDetails t) {
		try {
			
			 if(!("D".equalsIgnoreCase(t.getCoverageType()) ||
					 "L".equalsIgnoreCase(t.getCoverageType()) || 
					 "E".equalsIgnoreCase(t.getCoverageType()) ||
					 "P".equalsIgnoreCase(t.getCoverageType()) ||
					 "T".equalsIgnoreCase(t.getCoverageType()) ) &&  filterBy.equalsIgnoreCase(t.getSubCoverYn())) {
				 String subc=t.getSubCoverYn()==null?"N":t.getSubCoverYn();
				 
				 t.getCoverPeriodTo().setHours(23);
				 t.getCoverPeriodTo().setMinutes(59);
				 // Referral 
				 String referal=t.getIsReferral();
				 Cover c = Cover.builder()						 
					.calcType(t.getCalcType()==null?"":t.getCalcType())
					.coverId(t.getCoverId()==null?"":t.getCoverId().toString())
					.coverDesc(t.getCoverDesc()==null?"":t.getCoverDesc())
					.coverName(t.getCoverName()==null?"":t.getCoverName())
					.minimumPremium(t.getMinimumPremium()==null?BigDecimal.ZERO:t.getMinimumPremium())
					//.coverToolTip(t.get ==null?"":t.get("toolTip").toString())
					.isSubCover( t.getSubCoverYn()==null?"N":t.getSubCoverYn())
					.sumInsuredLc(BigDecimal.ZERO)
					//.sumInsured(BigDecimal.ZERO)
					.sumInsured(t.getSumInsured())
					.rate(t.getRate()==null?0D: t.getRate().doubleValue() )
					.subCoverId( (t.getSubCoverId()==null || "N".equals(subc) )?null:t.getSubCoverId().toString())
					.subCoverDesc("Y".equals(subc)?(t.getSubCoverDesc()==null?"":t.getSubCoverDesc()):null)
					.subCoverName("Y".equals(subc)?(t.getSubCoverName()==null?"":t.getSubCoverName()):null)
					.factorTypeId(t.getFactorTypeId()==null?"":t.getFactorTypeId().toString())
					.dependentCoveryn(t.getDependentCoverYn()==null?"N":t.getDependentCoverYn())
					.dependentCoverId(t.getDependentCoverId()==null?"":t.getDependentCoverId().toString())
					.coverageType(t.getCoverageType()==null?"NA":t.getCoverageType().toString())
					.isselected(t.getIsSelected()==null?"N":t.getIsSelected().toString())
					.isReferral(referal)
					.referalDescription("Y".equals(referal)?(t.getCoverDesc()==null?"":t.getCoverDesc().toString()+" Referral"):"")
					.userOpt(t.getUserOpt())
					.coverBasedOn(t.getCoverBasedOn())
					//.exchangeRate(t.get("isSelectedYn")==null?0D:t.get("isSelectedYn").toString())
					/*	.premiumBeforeDiscount(new BigDecimal(t.get("coverId").toString()))
					.premiumAfterDiscount(new BigDecimal(t.get("coverId").toString()))
					.premiumExcluedTax(new BigDecimal(t.get("coverId").toString()))
					.premiumIncludedTax(new BigDecimal(t.get("coverId").toString()))*/
					.regulatoryCode(t.getRegulatoryCode()==null?"N/A":t.getRegulatoryCode())
					.multiSelectYn(t.getMultiSelectYn()==null?"N":t.getMultiSelectYn())
					.excessAmount(t.getExcessAmount()==null?BigDecimal.ZERO:t.getExcessAmount())
					.excessDesc(t.getExcessDesc()==null?"":t.getExcessDesc())
					.excessPercent(t.getExcessPercent()==null?BigDecimal.ZERO:t.getExcessPercent())
					.minimumPremiumYn(t.getMinimumPremiumYn())
					.proRataYn(t.getProRataYn())
					.proRata(t.getProRataPercent().divide(new BigDecimal("100"), MathContext.DECIMAL32))
					.endtCount(t.getEndtCount())
					.status(t.getStatus())
					.effectiveDate(t.getCoverPeriodFrom())
					.policyEndDate(t.getCoverPeriodTo())
					.coverageLimit(t.getCoverageLimit())
					.tiraSumInsured(t.getRegulatorySuminsured())
					.tiraRate(t.getActualRate() ==null?(t.getRate()==null?0D: t.getRate().doubleValue() ):t.getActualRate().doubleValue())
					.minSumInsured(t.getMinCoverageLimit()==null?BigDecimal.ZERO:t.getMinCoverageLimit())
					.policyPeriod(t.getNoOfDays())
					.isTaxExcempted(t.getIsTaxExtempted()==null?"N":t.getIsTaxExtempted())
					.freeCoverLimit(t.getFreeCoverLimit()==null?BigDecimal.ZERO:t.getFreeCoverLimit())
					.coverDescLocal(t.getCoverDescLocal()==null?"":t.getCoverDescLocal().toString())
					.coverNameLocal( t.getCoverNameLocal()==null?"": t.getCoverNameLocal().toString())
					.subCoverDescLocal(t.getSubCoverDescLocal()==null?"":t.getSubCoverDescLocal())
					.subCoverNameLocal(t.getSubCoverNameLocal()==null?"":t.getSubCoverNameLocal().toString())
					.minrate(t.getMinimumRate() ==null?0D:t.getMinimumRate().doubleValue())
					.minimumRateYn(t.getMinimumRateYn() ==null?"N":t.getMinimumRateYn())
					.dependentCoveSIorPI(t.getDependentCoverSIorPRE() ==null?"":t.getDependentCoverSIorPRE())
					.endtProRataYn(t.getEndtProRataYn() ==null?"":t.getEndtProRataYn())
					.endtProRataDesc(t.getEndtProRataDesc() ==null?"":t.getEndtProRataDesc())
					.build();
				return c;
			 }			
		}catch (Exception e) {
			
			System.out.println("cccccccc"+t.getCoverId()==null?"":t.getCoverId().toString());
			e.printStackTrace();
		}
		return null;
	}

	 
}
