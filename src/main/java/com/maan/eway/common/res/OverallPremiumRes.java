package com.maan.eway.common.res;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class OverallPremiumRes {

    @JsonProperty("PremiumBeforeDiscountFc")
    private BigDecimal premiumBeforeDiscountFc;

    @JsonProperty("PremiumBeforeDiscountLc")
    private BigDecimal premiumBeforeDiscountLc;

    @JsonProperty("PremiumAfterDiscountFc")
    private BigDecimal premiumAfterDiscountFc;

    @JsonProperty("PremiumAfterDiscountLc")
    private BigDecimal premiumAfterDiscountLc;

    // These two you asked, but there are no specific columns.
    // Keep them for now – you can decide later how to calculate.
    @JsonProperty("PremiumBeforeLoadingFc")
    private BigDecimal premiumBeforeLoadingFc;

    @JsonProperty("PremiumBeforeLoadingLc")
    private BigDecimal premiumBeforeLoadingLc;

    @JsonProperty("PremiumAfterLoadingFc")
    private BigDecimal premiumAfterLoadingFc;

    @JsonProperty("PremiumAfterLoadingLc")
    private BigDecimal premiumAfterLoadingLc;

    @JsonProperty("PremiumExcludedTaxFc")
    private BigDecimal premiumExcludedTaxFc;

    @JsonProperty("PremiumExcludedTaxLc")
    private BigDecimal premiumExcludedTaxLc;

    @JsonProperty("PremiumIncludedTaxFc")
    private BigDecimal premiumIncludedTaxFc;

    @JsonProperty("PremiumIncludedTaxLc")
    private BigDecimal premiumIncludedTaxLc;

    // Overall = Included Tax total (you can change to what you prefer)
    @JsonProperty("OverallPremiumFc")
    private BigDecimal overallPremiumFc;

    @JsonProperty("OverallPremiumLc")
    private BigDecimal overallPremiumLc;

    @JsonProperty("TaxList")
    private List<TaxDetailsRes> taxList;
    
    
    // 🔹 NEW: Discount details
    @JsonProperty("DiscountRate")
    private BigDecimal discountRate;

    @JsonProperty("DiscountAmountFc")
    private BigDecimal discountAmountFc;

    @JsonProperty("DiscountAmountLc")
    private BigDecimal discountAmountLc;

    // 🔹 NEW: Loading details
    @JsonProperty("LoadingRate")
    private BigDecimal loadingRate;

    @JsonProperty("LoadingAmountFc")
    private BigDecimal loadingAmountFc;

    @JsonProperty("LoadingAmountLc")
    private BigDecimal loadingAmountLc;
}
