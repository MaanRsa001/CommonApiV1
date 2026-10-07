package com.maan.eway.thirdparty;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Function;

import com.maan.eway.req.calcengine.CalcEngine;
import com.maan.eway.res.calc.Cover;
import com.maan.eway.res.calc.Tax;
import com.maan.eway.thirdparty.response.Charge;
import com.maan.eway.thirdparty.response.UpsellPlan;

public class CoverAvailableUpsellPlan implements Function<UpsellPlan,Cover> {
	private CalcEngine engine;
	
	public CoverAvailableUpsellPlan(CalcEngine engine) {
		super();
		this.engine = engine;
	}
	private int twoDigitRandom() {
		try {
			Random random = new Random(); 
			int randomTwoDigitNumber = 10 + random.nextInt(90);
			return randomTwoDigitNumber;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return 17;
	}
	@Override
	public Cover apply(UpsellPlan t) {
		try {
			Integer coverId =twoDigitRandom();
					//new BigInteger(t.getSSRFeeCode().getBytes()).intValue();
			 Optional<Charge> isTax = t.getPlanPricingBreakdown().getPricingBreakdown().getPremiumBreakdown().getPremiumCharges().getCharges().getCharge()
					.stream().filter(x-> !"1".equals(x.getSequenceNo())).findFirst();
			 Charge charge =isTax.isPresent()?isTax.get():null;
			 
			 
			 BigDecimal totalPremium = new BigDecimal(t.getTotalPremiumAmount());
			    BigDecimal taxRate = new BigDecimal(charge.getPercentageValue()); 

			    BigDecimal divisor = BigDecimal.ONE.add(taxRate.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
			    BigDecimal premiumExclTax = totalPremium.divide(divisor, 2, RoundingMode.HALF_UP);

			    BigDecimal taxAmount = totalPremium.subtract(premiumExclTax);
			    
			 List<Tax> listTax=new ArrayList<Tax>();
			 if(charge!=null) {
				 Tax d=Tax.builder()
						 	.isTaxExempted(null)
						 	.taxAmount(taxAmount)
						 	.taxDesc(charge.getRateType())
						 	.taxExemptCode(null)
						 	.taxExemptType(null)
						 	.endtTypeId(null)					
						 	.taxId(charge.getSequenceNo())
						 	.taxRate(Double.valueOf(charge.getPercentageValue()))
						 	.calcType("P")
							.regulatoryCode("")
							.endtTypeCount(BigDecimal.ZERO)
							.dependentYn("N")
							.taxExemptedAllowed("Y")
							.minimumTaxAmountLc(BigDecimal.ZERO)
							.minimumTaxAmount(BigDecimal.ZERO)
							.taxAmountLc(taxAmount)
							.taxFor(coverId.toString())
							.extend_Cust_tax("")
						 	.build();
				 listTax.add(d);
			 }
		  
		  
			
			
			String amountValue = t.getPlanPricingBreakdown().getPricingBreakdown().getPremiumBreakdown().getPremiumCharges().getCharges().getCharge()
			.stream().filter(x-> "1".equals(x.getSequenceNo())).findFirst().get().getAmountValue();
			  Cover c = Cover.builder()						 
					.calcType("A")
					.coverId(coverId.toString())
					.coverDesc(t.getPlanDesc())
					.coverName(t.getPlanTitle())
					.minimumPremium(BigDecimal.ZERO)
					.coverToolTip("")
					.isSubCover("N")
					.sumInsuredLc(BigDecimal.ZERO)
					.sumInsured(BigDecimal.ZERO)
					.rate(Double.valueOf(amountValue))
					.subCoverId(null)
					.subCoverDesc(null)
					.subCoverName(null)
					.factorTypeId("")
					.dependentCoveryn("N")
					.dependentCoverId("")
					.coverageType("O")
					.isselected("N")
					.isReferral("N")
					.referalDescription("")
					.coverBasedOn(t.getsSRFeeCode())
					.sectionId(coverId.toString()) 
					.regulatoryCode(t.getPlanCode())
					
					.multiSelectYn("N")
					.excessAmount(BigDecimal.ZERO)
					.excessDesc(cleanHtml(t.getPlanContent()))
					.excessPercent(BigDecimal.ZERO)
					.minimumPremiumYn("N")
					.proRataYn("N")
					.endtCount(BigDecimal.ZERO)
					.effectiveDate(engine.getEffectiveDate())
					.policyEndDate(engine.getPolicyEndDate())
					.coverageLimit(BigDecimal.ZERO)
					.status("Y")
					.minSumInsured(BigDecimal.ZERO)
					.isTaxExcempted("N")
					.freeCoverLimit(BigDecimal.ZERO)
					.coverDescLocal(t.getPlanDesc())
					.coverNameLocal(t.getPlanTitle())
					.subCoverDescLocal("")
					.subCoverNameLocal("")
					.minrate(0D)
					.minimumRateYn("N")
					.exchangeRate(BigDecimal.ZERO)
					.premiumAfterDiscount(premiumExclTax)
					.premiumAfterDiscountLC(premiumExclTax)
					.premiumBeforeDiscount(premiumExclTax)
					.premiumBeforeDiscountLC(premiumExclTax)
					.premiumExcluedTax(premiumExclTax)
					.premiumExcluedTaxLC(premiumExclTax)
					.premiumIncludedTax(totalPremium)
					.premiumIncludedTaxLC(totalPremium)
					.exchangeRate(new BigDecimal(1))
					.taxes(listTax)
					.build();
			  
			   
			  
			return c; 
		}catch(Exception e) {
			System.out.println(t.getPlanDesc());
			e.printStackTrace();
		}
		return null;
	}
	
	private String cleanHtml(String input) {
        if (input == null) return "";
        // Remove extra spaces between tags
        String cleaned = input.replaceAll(">\\s+<", "><");
        // Trim leading/trailing whitespace
        cleaned = cleaned.trim();
        // Replace multiple spaces with a single space
        cleaned = cleaned.replaceAll("\\s{2,}", " ");
        // Remove redundant line breaks (multiple \n or \r\n)
        cleaned = cleaned.replaceAll("(\\r?\\n){2,}", "\n");
        return cleaned;
    }

}
