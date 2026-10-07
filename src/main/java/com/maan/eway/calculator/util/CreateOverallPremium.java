package com.maan.eway.calculator.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.maan.eway.req.calcengine.CalcEngine;
import com.maan.eway.res.calc.Cover;
import com.maan.eway.res.calc.Tax;

import jakarta.persistence.Tuple;

/**
 * Builds the "Overall premium" B-row + one T-row per applicable tax,
 * using an already-known excl-tax total premium (no drcrEntry / table lookup).
 */
public class CreateOverallPremium {

    private final CalcEngine engine;
    private final BigDecimal totalPremiumExclTax; // FC, excl tax
    private final List<Tuple> resolvedTaxes;       // output of ratingutil.resolveTaxByPolicyDays(...)
    private final List<Tuple> customers;
    private final DecimalFormat decimalFormat;
    private final BigDecimal exchangeRate;

    public CreateOverallPremium(CalcEngine engine, BigDecimal totalPremiumExclTax,
                                 List<Tuple> resolvedTaxes, List<Tuple> customers,
                                 DecimalFormat decimalFormat, BigDecimal exchangeRate) {
        this.engine = engine;
        this.totalPremiumExclTax = totalPremiumExclTax;
        this.resolvedTaxes = resolvedTaxes;
        this.customers = customers;
        this.decimalFormat = decimalFormat;
        this.exchangeRate = exchangeRate == null ? BigDecimal.ONE : exchangeRate;
    }

    public List<Cover> create() {
        List<Cover> result = new ArrayList<>();

        BigDecimal premiumLc = new BigDecimal(decimalFormat.format(
                totalPremiumExclTax.multiply(exchangeRate)));

        // --- 1. Resolve tax list (same TaxUtils mapping used elsewhere) ---
        TaxUtils tzx = new TaxUtils(BigDecimal.ZERO, "");
        List<Tax> taxes = resolvedTaxes.stream()
                .map(tzx)
                .filter(t -> t != null)
                .collect(Collectors.toList());

        // --- 2. Calculate each tax amount off the base premium (excl tax) ---
        if (!taxes.isEmpty() && customers != null && !customers.isEmpty()) {
            TaxCalculatorPolicy tcal = new TaxCalculatorPolicy(
                    totalPremiumExclTax, exchangeRate, customers.get(0), decimalFormat);
            taxes.forEach(tcal); // populates taxAmount / taxAmountLc on each Tax
        }

        BigDecimal totalTaxLc = taxes.stream()
                .map(Tax::getTaxAmountLc)
                .filter(java.util.Objects::nonNull)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // --- 3. Base "B" row: Overall premium ---
        Cover base = Cover.builder()
                .coverId("946")                       // sentinel base cover id, adjust if configurable
                .coverName("Overall premium")
                .coverDesc("Overall Premium")
                .coverageType("O")
                .isselected("D")
                .subCoverId("0")
                .vehicleId("99999")
                .sectionId("99999")
                .locationId("99999")
                .isSubCover("N")
                .calcType("A")
                .sumInsured(totalPremiumExclTax)
                .sumInsuredLc(premiumLc)
                .rate(totalPremiumExclTax.doubleValue())
                .exchangeRate(exchangeRate)
                .premiumBeforeDiscount(totalPremiumExclTax)
                .premiumBeforeDiscountLC(premiumLc)
                .premiumAfterDiscount(totalPremiumExclTax)
                .premiumAfterDiscountLC(premiumLc)
                .premiumExcluedTax(totalPremiumExclTax)
                .premiumExcluedTaxLC(premiumLc)
                .premiumIncludedTax(totalPremiumExclTax.add(
                        totalTaxLc.divide(this.exchangeRate, java.math.MathContext.DECIMAL64)))
                .premiumIncludedTaxLC(new BigDecimal(decimalFormat.format(premiumLc.add(totalTaxLc))))
                .dependentCoveryn("Y")
                .dependentCoverId("1")
                .build();

        result.add(base);

        // --- 4. One "T" row per tax ---
        for (Tax t : taxes) {
            Cover taxCover = Cover.builder()
                    .coverId("946")                   // same coverId as base row
                    .coverName("Overall premium " + t.getTaxDesc())
                    .coverDesc("Overall Premium " + t.getTaxDesc())
                    .coverageType("T")
                    .isselected("D")
                    .subCoverId("0")
                    .isSubCover("N")
                    .calcType("A")
                    .sumInsured(totalPremiumExclTax)
                    .sumInsuredLc(premiumLc)
                    .rate(totalPremiumExclTax.doubleValue())
                    .exchangeRate(exchangeRate)
                    .dependentCoveryn("Y")
                    .dependentCoverId("0")
                    .taxes(java.util.Collections.singletonList(t))
                    .build();

            result.add(taxCover);
        }

        return result;
    }
}
