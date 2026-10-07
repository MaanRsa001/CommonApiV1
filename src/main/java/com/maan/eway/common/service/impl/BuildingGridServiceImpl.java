package com.maan.eway.common.service.impl;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
import com.maan.eway.admin.res.ReferalGridCriteriaAdminRes;
import com.maan.eway.admin.res.ReferalGridCriteriaRes;
import com.maan.eway.bean.BuildingDetails;
import com.maan.eway.bean.BuildingRiskDetails;
import com.maan.eway.bean.CommonDataDetails;
import com.maan.eway.bean.ContentAndRisk;
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
import com.maan.eway.common.req.NonMotorBrokerReq;
import com.maan.eway.common.req.NonMotorPolicyReq;
import com.maan.eway.common.req.RevertGridReq;
import com.maan.eway.common.req.SequenceGenerateReq;
import com.maan.eway.common.res.GetExistingBrokerListRes;
import com.maan.eway.common.res.GetMotorProtfolioPendingRes;
import com.maan.eway.common.res.GetRejectedQuoteDetailsRes;
import com.maan.eway.common.res.GetTravelReferalDetailsRes;
import com.maan.eway.common.res.PortfolioPendingGridCriteriaRes;
import com.maan.eway.common.res.QuoteCriteriaRes;
import com.maan.eway.common.res.QuoteCriteriaResponse;
import com.maan.eway.common.res.RejectCriteriaRes;
import com.maan.eway.common.service.BuildingGridService;
import com.maan.eway.common.service.CommonGridService;
import com.maan.eway.integration.service.impl.MySqlQuery;
import com.maan.eway.master.req.CopyQuoteDropDownReq;
import com.maan.eway.repository.BuildingDetailsRepository;
import com.maan.eway.repository.BuildingRiskDetailsRepository;
import com.maan.eway.repository.CommonDataDetailsRepository;
import com.maan.eway.repository.ContentAndRiskRepository;
import com.maan.eway.repository.DocumentTransactionDetailsRepository;
import com.maan.eway.repository.EServiceSectionDetailsRepository;
import com.maan.eway.repository.EndtTypeMasterRepository;
import com.maan.eway.repository.EserviceBuildingDetailsRepository;
import com.maan.eway.repository.EserviceCommonDetailsRepository;
import com.maan.eway.repository.EserviceCustomerDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
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
import jakarta.persistence.Query;
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
public class BuildingGridServiceImpl implements BuildingGridService {
	
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

	@PersistenceContext
	private EntityManager em;
	
	private Query query=null;

	private Logger log = LogManager.getLogger(BuildingGridServiceImpl.class);

	@Autowired
	private CommonDataDetailsRepository commonDataRepo;
	@Autowired
	private SectionDataDetailsRepository sectionDataRepo;
	@Autowired
	private EserviceBuildingDetailsRepository repo;
	
	@Autowired
	private EserviceCommonDetailsRepository eserCommonRepo;
	
	@Autowired
	private GenerateSeqNoServiceImpl seqNo ;
	
	@Autowired
	private MotorGridServiceImpl motorService ;

	@Autowired
	private HomePositionMasterRepository homePosistionRepo;
	
	@Autowired
	private PersonalInfoRepository personalInforepo;
	
	@Autowired
	private PolicyCoverDataRepository policyCoverDataRepo;
	
	@Autowired
	private EserviceCustomerDetailsRepository custRepo ;
	
	@Autowired
	private SeqQuotenoRepository quoteNoRepo ;
	
	@Autowired
	private SeqCustidRepository custIdRepo ;

	@Autowired
	private SeqRefnoRepository refNoRepo ;
	
	@Autowired
	private SeqCustrefnoRepository custRefRepo  ;
	
	@Autowired
	private EndtTypeMasterRepository endtTypeRepo;
	
	@Autowired
	private DocumentTransactionDetailsRepository coverDocUploadDetails;

	@Autowired
	private BuildingDetailsRepository buildingRepo;
	
	@Autowired
	private EServiceSectionDetailsRepository eserSecRepo;
	
	@Autowired
	private ProductEmployeesDetailsRepository pARepo;
	
	@Autowired
	private ContentAndRiskRepository contentRiskRepo;

	@Autowired
	private BuildingRiskDetailsRepository buildRiskRepo;
	@Autowired
	private MySqlQuery oracle ;
	
	 @Autowired
     private GenerateSeqNoServiceImpl genSeqNoService ;

	@Autowired 
	private RatingFactorsUtil ratingutil;
	
	@Autowired
	private CommonGridService commonGridService;
	

	
	// Exiting Building Details
	
	//DropDown
	@Override
	public List<GetExistingBrokerListRes> getBuildingExistingDropdown(ExistingBrokerUserListReq req, Date today, Date before30) {
		List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
		List<Tuple> list = new ArrayList<Tuple>();
		try {
		if(!("issuer".equalsIgnoreCase(req.getUserType()))){		
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

			Root<EserviceBuildingDetails> m = query.from(EserviceBuildingDetails.class);
			Root<LoginUserInfo> us = query.from(LoginUserInfo.class);
			query.multiselect(m.get("loginId").alias("code"),us.get("userName").alias("codeDesc"),
					m.get("sourceType").alias("type"));
			// Find All
			Subquery<String> agencyCode = query.subquery(String.class);
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
				predics1.add(cb.equal(m.get("brokerCode"), agencyCode));
			} else if ("User".equalsIgnoreCase(req.getUserType())) {
				predics1.add(cb.equal(m.get("agencyCode"), agencyCode));
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

		Root<EserviceBuildingDetails> m = query.from(EserviceBuildingDetails.class);

		query.multiselect(m.get("bdmCode").alias("code"), m.get("bdmName").alias("codeDesc"),
				m.get("sourceType").alias("type"));
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
//				res.setType(data.get("type") == null ? "" : data.get("type").toString());
				String type = data.get("type") == null ? "" : data.get("type").toString();
				type = "Premia " + type;
				res.setType(type);
				resList.add(res);

			}
		}
		}
		{CriteriaBuilder cb1 = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> query1 = cb1.createQuery(Tuple.class);

		Root<EserviceBuildingDetails> m1 = query1.from(EserviceBuildingDetails.class);
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
	public QuoteCriteriaResponse getBuildingExistingQuoteDetails(ExistingQuoteReq req,	Date startDate, Date endDate, Integer limit, Integer offset) {
		
		QuoteCriteriaResponse resp = new QuoteCriteriaResponse();
		List<QuoteCriteriaRes> existingQuotes = new ArrayList<QuoteCriteriaRes>();
		try {
			String empty = "";
			// Get Datas
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<QuoteCriteriaRes> query = cb.createQuery(QuoteCriteriaRes.class);
			CriteriaQuery<QuoteCriteriaRes> query1 = cb.createQuery(QuoteCriteriaRes.class);

			// Find All
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
			Root<EserviceBuildingDetails> m = query.from(EserviceBuildingDetails.class);

			// Over All Premium Fc
			Subquery<Long> overAllPremiumFc = query.subquery(Long.class);
			Root<EserviceBuildingDetails> ocpm1 = overAllPremiumFc.from(EserviceBuildingDetails.class);
			overAllPremiumFc.select(cb.sum(ocpm1.get("overallPremiumFc")));
			Predicate a1 = cb.equal(m.get("requestReferenceNo"), ocpm1.get("requestReferenceNo"));
			overAllPremiumFc.where(a1);

			// Over All Premium Lc
			Subquery<Long> overAllPremiumLc = query.subquery(Long.class);
			Root<EserviceBuildingDetails> ocpm2 = overAllPremiumLc.from(EserviceBuildingDetails.class);
			overAllPremiumLc.select(cb.sum(ocpm2.get("overallPremiumLc")));
			Predicate a2 = cb.equal(m.get("requestReferenceNo"), ocpm2.get("requestReferenceNo"));
			overAllPremiumLc.where(a2);

			// Select
			query.multiselect(c.get("customerReferenceNo").alias("customerReferenceNo"),
					c.get("idNumber").alias("idNumber"), c.get("clientName").alias("clientName"),
					// Vehicle Info
					m.get("companyId").alias("companyId"), m.get("productId").alias("productId"),
					m.get("productDesc").alias("productName"), m.get("branchCode").alias("branchCode"),
					m.get("requestReferenceNo").alias("requestReferenceNo"), m.get("quoteNo").alias("quoteNo"),
					m.get("customerId").alias("customerId"), m.get("policyStartDate").alias("policyStartDate"),
					m.get("policyEndDate").alias("policyEndDate"), overAllPremiumLc.alias("overallPremiumLc"),
					overAllPremiumFc.alias("overallPremiumFc"), m.get("currency").alias("currency"),
					// This Line is For Empty String
					cb.selectCase().when(m.get("companyId").isNotNull(), "").alias("savedFrom"));

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(m.get("requestReferenceNo")));

			// Where
			Predicate n1 = cb.equal(c.get("customerReferenceNo"), m.get("customerReferenceNo"));
			Predicate n2 = cb.equal(m.get("companyId"), req.getInsuranceId());
			Predicate n3 = cb.equal(m.get("productId"), req.getProductId());
			Predicate n4 = cb.equal(m.get("status"), "Y");
			Predicate n5 = cb.lessThanOrEqualTo(m.get("updatedDate"), endDate);
			Predicate n6 = cb.greaterThanOrEqualTo(m.get("updatedDate"), startDate);
			Predicate n9 = cb.isNull(m.get("endorsementType"));
			Predicate n7 = null;
			Predicate n10 = null;
			n7 = cb.equal(m.get("applicationId"), req.getApplicationId());
			if (StringUtils.isNotBlank(req.getBdmCode())) {

				n10 = cb.equal(m.get("bdmCode"), req.getBdmCode());

			} else {

				n10 = cb.equal(m.get("loginId"), req.getLoginId());
			}

			Predicate n8 = null;

			if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {

				n8 = cb.equal(m.get("brokerBranchCode"), req.getBrokerBranchCode());
			} else {

				n8 = cb.equal(m.get("branchCode"), req.getBranchCode());
			}

			// Risk Max Filter
			Subquery<Long> riskId = query.subquery(Long.class);
			Root<EserviceBuildingDetails> ocp = riskId.from(EserviceBuildingDetails.class);
			riskId.select(cb.max(ocp.get("riskId")));
			Predicate a3 = cb.equal(ocp.get("requestReferenceNo"), m.get("requestReferenceNo"));
			riskId.where(a3);
			Predicate n13 = cb.equal(m.get("riskId"), riskId);
			query.where(n1, n2, n3, n4, n5, n6, n7, n8, n9, n10, n13).orderBy(orderList).distinct(true);

			// Get Result
			TypedQuery<QuoteCriteriaRes> result = em.createQuery(query);
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			existingQuotes.addAll(result.getResultList());

			Root<EserviceCustomerDetails> c1 = query1.from(EserviceCustomerDetails.class);
			Root<EserviceCommonDetails> m1 = query1.from(EserviceCommonDetails.class);

			Subquery<Long> overallPremiumLc = query1.subquery(Long.class);
			Root<EserviceCommonDetails> ocpm11 = overallPremiumLc.from(EserviceCommonDetails.class);
			overallPremiumLc.select(cb.sum(ocpm11.get("overallPremiumLc")));
			Predicate a4 = cb.equal(ocpm11.get("requestReferenceNo"), m1.get("requestReferenceNo"));
			overallPremiumLc.where(a4);
			
			//overallPremiumFc
			Subquery<Long> overallPremiumFc = query1.subquery(Long.class);
			Root<EserviceCommonDetails> oc = overallPremiumFc.from(EserviceCommonDetails.class);
			overallPremiumFc.select(cb.sum(oc.get("overallPremiumFc")));
			Predicate a5 = cb.equal(oc.get("requestReferenceNo"), m1.get("requestReferenceNo"));
			overallPremiumFc.where(a5);
		
			// Select
			query1.multiselect(
					
					// Customer Info
					c1.get("customerReferenceNo").alias("customerReferenceNo"), c1.get("idNumber").alias("idNumber"),
					c1.get("clientName").alias("clientName"),
					// Vehicle Info
					m1.get("companyId").alias("companyId"), m1.get("productId").alias("productId"),
					m1.get("productDesc").alias("productName"),
					m1.get("branchCode").alias("branchCode"), m1.get("requestReferenceNo").alias("requestReferenceNo"),
					m1.get("quoteNo").alias("quoteNo"),
					m1.get("customerId").alias("customerId"),
					m1.get("policyStartDate").alias("policyStartDate"), m1.get("policyEndDate").alias("policyEndDate"),
					overallPremiumLc.alias("overallPremiumLc"), 
					overallPremiumFc.alias("overallPremiumFc"),
					m1.get("currency").alias("currency"),
					cb.selectCase().when(m1.get("companyId").isNotNull(), "").alias("savedFrom")
					);
			

			// Order By
			List<Order> orderList1 = new ArrayList<Order>();
			orderList1.add(cb.desc(m1.get("updatedDate")));

			// Where
			Predicate n11 = cb.equal(c1.get("customerReferenceNo"), m1.get("customerReferenceNo"));
			Predicate n12 = cb.equal(m1.get("companyId"), req.getInsuranceId());
			Predicate n23 = cb.equal(m1.get("productId"), req.getProductId());
			Predicate n14 = cb.equal(m1.get("status"), "Y");
			Predicate n15 = cb.lessThanOrEqualTo(m1.get("updatedDate"), endDate);
			Predicate n16 = cb.greaterThanOrEqualTo(m1.get("updatedDate"), startDate);
			Predicate n19 = cb.isNull(m1.get("endorsementType"));
			Predicate n17 = null;
			Predicate n21 = null;
			
			n17 = cb.equal(m1.get("applicationId"), req.getApplicationId());
			if(StringUtils.isNotBlank(req.getBdmCode())){
				
				n21 = cb.equal(m1.get("bdmCode"), req.getBdmCode());
				
			}else {
				
				n21= cb.equal(m1.get("loginId"), req.getLoginId());
			}

			Predicate n18 = null;
			if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
			
				n18 = cb.equal(  m1.get("brokerBranchCode"),  req.getBrokerBranchCode());
			} else {
				
				n18 = cb.equal(  m1.get("branchCode"),  req.getBranchCode());
			}
			// Risk Max Filter
			Subquery<Long> riskId1 = query1.subquery(Long.class);
			Root<EserviceCommonDetails> ocp1 = riskId1.from(EserviceCommonDetails.class);
			riskId1.select(cb.max(ocp1.get("riskId")));
			Predicate a6 = cb.equal(ocp1.get("requestReferenceNo"), m1.get("requestReferenceNo"));
			riskId1.where(a6);

			Predicate n20 = cb.equal(m1.get("riskId"), riskId1);
			query1.where(n11, n12, n23, n14, n15, n16, n17, n18,n19,n20,n21).orderBy(orderList1);
			
			// Get Result
			TypedQuery<QuoteCriteriaRes> result1 = em.createQuery(query1);
			result1.setFirstResult(limit * offset);
			result1.setMaxResults(offset);
			existingQuotes.addAll(result1.getResultList());

			if (existingQuotes != null && existingQuotes.size() > 0) {
				existingQuotes = existingQuotes.stream().filter(distinctByKey(o -> Arrays.asList(o.getRequestReferenceNo()))).collect(Collectors.toList());
			}else {
				existingQuotes=null;
			}
			resp.setQuoteRes(existingQuotes);

			// resp.setTotalCount(totalcountexisting(req, startDate, endDate, "Y"));
			resp.setTotalCount((long) existingQuotes.size());

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

			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
			Root<EserviceBuildingDetails> m = query.from(EserviceBuildingDetails.class);

//			query.multiselect(cb.count(m));
			query.select(cb.countDistinct(m.get("requestReferenceNo"))); 
					

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(m.get("updatedDate")));

			// Where
			Predicate n1 = cb.equal(c.get("customerReferenceNo"), m.get("customerReferenceNo"));
			Predicate n2 = cb.equal(m.get("companyId"), req.getInsuranceId());
			Predicate n3 = cb.equal(m.get("productId"), req.getProductId());
			Predicate n4 = cb.equal(m.get("status"), status);
			Predicate n5 = cb.lessThanOrEqualTo(m.get("updatedDate"), endDate);
			Predicate n6 = cb.greaterThanOrEqualTo(m.get("updatedDate"), startDate);
			Predicate n9 = cb.isNull(m.get("endorsementType"));
			Predicate n7 = null;
			Predicate n10 = null;
			n7 = cb.equal(m.get("applicationId"), req.getApplicationId());
			if(StringUtils.isNotBlank(req.getBdmCode())){
				
				n10 = cb.equal(m.get("bdmCode"), req.getBdmCode());
				
			}else {
				
				n10 = cb.equal(m.get("loginId"), req.getLoginId());
			}

			Predicate n8 = null;
			if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
				
				n8 = cb.equal(  m.get("brokerBranchCode"),  req.getBrokerBranchCode());
			} else {
				
				n8 = cb.equal(  m.get("branchCode"),  req.getBranchCode());
			}
			// Risk Max Filter
			Subquery<Long> riskId = query.subquery(Long.class);
			Root<EserviceBuildingDetails> ocp = riskId.from(EserviceBuildingDetails.class);
			riskId.select(cb.max(ocp.get("riskId")));
			Predicate a3 = cb.equal(ocp.get("requestReferenceNo"), m.get("requestReferenceNo"));
			riskId.where(a3);
			Predicate n13 = cb.equal(m.get("riskId"), riskId);
//			Predicate n13 = cb.equal(  m.get("sectionId"),  "0");
			query.where(n1, n2, n3, n4, n5, n6, n7, n8, n9, n10,n13).orderBy(orderList);
			
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
	public QuoteCriteriaResponse getBuildingLapsedQuoteDetails(ExistingQuoteReq req, Date before30,
			int limit, int offset) {
		List<QuoteCriteriaRes> lapsedQuotes = new ArrayList<QuoteCriteriaRes>();
		QuoteCriteriaResponse resp = new QuoteCriteriaResponse();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<QuoteCriteriaRes> query = cb.createQuery(QuoteCriteriaRes.class);

			// Find All
			Root<EserviceBuildingDetails> m = query.from(EserviceBuildingDetails.class);
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);

			// Over All Premium Fc
			Subquery<Long> overAllPremiumFc = query.subquery(Long.class);
			Root<HomePositionMaster> ocpm1 = overAllPremiumFc.from(HomePositionMaster.class);
			overAllPremiumFc.select(cb.sum(ocpm1.get("overallPremiumFc")));
			Predicate a1 = cb.equal(m.get("quoteNo"),ocpm1.get("quoteNo") );
			overAllPremiumFc.where(a1);
			
			// Over All Premium Lc
			Subquery<Long> overAllPremiumLc = query.subquery(Long.class);
			Root<HomePositionMaster> ocpm2 = overAllPremiumLc.from(HomePositionMaster.class);
			overAllPremiumLc.select(cb.sum(ocpm2.get("overallPremiumLc")));
			Predicate a2 = cb.equal(m.get("quoteNo"),ocpm2.get("quoteNo") );
			overAllPremiumLc.where(a2);
			
			// Select
			query.multiselect(
					c.get("customerReferenceNo").alias("customerReferenceNo"), c.get("idNumber").alias("idNumber"),
					c.get("clientName").alias("clientName"),
					// Vehicle Info
					m.get("companyId").alias("companyId"), m.get("productId").alias("productId"),
					m.get("productDesc").alias("productName"),
					m.get("branchCode").alias("branchCode"), m.get("requestReferenceNo").alias("requestReferenceNo"),
					m.get("quoteNo").alias("quoteNo"),
					m.get("customerId").alias("customerId"),
					m.get("policyStartDate").alias("policyStartDate"), m.get("policyEndDate").alias("policyEndDate"),
					overAllPremiumLc.alias("overallPremiumLc"), 
					overAllPremiumFc.alias("overallPremiumFc"),
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

			Predicate n6 = null;
			Predicate n10 = null;
			
			if (req.getApplicationId().equalsIgnoreCase("1")) {
				n6 = cb.equal(m.get("loginId"), req.getLoginId());
				n10 = cb.equal(m.get("applicationId"), req.getApplicationId());
			} else {
				n6 = cb.equal(m.get("applicationId"), req.getApplicationId());
				if(StringUtils.isNotBlank(req.getBdmCode())){
					n10 = cb.equal(m.get("bdmCode"), req.getBdmCode());
				}else {
					n10 = cb.equal(m.get("loginId"), req.getLoginId());
				}
				
			}
			Predicate n7 = null;
			if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
				n7 = cb.equal( m.get("brokerBranchCode"),  req.getBrokerBranchCode());
			} else {
				
				n7 = cb.equal( m.get("branchCode"),  req.getBranchCode());
			}
			
//			Predicate n13 = cb.equal(  m.get("sectionId"),  "0");
			query.where(n1, n2, n3, n4, n5, n6, n7,n9,n10).orderBy(orderList);

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
			resp.setTotalCount(totallapsedquotes(req, before30));
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resp;
	}

	private Long totallapsedquotes(ExistingQuoteReq req, Date before30) {
		Long count = 0l;
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> query = cb.createQuery(Long.class);

			// Find All
			Root<EserviceBuildingDetails> m = query.from(EserviceBuildingDetails.class);
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);

			// Select
//			query.multiselect(cb.count(m));
			query.select(cb.countDistinct(m.get("requestReferenceNo"))); 

			// Where
			Predicate n1 = cb.equal(c.get("customerReferenceNo"), m.get("customerReferenceNo"));
			Predicate n2 = cb.equal(m.get("companyId"), req.getInsuranceId());
			Predicate n3 = cb.equal(m.get("productId"), req.getProductId());
			Predicate n4 = cb.equal(m.get("status"), "Y");
			Predicate n5 = cb.lessThanOrEqualTo(m.get("updatedDate"), before30);
			Predicate n9 = cb.isNull(m.get("endorsementType"));

			Predicate n6 = null;
			Predicate n10 = null;
			
			if (req.getApplicationId().equalsIgnoreCase("1")) {
				n6 = cb.equal(m.get("loginId"), req.getLoginId());
				n10 = cb.equal(m.get("applicationId"), req.getApplicationId());
			} else {
				n6 = cb.equal(m.get("applicationId"), req.getApplicationId());
				if(StringUtils.isNotBlank(req.getBdmCode())){
					n10 = cb.equal(m.get("bdmCode"), req.getBdmCode());
				}else {
					n10 = cb.equal(m.get("loginId"), req.getLoginId());
				}
				
			}
			Predicate n7 = null;
			
			if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
				n7 = cb.equal( m.get("brokerBranchCode"),  req.getBrokerBranchCode());
			} else {
				
				n7 = cb.equal( m.get("branchCode"),  req.getBranchCode());
			}
			
//			Predicate n13 = cb.equal(  m.get("sectionId"),  "0");
			query.where(n1, n2, n3, n4, n5, n6, n7,n9,n10);

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
	public GetRejectedQuoteDetailsRes getBuildingRejectedQuoteDetails(ExistingQuoteReq req,Date startDate, Date endDate, int limit,int offset) {
			
		List<RejectCriteriaRes> rejectedQuotes = new ArrayList<RejectCriteriaRes>();
		GetRejectedQuoteDetailsRes resp = new GetRejectedQuoteDetailsRes();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<RejectCriteriaRes> query = cb.createQuery(RejectCriteriaRes.class);

			
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
			Root<EserviceBuildingDetails> m = query.from(EserviceBuildingDetails.class);

			// Over All Premium Fc
			Subquery<Long> overAllPremiumFc = query.subquery(Long.class);
			Root<HomePositionMaster> ocpm1 = overAllPremiumFc.from(HomePositionMaster.class);
			overAllPremiumFc.select(cb.sum(ocpm1.get("overallPremiumFc")));
			Predicate a1 = cb.equal(m.get("quoteNo"),ocpm1.get("quoteNo") );
			overAllPremiumFc.where(a1);
			
			// Over All Premium Lc
			Subquery<Long> overAllPremiumLc = query.subquery(Long.class);
			Root<HomePositionMaster> ocpm2 = overAllPremiumLc.from(HomePositionMaster.class);
			overAllPremiumLc.select(cb.sum(ocpm2.get("overallPremiumLc")));
			Predicate a2 = cb.equal(m.get("quoteNo"),ocpm2.get("quoteNo") );
			overAllPremiumLc.where(a2);
			
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
					overAllPremiumLc.alias("overallPremiumLc"), 
					overAllPremiumFc.alias("overallPremiumFc"),
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
			Predicate n10 = null;
			
			
//			if (req.getApplicationId().equalsIgnoreCase("1")) {
//				n7 = cb.equal(m.get("loginId"), req.getLoginId());
//				n10 = cb.equal(m.get("applicationId"), req.getApplicationId());
//			} else {
//				n7 = cb.equal(m.get("applicationId"), req.getApplicationId());
//				if(StringUtils.isNotBlank(req.getBdmCode())){
//					n10 = cb.equal(m.get("bdmCode"), req.getBdmCode());
//				}else {
//					n10 = cb.equal(m.get("loginId"), req.getLoginId());
//				}
//			}
			
			n7 = cb.equal(m.get("applicationId"), req.getApplicationId());
			if(StringUtils.isNotBlank(req.getBdmCode())){
				n10 = cb.equal(m.get("bdmCode"), req.getBdmCode());
			}else {
				n10 = cb.equal(m.get("loginId"), req.getLoginId());
			}

			Predicate n8 = null;
			if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
				
				n8 = cb.equal(  m.get("brokerBranchCode"),  req.getBrokerBranchCode());
			} else {
				
				n8 = cb.equal(  m.get("branchCode"),  req.getBranchCode());
			}
//			Predicate n13 = cb.equal(  m.get("sectionId"),  "0");
			query.where(n1, n2, n3, n4, n5, n6, n7, n8,n9,n10).orderBy(orderList);

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
			resp.setTotalCount(totalcountexisting(req, startDate, endDate, "R"));
		
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resp;
	}

	
	@Override
	public synchronized GetTravelReferalDetailsRes getBuildingReferalDetails(ExistingQuoteReq req, 
			int limit, int offset , String status) {
		List<ReferalGridCriteriaRes> referrals = new ArrayList<ReferalGridCriteriaRes>();
		GetTravelReferalDetailsRes resp = new GetTravelReferalDetailsRes();
		try {
			resp.setTotalCount(0);
		
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ReferalGridCriteriaRes> query = cb.createQuery(ReferalGridCriteriaRes.class);

		
			Root<EserviceBuildingDetails> m = query.from(EserviceBuildingDetails.class); 
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class); 

			
			query.multiselect(
					// Customer Info
					c.get("customerReferenceNo").alias("customerReferenceNo"), c.get("idNumber").alias("idNumber"),
					c.get("clientName").alias("clientName"),
					c.get("titleDesc").alias("titleDesc"),
					c.get("middleName").alias("middleName"),c.get("lastName").alias("lastName"),
					// Vehicle Info
					m.get("companyId").alias("companyId"), m.get("productId").alias("productId"),
					m.get("branchCode").alias("branchCode"), m.get("requestReferenceNo").alias("requestReferenceNo"),
					 m.get("quoteNo").alias("quoteNo"),
							
					 m.get("customerId").alias("customerId"),
							
					m.get("policyStartDate").alias("policyStartDate"), m.get("policyEndDate").alias("policyEndDate"),
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
					m.get("endtPremium").alias("endtPremium"));

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(m.get("updatedDate")));

			// Where
			Predicate n1 = cb.equal(c.get("customerReferenceNo"), m.get("customerReferenceNo"));
			Predicate n2 = cb.equal(m.get("companyId"), req.getInsuranceId());
			Predicate n3 = cb.equal(m.get("productId"), req.getProductId());
			Predicate n4 = cb.equal(m.get("status"), status);

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
				 n6 = cb.equal(  m.get("brokerBranchCode"),  req.getBrokerBranchCode());
			} else {
				
				 n6 = cb.equal(  m.get("branchCode"),  req.getBranchCode());
			}
			Predicate n8 = null;
			if(req.getType().equalsIgnoreCase("Q"))
				n8 = cb.isNull(m.get("endorsementTypeDesc")); 
			else if (req.getType().equalsIgnoreCase("E"))
				n8 = cb.isNotNull(m.get("endorsementTypeDesc")); 
			
			Predicate nLead = null;
			if (req.getLeadSeqNo() != null) {
				nLead = cb.equal(c.get("leadSeqNo"), req.getLeadSeqNo());
			}

			if (req.getLeadSeqNo() == null) {
				query.where(n1, n2, n3, n4, n5, n6, n8, n9).orderBy(orderList);
			} else {
				query.where(n1, n2, n3, n4, n5, n6, n8, n9, nLead).orderBy(orderList);
			}
			// query.where(n1, n2, n3, n4, n5, n6,n8,n9) .orderBy(orderList);
			
			// Get Result
			TypedQuery<ReferalGridCriteriaRes> result = em.createQuery(query);
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			referrals = result.getResultList();
			
			if (referrals != null && referrals.size() > 0) {
				referrals = referrals.stream().filter(distinctByKey(o -> Arrays.asList(o.getRequestReferenceNo()))).collect(Collectors.toList());
			}else {
				referrals=null;
			}
	
			//Counts
			int totalcount = totalcountuser(req,  status);
			resp.setTotalCount(totalcount);		
			
			resp.setReferalGridCriteriaRes(referrals);
		
			
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

		
			Root<EserviceBuildingDetails> m = query.from(EserviceBuildingDetails.class); 
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class); 

			query.select(cb.countDistinct(m.get("requestReferenceNo"))); 

			
			// Where
			Predicate n1 = cb.equal(c.get("customerReferenceNo"), m.get("customerReferenceNo"));
			Predicate n2 = cb.equal(m.get("companyId"), req.getInsuranceId());
			Predicate n3 = cb.equal(m.get("productId"), req.getProductId());
			Predicate n4 = cb.equal(m.get("status"), status);

			Predicate n5 = null;
			Predicate n10 = null;
			if (req.getApplicationId().equalsIgnoreCase("1")) {
				n5 = cb.equal(m.get("loginId"), req.getLoginId());
				n10 = cb.equal(m.get("applicationId"), req.getApplicationId());
				
			} else {
				n5 = cb.equal(m.get("applicationId"), req.getApplicationId());
				if(StringUtils.isNotBlank(req.getBdmCode())){
					n10 = cb.equal(m.get("bdmCode"), req.getBdmCode());
				}else {
					n10 = cb.equal(m.get("loginId"), req.getLoginId());
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
	
//			Predicate n13 = cb.equal(  m.get("sectionId"),  "0");
			
			Predicate nLeadCount = null;
			if (req.getLeadSeqNo() != null) {
				nLeadCount = cb.equal(c.get("leadSeqNo"), req.getLeadSeqNo());
			}
			if (req.getLeadSeqNo() == null) {
				query.where(n1, n2, n3, n4, n5, n6, n8, n10);
			} else {
				query.where(n1, n2, n3, n4, n5, n6, n8, n10, nLeadCount);
			}
			
			//query.where(n1, n2, n3, n4, n5, n6,n8,n10);
	
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
		public synchronized GetTravelReferalDetailsRes getBuildingAdminReferalDetails(ExistingQuoteReq req,  int limit,	int offset, String status) {
			List<ReferalGridCriteriaRes> referrals = new ArrayList<ReferalGridCriteriaRes>();
			GetTravelReferalDetailsRes resp = new GetTravelReferalDetailsRes();
			try {
				resp.setTotalCount(0);
			
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<ReferalGridCriteriaRes> query = cb.createQuery(ReferalGridCriteriaRes.class);
				
				Root<EserviceBuildingDetails> m = query.from(EserviceBuildingDetails.class);
				Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
				
			
				query.multiselect(
						// Customer Info
						c.get("customerReferenceNo").alias("customerReferenceNo"), c.get("idNumber").alias("idNumber"),
						c.get("clientName").alias("clientName"),
						c.get("titleDesc").alias("titleDesc"),
						c.get("middleName").alias("middleName"),c.get("lastName").alias("lastName"),
						// Vehicle Info
						m.get("companyId").alias("companyId"), m.get("productId").alias("productId"),
						m.get("branchCode").alias("branchCode"), m.get("requestReferenceNo").alias("requestReferenceNo"),
						m.get("quoteNo").alias("quoteNo"),
								
						m.get("customerId").alias("customerId"),
						m.get("policyStartDate").alias("policyStartDate"), m.get("policyEndDate").alias("policyEndDate"),
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
						m.get("endtPremium").alias("endtPremium"));
				// Order By
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(m.get("updatedDate")));
				// Where
				Predicate n1 = cb.equal(c.get("customerReferenceNo"), m.get("customerReferenceNo"));
				Predicate n2 = cb.equal(m.get("companyId"), req.getInsuranceId());
				Predicate n3 = cb.equal(m.get("productId"), req.getProductId());
				Predicate n4 = cb.equal(m.get("status"), status);
				Predicate n6 = cb.equal(m.get("branchCode"), req.getBranchCode());
				
				Predicate n14 = null;
				if(req.getType().equalsIgnoreCase("Q"))
						n14 = cb.isNull(m.get("endorsementTypeDesc"));
				else if (req.getType().equalsIgnoreCase("E"))
						n14 = cb.isNotNull(m.get("endorsementTypeDesc"));
				Predicate n15 = null;
				Predicate n16 = null;
				n15 = cb.equal(m.get("adminLoginId"), req.getApplicationId());
				if (StringUtils.isNotBlank(req.getBdmCode())) {
				    n16 = cb.or(
				        cb.equal(m.get("bdmCode"), req.getBdmCode()),   
				        cb.equal(m.get("loginId"), req.getBdmCode())   
				    );
				} else {
				    n16 = cb.equal(m.get("loginId"), req.getLoginId());
				}
				
				// Uw Condition
//				Predicate n19 = cb.equal(  m.get("sectionId"),  "0");
				if("RP".equalsIgnoreCase(status)) {
					Root<UWReferralDetails> uw = query.from(UWReferralDetails.class);
					Predicate n8 = cb.equal(uw.get("requestReferenceNo"), m.get("requestReferenceNo"));
					Predicate n9 = cb.equal(uw.get("uwLoginId"),req.getApplicationId());
					Predicate n10 = cb.equal(uw.get("uwStatus"), "Y");
					Predicate n11 = cb.equal(uw.get("companyId"), req.getInsuranceId());
					Predicate n12 = cb.equal(uw.get("productId"), req.getProductId());
					Predicate n13 = cb.equal(uw.get("branchCode"), req.getBranchCode());
					query.where(n1, n2, n3, n4, n6,n8,n9,n10,n11,n12,n13,n14,n16).orderBy(orderList);
							
							
				} else {
					query.where(n1, n2, n3, n4, n6,n14,n16,n15).orderBy(orderList);
						
				}
				
				// Get Result
				System.out.println("ReferalGridCriteriaRes:");
				TypedQuery<ReferalGridCriteriaRes> result = em.createQuery(query);
				System.out.println("ReferalGridCriteriaRes 1 :"+result);
				result.setFirstResult(limit * offset);
				result.setMaxResults(offset);
				referrals = result.getResultList();
				System.out.println("ReferalGridCriteriaRes :"+referrals);
				if (referrals != null && referrals.size() > 0) {
					referrals = referrals.stream().filter(distinctByKey(o -> Arrays.asList(o.getRequestReferenceNo()))).collect(Collectors.toList());
				}else {
					referrals=null;
				}
				
				resp.setReferalGridCriteriaRes(referrals);
				
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
		int count =0;
		try {
			
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> query = cb.createQuery(Long.class);
			
			Root<EserviceBuildingDetails> m = query.from(EserviceBuildingDetails.class);
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
			
			query.select(cb.countDistinct(m.get("requestReferenceNo"))); 
		
		
			Predicate n7 = cb.isNull(m.get("endorsementTypeDesc")); 
			Predicate n1 = cb.equal(c.get("customerReferenceNo"), m.get("customerReferenceNo"));
			Predicate n2 = cb.equal(m.get("companyId"), req.getInsuranceId());
			Predicate n3 = cb.equal(m.get("productId"), req.getProductId());
			Predicate n4 = cb.equal(m.get("status"), status);
			Predicate n6 = cb.equal(m.get("branchCode"), req.getBranchCode()); 
			
		
			Predicate n15 = null;
			Predicate n16 = null;
			n15 = cb.equal(m.get("adminLoginId"), req.getApplicationId());
			if(StringUtils.isNotBlank(req.getBdmCode())){
				n16 = cb.equal(m.get("bdmCode"), req.getBdmCode());
			}else {
				n16 = cb.equal(m.get("loginId"), req.getLoginId());
			}
			
			
			// Uw Condition 
//			Predicate n19 = cb.equal(  m.get("sectionId"),  "0");
			if("RP".equalsIgnoreCase(status)) {
				Root<UWReferralDetails> uw = query.from(UWReferralDetails.class);
				Predicate n8 = cb.equal(uw.get("requestReferenceNo"), m.get("requestReferenceNo")); 
				Predicate n9 = cb.equal(uw.get("uwLoginId"),req.getApplicationId()); 
				Predicate n10 = cb.equal(uw.get("uwStatus"), "Y"); 
				Predicate n11 = cb.equal(uw.get("companyId"), req.getInsuranceId()); 
				Predicate n12 = cb.equal(uw.get("productId"), req.getProductId()); 
				Predicate n13 = cb.equal(uw.get("branchCode"), req.getBranchCode()); 
				query.where(n1, n2, n3, n4, n6,n7, n8,n9,n10,n11,n12,n13,n16);
						
						
			} else {
				query.where(n1, n2, n3, n4, n6,n7,n16,n15);
					
			}
			
			// Get Result
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
		int count =0;
		try {
			
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> query = cb.createQuery(Long.class);
			
			Root<EserviceBuildingDetails> m = query.from(EserviceBuildingDetails.class);
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
			
//			query.multiselect(cb.count(m));
			query.select(cb.countDistinct(m.get("requestReferenceNo")));  
			
			Predicate n7 = cb.isNotNull(m.get("endorsementTypeDesc")); 
			Predicate n1 = cb.equal(c.get("customerReferenceNo"), m.get("customerReferenceNo"));
			Predicate n2 = cb.equal(m.get("companyId"), req.getInsuranceId());
			Predicate n3 = cb.equal(m.get("productId"), req.getProductId());
			Predicate n4 = cb.equal(m.get("status"), status);
			Predicate n6 = cb.equal(m.get("branchCode"), req.getBranchCode()); 
			
		
			Predicate n15 = null;
			Predicate n16 = null;
			n15 = cb.equal(m.get("adminLoginId"), req.getApplicationId());
			if(StringUtils.isNotBlank(req.getBdmCode())){
				n16 = cb.equal(m.get("bdmCode"), req.getBdmCode());
			}else {
				n16 = cb.equal(m.get("loginId"), req.getLoginId());
			}
			
			
			// Uw Condition 
//			Predicate n19 = cb.equal(  m.get("sectionId"),  "0");
			if("RP".equalsIgnoreCase(status)) {
				Root<UWReferralDetails> uw = query.from(UWReferralDetails.class);
				Predicate n8 = cb.equal(uw.get("requestReferenceNo"), m.get("requestReferenceNo")); 
				Predicate n9 = cb.equal(uw.get("uwLoginId"),req.getApplicationId()); 
				Predicate n10 = cb.equal(uw.get("uwStatus"), "Y"); 
				Predicate n11 = cb.equal(uw.get("companyId"), req.getInsuranceId()); 
				Predicate n12 = cb.equal(uw.get("productId"), req.getProductId()); 
				Predicate n13 = cb.equal(uw.get("branchCode"), req.getBranchCode()); 
				query.where(n1, n2, n3, n4, n6,n7,n8,n9,n10,n11,n12,n13,n16);
						
						
			} else {
				query.where(n1, n2, n3, n4, n6,n7,n16,n15);
					
			}
			// Get Result
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

	private int totalcount(ExistingQuoteReq req, String status) {
		int count =0;
		try {
			
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> query = cb.createQuery(Long.class);
			
			Root<EserviceBuildingDetails> m = query.from(EserviceBuildingDetails.class);
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
			
			query.multiselect(cb.count(m));
			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(m.get("updatedDate")));
			// Where
			Predicate n1 = cb.equal(c.get("customerReferenceNo"), m.get("customerReferenceNo"));
			Predicate n2 = cb.equal(m.get("companyId"), req.getInsuranceId());
			Predicate n3 = cb.equal(m.get("productId"), req.getProductId());
			Predicate n4 = cb.equal(m.get("status"), status);
		
			Predicate n6 = cb.equal(m.get("branchCode"), req.getBranchCode()); 
		
			
			// Uw Condition 
//			Predicate n15 = cb.equal(  m.get("sectionId"),  "0");
			if("RP".equalsIgnoreCase(status)) {
				Root<UWReferralDetails> uw = query.from(UWReferralDetails.class);
				Predicate n8 = cb.equal(uw.get("requestReferenceNo"), m.get("requestReferenceNo")); 
				Predicate n9 = cb.equal(uw.get("uwLoginId"),req.getApplicationId()); 
				Predicate n10 = cb.equal(uw.get("uwStatus"), "Y"); 
				Predicate n11 = cb.equal(uw.get("companyId"), req.getInsuranceId()); 
				Predicate n12 = cb.equal(uw.get("productId"), req.getProductId()); 
				Predicate n13 = cb.equal(uw.get("branchCode"), req.getBranchCode()); 
				query.where(n1, n2, n3, n4, n6,n8,n9,n10,n11,n12,n13).orderBy(orderList);
						
						
			} else {
				query.where(n1, n2, n3, n4, n6).orderBy(orderList);
					
			}
			
			// Get Result
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

	@Override
	public List<Tuple> searchBuildingQuote(CopyQuoteReq req, List<String> branches) {
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
				searchQuote = searchDetails(searchKey, searchValue, companyId, loginId, userType, branches, productId);
			} else if ("CustomerReferenceNo".equalsIgnoreCase(searchKey)) {
				searchQuote = searchDetails(searchKey, searchValue, companyId, loginId, userType, branches, productId);
			} else if ("ClientName".equalsIgnoreCase(searchKey)) {
				searchQuote = searchDetails(searchKey, searchValue, companyId, loginId, userType, branches, productId);
			} else if ("QuoteNumber".equalsIgnoreCase(searchKey)) {
				searchQuote = searchDetails(searchKey, searchValue, companyId, loginId, userType, branches, productId);
			} else if ("ChassisNumber".equalsIgnoreCase(searchKey)) {
				searchQuote = searchDetails(searchKey, searchValue, companyId, loginId, userType, branches, productId);
			} else if ("RegistrationNumber".equalsIgnoreCase(searchKey)) {
				searchQuote = searchDetails(searchKey, searchValue, companyId, loginId, userType, branches, productId);
			} else if ("EntryDate".equalsIgnoreCase(searchKey)) {
			//	Date entryDate = sdf.parse(searchValue);
			//	searchValue = sdf.format(entryDate);
				searchQuote = searchDetails(searchKey, searchValue, companyId, loginId, userType, branches, productId);
			}else if ("PolicyNo".equalsIgnoreCase(searchKey)) {
				searchQuote = searchDetails(searchKey, searchValue, companyId, loginId, userType, branches, productId);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return searchQuote;
	}


	public List<Tuple> searchDetails(String searchKey, String searchValue, String companyId, String loginId,
			String userType, List<String> branches, String productId) {
		List<Tuple> customerDetailsList = new ArrayList<Tuple>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		try {

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

			Root<EserviceBuildingDetails> c = query.from(EserviceBuildingDetails.class);
			Root<EserviceCustomerDetails> cus = query.from(EserviceCustomerDetails.class);
			
			query.multiselect(cb.max(cus.get("clientName")).alias("clientName"), cb.count(c).alias("idsCount"),

					cb.max(c.get("requestReferenceNo")).alias("requestReferenceNo"),
					cb.max(c.get("riskId")).alias("riskId"), 
					cb.max(c.get("customerReferenceNo")).alias("customerReferenceNo"),
					cb.max(c.get("productId")).alias("productId"),
					cb.max(c.get("companyId")).alias("companyId"),
					cb.selectCase().when(cb.max(c.get("quoteNo")).isNotNull(), cb.max(c.get("quoteNo")))
					.otherwise(cb.max(c.get("quoteNo"))).alias("quoteNo"),

					cb.selectCase().when(cb.max(c.get("customerId")).isNotNull(), cb.max(c.get("customerId")))
					.otherwise(cb.max(c.get("customerId"))).alias("customerId"),
					cb.max(c.get("policyNo")).alias("policyNo"),
					cb.max(c.get("branchCode")).alias("branchCode"), 
					cb.max(c.get("inbuildConstructType")).alias("inbuildConstructType"),
					cb.max(c.get("buildingFloors")).alias("buildingFloors"),
					cb.max(c.get("outbuildConstructType")).alias("outbuildConstructType"),

					cb.max(c.get("buildingUsageYn")).alias("buildingUsageYn"),
					//cb.max(c.get("buildingPurpose")).alias("buildingPurpose"),
					//cb.max(c.get("buildingPurposeId")).alias("buildingPurposeId"),
					cb.max(c.get("buildingUsageId")).alias("buildingUsageId"), 
					cb.max(c.get("buildingUsageDesc")).alias("buildingUsageDesc"),
					cb.max(c.get("buildingType")).alias("buildingType"),
					cb.max(c.get("buildingOwnerYn")).alias("buildingOwnerYn"),
					cb.max(c.get("buildingOccupationType")).alias("buildingOccupationType"),
				//	cb.max(c.get("apartmentOrBorder")).alias("apartmentOrBorder"),
				//	cb.max(c.get("withoutInhabitantDays")).alias("withoutInhabitantDays"),
					cb.max(c.get("buildingCondition")).alias("buildingCondition"),
				//	cb.max(c.get("buildingBuildYear")).alias("buildingBuildYear"),
					cb.max(c.get("buildingAge")).alias("buildingAge"),
					cb.max(c.get("buildingAreaSqm")).alias("buildingAreaSqm"),
					//cb.max(c.get("buildingSuminsured")).alias("buildingSuminsured"),
					//cb.max(c.get("allriskSuminsured")).alias("allriskSuminsured"), 
					//cb.max(c.get("paDeathSuminsured")).alias("paDeathSuminsured"),
					//cb.max(c.get("paPermanentdisablementSuminsured")).alias("paPermanentdisablementSuminsured"),
					//cb.max(c.get("paTotaldisabilitySumInsured")).alias("paTotaldisabilitySumInsured"), 
					//cb.max(c.get("PaMedicalSuminsured")).alias("PaMedicalSuminsured"),
					//cb.max(c.get("personalIntSuminsured")).alias("personalIntSuminsured"),
					//cb.max(c.get("contentSuminsured")).alias("contentSuminsured"), 
					//cb.max(c.get("workmenCompSuminsured")).alias("workmenCompSuminsured"),
					cb.max(c.get("entryDate")).alias("entryDate"),
					cb.max(c.get("createdBy")).alias("createdBy"),
					cb.max(c.get("status")).alias("status"),
					cb.max(c.get("updatedDate")).alias("updatedDate"),
					cb.max(c.get("updatedBy")).alias("updatedBy"),

					cb.max(c.get("acExecutiveId")).alias("acExecutiveId"),
					cb.max(c.get("applicationId")).alias("applicationId"),
					cb.max(c.get("brokerCode")).alias("brokerCode"),
					cb.max(c.get("subUserType")).alias("subUserType"),
					cb.max(c.get("loginId")).alias("loginId"),
					cb.max(c.get("agencyCode")).alias("agencyCode"),
					cb.max(c.get("policyStartDate")).alias("policyStartDate"),
					cb.max(c.get("policyEndDate")).alias("policyEndDate"),
					cb.max(c.get("policyPeriord")).alias("policyPeriord"),
					cb.max(c.get("currency")).alias("currency"),
					cb.max(c.get("exchangeRate")).alias("exchangeRate"),
					cb.max(c.get("adminLoginId")).alias("adminLoginId"),
					cb.max(c.get("adminRemarks")).alias("adminRemarks"),
					cb.max(c.get("rejectReason")).alias("rejectReason"),
					cb.max(c.get("referalRemarks")).alias("referalRemarks"),
					cb.max(c.get("productDesc")).alias("productDesc"),
					cb.max(c.get("sectionId")).alias("sectionId"),
					cb.max(c.get("sectionDesc")).alias("sectionDesc"),
					cb.max(c.get("branchName")).alias("branchName"),
					cb.max(c.get("companyName")).alias("companyName"),
					cb.max(c.get("oldReqRefNo")).alias("oldReqRefNo"),
					cb.max(c.get("actualPremiumFc")).alias("actualPremiumFc"),
					cb.max(c.get("actualPremiumLc")).alias("actualPremiumLc"),
					cb.max(c.get("overallPremiumLc")).alias("overallPremiumLc"),
					cb.max(c.get("overallPremiumFc")).alias("overallPremiumFc"),
					cb.max(c.get("brokerBranchCode")).alias("brokerBranchCode"),
					cb.max(c.get("brokerBranchName")).alias("brokerBranchName"),
					cb.max(c.get("insuranceType")).alias("insuranceType"),
					cb.max(c.get("commissionType")).alias("commissionType"),
					cb.max(c.get("commissionTypeDesc")).alias("commissionTypeDesc"),
					cb.max(c.get("havepromocode")).alias("havepromocode"),
					cb.max(c.get("promocode")).alias("promocode"),
					cb.max(c.get("occupationType")).alias("occupationType"),
					cb.max(c.get("occupationTypeDesc")).alias("occupationTypeDesc"),
					cb.max(c.get("domesticPackageYn")).alias("domesticPackageYn"), 
					cb.max(c.get("categoryId")).alias("categoryId"),
					cb.max(c.get("categoryDesc")).alias("categoryDesc"),
					cb.max(c.get("bankCode")).alias("bankCode"),
					cb.max(c.get("sourceType")).alias("sourceType"),
					cb.max(c.get("customerCode")).alias("customerCode"),

					cb.max(c.get("bdmCode")).alias("bdmCode"),
					cb.max(c.get("manualReferalYn")).alias("manualReferalYn"),
					//cb.max(c.get("elecEquipSuminsured")).alias("elecEquipSuminsured"),
					//cb.max(c.get("moneySinglecarrySuminsured")).alias("moneySinglecarrySuminsured"),
					//cb.max(c.get("moneyAnnualcarrySuminsured")).alias("moneyAnnualcarrySuminsured"),
					////cb.max(c.get("moneyInsafeSuminsured")).alias("moneyInsafeSuminsured"),
					//cb.max(c.get("fidelityAnyoccuSuminsured")).alias("fidelityAnyoccuSuminsured"),
					//cb.max(c.get("fidelityAnnualSuminsured")).alias("fidelityAnnualSuminsured"),
					//cb.max(c.get("tpliabilityAnyoccuSuminsured")).alias("tpliabilityAnyoccuSuminsured"),
					//cb.max(c.get("empliabilityAnnualSuminsured")).alias("empliabilityAnnualSuminsured"),
					//cb.max(c.get("empliabilityExcessSuminsured")).alias("empliabilityExcessSuminsured"),
					//cb.max(c.get("goodsSinglecarrySuminsured")).alias("goodsSinglecarrySuminsured"),
					//cb.max(c.get("goodsTurnoverSuminsured")).alias("goodsTurnoverSuminsured"), 
					cb.max(c.get("industryId")).alias("industryId"),
					cb.max(c.get("industryDesc")).alias("industryDesc"),
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
					//cb.max(c.get("isFinaceYn")).alias("isFinaceYn"),
					cb.max(c.get("endtCategDesc")).alias("endtCategDesc"),
					cb.max(c.get("endtPremium")).alias("endtPremium"),
					//cb.max(c.get("liabilityOccupationId")).alias("liabilityOccupationId"), 
					//cb.max(c.get("liabilityOccupationDesc")).alias("liabilityOccupationDesc"),
					cb.max(c.get("wallType")).alias("wallType"),
					cb.max(c.get("wallTypeDesc")).alias("wallTypeDesc"),
					cb.max(c.get("roofType")).alias("roofType"),
					cb.max(c.get("roofTypeDesc")).alias("roofTypeDesc"),
					cb.max(c.get("natureOfTradeId")).alias("natureOfTradeId"),
					cb.max(c.get("natureOfTradeDesc")).alias("natureOfTradeDesc"),
					cb.max(c.get("insuranceForId")).alias("insuranceForId"),
					cb.max(c.get("insuranceForDesc")).alias("insuranceForDesc"),
					cb.max(c.get("internalWallType")).alias("internalWallType"),
					cb.max(c.get("internalWallDesc")).alias("internalWallDesc"),

					cb.max(c.get("ceilingType")).alias("ceilingType"),
					cb.max(c.get("ceilingTypeDesc")).alias("ceilingTypeDesc"),
					//cb.max(c.get("stockInTradeSi")).alias("stockInTradeSi"),
					//cb.max(c.get("goodsSi")).alias("goodsSi"),
					//cb.max(c.get("furnitureSi")).alias("furnitureSi"),
					//cb.max(c.get("applianceSi")).alias("applianceSi"),
					//cb.max(c.get("cashValueablesSi")).alias("cashValueablesSi"),
				//	cb.max(c.get("stockLossPercent")).alias("stockLossPercent"),
				//	cb.max(c.get("goodsLossPercent")).alias("goodsLossPercent"),

				//	cb.max(c.get("furnitureLossPercent")).alias("furnitureLossPercent"),
				//	cb.max(c.get("applianceLossPercent")).alias("applianceLossPercent"),
				//	cb.max(c.get("cashValueablesLossPercent")).alias("cashValueablesLossPercent"),
					cb.max(c.get("address")).alias("address"),
					cb.max(c.get("regionCode")).alias("regionCode"),
					cb.max(c.get("regionDesc")).alias("regionDesc"),
					cb.max(c.get("districtCode")).alias("districtCode"),
					cb.max(c.get("districtDesc")).alias("districtDesc"),
					cb.max(c.get("occupiedYear")).alias("occupiedYear"),
				//	cb.max(c.get("showWindows")).alias("showWindow"),

				//	cb.max(c.get("frontDoors")).alias("frontDoors"),
				//	cb.max(c.get("backDoors")).alias("backDoors"),
				//	cb.max(c.get("windowsMaterialId")).alias("windowsMaterialId"),
				//	cb.max(c.get("windowsMaterialDesc")).alias("windowsMaterialDesc"),
				//	cb.max(c.get("doorsMaterialId")).alias("doorsMaterialId"),
				//	cb.max(c.get("doorsMaterialDesc")).alias("doorsMaterialDesc"),
				//	cb.max(c.get("nightLeftDoor")).alias("nightLeftDoor"),
				//	cb.max(c.get("nightLeftDoorDesc")).alias("nightLeftDoorDesc"),
				//	cb.max(c.get("buildingOccupied")).alias("buildingOccupied"),
				//	cb.max(c.get("buildingOccupiedDesc")).alias("buildingOccupiedDesc"),
				//	cb.max(c.get("watchmanGuardHours")).alias("watchmanGuardHours"),
				//	cb.max(c.get("accessibleWindows")).alias("accessibleWindows"),
				//	cb.max(c.get("trapDoors")).alias("trapDoors"),
					//cb.max(c.get("cashInHandDirectors")).alias("cashInHandDirectors"),
					//cb.max(c.get("cashInTransit")).alias("cashInTransit"),
					//cb.max(c.get("cashInHandEmployees")).alias("cashInHandEmployees"),
					//cb.max(c.get("cashInSafe")).alias("cashInSafe"),
					//cb.max(c.get("cashInPremises")).alias("cashInPremises"),
				//	cb.max(c.get("revenueFromStamps")).alias("revenueFromStamps"),
					//cb.max(c.get("moneyInSafeBusiness")).alias("moneyInSafeBusiness"),
					//cb.max(c.get("moneyOutSafeBusiness")).alias("moneyOutSafeBusiness"),
					//cb.max(c.get("moneyInPremises")).alias("moneyInPremises"),
					//cb.max(c.get("moneyInLocker")).alias("moneyInLocker"),
					//cb.max(c.get("machineEquipSi")).alias("machineEquipSi"),
					//cb.max(c.get("plateGlassSi")).alias("plateGlassSi"),
					cb.max(c.get("firstLossPercentId")).alias("firstLossPercentId"),
					cb.max(c.get("firstLossPercent")).alias("firstLossPercent"),
					//cb.max(c.get("accDamageSi")).alias("accDamageSi"),
					//cb.max(c.get("burglarySi")).alias("burglarySi"),
					//cb.max(c.get("powerPlantSi")).alias("powerPlantSi"),
					//cb.max(c.get("elecMachinesSi")).alias("elecMachinesSi"),
					//cb.max(c.get("equipmentSi")).alias("equipmentSi"),
					//cb.max(c.get("generalMachineSi")).alias("generalMachineSi"),
					//cb.max(c.get("manuUnitsSi")).alias("manuUnitsSi"),
					//cb.max(c.get("boilerPlantsSi")).alias("boilerPlantsSi"),
					cb.max(c.get("tiraCoverNoteNo")).alias("tiraCoverNoteNo"),
					cb.max(c.get("indemityPeriod")).alias("indemityPeriod"),
					cb.max(c.get("indemityPeriodDesc")).alias("indemityPeriodDesc"),
					cb.max(c.get("makutiYn")).alias("makutiYn"),
					cb.max(c.get("plateGlassType")).alias("plateGlassType"),
					cb.max(c.get("plateGlassDesc")).alias("plateGlassDesc")

					);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("customerReferenceNo")));


			Predicate n1 = null;
			Predicate n3 = null;
			Predicate n4 = null;
			Predicate n5 = null;
			Predicate n6 = null;

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
//			Predicate n13 = cb.equal(  c.get("sectionId"),  "0");

			Predicate n2 = cb.equal(c.get("companyId"), companyId);

			if ("issuer".equalsIgnoreCase(userType)) {
//				n3 = cb.equal(c.get("applicationId"), loginId);
				Expression<String> e0 = c.get("branchCode");
				n4 = e0.in(branches);
			} else if ("Broker".equalsIgnoreCase(userType) || "User".equalsIgnoreCase(userType)) {
				n3 = cb.equal(c.get("loginId"), loginId);
				Expression<String> e0 = c.get("brokerBranchCode");
				//Expression<String> e0 = c.get("branchCode");
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
			n6=cb.equal(c.get("productId"),productId);
			if ("Broker".equalsIgnoreCase(userType) || "User".equalsIgnoreCase(userType)) {
			query.where(n1,n2,n3,n4,n5,n6)
			.groupBy(c.get("customerReferenceNo"), cus.get("clientName"), c.get("companyId")/*,c.get("riskId")*/,
					c.get("productId"), c.get("branchCode"), c.get("requestReferenceNo"), c.get("quoteNo"),
					c.get("customerId"), c.get("policyStartDate"), c.get("policyEndDate"))
			
			.orderBy(orderList);
			}else {
				query.where(n1,n2,n4,n5,n6)
				.groupBy(c.get("customerReferenceNo"), cus.get("clientName"), c.get("companyId")/*,c.get("riskId")*/,
						c.get("productId"), c.get("branchCode"), c.get("requestReferenceNo"), c.get("quoteNo"),
						c.get("customerId"), c.get("policyStartDate"), c.get("policyEndDate"))
				
				.orderBy(orderList);
			}
			if (searchKey.equalsIgnoreCase("ClientName")) {
				query.where(n1, n2,n4,n5,n6)
				.groupBy(c.get("customerReferenceNo"), cus.get("clientName"), c.get("companyId")/*,c.get("riskId")*/,
						c.get("productId"), c.get("branchCode"), c.get("requestReferenceNo"), c.get("quoteNo"),
						c.get("customerId"), c.get("policyStartDate"), c.get("policyEndDate"))
			
				.orderBy(orderList);
			}
			if (searchKey.equalsIgnoreCase("EntryDate")) {
				query.where(n1,n2,n3,n4,n6)
				.groupBy(c.get("customerReferenceNo"), cus.get("clientName"), c.get("companyId")/*,c.get("riskId")*/,
						c.get("productId"), c.get("branchCode"), c.get("requestReferenceNo"), c.get("quoteNo"),
						c.get("customerId"), c.get("policyStartDate"), c.get("policyEndDate"))
			
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

	@Override
	public CopyQuoteSuccessRes buildingCopyQuote(CopyQuoteReq req, List<String> branches,String loginId) 
	{
		CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
 		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		List<EserviceBuildingDetails> savedataList = new ArrayList<EserviceBuildingDetails>();
		List<EserviceCommonDetails> saveCommonList = new ArrayList<EserviceCommonDetails>();
		List<Tuple> copyQuote = new ArrayList<Tuple>();
		try {
			String searchValue = req.getRequestReferenceNo();
			String searchKey = "RequestReferenceNo";
			String companyId = req.getInsuranceId();
			String userType = req.getUserType();
			String branchCode = "";
			Calendar cal = Calendar.getInstance();
			cal.setTime(new Date());
			cal.add(Calendar.DAY_OF_YEAR, 365);

			

			Date policyStartDate;
			Date policyenddate;
			List<Tuple> list = copyQuoteSearchDetails(searchKey, searchValue, companyId, loginId, userType, branches);

			String refNo =req.getRequestReferenceNo();

			if (list.size() > 0) {
//				String refShortCode = motorService.getListItem(companyId, req.getBranchCode(), "PRODUCT_SHORT_CODE",req.getProductId());
//		        refNo = refShortCode + "-"  + seqNo.generateRefNo() ; 
		     // Generate Seq
	 			SequenceGenerateReq generateSeqReq = new SequenceGenerateReq();
	 		 	generateSeqReq.setInsuranceId(companyId);  
	 		 	generateSeqReq.setProductId(req.getProductId());
	 		 	generateSeqReq.setType("2");
	 		 	generateSeqReq.setTypeDesc("REQUEST_REFERENCE_NO");
	 		 	refNo =  genSeqNoService.generateSeqCall(generateSeqReq);
				for (Tuple data : list) {
					EserviceBuildingDetails savedata = new EserviceBuildingDetails();

						savedata = dozerMapper.map(data.get(0), EserviceBuildingDetails.class);
						
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
//						//	savedata.setBrokerBranchCode(branchCode);
//
//						} else if ("issuer".equalsIgnoreCase(userType)) {
//							savedata.setApplicationId(req.getLoginId());
//							branchCode = req.getBranchCode();
//						//	savedata.setBranchCode(branchCode);
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
						savedata.setVdRefno(null);
						savedataList.add(savedata);
						repo.saveAndFlush(savedata);
					}
				
				// Save Human 
				List<Tuple> list2 = copyCommonQuoteSearchDetails(searchKey, searchValue, companyId, loginId, userType, branches);
				for (Tuple data : list2) {
					EserviceCommonDetails savedata2 = new EserviceCommonDetails();
					savedata2 = dozerMapper.map(data.get(0), EserviceCommonDetails.class);

					savedata2.setEntryDate(new Date());
					savedata2.setCreatedBy(req.getLoginId());
					savedata2.setUpdatedBy(req.getLoginId());
					savedata2.setUpdatedDate(new Date());
					savedata2.setRequestReferenceNo(refNo);
					savedata2.setOldReqRefNo(req.getRequestReferenceNo());
					if (req.getUserType().equalsIgnoreCase("Broker")
							|| (req.getUserType().equalsIgnoreCase("User"))) {
						branchCode = req.getBranchCode();
						savedata2.setApplicationId("1");
						// savedata.setBrokerBranchCode(branchCode);

					} else if ("issuer".equalsIgnoreCase(userType)) {
						savedata2.setApplicationId(req.getLoginId());
						branchCode = req.getBranchCode();
						// savedata.setBranchCode(branchCode);
					}
					savedata2.setPolicyStartDate(null);		
					savedata2.setPolicyEndDate(null);
					savedata2.setActualPremiumFc(BigDecimal.ZERO);
					savedata2.setActualPremiumLc(BigDecimal.ZERO);
					savedata2.setOverallPremiumFc(BigDecimal.ZERO);
					savedata2.setOverallPremiumLc(BigDecimal.ZERO);
					savedata2.setQuoteNo("");
					savedata2.setStatus("Y");
					savedata2.setEndorsementDate(null);
					savedata2.setEndorsementEffdate(null);
					savedata2.setEndorsementRemarks(null);
					savedata2.setEndorsementType(null);
					savedata2.setEndorsementTypeDesc(null);
					savedata2.setEndtCategDesc(null);
					savedata2.setEndtCount(null);
					savedata2.setEndtPremium(null);
					savedata2.setEndtPrevPolicyNo(null);
					savedata2.setEndtPrevQuoteNo(null);
					savedata2.setEndtStatus(null);
					savedata2.setFinalizeYn("N");
					savedata2.setApplicationId(req.getApplicationId());
					if(StringUtils.isNotBlank(req.getLoginId())) {
						savedata2.setLoginId(req.getLoginId());
						}
						savedata2.setSubUserType(req.getSubUserType());
					savedata2.setPolicyNo(null);
					savedata2.setVatPremium(null);
					savedata2.setCdRefno(null);
					savedata2.setMsRefno(null);
					savedata2.setVdRefNo(null);
					savedata2.setSourceTypeId(null);
					saveCommonList.add(savedata2);
					eserCommonRepo.saveAndFlush(savedata2);
				}
				
				// Save Section
				List<Tuple> list3 = copySectionQuoteSearchDetails(searchKey, searchValue, companyId, loginId, userType,branches);
				if (list3 != null && list3.size() > 0) {
					for (Tuple data : list3) {
						EserviceSectionDetails savedata3 = new EserviceSectionDetails();
						savedata3 = dozerMapper.map(data.get(0), EserviceSectionDetails.class);

						savedata3.setEntryDate(new Date());
						savedata3.setCreatedBy(req.getLoginId());
						savedata3.setUpdatedBy(req.getLoginId());
						savedata3.setUpdatedDate(new Date());
						savedata3.setRequestReferenceNo(refNo);
						savedata3.setQuoteNo("");
						savedata3.setStatus("Y");
						savedata3.setEndorsementDate(null);
						savedata3.setEndorsementEffdate(null);
						savedata3.setEndorsementRemarks(null);
						savedata3.setEndorsementType(null);
						savedata3.setEndorsementTypeDesc(null);
						savedata3.setEndtCategDesc(null);
						savedata3.setEndtCount(null);
						savedata3.setEndtPremium(null);
						savedata3.setEndtPrevPolicyNo(null);
						savedata3.setEndtPrevQuoteNo(null);
						savedata3.setEndtStatus(null);
						savedata3.setPolicyNo(null);
						eserSecRepo.saveAndFlush(savedata3);
					}

				}
				if(savedataList!=null && !savedataList.isEmpty())
				{
					NonMotorPolicyReq policyReq = new NonMotorPolicyReq ();
					policyReq.setCreatedBy(savedataList.get(0).getLoginId());
					policyReq.setCompanyId(savedataList.get(0).getCompanyId());
					policyReq.setProductId(savedataList.get(0).getProductId());
					policyReq.setPolicyStartDate(savedataList.get(0).getEntryDate());
					policyReq.setPolicyEndDate(cal.getTime());
					policyReq.setBranchCode(savedataList.get(0).getBranchCode());
					policyReq.setExchangeRate(savedataList.get(0).getExchangeRate()+"");
					policyReq.setCustomerReferenceNo(savedataList.get(0).getCustomerReferenceNo());
					policyReq.setProductName(savedataList.get(0).getProductDesc());
					NonMotorBrokerReq brokerReq= new NonMotorBrokerReq ();
					brokerReq.setApplicationId(savedataList.get(0).getApplicationId());
					brokerReq.setBdmCode(savedataList.get(0).getBdmCode());
					brokerReq.setBrokerCode(savedataList.get(0).getBrokerCode());
					brokerReq.setAgencyCode(savedataList.get(0).getAgencyCode());
					brokerReq.setBrokerBranchCode(savedataList.get(0).getBrokerBranchCode());
					brokerReq.setSourceTypeId(savedataList.get(0).getSourceTypeId());
					brokerReq.setSourceType(savedataList.get(0).getSourceType());
					brokerReq.setBdmName(savedataList.get(0).getBdmName());
					insertHomePositionMaster(refNo,policyReq,brokerReq,savedataList.size());
				}
				else
				{
					NonMotorPolicyReq policyReq = new NonMotorPolicyReq ();
					policyReq.setCreatedBy(saveCommonList.get(0).getLoginId());
					policyReq.setCompanyId(saveCommonList.get(0).getCompanyId());
					policyReq.setProductId(saveCommonList.get(0).getProductId());
					policyReq.setPolicyStartDate(saveCommonList.get(0).getEntryDate());
					policyReq.setPolicyEndDate(cal.getTime());
					policyReq.setBranchCode(saveCommonList.get(0).getBranchCode());
					policyReq.setExchangeRate(saveCommonList.get(0).getExchangeRate()+"");
					policyReq.setCustomerReferenceNo(saveCommonList.get(0).getCustomerReferenceNo());
					policyReq.setProductName(saveCommonList.get(0).getProductDesc());
					NonMotorBrokerReq brokerReq= new NonMotorBrokerReq ();
					brokerReq.setApplicationId(saveCommonList.get(0).getApplicationId());
					brokerReq.setBdmCode(saveCommonList.get(0).getBdmCode());
					brokerReq.setBrokerCode(saveCommonList.get(0).getBrokerCode());
					brokerReq.setAgencyCode(saveCommonList.get(0).getAgencyCode());
					brokerReq.setBrokerBranchCode(saveCommonList.get(0).getBrokerBranchCode());
					brokerReq.setSourceTypeId(saveCommonList.get(0).getSourceTypeId());
					brokerReq.setSourceType(saveCommonList.get(0).getSourceType());
					brokerReq.setBdmName(saveCommonList.get(0).getBdmName());
					insertHomePositionMaster(refNo,policyReq,brokerReq,saveCommonList.size());
				}
				}
		
			
		//	res.setResponse("Successfully Updated");
			res.setRequestReferenceNo(refNo);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return res;
	}
	
	private HomePositionMaster insertHomePositionMaster(String requestReferenceNo, NonMotorPolicyReq policyReq,
			NonMotorBrokerReq brokerReq, int size) {
		HomePositionMaster home = null;
		long a = 99999;
		boolean isSameDate = false;
		try {
			home = homePosistionRepo.findByRequestReferenceNoOrderByNoOfVehiclesAsc(requestReferenceNo);
			if (home != null) {
				if (StringUtils.isBlank(home.getQuoteNo())) {
					Date existingStart = home.getInceptionDate();
					Date requestStart = policyReq.getPolicyStartDate();

					isSameDate = existingStart != null && requestStart != null
							&& existingStart.toInstant().atZone(ZoneId.systemDefault()).toLocalDate()
									.equals(requestStart.toInstant().atZone(ZoneId.systemDefault()).toLocalDate());
				} else {
					isSameDate = true;
				}
			}

			if (!isSameDate) {
				home = new HomePositionMaster();
				home.setNoOfVehicles(size);
				home.setSectionId(0);
				home.setAmendId(0);
				home.setApplicationNo(a);
				home.setApplicationId(StringUtils.isBlank(brokerReq.getApplicationId()) ? "0" : brokerReq.getApplicationId());
				home.setBdmCode(StringUtils.isBlank(brokerReq.getBdmCode()) ? "0" : brokerReq.getBdmCode());
				home.setBdmName(brokerReq.getBdmName());
				home.setLoginId(StringUtils.isBlank(policyReq.getCreatedBy()) ? null : policyReq.getCreatedBy());
				home.setCustomerId("99999");
				home.setRequestReferenceNo(requestReferenceNo);
				home.setCompanyId(StringUtils.isBlank(policyReq.getCompanyId()) ? null : policyReq.getCompanyId());
				home.setProductId(safeParseInt(policyReq.getProductId()));
				home.setBrokerCode(StringUtils.isBlank(brokerReq.getBrokerCode()) ? null : brokerReq.getBrokerCode());
				home.setAgencyCode(safeParseInt(brokerReq.getAgencyCode()));
				home.setEntryDate(policyReq.getPolicyStartDate() == null ? null : policyReq.getPolicyStartDate());
				home.setInceptionDate(policyReq.getPolicyStartDate() == null ? null : policyReq.getPolicyStartDate());
				home.setEffectiveDate(policyReq.getPolicyStartDate() == null ? null : policyReq.getPolicyStartDate());
				home.setExpiryDate(policyReq.getPolicyEndDate() == null ? null : policyReq.getPolicyEndDate());
				home.setBranchCode(StringUtils.isBlank(policyReq.getBranchCode()) ? null : policyReq.getBranchCode());
				home.setExchangeRate(safeParseBigDecimal(policyReq.getExchangeRate()));
				home.setCustomerCode(StringUtils.isBlank(policyReq.getCustomerReferenceNo()) ? "99999" : policyReq.getCustomerReferenceNo());
				home.setProductName(policyReq.getProductName());
				home.setBrokerBranchCode(StringUtils.isBlank(brokerReq.getBrokerBranchCode()) ? null : brokerReq.getBrokerBranchCode());
				home.setStatus("Y");
				home.setSourceTypeId(StringUtils.isBlank(brokerReq.getSourceTypeId()) ? null : brokerReq.getSourceTypeId());
				home.setSourceType(StringUtils.isBlank(brokerReq.getSourceType()) ? null : brokerReq.getSourceType());

				homePosistionRepo.saveAndFlush(home);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception Is HomePositionMaster ---> " + e.getMessage());
			return null;
		}
		return home;
	}

	/**
	 * Safely parses an Integer from a String, trimming whitespace/newlines first.
	 * Returns null (instead of throwing) for blank or unparsable input, and logs a warning.
	 */
	private Integer safeParseInt(String value) {
		if (StringUtils.isBlank(value)) {
			return null;
		}
		try {
			return Integer.valueOf(value.trim());
		} catch (NumberFormatException e) {
			log.warn("Could not parse integer from value: [{}]", value);
			return null;
		}
	}

	/**
	 * Safely parses a BigDecimal from a String, trimming whitespace/newlines first.
	 * Returns null (instead of throwing) for blank or unparsable input, and logs a warning.
	 */
	private BigDecimal safeParseBigDecimal(String value) {
		if (StringUtils.isBlank(value)) {
			return null;
		}
		try {
			return new BigDecimal(value.trim());
		} catch (NumberFormatException e) {
			log.warn("Could not parse BigDecimal from value: [{}]", value);
			return null;
		}
	}
	@Override
	public List<ListItemValue> geBuildingCoptyQuotetListItem(CopyQuoteDropDownReq req, String itemType) {
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
			
			if(itemType.equalsIgnoreCase("COPY_QUOTE_BY_BUILDING") || itemType.equalsIgnoreCase("COPY_QUOTE_BY_COMMON"))
					query.where(n1, n2, n3, n8, n9, n10).orderBy(orderList);
			else
				query.where(n1, n2, n3, n4, n9, n10).orderBy(orderList);
			
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
	public List<Tuple> copyQuoteSearchDetails(String searchKey, String searchValue, String companyId, String loginId,
			String userType, List<String> branches) {
		List<Tuple> customerDetailsList = new ArrayList<Tuple>();
		try {

				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

				Root<EserviceBuildingDetails> c = query.from(EserviceBuildingDetails.class);
				Root<EserviceCustomerDetails> cus = query.from(EserviceCustomerDetails.class);
				
				query.multiselect(c,
						cus.get("clientName").alias("clientName"));

				// Order By
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.asc(c.get("customerReferenceNo")));

				Predicate n1 = null;
	//			Predicate n3 = null;
	//			Predicate n4 = null;
				Predicate n5 = null;

				// Where
				if (searchKey.equalsIgnoreCase("RequestReferenceNo")) {
					n1 = cb.equal(cb.lower(c.get("requestReferenceNo")), searchValue);
				}

//				Predicate n2 = cb.equal(c.get("companyId"), companyId);
//
//				if ("issuer".equalsIgnoreCase(userType)) {
//					n3 = cb.equal(c.get("applicationId"), loginId);
//					Expression<String> e0 = c.get("branchCode");
//					n4 = e0.in(branches);
//				} else if ("Broker".equalsIgnoreCase(userType) || "User".equalsIgnoreCase(userType)) {
//					n3 = cb.equal(c.get("loginId"), loginId);
//				//	Expression<String> e0 = c.get("brokerBranchCode");
//				Expression<String> e0 = c.get("branchCode");
//					n4 = e0.in(branches);
//				}
//				
				n5 = cb.equal(c.get("customerReferenceNo"), cus.get("customerReferenceNo"));
		//		query.where(n1,n2,n3,n4,n5).orderBy(orderList);
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
	@Override
	public CopyQuoteSuccessRes buildingEndt(CopyQuoteReq req, List<String> branches, String loginId) {
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
//			String refShortCode = getListItem(req.getInsuranceId() ,req.getBranchCode(), "PRODUCT_SHORT_CODE",req.getProductId());
//			String refNo=refShortCode +"-"  +seqNo.generateRefNo();
//			String quoteNo  = "Q"+ generateQuoteNo();
//			String customerId = "C-" + generateCustId();
//			String custRefNo = "Cust-" +   generateCustRefNo() ; 
            //Copy Quote E service Building Details
			EserviceBuildingDetails savedata=eserviceBuildingCopyquote(req,refNo,branches,loginId,customerId,quoteNo,custRefNo);
			res.setCommonResponse(savedata);
			res.setQuoteNo(quoteNo);
		
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return res;
	}
	
	
	public List<Tuple> copyCommonQuoteSearchDetails(String searchKey, String searchValue, String companyId, String loginId,
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
//			Predicate n2 = cb.equal(c.get("companyId"), companyId);
//
//			if ("issuer".equalsIgnoreCase(userType)) {
//				n3 = cb.equal(c.get("applicationId"), loginId);
//				Expression<String> e0 = c.get("branchCode");
//				n4 = e0.in(branches);
//			} else if ("Broker".equalsIgnoreCase(userType) || "User".equalsIgnoreCase(userType)) {
//				n3 = cb.equal(c.get("loginId"), loginId);
//				//Expression<String> e0 = c.get("brokerBranchCode");
//				Expression<String> e0 = c.get("branchCode");
//				n4 = e0.in(branches);
//			}
			
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

	public List<Tuple> copySectionQuoteSearchDetails(String searchKey, String searchValue, String companyId, String loginId,
			String userType, List<String> branches) {
		List<Tuple> customerDetailsList = new ArrayList<Tuple>();
		try {

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

			Root<EserviceSectionDetails> c = query.from(EserviceSectionDetails.class);
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
//			Predicate n2 = cb.equal(c.get("companyId"), companyId);
//
//			if ("issuer".equalsIgnoreCase(userType)) {
//				n3 = cb.equal(c.get("applicationId"), loginId);
//				Expression<String> e0 = c.get("branchCode");
//				n4 = e0.in(branches);
//			} else if ("Broker".equalsIgnoreCase(userType) || "User".equalsIgnoreCase(userType)) {
//				n3 = cb.equal(c.get("loginId"), loginId);
//				//Expression<String> e0 = c.get("brokerBranchCode");
//				Expression<String> e0 = c.get("branchCode");
//				n4 = e0.in(branches);
//			}
			
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

public synchronized String generateCustRefNo() {
      try {
   	   SeqCustrefno entity;
           entity = custRefRepo.save(new SeqCustrefno());          
           return String.format("%05d",entity.getCustReferenceNo()) ;
       } catch (Exception e) {
			e.printStackTrace();
			log.info( "Exception is ---> " + e.getMessage());
           return null;
       }
      
}

public synchronized String generateQuoteNo() {
	try {
		SeqQuoteno entity;
		entity = quoteNoRepo.save(new SeqQuoteno());
		return String.format("%05d", entity.getQuoteNo());
	} catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is ---> " + e.getMessage());
		return null;
	}

}

public synchronized String generateCustId() {
	try {
		SeqCustid entity;
		entity = custIdRepo.save(new SeqCustid());
		return String.format("%05d", entity.getCustId());
	} catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is ---> " + e.getMessage());
		return null;
	}

}
			
// Eservice Motor Copy Quote
public EserviceBuildingDetails eserviceBuildingCopyquote(CopyQuoteReq req, String refNo, List<String> branches,
		String loginId, String customerId, String quoteNo, String custRefNo) {
	DozerBeanMapper dozerMapper = new DozerBeanMapper();
	EserviceBuildingDetails savedata = new EserviceBuildingDetails();
	try {
		String userType = req.getUserType();
		String branchCode = "";

		List<EserviceBuildingDetails> motor = null;
		Integer count=0;
		List<Object> list=getMasterTableCount(req.getPolicyNo());
		if (list.size() > 0) {
			count = list.size();
		}
		String prevPolicyNo = null;
		String prevQuoteNo = null;
		String newRequestNo = null;
		String newQuoteNo = null;
		String newCustRefNo = null;
		String newCustId = null;
		Integer preEndtId=null;
		long pendingcount = 0;
		
		
		if (count > 0) {
			List<EserviceBuildingDetails> motors = repo.findByOriginalPolicyNoOrderBySectionId(req.getPolicyNo());
			motors.sort(new Comparator<EserviceBuildingDetails>() {
				@Override
				public int compare(EserviceBuildingDetails o1, EserviceBuildingDetails o2) {
					// TODO Auto-generated method stub
					return o1.getEndtCount().compareTo(o2.getEndtCount());
				}
			}.reversed());
			
//			EserviceBuildingDetails motors=buildingDetails.get(0);
//			pendingcount = "P".equalsIgnoreCase(motors.getEndtStatus());
			pendingcount = motors.stream().filter(m -> m.getEndtStatus().equals("P")).count();
			if (pendingcount > 0) {
				 List<EserviceBuildingDetails> pendingData = motors.stream().filter(m->m.getEndtStatus().equals("P")).collect(Collectors.toList());
				if (!pendingData.get(0).getEndorsementType().equals(Integer.valueOf(req.getEndtTypeId()))) {
					deletePreviousEndo(req, pendingData);
					count--;
					pendingcount = 0;
				}
			}
		}
		if (count > 0) {
			List<EserviceBuildingDetails> motors = repo.findByOriginalPolicyNoOrderBySectionId(req.getPolicyNo());
			
			// Compare
			motors.sort(new Comparator<EserviceBuildingDetails>() {
				@Override
				public int compare(EserviceBuildingDetails o1, EserviceBuildingDetails o2) {
					// TODO Auto-generated method stub
					return o1.getEndtCount().compareTo(o2.getEndtCount());
				}
			}.reversed());

			pendingcount = motors.stream().filter(m -> m.getEndtStatus().equals("P")).count();
			if (pendingcount > 0) {
				List<EserviceBuildingDetails> pendingData = motors.stream().filter(m -> m.getEndtStatus().equals("P"))
						.collect(Collectors.toList());
				motor = pendingData;
				prevPolicyNo = motor.get(0).getEndtPrevPolicyNo();
				prevQuoteNo = motor.get(0).getEndtPrevQuoteNo();
				newRequestNo = motor.get(0).getRequestReferenceNo();
				newQuoteNo = motor.get(0).getQuoteNo();
				newCustRefNo = motor.get(0).getCustomerReferenceNo();
				newCustId = motor.get(0).getCustomerId();
				preEndtId =motor.get(0).getEndorsementType();
				count--;
			} else {
				motor = motors;

				if (motors.size() > 1) {
					prevPolicyNo = motors.get(0).getPolicyNo();
					prevQuoteNo = motors.get(0).getQuoteNo();
					preEndtId =motor.get(0).getEndorsementType();
				} else {
					//prevPolicyNo = req.getPolicyNo();
					prevPolicyNo =motor.get(0).getPolicyNo();
					prevQuoteNo = motor.get(0).getQuoteNo();
					preEndtId =motor.get(0).getEndorsementType();
				}
			}

		} else {
			motor = repo.findByPolicyNoAndStatus(req.getPolicyNo(), "P");
			prevPolicyNo = req.getPolicyNo();
			prevQuoteNo = motor.get(0).getQuoteNo();
			preEndtId =motor.get(0).getEndorsementType();
		}

		if (pendingcount == 0) {
			newRequestNo = refNo;
			newCustRefNo = custRefNo;
			newCustId = customerId;
		}

//					List<Tuple> list = copyQuoteSearchDetails(searchKey, searchValue, companyId, loginId, userType,
//							branches);
		List<EserviceBuildingDetails> ebuilding = repo.findByQuoteNoAndStatusNotOrderByRiskIdAsc(prevQuoteNo,"D");
		List<BuildingRiskDetails> buildingRisk = buildRiskRepo.findByQuoteNoAndStatusNotOrderByRiskIdAsc(prevQuoteNo,"D");
		
		List<EserviceBuildingDetails> motors = ebuilding.stream()
			    .filter(m -> buildingRisk.stream()
			        .anyMatch(risk -> m.getRiskId().equals(risk.getRiskId()) 
			                        && m.getRequestReferenceNo().equals(risk.getRequestReferenceNo())
			                        && m.getLocationId().equals(risk.getLocationId())
			                        && m.getSectionId().equals(risk.getSectionId())
			        		))
			    .collect(Collectors.toList());
		++count;
		
		if (motors.size() > 0) {
			for (EserviceBuildingDetails data : motors) {
				BuildingRiskDetails matchingRisk = buildingRisk.stream()
						.filter(risk -> risk.getRiskId().equals(data.getRiskId())
								&& risk.getRequestReferenceNo().equals(data.getRequestReferenceNo())
								&& risk.getLocationId().equals(data.getLocationId())
								&& risk.getSectionId().equals(data.getSectionId())
								&& risk.getCoverId().equals(data.getCoverId()))
						.findFirst().orElse(null);

				if (matchingRisk != null) {

					EndtTypeMaster entMaster = ratingutil.getEndtMasterData(req.getInsuranceId(), req.getProductId(),
							req.getEndtTypeId());
					/*
					 * endtTypeRepo
					 * .findByCompanyIdAndProductIdAndStatusAndEndtTypeIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqual(
					 * req.getInsuranceId(), Integer.parseInt(req.getProductId()), "Y",
					 * Integer.parseInt(req.getEndtTypeId()), new Date(), new Date());
					 */
					savedata = dozerMapper.map(data, EserviceBuildingDetails.class);
					savedata.setEntryDate(new Date());
					savedata.setCreatedBy(req.getLoginId());
					savedata.setUpdatedBy(req.getLoginId());
					savedata.setUpdatedDate(new Date());
					savedata.setRequestReferenceNo(newRequestNo);
					if("851".equalsIgnoreCase(req.getEndtTypeId()) )
					{
						savedata.setEndtoptd("Y");					
					}
					savedata.setCustomerReferenceNo(data.getCustomerReferenceNo());
					savedata.setCustomerId(newCustId);
					savedata.setOldReqRefNo(req.getRequestReferenceNo());
					savedata.setActualPremiumFc(BigDecimal.ZERO);
					savedata.setActualPremiumLc(BigDecimal.ZERO);
					savedata.setOverallPremiumFc(BigDecimal.ZERO);
					savedata.setOverallPremiumLc(BigDecimal.ZERO);
					if (pendingcount == 0) {
						savedata.setQuoteNo(quoteNo);
					} else {
						savedata.setQuoteNo(newQuoteNo);
					}
					savedata.setOriginalPolicyNo(req.getPolicyNo());
					savedata.setEndorsementDate(new Date());
					savedata.setEndorsementRemarks(req.getEndtRemarks());
					savedata.setEndorsementEffdate(req.getEndtEffectiveDate());
					savedata.setEndtPrevPolicyNo(prevPolicyNo);
					// savedata.setEndtPrevPolicyNo(req.getPolicyNo()+"-"+ count);
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
					if (req.getLoginId() == null || StringUtils.isBlank(req.getLoginId())) {
						savedata.setLoginId(data.getLoginId());
					} else {
						savedata.setLoginId(req.getLoginId());
					}
					savedata.setSubUserType(req.getSubUserType());

					// Source Type Condtion
					String sourceType = savedata.getSourceType();
					if (StringUtils.isNotBlank(savedata.getApplicationId())
							///&& !"1".equalsIgnoreCase(savedata.getApplicationId())
							) {
						List<ListItemValue> sourcerTypes = genSeqNoService.getSourceTypeDropdown(
								savedata.getCompanyId(), savedata.getBranchCode(), "SOURCE_TYPE");
						List<ListItemValue> acitveSourcerTypes = sourcerTypes.stream().filter(
								o -> "Y".equalsIgnoreCase(o.getStatus()) && o.getItemValue().contains(sourceType))
								.collect(Collectors.toList());
						savedata.setSourceType(acitveSourcerTypes.size() > 0 ? acitveSourcerTypes.get(0).getItemValue()
								: savedata.getSourceType());
						savedata.setSourceTypeId(acitveSourcerTypes.size() > 0 ? acitveSourcerTypes.get(0).getItemCode()
								: savedata.getSourceTypeId());
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
		}

	}
		System.out.println("*************EserviceBuildingDetails************");
		System.out.println("Old QUOTE NO:"+prevQuoteNo);
		System.out.println("Old Customer Id:"+customerId);
		System.out.println("Old Reference No:"+refNo);
		System.out.println("QUOTE NO:"+quoteNo);
		System.out.println("New Customer Id:"+newCustId);
		System.out.println("Reference No:"+newRequestNo);
		System.out.println("prevPolicyNo:"+prevPolicyNo);
		System.out.println("PreQuoteNo:"+prevQuoteNo);
		System.out.println("OriginalPoicyNo:"+req.getPolicyNo());
		System.out.println("Policy No:"+req.getPolicyNo()+"-"+count);
		System.out.println("**********************************************");
		if (pendingcount == 0) {
			// Copy Quote Home Position Master
			homeEndoCopyQuote(req, refNo, customerId, quoteNo, loginId, prevPolicyNo, prevQuoteNo, count, custRefNo);

			// Copy Quote Personal Info
			personolInfoEndoCopyQuote(req, customerId, prevPolicyNo, prevQuoteNo, count, custRefNo);

			// Copy Quote Policy Cover Data
		//	policyCoverDataEndocopyQuote(req, refNo, quoteNo, loginId, prevPolicyNo, prevQuoteNo, count,preEndtId);

			// Copy COVER_DOCUMENT_UPLOAD_DETAILS
			coverDocumentUploadDetailsEndoCopyquote(req, refNo, quoteNo, customerId, loginId, prevPolicyNo, prevQuoteNo,
					count);

//			// Copy ESERVICE_CUSTOMER_DETAILS
//			eserviceCustDetailsEndoCopyquote(req, refNo, quoteNo, customerId, loginId, prevPolicyNo, prevQuoteNo, count,
//					custRefNo);

			// Copy ESERVICE_COMMON_DETAILS
			eserviceCommonDetailsEndoCopyquote(req, refNo, quoteNo, customerId, loginId, prevPolicyNo, prevQuoteNo,
					count, custRefNo);

			// Copy ESERVICE_SECTION_DETAILS
			eserviceSectionDetailsEndoCopyquote(req, refNo, quoteNo, customerId, loginId,prevPolicyNo,prevQuoteNo,count,custRefNo);
			
			// Copy Section Data Details
			sectionDataDetailsEndoCopyquote(req, refNo, quoteNo, customerId, loginId,prevPolicyNo,prevQuoteNo,count,custRefNo);
			// Copy BUILDING_DETAILS
			buildingDetailsEndoCopyquote(req, refNo, quoteNo, customerId, loginId,prevPolicyNo,prevQuoteNo,count,custRefNo);
			// Copy Common Data Details
			commonDataDetailsEndoCopyquote(req, refNo, quoteNo, customerId, loginId,prevPolicyNo,prevQuoteNo,count,custRefNo);
			
			// Copy PERSONAL_ACCIDENT/ProductEmployeeDetails
			productEmpDetailsEndoCopyquote(req, refNo, quoteNo, customerId, loginId,prevPolicyNo,prevQuoteNo,count,custRefNo);
		
			// Copy CONDENT_AND_ALLRISK
			contentAndRiskEndoCopyquote(req, refNo, quoteNo, customerId, loginId,prevPolicyNo,prevQuoteNo,count,custRefNo);
			// Copy BUILDING_RISK_DETAILS
			buildingRiskDetailsCopyQuote(req, refNo, quoteNo, customerId, loginId,prevPolicyNo,prevQuoteNo,count,custRefNo);

		}
		// res.setRequestReferenceNo(newRequestNo);
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
				//List<EserviceMotorDetails> list = new ArrayList<EserviceMotorDetails>();
				// Find Latest Record
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Object> query = cb.createQuery(Object.class);
				//Find all
				Root<EserviceBuildingDetails> b = query.from(EserviceBuildingDetails.class);
				// Select
				query.multiselect(b.get("policyNo").alias("policyNo"));
							
				Predicate n1 = cb.equal(b.get("originalPolicyNo"),policyNo);
				query.where(n1).groupBy(b.get("policyNo"));
				
				// Get Result
				TypedQuery<Object> result = em.createQuery(query);
				list = result.getResultList();
				
			}
			catch(Exception e) {
				e.printStackTrace();
				log.info(e.getMessage());
			}
			return list;
		}

//Building Risk Details
private CopyQuoteSuccessRes buildingRiskDetailsCopyQuote(CopyQuoteReq req, String refNo, String quoteNo, String customerId,String loginId, String prevPolicyNo, String prevQuoteNo, Integer count, String custRefNo) {
	CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
	BuildingRiskDetails savedata = new BuildingRiskDetails();
	DozerBeanMapper dozerMapper = new DozerBeanMapper();
	try {
		EndtTypeMaster entMaster =ratingutil.getEndtMasterData(req.getInsuranceId(),req.getProductId(),req.getEndtTypeId()); 
				/*endtTypeRepo.findByCompanyIdAndProductIdAndStatusAndEndtTypeIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqual(
				req.getInsuranceId(), Integer.parseInt(req.getProductId()), "Y",
				Integer.parseInt(req.getEndtTypeId()), new Date(), new Date());*/
		List<BuildingRiskDetails> buildingRiskData=buildRiskRepo.findByQuoteNoAndStatusNotOrderByRiskIdAsc(prevQuoteNo,"D");
		if (buildingRiskData!=null) {
			for(BuildingRiskDetails data :buildingRiskData) {
				savedata = dozerMapper.map(data, BuildingRiskDetails.class);
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
				savedata.setApplicationId(req.getApplicationId());
				if(req.getLoginId()==null || StringUtils.isBlank(req.getLoginId())) {
					savedata.setLoginId(data.getLoginId());
				}else {
					savedata.setLoginId(req.getLoginId());
				}
				savedata.setSubUserType(req.getSubUserType());
				buildRiskRepo.saveAndFlush(savedata);
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
	CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
	private CopyQuoteSuccessRes buildingDetailsEndoCopyquote(CopyQuoteReq req, String refNo, String quoteNo, String customerId,String loginId, String prevPolicyNo, String prevQuoteNo, Integer count, String custRefNo) {
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

//Content and Risk
private CopyQuoteSuccessRes contentAndRiskEndoCopyquote(CopyQuoteReq req, String refNo, String quoteNo, String customerId,String loginId, String prevPolicyNo, String prevQuoteNo, Integer count, String custRefNo) {
	CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
	ContentAndRisk savedata = new ContentAndRisk();
	DozerBeanMapper dozerMapper = new DozerBeanMapper();
	try {
		EndtTypeMaster entMaster =ratingutil.getEndtMasterData(req.getInsuranceId(),req.getProductId(),req.getEndtTypeId()); /*endtTypeRepo.findByCompanyIdAndProductIdAndStatusAndEndtTypeIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqual(
				req.getInsuranceId(), Integer.parseInt(req.getProductId()), "Y",
				Integer.parseInt(req.getEndtTypeId()), new Date(), new Date());*/
		
		List<ContentAndRisk> content = contentRiskRepo.findByQuoteNoOrderByRiskIdAsc(prevQuoteNo);
		if (content != null && content.size() > 0) {
			for (ContentAndRisk data : content) {
				savedata = dozerMapper.map(data, ContentAndRisk.class);
				savedata.setEntryDate(new Date());
				savedata.setRequestReferenceNo(refNo);
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
				contentRiskRepo.saveAndFlush(savedata);
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

		List<CommonDataDetails> eserSec = commonDataRepo.findByQuoteNoAndStatusNot(prevQuoteNo,"D");
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
				if(req.getLoginId()==null || StringUtils.isBlank(req.getLoginId())) {
					savedata.setLoginId(data.getLoginId());
				}else {
					savedata.setLoginId(req.getLoginId());
				}
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
//Personal Accident And Personal Indem
private CopyQuoteSuccessRes productEmpDetailsEndoCopyquote(CopyQuoteReq req, String refNo, String quoteNo, String customerId,String loginId, String prevPolicyNo, String prevQuoteNo, Integer count, String custRefNo) {
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
		//List<EserviceSectionDetails> eserSec = eserSecRepo.findByRequestReferenceNoOrderBySectionIdAsc(eserSec1.get(0).getRequestReferenceNo());
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
				
				SectionDataDetails matchingRisk = secList.stream()
						.filter(risk -> risk.getRiskId().equals(data.getRiskId())
								&& risk.getRequestReferenceNo().equals(data.getRequestReferenceNo())
								&& risk.getLocationId().equals(data.getLocationId())
								&& risk.getSectionId().equals(data.getSectionId()))
						.findFirst().orElse(null);

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
//				savedata.setSumInsured(matchingRisk.getSumInsured()==null ? BigDecimal.ZERO : matchingRisk.getSumInsured() );
//				savedata.setSumInsuredLc(matchingRisk.getSumInsuredLc()==null ? BigDecimal.ZERO : matchingRisk.getSumInsuredLc() );
////				savedata.setEmiPremium(BigDecimal.ONE);
//				savedata.setVatPremium(matchingRisk.getVatPremium()==null ? BigDecimal.ZERO : matchingRisk.getVatPremium() );
//				savedata.setEndtPremium(matchingRisk.getEndtPremium()==null ? 0 : matchingRisk.getEndtPremium() );
//				savedata.setEndtVatPremium(matchingRisk.getEndtVatPremium()==null ? BigDecimal.ZERO : matchingRisk.getEndtVatPremium() );
//				savedata.setActualPremiumFc(matchingRisk.getActualPremiumFc()==null ? BigDecimal.ZERO : matchingRisk.getActualPremiumFc() );
//				savedata.setActualPremiumLc(matchingRisk.getActualPremiumLc()==null ? BigDecimal.ZERO : matchingRisk.getActualPremiumLc() );
//				savedata.setOverallPremiumFc(matchingRisk.getOverallPremiumFc()==null ? BigDecimal.ZERO : matchingRisk.getOverallPremiumFc() );
//				savedata.setOverallPremiumLc(matchingRisk.getOverallPremiumLc()==null ? BigDecimal.ZERO : matchingRisk.getOverallPremiumLc() );
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

			private CopyQuoteSuccessRes eserviceCommonDetailsEndoCopyquote(CopyQuoteReq req, String refNo, String quoteNo, String customerId,String loginId, String prevPolicyNo, String prevQuoteNo, Integer count,String custRefNo) {
				CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
				EserviceCommonDetails savedata = new EserviceCommonDetails();
				DozerBeanMapper dozerMapper = new DozerBeanMapper();
				try {
					EndtTypeMaster entMaster = ratingutil.getEndtMasterData(req.getInsuranceId(),req.getProductId(),req.getEndtTypeId()); /*endtTypeRepo.findByCompanyIdAndProductIdAndStatusAndEndtTypeIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqual(
							req.getInsuranceId(), Integer.parseInt(req.getProductId()), "Y",
							Integer.parseInt(req.getEndtTypeId()), new Date(), new Date());*/
					
					List<EserviceCommonDetails> ecommDataList = eserCommonRepo.findByQuoteNoAndStatusNotOrderByRiskIdAsc(prevQuoteNo,"D");
					List<CommonDataDetails> commDataList = commonDataRepo.findByQuoteNoAndStatusNot(prevQuoteNo,"D");
					List<EserviceCommonDetails> commData = ecommDataList.stream()
						    .filter(m -> commDataList.stream()
						        .anyMatch(risk -> m.getRiskId().equals(risk.getRiskId()) 
						                        && m.getQuoteNo().equals(risk.getQuoteNo())
						                        && m.getLocationId().equals(risk.getLocationId())
						                        && m.getSectionId().equals(risk.getSectionId())
						        		))
						    .collect(Collectors.toList());
					if (commData!=null && commData.size()>0 ) 
						for(EserviceCommonDetails commData1:commData) {
							CommonDataDetails matchingRisk = commDataList.stream()
									.filter(risk -> risk.getRiskId().equals(commData1.getRiskId())
											&& risk.getQuoteNo().equals(commData1.getQuoteNo())
											&& risk.getLocationId().equals(commData1.getLocationId())
											&& risk.getSectionId().equals(commData1.getSectionId()))
									.findFirst().orElse(null);

							savedata = dozerMapper.map(commData1, EserviceCommonDetails.class);
							savedata.setEntryDate(new Date());
							savedata.setQuoteNo(quoteNo);
							savedata.setCustomerId(customerId);
							savedata.setRequestReferenceNo(refNo);
							savedata.setCustomerReferenceNo(custRefNo);
							savedata.setCreatedBy(loginId);
							savedata.setUpdatedBy(loginId);
							savedata.setUpdatedDate(new Date());
							savedata.setOriginalPolicyNo(commData1.getOriginalPolicyNo());
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
							savedata.setLoginId(req.getLoginId()==null?commData1.getLoginId():(req.getLoginId()));
							savedata.setSubUserType(req.getSubUserType());
							savedata.setVatPremium(matchingRisk.getVatPremium()==null ? BigDecimal.ZERO : matchingRisk.getVatPremium() );
							savedata.setEndtPremium(matchingRisk.getEndtPremium()==null ? 0 : matchingRisk.getEndtPremium() );
							savedata.setEndtVatPremium(matchingRisk.getEndtVatPremium()==null ? BigDecimal.ZERO : matchingRisk.getEndtVatPremium() );
							savedata.setActualPremiumFc(matchingRisk.getActualPremiumFc()==null ? BigDecimal.ZERO : matchingRisk.getActualPremiumFc() );
							savedata.setActualPremiumLc(matchingRisk.getActualPremiumLc()==null ? BigDecimal.ZERO : matchingRisk.getActualPremiumLc() );
							savedata.setOverallPremiumFc(matchingRisk.getOverallPremiumFc()==null ? BigDecimal.ZERO : matchingRisk.getOverallPremiumFc() );
							savedata.setOverallPremiumLc(matchingRisk.getOverallPremiumLc()==null ? BigDecimal.ZERO : matchingRisk.getOverallPremiumLc() );
							eserCommonRepo.saveAndFlush(savedata);
				
						}
				} catch (Exception e) {
					e.printStackTrace();
					log.info("Exception is ---> " + e.getMessage());
					return null;
				}
				return res;

				
			}


			//Delete Previous Endo
			private CopyQuoteSuccessRes deletePreviousEndo(CopyQuoteReq req, List<EserviceBuildingDetails> motors) {
				CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
				try {
					String quoteNo=motors.get(0).getQuoteNo();
					// Delete Old Record
					
					//E service Motor Details
					if(motors.size()>0) {
						repo.deleteAll(motors);
					}
					//E service Customer Details 
					HomePositionMaster homeData=homePosistionRepo.findByQuoteNo(quoteNo);
					String customerId=homeData.getCustomerId();
					
					PersonalInfo personalInfoData=personalInforepo.findByCustomerId(customerId);
//					EserviceCustomerDetails custData = custRepo.findByCustomerReferenceNo(personalInfoData.getCustomerReferenceNo());
//					if (custData!=null) {
//						custRepo.delete(custData);
//					}
					//Personal Info
					if(personalInfoData!=null) {
						personalInforepo.delete(personalInfoData);
					}
					//Home Position master
					if(homeData!=null) {
						homePosistionRepo.delete(homeData);				}
					//Policy Cover Data
					List<PolicyCoverData> policyCoverData = policyCoverDataRepo.findByQuoteNo(quoteNo);
					if (policyCoverData.size() > 0) {
						policyCoverDataRepo.deleteAll(policyCoverData);
					}
					
					//Cover Document Upload Details
					List<DocumentTransactionDetails> coverDocList = coverDocUploadDetails.findByQuoteNo(quoteNo);
					if (coverDocList.size() > 0) {
						coverDocUploadDetails.deleteAll(coverDocList);
					}
					
					//Eservice Section Details
					List<EserviceSectionDetails> sectionList = eserSecRepo.findByQuoteNo(quoteNo);
					if (sectionList.size() > 0) {
						eserSecRepo.deleteAll(sectionList);
					}
					
					// Section Data Details
					List<SectionDataDetails> secDataList = sectionDataRepo.findByQuoteNo(quoteNo);
					if (secDataList.size() > 0) {
						sectionDataRepo.deleteAll(secDataList);
					}
					
					//Eservice Common Details
					List<EserviceCommonDetails> commonList = eserCommonRepo.findByQuoteNo(quoteNo);
					if (commonList!=null&& commonList .size()>0) {
						eserCommonRepo.deleteAll(commonList);
					}
					//Building Details
					List<BuildingDetails> buildingList = buildingRepo.findByQuoteNo(quoteNo);
					if (buildingList.size() > 0) {
						buildingRepo.deleteAll(buildingList);
					}
					//Personal Accident
					List<ProductEmployeeDetails> perList = pARepo.findByQuoteNo(quoteNo);
					if (perList.size() > 0) {
						pARepo.deleteAll(perList);
					}
					//Content And Risk
					List<ContentAndRisk> contentandrisk = contentRiskRepo.findByQuoteNo(quoteNo);
					if (contentandrisk.size() > 0) {
						contentRiskRepo.deleteAll(contentandrisk);
					}
					//Building And Risk
					List<BuildingRiskDetails> buildingRiskDetails = buildRiskRepo.findByQuoteNo(quoteNo);
					if (buildingRiskDetails.size() > 0) {
						buildRiskRepo.deleteAll(buildingRiskDetails);
					}
					//CommonDataDetals
					List<CommonDataDetails> commonData = commonDataRepo.findByQuoteNo(quoteNo);
					if (commonData.size() > 0) {
						commonDataRepo.deleteAll(commonData);
					}
					
					
				} catch (Exception e) {
					e.printStackTrace();
					log.info("Exception is ---> " + e.getMessage());
					return null;
				}
				return res;

			}

	/*	private CopyQuoteSuccessRes eserviceCustDetailsEndoCopyquote(CopyQuoteReq req, String refNo, String quoteNo,
					String customerId, String loginId, String prevPolicyNo, String prevQuoteNo, Integer count,
					String custRefNo) {
				CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
				EserviceCustomerDetails savedata = new EserviceCustomerDetails();
				DozerBeanMapper dozerMapper = new DozerBeanMapper();
				try {
					EndtTypeMaster entMaster = endtTypeRepo
							.findByCompanyIdAndProductIdAndStatusAndEndtTypeIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqual(
									req.getInsuranceId(), Integer.parseInt(req.getProductId()), "Y",
									Integer.parseInt(req.getEndtTypeId()), new Date(), new Date());
					HomePositionMaster homeData = homePosistionRepo.findByQuoteNo(prevQuoteNo);
					String olsCustomerId = homeData.getCustomerId();

					PersonalInfo personalInfoData = personalInforepo.findByCustomerId(olsCustomerId);
					EserviceCustomerDetails custData = custRepo
							.findByCustomerReferenceNo(personalInfoData.getCustomerReferenceNo());
					if (custData != null)
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
					// savedata.setPolicyNo(req.getPolicyNo() + "-" + count);
					custRepo.saveAndFlush(savedata);

				} catch (Exception e) {
					e.printStackTrace();
					log.info("Exception is ---> " + e.getMessage());
					return null;
				}
				return res;

			}
*/
			@Transactional
			public CopyQuoteSuccessRes homeEndoCopyQuote(CopyQuoteReq req,String refNo,String customerId,String quoteNo,String loginId,	String prevPolicyNo,
			String prevQuoteNo,Integer count,String custRefNo) {
				CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
				HomePositionMaster savedata = new HomePositionMaster();
				DozerBeanMapper dozerMapper = new DozerBeanMapper();
				
				try {
					EndtTypeMaster entMaster= ratingutil.getEndtMasterData(req.getInsuranceId(),req.getProductId(),req.getEndtTypeId());/*endtTypeRepo.findByCompanyIdAndProductIdAndStatusAndEndtTypeIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqual(req.getInsuranceId(), Integer.parseInt(req.getProductId()), "Y",Integer.parseInt(req.getEndtTypeId()),new Date(), new Date());*/
					String endtFeeYn=entMaster.getEndtFeeYn()	;
					BigDecimal endtPre=BigDecimal.ZERO;
					BigDecimal endtPremiumtax=BigDecimal.ZERO;
					Double endtPercent=0d;
					HomePositionMaster homeData=homePosistionRepo.findByQuoteNo(prevQuoteNo);
					Double tax=Double.valueOf(homeData.getVatPercent().toString());
					BigDecimal exchangeRate=homeData.getExchangeRate();
					BigDecimal overAllPremiumFc=new BigDecimal(homeData.getOverallPremiumFc().toString());
					if ("Y".equalsIgnoreCase(endtFeeYn)) {
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
					if("Y".equalsIgnoreCase(endtFeeYn)){
						savedata.setEndtPremium(endtPre);
						savedata.setIsChargRefund(txt);
						savedata.setEndtPremiumTax(endtPremiumtax);
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
			public CopyQuoteSuccessRes personolInfoEndoCopyQuote(CopyQuoteReq req,String customerId,String prevPolicyNo,
					String prevQuoteNo,Integer count,String custRefNo) {
				CopyQuoteSuccessRes res =new CopyQuoteSuccessRes();
				PersonalInfo savedata = new PersonalInfo();
				DozerBeanMapper dozerMapper = new DozerBeanMapper();
				try {
					EndtTypeMaster entMaster=ratingutil.getEndtMasterData(req.getInsuranceId(),req.getProductId(),req.getEndtTypeId());
					HomePositionMaster homeData=homePosistionRepo.findByQuoteNo(prevQuoteNo);
					String olsCustomerId=homeData.getCustomerId();
					
					PersonalInfo personalInfoData=personalInforepo.findByCustomerId(olsCustomerId);
					savedata = dozerMapper.map(personalInfoData, PersonalInfo.class);
					savedata.setCustomerId(customerId);
				//	savedata.setCustomerReferenceNo(custRefNo);
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
					EndtTypeMaster entMaster =ratingutil.getEndtMasterData(req.getInsuranceId(),req.getProductId(),req.getEndtTypeId());/* endtTypeRepo
							.findByCompanyIdAndProductIdAndStatusAndEndtTypeIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqual(
									req.getInsuranceId(), Integer.parseInt(req.getProductId()), "Y",
									Integer.parseInt(req.getEndtTypeId()), new Date(), new Date());*/
					String endTypeDesc = entMaster.getEndtTypeDesc();
					String endtFeeYn = entMaster.getEndtFeeYn();
					String coverDesc = "";
					BigDecimal endtFee=BigDecimal.ZERO;
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
				} catch (Exception e) {
					e.printStackTrace();
					log.info("Exception is ---> " + e.getMessage());
					return null;
				}
				return res;
			}

			// Cover Document Upload Details Enst Copy Quote
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
					List<String> status = new ArrayList<>();
					status.add("P");
					status.add("Y");
					status.add("E");
					List<DocumentTransactionDetails> motorData = coverDocUploadDetails.findByQuoteNoAndStatusIn(prevQuoteNo,status);
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
			// Section Data Details
			private CopyQuoteSuccessRes sectionDataDetailsEndoCopyquote(CopyQuoteReq req, String refNo, String quoteNo, String customerId,
					String loginId, String prevPolicyNo, String prevQuoteNo, Integer count, String custRefNo) {
				CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
				SectionDataDetails savedata = new SectionDataDetails();
				DozerBeanMapper dozerMapper = new DozerBeanMapper();
				try {
					EndtTypeMaster entMaster = ratingutil.getEndtMasterData(req.getInsuranceId(),req.getProductId(),req.getEndtTypeId()); /*endtTypeRepo
							.findByCompanyIdAndProductIdAndStatusAndEndtTypeIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqual(
									req.getInsuranceId(), Integer.parseInt(req.getProductId()), "Y",
									Integer.parseInt(req.getEndtTypeId()), new Date(), new Date());*/

					List<SectionDataDetails> eserSec = sectionDataRepo.findByQuoteNoAndStatusNot(prevQuoteNo,"D");
					if (eserSec != null && eserSec.size()>0 ) {
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

			@Override
			public GetMotorProtfolioPendingRes getBuildingProtfolioPending(ExistingQuoteReq req,
					List<String> branches, Date startDate, int limit, int offset, String status) {
				GetMotorProtfolioPendingRes resp = new GetMotorProtfolioPendingRes();
				List<PortfolioPendingGridCriteriaRes> portfolio = new ArrayList<PortfolioPendingGridCriteriaRes>();
				try {
					CriteriaBuilder cb = em.getCriteriaBuilder();
					CriteriaQuery<PortfolioPendingGridCriteriaRes> query = cb.createQuery(PortfolioPendingGridCriteriaRes.class);

					Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
					Root<EserviceBuildingDetails> m = query.from(EserviceBuildingDetails.class);
					Root<HomePositionMaster> h = query.from(HomePositionMaster.class);
					
					Subquery<Long> endtPre = query.subquery(Long.class);
					Root<HomePositionMaster> h1 = endtPre.from(HomePositionMaster.class);
					endtPre.select(cb.sum(h1.get("endtPremium"))) ;
					Predicate pm1 = cb.equal(h1.get("endtStatus"), m.get("endtStatus"));
					Predicate pm2   = cb.like(h1.get("originalPolicyNo"), m.get("originalPolicyNo"));
					endtPre.where(pm1,pm2);
			
					// Select
					query.multiselect(
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

				
					// Where
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
						n5 = cb.equal(m.get("loginId"), req.getLoginId());
//						n9 = cb.equal(m.get("applicationId"), req.getApplicationId());
						Expression<String> e0 = m.get("brokerBranchCode");
						n6 = e0.in(branches);
						query.where(n1, n2, n3, n4, n5, n6,n7,n8).groupBy((m.get("originalPolicyNo")),m.get("endtStatus"));
					} else {
						n5 = cb.equal(m.get("applicationId"), req.getApplicationId());
						Expression<String> e0 = m.get("branchCode");
						n6 = e0.in(branches);
						if(StringUtils.isNotBlank(req.getBdmCode())){
							n9 = cb.equal(m.get("bdmCode"), req.getBdmCode());
						}else {
							n9 = cb.equal(m.get("loginId"), req.getLoginId());
						}
						query.where(n1, n2, n3, n4, n5, n6,n7,n8,n9).groupBy((m.get("originalPolicyNo")),m.get("endtStatus"));
					}
					
//					if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
//					
//					} else {
//						Expression<String> e0 = m.get("branchCode");
//						n6 = e0.in(branches);
//					}
//					query.where(n1, n2, n3, n4, n5, n6,n7,n8,n9).groupBy((m.get("originalPolicyNo")),m.get("endtStatus"));
					// Get Result
					TypedQuery<PortfolioPendingGridCriteriaRes> result = em.createQuery(query);
					result.setFirstResult(limit * offset);
					result.setMaxResults(offset);
					portfolio = result.getResultList();
					portfolio = portfolio.stream().filter(o -> !o.getIdsCount().equals(0L))
							.collect(Collectors.toList());
					
					if (portfolio != null && portfolio.size() > 0) {
						portfolio = portfolio.stream().filter(distinctByKey(o -> Arrays.asList(o.getRequestReferenceNo()))).collect(Collectors.toList());
					}
					resp.setPending(portfolio);
					resp.setCount(totalProtfolioPending( req,branches,startDate,limit,offset, status) );
					
					
				
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
					Root<EserviceBuildingDetails> m = query.from(EserviceBuildingDetails.class);
			//		Root<HomePositionMaster> h = query.from(HomePositionMaster.class);
					
//					query.multiselect(cb.count(m));
					query.select(cb.countDistinct(m.get("requestReferenceNo")));  
					
					Predicate n1 = cb.equal(c.get("customerReferenceNo"), m.get("customerReferenceNo"));
					Predicate n2 = cb.equal(m.get("companyId"), req.getInsuranceId());
					Predicate n3 = cb.equal(m.get("productId"), req.getProductId());
					Predicate n4 = cb.equal(m.get("endtStatus"), status);
				//	Predicate n4 = cb.in(m.get("status")).value(Arrays.asList("E","D"));  
//					Predicate n7 = cb.greaterThanOrEqualTo(h.get("expiryDate"), startDate);
//					Predicate n8 = cb.lessThanOrEqualTo(h.get("entryDate"), startDate);

					Predicate n5 = null;
					Predicate n6 = null;
					Predicate n9 = null;
					if (req.getApplicationId().equalsIgnoreCase("1")) {
						n5 = cb.equal(m.get("loginId"), req.getLoginId());
						Expression<String> e0 = m.get("brokerBranchCode");
						n6 = e0.in(branches);
						query.where(n1, n2, n3, n4, n5, n6).groupBy((m.get("originalPolicyNo")),
								m.get("endtStatus"));
					} else {
						n5 = cb.equal(m.get("applicationId"), req.getApplicationId());
						if(StringUtils.isNotBlank(req.getBdmCode())){
							n9 = cb.equal(m.get("bdmCode"), req.getBdmCode());
						}else {
							n9 = cb.equal(m.get("loginId"), req.getLoginId());
						}
						Expression<String> e0 = m.get("branchCode");
						n6 = e0.in(branches);
						query.where(n1, n2, n3, n4, n5, n6, n9).groupBy((m.get("originalPolicyNo")),
								m.get("endtStatus"));
					}
					
//					if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
//						Expression<String> e0 = m.get("brokerBranchCode");
//						n6 = e0.in(branches);
//					} else {
//						Expression<String> e0 = m.get("branchCode");
//						n6 = e0.in(branches);
//					}
//					query.where(n1, n2, n3, n4, n5, n6, n9).groupBy((m.get("originalPolicyNo")),
//								m.get("endtStatus"));

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

			@Override
			public synchronized GetBuildingAdminReferalPendingDetailsRes getBuildingAdminReferalPendingDetails(RevertGridReq req, int limit,
					int offset, String status) {
				List<ReferalGridCriteriaAdminRes> referrals = new ArrayList<ReferalGridCriteriaAdminRes>();
				GetBuildingAdminReferalPendingDetailsRes resp = new GetBuildingAdminReferalPendingDetailsRes();
				try {
					resp.setCount(0l);
					
					CriteriaBuilder cb = em.getCriteriaBuilder();
					CriteriaQuery<ReferalGridCriteriaAdminRes> query = cb.createQuery(ReferalGridCriteriaAdminRes.class);

					Root<EserviceBuildingDetails> m = query.from(EserviceBuildingDetails.class);
					Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);

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
							m.get("endtPremium").alias("endtPremium"));

					// Order By
					List<Order> orderList = new ArrayList<Order>();
					orderList.add(cb.desc(m.get("updatedDate")));

					// Where
					Predicate n1 = cb.equal(c.get("customerReferenceNo"), m.get("customerReferenceNo"));
					Predicate n2 = cb.equal(m.get("companyId"), req.getInsuranceId());
					Predicate n3 = cb.equal(m.get("productId"), req.getProductId());
					Predicate n4 = cb.equal(m.get("status"), status);
				
					Predicate n14 = null;
					if(req.getType().equalsIgnoreCase("Q"))
							n14 = cb.isNull(m.get("endorsementTypeDesc")); 
					else if (req.getType().equalsIgnoreCase("E"))
							n14 = cb.isNotNull(m.get("endorsementTypeDesc")); 
					
					query.where(n1, n2, n3, n4, n14).orderBy(orderList);
				
			
					TypedQuery<ReferalGridCriteriaAdminRes> result = em.createQuery(query);
					result.setFirstResult(limit * offset);
					result.setMaxResults(offset);
					referrals = result.getResultList();
					
					resp.setReferalGridCriteriaAdminRes(referrals);
					resp.setCount(totalcountadminreferral(req,  status));
				
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
				return resp;
			}
			
			private Long totalcountadminreferral(RevertGridReq req, String status) {


				Long count = 0l;
				try {	
					CriteriaBuilder cb = em.getCriteriaBuilder();
					CriteriaQuery<Long> query = cb.createQuery(Long.class);

					Root<EserviceBuildingDetails> m = query.from(EserviceBuildingDetails.class);
					Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
					
					query.multiselect(cb.count(m));

					Predicate n1 = cb.equal(c.get("customerReferenceNo"), m.get("customerReferenceNo"));
					Predicate n2 = cb.equal(m.get("companyId"), req.getInsuranceId());
					Predicate n3 = cb.equal(m.get("productId"), req.getProductId());
					Predicate n4 = cb.equal(m.get("status"), status);
				
					Predicate n14 = null;
					if(req.getType().equalsIgnoreCase("Q"))
							n14 = cb.isNull(m.get("endorsementTypeDesc")); 
					else if (req.getType().equalsIgnoreCase("E"))
							n14 = cb.isNotNull(m.get("endorsementTypeDesc")); 
					
					query.where(n1, n2, n3, n4, n14);
					
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

			@Override
			public synchronized List<ReferalGridCriteriaAdminRes> getBuildingAdminReferalPendingDetailsCount(RevertGridReq req, String status) {
				List<ReferalGridCriteriaAdminRes> referrals = new ArrayList<ReferalGridCriteriaAdminRes>();
				try {
					CriteriaBuilder cb = em.getCriteriaBuilder();
					CriteriaQuery<ReferalGridCriteriaAdminRes> query = cb.createQuery(ReferalGridCriteriaAdminRes.class);

					// Find All
					Root<EserviceBuildingDetails> m = query.from(EserviceBuildingDetails.class);
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
							cb.max(m.get("endtPremium")).alias("endtPremium"));

					// Order By
					List<Order> orderList = new ArrayList<Order>();
					orderList.add(cb.desc(m.get("updatedDate")));

					// Where
					Predicate n1 = cb.equal(c.get("customerReferenceNo"), m.get("customerReferenceNo"));
					Predicate n2 = cb.equal(m.get("companyId"), req.getInsuranceId());
					Predicate n3 = cb.equal(m.get("productId"), req.getProductId());
					Predicate n4 = cb.equal(m.get("status"), status);
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
					} else {
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
				
				} catch (Exception e) {
					e.printStackTrace();
					log.info("Log Details" + e.getMessage());
					return null;
				}
				return referrals;
			}

			@Override
			public List<GetExistingBrokerListRes> getBuildingProtfolioDropdownPending(ExistingBrokerUserListReq req,
					Date today) {
				List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
				List<Tuple> list = new ArrayList<Tuple>();
				try {

					if (!("issuer".equalsIgnoreCase(req.getUserType()))) {
						CriteriaBuilder cb = em.getCriteriaBuilder();
						CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

						Root<EserviceBuildingDetails> m = query.from(EserviceBuildingDetails.class);
						Root<LoginUserInfo> us = query.from(LoginUserInfo.class);
						query.multiselect( m.get("loginId").alias("code"),us.get("userName").alias("codeDesc"),
								m.get("sourceType").alias("type"));

						// Find All
						Subquery<String> agencyCode = query.subquery(String.class);
						Root<LoginMaster> ocpm1 = agencyCode.from(LoginMaster.class);
						agencyCode.select(ocpm1.get("agencyCode"));
						Predicate a1 = cb.equal(ocpm1.get("loginId"), req.getLoginId());
						agencyCode.where(a1);

//						Predicate n1 = cb.equal(m.get("applicationId"), req.getApplicationId());
						Predicate n2 = cb.isNotNull(m.get("applicationId"));
						Predicate n3 = cb.equal(m.get("companyId"), req.getCompanyId());
						Predicate n4 = cb.equal(m.get("productId"), req.getProductId());
						Predicate n5 = cb.equal(m.get("endtStatus"), "P");
						Predicate n12 = null;
						if ("Broker".equalsIgnoreCase(req.getUserType())) {
							n12 = cb.equal(m.get("brokerCode"), agencyCode);
						} else if ("User".equalsIgnoreCase(req.getUserType())) {
							n12 = cb.equal(m.get("agencyCode"), agencyCode);
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
								res.setType(data.get("type") == null ? "" : data.get("type").toString());
								resList.add(res);

							}
						}
					} else {
						resList = getPortfolioPendingIssuerBuilding(req, today);
					}

				} catch (Exception e) {
					e.printStackTrace();
					log.info("Log Details" + e.getMessage());
					return null;
				}
				return resList;

			}

			private List<GetExistingBrokerListRes> getPortfolioPendingIssuerBuilding(ExistingBrokerUserListReq req,
					Date today) {
				List<Tuple> list = new ArrayList<Tuple>();
				List<Tuple> list1 = new ArrayList<Tuple>();
				List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
				try {
					{
						CriteriaBuilder cb = em.getCriteriaBuilder();
						CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

						Root<EserviceBuildingDetails> m = query.from(EserviceBuildingDetails.class);

						query.multiselect(m.get("bdmCode").alias("code"), m.get("customerName").alias("codeDesc"),
								m.get("sourceType").alias("type"));
						Predicate n1 = cb.equal(m.get("applicationId"), req.getApplicationId());
						Predicate n2 = cb.isNotNull(m.get("applicationId"));
						Predicate n3 = cb.equal(m.get("companyId"), req.getCompanyId());
						Predicate n4 = cb.equal(m.get("productId"), req.getProductId());
						Predicate n5 = cb.equal(m.get("endtStatus"), "P");
						Predicate n8 = cb.isNotNull(m.get("bdmCode"));
						Predicate n9 = cb.equal(m.get("branchCode"),req.getBranchCode());
						query.where(n1, n2, n3, n4, n5, n8,n9);

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

						Root<EserviceBuildingDetails> m1 = query1.from(EserviceBuildingDetails.class);
						Root<LoginUserInfo> us = query1.from(LoginUserInfo.class);
						query1.multiselect( m1.get("loginId").alias("code"),us.get("userName").alias("codeDesc"),
								m1.get("sourceType").alias("type"));
						Predicate n1 = cb1.equal(m1.get("applicationId"), req.getApplicationId());
						Predicate n2 = cb1.isNotNull(m1.get("applicationId"));
						Predicate n3 = cb1.equal(m1.get("companyId"), req.getCompanyId());
						Predicate n4 = cb1.equal(m1.get("productId"), req.getProductId());
						Predicate n5 = cb1.equal(m1.get("endtStatus"), "P");
						Predicate n6 = cb1.isNull(m1.get("bdmCode"));
						Predicate n9 = cb1.equal(m1.get("branchCode"),req.getBranchCode());
						Predicate us1 = cb1.equal(us.get("loginId"), m1.get("loginId"));
						query1.where(n1, n2, n3, n4, n5, n6,us1,n9);

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
			public List<GetExistingBrokerListRes> getBrokerUserListLapsedAsset(ExistingBrokerUserListReq req,Date today, Date before30) {
					
				List<Tuple> list = new ArrayList<Tuple>();
				List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
				try {
					if(!("issuer".equalsIgnoreCase(req.getUserType()))){		
						
						CriteriaBuilder cb = em.getCriteriaBuilder();
						CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);
		
						Root<EserviceBuildingDetails> m = query.from(EserviceBuildingDetails.class);
						Root<LoginUserInfo> us = query.from(LoginUserInfo.class);
						query.multiselect(
								m.get("loginId").alias("code"),
								us.get("userName").alias("codeDesc"),
								m.get("sourceType").alias("type")).distinct(true);
		
						// Find All
						Subquery<Long> agencyCode = query.subquery(Long.class);
						Root<LoginMaster> ocpm1 = agencyCode.from(LoginMaster.class);
						agencyCode.select(ocpm1.get("agencyCode"));
						Predicate a1 = cb.equal(ocpm1.get("loginId"), req.getLoginId());
				//		Predicate a3 = cb.equal(ocpm1.get("status"), "Y");
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
							predics1.add(cb.equal(m.get("brokerCode"), agencyCode.as(String.class)));
						} else if ("User".equalsIgnoreCase(req.getUserType())) {
							predics1.add(cb.equal(m.get("agencyCode"), agencyCode));
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
					 CriteriaBuilder cb = em.getCriteriaBuilder();
					 CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);
					 
					 Root<EserviceBuildingDetails> m = query.from(EserviceBuildingDetails.class); 
					 
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
//							 res.setType(data.get("type")==null?"":	data.get("type").toString());
							 String type = data.get("type") == null ? "" : data.get("type").toString();
								type = "Premia " + type;
								res.setType(type);
							 resList.add(res);
						
						 }
					 }	
					 
					 CriteriaBuilder cb1 = em.getCriteriaBuilder();
					 CriteriaQuery<Tuple> query1 = cb1.createQuery(Tuple.class);
					 
					 Root<EserviceBuildingDetails> m1 = query1.from(EserviceBuildingDetails.class); 
					 Root<LoginUserInfo> us = query1.from(LoginUserInfo.class);
					 query1.multiselect(
							 m1.get("loginId").alias("code"),
							 us.get("userName").alias("codeDesc"),
							 m1.get("sourceType").alias("type")
							 ).distinct(true) ;
					 
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
					
					
				} catch (Exception e) {
					e.printStackTrace();
					log.info("Log Details" + e.getMessage());
					return null;
				}
				return resList ;
			}


			@Override
			public List<GetExistingBrokerListRes> getBrokerUserListBuildingRejected(ExistingBrokerUserListReq req,
					Date today, Date before30) {

				List<Tuple> list = new ArrayList<Tuple>();	
				List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
				try {
					if(!("issuer".equalsIgnoreCase(req.getUserType()))){		
						CriteriaBuilder cb = em.getCriteriaBuilder();
						CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

						Root<EserviceBuildingDetails> m = query.from(EserviceBuildingDetails.class);
						Root<LoginUserInfo> us = query.from(LoginUserInfo.class);
						query.multiselect(
								m.get("loginId").alias("code"),
								us.get("userName").alias("codeDesc"),
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
							predics1.add(cb.equal(m.get("brokerCode"), agencyCode.as(String.class)));
						} else if ("User".equalsIgnoreCase(req.getUserType())) {
							predics1.add(cb.equal(m.get("agencyCode"), agencyCode));
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
					 
					 Root<EserviceBuildingDetails> m = query.from(EserviceBuildingDetails.class); 
					 
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
//							 res.setType(data.get("type")==null?"":	data.get("type").toString());
							 String type = data.get("type") == null ? "" : data.get("type").toString();
								type = "Premia " + type;
								res.setType(type);
							 resList.add(res);
						
						 }
					 }	
					 
					 CriteriaBuilder cb1 = em.getCriteriaBuilder();
					 CriteriaQuery<Tuple> query1 = cb1.createQuery(Tuple.class);
					 
					 Root<EserviceBuildingDetails> m1 = query1.from(EserviceBuildingDetails.class); 
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
			public List<GetExistingBrokerListRes> getBuildingReferalDropdown(ExistingBrokerUserListReq req,
					Date today,String status) {
				List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
				List<Tuple> list = new ArrayList<Tuple>();
				try {

					if (!("issuer".equalsIgnoreCase(req.getUserType()))) {
						CriteriaBuilder cb = em.getCriteriaBuilder();
						CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

						Root<EserviceBuildingDetails> m = query.from(EserviceBuildingDetails.class);
						Root<LoginUserInfo> us = query.from(LoginUserInfo.class);
						query.multiselect( m.get("loginId").alias("code"),us.get("userName").alias("codeDesc"),
								m.get("sourceType").alias("type"));

						// Find All
						Subquery<String> agencyCode = query.subquery(String.class);
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
							n12 = cb.equal(m.get("brokerCode"), agencyCode);
						} else if ("User".equalsIgnoreCase(req.getUserType())) {
							n12 = cb.equal(m.get("agencyCode"), agencyCode);
						}
						Predicate n13 = cb.isNotNull(m.get("sourceType"));
						Predicate n14 = cb.isNotNull(m.get("loginId"));
						Predicate n15 = null;
//						Predicate n16 = cb.equal(m.get("sectionId"), "0");
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
						} else {
							resList = commonGridService.getCommonReferalDropdown(req, today, status);
						}
					} else {
						resList = getReferalIssuerBuilding(req, today,status);
						if(resList==null || resList.isEmpty())
						{
							resList = commonGridService.getCommonReferalDropdown(req, today, status);
						}
						
					}

				} catch (Exception e) {
					e.printStackTrace();
					log.info("Log Details" + e.getMessage());
					return null;
				}
				return resList;

			}

			private List<GetExistingBrokerListRes> getReferalIssuerBuilding(ExistingBrokerUserListReq req,
					Date today,String status) {
				List<Tuple> list = new ArrayList<Tuple>();
				List<Tuple> list1 = new ArrayList<Tuple>();
				List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
				try {
					{
						CriteriaBuilder cb = em.getCriteriaBuilder();
						CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

						Root<EserviceBuildingDetails> m = query.from(EserviceBuildingDetails.class);

						query.multiselect(m.get("bdmCode").alias("code"), m.get("bdmName").alias("codeDesc"),
								m.get("sourceType").alias("type"));
						Predicate n1 = cb.equal(m.get("applicationId"), req.getApplicationId());
						Predicate n2 = cb.isNotNull(m.get("applicationId"));
						Predicate n3 = cb.equal(m.get("companyId"), req.getCompanyId());
						Predicate n4 = cb.equal(m.get("productId"), req.getProductId());
						Predicate n5 = cb.equal(m.get("status"), status);
						Predicate n8 = cb.isNotNull(m.get("bdmCode"));
						Predicate n15 = null;
//						Predicate n16 = cb.equal(m.get("sectionId"), "0");
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
//								res.setType(data.get("type") == null ? "" : data.get("type").toString());
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

						Root<EserviceBuildingDetails> m1 = query1.from(EserviceBuildingDetails.class);
						Root<LoginUserInfo> us = query1.from(LoginUserInfo.class);
						query1.multiselect( m1.get("loginId").alias("code"),us.get("userName").alias("codeDesc"),
								m1.get("sourceType").alias("type"));
						Predicate n1 = cb1.equal(m1.get("applicationId"), req.getApplicationId());
						Predicate n2 = cb1.isNotNull(m1.get("applicationId"));
						Predicate n3 = cb1.equal(m1.get("companyId"), req.getCompanyId());
						Predicate n4 = cb1.equal(m1.get("productId"), req.getProductId());
						Predicate n5 = cb1.equal(m1.get("status"), status);
						Predicate n6 = cb1.isNull(m1.get("bdmCode"));
//						Predicate n16 = cb1.equal(m1.get("sectionId"), "0");
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
			public List<GetExistingBrokerListRes> getAdminBuildingRPDropdown(ExistingBrokerUserListReq req,
					Date today) {
				List<Tuple> list = new ArrayList<Tuple>();
				List<Tuple> list1 = new ArrayList<Tuple>();
				List<Tuple> list2 = new ArrayList<Tuple>();
				List<Tuple> list3 = new ArrayList<Tuple>();
				List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
				try {
					{
					
						CriteriaBuilder cb = em.getCriteriaBuilder();
						CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

						Root<EserviceBuildingDetails> m = query.from(EserviceBuildingDetails.class);

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
//						Predicate n16 = cb.equal(m.get("sectionId"), "0");
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
//								res.setType(data.get("type") == null ? "" : data.get("type").toString());
								String type=data.get("type") == null ? "" : data.get("type").toString();
								type="Premia "+type;
								res.setType(type);
								resList.add(res);

							}
						} {
							List<GetExistingBrokerListRes> resList2 = commonGridService.getAdminCommonRPDropdown(req, today);
							if(resList2!=null && resList2.size()>0) {
								Set<String> existingKeys = resList.stream()
								        .map(e -> e.getCode() + "|" + e.getCodeDesc()) // unique key
								        .collect(Collectors.toSet());

								    resList2.stream()
								        .filter(e -> !existingKeys.contains(e.getCode() + "|" + e.getCodeDesc()))
								        .forEach(resList::add);
							}
						}
				}
					
						{
						CriteriaBuilder cb1 = em.getCriteriaBuilder();
						CriteriaQuery<Tuple> query1 = cb1.createQuery(Tuple.class);

						Root<EserviceBuildingDetails> m1 = query1.from(EserviceBuildingDetails.class);
						Root<LoginUserInfo> us = query1.from(LoginUserInfo.class);
						query1.multiselect( m1.get("loginId").alias("code"),us.get("userName").alias("codeDesc"),
								m1.get("sourceType").alias("type"));

						// Uw Condition

						Subquery<String> uwData1 = query1.subquery(String.class);
						Root<UWReferralDetails> uw1 = uwData1.from(UWReferralDetails.class);
						uwData1.select(uw1.get("requestReferenceNo"));
						Predicate u12 = cb1.equal(uw1.get("uwLoginId"), req.getApplicationId());
						Predicate u13 = cb1.equal(uw1.get("uwStatus"), "Y");
						Predicate u14 = cb1.equal(uw1.get("companyId"), req.getCompanyId());
						Predicate u15 = cb1.equal(uw1.get("productId"), req.getProductId());
						Predicate u16 = cb1.equal(uw1.get("branchCode"), req.getBranchCode());
						uwData1.where(u12,u13,u14,u15,u16);
						
						//In 
						Expression<String>e01=m1.get("requestReferenceNo");
						
					//	Predicate np1 = cb1.equal(m1.get("applicationId"), req.getApplicationId());
						Predicate np2 = cb1.isNotNull(m1.get("applicationId"));
						Predicate np3 = cb1.equal(m1.get("companyId"), req.getCompanyId());
						Predicate np4 = cb1.equal(m1.get("productId"), req.getProductId());
						Predicate np5 = cb1.equal(m1.get("status"), "RP");
						Predicate np6 = e01.in(uwData1);
						Predicate np8 = cb1.isNull(m1.get("bdmCode"));
//						Predicate np7 = cb1.equal(m1.get("sectionId"), "0");
						
						Predicate np9 = null;
						if(req.getType().equalsIgnoreCase("Q"))
							np9 = cb1.isNull(m1.get("endorsementTypeDesc")); 
						else if (req.getType().equalsIgnoreCase("E"))
							np9 = cb1.isNotNull(m1.get("endorsementTypeDesc"));
						Predicate us1 = cb1.equal(us.get("loginId"), m1.get("loginId"));
						
						query1.where(np2, np3,np4,np5,np6,np8,np9,us1);

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
			public List<GetExistingBrokerListRes> getBuildingAdminReferalDropdown(ExistingBrokerUserListReq req,
					Date today,String status) {
				List<Tuple> list = new ArrayList<Tuple>();
				List<Tuple> list1 = new ArrayList<Tuple>();
				List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
				try {
					{
						CriteriaBuilder cb = em.getCriteriaBuilder();
						CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

						Root<EserviceBuildingDetails> m = query.from(EserviceBuildingDetails.class);

						query.multiselect(m.get("bdmCode").alias("code"), m.get("bdmName").alias("codeDesc"),
								m.get("sourceType").alias("type"));

						
						Predicate n1 = cb.equal(m.get("adminLoginId"), req.getApplicationId());
//						Predicate n2 = cb.isNotNull(m.get("applicationId"));
						Predicate n3 = cb.equal(m.get("companyId"), req.getCompanyId());
						Predicate n4 = cb.equal(m.get("productId"), req.getProductId());
						Predicate n5 = cb.equal(m.get("status"), status);
						Predicate n8 = cb.isNotNull(m.get("bdmCode"));
						
						Predicate n15 = null;
//						Predicate n16 = cb.equal(m.get("sectionId"), "0");
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
//								res.setType(data.get("type") == null ? "" : data.get("type").toString());
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

						Root<EserviceBuildingDetails> m1 = query1.from(EserviceBuildingDetails.class);
						Root<LoginUserInfo> us = query1.from(LoginUserInfo.class);
						query1.multiselect( m1.get("loginId").alias("code"),us.get("userName").alias("codeDesc"),
								m1.get("sourceType").alias("type"));

						
						Predicate n1 = cb1.equal(m1.get("adminLoginId"), req.getApplicationId());
//						Predicate n2 = cb1.isNotNull(m1.get("applicationId"));
						Predicate n3 = cb1.equal(m1.get("companyId"), req.getCompanyId());
						Predicate n4 = cb1.equal(m1.get("productId"), req.getProductId());
						Predicate n5 = cb1.equal(m1.get("status"), status);
						Predicate n8 = cb1.isNull(m1.get("bdmCode"));
//						Predicate n16 = cb1.equal(m1.get("sectionId"), "0");
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
