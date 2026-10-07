package com.maan.eway.calculator.util;

import java.math.BigDecimal;
import java.util.List;

import com.maan.eway.req.calcengine.CalcEngine;
import com.maan.eway.res.calc.Cover;
import com.maan.eway.res.calc.Tax;

public class CreatePolicyPremium {
	protected CalcEngine engine;
	private List<Tax> taxey;
	
	
	
	public CreatePolicyPremium(CalcEngine engine, List<Tax> taxey) {
		super();
		this.engine = engine;
		this.taxey = taxey;
	}



	public Cover create() {

		// CurrentEndorsement
		Cover c = Cover.builder()						 
				.calcType("A")
				.coverId("1945")
				.coverDesc("Over All Premium")
				.coverName("Over All Premium")
				.minimumPremium(BigDecimal.ZERO)
				.coverToolTip("Over All Premium")
				.isSubCover( "N")
				.sumInsuredLc(BigDecimal.ZERO)
				.sumInsured(BigDecimal.ZERO)
				//.rate(0D)
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
				.sectionId("99999")
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
				.endtCount(BigDecimal.ZERO)
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
				.build();
  		

	
		return c;
	
	}
}
