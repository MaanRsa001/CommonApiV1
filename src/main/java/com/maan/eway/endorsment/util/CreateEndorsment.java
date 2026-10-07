	package com.maan.eway.endorsment.util;

	import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.maan.eway.bean.EndtTypeMaster;
import com.maan.eway.bean.PolicyCoverData;
import com.maan.eway.bean.PolicyCoverDataEndt;
import com.maan.eway.req.calcengine.CalcEngine;
import com.maan.eway.res.calc.Endorsement;
import com.maan.eway.res.calc.Tax;

	public class CreateEndorsment {
		private EndtTypeMaster endtmaster;
		private BigDecimal endtCount;
		private List<Tax> taxey;
		List<PolicyCoverDataEndt> coverData;
		private PolicyCoverData currentData;
		private CalcEngine engine;
		private Date policyEndDate;
		public CreateEndorsment(EndtTypeMaster endtmaster,BigDecimal endtCount, List<Tax> taxey, List<PolicyCoverDataEndt> coverData, PolicyCoverData currentData, Date policyEndDate,CalcEngine engine) {
			 this.endtmaster=endtmaster;
			 this.endtCount=endtCount;
			 this.taxey=taxey;
			 this.coverData=coverData;
			 this.currentData=currentData;
			 this.policyEndDate=policyEndDate;
			 this.engine=engine;
		}

		public Endorsement create() {
			// CurrentEndorsement

			String endtTypeId=String.valueOf(endtmaster.getEndtTypeId());
			coverData.sort(new Comparator<PolicyCoverDataEndt>() {

				@Override
				public int compare(PolicyCoverDataEndt o1, PolicyCoverDataEndt o2) {
					// TODO Auto-generated method stub
					return (o1.getEndtCount().compareTo(o2.getEndtCount()));
				}
			}.reversed());
			PolicyCoverDataEndt d = coverData.get(0);
			
			BigDecimal totalSumInsured=coverData.stream().map(x -> x.getSumInsured()).reduce(BigDecimal.ZERO,BigDecimal::add);
			
			//totalSumInsured=currentData.getSumInsured().subtract(totalSumInsured, MathContext.DECIMAL32);
			Endorsement currentEndt = Endorsement.builder()
					.endorsementDesc(d.getCoverDesc())
					.endorsementId(endtTypeId)
					.endorsementRate( /*"A".equals(d.getCalcType())? 0D:*/d.getRate().doubleValue())
					.endorsementCalcType(d.getCalcType())
					.endorsementforId(String.valueOf(d.getCoverId()))
					.maxAmount(BigDecimal.ZERO)
					.factorTypeId(null)
					.regulatoryCode("N/A")
					.endtCount(endtCount) 
					.premiumAfterDiscount(d.getPremiumAfterDiscountFc())
					.premiumAfterDiscountLC(d.getPremiumAfterDiscountLc())
					.premiumBeforeDiscount(d.getPremiumBeforeDiscountFc())
					.premiumBeforeDiscountLC(d.getPremiumBeforeDiscountLc())
					.premiumExcluedTax(d.getPremiumExcludedTaxFc())
					.premiumExcluedTaxLC(d.getPremiumExcludedTaxLc())
					.premiumIncludedTax(d.getPremiumIncludedTaxFc())
					.premiumIncludedTaxLC(d.getPremiumIncludedTaxLc())
					.proRata(d.getProRataPercent())
					.proRataYn(d.getProRataYn())
					.coverName(d.getCoverName())
					.minimumPremium(d.getMinimumPremium())				
					.minimumPremiumYn(d.getMinimumPremiumYn())
					.isSubCover("N")
					.endorsementsumInsured(totalSumInsured)
					.endorsementsumInsuredLc(totalSumInsured.multiply(d.getExchangeRate(),MathContext.DECIMAL64))
					.subCoverDesc("")
					.subCoverName("")
					.sectionId(String.valueOf(d.getSectionId()))
					.dependentCoveryn(d.getDependentCoverYn())
					.dependentCoverId(d.getDependentCoverId()==null?"":String.valueOf(d.getDependentCoverId()))
					.coverageType("E")
					.isselected("D")
					.userOpt("Y")
					.exchangeRate(d.getExchangeRate())
					.currency(d.getCurrency())
					.isReferral(d.getIsReferral())
					.referalDescription(d.getReferralDescription())
					.regulatoryCode(d.getRegulatoryCode())
					.tiraSumInsured(d.getRegulatorySuminsured())
					.tiraRate(d.getRegulatoryRate()==null?0D:d.getRegulatoryRate().doubleValue())
					.coverBasedOn(d.getCoverBasedOn())
					.insuranceId(d.getCompanyId())
					.productId(String.valueOf(d.getProductId()))
					.vehicleId(String.valueOf(d.getVehicleId()))
					.cdRefNo(d.getCdRefno())
					.vdRefNo(d.getVdRefno())
					.createdBy(d.getCreatedBy())
					.requestReferenceNo(d.getRequestReferenceNo())
					.multiSelectYn(d.getMultiSelectYn())
					.sectionId(String.valueOf(d.getSectionId()))
					.excessPercent(d.getExcessPercent())
					.excessAmount(d.getExcessAmount())
					.excessDesc(d.getExcessDesc())
					.effectiveDate(d.getCoverPeriodFrom())
	//				.policyEndDate(d.getCoverPeriodTo())
					.policyEndDate(policyEndDate)
					.status("Y")
					.diffPremiumIncludedTax(BigDecimal.ZERO)
					.coverageLimit(d.getCoverageLimit())
					.diffPremiumIncludedTaxLC(BigDecimal.ZERO)
					.policyPeriod(new BigDecimal(d.getNoOfDays()))
					.endtOpdt(StringUtils.isNotBlank(engine.getEndtOpdt()) ? engine.getEndtOpdt():null)
					.build();
			

			{

				taxey.stream().forEach(t -> t.setEndtTypeId(endtTypeId + ""));
				taxey.stream().forEach(t -> t.setEndtTypeCount(endtCount));
				taxey.stream().forEach(t -> t.setTaxDesc(t.getTaxDesc()));
				if ("Y".equals(endtmaster.getEndtFeeYn())) {
					Tax tax = Tax.builder()
							.calcType(endtmaster.getCalcTypeId())
							.isTaxExempted("N")
							.regulatoryCode("N/A")
							.taxAmount(BigDecimal.ZERO)
							.taxDesc(" Endorsement Fee")
							.taxExemptCode(null)
							.taxRate(Double.parseDouble(endtmaster.getEndtFeePercent()))
							.taxId(endtTypeId + "")
							.endtTypeId(endtTypeId + "")
							.endtTypeCount(endtCount)
							.taxFor("NB")
							.build();
					taxey.add(tax);
				}

				currentEndt.setTaxes(taxey);
			}
			return currentEndt;
		}
	}
