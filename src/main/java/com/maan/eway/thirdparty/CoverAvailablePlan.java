package com.maan.eway.thirdparty;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.function.Function;

import com.maan.eway.req.calcengine.CalcEngine;
import com.maan.eway.res.calc.Cover;
import com.maan.eway.res.calc.Tax;
import com.maan.eway.thirdparty.response.AvailablePlan;
import com.maan.eway.thirdparty.response.Charge;

public class CoverAvailablePlan implements Function<AvailablePlan,Cover> {
	private CalcEngine engine;
	
	public CoverAvailablePlan(CalcEngine engine) {
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
	public Cover apply(AvailablePlan t) {
		try {
			Integer coverId =twoDigitRandom();
					//new BigInteger(t.getSSRFeeCode().getBytes()).intValue();
			Charge charge = t.getPlanPricingBreakdown().getPricingBreakdown().getPremiumBreakdown().getPremiumCharges().getCharges().getCharge()
					.stream().filter(x-> !"1".equals(x.getSequenceNo())).findFirst().get();
			
			
			BigDecimal totalPremium = new BigDecimal(t.getTotalPremiumAmount());
		    BigDecimal taxRate = new BigDecimal(charge.getPercentageValue()); 

		    BigDecimal divisor = BigDecimal.ONE.add(taxRate.divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP));
		    BigDecimal premiumExclTax = totalPremium.divide(divisor, 2, RoundingMode.HALF_UP);

		    BigDecimal taxAmount = totalPremium.subtract(premiumExclTax);
		    
		    System.out.println(totalPremium);
		    System.out.println(taxRate);
		    System.out.println(divisor);
		    System.out.println(premiumExclTax);
		    System.out.println(taxAmount);
		    
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
		  
			List<Tax> listTax=new ArrayList<Tax>();
			listTax.add(d);
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
					.coverageType("B")
					.isselected("O")
					.isReferral("N")
					.referalDescription("")
					.coverBasedOn(t.getSSRFeeCode())
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
					.premiumAfterDiscount(premiumExclTax) //new BigDecimal(amountValue)
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
