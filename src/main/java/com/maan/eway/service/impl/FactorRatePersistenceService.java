package com.maan.eway.service.impl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.jsoup.internal.StringUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.CurrencyMaster;
import com.maan.eway.bean.EserviceSectionDetails;
import com.maan.eway.bean.EserviceTravelDetails;
import com.maan.eway.bean.EserviceTravelGroupDetails;
import com.maan.eway.bean.FactorRateRequestDetails;
import com.maan.eway.bean.MasterReferralDetails;
import com.maan.eway.bean.SectionCoverMaster;
import com.maan.eway.common.req.EserviceMotorDetailsSaveRes;
import com.maan.eway.repository.EServiceSectionDetailsRepository;
import com.maan.eway.repository.EserviceTravelDetailsRepository;
import com.maan.eway.repository.EserviceTravelGroupDetailsRepository;
import com.maan.eway.repository.FactorRateRequestDetailsRepository;
import com.maan.eway.repository.MasterReferralDetailsRepository;
import com.maan.eway.res.SuccessRes;
import com.maan.eway.res.calc.Cover;
import com.maan.eway.res.calc.Discount;
import com.maan.eway.res.calc.Endorsement;
import com.maan.eway.res.calc.Loading;
import com.maan.eway.res.calc.Tax;
import com.maan.eway.res.referal.MasterReferal;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

@Service
public class FactorRatePersistenceService {

@Autowired
private FactorRateRequestDetailsRepository repository;

@Autowired
private EserviceTravelDetailsRepository eserTraRepo;

@Autowired
private EserviceTravelGroupDetailsRepository eserGroupRepo;

@Autowired
private EServiceSectionDetailsRepository eserSecRepo; 

@Autowired
private MasterReferralDetailsRepository masReferralRepo;

@Value(value = "${travel.productId}")
private String travelProductId;


@PersistenceContext
private EntityManager em;

private Logger log=LogManager.getLogger(FactorRateRequestDetailsServiceImpl.class);
	
	@Transactional
	public SuccessRes saveFactorRateRequestDetails(EserviceMotorDetailsSaveRes req) {
		SuccessRes res = new SuccessRes();
		String successRes = "" ;
		try {

			// Delete Unopted Sections
			System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
			        + " <---- saveFactorRateRequestDetails Block  start :---->");
			List<EserviceSectionDetails> secList=new ArrayList<>();
			
			if(req.getProductId().equals("4")){
				secList = eserSecRepo
						.findByRequestReferenceNoAndProductIdAndLocationIdOrderBySectionIdAsc(
								req.getRequestReferenceNo(), req.getProductId(),
								1);
			}else {
					secList = eserSecRepo
					.findByRequestReferenceNoAndRiskIdAndProductIdAndLocationIdOrderBySectionIdAsc(
							req.getRequestReferenceNo(), Integer.valueOf(req.getVehicleId()), req.getProductId(),
							Integer.valueOf(req.getLocationId()));
			}
					
			List<Integer> optedSectionIds = new ArrayList<Integer>();
			secList.forEach(o -> {
 
				optedSectionIds.add(Integer.valueOf(o.getSectionId()));
			});
			
			Long notSecCount =0L;
				
				notSecCount = repository.countByRequestReferenceNoAndVehicleIdAndSectionIdNotInAndLocationId(
						req.getRequestReferenceNo(), Integer.valueOf(req.getVehicleId()), optedSectionIds,
						Integer.valueOf(req.getLocationId()));
			
			if (req.getCoverId()!=null && !"0".equals(req.getCoverId()) ) {
				
				
				
			if (notSecCount > 0) {
				System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
				        + " <---- FactorRateRequestDetailsRepository delete Block   :---->");
					repository.deleteByRequestReferenceNoAndVehicleIdAndSectionIdNotInAndLocationIdAndCoverId(
							req.getRequestReferenceNo(), Integer.valueOf(req.getVehicleId()), optedSectionIds,
							Integer.valueOf(req.getLocationId()),
							Integer.valueOf(req.getCoverId()));
				}
			} else {
				
				if (notSecCount > 0) {
					System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
					        + " <---- FactorRateRequestDetailsRepository delete Block   :---->");
					repository.deleteByRequestReferenceNoAndVehicleIdAndSectionIdNotInAndLocationId(
							req.getRequestReferenceNo(), Integer.valueOf(req.getVehicleId()), optedSectionIds,
							Integer.valueOf(req.getLocationId()));
				}
			}
	
			// Find Datas
			req.setSectionId(StringUtils.isNotBlank(req.getSectionId()) ? req.getSectionId() : "0");
			
			Long factorCount=0L;
			
//			if(req.getSectionId().equals("99999") &&
//					req.getVehicleId().equals("99999") &&
//					req.getLocationId().equals("99999")) {
//				factorCount = repository
//						.countByRequestReferenceNoAndVehicleIdAndCompanyIdAndProductIdAndSectionIdAndLocationId(
//								req.getRequestReferenceNo(), Integer.valueOf(req.getVehicleId()), req.getInsuranceId(),
//								Integer.valueOf(req.getProductId()), Integer.valueOf(req.getSectionId()),
//								Integer.valueOf(req.getLocationId()));
//				
//			}else {
			System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
			        + " <---- FactorRateRequestDetailsRepository count Block   :---->");
			factorCount = repository
					.countByRequestReferenceNoAndVehicleIdAndCompanyIdAndProductIdAndSectionIdAndLocationId(
							req.getRequestReferenceNo(), Integer.valueOf(req.getVehicleId()), req.getInsuranceId(),
							Integer.valueOf(req.getProductId()), Integer.valueOf(req.getSectionId()),
							Integer.valueOf(req.getLocationId()));
			/*
			 * List<Integer> coverIdList = req.getCoverList().stream() .map(a ->
			 * a.getCoverId().isBlank() ? 0 : Integer.valueOf(a.getCoverId()))
			 * .collect(Collectors.toList());
			 */
			List<FactorRateRequestDetails> coverIds = null;
			
			// Delete Old Datas
			if (factorCount > 0) {
				System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
				        + " <---- FactorRateRequestDetailsRepository count 1 Block   :---->");
				coverIds = repository
						.findByRequestReferenceNoAndVehicleIdAndCompanyIdAndProductIdAndSectionIdAndLocationIdOrderByCoverIdAsc(
								req.getRequestReferenceNo(), Integer.valueOf(req.getVehicleId()), req.getInsuranceId(),
								Integer.valueOf(req.getProductId()), Integer.valueOf(req.getSectionId()),
								Integer.valueOf(req.getLocationId()));
				if(req.getProductId().equals("5") || req.getProductId().equals("46"))
				{
					System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
					        + " <---- FactorRateRequestDetailsRepository delete 2 Block   :---->");
					repository.deleteByRequestReferenceNoAndVehicleIdAndCompanyIdAndProductIdAndSectionIdAndLocationId(req.getRequestReferenceNo(),Integer.valueOf(req.getVehicleId())
							, req.getInsuranceId() ,Integer.valueOf(req.getProductId()) ,Integer.valueOf(req.getSectionId()),Integer.valueOf(req.getLocationId()) );
				
				}
				else if (!"0".equals(req.getCoverId())) {
					
//					if(req.getSectionId().equals("99999") &&
//							req.getVehicleId().equals("99999") &&
//							req.getLocationId().equals("99999")) {
//						
//						log.info("Deleting 99999 entry");
//						
//						repository.deleteCovers(req.getRequestReferenceNo(),Integer.valueOf(req.getVehicleId())
//								, req.getInsuranceId() ,Integer.valueOf(req.getProductId()) ,Integer.valueOf(req.getSectionId()),Integer.valueOf(req.getLocationId()) );
//						em.flush();
//						em.clear();
//
//					}else {
					System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
					        + " <---- FactorRateRequestDetailsRepository delete 3 Block   :---->");
					repository.deleteByRequestReferenceNoAndVehicleIdAndCompanyIdAndProductIdAndSectionIdAndLocationIdAndCoverId(req.getRequestReferenceNo(),Integer.valueOf(req.getVehicleId())
							, req.getInsuranceId() ,Integer.valueOf(req.getProductId()) ,Integer.valueOf(req.getSectionId()),Integer.valueOf(req.getLocationId()),Integer.parseInt(req.getCoverId()) );
				}else {
					System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
					        + " <---- FactorRateRequestDetailsRepository delete 4 Block   :---->");
					repository.deleteByRequestReferenceNoAndVehicleIdAndCompanyIdAndProductIdAndSectionIdAndLocationId(req.getRequestReferenceNo(),Integer.valueOf(req.getVehicleId())
							, req.getInsuranceId() ,Integer.valueOf(req.getProductId()) ,Integer.valueOf(req.getSectionId()),Integer.valueOf(req.getLocationId()) );
				}
			}
			
			if (req.getProductId().equals("4")) {
				System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
				        + " <---- FactorRateRequestDetailsRepository delete 5 Block   :---->");
				repository
						.deleteByRequestReferenceNoAndCompanyIdAndProductIdAndSectionIdAndVdRefnoNotInAndCdRefnoNotInAndMsRefnoNotIn(
								req.getRequestReferenceNo(), req.getInsuranceId(),
								Integer.valueOf(req.getProductId()), Integer.valueOf(req.getSectionId()),
								Arrays.asList(req.getVdRefNo()), Arrays.asList(req.getCdRefNo()),
								Arrays.asList(req.getMsrefno()));
			}
			successRes = saveNewFactorRateRequestDetails(req, null);
 
			// Save New Details
			
			res.setResponse(successRes);
			res.setSuccessId(req.getRequestReferenceNo());
			System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
			        + " <---- saveFactorRateRequestDetails end   start :---->");
		} catch(Exception e){
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
			
		}return res;
	}
 

public String saveNewFactorRateRequestDetails(EserviceMotorDetailsSaveRes req, List<FactorRateRequestDetails> coverIds) {
	String successRes = "Saved Successfully" ;
	DozerBeanMapper dozerMapper = new DozerBeanMapper(); 
	System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
	        + " <---- saveNewFactorRateRequestDetails  Block  start  :---->");
	List<EserviceSectionDetails> sectionList =  eserSecRepo.findByRequestReferenceNoOrderByRiskIdAsc(req.getRequestReferenceNo() );
	String currencyId =sectionList.size()> 0 ? sectionList.get(0).getCurrencyId() : "" ;	
	String decimalDigits = currencyDecimalFormat(req.getInsuranceId() , currencyId ).toString();
	String stringFormat = "%0"+decimalDigits+"d" ;
	String decimalLength = decimalDigits.equals("0") ?"" : String.format(stringFormat ,0L)  ;
	String pattern = StringUtils.isBlank(decimalLength) ?  "#####0" :   "#####0." + decimalLength;
	DecimalFormat df = new DecimalFormat(pattern);
	
	try {
		Double premiumLc = 0D;
		Double premiumFc = 0D;
		Double overAllPremiumLc = 0D;
		Double overAllPremiumFc = 0D;
		List<FactorRateRequestDetails> saveCoverList = new ArrayList<FactorRateRequestDetails>(); 
		for ( Cover coverData  : req.getCoverList()  ) {
			
			System.out.println("cover id : "+coverData.getCoverId());
			if( coverData.getIsSubCover().equalsIgnoreCase("N") ) {
				// Save Cover Details
				FactorRateRequestDetails saveCover = new FactorRateRequestDetails(); 
				 
				dozerMapper.map(coverData, saveCover);
				saveCover.setRequestReferenceNo(req.getRequestReferenceNo());
				saveCover.setSubCoverId(0);
				//saveCover.setCoverId(Integer.valueOf(coverData.getCoverId()));
				saveCover.setCurrency(coverData.getCurrency());
				saveCover.setLocationId(Integer.valueOf(req.getLocationId()));
				saveCover.setExchangeRate(coverData.getExchangeRate()==null?null : coverData.getExchangeRate());
				saveCover.setCompanyId(req.getInsuranceId());
				saveCover.setProductId(Integer.valueOf(req.getProductId()));
				saveCover.setSectionId(Integer.valueOf(req.getSectionId()));
				saveCover.setSubCoverYn( coverData.getIsSubCover());
				saveCover.setCdRefno(req.getCdRefNo());
				saveCover.setVdRefno(req.getVdRefNo());
				saveCover.setMsRefno(req.getMsrefno());	
				saveCover.setDiscLoadId(0);
				saveCover.setTaxId(0);
				saveCover.setEntryDate(new Date());			
				saveCover.setCreatedBy(req.getCreatedBy());
				saveCover.setStatus(coverData.getStatus() );
				saveCover.setVehicleId(Integer.valueOf(req.getVehicleId()));	
				saveCover.setDependentCoverYn(coverData.getDependentCoveryn());
				saveCover.setDependentCoverId(StringUtils.isBlank(coverData.getDependentCoverId())?null :coverData.getDependentCoverId());
				saveCover.setIsSelected(coverData.getIsselected());
				
				saveCover.setPremiumAfterDiscountFc(coverData.getPremiumAfterDiscount()==null ? null : new BigDecimal(df.format( coverData.getPremiumAfterDiscount())));
				saveCover.setPremiumBeforeDiscountFc(coverData.getPremiumBeforeDiscount()==null ? null : new BigDecimal(df.format(coverData.getPremiumBeforeDiscount())));
				saveCover.setPremiumExcludedTaxFc(coverData.getPremiumExcluedTax()==null ? null : new BigDecimal(df.format(coverData.getPremiumExcluedTax())));
				saveCover.setPremiumIncludedTaxFc(coverData.getPremiumIncludedTax()==null ? null : new BigDecimal(df.format(coverData.getPremiumIncludedTax())));
				saveCover.setPremiumAfterDiscountLc(coverData.getPremiumAfterDiscountLC()==null ? null : new BigDecimal(df.format(coverData.getPremiumAfterDiscountLC())));
				saveCover.setPremiumBeforeDiscountLc(coverData.getPremiumBeforeDiscountLC()==null ? null : new BigDecimal(df.format(coverData.getPremiumBeforeDiscountLC())));
				saveCover.setPremiumExcludedTaxLc(coverData.getPremiumExcluedTaxLC()==null ? null : new BigDecimal(df.format(coverData.getPremiumExcluedTaxLC())));
				saveCover.setPremiumIncludedTaxLc(coverData.getPremiumIncludedTaxLC()==null ? null : new BigDecimal(df.format(coverData.getPremiumIncludedTaxLC())));
					saveCover.setIsReferral(StringUtils.isBlank(coverData.getIsReferral())?"N":coverData.getIsReferral());
				saveCover.setReferralDescription(StringUtils.isBlank(coverData.getReferalDescription())?"":coverData.getReferalDescription());
				saveCover.setMultiSelectYn(coverData.getMultiSelectYn()==null?"N": coverData.getMultiSelectYn());
				saveCover.setExcessAmount(coverData.getExcessAmount()==null ? null : coverData.getExcessAmount());
				saveCover.setExcessDesc(coverData.getExcessDesc()==null ? null : coverData.getExcessDesc());
				saveCover.setExcessPercent(coverData.getExcessPercent()==null ? null : coverData.getExcessPercent());
				saveCover.setProRataYn(coverData.getProRataYn()==null ? "N" : coverData.getProRataYn());
				saveCover.setDependentCoverSIorPRE(StringUtil.isBlank(coverData.getDependentCoveSIorPI())?"N":coverData.getDependentCoveSIorPI());
				saveCover.setEndtProRataDesc(StringUtil.isBlank(coverData.getEndtProRataDesc())?null:coverData.getEndtProRataDesc());
				saveCover.setEndtProRataYn(StringUtil.isBlank(coverData.getEndtProRataYn())?null:coverData.getEndtProRataYn());
				//String userOpt=(!"D".equals(saveCover.getIsSelected()) )?(StringUtils.isBlank(coverData.getUserOpt())?"N":coverData.getUserOpt()):(StringUtils.isBlank(coverData.getUserOpt())?"N":coverData.getUserOpt());
				String userOpt=coverData.getUserOpt();
				saveCover.setRegulatoryCode(coverData.getRegulatoryCode());
				saveCover.setMinimumPremiumYn(StringUtils.isBlank(coverData.getMinimumPremiumYn())?"N":coverData.getMinimumPremiumYn());
			/*	if(coverIds!=null && !coverIds.isEmpty()) {
					long count = coverIds.stream().filter(t-> (saveCover.getCoverId().equals(t.getCoverId()) && saveCover.getSubCoverId().equals(t.getSubCoverId()) )).count() ;
					if(count>0) userOpt="Y";
				}*/
				
				if(req.getUpdateas()==null) {
					saveCover.setUserOpt(userOpt);
					saveCover.setActualRate(new BigDecimal(coverData.getRate()));
				}else {
					saveCover.setActualRate(coverData.getTiraRate()==null?BigDecimal.ZERO:new BigDecimal(coverData.getTiraRate()));
					saveCover.setUserOpt( userOpt);
				}
					
					
				saveCover.setCoverBasedOn(StringUtils.isBlank(coverData.getCoverBasedOn())?"sumInsured":coverData.getCoverBasedOn());
		//      Double b=coverData.getPremiumBeforeDiscountLC()==null ? 0D : Double.valueOf(df.format(coverData.getPremiumBeforeDiscountLC()));
		//		saveCover.setSumInsured(coverData.getSumInsured()==null?BigDecimal.ZERO :coverData.getSumInsured());
				saveCover.setRegulSumInsured(coverData.getTiraSumInsured()==null?null:new BigDecimal(df.format(coverData.getTiraSumInsured())));
				saveCover.setEndtCount(coverData.getEndtCount()==null?BigDecimal.ZERO:coverData.getEndtCount());
				saveCover.setCoverPeriodFrom(coverData.getEffectiveDate());
				saveCover.setCoverPeriodTo(coverData.getPolicyEndDate());
				saveCover.setProRataPercent(coverData.getProRata()!=null ? coverData.getProRata().multiply(new BigDecimal("100")) : new BigDecimal("100"));
				saveCover.setRegulatorySuminsured(coverData.getTiraSumInsured()==null?BigDecimal.ZERO:coverData.getTiraSumInsured());
				saveCover.setRegulatoryRate(coverData.getTiraRate()==null?BigDecimal.ZERO:new BigDecimal(coverData.getTiraRate()));
				saveCover.setCoverageLimit(coverData.getCoverageLimit()==null?BigDecimal.ZERO:coverData.getCoverageLimit());
				saveCover.setMinCoverageLimit(coverData.getMinSumInsured()==null?BigDecimal.ZERO:coverData.getMinSumInsured());
				saveCover.setIsTaxExtempted(StringUtil.isBlank(coverData.getIsTaxExcempted())?"N":coverData.getIsTaxExcempted());
				
				saveCover.setCoverNameLocal(StringUtil.isBlank(coverData.getCoverNameLocal())?"":coverData.getCoverNameLocal());
				saveCover.setCoverDescLocal(StringUtil.isBlank(coverData.getSubCoverDescLocal())?"":coverData.getSubCoverDescLocal());
				saveCover.setSubCoverDescLocal(StringUtil.isBlank(coverData.getCoverNameLocal())?"":coverData.getCoverNameLocal());
				saveCover.setSubCoverNameLocal(StringUtil.isBlank(coverData.getSubCoverDescLocal())?"":coverData.getSubCoverDescLocal());
				saveCover.setMinimumRate(coverData.getMinrate()==null?BigDecimal.ZERO:new BigDecimal(coverData.getMinrate()));
				saveCover.setMinimumRateYn(coverData.getMinimumRateYn()==null?"":coverData.getMinimumRateYn());	
				saveCover.setMaximumRate(coverData.getMaxrate()==null?BigDecimal.ZERO:new BigDecimal(coverData.getMaxrate()));
				saveCover.setMaximumRateYn(coverData.getMaximumRateYn()==null?"":coverData.getMaximumRateYn());

				//private BigDecimal     minCoverageLimit;
				// Date Differents
				Date periodStart =  coverData.getEffectiveDate();
				Date periodEnd = coverData.getPolicyEndDate() ;
				String diff = "0";
				BigDecimal NoOfDays = new BigDecimal(0);
				
				if(periodStart!=null && periodEnd!=null && !"D".equals(coverData.getProRataYn())) {
					Long diffInMillies = Math.abs(periodEnd.getTime() - periodStart.getTime());
					Long daysBetween =  TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS)  + 1 ;
					
					// Check Leap Year
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
					boolean leapYear = LocalDate.parse(sdf.format(periodEnd) ).isLeapYear();
					diff = String.valueOf( daysBetween==365 &&  leapYear==true ? daysBetween+1 : daysBetween );
					System.out.println( "Calc Cover :  "+ coverData.getCoverDesc() + " Difference in days: " + diff);
					NoOfDays = new BigDecimal(diff);
					
				}else if("D".equals(coverData.getProRataYn())){
					NoOfDays=coverData.getPolicyPeriod();
				}
				saveCover.setNoOfDays(NoOfDays);
				
//				if(coverData.getTaxes()!=null && coverData.getTaxes().size() > 0 ) {
//					saveCover.setTax1(coverData.getTaxes().get(0).getTaxAmount()==null ? null : Double.valueOf(df.format(coverData.getTaxes().get(0).getTaxAmount())) );
//					if(coverData.getTaxes().size() > 1  ) 
//					saveCover.setTax2( coverData.getTaxes().get(1).getTaxAmount()==null ? null : Double.valueOf(df.format(coverData.getTaxes().get(1).getTaxAmount())) );
//					if(coverData.getTaxes().size() > 2  ) 
//					saveCover.setTax3(coverData.getTaxes().get(2).getTaxAmount()==null ? null : Double.valueOf(df.format(coverData.getTaxes().get(2).getTaxAmount())) );
//					
//				}
				saveCover.setDiscountCoverId(0) ;
				saveCover.setDiffPremiumIncludedTaxFc(coverData.getDiffPremiumIncludedTax()==null?BigDecimal.ZERO:coverData.getDiffPremiumIncludedTax());
				saveCover.setDiffPremiumIncludedTaxLc(coverData.getDiffPremiumIncludedTaxLC()==null?BigDecimal.ZERO:coverData.getDiffPremiumIncludedTaxLC());
				saveCover.setFreeCoverLimit(coverData.getFreeCoverLimit()==null?BigDecimal.ZERO:coverData.getFreeCoverLimit());
				//saveCover.setEndtCount(BigDecimal.ZERO );
				saveCoverList.add(saveCover);
				premiumLc = premiumLc + (saveCover.getPremiumExcludedTaxLc()==null ? 0D :Double.valueOf(saveCover.getPremiumExcludedTaxLc().toString()) );
				premiumFc = premiumFc + (saveCover.getPremiumExcludedTaxFc()==null ? 0D :Double.valueOf(saveCover.getPremiumExcludedTaxFc().toString()) );
				overAllPremiumLc = overAllPremiumLc + (saveCover.getPremiumIncludedTaxLc()==null ? 0D :Double.valueOf(saveCover.getPremiumIncludedTaxLc().toString() ));
				overAllPremiumFc = overAllPremiumFc + (saveCover.getPremiumIncludedTaxFc()==null ? 0D :Double.valueOf(saveCover.getPremiumIncludedTaxFc().toString()));
				
				Map<String,Object>  primaryKeys = new HashMap<String,Object>();
				primaryKeys.put("RefNo" , req.getRequestReferenceNo());
				primaryKeys.put("CreatedBy" , req.getCreatedBy() );
				primaryKeys.put("InsuranceId" ,req.getInsuranceId());
				primaryKeys.put("ProductId" ,req.getProductId());
				primaryKeys.put("SectionId" ,req.getSectionId());
				primaryKeys.put("SubCoverYn" ,coverData.getIsSubCover());
				primaryKeys.put("VehId" ,req.getVehicleId());
				primaryKeys.put("CdRefNo" ,req.getCdRefNo());
				primaryKeys.put("VdRefNo" ,req.getVdRefNo());
				primaryKeys.put("MsRefNo" ,req.getMsrefno());	
				primaryKeys.put("LocationId" ,req.getLocationId());
				
				
				// Save Discount Or Promo Cover
				if( coverData.getDiscounts()!=null && coverData.getDiscounts().size() > 0 ) {
					System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
					        + " <---- saveDiscountOrPromoRates save  Block  start  :---->");
					successRes  = 	saveDiscountOrPromoRates( primaryKeys ,coverData ,   coverData.getDiscounts() , df , diff) ;	
					System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
					        + " <---- saveDiscountOrPromoRates save  Block  end  :---->");
				}
				// Tax
				if(coverData.getTaxes()!=null && coverData.getTaxes().size() > 0 ) {
					System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
					        + " <---- saveTaxes save  Block  start  :---->");
					successRes  =  saveTaxes(primaryKeys ,coverData ,   coverData.getTaxes() ,df , diff) ;
					System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
					        + " <---- saveTaxes save  Block  end  :---->");
				}
				
				// Loginds
				if(coverData.getLoadings()!=null && coverData.getLoadings().size() > 0 ) {
					System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
					        + " <---- Save Loding   Block  start  :---->");
					successRes  =  saveLoadings(primaryKeys ,coverData ,   coverData.getLoadings() ,df , diff ) ;
					System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
					        + " <---- Save Loding   Block  end  :---->");
				}
				
				//Endt
				if(coverData.getEndorsements()!=null && coverData.getEndorsements().size() > 0 ) {
					System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
					        + " <---- Save Endorsements   Block  start  :---->");
					successRes  =  saveEndorsements(primaryKeys ,coverData ,   coverData.getEndorsements() ,df  , diff) ;
					System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
					        + " <---- Save Endorsements   Block  end  :---->");
				}
				
				
				
			} else {
				// Save SubCover Details
				System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
				        + " <---- Save cover loop in FactorRateRequestDetailsRepository   Block  start  :---->");
				for ( Cover subCoverData : coverData.getSubcovers() ) {
					// Save Cover Details
					FactorRateRequestDetails saveSubCover = new FactorRateRequestDetails(); 
					
					dozerMapper.map(subCoverData, saveSubCover);
					saveSubCover.setLocationId(Integer.valueOf(req.getLocationId()));
					saveSubCover.setRequestReferenceNo(req.getRequestReferenceNo());
					saveSubCover.setCompanyId(req.getInsuranceId());
					saveSubCover.setProductId(Integer.valueOf(req.getProductId()));
					saveSubCover.setSectionId(Integer.valueOf(req.getSectionId()));
					saveSubCover.setSubCoverYn( coverData.getIsSubCover());
					saveSubCover.setCurrency(subCoverData.getCurrency());
					saveSubCover.setExchangeRate(subCoverData.getExchangeRate()==null?null : subCoverData.getExchangeRate());
					saveSubCover.setCdRefno(req.getCdRefNo());
					saveSubCover.setVdRefno(req.getVdRefNo());
					saveSubCover.setMsRefno(req.getMsrefno());	
					saveSubCover.setDiscLoadId(0);
					saveSubCover.setTaxId(0);
					saveSubCover.setEntryDate(new Date());			
					saveSubCover.setCreatedBy(req.getCreatedBy());
					saveSubCover.setStatus(saveSubCover.getStatus());
					saveSubCover.setVehicleId(Integer.valueOf(req.getVehicleId()));		
					saveSubCover.setDependentCoverYn(subCoverData.getDependentCoveryn());
					saveSubCover.setDependentCoverId(StringUtils.isBlank(subCoverData.getDependentCoverId())?null :subCoverData.getDependentCoverId());	
					saveSubCover.setIsSelected(subCoverData.getIsselected());
					saveSubCover.setPremiumAfterDiscountFc(subCoverData.getPremiumAfterDiscount()==null ? null : new BigDecimal(df.format(subCoverData.getPremiumAfterDiscount())));
					saveSubCover.setPremiumBeforeDiscountFc(subCoverData.getPremiumBeforeDiscount()==null ? null : new BigDecimal(df.format(subCoverData.getPremiumBeforeDiscount())));
					saveSubCover.setPremiumExcludedTaxFc(subCoverData.getPremiumExcluedTax()==null ? null : new BigDecimal(df.format(subCoverData.getPremiumExcluedTax())));
					saveSubCover.setPremiumIncludedTaxFc(subCoverData.getPremiumIncludedTax()==null ? null : new BigDecimal(df.format(subCoverData.getPremiumIncludedTax())));
					saveSubCover.setPremiumAfterDiscountLc(subCoverData.getPremiumAfterDiscountLC()==null ? null : new BigDecimal(df.format(subCoverData.getPremiumAfterDiscountLC())));
					saveSubCover.setPremiumBeforeDiscountLc(subCoverData.getPremiumBeforeDiscountLC()==null ? null : new BigDecimal(df.format(subCoverData.getPremiumBeforeDiscountLC())));
					saveSubCover.setPremiumExcludedTaxLc(subCoverData.getPremiumExcluedTaxLC()==null ? null : new BigDecimal(df.format(subCoverData.getPremiumExcluedTaxLC())));
					saveSubCover.setPremiumIncludedTaxLc(subCoverData.getPremiumIncludedTaxLC()==null ? null : new BigDecimal(df.format(subCoverData.getPremiumIncludedTaxLC())));
					saveSubCover.setIsReferral(StringUtils.isBlank(subCoverData.getIsReferral())?"N":subCoverData.getIsReferral());
					saveSubCover.setReferralDescription(StringUtils.isBlank(coverData.getReferalDescription())?"":coverData.getReferalDescription());
					saveSubCover.setRegulSumInsured(subCoverData.getTiraSumInsured()==null?null:new BigDecimal(df.format(subCoverData.getTiraSumInsured())));
					saveSubCover.setExcessAmount(subCoverData.getExcessAmount()==null ? null : subCoverData.getExcessAmount());
					saveSubCover.setExcessDesc(subCoverData.getExcessDesc()==null ? null : subCoverData.getExcessDesc());
					saveSubCover.setExcessPercent(subCoverData.getExcessPercent()==null ? null : subCoverData.getExcessPercent());
					saveSubCover.setProRataYn(subCoverData.getProRataYn()==null ? "N" : subCoverData.getProRataYn());
					saveSubCover.setIsSelected(subCoverData.getIsselected());
					saveSubCover.setCoverageType(subCoverData.getCoverageType());
					saveSubCover.setCoverageLimit(saveSubCover.getCoverageLimit()==null?BigDecimal.ZERO:saveSubCover.getCoverageLimit());
					saveSubCover.setMinCoverageLimit(subCoverData.getMinSumInsured()==null?BigDecimal.ZERO:subCoverData.getMinSumInsured());
					
					saveSubCover.setCoverNameLocal(StringUtil.isBlank(coverData.getCoverNameLocal())?"":coverData.getCoverNameLocal());
					saveSubCover.setCoverDescLocal(StringUtil.isBlank(coverData.getSubCoverDescLocal())?"":coverData.getSubCoverDescLocal());
					saveSubCover.setSubCoverDescLocal(StringUtil.isBlank(subCoverData.getCoverNameLocal())?"":subCoverData.getCoverNameLocal());
					saveSubCover.setSubCoverNameLocal(StringUtil.isBlank(subCoverData.getSubCoverDescLocal())?"":subCoverData.getSubCoverDescLocal());
//					if(subCoverData.getTaxes()!=null && subCoverData.getTaxes().size() > 0 ) {
//						saveSubCover.setTax1(subCoverData.getTaxes().get(0).getTaxAmount()==null ? null : Double.valueOf(df.format(subCoverData.getTaxes().get(0).getTaxAmount())) );
//						if(coverData.getTaxes().size() > 1  ) 
//							saveSubCover.setTax2( subCoverData.getTaxes().get(1).getTaxAmount()==null ? null : Double.valueOf(df.format(subCoverData.getTaxes().get(1).getTaxAmount())) );
//						if(coverData.getTaxes().size() > 2  ) 
//							saveSubCover.setTax3(subCoverData.getTaxes().get(2).getTaxAmount()==null ? null : Double.valueOf(df.format(subCoverData.getTaxes().get(2).getTaxAmount())) );
//					}
					//String userOpt=(!"D".equals(saveSubCover.getIsSelected()))?"N":(StringUtils.isBlank(coverData.getUserOpt())?"N":coverData.getUserOpt());						saveSubCover.setRegulatoryCode(subCoverData.getRegulatoryCode());
					String userOpt=(!"D".equals(saveSubCover.getIsSelected()) )?(StringUtils.isBlank(saveSubCover.getUserOpt())?"N":saveSubCover.getUserOpt()):(StringUtils.isBlank(saveSubCover.getUserOpt())?"N":saveSubCover.getUserOpt());
					saveSubCover.setMinimumPremiumYn(StringUtils.isBlank(subCoverData.getMinimumPremiumYn())?"N":subCoverData.getMinimumPremiumYn());
					saveSubCover.setEndtCount(coverData.getEndtCount()==null?BigDecimal.ZERO:coverData.getEndtCount());
					saveSubCover.setFreeCoverLimit(coverData.getFreeCoverLimit()==null?BigDecimal.ZERO:coverData.getFreeCoverLimit());
					saveSubCover.setMinimumRate(coverData.getMinrate()==null?BigDecimal.ZERO:new BigDecimal(coverData.getMinrate()));
					saveSubCover.setMinimumRateYn(coverData.getMinimumRateYn()==null?"":coverData.getMinimumRateYn());
					saveSubCover.setMaximumRate(coverData.getMaxrate()==null?BigDecimal.ZERO:new BigDecimal(coverData.getMaxrate()));
					saveSubCover.setMaximumRateYn(coverData.getMaximumRateYn()==null?"":coverData.getMaximumRateYn());
					
					/*if(coverIds!=null && !coverIds.isEmpty()) {
						long count = coverIds.stream().filter(t-> (saveSubCover.getCoverId().equals(t.getCoverId()) && saveSubCover.getSubCoverId().equals(t.getSubCoverId()) )).count() ;
						if(count>0) userOpt="Y";
					}*/
					saveSubCover.setUserOpt(userOpt);
					if(req.getUpdateas()==null) {
						saveSubCover.setUserOpt(userOpt);
						saveSubCover.setActualRate(new BigDecimal(subCoverData.getRate()));
					}else {
						saveSubCover.setActualRate(new BigDecimal(subCoverData.getTiraRate()));
						saveSubCover.setUserOpt( userOpt);
					}
					///Double b=subCoverData.getPremiumBeforeDiscountLC()==null ? 0D : Double.valueOf(df.format(subCoverData.getPremiumBeforeDiscountLC()));
				//	saveSubCover.setSumInsured(subCoverData.getSumInsured()==null?BigDecimal.ZERO :subCoverData.getSumInsured());
					saveSubCover.setRegulSumInsured(subCoverData.getTiraSumInsured()==null?null:new BigDecimal(df.format(subCoverData.getTiraSumInsured())));

					saveSubCover.setCoverBasedOn(StringUtils.isBlank(coverData.getCoverBasedOn())?"sumInsured":coverData.getCoverBasedOn());
					saveSubCover.setCoverPeriodFrom(coverData.getEffectiveDate());
					saveSubCover.setCoverPeriodTo(coverData.getPolicyEndDate());
					saveSubCover.setProRataPercent(coverData.getProRata()!=null ? coverData.getProRata().multiply(new BigDecimal("100")) : new BigDecimal("100"));
					
					// Date Differents
					Date periodStart =  coverData.getEffectiveDate();
					Date periodEnd = coverData.getPolicyEndDate() ;
					String diff = "0";
					BigDecimal NoOfDays = new BigDecimal(0);
					
					if(periodStart!=null && periodEnd!=null && !"D".equals(coverData.getProRataYn())) {
						Long diffInMillies = Math.abs(periodEnd.getTime() - periodStart.getTime());
						Long daysBetween =  TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS)  + 1 ;
						
						// Check Leap Year
						SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd"); 
						boolean leapYear = LocalDate.parse(sdf.format(periodEnd) ).isLeapYear();
						diff = String.valueOf( daysBetween==365 &&  leapYear==true ? daysBetween+1 : daysBetween );
						System.out.println( "Calc Cover :  "+ coverData.getCoverDesc() + " Difference in days: " + diff);
						NoOfDays = new BigDecimal(diff);
						
					}else if("D".equals(coverData.getProRataYn())){
						NoOfDays=coverData.getPolicyPeriod();
					}
					saveSubCover.setNoOfDays(NoOfDays);
					
					premiumLc = premiumLc + (saveSubCover.getPremiumExcludedTaxLc()==null ? 0D :Double.valueOf(saveSubCover.getPremiumExcludedTaxLc().toString()));
					premiumFc = premiumFc + (saveSubCover.getPremiumExcludedTaxFc()==null ? 0D :Double.valueOf(saveSubCover.getPremiumExcludedTaxFc().toString()));
					overAllPremiumLc = overAllPremiumLc + (saveSubCover.getPremiumIncludedTaxLc()==null ? 0D :Double.valueOf(saveSubCover.getPremiumIncludedTaxLc().toString()));
					overAllPremiumFc = overAllPremiumFc + (saveSubCover.getPremiumIncludedTaxFc()==null ? 0D :Double.valueOf(saveSubCover.getPremiumIncludedTaxFc().toString()));
					
					saveSubCover.setDiscountCoverId(0) ;
					saveSubCover.setDiffPremiumIncludedTaxFc(coverData.getDiffPremiumIncludedTax()==null?BigDecimal.ZERO:coverData.getDiffPremiumIncludedTax());
					saveSubCover.setDiffPremiumIncludedTaxLc(coverData.getDiffPremiumIncludedTaxLC()==null?BigDecimal.ZERO:coverData.getDiffPremiumIncludedTaxLC());
					//saveSubCover.setEndtCount(BigDecimal.ZERO );
					saveCoverList.add(saveSubCover);
					// repository.save(saveSubCover);
					Map<String,Object>  primaryKeys = new HashMap<String,Object>();
					primaryKeys.put("RefNo" , req.getRequestReferenceNo());
					primaryKeys.put("CreatedBy" , req.getCreatedBy() );
					primaryKeys.put("InsuranceId" ,req.getInsuranceId());
					primaryKeys.put("ProductId" ,req.getProductId());
					primaryKeys.put("SectionId" ,req.getSectionId());
					primaryKeys.put("SubCoverYn" ,coverData.getIsSubCover());
					primaryKeys.put("VehId" ,req.getVehicleId());
					primaryKeys.put("CdRefNo" ,req.getCdRefNo());
					primaryKeys.put("VdRefNo" ,req.getVdRefNo());
					primaryKeys.put("MsRefNo" ,req.getMsrefno());	
					primaryKeys.put("LocationId" ,req.getLocationId());	
					// Save Discount Cover Or Promo Cover
					if( subCoverData.getDiscounts()!=null && subCoverData.getDiscounts().size() > 0 ) {
						System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
						        + " <---- Save saveDiscountOrPromoRates 2   Block  start  :---->");
						successRes  = 	saveDiscountOrPromoRates( primaryKeys ,subCoverData ,   subCoverData.getDiscounts() ,df , diff) ;
						System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
						        + " <---- Save saveDiscountOrPromoRates 2  Block  end  :---->");
					}
					
					 
					// Tax
					if(subCoverData.getTaxes()!=null && subCoverData.getTaxes().size() > 0 ) {
						System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
						        + " <---- Save Taxes    Block  2 start  :---->");
						successRes  =  saveTaxes(primaryKeys ,subCoverData ,   subCoverData.getTaxes() ,df , diff) ;
						System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
						        + " <---- Save Taxes    Block  2 nd  :---->");
					}
					
					// Loginds
					if(coverData.getLoadings()!=null && coverData.getLoadings().size() > 0 ) {
						System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
						        + " <---- Save Loading    Block  2 start  :---->");
						successRes  =  saveLoadings(primaryKeys ,subCoverData ,   subCoverData.getLoadings() ,df , diff) ;
						System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
						        + " <---- Save Loading    Block  2 end  :---->");
					}	
					//Endt
					if(coverData.getEndorsements()!=null && coverData.getEndorsements().size() > 0 ) {
						System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
						        + " <---- Save endorsement    Block  2 start  :---->");
						successRes  =  saveEndorsements(primaryKeys ,coverData ,   subCoverData.getEndorsements() ,df  , diff) ;
						System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
						        + " <---- Save endorsement    Block  2 end  :---->");
					}
					
					
				}
			}
			// Cover Save 
			repository.saveAll(saveCoverList);
			
			System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
			        + " <---- Save cover loop in FactorRateRequestDetailsRepository   Block  start  :---->");		
			
		}
		
		
		// Update Motor Premium
//		if(   req.getProductId().equalsIgnoreCase(motorProductId)) {
//			EserviceMotorDetails findData =   eserMotorRepo.findByRequestReferenceNoAndRiskId(req.getRequestReferenceNo()  ,Integer.valueOf(req.getVehicleId()));
//			findData.setActualPremiumLc(premiumLc ==null ? null :new BigDecimal(df.format(premiumLc )));
//			findData.setActualPremiumFc(premiumFc ==null ? null :new BigDecimal(df.format(premiumFc )));
//			findData.setOverallPremiumLc(overAllPremiumLc ==null ? null :new BigDecimal(df.format(overAllPremiumLc)));
//			findData.setOverallPremiumFc(overAllPremiumFc ==null ? null :new BigDecimal(df.format(overAllPremiumFc)));
//			
//			eserMotorRepo.save(findData);
//			
//		// Update  Travle PRemium
//		} else
		CompanyProductMaster product =  getCompanyProductMasterDropdown(req.getInsuranceId() , req.getProductId().toString());

		if( product.getMotorYn().equalsIgnoreCase("H") && req.getProductId().equalsIgnoreCase(travelProductId)) {
			
			// Update Group Premium
			
			EserviceTravelGroupDetails findData =eserGroupRepo.findByRequestReferenceNoAndGroupId(req.getRequestReferenceNo() ,Integer.valueOf(req.getVehicleId()) ); 
			
			if(findData!=null) {
			findData.setActualPremiumLc(premiumLc ==null ? null :new BigDecimal(df.format(premiumLc )));
			findData.setActualPremiumFc(premiumFc ==null ? null :new BigDecimal(df.format(premiumFc )));
			findData.setOverallPremiumLc(overAllPremiumLc ==null ? null :new BigDecimal(df.format(overAllPremiumLc)));
			findData.setOverallPremiumFc(overAllPremiumFc ==null ? null :new BigDecimal(df.format(overAllPremiumFc)));
			eserGroupRepo.save(findData);
			}
			
			List<EserviceTravelGroupDetails> findAll = eserGroupRepo.findByRequestReferenceNoOrderByGroupIdAsc(req.getRequestReferenceNo() );
			
			
			// Update OverAll Premium
			premiumFc = findAll.stream().filter( o -> o.getActualPremiumFc()!=null && o.getActualPremiumFc().doubleValue() > 0D ).mapToDouble( o ->   o.getActualPremiumFc().doubleValue()  ).sum();
			premiumLc = findAll.stream().filter( o -> o.getActualPremiumLc()!=null && o.getActualPremiumLc().doubleValue() > 0D ).mapToDouble( o ->   o.getActualPremiumLc().doubleValue()  ).sum();
			overAllPremiumFc = findAll.stream().filter( o -> o.getOverallPremiumFc()!=null && o.getOverallPremiumFc().doubleValue() > 0D ).mapToDouble( o ->   o.getOverallPremiumFc().doubleValue()  ).sum();
			overAllPremiumLc = findAll.stream().filter( o -> o.getOverallPremiumLc()!=null && o.getOverallPremiumLc().doubleValue() > 0D ).mapToDouble( o ->   o.getOverallPremiumLc().doubleValue()  ).sum();
			
			//Update TRavel Premium
			EserviceTravelDetails traData = eserTraRepo.findByRequestReferenceNo(req.getRequestReferenceNo());
			traData.setActualPremiumLc(premiumLc ==null ? null :new BigDecimal(df.format(premiumLc )));
			traData.setActualPremiumFc(premiumFc ==null ? null :new BigDecimal(df.format(premiumFc )));
			traData.setOverallPremiumLc(overAllPremiumLc ==null ? null :new BigDecimal(df.format(overAllPremiumLc)));
			traData.setOverallPremiumFc(overAllPremiumFc ==null ? null :new BigDecimal(df.format(overAllPremiumFc)));
			eserTraRepo.save(traData);
			
		} else if( product.getMotorYn().equalsIgnoreCase("A") ) {
			
//			// Update Group Premium
//			EserviceBuildingDetails findData = eserBuildRepo.findByRequestReferenceNoAndRiskIdAndSectionId(req.getRequestReferenceNo() ,1 , req.getSectionId()); 
//			findData.setActualPremiumLc(premiumLc ==null ? null :new BigDecimal(df.format(premiumLc )));
//			findData.setActualPremiumFc(premiumFc ==null ? null :new BigDecimal(df.format(premiumFc )));
//			findData.setOverallPremiumLc(overAllPremiumLc ==null ? null :new BigDecimal(df.format(overAllPremiumLc)));
//			findData.setOverallPremiumFc(overAllPremiumFc ==null ? null :new BigDecimal(df.format(overAllPremiumFc)));
//			
//			eserBuildRepo.save(findData);
			
		} else {
			
			// Update Group Premium
//			EserviceCommonDetails findData =eserCommonRepo.findByRequestReferenceNoAndRiskIdAndSectionId(req.getRequestReferenceNo() ,Integer.valueOf(req.getVehicleId()) , req.getSectionId()); 
//			if(findData !=null) {
//				findData.setActualPremiumLc(premiumLc ==null ? null :new BigDecimal(df.format(premiumLc )));
//				findData.setActualPremiumFc(premiumFc ==null ? null :new BigDecimal(df.format(premiumFc )));
//				findData.setOverallPremiumLc(overAllPremiumLc ==null ? null :new BigDecimal(df.format(overAllPremiumLc)));
//				findData.setOverallPremiumFc(overAllPremiumFc ==null ? null :new BigDecimal(df.format(overAllPremiumFc)));
//				
//				eserCommonRepo.save(findData);
//			}
//		
			
			
		}
		
		
		
		// Save Master Referals
		if(req.getReferals()!=null && req.getReferals().size()>0 ) {
			
			Long refCount  = masReferralRepo.countByRequestReferenceNoAndRiskIdAndProductIdAndSectionIdAndCompanyId(req.getRequestReferenceNo() ,Integer.valueOf(req.getVehicleId()) ,
                      Integer.valueOf(req.getProductId()),Integer.valueOf(req.getSectionId()),req.getInsuranceId() );
			if(refCount!=null && refCount > 0 ) {
				masReferralRepo.deleteByRequestReferenceNoAndRiskIdAndProductIdAndSectionIdAndCompanyId(req.getRequestReferenceNo() ,Integer.valueOf(req.getVehicleId()) ,
			            Integer.valueOf(req.getProductId()),Integer.valueOf(req.getSectionId()),req.getInsuranceId() );
			}
			
			// Remove non selected Section Master Referal		
			List<EserviceSectionDetails> sectionDetails = eserSecRepo.findByRequestReferenceNoAndRiskIdAndProductIdOrderBySectionIdAsc(req.getRequestReferenceNo() ,Integer.valueOf(req.getVehicleId()) , req.getProductId());
			
			List<Integer> sectionIds = new ArrayList<Integer>();
			sectionDetails.forEach( o -> sectionIds.add(Integer.valueOf(o.getSectionId())) );
			
			refCount  = masReferralRepo.countByRequestReferenceNoAndRiskIdAndProductIdAndSectionIdNotIn(req.getRequestReferenceNo() ,Integer.valueOf(req.getVehicleId()) , Integer.valueOf(req.getProductId()),sectionIds);
			if(refCount!=null && refCount > 0 ) {
				masReferralRepo.deleteByRequestReferenceNoAndRiskIdAndProductIdAndSectionIdNotIn(req.getRequestReferenceNo() ,Integer.valueOf(req.getVehicleId()) , Integer.valueOf(req.getProductId()),sectionIds);
			}
			
			Integer row = 0 ;		
			for ( MasterReferal referal : req.getReferals() ){
				MasterReferralDetails saveRef = new MasterReferralDetails();
				
				if (referal.getIsreferral()==true /*&& ! referal.getReferralDesc().contains("Exception") */) {
					row = row + 1 ;
					saveRef.setRequestReferenceNo(req.getRequestReferenceNo());
					saveRef.setApiInfo(referal.getApiInfo());
					saveRef.setReferralDesc(referal.getReferralDesc());
					saveRef.setSNo(row);
					saveRef.setEntryDate(new Date());
					saveRef.setCompanyId(req.getInsuranceId());
					saveRef.setCreatedBy(req.getCreatedBy());
					saveRef.setRiskId(Integer.valueOf(req.getVehicleId()));
					saveRef.setSectionId(Integer.valueOf(req.getSectionId()));
					saveRef.setStatus("Y");
					saveRef.setProductId(Integer.valueOf(req.getProductId()));
					masReferralRepo.save(saveRef);
				}
				
			}
				
		}
		else if(req.getReferals()==null || req.getReferals().isEmpty()) {
			System.out.println(" Deleting Previous Records in MasterReferralDetails : RequestReferenceNo :"+req.getRequestReferenceNo());
			Long refCount  = masReferralRepo.countByRequestReferenceNoAndRiskIdAndProductIdAndCompanyId(req.getRequestReferenceNo() ,Integer.valueOf(req.getVehicleId()) ,
                      Integer.valueOf(req.getProductId()),req.getInsuranceId() );
			if(refCount!=null && refCount > 0 ) {
				masReferralRepo.deleteByRequestReferenceNoAndRiskIdAndProductIdAndCompanyId(req.getRequestReferenceNo() ,Integer.valueOf(req.getVehicleId()) ,
			            Integer.valueOf(req.getProductId()),req.getInsuranceId() );
			}
		}
		
		
	} catch(Exception e){
		e.printStackTrace();
		log.info("Log Details" + e.getMessage());
		return null;
		
	}
	System.out.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")) 
	        + " <---- saveNewFactorRateRequestDetails   Block  end  :---->");
	return successRes;
}

public  CompanyProductMaster getCompanyProductMasterDropdown(String companyId, String productId) {
	CompanyProductMaster product = new CompanyProductMaster();
	try {
		Date today = new Date();
		Calendar cal = new GregorianCalendar();
		cal.setTime(today);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		;
		cal.set(Calendar.MINUTE, 1);
		today = cal.getTime();
		cal.set(Calendar.HOUR_OF_DAY, 1);
		cal.set(Calendar.MINUTE, 1);
		Date todayEnd = cal.getTime();

		// Criteria
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CompanyProductMaster> query = cb.createQuery(CompanyProductMaster.class);
		List<CompanyProductMaster> list = new ArrayList<CompanyProductMaster>();
		// Find All
		Root<CompanyProductMaster> c = query.from(CompanyProductMaster.class);
		// Select
		query.select(c);
		// Order By
		List<Order> orderList = new ArrayList<Order>();
		orderList.add(cb.asc(c.get("productName")));

		// Effective Date Start Max Filter
		Subquery<Date> effectiveDate = query.subquery(Date.class);
		Root<CompanyProductMaster> ocpm1 = effectiveDate.from(CompanyProductMaster.class);
		effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
		Predicate a1 = cb.equal(c.get("productId"), ocpm1.get("productId"));
		Predicate a2 = cb.equal(c.get("companyId"), ocpm1.get("companyId"));
		Predicate a3 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
		effectiveDate.where(a1, a2, a3);
		// Effective Date End Max Filter
		Subquery<Date> effectiveDate2 = query.subquery(Date.class);
		Root<CompanyProductMaster> ocpm2 = effectiveDate2.from(CompanyProductMaster.class);
		effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
		Predicate a4 = cb.equal(c.get("productId"), ocpm2.get("productId"));
		Predicate a5 = cb.equal(c.get("companyId"), ocpm2.get("companyId"));
		Predicate a6 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
		effectiveDate2.where(a4, a5, a6);

		// Where
		Predicate n1 = cb.equal(c.get("status"), "Y");
		Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
		Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
		Predicate n4 = cb.equal(c.get("companyId"), companyId);
		Predicate n5 = cb.equal(c.get("productId"), productId);
		query.where(n1, n2, n3, n4, n5).orderBy(orderList);
		// Get Result
		TypedQuery<CompanyProductMaster> result = em.createQuery(query);
		list = result.getResultList();
		product = list.size() > 0 ? list.get(0) :null;
	} catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is --->" + e.getMessage());
		return null;
	}
	return product;
}

public Integer currencyDecimalFormat(String insuranceId  ,String currencyId ) {
	Integer decimalFormat = 0 ;
	try {
		Date today = new Date();
		Calendar cal = new GregorianCalendar();
		cal.setTime(today);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 1);
		today = cal.getTime();
		cal.set(Calendar.HOUR_OF_DAY, 1);
		cal.set(Calendar.MINUTE, 1);
		Date todayEnd = cal.getTime();
		
		// Criteria
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<CurrencyMaster> query = cb.createQuery(CurrencyMaster.class);
		List<CurrencyMaster> list = new ArrayList<CurrencyMaster>();
		
		// Find All
		Root<CurrencyMaster>    c = query.from(CurrencyMaster.class);		
		
		// Select
		query.select(c);
		
	
		// Order By
		List<Order> orderList = new ArrayList<Order>();
		orderList.add(cb.asc(c.get("currencyName")));
		
		// Effective Date Max Filter
		Subquery<Date> effectiveDate = query.subquery(Date.class);
		Root<CurrencyMaster> ocpm1 = effectiveDate.from(CurrencyMaster.class);
		effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
		Predicate a11 = cb.equal(c.get("currencyId"),ocpm1.get("currencyId") );
		Predicate a12 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
		Predicate a18 = cb.equal(c.get("status"),ocpm1.get("status") );
		Predicate a22 = cb.equal(c.get("companyId"), ocpm1.get("companyId"));
		
		effectiveDate.where(a11,a12,a18,a22);
		
		// Effective Date Max Filter
		Subquery<Date> effectiveDate2 = query.subquery(Date.class);
		Root<CurrencyMaster> ocpm2 = effectiveDate2.from(CurrencyMaster.class);
		effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
		Predicate a13 = cb.equal(c.get("currencyId"),ocpm2.get("currencyId") );
		Predicate a14 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
		Predicate a19 = cb.equal(c.get("status"),ocpm2.get("status") );
		Predicate a23 = cb.equal(c.get("companyId"), ocpm2.get("companyId"));
		
		effectiveDate2.where(a13,a14,a19,a23);
		
	    // Where	
		Predicate n1 = cb.equal(c.get("status"), "Y");
		Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
		Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
		Predicate n4 = cb.equal(c.get("companyId"),insuranceId);
		Predicate n5 = cb.equal(c.get("companyId"),"99999");
		Predicate n6 = cb.or(n4,n5);
		Predicate n7 = cb.equal(c.get("currencyId"),currencyId);
		query.where(n1,n2,n3,n6,n7).orderBy(orderList);
		
		// Get Result
		TypedQuery<CurrencyMaster> result = em.createQuery(query);			
		list =  result.getResultList(); 
		
		decimalFormat = list.size() > 0 ? (list.get(0).getDecimalDigit()==null?0 :list.get(0).getDecimalDigit()) :0; 		
		
	} catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is ---> " + e.getMessage());
		return null;
	}
	return decimalFormat;
}

public String saveTaxes(Map<String,Object>  primaryKeys ,  Cover coverReq , List<Tax> taxes , DecimalFormat df ,String diff ) {
	String res = "Saved Successfully";
	DozerBeanMapper dozerMapper = new DozerBeanMapper();
	try {
		List<FactorRateRequestDetails> saveTaxList = new ArrayList<FactorRateRequestDetails>();
		for (Tax tax :  taxes ) {
			FactorRateRequestDetails saveTax = new FactorRateRequestDetails();
			dozerMapper.map(coverReq, saveTax);
			saveTax.setRequestReferenceNo(primaryKeys.get("RefNo").toString() );
			saveTax.setCoverName(coverReq.getCoverName() +" "+tax.getTaxDesc() );
			saveTax.setCoverDesc(coverReq.getCoverDesc() +" "+tax.getTaxDesc());
			saveTax.setCurrency(coverReq.getCurrency());
			saveTax.setLocationId(Integer.valueOf(primaryKeys.get("LocationId").toString()));
			saveTax.setExchangeRate(coverReq.getExchangeRate()==null?null :coverReq.getExchangeRate());
			saveTax.setCompanyId(primaryKeys.get("InsuranceId").toString());
			saveTax.setProductId(Integer.valueOf(primaryKeys.get("ProductId").toString()));
			saveTax.setCoverageType("T");
			saveTax.setSectionId(Integer.valueOf(primaryKeys.get("SectionId").toString()));
			saveTax.setSubCoverYn( primaryKeys.get("SubCoverYn").toString());
			saveTax.setVehicleId(Integer.valueOf(primaryKeys.get("VehId").toString()));	
			saveTax.setCdRefno( primaryKeys.get("CdRefNo").toString());
			saveTax.setVdRefno( primaryKeys.get("VdRefNo").toString());
			saveTax.setMsRefno( primaryKeys.get("MsRefNo").toString());	
			saveTax.setEntryDate(new Date());			
			saveTax.setCreatedBy(primaryKeys.get("CreatedBy")==null?"":primaryKeys.get("CreatedBy").toString());
			saveTax.setSubCoverId(StringUtils.isBlank(coverReq.getSubCoverId()) ?0 : Integer.valueOf(coverReq.getSubCoverId()) );
			saveTax.setCoverId(Integer.valueOf(coverReq.getCoverId()));
			saveTax.setStatus(coverReq.getStatus());
			saveTax.setIsSelected(coverReq.getIsselected());
			saveTax.setDiscLoadId(StringUtils.isBlank(tax.getEndtTypeId())?0:Integer.parseInt(tax.getEndtTypeId()));
			saveTax.setTaxAmount(tax.getTaxAmount()==null?null :new BigDecimal(df.format(tax.getTaxAmount())));
			saveTax.setTaxCalcType(tax.getCalcType());
			saveTax.setTaxDesc(tax.getTaxDesc());
			saveTax.setTaxExemptType(tax.getTaxExemptType());
			saveTax.setTaxExemptCode(tax.getTaxExemptCode());
			saveTax.setTaxId(tax.getTaxId()==null?null : Integer.valueOf(tax.getTaxId()) );
			saveTax.setTaxRate(tax.getTaxRate()==null?null :new BigDecimal(tax.getTaxRate()) );
			saveTax.setIsTaxExtempted(tax.getIsTaxExempted());
			saveTax.setEndtCount(tax.getEndtTypeCount()==null?BigDecimal.ZERO:tax.getEndtTypeCount());
			saveTax.setDiscountCoverId(0);
			saveTax.setCoverPeriodFrom(coverReq.getEffectiveDate());
			saveTax.setCoverPeriodTo(coverReq.getPolicyEndDate());
			saveTax.setDependentCoverYn(StringUtils.isBlank(tax.getDependentYn())?"N":tax.getDependentYn());
			saveTax.setMinimumPremium(tax.getMinimumTaxAmountLc());
			saveTax.setMinimumPremiumFc(tax.getMinimumTaxAmount());
			saveTax.setMaximumTaxAmount(tax.getMaxTaxAmount());
			saveTax.setTaxAmountLc(tax.getTaxAmountLc()==null?null :new BigDecimal(df.format(tax.getTaxAmountLc())));
			saveTax.setDependentCoverId("0");
			saveTax.setNoOfDays(new BigDecimal(diff));
			
			//	repository.save(saveTax);
			
			saveTaxList.add(saveTax);
			
		}
		repository.saveAll(saveTaxList);
		res = "Success" ;
	} catch(Exception e){
		e.printStackTrace();
		log.info("Log Details" + e.getMessage());
		res = "Failed";
		return res;
		
	}return res;
}
public String saveEndorsements(Map<String,Object>  primaryKeys ,  Cover coverReq , List<Endorsement> endorsements , DecimalFormat df ,String diff) {
	String res = "Saved Successfully";
	DozerBeanMapper dozerMapper = new DozerBeanMapper();
	try {
		List<FactorRateRequestDetails> saveEndt = new ArrayList<FactorRateRequestDetails>();
		for (Endorsement lod :  endorsements ) {
			FactorRateRequestDetails saveLod = new FactorRateRequestDetails();
			dozerMapper.map(coverReq, saveLod);
			saveLod.setRequestReferenceNo(primaryKeys.get("RefNo").toString() );
			saveLod.setCoverName(lod.getCoverName());
			saveLod.setCoverDesc(lod.getEndorsementDesc());	
			saveLod.setLocationId(Integer.valueOf(primaryKeys.get("LocationId").toString()));
			saveLod.setCompanyId(primaryKeys.get("InsuranceId").toString());
			saveLod.setProductId(Integer.valueOf(primaryKeys.get("ProductId").toString()));
			saveLod.setSectionId(Integer.valueOf(primaryKeys.get("SectionId").toString()));
			saveLod.setSubCoverYn( primaryKeys.get("SubCoverYn").toString());
			saveLod.setVehicleId(Integer.valueOf(primaryKeys.get("VehId").toString()));	
			saveLod.setCdRefno( primaryKeys.get("CdRefNo").toString());
			saveLod.setVdRefno( primaryKeys.get("VdRefNo").toString());
			saveLod.setMsRefno( primaryKeys.get("MsRefNo").toString());	
			saveLod.setEntryDate(new Date());			
			Object createdBy = primaryKeys.get("CreatedBy");
			saveLod.setCreatedBy(createdBy != null ? createdBy.toString() : null);
			saveLod.setSubCoverId(StringUtils.isBlank(coverReq.getSubCoverId()) ?0 : Integer.valueOf(coverReq.getSubCoverId()) );
//			saveLod.setSubCoverId(lod.getEndtCount()==null ?0 : lod.getEndtCount().intValue() );
			saveLod.setEndtCount(lod.getEndtCount()==null ?BigDecimal.ZERO: lod.getEndtCount() );
			saveLod.setCoverId(Integer.valueOf(coverReq.getCoverId()));
			saveLod.setStatus(coverReq.getStatus());
			saveLod.setIsSelected(lod.getIsselected());
			saveLod.setCoverageType("E");
			saveLod.setDiscLoadId(lod.getEndorsementId()==null?null:Integer.valueOf(lod.getEndorsementId()));
			saveLod.setDependentCoverYn(coverReq.getDependentCoveryn());
			saveLod.setDependentCoverId(StringUtils.isBlank(coverReq.getDependentCoverId())?null:coverReq.getDependentCoverId());
			
			// Factor
			saveLod.setFactorTypeId(StringUtils.isBlank(lod.getFactorTypeId())?null: new BigDecimal(lod.getFactorTypeId()));				
			saveLod.setCoverName(lod.getEndorsementDesc());
			saveLod.setCoverDesc(lod.getEndorsementDesc());
			saveLod.setTaxId(0);
			saveLod.setEndtoptd(lod.getEndtOpdt());								
			saveLod.setPremiumBeforeDiscountFc(lod.getPremiumBeforeDiscount()==null ? null : new BigDecimal(df.format(lod.getPremiumBeforeDiscount())));
			saveLod.setPremiumBeforeDiscountLc(lod.getPremiumBeforeDiscountLC()==null ? null : new BigDecimal(df.format(lod.getPremiumBeforeDiscountLC())));
			saveLod.setPremiumAfterDiscountFc(lod.getPremiumAfterDiscount()==null ? null : new BigDecimal(df.format( lod.getPremiumAfterDiscount())));
			saveLod.setPremiumAfterDiscountLc(lod.getPremiumAfterDiscountLC()==null ? null : new BigDecimal(df.format(lod.getPremiumAfterDiscountLC())));
			saveLod.setPremiumExcludedTaxFc(lod.getPremiumExcluedTax()==null ? null : new BigDecimal(df.format(lod.getPremiumExcluedTax())));
			saveLod.setPremiumExcludedTaxLc(lod.getPremiumExcluedTaxLC()==null ? null : new BigDecimal(df.format(lod.getPremiumExcluedTaxLC())));
			saveLod.setPremiumIncludedTaxFc(lod.getPremiumIncludedTax()==null ? null : new BigDecimal(df.format(lod.getPremiumIncludedTax())));					
			saveLod.setPremiumIncludedTaxLc(lod.getPremiumIncludedTaxLC()==null ? null : new BigDecimal(df.format(lod.getPremiumIncludedTaxLC())));
			
			saveLod.setDiscountCoverId(StringUtils.isBlank(lod.getEndorsementforId())?0:Integer.parseInt(lod.getEndorsementforId()));
			
			saveLod.setCoverPeriodFrom(coverReq.getEffectiveDate());
			saveLod.setCoverPeriodTo(coverReq.getPolicyEndDate());
			
			saveLod.setCalcType(lod.getEndorsementCalcType());
			saveLod.setMinimumPremium(lod.getMinimumPremium());
			saveLod.setMinimumPremiumFc(lod.getMinimumPremium().multiply(lod.getExchangeRate(),MathContext.DECIMAL64));
			saveLod.setMinimumPremiumYn(lod.getMinimumPremiumYn());
			saveLod.setSumInsured(lod.getEndorsementsumInsured());
			saveLod.setSumInsuredLc(lod.getEndorsementsumInsuredLc());
			saveLod.setCurrency(lod.getCurrency());
			saveLod.setExchangeRate(lod.getExchangeRate()==null?null : lod.getExchangeRate());
			saveLod.setRate(lod.getEndorsementRate()==null?null:new BigDecimal(lod.getEndorsementRate()));
 			saveLod.setCalcType(lod.getEndorsementCalcType());
			saveLod.setCoverageLimit(lod.getCoverageLimit());
			saveLod.setNoOfDays("D".equals(coverReq.getProRataYn())? lod.getPolicyPeriod(): new BigDecimal(diff));
			saveLod.setProRataYn(lod.getProRataYn()==null?"N":lod.getProRataYn());
			saveLod.setProRataPercent(lod.getProRata()==null?new BigDecimal("100"):lod.getProRata().multiply( new BigDecimal("100")));
			//repository.save(saveLod);
			saveEndt.add(saveLod);
			
			// Tax
			if(lod.getTaxes()!=null && lod.getTaxes().size() > 0 ) {
				String successRes = saveTaxes(primaryKeys ,coverReq ,   lod.getTaxes() ,df , diff) ;
			}
			
		}
		repository.saveAll(saveEndt);
		res = "Success" ;
	} catch(Exception e){
		e.printStackTrace();
		log.info("Log Details" + e.getMessage());
		res = "Failed";
		return res;
		
	}return res;
}
public String saveLoadings(Map<String,Object>  primaryKeys ,  Cover coverReq , List<Loading> lodings , DecimalFormat df,String diff ) {
	String res = "Saved Successfully";
	DozerBeanMapper dozerMapper = new DozerBeanMapper();
	
	try {
		List<FactorRateRequestDetails> saveLodings = new ArrayList<FactorRateRequestDetails>();
		for (Loading lod :  lodings ) {
			FactorRateRequestDetails saveLod = new FactorRateRequestDetails();
			dozerMapper.map(coverReq, saveLod);
			saveLod.setRequestReferenceNo(primaryKeys.get("RefNo").toString() );
			saveLod.setCoverName(lod.getLoadingDesc());
			saveLod.setCoverDesc(lod.getLoadingDesc());
			saveLod.setCurrency(coverReq.getCurrency());
			saveLod.setLocationId(Integer.valueOf(primaryKeys.get("LocationId").toString()));
			saveLod.setExchangeRate(coverReq.getExchangeRate()==null?null : coverReq.getExchangeRate());
			saveLod.setCompanyId(primaryKeys.get("InsuranceId").toString());
			saveLod.setProductId(Integer.valueOf(primaryKeys.get("ProductId").toString()));
			saveLod.setSectionId(Integer.valueOf(primaryKeys.get("SectionId").toString()));
			saveLod.setSubCoverYn( primaryKeys.get("SubCoverYn").toString());
			saveLod.setVehicleId(Integer.valueOf(primaryKeys.get("VehId").toString()));	
			saveLod.setCdRefno( primaryKeys.get("CdRefNo").toString());
			saveLod.setVdRefno( primaryKeys.get("VdRefNo").toString());
			saveLod.setMsRefno( primaryKeys.get("MsRefNo").toString());	
			saveLod.setEntryDate(new Date());			
			saveLod.setCreatedBy(primaryKeys.get("CreatedBy").toString());
			saveLod.setSubCoverId(StringUtils.isBlank(coverReq.getSubCoverId()) ?0 : Integer.valueOf(coverReq.getSubCoverId()) );
			saveLod.setCoverId(Integer.valueOf(coverReq.getCoverId()));
			saveLod.setStatus(coverReq.getStatus());
			saveLod.setIsSelected(coverReq.getIsselected());
			saveLod.setCoverageType("L");
			
			// Factor
			saveLod.setFactorTypeId(StringUtils.isBlank(lod.getFactorTypeId())?null: new BigDecimal(lod.getFactorTypeId()));
			saveLod.setMinimumPremium(lod.getLoadingAmount()==null?null: new BigDecimal(df.format(lod.getLoadingAmount())));
			saveLod.setPremiumIncludedTaxFc(lod.getMaxAmount()==null?null:new BigDecimal(df.format(lod.getMaxAmount())));
			saveLod.setPremiumIncludedTaxFc(lod.getMaxAmount()==null?null:new BigDecimal(df.format(lod.getMaxAmount())));
			saveLod.setRate(lod.getLoadingRate()==null?null:new BigDecimal(lod.getLoadingRate()));
		//	saveLod.setLodingSubcoverId(lod.getSubCoverId()==null?null:Integer.valueOf(lod.getSubCoverId()));
			saveLod.setDiscLoadId(lod.getLoadingId()==null?null:Integer.valueOf(lod.getLoadingId()));
			saveLod.setDependentCoverYn("N");
			saveLod.setDependentCoverId(null);
			saveLod.setCalcType(StringUtils.isBlank(lod.getLoadingCalcType()) ? "A" :lod.getLoadingCalcType());
			saveLod.setCoverName(lod.getLoadingDesc());
			saveLod.setCoverDesc(lod.getLoadingDesc());
			saveLod.setTaxId(0);
			saveLod.setPremiumAfterDiscountFc(lod.getLoadingAmount()==null ? null : new BigDecimal(df.format(lod.getLoadingAmount())));
			saveLod.setPremiumBeforeDiscountFc(lod.getLoadingAmount()==null ? null : new BigDecimal(df.format(lod.getLoadingAmount())));
			saveLod.setPremiumExcludedTaxFc(lod.getLoadingAmount()==null ? null : new BigDecimal(df.format(lod.getLoadingAmount())));
			saveLod.setPremiumIncludedTaxFc(lod.getLoadingAmount()==null ? null : new BigDecimal(df.format(lod.getLoadingAmount())));
			saveLod.setDiscountCoverId(StringUtils.isBlank(lod.getLoadingforId())?0:Integer.parseInt(lod.getLoadingforId()));
			saveLod.setEndtCount(BigDecimal.ZERO);
			saveLod.setCoverPeriodFrom(coverReq.getEffectiveDate());
			saveLod.setCoverPeriodTo(coverReq.getPolicyEndDate());
			saveLod.setMinimumRate(lod.getMinrate()==null ? BigDecimal.ZERO:new BigDecimal(lod.getMinrate()));
			saveLod.setActualRate(lod.getLoadingRate()==null ? null :new BigDecimal(lod.getLoadingRate()));
			saveLod.setNoOfDays(new BigDecimal(diff));
		//	repository.save(saveLod);
			
		//	if(!(saveLod.getDiscLoadId()==90001 && saveLod.getPremiumExcludedTaxFc().compareTo(BigDecimal.ZERO)==0) )
				saveLodings.add(saveLod)	;
		}
		repository.saveAll(saveLodings);
		res = "Success" ;
	} catch(Exception e){
		e.printStackTrace();
		log.info("Log Details" + e.getMessage());
		res = "Failed";
		return res;
		
	}return res;
}

public String saveDiscountOrPromoRates(Map<String,Object>  primaryKeys ,  Cover coverReq , List<Discount> discounts , DecimalFormat df ,String diff) {
	String res = "Saved Successfully";
	DozerBeanMapper dozerMapper = new DozerBeanMapper();
	try {
		List<FactorRateRequestDetails> saveDiscountList = new ArrayList<FactorRateRequestDetails>();
		for (Discount disc :  discounts ) {
			FactorRateRequestDetails saveDiscounts = new FactorRateRequestDetails();
			dozerMapper.map(coverReq, saveDiscounts);
			saveDiscounts.setRequestReferenceNo(primaryKeys.get("RefNo").toString() );
			saveDiscounts.setDiscLoadId(Integer.valueOf(disc.getDiscountId()));
			saveDiscounts.setLocationId(Integer.valueOf(primaryKeys.get("LocationId").toString()));
			saveDiscounts.setCoverName(disc.getDiscountDesc());
			saveDiscounts.setCoverDesc(disc.getDiscountDesc());
			saveDiscounts.setCurrency(coverReq.getCurrency());
			saveDiscounts.setExchangeRate(coverReq.getExchangeRate()==null?null : coverReq.getExchangeRate());
			saveDiscounts.setCompanyId(primaryKeys.get("InsuranceId").toString());
			saveDiscounts.setProductId(Integer.valueOf(primaryKeys.get("ProductId").toString()));
			saveDiscounts.setSectionId(Integer.valueOf(primaryKeys.get("SectionId").toString()));
			saveDiscounts.setSubCoverYn( primaryKeys.get("SubCoverYn").toString());
			saveDiscounts.setVehicleId(Integer.valueOf(primaryKeys.get("VehId").toString()));	
			saveDiscounts.setCdRefno( primaryKeys.get("CdRefNo").toString());
			saveDiscounts.setVdRefno( primaryKeys.get("VdRefNo").toString());
			saveDiscounts.setMsRefno( primaryKeys.get("MsRefNo").toString());	
			saveDiscounts.setEntryDate(new Date());			
			Object createdBy = primaryKeys.get("CreatedBy");
			saveDiscounts.setCreatedBy(createdBy != null ? createdBy.toString() : null);
			saveDiscounts.setSubCoverId(StringUtils.isBlank(coverReq.getSubCoverId()) ?0 : Integer.valueOf(coverReq.getSubCoverId()) );
			saveDiscounts.setStatus(coverReq.getStatus());
			saveDiscounts.setCoverageType(StringUtils.isBlank(disc.getCoverAgeType())?"D":disc.getCoverAgeType());
			saveDiscounts.setMinimumPremium(disc.getMaxAmount()==null ? null : new BigDecimal(df.format(disc.getMaxAmount())));
			saveDiscounts.setSumInsured(null);
			saveDiscounts.setRate(disc.getDiscountRate()==null ? null :new BigDecimal(disc.getDiscountRate()));
			saveDiscounts.setPremiumAfterDiscountFc(disc.getDiscountAmount()==null ? null : new BigDecimal(df.format(disc.getDiscountAmount())));
			saveDiscounts.setPremiumBeforeDiscountFc(disc.getDiscountAmount()==null ? null : new BigDecimal(df.format(disc.getDiscountAmount())));
			saveDiscounts.setPremiumExcludedTaxFc(disc.getDiscountAmount()==null ? null : new BigDecimal(df.format(disc.getDiscountAmount())));
			saveDiscounts.setPremiumIncludedTaxFc(disc.getDiscountAmount()==null ? null : new BigDecimal(df.format(disc.getDiscountAmount())));
			saveDiscounts.setDependentCoverYn("N");
			saveDiscounts.setDependentCoverId(null);
			saveDiscounts.setFactorTypeId(StringUtils.isBlank(disc.getFactorTypeId())?null :new BigDecimal(disc.getFactorTypeId()) );
			saveDiscounts.setIsSelected(coverReq.getIsselected() );
			saveDiscounts.setCalcType(disc.getDiscountCalcType());
			saveDiscounts.setTaxId(0);
			saveDiscounts.setDiscountCoverId(StringUtils.isBlank(disc.getDiscountforId())?0:Integer.parseInt(disc.getDiscountforId()));
			saveDiscounts.setEndtCount(BigDecimal.ZERO );
			saveDiscounts.setCoverPeriodFrom(coverReq.getEffectiveDate());
			saveDiscounts.setCoverPeriodTo(coverReq.getPolicyEndDate());
			saveDiscounts.setMinimumRate(disc.getMinrate()==null ? BigDecimal.ZERO:new BigDecimal(disc.getMinrate()));	
			saveDiscounts.setNoOfDays(new BigDecimal(diff));
			
				
			saveDiscounts.setActualRate(disc.getDiscountRate()==null ? null :new BigDecimal(disc.getDiscountRate()));
			
			//repository.save(saveDiscounts);
			if(!(saveDiscounts.getDiscLoadId()==90002 && saveDiscounts.getPremiumExcludedTaxFc().compareTo(BigDecimal.ZERO)==0) )
				saveDiscountList.add(saveDiscounts);
			
		}
		repository.saveAll(saveDiscountList);
		res = "Success" ;
	} catch(Exception e){
		e.printStackTrace();
		log.info("Log Details" + e.getMessage());
		res = "Failed";
		return res;
		
	}return res;
}

public List<SectionCoverMaster> getBySectionCoverId(String Company_id,Integer product_id,Integer Sectionid,List<Integer> coverIdsToExclude) {
	List<SectionCoverMaster> list = new ArrayList<SectionCoverMaster>();
	DozerBeanMapper mapper = new DozerBeanMapper();
	String pattern = "#####0.00";
	DecimalFormat df = new DecimalFormat(pattern);
	
	String patternn = "#####0.0000";
	DecimalFormat df1 = new DecimalFormat(patternn);
	
	try {
		Date today  =  new Date();
		Calendar cal = new GregorianCalendar();
		cal.setTime(today);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 50);
		today = cal.getTime();

		
		// Find Latest Record
		CriteriaBuilder cb2 = em.getCriteriaBuilder();
		CriteriaQuery<SectionCoverMaster> query2 = cb2.createQuery(SectionCoverMaster.class);

		// Find All
		Root<SectionCoverMaster> b2 = query2.from(SectionCoverMaster.class);

		// Effective Date Max Filter
		
		Subquery<Long> amendId = query2.subquery(Long.class);
		Root<SectionCoverMaster> ocpm2 = amendId.from(SectionCoverMaster.class);
		amendId.select(cb2.max(ocpm2.get("amendId")));
		Predicate a7 = cb2.equal(ocpm2.get("coverId"), b2.get("coverId"));
		Predicate a8 = cb2.equal(ocpm2.get("sectionId"), b2.get("sectionId"));
		Predicate a9 = cb2.equal(ocpm2.get("productId"), b2.get("productId"));
		Predicate a10 = cb2.equal(ocpm2.get("companyId"), b2.get("companyId"));
		Predicate a11 = cb2.equal(ocpm2.get("subCoverId"), b2.get("subCoverId"));
		Predicate a13 = cb2.equal(ocpm2.get("agencyCode"), b2.get("agencyCode"));
		Predicate a14 = cb2.equal(ocpm2.get("branchCode"), b2.get("branchCode"));
		amendId.where(a7,a8,a9,a10,a11,a13,a14);

		// Select
		query2.select(b2);

		// Order By
		List<Order> orderList2 = new ArrayList<Order>();
		orderList2.add(cb2.desc(b2.get("effectiveDateEnd")));

		// Where
		Predicate n5 = cb2.equal(b2.get("amendId"),amendId);
		Predicate n6 =cb2.equal(b2.get("subCoverId"), "0");
		Predicate n7 = cb2.equal(b2.get("productId"),product_id);
		Predicate n14 = cb2.equal(b2.get("companyId"), Company_id);
		Predicate n15 = cb2.equal(b2.get("sectionId"), Sectionid);
		Predicate n16 = cb2.equal(b2.get("coverageType"), "A");
		Predicate n18 = cb2.equal(b2.get("status"), "Y");

		Predicate n17;
		if (coverIdsToExclude != null && !coverIdsToExclude.isEmpty()) {
		    n17 = cb2.not(b2.get("coverId").in(coverIdsToExclude));
		} else {
		    n17 = cb2.conjunction(); // No exclusion
		}
		Predicate[] predicatesArray = new Predicate[] { n5, n6, n7, n14, n15, n16,n17,n18 };

	
		query2.where(predicatesArray).orderBy(orderList2);

		// Get Result
		TypedQuery<SectionCoverMaster> result2 = em.createQuery(query2);
		list = result2.getResultList();
		list.stream().distinct().collect(Collectors.toList());
		
		
	} catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is ---> " + e.getMessage());
		return null;
	}
	return list;
}

}
