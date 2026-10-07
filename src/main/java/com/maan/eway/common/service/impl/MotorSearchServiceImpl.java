package com.maan.eway.common.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.FactorRateRequestDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.LoginUserInfo;
import com.maan.eway.bean.MotorDataDetails;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.PremiaCustomerDetails;
import com.maan.eway.calculator.util.TaxFromFactor;
import com.maan.eway.common.req.SearchEservieMotorDetailsViewRatingRes;
import com.maan.eway.common.req.SearchReq;
import com.maan.eway.common.req.ViewQuoteDetailsReq;
import com.maan.eway.common.res.AdminViewQuoteRes;
import com.maan.eway.common.res.SearchCoverDetails;
import com.maan.eway.common.res.SearchCustomerDetailsRes;
import com.maan.eway.common.res.SearchDiscount;
import com.maan.eway.common.res.SearchEserviceMotorDetailsRes;
import com.maan.eway.common.res.SearchLoading;
import com.maan.eway.common.res.SearchTax;
import com.maan.eway.common.res.ViewQuoteDetailsRes;
import com.maan.eway.common.service.MotorSearchService;
import com.maan.eway.master.req.CopyQuoteDropDownReq;
import com.maan.eway.repository.EServiceMotorDetailsRepository;
import com.maan.eway.repository.EserviceCustomerDetailsRepository;
import com.maan.eway.repository.FactorRateRequestDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.LoginUserInfoRepository;
import com.maan.eway.repository.MotorDataDetailsRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.repository.PremiaCustomerDetailsRepository;
import com.maan.eway.res.calc.Endorsement;
import com.maan.eway.res.calc.Tax;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;


@Service
@Transactional
public class MotorSearchServiceImpl implements MotorSearchService {
	
	
	@PersistenceContext
	private EntityManager em;

	private Logger log = LogManager.getLogger(MotorGridServiceImpl.class);


	@Autowired
	private FactorRateRequestDetailsRepository factorrepo;

	@Autowired
	private EServiceMotorDetailsRepository repo;
	
	@Autowired
	private MotorDataDetailsRepository motorRepo;
	
	@Autowired
	private EserviceCustomerDetailsRepository cusrepo;
	@Autowired
	private PremiaCustomerDetailsRepository premiaRepo;

	@Autowired
	private PersonalInfoRepository perRepo;
	
	@Autowired
	private HomePositionMasterRepository homeRepo;
	
	@Autowired
	private LoginUserInfoRepository  loginUserInfoRepo;
	
	
	@Override
	public List<ListItemValue> searchDropdownMotor(CopyQuoteDropDownReq req) { 
		List<ListItemValue> list = new ArrayList<ListItemValue>();

		try {
			String itemType = "ADMIN_SEARCH_MOTOR";
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
			Predicate n1 = cb.equal(c.get("status"), "Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
			Predicate n4 = cb.equal(c.get("companyId"), req.getInsuranceId());
			Predicate n5 = cb.equal(c.get("companyId"), "99999");
			Predicate n6 = cb.equal(c.get("branchCode"), req.getBranchCode());
			Predicate n7 = cb.equal(c.get("branchCode"), "99999");
			Predicate n8 = cb.or(n4, n5);
			Predicate n9 = cb.or(n6, n7);
			Predicate n10 = cb.equal(c.get("itemType"), itemType);
			query.where(n1, n2, n3, n8, n9, n10).orderBy(orderList);
			// Get Result
			TypedQuery<ListItemValue> result = em.createQuery(query);
			list = result.getResultList();

			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getItemCode())))
					.collect(Collectors.toList());
			list.sort(Comparator.comparing(ListItemValue::getItemValue));
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return list;
	}
	
	private static <T> java.util.function.Predicate<T> distinctByKey(java.util.function.Function<? super T, ?> keyExtractor) {
	    Map<Object, Boolean> seen = new ConcurrentHashMap<>();
	    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}	



	@Override
	public List<Tuple> adminSearchMotorQuote(SearchReq req, List<String> branches) {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List<Tuple> searchQuote = new ArrayList<Tuple>();
		try {
			// Search
			String searchKey = req.getSearchKey();
			String searchValue = req.getSearchValue();
			String companyId = req.getInsuranceId();
			String loginId = req.getLoginId();
			String userType = req.getUserType();

			if ("RequestReferenceNo".equalsIgnoreCase(searchKey)) {
				searchQuote = adminsearch(req,searchKey, searchValue, companyId, loginId, userType, branches);
			} else if ("MobileNumber".equalsIgnoreCase(searchKey)) {
				searchQuote = adminsearch(req,searchKey, searchValue, companyId, loginId, userType, branches);
			} else if ("CustomerName".equalsIgnoreCase(searchKey)) {
				searchQuote = adminsearch(req,searchKey, searchValue, companyId, loginId, userType, branches);
			} else if ("QuoteNumber".equalsIgnoreCase(searchKey)) {
				searchQuote = adminsearch(req,searchKey, searchValue, companyId, loginId, userType, branches);
			} else if ("ChassisNumber".equalsIgnoreCase(searchKey)) {
				searchQuote = adminsearch(req,searchKey, searchValue, companyId, loginId, userType, branches);
			} else if ("PolicyNumber".equalsIgnoreCase(searchKey)) {
				searchQuote = adminsearch(req,searchKey, searchValue, companyId, loginId, userType, branches);
			} else if ("MotorCategory".equalsIgnoreCase(searchKey)) {
				searchQuote = adminsearch(req,searchKey, searchValue, companyId, loginId, userType, branches);
			} else if ("VehicleType".equalsIgnoreCase(searchKey)) {
				searchQuote = adminsearch(req,searchKey, searchValue, companyId, loginId, userType, branches);
			} else if ("CustomerCode".equalsIgnoreCase(searchKey)) {
				searchQuote = adminsearch(req,searchKey, searchValue, companyId, loginId, userType, branches);
			} else if ("VehicleModel".equalsIgnoreCase(searchKey)) {
				searchQuote = adminsearch(req,searchKey, searchValue, companyId, loginId, userType, branches);
			} else if ("VehicleMake".equalsIgnoreCase(searchKey)) {
				searchQuote = adminsearch(req,searchKey, searchValue, companyId, loginId, userType, branches);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return searchQuote;
	}


	public List<Tuple> adminsearch(SearchReq req,String searchKey, String searchValue, String companyId, String loginId,
			String userType, List<String> branches) {
		List<Tuple> customerDetailsList = new ArrayList<>();
		try {
		    CriteriaBuilder cb = em.getCriteriaBuilder();
		    CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

		    Root<EserviceMotorDetails> c = query.from(EserviceMotorDetails.class);
		    Root<EserviceCustomerDetails> cus = query.from(EserviceCustomerDetails.class);

		    query.multiselect(
		        cus.get("customerReferenceNo").alias("customerReferenceNo"),
		        cus.get("clientName").alias("clientName"),
		        cus.get("mobileNo1").alias("mobileNumber"),
		        cb.count(c).alias("totalCount"),
		        cb.max(c.get("requestReferenceNo")).alias("maxRequestReferenceNo"),
		        cb.max(c.get("quoteNo")).alias("maxQuoteNo"),
		        cb.max(c.get("policyNo")).alias("maxPolicyNo"),
		        cb.max(c.get("branchCode")).alias("maxBranchCode"),
		        cb.max(c.get("status")).alias("maxStatus"),
		        cb.max(c.get("loginId")).alias("maxLoginId"),
		        cb.max(c.get("policyTypeDesc")).alias("maxPolicyType"),
		        cb.max(c.get("vehicleTypeDesc")).alias("maxVehicleType"),
		        cb.max(c.get("policyStartDate")).alias("maxPolicyStartDate"),
		        cb.max(c.get("policyEndDate")).alias("maxPolicyEndDate"),
		        cb.max(c.get("entryDate")).alias("maxEntryDate"),
		        cb.max(c.get("overallPremiumLc")).alias("maxOverallPremium"),
		        cb.max(c.get("currency")).alias("maxCurrency"),
		        cb.max(c.get("exchangeRate")).alias("maxExchangeRate"),
		        cb.max(c.get("gpsTrackingInstalled")).alias("maxGpsTracking"),
		        cb.max(c.get("windScreenCoverRequired")).alias("maxWindScreenCover"),
		        cb.max(c.get("productName")).alias("maxProductName"),
		        cb.max(c.get("emiYn")).alias("maxEmi"),
		        cb.max(c.get("installmentPeriod")).alias("maxInstallmentPeriod"),
		        cb.max(c.get("noOfInstallment")).alias("maxNoOfInstallments"),
		        cb.max(c.get("emiPremium")).alias("maxEmiPremium")
		    );

		    // Building predicates for the WHERE clause
		    List<Predicate> predicates = new ArrayList<>();
		     if(searchKey.equals("CustomerName"))
		     {
		      predicates.add(cb.like(cus.get("clientName"), "%" + searchValue + "%")); 
		     }
		     else if(searchKey.equals("CustomerCode"))
		     {
			  predicates.add(cb.like(cus.get("customerCode"), "%" + searchValue + "%")); 
             }
		     else if(searchKey.equals("MobileNumber"))
		     {
			  predicates.add(cb.like(cus.get("mobileNo1"), "%" + searchValue + "%")); 
             }
		     else if(searchKey.equals("MobileNumber"))
		     {
			  predicates.add(cb.like(cus.get("mobileNo1"), "%" + searchValue + "%")); 
             }
		     else if(searchKey.equals("RequestReferenceNo"))
		     {
			  predicates.add(cb.like(c.get("requestReferenceNo"), "%" + searchValue + "%")); 
             }
		     else if(searchKey.equals("QuoteNumber"))
		     {
			  predicates.add(cb.like(c.get("quoteNo"), "%" + searchValue + "%")); 
             }
		     else if(searchKey.equals("ChassisNumber"))
		     {
			  predicates.add(cb.like(c.get("chassisNumber"), "%" + searchValue + "%")); 
             }
		     else if(searchKey.equals("PolicyNumber"))
		     {
			  predicates.add(cb.like(c.get("policyNo"), "%" + searchValue + "%")); 
             }
		     else if(searchKey.equals("MotorCategory"))
		     {
			  predicates.add(cb.like(c.get("motorCategory"), "%" + searchValue + "%")); 
             }
		     else if(searchKey.equals("VehicleType"))
		     {
			  predicates.add(cb.like(c.get("vehicleType"), "%" + searchValue + "%")); 
             }
		   
		     else if(searchKey.equals("VehicleMake"))
		     {
			  predicates.add(cb.like(c.get("vehicleMakeDesc"), "%" + searchValue + "%")); 
             }
		    // Adding the other filters similar to how you had them
		    predicates.add(cb.equal(c.get("companyId"), companyId));

		    // Handling other user type constraints and other predicates...
		    if ("issuer".equalsIgnoreCase(userType)) {
		        predicates.add(c.get("branchCode").in(branches));
		    } else if ("Broker".equalsIgnoreCase(userType) || "User".equalsIgnoreCase(userType)) {
		        predicates.add(cb.equal(c.get("loginId"), loginId));
		        predicates.add(c.get("brokerBranchCode").in(branches));
		    }

		    // Join between the tables based on the filtering condition
		    predicates.add(cb.equal(c.get("customerReferenceNo"), cus.get("customerReferenceNo")));

		    query.where(cb.and(predicates.toArray(new Predicate[0])))
		        // Adjust the GROUP BY statement to match the SQL query structure
		        .groupBy(
		            cus.get("customerReferenceNo"),
		            cus.get("clientName"),
		            cus.get("mobileNo1")
		        )
		        // Order By clause
		        .orderBy(cb.asc(cus.get("customerReferenceNo")));

		    // Execute the query
		    TypedQuery<Tuple> result = em.createQuery(query);
		    customerDetailsList = result.getResultList();
		    customerDetailsList = customerDetailsList.stream()
		        .filter(o -> !o.get("totalCount").equals(0L)) // Filter out zero counts
		        .collect(Collectors.toList());

		} catch (Exception e) {
		    e.printStackTrace();
		    log.info("Exception is --->" + e.getMessage());
		    return null;
		}

		return customerDetailsList;
}


	@Override
	public AdminViewQuoteRes getMotorProductDetails(SearchReq req) {
		AdminViewQuoteRes viewRes = new AdminViewQuoteRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			// Find Motor Data
			List<MotorDataDetails> motorDatas=null;
			if (StringUtils.isNotBlank(req.getQuoteNo())) {
				motorDatas =  motorRepo.findByQuoteNoOrderByVehicleIdAsc(req.getQuoteNo());
				
			}else if (StringUtils.isNotBlank(req.getRequestReferenceNo())) {
				 motorDatas =  motorRepo.findByRequestReferenceNoOrderByVehicleIdAsc(req.getRequestReferenceNo());
			}
			List<SearchEserviceMotorDetailsRes>   motorResList = new ArrayList<SearchEserviceMotorDetailsRes>();
						
			for (MotorDataDetails mot :  motorDatas) {
				SearchEserviceMotorDetailsRes vehicleDetails = new  SearchEserviceMotorDetailsRes()  ;
				
				// Mot
				dozerMapper.map(mot, vehicleDetails);
					
				// Response
				motorResList.add(vehicleDetails);		
			}
			viewRes.setRiskDetails(motorResList);
			
		} catch ( Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return viewRes;
	}


	@Override
	public List<SearchEservieMotorDetailsViewRatingRes> motorRating(SearchReq req) {
		List<SearchEservieMotorDetailsViewRatingRes> resList = new ArrayList<SearchEservieMotorDetailsViewRatingRes>();
		try {
			if (StringUtils.isNotBlank(req.getRequestReferenceNo())) {
				// Find Risk Datas
				List<EserviceMotorDetails> motorDatas = repo.findByRequestReferenceNo(req.getRequestReferenceNo());
				// Find Covers
				List<FactorRateRequestDetails> findCovers = factorrepo.findByRequestReferenceNoOrderByVehicleIdAsc(req.getRequestReferenceNo());

				// Response
				for (EserviceMotorDetails res : motorDatas) {
					SearchEservieMotorDetailsViewRatingRes response = new SearchEservieMotorDetailsViewRatingRes();

					// Set Covers
					List<FactorRateRequestDetails> filterVehicleCovers = findCovers.stream()
							.filter(o -> o.getVehicleId().equals(Integer.valueOf(res.getRiskId()))
									&& o.getCompanyId().equals(res.getCompanyId())
									&& o.getProductId().toString().equals(res.getProductId())
									&& o.getSectionId().toString().equals(res.getSectionId()))
							.collect(Collectors.toList());

					Map<Integer, List<FactorRateRequestDetails>> groupByCover = filterVehicleCovers.stream()
							.collect(Collectors.groupingBy(FactorRateRequestDetails::getCoverId));
					List<SearchCoverDetails> coverListRes = getCoversList(groupByCover);
					coverListRes.forEach(cov -> cov.setSectionName(res.getSectionName()));
					response.setCoverList(coverListRes);
					response.setVehicleId(res.getRiskId().toString());
					response.setRequestReferenceNo(res.getRequestReferenceNo());
					response.setOverallPremiumFc(
							res.getOverallPremiumFc() == null ? "0" : res.getOverallPremiumFc().toPlainString());
					response.setOverallPremiumLc(
							res.getOverallPremiumLc() == null ? "0" : res.getOverallPremiumLc().toPlainString());
					response.setActualPremiumFc(
							res.getActualPremiumFc() == null ? "0" : res.getActualPremiumFc().toPlainString());
					response.setActualPremiumLc(
							res.getActualPremiumLc() == null ? "0" : res.getActualPremiumLc().toPlainString());
					response.setCurrency(res.getCurrency());
					response.setSectionName(res.getSectionName());
					response.setExchangeRate(res.getExchangeRate());
					response.setPolicyEndDate(res.getPolicyEndDate());
					response.setPolicyStartDate(res.getPolicyStartDate());
					resList.add(response);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;

		}
		return resList;
	}
	public synchronized List<SearchCoverDetails> getCoversList(Map<Integer,List<FactorRateRequestDetails>> groupByCover) {
		List<SearchCoverDetails>  coverListRes = new ArrayList<SearchCoverDetails>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			for ( Integer coverId : groupByCover.keySet() ) {
				List<FactorRateRequestDetails>  covers  = groupByCover.get(coverId);
				SearchCoverDetails coverRes = new SearchCoverDetails();
				
				if (covers.get(0).getSubCoverYn().equalsIgnoreCase("N") ) {
					// Get Covers
					List<FactorRateRequestDetails> filterCover = covers.stream().filter( o -> o.getDiscLoadId().equals(0) && o.getTaxId().equals(0) ).collect(Collectors.toList());
					coverRes = dozerMapper.map(filterCover.get(0), SearchCoverDetails.class);
					coverRes.setIsSubCover(filterCover.get(0).getSubCoverYn());
					coverRes.setIsselected(filterCover.get(0).getIsSelected());
					coverRes.setSubCoverId(null);
					coverRes.setSubCoverDesc(null);
					coverRes.setSubCoverName(null);
					coverRes.setPremiumAfterDiscount(filterCover.get(0).getPremiumAfterDiscountFc()==null?BigDecimal.ZERO :filterCover.get(0).getPremiumAfterDiscountFc());
					coverRes.setPremiumBeforeDiscount(filterCover.get(0).getPremiumBeforeDiscountFc()==null?BigDecimal.ZERO :filterCover.get(0).getPremiumBeforeDiscountFc());
					coverRes.setPremiumExcluedTax(filterCover.get(0).getPremiumExcludedTaxFc()==null?BigDecimal.ZERO :filterCover.get(0).getPremiumExcludedTaxFc());
					coverRes.setPremiumIncludedTax(filterCover.get(0).getPremiumIncludedTaxFc()==null?BigDecimal.ZERO :filterCover.get(0).getPremiumIncludedTaxFc());
					coverRes.setPremiumAfterDiscountLC(filterCover.get(0).getPremiumAfterDiscountLc()==null?BigDecimal.ZERO :filterCover.get(0).getPremiumAfterDiscountLc());
					coverRes.setPremiumBeforeDiscountLC(filterCover.get(0).getPremiumBeforeDiscountLc()==null?BigDecimal.ZERO :filterCover.get(0).getPremiumBeforeDiscountLc());
					coverRes.setPremiumExcluedTaxLC(filterCover.get(0).getPremiumExcludedTaxLc()==null?BigDecimal.ZERO :filterCover.get(0).getPremiumExcludedTaxLc());
					coverRes.setPremiumIncludedTaxLC(filterCover.get(0).getPremiumIncludedTaxLc()==null?BigDecimal.ZERO :filterCover.get(0).getPremiumIncludedTaxLc());
					coverRes.setRequestReferenceNo(filterCover.get(0).getRequestReferenceNo());
					coverRes.setVehicleId(filterCover.get(0).getVehicleId()==null?"" :filterCover.get(0).getVehicleId().toString());
					coverRes.setDiffPremiumIncludedTax(filterCover.get(0).getDiffPremiumIncludedTaxFc());
					coverRes.setPolicyEndDate(filterCover.get(0).getCoverPeriodTo());
					coverRes.setProRata(filterCover.get(0).getProRataPercent());
					coverRes.setExcessPercent(filterCover.get(0).getExcessPercent());
					coverRes.setExcessAmount(filterCover.get(0).getExcessAmount());
					coverRes.setExcessDesc(filterCover.get(0).getExcessDesc());
					// Discount Covers Or Promo Covers
					List<FactorRateRequestDetails> filterDiscountCover = covers.stream().filter( o -> ( ! o.getDiscLoadId().equals(0)) && (   o.getCoverageType().equalsIgnoreCase("D") || o.getCoverageType().equalsIgnoreCase("P") ) ).collect(Collectors.toList());
					
					if ( filterDiscountCover.size() > 0 ) {
						 List<SearchDiscount> discounts =  getDiscountRates(filterDiscountCover);
						 coverRes.setDiscounts(discounts);	
					}
					
					// Tax Covers
					List<FactorRateRequestDetails> filterTaxCover = covers.stream().filter( o -> 
					(! o.getTaxId().equals(0)) && o.getDiscLoadId()==0 &&   o.getCoverageType().equalsIgnoreCase("T")).collect(Collectors.toList());
					
					if( filterTaxCover.size() > 0 ) {
						 List<SearchTax> taxes = getTaxRates(filterTaxCover) ;
						 coverRes.setTaxes(taxes);	
					}

					// Loginds Covers
					List<FactorRateRequestDetails> filterLodingCover = covers.stream().filter( o -> ( ! o.getDiscLoadId().equals(0)) &&  o.getCoverageType().equalsIgnoreCase("L") ).collect(Collectors.toList());
					
					if( filterLodingCover.size() > 0 ) {
						 List<SearchLoading> lodings =  getLodingCovers(filterLodingCover) ;
						 coverRes.setLoadings(lodings);	
					}
										
				} else {
					
					// Get Sub Covers
			
					List<FactorRateRequestDetails> filterCover = covers.stream().filter( o -> o.getDiscLoadId().equals(0) && o.getTaxId().equals(0) ).collect(Collectors.toList());
					 coverRes.setCoverId(filterCover.get(0).getCoverId().toString());
					 coverRes.setCalcType(filterCover.get(0).getCalcType());
					 coverRes.setCoverName(filterCover.get(0).getCoverName());
					 coverRes.setCoverDesc(filterCover.get(0).getCoverDesc());
					 coverRes.setIsSubCover(filterCover.get(0).getSubCoverYn());
					 coverRes.setSumInsured(filterCover.get(0).getSumInsured()==null ? BigDecimal.ZERO : new BigDecimal(filterCover.get(0).getSumInsured().toString()));
					 coverRes.setRate(filterCover.get(0).getRate()==null?null:Double.valueOf(filterCover.get(0).getRate().toString()));
					 coverRes.setPremiumAfterDiscount(filterCover.get(0).getPremiumAfterDiscountFc()==null?BigDecimal.ZERO :filterCover.get(0).getPremiumAfterDiscountFc());
					coverRes.setPremiumBeforeDiscount(filterCover.get(0).getPremiumBeforeDiscountFc()==null?BigDecimal.ZERO :filterCover.get(0).getPremiumBeforeDiscountFc());
					coverRes.setPremiumExcluedTax(filterCover.get(0).getPremiumExcludedTaxFc()==null?BigDecimal.ZERO :filterCover.get(0).getPremiumExcludedTaxFc());
					coverRes.setPremiumIncludedTax(filterCover.get(0).getPremiumIncludedTaxFc()==null?BigDecimal.ZERO :filterCover.get(0).getPremiumIncludedTaxFc());
					coverRes.setPremiumAfterDiscountLC(filterCover.get(0).getPremiumAfterDiscountLc()==null?BigDecimal.ZERO :filterCover.get(0).getPremiumAfterDiscountLc());
					coverRes.setPremiumBeforeDiscountLC(filterCover.get(0).getPremiumBeforeDiscountLc()==null?BigDecimal.ZERO :filterCover.get(0).getPremiumBeforeDiscountLc());
					coverRes.setPremiumExcluedTaxLC(filterCover.get(0).getPremiumExcludedTaxLc()==null?BigDecimal.ZERO :filterCover.get(0).getPremiumExcludedTaxLc());
					coverRes.setPremiumIncludedTaxLC(filterCover.get(0).getPremiumIncludedTaxLc()==null?BigDecimal.ZERO :filterCover.get(0).getPremiumIncludedTaxLc());
					coverRes.setPolicyEndDate(filterCover.get(0).getCoverPeriodTo());
					coverRes.setProRata(filterCover.get(0).getProRataPercent());
					coverRes.setExcessPercent(filterCover.get(0).getExcessPercent());
					coverRes.setExcessAmount(filterCover.get(0).getExcessAmount());
					coverRes.setExcessDesc(filterCover.get(0).getExcessDesc());
					List<SearchCoverDetails>  subCoverListRes = new ArrayList<SearchCoverDetails>();
					List<FactorRateRequestDetails> filterSubCover = covers.stream().filter( o -> o.getDiscLoadId().equals(0) && o.getTaxId().equals(0)).collect(Collectors.toList());
					for ( FactorRateRequestDetails subCovers : filterSubCover) {
						SearchCoverDetails subCoverRes =new SearchCoverDetails();
						subCoverRes = dozerMapper.map(subCovers, SearchCoverDetails.class);
						subCoverRes.setIsSubCover(filterSubCover.get(0).getSubCoverYn());
						subCoverRes.setIsselected(filterSubCover.get(0).getIsSelected());

						subCoverRes.setPremiumAfterDiscount(filterSubCover.get(0).getPremiumAfterDiscountFc());
						subCoverRes.setPremiumBeforeDiscount(filterSubCover.get(0).getPremiumBeforeDiscountFc());
						subCoverRes.setPremiumExcluedTax(filterSubCover.get(0).getPremiumExcludedTaxFc());
						subCoverRes.setPremiumIncludedTax(filterSubCover.get(0).getPremiumIncludedTaxFc());
						subCoverRes.setPremiumAfterDiscountLC(filterSubCover.get(0).getPremiumAfterDiscountLc());
						subCoverRes.setPremiumBeforeDiscountLC( filterSubCover.get(0).getPremiumBeforeDiscountLc());
						subCoverRes.setPremiumExcluedTaxLC(filterSubCover.get(0).getPremiumExcludedTaxLc());
						subCoverRes.setPremiumIncludedTaxLC(filterSubCover.get(0).getPremiumIncludedTaxLc());
						subCoverRes.setRequestReferenceNo(filterSubCover.get(0).getRequestReferenceNo());
						subCoverRes.setVehicleId(filterSubCover.get(0).getVehicleId()==null?"" :filterSubCover.get(0).getVehicleId().toString());
						subCoverRes.setDiffPremiumIncludedTax(filterSubCover.get(0).getDiffPremiumIncludedTaxFc());
						subCoverRes.setDiffPremiumIncludedTaxLC(filterSubCover.get(0).getDiffPremiumIncludedTaxLc());
						subCoverRes.setPolicyEndDate(filterSubCover.get(0).getCoverPeriodTo());
						subCoverRes.setProRata(filterSubCover.get(0).getProRataPercent());
						subCoverRes.setExcessPercent(filterSubCover.get(0).getExcessPercent());
						subCoverRes.setExcessAmount(filterSubCover.get(0).getExcessAmount());
						subCoverRes.setExcessDesc(filterSubCover.get(0).getExcessDesc());
						
						// Discount Covers Or Promo Covers
						List<FactorRateRequestDetails> filterDiscountCover = covers.stream().filter( o -> o.getCoverId().equals(subCovers.getCoverId()) && o.getSubCoverId().equals(subCovers.getSubCoverId()) && ( ! o.getDiscLoadId().equals(0)) && (   o.getCoverageType().equalsIgnoreCase("D") || o.getCoverageType().equalsIgnoreCase("P") ) ).collect(Collectors.toList());
						
						if ( filterDiscountCover.size() > 0 ) {
							 List<SearchDiscount> discounts =  getDiscountRates(filterDiscountCover);
							 subCoverRes.setDiscounts(discounts);	
						}
						
						// Tax Covers
						List<FactorRateRequestDetails> filterTaxCover = covers.stream().filter( o -> o.getCoverId().equals(subCovers.getCoverId()) && o.getSubCoverId().equals(subCovers.getSubCoverId()) && (! o.getTaxId().equals(0)) &&  o.getIsSelected().equalsIgnoreCase("T")).collect(Collectors.toList());
						
						if( filterTaxCover.size() > 0 ) {
							 List<SearchTax> taxes = getTaxRates(filterTaxCover) ;
							 subCoverRes.setTaxes(taxes);	
						}
						
						// Loginds Covers
						List<FactorRateRequestDetails> filterLodingCover = covers.stream().filter( o -> o.getCoverId().equals(subCovers.getCoverId()) && o.getSubCoverId().equals(subCovers.getSubCoverId()) &&  ( ! o.getDiscLoadId().equals(0)) &&  o.getIsSelected().equalsIgnoreCase("L") ).collect(Collectors.toList());
						
						if( filterLodingCover.size() > 0 ) {
							 List<SearchLoading> lodings =  getLodingCovers(filterLodingCover) ;
							 subCoverRes.setLoadings(lodings);	
						}
						subCoverListRes.add(subCoverRes);
					}
					coverRes.setSubcovers(subCoverListRes);
				}
				coverListRes.add(coverRes);
			}
			
			System.out.print("cover sort");
			coverListRes.sort(Comparator.comparing(SearchCoverDetails ::    getSumInsured ).reversed() );
			
		} catch(Exception e){
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
			
		}
		return coverListRes;
	}
	
	public List<SearchDiscount> getDiscountRates(List<FactorRateRequestDetails> filterDiscountCover) {
		List<SearchDiscount> DiscountList = new  ArrayList<SearchDiscount>();
		try {
			for (FactorRateRequestDetails disc :  filterDiscountCover ) {
				SearchDiscount discount = new SearchDiscount();
				discount.setDiscountAmount(disc.getPremiumIncludedTaxFc());
				discount.setDiscountId(disc.getDiscLoadId().toString());
				discount.setDiscountDesc(disc.getCoverName());	
				discount.setDiscountRate(disc.getRate()==null?"0.0" :disc.getRate().toString());
				
				DiscountList.add(discount);
				
			}
			
		} catch(Exception e){
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
			
		}return DiscountList;
	}
	
	
	public List<SearchLoading> getLodingCovers(List<FactorRateRequestDetails> filterLodingCover) {
		List<SearchLoading> LodingList = new  ArrayList<SearchLoading>();
		try {
			for (FactorRateRequestDetails lod :  filterLodingCover ) {
				SearchLoading loding = new SearchLoading();
				loding.setLoadingAmount(lod.getMinimumPremium());
				loding.setLoadingDesc(lod.getCoverName());
				loding.setLoadingId(lod.getDiscLoadId()==null?null:lod.getDiscLoadId().toString());
				loding.setLoadingRate(lod.getRate()==null?null:lod.getRate().toString());
				//loding.setSubCoverId(lod.getLodingSubcoverId()==null?null:lod.getLodingSubcoverId().toString());	
				LodingList.add(loding);
			}
			
		} catch(Exception e){
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
			
		}return LodingList;
	}
	
	public List<SearchTax> getTaxRates(List<FactorRateRequestDetails> filterTaxCover) {
		List<SearchTax> TaxList = new  ArrayList<SearchTax>();
		try {
			for (FactorRateRequestDetails tax :  filterTaxCover ) {
				SearchTax taxes = new SearchTax();
			
				taxes.setTaxAmount(tax.getTaxAmount());
				taxes.setTaxDesc(tax.getTaxDesc());
				taxes.setTaxId(tax.getTaxId()==null?null:tax.getTaxId().toString()) ;
				taxes.setTaxRate( tax.getTaxRate()==null?null : Double.valueOf(tax.getTaxRate().toString()));
				TaxList.add(taxes);
			}
			
		} catch(Exception e){
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
			
		}return TaxList;
	}
	
	public List<Endorsement> getEndorsementRates(List<FactorRateRequestDetails> filterEndtCover ,List<FactorRateRequestDetails> totalCovers) {
		List<Endorsement> endtList = new  ArrayList<Endorsement>();
		try {
			for (FactorRateRequestDetails t :  filterEndtCover ) {
				 Endorsement d=Endorsement.builder()
						 	.endorsementDesc(t.getCoverName()==null?"":t.getCoverName())
						 	.endorsementId(t.getDiscLoadId()==null?"":t.getDiscLoadId().toString())
						 	.endorsementRate("F".equals(t.getCalcType()==null?"A":t.getCalcType())?0D: t.getRate()==null?0D:t.getRate().doubleValue())
						 	.endorsementCalcType(t.getCalcType()==null?"":t.getCalcType())
						 	.endorsementforId(t.getDiscountCoverId()==null?"":t.getDiscountCoverId().toString())
						 	.maxAmount(t.getMinimumPremium()==null?BigDecimal.ZERO:t.getMinimumPremium())
						 	.factorTypeId(t.getFactorTypeId()==null?"":t.getFactorTypeId().toString())
						 	.regulatoryCode(t.getRegulatoryCode()==null?"N/A":t.getRegulatoryCode())	
						 	.premiumAfterDiscount(t.getPremiumAfterDiscountFc())
						    .premiumAfterDiscountLC(t.getPremiumAfterDiscountLc())
						     .premiumBeforeDiscount(t.getPremiumBeforeDiscountFc())
						    .premiumBeforeDiscountLC(t.getPremiumBeforeDiscountLc())
						    .premiumExcluedTax(t.getPremiumExcludedTaxFc())
						    .premiumExcluedTaxLC(t.getPremiumExcludedTaxLc())
						    .premiumIncludedTax(t.getPremiumIncludedTaxFc())
						    .premiumIncludedTaxLC(t.getPremiumIncludedTaxLc())	 
						    .endtCount(t.getEndtCount())
						     .proRata(t.getProRataPercent())
						     .proRataYn(t.getProRataYn())
						 	.build();
				 
				
					
					
				 endtList.add(d);
			}
			
			
			 TaxFromFactor endttaxUtil=new TaxFromFactor();
				if(endtList!=null && endtList.size()>0) {
					for (Endorsement e : endtList) {
						
						// only for endrose we cannt use cover objs tax cover wontbe list.
						 List<Tax> txx = totalCovers.stream().filter(r -> (r.getDiscLoadId()==Integer.parseInt(e.getEndorsementId())
								 && r.getCoverId()==Integer.parseInt(e.getEndorsementforId())
								 && r.getEndtCount().intValue()==e.getEndtCount().intValue()
								 && r.getTaxId() != Integer.parseInt(e.getEndorsementId())
								 )
								 
								  ).map(endttaxUtil).filter(dx->(dx!=null && !"0".equals(dx.getTaxId())) ).collect(Collectors.toList());
						 e.setTaxes(txx);
						 
						 List<Tax> endtfees = totalCovers.stream().filter(r -> (r.getDiscLoadId()==Integer.parseInt(e.getEndorsementId())
								 && r.getCoverId()==Integer.parseInt(e.getEndorsementforId())
								 && r.getEndtCount().intValue()==e.getEndtCount().intValue()
								 && r.getTaxId() == Integer.parseInt(e.getEndorsementId())
								 )
								 
								  ).map(endttaxUtil).filter(dx->(dx!=null && !"0".equals(dx.getTaxId())) ).collect(Collectors.toList());
						 	e.setEndtFees(endtfees);	
						 
					}
				}
			
		} catch(Exception e){
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
			
		}return endtList;
	}

	//Customer Search
	@Override
	public List<SearchCustomerDetailsRes> motorCustSearch(SearchReq req, CompanyProductMaster product) {
		List<SearchCustomerDetailsRes> reslist = new ArrayList<SearchCustomerDetailsRes>();
		DozerBeanMapper dozerMapper  = new DozerBeanMapper(); 
		try {
			List<HomePositionMaster> homeData=null;
			if (StringUtils.isNotBlank(req.getQuoteNo())) {
				homeData = homeRepo.findByQuoteNoAndProductId(req.getQuoteNo(),Integer.valueOf(req.getProductId()));
			String customerId=homeData.get(0).getCustomerId();
			String loginId = "";
			String appId = "";
			String aproverName = "";
			String sourceType="";
			String coustomerCode="";
			String customerCodeName="";
			String userName="";
			String source=""; 
			PersonalInfo list =new PersonalInfo();
			LoginUserInfo loginUserData=new LoginUserInfo();
			List<PremiaCustomerDetails> premiadata =null;
			List<EserviceMotorDetails> motor = repo.findByCustomerId(customerId);
			if (motor.size() > 0) {
				sourceType = motor.get(0).getSourceType();
				coustomerCode = motor.get(0).getCustomerCode();
				loginId = motor.get(0).getLoginId();
				appId = motor.get(0).getApplicationId();
				source = motor.get(0).getLoginId();
				customerCodeName = motor.get(0).getCustomerName();
//				premiadata = premiaRepo.findByCustomerCode(coustomerCode);
//				if (premiadata.size() > 0) {
//					customerCodeName = premiadata.get(0).getCustomerName();
//				}
			}
			list = perRepo.findByCustomerId(homeData.get(0).getCustomerId());
			if ("Broker".equalsIgnoreCase(sourceType)) {
				loginUserData=loginUserInfoRepo.findByLoginId(loginId);
				userName=loginUserData.getUserName();
				
			}
			if(!"1".equalsIgnoreCase(appId)){
				loginUserData=loginUserInfoRepo.findByLoginId(appId);
				aproverName=loginUserData.getUserName();
			}
			SearchCustomerDetailsRes res = new SearchCustomerDetailsRes();
			res = dozerMapper.map(list,SearchCustomerDetailsRes.class);	
			res.setLoginId(loginId);
			res.setBrokerName(userName==null?"":userName);
			res.setApplicationId(appId);
			res.setApproverName(aproverName==null?"":aproverName);
			res.setCustomerCode(coustomerCode);
			res.setCustomerName(customerCodeName);	
			res.setSourceType(sourceType);
			res.setBranchCode(list.getBranchCode().toString());
			res.setSource(source);
			res.setCompanyId(product.getCompanyId());
			res.setCompanyName(homeData.get(0).getCompanyName());
			res.setProductType(product.getMotorYn());	
			reslist.add(res);
			}else if (StringUtils.isNotBlank(req.getRequestReferenceNo())) {
				String customerId="";
				String loginId = "";
				String appId = "";
				String aproverName = "";
				String sourceType="";
				String coustomerCode="";
				String customerCodeName="";
				String userName="";
				String source=""; 
				LoginUserInfo loginUserData=new LoginUserInfo();
				List<EserviceMotorDetails> motor = repo.findByRequestReferenceNo(req.getRequestReferenceNo());
				if (motor.size() > 0) {
					sourceType = motor.get(0).getSourceType();
					coustomerCode = motor.get(0).getCustomerCode();
					loginId = motor.get(0).getLoginId();
					appId = motor.get(0).getApplicationId();
					source = motor.get(0).getLoginId();
					customerCodeName = motor.get(0).getCustomerName();
//					premiadata = premiaRepo.findByCustomerCode(coustomerCode);
//					if (premiadata.size() > 0) {
//						customerCodeName = premiadata.get(0).getCustomerName();
//					}
				}
				EserviceCustomerDetails list = cusrepo.findByCustomerReferenceNo(motor.get(0).getCustomerReferenceNo());
				if ("Broker".equalsIgnoreCase(sourceType)) {
					loginUserData=loginUserInfoRepo.findByLoginId(loginId);
					userName=loginUserData.getUserName();
					
				}
				if(!"1".equalsIgnoreCase(appId)){
					loginUserData=loginUserInfoRepo.findByLoginId(appId);
					aproverName=loginUserData.getUserName();
				}
				SearchCustomerDetailsRes res = new SearchCustomerDetailsRes();
				res = dozerMapper.map(list,SearchCustomerDetailsRes.class);	
				res.setClientName(list.getClientName());
				res.setLoginId(loginId);
				res.setBrokerName(userName==null?"":userName);
				res.setApplicationId(appId);
				res.setApproverName(aproverName==null?"":aproverName);
				res.setCustomerCode(coustomerCode);
				res.setCustomerName(customerCodeName);	
				res.setSourceType(sourceType);
				res.setBranchCode(list.getBranchCode().toString());
				res.setSource(source);
				res.setCompanyId(product.getCompanyId());
				res.setCompanyName(homeData.get(0).getCompanyName());
				res.setProductType(product.getMotorYn());	
				reslist.add(res);	
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return reslist;
	}

	@Override
	public ViewQuoteDetailsRes viewQuoteMotor(ViewQuoteDetailsReq req) {
		ViewQuoteDetailsRes res = new ViewQuoteDetailsRes();
		try {
		
			List<EserviceMotorDetails> motor = repo.findByRequestReferenceNo(req.getRequestReferenceNo());

				if(motor.size()>0) {
					EserviceMotorDetails mot = motor.get(0);
			//		res.setAdminReferralStatus(null);		
					res.setAdminRemarks(mot.getAdminRemarks()==null?"":mot.getAdminRemarks());
					res.setApplicationid(mot.getApplicationId()==null?"":mot.getApplicationId());
					res.setBranchCode(mot.getBranchCode()==null?"":mot.getBranchCode());
			//		res.setCommission(mot.getcomm);
					res.setCommissionPercentage(mot.getCommissionPercentage()==null?"":mot.getCommissionPercentage().toString());
				//	res.setCreditNo(mot.getcre);
					res.setCurrency(mot.getCurrency()==null?"":mot.getCurrency());
					res.setCustomerCode(mot.getCustomerCode()==null?"":mot.getCustomerCode());
					res.setCustomerName(mot.getCustomerName()==null?"":mot.getCustomerName());
				//	res.setDebitNoteNo(mot.getde);
					res.setEndtCount(mot.getEndtCount()==null?"":mot.getEndtCount().toString());
					res.setEndtPremium(mot.getEndtPremium()==null?"":mot.getEndtPremium().toString());
					res.setEndtTypeDesc(mot.getEndorsementTypeDesc()==null?"":mot.getEndorsementTypeDesc().toString());
					res.setEndtTypeId(mot.getEndorsementType()==null?"": mot.getEndorsementType().toString());
					res.setExchangeRate(mot.getExchangeRate()==null?"":mot.getExchangeRate().toString() );
					res.setExpiryDate(mot.getPolicyEndDate()==null?null:mot.getPolicyEndDate() );
					res.setInceptionDate(mot.getPolicyStartDate()==null?null:mot.getPolicyStartDate());
				//	res.setIntegrationStatus(mot.getint);
					res.setLoginid(mot.getLoginId()==null?"":mot.getLoginId());
					res.setOverallPremiumFc(mot.getOverallPremiumFc()==null?"":mot.getOverallPremiumFc().toString() );
					res.setOverallPremiumLc(mot.getOverallPremiumLc()==null?"": mot.getOverallPremiumLc().toString());
			//		res.setPolicyCovertedDate(mot.getdate);
					res.setPolicyPeriod(mot.getPeriodOfInsurance()==null?"":mot.getPeriodOfInsurance());
				//	res.setQuoteCreatedDate(mot.getdate);
					res.setReferralDescription(mot.getReferalRemarks()==null?"":mot.getReferalRemarks());
					res.setSourcetype(mot.getSourceType()==null?"":mot.getSourceType().toString());
					res.setVatCommission(mot.getVatCommission()==null?"":mot.getVatCommission().toString());	
				
				}
			
		}catch(Exception e) {
			e.printStackTrace();
			}
		return res;
	}
}
