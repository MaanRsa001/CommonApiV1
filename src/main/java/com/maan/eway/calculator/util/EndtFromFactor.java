package com.maan.eway.calculator.util;

import java.math.BigDecimal;
import java.util.function.Function;

import com.maan.eway.bean.FactorRateRequestDetails;
import com.maan.eway.res.calc.Endorsement;

public class EndtFromFactor  implements Function<FactorRateRequestDetails,Endorsement>{

	@Override
	public Endorsement apply(FactorRateRequestDetails t) {
		try {
			if("E".equalsIgnoreCase(t.getCoverageType())) {
				String calctype=t.getCalcType()==null?"":t.getCalcType();
				Endorsement d=Endorsement.builder()
						.endorsementDesc(t.getCoverName()==null?"":t.getCoverName())
						.endorsementId(t.getDiscLoadId()==null?"":t.getDiscLoadId().toString())
						.endorsementRate("F".equals(calctype)?0D: t.getRate()==null?0D:t.getRate().doubleValue())
						.endorsementCalcType(calctype)
						.endorsementforId(t.getDiscountCoverId()==null?"":t.getDiscountCoverId().toString())
						.maxAmount(t.getMinimumPremium()==null?BigDecimal.ZERO:t.getMinimumPremium())
						.factorTypeId(t.getFactorTypeId()==null?"":t.getFactorTypeId().toString())
						.regulatoryCode(t.getRegulatoryCode()==null?"N/A":t.getRegulatoryCode())	
						.premiumAfterDiscount(t.getPremiumAfterDiscountFc())
						.premiumAfterDiscountLC(t.getPremiumAfterDiscountLc())
						.premiumBeforeDiscount(t.getPremiumBeforeDiscountFc())
						.premiumBeforeDiscountLC(t.getPremiumBeforeDiscountLc())
						.premiumExcluedTax(t.getPremiumExcludedTaxFc())
						.premiumExcluedTaxLC(t.getPremiumExcludedTaxLc())
						.premiumIncludedTax(t.getPremiumIncludedTaxFc())
						.premiumIncludedTaxLC(t.getPremiumIncludedTaxLc())	 
						.endtCount(t.getEndtCount())
						.proRata(BigDecimal.ZERO)
						.proRataYn("N")

						.coverName(t.getCoverName())
						.minimumPremium(t.getMinimumPremium())
						.minimumPremiumYn(t.getMinimumPremiumYn())
						.isSubCover("N")
						.endorsementsumInsured(t.getSumInsured())
						.endorsementsumInsuredLc(t.getSumInsuredLc())
						.subCoverDesc("")
						.subCoverName("")
						.sectionId(String.valueOf(t.getSectionId()))
						.dependentCoveryn(t.getDependentCoverYn())
						.dependentCoverId(t.getDependentCoverId()==null?"":String.valueOf(t.getDependentCoverId()))
						.coverageType(t.getCoverageType())
						.isselected(t.getIsSelected())
						.userOpt(t.getUserOpt())
						.exchangeRate(t.getExchangeRate())
						.currency(t.getCurrency())
						.isReferral(t.getIsReferral())
						.referalDescription(t.getReferralDescription())
						.regulatoryCode(t.getRegulatoryCode())
						.tiraSumInsured(t.getRegulatorySuminsured())
						.tiraRate(t.getRegulatoryRate()==null?0D:t.getRegulatoryRate().doubleValue())
						.coverBasedOn(t.getCoverBasedOn())
						.insuranceId(t.getCompanyId())
						.productId(String.valueOf(t.getProductId()))
						.vehicleId(String.valueOf(t.getVehicleId()))
						.cdRefNo(t.getCdRefno())
						.vdRefNo(t.getVdRefno())
						.createdBy(t.getCreatedBy())
						.requestReferenceNo(t.getRequestReferenceNo())
						.multiSelectYn(t.getMultiSelectYn())
						.sectionId(String.valueOf(t.getSectionId()))
						.excessPercent(t.getExcessPercent())
						.excessAmount(t.getExcessAmount())
						.excessDesc(t.getExcessDesc())
						.effectiveDate(t.getCoverPeriodFrom())
						.policyEndDate(t.getCoverPeriodTo())
						.status("Y")
						.diffPremiumIncludedTax(BigDecimal.ZERO)
						.coverageLimit(t.getCoverageLimit())
						.diffPremiumIncludedTaxLC(BigDecimal.ZERO)
						.policyPeriod(t.getNoOfDays())
						.endtOpdt(t.getEndtoptd())
						.build();

				return d;
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

 

}
