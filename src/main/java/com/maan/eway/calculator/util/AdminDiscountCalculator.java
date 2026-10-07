package com.maan.eway.calculator.util;

import java.math.BigDecimal;
import java.util.List;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;

import com.maan.eway.res.calc.CoverException;
import com.maan.eway.res.calc.Discount;

import jakarta.persistence.Tuple;

public class AdminDiscountCalculator implements Consumer<Discount> {

    private BigDecimal premium;
    private BigDecimal exchangeRate;
    private CommonCalculator calc;

    public AdminDiscountCalculator(BigDecimal premium, BigDecimal exchangeRate, CommonCalculator calc) {
        super();
        this.premium = premium;
        this.exchangeRate = exchangeRate;
        this.calc = calc;
    }

    @Override
    public void accept(Discount t) {
        try {
            String calctype = t.getDiscountCalcType();
            t.setDiscountAmount(BigDecimal.ZERO);

            if ("F".equals(t.getDiscountCalcType())) {
                System.out.println("[AdminDiscountCalculator] F-type discount detected -> discountId=" 
                    + t.getDiscountId() + " | factorTypeId=" + t.getFactorTypeId()
                    + " | subCoverId=" + t.getSubCoverId());

                String factorTypeId = StringUtils.isBlank(t.getFactorTypeId()) 
                	    ? "0" 
                	    : String.valueOf(new BigDecimal(t.getFactorTypeId()).intValue());

                	List<Tuple> factors = calc.LoadFactorRates(
                	    calc.engine,
                	    t.getDiscountId(),
                	    factorTypeId,
                	    calc.engine.getVehicleId(),
                	    StringUtils.isBlank(t.getSubCoverId()) ? "0" : t.getSubCoverId()
                	);

                Tuple tuple = null;
                try {
                    tuple = factors.get(0);
                } catch (Exception e) {
                    System.out.println("[AdminDiscountCalculator] ERROR: No factor found for discountId=" 
                        + t.getDiscountId());
                    CoverException build = CoverException.builder()
                        .message("No factor found")
                        .isError(true)
                        .build();
                    throw build;
                }

                calctype = tuple.get("calcType").toString();
                String rate = tuple.get("rate") == null ? "0" : tuple.get("rate").toString();
                String minPremium = tuple.get("minPremium") == null ? "0" : tuple.get("minPremium").toString();
                String regulatoryCode = tuple.get("regulatoryCode") == null ? "N/A" : tuple.get("regulatoryCode").toString();

                t.setDiscountRate(rate);
                t.setMaxAmount(new BigDecimal(minPremium));
                t.setRegulatoryCode(regulatoryCode);

                System.out.println("[AdminDiscountCalculator] F-type factor loaded -> calcType=" + calctype
                    + " | rate=" + rate + " | minPremium=" + minPremium);
            }

            System.out.println("[AdminDiscountCalculator] Calculating -> discountId=" + t.getDiscountId()
                + " | calcType=" + calctype + " | discountRate=" + t.getDiscountRate()
                + " | maxAmount=" + t.getMaxAmount() + " | premium=" + premium);

            BigDecimal domath = calc.domath(calctype, Double.parseDouble(t.getDiscountRate()), premium, exchangeRate);
            t.setDiscountAmount(domath);

            if (t.getDiscountAmount().compareTo(t.getMaxAmount()) == 1) {
                t.setDiscountAmount(t.getMaxAmount());
            }

            System.out.println("[AdminDiscountCalculator] Result -> discountAmount=" + t.getDiscountAmount());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}


//package com.maan.eway.calculator.util;
//
//import java.math.BigDecimal;
//import java.util.function.Consumer;
//
//import com.maan.eway.res.calc.Discount;
//
//public class AdminDiscountCalculator   implements Consumer<Discount> {
//
//	private BigDecimal premium;
//	private BigDecimal exchangeRate;
//	public AdminDiscountCalculator(BigDecimal premium, BigDecimal exchangeRate, CommonCalculator calc) {
//		super();
//		this.premium = premium;
//		this.exchangeRate = exchangeRate;
//		this.calc = calc;
//	}
//
//
//
//	private CommonCalculator calc;
//	 
//
// 
//	
//	@Override
//	public void accept(Discount t) {
//	 try {
//		 String calctype= t.getDiscountCalcType();
//		 /*if("F".equals(t.getDiscountCalcType())) {
//			 List<Tuple> factors = calc.LoadFactorRates(calc.engine, t.getDiscountId(),t.getFactorTypeId(),calc.engine.getVehicleId());
//			 Tuple tuple = factors.get(0);
//			 calctype=tuple.get("calcType").toString();
//			 String rate=tuple.get("rate")==null?"0":tuple.get("rate").toString();
//			 String minPremium=tuple.get("minPremium")==null?"0":tuple.get("minPremium").toString();
//			 t.setDiscountRate(rate);
//			 t.setMaxAmount(new BigDecimal(minPremium));
//		 }*/
//		 BigDecimal domath = calc.domath(calctype, Double.parseDouble(t.getDiscountRate()), premium,exchangeRate);
//		 t.setDiscountAmount(domath);
//		 if(t.getDiscountAmount().compareTo(t.getMaxAmount())==1) {
//			 t.setDiscountAmount(t.getMaxAmount());
//		 }
//		 
//	 }catch (Exception e) {
//		 e.printStackTrace();
//	 }
//		
//	}
//
//}
