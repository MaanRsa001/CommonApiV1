package com.maan.eway.common.service.impl;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
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

import com.maan.eway.bean.BrokerCommissionDetails;
import com.maan.eway.bean.CommonDataDetails;
import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.LoginUserInfo;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.PremiaCustomerDetails;
import com.maan.eway.common.req.SearchEservieMotorDetailsViewRatingRes;
import com.maan.eway.common.req.SearchReq;
import com.maan.eway.common.req.ViewQuoteDetailsReq;
import com.maan.eway.common.res.AdminViewQuoteRes;
import com.maan.eway.common.res.EserviceCommonGetRes;
import com.maan.eway.common.res.SearchCustomerDetailsRes;
import com.maan.eway.common.res.ViewQuoteDetailsRes;
import com.maan.eway.common.service.CommonSearchService;
import com.maan.eway.master.req.CopyQuoteDropDownReq;
import com.maan.eway.repository.CommonDataDetailsRepository;
import com.maan.eway.repository.EserviceCommonDetailsRepository;
import com.maan.eway.repository.EserviceCustomerDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.LoginUserInfoRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.repository.PremiaCustomerDetailsRepository;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SectionDetails;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

@Service
public class CommonSearchServiceImpl implements CommonSearchService{
	
	@Autowired
	private CommonDataDetailsRepository commonDataRepo ;
	
	@Autowired
	private PremiaCustomerDetailsRepository premiaRepo;

	@Autowired
	private EserviceCustomerDetailsRepository cusrepo;
	
	@Autowired
	private PersonalInfoRepository perRepo;
	
	@Autowired
	private HomePositionMasterRepository homeRepo;
	
	@Autowired
	private EserviceCommonDetailsRepository eCommonRepo;
	@Autowired
	private LoginUserInfoRepository loginUserRepo;
	@PersistenceContext
	private EntityManager em;
	
	
	private Logger log = LogManager.getLogger(CommonSearchServiceImpl.class);



	@Override
	public List<Tuple> searchCommon(SearchReq req, List<String> branches) {
		// TODO Auto-generated method stub
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		List<Tuple> searchQuote = new ArrayList<Tuple>();
		try {
			// Search
			String searchKey = req.getSearchKey();
			String searchValue = req.getSearchValue();
			String companyId = req.getInsuranceId();
			String loginId = req.getLoginId();
			String userType = req.getUserType();
			String productId=req.getProductId();

			if ("RequestReferenceNo".equalsIgnoreCase(searchKey)) {
				searchQuote = commonDetails(req,searchKey, searchValue, companyId, loginId, userType, branches,productId);
			} else if ("CustomerReferenceNo".equalsIgnoreCase(searchKey)) {
				searchQuote = commonDetails(req,searchKey, searchValue, companyId, loginId, userType, branches,productId);
			} else if ("CustomerName".equalsIgnoreCase(searchKey)) {
				searchQuote = commonDetails(req,searchKey, searchValue, companyId, loginId, userType, branches,productId);
			} else if ("QuoteNumber".equalsIgnoreCase(searchKey)) {
				searchQuote = commonDetails(req,searchKey, searchValue, companyId, loginId, userType, branches,productId);
			} 
			else if ("MobileNumber".equalsIgnoreCase(searchKey)) {
				searchQuote = commonDetails(req,searchKey, searchValue, companyId, loginId, userType, branches,productId);
			}else if ("PolicyNumber".equalsIgnoreCase(searchKey)) {
				searchQuote = commonDetails(req,searchKey, searchValue, companyId, loginId, userType, branches,productId);
			}
			} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return searchQuote;
	}


	public List<Tuple> commonDetails(SearchReq req,String searchKey, String searchValue, String companyId, String loginId,
			String userType, List<String> branches,String productId) {
		// TODO Auto-generated method stub
		List<Tuple> customerDetailsList = new ArrayList<Tuple>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try {

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

			Root<EserviceCommonDetails> c = query.from(EserviceCommonDetails.class);
			Root<EserviceCustomerDetails> cus = query.from(EserviceCustomerDetails.class);
			
//			query.multiselect(c.alias("c"),
//					cus.get("clientName").alias("clientName"),
//					cus.get("mobileNo1").alias("mobileNumber"),cb.count(c).alias("idsCount"));
			query.multiselect(/*c.alias("c"),*/

					cus.get("customerReferenceNo").alias("customerReferenceNo"),
					cus.get("clientName").alias("clientName"),
					cus.get("mobileNo1").alias("mobileNumber"),cb.count(c).alias("idsCount"),
					cb.max(c.get("requestReferenceNo")).alias("requestReferenceNo"),
					cb.selectCase().when(cb.max(c.get("quoteNo")).isNotNull(), cb.max(c.get("quoteNo")))
							.otherwise(cb.max(c.get("quoteNo"))).alias("quoteNo"),
					cb.max(c.get("policyNo")).alias("policyNo"),
					cb.max(c.get("branchCode")).alias("branchCode"),
					cb.max(c.get("status")).alias("status"),
					cb.max(c.get("loginId")).alias("loginId"), 
					cb.max(c.get("policyStartDate")).alias("policyStartDate"),
					cb.max(c.get("policyEndDate")).alias("policyEndDate"),
					cb.max(c.get("entryDate")).alias("entryDate"),
					cb.max(c.get("overallPremiumLc")).alias("overallPremiumLc"),
					cb.max(c.get("currency")).alias("currency"),
					cb.max(c.get("exchangeRate")).alias("exchangeRate"),
					cb.max(c.get("productDesc")).alias("productDesc")	,
					cb.max(c.get("emiYn")).alias("emiYn"),
					cb.max(c.get("installmentPeriod")).alias("installmentPeriod"),
					cb.max(c.get("noOfInstallment")).alias("noOfInstallment"),
					cb.max(c.get("emiPremium")).alias("emiPremium")
					);


			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("customerReferenceNo")));

			Predicate n1 = null;
			Predicate n3 = null;
			Predicate n4 = null;
			Predicate n5 = null;
	

			// Where
			if (searchKey.equalsIgnoreCase("RequestReferenceNo")) {
				n1 = cb.equal(cb.lower(c.get("requestReferenceNo")), searchValue);
			} else if (searchKey.equalsIgnoreCase("CustomerReferenceNo")) {
				n1 = cb.equal(cb.lower(c.get("customerReferenceNo")), searchValue);
			} else if (searchKey.equalsIgnoreCase("QuoteNumber")) {
				n1 = cb.equal(cb.lower(c.get("quoteNo")), searchValue);
			}
				else if (searchKey.equalsIgnoreCase("MobileNumber")) {
					n1 = cb.equal(cb.lower(cus.get("mobileNo1")), searchValue);
			 }
			 else if (searchKey.equalsIgnoreCase("CustomerName")) {
				n1 = cb.like(cb.lower(cus.get("clientName")), "%" + searchValue + "%");
				n5 = cb.equal(c.get("customerReferenceNo"), cus.get("customerReferenceNo"));
			}else if (searchKey.equalsIgnoreCase("PolicyNumber")) {
				n1 = cb.equal(cb.lower(c.get("policyNo")), searchValue);
			}

			Predicate n2 = cb.equal(c.get("companyId"), companyId);
			Predicate n6 = cb.equal(c.get("productId"), productId);


			if ("issuer".equalsIgnoreCase(userType)) {
//				n3 = cb.equal(c.get("applicationId"), req.getApplicationId());
				Expression<String> e0 = c.get("branchCode");
				n4 = e0.in(branches);
			} else if ("Broker".equalsIgnoreCase(userType) || "User".equalsIgnoreCase(userType)) {
				n3 = cb.equal(c.get("loginId"), loginId);
				Expression<String> e0 = c.get("brokerBranchCode");
				n4 = e0.in(branches);
			}
			if (searchKey.equalsIgnoreCase("CustomerName")) {
				if ("issuer".equalsIgnoreCase(userType)) {

					Expression<String> e0 = cus.get("branchCode");
					n4 = e0.in(branches);
				} else if ("Broker".equalsIgnoreCase(userType) || "User".equalsIgnoreCase(userType)) {

					Expression<String> e0 = cus.get("brokerBranchCode");
					n4 = e0.in(branches);
				}
			}
			n5 = cb.equal(cus.get("customerReferenceNo"), c.get("customerReferenceNo"));
		//	Predicate n6 = cb.isNull(c.get("endtTypeId"));
			if ("Broker".equalsIgnoreCase(userType) || "User".equalsIgnoreCase(userType)) {
			query.where(n1,n2,n3,n4,n5,n6)
			.groupBy(c.get("customerReferenceNo"), cus.get("clientName"),cus.get("mobileNo1"),
					 c.get("requestReferenceNo"), c.get("quoteNo"),
					c.get("customerId"), c.get("policyStartDate"),c.get("policyEndDate"),
					c.get("rejectReason"))
			.orderBy(orderList);
			}else {
				query.where(n1,n2,n4,n5,n6)
				.groupBy(c.get("customerReferenceNo"), cus.get("clientName"),cus.get("mobileNo1"),
						 c.get("requestReferenceNo"), c.get("quoteNo"),
						c.get("customerId"), c.get("policyStartDate"),c.get("policyEndDate"),
						c.get("rejectReason"))
				.orderBy(orderList);	
			}
			if (searchKey.equalsIgnoreCase("CustomerName")) {
				query.where(n1, n2,n4,n5,n6)
				.groupBy(c.get("customerReferenceNo"),cus.get("clientName"),cus.get("mobileNo1"),
					c.get("requestReferenceNo"), c.get("quoteNo"),
						c.get("customerId"), c.get("policyStartDate"), c.get("occupationType"),c.get("policyEndDate"),
						c.get("rejectReason"))
				.orderBy(orderList);
			}
			

			// Get Result
			TypedQuery<Tuple> result = em.createQuery(query);
			customerDetailsList = result.getResultList();
			customerDetailsList = customerDetailsList.stream().filter(o -> !o.get("idsCount").equals(0L))
					.collect(Collectors.toList());
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return customerDetailsList;

}
	
	private static <T> java.util.function.Predicate<T> distinctByKey(java.util.function.Function<? super T, ?> keyExtractor) {
	    Map<Object, Boolean> seen = new ConcurrentHashMap<>();
	    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}	

	@Override
	public List<ListItemValue> searchDropdownCommon(CopyQuoteDropDownReq req) {
		// TODO Auto-generated method stub
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		List<ListItemValue> list = new ArrayList<ListItemValue>();

		try {
			String itemType = "ADMIN_SEARCH_BUILDING";
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
//			
//			for (ListItemValue data : list) {
//				DropDownRes res = new DropDownRes();
//				res.setCode(data.getItemCode());
//				res.setCodeDesc(data.getItemValue());
//				res.setStatus(data.getStatus());
//				resList.add(res);
//			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return list;
		}
	
	@Override
	public AdminViewQuoteRes getCommonProductDetails(SearchReq req) {
		AdminViewQuoteRes viewRes = new AdminViewQuoteRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			// Find Motor Data
			List<CommonDataDetails> commonDatas =  commonDataRepo.findByQuoteNoOrderByRiskIdAsc(req.getQuoteNo());
			
			List<EserviceCommonGetRes>   commonResList = new ArrayList<EserviceCommonGetRes>();
			for (CommonDataDetails com :  commonDatas) {
				// Response
				// Mot
				 List<BrokerCommissionDetails> policylist = getPolicyName(com.getCompanyId() , com.getProductId().toString(), com.getCreatedBy(),com.getAgencyCode(),"99999");
			
				 Double commissionPercent = 0.0;
					if(policylist.size()>0 && policylist!=null) {
					
				 commissionPercent = policylist.get(0).getCommissionPercentage().toString()==null?0: Double.valueOf(policylist.get(0).getCommissionPercentage().toString());	
					}
					else {
						commissionPercent =5.0;
					}
				 String premiumFc = com.getOverallPremiumFc().toString();
				 String vatPremiumFc =	com.getOverallPremiumFc().toString();
				 BigDecimal commission=	new BigDecimal(premiumFc)
			 				.multiply(new BigDecimal(commissionPercent))
	 						.divide(BigDecimal.valueOf(100D))
	 						.setScale(new MathContext(3, RoundingMode.HALF_UP)
	 						.getPrecision(),RoundingMode.HALF_UP);

				
				EserviceCommonGetRes commonDetails = new  EserviceCommonGetRes()  ;
				dozerMapper.map(com, commonDetails);
				commonDetails.setSectionId(com.getSectionId()==null?"":com.getSectionId().toString());
				commonDetails.setOverAllPremiumFc(com.getOverallPremiumFc()==null?0D:Double.valueOf(com.getOverallPremiumFc().toString()));
				commonDetails.setOverAllPremiumLc(com.getOverallPremiumLc()==null?0D:Double.valueOf(com.getOverallPremiumLc().toString()));
				commonDetails.setPremiumFc(com.getActualPremiumFc()==null?0D:Double.valueOf(com.getActualPremiumFc().toString()));
				commonDetails.setPremiumLc(com.getActualPremiumLc()==null?0D:Double.valueOf(com.getActualPremiumLc().toString()));
				commonDetails.setCommissionAmount(commission.toString()==null?"":commission.toString());
				commonDetails.setCommissionPercentage(commissionPercent.toString()==null?"":commissionPercent.toString());

				// Section Details
				SectionDetails sec = new SectionDetails(); 
				sec.setSectionId(com.getSectionId()==null?"":com.getSectionId().toString());
				sec.setSectionName( com.getSectionDesc());
				
				List<SectionDetails>  sectionList = new ArrayList<SectionDetails>();
				sectionList.add(sec);
				commonDetails.setSectionDetails(sectionList);
				commonResList.add(commonDetails);
			}
			viewRes.setRiskDetails(commonResList);	
			
		} catch ( Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return viewRes;
	}

	private List<BrokerCommissionDetails> getPolicyName( String companyId, String productId, String loginId, String agencyCode, String policyType) {
		List<BrokerCommissionDetails> list = new ArrayList<BrokerCommissionDetails>();
		try {
			Date today = new Date();
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<BrokerCommissionDetails> query = cb.createQuery(BrokerCommissionDetails.class);

			// Find All
			Root<BrokerCommissionDetails> b = query.from(BrokerCommissionDetails.class);

			// Select
			query.select(b);

			// Effective Date Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<BrokerCommissionDetails> ocpm1 = amendId.from(BrokerCommissionDetails.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("id"), b.get("id"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			Predicate a4 = cb.equal(ocpm1.get("policyType"), b.get("policyType"));
			Predicate a5 = cb.equal(ocpm1.get("loginId"), b.get("loginId"));
			Predicate a6 = cb.equal(ocpm1.get("agencyCode"), b.get("agencyCode"));
			
			amendId.where(a1,a2,a3,a4,a5,a6);

			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("policyType"), policyType);
			Predicate n3 = cb.equal(b.get("companyId"),companyId);
			Predicate n4 = cb.equal(b.get("productId"),productId);
			Predicate n5 = cb.equal(b.get("loginId"),loginId);
			Predicate n6 = cb.equal(b.get("agencyCode"),agencyCode);
			
			query.where(n1,n2,n3,n4,n5,n6);
			
			// Get Result
			TypedQuery<BrokerCommissionDetails> result = em.createQuery(query);
			list = result.getResultList();		
		
		} catch (Exception e) {
			e.printStackTrace();

		}
		return list;
	}
//Rating
	@Override
	public List<SearchEservieMotorDetailsViewRatingRes> commonRating(SearchReq req) {
		// TODO Auto-generated method stub
		return null;
	}

	// Customer Search

	@Override
	public List<SearchCustomerDetailsRes> commonCustSearch(SearchReq req, CompanyProductMaster product) {
		List<SearchCustomerDetailsRes> reslist = new ArrayList<SearchCustomerDetailsRes>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {List<HomePositionMaster> homeData=null;
		if (StringUtils.isNotBlank(req.getQuoteNo())) {
			homeData = homeRepo.findByQuoteNoAndProductId(req.getQuoteNo(),Integer.valueOf(req.getProductId()));
			String customerId = homeData.get(0).getCustomerId();
			String loginId = "";
			String appId = "";
			String userName = "";
			String aproverName = "";
			String sourceType = "";
			String coustomerCode = "";
			String customerCodeName = "";
			String source = "";
			PersonalInfo list = new PersonalInfo();
			List<PremiaCustomerDetails> premiadata = null;
			LoginUserInfo loginUserData=new LoginUserInfo();
			List<EserviceCommonDetails> motor = eCommonRepo.findByCustomerId(customerId);
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
				loginUserData=loginUserRepo.findByLoginId(loginId);
				userName=loginUserData.getUserName();
				
			}
			if(!"1".equalsIgnoreCase(appId)){
				loginUserData=loginUserRepo.findByLoginId(appId);
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
			// }
		}else if (StringUtils.isNotBlank(req.getRequestReferenceNo())) {
			String loginId = "";
			String appId = "";
			String userName = "";
			String aproverName = "";
			String sourceType = "";
			String coustomerCode = "";
			String customerCodeName = "";
			String source = "";
			LoginUserInfo loginUserData=new LoginUserInfo();
			List<EserviceCommonDetails> motor = eCommonRepo.findByRequestReferenceNo(req.getRequestReferenceNo());
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
			EserviceCustomerDetails list = cusrepo.findByCustomerReferenceNo(motor.get(0).getCustomerReferenceNo());
			if ("Broker".equalsIgnoreCase(sourceType)) {
				loginUserData=loginUserRepo.findByLoginId(loginId);
				userName=loginUserData.getUserName();
				
			}
			if(!"1".equalsIgnoreCase(appId)){
				loginUserData=loginUserRepo.findByLoginId(appId);
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
			// }
		}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return reslist;
	}


	@Override
	public ViewQuoteDetailsRes viewQuoteCommon(ViewQuoteDetailsReq req) {

		ViewQuoteDetailsRes res = new ViewQuoteDetailsRes();
		try {
		
			List<EserviceCommonDetails> motor = eCommonRepo.findByRequestReferenceNo(req.getRequestReferenceNo());

				if(motor.size()>0) {
					EserviceCommonDetails mot = motor.get(0);
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
					res.setPolicyPeriod(mot.getPolicyPeriod()==null?"":mot.getPolicyPeriod().toString());
				//	res.setQuoteCreatedDate(mot.getdate);
					res.setReferralDescription(mot.getReferalRemarks()==null?"":mot.getReferalRemarks());
					res.setSourcetype(mot.getSourceType()==null?"":mot.getSourceType().toString());
					res.setVatCommission(mot.getVatCommission()==null?"":mot.getVatCommission().toString());	
					res.setEndorsementYn( mot.getEndorsementType()==null?"N":"Y");
				}
			
		}catch(Exception e) {
			e.printStackTrace();
			}
		return res;
	
	
	}



}


