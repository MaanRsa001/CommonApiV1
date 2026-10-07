package com.maan.eway.calculator.util;

import java.math.BigDecimal;
import java.util.function.Consumer;

import com.maan.eway.res.calc.Loading;

public class AdminLoadingCalculator   implements Consumer<Loading> {

	private BigDecimal premium;
	private BigDecimal exchangeRate;
	private CommonCalculator calc;
 

 
	
	public AdminLoadingCalculator(BigDecimal premium, BigDecimal exchangeRate, CommonCalculator calc) {
		super();
		this.premium = premium;
		this.exchangeRate = exchangeRate;
		this.calc = calc;
	}




	@Override
	public void accept(Loading t) {
	 try {
		 String calctype= t.getLoadingCalcType();
			/*
			 * if("F".equals(t.getLoadingCalcType())) { List<Tuple> factors =
			 * calc.LoadFactorRates(calc.engine,
			 * t.getLoadingId(),t.getFactorTypeId(),calc.engine.getVehicleId()); Tuple tuple
			 * = factors.get(0); calctype=tuple.get("calcType").toString(); String
			 * rate=tuple.get("rate")==null?"0":tuple.get("rate").toString(); String
			 * minPremium=tuple.get("minPremium")==null?"0":tuple.get("minPremium").toString
			 * (); t.setLoadingRate(rate); t.setMaxAmount(new BigDecimal(minPremium)); }
			 */
		 BigDecimal domath = calc.domath(calctype, Double.parseDouble(t.getLoadingRate()), premium,exchangeRate);
		 t.setLoadingAmount(domath);
		/* if(t.getLoadingAmount().compareTo(t.getMaxAmount())==1) {
			 t.setLoadingAmount(t.getMaxAmount());
		 }*/
		 
	 }catch (Exception e) {
		 e.printStackTrace();
	 }
		
	}

}
