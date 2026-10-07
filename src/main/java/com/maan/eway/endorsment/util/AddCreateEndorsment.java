package com.maan.eway.endorsment.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.maan.eway.bean.EndtTypeMaster;
import com.maan.eway.bean.PolicyCoverData;
import com.maan.eway.req.calcengine.CalcEngine;
import com.maan.eway.res.calc.Cover;
import com.maan.eway.res.calc.Endorsement;
import com.maan.eway.res.calc.Tax;

public class AddCreateEndorsment {
	
	private EndtTypeMaster endtmaster;
	private BigDecimal endtCount;
	private List<Tax> taxey;
	List<Cover> coverData;
	private PolicyCoverData currentData;
	private Date policyEndDate;
	private String coverid;
	private Long noofdays;
	private String key;
	private String addcover;
	private CalcEngine engine;
	public AddCreateEndorsment(EndtTypeMaster endtmaster,BigDecimal endtCount, List<Tax> taxey, List<Cover> coverData, 
			Date policyEndDate,String coverid,Long noofdays,String key,String addcover,CalcEngine engine) {
		 this.endtmaster=endtmaster;
		 this.endtCount=endtCount;
		 this.taxey=taxey;
		 this.coverData=coverData;
		 this.policyEndDate=policyEndDate;
		 this.coverid=coverid;
		 this.noofdays=noofdays;
		 this.key=key;
		 this.addcover=addcover;
		 this.engine=engine;
	}
	
	public Endorsement create() {
		// CurrentEndorsement
		Cover d = null;

		List<Cover> dList = coverData.stream().filter(t -> t.getCoverId().equalsIgnoreCase(coverid))
				.collect(Collectors.toList());
		d = dList.get(0);
		
		BigDecimal totalSumInsured = d.getSumInsured();
		String endtTypeId = String.valueOf(endtmaster.getEndtTypeId());
		System.out.println("totalSumInsured========> " + totalSumInsured);
		Endorsement currentEndt = Endorsement.builder()
				.endorsementDesc(d.getCoverDesc())
				.endorsementId(endtTypeId)
				.endorsementRate( /* "A".equals(d.getCalcType())? 0D: */d.getRate().doubleValue())
				.endorsementCalcType(d.getCalcType())
				.endorsementforId(String.valueOf(d.getCoverId()))
				.maxAmount(BigDecimal.ZERO).factorTypeId(null)
				.regulatoryCode("N/A").endtCount(endtCount)
				.premiumAfterDiscount(d.getPremiumAfterDiscount())
				.premiumAfterDiscountLC(d.getPremiumAfterDiscountLC())
				.premiumBeforeDiscount(d.getPremiumBeforeDiscount())
				.premiumBeforeDiscountLC(d.getPremiumBeforeDiscountLC())
				.premiumExcluedTax(d.getPremiumExcluedTax())
				.premiumExcluedTaxLC(d.getPremiumExcluedTaxLC())
				.premiumIncludedTax(d.getPremiumIncludedTax())
				.premiumIncludedTaxLC(d.getPremiumIncludedTaxLC())
				.proRata(d.getProRata())
				.proRataYn(d.getProRataYn())
				.coverName(d.getCoverName())
				.minimumPremium(d.getMinimumPremium())
				.minimumPremiumYn(d.getMinimumPremiumYn())
				.isSubCover("N")
				.endorsementsumInsured(totalSumInsured)
				.endorsementsumInsuredLc(totalSumInsured.multiply(d.getExchangeRate(), MathContext.DECIMAL64))
				.subCoverDesc("")
				.subCoverName("")
				.sectionId(String.valueOf(d.getSectionId()))
				.dependentCoveryn(d.getDependentCoveryn())
				.dependentCoverId(d.getDependentCoverId() == null ? "" : String.valueOf(d.getDependentCoverId()))
				.coverageType("E")
				.isselected(key)
				.userOpt(key)
				.exchangeRate(d.getExchangeRate())
				.currency(d.getCurrency()).isReferral(d.getIsReferral())
				.referalDescription(d.getReferalDescription())
				.regulatoryCode(d.getRegulatoryCode())
				.tiraSumInsured(d.getTiraSumInsured())
				.tiraRate(d.getTiraRate() == null ? 0D : d.getTiraRate().doubleValue())
				.coverBasedOn(d.getCoverBasedOn()).insuranceId("").productId(String.valueOf(d.getProductId()))
				.vehicleId(String.valueOf(d.getVehicleId()))
				.cdRefNo(d.getCdRefNo()).vdRefNo(d.getVdRefNo())
				.createdBy(d.getCreatedBy()).requestReferenceNo(d.getRequestReferenceNo())
				.multiSelectYn(d.getMultiSelectYn())
				.sectionId(String.valueOf(d.getSectionId()))
				.excessPercent(d.getExcessPercent())
				.excessAmount(d.getExcessAmount()).excessDesc(d.getExcessDesc())
				.effectiveDate(d.getEffectiveDate())
//				.policyEndDate(d.getCoverPeriodTo())
				.policyEndDate(policyEndDate)
				.status("Y")
				.diffPremiumIncludedTax(BigDecimal.ZERO)
				.coverageLimit(d.getCoverageLimit()).diffPremiumIncludedTaxLC(BigDecimal.ZERO)
				.policyPeriod(d.getPolicyPeriod())
				.addcover(addcover)
				.endtOpdt(engine.getEndtOpdt())
				.build();

		{

			taxey.stream().forEach(t -> t.setEndtTypeId(endtTypeId + ""));
			taxey.stream().forEach(t -> t.setEndtTypeCount(endtCount));
			taxey.stream().forEach(t -> t.setTaxDesc(t.getTaxDesc()));
			if ("Y".equals(endtmaster.getEndtFeeYn())) {
				Tax tax = Tax.builder().calcType(endtmaster.getCalcTypeId()).isTaxExempted("N").regulatoryCode("N/A")
						.taxAmount(BigDecimal.ZERO).taxDesc(" Endorsement Fee").taxExemptCode(null)
						.taxRate(Double.parseDouble(endtmaster.getEndtFeePercent())).taxId(endtTypeId + "")
						.endtTypeId(endtTypeId + "").endtTypeCount(endtCount).taxFor("NB").build();
				taxey.add(tax);
			}

			currentEndt.setTaxes(taxey);
		}
		return currentEndt;
	}

}
