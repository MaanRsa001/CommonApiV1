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

import com.maan.eway.admin.res.GetBuildingAdminReferalPendingDetailsRes;
import com.maan.eway.admin.res.ReferalCommonCriteriaRes;
import com.maan.eway.admin.res.ReferalGridCriteriaAdminRes;
import com.maan.eway.bean.BuildingDetails;
import com.maan.eway.bean.CommonDataDetails;
import com.maan.eway.bean.CoverMaster;
import com.maan.eway.bean.DocumentTransactionDetails;
import com.maan.eway.bean.EndtTypeMaster;
import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.EserviceSectionDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.LoginMaster;
import com.maan.eway.bean.LoginUserInfo;
import com.maan.eway.bean.MotorDataDetails;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.PolicyCoverData;
import com.maan.eway.bean.ProductEmployeeDetails;
import com.maan.eway.bean.SectionDataDetails;
import com.maan.eway.bean.SeqCustid;
import com.maan.eway.bean.SeqCustrefno;
import com.maan.eway.bean.SeqQuoteno;
import com.maan.eway.bean.UWReferralDetails;
import com.maan.eway.calculator.util.RatingFactorsUtil;
import com.maan.eway.common.req.CopyQuoteReq;
import com.maan.eway.common.req.ExistingBrokerUserListReq;
import com.maan.eway.common.req.ExistingQuoteReq;
import com.maan.eway.common.req.RevertGridReq;
import com.maan.eway.common.req.SequenceGenerateReq;
import com.maan.eway.common.res.GetCommonReferalDetailsRes;
import com.maan.eway.common.res.GetExistingBrokerListRes;
import com.maan.eway.common.res.GetMotorProtfolioPendingRes;
import com.maan.eway.common.res.GetRejectedQuoteDetailsRes;
import com.maan.eway.common.res.PortfolioPendingGridCriteriaRes;
import com.maan.eway.common.res.QuoteCriteriaRes;
import com.maan.eway.common.res.QuoteCriteriaResponse;
import com.maan.eway.common.res.QuoteHPMRes;
import com.maan.eway.common.res.RejectCriteriaRes;
import com.maan.eway.common.service.CommonGridService;
import com.maan.eway.master.req.CopyQuoteDropDownReq;
import com.maan.eway.repository.BuildingDetailsRepository;
import com.maan.eway.repository.CommonDataDetailsRepository;
import com.maan.eway.repository.DocumentTransactionDetailsRepository;
import com.maan.eway.repository.EServiceSectionDetailsRepository;
import com.maan.eway.repository.EndtTypeMasterRepository;
import com.maan.eway.repository.EserviceBuildingDetailsRepository;
import com.maan.eway.repository.EserviceCommonDetailsRepository;
import com.maan.eway.repository.EserviceCustomerDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.MotorDataDetailsRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.repository.PolicyCoverDataRepository;
import com.maan.eway.repository.ProductEmployeesDetailsRepository;
import com.maan.eway.repository.SectionDataDetailsRepository;
import com.maan.eway.repository.SeqCustidRepository;
import com.maan.eway.repository.SeqCustrefnoRepository;
import com.maan.eway.repository.SeqQuotenoRepository;
import com.maan.eway.repository.SeqRefnoRepository;
import com.maan.eway.res.CopyQuoteSuccessRes;

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
@Transactional
public class CommonGridServiceImpl implements CommonGridService {
	@PersistenceContext
	private EntityManager em;

	private Logger log = LogManager.getLogger(CommonGridServiceImpl.class);

	@Autowired
	private BuildingDetailsRepository buildingRepo;
	@Autowired
	private HomePositionMasterRepository homePosistionRepo;
	
	@Autowired
	private CommonDataDetailsRepository commonDataRepo;
	
	@Autowired
	private PersonalInfoRepository personalInforepo;
	
	@Autowired
	private PolicyCoverDataRepository policyCoverDataRepo;
	
	@Autowired
	private MotorDataDetailsRepository motorDataDetepo;
	
	@Autowired
	private EserviceCommonDetailsRepository repo;
	
	@Autowired
	private EserviceCustomerDetailsRepository custRepo ;
	
	@Autowired
	private GenerateSeqNoServiceImpl seqNo ;
	
	@Autowired
	private SeqQuotenoRepository quoteNoRepo ;
	
	@Autowired
	private SeqCustidRepository custIdRepo ;

	@Autowired
	private SeqRefnoRepository refNoRepo ;
	
	@Autowired
	private ProductEmployeesDetailsRepository pARepo;
	@Autowired
	private SeqCustrefnoRepository custRefRepo  ;

	@Autowired
	private EndtTypeMasterRepository endtTypeRepo;
	
	@Autowired
	private DocumentTransactionDetailsRepository coverDocUploadDetails;

	@Autowired 
	private RatingFactorsUtil ratingutil;

	@Autowired
	private SectionDataDetailsRepository sectionDataRepo;
	@Autowired
	private EServiceSectionDetailsRepository eserSecRepo;
	
	@Autowired
	private EserviceBuildingDetailsRepository eseBuildingRepo;
	
	 @Autowired
     private GenerateSeqNoServiceImpl genSeqNoService ;

	 
	// Exiting Common Details
	//DropDown
		@Override
		public List<GetExistingBrokerListRes> getCommonExistingDropdown(ExistingBrokerUserListReq req, Date today, Date before30) {
			List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
			List<Tuple> list = new ArrayList<Tuple>();
			try {
			if(!("issuer".equalsIgnoreCase(req.getUserType()))){		
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

				Root<EserviceCommonDetails> m = query.from(EserviceCommonDetails.class);
				Root<LoginUserInfo> us = query.from(LoginUserInfo.class);
				query.multiselect(m.get("loginId").alias("code"),us.get("userName").alias("codeDesc"),
						m.get("sourceType").alias("type")).distinct(true);

				// Find All
				Subquery<Long> agencyCode = query.subquery(Long.class);
				Root<LoginMaster> ocpm1 = agencyCode.from(LoginMaster.class);
				agencyCode.select(ocpm1.get("agencyCode"));
				Predicate a1 = cb.equal(ocpm1.get("loginId"), req.getLoginId());
				Predicate a3 = cb.equal(ocpm1.get("status"), "Y");
				agencyCode.where(a1, a3);

				List<Predicate> predics1 = new ArrayList<Predicate>();
				predics1.add(cb.equal(m.get("applicationId"), req.getApplicationId()));
				predics1.add(cb.equal(m.get("status"), "Y"));
				predics1.add(cb.equal(m.get("productId"), req.getProductId()));
				predics1.add(cb.equal(m.get("companyId"), req.getCompanyId()));
				predics1.add(cb.equal(m.get("branchCode"), req.getBranchCode()));
				predics1.add(cb.greaterThanOrEqualTo(m.get("updatedDate"), before30));
				predics1.add(cb.lessThanOrEqualTo(m.get("updatedDate"), today));
				if ("Broker".equalsIgnoreCase(req.getUserType())) {
					predics1.add(cb.equal(m.get("brokerCode").as(Long.class), agencyCode));
				} else if ("User".equalsIgnoreCase(req.getUserType())) {
					predics1.add(cb.equal(m.get("agencyCode").as(Long.class), agencyCode));
				}
				predics1.add(cb.isNotNull(m.get("sourceType")));
				predics1.add(cb.isNotNull(m.get("loginId")));
				predics1.add(cb.equal(us.get("loginId"), m.get("loginId")));
				query.where(predics1.toArray(new Predicate[0]));

				TypedQuery<Tuple> typedQuery1 = em.createQuery(query);
				list = typedQuery1.getResultList();
				if (list != null && list.size() > 0) {
					list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.get("code")))).collect(Collectors.toList());
				for (Tuple data : list) {
						GetExistingBrokerListRes res = new GetExistingBrokerListRes();
						res.setCode(data.get("code") == null ? "" : data.get("code").toString());
						res.setCodeDesc(data.get("codeDesc") == null ? "" : data.get("codeDesc").toString());
						res.setType(data.get("type") == null ? "" : data.get("type").toString().toLowerCase().replaceAll("premia ", ""));
						resList.add(res);
					
							}
				}
				}else {
					
					resList=getExistingIssuerMotor(req,today,before30);
				}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resList;
	}    
	 
	private List<GetExistingBrokerListRes> getExistingIssuerMotor(ExistingBrokerUserListReq req, Date today,
			Date before30) {
		List<Tuple> list = new ArrayList<Tuple>();
		List<Tuple> list1 = new ArrayList<Tuple>();
		List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
		try {
			{CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

			Root<EserviceCommonDetails> m = query.from(EserviceCommonDetails.class);

			query.multiselect(m.get("bdmCode").alias("code"), m.get("bdmName").alias("codeDesc"),m.get("sourceType").alias("type"));
			List<Predicate> predics = new ArrayList<Predicate>();
			predics.add(cb.equal(m.get("applicationId"), req.getApplicationId()));
			predics.add(cb.equal(m.get("status"), "Y"));
			predics.add(cb.equal(m.get("productId"), req.getProductId()));
			predics.add(cb.equal(m.get("companyId"), req.getCompanyId()));
			predics.add(cb.equal(m.get("branchCode"), req.getBranchCode()));
			predics.add(cb.greaterThanOrEqualTo(m.get("updatedDate"), before30));
			predics.add(cb.lessThanOrEqualTo(m.get("updatedDate"), today));
			predics.add(cb.isNotNull(m.get("bdmCode")));
			query.where(predics.toArray(new Predicate[0]));

			TypedQuery<Tuple> typedQuery = em.createQuery(query);
			list = typedQuery.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.get("code")))).collect(Collectors.toList());
			if (list != null && list.size() > 0) {

				for (Tuple data : list) {
					GetExistingBrokerListRes res = new GetExistingBrokerListRes();
					res.setCode(data.get("code") == null ? "" : data.get("code").toString());
					res.setCodeDesc(data.get("codeDesc") == null ? "" : data.get("codeDesc").toString());
//					res.setType(data.get("type") == null ? "" : data.get("type").toString());
					String type = data.get("type") == null ? "" : data.get("type").toString();
					type = "Premia " + type;
					res.setType(type);
					resList.add(res);

				}
			}
			}
			{CriteriaBuilder cb1 = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> query1 = cb1.createQuery(Tuple.class);

			Root<EserviceCommonDetails> m1 = query1.from(EserviceCommonDetails.class);
			Root<LoginUserInfo> us = query1.from(LoginUserInfo.class);
			query1.multiselect (m1.get("loginId").alias("code"),us.get("userName").alias("codeDesc"),
					m1.get("sourceType").alias("type"));

			List<Predicate> predics1 = new ArrayList<Predicate>();
			predics1.add(cb1.equal(m1.get("applicationId"), req.getApplicationId()));
			predics1.add(cb1.equal(m1.get("status"), "Y"));
			predics1.add(cb1.equal(m1.get("productId"), req.getProductId()));
			predics1.add(cb1.equal(m1.get("companyId"), req.getCompanyId()));
			predics1.add(cb1.equal(m1.get("branchCode"), req.getBranchCode()));
			predics1.add(cb1.greaterThanOrEqualTo(m1.get("updatedDate"), before30));
			predics1.add(cb1.lessThanOrEqualTo(m1.get("updatedDate"), today));
			predics1.add(cb1.isNull(m1.get("bdmCode")));
			predics1.add(cb1.equal(us.get("loginId"), m1.get("loginId")));
			query1.where(predics1.toArray(new Predicate[0]));

			TypedQuery<Tuple> typedQuery1 = em.createQuery(query1);
			list1 = typedQuery1.getResultList();
			list1 = list1.stream().filter(distinctByKey(o -> Arrays.asList(o.get("code")))).collect(Collectors.toList());
			if (list1 != null && list1.size() > 0) {

				for (Tuple data : list1) {
					GetExistingBrokerListRes res = new GetExistingBrokerListRes();
					res.setCode(data.get("code") == null ? "" : data.get("code").toString());
					res.setCodeDesc(data.get("codeDesc") == null ? "" : data.get("codeDesc").toString());
					res.setType(data.get("type") == null ? "" : data.get("type").toString());
					resList.add(res);

				}
			}
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resList;
	}

	@Override
	public QuoteCriteriaResponse getCommonExistingQuoteDetails(ExistingQuoteReq req,Date startDate, Date endDate, Integer limit, Integer offset) {
		List<QuoteCriteriaRes> existingQuotes = new ArrayList<QuoteCriteriaRes>();
		QuoteCriteriaResponse resp = new QuoteCriteriaResponse();
		try {

			// Get Datas
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<QuoteCriteriaRes> query = cb.createQuery(QuoteCriteriaRes.class);

			// Find All
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
			Root<EserviceCommonDetails> m = query.from(EserviceCommonDetails.class);

			//overallPremiumLc
			Subquery<Long> overallPremiumLc = query.subquery(Long.class);
			Root<EserviceCommonDetails> ocpm1 = overallPremiumLc.from(EserviceCommonDetails.class);
			overallPremiumLc.select(cb.sum(ocpm1.get("overallPremiumLc")));
			Predicate a1 = cb.equal(ocpm1.get("requestReferenceNo"), m.get("requestReferenceNo"));
			overallPremiumLc.where(a1);
			
			//overallPremiumFc
			Subquery<Long> overallPremiumFc = query.subquery(Long.class);
			Root<EserviceCommonDetails> oc = overallPremiumFc.from(EserviceCommonDetails.class);
			overallPremiumFc.select(cb.sum(oc.get("overallPremiumFc")));
			Predicate a2 = cb.equal(oc.get("requestReferenceNo"), m.get("requestReferenceNo"));
			overallPremiumFc.where(a2);
		
			// Select
			query.multiselect(
					
					// Customer Info
					c.get("customerReferenceNo").alias("customerReferenceNo"), c.get("idNumber").alias("idNumber"),
					c.get("clientName").alias("clientName"),
					// Vehicle Info
					m.get("companyId").alias("companyId"), m.get("productId").alias("productId"),
					m.get("productDesc").alias("productName"),
					m.get("branchCode").alias("branchCode"), m.get("requestReferenceNo").alias("requestReferenceNo"),
					m.get("quoteNo").alias("quoteNo"),
					m.get("customerId").alias("customerId"),
					m.get("policyStartDate").alias("policyStartDate"), m.get("policyEndDate").alias("policyEndDate"),
					overallPremiumLc.alias("overallPremiumLc"), 
					overallPremiumFc.alias("overallPremiumFc"),
					m.get("currency").alias("currency"),
					cb.selectCase().when(m.get("companyId").isNotNull(), "").alias("savedFrom")
					);
			

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(m.get("updatedDate")));

			// Where
			Predicate n1 = cb.equal(c.get("customerReferenceNo"), m.get("customerReferenceNo"));
			Predicate n2 = cb.equal(m.get("companyId"), req.getInsuranceId());
			Predicate n3 = cb.equal(m.get("productId"), req.getProductId());
			Predicate n4 = cb.equal(m.get("status"), "Y");
			Predicate n5 = cb.lessThanOrEqualTo(m.get("updatedDate"), endDate);
			Predicate n6 = cb.greaterThanOrEqualTo(m.get("updatedDate"), startDate);
			Predicate n9 = cb.isNull(m.get("endorsementType"));
			Predicate n7 = null;
			Predicate n11 = null;
			
			n7 = cb.equal(m.get("applicationId"), req.getApplicationId());
			if(StringUtils.isNotBlank(req.getBdmCode())){
				
				n11 = cb.equal(m.get("bdmCode"), req.getBdmCode());
				
			}else {
				
				n11= cb.equal(m.get("loginId"), req.getLoginId());
			}

			Predicate n8 = null;
			if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
			
				n8 = cb.equal(  m.get("brokerBranchCode"),  req.getBrokerBranchCode());
			} else {
				
				n8 = cb.equal(  m.get("branchCode"),  req.getBranchCode());
			}
			// Risk Max Filter
			Subquery<Long> riskId = query.subquery(Long.class);
			Root<EserviceCommonDetails> ocp = riskId.from(EserviceCommonDetails.class);
			riskId.select(cb.max(ocp.get("riskId")));
			Predicate a3 = cb.equal(ocp.get("requestReferenceNo"), m.get("requestReferenceNo"));
			riskId.where(a3);

			Predicate n10 = cb.equal(m.get("riskId"), riskId);
			query.where(n1, n2, n3, n4, n5, n6, n7, n8,n9,n10,n11).orderBy(orderList);
			
			// Get Result
			TypedQuery<QuoteCriteriaRes> result = em.createQuery(query);
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			existingQuotes = result.getResultList();
			existingQuotes = result.getResultList();
			if (existingQuotes != null && existingQuotes.size() > 0) {
				existingQuotes = existingQuotes.stream().filter(distinctByKey(o -> Arrays.asList(o.getRequestReferenceNo()))).collect(Collectors.toList());
			}else {
				existingQuotes=null;
			}
			resp.setQuoteRes(existingQuotes);
			resp.setTotalCount(totalcountexisting(req, startDate,endDate, "Y"));
			
		
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resp;
	}

	private Long totalcountexisting(ExistingQuoteReq req, Date startDate, Date endDate, String status) {
		Long count = 0l;
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> query = cb.createQuery(Long.class);

			// Find All
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
			Root<EserviceCommonDetails> m = query.from(EserviceCommonDetails.class);

			//overallPremiumLc
			Subquery<Long> overallPremiumLc = query.subquery(Long.class);
			Root<EserviceCommonDetails> ocpm1 = overallPremiumLc.from(EserviceCommonDetails.class);
			overallPremiumLc.select(cb.sum(ocpm1.get("overallPremiumLc")));
			Predicate a1 = cb.equal(ocpm1.get("requestReferenceNo"), m.get("requestReferenceNo"));
			overallPremiumLc.where(a1);
			
			//overallPremiumFc
			Subquery<Long> overallPremiumFc = query.subquery(Long.class);
			Root<EserviceCommonDetails> oc = overallPremiumFc.from(EserviceCommonDetails.class);
			overallPremiumFc.select(cb.sum(oc.get("overallPremiumFc")));
			Predicate a2 = cb.equal(oc.get("requestReferenceNo"), m.get("requestReferenceNo"));
			overallPremiumFc.where(a2);
		
			// Select
			query.select(cb.countDistinct(m.get("requestReferenceNo"))); 
			
			// Where
			Predicate n1 = cb.equal(c.get("customerReferenceNo"), m.get("customerReferenceNo"));
			Predicate n2 = cb.equal(m.get("companyId"), req.getInsuranceId());
			Predicate n3 = cb.equal(m.get("productId"), req.getProductId());
			Predicate n4 = cb.equal(m.get("status"), status);
			Predicate n5 = cb.lessThanOrEqualTo(m.get("updatedDate"), endDate);
			Predicate n6 = cb.greaterThanOrEqualTo(m.get("updatedDate"), startDate);
			Predicate n9 = cb.isNull(m.get("endorsementType"));
			Predicate n7 = null;
			Predicate n11 = null;
			n7 = cb.equal(m.get("applicationId"), req.getApplicationId());
			if(StringUtils.isNotBlank(req.getBdmCode())){
				
				n11= cb.equal(m.get("bdmCode"), req.getBdmCode());
				
			}else {
				
				n11 = cb.equal(m.get("loginId"), req.getLoginId());
			}
			Predicate n8 = null;
			if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
			
				n8 = cb.equal(  m.get("brokerBranchCode"), req.getBrokerBranchCode());
			} else {
				
				n8 = cb.equal(  m.get("branchCode"),  req.getBranchCode());
			}
			// Risk Max Filter
			Subquery<Long> riskId = query.subquery(Long.class);
			Root<EserviceCommonDetails> ocp = riskId.from(EserviceCommonDetails.class);
			riskId.select(cb.max(ocp.get("riskId")));
			Predicate a3 = cb.equal(ocp.get("requestReferenceNo"), m.get("requestReferenceNo"));
			riskId.where(a3);

			Predicate n10 = cb.equal(m.get("riskId"), riskId);

			query.where(n1, n2, n3, n4, n5, n6, n7, n8, n9, n10, n11);

			TypedQuery<Long> result = em.createQuery(query);
			List<Long> val = result.getResultList();
				
			if(val.size()>0)
				count = val.get(0);		
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return count;
	}

	@Override
	public QuoteCriteriaResponse getCommonLapsedQuoteDetails(ExistingQuoteReq req,  Date before30,int limit, int offset) {
			
		QuoteCriteriaResponse resp = new QuoteCriteriaResponse();
		List<QuoteCriteriaRes> lapsedQuotes = new ArrayList<QuoteCriteriaRes>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<QuoteCriteriaRes> query = cb.createQuery(QuoteCriteriaRes.class);

			// Find All
			Root<EserviceCommonDetails> m = query.from(EserviceCommonDetails.class);
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);

			//overallPremiumLc
			Subquery<Long> overallPremiumLc = query.subquery(Long.class);
			Root<EserviceCommonDetails> ocpm1 = overallPremiumLc.from(EserviceCommonDetails.class);
			overallPremiumLc.select(cb.sum(ocpm1.get("overallPremiumLc")));
			Predicate a1 = cb.equal(ocpm1.get("requestReferenceNo"), m.get("requestReferenceNo"));
			overallPremiumLc.where(a1);
			
			//overallPremiumFc
			Subquery<Long> overallPremiumFc = query.subquery(Long.class);
			Root<EserviceCommonDetails> oc = overallPremiumFc.from(EserviceCommonDetails.class);
			overallPremiumFc.select(cb.sum(oc.get("overallPremiumFc")));
			Predicate a2 = cb.equal(oc.get("requestReferenceNo"), m.get("requestReferenceNo"));
			overallPremiumFc.where(a2);
		
			// Select
			query.multiselect(
					
					// Customer Info
					c.get("customerReferenceNo").alias("customerReferenceNo"), c.get("idNumber").alias("idNumber"),
					c.get("clientName").alias("clientName"),
					// Vehicle Info
					m.get("companyId").alias("companyId"), m.get("productId").alias("productId"),
					m.get("productDesc").alias("productName"),
					m.get("branchCode").alias("branchCode"), m.get("requestReferenceNo").alias("requestReferenceNo"),
					m.get("quoteNo").alias("quoteNo"),
					m.get("customerId").alias("customerId"),
					m.get("policyStartDate").alias("policyStartDate"), m.get("policyEndDate").alias("policyEndDate"),
					overallPremiumLc.alias("overallPremiumLc"), 
					overallPremiumFc.alias("overallPremiumFc"),
					m.get("currency").alias("currency"),
					//This Line is for empty string
					cb.selectCase().when(m.get("companyId").isNotNull(), "").alias("savedFrom")
					);
			

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(m.get("updatedDate")));

			// Where
			Predicate n1 = cb.equal(c.get("customerReferenceNo"), m.get("customerReferenceNo"));
			Predicate n2 = cb.equal(m.get("companyId"), req.getInsuranceId());
			Predicate n3 = cb.equal(m.get("productId"), req.getProductId());
			Predicate n4 = cb.equal(m.get("status"), "Y");
			Predicate n5 = cb.lessThanOrEqualTo(m.get("updatedDate"), before30);
			Predicate n9 = cb.isNull(m.get("endorsementType"));
			Predicate n7 = null;
			Predicate n11 = null;
			
			n7 = cb.equal(m.get("applicationId"), req.getApplicationId());
			if (StringUtils.isNotBlank(req.getBdmCode())) {

				n11 = cb.equal(m.get("bdmCode"), req.getBdmCode());

			} else {

				n11 = cb.equal(m.get("loginId"), req.getLoginId());
			}

			Predicate n8 = null;
			if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
			
				n8 = cb.equal(  m.get("brokerBranchCode"),  req.getBrokerBranchCode());
			} else {
				
				n8 = cb.equal(  m.get("branchCode"),  req.getBranchCode());
			}
			// Risk Max Filter
			Subquery<Long> riskId = query.subquery(Long.class);
			Root<EserviceCommonDetails> ocp = riskId.from(EserviceCommonDetails.class);
			riskId.select(cb.max(ocp.get("riskId")));
			Predicate a3 = cb.equal(ocp.get("requestReferenceNo"), m.get("requestReferenceNo"));
			riskId.where(a3);

			Predicate n10 = cb.equal(m.get("riskId"),  riskId );
			query.where(n1, n2, n3, n4, n5,n7, n8,n9,n10,n11).orderBy(orderList);

			// Get Result
			TypedQuery<QuoteCriteriaRes> result = em.createQuery(query);
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			lapsedQuotes = result.getResultList();
			if (lapsedQuotes != null && lapsedQuotes.size() > 0) {
				lapsedQuotes = lapsedQuotes.stream().filter(distinctByKey(o -> Arrays.asList(o.getRequestReferenceNo()))).collect(Collectors.toList());
			}else {
				lapsedQuotes=null;
			}
			resp.setQuoteRes(lapsedQuotes);
			resp.setTotalCount(tatallapsedQuotes(req, before30));
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resp;
	}

	private Long tatallapsedQuotes(ExistingQuoteReq req, Date before30) {
		Long count = 0l;
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> query = cb.createQuery(Long.class);

			// Find All
			Root<EserviceCommonDetails> m = query.from(EserviceCommonDetails.class);
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);

			//overallPremiumLc
			Subquery<Long> overallPremiumLc = query.subquery(Long.class);
			Root<EserviceCommonDetails> ocpm1 = overallPremiumLc.from(EserviceCommonDetails.class);
			overallPremiumLc.select(cb.sum(ocpm1.get("overallPremiumLc")));
			Predicate a1 = cb.equal(ocpm1.get("requestReferenceNo"), m.get("requestReferenceNo"));
			overallPremiumLc.where(a1);
			
			//overallPremiumFc
			Subquery<Long> overallPremiumFc = query.subquery(Long.class);
			Root<EserviceCommonDetails> oc = overallPremiumFc.from(EserviceCommonDetails.class);
			overallPremiumFc.select(cb.sum(oc.get("overallPremiumFc")));
			Predicate a2 = cb.equal(oc.get("requestReferenceNo"), m.get("requestReferenceNo"));
			overallPremiumFc.where(a2);
		
			// Select
			query.select(cb.countDistinct(m.get("requestReferenceNo"))); 
			
			Predicate n1 = cb.equal(c.get("customerReferenceNo"), m.get("customerReferenceNo"));
			Predicate n2 = cb.equal(m.get("companyId"), req.getInsuranceId());
			Predicate n3 = cb.equal(m.get("productId"), req.getProductId());
			Predicate n4 = cb.equal(m.get("status"), "Y");
			Predicate n5 = cb.lessThanOrEqualTo(m.get("updatedDate"), before30);
			Predicate n9 = cb.isNull(m.get("endorsementType"));
			Predicate n7 = null;
			Predicate n11 = null;
			
			n7 = cb.equal(m.get("applicationId"), req.getApplicationId());
			if (StringUtils.isNotBlank(req.getBdmCode())) {

				n11 = cb.equal(m.get("bdmCode"), req.getBdmCode());

			} else {

				n11 = cb.equal(m.get("loginId"), req.getLoginId());
			}

			Predicate n8 = null;
			if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
			
				n8 = cb.equal(  m.get("brokerBranchCode"),  req.getBrokerBranchCode());
			} else {
				
				n8 = cb.equal(  m.get("branchCode"),  req.getBranchCode());
			}
			// Risk Max Filter
			Subquery<Long> riskId = query.subquery(Long.class);
			Root<EserviceCommonDetails> ocp = riskId.from(EserviceCommonDetails.class);
			riskId.select(cb.max(ocp.get("riskId")));
			Predicate a3 = cb.equal(ocp.get("requestReferenceNo"), m.get("requestReferenceNo"));
			riskId.where(a3);

			Predicate n10 = cb.equal(m.get("riskId"), riskId);
		
			query.where(n1, n2, n3, n4, n5,n7, n8,n9,n10,n11);
			
			TypedQuery<Long> result = em.createQuery(query);
			List<Long> val = result.getResultList();
				
			if(val.size()>0)
				count = val.get(0);		
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return count;
	}

	@Override
	public GetRejectedQuoteDetailsRes getCommonRejectedQuoteDetails(ExistingQuoteReq req, Date startDate ,Date  endDate , int limit,
			int offset) {
		GetRejectedQuoteDetailsRes resp = new GetRejectedQuoteDetailsRes();
		List<RejectCriteriaRes> rejectedQuotes = new ArrayList<RejectCriteriaRes>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<RejectCriteriaRes> query = cb.createQuery(RejectCriteriaRes.class);

			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
			Root<EserviceCommonDetails> m = query.from(EserviceCommonDetails.class);

			//overallPremiumLc
			Subquery<Long> overallPremiumLc = query.subquery(Long.class);
			Root<EserviceCommonDetails> ocpm1 = overallPremiumLc.from(EserviceCommonDetails.class);
			overallPremiumLc.select(cb.sum(ocpm1.get("overallPremiumLc")));
			Predicate a1 = cb.equal(ocpm1.get("requestReferenceNo"), m.get("requestReferenceNo"));
			overallPremiumLc.where(a1);
			
			//overallPremiumFc
			Subquery<Long> overallPremiumFc = query.subquery(Long.class);
			Root<EserviceCommonDetails> oc = overallPremiumFc.from(EserviceCommonDetails.class);
			overallPremiumFc.select(cb.sum(oc.get("overallPremiumFc")));
			Predicate a2 = cb.equal(oc.get("requestReferenceNo"), m.get("requestReferenceNo"));
			overallPremiumFc.where(a2);
		
			// Select
			query.multiselect(
					
					// Customer Info
					c.get("customerReferenceNo").alias("customerReferenceNo"), c.get("idNumber").alias("idNumber"),
					c.get("clientName").alias("clientName"),
					// Vehicle Info
					m.get("companyId").alias("companyId"), m.get("productId").alias("productId"),
					m.get("branchCode").alias("branchCode"), m.get("requestReferenceNo").alias("requestReferenceNo"),
					m.get("quoteNo").alias("quoteNo"),
					m.get("customerId").alias("customerId"),
					m.get("policyStartDate").alias("policyStartDate"), m.get("policyEndDate").alias("policyEndDate"),
					m.get("rejectReason").alias("rejectReason"),
					overallPremiumLc.alias("overallPremiumLc"), 
					overallPremiumFc.alias("overallPremiumFc"),
					m.get("currency").alias("currency")
					);
			

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(m.get("updatedDate")));

			// Where
			Predicate n1 = cb.equal(c.get("customerReferenceNo"), m.get("customerReferenceNo"));
			Predicate n2 = cb.equal(m.get("companyId"), req.getInsuranceId());
			Predicate n3 = cb.equal(m.get("productId"), req.getProductId());
			Predicate n4 = cb.equal(m.get("status"), "R");
			Predicate n5 = cb.lessThanOrEqualTo(m.get("updatedDate"), endDate);
			Predicate n6 = cb.greaterThanOrEqualTo(m.get("updatedDate"), startDate);
			Predicate n9 = cb.isNull(m.get("endorsementType"));
			Predicate n7 = null;
			Predicate n11 = null;
//			if (req.getApplicationId().equalsIgnoreCase("1")) {
//				n7 = cb.equal(m.get("loginId"), req.getLoginId());
//				n7 = cb.equal(m.get("applicationId"), req.getApplicationId());
//			} else {
				n7 = cb.equal(m.get("applicationId"), req.getApplicationId());
				if(StringUtils.isNotBlank(req.getBdmCode())){
					n11 = cb.equal(m.get("bdmCode"), req.getBdmCode());
				}else {
					n11 = cb.equal(m.get("loginId"), req.getLoginId());
				}
//			}

			Predicate n8 = null;
			if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
			
				n8 = cb.equal(  m.get("brokerBranchCode"), req.getBrokerBranchCode());
			} else {
				
				n8 = cb.equal(  m.get("branchCode"),  req.getBranchCode());
			}
			// Risk Max Filter
			Subquery<Long> riskId = query.subquery(Long.class);
			Root<EserviceCommonDetails> ocp = riskId.from(EserviceCommonDetails.class);
			riskId.select(cb.max(ocp.get("riskId")));
			Predicate a3 = cb.equal(ocp.get("requestReferenceNo"), m.get("requestReferenceNo"));
			riskId.where(a3);

			Predicate n10 = cb.equal(m.get("riskId"), riskId==null?null:riskId);
			query.where(n1, n2, n3, n4, n5, n6, n7, n8,n9,n10,n11).orderBy(orderList);
			
			// Get Result
			TypedQuery<RejectCriteriaRes> result = em.createQuery(query);
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			rejectedQuotes = result.getResultList();
			if (rejectedQuotes != null && rejectedQuotes.size() > 0) {
				rejectedQuotes = rejectedQuotes.stream().filter(distinctByKey(o -> Arrays.asList(o.getRequestReferenceNo()))).collect(Collectors.toList());
			}else {
				rejectedQuotes=null;
			}
			resp.setQuoteRes(rejectedQuotes);
			resp.setTotalCount(totalcountexisting(req, startDate,endDate, "R"));
		
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resp;
	}


	@Override
	public synchronized GetCommonReferalDetailsRes getCommonReferalDetails(ExistingQuoteReq req, 
			int limit, int offset, String status) {
		
		List<ReferalCommonCriteriaRes> referrals = new ArrayList<ReferalCommonCriteriaRes>();
		GetCommonReferalDetailsRes resp = new GetCommonReferalDetailsRes();
		
		try {
			resp.setTotalCount(0);
			
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ReferalCommonCriteriaRes> query = cb.createQuery(ReferalCommonCriteriaRes.class);

			Root<EserviceCommonDetails> m = query.from(EserviceCommonDetails.class);
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);

			query.multiselect(
					c.get("customerReferenceNo").alias("customerReferenceNo"),
					c.get("idNumber").alias("idNumber"),
					c.get("clientName").alias("clientName"),
					c.get("titleDesc").alias("titleDesc"),
					c.get("middleName").alias("middleName"),
					c.get("lastName").alias("lastName"),
					m.get("companyId").alias("companyId"), 
					m.get("productId").alias("productId"),
					m.get("branchCode").alias("branchCode"), 
					m.get("requestReferenceNo").alias("requestReferenceNo"),
					m.get("quoteNo").alias("quoteNo"),
					m.get("customerId").alias("customerId"),
					m.get("policyStartDate").alias("policyStartDate"),
					m.get("policyEndDate").alias("policyEndDate"),
					m.get("rejectReason").alias("rejectReason"),
					m.get("adminRemarks").alias("adminRemarks"),
					m.get("referalRemarks").alias("referalRemarks"),
					m.get("endorsementType").alias("endorsementType"),
					m.get("endorsementTypeDesc").alias("endorsementTypeDesc"),
					m.get("endorsementDate").alias("endorsementDate"),
					m.get("endorsementRemarks").alias("endorsementRemarks"),
					m.get("endorsementEffdate").alias("endorsementEffdate"),
					m.get("originalPolicyNo").alias("originalPolicyNo"),
					m.get("endtPrevPolicyNo").alias("endtPrevPolicyNo"),
					m.get("endtPrevQuoteNo").alias("endtPrevQuoteNo"),
					m.get("endtCount").alias("endtCount"),
					m.get("endtStatus").alias("endtStatus"),
					m.get("endtCategDesc").alias("endtCategDesc"),
					m.get("endtPremium").alias("endtPremium")
					);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(m.get("updatedDate")));
			
			Subquery<Long> riskId = query.subquery(Long.class);
			Root<EserviceCommonDetails> ocpm1 = riskId.from(EserviceCommonDetails.class);
			riskId.select(cb.max(ocpm1.get("riskId")));
			Predicate a1 = cb.equal(ocpm1.get("requestReferenceNo"), m.get("requestReferenceNo"));
			riskId.where(a1);

			// Where
			Predicate n1 = cb.equal(c.get("customerReferenceNo"), m.get("customerReferenceNo"));
			Predicate n2 = cb.equal(m.get("companyId"), req.getInsuranceId());
			Predicate n3 = cb.equal(m.get("productId"), req.getProductId());
			Predicate n4 = cb.equal(m.get("status"), status);
			Predicate n7 = cb.equal(m.get("riskId"), riskId);

			Predicate n5 = null;
			Predicate n9 = null;
			if (req.getApplicationId().equalsIgnoreCase("1")) {
				n5 = cb.equal(m.get("loginId"), req.getLoginId());
				n9 = cb.equal(m.get("applicationId"), req.getApplicationId());
			} else {
				n5 = cb.equal(m.get("applicationId"), req.getApplicationId());
				if(StringUtils.isNotBlank(req.getBdmCode())){
					n9 = cb.equal(m.get("bdmCode"), req.getBdmCode());
				}else {
					n9 = cb.equal(m.get("loginId"), req.getLoginId());
				}
			}
			Predicate n6 = null;
			if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
				
				 n6 = cb.equal(  m.get("brokerBranchCode"), req.getBrokerBranchCode());
			} else {
				
				 n6 = cb.equal(  m.get("branchCode"),  req.getBranchCode());
			}
			Predicate n8 = null;
			if(req.getType().equalsIgnoreCase("Q"))
				n8 = cb.isNull(m.get("endorsementTypeDesc")); 
			else if (req.getType().equalsIgnoreCase("E"))
				n8 = cb.isNotNull(m.get("endorsementTypeDesc"));
			Predicate nLead=null;
			if (req.getLeadSeqNo() != null) {
		         nLead = cb.equal(c.get("leadSeqNo"), req.getLeadSeqNo());
				}
				
				if (req.getLeadSeqNo() == null) {
					query.where(n1,n2,n3,n4,n5,n6,n7,n8,n9).orderBy(orderList);
				}else {
					query.where(n1,n2,n3,n4,n5,n6,n7,n8,n9,nLead).orderBy(orderList);
				}
			
			//query.where(n1, n2, n3, n4, n5, n6,n7,n8,n9).orderBy(orderList);
				
			TypedQuery<ReferalCommonCriteriaRes> result = em.createQuery(query);
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			referrals = result.getResultList();
			
			if (referrals != null && referrals.size() > 0) {
				referrals = referrals.stream().filter(distinctByKey(o -> Arrays.asList(o.getRequestReferenceNo()))).collect(Collectors.toList());
			}else {
				referrals=null;
			}
			resp.setReferalCommonCriteriaRes(referrals);
					
			//Counts
			int totalend = totalcountuser(req,  status);
			resp.setTotalCount(totalend);				
					
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resp;
	}

	private int totalcountuser(ExistingQuoteReq req, String status) {

		int count = 0;
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> query = cb.createQuery(Long.class);

			Root<EserviceCommonDetails> m = query.from(EserviceCommonDetails.class);
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);

//			query.multiselect(cb.count(m));
			query.select(cb.countDistinct(m.get("requestReferenceNo"))); 

			Subquery<Long> riskId = query.subquery(Long.class);
			Root<EserviceCommonDetails> ocpm1 = riskId.from(EserviceCommonDetails.class);
			riskId.select(cb.max(ocpm1.get("riskId")));
			Predicate a1 = cb.equal(ocpm1.get("requestReferenceNo"), m.get("requestReferenceNo"));
			riskId.where(a1);

			// Where
			Predicate n1 = cb.equal(c.get("customerReferenceNo"), m.get("customerReferenceNo"));
			Predicate n2 = cb.equal(m.get("companyId"), req.getInsuranceId());
			Predicate n3 = cb.equal(m.get("productId"), req.getProductId());
			Predicate n4 = cb.equal(m.get("status"), status);
			Predicate n7 = cb.equal(m.get("riskId"), riskId);

			Predicate n5 = null;
			Predicate n9 = null;
			
			if (req.getApplicationId().equalsIgnoreCase("1")) {
				n5 = cb.equal(m.get("loginId"), req.getLoginId());
			} else {
				n5 = cb.equal(m.get("applicationId"), req.getApplicationId());
				if(StringUtils.isNotBlank(req.getBdmCode())){
					n9 = cb.equal(m.get("bdmCode"), req.getBdmCode());
				}else {
					n9 = cb.equal(m.get("loginId"), req.getLoginId());
				}
			}
			Predicate n6 = null;
			if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
				
				 n6 = cb.equal(  m.get("brokerBranchCode"), req.getBrokerBranchCode());
			} else {
				
				 n6 = cb.equal(  m.get("branchCode"),  req.getBranchCode());
			}
			Predicate n8 = null;
			if(req.getType().equalsIgnoreCase("Q"))
				n8 = cb.isNull(m.get("endorsementTypeDesc")); 
			else if (req.getType().equalsIgnoreCase("E"))
				n8 = cb.isNotNull(m.get("endorsementTypeDesc"));

//			if (req.getApplicationId().equalsIgnoreCase("1"))
//				query.where(n1, n2, n3, n4, n5, n6,n7,n8);
//			else
//				query.where(n1, n2, n3, n4, n5, n6,n7,n8,n9);
			

Predicate nLeadCount =null;
			if(req.getLeadSeqNo() !=null) {
	         nLeadCount = cb.equal(c.get("leadSeqNo"), req.getLeadSeqNo());
			}

			if (req.getApplicationId().equalsIgnoreCase("1")) {
				if (req.getLeadSeqNo() != null) {
					query.where(n1, n2, n3, n4, n5, n6, n7, n8, nLeadCount);
				} else {
					query.where(n1, n2, n3, n4, n5, n6, n7, n8);

				}
			} else {
				if (req.getLeadSeqNo() != null) {
					query.where(n1, n2, n3, n4, n5, n6, n7, n8, n9, nLeadCount);
				} else {
					query.where(n1, n2, n3, n4, n5, n6, n7, n8, n9);

				}

				// query.where(n1, n2, n3, n4, n5, n6, n7, n8, n9);
			}
				
			// Get Result
			TypedQuery<Long> result = em.createQuery(query);
			List<Long> referralsList = result.getResultList();
			
			if(referralsList.size()>0)
				count = referralsList.get(0).intValue();
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
		}
		return count;
	
	}

	@Override
		public synchronized GetCommonReferalDetailsRes getCommonAdminReferalDetails(ExistingQuoteReq req, int limit,
				int offset ,String status) {
			List<ReferalCommonCriteriaRes> referrals = new ArrayList<ReferalCommonCriteriaRes>();
			GetCommonReferalDetailsRes resp = new GetCommonReferalDetailsRes();
			try {
				
				resp.setTotalCount(0);
				
				
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<ReferalCommonCriteriaRes> query = cb.createQuery(ReferalCommonCriteriaRes.class);
	 
				// Find All
				Root<EserviceCommonDetails> m = query.from(EserviceCommonDetails.class);
				Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
	 
				// Select
				query.multiselect(
						// Customer Info
						c.get("customerReferenceNo").alias("customerReferenceNo"), c.get("idNumber").alias("idNumber"),
						c.get("clientName").alias("clientName"),
						c.get("titleDesc").alias("titleDesc"),
						c.get("middleName").alias("middleName"),c.get("lastName").alias("lastName"),
						// Vehicle Info
						m.get("companyId").alias("companyId"), m.get("productId").alias("productId"),
						m.get("branchCode").alias("branchCode"), m.get("requestReferenceNo").alias("requestReferenceNo"),
						m.get("quoteNo")
								.alias("quoteNo"),
						m.get("customerId").alias("customerId"),
						m.get("policyStartDate").alias("policyStartDate"), m.get("policyEndDate").alias("policyEndDate"),
						m.get("rejectReason").alias("rejectReason"),
						m.get("adminRemarks").alias("adminRemarks"),m.get("referalRemarks").alias("referalRemarks"),
						m.get("endorsementType").alias("endorsementType"),
						m.get("endorsementTypeDesc").alias("endorsementTypeDesc"),
						m.get("endorsementDate").alias("endorsementDate"),
						m.get("endorsementRemarks").alias("endorsementRemarks"),
						m.get("endorsementEffdate").alias("endorsementEffdate"),
						m.get("originalPolicyNo").alias("originalPolicyNo"),
						m.get("endtPrevPolicyNo").alias("endtPrevPolicyNo"),
						m.get("endtPrevQuoteNo").alias("endtPrevQuoteNo"),
						m.get("endtCount").alias("endtCount"),
						m.get("endtStatus").alias("endtStatus"),
						m.get("endtCategDesc").alias("endtCategDesc"),
						m.get("endtPremium").alias("endtPremium")
						);
				
				//Riskid
				Subquery<Long> riskId = query.subquery(Long.class);
				Root<EserviceCommonDetails> ocpm2 = riskId.from(EserviceCommonDetails.class);
				riskId.select(cb.max(ocpm2.get("riskId")));
				Predicate a3 = cb.equal(m.get("requestReferenceNo"), ocpm2.get("requestReferenceNo"));
				riskId.where(a3);
	 
				// Order By
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(m.get("updatedDate")));
	 
				// Where
				Predicate n1 = cb.equal(c.get("customerReferenceNo"), m.get("customerReferenceNo"));
				Predicate n2 = cb.equal(m.get("companyId"), req.getInsuranceId());
				Predicate n3 = cb.equal(m.get("productId"), req.getProductId());
				Predicate n4 = cb.equal(m.get("status"),status);
			//	Predicate n6 = cb.equal(c.get("branchCode"), req.getBdmCode());
				Predicate n6 = cb.equal(m.get("branchCode"), req.getBranchCode());
				Predicate n7 = cb.equal(m.get("riskId"), riskId);
				Predicate n15 = cb.equal(m.get("adminLoginId"), req.getApplicationId());
				Predicate n16 = null;
				if (StringUtils.isNotBlank(req.getBdmCode())) {
				    n16 = cb.or(
				        cb.equal(m.get("bdmCode"), req.getBdmCode()),   
				        cb.equal(m.get("loginId"), req.getBdmCode())   
				    );
				} else {
				    n16 = cb.equal(m.get("loginId"), req.getLoginId());
				}
				Predicate n14 = null;
				if(req.getType().equalsIgnoreCase("Q"))
						n14 = cb.isNull(m.get("endorsementTypeDesc"));
				else if (req.getType().equalsIgnoreCase("E"))
						n14 = cb.isNotNull(m.get("endorsementTypeDesc"));
				
				// Uw Condition
				if("RP".equalsIgnoreCase(status)) {
					
					Root<UWReferralDetails> uw = query.from(UWReferralDetails.class);
					Predicate n8 = cb.equal(uw.get("requestReferenceNo"), m.get("requestReferenceNo"));
					Predicate n9 = cb.equal(uw.get("uwLoginId"),req.getApplicationId());
					Predicate n10 = cb.equal(uw.get("uwStatus"), "Y");
					Predicate n11 = cb.equal(uw.get("companyId"), req.getInsuranceId());
					Predicate n12 = cb.equal(uw.get("productId"), req.getProductId());
					Predicate n13 = cb.equal(uw.get("branchCode"), req.getBranchCode());
					query.where(n7,n1, n2, n3, n4, n6,n8,n9,n10,n11,n12,n13,n14,n16)
				
					.orderBy(orderList);
					
				}else {
					
					query.where(n7,n1, n2, n3, n4, n6,n14,n15,n16)
				
					.orderBy(orderList);
				}
				
				// Get Result
				TypedQuery<ReferalCommonCriteriaRes> result = em.createQuery(query);
				
				result.setFirstResult(limit * offset);
				result.setMaxResults(offset);
				referrals = result.getResultList();
				System.out.println("ReferalCommonCriteriaRes Common :"+referrals );
				if (referrals != null && referrals.size() > 0) {
					referrals = referrals.stream().filter(distinctByKey(o -> Arrays.asList(o.getRequestReferenceNo()))).collect(Collectors.toList());
				}else {
					referrals=null;
				}
				
				resp.setReferalCommonCriteriaRes(referrals);
				
				//Counts
				if(req.getType().equalsIgnoreCase("E")) {
					int totalend = totalend(req,  status);
					resp.setTotalCount(totalend);	}
				
				if(req.getType().equalsIgnoreCase("Q")) {
					int totalquote = totalquote(req,  status);
					resp.setTotalCount(totalquote);	}
				
				
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Log Details" + e.getMessage());
				return null;
			}
			return resp;
		}
	 

	private int totalquote(ExistingQuoteReq req, String status) {
		int count = 0;
		try {	
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> query = cb.createQuery(Long.class);

			// Find All
			Root<EserviceCommonDetails> m = query.from(EserviceCommonDetails.class);
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);

//			query.multiselect(cb.count(m));
			query.select(cb.countDistinct(m.get("requestReferenceNo"))); 
			
			//Riskid
			Subquery<Long> riskId = query.subquery(Long.class);
			Root<EserviceCommonDetails> ocpm2 = riskId.from(EserviceCommonDetails.class);
			riskId.select(cb.max(ocpm2.get("riskId")));
			Predicate a3 = cb.equal(m.get("requestReferenceNo"), ocpm2.get("requestReferenceNo"));
			riskId.where(a3);


			// Where
			Predicate n1 = cb.equal(c.get("customerReferenceNo"), m.get("customerReferenceNo"));
			Predicate n2 = cb.equal(m.get("companyId"), req.getInsuranceId());
			Predicate n3 = cb.equal(m.get("productId"), req.getProductId());
			Predicate n4 = cb.equal(m.get("status"),status);
		//	Predicate n6 = cb.equal(c.get("branchCode"), req.getBdmCode());
			Predicate n6 = cb.equal(m.get("branchCode"), req.getBranchCode());
			Predicate n7 = cb.equal(m.get("riskId"), riskId);
			Predicate n14 = cb.isNull(m.get("endorsementTypeDesc")); 
			Predicate n15 = cb.equal(m.get("adminLoginId"), req.getApplicationId());
			Predicate n16 = null;
			if(StringUtils.isNotBlank(req.getBdmCode())){
				n16 = cb.equal(m.get("bdmCode"), req.getBdmCode());
			}else {
				n16 = cb.equal(m.get("loginId"), req.getLoginId());
			}
			// Uw Condition 
			if("RP".equalsIgnoreCase(status)) {
				
				Root<UWReferralDetails> uw = query.from(UWReferralDetails.class);
				Predicate n8 = cb.equal(uw.get("requestReferenceNo"), m.get("requestReferenceNo")); 
				Predicate n9 = cb.equal(uw.get("uwLoginId"),req.getApplicationId()); 
				Predicate n10 = cb.equal(uw.get("uwStatus"), "Y"); 
				Predicate n11 = cb.equal(uw.get("companyId"), req.getInsuranceId()); 
				Predicate n12 = cb.equal(uw.get("productId"), req.getProductId()); 
				Predicate n13 = cb.equal(uw.get("branchCode"), req.getBranchCode()); 
				query.where(n7,n1, n2, n3, n4, n6,n8,n9,n10,n11,n12,n13,n14,n16);
			
			
				
			}else {
				
				query.where(n7,n1, n2, n3, n4, n6,n14,n15,n16);
			
				
			}
			
			TypedQuery<Long> result = em.createQuery(query);
			List<Long> val = result.getResultList();
				
					if(val.size()>0)
						count = val.get(0).intValue();
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
		
		}
		return count;
	}

	private int totalend(ExistingQuoteReq req, String status) {

		int count = 0;
		try {	
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> query = cb.createQuery(Long.class);

			// Find All
			Root<EserviceCommonDetails> m = query.from(EserviceCommonDetails.class);
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);

//			query.multiselect(cb.count(m));
			query.select(cb.countDistinct(m.get("requestReferenceNo"))); 
			
			//Riskid
			Subquery<Long> riskId = query.subquery(Long.class);
			Root<EserviceCommonDetails> ocpm2 = riskId.from(EserviceCommonDetails.class);
			riskId.select(cb.max(ocpm2.get("riskId")));
			Predicate a3 = cb.equal(m.get("requestReferenceNo"), ocpm2.get("requestReferenceNo"));
			riskId.where(a3);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(m.get("updatedDate")));

			// Where
			Predicate n1 = cb.equal(c.get("customerReferenceNo"), m.get("customerReferenceNo"));
			Predicate n2 = cb.equal(m.get("companyId"), req.getInsuranceId());
			Predicate n3 = cb.equal(m.get("productId"), req.getProductId());
			Predicate n4 = cb.equal(m.get("status"),status);
		//	Predicate n6 = cb.equal(c.get("branchCode"), req.getBdmCode());
			Predicate n6 = cb.equal(m.get("branchCode"), req.getBranchCode());
			Predicate n7 = cb.equal(m.get("riskId"), riskId);
			Predicate n14 = cb.isNotNull(m.get("endorsementTypeDesc")); 
			Predicate n15 = cb.equal(m.get("adminLoginId"), req.getApplicationId());
			Predicate n16 = null;
			if(StringUtils.isNotBlank(req.getBdmCode())){
				n16 = cb.equal(m.get("bdmCode"), req.getBdmCode());
			}else {
				n16 = cb.equal(m.get("loginId"), req.getLoginId());
			}
			
			// Uw Condition 
			if("RP".equalsIgnoreCase(status)) {
				
				Root<UWReferralDetails> uw = query.from(UWReferralDetails.class);
				Predicate n8 = cb.equal(uw.get("requestReferenceNo"), m.get("requestReferenceNo")); 
				Predicate n9 = cb.equal(uw.get("uwLoginId"),req.getApplicationId()); 
				Predicate n10 = cb.equal(uw.get("uwStatus"), "Y"); 
				Predicate n11 = cb.equal(uw.get("companyId"), req.getInsuranceId()); 
				Predicate n12 = cb.equal(uw.get("productId"), req.getProductId()); 
				Predicate n13 = cb.equal(uw.get("branchCode"), req.getBranchCode()); 
				query.where(n7,n1, n2, n3, n4, n6,n8,n9,n10,n11,n12,n13,n14,n16)
			
				.orderBy(orderList);
				
			}else {
				
				query.where(n7,n1, n2, n3, n4, n6,n14,n15,n16)
			
				.orderBy(orderList);
			}
			
			TypedQuery<Long> result = em.createQuery(query);
			List<Long> val = result.getResultList();
				
			if(val.size()>0)
				count = val.get(0).intValue();
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
		
		}
		return count;
	
	}

		//SearchMotorQuote
		@Override
		public List<Tuple> searchCommonQuote(CopyQuoteReq req, List<String> branches) {
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
					searchQuote = searchDetails(searchKey, searchValue, companyId, loginId, userType, branches);
				} else if ("CustomerReferenceNo".equalsIgnoreCase(searchKey)) {
					searchQuote = searchDetails(searchKey, searchValue, companyId, loginId, userType, branches);
				} else if ("ClientName".equalsIgnoreCase(searchKey)) {
					searchQuote = searchDetails(searchKey, searchValue, companyId, loginId, userType, branches);
				} else if ("QuoteNumber".equalsIgnoreCase(searchKey)) {
					searchQuote = searchDetails(searchKey, searchValue, companyId, loginId, userType, branches);
				} else if ("ChassisNumber".equalsIgnoreCase(searchKey)) {
					searchQuote = searchDetails(searchKey, searchValue, companyId, loginId, userType, branches);
				} else if ("RegistrationNumber".equalsIgnoreCase(searchKey)) {
					searchQuote = searchDetails(searchKey, searchValue, companyId, loginId, userType, branches);
				} else if ("EntryDate".equalsIgnoreCase(searchKey)) {
					//Date entryDate = sdf.parse(searchValue);
					//searchValue = sdf.format(entryDate);
					searchQuote = searchDetails(searchKey, searchValue, companyId, loginId, userType, branches);
				}else if ("PolicyNo".equalsIgnoreCase(searchKey)) {
					searchQuote = searchDetails(searchKey, searchValue, companyId, loginId, userType, branches);
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Log Details" + e.getMessage());
				return null;
			}
			return searchQuote;
		}

		public List<Tuple> searchDetails(String searchKey, String searchValue, String companyId, String loginId,
				String userType, List<String> branches) {
			List<Tuple> customerDetailsList = new ArrayList<Tuple>();
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

			try {

				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

				Root<EserviceCommonDetails> c = query.from(EserviceCommonDetails.class);
				Root<EserviceCustomerDetails> cus = query.from(EserviceCustomerDetails.class);
				
				query.multiselect(cus.get("clientName").alias("clientName"),cb.count(c).alias("idsCount"),

				cb.max(c.get("requestReferenceNo")).alias("requestReferenceNo"),
				cb.max(c.get("riskId")).alias("riskId"), 
				cb.max(c.get("customerReferenceNo")).alias("customerReferenceNo"),
				cb.max(c.get("sectionId")).alias("sectionId"),
				cb.max(c.get("occupationType")).alias("occupationType"),
				cb.selectCase().when(cb.max(c.get("quoteNo")).isNotNull(), cb.max(c.get("quoteNo")))
				.otherwise(cb.max(c.get("quoteNo"))).alias("quoteNo"),

				cb.selectCase().when(cb.max(c.get("customerId")).isNotNull(), cb.max(c.get("customerId")))
				.otherwise(cb.max(c.get("customerId"))).alias("customerId"),
				cb.max(c.get("productId")).alias("productId"),
				cb.max(c.get("productDesc")).alias("productDesc"),
				cb.max(c.get("sectionName")).alias("sectionName"), 

				cb.max(c.get("occupationDesc")).alias("occupationDesc"),
				cb.max(c.get("companyId")).alias("companyId"), 
				cb.max(c.get("companyName")).alias("companyName"),
				cb.max(c.get("branchCode")).alias("branchCode"),
				cb.max(c.get("branchName")).alias("branchName"),
				cb.max(c.get("policyNo")).alias("policyNo"),
				cb.max(c.get("agencyCode")).alias("agencyCode"),
				cb.max(c.get("categoryId")).alias("categoryId"),
				cb.max(c.get("sumInsured")).alias("sumInsured"),
				cb.max(c.get("entryDate")).alias("entryDate"), 
				cb.max(c.get("createdBy")).alias("createdBy"),
				cb.max(c.get("status")).alias("status"),
				cb.max(c.get("updatedDate")).alias("updatedDate"),
				cb.max(c.get("updatedBy")).alias("updatedBy"),
				cb.max(c.get("count")).alias("count"),
				cb.max(c.get("adminLoginId")).alias("adminLoginId"),
				cb.max(c.get("adminRemarks")).alias("adminRemarks"),
				cb.max(c.get("referalRemarks")).alias("referalRemarks"),
				cb.max(c.get("rejectReason")).alias("rejectReason"),
				cb.max(c.get("loginId")).alias("loginId"),
				cb.max(c.get("brokerCode")).alias("brokerCode"),
				cb.max(c.get("policyPeriod")).alias("policyPeriod"),
				cb.max(c.get("acExecutiveId")).alias("acExecutiveId"), 
				cb.max(c.get("applicationId")).alias("applicationId"),
				cb.max(c.get("brokerBranchCode")).alias("brokerBranchCode"), 
				cb.max(c.get("brokerBranchName")).alias("brokerBranchName"),
				cb.max(c.get("oldReqRefNo")).alias("oldReqRefNo"), 
				cb.max(c.get("benefitCoverMonth")).alias("benefitCoverMonth"),
				cb.max(c.get("salaryPerAnnum")).alias("salaryPerAnnum"),
				cb.max(c.get("currency")).alias("currency"),
				cb.max(c.get("exchangeRate")).alias("exchangeRate"),
				cb.max(c.get("policyStartDate")).alias("policyStartDate"),
				cb.max(c.get("policyEndDate")).alias("policyEndDate"),
				cb.max(c.get("actualPremiumFc")).alias("actualPremiumFc"),
				cb.max(c.get("actualPremiumLc")).alias("actualPremiumLc"),
				cb.max(c.get("overallPremiumLc")).alias("overallPremiumLc"),
				cb.max(c.get("overallPremiumFc")).alias("overallPremiumFc"),
				cb.max(c.get("havepromocode")).alias("havepromocode"),
				cb.max(c.get("promocode")).alias("promocode"),
				cb.max(c.get("customerName")).alias("customerName"),
				cb.max(c.get("dob")).alias("dob"),
				cb.max(c.get("jobJoiningMonth")).alias("jobJoiningMonth"),
				cb.max(c.get("betweenDiscontinued")).alias("betweenDiscontinued"),
				cb.max(c.get("ethicalWorkInvolved")).alias("ethicalWorkInvolved"),
				cb.max(c.get("bankCode")).alias("bankCode"),
				cb.max(c.get("sourceType")).alias("sourceType"),
				cb.max(c.get("customerCode")).alias("customerCode"),
				cb.max(c.get("bdmCode")).alias("bdmCode"),
				cb.max(c.get("manualReferalYn")).alias("manualReferalYn"),
				cb.max(c.get("endorsementType")).alias("endorsementType"),
				cb.max(c.get("endorsementTypeDesc")).alias("endorsementTypeDesc"),
				cb.max(c.get("endorsementDate")).alias("endorsementDate"),
				cb.max(c.get("endorsementRemarks")).alias("endorsementRemarks"),
				cb.max(c.get("endorsementEffdate")).alias("endorsementEffdate"),
				cb.max(c.get("originalPolicyNo")).alias("originalPolicyNo"),
				cb.max(c.get("endtPrevPolicyNo")).alias("endtPrevPolicyNo"),
				cb.max(c.get("endtPrevQuoteNo")).alias("endtPrevQuoteNo"),
				cb.max(c.get("endtCount")).alias("endtCount"),
				cb.max(c.get("endtStatus")).alias("endtStatus"),
				cb.max(c.get("isFinaceYn")).alias("isFinaceYn"),
				cb.max(c.get("endtCategDesc")).alias("endtCategDesc"),
				cb.max(c.get("endtPremium")).alias("endtPremium"),
				cb.max(c.get("natureOfBusinessId")).alias("natureOfBusinessId"),
				cb.max(c.get("natureOfBusinessDesc")).alias("natureOfBusinessDesc"),
				cb.max(c.get("totalNoOfEmployees")).alias("totalNoOfEmployees"),
				cb.max(c.get("totalExcludedEmployees")).alias("totalExcludedEmployees"),
				cb.max(c.get("totalRejoinedEmployees")).alias("totalRejoinedEmployees"),
				cb.max(c.get("accountOutstandingEmployees")).alias("accountOutstandingEmployees"),
				cb.max(c.get("accountAuditentType")).alias("accountAuditentType"),
				cb.max(c.get("totalOutstandingAmount")).alias("totalOutstandingAmount"), 
				cb.max(c.get("auditentTypeDesc")).alias("auditentTypeDesc"),
				cb.max(c.get("industryName")).alias("industryName"),
				cb.max(c.get("liabilitySi")).alias("liabilitySi"),
				cb.max(c.get("fidEmpCount")).alias("fidEmpCount"),
				cb.max(c.get("fidEmpSi")).alias("fidEmpSi"),

				cb.max(c.get("empLiabilitySi")).alias("empLiabilitySi"),
				cb.max(c.get("personalLiabilityOccupation")).alias("personalLiabilityOccupation"),
				cb.max(c.get("personalLiabilitySi")).alias("personalLiabilitySi"),
				cb.max(c.get("personalLiabilityCategory")).alias("personalLiabilityCategory"),
				cb.max(c.get("tiraCoverNoteNo")).alias("tiraCoverNoteNo"),
				cb.max(c.get("industryId")).alias("industryId"));

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
				} else if (searchKey.equalsIgnoreCase("RegistrationNumber")) {
					n1 = cb.equal(cb.lower(c.get("registrationNumber")), searchValue);
				} else if (searchKey.equalsIgnoreCase("QuoteNumber")) {
					n1 = cb.equal(c.get("quoteNo"), searchValue);
				} else if (searchKey.equalsIgnoreCase("EntryDate")) {
					Date entryDate = sdf.parse(searchValue);
					Calendar cal = new GregorianCalendar();
					cal.setTime(entryDate);
					//cal.add(Calendar.HOUR , -1);
					cal.add(Calendar.DAY_OF_MONTH, -1);cal.set(Calendar.HOUR_OF_DAY, 23);cal.set(Calendar.MINUTE, 59);
					Date startDate = cal.getTime() ;
					cal.setTime(entryDate);
				//	cal.add(Calendar.HOUR , +23);
					cal.add(Calendar.DAY_OF_MONTH, 0);cal.set(Calendar.HOUR_OF_DAY,23 );cal.set(Calendar.MINUTE, 59);
					Date endDate = cal.getTime() ;
					n1=cb.between(c.get("entryDate"), startDate, endDate);
					
				} else if (searchKey.equalsIgnoreCase("ChassisNumber")) {
					n1 = cb.equal(cb.lower(c.get("chassisNumber")), searchValue);
				} else if (searchKey.equalsIgnoreCase("ClientName")) {
					n1 = cb.like(cb.lower(cus.get("clientName")), "%" + searchValue + "%");
					n5 = cb.equal(c.get("customerReferenceNo"), cus.get("customerReferenceNo"));
				} else if (searchKey.equalsIgnoreCase("PolicyNo")) {
					n1 = cb.like(cb.lower(c.get("policyNo")), searchValue );
				
				}

				Predicate n2 = cb.equal(c.get("companyId"), companyId);

				if ("issuer".equalsIgnoreCase(userType)) {
//					n3 = cb.equal(c.get("applicationId"), loginId);
					Expression<String> e0 = c.get("branchCode");
					n4 = e0.in(branches);
				} else if ("Broker".equalsIgnoreCase(userType) || "User".equalsIgnoreCase(userType)) {
					n3 = cb.equal(c.get("loginId"), loginId);
					Expression<String> e0 = c.get("brokerBranchCode");
//					Expression<String> e0 = c.get("branchCode");
					n4 = e0.in(branches);
				}
				if (searchKey.equalsIgnoreCase("ClientName")) {
					if ("issuer".equalsIgnoreCase(userType)) {

						Expression<String> e0 = cus.get("branchCode");
						n4 = e0.in(branches);
					} else if ("Broker".equalsIgnoreCase(userType) || "User".equalsIgnoreCase(userType)) {

						Expression<String> e0 = cus.get("brokerBranchCode");
						n4 = e0.in(branches);
					}
				}
				n5 = cb.equal(c.get("customerReferenceNo"), cus.get("customerReferenceNo"));
				if ("Broker".equalsIgnoreCase(userType) || "User".equalsIgnoreCase(userType)) {
				query.where(n1,n2,n3,n4,n5)
				.groupBy(c.get("customerReferenceNo"), cus.get("clientName"), c.get("companyId"),c.get("riskId"),
						c.get("productId"), c.get("branchCode"), c.get("requestReferenceNo"), c.get("quoteNo"),
						c.get("customerId"), c.get("policyStartDate"), c.get("policyEndDate"),c.get("sectionId"),c.get("occupationType"))
				
				.orderBy(orderList);
				}else {
					query.where(n1,n2,n4,n5)
					.groupBy(c.get("customerReferenceNo"), cus.get("clientName"), c.get("companyId"),c.get("riskId"),
							c.get("productId"), c.get("branchCode"), c.get("requestReferenceNo"), c.get("quoteNo"),
							c.get("customerId"), c.get("policyStartDate"), c.get("policyEndDate"),c.get("sectionId"),c.get("occupationType"))
					
					.orderBy(orderList);
				}
				
				if (searchKey.equalsIgnoreCase("ClientName")) {
					query.where(n1, n2,n4,n5)
					.groupBy(c.get("customerReferenceNo"), cus.get("clientName"), c.get("companyId"),c.get("riskId"),
							c.get("productId"), c.get("branchCode"), c.get("requestReferenceNo"), c.get("quoteNo"),
							c.get("customerId"), c.get("policyStartDate"), c.get("policyEndDate"),c.get("sectionId"),c.get("occupationType"))
				
					.orderBy(orderList);
				}
				if (searchKey.equalsIgnoreCase("EntryDate")) {
					query.where(n1,n2,n3,n4)
					.groupBy(c.get("customerReferenceNo"), cus.get("clientName"), c.get("companyId"),c.get("riskId"),
							c.get("productId"), c.get("branchCode"), c.get("requestReferenceNo"), c.get("quoteNo"),
							c.get("customerId"), c.get("policyStartDate"), c.get("policyEndDate"),c.get("sectionId"),c.get("occupationType"))
				
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

		//CopyQuote
		@Override
		public CopyQuoteSuccessRes commonCopyQuote(CopyQuoteReq req, List<String> branches) {
			CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			EserviceCommonDetails savedata = new EserviceCommonDetails();
			
			try {
				String searchValue = req.getRequestReferenceNo();
				String searchKey = "RequestReferenceNo";
				String companyId = req.getInsuranceId();
				String loginId = req.getLoginId();
				String userType = req.getUserType();
				String branchCode = "";
				List<Tuple> list = copyQuoteSearchDetails(searchKey, searchValue, companyId, loginId, userType, branches);

				String refNo = req.getRequestReferenceNo();

				if (list.size() > 0) {
//					String refShortCode = motorService.getListItem(companyId, req.getBranchCode(), "PRODUCT_SHORT_CODE",
//							req.getProductId());
//					refNo = refShortCode + "-" + seqNo.generateRefNo();
					// Generate Seq
		 			SequenceGenerateReq generateSeqReq = new SequenceGenerateReq();
		 		 	generateSeqReq.setInsuranceId(companyId);  
		 		 	generateSeqReq.setProductId(req.getProductId());
		 		 	generateSeqReq.setType("2");
		 		 	generateSeqReq.setTypeDesc("REQUEST_REFERENCE_NO");
		 		 	refNo =  genSeqNoService.generateSeqCall(generateSeqReq);
					for (Tuple data : list) {

						savedata = dozerMapper.map(data.get(0), EserviceCommonDetails.class);

						savedata.setEntryDate(new Date());
						savedata.setCreatedBy(req.getLoginId());
						savedata.setUpdatedBy(req.getLoginId());
						savedata.setUpdatedDate(new Date());
						savedata.setRequestReferenceNo(refNo);
						savedata.setOldReqRefNo(req.getRequestReferenceNo());
//						if (req.getUserType().equalsIgnoreCase("Broker")
//								|| (req.getUserType().equalsIgnoreCase("User"))) {
//							branchCode = req.getBranchCode();
//							savedata.setApplicationId("1");
//							// savedata.setBrokerBranchCode(branchCode);
//
//						} else if ("issuer".equalsIgnoreCase(userType)) {
//							savedata.setApplicationId(req.getLoginId());
//							branchCode = req.getBranchCode();
//							// savedata.setBranchCode(branchCode);
//						}
						savedata.setPolicyStartDate(null);		
						savedata.setPolicyEndDate(null);
						savedata.setActualPremiumFc(BigDecimal.ZERO);
						savedata.setActualPremiumLc(BigDecimal.ZERO);
						savedata.setOverallPremiumFc(BigDecimal.ZERO);
						savedata.setOverallPremiumLc(BigDecimal.ZERO);
						savedata.setQuoteNo("");
						savedata.setStatus("Y");
						savedata.setEndorsementDate(null);
						savedata.setEndorsementEffdate(null);
						savedata.setEndorsementRemarks(null);
						savedata.setEndorsementType(null);
						savedata.setEndorsementTypeDesc(null);
						savedata.setEndtCategDesc(null);
						savedata.setEndtCount(null);
						savedata.setEndtPremium(null);
						savedata.setEndtPrevPolicyNo(null);
						savedata.setEndtPrevQuoteNo(null);
						savedata.setEndtStatus(null);
						savedata.setFinalizeYn("N");
						savedata.setEmiYn("N");
						savedata.setEmiPremium(null);
						savedata.setInstallmentPeriod(null);
						savedata.setNoOfInstallment(null);
						savedata.setApplicationId(req.getApplicationId());
						if (StringUtils.isNotBlank(req.getLoginId())) {
							savedata.setLoginId(req.getLoginId());
						}
						savedata.setSubUserType(req.getSubUserType());
						savedata.setPolicyNo(null);
						savedata.setVatPremium(null);
						savedata.setCdRefno(null);
						savedata.setMsRefno(null);
						savedata.setVdRefNo(null);
						savedata.setSumInsuredLc(null);
						// Source Type Condtion
						String sourceType = savedata.getSourceType() ;
						if(StringUtils.isNotBlank(savedata.getApplicationId()) && ! "1".equalsIgnoreCase(savedata.getApplicationId()) ) {
							List<ListItemValue> sourcerTypes = genSeqNoService.getSourceTypeDropdown(savedata.getCompanyId() , savedata.getBranchCode() ,"SOURCE_TYPE"); 
							List<ListItemValue> acitveSourcerTypes = sourcerTypes.stream().filter( o -> "Y".equalsIgnoreCase(o.getStatus()) 
									&& o.getItemValue().contains(sourceType) ).collect(Collectors.toList()); 							
							savedata.setSourceType(acitveSourcerTypes.size() > 0 ? acitveSourcerTypes.get(0).getItemValue()	: 	savedata.getSourceType());			
							savedata.setSourceTypeId(acitveSourcerTypes.size() > 0 ? acitveSourcerTypes.get(0).getItemCode()	: 	savedata.getSourceTypeId());
						}
						repo.saveAndFlush(savedata);
					}
//					res.setResponse("Successfully Updated");
					res.setRequestReferenceNo(refNo);
					// res.setQuoteNo(quoteNo);
				}				
				
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is --->" + e.getMessage());
				return null;
			}
			return res;
		}
		public List<Tuple> copyQuoteSearchDetails(String searchKey, String searchValue, String companyId, String loginId,
				String userType, List<String> branches) {
			List<Tuple> customerDetailsList = new ArrayList<Tuple>();
			try {

				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

				Root<EserviceCommonDetails> c = query.from(EserviceCommonDetails.class);
				Root<EserviceCustomerDetails> cus = query.from(EserviceCustomerDetails.class);
				
				query.multiselect(c,
						cus.get("clientName").alias("clientName"));

				// Order By
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.asc(c.get("customerReferenceNo")));

				Predicate n1 = null;
			//	Predicate n3 = null;
			//	Predicate n4 = null;
				Predicate n5 = null;

				// Where
				if (searchKey.equalsIgnoreCase("RequestReferenceNo")) {
					n1 = cb.equal(cb.lower(c.get("requestReferenceNo")), searchValue);
				}
//
//				Predicate n2 = cb.equal(c.get("companyId"), companyId);
//
//				if ("issuer".equalsIgnoreCase(userType)) {
//					n3 = cb.equal(c.get("applicationId"), loginId);
//					Expression<String> e0 = c.get("branchCode");
//					n4 = e0.in(branches);
//				} else if ("Broker".equalsIgnoreCase(userType) || "User".equalsIgnoreCase(userType)) {
//					n3 = cb.equal(c.get("loginId"), loginId);
//					//Expression<String> e0 = c.get("brokerBranchCode");
//					Expression<String> e0 = c.get("branchCode");
//					n4 = e0.in(branches);
//				}
				
				n5 = cb.equal(c.get("customerReferenceNo"), cus.get("customerReferenceNo"));
				//query.where(n1,n2,n3,n4,n5).orderBy(orderList);
				query.where(n1,n5).orderBy(orderList);
		

				// Get Result
				TypedQuery<Tuple> result = em.createQuery(query);
				customerDetailsList = result.getResultList();
				
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is --->" + e.getMessage());
				return null;
			}
			return customerDetailsList;
		}
		

		//Validaton
		@Override
		public List<Tuple> validateCommonEndt(String quoteNo) {
			List<Tuple> list = new ArrayList<Tuple>();
			try {

				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);
				Root<HomePositionMaster> c = query.from(HomePositionMaster.class);
				// Customer Id
				Subquery<Long> cusId = query.subquery(Long.class);
				Root<HomePositionMaster> ocpm1 = cusId.from(HomePositionMaster.class);
				cusId.select(ocpm1.get("customerId"));
				Predicate a1 = cb.equal(ocpm1.get("quoteNo"), quoteNo);
				cusId.where(a1);

				// PersonalInfo Count SubQuery
				Subquery<Long> perInfo = query.subquery(Long.class);
				Root<PersonalInfo> e = perInfo.from(PersonalInfo.class);
				perInfo.select(cb.count(e));
				Predicate n2 = cb.equal(e.get("customerId"), c.get("customerId"));
				perInfo.where(n2);

				// Policy Cover Data Count SubQuery
				Subquery<Long> policyCover = query.subquery(Long.class);
				Root<PolicyCoverData> q = policyCover.from(PolicyCoverData.class);
				policyCover.select(cb.count(q));
				Predicate n3 = cb.equal(q.get("quoteNo"), quoteNo);
				policyCover.where(n3);

				// Motor Data Details Count SubQuery
				Subquery<Long> motorData = query.subquery(Long.class);
				Root<MotorDataDetails> m = motorData.from(MotorDataDetails.class);
				motorData.select(cb.count(m));
				Predicate n4 = cb.equal(m.get("quoteNo"), quoteNo);
				motorData.where(n4);

				query.multiselect(cb.count(c).alias("homeCount"),c.get("policyNo").alias("policyNo"),
						c.get("status").alias("status"),perInfo.alias("perCount"),
						policyCover.alias("policyCount"), motorData.alias("motorCount"));

				// Where
				Predicate n1 = cb.equal(c.get("quoteNo"), quoteNo);
				query.where(n1);

				// Get Result
				TypedQuery<Tuple> result = em.createQuery(query);
				list = result.getResultList();

				} catch (Exception e) {
					e.printStackTrace();
					log.info("Exception is --->" + e.getMessage());
					return null;
				}
				return list;
			}
		
		//CopyQuote Dropdown 
		@Override
		public List<ListItemValue> getCommonCoptyQuotetListItem(CopyQuoteDropDownReq req, String itemType) {
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
				effectiveDate.where(a1, a2);
				// Effective Date End Max Filter
				Subquery<Date> effectiveDate2 = query.subquery(Date.class);
				Root<ListItemValue> ocpm2 = effectiveDate2.from(ListItemValue.class);
				effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
				Predicate a3 = cb.equal(c.get("itemId"), ocpm2.get("itemId"));
				Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
				effectiveDate2.where(a3, a4);

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
				query.where(n1, n2, n3, n8,n9, n10).orderBy(orderList);
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

		private static <T> java.util.function.Predicate<T> distinctByKey(
				java.util.function.Function<? super T, ?> keyExtractor) {
			Map<Object, Boolean> seen = new ConcurrentHashMap<>();
			return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
		}



		@Override
		public GetMotorProtfolioPendingRes getCommonProtfolioPending(ExistingQuoteReq req, List<String> branches,
				Date startDate, int limit, int offset, String status) {
			
			GetMotorProtfolioPendingRes resp = new GetMotorProtfolioPendingRes();
			List<PortfolioPendingGridCriteriaRes> portfolio = new ArrayList<PortfolioPendingGridCriteriaRes>();
			
			try {
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<PortfolioPendingGridCriteriaRes> query = cb.createQuery(PortfolioPendingGridCriteriaRes.class);

				Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
				Root<EserviceCommonDetails> m = query.from(EserviceCommonDetails.class);
				Root<HomePositionMaster> h = query.from(HomePositionMaster.class);
				
				Subquery<Long> endtPre = query.subquery(Long.class);
				Root<HomePositionMaster> h1 = endtPre.from(HomePositionMaster.class);
				endtPre.select(cb.sum(h1.get("endtPremium"))) ;
				Predicate pm1 = cb.equal(h1.get("endtStatus"), m.get("endtStatus"));
				Predicate pm2   = cb.like(h1.get("originalPolicyNo"), m.get("originalPolicyNo"));
				endtPre.where(pm1,pm2);
		
				// Select
				query.multiselect(//cb.literal(Long.parseLong("1")).alias("idsCount"),
						cb.count(m).as(Long.class).alias("idsCount"),
						// Customer Info
						cb.max(c.get("customerReferenceNo")).alias("customerReferenceNo"), 
						cb.max(c.get("idNumber")).alias("idNumber"),
						cb.max(c.get("clientName")).alias("clientName"),
						
						cb.max(c.get("mobileNo1")).alias("mobileNo1"),
						cb.max(c.get("isTaxExempted")).alias("isTaxExempted"),
						cb.max(c.get("taxExemptedId")).alias("taxExemptedId"),
						// Vehicle Info
						cb.max(m.get("companyId")).alias("companyId"), 
						cb.max(m.get("productId")).alias("productId"),
						cb.max(m.get("branchCode")).alias("branchCode"),
						cb.max(m.get("requestReferenceNo")).alias("requestReferenceNo"),
						cb.selectCase().when(cb.max(m.get("quoteNo")).isNotNull(), cb.max(m.get("quoteNo"))).otherwise(cb.max(m.get("quoteNo")))
								.alias("quoteNo"),
						cb.selectCase().when(cb.max(m.get("customerId")).isNotNull(), cb.max(m.get("customerId")))
								.otherwise(cb.max(m.get("customerId"))).alias("customerId"),
						cb.max(m.get("policyStartDate")).alias("inceptionDate"),
						cb.max(m.get("policyEndDate")).alias("expiryDate"),
						cb.sum(m.get("overallPremiumLc")).alias("overallPremiumLc"), 
						cb.sum(m.get("overallPremiumFc")).alias("overallPremiumFc"),
						cb.max(m.get("policyNo")).alias("policyNo"),
						
						//Home Position Master
						
						cb.max(h.get("debitAcNo")).alias("debitAcNo"),
						cb.max(h.get("debitTo")).alias("debitTo"),
						cb.max(h.get("debitToId")).alias("debitToId"),
						cb.max(h.get("debitNoteNo")).alias("debitNoteNo"),
						cb.max(h.get("debitNoteDate")).alias("debitNoteDate"),
						cb.max(h.get("creditTo")).alias("creditTo"),
						cb.max(h.get("creditToId")).alias("creditToId"),
						cb.max(h.get("creditNo")).alias("creditNo"),
						cb.max(h.get("creditDate")).alias("creditDate"),
						cb.max(h.get("emiYn")).alias("emiYn"),
						cb.max(h.get("installmentPeriod")).alias("installmentPeriod"),
						cb.max(h.get("effectiveDate")).alias("effectiveDate"),
						cb.max(m.get("currency")).alias("currency"),
						cb.max(m.get("originalPolicyNo")).alias("originalPolicyNo"),
						
						cb.max(m.get("endorsementType")).alias("endorsementTypeId"),
						cb.max(m.get("endorsementTypeDesc")).alias("endorsementDesc"),
						cb.max(m.get("endtCategDesc")).alias("endorsementCategoryDesc"),
						//cb.max(m.get("endorsementEffdate")).alias("effectiveDate"),
						cb.max(m.get("endtStatus")).alias("endorsementStatus"),
						cb.max(m.get("endorsementRemarks")).alias("endorsementRemarks"),
						cb.max(m.get("endorsementDate")).alias("endorsementDate"),
						endtPre.alias("endtPremium")
						);
				// Order By
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(m.get("entryDate")));

				Predicate n1 = cb.equal(c.get("customerReferenceNo"), m.get("customerReferenceNo"));
				Predicate n2 = cb.equal(m.get("companyId"), req.getInsuranceId());
				Predicate n3 = cb.equal(m.get("productId"), req.getProductId());
				Predicate n4 = cb.equal(m.get("endtStatus"), status);
			//	Predicate n4 = cb.in(m.get("status")).value(Arrays.asList("E","D"));  
				Predicate n7 = cb.greaterThanOrEqualTo(h.get("expiryDate"), startDate);
				Predicate n8 = cb.lessThanOrEqualTo(h.get("entryDate"), startDate);

				Predicate n5 = null;
				Predicate n6 = null;
				Predicate n9 = null;
				if (req.getApplicationId().equalsIgnoreCase("1")) {
					n9 = cb.equal(m.get("loginId"), req.getLoginId());
//					n5 = cb.equal(m.get("applicationId"), req.getApplicationId());
					Expression<String> e0 = m.get("brokerBranchCode");
					n6 = e0.in(branches);
					query.where(n1, n2, n3, n4, n6,n7,n8,n9).groupBy((m.get("originalPolicyNo")),m.get("endtStatus"));
				} else {
					n5 = cb.equal(m.get("applicationId"), req.getApplicationId());
					if(StringUtils.isNotBlank(req.getBdmCode())){
						n9 = cb.equal(m.get("bdmCode"), req.getBdmCode());
					}else {
						n9 = cb.equal(m.get("loginId"), req.getLoginId());
					}
					Expression<String> e0 = m.get("branchCode");
					n6 = e0.in(branches);
					query.where(n1, n2, n3, n4, n5, n6,n7,n8,n9).groupBy((m.get("originalPolicyNo")),m.get("endtStatus"));
				}
				
//				if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
//					Expression<String> e0 = m.get("brokerBranchCode");
//					n6 = e0.in(branches);
//				} else {
//					Expression<String> e0 = m.get("branchCode");
//					n6 = e0.in(branches);
//				}
//				query.where(n1, n2, n3, n4, n5, n6,n7,n8,n9).groupBy((m.get("originalPolicyNo")),m.get("endtStatus"));
				// Get Result
				TypedQuery<PortfolioPendingGridCriteriaRes> result = em.createQuery(query);
				result.setFirstResult(limit * offset);
				result.setMaxResults(offset);
				portfolio = result.getResultList();
				portfolio = portfolio.stream().filter(o -> !o.getIdsCount().equals(0L))
						.collect(Collectors.toList());
				
				resp.setPending(portfolio);
				resp.setCount(totalProtfolioPending( req,  branches,startDate,limit, offset, status) );
						
				
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Log Details" + e.getMessage());
				return null;
			}
			return resp;
		}

		
		
		private Long totalProtfolioPending(ExistingQuoteReq req, List<String> branches, Date startDate, int limit,
				int offset, String status) {
			
			Long count = 0l;
			
			try {			
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Long> query = cb.createQuery(Long.class);
	
				Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
				Root<EserviceCommonDetails> m = query.from(EserviceCommonDetails.class);
			//	Root<HomePositionMaster> h = query.from(HomePositionMaster.class);
				
				query.multiselect(cb.count(m));
				
				Predicate n1 = cb.equal(c.get("customerReferenceNo"), m.get("customerReferenceNo"));
				Predicate n2 = cb.equal(m.get("companyId"), req.getInsuranceId());
				Predicate n3 = cb.equal(m.get("productId"), req.getProductId());
				Predicate n4 = cb.equal(m.get("endtStatus"), status);
			//	Predicate n4 = cb.in(m.get("status")).value(Arrays.asList("E","D"));  
//				Predicate n7 = cb.greaterThanOrEqualTo(h.get("expiryDate"), startDate);
//				Predicate n8 = cb.lessThanOrEqualTo(h.get("entryDate"), startDate);
	
				Predicate n5 = null;
				Predicate n6 = null;
				Predicate n9 = null;
				if (req.getApplicationId().equalsIgnoreCase("1")) {
					n5 = cb.equal(m.get("loginId"), req.getLoginId());
//					n9 = cb.equal(m.get("applicationId"), req.getApplicationId());
					Expression<String> e0 = m.get("brokerBranchCode");
					n6 = e0.in(branches);
					query.where(n1, n2, n3, n4, n5, n6).groupBy((m.get("originalPolicyNo")),m.get("endtStatus"));		
					
				} else {
					n5 = cb.equal(m.get("applicationId"), req.getApplicationId());
					Expression<String> e0 = m.get("branchCode");
					n6 = e0.in(branches);
					if(StringUtils.isNotBlank(req.getBdmCode())){
						n9 = cb.equal(m.get("bdmCode"), req.getBdmCode());
					}else {
						n9 = cb.equal(m.get("loginId"), req.getLoginId());
					}
					query.where(n1, n2, n3, n4, n5, n6,n9).groupBy((m.get("originalPolicyNo")),m.get("endtStatus"));		
					
				}
				
				if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
					Expression<String> e0 = m.get("brokerBranchCode");
					n6 = e0.in(branches);
				} else {
					Expression<String> e0 = m.get("branchCode");
					n6 = e0.in(branches);
				}
				Predicate n10 = (n9==null)?cb.isTrue(cb.literal(true)):n9;
				query.where(n1, n2, n3, n4, n5, n6,n10).groupBy((m.get("originalPolicyNo")),m.get("endtStatus"));		
					
				
				TypedQuery<Long> result = em.createQuery(query);
				List<Long> list  = result.getResultList();
				
				if(list.size()>0)
					count = Long.valueOf(list.size());
				
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Log Details" + e.getMessage());
				return null;
			}
			return count;
		}

		//Endorsement
		@Transactional
		@Override
		public  CopyQuoteSuccessRes commonEndt(CopyQuoteReq req, List<String> branches,String loginId) {
			CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
			DozerBeanMapper mapper = new DozerBeanMapper();
			try {
				// Generate Seq
	 			SequenceGenerateReq generateSeqReq = new SequenceGenerateReq();
	 		 	generateSeqReq.setInsuranceId(req.getInsuranceId());  
	 		 	generateSeqReq.setProductId(req.getProductId());
	 		 	generateSeqReq.setType("1");
	 		 	generateSeqReq.setTypeDesc("CUSTOMER_REFERENCE_NO");
	 		 	String custRefNo = genSeqNoService.generateSeqCall(generateSeqReq);
	 		 	
	 		 	generateSeqReq.setType("2");
	 		 	generateSeqReq.setTypeDesc("REQUEST_REFERENCE_NO");
	 		 	String refNo= genSeqNoService.generateSeqCall(generateSeqReq);
	 		 	
	 		 	generateSeqReq.setType("3");
	 		 	generateSeqReq.setTypeDesc("CUSTOMER_ID");
	 		 	String customerId = genSeqNoService.generateSeqCall(generateSeqReq);
	 		 	
	 		 	generateSeqReq.setType("4");
	 		 	generateSeqReq.setTypeDesc("QUOTE_NO");
	 		 	String quoteNo  = genSeqNoService.generateSeqCall(generateSeqReq);
//				String refShortCode = getListItem(req.getInsuranceId() ,req.getBranchCode(), "PRODUCT_SHORT_CODE",req.getProductId());
//				String refNo=refShortCode +"-"  +seqNo.generateRefNo();
//				String quoteNo  = "Q"+ generateQuoteNo();
//				String customerId = "C-" + generateCustId();
//				String custRefNo = "Cust-" +   generateCustRefNo() ; 
	            //Copy Quote E service Motor Details
				EserviceCommonDetails savedata=eserviceCommonCopyquote(req,refNo,branches,loginId,customerId,quoteNo,custRefNo);
				res.setCommonResponse(savedata);
				res.setQuoteNo(quoteNo);
			
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is --->" + e.getMessage());
				return null;
			}
			return res;
		}

		public synchronized String generateQuoteNo() {
		       try {
		    	   SeqQuoteno entity;
		            entity = quoteNoRepo.save(new SeqQuoteno());          
		            return String.format("%05d",entity.getQuoteNo()) ;
		        } catch (Exception e) {
					e.printStackTrace();
					log.info( "Exception is ---> " + e.getMessage());
		            return null;
		        }
		       
		 }
		
		 public synchronized String generateCustId() {
		       try {
		    	   SeqCustid entity;
		            entity = custIdRepo.save(new SeqCustid());          
		            return String.format("%05d",entity.getCustId()) ;
		        } catch (Exception e) {
					e.printStackTrace();
					log.info( "Exception is ---> " + e.getMessage());
		            return null;
		        }
		       
			}

			public synchronized String generateCustRefNo() {
				try {
					SeqCustrefno entity;
					entity = custRefRepo.save(new SeqCustrefno());
					return String.format("%05d", entity.getCustReferenceNo());
				} catch (Exception e) {
					e.printStackTrace();
					log.info("Exception is ---> " + e.getMessage());
					return null;
				}

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
					Predicate n1 = cb.equal(c.get("status"), "Y");
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
					
					if(itemType.equalsIgnoreCase("PRODUCT_SHORT_CODE"))          //not company based
						query.where(n1, n2, n3, n8, n9, n10, n11).orderBy(orderList);
					else
						query.where(n1, n2, n3, n4, n9, n10, n11).orderBy(orderList);
					
					
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

		//Endt Copy Quote
		public EserviceCommonDetails eserviceCommonCopyquote(CopyQuoteReq req, String refNo, List<String> branches,String loginId,String customerId,String quoteNo,String custRefNo) {
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			EserviceCommonDetails savedata = new EserviceCommonDetails();
			try {
				String userType = req.getUserType();
				String branchCode = "";

				List<EserviceCommonDetails> motor=null;
				Integer count=0;
				List<Object> list=getMasterTableCount(req.getPolicyNo());
				if (list.size() > 0) {
					count = list.size();
				}
				String prevPolicyNo=null; 
				String prevQuoteNo=null;
				String newRequestNo =null;
				String newQuoteNo =null;
				String newCustRefNo=null;
				String newCustId=null;
				Integer preEndtId=null;
				long pendingcount =0;
				if (count > 0) {
					List<EserviceCommonDetails> motors = repo.findByOriginalPolicyNo(req.getPolicyNo());
					pendingcount = motors.stream().filter(m -> m.getEndtStatus().equals("P")).count();
					if (pendingcount > 0) {
						 List<EserviceCommonDetails> pendingData = motors.stream().filter(m->m.getEndtStatus().equals("P")).collect(Collectors.toList());
						if (!pendingData.get(0).getEndorsementType().equals(Integer.valueOf(req.getEndtTypeId()))) {
							deletePreviousEndo(req,pendingData);
						
							count--;
							pendingcount=0;
						}
					}
				}
				if(count>0) {
					List<EserviceCommonDetails> motors=repo.findByOriginalPolicyNo(req.getPolicyNo());
					//motors=motors.stream().filter(distinctByKey(m ->m.getPolicyNo())).collect(Collectors.toList());
					//Compare
					motors.sort(new Comparator<EserviceCommonDetails>() {
						@Override
						public int compare(EserviceCommonDetails o1, EserviceCommonDetails o2) {
							// TODO Auto-generated method stub
							return o1.getEndtCount().compareTo(o2.getEndtCount());
						}
					}.reversed());
					
					pendingcount = motors.stream().filter(m->m.getEndtStatus().equals("P")).count();
				
					if(pendingcount>0) {
						 List<EserviceCommonDetails> pendingData = motors.stream().filter(m->m.getEndtStatus().equals("P")).collect(Collectors.toList());
						 motor= pendingData;
						 prevPolicyNo=motor.get(0).getEndtPrevPolicyNo();
						 prevQuoteNo=motor.get(0).getEndtPrevQuoteNo();
						 newRequestNo=motor.get(0).getRequestReferenceNo();
						 newQuoteNo=motor.get(0).getQuoteNo();
						 newCustRefNo=motor.get(0).getCustomerReferenceNo();
						 newCustId=motor.get(0).getCustomerId();
						 preEndtId=motor.get(0).getEndorsementType();
						 count--;
					}else {
						motor=motors;
						
						if(motors.size()>1) {
							prevPolicyNo=motors.get(0).getPolicyNo();
							prevQuoteNo =motors.get(0).getQuoteNo();
							 preEndtId=motor.get(0).getEndorsementType();
						}else {
//							prevPolicyNo=req.getPolicyNo();
//							prevQuoteNo =motor.get(0).getEndtPrevQuoteNo();
							prevPolicyNo =motor.get(0).getPolicyNo();
							prevQuoteNo = motor.get(0).getQuoteNo();
							 preEndtId=motor.get(0).getEndorsementType();
						}
					}
					
				}else {
					motor=repo.findByPolicyNoAndStatus(req.getPolicyNo(),"P");
					prevPolicyNo=req.getPolicyNo();
					prevQuoteNo =motor.get(0).getQuoteNo();
					 preEndtId=motor.get(0).getEndorsementType();
				}
				
				if(pendingcount==0) 
				{
					newRequestNo=refNo;
					newCustRefNo=custRefNo;
					newCustId=customerId;
				}	
				
//				List<Tuple> list = copyQuoteSearchDetails(searchKey, searchValue, companyId, loginId, userType,
//						branches);
				List<EserviceCommonDetails> ecommDataList=repo.findByQuoteNoOrderByRiskIdAsc(prevQuoteNo);
				List<CommonDataDetails> commDataList = commonDataRepo.findByQuoteNoAndStatusNot(prevQuoteNo,"D");
				List<EserviceCommonDetails> motors = ecommDataList.stream()
					    .filter(m -> commDataList.stream()
					        .anyMatch(risk -> m.getRiskId().equals(risk.getRiskId()) 
					                        && m.getRequestReferenceNo().equals(risk.getRequestReferenceNo())
					                        && m.getLocationId().equals(risk.getLocationId())
					                        && m.getSectionId().equals(risk.getSectionId())
					        		))
					    .collect(Collectors.toList());
				++count;
				if (motors.size() > 0) {
					for (EserviceCommonDetails data : motors) {
						CommonDataDetails matchingRisk = commDataList.stream()
								.filter(risk -> risk.getRiskId().equals(data.getRiskId())
										&& risk.getRequestReferenceNo().equals(data.getRequestReferenceNo())
										&& risk.getLocationId().equals(data.getLocationId())
										&& risk.getSectionId().equals(data.getSectionId())
										&& risk.getCoverId().equals(data.getCoverId()))
								.findFirst().orElse(null);
						EndtTypeMaster entMaster=ratingutil.getEndtMasterData(req.getInsuranceId(),req.getProductId(),req.getEndtTypeId());
								//endtTypeRepo.findByCompanyIdAndProductIdAndStatusAndEndtTypeIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqual(req.getInsuranceId(), Integer.parseInt(req.getProductId()), "Y",Integer.parseInt(req.getEndtTypeId()),new Date(), new Date());
						savedata = dozerMapper.map(data, EserviceCommonDetails.class);
						savedata.setEntryDate(new Date());
						savedata.setCreatedBy(req.getLoginId());
						savedata.setUpdatedBy(req.getLoginId());
						savedata.setUpdatedDate(new Date());
						savedata.setRequestReferenceNo(newRequestNo);
						savedata.setCustomerReferenceNo(data.getCustomerReferenceNo());
						savedata.setCustomerId(newCustId);
						savedata.setOldReqRefNo(req.getRequestReferenceNo());
						savedata.setBrokerBranchCode(data.getBrokerBranchCode());
						savedata.setActualPremiumFc(BigDecimal.ZERO);
						savedata.setActualPremiumLc(BigDecimal.ZERO);
						savedata.setOverallPremiumFc(BigDecimal.ZERO);
						savedata.setOverallPremiumLc(BigDecimal.ZERO);
						if(pendingcount==0) {
						savedata.setQuoteNo(quoteNo);
						}else {
						savedata.setQuoteNo(newQuoteNo);
						}
						savedata.setOriginalPolicyNo(req.getPolicyNo());
						savedata.setEndorsementDate(new Date());
						savedata.setEndorsementRemarks(req.getEndtRemarks());
						savedata.setEndorsementEffdate(req.getEndtEffectiveDate());
						savedata.setEndtPrevPolicyNo(prevPolicyNo);
						//savedata.setEndtPrevPolicyNo(req.getPolicyNo()+"-"+count);
						savedata.setEndtPrevQuoteNo(prevQuoteNo);
						savedata.setEndtCount(new BigDecimal(count));
						savedata.setEndtStatus("P");
						savedata.setIsFinaceYn(entMaster.getEndtTypeCategoryId()==2?"Y":"N");
						savedata.setEndtCategDesc(entMaster.getEndtTypeCategory());
						savedata.setEndorsementType(Integer.parseInt(req.getEndtTypeId()));
						savedata.setEndorsementTypeDesc(entMaster.getEndtTypeDesc());
						savedata.setStatus("E");
						savedata.setPolicyNo(req.getPolicyNo()+"-"+count);
						savedata.setApplicationId(req.getApplicationId());
						if(req.getLoginId()==null || StringUtils.isBlank(req.getLoginId())) {
							savedata.setLoginId(data.getLoginId());
						}else {
							savedata.setLoginId(req.getLoginId());
						}
						savedata.setSubUserType(req.getSubUserType());
						// Source Type Condtion
						String sourceType = savedata.getSourceType() ;
						if(StringUtils.isNotBlank(savedata.getApplicationId()) && ! "1".equalsIgnoreCase(savedata.getApplicationId()) ) {
							List<ListItemValue> sourcerTypes = genSeqNoService.getSourceTypeDropdown(savedata.getCompanyId() , savedata.getBranchCode() ,"SOURCE_TYPE"); 
							List<ListItemValue> acitveSourcerTypes = sourcerTypes.stream().filter( o -> "Y".equalsIgnoreCase(o.getStatus()) 
									&& o.getItemValue().contains(sourceType) ).collect(Collectors.toList()); 							
							savedata.setSourceType(acitveSourcerTypes.size() > 0 ? acitveSourcerTypes.get(0).getItemValue()	: 	savedata.getSourceType());			
							savedata.setSourceTypeId(acitveSourcerTypes.size() > 0 ? acitveSourcerTypes.get(0).getItemCode()	: 	savedata.getSourceTypeId());
						}
						savedata.setSumInsured(matchingRisk.getSumInsured()==null ? BigDecimal.ZERO : matchingRisk.getSumInsured() );
						savedata.setSumInsuredLc(matchingRisk.getSumInsuredLc()==null ? BigDecimal.ZERO : matchingRisk.getSumInsuredLc() );
//						savedata.setEmiPremium(BigDecimal.ONE);
						savedata.setVatPremium(matchingRisk.getVatPremium()==null ? BigDecimal.ZERO : matchingRisk.getVatPremium() );
						savedata.setEndtPremium(matchingRisk.getEndtPremium()==null ? 0 : matchingRisk.getEndtPremium() );
						savedata.setEndtVatPremium(matchingRisk.getEndtVatPremium()==null ? BigDecimal.ZERO : matchingRisk.getEndtVatPremium() );
						savedata.setActualPremiumFc(matchingRisk.getActualPremiumFc()==null ? BigDecimal.ZERO : matchingRisk.getActualPremiumFc() );
						savedata.setActualPremiumLc(matchingRisk.getActualPremiumLc()==null ? BigDecimal.ZERO : matchingRisk.getActualPremiumLc() );
						savedata.setOverallPremiumFc(matchingRisk.getOverallPremiumFc()==null ? BigDecimal.ZERO : matchingRisk.getOverallPremiumFc() );
						savedata.setOverallPremiumLc(matchingRisk.getOverallPremiumLc()==null ? BigDecimal.ZERO : matchingRisk.getOverallPremiumLc() );
						repo.saveAndFlush(savedata);
					}
		
				}
				System.out.println("*************EserviceCommonDetails************");
				System.out.println("Old QUOTE NO:"+prevQuoteNo);
				System.out.println("Old Customer Id:"+customerId);
				System.out.println("Old Reference No:"+refNo);
				System.out.println("QUOTE NO:"+quoteNo);
				System.out.println("New Customer Id:"+newCustId);
				System.out.println("Reference No:"+newRequestNo);
				System.out.println("PreQuoteNo:"+prevPolicyNo);
				System.out.println("OriginalPoicyNo:"+req.getPolicyNo());
				System.out.println("Policy No:"+req.getPolicyNo()+"-"+count);
				System.out.println("**********************************************");
				if (pendingcount == 0) {
					// Copy Quote Home Position Master
					 homeEndoCopyQuote(req, refNo, customerId, quoteNo, loginId,prevPolicyNo,prevQuoteNo,count,custRefNo);

					// Copy Quote Personal Info
					 personolInfoEndoCopyQuote(req, customerId, prevPolicyNo,prevQuoteNo, count,custRefNo);

					// Copy Quote Policy Cover Data
				//	 policyCoverDataEndocopyQuote(req, refNo, quoteNo, loginId, prevPolicyNo,prevQuoteNo, count,preEndtId);

					// Copy COVER_DOCUMENT_UPLOAD_DETAILS
					coverDocumentUploadDetailsEndoCopyquote(req, refNo, quoteNo, customerId, loginId,prevPolicyNo,prevQuoteNo,count);
				
					// Copy ESERVICE_SECTION_DETAILS
					eserviceSectionDetailsEndoCopyquote(req, refNo, quoteNo, customerId, loginId,prevPolicyNo,prevQuoteNo,count,custRefNo);
					// Copy Section Data Details
					sectionDataDetailsEndoCopyquote(req, refNo, quoteNo, customerId, loginId,prevPolicyNo,prevQuoteNo,count,custRefNo);
					// Copy Product Employee
					personalAccidentEndoCopyquote(req, refNo, quoteNo, customerId, loginId,prevPolicyNo,prevQuoteNo,count,custRefNo);
					// Copy Common Data Details
					commonDataDetailsEndoCopyquote(req, refNo, quoteNo, customerId, loginId,prevPolicyNo,prevQuoteNo,count,custRefNo);
					//EserviceBuilding Details
					esrviceBuildingDetailsEndoCopyquote(req, refNo, quoteNo, customerId, loginId,prevPolicyNo,prevQuoteNo,count,custRefNo);
					// Copy BUILDING_DETAILS
					buildingDetailsEndoCopyquote(req, refNo, quoteNo, customerId, loginId,prevPolicyNo,prevQuoteNo,count,custRefNo);
				}
				//res.setRequestReferenceNo(newRequestNo);
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return savedata;
		}

//Count
		public List<Object> getMasterTableCount(String policyNo) {
			List<Object> list = new ArrayList<Object>();
			try {
				// Find Latest Record
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Object> query = cb.createQuery(Object.class);
				// Find all
				Root<EserviceCommonDetails> b = query.from(EserviceCommonDetails.class);
				// Select
				query.multiselect(b.get("policyNo").alias("policyNo"));

				Predicate n1 = cb.equal(b.get("originalPolicyNo"), policyNo);
				query.where(n1).groupBy(b.get("policyNo"));

				// Get Result
				TypedQuery<Object> result = em.createQuery(query);
				list = result.getResultList();

			} catch (Exception e) {
				e.printStackTrace();
				log.info(e.getMessage());
			}
			return list;
		}

		//Delete Previous Endo
		private CopyQuoteSuccessRes deletePreviousEndo(CopyQuoteReq req, List<EserviceCommonDetails> motorsPending) {
			CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
			try {
				String quoteNo=motorsPending.get(0).getQuoteNo();
				// Delete Old Record
				
				//E service Motor Details
				if(motorsPending.size()>0&&motorsPending!=null) {
					repo.deleteAll(motorsPending);
				}
				//E service Customer Details 
				HomePositionMaster homeData=homePosistionRepo.findByQuoteNo(quoteNo);
				String customerId=homeData.getCustomerId();
				
				PersonalInfo personalInfoData=personalInforepo.findByCustomerId(customerId);
//				EserviceCustomerDetails custData = custRepo.findByCustomerReferenceNo(personalInfoData.getCustomerReferenceNo());
//				if (custData!=null) {
//					custRepo.delete(custData);
//				}
				//Personal Info
				if(personalInfoData!=null) {
					personalInforepo.delete(personalInfoData);
				}
				//Home Position master
				if(homeData!=null) {
					homePosistionRepo.delete(homeData);				}
				//Policy Cover Data
				List<PolicyCoverData> policyCoverData = policyCoverDataRepo.findByQuoteNo(quoteNo);
				if (policyCoverData.size() > 0&& policyCoverData!=null) {
					policyCoverDataRepo.deleteAll(policyCoverData);
				}
				//Cover Document Upload Details
				List<DocumentTransactionDetails> coverDocList = coverDocUploadDetails.findByQuoteNo(quoteNo);
				if (coverDocList.size() > 0&&coverDocList!=null) {
					coverDocUploadDetails.deleteAll(coverDocList);
				}
				//Eservice SectionDetails
				List<EserviceSectionDetails> eseSecList = eserSecRepo.findByQuoteNo(quoteNo);
				if (eseSecList.size() > 0&&eseSecList!=null) {
					eserSecRepo.deleteAll(eseSecList);
				}
				// Section Data Details
				List<SectionDataDetails> secDataList = sectionDataRepo.findByQuoteNo(quoteNo);
				if (secDataList.size() > 0&&secDataList!=null) {
					sectionDataRepo.deleteAll(secDataList);
				}
				//ProductEmployeeDetails
				List<ProductEmployeeDetails> perList = pARepo.findByQuoteNo(quoteNo);
				if (perList.size() > 0&& perList!=null) {
					pARepo.deleteAll(perList);
				}
				//CommonDataDetals
				List<CommonDataDetails> commonData = commonDataRepo.findByQuoteNo(quoteNo);
				if (commonData.size() > 0 &&commonData!=null) {
					commonDataRepo.deleteAll(commonData);
				}
				//EserviceBuildingDetals
				List<EserviceBuildingDetails> buildingDatas = eseBuildingRepo.findByQuoteNoOrderByRiskIdAsc(quoteNo);
				if (buildingDatas.size()>0) {
					eseBuildingRepo.deleteAll(buildingDatas);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return res;

		}

	/*	private CopyQuoteSuccessRes eserviceCustDetailsEndoCopyquote(CopyQuoteReq req, String refNo, String quoteNo, String customerId,String loginId, String prevPolicyNo, String prevQuoteNo, Integer count,String custRefNo) {
			CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
			EserviceCustomerDetails savedata = new EserviceCustomerDetails();
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			try {
				EndtTypeMaster entMaster = endtTypeRepo.findByCompanyIdAndProductIdAndStatusAndEndtTypeIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqual(
						req.getInsuranceId(), Integer.parseInt(req.getProductId()), "Y",
						Integer.parseInt(req.getEndtTypeId()), new Date(), new Date());
				HomePositionMaster homeData=homePosistionRepo.findByQuoteNo(req.getQuoteNo());
				String olsCustomerId=homeData.getCustomerId();
				
				PersonalInfo personalInfoData=personalInforepo.findByCustomerId(olsCustomerId);
				EserviceCustomerDetails custData = custRepo.findByCustomerReferenceNo(personalInfoData.getCustomerReferenceNo());
				if (custData!=null) 
						savedata = dozerMapper.map(custData, EserviceCustomerDetails.class);
						savedata.setEntryDate(new Date());
						savedata.setCustomerReferenceNo(custRefNo);
						savedata.setCreatedBy(loginId);
						savedata.setUpdatedBy(loginId);
						savedata.setUpdatedDate(new Date());
						savedata.setOriginalPolicyNo(req.getPolicyNo());
						savedata.setEndorsementDate(new Date());
						savedata.setEndorsementRemarks(req.getEndtRemarks());
						savedata.setEndorsementEffdate(req.getEndtEffectiveDate());
						savedata.setEndtPrevPolicyNo(prevPolicyNo);
						savedata.setEndtPrevQuoteNo(prevQuoteNo);
						savedata.setEndtCount(new BigDecimal(count));
						savedata.setEndtStatus("P");
						savedata.setIsFinaceYn(entMaster.getEndtTypeCategoryId() == 2 ? "Y" : "N");
						savedata.setEndtCategDesc(entMaster.getEndtTypeCategory());
						savedata.setEndorsementType(Integer.parseInt(req.getEndtTypeId()));
						savedata.setEndorsementTypeDesc(entMaster.getEndtTypeDesc());
						savedata.setStatus("E");
						//savedata.setPolicyNo(req.getPolicyNo() + "-" + count);
						custRepo.saveAndFlush(savedata);
			
			
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return res;

			
		}*/

		
		//Home Position Master Endt Copy Quote
		@Transactional
		public CopyQuoteSuccessRes homeEndoCopyQuote(CopyQuoteReq req,String refNo,String customerId,String quoteNo,String loginId,	String prevPolicyNo,
		String prevQuoteNo,Integer count,String custRefNo) {
			CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
			HomePositionMaster savedata = new HomePositionMaster();
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			
		try {
			EndtTypeMaster entMaster=ratingutil.getEndtMasterData(req.getInsuranceId(),req.getProductId(),req.getEndtTypeId());
					//endtTypeRepo.findByCompanyIdAndProductIdAndStatusAndEndtTypeIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqual(req.getInsuranceId(), Integer.parseInt(req.getProductId()), "Y",Integer.parseInt(req.getEndtTypeId()),new Date(), new Date());
			String endtFeeYn=entMaster.getEndtFeeYn();
			BigDecimal endtPre=BigDecimal.ZERO;
			BigDecimal endtPremiumtax=BigDecimal.ZERO;
			Double endtPercent=0d;
			HomePositionMaster homeData=homePosistionRepo.findByQuoteNo(prevQuoteNo);
			Double tax=Double.valueOf(homeData.getVatPercent().toString());
			BigDecimal exchangeRate=homeData.getExchangeRate();
			BigDecimal overAllPremiumFc=new BigDecimal(homeData.getOverallPremiumFc().toString());
			if ("Y".equalsIgnoreCase(endtFeeYn)&& StringUtils.isNotBlank(endtFeeYn)) {
				endtPercent = Double.valueOf(entMaster.getEndtFeePercent());
				endtPre = domath(entMaster.getCalcTypeId(), endtPercent, overAllPremiumFc,exchangeRate);
				endtPremiumtax=endtPre.multiply(new BigDecimal(tax/100));
			}
			
			
			String txt="";
			if(endtPre.intValue()>0) {
				txt="CHARGE";
			}else {
				txt="REFUND";
			}
			savedata = dozerMapper.map(homeData, HomePositionMaster.class);
			savedata.setRequestReferenceNo(refNo);
			savedata.setCustomerId(customerId);
			savedata.setQuoteNo(quoteNo);
			savedata.setEndtTypeId(req.getEndtTypeId());
			savedata.setEndtDate(new Date());
			savedata.setEndtBy(loginId);
			savedata.setEndtStatus("P");
			savedata.setEndtCommission(BigDecimal.valueOf(0));
			savedata.setQuoteCreatedDate(new Date());
			savedata.setEntryDate(new Date());
			if("Y".equalsIgnoreCase(endtFeeYn)&& StringUtils.isNotBlank(endtFeeYn)){
				savedata.setEndtPremium(endtPre);
				savedata.setIsChargRefund(txt);
				}else {
				savedata.setEndtPremium(endtPre);
				savedata.setIsChargRefund("");
			}
			savedata.setEndtPremiumTax(endtPremiumtax);
			savedata.setRequestReferenceNo(refNo);
			savedata.setOriginalPolicyNo(req.getPolicyNo());
			savedata.setEndorsementRemarks(req.getEndtRemarks());
			savedata.setEndorsementEffdate(req.getEndtEffectiveDate());
			savedata.setEndtPrevPolicyNo(prevPolicyNo);
			savedata.setEndtPrevQuoteNo(prevQuoteNo);
			savedata.setEndtCount(count);
			savedata.setEndtStatus("P");
			savedata.setIsFinacialEndt("N");
			savedata.setEndtCategDesc(entMaster.getEndtTypeCategory());
			savedata.setEndtTypeDesc(entMaster.getEndtTypeDesc());
			savedata.setStatus("E");
			savedata.setPolicyNo(req.getPolicyNo()+"-"+count);
			savedata.setAhpolIdx(Long.valueOf(count));

			savedata.setApplicationId(req.getApplicationId());
			if(req.getLoginId()==null || StringUtils.isBlank(req.getLoginId())) {
				savedata.setLoginId(homeData.getLoginId());
			}else {
				savedata.setLoginId(req.getLoginId());
			}
			savedata.setSubUserType(req.getSubUserType());
			homePosistionRepo.saveAndFlush(savedata);
		
			System.out.println("QUOTE NO:"+quoteNo);
			System.out.println("Customer Id:"+customerId);
			System.out.println("Reference No:"+refNo);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return res;
		}
		
		protected BigDecimal domath(String calctype, Double rate,BigDecimal si,BigDecimal exchangeRate) {
			BigDecimal d=BigDecimal.ZERO;
			if("P".equals(calctype)) {
			d = si.multiply(new BigDecimal(rate/100)/*, round*/);	
			 }else if("A".equals(calctype)) {
			d=(new BigDecimal(rate).divide(exchangeRate/*,round*/));// for foreign currency calculation we have to divide by exchange rate	
			 }else if("M".equals(calctype)) {
			 d = si.multiply(new BigDecimal(rate/1000)/*, round*/);	
			 }
			return d;
			}

		//Personal Info Endt Copy Quote
		@Transactional
		public CopyQuoteSuccessRes personolInfoEndoCopyQuote(CopyQuoteReq req,String customerId,String prevPolicyNo,
				String prevQuoteNo,Integer count,String custRefNo) {
			CopyQuoteSuccessRes res =new CopyQuoteSuccessRes();
			PersonalInfo savedata = new PersonalInfo();
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			try {
				EndtTypeMaster entMaster=ratingutil.getEndtMasterData(req.getInsuranceId(),req.getProductId(),req.getEndtTypeId());
				//endtTypeRepo.findByCompanyIdAndProductIdAndStatusAndEndtTypeIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqual(req.getInsuranceId(), Integer.parseInt(req.getProductId()), "Y",Integer.parseInt(req.getEndtTypeId()), new Date(), new Date());
				HomePositionMaster homeData=homePosistionRepo.findByQuoteNo(prevQuoteNo);
				String olsCustomerId=homeData.getCustomerId();
				
				PersonalInfo personalInfoData=personalInforepo.findByCustomerId(olsCustomerId);
				savedata = dozerMapper.map(personalInfoData, PersonalInfo.class);
				savedata.setCustomerId(customerId);
				//savedata.setCustomerReferenceNo(custRefNo);
				savedata.setEntryDate(new Date());
				savedata.setCreatedBy(req.getLoginId());
				savedata.setUpdatedBy(req.getLoginId());
				savedata.setUpdatedDate(new Date());
				
				//Endo
				savedata.setOriginalPolicyNo(req.getPolicyNo());
				savedata.setEndorsementDate(new Date());
				savedata.setEndorsementRemarks(req.getEndtRemarks());
				savedata.setEndorsementEffdate(req.getEndtEffectiveDate());
				savedata.setEndtPrevPolicyNo(prevPolicyNo);
				savedata.setEndtPrevQuoteNo(prevQuoteNo);
				savedata.setEndtCount(new BigDecimal(count));
				savedata.setEndtStatus("P");
				savedata.setIsFinaceYn(entMaster.getEndtTypeCategoryId()==2?"Y":"N");
				savedata.setEndtCategDesc(entMaster.getEndtTypeCategory());
				savedata.setEndorsementType(Integer.parseInt(req.getEndtTypeId()));
				savedata.setEndorsementTypeDesc(entMaster.getEndtTypeDesc());
				savedata.setStatus("E");
				personalInforepo.saveAndFlush(savedata);
	
				//res.setSuccessId(customerId);
				
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is --->" + e.getMessage());
				return null;
			}
			return res;
		}

		// Policy Cover Data Enst Copy Quote
		public CopyQuoteSuccessRes policyCoverDataEndocopyQuote(CopyQuoteReq req, String refNo, String quoteNo,
				String loginId, String prevPolicyNo, String prevQuoteNo, Integer count,Integer preEndtId) {
			CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
			PolicyCoverData savedata = new PolicyCoverData();
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			try {
				CoverMaster coverdata = null;
				EndtTypeMaster entMaster =ratingutil.getEndtMasterData(req.getInsuranceId(),req.getProductId(),req.getEndtTypeId());
				String endTypeDesc = entMaster.getEndtTypeDesc();
				String endtFeeYn = entMaster.getEndtFeeYn();
				String coverDesc = "";
				BigDecimal endtFee = BigDecimal.ZERO;
				if(StringUtils.isBlank(entMaster.getEndtFeePercent())|entMaster.getEndtFeePercent()==null) {
					endtFee = BigDecimal.ZERO;
				}else {
					endtFee =new BigDecimal(entMaster.getEndtFeePercent());
				}
				BigDecimal endtAmt = BigDecimal.ZERO;
				List<PolicyCoverData> policyCoverData = policyCoverDataRepo.findByQuoteNo(prevQuoteNo);
				if (policyCoverData.size() > 0) {
					List<PolicyCoverData> oldData =null;
					if (preEndtId != null && preEndtId > 0) {
						oldData = policyCoverData.stream().filter(d -> (!preEndtId.equals(d.getDiscLoadId())))
								.collect(Collectors.toList());
					} else {
						oldData = policyCoverData;
					}
					for (PolicyCoverData data : oldData) {
						savedata = dozerMapper.map(data, PolicyCoverData.class);
						savedata.setRequestReferenceNo(refNo);
						savedata.setQuoteNo(quoteNo);
						savedata.setEntryDate(new Date());
						savedata.setCreatedBy(loginId);
						savedata.setEndtCount(new BigDecimal(count));
					//	savedata.setStatus("E");
						policyCoverDataRepo.saveAndFlush(savedata);
					}

					List<PolicyCoverData> basecovers1 = policyCoverData.stream()
							.filter(d -> ("B".equalsIgnoreCase(d.getCoverageType()))).collect(Collectors.toList());
					for (PolicyCoverData data : basecovers1) {
						coverDesc = basecovers1.get(0).getCoverDesc();
						savedata = dozerMapper.map(data, PolicyCoverData.class);
						savedata.setDiscLoadId(Integer.valueOf(req.getEndtTypeId()));
						savedata.setCoverName(coverDesc + " " + endTypeDesc + " " + count);
						savedata.setCoverDesc(coverDesc + " " + endTypeDesc + " " + count);
						savedata.setCoverageType("E");
						savedata.setRequestReferenceNo(refNo);
						savedata.setQuoteNo(quoteNo);
						savedata.setEntryDate(new Date());
						savedata.setCreatedBy(loginId);

						savedata.setPremiumAfterDiscountFc(BigDecimal.ZERO);
						savedata.setPremiumAfterDiscountLc(BigDecimal.ZERO);
						savedata.setPremiumBeforeDiscountFc(BigDecimal.ZERO);
						savedata.setPremiumBeforeDiscountLc(BigDecimal.ZERO);
						savedata.setPremiumExcludedTaxFc(BigDecimal.ZERO);
						savedata.setPremiumExcludedTaxLc(BigDecimal.ZERO);
						savedata.setPremiumIncludedTaxFc(BigDecimal.ZERO);
						savedata.setPremiumIncludedTaxLc(BigDecimal.ZERO);

						savedata.setDependentCoverYn("N");
						savedata.setDependentCoverId(null);

						savedata.setMinimumPremium(BigDecimal.ZERO);
						savedata.setIsTaxExtempted("N");
						savedata.setTaxId(0);
						savedata.setTaxRate(BigDecimal.ZERO);
						savedata.setTaxDesc("");
						savedata.setEndtCount(new BigDecimal(count));
						savedata.setDiscountCoverId(data.getCoverId());
						policyCoverDataRepo.saveAndFlush(savedata);
					}
					List<PolicyCoverData> basecovers2 = policyCoverData.stream()
							.filter(d -> ("T".equalsIgnoreCase(d.getCoverageType()))).collect(Collectors.toList());
					for (PolicyCoverData data : basecovers2) {
						BigDecimal divisor = new BigDecimal(100);
						Double taxRate = Double.valueOf(data.getTaxRate().toString());
						endtAmt = endtFee.multiply(new BigDecimal(taxRate / 100));
						coverDesc = data.getCoverName();
						savedata = dozerMapper.map(data, PolicyCoverData.class);
						savedata.setRequestReferenceNo(refNo);
						savedata.setQuoteNo(quoteNo);
						savedata.setEntryDate(new Date());
						savedata.setCreatedBy(loginId);
						savedata.setEndtCount(new BigDecimal(count));
						savedata.setDiscLoadId(Integer.valueOf(req.getEndtTypeId()));
						savedata.setCoverName(coverDesc + " " + endTypeDesc);
						savedata.setCoverDesc(coverDesc + " " + endTypeDesc);
						savedata.setPremiumAfterDiscountFc(BigDecimal.ZERO);
						savedata.setPremiumAfterDiscountLc(BigDecimal.ZERO);
						savedata.setPremiumBeforeDiscountFc(BigDecimal.ZERO);
						savedata.setPremiumBeforeDiscountLc(BigDecimal.ZERO);
						savedata.setPremiumExcludedTaxFc(BigDecimal.ZERO);
						savedata.setPremiumExcludedTaxLc(BigDecimal.ZERO);
						savedata.setPremiumIncludedTaxFc(BigDecimal.ZERO);
						savedata.setPremiumIncludedTaxLc(BigDecimal.ZERO);
						savedata.setDependentCoverYn("N");
						savedata.setDependentCoverId(null);

						savedata.setMinimumPremium(BigDecimal.ZERO);
						savedata.setTaxId(data.getTaxId());
						savedata.setTaxRate(data.getTaxRate());
						savedata.setTaxCalcType(data.getTaxCalcType());
						savedata.setTaxDesc(endTypeDesc + " VAT");

						savedata.setTaxAmount(endtAmt);
						savedata.setIsTaxExtempted("N");
						savedata.setEndtCount(new BigDecimal(count));
						savedata.setDiscountCoverId(data.getCoverId());
						policyCoverDataRepo.saveAndFlush(savedata);
					}
					if ("Y".equalsIgnoreCase(endtFeeYn)&& StringUtils.isNotBlank(endtFeeYn)) {
						for (PolicyCoverData data : basecovers2) {
							coverDesc = data.getCoverName();
							savedata = dozerMapper.map(data, PolicyCoverData.class);
							savedata.setRequestReferenceNo(refNo);
							savedata.setQuoteNo(quoteNo);
							savedata.setEntryDate(new Date());
							savedata.setCreatedBy(loginId);
							savedata.setDiscLoadId(Integer.valueOf(req.getEndtTypeId()));
							savedata.setCoverName(coverDesc + " " + endTypeDesc + " Endorsement Fee" + count);
							savedata.setCoverDesc(coverDesc + " " + endTypeDesc + " Endorsement Fee" + count);
							savedata.setPremiumAfterDiscountFc(BigDecimal.ZERO);
							savedata.setPremiumAfterDiscountLc(BigDecimal.ZERO);
							savedata.setPremiumBeforeDiscountFc(BigDecimal.ZERO);
							savedata.setPremiumBeforeDiscountLc(BigDecimal.ZERO);
							savedata.setPremiumExcludedTaxFc(BigDecimal.ZERO);
							savedata.setPremiumExcludedTaxLc(BigDecimal.ZERO);
							savedata.setPremiumIncludedTaxFc(endtAmt.add(endtFee));
							savedata.setPremiumIncludedTaxLc(endtAmt.add(endtFee));
							savedata.setDependentCoverYn("N");
							savedata.setDependentCoverId(null);
							savedata.setTaxId(Integer.valueOf(req.getEndtTypeId()));
							savedata.setTaxRate(endtFee);
							savedata.setTaxAmount(endtFee);
							savedata.setTaxCalcType(data.getTaxCalcType());
							savedata.setTaxDesc(endTypeDesc + " Endorsement Fee");
							savedata.setEndtCount(new BigDecimal(count));
							savedata.setDiscountCoverId(data.getCoverId());
							savedata.setCoverageType("T");
							savedata.setIsTaxExtempted("N");
							policyCoverDataRepo.saveAndFlush(savedata);
						}
					}
					List<PolicyCoverData> basecovers3 = policyCoverData.stream()
							.filter(d -> ("O".equalsIgnoreCase(d.getCoverageType()))).collect(Collectors.toList());
					for (PolicyCoverData data : basecovers3) {
						coverDesc = data.getCoverDesc();
						savedata = dozerMapper.map(data, PolicyCoverData.class);
						savedata.setRequestReferenceNo(refNo);
						savedata.setQuoteNo(quoteNo);
						savedata.setEntryDate(new Date());
						savedata.setCreatedBy(loginId);
						savedata.setCoverageType("T");
						savedata.setDiscLoadId(Integer.valueOf(req.getEndtTypeId()));
						savedata.setCoverName(coverDesc + " " + endTypeDesc + " " + count);
						savedata.setCoverDesc(coverDesc + " " + endTypeDesc + " " + count);
						savedata.setPremiumAfterDiscountFc(BigDecimal.ZERO);
						savedata.setPremiumAfterDiscountLc(BigDecimal.ZERO);
						savedata.setPremiumBeforeDiscountFc(BigDecimal.ZERO);
						savedata.setPremiumBeforeDiscountLc(BigDecimal.ZERO);
						savedata.setPremiumExcludedTaxFc(BigDecimal.ZERO);
						savedata.setPremiumExcludedTaxLc(BigDecimal.ZERO);
						savedata.setPremiumIncludedTaxFc(BigDecimal.ZERO);
						savedata.setPremiumIncludedTaxLc(BigDecimal.ZERO);
						savedata.setDependentCoverYn("N");
						savedata.setDependentCoverId(null);
						savedata.setTaxId(0);
						savedata.setTaxRate(BigDecimal.ZERO);
						savedata.setTaxCalcType(data.getTaxCalcType());
						savedata.setTaxDesc(" ");
						savedata.setIsTaxExtempted("N");
						savedata.setEndtCount(new BigDecimal(count));
						savedata.setDiscountCoverId(data.getCoverId());
						policyCoverDataRepo.saveAndFlush(savedata);
					}
					

				}
				System.out.println("*************PolicyCoverData************");
				System.out.println("Old QUOTE NO:"+prevQuoteNo);
				//System.out.println("Old Customer Id:"+customerId);
				System.out.println("Old Reference No:"+refNo);
				System.out.println("QUOTE NO:"+quoteNo);
				//System.out.println("New Customer Id:"+newCustId);
				//System.out.println("Reference No:"+newRequestNo);
				System.out.println("PreQuoteNo:"+prevPolicyNo);
				System.out.println("OriginalPoicyNo:"+req.getPolicyNo());
				System.out.println("Policy No:"+req.getPolicyNo()+"-"+count);
				System.out.println("**********************************************");
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return res;
		}

		// Section Data Details
		private CopyQuoteSuccessRes sectionDataDetailsEndoCopyquote(CopyQuoteReq req, String refNo, String quoteNo,
				String customerId, String loginId, String prevPolicyNo, String prevQuoteNo, Integer count,
				String custRefNo) {
			CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
			SectionDataDetails savedata = new SectionDataDetails();
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			try {
				EndtTypeMaster entMaster = ratingutil.getEndtMasterData(req.getInsuranceId(), req.getProductId(),
						req.getEndtTypeId()); /*
												 * endtTypeRepo
												 * .findByCompanyIdAndProductIdAndStatusAndEndtTypeIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqual(
												 * req.getInsuranceId(), Integer.parseInt(req.getProductId()), "Y",
												 * Integer.parseInt(req.getEndtTypeId()), new Date(), new Date());
												 */

				List<SectionDataDetails> eserSec = sectionDataRepo.findByQuoteNoAndStatusNot(prevQuoteNo,"D");
				if (eserSec != null && eserSec.size() > 0) {
					for (SectionDataDetails data : eserSec) {
						savedata = dozerMapper.map(data, SectionDataDetails.class);
						savedata.setEntryDate(new Date());
						savedata.setCustomerReferenceNo(custRefNo);
						savedata.setRequestReferenceNo(refNo);
						savedata.setCustomerId(customerId);
						savedata.setQuoteNo(quoteNo);
						savedata.setCreatedBy(loginId);
						savedata.setUpdatedBy(loginId);
						savedata.setUpdatedDate(new Date());
						savedata.setOriginalPolicyNo(req.getPolicyNo());
						savedata.setEndorsementDate(new Date());
						savedata.setEndorsementRemarks(req.getEndtRemarks());
						savedata.setEndorsementEffdate(req.getEndtEffectiveDate());
						savedata.setEndtPrevPolicyNo(prevPolicyNo);
						savedata.setEndtPrevQuoteNo(prevQuoteNo);
						savedata.setEndtCount(new BigDecimal(count));
						savedata.setEndtStatus("P");
						savedata.setIsFinaceYn(entMaster.getEndtTypeCategoryId() == 2 ? "Y" : "N");
						savedata.setEndtCategDesc(entMaster.getEndtTypeCategory());
						savedata.setEndorsementType(Integer.parseInt(req.getEndtTypeId()));
						savedata.setEndorsementTypeDesc(entMaster.getEndtTypeDesc());
						savedata.setStatus("E");
						savedata.setPolicyNo(req.getPolicyNo() + "-" + count);
						sectionDataRepo.saveAndFlush(savedata);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return res;

		}
		// Common Data Details
		private CopyQuoteSuccessRes commonDataDetailsEndoCopyquote(CopyQuoteReq req, String refNo, String quoteNo,
				String customerId, String loginId, String prevPolicyNo, String prevQuoteNo, Integer count,
				String custRefNo) {
			CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
			CommonDataDetails savedata = new CommonDataDetails();
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			try {
				EndtTypeMaster entMaster = ratingutil.getEndtMasterData(req.getInsuranceId(), req.getProductId(),
						req.getEndtTypeId()); /*
												 * endtTypeRepo
												 * .findByCompanyIdAndProductIdAndStatusAndEndtTypeIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqual(
												 * req.getInsuranceId(), Integer.parseInt(req.getProductId()), "Y",
												 * Integer.parseInt(req.getEndtTypeId()), new Date(), new Date());
												 */

				List<CommonDataDetails> eserSec = commonDataRepo.findByQuoteNo(prevQuoteNo);
				if (eserSec != null && eserSec.size() > 0) {
					for (CommonDataDetails data : eserSec) {
						savedata = dozerMapper.map(data, CommonDataDetails.class);
						savedata.setEntryDate(new Date());
						savedata.setCustomerReferenceNo(custRefNo);
						savedata.setRequestReferenceNo(refNo);
						savedata.setCustomerId(customerId);
						savedata.setQuoteNo(quoteNo);
						savedata.setCreatedBy(loginId);
						savedata.setUpdatedBy(loginId);
						savedata.setUpdatedDate(new Date());
						savedata.setOriginalPolicyNo(req.getPolicyNo());
						savedata.setEndorsementDate(new Date());
						savedata.setEndorsementRemarks(req.getEndtRemarks());
						savedata.setEndorsementEffdate(req.getEndtEffectiveDate());
						savedata.setEndtPrevPolicyNo(prevPolicyNo);
						savedata.setEndtPrevQuoteNo(prevQuoteNo);
						savedata.setEndtCount(new BigDecimal(count));
						savedata.setEndtStatus("P");
						savedata.setIsFinaceYn(entMaster.getEndtTypeCategoryId() == 2 ? "Y" : "N");
						savedata.setEndtCategDesc(entMaster.getEndtTypeCategory());
						savedata.setEndorsementType(Integer.parseInt(req.getEndtTypeId()));
						savedata.setEndorsementTypeDesc(entMaster.getEndtTypeDesc());
						savedata.setStatus("E");
						savedata.setPolicyNo(req.getPolicyNo() + "-" + count);
						savedata.setApplicationId(req.getApplicationId());
						savedata.setLoginId(req.getLoginId()==null?data.getLoginId():(req.getLoginId()));
						savedata.setSubUserType(req.getSubUserType());
						commonDataRepo.saveAndFlush(savedata);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return res;

		}
		// Document Enst Copy Quote
		public CopyQuoteSuccessRes coverDocumentUploadDetailsEndoCopyquote(CopyQuoteReq req, String refNo,
				String quoteNo, String customerId, String loginId, String prevPolicyNo, String prevQuoteNo,
				Integer count) {
			CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
			DocumentTransactionDetails savedata = new DocumentTransactionDetails();
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			try {
				EndtTypeMaster entMaster = ratingutil.getEndtMasterData(req.getInsuranceId(),req.getProductId(),req.getEndtTypeId());/*endtTypeRepo.findByCompanyIdAndProductIdAndStatusAndEndtTypeIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqual(
						req.getInsuranceId(), Integer.parseInt(req.getProductId()), "Y",
						Integer.parseInt(req.getEndtTypeId()), new Date(), new Date());*/
				List<DocumentTransactionDetails> motorData = coverDocUploadDetails.findByQuoteNo(prevQuoteNo);
				if (motorData.size() > 0) {
					for (DocumentTransactionDetails data : motorData) {
						savedata = dozerMapper.map(data, DocumentTransactionDetails.class);
						savedata.setRequestReferenceNo(refNo);
						savedata.setQuoteNo(quoteNo);
						savedata.setEntryDate(new Date());
						savedata.setCreatedBy(loginId);
						savedata.setOriginalPolicyNo(req.getPolicyNo());
						savedata.setEndorsementDate(new Date());
						savedata.setEndorsementRemarks(req.getEndtRemarks());
						savedata.setEndorsementEffdate(req.getEndtEffectiveDate());
						savedata.setEndtPrevPolicyNo(prevPolicyNo);
						savedata.setEndtPrevQuoteNo(prevQuoteNo);
						savedata.setEndtCount(new BigDecimal(count));
						savedata.setEndtStatus("P");
						savedata.setIsFinaceYn(entMaster.getEndtTypeCategoryId() == 2 ? "Y" : "N");
						savedata.setEndtCategDesc(entMaster.getEndtTypeCategory());
						savedata.setEndorsementType(Integer.parseInt(req.getEndtTypeId()));
						savedata.setEndorsementTypeDesc(entMaster.getEndtTypeDesc());
						savedata.setStatus("E");
						//savedata.setPolicyNo(req.getPolicyNo() + "-" + count);
						coverDocUploadDetails.saveAndFlush(savedata);
					}
				}
			
			
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return res;
		}
		//Eservice Section Details
		private CopyQuoteSuccessRes eserviceSectionDetailsEndoCopyquote(CopyQuoteReq req, String refNo, String quoteNo, String customerId,
				String loginId, String prevPolicyNo, String prevQuoteNo, Integer count, String custRefNo) {
			CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
			EserviceSectionDetails savedata = new EserviceSectionDetails();
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			try {
				EndtTypeMaster entMaster = ratingutil.getEndtMasterData(req.getInsuranceId(),req.getProductId(),req.getEndtTypeId()); /*endtTypeRepo
						.findByCompanyIdAndProductIdAndStatusAndEndtTypeIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqual(
								req.getInsuranceId(), Integer.parseInt(req.getProductId()), "Y",
								Integer.parseInt(req.getEndtTypeId()), new Date(), new Date());*/
				List<EserviceSectionDetails> eserSec = eserSecRepo.findByQuoteNoAndStatusNotOrderByRiskIdAsc(prevQuoteNo,"D");
				List<SectionDataDetails> secList = sectionDataRepo.findByQuoteNoAndStatusNotOrderByRiskIdAsc(prevQuoteNo,"D");
				
				List<EserviceSectionDetails> filteredSectionList = eserSec.stream()
					    .filter(m -> secList.stream()
					        .anyMatch(risk -> m.getRiskId().equals(risk.getRiskId()) 
					        		  && m.getRequestReferenceNo().equals(risk.getRequestReferenceNo())
					                        && m.getLocationId().equals(risk.getLocationId())
					                        && m.getSectionId().equals(risk.getSectionId())
					        		))
					    .collect(Collectors.toList());
				
				
				
				
				if (filteredSectionList != null && filteredSectionList.size()>0 ) {
					for (EserviceSectionDetails data : filteredSectionList) {
						savedata = dozerMapper.map(data, EserviceSectionDetails.class);
						savedata.setEntryDate(new Date());
						savedata.setCustomerReferenceNo(custRefNo);
						savedata.setRequestReferenceNo(refNo);
						savedata.setCustomerId(customerId);
						savedata.setQuoteNo(quoteNo);
						savedata.setCreatedBy(loginId);
						savedata.setUpdatedBy(loginId);
						savedata.setUpdatedDate(new Date());
						savedata.setOriginalPolicyNo(req.getPolicyNo());
						savedata.setEndorsementDate(new Date());
						savedata.setEndorsementRemarks(req.getEndtRemarks());
						savedata.setEndorsementEffdate(req.getEndtEffectiveDate());
						savedata.setEndtPrevPolicyNo(prevPolicyNo);
						savedata.setEndtPrevQuoteNo(prevQuoteNo);
						savedata.setEndtCount(new BigDecimal(count));
						savedata.setEndtStatus("P");
						savedata.setIsFinaceYn(entMaster.getEndtTypeCategoryId() == 2 ? "Y" : "N");
						savedata.setEndtCategDesc(entMaster.getEndtTypeCategory());
						savedata.setEndorsementType(Integer.parseInt(req.getEndtTypeId()));
						savedata.setEndorsementTypeDesc(entMaster.getEndtTypeDesc());
						savedata.setStatus("E");
						savedata.setPolicyNo(req.getPolicyNo() + "-" + count);
						eserSecRepo.saveAndFlush(savedata);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return res;

		}
		//ProductEmployeeDetails
		private CopyQuoteSuccessRes personalAccidentEndoCopyquote(CopyQuoteReq req, String refNo, String quoteNo, String customerId,String loginId, String prevPolicyNo, String prevQuoteNo, Integer count, String custRefNo) {
			CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
			ProductEmployeeDetails savedata = new ProductEmployeeDetails();
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			try {
				EndtTypeMaster entMaster = ratingutil.getEndtMasterData(req.getInsuranceId(),req.getProductId(),req.getEndtTypeId());/* endtTypeRepo
						.findByCompanyIdAndProductIdAndStatusAndEndtTypeIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqual(
								req.getInsuranceId(), Integer.parseInt(req.getProductId()), "Y",
								Integer.parseInt(req.getEndtTypeId()), new Date(), new Date());*/

				List<ProductEmployeeDetails> PA = pARepo.findByQuoteNo(prevQuoteNo);
				if (PA != null && PA.size() > 0) {
					for (ProductEmployeeDetails data : PA) {
						savedata = dozerMapper.map(data, ProductEmployeeDetails.class);
						savedata.setEntryDate(new Date());
						savedata.setRequestReferenceNo(refNo);
						savedata.setQuoteNo(quoteNo);
						savedata.setCreatedBy(loginId);
					//	savedata.setUpdatedBy(loginId);
				//		savedata.setUpdatedDate(new Date());
						savedata.setOriginalPolicyNo(req.getPolicyNo());
						savedata.setEndorsementDate(new Date());
						savedata.setEndorsementRemarks(req.getEndtRemarks());
						savedata.setEndorsementEffdate(req.getEndtEffectiveDate());
						savedata.setEndtPrevPolicyNo(prevPolicyNo);
						savedata.setEndtPrevQuoteNo(prevQuoteNo);
						savedata.setEndtCount(new BigDecimal(count));
						savedata.setEndtStatus("P");
						savedata.setIsFinaceYn(entMaster.getEndtTypeCategoryId() == 2 ? "Y" : "N");
						savedata.setEndtCategDesc(entMaster.getEndtTypeCategory());
						savedata.setEndorsementType(Integer.parseInt(req.getEndtTypeId()));
						savedata.setEndorsementTypeDesc(entMaster.getEndtTypeDesc());
						savedata.setStatus("E");
						savedata.setPolicyNo(req.getPolicyNo() + "-" + count);
						pARepo.saveAndFlush(savedata);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return res;

		}

		//Eservice Building Details
		private CopyQuoteSuccessRes esrviceBuildingDetailsEndoCopyquote(CopyQuoteReq req, String refNo, String quoteNo, String customerId,
				String loginId, String prevPolicyNo, String prevQuoteNo, Integer count, String custRefNo) {
			CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
			EserviceBuildingDetails savedata = new EserviceBuildingDetails();
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			try {
				EndtTypeMaster entMaster = ratingutil.getEndtMasterData(req.getInsuranceId(),req.getProductId(),req.getEndtTypeId()); /*endtTypeRepo
						.findByCompanyIdAndProductIdAndStatusAndEndtTypeIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqual(
								req.getInsuranceId(), Integer.parseInt(req.getProductId()), "Y",
								Integer.parseInt(req.getEndtTypeId()), new Date(), new Date());*/

				List<EserviceBuildingDetails> eserSec = eseBuildingRepo.findByQuoteNoOrderByRiskIdAsc(prevQuoteNo);
				if (eserSec != null && eserSec.size()>0 ) {
					for (EserviceBuildingDetails data : eserSec) {
						savedata = dozerMapper.map(data, EserviceBuildingDetails.class);
						savedata.setEntryDate(new Date());
						savedata.setCustomerReferenceNo(custRefNo);
						savedata.setRequestReferenceNo(refNo);
						savedata.setCustomerId(customerId);
						savedata.setQuoteNo(quoteNo);
						savedata.setCreatedBy(loginId);
						savedata.setUpdatedBy(loginId);
						savedata.setUpdatedDate(new Date());
						savedata.setOriginalPolicyNo(req.getPolicyNo());
						savedata.setEndorsementDate(new Date());
						savedata.setEndorsementRemarks(req.getEndtRemarks());
						savedata.setEndorsementEffdate(req.getEndtEffectiveDate());
						savedata.setEndtPrevPolicyNo(prevPolicyNo);
						savedata.setEndtPrevQuoteNo(prevQuoteNo);
						savedata.setEndtCount(new BigDecimal(count));
						savedata.setEndtStatus("P");
						savedata.setIsFinyn(entMaster.getEndtTypeCategoryId() == 2 ? "Y" : "N");
						savedata.setEndtCategDesc(entMaster.getEndtTypeCategory());
						savedata.setEndorsementType(Integer.parseInt(req.getEndtTypeId()));
						savedata.setEndorsementTypeDesc(entMaster.getEndtTypeDesc());
						savedata.setStatus("E");
						savedata.setPolicyNo(req.getPolicyNo() + "-" + count);
						savedata.setApplicationId(req.getApplicationId());
						if(req.getLoginId()==null || StringUtils.isBlank(req.getLoginId())) {
							savedata.setLoginId(data.getLoginId());
						}else {
							savedata.setLoginId(req.getLoginId());
						}
						savedata.setSubUserType(req.getSubUserType());
						eseBuildingRepo.saveAndFlush(savedata);
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return res;

		}
		
		//Building Details
		private CopyQuoteSuccessRes buildingDetailsEndoCopyquote(CopyQuoteReq req, String refNo, String quoteNo, String customerId,String loginId, String prevPolicyNo, String prevQuoteNo, Integer count, String custRefNo) {
			CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
			BuildingDetails savedata = new BuildingDetails();
			DozerBeanMapper dozerMapper = new DozerBeanMapper();
			try {
				EndtTypeMaster entMaster = ratingutil.getEndtMasterData(req.getInsuranceId(),req.getProductId(),req.getEndtTypeId());  /*endtTypeRepo.findByCompanyIdAndProductIdAndStatusAndEndtTypeIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqual(
						req.getInsuranceId(), Integer.parseInt(req.getProductId()), "Y",
						Integer.parseInt(req.getEndtTypeId()), new Date(), new Date());*/
				List<BuildingDetails> buildingData=buildingRepo.findByQuoteNoOrderByRiskIdAsc(prevQuoteNo);
				if (buildingData!=null) {
					for(BuildingDetails data :buildingData) {
						savedata = dozerMapper.map(data, BuildingDetails.class);
						savedata.setEntryDate(new Date());
						savedata.setRequestReferenceNo(refNo);
						savedata.setCustomerId(customerId);
						savedata.setQuoteNo(quoteNo);
						savedata.setCreatedBy(loginId);
						savedata.setUpdatedBy(loginId);
						savedata.setUpdatedDate(new Date());
						savedata.setOriginalPolicyNo(req.getPolicyNo());
						savedata.setEndorsementDate(new Date());
						savedata.setEndorsementRemarks(req.getEndtRemarks());
						savedata.setEndorsementEffdate(req.getEndtEffectiveDate());
						savedata.setEndtPrevPolicyNo(prevPolicyNo);
						savedata.setEndtPrevQuoteNo(prevQuoteNo);
						savedata.setEndtCount(new BigDecimal(count));
						savedata.setEndtStatus("P");
						savedata.setIsFinaceYn(entMaster.getEndtTypeCategoryId() == 2 ? "Y" : "N");
						savedata.setEndtCategDesc(entMaster.getEndtTypeCategory());
						savedata.setEndorsementType(Integer.parseInt(req.getEndtTypeId()));
						savedata.setEndorsementTypeDesc(entMaster.getEndtTypeDesc());
						savedata.setStatus("E");
						savedata.setPolicyNo(req.getPolicyNo() + "-" + count);
						buildingRepo.saveAndFlush(savedata);
					}
				}
			
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
			return res;
			
		}
		
		@Override
		public synchronized GetBuildingAdminReferalPendingDetailsRes getCommonAdminReferalPendingDetails(RevertGridReq req,  int limit,
				int offset ,String status) {
			List<ReferalGridCriteriaAdminRes> referrals = new ArrayList<ReferalGridCriteriaAdminRes>();
			GetBuildingAdminReferalPendingDetailsRes resp = new GetBuildingAdminReferalPendingDetailsRes();
			try {
				resp.setCount(0l);
				
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<ReferalGridCriteriaAdminRes> query = cb.createQuery(ReferalGridCriteriaAdminRes.class);

				// Find All
				Root<EserviceCommonDetails> m = query.from(EserviceCommonDetails.class);
				Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);

				// Select
				query.multiselect(
						c.get("customerReferenceNo").alias("customerReferenceNo"), c.get("idNumber").alias("idNumber"),
						c.get("clientName").alias("clientName"),
						// Vehicle Info
						m.get("companyId").alias("companyId"), m.get("productId").alias("productId"),
						m.get("branchCode").alias("branchCode"), m.get("requestReferenceNo").alias("requestReferenceNo"),
						m.get("quoteNo").alias("quoteNo"),
						m.get("customerId").alias("customerId"),
						m.get("policyStartDate").alias("policyStartDate"), m.get("policyEndDate").alias("policyEndDate"),
						m.get("rejectReason").alias("rejectReason"),
						m.get("adminRemarks").alias("adminRemarks"),
						m.get("status").alias("status"),
						m.get("entryDate").alias("entryDate"),
						m.get("referalRemarks").alias("referalRemarks"),
						m.get("endorsementType").alias("endorsementType"),
						m.get("endorsementTypeDesc").alias("endorsementTypeDesc"),
						m.get("endorsementDate").alias("endorsementDate"),
						m.get("endorsementRemarks").alias("endorsementRemarks"),
						m.get("endorsementEffdate").alias("endorsementEffdate"),
						m.get("originalPolicyNo").alias("originalPolicyNo"),
						m.get("endtPrevPolicyNo").alias("endtPrevPolicyNo"),
						m.get("endtPrevQuoteNo").alias("endtPrevQuoteNo"),
						m.get("endtCount").alias("endtCount"),
						m.get("endtStatus").alias("endtStatus"),
						m.get("endtCategDesc").alias("endtCategDesc"),
						m.get("endtPremium").alias("endtPremium")
						);

				//Riskid
				Subquery<Long> riskId = query.subquery(Long.class);
				Root<EserviceCommonDetails> ocpm2 = riskId.from(EserviceCommonDetails.class);
				riskId.select(cb.max(ocpm2.get("riskId")));
				Predicate a3 = cb.equal(m.get("requestReferenceNo"), ocpm2.get("requestReferenceNo"));
				riskId.where(a3);
				
				// Order By
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(m.get("updatedDate")));

				// Where
				Predicate n1 = cb.equal(c.get("customerReferenceNo"), m.get("customerReferenceNo"));
				Predicate n2 = cb.equal(m.get("companyId"), req.getInsuranceId());
				Predicate n3 = cb.equal(m.get("productId"), req.getProductId());
				Predicate n4 = cb.equal(m.get("status"),status);
				Predicate n6 =cb.equal(m.get("riskId"), riskId);
				
				Predicate n14 = null;
				if(req.getType().equalsIgnoreCase("Q"))
						n14 = cb.isNull(m.get("endorsementTypeDesc")); 
				else if (req.getType().equalsIgnoreCase("E"))
						n14 = cb.isNotNull(m.get("endorsementTypeDesc")); 
			
					
				query.where(n1, n2, n3, n4, n6,n14).orderBy(orderList);
					
				// Get Result
				TypedQuery<ReferalGridCriteriaAdminRes> result = em.createQuery(query);
				result.setFirstResult(limit * offset);
				result.setMaxResults(offset);
				referrals = result.getResultList();
				if (referrals != null && referrals.size() > 0) {
					referrals = referrals.stream().filter(distinctByKey(o -> Arrays.asList(o.getRequestReferenceNo()))).collect(Collectors.toList());
				}else {
					referrals=null;
				}
				resp.setReferalGridCriteriaAdminRes(referrals);
				resp.setCount(totalcountadminreferral(req,  status));
			
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Log Details" + e.getMessage());
				return null;
			}
			return resp;
		}

		private Long totalcountadminreferral(RevertGridReq req, String status) {

			Long count = 0l;
			try {	
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Long> query = cb.createQuery(Long.class);

				Root<EserviceCommonDetails> m = query.from(EserviceCommonDetails.class);
				Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
				
//				query.multiselect(cb.count(m));
				query.select(cb.countDistinct(m.get("requestReferenceNo"))); 

				//Riskid
				Subquery<Long> riskId = query.subquery(Long.class);
				Root<EserviceCommonDetails> ocpm2 = riskId.from(EserviceCommonDetails.class);
				riskId.select(cb.max(ocpm2.get("riskId")));
				Predicate a3 = cb.equal(m.get("requestReferenceNo"), ocpm2.get("requestReferenceNo"));
				riskId.where(a3);
				
				Predicate n1 = cb.equal(c.get("customerReferenceNo"), m.get("customerReferenceNo"));
				Predicate n2 = cb.equal(m.get("companyId"), req.getInsuranceId());
				Predicate n3 = cb.equal(m.get("productId"), req.getProductId());
				Predicate n4 = cb.equal(m.get("status"),status);
				Predicate n6 =cb.equal(m.get("riskId"), riskId);
				
				Predicate n14 = null;
				if(req.getType().equalsIgnoreCase("Q"))
						n14 = cb.isNull(m.get("endorsementTypeDesc")); 
				else if (req.getType().equalsIgnoreCase("E"))
						n14 = cb.isNotNull(m.get("endorsementTypeDesc")); 
			
				query.where(n1, n2, n3, n4, n6,n14);
				
				TypedQuery<Long> result = em.createQuery(query);
				List<Long> val = result.getResultList();
					
						if(val.size()>0)
							count = val.get(0);
				
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Log Details" + e.getMessage());
			}
			return count;
		}

		public synchronized List<ReferalGridCriteriaAdminRes> getCommonAdminReferalPendingDetailsCount(RevertGridReq req,String status) {
			List<ReferalGridCriteriaAdminRes> referrals = new ArrayList<ReferalGridCriteriaAdminRes>();
			try {
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<ReferalGridCriteriaAdminRes> query = cb.createQuery(ReferalGridCriteriaAdminRes.class);

				// Find All
				Root<EserviceCommonDetails> m = query.from(EserviceCommonDetails.class);
				Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);

				// Select
				query.multiselect(cb.count(m).as(Long.class).alias("idsCount"),
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
						m.get("rejectReason").alias("rejectReason"),
						m.get("adminRemarks").alias("adminRemarks"),
						cb.max(m.get("status")).alias("status"),
						cb.max(m.get("entryDate")).alias("entryDate"),
						m.get("referalRemarks").alias("referalRemarks"),
						cb.max(m.get("endorsementType")).alias("endorsementType"),
						cb.max(m.get("endorsementTypeDesc")).alias("endorsementTypeDesc"),
						cb.max(m.get("endorsementDate")).alias("endorsementDate"),
						cb.max(m.get("endorsementRemarks")).alias("endorsementRemarks"),
						cb.max(m.get("endorsementEffdate")).alias("endorsementEffdate"),
						cb.max(m.get("originalPolicyNo")).alias("originalPolicyNo"),
						cb.max(m.get("endtPrevPolicyNo")).alias("endtPrevPolicyNo"),
						cb.max(m.get("endtPrevQuoteNo")).alias("endtPrevQuoteNo"),
						cb.max(m.get("endtCount")).alias("endtCount"),
						cb.max(m.get("endtStatus")).alias("endtStatus"),
						cb.max(m.get("endtCategDesc")).alias("endtCategDesc"),
						cb.max(m.get("endtPremium")).alias("endtPremium")
						);

				// Order By
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(m.get("updatedDate")));

				// Where
				Predicate n1 = cb.equal(c.get("customerReferenceNo"), m.get("customerReferenceNo"));
				Predicate n2 = cb.equal(m.get("companyId"), req.getInsuranceId());
				Predicate n3 = cb.equal(m.get("productId"), req.getProductId());
				Predicate n4 = cb.equal(m.get("status"),status);
				
				String branchCode ="";
				if (StringUtils.isNotBlank(req.getBranchCode()) && !"99999".equals(req.getBranchCode())) {
					branchCode = req.getBranchCode();
					Predicate n6 =cb.equal(m.get("branchCode"),branchCode);
					query.where(n1, n2, n3, n4, n6)
					.groupBy(c.get("customerReferenceNo"), c.get("idNumber"), c.get("clientName"), m.get("companyId"),
							m.get("productId"), m.get("branchCode"), m.get("requestReferenceNo"), m.get("quoteNo"),
							m.get("customerId"), m.get("policyStartDate"), m.get("policyEndDate"),
							m.get("rejectReason"),m.get("adminRemarks"),m.get("referalRemarks"),m.get("updatedDate")
							)
					.orderBy(orderList);
				}else {
					
					query.where(n1, n2, n3, n4)
					.groupBy(c.get("customerReferenceNo"), c.get("idNumber"), c.get("clientName"), m.get("companyId"),
							m.get("productId"), m.get("branchCode"), m.get("requestReferenceNo"), m.get("quoteNo"),
							m.get("customerId"), m.get("policyStartDate"), m.get("policyEndDate"),
							m.get("rejectReason"),m.get("adminRemarks"),m.get("referalRemarks"),m.get("updatedDate"))
					.orderBy(orderList);
				}
				
						

				// Get Result
				TypedQuery<ReferalGridCriteriaAdminRes> result = em.createQuery(query);
				referrals = result.getResultList();
				if (referrals != null && referrals.size() > 0) {
					referrals = referrals.stream().filter(distinctByKey(o -> Arrays.asList(o.getRequestReferenceNo()))).collect(Collectors.toList());
				}else {
					referrals=null;
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Log Details" + e.getMessage());
				return null;
			}
			return referrals;
		}


		@Override
		public List<GetExistingBrokerListRes> getCommonProtfolioDropdownPending(ExistingBrokerUserListReq req,
				Date today) {
			List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
			List<Tuple> list = new ArrayList<Tuple>();
			try {

				if (!("issuer".equalsIgnoreCase(req.getUserType()))) {
					CriteriaBuilder cb = em.getCriteriaBuilder();
					CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

					Root<EserviceCommonDetails> m = query.from(EserviceCommonDetails.class);
					Root<LoginUserInfo> us = query.from(LoginUserInfo.class);
					query.multiselect( m.get("loginId").alias("code"),us.get("userName").alias("codeDesc"),
							m.get("sourceType").alias("type"));

					// Find All
					Subquery<Long> agencyCode = query.subquery(Long.class);
					Root<LoginMaster> ocpm1 = agencyCode.from(LoginMaster.class);
					agencyCode.select(ocpm1.get("agencyCode"));
					Predicate a1 = cb.equal(ocpm1.get("loginId"), req.getLoginId());
					agencyCode.where(a1);

//					Predicate n1 = cb.equal(m.get("applicationId"), req.getApplicationId());
					Predicate n2 = cb.isNotNull(m.get("applicationId"));
					Predicate n3 = cb.equal(m.get("companyId"), req.getCompanyId());
					Predicate n4 = cb.equal(m.get("productId"), req.getProductId());
					Predicate n5 = cb.equal(m.get("endtStatus"), "P");
					Predicate n12 = null;
					if ("Broker".equalsIgnoreCase(req.getUserType())) {
						n12 = cb.equal(m.get("brokerCode").as(Long.class), agencyCode);
					} else if ("User".equalsIgnoreCase(req.getUserType())) {
						n12 = cb.equal(m.get("agencyCode").as(Long.class), agencyCode);
					}
					Predicate n13 = cb.isNotNull(m.get("sourceType"));
					Predicate n14 = cb.isNotNull(m.get("loginId"));
					Predicate us1 = cb.equal(us.get("loginId"), m.get("loginId"));
					query.where( n2,n3, n4, n5, n12, n13, n14,us1);

					TypedQuery<Tuple> typedQuery1 = em.createQuery(query);
					list = typedQuery1.getResultList();
					list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.get("code"))))
							.collect(Collectors.toList());
					if (list != null && list.size() > 0) {

						for (Tuple data : list) {
							GetExistingBrokerListRes res = new GetExistingBrokerListRes();

							res.setCode(data.get("code") == null ? "" : data.get("code").toString());
							res.setCodeDesc(data.get("codeDesc") == null ? "" : data.get("codeDesc").toString());
							res.setType(data.get("type") == null ? "" : data.get("type").toString().toLowerCase().replaceAll("premia ", ""));
							resList.add(res);

						}
					}
				} else {
					resList = getPortfolioPendingIssuerCommon(req, today);
				}

			} catch (Exception e) {
				e.printStackTrace();
				log.info("Log Details" + e.getMessage());
				return null;
			}
			return resList;

		}

		private List<GetExistingBrokerListRes> getPortfolioPendingIssuerCommon(ExistingBrokerUserListReq req,
				Date today) {
			List<Tuple> list = new ArrayList<Tuple>();
			List<Tuple> list1 = new ArrayList<Tuple>();
			List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
			try {
				{
					CriteriaBuilder cb = em.getCriteriaBuilder();
					CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

					Root<EserviceCommonDetails> m = query.from(EserviceCommonDetails.class);

					query.multiselect(m.get("bdmCode").alias("code"), m.get("customerName").alias("codeDesc"),
							m.get("sourceType").alias("type"));
					Predicate n1 = cb.equal(m.get("applicationId"), req.getApplicationId());
					Predicate n2 = cb.isNotNull(m.get("applicationId"));
					Predicate n3 = cb.equal(m.get("companyId"), req.getCompanyId());
					Predicate n4 = cb.equal(m.get("productId"), req.getProductId());
					Predicate n5 = cb.equal(m.get("endtStatus"), "P");
					Predicate n8 = cb.isNotNull(m.get("bdmCode"));
					query.where(n1, n2, n3, n4, n5, n8);

					TypedQuery<Tuple> typedQuery = em.createQuery(query);
					list = typedQuery.getResultList();
					list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.get("code"))))
							.collect(Collectors.toList());
					if (list != null && list.size() > 0) {

						for (Tuple data : list) {
							GetExistingBrokerListRes res = new GetExistingBrokerListRes();
							res.setCode(data.get("code") == null ? "" : data.get("code").toString());
							res.setCodeDesc(data.get("codeDesc") == null ? "" : data.get("codeDesc").toString());
							res.setType(data.get("type") == null ? "" : data.get("type").toString());
							String type=data.get("type") == null ? "" : data.get("type").toString();
							type="Premia "+type;
							res.setType(type);
							resList.add(res);

						}
					}
				}
				{
					CriteriaBuilder cb1 = em.getCriteriaBuilder();
					CriteriaQuery<Tuple> query1 = cb1.createQuery(Tuple.class);

					Root<EserviceCommonDetails> m1 = query1.from(EserviceCommonDetails.class);
					Root<LoginUserInfo> us = query1.from(LoginUserInfo.class);
					query1.multiselect( m1.get("loginId").alias("code"),us.get("userName").alias("codeDesc"),
							m1.get("sourceType").alias("type"));
					Predicate n1 = cb1.equal(m1.get("applicationId"), req.getApplicationId());
					Predicate n2 = cb1.isNotNull(m1.get("applicationId"));
					Predicate n3 = cb1.equal(m1.get("companyId"), req.getCompanyId());
					Predicate n4 = cb1.equal(m1.get("productId"), req.getProductId());
					Predicate n5 = cb1.equal(m1.get("endtStatus"), "P");
					Predicate n6 = cb1.isNull(m1.get("bdmCode"));
					Predicate us1 = cb1.equal(us.get("loginId"), m1.get("loginId"));
					query1.where(n1, n2, n3, n4, n5, n6,us1);

					TypedQuery<Tuple> typedQuery1 = em.createQuery(query1);
					list1 = typedQuery1.getResultList();
					list1 = list1.stream().filter(distinctByKey(o -> Arrays.asList(o.get("code"))))
							.collect(Collectors.toList());
					if (list1 != null && list1.size() > 0) {

						for (Tuple data : list1) {
							GetExistingBrokerListRes res = new GetExistingBrokerListRes();
							res.setCode(data.get("code") == null ? "" : data.get("code").toString());
							res.setCodeDesc(data.get("codeDesc") == null ? "" : data.get("codeDesc").toString());
							res.setType(data.get("type") == null ? "" : data.get("type").toString());
							resList.add(res);

						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Log Details" + e.getMessage());
				return null;
			}
			return resList;
		}
		@Override
		public List<GetExistingBrokerListRes> getBrokerUserListLapsedCommon(ExistingBrokerUserListReq req, Date today,
				Date before30) {
			List<Tuple> list = new ArrayList<Tuple>();
			List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
			try {
				if(!("issuer".equalsIgnoreCase(req.getUserType()))){		
					
					CriteriaBuilder cb = em.getCriteriaBuilder();
					CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);
	
					Root<EserviceCommonDetails> m = query.from(EserviceCommonDetails.class);
					Root<LoginUserInfo> us = query.from(LoginUserInfo.class);
					query.multiselect( m.get("loginId").alias("code"),us.get("userName").alias("codeDesc"),
							m.get("sourceType").alias("type")).distinct(true);
	
					// Find All
					Subquery<Long> agencyCode = query.subquery(Long.class);
					Root<LoginMaster> ocpm1 = agencyCode.from(LoginMaster.class);
					agencyCode.select(ocpm1.get("agencyCode"));
					Predicate a1 = cb.equal(ocpm1.get("loginId"), req.getLoginId());
				//	Predicate a3 = cb.equal(ocpm1.get("status"), "Y");
					agencyCode.where(a1);
	
					List<Predicate> predics1 = new ArrayList<Predicate>();
					predics1.add(cb.equal(m.get("applicationId"), req.getApplicationId()));
					predics1.add(cb.equal(m.get("status"), "Y"));
					predics1.add(cb.equal(m.get("productId"), req.getProductId()));
					predics1.add(cb.equal(m.get("companyId"), req.getCompanyId()));
					predics1.add(cb.equal(m.get("branchCode"), req.getBranchCode()));
					predics1.add(cb.lessThanOrEqualTo(m.get("updatedDate"), before30));
			//		predics1.add(cb.lessThanOrEqualTo(m.get("updatedDate"), today));
					if ("Broker".equalsIgnoreCase(req.getUserType())) {
						predics1.add(cb.equal(m.get("brokerCode").as(Long.class), agencyCode));
					} else if ("User".equalsIgnoreCase(req.getUserType())) {
						predics1.add(cb.equal(m.get("agencyCode").as(Long.class), agencyCode));
					}
					predics1.add(cb.isNotNull(m.get("sourceType")));
					predics1.add(cb.isNotNull(m.get("loginId")));
					predics1.add(cb.equal(us.get("loginId"), m.get("loginId")));
					query.where(predics1.toArray(new Predicate[0]));
	
					TypedQuery<Tuple> typedQuery1 = em.createQuery(query);
					list = typedQuery1.getResultList();
					list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.get("code")))).collect(Collectors.toList());
				}else {
					resList = getBrokerListLapsedIssuer(req, today,  before30) ; //Issuer
					
					
				}
				
				if (list != null && list.size() > 0) {

					for (Tuple data : list) {
						GetExistingBrokerListRes res = new GetExistingBrokerListRes();

						res.setCode(data.get("code") == null ? "" : data.get("code").toString());
						res.setCodeDesc(data.get("codeDesc") == null ? "" : data.get("codeDesc").toString());
						res.setType(data.get("type") == null ? "" : data.get("type").toString());
						resList.add(res);

					}
				}
				
			}catch (Exception e) {
				e.printStackTrace();
				log.info("Log Details" + e.getMessage());
				return null;
			}
			return resList;
				
		}
		private List<GetExistingBrokerListRes> getBrokerListLapsedIssuer(ExistingBrokerUserListReq req, Date today,
				Date before30) {
			List<Tuple> list = new ArrayList<Tuple>();
			List<Tuple> list1 = new ArrayList<Tuple>();
			List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
			try {
				{ CriteriaBuilder cb = em.getCriteriaBuilder();
				 CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);
				 
				 Root<EserviceCommonDetails> m = query.from(EserviceCommonDetails.class); 
				 
				 query.multiselect(
						 m.get("bdmCode").alias("code"),
						 m.get("customerName").alias("codeDesc"),
						 m.get("sourceType").alias("type")
						 ).distinct(true) ;
				 
				 List<Predicate> predics = new ArrayList<Predicate>();
				 predics.add(cb.equal(m.get("applicationId"), req.getApplicationId()));
				 predics.add(cb.equal(m.get("status"), "Y"));
				 predics.add(cb.equal(m.get("productId"), req.getProductId()));
				 predics.add(cb.equal(m.get("companyId"), req.getCompanyId()));
				 predics.add(cb.isNotNull(m.get("bdmCode")));
				 
				predics.add(cb.equal(m.get("branchCode"), req.getBranchCode()));
				predics.add(cb.lessThanOrEqualTo(m.get("updatedDate"), before30)); //lapsed
				predics.add(cb.isNotNull(m.get("sourceType")));
				 
				 query.where(predics.toArray(new Predicate[0]));
				 
				 TypedQuery<Tuple> typedQuery = em.createQuery(query);
				 list=  typedQuery.getResultList();
				 list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.get("code")))).collect(Collectors.toList());
				 if(list!=null && list.size()>0) {
					 
					 for(Tuple data : list) {
						 GetExistingBrokerListRes res = new GetExistingBrokerListRes();
						 res.setCode(data.get("code")==null?"":	data.get("code").toString());
						 res.setCodeDesc(data.get("codeDesc")==null?"":	data.get("codeDesc").toString());
						 res.setType(data.get("type")==null?"":	data.get("type").toString());
						 resList.add(res);
					
					 }
				 }	
				}
				{
				 CriteriaBuilder cb1 = em.getCriteriaBuilder();
				 CriteriaQuery<Tuple> query1 = cb1.createQuery(Tuple.class);
				 
				 Root<EserviceCommonDetails> m1 = query1.from(EserviceCommonDetails.class); 
				 Root<LoginUserInfo> us = query1.from(LoginUserInfo.class);
					query1.multiselect( m1.get("loginId").alias("code"),us.get("userName").alias("codeDesc"),
							m1.get("sourceType").alias("type")).distinct(true) ;
				 
				 List<Predicate> predics1 = new ArrayList<Predicate>();
				 predics1.add(cb1.equal(m1.get("applicationId"),req.getApplicationId()));
				 predics1.add(cb1.equal(m1.get("status"), "Y"));
				 predics1.add(cb1.equal(m1.get("productId"), req.getProductId()));
				 predics1.add(cb1.equal(m1.get("companyId"), req.getCompanyId()));
				 predics1.add(cb1.isNull(m1.get("bdmCode")));
				 
				predics1.add(cb1.equal(m1.get("branchCode"), req.getBranchCode()));
				predics1.add(cb1.lessThanOrEqualTo(m1.get("updatedDate"), before30));
				predics1.add(cb1.isNotNull(m1.get("sourceType")));
				predics1.add(cb1.isNotNull(m1.get("loginId")));
				predics1.add(cb1.equal(us.get("loginId"), m1.get("loginId")));
				 query1.where(predics1.toArray(new Predicate[0]));
				 
				 TypedQuery<Tuple> typedQuery1 = em.createQuery(query1);
				 list1=  typedQuery1.getResultList();
				 list1 = list1.stream().filter(distinctByKey(o -> Arrays.asList(o.get("code")))).collect(Collectors.toList());
				 if(list1!=null && list1.size()>0) {
					 
					 for(Tuple data : list1) {
						 GetExistingBrokerListRes res = new GetExistingBrokerListRes();
						 res.setCode(data.get("code")==null?"":	data.get("code").toString());
						 res.setCodeDesc(data.get("codeDesc")==null?"":	data.get("codeDesc").toString());
						 res.setType(data.get("type")==null?"":	data.get("type").toString());
						 resList.add(res);
					
					 }
				 }	
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Log Details" + e.getMessage());
				return null;
			}
			return resList ;
		}

		@Override
		public List<GetExistingBrokerListRes> getBrokerUserListCommonRejected(ExistingBrokerUserListReq req, Date today,
				Date before30) {
			List<Tuple> list = new ArrayList<Tuple>();	
			List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
			try {
				if(!("issuer".equalsIgnoreCase(req.getUserType()))){		
					CriteriaBuilder cb = em.getCriteriaBuilder();
					CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

					Root<EserviceCommonDetails> m = query.from(EserviceCommonDetails.class);
					Root<LoginUserInfo> us = query.from(LoginUserInfo.class);
					query.multiselect( m.get("loginId").alias("code"),us.get("userName").alias("codeDesc"),
							m.get("sourceType").alias("type")).distinct(true);

					// Find All
					Subquery<Long> agencyCode = query.subquery(Long.class);
					Root<LoginMaster> ocpm1 = agencyCode.from(LoginMaster.class);
					agencyCode.select(ocpm1.get("agencyCode"));
					Predicate a1 = cb.equal(ocpm1.get("loginId"), req.getLoginId());
				//	Predicate a3 = cb.equal(ocpm1.get("status"), "Y");
					agencyCode.where(a1);

					List<Predicate> predics1 = new ArrayList<Predicate>();
					predics1.add(cb.equal(m.get("applicationId"), req.getApplicationId()));
					predics1.add(cb.equal(m.get("status"), "R"));
					predics1.add(cb.equal(m.get("productId"), req.getProductId()));
					predics1.add(cb.equal(m.get("companyId"), req.getCompanyId()));
					predics1.add(cb.equal(m.get("branchCode"), req.getBranchCode()));
					predics1.add(cb.greaterThanOrEqualTo(m.get("updatedDate"), before30));
					predics1.add(cb.lessThanOrEqualTo(m.get("updatedDate"), today));
					if ("Broker".equalsIgnoreCase(req.getUserType())) {
						predics1.add(cb.equal(m.get("brokerCode").as(Long.class), agencyCode));
					} else if ("User".equalsIgnoreCase(req.getUserType())) {
						predics1.add(cb.equal(m.get("agencyCode").as(Long.class), agencyCode));
					}
					predics1.add(cb.isNotNull(m.get("sourceType")));
					predics1.add(cb.isNotNull(m.get("loginId")));
					predics1.add(cb.equal(us.get("loginId"), m.get("loginId")));
					query.where(predics1.toArray(new Predicate[0]));

					TypedQuery<Tuple> typedQuery1 = em.createQuery(query);
					list = typedQuery1.getResultList();
					list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.get("code")))).collect(Collectors.toList());
					}else {
						
						resList = getBrokerListRejectedIssuer(req, today,  before30) ; //Issuer
						
					}
						if (list != null && list.size() > 0) {

							for (Tuple data : list) {
								GetExistingBrokerListRes res = new GetExistingBrokerListRes();

								res.setCode(data.get("code") == null ? "" : data.get("code").toString());
								res.setCodeDesc(data.get("codeDesc") == null ? "" : data.get("codeDesc").toString());
								res.setType(data.get("type") == null ? "" : data.get("type").toString());
								resList.add(res);

							}
						}
					
					
			}catch (Exception e) {
				e.printStackTrace();
				log.info("Log Details" + e.getMessage());
				return null;
			}
			return resList;
			
		
		}

		private List<GetExistingBrokerListRes> getBrokerListRejectedIssuer(ExistingBrokerUserListReq req, Date today,
				Date before30) {
			List<Tuple> list = new ArrayList<Tuple>();
			List<Tuple> list1 = new ArrayList<Tuple>();
			List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
			try {
				 CriteriaBuilder cb = em.getCriteriaBuilder();
				 CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);
				 
				 Root<EserviceCommonDetails> m = query.from(EserviceCommonDetails.class); 
				 
				 query.multiselect(
						 m.get("bdmCode").alias("code"),
						 m.get("customerName").alias("codeDesc"),
						 m.get("sourceType").alias("type")
						 ).distinct(true) ;
				 
				 List<Predicate> predics = new ArrayList<Predicate>();
				 predics.add(cb.equal(m.get("applicationId"), req.getApplicationId()));
				 predics.add(cb.equal(m.get("status"), "R"));
				 predics.add(cb.equal(m.get("productId"), req.getProductId()));
				 predics.add(cb.equal(m.get("companyId"), req.getCompanyId()));
				 predics.add(cb.isNotNull(m.get("bdmCode")));
				 
				predics.add(cb.equal(m.get("branchCode"), req.getBranchCode()));
				predics.add(cb.greaterThanOrEqualTo(m.get("updatedDate"), before30));
				predics.add(cb.lessThanOrEqualTo(m.get("updatedDate"), today));
				predics.add(cb.isNotNull(m.get("sourceType")));
				 
				 query.where(predics.toArray(new Predicate[0]));
				 
				 TypedQuery<Tuple> typedQuery = em.createQuery(query);
				 list=  typedQuery.getResultList();
				 list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.get("code")))).collect(Collectors.toList());
				 if(list!=null && list.size()>0) {
					 
					 for(Tuple data : list) {
						 GetExistingBrokerListRes res = new GetExistingBrokerListRes();
						 res.setCode(data.get("code")==null?"":	data.get("code").toString());
						 res.setCodeDesc(data.get("codeDesc")==null?"":	data.get("codeDesc").toString());
						 res.setType(data.get("type")==null?"":	data.get("type").toString());
						 resList.add(res);
					
					 }
				 }	
				 
				 CriteriaBuilder cb1 = em.getCriteriaBuilder();
				 CriteriaQuery<Tuple> query1 = cb1.createQuery(Tuple.class);
				 
				 Root<EserviceCommonDetails> m1 = query1.from(EserviceCommonDetails.class); 
				 
					Root<LoginUserInfo> us = query1.from(LoginUserInfo.class);
					query1.multiselect( m1.get("loginId").alias("code"),us.get("userName").alias("codeDesc"),
							m1.get("sourceType").alias("type")).distinct(true) ;
				 
				 List<Predicate> predics1 = new ArrayList<Predicate>();
				 predics1.add(cb1.equal(m1.get("applicationId"),req.getApplicationId()));
				 predics1.add(cb1.equal(m1.get("status"), "R"));
				 predics1.add(cb1.equal(m1.get("productId"), req.getProductId()));
				 predics1.add(cb1.equal(m1.get("companyId"), req.getCompanyId()));
				 predics1.add(cb1.isNull(m1.get("bdmCode")));
				 
				predics1.add(cb1.equal(m1.get("branchCode"), req.getBranchCode()));
				predics1.add(cb1.greaterThanOrEqualTo(m1.get("updatedDate"), before30));
				predics1.add(cb1.lessThanOrEqualTo(m1.get("updatedDate"), today));
				predics1.add(cb1.isNotNull(m1.get("sourceType")));
				predics1.add(cb1.isNotNull(m1.get("loginId")));
				predics1.add(cb1.equal(us.get("loginId"), m1.get("loginId")));
				 query1.where(predics1.toArray(new Predicate[0]));
				 
				 TypedQuery<Tuple> typedQuery1 = em.createQuery(query1);
				 list1=  typedQuery1.getResultList();
				 list1 = list1.stream().filter(distinctByKey(o -> Arrays.asList(o.get("code")))).collect(Collectors.toList());
				 if(list1!=null && list1.size()>0) {
					 
					 for(Tuple data : list1) {
						 GetExistingBrokerListRes res = new GetExistingBrokerListRes();
						 res.setCode(data.get("code")==null?"":	data.get("code").toString());
						 res.setCodeDesc(data.get("codeDesc")==null?"":	data.get("codeDesc").toString());
						 res.setType(data.get("type")==null?"":	data.get("type").toString());
						 resList.add(res);
					
					 }
				 }	
				
				
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Log Details" + e.getMessage());
				return null;
			}
			return resList ;
		}
		@Override
		public List<GetExistingBrokerListRes> getCommonReferalDropdown(ExistingBrokerUserListReq req,
				Date today,String status) {
			List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
			List<Tuple> list = new ArrayList<Tuple>();
			try {

				if (!("issuer".equalsIgnoreCase(req.getUserType()))) {
					CriteriaBuilder cb = em.getCriteriaBuilder();
					CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

					Root<EserviceCommonDetails> m = query.from(EserviceCommonDetails.class);
					Root<LoginUserInfo> us = query.from(LoginUserInfo.class);
					query.multiselect( m.get("loginId").alias("code"),us.get("userName").alias("codeDesc"),
							m.get("sourceType").alias("type"));

					// Find All
					Subquery<Long> agencyCode = query.subquery(Long.class);
					Root<LoginMaster> ocpm1 = agencyCode.from(LoginMaster.class);
					agencyCode.select(ocpm1.get("agencyCode"));
					Predicate a1 = cb.equal(ocpm1.get("loginId"), req.getLoginId());
					agencyCode.where(a1);

					Predicate n1 = cb.equal(m.get("applicationId"), req.getApplicationId());
					Predicate n3 = cb.equal(m.get("companyId"), req.getCompanyId());
					Predicate n4 = cb.equal(m.get("productId"), req.getProductId());
					Predicate n5 = cb.equal(m.get("status"), status);
					Predicate n12 = null;
					if ("Broker".equalsIgnoreCase(req.getUserType())) {
						n12 = cb.equal(m.get("brokerCode").as(Long.class), agencyCode);
					} else if ("User".equalsIgnoreCase(req.getUserType())) {
						n12 = cb.equal(m.get("agencyCode").as(Long.class), agencyCode);
					}
					Predicate n13 = cb.isNotNull(m.get("sourceType"));
					Predicate n14 = cb.isNotNull(m.get("loginId"));
					Predicate n15 = null;
					if(req.getType().equalsIgnoreCase("Q"))
						n15 = cb.isNull(m.get("endorsementTypeDesc")); 
					else if (req.getType().equalsIgnoreCase("E"))
						n15 = cb.isNotNull(m.get("endorsementTypeDesc"));
					
					Predicate us1 = cb.equal(us.get("loginId"), m.get("loginId"));
					query.where(n1, n3, n4, n5, n12, n13, n14,n15,us1);

					TypedQuery<Tuple> typedQuery1 = em.createQuery(query);
					list = typedQuery1.getResultList();
					list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.get("code"))))
							.collect(Collectors.toList());
					if (list != null && list.size() > 0) {

						for (Tuple data : list) {
							GetExistingBrokerListRes res = new GetExistingBrokerListRes();

							res.setCode(data.get("code") == null ? "" : data.get("code").toString());
							res.setCodeDesc(data.get("codeDesc") == null ? "" : data.get("codeDesc").toString());
							res.setType(data.get("type") == null ? "" : data.get("type").toString());
							resList.add(res);

						}
					}
				} else {
					resList = getReferalIssuerCommon(req, today,status);
				}

			} catch (Exception e) {
				e.printStackTrace();
				log.info("Log Details" + e.getMessage());
				return null;
			}
			return resList;

		}

		private List<GetExistingBrokerListRes> getReferalIssuerCommon(ExistingBrokerUserListReq req,
				Date today,String status) {
			List<Tuple> list = new ArrayList<Tuple>();
			List<Tuple> list1 = new ArrayList<Tuple>();
			List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
			try {
				{
					CriteriaBuilder cb = em.getCriteriaBuilder();
					CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

					Root<EserviceCommonDetails> m = query.from(EserviceCommonDetails.class);

					query.multiselect(m.get("bdmCode").alias("code"), m.get("bdmName").alias("codeDesc"),
							m.get("sourceType").alias("type"));
					Predicate n1 = cb.equal(m.get("applicationId"), req.getApplicationId());
					Predicate n2 = cb.isNotNull(m.get("applicationId"));
					Predicate n3 = cb.equal(m.get("companyId"), req.getCompanyId());
					Predicate n4 = cb.equal(m.get("productId"), req.getProductId());
					Predicate n5 = cb.equal(m.get("status"), status);
					Predicate n8 = cb.isNotNull(m.get("bdmCode"));
					Predicate n15 = null;
					if(req.getType().equalsIgnoreCase("Q"))
						n15 = cb.isNull(m.get("endorsementTypeDesc")); 
					else if (req.getType().equalsIgnoreCase("E"))
						n15 = cb.isNotNull(m.get("endorsementTypeDesc"));
					query.where(n1, n2, n3, n4, n5, n8,n15);

					TypedQuery<Tuple> typedQuery = em.createQuery(query);
					list = typedQuery.getResultList();
					list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.get("code"))))
							.collect(Collectors.toList());
					if (list != null && list.size() > 0) {

						for (Tuple data : list) {
							GetExistingBrokerListRes res = new GetExistingBrokerListRes();
							res.setCode(data.get("code") == null ? "" : data.get("code").toString());
							res.setCodeDesc(data.get("codeDesc") == null ? "" : data.get("codeDesc").toString());
							res.setType(data.get("type") == null ? "" : data.get("type").toString());
							resList.add(res);

						}
					}
				}
				{
					CriteriaBuilder cb1 = em.getCriteriaBuilder();
					CriteriaQuery<Tuple> query1 = cb1.createQuery(Tuple.class);

					Root<EserviceCommonDetails> m1 = query1.from(EserviceCommonDetails.class);
					Root<LoginUserInfo> us = query1.from(LoginUserInfo.class);
					query1.multiselect(m1.get("loginId").alias("code"), us.get("userName").alias("codeDesc"),
							m1.get("sourceType").alias("type"));
					Predicate n1 = cb1.equal(m1.get("applicationId"), req.getApplicationId());
					Predicate n2 = cb1.isNotNull(m1.get("applicationId"));
					Predicate n3 = cb1.equal(m1.get("companyId"), req.getCompanyId());
					Predicate n4 = cb1.equal(m1.get("productId"), req.getProductId());
					Predicate n5 = cb1.equal(m1.get("status"), status);
					Predicate n6 = cb1.isNull(m1.get("bdmCode"));
					Predicate us1 = cb1.equal(us.get("loginId"), m1.get("loginId"));
					
					Predicate n15 = null;
					if(req.getType().equalsIgnoreCase("Q"))
						n15 = cb1.isNull(m1.get("endorsementTypeDesc")); 
					else if (req.getType().equalsIgnoreCase("E"))
						n15 = cb1.isNotNull(m1.get("endorsementTypeDesc"));
					
					query1.where(n1, n2, n3, n4, n5, n6,us1,n15);

					TypedQuery<Tuple> typedQuery1 = em.createQuery(query1);
					list1 = typedQuery1.getResultList();
					list1 = list1.stream().filter(distinctByKey(o -> Arrays.asList(o.get("code"))))
							.collect(Collectors.toList());
					if (list1 != null && list1.size() > 0) {

						for (Tuple data : list1) {
							GetExistingBrokerListRes res = new GetExistingBrokerListRes();
							res.setCode(data.get("code") == null ? "" : data.get("code").toString());
							res.setCodeDesc(data.get("codeDesc") == null ? "" : data.get("codeDesc").toString());
							res.setType(data.get("type") == null ? "" : data.get("type").toString());
							resList.add(res);

						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Log Details" + e.getMessage());
				return null;
			}
			return resList;
		}

		@Override
		public List<GetExistingBrokerListRes> getAdminCommonRPDropdown(ExistingBrokerUserListReq req,
				Date today) {
			List<Tuple> list = new ArrayList<Tuple>();
			List<Tuple> list1 = new ArrayList<Tuple>();
			List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
			try {
				{
					CriteriaBuilder cb = em.getCriteriaBuilder();
					CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

					Root<EserviceCommonDetails> m = query.from(EserviceCommonDetails.class);

					query.multiselect(m.get("bdmCode").alias("code"), m.get("bdmName").alias("codeDesc"),
							m.get("sourceType").alias("type"));

					// Uw Condition

					Subquery<String> uwData = query.subquery(String.class);
					Root<UWReferralDetails> uw = uwData.from(UWReferralDetails.class);
					uwData.select(uw.get("requestReferenceNo"));
					Predicate u2 = cb.equal(uw.get("uwLoginId"), req.getApplicationId());
					Predicate u3 = cb.equal(uw.get("uwStatus"), "Y");
					Predicate u4 = cb.equal(uw.get("companyId"), req.getCompanyId());
					Predicate u5 = cb.equal(uw.get("productId"), req.getProductId());
					Predicate u6 = cb.equal(uw.get("branchCode"), req.getBranchCode());
					uwData.where(u2,u3,u4,u5,u6);
					
					//In 
					Expression<String>e0=m.get("requestReferenceNo"); 
					
					//Predicate n1 = cb.equal(m.get("applicationId"), req.getApplicationId());
					Predicate n2 = cb.isNotNull(m.get("applicationId"));
					Predicate n3 = cb.equal(m.get("companyId"), req.getCompanyId());
					Predicate n4 = cb.equal(m.get("productId"), req.getProductId());
					Predicate n5 = cb.equal(m.get("status"), "RP");
					Predicate n6 = e0.in(uwData);
					Predicate n8 = cb.isNotNull(m.get("bdmCode"));
					
					Predicate n15 = null;
					if(req.getType().equalsIgnoreCase("Q"))
						n15 = cb.isNull(m.get("endorsementTypeDesc")); 
					else if (req.getType().equalsIgnoreCase("E"))
						n15 = cb.isNotNull(m.get("endorsementTypeDesc"));
					query.where(n2,n3,n4,n5,n6, n8,n15);

					TypedQuery<Tuple> typedQuery = em.createQuery(query);
					list = typedQuery.getResultList();
					list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.get("code"))))
							.collect(Collectors.toList());
					if (list != null && list.size() > 0) {

						for (Tuple data : list) {
							GetExistingBrokerListRes res = new GetExistingBrokerListRes();
							res.setCode(data.get("code") == null ? "" : data.get("code").toString());
							res.setCodeDesc(data.get("codeDesc") == null ? "" : data.get("codeDesc").toString());
							res.setType(data.get("type") == null ? "" : data.get("type").toString());
							resList.add(res);

						}
					}
				}
				{
					CriteriaBuilder cb1 = em.getCriteriaBuilder();
					CriteriaQuery<Tuple> query1 = cb1.createQuery(Tuple.class);

					Root<EserviceCommonDetails> m1 = query1.from(EserviceCommonDetails.class);
					Root<LoginUserInfo> us = query1.from(LoginUserInfo.class);
					query1.multiselect( m1.get("loginId").alias("code"),us.get("userName").alias("codeDesc"),
							m1.get("sourceType").alias("type"));

					// Uw Condition

					Subquery<String> uwData = query1.subquery(String.class);
					Root<UWReferralDetails> uw = uwData.from(UWReferralDetails.class);
					uwData.select(uw.get("requestReferenceNo"));
					Predicate u2 = cb1.equal(uw.get("uwLoginId"), req.getApplicationId());
					Predicate u3 = cb1.equal(uw.get("uwStatus"), "Y");
					Predicate u4 = cb1.equal(uw.get("companyId"), req.getCompanyId());
					Predicate u5 = cb1.equal(uw.get("productId"), req.getProductId());
					Predicate u6 = cb1.equal(uw.get("branchCode"), req.getBranchCode());
					uwData.where(u2,u3,u4,u5,u6);
					
					//In 
					Expression<String>e0=m1.get("requestReferenceNo");
					
					//Predicate n1 = cb1.equal(m1.get("applicationId"), req.getApplicationId());
					Predicate n2 = cb1.isNotNull(m1.get("applicationId"));
					Predicate n3 = cb1.equal(m1.get("companyId"), req.getCompanyId());
					Predicate n4 = cb1.equal(m1.get("productId"), req.getProductId());
					Predicate n5 = cb1.equal(m1.get("status"), "RP");
					Predicate n6 = e0.in(uwData);
					Predicate n8 = cb1.isNull(m1.get("bdmCode"));
					Predicate us1 = cb1.equal(us.get("loginId"), m1.get("loginId"));
					
					Predicate n15 = null;
					if(req.getType().equalsIgnoreCase("Q"))
						n15 = cb1.isNull(m1.get("endorsementTypeDesc")); 
					else if (req.getType().equalsIgnoreCase("E"))
						n15 = cb1.isNotNull(m1.get("endorsementTypeDesc"));
					
					query1.where(n2,n3,n4,n5,n6,n8,us1,n15);

					TypedQuery<Tuple> typedQuery1 = em.createQuery(query1);
					list1 = typedQuery1.getResultList();
					list1 = list1.stream().filter(distinctByKey(o -> Arrays.asList(o.get("code"))))
							.collect(Collectors.toList());
					if (list1 != null && list1.size() > 0) {

						for (Tuple data : list1) {
							GetExistingBrokerListRes res = new GetExistingBrokerListRes();
							res.setCode(data.get("code") == null ? "" : data.get("code").toString());
							res.setCodeDesc(data.get("codeDesc") == null ? "" : data.get("codeDesc").toString());
							res.setType(data.get("type") == null ? "" : data.get("type").toString());
							resList.add(res);

						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Log Details" + e.getMessage());
				return null;
			}
			return resList;
		}
		@Override
		public List<GetExistingBrokerListRes> getCommonAdminReferalDropdown(ExistingBrokerUserListReq req,
				Date today,String status) {
			List<Tuple> list = new ArrayList<Tuple>();
			List<Tuple> list1 = new ArrayList<Tuple>();
			List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
			try {
				{
					CriteriaBuilder cb = em.getCriteriaBuilder();
					CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

					Root<EserviceCommonDetails> m = query.from(EserviceCommonDetails.class);

					query.multiselect(m.get("bdmCode").alias("code"), m.get("bdmName").alias("codeDesc"),
							m.get("sourceType").alias("type"));

					
					Predicate n1 = cb.equal(m.get("adminLoginId"), req.getApplicationId());
//					Predicate n2 = cb.isNotNull(m.get("applicationId"));
					Predicate n3 = cb.equal(m.get("companyId"), req.getCompanyId());
					Predicate n4 = cb.equal(m.get("productId"), req.getProductId());
					Predicate n5 = cb.equal(m.get("status"), status);
					Predicate n8 = cb.isNotNull(m.get("bdmCode"));
					
					Predicate n15 = null;
					if(req.getType().equalsIgnoreCase("Q"))
						n15 = cb.isNull(m.get("endorsementTypeDesc")); 
					else if (req.getType().equalsIgnoreCase("E"))
						n15 = cb.isNotNull(m.get("endorsementTypeDesc"));
					query.where(n1,n3,n4,n5, n8,n15);

					TypedQuery<Tuple> typedQuery = em.createQuery(query);
					list = typedQuery.getResultList();
					list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.get("code"))))
							.collect(Collectors.toList());
					if (list != null && list.size() > 0) {

						for (Tuple data : list) {
							GetExistingBrokerListRes res = new GetExistingBrokerListRes();
							res.setCode(data.get("code") == null ? "" : data.get("code").toString());
							res.setCodeDesc(data.get("codeDesc") == null ? "" : data.get("codeDesc").toString());
							res.setType(data.get("type") == null ? "" : data.get("type").toString());
							resList.add(res);

						}
					}
				}
				{
					CriteriaBuilder cb1 = em.getCriteriaBuilder();
					CriteriaQuery<Tuple> query1 = cb1.createQuery(Tuple.class);

					Root<EserviceCommonDetails> m1 = query1.from(EserviceCommonDetails.class);
					Root<LoginUserInfo> us = query1.from(LoginUserInfo.class);
					query1.multiselect(m1.get("loginId").alias("code"), us.get("userName").alias("codeDesc"),
							m1.get("sourceType").alias("type"));

					
					Predicate n1 = cb1.equal(m1.get("adminLoginId"), req.getApplicationId());
//					Predicate n2 = cb1.isNotNull(m1.get("applicationId"));
					Predicate n3 = cb1.equal(m1.get("companyId"), req.getCompanyId());
					Predicate n4 = cb1.equal(m1.get("productId"), req.getProductId());
					Predicate n5 = cb1.equal(m1.get("status"), status);
					Predicate n8 = cb1.isNull(m1.get("bdmCode"));
					Predicate us1 = cb1.equal(us.get("loginId"), m1.get("loginId"));
					
					Predicate n15 = null;
					if(req.getType().equalsIgnoreCase("Q"))
						n15 = cb1.isNull(m1.get("endorsementTypeDesc")); 
					else if (req.getType().equalsIgnoreCase("E"))
						n15 = cb1.isNotNull(m1.get("endorsementTypeDesc"));
					
					query1.where(n1,n3,n4,n5,n8,us1,n15);

					TypedQuery<Tuple> typedQuery1 = em.createQuery(query1);
					list1 = typedQuery1.getResultList();
					list1 = list1.stream().filter(distinctByKey(o -> Arrays.asList(o.get("code"))))
							.collect(Collectors.toList());
					if (list1 != null && list1.size() > 0) {

						for (Tuple data : list1) {
							GetExistingBrokerListRes res = new GetExistingBrokerListRes();
							res.setCode(data.get("code") == null ? "" : data.get("code").toString());
							res.setCodeDesc(data.get("codeDesc") == null ? "" : data.get("codeDesc").toString());
							res.setType(data.get("type") == null ? "" : data.get("type").toString());
							resList.add(res);

						}
					}
				}
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Log Details" + e.getMessage());
				return null;
			}
			return resList;
		}



}
