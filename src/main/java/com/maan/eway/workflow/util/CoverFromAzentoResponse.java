package com.maan.eway.workflow.util;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

import com.maan.eway.res.calc.Cover;

public class CoverFromAzentoResponse implements Function<Map<String, Object>,Cover> {
	private String coverageType;
	private String exchangeRateStr; 
	public CoverFromAzentoResponse(String coverageType,String exchangeRateStr) {
		super();
		this.coverageType = coverageType;
		this.exchangeRateStr=exchangeRateStr;
	}

	@Override
	public Cover apply(Map<String, Object> t) {
		try {
			
			 
			  Cover c = Cover.builder()						 
					.calcType(t.get("chargeRatePer")==null?"A":"100".equals(t.get("chargeRatePer").toString())?"P":"A")
					.coverId(t.get("coverageCode")==null?"":t.get("coverageCode").toString())
					.coverDesc(t.get("coverageName")==null?"":t.get("coverageName").toString())
					.coverName(t.get("coverageName")==null?"":t.get("coverageName").toString())
					.minimumPremium(BigDecimal.ZERO)
					.coverToolTip("")
					.isSubCover("N")
					.sumInsuredLc((t.get("coverageLimit")==null || "null".equals(t.get("coverageLimit")))?BigDecimal.ZERO:new BigDecimal(t.get("coverageLimit").toString()))
					.sumInsured((t.get("coverageLimit")==null || "null".equals(t.get("coverageLimit")))?BigDecimal.ZERO:new BigDecimal(t.get("coverageLimit").toString()))
					.rate(t.get("coverBasicRate")==null?0D:Double.parseDouble(t.get("coverBasicRate").toString()))
					.subCoverId(null)
					.subCoverDesc(null)
					.subCoverName(null)
					.factorTypeId("")
					.dependentCoveryn("N")
					.dependentCoverId("")
					.coverageType(coverageType)
					.isselected(!"B".equals(coverageType)?t.get("addCoverYN")==null?"N":t.get("addCoverYN").toString():"D")
					.isReferral("N")
					.referalDescription("")
					.coverBasedOn("sumInsured")
					.sectionId(t.get("sectionId")==null?"":t.get("sectionId").toString()) 
					.regulatoryCode(t.get("coverageCode")==null?"N/A":t.get("coverageCode").toString())
					.multiSelectYn("N")
					.excessAmount(BigDecimal.ZERO)
					.excessDesc("N")
					.excessPercent(BigDecimal.ZERO)
					.minimumPremiumYn("N")
					.proRataYn("N")
					.endtCount(BigDecimal.ZERO)
					.effectiveDate(convertDate(t.get("coverageStartDate").toString()))
					.policyEndDate(convertDate(t.get("coverageEndDate").toString()))
					.coverageLimit((t.get("coverageLimit")==null || "null".equals(t.get("coverageLimit")))?BigDecimal.ZERO:new BigDecimal(t.get("coverageLimit").toString()))
					.status("Y")
					.minSumInsured(BigDecimal.ZERO)
					.isTaxExcempted("N")
					.freeCoverLimit(BigDecimal.ZERO)
					.coverDescLocal(t.get("coverageName")==null?"":t.get("coverageName").toString())
					.coverNameLocal(t.get("coverageName")==null?"":t.get("coverageName").toString())
					.subCoverDescLocal("")
					.subCoverNameLocal("")
					.minrate(t.get("minRate")==null?0D:Double.parseDouble(t.get("minRate").toString()))
					.minimumRateYn("N")
					.exchangeRate(t.get("")==null?BigDecimal.ZERO:new BigDecimal(t.get("").toString()))
					.premiumAfterDiscount(t.get("coveragePremium")==null?BigDecimal.ZERO:new BigDecimal(t.get("coveragePremium").toString()))
					.premiumAfterDiscountLC(t.get("coveragePremium")==null?BigDecimal.ZERO:new BigDecimal(t.get("coveragePremium").toString()))
					.premiumBeforeDiscount(t.get("coveragePremium")==null?BigDecimal.ZERO:new BigDecimal(t.get("coveragePremium").toString()))
					.premiumBeforeDiscountLC(t.get("coveragePremium")==null?BigDecimal.ZERO:new BigDecimal(t.get("coveragePremium").toString()))
					.premiumExcluedTax(t.get("coveragePremium")==null?BigDecimal.ZERO:new BigDecimal(t.get("coveragePremium").toString()))
					.premiumExcluedTaxLC(t.get("coveragePremium")==null?BigDecimal.ZERO:new BigDecimal(t.get("coveragePremium").toString()))
					.premiumIncludedTax(t.get("coveragePremium")==null?BigDecimal.ZERO:new BigDecimal(t.get("coveragePremium").toString()))
					.premiumIncludedTaxLC(t.get("coveragePremium")==null?BigDecimal.ZERO:new BigDecimal(t.get("coveragePremium").toString()))
					.exchangeRate(new BigDecimal(exchangeRateStr))
					.build();
			
			return c; 
		}catch(Exception e) {
			e.printStackTrace();
		}
 		return null;
	}

	private Date convertDate(String dateString) {
		
		SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.S");
		try {
			Date date = formatter.parse(dateString);
			System.out.println("Converted Date: " + date);
			return date;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
