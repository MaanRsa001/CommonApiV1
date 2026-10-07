package com.maan.eway.calculator.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.function.Consumer;

import com.maan.eway.res.calc.Tax;

import jakarta.persistence.Tuple;

public class TaxCalculatorPolicy implements Consumer<Tax> {

	private BigDecimal premium;
	private BigDecimal exchangeRate;
	
	protected Tuple customer =null;
	protected DecimalFormat decimalFormat = null;
	 
	public TaxCalculatorPolicy(BigDecimal premium, BigDecimal exchangeRate, Tuple customer,
			DecimalFormat decimalFormat) {
		super();
		this.premium = premium;
		this.exchangeRate = exchangeRate;
		this.customer = customer;
		this.decimalFormat = decimalFormat;
	}



	protected BigDecimal domath(String calctype, Double rate,BigDecimal si,BigDecimal exchangeRate) throws ParseException {
		BigDecimal d=BigDecimal.ZERO;
		if("P".equals(calctype)) {
			d = si.multiply(new BigDecimal(rate/100)/*, round*/);			
		 }else if("A".equals(calctype)) {
			d=(new BigDecimal(rate).divide(exchangeRate,3,RoundingMode.HALF_UP));// for foreign currency calculation we have to divide by exchange rate			
		 }else if("M".equals(calctype)) {
			 d = si.multiply(new BigDecimal(rate/1000)/*, round*/);			
		 }
		d = new BigDecimal(decimalFormat.format(d));
		return d;
	}

 
	
	@Override
	public void accept(Tax t) {
	 try {
		 String calctype= t.getCalcType();
		 
		 String isTaxExempted=customer.get("isTaxExempted")==null?"N":customer.get("isTaxExempted").toString();
		 String taxExemptedId=customer.get("taxExemptedId")==null?"":customer.get("taxExemptedId").toString();
		 
		 t.setIsTaxExempted(isTaxExempted);
		 t.setTaxExemptCode(taxExemptedId);
		 
		 BigDecimal domath_Fc = BigDecimal.ZERO;
		 t.setTaxAmount(BigDecimal.ZERO);
		 t.setTaxAmountLc(BigDecimal.ZERO);
		 if( ("Y".equals(t.getTaxExemptedAllowed()) && t.getIsTaxExempted().equals("N")) || t.getTaxExemptedAllowed().equals("N") ) {
			 domath_Fc= domath(calctype, t.getTaxRate(), premium,exchangeRate); 
			 BigDecimal domath_Lc = domath_Fc.multiply(exchangeRate);
			 t.setTaxAmount(domath_Fc);
			 t.setTaxAmountLc(domath_Lc);
			 t.setMinimumTaxAmount(t.getMinimumTaxAmountLc().multiply(exchangeRate));
			 if(domath_Lc.compareTo(t.getMinimumTaxAmountLc())<0) {
				 t.setTaxAmount(t.getMinimumTaxAmount());
				 t.setTaxAmountLc(t.getMinimumTaxAmountLc());
			 }
			 
			 BigDecimal finalTaxLc = t.getTaxAmountLc();
	            String maxAmountYn   = t.getMaxTaxAmountYn();
	            BigDecimal maxAmount = t.getMaxTaxAmount();

	            if ("Y".equals(maxAmountYn)
	                    && maxAmount != null
	                    && maxAmount.compareTo(BigDecimal.ZERO) > 0
	                    && finalTaxLc.compareTo(maxAmount) > 0) {

	                BigDecimal maxAmountFc = exchangeRate.compareTo(BigDecimal.ZERO) > 0
	                    ? maxAmount.divide(exchangeRate, 2, RoundingMode.HALF_UP)
	                    : BigDecimal.ZERO;

	                t.setTaxAmount(maxAmountFc);
	                t.setTaxAmountLc(maxAmount);

	                System.out.println(">>> [PolicyCalc] TaxId=" + t.getTaxId()
	                    + " capped at maxAmount=" + maxAmount
	                    + " (calculated was " + finalTaxLc + ")");
	            }
	        }
			 		 
		  
	 }catch (Exception e) {
		 e.printStackTrace();
	 }
		
	}

}
