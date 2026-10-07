package com.maan.eway.calculator.util;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.maan.eway.req.calcengine.CalcEngine;
import com.maan.eway.res.calc.Cover;
import com.maan.eway.res.calc.CoverException;
import com.maan.eway.res.calc.Endorsement;
import com.maan.eway.res.calc.Tax;

import jakarta.persistence.Tuple;

//@Component
public class EndtCoverCalculator  extends CommonCalculator implements Consumer<Cover> {
	
	
	/*@Autowired
	private CoverCalculator calc;*/
	private Date effectiveDate;	
	private Boolean isPolicyPeriod;
	private String oldPolicyPeriod;
	private String currentPolicyPeriod;
	private Date prevPolicyEndDate;
	public EndtCoverCalculator(Boolean isPolicyPeriod, String oldPolicyPeriod, String currentPolicyPeriod, Date prevPolicyEndDate) {
		// TODO Auto-generated constructor stub
		this.isPolicyPeriod=isPolicyPeriod;
		this.oldPolicyPeriod=oldPolicyPeriod;
		this.currentPolicyPeriod=currentPolicyPeriod;
		this.prevPolicyEndDate=prevPolicyEndDate;
	}

	public void setEngine(CalcEngine engine,List<Cover> c,List<Tuple> result,List<Tuple> vehicles,List<Tuple> customers
			,List<Tuple> prorata, RatingFactorsUtil crservice,Date effectiveDate,DecimalFormat decimalFormat,List<Tuple> drivers,List<Tuple> customerChoiceTaxes) {		
		this.setEngine(engine, c, result, vehicles, customers, prorata, crservice,decimalFormat,drivers,customerChoiceTaxes);
		this.effectiveDate=effectiveDate;
	}

	@Override
	public void accept(Cover t) {
		try {
              Boolean cancelWith0=true;
			if("Y".equals( t.getIsSubCover())) {
				//this.setEngine(engine);
				t.getSubcovers().stream().forEach(this);
			}else {
				Endorsement endorsement = null;
				//	 loadOnetimetable(engine);
				if(t.getEndorsements()!=null && t.getEndorsements().size()>0) {
					t.getEndorsements().sort(new Comparator<Endorsement>() {
						@Override
						public int compare(Endorsement o1, Endorsement o2) {
							// TODO Auto-generated method stub
							return o1.getEndtCount().compareTo(o2.getEndtCount());
						}

					}.reversed());
					endorsement= t.getEndorsements().get(0);
					//isCancellation=endorsement.getEndorsementId().equals("842");
				}

				BigDecimal exchangeRate= new BigDecimal(vehicles.get(0).get("exchangeRate")==null?"1":vehicles.get(0).get("exchangeRate").toString());
				t.setExchangeRate(exchangeRate);
				String currecy=vehicles.get(0).get("currency")==null?"N/A":vehicles.get(0).get("currency").toString();
				t.setCurrency(currecy);

				// t.setProRata(new BigDecimal("1"));
				if("Y".equals(engine.getCoverModification()) && "Y".equals(t.getEndtProRataYn()) && "Y".equals(t.getUserOpt()) && endorsement!=null) {
//					t.setProRata(t.getProRata().divide(new BigDecimal("100")));
					t.setProRata(t.getProRata());
				}else  if("Y".equals(t.getEndtProRataYn()) && !"Y".equals(t.getUserOpt()) ) {
					/*BigDecimal percenat=prorata.get(0).get("percent")==null?BigDecimal.ZERO:new BigDecimal(prorata.get(0).get("percent").toString());	
					t.setProRata(percenat.divide(new BigDecimal("100")));*/
					
					Date periodStart =  effectiveDate;
					Date periodEnd = t.getPolicyEndDate() ;
					Long diffInMillies = Math.abs(periodEnd.getTime() - periodStart.getTime());
					Long daysBetween =  TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) +1 ;						
					// Check Leap Year
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
					boolean leapYear = LocalDate.parse(sdf.format(periodEnd) ).isLeapYear();
					String diff = String.valueOf( daysBetween==365 &&  leapYear==true ? daysBetween+1 : daysBetween );
					if(Integer.parseInt(diff)<0) diff="0";

					String policyTypeId = (vehicles.get(0).get("insuranceClass") == null ? "99999"
							: vehicles.get(0).get("insuranceClass").toString());

					List<Tuple> prorata =null;
					if(!"D".equals(t.getEndtProRataYn())) 
						prorata =  crservice.loadProRataData(engine, diff,policyTypeId);
					BigDecimal percenat=prorata.get(0).get("percent")==null?BigDecimal.ZERO:new BigDecimal(prorata.get(0).get("percent").toString());	
					t.setProRata(percenat.divide(new BigDecimal("100")));
				}else if("D".equals(t.getEndtProRataYn()) && !"Y".equals(t.getUserOpt())) {
					Date periodStart =  effectiveDate;
					Date periodEnd = t.getPolicyEndDate() ;
					Long diffInMillies = Math.abs(periodEnd.getTime() - periodStart.getTime());
					Long daysBetween =  TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) +1 ;						
					// Check Leap Year
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
					boolean leapYear = LocalDate.parse(sdf.format(periodEnd) ).isLeapYear();
					String diff = String.valueOf( daysBetween==365 &&  leapYear==true ? daysBetween+1 : daysBetween );
					if(Integer.parseInt(diff)<0) diff="0";
//					String periodOfInsurance =vehicles.get(0).get("periodOfInsurance") == null ? "365": vehicles.get(0).get("periodOfInsurance").toString();
					t.setPolicyPeriod(new BigDecimal(diff));
					t.setProRata(t.getPolicyPeriod().divide(new BigDecimal("365") ,MathContext.DECIMAL32));
				}else {
					t.setProRata(new BigDecimal("1"));
				}
				BigDecimal si=BigDecimal.ZERO;
				if(!"A".equals(t.getCalcType()))
					si=vehicles.get(0).get(t.getCoverBasedOn())==null?BigDecimal.ZERO:new BigDecimal(vehicles.get(0).get(t.getCoverBasedOn()).toString());
				if("Y".equals(t.getDependentCoveryn())) {
					if(calculatedcover!=null) {
						Cover ct = calculatedcover.stream().filter(c->c.getCoverId().equals(t.getDependentCoverId())).findAny().orElse(null);
						si=ct!=null?ct.getPremiumExcluedTax():BigDecimal.ZERO;
					}
				}		
				 si=si.subtract(t.getFreeCoverLimit());
				 si=si.compareTo(BigDecimal.ZERO)>0?si:BigDecimal.ZERO;
				t.setSumInsured(si);
				t.setSumInsuredLc(si.multiply(exchangeRate,MathContext.DECIMAL64));
				if(t.getSumInsured().compareTo(t.getCoverageLimit())>0) {
					t.setIsReferral("Y");
					t.setReferalDescription("CoverageLimit Referral Limits Upto"+t.getCoverageLimit());
					t.setPremiumBeforeDiscount(BigDecimal.ZERO);					 
					t.setPremiumBeforeDiscountLC(BigDecimal.ZERO);
					//t.setCalcType("P");
				}
				BigDecimal domath = domath(t.getCalcType(), t.getRate(), si,t.getExchangeRate());
				t.setPremiumBeforeDiscount(domath);				 
				t.setPremiumBeforeDiscountLC((BigDecimal) decimalFormat.parse(decimalFormat.format(t.getPremiumBeforeDiscount().multiply(t.getExchangeRate())))) ;


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
				t.setPremiumAfterDiscountLC((BigDecimal) decimalFormat.parse(decimalFormat.format(t.getPremiumAfterDiscount().multiply(t.getExchangeRate()))));

				t.setPremiumExcluedTax(t.getPremiumAfterDiscount());				 
				t.setPremiumExcluedTaxLC((BigDecimal) decimalFormat.parse(decimalFormat.format(t.getPremiumExcluedTax().multiply(t.getExchangeRate()))));
				// Minimium Premium setup.

				boolean isCancellation=false;

				t.setMinimumPremiumYn("N");
				if(t.getPremiumAfterDiscountLC().compareTo(t.getMinimumPremium())<0 /*&& !isCancellation*/) {

					t.setPremiumExcluedTax((BigDecimal) decimalFormat.parse(decimalFormat.format(t.getMinimumPremium().divide(t.getExchangeRate(),MathContext.DECIMAL64)))); 
					t.setPremiumExcluedTaxLC(t.getMinimumPremium());
					t.setMinimumPremiumYn("Y");
				}		 
				Double totaltax=0D;
				if(t.getTaxes()!=null && t.getTaxes().size()>0) {
					TaxCalculator tcal=new TaxCalculator(t.getPremiumExcluedTax(),t.getExchangeRate(),this,customers.get(0),this.customerChoiceTaxes);
					t.getTaxes().stream().forEach(tcal);
					totaltax = t.getTaxes().stream().mapToDouble(i->i.getTaxAmount().doubleValue()).sum();
				}		 

				t.setPremiumIncludedTax((BigDecimal) decimalFormat.parse(decimalFormat.format(t.getPremiumExcluedTax().add(new BigDecimal(totaltax)))));	
				t.setPremiumIncludedTaxLC((BigDecimal) decimalFormat.parse(decimalFormat.format(t.getPremiumIncludedTax().multiply(t.getExchangeRate()))));


				if(t.getEndorsements()!=null && t.getEndorsements().size()>0) {
					/* t.getEndorsements().sort(new Comparator<Endorsement>() {
						@Override
						public int compare(Endorsement o1, Endorsement o2) {
							// TODO Auto-generated method stub
							return o1.getEndtCount().compareTo(o2.getEndtCount());
						}

					}.reversed());*/
					//new premium-old prem
					endorsement = t.getEndorsements().get(0);
					boolean dontGo=false;

					LocalDate effectiveLocalDate = effectiveDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
					LocalDate policyEndLocalDate = endorsement.getPolicyEndDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();

					if (effectiveLocalDate.equals(policyEndLocalDate)) {
					    isCancellation = true;
					    isPolicyPeriod = false;
					}


					
					if("Y".equals(engine.getCoverModification())) {

						if(("Y".equals(t.getEndtProRataYn()) || "D".equals(t.getEndtProRataYn())) && "Y".equals(t.getUserOpt())) {
							// Date Differents
							Date periodStart =  effectiveDate;
//							Date periodEnd = t.getPolicyEndDate() ;
							Date periodEnd = endorsement.getPolicyEndDate() ;
							if(isCancellation) periodEnd = prevPolicyEndDate;
							Long diffInMillies = Math.abs(periodEnd.getTime() - periodStart.getTime());
							Long daysBetween =  TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) +1;
							// Check Leap Year
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
							boolean leapYear = LocalDate.parse(sdf.format(periodEnd) ).isLeapYear();
							String diff = String.valueOf( daysBetween==365 &&  leapYear==true ? daysBetween+1 : daysBetween );

//							String periodOfInsurance=(vehicles.get(0).get("periodOfInsurance")==null?(currentPolicyPeriod != null && !currentPolicyPeriod.trim().isEmpty())?currentPolicyPeriod:"365":vehicles.get(0).get("periodOfInsurance").toString());
							String periodOfInsurance=(vehicles.get(0).get("periodOfInsurance")==null?"365":vehicles.get(0).get("periodOfInsurance").toString());
//					        int period = Integer.parseInt(periodOfInsurance);
//					        int diffValue = Integer.parseInt(diff);
//					        int result = period - diffValue;
//					        diff = String.valueOf(result);
							diff= String.valueOf(Integer.parseInt(periodOfInsurance)-Integer.parseInt(diff));
							diff= String.valueOf(Math.abs(Integer.parseInt(diff)));
							String policyTypeId = (vehicles.get(0).get("insuranceClass") == null ? "99999"
									: vehicles.get(0).get("insuranceClass").toString());
							if(Integer.parseInt(diff)<0) diff="0";
							List<Tuple> prorata =null;
							if(!"D".equals(t.getEndtProRataYn())) 
								prorata = crservice.loadProRataData(engine, diff,policyTypeId);


							endorsement.setProRataYn("Y");
							if(prorata!=null && prorata.size()>0) {
								BigDecimal percenat=prorata.get(0).get("percent")==null?BigDecimal.ZERO:new BigDecimal(prorata.get(0).get("percent").toString());
								BigDecimal p=new BigDecimal("100").subtract(percenat).divide(new BigDecimal("100"));
								if(p.compareTo(BigDecimal.ZERO)==0)
								cancelWith0=false;
								t.setProRata(p);
								endorsement.setProRata(p);
							}else if("D".equals(t.getEndtProRataYn())){
//								BigDecimal endtPolicyPeriod= new BigDecimal(periodOfInsurance).subtract(endorsement.getPolicyPeriod());
								BigDecimal endtPolicyPeriod;
								BigDecimal p;
								if(isPolicyPeriod) {
//									endtPolicyPeriod= new BigDecimal(endorsement.getPolicyPeriod().toString()).subtract(new BigDecimal (periodOfInsurance)); //by amal
									endtPolicyPeriod= new BigDecimal(oldPolicyPeriod).subtract(new BigDecimal (periodOfInsurance));
									p = endtPolicyPeriod.divide(new BigDecimal("365") ,MathContext.DECIMAL32);
									if(endtPolicyPeriod.compareTo(BigDecimal.ZERO)<0) {
										dontGo=true;
										p=p.abs();
									}
								}else {
								 endtPolicyPeriod= new BigDecimal(periodOfInsurance).subtract(new BigDecimal (diff)); //by amal
								// p = (currentPolicyPeriod != null && !currentPolicyPeriod.trim().isEmpty())?endtPolicyPeriod.divide(new BigDecimal(currentPolicyPeriod) ,MathContext.DECIMAL32):endtPolicyPeriod.divide(new BigDecimal("365") ,MathContext.DECIMAL32);
								 p = endtPolicyPeriod.divide(new BigDecimal("365") ,MathContext.DECIMAL32);
								}
								t.setProRata(p);
								endorsement.setProRata(p);
							}else {
								BigDecimal p=new BigDecimal("1");
								t.setProRata(p);
								endorsement.setProRata(p);
							}

						}else if(("Y".equals(t.getEndtProRataYn()) || "D".equals(t.getEndtProRataYn())) && !"Y".equals(t.getUserOpt())) {
							// Date Differents
							Date periodStart =  effectiveDate;
							Date periodEnd = t.getPolicyEndDate() ;
							Long diffInMillies = Math.abs(periodEnd.getTime() - periodStart.getTime());
							Long daysBetween =  TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) +1 ;						
							// Check Leap Year
							SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
							boolean leapYear = LocalDate.parse(sdf.format(periodEnd) ).isLeapYear();
							String diff = String.valueOf( daysBetween==365 &&  leapYear==true ? daysBetween+1 : daysBetween );
							if(Integer.parseInt(diff)<0) diff="0";

							String policyTypeId = (vehicles.get(0).get("insuranceClass") == null ? "99999"
									: vehicles.get(0).get("insuranceClass").toString());

							List<Tuple> prorata =null;
							if(!"D".equals(t.getEndtProRataYn())) 
								prorata =  crservice.loadProRataData(engine, diff,policyTypeId);

							if(prorata !=null && prorata.size()>0) {
								BigDecimal percenat=prorata.get(0).get("percent")==null?BigDecimal.ZERO:new BigDecimal(prorata.get(0).get("percent").toString());
								//BigDecimal p=new BigDecimal("100").subtract(percenat).divide(new BigDecimal("100"));
								BigDecimal p=percenat.divide(new BigDecimal("100"));
								t.setProRata(p);
								endorsement.setProRata(p);
							}else if("D".equals(t.getEndtProRataYn())){
							//	BigDecimal endtPolicyPeriod= new BigDecimal(diff).subtract(endorsement.getPolicyPeriod());
								BigDecimal endtPolicyPeriod= endorsement.getPolicyPeriod().subtract(new BigDecimal(diff));
								
								BigDecimal p = endtPolicyPeriod.divide(new BigDecimal("365") ,MathContext.DECIMAL32);
								t.setProRata(p);
								endorsement.setProRata(p);
								if(("851".equalsIgnoreCase(engine.getEndtTypeId()) || "854".equalsIgnoreCase(engine.getEndtTypeId()) ||  "846".equalsIgnoreCase(engine.getEndtTypeId())))
								{
									BigDecimal p1=new BigDecimal("1");
									t.setProRata(p1);
									endorsement.setProRata(p1);
									endorsement.setPolicyPeriod(endtPolicyPeriod.compareTo(BigDecimal.ZERO) == 0 ? endorsement.getPolicyPeriod() : new BigDecimal(diff));
								}
							}else {
								BigDecimal p=new BigDecimal("1");
								t.setProRata(p);
								endorsement.setProRata(p);
							}

						}

						if(t.getProRata().doubleValue()==0D && cancelWith0) 
							t.setProRata(new BigDecimal("1"));

						//endorsement.setPremiumAfterDiscountLC(t.getPremiumAfterDiscountLC().multiply(t.getProRata()).setScale(round.getPrecision(),RoundingMode.HALF_UP));
						//endorsement.setPremiumAfterDiscount(t.getPremiumAfterDiscount().multiply(t.getProRata()).setScale(round.getPrecision(),RoundingMode.HALF_UP));

						// Temp Prev Premium
						endorsement.setPremiumAfterDiscountLC(endorsement.getPremiumExcluedTaxLC());
						endorsement.setPremiumAfterDiscount(endorsement.getPremiumExcluedTaxLC().divide(endorsement.getExchangeRate(),MathContext.DECIMAL64));
						//if(!"A".equals(t.getCalcType())) {	
							endorsement.setPremiumBeforeDiscountLC((BigDecimal) decimalFormat.parse(decimalFormat.format(t.getPremiumBeforeDiscountLC().multiply(t.getProRata()))));
							endorsement.setPremiumBeforeDiscount((BigDecimal) decimalFormat.parse(decimalFormat.format(t.getPremiumBeforeDiscount().multiply(t.getProRata()))));
							
	
							endorsement.setPremiumExcluedTax((BigDecimal) decimalFormat.parse(decimalFormat.format(t.getPremiumExcluedTax().multiply(t.getProRata()))));					 		
							endorsement.setPremiumExcluedTaxLC((BigDecimal) decimalFormat.parse(decimalFormat.format(t.getPremiumExcluedTaxLC().multiply(t.getProRata()))));
						/*}else {
							endorsement.setPremiumBeforeDiscountLC(BigDecimal.ZERO);
							endorsement.setPremiumBeforeDiscount(BigDecimal.ZERO);
							endorsement.setPremiumExcluedTax(BigDecimal.ZERO);
							endorsement.setPremiumExcluedTaxLC(BigDecimal.ZERO);
						}*/
						t.setDiffPremiumIncludedTax(BigDecimal.ZERO);
						t.setDiffPremiumIncludedTaxLC(BigDecimal.ZERO);


					}else {
						// endorsement.setPremiumAfterDiscountLC(t.getPremiumAfterDiscountLC().subtract(endorsement.getPremiumAfterDiscountLC()));
						// endorsement.setPremiumAfterDiscount(t.getPremiumAfterDiscount().subtract(endorsement.getPremiumAfterDiscount()));

						/*	 if("Y".equals(t.getProRataYn()) && "Y".equals(t.getUserOpt())) {
									// Date Differents
										 Date periodStart =  effectiveDate;
											Date periodEnd = t.getPolicyEndDate() ;
										Long diffInMillies = Math.abs(periodEnd.getTime() - periodStart.getTime());
										Long daysBetween =  TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) ;
										// Check Leap Year
										SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
										boolean leapYear = LocalDate.parse(sdf.format(periodEnd) ).isLeapYear();
										String diff = String.valueOf( daysBetween==365 &&  leapYear==true ? daysBetween+1 : daysBetween );

										String periodOfInsurance=(vehicles.get(0).get("periodOfInsurance")==null?"365":vehicles.get(0).get("periodOfInsurance").toString());
										//Removal Logic
										diff= String.valueOf(Integer.parseInt(periodOfInsurance)-Integer.parseInt(diff));

										List<Tuple> prorata =  crservice.loadProRataData(engine, diff);

										endorsement.setProRataYn("Y");
										if(prorata.size()>0) {
										 BigDecimal percenat=prorata.get(0).get("percent")==null?BigDecimal.ZERO:new BigDecimal(prorata.get(0).get("percent").toString());
										 BigDecimal p=new BigDecimal("100").subtract(percenat).divide(new BigDecimal("100"));
										 t.setProRata(p);
										 endorsement.setProRata(p);
										}else {
											 BigDecimal p=new BigDecimal("1");
											t.setProRata(p);
											 endorsement.setProRata(p);
										}

					 		 }else if("Y".equals(t.getProRataYn()) && !"Y".equals(t.getUserOpt()))*/
						BigDecimal totalSumInsuredendt=endorsement.getEndorsementsumInsuredLc().abs();
								{
					 			
					 			
					 			if(!"A".equals(t.getCalcType())) {
						 			 endorsement.setEndorsementsumInsuredLc(t.getSumInsuredLc().subtract(endorsement.getEndorsementsumInsuredLc()));//.multiply(exchangeRate,MathContext.DECIMAL64));
						 			 endorsement.setEndorsementsumInsured((BigDecimal) decimalFormat.parse(decimalFormat.format(endorsement.getEndorsementsumInsuredLc().divide(exchangeRate,MathContext.DECIMAL64))));
						 		 }else {
						 			 endorsement.setEndorsementsumInsuredLc(BigDecimal.ZERO);
						 			 endorsement.setEndorsementsumInsured(BigDecimal.ZERO);
						 			 endorsement.setEndorsementRate(0D);
						 		 }
					 			
					 			 
					 			 // Date Differents
					 			 Date periodStart =  effectiveDate;
//					 			 Date periodEnd = t.getPolicyEndDate() ;
					 			Date periodEnd = endorsement.getPolicyEndDate() ;  //new
					 			 Long diffInMillies = Math.abs(periodEnd.getTime() - periodStart.getTime());
					 			 Long daysBetween =  TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) +1 ;						
					 			 // Check Leap Year
					 			 SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
					 			 boolean leapYear = LocalDate.parse(sdf.format(periodEnd) ).isLeapYear();
					 			 if(daysBetween>366) daysBetween=365L;
					 			 String diff = String.valueOf( daysBetween==365 &&  leapYear==true ? daysBetween+1 : daysBetween );

					 			String periodOfInsurance=(vehicles.get(0).get("periodOfInsurance")==null?"365":vehicles.get(0).get("periodOfInsurance").toString());
					 			//Removal Logic
								diff= String.valueOf(Integer.parseInt(periodOfInsurance)-Integer.parseInt(diff));

					 			
					 			 String policyTypeId = (vehicles.get(0).get("insuranceClass") == null ? "99999"
					 					 : vehicles.get(0).get("insuranceClass").toString());
					 			 List<Tuple> prorata =null;
					 			 if(!"D".equals(t.getEndtProRataYn()))
					 				 prorata =  crservice.loadProRataData(engine, diff,policyTypeId);

					 			 if(prorata !=null && prorata.size()>0) {
					 				 BigDecimal percenat=prorata.get(0).get("percent")==null?BigDecimal.ZERO:new BigDecimal(prorata.get(0).get("percent").toString());
					 				BigDecimal p=percenat.divide(new BigDecimal("100"),MathContext.DECIMAL32);					 				
					 				if(endorsement.getEndorsementsumInsured().compareTo(BigDecimal.ZERO)<0) {
					 					p=new BigDecimal("100").subtract(percenat).divide(new BigDecimal("100"),MathContext.DECIMAL32);
					 				}
					 				
					 				 
					 				 t.setProRata(p);
					 				 endorsement.setProRata(p);
					 			 }else if("D".equals(t.getEndtProRataYn())){
					 				// BigDecimal endtPolicyPeriod= new BigDecimal(diff).subtract(endorsement.getPolicyPeriod());
					 				 BigDecimal endtPolicyPeriod= new BigDecimal(periodOfInsurance).subtract(new BigDecimal(diff)); 
					 				// BigDecimal p = (currentPolicyPeriod != null && !currentPolicyPeriod.trim().isEmpty())?endtPolicyPeriod.divide(new BigDecimal(currentPolicyPeriod) ,MathContext.DECIMAL32):endtPolicyPeriod.divide(new BigDecimal("365") ,MathContext.DECIMAL32);
					 				if(isPolicyPeriod) {
					 					endtPolicyPeriod = new BigDecimal(periodOfInsurance).subtract(new BigDecimal(oldPolicyPeriod)); 
					 				}
					 				BigDecimal p = endtPolicyPeriod.divide(new BigDecimal("365") ,MathContext.DECIMAL32);
					 				 t.setProRata(p);
					 				 endorsement.setProRata(p);
					 			 }else {
					 				 BigDecimal p=new BigDecimal("1");
					 				 t.setProRata(p);
					 				 endorsement.setProRata(p);
					 			 }

					 		 }

					 		 if(t.getProRata().doubleValue()==0D) {
					 			 t.setProRata(BigDecimal.ONE);
					 			 endorsement.setProRata(BigDecimal.ONE);
					 		 }

					 		 // Temp Prev Premium

					 		 endorsement.setPremiumAfterDiscountLC(endorsement.getPremiumExcluedTaxLC());
					 		 endorsement.setPremiumAfterDiscount(endorsement.getPremiumExcluedTax());
					 		 endorsement.setExchangeRate(exchangeRate);
					 		 

					 		 if("Y".equals(endorsement.getMinimumPremiumYn())) { // if previous endorsment is minimum Premium dont calculation only subtract ... 14/12/2023 lalit sir told 
					 			domath = t.getPremiumExcluedTax().subtract(endorsement.getMinimumPremium());  ///endorsement.getPremiumExcluedTax());
					 		 }else if("Y".equals(t.getMinimumPremiumYn()) && !"A".equals(t.getCalcType())){
					 			 BigDecimal actualSumInsured = t.getMinimumPremium().divide(new BigDecimal(endorsement.getEndorsementRate()/100),MathContext.DECIMAL32);
					 			 BigDecimal derivedSumInsred = totalSumInsuredendt.subtract(actualSumInsured);
					 			if(endorsement.getEndorsementsumInsured().compareTo(BigDecimal.ZERO)<0)
					 				derivedSumInsred=derivedSumInsred.multiply(new BigDecimal("-1"));
					 			/*else
					 				derivedSumInsred=derivedSumInsred.multiply(new BigDecimal("1"));*/
					 				
					 			 domath = domath(endorsement.getEndorsementCalcType(), endorsement.getEndorsementRate(), derivedSumInsred,endorsement.getExchangeRate());
					 		 }else {
						 		 domath = domath(endorsement.getEndorsementCalcType(), endorsement.getEndorsementRate(), endorsement.getEndorsementsumInsured(),endorsement.getExchangeRate()); 	 
					 		 }
					 		endorsement.setMinimumPremiumYn(t.getMinimumPremiumYn());
					 		endorsement.setProRataYn("Y");
					 		 if(domath.compareTo(BigDecimal.ZERO)<0)
					 			 dontGo=true;
					 		 /* else if(endorsement.getPremiumAfterDiscountLC().compareTo(endorsement.getPremiumExcluedTaxLC())>0)
							 	dontGo=false;
					 		  */
					 		 endorsement.setPremiumBeforeDiscount(domath.multiply(endorsement.getProRata()));
					 		 endorsement.setPremiumBeforeDiscountLC((BigDecimal) decimalFormat.parse(decimalFormat.format(endorsement.getPremiumBeforeDiscount().multiply(endorsement.getExchangeRate()))));

					 		 endorsement.setPremiumExcluedTax(endorsement.getPremiumBeforeDiscount());				 
					 		 endorsement.setPremiumExcluedTaxLC((BigDecimal) decimalFormat.parse(decimalFormat.format(endorsement.getPremiumExcluedTax().multiply(endorsement.getExchangeRate())))); 						 

					 		 //endorsement.setProRata(t.getProRata());

					}



					totaltax=0D;
					if(endorsement.getTaxes()!=null && endorsement.getTaxes().size()>0) {

						if(endorsement.getPremiumExcluedTax().compareTo(BigDecimal.ZERO)>=0)
							endorsement.getTaxes().removeIf(ta -> ta.getTaxFor().equals("EC"));
						else
							endorsement.getTaxes().removeIf(ta -> ta.getTaxFor().equals("ER"));

						String endtTypeId;
						if(StringUtils.isNotBlank(engine.getEndtTypeId())) {
							endtTypeId = engine.getEndtTypeId();
						}else
						{
							 endtTypeId = vehicles.get(0).get("endtTypeId")==null?"":vehicles.get(0).get("endtTypeId").toString();
						}

					

						List<Tax> notendtfees=endorsement.getTaxes().stream().filter(v -> !v.getTaxId().equals(endtTypeId)).collect(Collectors.toList());
						List<Tax> inendtfees=endorsement.getTaxes().stream().filter(v -> v.getTaxId().equals(endtTypeId)).collect(Collectors.toList());
						Double endtFee=0D;
						if(inendtfees.size()>0) {
							TaxCalculator tcal=new TaxCalculator(endorsement.getPremiumExcluedTax().abs(),t.getExchangeRate(),this,customers.get(0),this.customerChoiceTaxes);
							inendtfees.stream().forEach(tcal);
							endtFee= inendtfees.stream().mapToDouble(o -> o.getTaxAmount().doubleValue()).sum();
						}



						TaxCalculator tcal=new TaxCalculator(endorsement.getPremiumExcluedTax().abs().add(new BigDecimal(endtFee)),t.getExchangeRate(),this,customers.get(0),this.customerChoiceTaxes);  
						notendtfees.stream().filter(f -> "N".equals(f.getDependentYn())).forEach(tcal);
						Double totaltax_N = notendtfees.stream().filter(f -> "N".equals(f.getDependentYn())).mapToDouble(i->i.getTaxAmount().doubleValue()).sum();


						tcal=new TaxCalculator(endorsement.getPremiumExcluedTax().abs().add(new BigDecimal(endtFee)).add(new BigDecimal(totaltax_N)),t.getExchangeRate(),this,customers.get(0),this.customerChoiceTaxes);
						notendtfees.stream().filter(f -> "Y".equals(f.getDependentYn())).forEach(tcal);
						Double totaltax_Y = notendtfees.stream().filter(f -> "Y".equals(f.getDependentYn())).mapToDouble(i->i.getTaxAmount().doubleValue()).sum();						 
						totaltax=totaltax_N+totaltax_Y;  


					}
					/* t.setPremiumIncludedTax(t.getPremiumExcluedTax().add(new BigDecimal(totaltax,round)));				 
					 t.setPremiumIncludedTaxLC(t.getPremiumIncludedTax().multiply(t.getExchangeRate()).round(round));
					 */
					BigDecimal totalWithTax=(BigDecimal) decimalFormat.parse(decimalFormat.format(endorsement.getPremiumExcluedTax().abs().add(new BigDecimal(totaltax))));					 
					BigDecimal totalWithTaxLC=(BigDecimal) decimalFormat.parse(decimalFormat.format(totalWithTax.abs().multiply(t.getExchangeRate())));


					endorsement.setPremiumIncludedTax(totalWithTax);
					endorsement.setPremiumIncludedTaxLC(totalWithTaxLC);
					if(("Y".equals(engine.getCoverModification()) && ("Y".equals(t.getEndtProRataYn()) || "D".equals(t.getEndtProRataYn())) && "Y".equals(t.getUserOpt()) /*&&  !isPolicyPeriod*/ && !dontGo)
							||
							("N".equals(engine.getCoverModification()) && ("Y".equals(t.getEndtProRataYn()) || "D".equals(t.getEndtProRataYn())) && "Y".equals(t.getUserOpt())
									&& dontGo )	
							) {

						endorsement.setPremiumIncludedTax(totalWithTax.multiply(new BigDecimal("-1")));
						endorsement.setPremiumIncludedTaxLC(totalWithTaxLC.multiply(new BigDecimal("-1")));
						endorsement.setPremiumExcluedTax( endorsement.getPremiumExcluedTax().multiply(new BigDecimal("-1")));	
						endorsement.setPremiumExcluedTaxLC( endorsement.getPremiumExcluedTaxLC().multiply(new BigDecimal("-1"))); 
						

						// endorsement.setPremiumIncludedTax(BigDecimal.ZERO);
						// endorsement.setPremiumIncludedTaxLC(BigDecimal.ZERO);
						t.setDiffPremiumIncludedTax(totalWithTax.multiply(new BigDecimal("-1")));
						t.setDiffPremiumIncludedTaxLC(totalWithTaxLC.multiply(new BigDecimal("-1")));
					}
					

				} 


			}



		} catch (Exception e) {
			System.out.println("CoverID:"+t.getCoverId()+"<Desc>:"+t.getCoverDesc()+",subcoverId:"+t.getSubCoverId());
			e.printStackTrace();
			CoverException build = CoverException.builder().isError(true).message(e.getMessage() +" "+"CoverID:"+t.getCoverId()+"<Desc>:"+t.getCoverDesc()+",subcoverId:"+t.getSubCoverId()).build();

			t.setError(build);
		}

	}
	
	 
	
	

}

