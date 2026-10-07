package com.maan.eway.calculator.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.maan.eway.bean.EwayFactorDetails;
import com.maan.eway.bean.EwayFactorResultDetail;
import com.maan.eway.bean.EwayVehicleMakemodelMasterDetail;
import com.maan.eway.req.calcengine.CalcEngine;
import com.maan.eway.res.calc.Cover;
import com.maan.eway.res.calc.Loading;

import jakarta.persistence.Tuple;

public class PerilCalculator {
	
	protected RatingFactorsUtil crservice;
	protected CalcEngine engine;
	
	protected List<Tuple> result=null;	
	protected List<Tuple> vehicles=null;
	protected List<Tuple> customers =null;
	
	protected CoverCalculator coverCalculator=null;protected List<Tuple> drivers=null;
	protected List<Tuple> factors=null;
	public PerilCalculator(RatingFactorsUtil crservice, CalcEngine engine, List<Tuple> result, List<Tuple> vehicles,
			List<Tuple> customers, CoverCalculator coverCalculator, List<Tuple> factors,List<Tuple> drivers) {
		super();
		this.crservice = crservice;
		this.engine = engine;
		this.result = result;
		this.vehicles = vehicles;
		this.customers = customers;
		this.coverCalculator=coverCalculator;
		this.factors=factors;
		this.drivers=drivers;
	}

	protected SimpleDateFormat DD_MM_YYYY = new SimpleDateFormat("dd/MM/yyyy")  ;
	
	public void perilCalculator(Cover t) {
		
		if("100020".equals(engine.getInsuranceId())) {
			try {
				String todayInString = DD_MM_YYYY.format(new Date());
				//agencyCode:"+engine.getAgencyCode()+";branchCode:"+engine.getBranchCode()+";"

				EwayVehicleMakemodelMasterDetail vmaster=crservice.collectMakeModelDetails(engine,vehicles);
				//MsDriverDetails	driver=crservice.collectDriver(engine);
				String VehicleClass="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()
				+";status:{Y,R};subCoverId:0;"+todayInString+"~effectiveDateStart&effectiveDateEnd;coverId:"+56+";"+"param9:"+vehicles.get(0).get("vehicleClass").toString()+";";

				String CoverType="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()
				+";status:{Y,R};subCoverId:0;"+todayInString+"~effectiveDateStart&effectiveDateEnd;coverId:"+23+";param9:"+vehicles.get(0).get("insuranceClass").toString()+";";

				String SumInsured="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()
				+";status:{Y,R};subCoverId:0;"+todayInString+"~effectiveDateStart&effectiveDateEnd;coverId:"+101+";"+new BigDecimal(vehicles.get(0).get("sumInsured").toString()).toPlainString()+"~param1&param2;";

				/* old String ThirdPartyLiabilityLimit="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()
			+";status:{Y,R};subCoverId:0;"+todayInString+"~effectiveDateStart&effectiveDateEnd;coverId:"+56+";";
				 */
			/*	String NumberOfVehicles="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()
				+";status:{Y,R};subCoverId:0;"+todayInString+"~effectiveDateStart&effectiveDateEnd;coverId:"+96+";"+vehicles.get(0).get("noOfVehicles").toString()+"~param1&param2;param10:"+customers.get(0).get("policyHolderType").toString()+";";
*/
				String VehicleAge="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()
				+";status:{Y,R};subCoverId:0;"+todayInString+"~effectiveDateStart&effectiveDateEnd;coverId:"+105+";"+vehicles.get(0).get("manufactureAge").toString()+"~param1&param2;";

				String PolicyDuration="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()
				+";status:{Y,R};subCoverId:0;"+todayInString+"~effectiveDateStart&effectiveDateEnd;coverId:"+41+";"+Math.round(Double.parseDouble(vehicles.get(0).get("periodOfInsurance").toString())/365)+"~param1&param2;";

				String LicenseDuration="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()
				+";status:{Y,R};subCoverId:0;"+todayInString+"~effectiveDateStart&effectiveDateEnd;coverId:"+43+";"+drivers.get(0).get("licenseExperience").toString()+"~param1&param2;";;

			/*	String PowerMassRatio="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()
				+";status:{Y,R};subCoverId:0;"+todayInString+"~effectiveDateStart&effectiveDateEnd;coverId:"+38+";"+((Double) Double.parseDouble(vmaster.getPowerKw())/Double.parseDouble(vmaster.getWeightKg()))*1000+"~param1&param2;";
*/

				String VehBodyType="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()
				+";status:{Y,R};subCoverId:0;"+todayInString+"~effectiveDateStart&effectiveDateEnd;coverId:"+54+";param9:"+vmaster.getBodyId()+";";
				Long countBody =crservice.getCountFromRating(VehBodyType);
				if(countBody<=0) {
					VehBodyType="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()
					+";status:{Y,R};subCoverId:0;"+todayInString+"~effectiveDateStart&effectiveDateEnd;coverId:"+54+";param9:99999;";
				}


			/*	String FuelType="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()
				+";status:{Y,R};subCoverId:0;"+todayInString+"~effectiveDateStart&effectiveDateEnd;coverId:"+19+";param9:"+(vmaster.getFueltype().equalsIgnoreCase("Petrol")?"2":"1")+";";
*/

				String VehicleGroup="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()
				+";status:{Y,R};subCoverId:0;"+todayInString+"~effectiveDateStart&effectiveDateEnd;coverId:"+67+";"+vmaster.getVehiclegroup()+"~param21&param22;";

				/*String VehicleUse="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()
				+";status:{Y,R};subCoverId:0;"+todayInString+"~effectiveDateStart&effectiveDateEnd;coverId:"+73+";param9:"+vehicles.get(0).get("motorUsage").toString()+";";
*/
				String DriveClaimNum_12m_0m="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()
				+";status:{Y,R};subCoverId:0;"+todayInString+"~effectiveDateStart&effectiveDateEnd;coverId:"+4+";"+drivers.get(0).get("claimExperience").toString()+"~param1&param2;";

				/*countBody =crservice.getCountFromRating(DriveClaimNum_12m_0m);
				if(countBody<=0) {
					DriveClaimNum_12m_0m="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()
					+";status:{Y,R};subCoverId:0;"+todayInString+"~effectiveDateStart&effectiveDateEnd;coverId:"+4+";param9:99999;";
				}
				
				String DriveClaimNum_24m_12m="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()
				+";status:{Y,R};subCoverId:0;"+todayInString+"~effectiveDateStart&effectiveDateEnd;coverId:"+11+";param9:"+vehicles.get(0).get("claimNum24m12m").toString()+";";
				countBody =crservice.getCountFromRating(DriveClaimNum_24m_12m);
				if(countBody<=0) {
					DriveClaimNum_24m_12m= "companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()
					+";status:{Y,R};subCoverId:0;"+todayInString+"~effectiveDateStart&effectiveDateEnd;coverId:"+11+";param9:99999;";
				}
				String DriveClaimNum_36m_24m="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()
				+";status:{Y,R};subCoverId:0;"+todayInString+"~effectiveDateStart&effectiveDateEnd;coverId:"+12+";param9:"+vehicles.get(0).get("claimNum36m24m").toString()+";";

				countBody =crservice.getCountFromRating(DriveClaimNum_36m_24m);
				if(countBody<=0) {
					DriveClaimNum_36m_24m="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()
					+";status:{Y,R};subCoverId:0;"+todayInString+"~effectiveDateStart&effectiveDateEnd;coverId:"+12+";param9:99999;";
				}
				
				*/

				String DriverAge="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()
				+";status:{Y,R};subCoverId:0;"+todayInString+"~effectiveDateStart&effectiveDateEnd;coverId:"+40+";"+ drivers.get(0).get("age") +"~param1&param2;";
				
				String DriverGender="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()
				+";status:{Y,R};subCoverId:0;"+todayInString+"~effectiveDateStart&effectiveDateEnd;coverId:"+684+";param9:"+drivers.get(0).get("gender")+";";
				
				String RiskArea="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()
				+";status:{Y,R};subCoverId:0;"+todayInString+"~effectiveDateStart&effectiveDateEnd;coverId:"+683+";param10:"+vehicles.get(0).get("cityLimit").toString()+";";
				
				String MaritalStatus="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()
				+";status:{Y,R};subCoverId:0;"+todayInString+"~effectiveDateStart&effectiveDateEnd;coverId:"+10+";param9:"+drivers.get(0).get("maritalStatus")+";";

				
//				String MaritalStatus="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()
//				+";status:{Y,R};subCoverId:0;"+todayInString+"~effectiveDateStart&effectiveDateEnd;coverId:"+40+";param9:"+vehicles.get(0).get("classType").toString()+";";

				/*String PaymentFreq="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()
				+";status:{Y,R};subCoverId:0;"+todayInString+"~effectiveDateStart&effectiveDateEnd;coverId:"+29+";param9:"+vehicles.get(0).get("paymentFrequency").toString()+";";

				String EngineSize="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()
				+";status:{Y,R};subCoverId:0;"+todayInString+"~effectiveDateStart&effectiveDateEnd;coverId:"+100+";"+vmaster.getEnginesizeCc()+"~param1&param2;";

				String MaritalStatus="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()
				+";status:{Y,R};subCoverId:0;"+todayInString+"~effectiveDateStart&effectiveDateEnd;coverId:"+10+";param9:"+drivers.get(0).get("maritalStatus")+";";

				String AreaGroup="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()
				+";status:{Y,R};subCoverId:0;"+todayInString+"~effectiveDateStart&effectiveDateEnd;coverId:"+68+";"+drivers.get(0).get("areaGroup")+"~param21&param22;";
				//  23

				String LossRatio="companyId:"+ engine.getInsuranceId() +";productId:"+engine.getProductId()+";sectionId:"+engine.getSectionId()
				+";status:{Y,R};subCoverId:0;"+todayInString+"~effectiveDateStart&effectiveDateEnd;coverId:"+229+";param10:"+vehicles.get(0).get("previousInsuranceYN")+";"+vehicles.get(0).get("lossRatio")+"~param1&param2;";
				 
				*/
				Map<String,String> queries=new HashMap<String,String>();
				queries.put("VehicleGroup",VehicleGroup);
				queries.put("DriverAgeXGender",DriverAge);
				// queries.put("EngineSize",EngineSize);
				// queries.put("PowerMassRatio",PowerMassRatio);
				queries.put("PolicyDuration",PolicyDuration);
				// queries.put("PaymentFreq",PaymentFreq);
				// queries.put("VehicleUse",VehicleUse);
				queries.put("VehicleAge",VehicleAge);
				queries.put("Vehicle Class",VehicleClass);
				queries.put("MaritalStatus",MaritalStatus);
				queries.put("LicenseDuration",LicenseDuration);
				queries.put("NoClaims0to1",DriveClaimNum_12m_0m);
				queries.put("RiskArea",RiskArea);
				queries.put("DriverGender",DriverGender);
				/*queries.put("NoClaims1to2",DriveClaimNum_24m_12m);
				queries.put("NoClaims2to3",DriveClaimNum_36m_24m);*/ 
				// queries.put("FuelType",FuelType);
				queries.put("VehBodyType",VehBodyType);
				// queries.put("AreaGroup",AreaGroup);
				// queries.put("NumberOfVehicles",NumberOfVehicles);
				queries.put("CoverType",CoverType);
				// queries.put("3YearLossRatio",LossRatio);
				queries.put("Base",SumInsured);
				
				Map<String, List<Tuple>> queriesResult = queryExecuting(queries);
				
				Map<String, List<Tuple>> minRateLoadingResult=new HashMap<String, List<Tuple>>();
				minRateLoadingResult.put("VehicleGroup", queriesResult.get("VehicleGroup"));
				minRateLoadingResult.put("DriverAgeXGender", queriesResult.get("DriverAgeXGender"));
				minRateLoadingResult.put("VehicleAge", queriesResult.get("VehicleAge"));
				minRateLoadingResult.put("LicenseDuration", queriesResult.get("LicenseDuration"));
				minRateLoadingResult.put("NoClaims0to1", queriesResult.get("NoClaims0to1"));
				minRateLoadingResult.put("MaritalStatus", queriesResult.get("MaritalStatus"));
				minRateLoadingResult.put("RiskArea", queriesResult.get("RiskArea"));
				minRateLoadingResult.put("DriverGender", queriesResult.get("DriverGender"));
				
				System.out.println("===== DEBUG: queriesResult =====");
				for (Map.Entry<String, List<Tuple>> entry : queriesResult.entrySet()) {
				    System.out.println("Key: " + entry.getKey() + 
				                       " | Value NULL? " + (entry.getValue() == null) +
				                       " | Size: " + (entry.getValue() == null ? "NULL" : entry.getValue().size()));
				}
					
				List<EwayFactorDetails> data = crservice.saveFactorDetails(queriesResult,engine,result,vehicles,customers,t,minRateLoadingResult);

				// for(EwayFactorDetails f :data)
			}catch (Exception e) {
				e.printStackTrace();
			}
		}else {
			if(t.getLoadings()!=null && t.getLoadings().size()>0) {
				List<EwayFactorDetails> fds=new ArrayList<EwayFactorDetails>();
				int sno=1;
				
				EwayFactorDetails fd=null;						
				 fd=EwayFactorDetails.builder()
						.amendId(0)
						.cdRefno(engine.getCdRefNo())
						.companyId(engine.getInsuranceId())
						.factorId(sno++)
						.factorName(t.getCoverDesc())
						.coverId(Integer.parseInt(t.getCoverId()))
						.coverName(t.getCoverDesc())
						.createdBy(engine.getCreatedBy())
						.entryDate(new Date())
						.fire(0D)
						.thirdParty(0D)
						.theft(0D)
						.windscreen(0D)
						.ownDamage(StringUtils.isBlank(factors.get(0).get("rate")==null?"0":factors.get(0).get("rate").toString())?0D:Double.parseDouble(factors.get(0).get("rate")==null?"0":factors.get(0).get("rate").toString()))
						.msRefno(engine.getMsrefno())
						.productId(Integer.parseInt(engine.getProductId()))
						.requestReferenceNo(engine.getRequestReferenceNo())
						.sectionId(Integer.parseInt(engine.getSectionId()))
						.status("Y")
						.subCoverYn("N")
						.subCoverId(0)
						.subCoverName("")
						.vdRefno(engine.getVdRefNo())
						.vehicleId(Integer.parseInt(engine.getVehicleId()))						
						.build();
			 
			fds.add(fd);
				for(Loading l:t.getLoadings()) {
					List<Tuple> factors = coverCalculator.LoadFactorRates(engine, l.getLoadingId(),l.getFactorTypeId(),engine.getVehicleId(),StringUtils.isBlank(t.getSubCoverId())?"0":t.getSubCoverId());
					try {
					 Tuple tuple = factors.get(0);
					 
					 String calctype=tuple.get("calcType").toString();
					 String rate=tuple.get("rate")==null?"0":tuple.get("rate").toString();
					 String minPremium=tuple.get("minPremium")==null?"0":tuple.get("minPremium").toString();
					 l.setLoadingRate(rate);
					 l.setMaxAmount(new BigDecimal(minPremium));
					 String regulatoryCode=tuple.get("regulatoryCode")==null?"N/A":tuple.get("regulatoryCode").toString();
					 l.setRegulatoryCode(regulatoryCode);
					 l.setLoadingCalcType(calctype);
					 
					}catch (Exception e) {
						 l.setLoadingRate("0");
						 l.setMaxAmount(new BigDecimal("0"));
						 String regulatoryCode="N/A";
						 l.setRegulatoryCode(regulatoryCode);
						 l.setLoadingCalcType("P");
						} 
					 						
							 fd=EwayFactorDetails.builder()
									.amendId(0)
									.cdRefno(engine.getCdRefNo())
									.companyId(engine.getInsuranceId())
									.factorId(sno++)
									.factorName(l.getLoadingDesc())
									.coverId(Integer.parseInt(t.getCoverId()))
									.coverName(l.getLoadingDesc())
									.createdBy(engine.getCreatedBy())
									.entryDate(new Date())
									.fire(0D)
									.thirdParty(0D)
									.theft(0D)
									.windscreen(0D)
									.ownDamage(StringUtils.isBlank(l.getLoadingRate())?0D:Double.parseDouble(l.getLoadingRate()))
									.msRefno(engine.getMsrefno())
									.productId(Integer.parseInt(engine.getProductId()))
									.requestReferenceNo(engine.getRequestReferenceNo())
									.sectionId(Integer.parseInt(engine.getSectionId()))
									.status("Y")
									.subCoverYn("N")
									.subCoverId(0)
									.subCoverName("")
									.vdRefno(engine.getVdRefNo())
									.vehicleId(Integer.parseInt(engine.getVehicleId()))						
									.build();
						 
						fds.add(fd);
					 
				}
				Double premiumRate=fds.stream().mapToDouble(EwayFactorDetails::getOwnDamage).reduce((a,b)->a*b).getAsDouble();	
				try {
					BigDecimal basePremium = domath(this.factors.get(0).get("calcType").toString(),Double.parseDouble(this.factors.get(0).get("rate").toString()), t.getSumInsured(), t.getExchangeRate());
				} catch (NumberFormatException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (ParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				crservice.saveFds(fds,engine);
				
						
				String pattern =  "#####0.####" ;
				DecimalFormat decimalFormat = new DecimalFormat(pattern);
				try {
					premiumRate=Double.valueOf(decimalFormat.format(premiumRate));
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				Double riskPremiumAmt=0D;
				String calctype=this.factors.get(0).get("calcType").toString();
				if("P".equals(calctype)){
					riskPremiumAmt=	t.getSumInsured().multiply(new BigDecimal(premiumRate/100), MathContext.DECIMAL32).doubleValue();	
				 }else if("A".equals(calctype)) {
					 riskPremiumAmt=new BigDecimal(premiumRate, MathContext.DECIMAL32).doubleValue();			
				 }else if("M".equals(calctype)) {
					 riskPremiumAmt = t.getSumInsured().multiply(new BigDecimal(premiumRate/1000), MathContext.DECIMAL32).doubleValue();		
				 }else if("X".equals(calctype)) {
					 riskPremiumAmt = t.getSumInsured().multiply(new BigDecimal(premiumRate)).doubleValue();
				 }
				
				EwayFactorResultDetail efResult=EwayFactorResultDetail.builder()
						.cdRefno(engine.getCdRefNo())
						.companyId(engine.getInsuranceId())
						.coverId(Integer.parseInt(t.getCoverId()))
						.coverName(t.getCoverName())
						.createdBy(engine.getCreatedBy())
						.entryDate(new Date()) 
						.msRefno(engine.getMsrefno())
						.productId(Integer.parseInt(engine.getProductId()))					
						.requestReferenceNo(engine.getRequestReferenceNo())
						.sectionId(Integer.parseInt(engine.getSectionId()))
						.status("Y")
						.targetLossRatio(60D)
						.vdRefno(engine.getVdRefNo())
						.vehicleId(Integer.parseInt(engine.getVehicleId()))
						.minPremium(t.getMinimumPremium().doubleValue())
						.minRate(0D)
						.finalPremiumAmtExclTax(riskPremiumAmt)
						.finalPremiumRateExclTax(premiumRate)
						//.proRataPremiumAmtExclTax(sumInsured)					
						.riskPremiumAmt(t.getSumInsured().doubleValue())
						.riskPremiumRate(premiumRate)
						.build();
				crservice.saveFactorResult(efResult);
				 t.setPremiumBeforeDiscount(new BigDecimal(riskPremiumAmt));
				 t.setMinimumPremium(new BigDecimal("0"));
				 t.setRate(premiumRate);
				 t.setMinrate(StringUtils.isBlank(factors.get(0).get("minimumRate")==null?"0":factors.get(0).get("minimumRate").toString())?0D:Double.parseDouble(factors.get(0).get("minimumRate")==null?"0":factors.get(0).get("minimumRate").toString()));
				 t.setCalcType("P");
				 //t.setRegulatoryCode("NA");
				 /// Referal
				 t.setIsReferral((riskPremiumAmt<1)?"Y":"N");
				 if("Y".equals(t.getIsReferral())){
					 t.setReferalDescription(t.getCoverDesc() +" Referral" );
					
				 }
			}
		}
	}
	
	private Map<String,List<Tuple>> queryExecuting(Map<String,String> queries) {
		Map<String,List<Tuple>> queriesResult=new HashMap<String, List<Tuple>>();
		try {
			/*for(Entry<String, String> entrySet : queries.entrySet()) {
				String key = entrySet.getKey();
				String dataquery = entrySet.getValue();

				Long count =crservice.getCountFromRating(dataquery+"agencyCode:"+engine.getAgencyCode()+";");
				if(count>0) {
					queries.put(key, dataquery+"agencyCode:"+engine.getAgencyCode()+";"); 
				}else {
					queries.put(key, dataquery+"agencyCode:99999;"); 
				}

			}*/
			
			for(Entry<String, String> entrySet : queries.entrySet()) {
				String key = entrySet.getKey();
				String dataquery = entrySet.getValue();
				List<Tuple> queryResult = crservice.getResult(dataquery);
				if(queryResult!=null && queryResult.size()>0) {
					//"agencyCode:"+engine.getAgencyCode()+";"
					List<Tuple> collect = queryResult.stream().filter(t -> t.get("agencyCode").toString().equals(engine.getAgencyCode())).collect(Collectors.toList());
					
					if(collect!=null && collect.size()>0)					
						queriesResult.put(key,collect);	
					else {
						collect = queryResult.stream().filter(t -> t.get("agencyCode").toString().equals("99999")).collect(Collectors.toList());
						queriesResult.put(key,collect);	
					}
				}else 
					System.out.println("No Factor Found for "+key +"\nquery:"+dataquery);
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return queriesResult;
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
		//d = (BigDecimal) decimalFormat.parse(decimalFormat.format(d));
		return d;
	}
}
