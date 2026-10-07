
package com.maan.eway.endorsment.service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategy;
import com.maan.eway.bean.AviationInfo;
import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.DocumentTransactionDetails;
import com.maan.eway.bean.EmiTransactionDetails;
import com.maan.eway.bean.EndtTypeMaster;
import com.maan.eway.bean.EngineerInfo;
import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.EserviceSectionDetails;
import com.maan.eway.bean.EserviceTravelDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.LoginMaster;
import com.maan.eway.bean.LoginProductMaster;
import com.maan.eway.bean.MarineHullInfo;
import com.maan.eway.bean.MsAssetDetails;
import com.maan.eway.bean.MsCommonDetails;
import com.maan.eway.bean.MsHumanDetails;
import com.maan.eway.bean.MsVehicleDetails;
import com.maan.eway.bean.PolicyCoverData;
import com.maan.eway.bean.PolicyCoverDataIndividuals;
import com.maan.eway.bean.ProductSectionMaster;
import com.maan.eway.bean.SectionDataDetails;
import com.maan.eway.bean.TermsAndCondition;
import com.maan.eway.calculator.util.RatingFactorsUtil;
import com.maan.eway.common.req.ChangeEndoStatusReq;
import com.maan.eway.common.req.CopyQuoteReq;
import com.maan.eway.common.req.CoverIdsReq;
import com.maan.eway.common.req.EndtSectionListReq;
import com.maan.eway.common.req.EndtSectionSaveReq;
import com.maan.eway.common.req.EservieMotorDetailsViewRes;
import com.maan.eway.common.req.NewQuoteReq;
import com.maan.eway.common.req.VehicleIdsReq;
import com.maan.eway.common.req.ViewQuoteReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.EndorsementCriteriaRes;
import com.maan.eway.common.res.EndtSectionListRes;
import com.maan.eway.common.res.EndtSectionsRes;
import com.maan.eway.common.res.NewQuoteRes;
import com.maan.eway.common.res.ViewQuoteRes;
import com.maan.eway.common.service.QuoteService;
import com.maan.eway.common.service.impl.GridServiceImpl;
import com.maan.eway.common.service.impl.PaymentServiceImpl;
import com.maan.eway.common.service.impl.QuoteThreadServiceImpl;
import com.maan.eway.endorsment.request.EndDeleteReq;
import com.maan.eway.endorsment.request.EndRequest;
import com.maan.eway.endorsment.request.EndorsementType;
import com.maan.eway.endorsment.request.Endorsment;
import com.maan.eway.endorsment.request.EndtMaster;
import com.maan.eway.endorsment.util.CopyBuildingRaw;
import com.maan.eway.endorsment.util.CopyCommonRaw;
import com.maan.eway.endorsment.util.CopyPolicyCoverData;
import com.maan.eway.endorsment.util.CopyRawTable;
import com.maan.eway.endorsment.util.CopyTravelRaw;
import com.maan.eway.endorsment.util.QuoteInfoUtil;
import com.maan.eway.error.Error;
import com.maan.eway.excelupload.bean.AdditionalInformation;
import com.maan.eway.excelupload.repository.AdditionalInformationRepository;
import com.maan.eway.repository.AviationInfoRepository;
import com.maan.eway.repository.BuildingRiskDetailsRepository;
import com.maan.eway.repository.CommonDataDetailsRepository;
import com.maan.eway.repository.ContentAndRiskRepository;
import com.maan.eway.repository.DocumentTransactionDetailsRepository;
import com.maan.eway.repository.EServiceMotorDetailsRepository;
import com.maan.eway.repository.EServiceSectionDetailsRepository;
import com.maan.eway.repository.EmiTransactionDetailsRepository;
import com.maan.eway.repository.EndtDependantFieldsMasterRepository;
import com.maan.eway.repository.EndtTypeMasterRepository;
import com.maan.eway.repository.EngineerInfoRepository;
import com.maan.eway.repository.EserviceBuildingDetailsRepository;
import com.maan.eway.repository.EserviceCommonDetailsRepository;
import com.maan.eway.repository.FactorRateRequestDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.LoginMasterRepository;
import com.maan.eway.repository.MarineHullInfoRepo;
import com.maan.eway.repository.MotorDataDetailsRepository;
import com.maan.eway.repository.MsAssetDetailsRepository;
import com.maan.eway.repository.MsCommonDetailsRepository;
import com.maan.eway.repository.MsHumanDetailsRepository;
import com.maan.eway.repository.MsVehicleDetailsRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.repository.PolicyCoverDataRepository;
import com.maan.eway.repository.ProductEmployeesDetailsRepository;
import com.maan.eway.repository.SectionDataDetailsRepository;
import com.maan.eway.repository.TermsAndConditionRepository;
import com.maan.eway.req.FactorRateDetailsGetReq;
import com.maan.eway.res.SuccessRes;
import com.maan.eway.res.calc.Cover;
import com.maan.eway.service.FactorRateRequestDetailsService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import jakarta.transaction.Transactional;

@Service
public class EndorsementService {

	@Autowired
	private HomePositionMasterRepository hpmrepo;
	@Autowired
	private GridServiceImpl copyquoteService;
	@Autowired
	private PaymentServiceImpl paymentServiceImpl;
	
	@Autowired
	private QuoteInfoUtil quoteutil;
	
	@Autowired
	private PolicyCoverDataRepository pcdRepo;
	
	@Autowired
	private CopyRawTable copyraw;
	
	@Autowired
	private CopyBuildingRaw copyBuildingraw;
	
	@Autowired
	private CopyTravelRaw copyTravelraw;
	
	@Autowired
	private CopyCommonRaw copyCommonraw;
	
	@Autowired
	private EServiceSectionDetailsRepository eserSecRepo ; 

	@Autowired
	private HomePositionMasterRepository homeRepo ; 

	@Autowired
	private EndtDependantFieldsMasterRepository dependantRepo;
	
	
	@Autowired
	private EserviceBuildingDetailsRepository eserBuldingRepo ;
	
	@Autowired
	private EserviceCommonDetailsRepository eserCommonRepo ;
	
	@Value(value = "${travel.productId}")
	private String travelProductId;

	@Autowired
	private  FactorRateRequestDetailsService factorService;
	@Autowired
	private  QuoteService entityService ;
	
	@Autowired
	private TermsAndConditionRepository termsRepo;
	
	@Autowired
	private EmiTransactionDetailsRepository emiRepo;
	
	private Logger log = LogManager.getLogger(EndorsementService.class);
	
	@Autowired
	private CopyPolicyCoverData copycover;
	
	@Autowired
	private LoginMasterRepository loginRepo ;
	

	@Autowired
	private EServiceSectionDetailsRepository sectionRepo ;
	

	@Autowired
	private  SectionDataDetailsRepository sddRepo;
	
	
	@Autowired
	private MsAssetDetailsRepository assertRepo;
	
	
	@Autowired
	private MsHumanDetailsRepository mshummanRepo;
	
	@Autowired
	private MsCommonDetailsRepository msCommonRepo;
	
	@Autowired
	private FactorRateRequestDetailsRepository factRepo;
	
	@Autowired
	private PolicyCoverDataRepository policyRepo;
	
	@Autowired
	private BuildingRiskDetailsRepository buildingRepo;
	
	@Autowired
	private CommonDataDetailsRepository commonRepo;
	
	@Autowired
	private MotorDataDetailsRepository motorRepo;
	
	@Autowired
	private EServiceMotorDetailsRepository emotorRepo;
	
	@Autowired
	private EngineerInfoRepository engRepo;
	
	@Autowired
	private QuoteThreadServiceImpl quoteThreadimpl;
	
	@Autowired
	private EmiTransactionDetailsRepository emiTransactionDetailsRepo;
	

	@Autowired
	private PersonalInfoRepository personalInforepo;
	
	@Autowired
	private ContentAndRiskRepository crRepo;
	
	@Autowired
	private ProductEmployeesDetailsRepository pedRepo;
	
	@Autowired
	private DocumentTransactionDetailsRepository docRepo;
	
	@Autowired
	private DozerBeanMapper dozerBeanMapper;
	
	@Autowired
	private MsVehicleDetailsRepository msVehicleRepo;
	
	@Autowired
	private AviationInfoRepository aviRepo;
	
	@Autowired
	private  MarineHullInfoRepo marineRepo;
	
	
	@Autowired
	private AdditionalInformationRepository additonRepo;
	
	public CommonRes cancelPolicy(Endorsment request) {
		try {

			/*HomePositionMaster hp=hpmrepo.findByPolicyNoAndStatusAndCompanyIdAndProductId(
			 * request.getPolicyNo(),
			 * "P",
			 * request.getCompanyId(),
			 * Integer.valueOf(request.getProductId().intValue())
			 * );

			CopyQuoteReq c= new CopyQuoteReq();
					c.setRequestReferenceNo(hp.getRequestReferenceNo());
					c.setLoginId(hp.getLoginId());
					c.setApplicationId(hp.getApplicationId());
					c.setInsuranceId(hp.getCompanyId());
					c.setBranchCode(hp.getBranchCode());
					c.setProductId(String.valueOf(hp.getProductId()));
					c.setUserType("Broker");
					c.setEndtTypeId("42");
					c.setTypeId("Endt");
					c.setQuoteNo(hp.getQuoteNo());
					

			CopyQuoteSuccessRes copyQuote = copyquoteService.copyQuote(c);
			;

			List<PolicyCoverData> covers = quoteutil.getFromPolicyCoverData(copyQuote.getQuoteNo());

			List<PolicyCoverData> distinctsVehicle = covers.stream()
					.filter(distinctByKey(cust -> cust.getVehicleId()))
					.collect(Collectors.toList());
			for(PolicyCoverData v  :distinctsVehicle) {

				List<PolicyCoverData> distinctsCovers = covers.stream()
						.filter(p-> p.getVehicleId().equals(v.getVehicleId()) )
						.filter(distinctByKey(cust -> cust.getCoverId() ))
						.collect(Collectors.toList());
				for(PolicyCoverData dc:distinctsCovers) {
					List<PolicyCoverData> cover = covers.stream()
							.filter(p-> p.getVehicleId().equals(v.getVehicleId()) )
							.filter(cust -> cust.getCoverId().equals(dc.getCoverId()) )
							.collect(Collectors.toList());
					
					List<PolicyCoverData> basecov = cover.stream().filter(cc-> cc.getDiscLoadId()==0 && !cc.getCoverageType().equals("T")).collect(Collectors.toList());
					//mapToDouble(i->i.getLoadingAmount().doubleValue()).sum();
					basecov.get(0).setPremiumAfterDiscountFc(new BigDecimal(cover.stream().mapToDouble(i->i.getPremiumAfterDiscountFc().doubleValue()).sum()));
					basecov.get(0).setPremiumAfterDiscountLc(new BigDecimal(cover.stream().mapToDouble(i->i.getPremiumAfterDiscountLc().doubleValue()).sum()));
					basecov.get(0).setPremiumBeforeDiscountFc(new BigDecimal(cover.stream().mapToDouble(i->i.getPremiumBeforeDiscountFc().doubleValue()).sum()));
					basecov.get(0).setPremiumBeforeDiscountLc(new BigDecimal(cover.stream().mapToDouble(i->i.getPremiumBeforeDiscountLc().doubleValue()).sum()));
					basecov.get(0).setPremiumExcludedTaxFc(new BigDecimal(cover.stream().mapToDouble(i->i.getPremiumExcludedTaxFc().doubleValue()).sum()));
					basecov.get(0).setPremiumExcludedTaxLc(new BigDecimal(cover.stream().mapToDouble(i->i.getPremiumExcludedTaxLc().doubleValue()).sum()));
					basecov.get(0).setPremiumIncludedTaxFc(new BigDecimal(cover.stream().mapToDouble(i->i.getPremiumIncludedTaxFc().doubleValue()).sum()));
					basecov.get(0).setPremiumIncludedTaxLc(new BigDecimal(cover.stream().mapToDouble(i->i.getPremiumIncludedTaxLc().doubleValue()).sum()));
					covers.add(basecov.get(0));
				}
			}
			pcdRepo.saveAllAndFlush(covers);*/
			FactorRateDetailsGetReq viewCalcReq=new FactorRateDetailsGetReq();
			viewCalcReq.setProductId(request.getProductId().toPlainString());
			viewCalcReq.setRequestReferenceNo(request.getRequestReferenceNo());
			viewCalcReq.setInsuranceId(request.getCompanyId());
			List<EservieMotorDetailsViewRes> viewCalc = new ArrayList<>();
			Map<List<EservieMotorDetailsViewRes>, List<Cover>> res = factorService.getFactorRateRequestDetails(viewCalcReq, "");
			if(res != null && !res.isEmpty() ) 
			{
				Entry<List<EservieMotorDetailsViewRes>, List<com.maan.eway.res.calc.Cover>> entry =res.entrySet().iterator().next();
				viewCalc=entry.getKey();
			}
		//	viewCalc = factorService.getFactorRateRequestDetails(viewCalcReq, "");
			CompanyProductMaster product =  getCompanyProductMasterDropdown(viewCalcReq.getInsuranceId(), viewCalcReq.getProductId().toString());
			List<VehicleIdsReq> vehicles=new ArrayList<VehicleIdsReq>();
			if(product.getMotorYn().equals("M") || viewCalcReq.getProductId().equalsIgnoreCase(travelProductId)) {
				
			for (EservieMotorDetailsViewRes motors : viewCalc) {
				
				VehicleIdsReq v=new VehicleIdsReq();
				
				v.setVehicleId(Integer.parseInt(motors.getVehicleId()));
				v.setLocationId(motors.getLocationId()==null?1:Integer.parseInt(motors.getLocationId()));
				
				List<Cover> coverList = motors.getCoverList();
				List<Cover> distinctSections = coverList.stream().filter(distinctByKey(c->c.getSectionId())).collect(Collectors.toList());
				
				for (Cover ds : distinctSections) {
					v.setSectionId(ds.getSectionId());
					List<CoverIdsReq> covers=new ArrayList<CoverIdsReq>();				
					for (Cover cover : coverList) {

						if("Y".equals(cover.getUserOpt()) && ds.getSectionId().equals(cover.getSectionId())) {
							String isSubCover = cover.getIsSubCover();
							
							if("Y".equals(isSubCover)) {
								List<Cover> subcovers = cover.getSubcovers().stream().filter(f-> "Y".equals(f.getUserOpt())).collect(Collectors.toList());
								for (Cover c : subcovers) {
									CoverIdsReq r=new CoverIdsReq();
									r.setSubCoverYn(isSubCover);
									r.setCoverId(Integer.parseInt(c.getCoverId()));
									r.setSubCoverId(c.getSubCoverId());
									covers.add(r);
								}
							}else {
								CoverIdsReq r=new CoverIdsReq();						
								
								r.setSubCoverYn(isSubCover);
								r.setCoverId(Integer.parseInt(cover.getCoverId()));
								r.setSubCoverId(null);
								covers.add(r);
							}
							
						}
					}
					v.setCoverIdList(covers);
				}
				vehicles.add(v);
				
				
			}  
			
		}else {
			Set<String> findlocationid = viewCalc.stream().map(EservieMotorDetailsViewRes::getLocationId)
					.distinct().collect(Collectors.toSet());
			for (String data : findlocationid) {
				String LocationId=data;
				List<EservieMotorDetailsViewRes> locFilter = viewCalc.stream()
						.filter(o -> o.getLocationId().equals(data)).collect(Collectors.toList());
				Set<String> findVehicleid = locFilter.stream().map(EservieMotorDetailsViewRes::getVehicleId).distinct()
						.collect(Collectors.toSet());
				for (String vehicle : findVehicleid) {
					List<EservieMotorDetailsViewRes> vehicleFilter = locFilter.stream()
							.filter(o -> o.getVehicleId().equals(vehicle)).collect(Collectors.toList());
										
					for (EservieMotorDetailsViewRes motors : vehicleFilter) {
						List<CoverIdsReq> covers = new ArrayList<CoverIdsReq>();
						
						List<Cover> coverList = motors.getCoverList();
						List<Cover> distinctSections = coverList.stream().filter(distinctByKey(c -> c.getSectionId()))
								.collect(Collectors.toList());

						for (Cover ds : distinctSections) {
							
							VehicleIdsReq v = new VehicleIdsReq();

							v.setVehicleId(Integer.parseInt(vehicle));
							v.setLocationId(Integer.parseInt(LocationId));
							v.setSectionId(ds.getSectionId());
							
							for (Cover cover : coverList) {

								if ("Y".equals(cover.getUserOpt()) && ds.getSectionId().equals(cover.getSectionId())) {
									String isSubCover = cover.getIsSubCover();

									if ("Y".equals(isSubCover)) {
										List<Cover> subcovers = cover.getSubcovers().stream()
												.filter(f -> "Y".equals(f.getUserOpt())).collect(Collectors.toList());
										for (Cover c : subcovers) {
											CoverIdsReq r = new CoverIdsReq();
											r.setSubCoverYn(isSubCover);
											r.setCoverId(Integer.parseInt(c.getCoverId()));
											r.setSubCoverId(c.getSubCoverId());
											covers.add(r);
										}
									} else {
										CoverIdsReq r = new CoverIdsReq();

										r.setSubCoverYn(isSubCover);
										r.setCoverId(Integer.parseInt(cover.getCoverId()));
										r.setSubCoverId(null);
										covers.add(r);
									}

								}
							}
							v.setCoverIdList(covers);
							vehicles.add(v);
						}
					
						
					}
					
				}
			}
		}
			
			NewQuoteReq newq=new NewQuoteReq();
			if (StringUtils.isNotBlank(request.getRequestReferenceNo()) && StringUtils.isNotBlank(request.getProductId().toPlainString())) {
				List<EserviceMotorDetails> motorDatas = emotorRepo
						.findByRequestReferenceNoOrderBySectionNameAsc(request.getRequestReferenceNo());
				 motorDatas.forEach(m -> {
			            String key = m.getRequestReferenceNo() + "_" + m.getRiskId();
			            newq.getMotorDetailsMap()
			                    .computeIfAbsent(key, k -> new ArrayList<>())
			                    .add(m);
			        });
			}
			newq.setCreatedBy(request.getCreatedBy());
			newq.setManualReferralYn("N");
			newq.setProductId(request.getProductId().toPlainString());
			newq.setReferralRemarks("");
			newq.setRequestReferenceNo(request.getRequestReferenceNo());
			newq.setSectionId(null);
			newq.setVehicleIdsList(vehicles);
			
			ObjectMapper objectMapper = new ObjectMapper();
			objectMapper.setPropertyNamingStrategy(PropertyNamingStrategy.UPPER_CAMEL_CASE);
			System.out.println("Buy Policy Request -->" + objectMapper.writeValueAsString(newq));
			CommonRes generateNewQuote = entityService.generateNewQuote(newq);		    
			if(!generateNewQuote.getIsError()) {
				NewQuoteRes view=(NewQuoteRes) generateNewQuote.getCommonResponse();
				ViewQuoteReq requestView=new ViewQuoteReq();
				requestView.setQuoteNo(view.getQuoteNo());
				ViewQuoteRes viewQuoteDetails = entityService.viewQuoteDetails(requestView);
				quoteThreadimpl.QuoteGenerateUwsysIdAndPolIdxID(newq, view.getQuoteNo());
				generateNewQuote.setCommonResponse(viewQuoteDetails);
			}
			return generateNewQuote;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public  <T> java.util.function.Predicate<T> distinctByKey(Function<? super T, Object> keyExtractor) {
		Map<Object, Boolean> uniqueMap = new ConcurrentHashMap<>();
		return t -> uniqueMap.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}
	
	
	@Autowired
	private EndtTypeMasterRepository endtTypeRepo;
	
	public EndtMaster getEndorsementTypes(Endorsment request) {
		try {
			
			
			List<EndtTypeMaster> m =ratingutil.getEndtMasterDatas(request.getCompanyId(),request.getProductId().toPlainString());

					// endtTypeRepo.findByCompanyIdAndProductIdAndStatusAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqualOrderByPriorityAsc(request.getCompanyId(),Integer.valueOf(request.getProductId().intValue()),"Y",new Date(),new Date());
			// LoginDetails
			List<String> financeids = new ArrayList<String>();
			List<String> nonfinanceids = new ArrayList<String>();
			LoginMaster loginData = loginRepo.findByLoginId(request.getLoginId());
			if(StringUtils.isNotBlank(request.getLoginId()) ) {
				LoginProductMaster loginProduct =   getLoginProductDetails(request.getCompanyId() , request.getProductId().toPlainString() , request.getLoginId() );
				
				if ( loginProduct !=null ) {
					String financeid = StringUtils.isBlank(loginProduct.getFinancialEndtIds()) ? "" :  loginProduct.getFinancialEndtIds();
					String nonFinanceid =StringUtils.isBlank( loginProduct.getNonFinancialEndtIds()) ? "" : loginProduct.getNonFinancialEndtIds();

					if( "Issuer".equalsIgnoreCase(loginData.getUserType()) ) {
						financeids = new ArrayList<String>(Arrays.asList(financeid.split(",")));
						;
						 // String[] strSplit = financeid.split(",");
						 // strSplit
						//  totalids.addAll(Arrays.asList(strSplit));
							
						  
					}
			        //nonfinanceids = new ArrayList<String>(Arrays.asList(nonFinanceid));
					nonfinanceids =  new ArrayList<String>(Arrays.asList(nonFinanceid.split(",")));
					
					
			  	}
			//	totalids.add(financeids);
			//	totalids.addAll(nonfinanceids);
			}
			
			List<EndorsementType> ets=new ArrayList<EndorsementType>(); 
			for(EndtTypeMaster ent:m) {
				
				// Login Restrict Condition
				boolean endtAvailable = false ;
				if(StringUtils.isNotBlank(request.getLoginId())  ) {
					if(ent.getEndtTypeCategoryId().equals(1) ) {
						List<String> filterTotalIds = nonfinanceids.stream().filter( o -> o.equalsIgnoreCase(ent.getEndtTypeId().toString()) ).collect(Collectors.toList());
						if(filterTotalIds.size() > 0 ) {
							endtAvailable = true ;
						}
					} else if( "Issuer".equalsIgnoreCase(loginData.getUserType())){
						List<String> filterTotalIds = financeids.stream().filter( o -> o.equalsIgnoreCase(ent.getEndtTypeId().toString()) ).collect(Collectors.toList());
						if(filterTotalIds.size() > 0 ) {
							endtAvailable = true ;
						}
					}
					
				} else {
					endtAvailable = true ;
				}
			
				if( endtAvailable == true  ) {
					String fieldAsString = ent.getEndtDependantFields();
					List<String> fields=null;
					if(StringUtils.isNotBlank(fieldAsString)) {
						 fields=new ArrayList<String>();
						 if(fieldAsString.indexOf(",")!=-1) {
							 String[] split = fieldAsString.split(",");
							 fields= Arrays.asList(split);
						 }else {
							 fields.add(fieldAsString);
						 }
					}
					
					EndorsementType e = EndorsementType.builder()
							.endorsementCategory(new BigDecimal(ent.getEndtTypeCategoryId()))
							.endorsementCategoryDesc(ent.getEndtTypeCategory())
							.endorsementDesc(ent.getEndtTypeDesc())
							.endtType(new BigDecimal(ent.getEndtTypeId()))
							.fieldsAllowed(fields)
							.sectionModificationYn(ent.getSectionModificationYn())
							.sectionModificationType(ent.getSectionModificationType())
							.isCoverEndt(ent.getIsCoverendt())
							.endtShortCode(ent.getEndtShortCode())
							.endtShortDesc(ent.getEndtShortDesc())
							.build();
					
					ets.add(e);
				}
				
			}
			
			// Vehicle Endt Condition
			if(request.getProductId()!=null && ( request.getProductId().equals(new BigDecimal("5")) ||	request.getProductId().equals(new BigDecimal("46")) ) ) {
				if(StringUtils.isNotBlank(request.getOriginalPolicyNo())   ) {
					// Get Datas
					CriteriaBuilder cb = em.getCriteriaBuilder();
					CriteriaQuery<EserviceMotorDetails> query = cb.createQuery(EserviceMotorDetails.class);

					// Find All
					Root<EserviceMotorDetails> mot = query.from(EserviceMotorDetails.class);
				
					// Select
					query.select(mot);
					
					List<Order> orderList = new ArrayList<Order>();
					orderList.add(cb.desc(mot.get("entryDate")));
					
					// Where
					Predicate n1 = cb.equal(mot.get("originalPolicyNo"), request.getOriginalPolicyNo());
					Predicate n2 = cb.equal(mot.get("policyNo"), request.getOriginalPolicyNo());
					Predicate n3 = cb.or(n1,n2);
					Predicate n4 = cb.equal(mot.get("status"), "P");
					query.where( n3,n4).orderBy(orderList);
			
					// Get Result
					TypedQuery<EserviceMotorDetails> result = em.createQuery(query);
					List<EserviceMotorDetails> list = result.getResultList();
						if(list.size() > 0 ) {
							EserviceMotorDetails lastData = list.get(0);
							List<EserviceMotorDetails> filterLastEndtList = new ArrayList<EserviceMotorDetails>();
							if( lastData.getEndtCount()==null) {
								filterLastEndtList  = list.stream().filter( o -> o.getPolicyNo().equalsIgnoreCase( request.getOriginalPolicyNo() ) ).collect(Collectors.toList());
								
							} else {
								filterLastEndtList  = list.stream().filter( o -> o.getEndtCount()!=null && o.getEndtCount().equals(lastData.getEndtCount()) && 
										o.getPolicyNo().equalsIgnoreCase(lastData.getPolicyNo()) 	).collect(Collectors.toList());
							}
							
							// Removal Of Vehicle Condition
							if(filterLastEndtList.size() == 1  ) {
								ets.removeIf( o -> o.getEndtType().equals(new BigDecimal("847")) ) ;								
							}	
							
							// Modification of Suminsured Condition
							if(lastData.getInsuranceClass().equalsIgnoreCase("3")) {
								ets.removeIf( o -> o.getEndtType().equals(new BigDecimal("850")) ) ;
							}
									
						}
						
					}
					
				
			}
			EndtMaster endt=EndtMaster.builder().endorsementTypes(ets).build();
			
			return endt;
			
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@PersistenceContext
	private EntityManager em;
	private EndtTypeMaster entMaster;
	
	
	public LoginProductMaster getLoginProductDetails(String companyId  , String productId , String loginId ) {
		LoginProductMaster res = new LoginProductMaster();
		List<LoginProductMaster> list = new ArrayList<LoginProductMaster>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			// Update
			// Get Less than Equal Today Record 
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<LoginProductMaster> query = cb.createQuery(LoginProductMaster.class);

			// Find All
			Root<LoginProductMaster> b = query.from(LoginProductMaster.class);

			// Select
			query.select(b);

			// Effective Date Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<LoginProductMaster> ocpm1 = effectiveDate.from(LoginProductMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("loginId"), b.get("loginId"));
			effectiveDate.where(a1,a2,a3);

			// Effective Date Max Filter
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<LoginProductMaster> ocpm2 = effectiveDate2.from(LoginProductMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate a4 = cb.equal(ocpm2.get("productId"), b.get("productId"));
			Predicate a5 = cb.equal(ocpm2.get("companyId"), b.get("companyId"));
			Predicate a6 = cb.equal(ocpm2.get("loginId"), b.get("loginId"));
			effectiveDate2.where(a4,a5,a6);
			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(b.get("effectiveDateStart")));
			
			// Where
			Predicate n1 = cb.lessThanOrEqualTo(b.get("effectiveDateEnd"), effectiveDate2);
			Predicate n2 = cb.greaterThanOrEqualTo(b.get("effectiveDateStart"), effectiveDate);
			Predicate n3 =  cb.equal(b.get("productId"), productId );
			Predicate n4 =  cb.equal(b.get("companyId"), companyId );
			Predicate n5 =  cb.equal(b.get("loginId"), loginId );

			query.where(n1, n2, n3,n4,n5);//.orderBy(orderList);

			// Get Result
			TypedQuery<LoginProductMaster> result = em.createQuery(query);
			list = result.getResultList();
			res = list.get(0);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return res;
	}
	
	
	public List<EndorsementCriteriaRes> endorsementPendingData(Endorsment request) {

		 try {

			 
				  List<EndorsementCriteriaRes> grids = new ArrayList<EndorsementCriteriaRes>();
				  CompanyProductMaster product =  getCompanyProductMasterDropdown(request.getCompanyId() , request.getProductId().toString());
					HomePositionMaster homePositionMaster = homeRepo.findByPolicyNo(request.getPolicyNo());

				if(product.getMotorYn().equalsIgnoreCase("M") ) {
					 grids = endorsementMotorGrid(request);
					
					
				} else if (product.getMotorYn().equalsIgnoreCase("H")  && request.getProductId().equals(new BigDecimal(travelProductId))  ) {
					grids = copyTravelraw.endorsementTravelGrid(request);
				
					
				} else if (product.getMotorYn().equalsIgnoreCase("A") ) {
					grids = copyBuildingraw.endorsementBuildingGrid(request);
				
					
				} else {
					
					grids = copyCommonraw.endorsementCommonGrid(request);
				
				
				}
				/// for endorsement Emi Check...
			if(grids!=null){
				grids.forEach(eachGrid->{
					eachGrid.setEmiYn(homePositionMaster.getEmiYn());
				});
			}
			 return grids;
		 }catch (Exception e) {
			 e.printStackTrace();
		}
		return null;
	}
	
	
	public List<EndorsementCriteriaRes> endorsementMotorGrid(Endorsment request) {

		 try {

			 
				// Get Datas
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<EndorsementCriteriaRes> query = cb.createQuery(EndorsementCriteriaRes.class);

				// Find All
				Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
				Root<EserviceMotorDetails> m = query.from(EserviceMotorDetails.class);
			
				
				Subquery<BigDecimal> endtPre = query.subquery(BigDecimal.class);
				Root<HomePositionMaster> h = endtPre.from(HomePositionMaster.class);
				endtPre.select(cb.sum(h.get("endtPremium") ) ) ;
				Predicate pm1 = cb.equal(h.get("companyId").as(String.class), m.get("companyId"));
				Predicate pm2 = cb.equal(h.get("productId").as(String.class), m.get("productId"));
				Predicate pm3   = cb.equal(h.get("policyNo"), m.get("policyNo"));
				endtPre.where(pm1,pm2,pm3);
				
				Subquery<BigDecimal> overAllPremiumLc = query.subquery(BigDecimal.class);
				Root<HomePositionMaster> ocpm2 = overAllPremiumLc.from(HomePositionMaster.class);
				overAllPremiumLc.select(cb.sum(ocpm2.get("overallPremiumLc")));
				Predicate a2 = cb.equal(m.get("quoteNo"),ocpm2.get("quoteNo") );
				overAllPremiumLc.where(a2);
				
				Subquery<BigDecimal> overAllPremiumFc = query.subquery(BigDecimal.class);
				Root<HomePositionMaster> ocpm1 = overAllPremiumFc.from(HomePositionMaster.class);
				overAllPremiumFc.select(cb.sum(ocpm1.get("overallPremiumFc")));
				Predicate a1 = cb.equal(m.get("quoteNo"),ocpm1.get("quoteNo") );
				overAllPremiumFc.where(a1);
				
				Subquery<Long> debitNoteNo = query.subquery(Long.class);
				Root<HomePositionMaster> h2 = debitNoteNo.from(HomePositionMaster.class);
				debitNoteNo.select(cb.max(h2.get("debitNoteNo"))) ;
				Predicate pm4 = cb.equal(h2.get("companyId"), m.get("companyId"));
				Predicate pm5 = cb.equal(h2.get("productId").as(String.class), m.get("productId"));
				Predicate pm6   = cb.equal(h2.get("policyNo"), m.get("policyNo"));
				debitNoteNo.where(pm4,pm5,pm6);
				
				
				Subquery<Long> creditNo = query.subquery(Long.class);
				Root<HomePositionMaster> h3 = creditNo.from(HomePositionMaster.class);
				creditNo.select(cb.max(h3.get("creditNo"))) ;
				Predicate pm7 = cb.equal(h3.get("companyId"), m.get("companyId"));
				Predicate pm8 = cb.equal(h3.get("productId").as(String.class), m.get("productId"));
				Predicate pm9   = cb.equal(h3.get("policyNo"), m.get("policyNo"));
				creditNo.where(pm7,pm8,pm9);
				
				Subquery<BigDecimal> endtPreTax = query.subquery(BigDecimal.class);
				Root<HomePositionMaster> h4 = endtPreTax.from(HomePositionMaster.class);
				endtPreTax.select(cb.sum(h4.get("endtPremiumTax") ) ) ;
				Predicate pm10 = cb.equal(h4.get("companyId"), m.get("companyId"));
				Predicate pm11 = cb.equal(h4.get("productId").as(String.class), m.get("productId"));
				Predicate pm12   = cb.equal(h4.get("policyNo"), m.get("policyNo"));
				endtPreTax.where(pm10,pm11,pm12);

// UW System ID
             Subquery<Long> uwsysId = query.subquery(Long.class);
             Root<HomePositionMaster> h5 = uwsysId.from(HomePositionMaster.class);
             uwsysId.select(cb.max(h5.get("uwsysId")));
             Predicate uw1 = cb.equal(h5.get("quoteNo"), cb.max(m.get("quoteNo"))); // Changed
             uwsysId.where(uw1);

// Policy Index
             Subquery<Long> polIdx = query.subquery(Long.class);
             Root<HomePositionMaster> h6 = polIdx.from(HomePositionMaster.class);
             polIdx.select(cb.max(h6.get("ahpolIdx")));
             Predicate pol1 = cb.equal(h6.get("quoteNo"), cb.max(m.get("quoteNo"))); // Changed
             polIdx.where(pol1);
				// Select
				query.multiselect(//cb.literal(Long.parseLong("1")).alias("idsCount"),
						// Customer Info
						cb.max(c.get("customerReferenceNo")).alias("customerReferenceNo"), cb.max(c.get("idNumber")).alias("idNumber"),
						cb.max(c.get("clientName")).alias("clientName"),
						// Vehicle Info
						cb.max(m.get("companyId")).alias("companyId"), cb.max(m.get("productId")).alias("productId"),
						cb.max(m.get("branchCode")).alias("branchCode"), cb.max(m.get("requestReferenceNo")).alias("requestReferenceNo"),
						cb.selectCase().when(cb.max(m.get("quoteNo")).isNotNull(), cb.max(m.get("quoteNo"))).otherwise(cb.max(m.get("quoteNo")))
								.alias("quoteNo"),
						cb.selectCase().when(cb.max(m.get("customerId")).isNotNull(), cb.max(m.get("customerId")))
								.otherwise(cb.max(m.get("customerId"))).alias("customerId"),
						cb.max(m.get("policyStartDate")).alias("policyStartDate"), cb.max(m.get("policyEndDate")).alias("policyEndDate"),
						cb.max(m.get("endorsementType")).alias("endorsementTypeId"),
						cb.max(m.get("endorsementTypeDesc")).alias("endorsementDesc"),
						cb.max(m.get("endtCategDesc")).alias("endorsementCategoryDesc"),
						cb.max(m.get("endorsementEffdate")).alias("effectiveDate"),
						cb.max(m.get("endtStatus")).alias("endorsementStatus"),
						cb.max(m.get("policyNo")).alias("policyNo"),
						cb.max(m.get("endorsementRemarks")).alias("endorsementRemarks"),
						cb.max(m.get("endorsementDate")).alias("endorsementDate"),
						//Home Position Master
						cb.max(overAllPremiumLc).alias("overallPremiumLc"), cb.max(overAllPremiumFc).alias("overallPremiumFc"),

						//cb.sum(h.get("overallPremiumLc")).alias("overallPremiumLc"), cb.sum(h.get("overallPremiumFc")).alias("overallPremiumFc"),
						cb.sum(endtPre,endtPreTax).alias("endtPremium"),cb.max( m.get("currency")).alias("currency"),
						debitNoteNo.alias("debitNoteNo") ,creditNo.alias("creditNo"),
                        uwsysId.alias("uwSysId"),
                        polIdx.alias("polIdx"),
						cb.max(m.get("emiYn")).alias("emiYn")
                        );
			 
				// Order By
//				List<Order> orderList = new ArrayList<Order>();
//				orderList.add(cb.desc(cb.max(m.get("endorsementDate"))));
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc((m.get("policyNo"))));

			
				// Where
				Predicate n1 = cb.equal(c.get("customerReferenceNo"), m.get("customerReferenceNo"));
				Predicate n2 = cb.equal(m.get("companyId"), request.getCompanyId());
				Predicate n3 = cb.equal(m.get("productId"), request.getProductId().toString());
		//		Predicate n4 = cb.notEqual(m.get("status"), "D");
				// Predicate n4 = cb.in(m.get("status")).value(Arrays.asList("E","P","D")); //
				// m.get("status").in("E","P"));
				Predicate n5 = cb.or(cb.like(m.get("originalPolicyNo"), request.getPolicyNo()),
						cb.like(m.get("policyNo"), request.getPolicyNo()));
				// Predicate n6 = cb.equal(m.get("riskId"), "1");
				// Predicate n7 = cb.like(h.get("quoteNo"), m.get("quoteNo"));
				// Predicate n8 = cb.like(m.get("PolicyNo"), request.getPolicyNo());
				// Predicate n5 = cb.lessThanOrEqualTo(m.get("updatedDate"), endDate);
				// Predicate n6 = cb.greaterThanOrEqualTo(m.get("updatedDate"), startDate);

			/*	Predicate n7 = null;
				if (req.getApplicationId().equalsIgnoreCase("1")) {
					n7 = cb.equal(m.get("loginId"), req.getLoginId());
				} else {
					n7 = cb.equal(m.get("applicationId"), req.getApplicationId());
				}*/

				/*Predicate n8 = null;
				if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
					Expression<String> e0 = m.get("brokerBranchCode");
					n8 = e0.in(branches);
				} else {
					Expression<String> e0 = m.get("branchCode");
					n8 = e0.in(branches);
				}*/
			
				query.where(n1, n2, n3, n5)
						/*.groupBy(c.get("customerReferenceNo"), c.get("idNumber"), c.get("clientName"), m.get("companyId"),
								m.get("productId"), m.get("branchCode"), m.get("requestReferenceNo"), m.get("quoteNo"),
								m.get("customerId"), m.get("policyStartDate"), m.get("policyEndDate"))*/
				
				.groupBy(/*m.get("overallPremiumLc"),m.get("overallPremiumFc"),*/m.get("policyNo"))
						.orderBy(orderList);

				// Get Result
				TypedQuery<EndorsementCriteriaRes> result = em.createQuery(query);
				////result.setFirstResult(500);
				//result.setMaxResults(500);
				  List<EndorsementCriteriaRes> grids = result.getResultList();
				  
				  return grids;
			
		 }catch (Exception e) {
			 e.printStackTrace();
		}
		return null;
	}
	public List<EndorsementCriteriaRes> endorsementGrid(Endorsment request) {

		 try {

			 
				// Get Datas
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<EndorsementCriteriaRes> query = cb.createQuery(EndorsementCriteriaRes.class);

				// Find All
				Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
				Root<EserviceMotorDetails> m = query.from(EserviceMotorDetails.class);

				// Select
				query.multiselect(//cb.literal(Long.parseLong("1")).alias("idsCount"),
						// Customer Info
						c.get("customerReferenceNo").alias("customerReferenceNo"), c.get("idNumber").alias("idNumber"),
						c.get("clientName").alias("clientName"),
						// Vehicle Info
						m.get("companyId").alias("companyId"), m.get("productId").alias("productId"),
						m.get("branchCode").alias("branchCode"), m.get("requestReferenceNo").alias("requestReferenceNo"),
						cb.selectCase().when(m.get("quoteNo").isNotNull(), m.get("quoteNo")).otherwise(m.get("quoteNo"))
								.alias("quoteNo"),
						cb.selectCase().when(m.get("customerId").isNotNull(), m.get("customerId"))
								.otherwise(m.get("customerId")).alias("customerId"),
						m.get("policyStartDate").alias("policyStartDate"), m.get("policyEndDate").alias("policyEndDate"),
						m.get("endorsementType").alias("endorsementTypeId"),
						m.get("endorsementTypeDesc").alias("endorsementDesc"),
						m.get("endtCategDesc").alias("endorsementCategoryDesc"),
						m.get("endorsementEffdate").alias("effectiveDate"),
						m.get("endtStatus").alias("endorsementStatus"),
						m.get("policyNo").alias("policyNo"),
						m.get("endorsementRemarks").alias("endorsementRemarks")
						
						);
			 
				// Order By
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(m.get("endorsementDate")));

				// Where
				Predicate n1 = cb.equal(c.get("customerReferenceNo"), m.get("customerReferenceNo"));
				Predicate n2 = cb.equal(m.get("companyId"), request.getCompanyId());
				Predicate n3 = cb.equal(m.get("productId"), request.getProductId());
				Predicate n4 = cb.in(m.get("status")).value(Arrays.asList("E","P"));  // m.get("status").in("E","P"));
				Predicate n5 = cb.like(m.get("originalPolicyNo"), request.getPolicyNo());
				//Predicate n5 = cb.lessThanOrEqualTo(m.get("updatedDate"), endDate);
				//Predicate n6 = cb.greaterThanOrEqualTo(m.get("updatedDate"), startDate);

			/*	Predicate n7 = null;
				if (req.getApplicationId().equalsIgnoreCase("1")) {
					n7 = cb.equal(m.get("loginId"), req.getLoginId());
				} else {
					n7 = cb.equal(m.get("applicationId"), req.getApplicationId());
				}*/

				/*Predicate n8 = null;
				if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
					Expression<String> e0 = m.get("brokerBranchCode");
					n8 = e0.in(branches);
				} else {
					Expression<String> e0 = m.get("branchCode");
					n8 = e0.in(branches);
				}*/

				query.where(n1, n2, n3, n4, n5 )
						/*.groupBy(c.get("customerReferenceNo"), c.get("idNumber"), c.get("clientName"), m.get("companyId"),
								m.get("productId"), m.get("branchCode"), m.get("requestReferenceNo"), m.get("quoteNo"),
								m.get("customerId"), m.get("policyStartDate"), m.get("policyEndDate"))*/
						.orderBy(orderList);

				// Get Result
				TypedQuery<EndorsementCriteriaRes> result = em.createQuery(query);
				////result.setFirstResult(500);
				//result.setMaxResults(500);
				  List<EndorsementCriteriaRes> grids = result.getResultList();
				  
				  return grids;
			
		 }catch (Exception e) {
			 e.printStackTrace();
		}
		return null;
	}
	
	@Autowired 
	private RatingFactorsUtil ratingutil;
	
	@Transactional
	public CommonRes createEndorsment(Endorsment request) {
		CommonRes cs=new CommonRes();
		try {
			HomePositionMaster hp = new HomePositionMaster();
			EndDeleteReq delreq=new EndDeleteReq();
			List<HomePositionMaster> listHp = new ArrayList<HomePositionMaster>();
			CompanyProductMaster product =  getCompanyProductMasterDropdown(request.getCompanyId() , request.getProductId().toString());
		//	HomePositionMaster hp = hpmrepo.findByPolicyNoAndStatusAndCompanyIdAndProductId(request.getPolicyNo(),"P", request.getCompanyId(), Integer.valueOf(request.getProductId().intValue()));
		    listHp = hpmrepo.findByOriginalPolicyNoAndCompanyIdAndProductIdOrderByPolicyNoDesc(request.getPolicyNo(),request.getCompanyId(), Integer.valueOf(request.getProductId().intValue()));
		    List<String> requestReferenceNos = listHp.stream().filter(t -> "P".equalsIgnoreCase(t.getEndtStatus()))
		        .map(HomePositionMaster::getRequestReferenceNo)
		        .collect(Collectors.toList());
		if(requestReferenceNos != null && !requestReferenceNos.isEmpty())
		{
			for(String ref : requestReferenceNos)
			{
				delreq.setRequestReferenceNo(ref);
				delreq.setCompanyid(request.getCompanyId());		
				editDeleteAlltable(delreq);
			}
			
		}
		List<HomePositionMaster> compeletedEndtlates = listHp.stream()
		        .filter(t -> "C".equalsIgnoreCase(t.getEndtStatus()))
		        .collect(Collectors.toList());
		compeletedEndtlates.sort(new Comparator<HomePositionMaster>() {
			@Override
			public int compare(HomePositionMaster o1, HomePositionMaster o2) {
				// TODO Auto-generated method stub
				return o1.getEndtCount().compareTo(o2.getEndtCount());
			}
		}.reversed());
		if(compeletedEndtlates!=null && !compeletedEndtlates.isEmpty())
		{
			hp=compeletedEndtlates.get(0);
		}
		else
		{
			 hp = hpmrepo.findByPolicyNoAndStatusAndCompanyIdAndProductId(request.getPolicyNo(),"P", request.getCompanyId(), Integer.valueOf(request.getProductId().intValue()));
		}
		
	    
				//		if(!((hp.getInceptionDate().compareTo(request.getEndtEffectiveDate()) * request.getEndtEffectiveDate().compareTo(hp.getExpiryDate()) ) >=0) ) {
//				CommonRes com=new CommonRes();
//				//com.setCommonResponse();
//				com.setErroCode(0);
//				com.setIsError(true);
//				com.setMessage("invalid Data ,Unable todo Endorsement");
//				return com;
//			}
				
			//EndtTypeMaster entTypeMaster = endtTypeRepo.findByCompanyIdAndProductIdAndStatusAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqual(request.getCompanyId(), request.getProductId().intValue(), "Y",Integer.parseInt(request.getEndtType()),new Date(), new Date());
			EndtTypeMaster entTypeMaster =ratingutil.getEndtMasterData(request.getCompanyId(),request.getProductId().toPlainString(), request.getEndtType());
			if("42".equals(request.getEndtType())) {
				CommonRes cancelPolicy = cancelPolicy(request);	
				return cancelPolicy;
			}else if ("1".equals(entTypeMaster.getEndtTypeCategoryId().toString()) ) { 
				// Non- Finacial
				Object response = null ;
				String policyNo = "" ;
				if(hp!=null) {
				CopyQuoteReq c = new CopyQuoteReq();
				c.setRequestReferenceNo(hp.getRequestReferenceNo());
				c.setLoginId(hp.getLoginId());
				c.setApplicationId(request.getApplicationId());
				c.setInsuranceId(request.getCompanyId());
				c.setBranchCode(request.getBranchCode());
				c.setProductId(String.valueOf(hp.getProductId()));
				c.setUserType(request.getUserType());
				c.setSubUserType(request.getSubUserType());
				c.setEndtTypeId(request.getEndtType());
				c.setLoginId(request.getLoginId()==null?hp.getLoginId():request.getLoginId());
				c.setTypeId("Endt");
				c.setQuoteNo(hp.getQuoteNo());
				c.setPolicyNo(request.getPolicyNo());
				c.setEndtRemarks(request.getEndtRemarks());
				c.setEndtEffectiveDate(request.getEndtEffectiveDate());

				if (product.getMotorYn().equalsIgnoreCase("M") ) {
					List<EserviceMotorDetails> copyQuote = new ArrayList<EserviceMotorDetails>();
					copyQuote.add((EserviceMotorDetails) copyquoteService.copyQuote(c).getCommonResponse());
					response = copyQuote;
					 policyNo=copyQuote.get(0).getEndtPrevPolicyNo();
					updateTermsAndCondition(hp , (copyQuote.size() > 0 ?  copyQuote.get(0).getRequestReferenceNo() : "" ));
					insertEMITransactionDetails(request, entTypeMaster, copyQuote.get(0).getRequestReferenceNo());
					
					insertEngineeringInfo(hp,copyQuote.get(0).getRequestReferenceNo());
				} else if (product.getMotorYn().equalsIgnoreCase("H")  &&  request.getProductId().equals(new BigDecimal(travelProductId))  ) {
					List<EserviceTravelDetails> travelCopyQuote = new ArrayList<EserviceTravelDetails>(); 
					travelCopyQuote.add((EserviceTravelDetails)copyquoteService.copyQuote(c).getCommonResponse());
					response = travelCopyQuote ;
					 policyNo=travelCopyQuote.get(0).getEndtPrevPolicyNo();
					updateTermsAndCondition(hp , (travelCopyQuote.size() > 0 ?  travelCopyQuote.get(0).getRequestReferenceNo() : "" ));
					insertEMITransactionDetails(request, entTypeMaster,travelCopyQuote.get(0).getRequestReferenceNo());
				//	updateHomePosition(travelCopyQuote.get(0).getRequestReferenceNo(),hp,request);
					insertEngineeringInfo(hp,travelCopyQuote.get(0).getRequestReferenceNo());
					
					
				}else if (product.getMotorYn().equalsIgnoreCase("A") ) {
					List<EserviceBuildingDetails> buildcopyquote = new ArrayList<EserviceBuildingDetails>(); 
					buildcopyquote.add((EserviceBuildingDetails) copyquoteService.copyQuote(c).getCommonResponse());
					response = buildcopyquote ;
					 policyNo=buildcopyquote.get(0).getEndtPrevPolicyNo();
					updateTermsAndCondition(hp , (buildcopyquote.size() > 0 ?  buildcopyquote.get(0).getRequestReferenceNo() : "" ));
					insertEMITransactionDetails(request, entTypeMaster, buildcopyquote.get(0).getRequestReferenceNo());
				//	updateHomePoonesition(buildcopyquote.get(0).getRequestReferenceNo(),hp,request);
					insertEngineeringInfo(hp, buildcopyquote.get(0).getRequestReferenceNo());
					insertAv(hp, buildcopyquote.get(0).getRequestReferenceNo());
					insertMA(hp, buildcopyquote.get(0).getRequestReferenceNo());
				} else {
					List<EserviceCommonDetails> commonCopyQuote = new ArrayList<EserviceCommonDetails>();
					commonCopyQuote.add((EserviceCommonDetails) copyquoteService.copyQuote(c).getCommonResponse());
					response = commonCopyQuote ;
					 policyNo=commonCopyQuote.get(0).getEndtPrevPolicyNo();
					updateTermsAndCondition(hp , (commonCopyQuote.size() > 0 ?  commonCopyQuote.get(0).getRequestReferenceNo() : "" ));
					insertEMITransactionDetails(request, entTypeMaster, commonCopyQuote.get(0).getRequestReferenceNo());
					insertEngineeringInfo(hp,commonCopyQuote.get(0).getRequestReferenceNo());
				//	updateHomePosition(commonCopyQuote.get(0).getRequestReferenceNo(),hp,request);
				
				}
				
				if(StringUtils.isNotBlank(policyNo)) {
					copycover.copy(policyNo);
				}
				
				CommonRes com=new CommonRes();
				com.setCommonResponse(response);
				com.setErroCode(0);
				com.setIsError(false);
				com.setMessage("Success");
				return com;
				}
			}else {//FInancial
				Object response = null ;
				String policyNo=null;
				if(product.getMotorYn().equalsIgnoreCase("M")) {
					List<EserviceMotorDetails> motorRaw = copyraw.copyMotorRaw(request,entTypeMaster);
					policyNo=motorRaw.get(0).getEndtPrevPolicyNo();
					response = motorRaw ;
					updateTermsAndCondition(hp , (motorRaw.size() > 0 ?  motorRaw.get(0).getRequestReferenceNo() : "" ));
					insertEngineeringInfo(hp,motorRaw.get(0).getRequestReferenceNo());
					insertOneTableInsert(motorRaw.get(0).getRequestReferenceNo() , product.getMotorYn(),request.getProductId(),request.getEndtType());
					insertEMITransactionDetails(request, entTypeMaster,motorRaw.get(0).getRequestReferenceNo());
					updateHomePosition(motorRaw.get(0).getRequestReferenceNo(),hp,request);
					cs.setRequestRefNo(motorRaw.get(0).getRequestReferenceNo());	
				} else if (product.getMotorYn().equalsIgnoreCase("H")  && request.getProductId().equals(new BigDecimal(travelProductId))  ) {
					List<EserviceTravelDetails> travelRaw = new ArrayList<EserviceTravelDetails>(); 
					travelRaw.add(copyTravelraw.copyTravelRaw(request));
					policyNo=travelRaw.get(0).getEndtPrevPolicyNo();
					response = travelRaw ;
					updateTermsAndCondition(hp , (travelRaw.size() > 0 ?  travelRaw.get(0).getRequestReferenceNo() : "" ));
					insertEMITransactionDetails(request, entTypeMaster, travelRaw.get(0).getRequestReferenceNo());
					updateHomePosition(travelRaw.get(0).getRequestReferenceNo(),hp,request);
					cs.setRequestRefNo(travelRaw.get(0).getRequestReferenceNo());	
				} else if (product.getMotorYn().equalsIgnoreCase("A") ) {
					List<EserviceBuildingDetails> buildRaw = new ArrayList<EserviceBuildingDetails>(); 
					buildRaw.add( copyBuildingraw.copyBuildingRaw(request));
					policyNo=buildRaw.get(0).getEndtPrevPolicyNo();
							
					response = buildRaw ;
					insertOneTableInsert(buildRaw.get(0).getRequestReferenceNo() , product.getMotorYn(),request.getProductId(),request.getEndtType());
					updateTermsAndCondition(hp , (buildRaw.size() > 0 ?  buildRaw.get(0).getRequestReferenceNo() : "" ));
					insertEngineeringInfo(hp,buildRaw.get(0).getRequestReferenceNo());
					insertAv(hp,buildRaw.get(0).getRequestReferenceNo());
					insertMA(hp, buildRaw.get(0).getRequestReferenceNo());
					insertEMITransactionDetails(request, entTypeMaster,buildRaw.get(0).getRequestReferenceNo());
					updateHomePosition(buildRaw.get(0).getRequestReferenceNo(),hp,request);
					cs.setRequestRefNo(buildRaw.get(0).getRequestReferenceNo());			
					} else {
					List<EserviceCommonDetails> commonRaw = new ArrayList<EserviceCommonDetails>();
					 commonRaw.add(copyCommonraw.copyCommonRaw(request));
					 policyNo=commonRaw.get(0).getEndtPrevPolicyNo();
					response = commonRaw ;
					updateTermsAndCondition(hp , (commonRaw.size() > 0 ?  commonRaw.get(0).getRequestReferenceNo() : "" ) );
					insertEngineeringInfo(hp,commonRaw.get(0).getRequestReferenceNo());
					insertEMITransactionDetails(request, entTypeMaster,commonRaw.get(0).getRequestReferenceNo());
					updateHomePosition(commonRaw.get(0).getRequestReferenceNo(),hp,request);
					cs.setRequestRefNo(commonRaw.get(0).getRequestReferenceNo());	
				}
				
				if(StringUtils.isNotBlank(policyNo)) {
					copycover.copy(policyNo);
				}
				
				
				
				cs.setCommonResponse(response);
				cs.setErroCode(0);
				cs.setIsError(false);
				cs.setMessage("Success");
				return cs;
			}
			
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private void insertOneTableInsert(String requestReferenceNo, String yn, BigDecimal productId, String endtType) {
		try {
			if ("842".equalsIgnoreCase(endtType)) {
				if (yn.equalsIgnoreCase("M")) {
					motorOneTimeInsert(requestReferenceNo);
				} else if (yn.equalsIgnoreCase("H") && productId.equals(new BigDecimal(travelProductId))) {

				} else {
					buildingOneTimeInsert(requestReferenceNo);
				}

			}

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	private void buildingOneTimeInsert(String requestReferenceNo) {
		try
		{
			List<EserviceBuildingDetails> eBlist = eserBuldingRepo.findByRequestReferenceNo(requestReferenceNo);
			if (eBlist != null && !eBlist.isEmpty()) {
				for (EserviceBuildingDetails eserBuild : eBlist) {
					MsAssetDetails saveAsset = new MsAssetDetails();
					dozerBeanMapper.map(eserBuild, saveAsset);
					saveAsset.setVdRefno(Long.valueOf(eserBuild.getVdRefno()));	
					//Location Id
					saveAsset.setLocationId(eserBuild.getLocationId()==null?1:eserBuild.getLocationId());
					//Risk Id
					saveAsset.setRiskId(eserBuild.getRiskId());
					saveAsset.setEntryDate(new Date());
					saveAsset.setStatus(eserBuild.getStatus());
					saveAsset.setGroupCount(1);
					saveAsset.setPeriodOfInsurance(String.valueOf(eserBuild.getPolicyPeriord()));
					saveAsset.setExchangeRate(eserBuild.getExchangeRate());
					saveAsset.setCurrency(eserBuild.getCurrency());
					saveAsset.setRequestReferenceNo(eserBuild.getRequestReferenceNo());
					saveAsset.setSectionId(Integer.valueOf(eserBuild.getSectionId()));
					saveAsset.setEndtTypeId(eserBuild.getEndorsementType()==null?0 :eserBuild.getEndorsementType() ) ;
					saveAsset.setEndtCategoryId(eserBuild.getIsFinyn()==null?"N" :eserBuild.getIsFinyn() );
					saveAsset.setIndustryId(eserBuild.getIndustryId() );
					saveAsset.setBuildingAge(eserBuild.getBuildingAge() == null ? null : eserBuild.getBuildingAge());
					saveAsset.setCompanyId(eserBuild.getCompanyId());
					saveAsset.setBranchCode(eserBuild.getBranchCode());
					saveAsset.setProductId(eserBuild.getProductId()==null?null : Integer.valueOf(eserBuild.getProductId()));
					saveAsset.setCreatedBy(eserBuild.getCreatedBy());
					saveAsset.setPromocode(eserBuild.getPromocode());
					saveAsset.setHavepromocode(eserBuild.getHavepromocode());
					saveAsset.setInsuranceClass("99999");
					saveAsset.setWallType(eserBuild.getWallType());
					saveAsset.setRoofType(eserBuild.getRoofType());
					saveAsset.setFirstLossPercent(eserBuild.getFirstLossPercent()==null?null:Long.valueOf(eserBuild.getFirstLossPercent()));
					saveAsset.setInternalWallType(eserBuild.getInternalWallType()==null?null:eserBuild.getInternalWallType());
					saveAsset.setSumInsured(eserBuild.getSumInsured()==null?null:eserBuild.getSumInsured());
					saveAsset.setSumInsuredLc(eserBuild.getSumInsuredLc()==null?null:eserBuild.getSumInsuredLc());
					saveAsset.setIndemityPeriod(StringUtils.isBlank(eserBuild.getIndemityPeriod())?"0":eserBuild.getIndemityPeriod());
					saveAsset.setCoverId(eserBuild.getCoverId()!=null?eserBuild.getCoverId():0);
					saveAsset.setNoOfClaim(eserBuild.getNoOfClaim()!=null?eserBuild.getNoOfClaim():0);
					saveAsset.setParam1(StringUtils.isBlank(eserBuild.getParam1())?"":eserBuild.getParam1());
					saveAsset.setParam2(StringUtils.isBlank(eserBuild.getParam2())?"":eserBuild.getParam2());
					saveAsset.setParam3(StringUtils.isBlank(eserBuild.getParam3())?"":eserBuild.getParam3());
					saveAsset.setParam4(StringUtils.isBlank(eserBuild.getParam4())?"":eserBuild.getParam4());
					saveAsset.setParam5(StringUtils.isBlank(eserBuild.getParam5())?"":eserBuild.getParam5());
					saveAsset.setParam6(StringUtils.isBlank(eserBuild.getParam6())?"":eserBuild.getParam6());
					saveAsset.setParam7(StringUtils.isBlank(eserBuild.getParam7())?"":eserBuild.getParam7());
					saveAsset.setParam8(StringUtils.isBlank(eserBuild.getParam8())?"":eserBuild.getParam8());
					saveAsset.setParam9(StringUtils.isBlank(eserBuild.getParam9())?"":eserBuild.getParam9());
					saveAsset.setParam10(StringUtils.isBlank(eserBuild.getParam10())?"":eserBuild.getParam10());
					saveAsset.setParam11(StringUtils.isBlank(eserBuild.getParam11())?"":eserBuild.getParam11());
					saveAsset.setParam12(StringUtils.isBlank(eserBuild.getParam12())?"":eserBuild.getParam12());
					saveAsset.setParam13(StringUtils.isBlank(eserBuild.getParam13())?"":eserBuild.getParam13());
					saveAsset.setParam14(StringUtils.isBlank(eserBuild.getParam14())?"":eserBuild.getParam14());
					saveAsset.setParam15(StringUtils.isBlank(eserBuild.getParam14())?"":eserBuild.getParam15());
					assertRepo.saveAndFlush(saveAsset);
					MsCommonDetails saveData = new MsCommonDetails();
					
					saveData.setAgencyCode(eserBuild.getAgencyCode());
					saveData.setBranchCode(eserBuild.getBranchCode());
					saveData.setInsuranceId(eserBuild.getCompanyId());
					saveData.setProductId(Integer.valueOf(eserBuild.getProductId()));
					saveData.setRequestreferenceno(requestReferenceNo);	    
					saveData.setCdRefno(Long.valueOf(eserBuild.getCdRefno()));
					saveData.setEntryDate(new Date());
					saveData.setVdRefno(Long.valueOf(eserBuild.getVdRefno()));
					saveData.setMsRefno(Long.valueOf(eserBuild.getMsRefno()));
					saveData.setDdRefno(null);
					saveData.setStatus("Y");
					saveData.setSectionId(Integer.valueOf(eserBuild.getSectionId()));
					saveData.setLocationId(Integer.valueOf(eserBuild.getLocationId()));					
					saveData.setParam1(StringUtils.isBlank(eserBuild.getParam1())?"":eserBuild.getParam1());
					saveData.setParam2(StringUtils.isBlank(eserBuild.getParam2())?"":eserBuild.getParam2());
					saveData.setParam3(StringUtils.isBlank(eserBuild.getParam3())?"":eserBuild.getParam3());
					saveData.setParam4(StringUtils.isBlank(eserBuild.getParam4())?"":eserBuild.getParam4());
					saveData.setParam5(StringUtils.isBlank(eserBuild.getParam5())?"":eserBuild.getParam5());
					saveData.setParam6(StringUtils.isBlank(eserBuild.getParam6())?"":eserBuild.getParam6());
					saveData.setParam7(StringUtils.isBlank(eserBuild.getParam7())?"":eserBuild.getParam7());
					saveData.setParam8(StringUtils.isBlank(eserBuild.getParam8())?"":eserBuild.getParam8());
					saveData.setParam9(StringUtils.isBlank(eserBuild.getParam9())?"":eserBuild.getParam9());
					saveData.setParam10(StringUtils.isBlank(eserBuild.getParam10())?"":eserBuild.getParam10());
					msCommonRepo.saveAndFlush(saveData);
					
				}
			}else
			{
				List<EserviceCommonDetails> eClist = eserCommonRepo.findByRequestReferenceNo(requestReferenceNo);
				if (eClist != null && !eClist.isEmpty()) {
					for (EserviceCommonDetails commonData : eClist) {
						MsHumanDetails saveHuman = new MsHumanDetails();
						dozerBeanMapper.map(commonData, saveHuman);
						saveHuman.setHumanId(commonData.getRiskId() );

						saveHuman.setVdRefno(Long.valueOf(commonData.getVdRefNo()));
						saveHuman.setEntryDate(new Date());
						saveHuman.setGroupId(commonData.getGroupId());						
						saveHuman.setSumInsured(commonData.getSumInsured());
						
						saveHuman.setGroupCount(Integer.valueOf(1));
						saveHuman.setPeriodOfInsurance(String.valueOf(commonData.getPolicyPeriod()));
						saveHuman.setCategoryId(commonData.getCategoryId());
						saveHuman.setEndtTypeId(commonData.getEndorsementType()==null?0 :commonData.getEndorsementType() ) ;
						saveHuman.setEndtCategoryId(commonData.getIsFinaceYn()==null?"N" :commonData.getIsFinaceYn() );
						saveHuman.setNatureOfBusinessId(commonData.getNatureOfBusinessId());
						saveHuman.setTotalNoOfEmployees(commonData.getTotalNoOfEmployees());
						saveHuman.setFidEmpCount(commonData.getFidEmpCount()==null?BigDecimal.ZERO:new BigDecimal(commonData.getFidEmpCount().toString()));
						saveHuman.setTotalExcludedEmployees(commonData.getTotalExcludedEmployees());
						saveHuman.setTotalRejoinedEmployees(commonData.getTotalRejoinedEmployees());
						saveHuman.setAccountOutstandingEmployees(commonData.getAccountOutstandingEmployees());
						saveHuman.setAccountAuditentType(commonData.getAccountAuditentType());
						saveHuman.setEndtTypeId(commonData.getEndorsementType()==null?0 :commonData.getEndorsementType() ) ;
						saveHuman.setEndtCategoryId(commonData.getIsFinaceYn()==null?"N" :commonData.getIsFinaceYn());
						saveHuman.setUwLoading(BigDecimal.ZERO);
						if(commonData.getCompanyId().equals("100020") && commonData.getProductId().equals("60")) {
						saveHuman.setPersonalLiabilityOccupation(commonData.getOccupationType());
						}
						saveHuman.setInsuranceClass("99999");
						saveHuman.setAge(commonData.getAge());
						saveHuman.setRatingRelationId(commonData.getRelationType());
						saveHuman.setProfessionalType(commonData.getProfessionaltype());
						saveHuman.setIndemnityType(commonData.getIndemnitytype());
						saveHuman.setParam1(StringUtils.isBlank(commonData.getParam1())?"":commonData.getParam1());
						saveHuman.setParam2(StringUtils.isBlank(commonData.getParam2())?"":commonData.getParam2());
						saveHuman.setParam3(StringUtils.isBlank(commonData.getParam3())?"":commonData.getParam3());
						saveHuman.setParam4(StringUtils.isBlank(commonData.getParam4())?"":commonData.getParam4());
						saveHuman.setParam5(StringUtils.isBlank(commonData.getParam5())?"":commonData.getParam5());
						saveHuman.setParam6(StringUtils.isBlank(commonData.getParam6())?"":commonData.getParam6());
						saveHuman.setParam7(StringUtils.isBlank(commonData.getParam7())?"":commonData.getParam7());
						saveHuman.setParam8(StringUtils.isBlank(commonData.getParam8())?"":commonData.getParam8());
						saveHuman.setParam9(StringUtils.isBlank(commonData.getParam9())?"":commonData.getParam9());
						saveHuman.setParam10(StringUtils.isBlank(commonData.getParam10())?"":commonData.getParam10());
						saveHuman.setSectionid(StringUtils.isBlank(commonData.getSectionId())?0 :Integer.valueOf(commonData.getSectionId()));

						if (commonData.getCompanyId()!=null && commonData.getCompanyId().equalsIgnoreCase("100020")  ) {
							if(commonData.getSectionId()!=null && commonData.getSectionId().equalsIgnoreCase("120") ) {
								saveHuman.setGroupCount(commonData.getCount());
							}
							
							 if(commonData.getSectionId().equals("45")) {
			                    	
			                    saveHuman.setGroupCount(commonData.getCount());           
			                  }
						}

	                    saveHuman.setGroupId(commonData.getGroupId() != null ? commonData.getGroupId() : 0);
						saveHuman.setLocationId(commonData.getLocationId()==null?1:commonData.getLocationId());

						saveHuman.setCount(commonData.getCount()==null?null:commonData.getCount());
						saveHuman.setIndemnityType(commonData.getIndemnitytype()==null?null:commonData.getIndemnitytype());
						saveHuman.setCoverId(commonData.getCoverId()!=null?commonData.getCoverId():0);
						mshummanRepo.saveAndFlush(saveHuman);
						MsCommonDetails saveData = new MsCommonDetails();
						saveData.setAgencyCode(commonData.getAgencyCode());
						saveData.setBranchCode(commonData.getBranchCode());
						saveData.setInsuranceId(commonData.getCompanyId());
						saveData.setProductId(Integer.valueOf(commonData.getProductId()));
						saveData.setRequestreferenceno(requestReferenceNo);	    
						saveData.setCdRefno(Long.valueOf(commonData.getCdRefno()));
						saveData.setEntryDate(new Date());
						saveData.setVdRefno(Long.valueOf(commonData.getVdRefNo()));
						saveData.setMsRefno(Long.valueOf(commonData.getMsRefno()));
						saveData.setDdRefno(null);
						saveData.setStatus("Y");
						saveData.setSectionId(Integer.valueOf(commonData.getSectionId()));
						saveData.setLocationId(Integer.valueOf(commonData.getLocationId()));					
						saveData.setParam1(StringUtils.isBlank(commonData.getParam1())?"":commonData.getParam1());
						saveData.setParam2(StringUtils.isBlank(commonData.getParam2())?"":commonData.getParam2());
						saveData.setParam3(StringUtils.isBlank(commonData.getParam3())?"":commonData.getParam3());
						saveData.setParam4(StringUtils.isBlank(commonData.getParam4())?"":commonData.getParam4());
						saveData.setParam5(StringUtils.isBlank(commonData.getParam5())?"":commonData.getParam5());
						saveData.setParam6(StringUtils.isBlank(commonData.getParam6())?"":commonData.getParam6());
						saveData.setParam7(StringUtils.isBlank(commonData.getParam7())?"":commonData.getParam7());
						saveData.setParam8(StringUtils.isBlank(commonData.getParam8())?"":commonData.getParam8());
						saveData.setParam9(StringUtils.isBlank(commonData.getParam9())?"":commonData.getParam9());
						saveData.setParam10(StringUtils.isBlank(commonData.getParam10())?"":commonData.getParam10());
						msCommonRepo.saveAndFlush(saveData);
						
					}
					
				}
			
				
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		
	}
	private void motorOneTimeInsert(String requestReferenceNo) {
		try {
			List<EserviceMotorDetails> eMlist = emotorRepo.findByRequestReferenceNo(requestReferenceNo);
			if (eMlist != null && !eMlist.isEmpty()) {
				for (EserviceMotorDetails motorDatas : eMlist) {
					MsVehicleDetails saveVehicle = new MsVehicleDetails();
					dozerBeanMapper.map(motorDatas, saveVehicle);
					saveVehicle.setRequestReferenceNo(requestReferenceNo);					
					saveVehicle.setVehicleId(motorDatas.getRiskId().toString());
					saveVehicle.setLocationId(1);
					saveVehicle.setAccidentYn(StringUtils.isBlank(motorDatas.getAccident())?"N":motorDatas.getAccident());
					saveVehicle.setVdRefno(Long.valueOf(motorDatas.getVdRefNo()));
					saveVehicle.setEntryDate(new Date());
					saveVehicle.setStatus(motorDatas.getStatus());
					saveVehicle.setNcdyears(motorDatas.getNcdYears());
					saveVehicle.setInsuranceClass(motorDatas.getInsuranceClass());
					saveVehicle.setInsuranceType(motorDatas.getInsuranceType());
					saveVehicle.setNcdYn(motorDatas.getNcdYn());
					saveVehicle.setNoOfClaims(motorDatas.getNoOfClaims()==null?0 :motorDatas.getNoOfClaims());
					saveVehicle.setNoOfCompehensives(motorDatas.getNoOfCompehensives()==null?0 :motorDatas.getNoOfCompehensives());
					saveVehicle.setNoOfVehicles(motorDatas.getNoOfVehicles()==null?0 :motorDatas.getNoOfVehicles());
					saveVehicle.setNcdyears(motorDatas.getNcdYears()==null?0 :motorDatas.getNcdYears());
					saveVehicle.setOwnerCategory(motorDatas.getOwnerCategory()==null?"0":motorDatas.getOwnerCategory());
					saveVehicle.setRegistrationYear(motorDatas.getRegistrationYear());
					saveVehicle.setVehicleBodyType(motorDatas.getVehicleType());
					saveVehicle.setTpdIncreaseLimit(motorDatas.getTppdIncreaeLimit());
					saveVehicle.setWindScreenCoverRequeired(motorDatas.getWindScreenCoverRequired());
					saveVehicle.setMotorUsage(motorDatas.getMotorUsage());
					saveVehicle.setGpsyn(motorDatas.getGpsTrackingInstalled());
					saveVehicle.setGroupCount(1);
					saveVehicle.setCityLimit(StringUtils.isBlank(motorDatas.getCityLimit())?"0": motorDatas.getCityLimit());
					saveVehicle.setWindScreenSumInsuredLc(motorDatas.getWindScreenSumInsuredLc());
					saveVehicle.setAcccessoriesSumInsuredLc(motorDatas.getAcccessoriesSumInsuredLc());
					saveVehicle.setWindScreenSumInsured(motorDatas.getWindScreenSumInsured());
					saveVehicle.setAcccessoriesSumInsured(motorDatas.getAcccessoriesSumInsured());
					saveVehicle.setHavepromocode(motorDatas.getHavepromocode());
					saveVehicle.setPromocode(StringUtils.isBlank(motorDatas.getPromocode())?"": motorDatas.getPromocode());
					saveVehicle.setEndtTypeId(motorDatas.getEndorsementType()==null?0 :motorDatas.getEndorsementType() ) ;
					saveVehicle.setEndtCategoryId(motorDatas.getIsFinaceYn()==null?"N" :motorDatas.getIsFinaceYn() );
					saveVehicle.setTpdIncreaseLimit(motorDatas.getTppdIncreaeLimit());
					saveVehicle.setTppdIncreaeLimitLc(motorDatas.getTppdIncreaeLimitLc());
					saveVehicle.setCarAlarmYn(StringUtils.isNotBlank(motorDatas.getCarAlarmYn()) ? motorDatas.getCarAlarmYn() :"N") ;
					saveVehicle.setUwLoading(BigDecimal.ZERO);
					saveVehicle.setExcess(motorDatas.getExcess());
					saveVehicle.setVehicleWeight(motorDatas.getGrossWeight());
					saveVehicle.setVehicleValueType(StringUtils.isBlank(motorDatas.getVehicleValueType())? "":motorDatas.getVehicleValueType());
					saveVehicle.setInflation(StringUtils.isBlank(motorDatas.getInflation())? "":motorDatas.getInflation());
					saveVehicle.setNcb(motorDatas.getNcb()==null? "":motorDatas.getNcb());
					saveVehicle.setDefenceValue(StringUtils.isBlank(motorDatas.getDefenceValue())? "":motorDatas.getDefenceValue());
					saveVehicle.setRegistrationDate(motorDatas.getRegistrationDate()==null?null:motorDatas.getRegistrationDate());
					saveVehicle.setPurchaseDate(motorDatas.getPurchaseDate()==null?null:motorDatas.getPurchaseDate());
					saveVehicle.setNcdyears(motorDatas.getNcdYears()==null?0:motorDatas.getNcdYears());
					saveVehicle.setCustRenewalYn(motorDatas.getCustRenewalYn()==null?"N":motorDatas.getCustRenewalYn());
					saveVehicle.setZone(motorDatas.getZone()==null?null:motorDatas.getZone());
					saveVehicle.setHorsePower(motorDatas.getHorsePower()==null?null:motorDatas.getHorsePower());
					saveVehicle.setPaCoverId(motorDatas.getPaCoverId()==null?null:motorDatas.getPaCoverId());
	                saveVehicle.setNoOfPassengers(motorDatas.getNoOfPassengers()==null?null:motorDatas.getNoOfPassengers());
	                saveVehicle.setCoverId(motorDatas.getCoverId()!=null?motorDatas.getCoverId():0);
	                saveVehicle.setCreditShortfallSi(motorDatas.getCreditShortfallSi());
	                saveVehicle.setImportYN(motorDatas.getImportYN());
	                msVehicleRepo.saveAndFlush(saveVehicle);
	                
	                MsCommonDetails saveData = new MsCommonDetails();
	                saveData.setRequestreferenceno(requestReferenceNo);	                saveData.setAgencyCode(motorDatas.getAgencyCode());					
	                saveData.setBranchCode(motorDatas.getBranchCode());
					saveData.setInsuranceId(motorDatas.getCompanyId());
					saveData.setProductId(Integer.valueOf(motorDatas.getProductId()));
					saveData.setRequestreferenceno(requestReferenceNo);
					saveData.setCdRefno(Long.valueOf(motorDatas.getCdRefno()));
					saveData.setEntryDate(new Date());
					saveData.setVdRefno(Long.valueOf(motorDatas.getVdRefNo()));
					saveData.setMsRefno(Long.valueOf(motorDatas.getMsRefno()));
					saveData.setDdRefno(null);
					saveData.setStatus("Y");
					saveData.setSectionId(Integer.valueOf(motorDatas.getSectionId()));
					saveData.setLocationId(1);		
					msCommonRepo.saveAndFlush(saveData);
					motorDatas.setStatus("D");
					emotorRepo.save(motorDatas);
					
				}
			}else
			{
				
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	private void updateHomePosition(String ref, HomePositionMaster hp, Endorsment req) {
	//	DozerBeanMapper dozerMapper = new DozerBeanMapper(); 
		try {
			Long applicationNo = hp.getApplicationNo();		
			HomePositionMaster home = new HomePositionMaster();
			dozerBeanMapper.map(hp,home);
			home.setRequestReferenceNo(ref);
			home.setEmiinstallYn(hp.getEmiinstallYn());
			home.setPolicyNo(null);
			home.setEmiYn(hp.getEmiYn());
			home.setApplicationId(req.getApplicationId());		
			home.setQuoteNo(null);
			home.setNoOfVehicles(hp.getNoOfVehicles());
			home.setAgencyCode(hp.getAgencyCode());
			home.setProductId(hp.getProductId());	
			home.setSectionId(hp.getSectionId());
			home.setCustomerName(hp.getCustomerName());
			home.setLoginId(hp.getLoginId());
			home.setDebitAcNo(null);
			home.setDebitTo(null);
			home.setDebitNoteNo(null);
			home.setCreditTo(null);
			home.setCreditToId(null);
			home.setCreditNo(null);
			home.setCreditDate(null);
			home.setReceiptNo(null);
			home.setReceiptDate(null);
			home.setPaymentMode(null);
			home.setPaymentStatus(null);
			home.setDebitNoteDate(null);
			home.setEntryDate(new Date(	));
			home.setAmendId(0);	
			home.setStatus("E");
			home.setEndtStatus("P");		
			home.setCoreIntgStatus(null);
			home.setIntegrationError(null);
			home.setIntegrationStatus(null);
			home.setApplicationNo(applicationNo);
			home.setCustomerId(hp.getCustomerId());
			home.setCompanyId(hp.getCompanyId());
			hpmrepo.saveAndFlush(home);
		} catch (Exception e) {
			e.printStackTrace();
		}

	}
	private void insertMA(HomePositionMaster hp, String requestReferenceNo) {
		DozerBeanMapper dozerMapper = new DozerBeanMapper(); 
		try {
			List<MarineHullInfo> engInfo = marineRepo.findByRequestReferenceNo(hp.getRequestReferenceNo());
			if(engInfo!=null)
			{
				List<MarineHullInfo> info = engInfo.stream()
					    .map(e -> {
					    	MarineHullInfo en = new MarineHullInfo();
					        dozerMapper.map(e,en);
					        en.setRequestReferenceNo(requestReferenceNo);
					        return en;
					    })
					    .collect(Collectors.toList());	
				marineRepo.saveAll(info);
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			log.error("Exception insertMH ---> " + e.getMessage());
			
		}
	}
	
	private void insertAv(HomePositionMaster hp, String requestReferenceNo) {
		DozerBeanMapper dozerMapper = new DozerBeanMapper(); 
		try {
			List<AviationInfo> engInfo=aviRepo.findByRequestReferenceNo(hp.getRequestReferenceNo());
			if(engInfo!=null)
			{
				List<AviationInfo> info = engInfo.stream()
					    .map(e -> {
					    	AviationInfo en = new AviationInfo();
					        dozerMapper.map(e,en);
					        en.setRequestReferenceNo(requestReferenceNo);
					        return en;
					    })
					    .collect(Collectors.toList());	
				aviRepo.saveAll(info);
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			log.error("Exception insertAv ---> " + e.getMessage());
			
		}
	}
	
	private void insertEngineeringInfo(HomePositionMaster hp, String requestReferenceNo) {
		DozerBeanMapper dozerMapper = new DozerBeanMapper(); 
		try {
			List<EngineerInfo> engInfo=engRepo.findByRequestReferenceNo(hp.getRequestReferenceNo());
			if(engInfo!=null)
			{
				List<EngineerInfo> info = engInfo.stream()
					    .map(e -> {
					        EngineerInfo en = new EngineerInfo();
					        dozerMapper.map(e,en);
					        en.setRequestReferenceNo(requestReferenceNo);
					        return en;
					    })
					    .collect(Collectors.toList());	
				engRepo.saveAll(info);
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			log.error("Exception insertEngeeringInfo ---> " + e.getMessage());
			
		}
		
	}
	
	private List<EmiTransactionDetails> insertEMITransactionDetails(Endorsment req, EndtTypeMaster entTypeMaster,
			String requestReferenceNo) {
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		List<EmiTransactionDetails> saveList = new ArrayList<>();
		try {
			HomePositionMaster hp = hpmrepo.findTopByOriginalPolicyNoAndQuoteNoIsNotNullOrderByPolicyNoDesc(req.getPolicyNo());
			List<EmiTransactionDetails> emiData = emiTransactionDetailsRepo.findByQuoteNo(hp.getQuoteNo());

			if (emiData.size() > 0) {
				List<EmiTransactionDetails> prevemiList = new ArrayList<>();
				emiData.stream().forEach(f -> {
					if (f.getPaymentStatus().equalsIgnoreCase("Pending")) {
						f.setStatus("N");
						prevemiList.add(f);
					}
					if (prevemiList != null && prevemiList.isEmpty()) {
						emiTransactionDetailsRepo.saveAllAndFlush(prevemiList);
					}
				});
				
				for (EmiTransactionDetails data : emiData) {
					EmiTransactionDetails savedata = new EmiTransactionDetails();
					savedata = dozerMapper.map(data, EmiTransactionDetails.class);
					savedata.setRequestReferenceNo(requestReferenceNo);
					savedata.setQuoteNo(null);
					savedata.setEntryDate(new Date());
					savedata.setCreatedBy(req.getApplicationId());
					savedata.setOriginalPolicyNo(req.getOriginalPolicyNo());
					savedata.setEndorsementRemarks(req.getEndtRemarks());
					savedata.setEndorsementEffdate(req.getEndtEffectiveDate());
					savedata.setEndtPrevPolicyNo(
							hp.getEndtPrevPolicyNo() != null ? hp.getEndtPrevPolicyNo() : req.getOriginalPolicyNo());
					savedata.setEndtPrevQuoteNo(
							hp.getEndtPrevQuoteNo() != null ? hp.getEndtPrevQuoteNo() : hp.getQuoteNo());
					savedata.setEndtCount(Integer.valueOf(hp.getEndtCount().toString()));
					savedata.setEndtStatus("P");
					savedata.setEndtCategDesc(entTypeMaster.getEndtShortDesc());
					savedata.setEndtTypeId(req.getEndtType());
					savedata.setEndtTypeDesc(entTypeMaster.getEndtTypeDesc());
					savedata.setStatus("E");
					saveList.add(savedata);
				}
				emiTransactionDetailsRepo.saveAllAndFlush(saveList);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return saveList;
	}
	
	private void updateTermsAndCondition(HomePositionMaster hp , String newRefNo ) {
		DozerBeanMapper dozerMapper = new DozerBeanMapper();  
		try {
			//Insert Terms and Condtiond
			// Find Latest Record
			CriteriaBuilder cb2 = em.getCriteriaBuilder();
			CriteriaQuery<TermsAndCondition> query2 = cb2.createQuery(TermsAndCondition.class);

			// Find All
			Root<TermsAndCondition> b2 = query2.from(TermsAndCondition.class);

			// Select
			query2.select(b2);
			
			// Where
			Predicate n4 = cb2.equal(b2.get("quoteNo"), hp.getQuoteNo());
			Predicate n5 = cb2.equal(b2.get("companyId"),   hp.getCompanyId());
			Predicate n6 = cb2.equal(b2.get("productId"), hp.getProductId());
			
			query2.where(n4,n5,n6);

			// Get Result
			TypedQuery<TermsAndCondition> result2 = em.createQuery(query2);
			List<TermsAndCondition> list2 = result2.getResultList();
			if(list2.size() > 0 ){
				List<TermsAndCondition> copyTerms = new ArrayList<TermsAndCondition>(); 
				list2.forEach( o -> {
					TermsAndCondition newTerm = new TermsAndCondition(); 
					dozerMapper.map(o, newTerm)			;
					newTerm.setRequestReferenceNo(newRefNo);
					newTerm.setQuoteNo("");
					copyTerms.add(newTerm);
				});	
				termsRepo.saveAllAndFlush(copyTerms);
			}
			
		}catch (Exception e) {
			e.printStackTrace();
			log.error("Exception is ---> " + e.getMessage());
			
		}
	}

	@Transactional
	public CommonRes changeEndtStatus(ChangeEndoStatusReq req) {
		DozerBeanMapper dozerMapper =new DozerBeanMapper(); 
		try {
			HomePositionMaster data=hpmrepo.findByQuoteNo(req.getQuoteNo());
			CompanyProductMaster product =  getCompanyProductMasterDropdown(data.getCompanyId() , req.getProductId().toString());

			if ("Financial".equalsIgnoreCase(data.getEndtCategDesc())) {
				// Update Home Posion Master
				if (StringUtils.isNotBlank(data.getEndtTypeId()))
					data.setStatus("P");
					data.setEndtStatus("C");
					hpmrepo.saveAndFlush(data);
					// Update ProductWise
					paymentServiceImpl.updateProductWisePolicyNo(req.getProductId().toString(), data.getPolicyNo(),
						req.getQuoteNo(), data.getEndtTypeId(),product.getMotorYn() , new BigDecimal(0));
			} else {
				  // Policy Cover Data
		    	   {
		    		   CriteriaBuilder cb = em.getCriteriaBuilder();
						// create update
						CriteriaUpdate<PolicyCoverData> update = cb.createCriteriaUpdate(PolicyCoverData.class);
						// set the root class
						Root<PolicyCoverData> m = update.from(PolicyCoverData.class);
						// set update and where clause
						update.set("policyNo", data.getPolicyNo() );
						
						Predicate n1 = cb.equal(m.get("quoteNo"),req.getQuoteNo());
						// Cancellation Condition
						if(StringUtils.isNotBlank(data.getEndtTypeId()) && data.getEndtTypeId().equalsIgnoreCase("842")) {
							update.where(n1);
						} else {
							Predicate n2 = cb.notEqual(m.get("status"),"D" );
							update.where(n1,n2);
						}
						// perform update
						em.createQuery(update).executeUpdate();
		    	   }
		    	   
		    	   // Policy Cover Data Induviduals
		    	   {
		    		   CriteriaBuilder cb = em.getCriteriaBuilder();
						// create update
						CriteriaUpdate<PolicyCoverDataIndividuals> update = cb.createCriteriaUpdate(PolicyCoverDataIndividuals.class);
						// set the root class
						Root<PolicyCoverDataIndividuals> m = update.from(PolicyCoverDataIndividuals.class);
						// set update and where clause
						update.set("policyNo", data.getPolicyNo());
						
						Predicate n1 = cb.equal(m.get("quoteNo"),req.getQuoteNo() );
						// Cancellation Condition
						if(StringUtils.isNotBlank(data.getEndtTypeId()) && data.getEndtTypeId().equalsIgnoreCase("842")) {
							update.where(n1);
						} else {
							Predicate n2 = cb.notEqual(m.get("status"),"D" );
							update.where(n1,n2);
						}
						// perform update
						em.createQuery(update).executeUpdate();  
		    	   }
		    	   
		    	// Section Update
		    	   List<SectionDataDetails> secList =  sddRepo.findByQuoteNo(req.getQuoteNo());
	    		   if(StringUtils.isNotBlank(data.getEndtTypeId()) && data.getEndtTypeId().equalsIgnoreCase("842")) {
	    			   secList.forEach( o -> {
	    				   o.setPolicyNo(data.getPolicyNo());
	    				   o.setStatus("D");
	    				   o.setEndtStatus("C");
	    			   });
					} else {
						secList.forEach( o -> {
						   if( ! "D".equalsIgnoreCase(o.getStatus()) ) {
							   o.setPolicyNo(data.getPolicyNo());
			    			   o.setStatus("P");
						   }
			    			   o.setEndtStatus(StringUtils.isNotBlank(data.getEndtTypeId()) ? "C" : "");
			    		    
		    		   });
					}
	    		   sddRepo.saveAllAndFlush(secList);
	    		  
	    		// Update Eservice Asset
	    		   List<EserviceSectionDetails> updateEserList = new ArrayList<EserviceSectionDetails>();
	    		   List<EserviceSectionDetails> eserBuildingList =  sectionRepo.findByQuoteNoOrderByRiskIdAsc(req.getQuoteNo());
		    	   eserBuildingList.forEach( o -> {
		    			  
	    			  List<SectionDataDetails> filterAsset = secList.stream().filter( e -> e.getRiskId().equals(o.getRiskId())
	    					  && e.getSectionId().equals(o.getSectionId()) && e.getCoverId().equals(o.getCoverId())).collect(Collectors.toList());
	    			  
	    			  if( filterAsset.size()> 0 ) {
	    				  EserviceSectionDetails updateEser = o; 
	    				  dozerMapper.map(filterAsset.get(0) , updateEser);
	    				  updateEserList.add(updateEser);
	    			   }
		    		 
		    		  });	    		   
		    	   sectionRepo.saveAllAndFlush(updateEserList);
			}
			Object res = null ;
			if (product.getMotorYn().equalsIgnoreCase("M") ) {
				EserviceMotorDetails motorEndtStatus = copyraw.eserviceMotorEndtStatus(req);
				res = motorEndtStatus ;
			} else if (product.getMotorYn().equalsIgnoreCase("A") ) {
				List<EserviceBuildingDetails> buildEndtStatus = new ArrayList<EserviceBuildingDetails>();
				buildEndtStatus.add( copyBuildingraw.buildingRawEndtStatus(req));
				res = buildEndtStatus ;
			} else if (product.getMotorYn().equalsIgnoreCase("H")
					&& req.getProductId().equalsIgnoreCase(travelProductId)) {
				List<EserviceTravelDetails> travelEndtStatus = new ArrayList<EserviceTravelDetails>();
				travelEndtStatus.add( copyTravelraw.travelRawEndtStatus(req));
				res=travelEndtStatus;
			}else { 
				List<EserviceCommonDetails> comonEndtStatus = new ArrayList<EserviceCommonDetails>();
				comonEndtStatus.add( copyCommonraw.commonRawEndtStatus(req));
				res = comonEndtStatus ;
			}
			HomePositionMaster datas=hpmrepo.findByQuoteNo(req.getQuoteNo());
			System.out.println("HOMEPOSITIONMASTER CHECK: " + datas);
			CommonRes c=new CommonRes();
			c.setCommonResponse(res);
			c.setErroCode(0);
			c.setIsError(false);
			c.setMessage("Success");
			return c;
		} catch (

		Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public synchronized CompanyProductMaster getCompanyProductMasterDropdown(String companyId, String productId) {
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
			return null;
		}
		return product;
	}
	
	
	
	
	public CommonRes getSectionList(EndtSectionListReq req) {
		CommonRes commonRes = new CommonRes();
		try {
			// Find Data
			List<ProductSectionMaster> secList = getProductSectionDropdown(req.getInsuranceId() , req.getProductId() ) ;
			List<EserviceSectionDetails> sectionDatas = eserSecRepo.findByRequestReferenceNoOrderByRiskIdAsc(req.getRequestReferenceNo());
			
			// Opted Sections
			List<EndtSectionsRes> optedResList = new ArrayList<EndtSectionsRes>() ;
	
			sectionDatas.forEach( sec ->  {
				EndtSectionsRes secRes = new EndtSectionsRes();
				secRes.setSectionId(sec.getSectionId());
				secRes.setSectionName(sec.getSectionName());
				secRes.setProductType(sec.getProductType());
				String viewOrEdit = "";
				viewOrEdit = StringUtils.isBlank(sec.getSectionEndtModification()) || sec.getSectionEndtModification().equalsIgnoreCase("None") || sec.getSectionEndtModification().equalsIgnoreCase("Removed")
						? "View"  : "Edit" ;
				secRes.setViewOrEdit(viewOrEdit);
				secRes.setModificationType(sec.getSectionEndtModification());
				optedResList.add(secRes);
				
			} );
			
			
			// NonOpted Sections Fiter Based on Opted Section
			List<EndtSectionsRes> nonoptedResList = new ArrayList<EndtSectionsRes>();
			List<ProductSectionMaster> filterNonOptedSections = secList.stream().filter( sec -> sectionDatas.stream().map( EserviceSectionDetails :: getSectionId  ).anyMatch(  
					eser ->  eser.equalsIgnoreCase( sec.getSectionId().toString()) )).collect(Collectors.toList());
			List<ProductSectionMaster> fiterNonOptedSecList = secList ; 
			fiterNonOptedSecList.removeAll(filterNonOptedSections) ; 
			 
			fiterNonOptedSecList.forEach( sec ->  {
				EndtSectionsRes secRes = new EndtSectionsRes();
				secRes.setSectionId(sec.getSectionId().toString());
				secRes.setSectionName(sec.getSectionName());
				secRes.setProductType(sec.getMotorYn());
				nonoptedResList.add(secRes);
			} );
			
			
//			// Endt Sections
//			List<EndtSectionsRes> endtSecResList = new  ArrayList<EndtSectionsRes>();
//			List<EserviceSectionDetails> filterEndtSections = sectionDatas.stream().filter( o -> o.getSectionEndtModification() != null 
//					&& ! o.getSectionEndtModification().equalsIgnoreCase("None") ).collect( Collectors.toList());
//			
//			filterEndtSections.forEach( sec ->  {
//				EndtSectionsRes secRes = new EndtSectionsRes();
//				secRes.setSectionId(sec.getSectionId());
//				secRes.setSectionName(sec.getSectionName());
//				secRes.setProductType(sec.getProductType());
//				endtSecResList.add(secRes);
//			} );
			
			// Response 
			EndtSectionListRes  sectionRes = new EndtSectionListRes(); 
			sectionRes.setOptedSections(optedResList);
			sectionRes.setNonoptedSections(nonoptedResList);
		//	sectionRes.setEndtSections(endtSecResList);
			
			commonRes.setCommonResponse(sectionRes);
			commonRes.setErroCode(0);
			commonRes.setIsError(false);
			commonRes.setMessage("Success");
			
			
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return commonRes;
	}
	
	
	public List<ProductSectionMaster> getProductSectionDropdown(String companyId, String productId) {
		List<ProductSectionMaster> sectionList = new ArrayList<ProductSectionMaster>();
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
			CriteriaQuery<ProductSectionMaster> query = cb.createQuery(ProductSectionMaster.class);
		
			// Find All
			Root<ProductSectionMaster> c = query.from(ProductSectionMaster.class);

			// Select
			query.select(c);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("sectionName")));

			// Effective Date Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<ProductSectionMaster> ocpm1 = effectiveDate.from(ProductSectionMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(c.get("sectionId"), ocpm1.get("sectionId"));
			Predicate a2 = cb.equal(c.get("companyId"), ocpm1.get("companyId"));
			Predicate a3 = cb.equal(c.get("productId"), ocpm1.get("productId"));
			Predicate a4 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1, a2, a3, a4);

			// Effective Date End
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<ProductSectionMaster> ocpm2 = effectiveDate2.from(ProductSectionMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			jakarta.persistence.criteria.Predicate a5 = cb.equal(c.get("sectionId"), ocpm2.get("sectionId"));
			Predicate a7 = cb.equal(c.get("companyId"), ocpm2.get("companyId"));
			Predicate a8 = cb.equal(c.get("productId"), ocpm2.get("productId"));

			jakarta.persistence.criteria.Predicate a6 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			effectiveDate2.where(a5, a6, a7, a8);

			// Where
			jakarta.persistence.criteria.Predicate n1 = cb.equal(c.get("status"), "Y");
			jakarta.persistence.criteria.Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			jakarta.persistence.criteria.Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
			jakarta.persistence.criteria.Predicate n4 = cb.equal(c.get("companyId"), companyId);
			jakarta.persistence.criteria.Predicate n5 = cb.equal(c.get("productId"), productId);
		//	Predicate n6 = cb.equal(c.get("sectionId"), sectionId);
		//	query.where(n1, n2, n3, n4, n5, n6).orderBy(orderList);
			query.where(n1, n2, n3, n4, n5).orderBy(orderList);

			// Get Result
			TypedQuery<ProductSectionMaster> result = em.createQuery(query);
			sectionList = result.getResultList();
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return sectionList;
	}
	
	
	public CommonRes saveEndtSection(EndtSectionSaveReq req) {
		CommonRes commonRes = new CommonRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper(); 
		try {
			// Section Master
			List<ProductSectionMaster> sectionList = getProductSectionDropdown(req.getInsuranceId() , req.getProductId() ) ;
			
			// Transaction Tables
			List<EserviceSectionDetails>  secDatas =  eserSecRepo.findByRequestReferenceNoOrderByRiskIdAsc(req.getRequestReferenceNo());
			EserviceSectionDetails mapSec = secDatas.get(0);
			
			String secEndtModify = req.getSectionEndtModification() ;
			String humanProductType = getListItem("99999", req.getBranchCode(), "PRODUCT_CATEGORY", "H");
			String assetProductType = getListItem("99999", req.getBranchCode(), "PRODUCT_CATEGORY", "A");
			
			
			List<EserviceSectionDetails>  saveSecList = new ArrayList<EserviceSectionDetails>(); 
			
			// Modify Section 
			for ( String sec :  req.getEndtSectionIds() ) {
				List<EserviceSectionDetails>  filterSecs =  secDatas.stream().filter(  o -> o.getSectionId().equalsIgnoreCase(sec) ).collect(Collectors.toList() ) ;
				if(filterSecs.size() > 0 ) {
					EserviceSectionDetails secData =   filterSecs.get(0);
					secData.setSectionEndtModification(secEndtModify);
					saveSecList.add(secData);
				} else {
					EserviceSectionDetails secData = new EserviceSectionDetails();
					List<ProductSectionMaster> filterSection = sectionList.stream().filter( o -> o.getSectionId().equals(Integer.valueOf(sec ) ) ).collect(Collectors.toList());		
					if( filterSection.size() > 0 ) {
						ProductSectionMaster section =  filterSection.get(0) ;
						
						dozerMapper.map(mapSec, secData);	
						
						secData.setSectionId(sec);
						secData.setSectionName( section.getSectionName() );
						secData.setRiskId(1);
						secData.setProductType(section.getMotorYn());
						secData.setProductTypeDesc(section.getMotorYn().equalsIgnoreCase("H") ? humanProductType : assetProductType  );
						secData.setUserOpt("N");
						secData.setSectionEndtModification(secEndtModify);
						if(!(req.getEndorsementType()==null || req.getEndorsementType()==0)) {
							secData.setOriginalPolicyNo(req.getOriginalPolicyNo());
							secData.setEndorsementDate(req.getEndorsementDate());
							secData.setEndorsementRemarks(req.getEndorsementRemarks());
							secData.setEndorsementEffdate(req.getEndorsementEffdate());
							secData.setEndtPrevPolicyNo(req.getEndtPrevPolicyNo());
							secData.setEndtPrevQuoteNo(req.getEndtPrevQuoteNo());
							secData.setEndtCount(req.getEndtCount());
							secData.setEndtStatus(req.getEndtStatus());
							secData.setIsFinaceYn(req.getIsFinaceYn());
							secData.setEndtCategDesc(req.getEndtCategDesc());
							secData.setEndorsementType(req.getEndorsementType());
							secData.setEndorsementTypeDesc(req.getEndorsementTypeDesc()); 
								
						} 
						saveSecList.add(secData);
						
					}
				}
			}
			
			  
			// Save Sections
			eserSecRepo.saveAllAndFlush(saveSecList);
				
			// Response 
			SuccessRes  res = new SuccessRes(); 
			res.setResponse("Saved Successfully");
			res.setSuccessId("1");
			
			commonRes.setCommonResponse(res);
			commonRes.setErroCode(0);
			commonRes.setIsError(false);
			commonRes.setMessage("Success");
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return commonRes;
	}
	
	
	
	public synchronized String getListItem(String insuranceId, String branchCode, String itemType, String itemCode) {
		String itemDesc = "";
		List<ListItemValue> list = new ArrayList<ListItemValue>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			today = cal.getTime();
			Date todayEnd = cal.getTime();

			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ListItemValue> query = cb.createQuery(ListItemValue.class);
			// Find All
			Root<ListItemValue> c = query.from(ListItemValue.class);

			// Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("branchCode")));

			// Effective Date Start Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<ListItemValue> ocpm1 = effectiveDate.from(ListItemValue.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(c.get("itemId"), ocpm1.get("itemId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate b1= cb.equal(c.get("branchCode"),ocpm1.get("branchCode"));
			Predicate b2 = cb.equal(c.get("companyId"),ocpm1.get("companyId"));
			effectiveDate.where(a1,a2,b1,b2);
			
			// Effective Date End Max Filter
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<ListItemValue> ocpm2 = effectiveDate2.from(ListItemValue.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate a3 = cb.equal(c.get("itemId"), ocpm2.get("itemId"));
			Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			Predicate b3= cb.equal(c.get("companyId"),ocpm2.get("companyId"));
			Predicate b4= cb.equal(c.get("branchCode"),ocpm2.get("branchCode"));
			effectiveDate2.where(a3,a4,b3,b4);

			// Where
			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n12 = cb.equal(c.get("status"),"R");
			Predicate n13 = cb.or(n1,n12);
			Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
			Predicate n4 = cb.equal(c.get("companyId"), insuranceId);
			Predicate n5 = cb.equal(c.get("companyId"), "99999");
			Predicate n6 = cb.equal(c.get("branchCode"), branchCode);
			Predicate n7 = cb.equal(c.get("branchCode"), "99999");
			Predicate n8 = cb.or(n4, n5);
			Predicate n9 = cb.or(n6, n7);
			Predicate n10 = cb.equal(c.get("itemType"), itemType);
			Predicate n11 = cb.equal(c.get("itemCode"), itemCode);
			query.where(n13, n2, n3, n8, n9, n10, n11).orderBy(orderList);
			// Get Result
			TypedQuery<ListItemValue> result = em.createQuery(query);
			list = result.getResultList();

			itemDesc = list.size() > 0 ? list.get(0).getItemValue() : "";
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return itemDesc;
	}
	
	public List<Error> validateEndtDetails(Endorsment request) {
		List<Error> error = new ArrayList<Error>();

		try {
			HomePositionMaster policy = hpmrepo.findByPolicyNoAndStatusAndCompanyIdAndProductId(request.getPolicyNo(),"P", request.getCompanyId(), Integer.valueOf(request.getProductId().intValue()));
			if(request.getEndtEffectiveDate() ==null ) {
				error.add(new Error("01", "EndtEffectiveDate", "Please Select Endoresment Effective Date"));
				
			}else if (stripTime(request.getEndtEffectiveDate()).before(stripTime(policy.getInceptionDate()))) {
			    error.add(new Error("02", "EndtEffectiveDate", "Endorsement Effective Date cannot be before Policy Inception Date"));
			}
			else if (request.getProductId()!=null && travelProductId.equalsIgnoreCase(request.getProductId().toPlainString())  ) {
//				HomePositionMaster homeData = homeRepo.findByPolicyNo(request.getPolicyNo() );	
//				if(homeData !=null ) {
//					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy") ;
//					Date effDate = request.getEndtEffectiveDate();
//					Date travelStartDate = homeData.getInceptionDate() ;
//					if (travelStartDate.before(effDate) ) {
//						error.add(new Error("01", "TravelStartDate",  "Future  Policy only we can modify in travel" ));
//								//"Policy Start Date - " + sdf.format(homeData.getInceptionDate()) 
//								//+ " is Less Than Effective Date -" + sdf.format(request.getEndtEffectiveDate())  + " Not Allowed . Future  Policy only we can cancel in travel" ));
//					} else  {
//						
//						String st = sdf.format(travelStartDate);
//						String today = sdf.format(new Date());
//						if(st.equals(today) ) {
//							error.add(new Error("01", "TravelStartDate",  "Future  Policy only we can modify in travel" ));
//						}
//						
//					}
//				}
			}
			
			
			
		} catch (Exception e) {
			e.printStackTrace();
			error.add(new Error("01", "CommonError", e.getMessage() ));
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return error;
	}

	private Date stripTime(Date date) {
	    if (date == null) return null;
	    Calendar cal = Calendar.getInstance();
	    cal.setTime(date);
	    cal.set(Calendar.HOUR_OF_DAY, 0);
	    cal.set(Calendar.MINUTE, 0);
	    cal.set(Calendar.SECOND, 0);
	    cal.set(Calendar.MILLISECOND, 0);
	    return cal.getTime();
	}
	
	public CommonRes updateAdditionalInfo(EndRequest request) {
		try {

			// Document Copy
			List<DocumentTransactionDetails> response=copyBuildingraw.coverDocumentUploadDetailsEndoCopyquote(request);

//			// Human Additional info
//			copyBuildingraw.productEmpDetailsEndoCopyquote(request);
//
//			// Content copy
//			copyBuildingraw.contentAndRiskEndoCopyquote(request);
//
//			// Building Details copy
//			copyBuildingraw.buildingDetailsEndoCopyquote(request);
			
			CommonRes com=new CommonRes();
			com.setCommonResponse(response);
			com.setErroCode(0);
			com.setIsError(false);
			com.setMessage("Success");
			return com;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Transactional
	public CommonRes editDeleteAlltable(EndDeleteReq request) {
	    CommonRes res = new CommonRes();
	    String refNo = request.getRequestReferenceNo();
	    String companyid=request.getCompanyid();
	    StringBuilder msg = new StringBuilder();

	    try {
	        // SECTION table
	        long deletedSection = sectionRepo.deleteByRequestReferenceNoAndCompanyId(refNo,companyid);
	        if (deletedSection > 0) msg.append("ESECTION :" +deletedSection+", ");

	        // BUILDING table
	        long deletedBuilding = eserBuldingRepo.deleteByRequestReferenceNo(refNo);
	        if (deletedBuilding > 0) msg.append("EBUILDING :"+deletedBuilding+", ");
	        
	        long eMotor = emotorRepo.deleteByRequestReferenceNo(refNo);
	        if (eMotor > 0) msg.append("eMotor :"+eMotor+", ");

	        // ASSET table
	        long deletedAsset = assertRepo.deleteByRequestReferenceNo(refNo);
	        if (deletedAsset > 0) msg.append("Asset :"+deletedAsset+", ");

	        // ECOMMON table
	        long deletedECommon = eserCommonRepo.deleteByRequestReferenceNoAndCompanyId(refNo,companyid);
	        if (deletedECommon > 0) msg.append("ECommon  :"+deletedECommon+", ");

	        // MSHUMAN table
	        long deletedMsHuman = mshummanRepo.deleteByRequestReferenceNo(refNo);
	        if (deletedMsHuman > 0) msg.append("MsHuman :"+deletedMsHuman+", ");

	        // MSCOMMON table
	        long deletedMsCommon = msCommonRepo.deleteByRequestreferenceno(refNo);
	        if (deletedMsCommon > 0) msg.append("MsCommon :"+deletedMsCommon+", ");
	        
	        List<HomePositionMaster> homelist = hpmrepo.findByRequestReferenceNo(refNo);
	        if(homelist!=null && homelist.isEmpty())
			{
	        	for(HomePositionMaster hp : homelist) {
				if (StringUtils.isNotBlank(hp.getQuoteNo()) && StringUtils.isNotBlank(hp.getCustomerId())) {
					 personalInforepo.deleteByCustomerId(hp.getCustomerId());
				}
			}
			}
	        
	        long fact = factRepo.deleteByRequestReferenceNo(refNo);
	        if (fact > 0) msg.append("FactTable :"+fact+", ");
	        
	        long emi  =emiRepo.deleteByRequestReferenceNo(refNo);
	        if (emi > 0) msg.append("emi :"+emi+", ");
	        
	        long contentrisk = crRepo.deleteByRequestReferenceNo(refNo);
	        if (contentrisk > 0) msg.append("contentrisk :"+contentrisk+", ");
	        
	        long productED = pedRepo.deleteByRequestReferenceNo(refNo);
	        if (productED > 0) msg.append("productED :"+productED+", ");
	        
			  long home = hpmrepo.deleteByRequestReferenceNo(refNo);
		        if (home > 0) msg.append("HomePoisitionMaster :"+home+", ");
		       
		   //   additonRepo.deleteByEndtReqRefNoAndEndtStatusNot(refNo,"B");
		        
		        
	        if(fact > 0 && home >0)
	        {
	        	long policy = policyRepo.deleteByRequestReferenceNo(refNo);
	 	        if (policy > 0) msg.append("PolicyCoverdata :"+policy+", ");
	 	        
	 	       long mBuilding = buildingRepo.deleteByRequestReferenceNo(refNo);
		        if (mBuilding > 0) msg.append("MBuilding :"+mBuilding+", ");
		        
		        long mCommon = commonRepo.deleteByRequestReferenceNo(refNo);
		        if (mCommon > 0) msg.append("MCommon :"+mCommon+", ");
		        
		        long mMotor = motorRepo.deleteByRequestReferenceNo(refNo);
		        if (mMotor > 0) msg.append("mMotor :"+mMotor+", ");
		        
		        long mSection= sddRepo.deleteByRequestReferenceNo(refNo);
		        if (mSection > 0) msg.append("mSection :"+mSection+", ");
		        
		        long doc= docRepo.deleteByRequestReferenceNo(refNo);
		        if (doc > 0) msg.append("doc :"+doc+", ");
		    }
	        
	        List<AdditionalInformation> entities = additonRepo.findByEndtReqRefNo(refNo);
	        List<AdditionalInformation> toUpdate = new ArrayList<>();
	        List<AdditionalInformation> toDelete = new ArrayList<>();
	        if(entities!=null && !entities.isEmpty()) {
	        	   String entdTypeId = entities.stream()
	        	            .map(AdditionalInformation::getEndtTypeId)
	        	            .filter(Objects::nonNull)
	        	            .findFirst()
	        	            .orElse(null); 
	        	if ("895".equalsIgnoreCase(entdTypeId)) {
	        	

	        entities.stream()
	                .filter(f -> f.getEndtReqRefNo() != null &&
	                             f.getEndtReqRefNo().equalsIgnoreCase(refNo))
	                .forEach(f -> {
	                    if (f.getStatus() != null && f.getStatus().equalsIgnoreCase("N")) {
	                        f.setStatus("Y");
	                        f.setEndtReqRefNo(null);
	                        toUpdate.add(f);
	                    } else {
	                        toDelete.add(f);
	                    }
	                });

	        if (!toUpdate.isEmpty()) {
	        	additonRepo.saveAllAndFlush(toUpdate);
	         }
	        if (!toDelete.isEmpty()) {
	        	additonRepo.deleteAll(toDelete); // ← deleteAll instead of loop
            }
	        }
	        else if ("896".equalsIgnoreCase(entdTypeId)){
	        	 entities.stream()
	             .filter(f -> f.getEndtReqRefNo() != null &&
	                          f.getEndtReqRefNo().equalsIgnoreCase(refNo))
	             .forEach(f -> {
	                 if (f.getEndtAdd() != null && f.getEndtAdd().equalsIgnoreCase("Y")) {
	                     toDelete.add(f);
	                 }});
	        	 if (!toDelete.isEmpty()) {
	        		 additonRepo.deleteAll(toDelete);
	                
	             }
	        }else if ("897".equalsIgnoreCase(entdTypeId)){
	        	entities.stream().filter(f -> f.getEndtReqRefNo() != null &&  f.getEndtReqRefNo().equalsIgnoreCase(refNo))
	             .forEach(f -> {
	                 if (f.getStatus() != null  &&  f.getStatus().equalsIgnoreCase("D") && f.getEndtAdd() != null  &&  f.getEndtAdd().equalsIgnoreCase("D") ) {
	                	 f.setStatus("Y");
	     	    	     f.setEndtTypeId(null);
	     	    	     f.setEndtStatus("P");
	     	    	     f.setEndtReqRefNo(null);
	     	    	     f.setEndtAdd(null);
	                     toUpdate.add(f);
	                    
	                 }
	                 
	             });
	        	if (!toUpdate.isEmpty()) {
		        	additonRepo.saveAllAndFlush(toUpdate);
		         }
	        }
	        }
	         if (msg.length() > 0) {
	            res.setMessage("Deleted tables: " + msg.toString());
	            System.out.println("Deleted tables:==> " + msg.toString());
	        } else {
	            res.setMessage("No records found to delete");
	        }
	         res.setErroCode(0);
	        res.setIsError(false);
		} catch (Exception e) {
			e.printStackTrace();
			res.setMessage("Error occurred while deleting records");
			res.setErroCode(1);
			res.setIsError(true);
		}
	    return res;
	}
}

