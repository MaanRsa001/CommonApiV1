package com.maan.eway.calculator.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

import org.springframework.stereotype.Component;

import com.maan.eway.res.calc.Cover;
import com.maan.eway.res.calc.CoverException;

@Component
public class AdminCoverCalculator  extends CommonCalculator implements Consumer<Cover> {
	
	
	@Override
	public void accept(Cover t) {
		 try {
			
			 if("Y".equals( t.getIsSubCover())) {
				 //this.setEngine(engine);
				 t.getSubcovers().stream().forEach(this);
			 }else {

			//	 loadOnetimetable(engine);
				 
				 BigDecimal exchangeRate= new BigDecimal(vehicles.get(0).get("exchangeRate")==null?"1":vehicles.get(0).get("exchangeRate").toString());
				 t.setExchangeRate(exchangeRate);
				 String currecy=vehicles.get(0).get("currency")==null?"N/A":vehicles.get(0).get("currency").toString();
				 t.setCurrency(currecy);
				 
				 t.setProRata(new BigDecimal("1"));
				 if(prorata!=null  && "Y".equals(t.getProRataYn())) {
					 BigDecimal percenat=prorata.get(0).get("percent")==null?BigDecimal.ZERO:new BigDecimal(prorata.get(0).get("percent").toString());	
					 t.setProRata(percenat.divide(new BigDecimal("100")));
				 }else if("D".equals(t.getProRataYn())) {
						String periodOfInsurance =vehicles.get(0).get("periodOfInsurance") == null ? "365": vehicles.get(0).get("periodOfInsurance").toString();
						t.setPolicyPeriod(new BigDecimal(periodOfInsurance));
						t.setProRata(t.getPolicyPeriod().divide(new BigDecimal("365") ,MathContext.DECIMAL32));
				}
				 
				 //BigDecimal si=vehicles.get(0).get(t.getCoverBasedOn())==null?BigDecimal.ZERO:new BigDecimal(vehicles.get(0).get(t.getCoverBasedOn()).toString());
				 BigDecimal si=t.getSumInsured();
				 
				 BigDecimal actualSi = si;   
				 
				 BigDecimal ratingSi = actualSi.subtract(
					        t.getFreeCoverLimit() == null ? BigDecimal.ZERO : t.getFreeCoverLimit()
					);

					ratingSi = ratingSi.compareTo(BigDecimal.ZERO) > 0 ? ratingSi : BigDecimal.ZERO;
				/* if(!"A".equals(t.getCalcType()))
					 si=vehicles.get(0).get(t.getCoverBasedOn())==null?BigDecimal.ZERO:new BigDecimal(vehicles.get(0).get(t.getCoverBasedOn()).toString());
					 */
//				 if("Y".equals(t.getDependentCoveryn())) {
//					 if(calculatedcover!=null) {
//						Cover ct = calculatedcover.stream().filter(c->c.getCoverId().equals(t.getDependentCoverId())).findAny().orElse(null);
//						si=ct!=null?ct.getPremiumExcluedTax():BigDecimal.ZERO;
//					}
//				 }		
					
						
					
					 if ("Y".equals(t.getDependentCoveryn())) {
						 
							if ("SI".equalsIgnoreCase(t.getDependentCoveSIorPI())) {

						    if (calculatedcover != null && t.getDependentCoverId() != null) {

						        List<String> dependentIds = Arrays.asList(t.getDependentCoverId().split(","));

						        si = calculatedcover.stream()
						                .filter(c -> dependentIds.contains(c.getCoverId()))
						                .map(Cover::getSumInsured)   
						                .findFirst()                
						                .orElse(BigDecimal.ZERO);

						        System.out.println("Dependent Cover SI set from Base Cover SI: " + si);
						    }
							}
							else if ("PI".equalsIgnoreCase(t.getDependentCoveSIorPI())){
						
									 if(calculatedcover!=null) {
										Cover ct = calculatedcover.stream().filter(c->c.getCoverId().equals(t.getDependentCoverId())).findAny().orElse(null);
										si=ct!=null?ct.getPremiumExcluedTax():BigDecimal.ZERO;
										System.out.println("Dependent Cover PI set from Base Cover PI: " + si);
										
										actualSi = si; 
										
										ratingSi = actualSi.subtract(
										        t.getFreeCoverLimit() == null ? BigDecimal.ZERO : t.getFreeCoverLimit()
										);

										ratingSi = ratingSi.compareTo(BigDecimal.ZERO) > 0 ? ratingSi : BigDecimal.ZERO;
									}	
						}	
						}
//				 si=si.subtract(t.getFreeCoverLimit());
//				 si=si.compareTo(BigDecimal.ZERO)>0?si:BigDecimal.ZERO;
				 t.setSumInsured(si);
				 t.setSumInsuredLc(si.multiply(exchangeRate,MathContext.DECIMAL64));
			
				 if("F".equals(t.getCalcType()))
					 t.setCalcType("P");
				BigDecimal domath = domath(t.getCalcType(), t.getRate(), ratingSi,t.getExchangeRate());
				 t.setPremiumBeforeDiscount(domath);
				 t.setPremiumBeforeDiscountLC((BigDecimal) decimalFormat.parse(decimalFormat.format(t.getPremiumBeforeDiscount().multiply(t.getExchangeRate())))) ;
			 
				 
				 System.out.println(t.getRate());
				 System.out.println();
				 
				 
				 
				 BigDecimal domathTira = domathTira(t.getCalcType(),t.getTiraRate(),t.getPremiumBeforeDiscountLC(),t.getExchangeRate()); //Tira Calculation only for referral
				 t.setTiraSumInsured(domathTira);
				 
				 Double totaldiscount=0D;
				 if(t.getDiscounts()!=null && t.getDiscounts().size()>0) {
					 AdminDiscountCalculator dcal=new AdminDiscountCalculator(t.getPremiumBeforeDiscount(),t.getExchangeRate(),this);					 
					 t.getDiscounts().stream().forEach(dcal);
					 totaldiscount= t.getDiscounts().stream().mapToDouble(i->i.getDiscountAmount().doubleValue()).sum();
				 }
				 
				 
				 Double totalloading=0D;
				 if(t.getLoadings()!=null && t.getLoadings().size()>0) {
					 AdminLoadingCalculator dcal=new AdminLoadingCalculator(t.getPremiumBeforeDiscount(),t.getExchangeRate(),this);					 
					 t.getLoadings().stream().forEach(dcal);
					 totalloading= t.getLoadings().stream().mapToDouble(i->i.getLoadingAmount().doubleValue()).sum();
				 }
				 
				 
				 
				 t.setPremiumAfterDiscount((BigDecimal) decimalFormat.parse(decimalFormat.format( t.getPremiumBeforeDiscount().subtract(new BigDecimal(totaldiscount)).add(new BigDecimal(totalloading)).multiply(t.getProRata()))));
				 t.setPremiumAfterDiscountLC((BigDecimal) decimalFormat.parse(decimalFormat.format(t.getPremiumAfterDiscount().multiply(t.getExchangeRate())/*.multiply(t.getProRata())*/)));
				 
				 t.setPremiumExcluedTax(t.getPremiumAfterDiscount());				 
				 t.setPremiumExcluedTaxLC((BigDecimal) decimalFormat.parse(decimalFormat.format(t.getPremiumExcluedTax().multiply(t.getExchangeRate()))));
				 
				 // Minimium Premium setup.
				 t.setMinimumPremiumYn("N");
				 if(t.getPremiumAfterDiscountLC().compareTo(t.getMinimumPremium())<0) {
					 t.setPremiumExcluedTax((BigDecimal) decimalFormat.parse(decimalFormat.format(t.getMinimumPremium().divide(t.getExchangeRate())))); 
					 t.setPremiumExcluedTaxLC(t.getMinimumPremium());
					 t.setMinimumPremiumYn("Y");
				 }
				 
				 Double totaltax=0D;
				 if(t.getTaxes()!=null && t.getTaxes().size()>0) {
					
					 System.out.println("=== [TaxCalc] CoverId: " + t.getCoverId() 
				        + " | SubCoverId: " + t.getSubCoverId()
				        + " | PremiumExcludedTax (base): " + t.getPremiumExcluedTax()); 

					 TaxCalculator tcal=new TaxCalculator(t.getPremiumExcluedTax(),t.getExchangeRate(),this,customers.get(0),this.customerChoiceTaxes);
					 t.getTaxes().stream().filter(f -> "N".equals(f.getDependentYn())).forEach(tcal);
					 Double totaltax_N = t.getTaxes().stream().filter(f -> "N".equals(f.getDependentYn())).mapToDouble(i->i.getTaxAmount().doubleValue()).sum();
					 
					    BigDecimal cascadingBase = t.getPremiumExcluedTax().add(new BigDecimal(totaltax_N));
					    System.out.println("=== [TaxCalc] Cascading Base for Y-taxes (premium + N-taxes): " 
					        + cascadingBase); 
					    
					 tcal=new TaxCalculator(t.getPremiumExcluedTax().add(new BigDecimal(totaltax_N)),t.getExchangeRate(),this,customers.get(0),this.customerChoiceTaxes);
 					 t.getTaxes().stream().filter(f -> "Y".equals(f.getDependentYn())).forEach(tcal);
 					 
 					 
 					 
					 Double totaltax_Y = t.getTaxes().stream().filter(f -> "Y".equals(f.getDependentYn())).mapToDouble(i->i.getTaxAmount().doubleValue()).sum();
					 
					 totaltax=totaltax_N+totaltax_Y;
					 
					 System.out.println("=== [TaxCalc] Final TotalTax: " + totaltax);
				 
				 }
				 
				 t.setPremiumIncludedTax((BigDecimal) decimalFormat.parse(decimalFormat.format( t.getPremiumExcluedTax().add(new BigDecimal(totaltax)))));				 
				 t.setPremiumIncludedTaxLC((BigDecimal) decimalFormat.parse(decimalFormat.format(t.getPremiumIncludedTax().multiply(t.getExchangeRate()))));
			 }
			 
			
			 
		 } catch (Exception e) {
			 System.out.println("CoverID:"+t.getCoverId()+"<Desc>:"+t.getCoverDesc()+",subcoverId:"+t.getSubCoverId());
			 e.printStackTrace();
			 CoverException build = CoverException.builder().isError(true).message(e.getMessage() +" "+"CoverID:"+t.getCoverId()+"<Desc>:"+t.getCoverDesc()+",subcoverId:"+t.getSubCoverId()).build();
			 
			 t.setError(build);
		 }
		
	}
	
	

}

