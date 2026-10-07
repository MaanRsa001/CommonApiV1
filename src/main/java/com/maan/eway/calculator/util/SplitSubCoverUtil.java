package com.maan.eway.calculator.util;

import java.math.BigDecimal;
import java.util.Date;
import java.util.function.Function;

import com.maan.eway.res.calc.Cover;

import jakarta.persistence.Tuple;

public class SplitSubCoverUtil implements Function<Tuple,Cover> {

	private String filterBy;
	
	private Date effectiveDate;
	private Date policyEndDate;
	
	public SplitSubCoverUtil(String filterBy, Date effectiveDate, Date policyEndDate) {
		super();
		this.filterBy = filterBy;
		this.effectiveDate=effectiveDate;
		this.policyEndDate=policyEndDate;
		this.policyEndDate.setHours(23);
		this.policyEndDate.setMinutes(59);
	}


	@Override
	public Cover apply(Tuple t) {
		try {
			
			 if(t.get("coverageType")!=null && !("D".equalsIgnoreCase(t.get("coverageType").toString()) || "L".equalsIgnoreCase(t.get("coverageType").toString()) || "P".equalsIgnoreCase(t.get("coverageType").toString()) ) &&  filterBy.equalsIgnoreCase(t.get("subCoverYn").toString())) {
				 String subc=t.get("subCoverYn")==null?"N":t.get("subCoverYn").toString();
				 
				 // Referral 
				 String referal=(t.get("status")==null?"N":t.get("status").toString()).equals("R")?"Y":"N";
				 System.out.println("<==========set cover details====>");
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
					.dependentCoverId(t.get("dependentCoverId")==null?"":t.get("dependentCoverId").toString())
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
					.freeCoverLimit(t.get("freeCoverLimit")==null?BigDecimal.ZERO:new BigDecimal(t.get("freeCoverLimit").toString()))
					.coverDescLocal(t.get("coverDescLocal")==null?"":t.get("coverDescLocal").toString())
					.coverNameLocal(t.get("coverNameLocal")==null?"":t.get("coverNameLocal").toString())
					.subCoverDescLocal(t.get("subCoverDescLocal")==null?"":t.get("subCoverDescLocal").toString())
					.subCoverNameLocal(t.get("subCoverNameLocal")==null?"":t.get("subCoverNameLocal").toString())
					.minrate(t.get("minimumRate")==null?0D:Double.parseDouble(t.get("minimumRate").toString()))
					.minimumRateYn(t.get("minimumRateYn")==null?"N":t.get("minimumRateYn").toString())
					.dependentCoveSIorPI(t.get("dependentCoverSIorPRE")==null?"":t.get("dependentCoverSIorPRE").toString())
					.endtProRataYn(t.get("endtProRataYn")==null?"":t.get("endtProRataYn").toString())
					.endtProRataDesc(t.get("endtProRataDesc")==null?"":t.get("endtProRataDesc").toString())
					.coverAgetype(t.get("coverageType")==null?"":t.get("coverageType").toString())
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
