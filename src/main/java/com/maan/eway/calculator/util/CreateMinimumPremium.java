package com.maan.eway.calculator.util;

import java.math.BigDecimal;
import java.util.List;

import com.maan.eway.req.calcengine.CalcEngine;
import com.maan.eway.res.calc.Cover;
import com.maan.eway.res.calc.Tax;


public class CreateMinimumPremium {

	private BigDecimal minimumPremium;
	private CalcEngine engine;
	private BigDecimal endtCount;
	private List<Tax> taxey;
	
	
	
	public CreateMinimumPremium(BigDecimal minimumPremium, CalcEngine engine, BigDecimal endtCount,  List<Tax> taxey) {
		super();
		this.minimumPremium = minimumPremium;
		this.engine = engine;
		this.endtCount = endtCount;
		this.taxey = taxey;
	}



	public Cover create() {

		// CurrentEndorsement
		Cover c = Cover.builder()						 
				.calcType("A")
				.coverId("945")
				.coverDesc("Minimum Premium Adjustment")
				.coverName("Minimum Premium Adjustment")
				.minimumPremium(minimumPremium)
				.coverToolTip("Adjustment Cover for Minimum Premium")
				.isSubCover( "N")
				.sumInsuredLc(BigDecimal.ZERO)
				.sumInsured(BigDecimal.ZERO)
				.rate(minimumPremium.doubleValue())
				.subCoverId( null)
				.subCoverDesc(null)
				.subCoverName(null)
				.factorTypeId("")
				.dependentCoveryn("N")
				.dependentCoverId("")
				.coverageType("O")
				.isselected("D")
				.isReferral("N")
				.referalDescription("")
				.coverBasedOn("sumInsured")
				.sectionId(engine.getSectionId())
				.userOpt("Y")
				//.exchangeRate(t.get("isSelectedYn")==null?0D:t.get("isSelectedYn").toString())
				/*	.premiumBeforeDiscount(new BigDecimal(t.get("coverId").toString()))
				.premiumAfterDiscount(new BigDecimal(t.get("coverId").toString()))
				.premiumExcluedTax(new BigDecimal(t.get("coverId").toString()))
				.premiumIncludedTax(new BigDecimal(t.get("coverId").toString()))*/
				.regulatoryCode("N/A")
				.multiSelectYn("N")
				.excessAmount(BigDecimal.ZERO)
				.excessDesc("N/A")
				.excessPercent(BigDecimal.ZERO)
				.minimumPremiumYn("Y")
				.proRataYn("N")
				.endtCount(endtCount)
				.effectiveDate(engine.getEffectiveDate())
				.policyEndDate(engine.getPolicyEndDate())
				.coverageLimit(BigDecimal.ONE)
				.status("Y")
				.minSumInsured(BigDecimal.ZERO)
				.taxes(taxey)
				.vdRefNo(engine.getVdRefNo())
				.vehicleId(engine.getVehicleId())
				.requestReferenceNo(engine.getRequestReferenceNo())
				.productId(engine.getProductId())
				.msrefno(engine.getMsrefno())
				.insuranceId(engine.getInsuranceId())
				.diffPremiumIncludedTax(BigDecimal.ZERO)
				.diffPremiumIncludedTaxLC(BigDecimal.ZERO)
				.createdBy(engine.getCreatedBy())
				.cdRefNo(engine.getCdRefNo())
				.branchCode(engine.getBranchCode())
				.agencyCode(engine.getAgencyCode())
				.tiraRate(0D)
				.freeCoverLimit(BigDecimal.ZERO)
				.build();
  		

	
		return c;
	
	}
}
