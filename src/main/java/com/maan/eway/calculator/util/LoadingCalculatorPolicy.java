package com.maan.eway.calculator.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;

import com.maan.eway.req.calcengine.CalcEngine;
import com.maan.eway.res.calc.Loading;
import com.maan.eway.res.calc.RatingInfo;

import jakarta.persistence.Tuple;

public class LoadingCalculatorPolicy implements Consumer<Loading> {

	private BigDecimal premium;
	private BigDecimal exchangeRate;
	
	
	protected List<Tuple> policy;
	protected RatingFactorsUtil crservice;
	protected CalcEngine engine;
	protected DecimalFormat decimalFormat = null;
	protected boolean isRateUpdate;
	public LoadingCalculatorPolicy(BigDecimal premium, BigDecimal exchangeRate, List<Tuple> policy,
			RatingFactorsUtil crservice, CalcEngine engine, DecimalFormat decimalFormat,boolean isRateUpdate) {
		super();
		this.premium = premium;
		this.exchangeRate = exchangeRate;
		this.policy = policy;
		this.crservice = crservice;
		this.engine = engine;
		this.decimalFormat = decimalFormat;
		this.isRateUpdate=isRateUpdate;
	}


	@Override
	public void accept(Loading t) {
	 try {
		 String calctype= t.getLoadingCalcType();
		 t.setLoadingAmount(BigDecimal.ZERO);
		 /*ChartAccountRequest r=new ChartAccountRequest();
			r.setCompanyId(engine.getInsuranceId());
			r.setChartId(t.getLoadingforId());
			r.setProductId(engine.getProductId());
			r.setDiscountYn("Y");
			r.setRequestRefNo(engine.getRequestReferenceNo());
			
			CommonRes drcr = crservice.drcrEntry(r);
			
			premium = new BigDecimal(drcr.getCommonResponse().toString());
*/
			
		 if("F".equals(t.getLoadingCalcType()) && !isRateUpdate) {
			 List<Tuple> factors = LoadFactorRates(engine, t.getLoadingId(),t.getFactorTypeId());
			 Tuple tuple = factors.get(0);
			 calctype=tuple.get("calcType").toString();
			 String rate=tuple.get("rate")==null?"0":tuple.get("rate").toString();
			 String minPremium=tuple.get("minPremium")==null?"0":tuple.get("minPremium").toString();
			 t.setLoadingRate(rate);
			 t.setMaxAmount(new BigDecimal(minPremium));
			 String regulatoryCode=tuple.get("regulatoryCode")==null?"N/A":tuple.get("regulatoryCode").toString();
			 t.setRegulatoryCode(regulatoryCode);
			 t.setLoadingCalcType(calctype);
		 }
		 BigDecimal domath = domath(calctype, Double.parseDouble(t.getLoadingRate()), premium,exchangeRate);
		 t.setLoadingAmount(domath);
		 t.setMaxAmount(premium);
		/* if(t.getLoadingAmount().compareTo(t.getMaxAmount())==1) {
			 t.setLoadingAmount(t.getMaxAmount());
		 }*/
		 
	 }catch (Exception e) {
		 e.printStackTrace();
	 }
		
	}
	

	public List<Tuple> LoadFactorRates(CalcEngine engine,String coverId,String factorid){
		return LoadFactorRates(engine, coverId, factorid, policy.get(0));
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
	private List<Tuple> LoadFactorRates(CalcEngine engine, String coverId, String factorid, Tuple tuple) {
		Map<String,List<String>> vloop=new HashMap<String, List<String>>();
		try {
			
			
			
				List<RatingInfo> rateInfos = crservice.LoadRatingType(engine, factorid);
				
				
					
				 

				List<String> condtions=new ArrayList<String>();
				for (RatingInfo r : rateInfos) {
					  if("MsPolicyDetails".equalsIgnoreCase(r.getInputTableName())){
						r.setInputColumValue(tuple.get(r.getInputColumName()).toString());
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
