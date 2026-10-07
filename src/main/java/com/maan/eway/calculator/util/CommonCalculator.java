package com.maan.eway.calculator.util;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.maan.eway.bean.SalamaOccupation;
import com.maan.eway.bean.SectionCoverMaster;
import com.maan.eway.req.calcengine.CalcEngine;
import com.maan.eway.res.calc.Cover;
import com.maan.eway.res.calc.RatingInfo;
import com.maan.eway.upgrade.criteria.CriteriaService;
import com.maan.eway.upgrade.criteria.SpecCriteria;

import jakarta.persistence.Tuple;
import jakarta.persistence.TupleElement;
//@Component
//@CacheConfig(cacheNames = {"RatingType"})
public class CommonCalculator {
 
	protected RatingFactorsUtil crservice;
	
	protected CriteriaService criservice;
	
	protected SimpleDateFormat DD_MM_YYYY = new SimpleDateFormat("dd/MM/yyyy")  ;
	
	//protected MathContext round=new MathContext(3, RoundingMode.HALF_UP);
	protected CalcEngine engine;
	
	protected String cdRefno;
	protected String vdRefno;
	
	protected List<Tuple> result=null;	
	protected List<Tuple> vehicles=null;
	protected List<Tuple> customers =null;
	protected List<Tuple> commontbl =null;
	protected List<Cover> calculatedcover=null;
	protected List<Tuple> prorata=null;
	protected List<Tuple> drivers=null;
	protected DecimalFormat decimalFormat = null;
	protected  List<Tuple> customerChoiceTaxes;

	/*public void setEngine(CalcEngine engine,List<Cover> c) {
		this.engine = engine;
		this.calculatedcover=c;
	}
	*/
	public void setEngine(CalcEngine engine,List<Cover> c,List<Tuple> result,List<Tuple> vehicles,List<Tuple> customers,List<Tuple> prorata, RatingFactorsUtil crservice,DecimalFormat decimalFormat, List<Tuple> drivers, List<Tuple> customerChoiceTaxes) {
		this.engine = engine;
		this.calculatedcover=c;
		this.result=result;
		this.vehicles=vehicles;
		this.customers=customers;
		this.prorata=prorata;
		this.crservice=crservice;
		this.decimalFormat=decimalFormat;
		this.decimalFormat.setParseBigDecimal(true);		
		this.drivers=drivers;
		this.customerChoiceTaxes=customerChoiceTaxes;
		
	}
	
	public List<Tuple> LoadFactorRates(CalcEngine engine,String coverId,String factorid,String vehicleId, String subCoverId){

	    System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
	            + " <---- LoadFactorRates Block start :---->");

	    Tuple vehicleTuple = vehicles.get(0);

	    if ("294".equals(engine.getSectionId()) && "100053".equals(engine.getInsuranceId())) {

	        vehicleTuple = vehicles.stream()
	                .filter(v -> coverId.equals(String.valueOf(v.get("coverId"))))
	                .findFirst()
	                .orElse(vehicles.get(0));

	        System.out.println("Section 294 vehicle selected for cover : " + coverId);
	        System.out.println("CategoryId = " + vehicleTuple.get("categoryId"));
	        System.out.println("IndustryId = " + vehicleTuple.get("industryId"));
	    }

	    List<Tuple> loadFactorRates = LoadFactorRates(
	            engine,
	            coverId,
	            factorid,
	            vehicleId,
	            vehicleTuple,   
	            customers.get(0),
	            result.get(0),
	            subCoverId,
	            (drivers==null || drivers.isEmpty())?null:drivers.get(0)
	    );

	   	    System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
	            + " <---- LoadFactorRates Block end :---->");

	    return loadFactorRates;
	}
	
	public  List<Tuple> LoadFactorRates(CalcEngine engine,String coverId,String factorid,String vehicleId,Tuple vehicle,Tuple customer,Tuple common,String subCoverId,Tuple drivers) {
		
//		System.out.println("------ VEHICLE TUPLE COLUMNS ------");
//
//        for (TupleElement<?> e : vehicle.getElements()) {
//            System.out.println("Tuple column alias : " + e.getAlias());
//        }
//
//        System.out.println("-----------------------------------");
		
		Map<String,List<String>> vloop=new HashMap<String, List<String>>();
		try {
			
			System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
			        + " <---- LoadFactorRates Block 1 start :---->");
			
				final List<RatingInfo> rateInfos = crservice.LoadRatingType(engine, factorid);
				
				System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
				        + " <---- LoadFactorRates Block 1 end :---->");
					
				 int count=0;

				for (RatingInfo r : rateInfos) {
					
					System.out.println(
					        "RatingField : " + r.getInputColumName() +
					        " Table : " + r.getInputTableName()
					    );

					    System.out.println(
					        "Vehicle value = " + vehicle.get(r.getInputColumName())
					    );
					System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
					        + " <---- RatingInfo loop count :---->"+count++);
					if("MsCustomerDetails".equalsIgnoreCase(r.getInputTableName())) {
						r.setInputColumValue(customer.get(r.getInputColumName()).toString());
					}else if("MsCommonDetails".equalsIgnoreCase(r.getInputTableName())) {
						r.setInputColumValue(common.get(r.getInputColumName()).toString());
					}else if("MsDriverDetails".equalsIgnoreCase(r.getInputTableName())){
						r.setInputColumValue(drivers.get(r.getInputColumName()).toString());
					}else /*if("MS_Vehicle_DETAILS".equalsIgnoreCase(r.getInputTableName()) || "MSVehicleDETAILS".equalsIgnoreCase(r.getInputTableName()) 
							|| "MsHumanDetails".equalsIgnoreCase(r.getInputTableName()) || "MsAssetDetails".equalsIgnoreCase(r.getInputTableName()) )*/ {
						if (vehicle.get(r.getInputColumName()) instanceof BigDecimal) {
							r.setInputColumValue( ((BigDecimal) vehicle.get(r.getInputColumName())).toPlainString());
						}else
							r.setInputColumValue(vehicle.get(r.getInputColumName())!=null?vehicle.get(r.getInputColumName()).toString():"");
					}
					
					
					/*
					String condtion=r.getDiscretCol()+":"+r.getInputColumValue()+"";
					if("Y".equals(r.getFactorRangeYn())) {
						condtion=""+r.getInputColumValue()+"~"+r.getRangeFromCol()+"&"+r.getRangeToCol();  
					} 
					condtions.add(condtion);*/  
				}
			
				//vloop.put(vehicleId, condtions);

				System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
				        + " <---- loopfactorrates Block 2 start :---->");
				List<Tuple> loopfactorrates = crservice.loopfactorrates(engine,vloop,coverId,subCoverId,rateInfos);
				System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
				        + " <---- loopfactorrates Block 2 end :---->");
			/*	if(loopfactorrates==null || loopfactorrates.size()==0) {
					condtions.clear(); 
					vloop.clear();
					for(int i=0;i<rateInfos.size();i++) {							
						RatingInfo r = rateInfos.get(i);
						if("Y".equals(r.getFactorRangeYn())) {
							String condtion=""+r.getInputColumValue()+"~"+r.getRangeFromCol()+"&"+r.getRangeToCol(); 
							condtions.add(condtion); 
						}
					}
					//System.out.println("----------------------"+coverId);
					for(int i=0;i<rateInfos.size();i++) {							
						RatingInfo r = rateInfos.get(i);
						if("N".equals(r.getFactorRangeYn())) {
							String condtion=r.getDiscretCol()+":"+r.getInputColumValue()+";";
							if(condtions.size()>0)
								condtion=condtion.concat(StringUtils.join(condtions,';'));
							//List<> onlyquery =null;
							Long count =0L;
							try {
								count =	crservice.countfactorOnlyquery(engine,condtion, coverId,subCoverId);
								//System.out.println(" ----------------------"+coverId+""+count +condtion);
							}catch (Exception e) {
								e.printStackTrace();
							}	
							
							if(count<=0L) {
								r.setInputColumValue("99999");
								condtion=r.getDiscretCol()+":"+r.getInputColumValue()+";";
								condtions.add(condtion); 
							}else {
								condtion=r.getDiscretCol()+":"+r.getInputColumValue()+";";
								condtions.add(condtion);
							}
						}					
						 
					}
					//System.out.println("----------------------"+coverId);
					vloop.put(vehicleId, condtions);
					loopfactorrates =  crservice.loopfactorrates(engine,vloop,coverId,subCoverId); 
			 		 
				}*/
			
			return loopfactorrates;  
		}catch (Exception e) {
			e.printStackTrace();
		}
		
		
		return null;
	}

	protected BigDecimal domath(String calctype, Double rate,BigDecimal si,BigDecimal exchangeRate) throws ParseException {
		BigDecimal d=BigDecimal.ZERO;
		if("P".equals(calctype)) {
			d = si.multiply(new BigDecimal(rate/100)/*, round*/);			
		 }else if("A".equals(calctype)) {
			d=(new BigDecimal(rate).divide(exchangeRate,3,RoundingMode.HALF_UP));// for foreign currency calculation we have to divide by exchange rate			
		 }else if("M".equals(calctype)) {
			 d = si.multiply(new BigDecimal(rate/1000)/*, round*/);			
		 }else if("X".equals(calctype)) {
			 d = si.multiply(new BigDecimal(rate));
		 }
		d = (BigDecimal) decimalFormat.parse(decimalFormat.format(d));
		return d;
	}
	
	protected BigDecimal domathTira(String calctype, Double rate,BigDecimal premium,BigDecimal exchangeRate) throws ParseException {
		BigDecimal d=BigDecimal.ZERO;
		//(3500/4)*100
		if("P".equals(calctype)) {
			d = premium.divide(new BigDecimal((rate>0D?rate:1D)),3, RoundingMode.HALF_UP ).multiply(new BigDecimal(100)); ///multiply(new BigDecimal(rate/100), round);			
		 }else if("A".equals(calctype)) {
			d=(new BigDecimal(rate));			
		 }else if("M".equals(calctype)) {
			 d = premium.divide(new BigDecimal((rate>0D?rate:1D))).multiply(new BigDecimal(1000));			
		 }else if("X".equals(calctype)) {
			 d = premium.multiply(new BigDecimal(rate));
		 }
		
		d = (BigDecimal) decimalFormat.parse(decimalFormat.format(d));
		return d;
	}
	
	public double derivePropertyRate(CalcEngine engine) {

	    try {

	        System.out.println("\n================ PROPERTY RATE DERIVATION START ================");

	        Tuple firstVehicle = vehicles.get(0);

	        System.out.println("VehicleId        : " + engine.getVehicleId());
	        System.out.println("LocationId       : " + engine.getLocationId());
	        System.out.println("OccupationType   : " + firstVehicle.get("occupationType"));
	        System.out.println("CategoryId       : " + firstVehicle.get("categoryId"));

	        String occupationType = firstVehicle.get("occupationType") == null ? ""
	                : firstVehicle.get("occupationType").toString();

	        String todayInString = DD_MM_YYYY.format(new Date());

	        System.out.println("\n--- STEP 1 : Fetch Base Rate from SalamaOccupation ---");

	        String occSearch = "sNo:" + occupationType
	                + ";companyId:" + engine.getInsuranceId()
	                + ";" + todayInString + "~effectiveDateStart&effectiveDateEnd;";

	        SpecCriteria occCriteria = criservice.createCriteria(
	                SalamaOccupation.class, occSearch, "sNo");

	        List<Tuple> occResult = criservice.getResult(occCriteria, 0, 1);

	        if (occResult == null || occResult.isEmpty()) {
	            System.out.println("No occupation rate found. Returning 0");
	            return 0D;
	        }
	        String uwCategory = occResult.get(0).get("uwCategory") == null
	                ? ""
	                : occResult.get(0).get("uwCategory").toString();

	        System.out.println("UW Category : " + uwCategory);

	        if ("Exclusion".equalsIgnoreCase(uwCategory)) {
	            System.out.println("UW Category is Exclusion → Occupation Referral flagged");
	            this.engine.setIsReferral ("Y");
	            this.engine.setReferralRemarks("Occupation Referral");
	            
	        }

	        double baseRate = occResult.get(0).get("baseRate") == null
	                ? 0D
	                : Double.parseDouble(occResult.get(0).get("baseRate").toString());

	        System.out.println("Base Rate (SalamaOccupation) : " + baseRate);

	        System.out.println("\n--- STEP 2 : Load Section 294 Covers ---");

	        String sec294Search = "companyId:" + engine.getInsuranceId()
	                + ";productId:" + engine.getProductId()
	                + ";sectionId:294"
	                + ";status:{Y,R};"
	                + todayInString + "~effectiveDateStart&effectiveDateEnd;"
	                + "agencyCode:99999;branchCode:99999;";

	        SpecCriteria sec294Criteria = criservice.createCriteria(
	                SectionCoverMaster.class, sec294Search, "coverId");

	        List<Tuple> sec294Covers = criservice.getResult(sec294Criteria, 0, 50);

	        System.out.println("Total Section 294 Covers Loaded : " + sec294Covers.size());

	        if (sec294Covers.isEmpty()) {
	            System.out.println("No section 294 covers found. Returning baseRate.");
	            return baseRate;
	        }

	        String originalSectionId = engine.getSectionId();
	        engine.setSectionId("294");

	        double loadingTotal = 0D;
	        double discountTotal = 0D;

	        System.out.println("\n--- STEP 3 : Calculate Loading / Discount from 294 ---");

	        double lossRatioPercent = 0D;
	        for (Tuple coverTuple : sec294Covers) {

	            String coverId = coverTuple.get("coverId").toString();
	            String factorTypeId = coverTuple.get("factorTypeId").toString();

	            System.out.println("\nProcessing CoverId : " + coverId);
	            System.out.println("FactorTypeId       : " + factorTypeId);

	            List<Tuple> factorRows = LoadFactorRates(
	                    engine,
	                    coverId,
	                    factorTypeId,
	                    engine.getVehicleId(),
	                    "0"
	            );

	            if (factorRows == null || factorRows.isEmpty()) {
	                System.out.println("No rate found for cover");
	                continue;
	            }

	            double coverRate = factorRows.get(0).get("rate") == null
	                    ? 0D
	                    : Double.parseDouble(factorRows.get(0).get("rate").toString());
	            
	            if ("715".equals(coverId)) {
	                lossRatioPercent = coverRate;
	                System.out.println("Cover 715 → LR% = " + lossRatioPercent
	                        + " (captured, excluded from loading/discount)");
	                continue; 
	            }

	            Tuple vehicle = vehicles.stream()
	                    .filter(v -> coverId.equals(String.valueOf(v.get("coverId"))))
	                    .findFirst()
	                    .orElse(firstVehicle);

	            String categoryId = vehicle.get("categoryId") == null
	                    ? "2"
	                    : vehicle.get("categoryId").toString();

	            int category = Integer.parseInt(categoryId);

	            System.out.println("Category           : " + category);
	            System.out.println("Calculated Rate    : " + coverRate);

	            if (category == 1) {

	                discountTotal += coverRate;

	                System.out.println("Type               : DISCOUNT");
	                System.out.println("Running Discount   : " + discountTotal);

	            } else if (category == 2) {

	                System.out.println("Type               : STANDARD (ignored)");

	            } else {

	                loadingTotal += coverRate;

	                System.out.println("Type               : LOADING");
	                System.out.println("Running Loading    : " + loadingTotal);
	            }
	        }

	        engine.setSectionId(originalSectionId);

	        System.out.println("\n--- STEP 4 : LDF Calculation ---");

	        double C = loadingTotal - discountTotal;

	        System.out.println("Total Loading      : " + loadingTotal);
	        System.out.println("Total Discount     : " + discountTotal);
	        System.out.println("C (Load-Discount)  : " + C);

	        double ldf = 1 - C;

	        System.out.println("LDF                : " + ldf);

	        if (ldf <= 0) {

	            System.out.println("Invalid LDF. Returning Base Rate");

	            return baseRate;
	        }

	        System.out.println("Base Rate          : " + baseRate);
	        double adjustedRate = baseRate / ldf;
	        
	        System.out.println("Adjusted Rate      : " + adjustedRate);
	        
	        System.out.println("Loss ratio  : " + lossRatioPercent);
	        
	        if (lossRatioPercent > 0) {
	            adjustedRate = adjustedRate / (lossRatioPercent / 100.0);
	            System.out.println("Adjusted Rate with Loss Ratio : " + adjustedRate);
	        } else {
	            System.out.println("Loss Ratio % is 0 or cover 715 not found — skipping LR adjustment");
	        }

	        System.out.println("\n--- STEP 5 : Final Rate ---");

	        System.out.println("Adjusted Rate with loss ratio : " + adjustedRate);

	        System.out.println("================ PROPERTY RATE DERIVATION END ==================\n");

	        return adjustedRate;

	    } catch (Exception e) {

	        e.printStackTrace();

	        System.out.println("derivePropertyRate error: " + e.getMessage());

	        return 0D;
	    }
	}
	public void setCriservice(CriteriaService criservice) {
	    this.criservice = criservice;
	}
}
