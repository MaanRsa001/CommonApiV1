package com.maan.eway.calculator.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

import com.maan.eway.res.calc.Tax;

import jakarta.persistence.Tuple;

public class TaxCalculator implements Consumer<Tax> {

    private BigDecimal premium;
    private BigDecimal exchangeRate;
    protected Tuple customer = null;
    protected List<Tuple> customerChoiceTaxes;
    private CommonCalculator calc;
    private String companyId;
    private String productId;

    public TaxCalculator(BigDecimal premium, BigDecimal exchangeRate,
                         CommonCalculator calc, Tuple customer,
                         List<Tuple> customerChoiceTaxes) {
        this.premium = premium;
        this.exchangeRate = exchangeRate;
        this.calc = calc;
        this.customer = customer;
        this.customerChoiceTaxes = customerChoiceTaxes;
        this.companyId = "";
        this.productId = "";
    }
    
    public TaxCalculator(BigDecimal premium, BigDecimal exchangeRate,
                         CommonCalculator calc, Tuple customer,
                         List<Tuple> customerChoiceTaxes,
                         String companyId, String productId) {
        this.premium = premium;
        this.exchangeRate = exchangeRate;
        this.calc = calc;
        this.customer = customer;
        this.customerChoiceTaxes = customerChoiceTaxes;
        this.companyId = companyId == null ? "" : companyId;
        this.productId = productId == null ? "" : productId;
    }

    @Override
    public void accept(Tax t) {
        try {

            if ("100019".equals(companyId) && "125".equals(productId)) {
                t.setTaxAmount(BigDecimal.ZERO);
                t.setTaxAmountLc(BigDecimal.ZERO);
                System.out.println("[Product125] TaxCalculator → zeroing taxId="
                        + t.getTaxId() + " (" + t.getTaxDesc() + ")");
                return;
            }
            // ────────────────────────────────────────────────────────────────

            String calctype = t.getCalcType();

            String isTaxExempted = customer.get("isTaxExempted") == null
                    ? "N" : customer.get("isTaxExempted").toString();
            String taxExemptedId = customer.get("taxExemptedId") == null
                    ? "" : customer.get("taxExemptedId").toString();

            Optional<Tuple> first = customerChoiceTaxes.stream()
                    .filter(tx -> isTaxExempted.equals(tx.get("itemCode")))
                    .findFirst();

            String Percentage = "0";
            if (!first.isEmpty()) {
                Tuple tuple = first.get();
                Percentage = tuple.get("param1") == null ? "0" : tuple.get("param1").toString();
            }

            t.setIsTaxExempted(isTaxExempted);
            t.setTaxExemptCode(taxExemptedId);

            BigDecimal domath_Fc = BigDecimal.ZERO;
            t.setTaxAmount(BigDecimal.ZERO);
            t.setTaxAmountLc(BigDecimal.ZERO);

            if (("Y".equals(t.getTaxExemptedAllowed()) && !t.getIsTaxExempted().equals("Y"))
                    || t.getTaxExemptedAllowed().equals("N")) {

                Double taxRate = t.getTaxRate();
                if (!(t.getIsTaxExempted().equals("N") || t.getIsTaxExempted().equals("Y"))) {
                    Double percentage = Double.parseDouble(Percentage) / 100;
                    taxRate = taxRate * percentage;
                    t.setTaxRate(taxRate);
                }

                domath_Fc = calc.domath(calctype, taxRate, premium, exchangeRate);
                BigDecimal domath_Lc = domath_Fc.multiply(exchangeRate);
                t.setTaxAmount(domath_Fc);
                t.setTaxAmountLc(domath_Lc);
                t.setMinimumTaxAmount(t.getMinimumTaxAmountLc().multiply(exchangeRate));

                if (domath_Lc.compareTo(t.getMinimumTaxAmountLc()) < 0) {
                    t.setTaxAmount(t.getMinimumTaxAmount());
                    t.setTaxAmountLc(t.getMinimumTaxAmountLc());
                }

                BigDecimal finalTaxLc = t.getTaxAmountLc();
                String maxAmountYn = t.getMaxTaxAmountYn();
                BigDecimal maxAmount = t.getMaxTaxAmount();

                if ("Y".equals(maxAmountYn)
                        && maxAmount != null
                        && maxAmount.compareTo(BigDecimal.ZERO) > 0
                        && finalTaxLc.compareTo(maxAmount) > 0) {

                    BigDecimal maxAmountFc = maxAmount.compareTo(BigDecimal.ZERO) > 0
                            ? maxAmount.divide(exchangeRate, 2, RoundingMode.HALF_UP)
                            : BigDecimal.ZERO;
                    t.setTaxAmount(maxAmountFc);
                    t.setTaxAmountLc(maxAmount);
                    System.out.println(">>> TaxId=" + t.getTaxId()
                            + " capped at maxAmount=" + maxAmount
                            + " (calculated was " + finalTaxLc + ")");
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

