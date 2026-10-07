package com.maan.eway.calculator.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;

import com.maan.eway.chartaccount.ChartAccountRequest;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.req.calcengine.CalcEngine;
import com.maan.eway.res.calc.Cover;
import com.maan.eway.res.calc.CoverException;
import com.maan.eway.res.calc.RatingInfo;

import jakarta.persistence.Tuple;

public class PolicyCoverCalculator implements Consumer<Cover> {

	public PolicyCoverCalculator(List<Tuple> policy, RatingFactorsUtil crservice, CalcEngine engine,DecimalFormat decimalFormat,List<Tuple> customers,List<Tuple> vehicles,boolean isRateUpdate) {
		super();
		this.policy = policy;
		this.crservice = crservice;
		this.engine = engine;
		this.decimalFormat = decimalFormat;
		this.customers=customers;
		this.vehicles=vehicles;
		this.isRateUpdate=isRateUpdate;
	}
	protected List<Tuple> policy;
	protected RatingFactorsUtil crservice;
	protected CalcEngine engine;
	protected DecimalFormat decimalFormat = null;
	protected List<Tuple> customers =null;
	protected List<Tuple> vehicles =null;
	protected boolean isRateUpdate;
	
	@Override
	public void accept(Cover t) {

		 try {
			
			 if("Y".equals( t.getIsSubCover())) {
				 //this.setEngine(engine);
				 t.getSubcovers().stream().forEach(this);
			 }else {

			//	 loadOnetimetable(engine);
				 boolean discountLoading=true;
				 
				 BigDecimal exchangeRate= new BigDecimal(policy.get(0).get("exchangeRate")==null?"1":policy.get(0).get("exchangeRate").toString());
				 t.setExchangeRate(exchangeRate);
				 String currecy=policy.get(0).get("currency")==null?"N/A":policy.get(0).get("currency").toString();
				 t.setCurrency(currecy);
				 
				 t.setProRata(new BigDecimal("1"));

				 BigDecimal si=BigDecimal.ZERO;
				 
				 if("B".equals(t.getCoverageType())) {
					 ChartAccountRequest r=new ChartAccountRequest();
						r.setCompanyId(engine.getInsuranceId());
						r.setChartId(t.getDependentCoverId());
						r.setProductId(engine.getProductId());
						r.setDiscountYn("Y");
						r.setRequestRefNo(engine.getRequestReferenceNo());
						r.setIsCheckMinimumPremium(true);
						CommonRes drcr = crservice.drcrEntry(r);
						
						si = new BigDecimal(drcr.getCommonResponse().toString());
						t.setRate(si.doubleValue());
				 }else if("Y".equals(t.getDependentCoveryn())) {
					 /*if(calculatedcover!=null) {
							Cover ct = calculatedcover.stream().filter(c->c.getCoverId().equals(t.getDependentCoverId())).findAny().orElse(null);
							si=ct!=null?ct.getPremiumExcluedTax():BigDecimal.ZERO;
						}*/ 
				 }else if(!"A".equals(t.getCalcType())) {
					 si=policy.get(0).get(t.getCoverBasedOn())==null?BigDecimal.ZERO:new BigDecimal(policy.get(0).get(t.getCoverBasedOn()).toString());
				 }else {
					si=new BigDecimal(t.getRate());
				 }
				 				 
				 t.setSumInsured(si);
				 t.setSumInsuredLc(si.multiply(exchangeRate,MathContext.DECIMAL64));
				 //t.getPremiumAfterDiscountLC().compareTo(t.getMinimumPremium())<0
				/* if(t.getSumInsured().compareTo(t.getCoverageLimit())>0) {
					 t.setIsReferral("Y");
					 t.setReferalDescription("CoverageLimit Referral Limits Upto"+t.getCoverageLimit());
					 t.setPremiumBeforeDiscount(BigDecimal.ZERO);					 
					 t.setPremiumBeforeDiscountLC(BigDecimal.ZERO);
					 t.setCalcType("P");
				 }else */if(t.getSumInsured().compareTo(t.getMinSumInsured())<0) {
					    discountLoading=false;
						CoverException build = CoverException.builder().message(t.getCoverName()+ "Min SumInsured is:"+t.getMinSumInsured()+ " & SumInsured:"+t.getSumInsured())
						.isError(true).build();
						t.setError(build);
						t.setNotsutable(true);
						throw build;
				 } if("F".equals(t.getCalcType())) {
					 // Tuple vehicle,Tuple customer,Tuple common
					 List<Tuple> factors = LoadFactorRates(engine, t.getCoverId(),t.getFactorTypeId());
					 
					 /*if(factors==null || factors.size()==0) 
						 throw CoverException.builder().message("Not Found Result "+"CoverID:"+t.getCoverId()+"<Desc>:"+t.getCoverDesc()+",subcoverId:"+t.getSubCoverId())
					 .isError(true).build();*/
						 
					 Tuple tuple = null;
					 try {
						 tuple=factors.get(0);
					 }catch (Exception e) {
						// TODO: handle exception
						 discountLoading=false;
						CoverException build = CoverException.builder().message("No factor found")
						 .isError(true).build();
						 t.setError(build);
						 t.setNotsutable(true);
						 throw build;
						 /*t.setIsReferral("Y");
						 t.setReferalDescription("No factor found Referral for "+t.getCoverDesc());
						 t.setPremiumBeforeDiscount(BigDecimal.ZERO);					 
						 t.setPremiumBeforeDiscountLC(BigDecimal.ZERO);*/
					}
					 if(tuple!=null) {
						 String calctype=tuple.get("calcType").toString();
						 String rate=tuple.get("rate")==null?"0":tuple.get("rate").toString();
						 String regulatoryCode=tuple.get("regulatoryCode")==null?"N/A":tuple.get("regulatoryCode").toString();

						 t.setRate((Double) ((Double.parseDouble(rate) )));

						 t.setMinimumPremium(tuple.get("minPremium")==null?BigDecimal.ZERO:new BigDecimal(tuple.get("minPremium").toString())/*.divide(t.getExchangeRate(),round)*/);
						 BigDecimal domath = domath(calctype, t.getRate(), si,t.getExchangeRate());
						 t.setPremiumBeforeDiscount(domath);

						 t.setPremiumBeforeDiscountLC(new BigDecimal(decimalFormat.format(t.getPremiumBeforeDiscount().multiply(t.getExchangeRate())))) ;
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
					 }
				 } else {
					  
					 
					 BigDecimal domath = domath(t.getCalcType(), t.getRate(), si,t.getExchangeRate());
					 t.setPremiumBeforeDiscount(domath);					 
					 t.setPremiumBeforeDiscountLC(new BigDecimal(decimalFormat.format(t.getPremiumBeforeDiscount().multiply(t.getExchangeRate())))) ;
				 }
				 
				 
				 //BigDecimal domathTira = domathTira(t.getCalcType(),t.getRate(),t.getSumInsured(),t.getExchangeRate()); Tira Calculation only for referral
				 t.setTiraSumInsured(si);
				 if(!"A".equals(t.getCalcType()))
					 t.setTiraRate(t.getRate());
				 Double totaldiscount=0D;
				 Double totalloading=0D;
				 BigDecimal premiumBeforeDiscount = t.getPremiumBeforeDiscount();
				 if("B".equals(t.getCoverageType())) {
					 ChartAccountRequest r=new ChartAccountRequest();
						r.setCompanyId(engine.getInsuranceId());
						r.setChartId(t.getDependentCoverId());
						r.setProductId(engine.getProductId());
						r.setDiscountYn("Y");
						r.setRequestRefNo(engine.getRequestReferenceNo());
						r.setIsCheckMinimumPremium(false);
						CommonRes drcr = crservice.drcrEntry(r);
						premiumBeforeDiscount = new BigDecimal(drcr.getCommonResponse().toString());
						 
				}
				 
				 if(discountLoading) {
					
					 if(t.getDiscounts()!=null && t.getDiscounts().size()>0) {
						 DiscountCalculatorPolicy dcal=new DiscountCalculatorPolicy(premiumBeforeDiscount,t.getExchangeRate(),policy,vehicles,crservice,engine,decimalFormat,isRateUpdate);
 						 t.getDiscounts().stream().forEach(dcal);
						 totaldiscount= t.getDiscounts().stream().mapToDouble(i->i.getDiscountAmount().doubleValue()).sum();
					 }


					 
					 if(t.getLoadings()!=null && t.getLoadings().size()>0) {
						 LoadingCalculatorPolicy dcal=new LoadingCalculatorPolicy(t.getPremiumBeforeDiscount(),t.getExchangeRate(),policy,crservice,engine,decimalFormat,isRateUpdate);					 
						 t.getLoadings().stream().forEach(dcal);
						 totalloading= t.getLoadings().stream().mapToDouble(i->i.getLoadingAmount().doubleValue()).sum();
					 }
				 }
				 
				 
				 t.setPremiumAfterDiscount(new BigDecimal(decimalFormat.format(t.getPremiumBeforeDiscount().subtract(new BigDecimal(totaldiscount)).add(new BigDecimal(totalloading)).multiply(t.getProRata()))) );

				 t.setPremiumAfterDiscountLC(new BigDecimal(decimalFormat.format(t.getPremiumAfterDiscount().multiply(t.getExchangeRate()))));
				 //.multiply(t.getProRata())				 
				 t.setPremiumExcluedTax(t.getPremiumAfterDiscount());				 
				 t.setPremiumExcluedTaxLC(new BigDecimal(decimalFormat.format(t.getPremiumExcluedTax().multiply(t.getExchangeRate()))));
				 
				 // Minimium Premium setup.
				 if(t.getPremiumAfterDiscountLC().compareTo(t.getMinimumPremium())<0 && !"Y".equals(t.getIsReferral())) {
					 
				//	 t.setPremiumExcluedTax(new BigDecimal(decimalFormat.format(t.getMinimumPremium().divide(t.getExchangeRate(),MathContext.DECIMAL64)))); 
				//	 t.setPremiumExcluedTaxLC(t.getMinimumPremium());
					 t.setMinimumPremiumYn("Y");
				 }
				 
				 Double totaltax=0D;
				 if(t.getTaxes()!=null && t.getTaxes().size()>0 && customers!=null && customers.get(0)!=null ) {
						TaxCalculatorPolicy tcal=new TaxCalculatorPolicy(t.getPremiumExcluedTax(),t.getExchangeRate(),customers.get(0),decimalFormat);
					 t.getTaxes().stream().filter(f -> "N".equals(f.getDependentYn())).forEach(tcal);
					 Double totaltax_N = t.getTaxes().stream().filter(f -> "N".equals(f.getDependentYn())).mapToDouble(i->i.getTaxAmount().doubleValue()).sum();
					 
					 
					 tcal=new TaxCalculatorPolicy(t.getPremiumExcluedTax().add(new BigDecimal(totaltax_N)),t.getExchangeRate(),customers.get(0),decimalFormat);
					 t.getTaxes().stream().filter(f -> "Y".equals(f.getDependentYn())).forEach(tcal);
					 Double totaltax_Y = t.getTaxes().stream().filter(f -> "Y".equals(f.getDependentYn())).mapToDouble(i->i.getTaxAmount().doubleValue()).sum();
					 
					 totaltax=totaltax_N+totaltax_Y;
				 }
				 
				 t.setPremiumIncludedTax(new BigDecimal(decimalFormat.format(t.getPremiumExcluedTax().add(new BigDecimal(totaltax)))));				 
				 t.setPremiumIncludedTaxLC(new BigDecimal(decimalFormat.format(t.getPremiumIncludedTax().multiply(t.getExchangeRate()))));
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

	public PolicyCoverCalculator(List<Tuple> policy, RatingFactorsUtil crservice, CalcEngine engine) {
		super();
		this.policy = policy;
		this.crservice = crservice;
		this.engine = engine;
	}


	public List<Tuple> LoadFactorRates(CalcEngine engine,String coverId,String factorid){
		return LoadFactorRates(engine, coverId, factorid, policy.get(0), vehicles.get(0));
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
	private List<Tuple> LoadFactorRates(CalcEngine engine, String coverId, String factorid, Tuple tuple, Tuple vehicles ) {
		Map<String,List<String>> vloop=new HashMap<String, List<String>>();
		try {

				List<RatingInfo> rateInfos = crservice.LoadRatingType(engine, factorid);
				List<String> condtions=new ArrayList<String>();
				for (RatingInfo r : rateInfos) {
					  if("MsPolicyDetails".equalsIgnoreCase(r.getInputTableName())){
						r.setInputColumValue(tuple.get(r.getInputColumName()).toString());
					}
					  else {
			                
			                Object val = vehicles.get(r.getInputColumName());
			                if (val instanceof BigDecimal) {
			                    r.setInputColumValue(((BigDecimal) val).toPlainString());
			                } else {
			                    r.setInputColumValue(val != null ? val.toString() : "");
			                }
			            }
					
					String condtion=r.getDiscretCol()+":"+r.getInputColumValue()+"";
					if("Y".equals(r.getFactorRangeYn())) {
						condtion=""+r.getInputColumValue()+"~"+r.getRangeFromCol()+"&"+r.getRangeToCol();  
					} 
					condtions.add(condtion);   
				}

				vloop.put("1", condtions);


				List<Tuple> loopfactorrates = crservice.loopfactorrates(engine,vloop,coverId,"0");

				if(loopfactorrates==null || loopfactorrates.size()==0) {
					condtions.clear(); 
					vloop.clear();
		
					for(int i=0;i<rateInfos.size();i++) {							
						RatingInfo r = rateInfos.get(i);
						if("N".equals(r.getFactorRangeYn())) {
							String condtion=r.getDiscretCol()+":"+r.getInputColumValue()+";";
							if(condtions.size()>0)
								condtion=condtion.concat(StringUtils.join(condtions,';'));
							Long count=0L;
							try {
								count =	crservice.countfactorOnlyquery(engine,condtion, coverId,"0");
							}catch (Exception e) {
								e.printStackTrace();
							}	
							
							if(count<=0) {
								r.setInputColumValue("99999");
							} 	
						}
						String condtion=r.getDiscretCol()+":"+r.getInputColumValue()+"";
						if("Y".equals(r.getFactorRangeYn())) {
							condtion=""+r.getInputColumValue()+"~"+r.getRangeFromCol()+"&"+r.getRangeToCol();  
						} 
						condtions.add(condtion);  
					}						 
					vloop.put("1", condtions);
					loopfactorrates =  crservice.loopfactorrates(engine,vloop,coverId,"0"); 

				}
			
			return loopfactorrates;  
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return null;
	}
}
