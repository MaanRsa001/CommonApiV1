package com.maan.eway.calculator.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Component;

import com.maan.eway.res.calc.Cover;
import com.maan.eway.res.calc.CoverException;
import com.maan.eway.res.calc.Discount;
import com.maan.eway.res.calc.Loading;

import jakarta.persistence.Tuple;

@Component
public class CoverCalculator extends CommonCalculator implements Consumer<Cover> {
	
	
	/*@Autowired
	private CoverCalculator calc;*/
	
	@Override
	public void accept(Cover t) {
		 try {
			 System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
				        + " <---- accept Block1 start :---->");
			 
			 String minrate=null;
			 DecimalFormat dcf=	 (DecimalFormat) this.decimalFormat.clone();
			 if("Y".equals( t.getIsSubCover())) {
				 //this.setEngine(engine);
				 t.getSubcovers().stream().forEach(this);
				 t.getSubcovers().removeIf(ll -> (ll.isNotsutable()));
			 }else {

			//	 loadOnetimetable(engine);
				 boolean discountLoading=true;
				 System.out.println(t.getCoverId()+ "--- "+t.getCoverName());
				 BigDecimal exchangeRate= new BigDecimal(vehicles.get(0).get("exchangeRate")==null?"1":vehicles.get(0).get("exchangeRate").toString());
				 t.setExchangeRate(exchangeRate);
				 String currecy=vehicles.get(0).get("currency")==null?"N/A":vehicles.get(0).get("currency").toString();
				 t.setCurrency(currecy);
				 
				 t.setProRata(new BigDecimal("1"));
				 if(prorata!=null && prorata.size()>0 && "Y".equals(t.getProRataYn())) {
					 BigDecimal percenat=prorata.get(0).get("percent")==null?BigDecimal.ZERO:new BigDecimal(prorata.get(0).get("percent").toString());	
					 t.setProRata(percenat.divide(new BigDecimal("100"),MathContext.DECIMAL32));
				 }else if("D".equals(t.getProRataYn())) {
					String periodOfInsurance =vehicles.get(0).get("periodOfInsurance") == null ? "365": vehicles.get(0).get("periodOfInsurance").toString();
					t.setPolicyPeriod(new BigDecimal(periodOfInsurance));
					t.setProRata(t.getPolicyPeriod().divide(new BigDecimal("365") ,MathContext.DECIMAL32));
				 }
				 
				 /// this particular variable is for is rate defined for Single
				 String rateFor=vehicles.get(0).get("groupCount")==null?"1":vehicles.get(0).get("groupCount").toString();
				 
				 BigDecimal si=BigDecimal.ZERO;
				 if(!"A".equals(t.getCalcType()))
					 si=vehicles.get(0).get(t.getCoverBasedOn())==null?BigDecimal.ZERO:new BigDecimal(vehicles.get(0).get(t.getCoverBasedOn()).toString());
				 
//				 if("Y".equals(t.getDependentCoveryn())) {
//					 System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
//						        + " <---- accept Block2  start :---->");
//					 if(calculatedcover!=null) {
//						 List<String> dependentIds = Arrays.asList(t.getDependentCoverId().split(","));
//						 
//						 si = calculatedcover.stream().sorted(Comparator.comparing(Cover::getPremiumExcluedTax).reversed()).filter(c->{					        
//					        return dependentIds.contains(c.getCoverId());
//						}).map(x-> x.getPremiumExcluedTax()).reduce((a, b) -> a.subtract(b)).orElse(BigDecimal.ZERO);
//								//findAny().orElse(null);
//						//si=ct!=null?ct.getPremiumExcluedTax():BigDecimal.ZERO;
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
								}	
					}	
					}
				 
				 BigDecimal actualSi = si;   
				 
				 BigDecimal ratingSi = actualSi.subtract(
					        t.getFreeCoverLimit() == null ? BigDecimal.ZERO : t.getFreeCoverLimit()
					);

					ratingSi = ratingSi.compareTo(BigDecimal.ZERO) > 0 ? ratingSi : BigDecimal.ZERO;

				 
//				 si=si.subtract(t.getFreeCoverLimit());
//				 si=si.compareTo(BigDecimal.ZERO)>0?si:BigDecimal.ZERO;
				 t.setSumInsured(actualSi);
				 t.setSumInsuredLc(actualSi.multiply(exchangeRate, MathContext.DECIMAL64));

				 //t.getPremiumAfterDiscountLC().compareTo(t.getMinimumPremium())<0
				 if(t.getSumInsured().compareTo(t.getCoverageLimit())>0) {
					 t.setIsReferral("Y");
					 t.setReferalDescription("CoverageLimit Referral Limits Upto "+t.getCoverageLimit().toPlainString());
					 t.setPremiumBeforeDiscount(BigDecimal.ZERO);					 
					 t.setPremiumBeforeDiscountLC(BigDecimal.ZERO);
					 t.setCalcType("P");
				 }else if(t.getSumInsured().compareTo(t.getMinSumInsured())<0) {
					 System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
						        + " <---- accept Block3 start :---->");
					    discountLoading=false;
//						CoverException build = CoverException.builder().message(t.getCoverName()+ "Min SumInsured is:"+t.getMinSumInsured().toPlainString()+ " & SumInsured:"+t.getSumInsured().toPlainString())
//						.isError(true).build();
//						t.setError(build);
//						t.setNotsutable(true);
//						throw build;
					    t.setIsReferral("Y");
						 t.setReferalDescription("Suminsured referral : Sumisured should not less than "+t.getMinSumInsured());
						 t.setPremiumBeforeDiscount(BigDecimal.ZERO);
						 t.setRate(0D);

						 t.setMinimumPremium(BigDecimal.ZERO);
						 t.setPremiumBeforeDiscountLC(BigDecimal.ZERO);
						 t.setCalcType("A");
						 t.setRegulatoryCode("NA");
				 } if("F".equals(t.getCalcType()) || "FD".equals(t.getCalcType())) {
					 // Tuple vehicle,Tuple customer,Tuple common
					 System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
						        + " <---- accept Block 4 start :---->");
					 List<Tuple> factors = LoadFactorRates(engine, t.getCoverId(),t.getFactorTypeId(),engine.getVehicleId(),StringUtils.isBlank(t.getSubCoverId())?"0":t.getSubCoverId());
					 
					 /*if(factors==null || factors.size()==0) 
						 throw CoverException.builder().message("Not Found Result "+"CoverID:"+t.getCoverId()+"<Desc>:"+t.getCoverDesc()+",subcoverId:"+t.getSubCoverId())
					 .isError(true).build();*/
						 
					 Tuple tuple = null;
					 try {
						 tuple=factors.get(0);
					 }catch (Exception e) {
						// TODO: handle exception
						
						discountLoading=false;
						if("B".equals(t.getCoverageType())) {
							 t.setIsReferral("Y");
							 t.setReferalDescription("No factor found "+t.getCoverDesc() +" Referral" );
							 t.setPremiumBeforeDiscount(BigDecimal.ZERO);
							 t.setRate(0D);

							 t.setMinimumPremium(BigDecimal.ZERO);
							 t.setPremiumBeforeDiscountLC(BigDecimal.ZERO);
							 t.setCalcType("A");
							 t.setRegulatoryCode("NA");
						}else {
							CoverException build = CoverException.builder().message("No factor found "+t.getCoverDesc())
							.isError(true).build();
							 t.setError(build);
							 t.setNotsutable(true);
							 throw build;
						}
						 /*t.setIsReferral("Y");
						 t.setReferalDescription("No factor found Referral for "+t.getCoverDesc());
						 t.setPremiumBeforeDiscount(BigDecimal.ZERO);					 
						 t.setPremiumBeforeDiscountLC(BigDecimal.ZERO);*/
					}
					 if("FD".equals(t.getCalcType())){
						 System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
							        + " <---- PerilCalculator Block start :---->");
						 PerilCalculator calc=new PerilCalculator(crservice, engine, result, vehicles, customers,this,factors,this.drivers);
						 calc.perilCalculator(t);
						 t.setPremiumBeforeDiscountLC((BigDecimal) dcf.parse(dcf.format(t.getPremiumBeforeDiscount().multiply(t.getExchangeRate())))) ;
						 t.setLoadings(new ArrayList<>());
						 t.setMinimumPremium(tuple.get("minPremium")==null?BigDecimal.ZERO:new BigDecimal(tuple.get("minPremium").toString())/*.divide(t.getExchangeRate(),round)*/);
						 t.setExcessAmount(tuple.get("excessAmount")==null?BigDecimal.ZERO:new BigDecimal(tuple.get("excessAmount").toString()));
						 t.setExcessDesc(tuple.get("excessDesc")==null?"":tuple.get("excessDesc").toString());
						 t.setExcessPercent(tuple.get("excessPercent")==null?BigDecimal.ZERO:new BigDecimal(tuple.get("excessPercent").toString()));
						 minrate=tuple.get("minimumRate")==null?"0":tuple.get("minimumRate").toString();
						 //t.getDiscounts().clear();
						 discountLoading=false;
						 System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
							        + " <---- PerilCalculator Block end :---->");
					 }else if(tuple!=null) {
						 System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
							        + " <---- accept Block 6 start :---->");
						 String calctype=tuple.get("calcType").toString();
						 String rate=tuple.get("rate")==null?"0":tuple.get("rate").toString();
						 minrate=tuple.get("minimumRate")==null?"0":tuple.get("minimumRate").toString();
						 String maxrate=tuple.get("maximumRate")==null?"0":tuple.get("maximumRate").toString();
						 String regulatoryCode=tuple.get("regulatoryCode")==null?"N/A":tuple.get("regulatoryCode").toString();
						 t.setRate((Double) ((Double.parseDouble(rate)*Double.parseDouble(rateFor))));
						 t.setMinrate((Double) ((Double.parseDouble(minrate)*Double.parseDouble(rateFor))));
						 t.setMaxrate((Double) ((Double.parseDouble(maxrate)*Double.parseDouble(rateFor))));
						 
						 t.setMinimumPremium(tuple.get("minPremium")==null?BigDecimal.ZERO:new BigDecimal(tuple.get("minPremium").toString())/*.divide(t.getExchangeRate(),round)*/);

						 //System.out.println(t.getCoverDesc()+ "<--->"+t.getRate() +"---"+si);
						 
						 BigDecimal domath = domath(calctype, t.getRate(), ratingSi,t.getExchangeRate());
						// System.out.println(t.getCoverDesc()+ "<--->"+domath);
						 t.setPremiumBeforeDiscount(domath);

						 t.setPremiumBeforeDiscountLC((BigDecimal) dcf.parse(dcf.format(t.getPremiumBeforeDiscount().multiply(t.getExchangeRate())))) ;
						 t.setCalcType(calctype);
						 t.setRegulatoryCode(regulatoryCode);
						 /// Referal
						 t.setIsReferral((tuple.get("status")==null?"N":tuple.get("status").toString()).equals("R")?"Y":"N");
						 if("Y".equals(t.getIsReferral())){
							 t.setReferalDescription(t.getCoverDesc() +" Referral" );
							
						 }
						 t.setExcessAmount(tuple.get("excessAmount")==null?BigDecimal.ZERO:new BigDecimal(tuple.get("excessAmount").toString()));
						 t.setExcessDesc(tuple.get("excessDesc")==null?"":tuple.get("excessDesc").toString());
						 t.setExcessPercent(tuple.get("excessPercent")==null?BigDecimal.ZERO:new BigDecimal(tuple.get("excessPercent").toString()));
						 System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
							        + " <---- accept Block 6 end :---->");
					 }
				 }/*else if("FD".equals(t.getCalcType())){
					 PerilCalculator calc=new PerilCalculator(crservice, engine, result, vehicles, customers,this);
					 calc.perilCalculator(t);
					 t.setPremiumBeforeDiscountLC((BigDecimal) dcf.parse(dcf.format(t.getPremiumBeforeDiscount().multiply(t.getExchangeRate())))) ;
					 t.getLoadings().clear();
					 //t.getDiscounts().clear();
					 discountLoading=false;
				 }*/else {
					 System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
						        + " <---- accept common calculator Block7 start :---->");
					 t.setRate((t.getRate()*Double.parseDouble(rateFor)));
					 CommonCalculator calcul=new CommonCalculator();
					 calcul.setEngine(engine, calculatedcover, result, vehicles, customers, prorata, crservice, dcf,drivers,this.customerChoiceTaxes);
					// System.out.println(t.getCoverDesc()+ "---"+t.getRate() +"---"+si);
					 BigDecimal domath = calcul.domath(t.getCalcType(), t.getRate(), ratingSi,t.getExchangeRate());
					// System.out.println(t.getCoverDesc()+ "---"+domath);
					 t.setPremiumBeforeDiscount(domath);					 
					 t.setPremiumBeforeDiscountLC((BigDecimal) dcf.parse(dcf.format(t.getPremiumBeforeDiscount().multiply(t.getExchangeRate())))) ;
					 System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
						        + " <---- accept common calculator  Block 7 end :---->");
				 }
				 
				 
				 //BigDecimal domathTira = domathTira(t.getCalcType(),t.getRate(),t.getSumInsured(),t.getExchangeRate()); Tira Calculation only for referral
				 t.setTiraSumInsured(si);
				 if(!"A".equals(t.getCalcType()))
					 t.setTiraRate(t.getRate());
				 Double totaldiscount=0D;
				 Double totalloading=0D;
				 if(discountLoading) {
					
					 if(t.getDiscounts()!=null && t.getDiscounts().size()>0) {
						 DiscountCalculator dcal=new DiscountCalculator(t.getPremiumBeforeDiscount(),t.getExchangeRate(),this);					 
						 t.getDiscounts().stream().forEach(dcal);
						 totaldiscount= t.getDiscounts().stream().mapToDouble(i->i.getDiscountAmount().doubleValue()).sum();
						
					 }

					 if(t.getLoadings()!=null && t.getLoadings().size()>0) {
						 LoadingCalculator dcal=new LoadingCalculator(t.getPremiumBeforeDiscount(),t.getExchangeRate(),this);					 
						 t.getLoadings().stream().forEach(dcal);
						 totalloading= t.getLoadings().stream().mapToDouble(i->i.getLoadingAmount().doubleValue()).sum();
					 }
					 
					 if (StringUtils.isNotBlank(minrate) && Double.parseDouble(minrate) != 0D) {

				         BigDecimal baseRate = BigDecimal.valueOf(t.getRate());
				         BigDecimal finalRate = computeFinalRate(baseRate, t.getDiscounts(), t.getLoadings());

				         BigDecimal minRate = BigDecimal.valueOf(t.getMinrate());

				         if (finalRate.compareTo(minRate) < 0) {
				             BigDecimal minRatePremium = minRate
				                     .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
				                     .multiply(ratingSi)
				                     .setScale(2, RoundingMode.HALF_UP);
				             t.setMinimumPremium(minRatePremium);
				         }
				     }
				 }
				 
				 if (StringUtils.isNotBlank(minrate) && Double.parseDouble(minrate) != 0D) {

			         BigDecimal baseRate = BigDecimal.valueOf(t.getRate());
			         BigDecimal finalRate = computeFinalRate(baseRate, t.getDiscounts(), t.getLoadings());

			         BigDecimal minRate = BigDecimal.valueOf(t.getMinrate());

			         if (finalRate.compareTo(minRate) < 0) {
			             BigDecimal minRatePremium = minRate
			                     .divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP)
			                     .multiply(ratingSi)
			                     .setScale(2, RoundingMode.HALF_UP);
			             t.setMinimumPremium(minRatePremium);
			         }
			     }
				 
				 
				 t.setPremiumAfterDiscount((BigDecimal) dcf.parse(dcf.format(t.getPremiumBeforeDiscount().subtract(new BigDecimal(totaldiscount)).add(new BigDecimal(totalloading)).multiply(t.getProRata()))) );
 
				 t.setPremiumAfterDiscountLC((BigDecimal) dcf.parse(dcf.format(t.getPremiumAfterDiscount().multiply(t.getExchangeRate()))));
				 //.multiply(t.getProRata())				 
				 t.setPremiumExcluedTax((BigDecimal) dcf.parse(dcf.format(t.getPremiumAfterDiscount().multiply(t.getExchangeRate()))));				 
				 t.setPremiumExcluedTaxLC((BigDecimal) dcf.parse(dcf.format(t.getPremiumExcluedTax().multiply(t.getExchangeRate()))));
				 
				 // Minimium Premium setup.
				 if(t.getPremiumAfterDiscountLC().compareTo(t.getMinimumPremium())<0 && !"Y".equals(t.getIsReferral())) {
					 
					 t.setPremiumExcluedTax((BigDecimal) dcf.parse(dcf.format(t.getMinimumPremium().divide(t.getExchangeRate(),MathContext.DECIMAL64)))); 
					 t.setPremiumExcluedTaxLC((BigDecimal) dcf.parse(dcf.format(t.getPremiumExcluedTax().divide(t.getExchangeRate(),MathContext.DECIMAL64))));
					 t.setMinimumPremiumYn("Y");
				 }
				 
				 Double totaltax=0D;
				 if(t.getTaxes()!=null && t.getTaxes().size()>0 && customers!=null && customers.get(0)!=null ) {
					 
					 String engineCompanyId = engine.getInsuranceId() == null ? "" : engine.getInsuranceId();
					 String engineProductId = engine.getProductId()   == null ? "" : engine.getProductId();
					
					 System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
						        + " <---- TaxCalculator Block  start :---->");
					 TaxCalculator tcal=new TaxCalculator(t.getPremiumExcluedTax(),t.getExchangeRate(),this,customers.get(0),this.customerChoiceTaxes,engineCompanyId, engineProductId);
					 t.getTaxes().stream().filter(f -> "N".equals(f.getDependentYn())).forEach(tcal);
					 Double totaltax_N = t.getTaxes().stream().filter(f -> "N".equals(f.getDependentYn())).mapToDouble(i->i.getTaxAmount().doubleValue()).sum();
					 
					 
					 tcal=new TaxCalculator(t.getPremiumExcluedTax().add(new BigDecimal(totaltax_N)),t.getExchangeRate(),this,customers.get(0),customerChoiceTaxes,engineCompanyId, engineProductId);
					 t.getTaxes().stream().filter(f -> "Y".equals(f.getDependentYn())).forEach(tcal);
					 Double totaltax_Y = t.getTaxes().stream().filter(f -> "Y".equals(f.getDependentYn())).mapToDouble(i->i.getTaxAmount().doubleValue()).sum();
					 
					 totaltax=totaltax_N+totaltax_Y;
				 }
				 
				 t.setPremiumIncludedTax((BigDecimal) dcf.parse(dcf.format(t.getPremiumExcluedTax().add(new BigDecimal(totaltax)))));				 
				 t.setPremiumIncludedTaxLC((BigDecimal) dcf.parse(dcf.format(t.getPremiumIncludedTax().multiply(t.getExchangeRate()))));
				 System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
					        + " <---- TaxCalculator Block  end :---->");
			 }
			 
			
			 
		 }catch(CoverException ex) {
			 ex.printStackTrace();
		 }catch (Exception e) {
			 System.out.println("CoverID:"+t.getCoverId()+"<Desc>:"+t.getCoverDesc()+",subcoverId:"+t.getSubCoverId());
			 e.printStackTrace();
			 CoverException build = CoverException.builder().isError(true).message(e.getMessage() +" "+"CoverID:"+t.getCoverId()+"<Desc>:"+t.getCoverDesc()+",subcoverId:"+t.getSubCoverId()).build();
			 
			 t.setError(build);
		 }
		
	}
	
	/**
	 * Computes the final rate after applying all discounts to a base rate.
	 * - calcType "P": percentage discount, compounds on the running rate
	 *   e.g. base=5, discounts 10% then 40% ->
	 *        step1: 5 * (1 - 10/100) = 4.5
	 *        step2: 4.5 * (1 - 40/100) = 2.7
	 * - calcType "A": amount discount, subtracted directly from the running rate
	 *   e.g. running rate 4.5, discount amount 0.5 -> 4.5 - 0.5 = 4.0
	 */
//	private BigDecimal computeDiscountedRate(BigDecimal baseRate, List<Discount> discounts) {
//	    BigDecimal runningRate = baseRate;
//
//	    for (Discount d : discounts) {
//	        String calcType = d.getDiscountCalcType();
//	        BigDecimal discountRate = new BigDecimal(d.getDiscountRate()); 
//
//	        if ("P".equals(calcType)) {
//	            // percentage: rate = rate * (1 - discountRate/100)
//	            BigDecimal factor = BigDecimal.ONE.subtract(
//	                    discountRate.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP));
//	            runningRate = runningRate.multiply(factor);
//
//	        } else if ("A".equals(calcType)) {
//	            // amount: subtract discountRate directly from the running rate
//	            runningRate = runningRate.subtract(discountRate);
//	        }
//	    }
//
//	    return runningRate.setScale(6, RoundingMode.HALF_UP);
//	}
	
	private BigDecimal computeFinalRate(BigDecimal baseRate, List<Discount> discounts, List<Loading> loadings) {

		BigDecimal runningRate = baseRate;
		
		if (discounts != null) {
			for (Discount d : discounts) {
				String calcType = d.getDiscountCalcType();
				String rateStr = d.getDiscountRate();

				if ("F".equals(calcType)) {
					List<Tuple> factors = LoadFactorRates(engine, d.getDiscountId(), d.getFactorTypeId(),
							engine.getVehicleId(), StringUtils.isBlank(d.getSubCoverId()) ? "0" : d.getSubCoverId());
					if (factors == null || factors.isEmpty()) {
						continue; // no factor found, skip this discount
					}
					Tuple tuple = factors.get(0);
					calcType = tuple.get("calcType").toString();
					rateStr = tuple.get("rate") == null ? "0" : tuple.get("rate").toString();
				}

				BigDecimal discountRate = new BigDecimal(rateStr);

				if ("P".equals(calcType)) {
					BigDecimal factor = BigDecimal.ONE
							.subtract(discountRate.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP));
					runningRate = runningRate.multiply(factor);
				} else if ("A".equals(calcType)) {
					runningRate = runningRate.subtract(discountRate);
				}
			}
		}

// Then apply loadings (adds back to the rate)
		if (loadings != null) {
			for (Loading l : loadings) {
				String calcType = l.getLoadingCalcType();
				String rateStr = l.getLoadingRate();

				if ("F".equals(calcType)) {
					List<Tuple> factors = LoadFactorRates(engine, l.getLoadingId(), l.getFactorTypeId(),
							engine.getVehicleId(), StringUtils.isBlank(l.getSubCoverId()) ? "0" : l.getSubCoverId());
					if (factors == null || factors.isEmpty()) {
						continue;
					}
					Tuple tuple = factors.get(0);
					calcType = tuple.get("calcType").toString();
					rateStr = tuple.get("rate") == null ? "0" : tuple.get("rate").toString();
				}

				BigDecimal loadingRate = new BigDecimal(rateStr);

				if ("P".equals(calcType)) {
					BigDecimal factor = BigDecimal.ONE
							.add(loadingRate.divide(BigDecimal.valueOf(100), 10, RoundingMode.HALF_UP));
					runningRate = runningRate.multiply(factor);
				} else if ("A".equals(calcType)) {
					runningRate = runningRate.add(loadingRate);
				}
			}
		}

		return runningRate.setScale(6, RoundingMode.HALF_UP);
	}
	

}

