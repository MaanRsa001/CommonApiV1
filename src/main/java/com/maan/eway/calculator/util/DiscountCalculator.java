package com.maan.eway.calculator.util;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;

import com.maan.eway.res.calc.CoverException;
import com.maan.eway.res.calc.Discount;

import jakarta.persistence.Tuple;

public class DiscountCalculator   implements Consumer<Discount> {

	private BigDecimal premium;
	private BigDecimal exchangeRate;
	public DiscountCalculator(BigDecimal premium, BigDecimal exchangeRate, CommonCalculator calc) {
		super();
		this.premium = premium;
		this.exchangeRate = exchangeRate;
		this.calc = calc;
	}



	private CommonCalculator calc;
	 
 
	
	@Override
	public void accept(Discount t) {
	 try {
		 String calctype= t.getDiscountCalcType();
		 t.setDiscountAmount(BigDecimal.ZERO);
		 if("F".equals(t.getDiscountCalcType())) {
			 List<Tuple> factors = calc.LoadFactorRates(calc.engine, t.getDiscountId(),t.getFactorTypeId(),calc.engine.getVehicleId(),StringUtils.isBlank(t.getSubCoverId())?"0":t.getSubCoverId());
			 
			 Tuple tuple = null;
			 try {
				 tuple=factors.get(0);
			 }catch (Exception e) {
				// TODO: handle exception
				
				CoverException build = CoverException.builder().message("No factor found")
				.isError(true).build();
				 throw build;
				 /*t.setIsReferral("Y");
				 t.setReferalDescription("No factor found Referral for "+t.getCoverDesc());
				 t.setPremiumBeforeDiscount(BigDecimal.ZERO);					 
				 t.setPremiumBeforeDiscountLC(BigDecimal.ZERO);*/
			}
			 tuple = factors.get(0);
			 calctype=tuple.get("calcType").toString();
			 String rate=tuple.get("rate")==null?"0":tuple.get("rate").toString();
			 String minPremium=tuple.get("minPremium")==null?"0":tuple.get("minPremium").toString();
			 t.setDiscountRate(rate);
			 t.setMaxAmount(new BigDecimal(minPremium));
//			 t.setDiscountCalcType(calctype);
			 String regulatoryCode=tuple.get("regulatoryCode")==null?"N/A":tuple.get("regulatoryCode").toString();
			 t.setRegulatoryCode(regulatoryCode);
		 }
		 
		 if("90002".equals(t.getDiscountId())) {
				String LoadIngrate=calc.vehicles.get(0).get("uwLoading")==null?"0":calc.vehicles.get(0).get("uwLoading").toString();	
				if( Double.parseDouble(LoadIngrate)<0D)
					t.setDiscountRate(LoadIngrate);
			 }
		 
		 BigDecimal domath = calc.domath(calctype, Double.parseDouble(t.getDiscountRate()), premium,exchangeRate);
		 t.setDiscountAmount(domath);
		 if(t.getDiscountAmount().compareTo(t.getMaxAmount())==1) {
			 t.setDiscountAmount(t.getMaxAmount());
		 }
		 
	 }catch (Exception e) {
		// t.setDiscountAmount(BigDecimal.ZERO);
		 e.printStackTrace();
	 }
		
	}

}
