package com.maan.eway.common.service.impl;

import java.math.BigDecimal;
import java.sql.DatabaseMetaData;
import java.text.DateFormat;
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

import com.maan.eway.admin.res.GetTravelAdminReferalPendingDetailsRes;
import com.maan.eway.admin.res.ReferalGridCriteriaAdminRes;
import com.maan.eway.admin.res.ReferalGridCriteriaRes;
import com.maan.eway.bean.CoverMaster;
import com.maan.eway.bean.DocumentTransactionDetails;
import com.maan.eway.bean.EndtTypeMaster;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.EserviceSectionDetails;
import com.maan.eway.bean.EserviceTravelDetails;
import com.maan.eway.bean.EserviceTravelGroupDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.InsuranceCompanyMaster;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.LoginMaster;
import com.maan.eway.bean.LoginUserInfo;
import com.maan.eway.bean.PaymentInfo;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.bean.PolicyCoverData;
import com.maan.eway.bean.SectionDataDetails;
import com.maan.eway.bean.SeqCustid;
import com.maan.eway.bean.SeqCustrefno;
import com.maan.eway.bean.SeqQuoteno;
import com.maan.eway.bean.TravelPassengerDetails;
import com.maan.eway.bean.TravelPassengerHistory;
import com.maan.eway.bean.UWReferralDetails;
import com.maan.eway.calculator.util.RatingFactorsUtil;
import com.maan.eway.common.req.CopyQuoteReq;
import com.maan.eway.common.req.ExistingBrokerUserListReq;
import com.maan.eway.common.req.ExistingQuoteReq;
import com.maan.eway.common.req.GetallPolicyReportsReq;
import com.maan.eway.common.req.RevertGridReq;
import com.maan.eway.common.req.SequenceGenerateReq;
import com.maan.eway.common.res.GetExistingBrokerListRes;
import com.maan.eway.common.res.GetMotorProtfolioPendingRes;
import com.maan.eway.common.res.GetTravelReferalDetailsRes;
import com.maan.eway.common.res.GetTravelRejectedQuoteDetailsRes;
import com.maan.eway.common.res.PortfolioPendingGridCriteriaRes;
import com.maan.eway.common.res.TravelQuoteCriteriaRes;
import com.maan.eway.common.res.TravelQuoteCriteriaResponse;
import com.maan.eway.common.res.TravelRejectCriteriaRes;
import com.maan.eway.common.service.TravelGridService;
import com.maan.eway.jasper.service.impl.JasperConfiguration;
import com.maan.eway.master.req.CopyQuoteDropDownReq;
import com.maan.eway.repository.DocumentTransactionDetailsRepository;
import com.maan.eway.repository.EServiceSectionDetailsRepository;
import com.maan.eway.repository.EndtTypeMasterRepository;
import com.maan.eway.repository.EserviceCustomerDetailsRepository;
import com.maan.eway.repository.EserviceTravelDetailsRepository;
import com.maan.eway.repository.EserviceTravelGroupDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.PersonalInfoRepository;
import com.maan.eway.repository.PolicyCoverDataRepository;
import com.maan.eway.repository.SectionDataDetailsRepository;
import com.maan.eway.repository.SeqCustidRepository;
import com.maan.eway.repository.SeqCustrefnoRepository;
import com.maan.eway.repository.SeqQuotenoRepository;
import com.maan.eway.repository.SeqRefnoRepository;
import com.maan.eway.repository.TravelPassengerDetailsRepository;
import com.maan.eway.repository.TravelPassengerHistoryRepository;
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

@Transactional
@Service
public class TravelGridServiceImpl implements  TravelGridService {

	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private EserviceTravelDetailsRepository repo;
	
	@Autowired
	private GenerateSeqNoServiceImpl seqNo ;
	
	@Autowired
	private MotorGridServiceImpl motorService ;
	
	
	@Autowired
	private TravelPassengerDetailsRepository travelPassengerRepo;
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
	private EserviceTravelGroupDetailsRepository groupRepo;
	
	@Autowired
	private SeqCustrefnoRepository custRefRepo  ;
	
	@Autowired
	private EndtTypeMasterRepository endtTypeRepo;
	@Autowired
	private EServiceSectionDetailsRepository eserSecRepo;
	
	@Autowired
	private DocumentTransactionDetailsRepository coverDocUploadDetails;

	@Autowired
	private TravelPassengerDetailsRepository traPassDetailsRepo;
	@Autowired
	private TravelPassengerHistoryRepository traPassHisRepo  ;

	@Autowired
	private SectionDataDetailsRepository sectionDataRepo;
	
	@Autowired 
	private RatingFactorsUtil ratingutil;
	
	 @Autowired
	 private JasperConfiguration jasperConfiguration;
	 
	 private boolean isOracle;
	 private boolean isMySQL;
	 
	 @Autowired
     private GenerateSeqNoServiceImpl genSeqNoService ;

	
	private Logger log = LogManager.getLogger(MotorGridServiceImpl.class);
	
	//EXISITING QUOTE
	//DropDown
	 
	@Override
		public List<GetExistingBrokerListRes> getTravelExistingDropdown(ExistingBrokerUserListReq req, Date today, Date before30) {
			List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
			List<Tuple> list = new ArrayList<Tuple>();
			try {
			if(!("issuer".equalsIgnoreCase(req.getUserType()))){		
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);
	 
				Root<EserviceTravelDetails> m = query.from(EserviceTravelDetails.class);
				Root<LoginUserInfo> us = query.from(LoginUserInfo.class); 	//changes
				query.multiselect( m.get("loginId").alias("code"),us.get("userName").alias("codeDesc"),
				m.get("sourceType").alias("type"));
	 
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
					predics1.add(cb.equal(m.get("brokerCode"), agencyCode.as(String.class)));
				} else if ("User".equalsIgnoreCase(req.getUserType())) {
					predics1.add(cb.equal(m.get("agencyCode"), agencyCode.as(String.class)));
				}
				predics1.add(cb.isNotNull(m.get("sourceType")));
				predics1.add(cb.isNotNull(m.get("loginId")));
				
				predics1.add( cb.equal(us.get("loginId"), m.get("loginId"))); //change
				
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

		Root<EserviceTravelDetails> m = query.from(EserviceTravelDetails.class);

		query.multiselect(m.get("bdmCode").alias("code"), m.get("customerName").alias("codeDesc"), //change
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

		Root<EserviceTravelDetails> m1 = query1.from(EserviceTravelDetails.class);
		Root<LoginUserInfo> us = query1.from(LoginUserInfo.class);
		query1.multiselect( m1.get("loginId").alias("code"),us.get("userName").alias("codeDesc"),
		m1.get("sourceType").alias("type"));  //change

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
	public TravelQuoteCriteriaResponse getTravelExistingQuoteDetails(ExistingQuoteReq req, Date startDate, Date endDate, Integer limit, Integer offset) {
		TravelQuoteCriteriaResponse resp = new TravelQuoteCriteriaResponse();
		List<TravelQuoteCriteriaRes> existingQuotes = new ArrayList<TravelQuoteCriteriaRes>();
		try {
			
			// Get Datas
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<TravelQuoteCriteriaRes> query = cb.createQuery(TravelQuoteCriteriaRes.class);

			// Find All
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
			Root<EserviceTravelDetails> m = query.from(EserviceTravelDetails.class);
			
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
			
			query.multiselect(	
					m.get("totalPassengers").alias("idsCount"),
					c.get("customerReferenceNo").alias("customerReferenceNo"),
					c.get("idNumber").alias("idNumber"),
					c.get("clientName").alias("clientName"),
					// Travel Info

					m.get("companyId").alias("companyId"),
					m.get("productId").alias("productId"),
					m.get("productName").alias("productName"),
					m.get("branchCode").alias("branchCode"),
					m.get("requestReferenceNo").alias("requestReferenceNo") , 
					m.get("quoteNo").alias("quoteNo") ,
					m.get("customerId").alias("customerId") ,
					m.get("travelStartDate").alias("policyStartDate"),
					m.get("travelEndDate").alias("policyEndDate"),
					overAllPremiumLc.alias("overallPremiumLc"), 
					overAllPremiumFc.alias("overallPremiumFc"),
					m.get("currency").alias("currency"));

			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(m.get("updatedDate")));
			
			
		    // Where	
			Predicate n1 = cb.equal(  c.get("customerReferenceNo"),  m.get("customerReferenceNo"));
			Predicate n2 = cb.equal(  m.get("companyId"),  req.getInsuranceId()) ;
			Predicate n3 = cb.equal(  m.get("productId"),  req.getProductId());
			Predicate n4 = cb.equal(m.get("status"),"Y" );
			Predicate n5 = cb.lessThanOrEqualTo(m.get("updatedDate"), endDate);
			Predicate n6 = cb.greaterThanOrEqualTo(m.get("updatedDate"), startDate);
			Predicate n9 = cb.isNull(m.get("endorsementType"));
			Predicate n7 =  null ;
			Predicate n10 = null;
			
			//Changes
			n7 = cb.equal(m.get("applicationId"), req.getApplicationId());
			if(StringUtils.isNotBlank(req.getBdmCode())){
				
				n10 = cb.equal(m.get("bdmCode"), req.getBdmCode());
				
			}else {
				
				n10 = cb.equal(m.get("loginId"), req.getLoginId());
			}
			
			Predicate n8 = null;
			if(  req.getUserType().equalsIgnoreCase("Broker") ||   req.getUserType().equalsIgnoreCase("User") ) {
				n8 = cb.equal(  m.get("brokerBranchCode"),  req.getBrokerBranchCode());
			} else {
				n8 = cb.equal(  m.get("branchCode"),  req.getBranchCode());
			}
		
			query.where(n1,n2,n3,n4,n5,n6,n7,n8,n9,n10).orderBy(orderList);
		
			
		
			TypedQuery<TravelQuoteCriteriaRes> result = em.createQuery(query);
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			existingQuotes = result.getResultList();
			
			resp.setQuoteRes(existingQuotes);
			resp.setTotalCount(totalcountexist(req, startDate, endDate, "Y"));
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resp;
	}

	private Long totalcountexist(ExistingQuoteReq req, Date startDate, Date endDate, String status) {
		Long count = 0l;
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> query = cb.createQuery(Long.class);

			// Find All
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
			Root<EserviceTravelDetails> m = query.from(EserviceTravelDetails.class);
			
			query.multiselect(cb.count(m));
			
			Predicate n1 = cb.equal(  c.get("customerReferenceNo"),  m.get("customerReferenceNo"));
			Predicate n2 = cb.equal(  m.get("companyId"),  req.getInsuranceId()) ;
			Predicate n3 = cb.equal(  m.get("productId"),  req.getProductId());
			Predicate n4 = cb.equal(m.get("status"),status );
			Predicate n5 = cb.lessThanOrEqualTo(m.get("updatedDate"), endDate);
			Predicate n6 = cb.greaterThanOrEqualTo(m.get("updatedDate"), startDate);
			Predicate n9 = cb.isNull(m.get("endorsementType"));
			
			Predicate n7 =  null ;
			Predicate n10 =  null ;
			//Changes
			n7 = cb.equal(m.get("applicationId"), req.getApplicationId());
			if(StringUtils.isNotBlank(req.getBdmCode())){
				
				n10 = cb.equal(m.get("bdmCode"), req.getBdmCode());
				
			}else {
				
				n10 = cb.equal(m.get("loginId"), req.getLoginId());
			}
			
			Predicate n8 = null;
			if(  req.getUserType().equalsIgnoreCase("Broker") ||   req.getUserType().equalsIgnoreCase("User") ) {
				n8 = cb.equal(  m.get("brokerBranchCode"),  req.getBrokerBranchCode());
			} else {
				n8 = cb.equal(  m.get("branchCode"),  req.getBranchCode());
			}
		
			query.where(n1,n2,n3,n4,n5,n6,n7,n8,n9,n10);
			
		
			
			// Get Result
			TypedQuery<Long> result = em.createQuery(query);
			List<Long> list = result.getResultList();
			
			if(list.size()>0)
				count = list.get(0);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return count;
	}

	@Override
	public TravelQuoteCriteriaResponse getTravelLapsedQuoteDetails(ExistingQuoteReq req, Date before30, int limit, int offset) {
		List<TravelQuoteCriteriaRes> lapsedQuotes = new ArrayList<TravelQuoteCriteriaRes>();
		TravelQuoteCriteriaResponse resp = new TravelQuoteCriteriaResponse();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<TravelQuoteCriteriaRes> query = cb.createQuery(TravelQuoteCriteriaRes.class);

			// Find All
			Root<EserviceTravelDetails> m = query.from(EserviceTravelDetails.class);
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
			query.multiselect( m.get("totalPassengers").alias("idsCount"),
					c.get("customerReferenceNo").alias("customerReferenceNo"),
					c.get("idNumber").alias("idNumber"),
					c.get("clientName").alias("clientName"),
					// Travel Info

					m.get("companyId").alias("companyId"),
					m.get("productId").alias("productId"),
					m.get("productName").alias("productName"),
					m.get("branchCode").alias("branchCode"),
					m.get("requestReferenceNo").alias("requestReferenceNo") , 
					m.get("quoteNo").alias("quoteNo") ,
					m.get("customerId").alias("customerId") ,
					m.get("travelStartDate").alias("policyStartDate"),
					m.get("travelEndDate").alias("policyEndDate"),
					overAllPremiumLc.alias("overallPremiumLc"), 
					overAllPremiumFc.alias("overallPremiumFc"),
					m.get("currency").alias("currency"));

			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(m.get("updatedDate")));
			
		
			// Where
			Predicate n1 = cb.equal(  c.get("customerReferenceNo"),  m.get("customerReferenceNo"));
			Predicate n2 = cb.equal(  m.get("companyId"),  req.getInsuranceId()) ;
			Predicate n3 = cb.equal(  m.get("productId"),  req.getProductId());
			Predicate n4 = cb.equal(m.get("status"),"Y" );
			Predicate n5 = cb.lessThanOrEqualTo(m.get("updatedDate"), before30);
			Predicate n9 = cb.isNull(m.get("endorsementType"));
			
			Predicate n10 =  null ;
			
			Predicate n6 = cb.equal(m.get("applicationId"), req.getApplicationId());
			if(StringUtils.isNotBlank(req.getBdmCode())){
				n10 = cb.equal(m.get("bdmCode"), req.getBdmCode());
			}else {
				n10 = cb.equal(m.get("loginId"), req.getLoginId());
			}
			
			Predicate n7 = null;
			
			if(  req.getUserType().equalsIgnoreCase("Broker") ||   req.getUserType().equalsIgnoreCase("User") ) {
				
				n7 = cb.equal(  m.get("brokerBranchCode"),  req.getBrokerBranchCode());
			} else {
				
				n7 = cb.equal(  m.get("branchCode"),  req.getBranchCode());
			}
		
			query.where(n1,n2,n3,n4,n5,n6,n7,n9,n10).orderBy(orderList);
			
			// Get Result
			TypedQuery<TravelQuoteCriteriaRes> result = em.createQuery(query);
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			lapsedQuotes = result.getResultList();
			
			resp.setTotalCount(totallapsedQuotes(req, before30));
			resp.setQuoteRes(lapsedQuotes);
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resp;
	}

	private Long totallapsedQuotes(ExistingQuoteReq req, Date before30) {
		Long count = 0l;
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> query = cb.createQuery(Long.class);

			// Find All
			Root<EserviceTravelDetails> m = query.from(EserviceTravelDetails.class);
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
			
			// Select
			query.multiselect( cb.count(m));			
			// Where
			Predicate n1 = cb.equal(  c.get("customerReferenceNo"),  m.get("customerReferenceNo"));
			Predicate n2 = cb.equal(  m.get("companyId"),  req.getInsuranceId()) ;
			Predicate n3 = cb.equal(  m.get("productId"),  req.getProductId());
			Predicate n4 = cb.equal(m.get("status"),"Y" );
			Predicate n5 = cb.lessThanOrEqualTo(m.get("updatedDate"), before30);
			Predicate n9 = cb.isNull(m.get("endorsementType"));
			

			Predicate n10 =  null ;
			
			Predicate n6 = cb.equal(m.get("applicationId"), req.getApplicationId());
			if(StringUtils.isNotBlank(req.getBdmCode())){
				n10 = cb.equal(m.get("bdmCode"), req.getBdmCode());
			}else {
				n10 = cb.equal(m.get("loginId"), req.getLoginId());
			}
			
			Predicate n7 = null;
			
			if(  req.getUserType().equalsIgnoreCase("Broker") ||   req.getUserType().equalsIgnoreCase("User") ) {
				
				n7 = cb.equal(  m.get("brokerBranchCode"),  req.getBrokerBranchCode());
			} else {
				
				n7 = cb.equal(  m.get("branchCode"),  req.getBranchCode());
			}
		
			query.where(n1,n2,n3,n4,n5,n6,n7,n9,n10);
			
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
	public GetTravelRejectedQuoteDetailsRes getTravelRejectedQuoteDetails(ExistingQuoteReq req, Date startDate ,Date  endDate , int limit,	int offset) {

		GetTravelRejectedQuoteDetailsRes resp= new GetTravelRejectedQuoteDetailsRes();
		List<TravelRejectCriteriaRes> rejectedQuotes = new ArrayList<TravelRejectCriteriaRes>();
		try {
			// Get Datas
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<TravelRejectCriteriaRes> query = cb.createQuery(TravelRejectCriteriaRes.class);

			// Find All
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
			Root<EserviceTravelDetails> m = query.from(EserviceTravelDetails.class);

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
			
			
			query.multiselect(	
					m.get("totalPassengers").alias("idsCount"),
					c.get("customerReferenceNo").alias("customerReferenceNo"),
					c.get("idNumber").alias("idNumber"),
					c.get("clientName").alias("clientName"),
					// Travel Info

					m.get("companyId").alias("companyId"),
					m.get("productId").alias("productId"),
					m.get("branchCode").alias("branchCode"),
					m.get("requestReferenceNo").alias("requestReferenceNo") , 
					m.get("quoteNo").alias("quoteNo") ,
					m.get("customerId").alias("customerId") ,
					m.get("travelStartDate").alias("policyStartDate"),
					m.get("travelEndDate").alias("policyEndDate"),
					m.get("rejectReason").alias("rejectReason"),
					overAllPremiumLc.alias("overallPremiumLc"), 
					overAllPremiumFc.alias("overallPremiumFc"),
					m.get("currency").alias("currency"));

			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(m.get("updatedDate")));
			
		    // Where	
			Predicate n1 = cb.equal(  c.get("customerReferenceNo"),  m.get("customerReferenceNo"));
			Predicate n2 = cb.equal(  m.get("companyId"),  req.getInsuranceId()) ;
			Predicate n3 = cb.equal(  m.get("productId"),  req.getProductId());
			Predicate n4 = cb.equal(m.get("status"),"R" );
			Predicate n5 = cb.lessThanOrEqualTo(m.get("updatedDate"), endDate);
			Predicate n6 = cb.greaterThanOrEqualTo(m.get("updatedDate"), startDate);
			Predicate n9 = cb.isNull(m.get("endorsementType"));
			
			Predicate n7 =  null ;
			Predicate n10 =  null ;
		
			n7 = cb.equal(m.get("applicationId"), req.getApplicationId());
			if(StringUtils.isNotBlank(req.getBdmCode())){
			n10 = cb.equal(m.get("bdmCode"), req.getBdmCode());
			}else {
			n10 = cb.equal(m.get("loginId"), req.getLoginId());
			}
			
			Predicate n8 = null;
			if(  req.getUserType().equalsIgnoreCase("Broker") ||   req.getUserType().equalsIgnoreCase("User") ) {
				n8 = cb.equal( m.get("brokerBranchCode"),  req.getBrokerBranchCode());
			} else {
				n8 = cb.equal( m.get("branchCode"),  req.getBranchCode());
			}
	
			query.where(n1,n2,n3,n4,n5,n6,n7,n8,n9, n10).orderBy(orderList);
			
			
			// Get Result
			TypedQuery<TravelRejectCriteriaRes> result = em.createQuery(query);
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			rejectedQuotes = result.getResultList();
			
			resp.setQuoteRes(rejectedQuotes);
			resp.setTotalCount(totalcountexist(req, startDate, endDate, "R"));
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resp ;
	}

	
	@Override
	public synchronized  GetTravelReferalDetailsRes getTravelReferalDetails(ExistingQuoteReq req,int limit,	int offset, String status) {
		List<ReferalGridCriteriaRes> referrals = new ArrayList<ReferalGridCriteriaRes>();
		GetTravelReferalDetailsRes resp = new GetTravelReferalDetailsRes();
		
		try {
			
			resp.setTotalCount(0);
		
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ReferalGridCriteriaRes> query = cb.createQuery(ReferalGridCriteriaRes.class);
		
			Root<EserviceTravelDetails> m = query.from(EserviceTravelDetails.class);
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
			
			query.multiselect(  
					c.get("customerReferenceNo").alias("customerReferenceNo"),
					c.get("idNumber").alias("idNumber"),
					c.get("clientName").alias("clientName"),
					c.get("titleDesc").alias("titleDesc"),
					c.get("middleName").alias("middleName"),c.get("lastName").alias("lastName"),
					// Travel Info

					m.get("companyId").alias("companyId"),
					m.get("productId").alias("productId"),
					m.get("branchCode").alias("branchCode"),
					m.get("requestReferenceNo").alias("requestReferenceNo") , 
					m.get("quoteNo").alias("quoteNo") ,
					m.get("customerId").alias("customerId") ,
					m.get("travelStartDate").alias("policyStartDate"),
					m.get("travelEndDate").alias("policyEndDate") , m.get("rejectReason").alias("rejectReason"),
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
			
		
			// Where
			Predicate n1 = cb.equal(  c.get("customerReferenceNo"),  m.get("customerReferenceNo"));
			Predicate n2 = cb.equal(  m.get("companyId"),  req.getInsuranceId()) ;
			Predicate n3 = cb.equal(  m.get("productId"),  req.getProductId());
			Predicate n4 = cb.equal(m.get("status"),status );
		
			Predicate n5= null;
			
			Predicate n7 = cb.equal(m.get("applicationId"), req.getApplicationId());
			if(StringUtils.isNotBlank(req.getBdmCode())){
				n5 = cb.equal(m.get("bdmCode"), req.getBdmCode());
			}else {
				n5 = cb.equal(m.get("loginId"), req.getLoginId());
			}
			
			Predicate n6 = null;
			if(  req.getUserType().equalsIgnoreCase("Broker") ||   req.getUserType().equalsIgnoreCase("User") ) {
				
				 n6 = cb.equal(m.get("brokerBranchCode"),  req.getBrokerBranchCode());
			} else {
				
				 n6 = cb.equal( m.get("branchCode"),  req.getBranchCode());
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
				query.where(n1,n2,n3,n4,n5,n6,n7,n8).orderBy(orderList);
			}else {
				query.where(n1,n2,n3,n4,n5,n6,n7,n8,nLead).orderBy(orderList);
			}
			//query.where(n1,n2,n3,n4,n5,n6,n7,n8).orderBy(orderList);
			
			
			// Get Result
			TypedQuery<ReferalGridCriteriaRes> result = em.createQuery(query);
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			referrals = result.getResultList();
			
			resp.setReferalGridCriteriaRes(referrals);
			
			//Counts
			int totalcount = totalcountuser(req,  status);
			resp.setTotalCount(totalcount);	
		
			
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

			Root<EserviceTravelDetails> m = query.from(EserviceTravelDetails.class);
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
			
			query.multiselect(cb.count(m));
			
		
			// Where
			Predicate n1 = cb.equal(  c.get("customerReferenceNo"),  m.get("customerReferenceNo"));
			Predicate n2 = cb.equal(  m.get("companyId"),  req.getInsuranceId()) ;
			Predicate n3 = cb.equal(  m.get("productId"),  req.getProductId());
			Predicate n4 = cb.equal(m.get("status"),status );
			
			Predicate n5= null;
			
			Predicate n7 = cb.equal(m.get("applicationId"), req.getApplicationId());
			if(StringUtils.isNotBlank(req.getBdmCode())){
				n5 = cb.equal(m.get("bdmCode"), req.getBdmCode());
			}else {
				n5 = cb.equal(m.get("loginId"), req.getLoginId());
			}
			
			Predicate n6 = null;
			if(  req.getUserType().equalsIgnoreCase("Broker") ||   req.getUserType().equalsIgnoreCase("User") ) {
				
				 n6 = cb.equal(m.get("brokerBranchCode"),  req.getBrokerBranchCode());
			} else {
				
				 n6 = cb.equal( m.get("branchCode"),  req.getBranchCode());
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
				query.where(n1, n2, n3, n4, n5, n6, n7, n8);
			} else {
				query.where(n1, n2, n3, n4, n5, n6, n7, n8, nLead);
			}
			//query.where(n1,n2,n3,n4,n5,n6,n7,n8);
	
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
		public synchronized GetTravelReferalDetailsRes getTravelAdminReferalDetails(ExistingQuoteReq req, int limit,
				int offset , String status) {
			List<ReferalGridCriteriaRes> referrals = new ArrayList<ReferalGridCriteriaRes>();
			GetTravelReferalDetailsRes resp = new GetTravelReferalDetailsRes();
			
			try {
				
				resp.setTotalCount(0);
			
				
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<ReferalGridCriteriaRes> query = cb.createQuery(ReferalGridCriteriaRes.class);
	 
				// Find All
				Root<EserviceTravelDetails> m = query.from(EserviceTravelDetails.class);
				Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
				
				
				// Select
				query.multiselect(
						// Customer Info
						c.get("customerReferenceNo").alias("customerReferenceNo"),
						 c.get("idNumber").alias("idNumber"),
						c.get("clientName").alias("clientName"),
						c.get("titleDesc").alias("titleDesc"),
						c.get("middleName").alias("middleName"),c.get("lastName").alias("lastName"),
						// Vehicle Info
	 
						m.get("companyId").alias("companyId"),
						m.get("productId").alias("productId"),
						m.get("branchCode").alias("branchCode"),
						 m.get("requestReferenceNo").alias("requestReferenceNo") ,
						 m.get("quoteNo").alias("quoteNo") ,
						 m.get("customerId").alias("customerId") ,
						m.get("travelStartDate").alias("policyStartDate"),
						m.get("travelEndDate").alias("policyEndDate") , m.get("rejectReason").alias("rejectReason"),
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
						m.get("endtPremium").alias("endtPremium")   );
	 
				
				// Order By
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(m.get("updatedDate")));
				
				// Where
				Predicate n1 = cb.equal(  c.get("customerReferenceNo"),  m.get("customerReferenceNo"));
				Predicate n2 = cb.equal(  m.get("companyId"),  req.getInsuranceId()) ;
				Predicate n3 = cb.equal(  m.get("productId"),  req.getProductId());
				Predicate n4 = cb.equal(m.get("status"),status );
				Predicate n6 = cb.equal(m.get("branchCode"),  req.getBranchCode());
				
				Predicate n14 = null;
				Predicate n16 = null;
				Predicate n15 = cb.equal(m.get("adminLoginId"), req.getApplicationId());
				if (StringUtils.isNotBlank(req.getBdmCode())) {
				    n16 = cb.or(
				        cb.equal(m.get("bdmCode"), req.getBdmCode()),   
				        cb.equal(m.get("loginId"), req.getLoginId())   
				    );
				} else {
				    n16 = cb.equal(m.get("loginId"), req.getLoginId());
				}
				
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
					query.where(n1,n2,n3,n4,n6,n8,n9,n10,n11,n12,n13,n14,n16).orderBy(orderList);
					
				} else {
					query.where(n1,n2,n3,n4,n6,n14,n15,n16).orderBy(orderList);
					
				}
				
				// Get Result
				TypedQuery<ReferalGridCriteriaRes> result = em.createQuery(query);
				result.setFirstResult(limit * offset);
				result.setMaxResults(offset);
				referrals = result.getResultList();
				
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

			// Find All
			Root<EserviceTravelDetails> m = query.from(EserviceTravelDetails.class);
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
			
			query.multiselect(cb.count(m));
			
			
			Predicate n1 = cb.equal(  c.get("customerReferenceNo"),  m.get("customerReferenceNo"));
			Predicate n2 = cb.equal(  m.get("companyId"),  req.getInsuranceId()) ;
			Predicate n3 = cb.equal(  m.get("productId"),  req.getProductId());
			Predicate n4 = cb.equal(m.get("status"),status );
			Predicate n6 = cb.equal(m.get("branchCode"),  req.getBranchCode());
			Predicate n7 = cb.isNull(m.get("endorsementTypeDesc")); 
			
//			Predicate n15 = cb.equal(m.get("applicationId"), req.getApplicationId());
//			Predicate n16 = cb.equal(m.get("loginId"), req.getLoginId());
//			Predicate n17 = cb.equal(m.get("customerName"), req.getLoginId());
//			Predicate n18 = cb.or(n16,n17);
			
			Predicate n16 = null;
			Predicate n15 = cb.equal(m.get("adminLoginId"), req.getApplicationId());
			if(StringUtils.isNotBlank(req.getBdmCode())){
				n16= cb.equal(m.get("bdmCode"), req.getBdmCode());
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
				query.where(n7,n1,n2,n3,n4,n6,n8,n9,n10,n11,n12,n13,n16);
				
			} else {
				query.where(n7,n1,n2,n3,n4,n6,n15,n16);
				
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

			// Find All
			Root<EserviceTravelDetails> m = query.from(EserviceTravelDetails.class);
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
			
			query.multiselect(cb.count(m));
			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(m.get("updatedDate")));
			
			// Where
			Predicate n1 = cb.equal(  c.get("customerReferenceNo"),  m.get("customerReferenceNo"));
			Predicate n2 = cb.equal(  m.get("companyId"),  req.getInsuranceId()) ;
			Predicate n3 = cb.equal(  m.get("productId"),  req.getProductId());
			Predicate n4 = cb.equal(m.get("status"),status );
			Predicate n6 = cb.equal(m.get("branchCode"),  req.getBranchCode());
			Predicate n7 = cb.isNotNull(m.get("endorsementTypeDesc")); 
//			Predicate n15 = cb.equal(m.get("applicationId"), req.getApplicationId());
//			Predicate n16 = cb.equal(m.get("loginId"), req.getLoginId());
//			Predicate n17 = cb.equal(m.get("customerName"), req.getLoginId());
//			Predicate n18 = cb.or(n16,n17);
			
			Predicate n16 = null;
			Predicate n15 = cb.equal(m.get("adminLoginId"), req.getApplicationId());
			if(StringUtils.isNotBlank(req.getBdmCode())){
				n16= cb.equal(m.get("bdmCode"), req.getBdmCode());
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
				query.where(n7,n1,n2,n3,n4,n6,n8,n9,n10,n11,n12,n13,n16).orderBy(orderList);
				
			} else {
				query.where(n7,n1,n2,n3,n4,n6,n15,n16).orderBy(orderList);
				
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
	public CopyQuoteSuccessRes travelCopyQuote(CopyQuoteReq req, List<String> branches,String loginId) {
		CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
		DozerBeanMapper dozerMapper  = new DozerBeanMapper(); 
		EserviceTravelDetails savedata = new EserviceTravelDetails();
		
		try {
			String searchValue = req.getRequestReferenceNo();
			String searchKey = "RequestReferenceNo";
			String companyId = req.getInsuranceId();
			String userType=req.getUserType();
			String branchCode="";
			List<Tuple> list = copyQuoteSearchDetails(searchKey, searchValue, companyId,loginId,userType,branches);
	
			String refNo=req.getRequestReferenceNo();
            
			if(list.size()>0) {
//				String refShortCode = motorService.getListItem(companyId, req.getBranchCode(), "PRODUCT_SHORT_CODE",req.getProductId());
//		        refNo = refShortCode + "-" + seqNo.generateRefNo() ; 
		     // Generate Seq
	 			SequenceGenerateReq generateSeqReq = new SequenceGenerateReq();
	 		 	generateSeqReq.setInsuranceId(companyId);  
	 		 	generateSeqReq.setProductId(req.getProductId());
	 		 	generateSeqReq.setType("2");
	 		 	generateSeqReq.setTypeDesc("REQUEST_REFERENCE_NO");
	 		 	refNo =  genSeqNoService.generateSeqCall(generateSeqReq);

				for (Tuple data : list) {
				savedata=dozerMapper.map(data.get(0),EserviceTravelDetails.class);
				
				savedata.setEntryDate( new Date());
				savedata.setCreatedBy(req.getLoginId());
				savedata.setUpdatedBy(req.getLoginId());
				savedata.setUpdatedDate(new Date());
				savedata.setRequestReferenceNo(refNo);
				savedata.setOldReqRefNo(req.getRequestReferenceNo());
//				if (req.getUserType().equalsIgnoreCase("Broker") || ( req.getUserType().equalsIgnoreCase("User"))) {  
//						branchCode = req.getBranchCode();
//						savedata.setApplicationId("1");
//					//	savedata.setBrokerBranchCode(branchCode);
//
//					} else if ("issuer".equalsIgnoreCase(userType)) {
//						savedata.setApplicationId(req.getLoginId());
//						branchCode = req.getBranchCode();
//					//	savedata.setBranchCode(branchCode);
//					}
					savedata.setTravelStartDate(null);
					savedata.setTravelEndDate(null);
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
					// Source Type Condtion
					String sourceType = savedata.getSourceType() ;
					if(StringUtils.isNotBlank(savedata.getApplicationId()) && ! "1".equalsIgnoreCase(savedata.getApplicationId()) ) {
						List<ListItemValue> sourcerTypes =genSeqNoService.getSourceTypeDropdown(req.getInsuranceId() , req.getBranchCode() ,"SOURCE_TYPE");
						List<ListItemValue> acitveSourcerTypes = sourcerTypes.stream().filter( o -> "Y".equalsIgnoreCase(o.getStatus()) 
								&& o.getItemValue().contains(sourceType) ).collect(Collectors.toList()); 							
						savedata.setSourceType(acitveSourcerTypes.size() > 0 ? acitveSourcerTypes.get(0).getItemValue()	: 	savedata.getSourceType());			
						savedata.setSourceTypeId(acitveSourcerTypes.size() > 0 ? acitveSourcerTypes.get(0).getItemCode()	: 	savedata.getSourceTypeId());
					}
				repo.saveAndFlush(savedata);
				}
				// Save Section
				List<Tuple> list3 = copySectionQuoteSearchDetails(searchKey, searchValue, companyId, loginId, userType,
						branches);
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
						savedata.setPolicyNo(null);
						eserSecRepo.saveAndFlush(savedata3);
					}

				}
				
				//Travel Group Details
	
				List<Tuple> list4 = copyTravelQuoteSearchDetails(searchKey, searchValue, companyId, loginId, userType,
						branches);
				if (list4 != null && list4.size() > 0) {
					for (Tuple data : list4) {
						EserviceTravelGroupDetails savedata4 = new EserviceTravelGroupDetails();
						savedata4 = dozerMapper.map(data.get(0), EserviceTravelGroupDetails.class);

						savedata4.setEntryDate(new Date());
						savedata4.setCreatedBy(req.getLoginId());
						savedata4.setRequestReferenceNo(refNo);
						savedata4.setQuoteNo("");
						savedata4.setStatus("Y");
						savedata4.setPolicyNo(null);
						groupRepo.saveAndFlush(savedata4);
					}

				}
/*				//Travel Passenger
				List<Tuple> list5 = copyTravelPassengerQuoteSearchDetails(searchKey, searchValue, companyId, loginId, userType,
						branches);
				if (list5 != null && list5.size() > 0) {
					for (Tuple data : list5) {
						TravelPassengerDetails savedata5 = new TravelPassengerDetails();
						savedata5 = dozerMapper.map(data.get(0), TravelPassengerDetails.class);

						savedata5.setEntryDate(new Date());
						savedata5.setCreatedBy(req.getLoginId());
						savedata5.setRequestReferenceNo(refNo);
						savedata5.setQuoteNo("");
						savedata5.setStatus("Y");
						traPassDetailsRepo.saveAndFlush(savedata5);
					}

				}
				//Travel Passenger History
				List<Tuple> list6 = copyTravelPassengerHistoryQuoteSearchDetails(searchKey, searchValue, companyId, loginId, userType,
						branches);
				if (list6 != null && list6.size() > 0) {
					for (Tuple data : list6) {
						TravelPassengerHistory savedata6 = new TravelPassengerHistory();
						savedata6 = dozerMapper.map(data.get(0), TravelPassengerHistory.class);

						savedata6.setEntryDate(new Date());
						savedata6.setCreatedBy(req.getLoginId());
						savedata6.setRequestReferenceNo(refNo);
						savedata6.setQuoteNo("");
						savedata6.setStatus("Y");
						traPassHisRepo.saveAndFlush(savedata6);
					}

				}
*/
			}
			res.setRequestReferenceNo(refNo);
			
			
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

			Root<EserviceTravelDetails> c = query.from(EserviceTravelDetails.class);
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

//			Predicate n2 = cb.equal(c.get("companyId"), companyId);

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
	public List<Tuple> copyTravelQuoteSearchDetails(String searchKey, String searchValue, String companyId, String loginId,
			String userType, List<String> branches) {
		List<Tuple> customerDetailsList = new ArrayList<Tuple>();
		try {

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

			Root<EserviceTravelGroupDetails> c = query.from(EserviceTravelGroupDetails.class);
		//	Root<EserviceCustomerDetails> cus = query.from(EserviceCustomerDetails.class);
			
			query.multiselect(c);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("groupId")));

			Predicate n1 = null;
		//	Predicate n3 = null;
		//	Predicate n4 = null;
		//	Predicate n5 = null;

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
			
		//	n5 = cb.equal(c.get("customerReferenceNo"), cus.get("customerReferenceNo"));
			//query.where(n1,n2,n3,n4,n5).orderBy(orderList);
			query.where(n1).orderBy(orderList);
	

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
	public List<Tuple> copyTravelPassengerQuoteSearchDetails(String searchKey, String searchValue, String companyId, String loginId,
			String userType, List<String> branches) {
		List<Tuple> customerDetailsList = new ArrayList<Tuple>();
		try {

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

			Root<TravelPassengerDetails> c = query.from(TravelPassengerDetails.class);
			Root<EserviceCustomerDetails> cus = query.from(EserviceCustomerDetails.class);
			
			query.multiselect(c,
					cus.get("clientName").alias("clientName"));

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("customerReferenceNo")));

			Predicate n1 = null;
			Predicate n5 = null;

			// Where
			if (searchKey.equalsIgnoreCase("RequestReferenceNo")) {
				n1 = cb.equal(cb.lower(c.get("requestReferenceNo")), searchValue);
			}

			
			n5 = cb.equal(c.get("customerReferenceNo"), cus.get("customerReferenceNo"));
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
	public List<Tuple> copyTravelPassengerHistoryQuoteSearchDetails(String searchKey, String searchValue, String companyId, String loginId,
			String userType, List<String> branches) {
		List<Tuple> customerDetailsList = new ArrayList<Tuple>();
		try {

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

			Root<TravelPassengerHistory> c = query.from(TravelPassengerHistory.class);
			Root<EserviceCustomerDetails> cus = query.from(EserviceCustomerDetails.class);
			
			query.multiselect(c,
					cus.get("clientName").alias("clientName"));

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("customerReferenceNo")));

			Predicate n1 = null;
			Predicate n5 = null;

			// Where
			if (searchKey.equalsIgnoreCase("RequestReferenceNo")) {
				n1 = cb.equal(cb.lower(c.get("requestReferenceNo")), searchValue);
			}

			
			n5 = cb.equal(c.get("customerReferenceNo"), cus.get("customerReferenceNo"));
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
	public List<Tuple> searchTravelQuote(CopyQuoteReq req, List<String> branches) {
		SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd");
		List<Tuple> searchQuote = new ArrayList<Tuple>();
	try {
		// Search 
		String searchKey = req.getSearchKey();
		String searchValue = req.getSearchValue();
		String companyId = req.getInsuranceId();
		String loginId=req.getLoginId();
		String userType=req.getUserType();
		
			if ("RequestReferenceNo".equalsIgnoreCase(searchKey)) {	
			searchQuote = searchDetails(searchKey, searchValue, companyId,loginId,userType,branches);
			}else if ("CustomerReferenceNo".equalsIgnoreCase(searchKey)) {	
				searchQuote = searchDetails(searchKey, searchValue, companyId,loginId,userType,branches);
			}else if ("ClientName".equalsIgnoreCase(searchKey)) {	
				searchQuote = searchDetails(searchKey, searchValue, companyId,loginId,userType,branches);
			}else if ("QuoteNumber".equalsIgnoreCase(searchKey)) {	
				searchQuote =  searchDetails(searchKey, searchValue, companyId,loginId,userType,branches);
			}else if ("ChassisNumber".equalsIgnoreCase(searchKey)) {
				searchQuote =  searchDetails(searchKey, searchValue, companyId,loginId,userType,branches);
			}else if ("RegistrationNumber".equalsIgnoreCase(searchKey)) {
				searchQuote =  searchDetails(searchKey, searchValue, companyId,loginId,userType,branches);
			}else if ("EntryDate".equalsIgnoreCase(searchKey)) {
				Date entryDate=sdf.parse(searchValue);
				searchValue=sdf.format(entryDate);
				searchQuote =  searchDetails(searchKey, searchValue, companyId,loginId,userType,branches);
			} else if ("PolicyNo".equalsIgnoreCase(searchKey)) {
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

			Root<EserviceTravelDetails> c = query.from(EserviceTravelDetails.class);
			Root<EserviceCustomerDetails> cus = query.from(EserviceCustomerDetails.class);
			
			query.multiselect(cb.max(cus.get("clientName")).alias("clientName"), cb.count(c).alias("idsCount"),
					cb.max(c.get("companyId")).alias("companyId"), cb.max(c.get("productId")).alias("productId"),
					cb.max(c.get("branchCode")).alias("branchCode"),
					cb.max(c.get("requestReferenceNo")).alias("requestReferenceNo"),
					cb.selectCase().when(cb.max(c.get("quoteNo")).isNotNull(), cb.max(c.get("quoteNo")))
							.otherwise(cb.max(c.get("quoteNo"))).alias("quoteNo"),

					cb.selectCase().when(cb.max(c.get("customerId")).isNotNull(), cb.max(c.get("customerId")))
							.otherwise(cb.max(c.get("customerId"))).alias("customerId"),
					cb.max(c.get("travelStartDate")).alias("policyStartDate"),
					cb.max(c.get("travelEndDate")).alias("policyEndDate"), 
					cb.max(c.get("rejectReason")).alias("rejectReason"),
					cb.max(c.get("adminRemarks")).alias("adminRemarks"),
					cb.max(c.get("referalRemarks")).alias("referalRemarks"),
					cb.max(c.get("customerReferenceNo")).alias("customerReferenceNo"),
					cb.max(c.get("riskId")).alias("riskId"),
					cb.max(c.get("travelCoverId")).alias("travelCoverId"),
					cb.max(c.get("travelCoverDesc")).alias("travelCoverDesc"),
					cb.max(c.get("sectionId")).alias("sectionId"), 
					cb.max(c.get("policyNo")).alias("policyNo"),
					cb.max(c.get("sourceCountry")).alias("sourceCountry"),
					cb.max(c.get("destinationCountry")).alias("destinationCountry"),
					cb.max(c.get("sportsCoverYn")).alias("sportsCoverYn"),
					cb.max(c.get("terrorismCoverYn")).alias("terrorismCoverYn"),
					cb.max(c.get("planTypeId")).alias("planTypeId"),
					cb.max(c.get("currency")).alias("currency"),
					cb.max(c.get("exchangeRate")).alias("exchangeRate"),
					cb.max(c.get("planTypeDesc")).alias("planTypeDesc"),
					cb.max(c.get("travelCoverDuration")).alias("travelCoverDuration"),
					cb.max(c.get("totalPassengers")).alias("totalPassengers"),
					cb.max(c.get("totalPremium")).alias("totalPremium"),
					cb.max(c.get("age")).alias("age"), 
					cb.max(c.get("effectiveDate")).alias("effectiveDate"),
					cb.max(c.get("entryDate")).alias("entryDate"),
					cb.max(c.get("createdBy")).alias("createdBy"), 
					cb.max(c.get("status")).alias("status"),
					cb.max(c.get("updatedDate")).alias("updatedDate"),
					cb.max(c.get("updatedBy")).alias("updatedBy"), 
					cb.max(c.get("remarks")).alias("remarks"),
					cb.max(c.get("havepromocode")).alias("havepromocode"),
					cb.max(c.get("promocode")).alias("promocode"),
					cb.max(c.get("covidCoverYn")).alias("covidCoverYn"),
					cb.max(c.get("acExecutiveId")).alias("acExecutiveId"),
					cb.max(c.get("applicationId")).alias("applicationId"),
					cb.max(c.get("brokerCode")).alias("brokerCode"),
					cb.max(c.get("subUserType")).alias("subUserType"),
					cb.max(c.get("loginId")).alias("loginId"),
					cb.max(c.get("adminLoginId")).alias("adminLoginId"),
					cb.max(c.get("bdmCode")).alias("bdmCode"),
					cb.max(c.get("sourceType")).alias("sourceType"),
					cb.max(c.get("customerCode")).alias("customerCode"),
					cb.max(c.get("brokerBranchName")).alias("brokerBranchName"),
					cb.max(c.get("brokerBranchCode")).alias("brokerBranchCode"),
					cb.max(c.get("companyName")).alias("companyName"),
					cb.max(c.get("productName")).alias("productName"),
					cb.max(c.get("sectionName")).alias("sectionName"),
					cb.max(c.get("commissionType")).alias("commissionType"),
					cb.max(c.get("commissionTypeDesc")).alias("commissionTypeDesc"),
					cb.max(c.get("sourceCountryDesc")).alias("sourceCountryDesc"),
					cb.max(c.get("destinationCountryDesc")).alias("destinationCountryDesc"),
					cb.max(c.get("actualPremiumLc")).alias("actualPremiumLc"),
					cb.max(c.get("actualPremiumFc")).alias("actualPremiumFc"),
					cb.max(c.get("overallPremiumLc")).alias("overallPremiumLc"),
					cb.max(c.get("overallPremiumFc")).alias("overallPremiumFc"),
					cb.max(c.get("oldReqRefNo")).alias("oldReqRefNo"),
					cb.max(c.get("bankCode")).alias("bankCode"),
					cb.max(c.get("manualReferalYn")).alias("manualReferalYn"),
					cb.max(c.get("endorsementType")).alias("endorsementType"),
					cb.max(c.get("endorsementTypeDesc")).alias("endorsementTypeDesc"),
					cb.max(c.get("endorsementDate")).alias("endorsementDate"),
					cb.max(c.get("endorsementRemarks")).alias("endorsementRemarks"),
					cb.max(c.get("endorsementEffdate")).alias("endorsementEffdate"),
					cb.max(c.get("originalPolicyNo")).alias("originalPolicyNo"),
					cb.max(c.get("endtPrevPolicyNo")).alias("endtPrevPolicyNo"),
					cb.max(c.get("endtPrevQuoteNo")).alias("endtPrevQuoteNo"),
					cb.max(c.get("endtStatus")).alias("endtStatus"),
					cb.max(c.get("isFinaceYn")).alias("isFinaceYn"),
					cb.max(c.get("endtCategDesc")).alias("endtCategDesc"),
					cb.max(c.get("endtPremium")).alias("endtPremium"));

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
//				n3 = cb.equal(c.get("applicationId"), loginId);
				Expression<String> e0 = c.get("branchCode");
				n4 = e0.in(branches);
			} else if ("Broker".equalsIgnoreCase(userType) || "User".equalsIgnoreCase(userType)) {
				n3 = cb.equal(c.get("loginId"), loginId);
				Expression<String> e0 = c.get("brokerBranchCode");
				n4 = e0.in(branches);
			}
			if (searchKey.equalsIgnoreCase("ClientName")) {
				if ("issuer".equalsIgnoreCase(userType)) {

					Expression<String> e0 = cus.get("branchCode");
					n4 = e0.in(branches);
				} else if ("Broker".equalsIgnoreCase(userType) || "User".equalsIgnoreCase(userType)) {

					Expression<String> e0 = cus.get("brokerBranchCode");
					//Expression<String> e0 = c.get("branchCode");
					n4 = e0.in(branches);
				}
			}
			n5 = cb.equal(c.get("customerReferenceNo"), cus.get("customerReferenceNo"));
			if ("Broker".equalsIgnoreCase(userType) || "User".equalsIgnoreCase(userType)) {
			query.where(n1,n2,n3,n4,n5)
			.groupBy(c.get("customerReferenceNo"), cus.get("clientName"), c.get("companyId")/*,c.get("riskId")*/,
					c.get("productId"), c.get("branchCode"), c.get("requestReferenceNo"), c.get("quoteNo"),
					c.get("customerId"), c.get("travelEndDate"), c.get("travelStartDate")/*,c.get("acExecutiveId"),
				c.get("actualPremiumLc"), c.get("actualPremiumFc"), c.get("overallPremiumLc"),c.get("overallPremiumFc")*/)
			.orderBy(orderList);
			}else {
				query.where(n1,n2,n4,n5)
				.groupBy(c.get("customerReferenceNo"), cus.get("clientName"), c.get("companyId")/*,c.get("riskId")*/,
						c.get("productId"), c.get("branchCode"), c.get("requestReferenceNo"), c.get("quoteNo"),
						c.get("customerId"), c.get("travelEndDate"), c.get("travelStartDate")/*,c.get("acExecutiveId"),
					c.get("actualPremiumLc"), c.get("actualPremiumFc"), c.get("overallPremiumLc"),c.get("overallPremiumFc")*/)
				.orderBy(orderList);
			}
			if (searchKey.equalsIgnoreCase("ClientName")) {
				query.where(n1, n2,n4,n5)
				.groupBy(c.get("customerReferenceNo"), cus.get("clientName"), c.get("companyId")/*,c.get("riskId")*/,
						c.get("productId"), c.get("branchCode"), c.get("requestReferenceNo"), c.get("quoteNo"),
						c.get("customerId"), c.get("travelEndDate"), c.get("travelStartDate")/*,c.get("acExecutiveId"),
						c.get("actualPremiumLc"), c.get("actualPremiumFc"), c.get("overallPremiumLc"),c.get("overallPremiumFc")*/)
		
				.orderBy(orderList);
			}
			if (searchKey.equalsIgnoreCase("EntryDate")) {
				query.where(n1,n2,n3,n4)
				.groupBy(c.get("customerReferenceNo"), cus.get("clientName"), c.get("companyId")/*,c.get("riskId")*/,
						c.get("productId"), c.get("branchCode"), c.get("requestReferenceNo"), c.get("quoteNo"),
						c.get("customerId"), c.get("travelEndDate"), c.get("travelStartDate")/*,c.get("acExecutiveId"),
						c.get("actualPremiumLc"), c.get("actualPremiumFc"), c.get("overallPremiumLc"),c.get("overallPremiumFc")*/)
			
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
	public  List<ListItemValue> getTravelCoptyQuotetListItem(CopyQuoteDropDownReq req ,String itemType) {
		List<ListItemValue> list = new ArrayList<ListItemValue>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			today = cal.getTime();
			Date todayEnd = cal.getTime();
			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ListItemValue> query=  cb.createQuery(ListItemValue.class);
			// Find All
			Root<ListItemValue> c = query.from(ListItemValue.class);
			
			//Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("branchCode")));
			
			
			// Effective Date Start Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<ListItemValue> ocpm1 = effectiveDate.from(ListItemValue.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(c.get("itemId"),ocpm1.get("itemId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1,a2);
			// Effective Date End Max Filter
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<ListItemValue> ocpm2 = effectiveDate2.from(ListItemValue.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate a3 = cb.equal(c.get("itemId"),ocpm2.get("itemId"));
			Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			effectiveDate2.where(a3,a4);
						
			// Where
			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);	
			Predicate n4 = cb.equal(c.get("companyId"), req.getInsuranceId());
			Predicate n5 = cb.equal(c.get("companyId"), "99999");
			Predicate n6 = cb.equal(c.get("branchCode"), req.getBranchCode());
			Predicate n7 = cb.equal(c.get("branchCode"), "99999");
			Predicate n8 = cb.or(n4,n5);
			Predicate n9 = cb.or(n6,n7);
			Predicate n10 = cb.equal(c.get("itemType"),itemType);
			
			if(itemType.equalsIgnoreCase("COPY_QUOTE_BY_TRAVEL"))
				query.where(n1,n2,n3,n8,n9,n10).orderBy(orderList);
			else
				query.where(n1,n2,n3,n4,n9,n10).orderBy(orderList);
			// Get Result
			TypedQuery<ListItemValue> result = em.createQuery(query);
			list = result.getResultList();
			
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getItemCode()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(ListItemValue :: getItemValue));
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return list ;
	}
	
	private static <T> java.util.function.Predicate<T> distinctByKey(java.util.function.Function<? super T, ?> keyExtractor) {
	    Map<Object, Boolean> seen = new ConcurrentHashMap<>();
	    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

	@Override
	public CopyQuoteSuccessRes travelEndt(CopyQuoteReq req, List<String> branches, String loginId) {
		CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
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
            //Copy Quote E service Travel Details
			EserviceTravelDetails savedata=eserviceTravelCopyquote(req,refNo,branches,loginId,customerId,quoteNo,custRefNo);
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
	
	//Eservice Motor Copy Quote
	public EserviceTravelDetails eserviceTravelCopyquote(CopyQuoteReq req, String refNo, List<String> branches,String loginId,String customerId,String quoteNo,String custRefNo) {
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		EserviceTravelDetails savedata = new EserviceTravelDetails();
		try {
			String userType = req.getUserType();
			String branchCode = "";

			List<EserviceTravelDetails> motor=null;
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
				List<EserviceTravelDetails> motors = repo.findByOriginalPolicyNoAndRiskId(req.getPolicyNo(), 1);
				pendingcount = motors.stream().filter(m -> m.getEndtStatus().equals("P")).count();
				if (pendingcount > 0) {
					 List<EserviceTravelDetails> pendingData = motors.stream().filter(m->m.getEndtStatus().equals("P")).collect(Collectors.toList());
					if (!pendingData.get(0).getEndorsementType().equals(Integer.valueOf(req.getEndtTypeId()))) {
						deletePreviousEndo(req,pendingData);
						count--;
						pendingcount=0;
					}
				}
			}
			if(count>0) {
				List<EserviceTravelDetails> motors=repo.findByOriginalPolicyNoAndRiskId(req.getPolicyNo(),1);
				//Compare
				motors.sort(new Comparator<EserviceTravelDetails>() {
					@Override
					public int compare(EserviceTravelDetails o1, EserviceTravelDetails o2) {
						// TODO Auto-generated method stub
						return o1.getEndtCount().compareTo(o2.getEndtCount());
					}
				}.reversed());
				
				pendingcount = motors.stream().filter(m->m.getEndtStatus().equals("P")).count();
				if(pendingcount>0) {
					 List<EserviceTravelDetails> pendingData = motors.stream().filter(m->m.getEndtStatus().equals("P")).collect(Collectors.toList());
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
//						prevPolicyNo=req.getPolicyNo();
//						prevQuoteNo =motor.get(0).getEndtPrevQuoteNo();
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
			
//			List<Tuple> list = copyQuoteSearchDetails(searchKey, searchValue, companyId, loginId, userType,
//					branches);
			List<EserviceTravelDetails> motors=repo.findByQuoteNoOrderByRiskIdAsc(prevQuoteNo);
			++count;
			if (motors.size() > 0) {
				for (EserviceTravelDetails data : motors) {
					EndtTypeMaster entMaster=ratingutil.getEndtMasterData(req.getInsuranceId(),req.getProductId(),req.getEndtTypeId());
							//endtTypeRepo.findByCompanyIdAndProductIdAndStatusAndEndtTypeIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqual(req.getInsuranceId(), Integer.parseInt(req.getProductId()), "Y",Integer.parseInt(req.getEndtTypeId()), new Date(), new Date());
					savedata = dozerMapper.map(data, EserviceTravelDetails.class);
					savedata.setEntryDate(new Date());
					savedata.setCreatedBy(req.getLoginId());
					savedata.setUpdatedBy(req.getLoginId());
					savedata.setUpdatedDate(new Date());
					savedata.setRequestReferenceNo(newRequestNo);
					//savedata.setCustomerReferenceNo(newCustRefNo);
					savedata.setCustomerReferenceNo(data.getCustomerReferenceNo());
					savedata.setCustomerId(newCustId);
					savedata.setOldReqRefNo(req.getRequestReferenceNo());
//					if (req.getUserType().equalsIgnoreCase("Broker")|| (req.getUserType().equalsIgnoreCase("User"))) {
//						branchCode = req.getBranchCode();
//						savedata.setApplicationId("1");
//						savedata.setBrokerBranchCode(branchCode);
//
//					} else if ("issuer".equalsIgnoreCase(userType)) {
//						savedata.setApplicationId(req.getLoginId());
//						branchCode = req.getBranchCode();
//						savedata.setBranchCode(branchCode);
//					}
					savedata.setApplicationId(req.getApplicationId());
					if(req.getLoginId()==null || StringUtils.isBlank(req.getLoginId())) {
						savedata.setLoginId(data.getLoginId());
					}else {
						savedata.setLoginId(req.getLoginId());
					}
					savedata.setSubUserType(req.getSubUserType());
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
	
			}

			System.out.println("*************EserviceTravelDetails************");
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
				 policyCoverDataEndocopyQuote(req, refNo, quoteNo, loginId, prevPolicyNo,prevQuoteNo, count,preEndtId);

				// Copy COVER_DOCUMENT_UPLOAD_DETAILS
				coverDocumentUploadDetailsEndoCopyquote(req, refNo, quoteNo, customerId, loginId,prevPolicyNo,prevQuoteNo,count);
			
//				// Copy ESERVICE_CUSTOMER_DETAILS
//				eserviceCustDetailsEndoCopyquote(req, refNo, quoteNo, customerId, loginId,prevPolicyNo,prevQuoteNo,count,custRefNo);
				
				// Copy TravelPassengerDetails
				travelPassengerDetailsEndoCopyquote(req, refNo, quoteNo, customerId, loginId,prevPolicyNo,prevQuoteNo,count,custRefNo);
				
				//Copy EserviceTravelGroupDetails
				eserviceTravelGroupDetailsEndoCopyquote(req, refNo, quoteNo, customerId, loginId,prevPolicyNo,prevQuoteNo,count,custRefNo);
				
				// Copy ESERVICE_SECTION_DETAILS
				eserviceSectionDetailsEndoCopyquote(req, refNo, quoteNo, customerId, loginId,prevPolicyNo,prevQuoteNo,count,custRefNo);
				
				// Copy Section Data Details
				sectionDataDetailsEndoCopyquote(req, refNo, quoteNo, customerId, loginId,prevPolicyNo,prevQuoteNo,count,custRefNo);
			
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
					//List<EserviceMotorDetails> list = new ArrayList<EserviceMotorDetails>();
					// Find Latest Record
					CriteriaBuilder cb = em.getCriteriaBuilder();
					CriteriaQuery<Object> query = cb.createQuery(Object.class);
					//Find all
					Root<EserviceTravelDetails> b = query.from(EserviceTravelDetails.class);
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

	//Delete Previous Endo
	private CopyQuoteSuccessRes deletePreviousEndo(CopyQuoteReq req, List<EserviceTravelDetails> motors) {
		CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
		try {
			String quoteNo=motors.get(0).getQuoteNo();
			// Delete Old Record
			
			//E service Motor Details
			if(motors.size()>0  && motors!=null) {
				repo.deleteAll(motors);
			}
			//E service Customer Details 
			HomePositionMaster homeData=homePosistionRepo.findByQuoteNo(quoteNo);
			String customerId=homeData.getCustomerId();
			
			PersonalInfo personalInfoData=personalInforepo.findByCustomerId(customerId);
//			EserviceCustomerDetails custData = custRepo.findByCustomerReferenceNo(personalInfoData.getCustomerReferenceNo());
//			if (custData!=null) {
//				custRepo.delete(custData);
//			}
			//Personal Info
			if(personalInfoData!=null) {
				personalInforepo.delete(personalInfoData);
			}
			//Home Position master
			if(homeData!=null) {
				homePosistionRepo.delete(homeData);				}
			//Policy Cover Data
			List<PolicyCoverData> policyCoverData = policyCoverDataRepo.findByQuoteNo(quoteNo);
			if (policyCoverData.size() > 0 && policyCoverData!=null) {
				policyCoverDataRepo.deleteAll(policyCoverData);
			}
			
			//Cover Document Upload Details
			List<DocumentTransactionDetails> coverDocList = coverDocUploadDetails.findByQuoteNo(quoteNo);
			if (coverDocList.size() > 0 && coverDocList!=null) {
				coverDocUploadDetails.deleteAll(coverDocList);
			}
			//Travel Passenger Details
			List<TravelPassengerDetails> traList = traPassDetailsRepo.findByQuoteNo(quoteNo);
			if (traList.size() > 0 && traList!=null) {
				traPassDetailsRepo.deleteAll(traList);
			}
			//Eservice Section Details
			List<EserviceSectionDetails> sectionList = eserSecRepo.findByQuoteNo(quoteNo);
			if (sectionList.size() > 0 && sectionList!=null) {
				eserSecRepo.deleteAll(sectionList);
			}
			
			// Section Data Details
			List<SectionDataDetails> secDataList = sectionDataRepo.findByQuoteNo(quoteNo);
			if (secDataList.size() > 0 && secDataList!=null) {
				sectionDataRepo.deleteAll(secDataList);
			}
			// Eservice Travel Group Details
			List<EserviceTravelGroupDetails> eseTraGrpList = groupRepo.findByQuoteNo(quoteNo);
			if (eseTraGrpList.size() > 0 && eseTraGrpList!=null) {
				groupRepo.deleteAll(eseTraGrpList);
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return res;

	}
	
	private CopyQuoteSuccessRes eserviceTravelGroupDetailsEndoCopyquote(CopyQuoteReq req, String refNo, String quoteNo, String customerId,String loginId, String prevPolicyNo, String prevQuoteNo, Integer count,String custRefNo) {
		CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
		EserviceTravelGroupDetails savedata = new EserviceTravelGroupDetails();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			EndtTypeMaster entMaster =ratingutil.getEndtMasterData(req.getInsuranceId(),req.getProductId(),req.getEndtTypeId());
					/*endtTypeRepo.findByCompanyIdAndProductIdAndStatusAndEndtTypeIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqual(
					req.getInsuranceId(), Integer.parseInt(req.getProductId()), "Y",
					Integer.parseInt(req.getEndtTypeId()), new Date(), new Date());
			*/
			List<EserviceTravelGroupDetails> traData = groupRepo.findByQuoteNoAndStatusNot(prevQuoteNo,"D");
			if (traData.size()>0) { 
				for(EserviceTravelGroupDetails data:traData) {
					savedata = dozerMapper.map(data, EserviceTravelGroupDetails.class);
					savedata.setEntryDate(new Date());
					savedata.setRequestReferenceNo(refNo);
					savedata.setCustomerId(customerId);
					savedata.setQuoteNo(quoteNo);
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
					savedata.setPolicyNo(req.getPolicyNo() + "-" + count);
					groupRepo.saveAndFlush(savedata);
				}
			}
		
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return res;

		
	}
	
	private CopyQuoteSuccessRes travelPassengerDetailsEndoCopyquote(CopyQuoteReq req, String refNo, String quoteNo, String customerId,String loginId, String prevPolicyNo, String prevQuoteNo, Integer count,String custRefNo) {
		CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
		TravelPassengerDetails savedata = new TravelPassengerDetails();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			EndtTypeMaster entMaster =ratingutil.getEndtMasterData(req.getInsuranceId(),req.getProductId(),req.getEndtTypeId());
					/*endtTypeRepo.findByCompanyIdAndProductIdAndStatusAndEndtTypeIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqual(
					req.getInsuranceId(), Integer.parseInt(req.getProductId()), "Y",
					Integer.parseInt(req.getEndtTypeId()), new Date(), new Date());
			*/
			List<TravelPassengerDetails> traData = traPassDetailsRepo.findByQuoteNoAndStatusNot(prevQuoteNo,"D");
			if (traData.size()>0) { 
				for(TravelPassengerDetails data:traData) {
					savedata = dozerMapper.map(data, TravelPassengerDetails.class);
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
					};
					savedata.setSubUserType(req.getSubUserType());
					traPassDetailsRepo.saveAndFlush(savedata);
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
					if (eserSec != null && eserSec.size()>0 ) {
						for (EserviceSectionDetails data : eserSec) {
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

//					List<SectionDataDetails> eserSec = sectionDataRepo.findByQuoteNoAndStatusOrderByRiskIdAsc(prevQuoteNo,"Y");
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



	/*private CopyQuoteSuccessRes eserviceCustDetailsEndoCopyquote(CopyQuoteReq req, String refNo, String quoteNo, String customerId,String loginId, String prevPolicyNo, String prevQuoteNo, Integer count,String custRefNo) {
		CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
		EserviceCustomerDetails savedata = new EserviceCustomerDetails();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			EndtTypeMaster entMaster = endtTypeRepo.findByCompanyIdAndProductIdAndStatusAndEndtTypeIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqual(
					req.getInsuranceId(), Integer.parseInt(req.getProductId()), "Y",
					Integer.parseInt(req.getEndtTypeId()), new Date(), new Date());
			HomePositionMaster homeData=homePosistionRepo.findByQuoteNo(prevQuoteNo);
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

		
	}
	*/

	// Policy Cover Data Enst Copy Quote
	public CopyQuoteSuccessRes policyCoverDataEndocopyQuote(CopyQuoteReq req, String refNo, String quoteNo,
			String loginId, String prevPolicyNo, String prevQuoteNo, Integer count,Integer preEndtId) {
		CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
		PolicyCoverData savedata = new PolicyCoverData();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			CoverMaster coverdata = null;
			EndtTypeMaster entMaster =ratingutil.getEndtMasterData(req.getInsuranceId(),req.getProductId(),req.getEndtTypeId()); /*endtTypeRepo
					.findByCompanyIdAndProductIdAndStatusAndEndtTypeIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqual(
							req.getInsuranceId(), Integer.parseInt(req.getProductId()), "Y",
							Integer.parseInt(req.getEndtTypeId()), new Date(), new Date());*/
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
					savedata.setStatus("E");
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
				if ("Y".equalsIgnoreCase(endtFeeYn)) {
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


	//Home Position Master Endt Copy Quote
			@Transactional
			public CopyQuoteSuccessRes homeEndoCopyQuote(CopyQuoteReq req,String refNo,String customerId,String quoteNo,String loginId,	String prevPolicyNo,
			String prevQuoteNo,Integer count,String custRefNo) {
				CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
				HomePositionMaster savedata = new HomePositionMaster();
				DozerBeanMapper dozerMapper = new DozerBeanMapper();
				
				try {
					EndtTypeMaster entMaster=ratingutil.getEndtMasterData(req.getInsuranceId(),req.getProductId(),req.getEndtTypeId());
							/*endtTypeRepo.findByCompanyIdAndProductIdAndStatusAndEndtTypeIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqual(req.getInsuranceId(), Integer.parseInt(req.getProductId()), "Y",Integer.parseInt(req.getEndtTypeId()),new Date(), new Date());*/
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
					savedata.setEndtPremiumTax(BigDecimal.ZERO);
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
				
					System.out.println("*************HomePositionMaster************");
					System.out.println("QUOTE NO:"+quoteNo);
					System.out.println("Customer Id:"+customerId);
					System.out.println("Reference No:"+refNo);
					System.out.println("PreQuoteNo:"+prevPolicyNo);
					System.out.println("OriginalPoicyNo:"+req.getPolicyNo());
					System.out.println("Policy No:"+req.getPolicyNo()+"-"+count);
					
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
			EndtTypeMaster entMaster=ratingutil.getEndtMasterData(req.getInsuranceId(),req.getProductId(),req.getEndtTypeId());/*endtTypeRepo.findByCompanyIdAndProductIdAndStatusAndEndtTypeIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqual(req.getInsuranceId(), Integer.parseInt(req.getProductId()), "Y",Integer.parseInt(req.getEndtTypeId()), new Date(), new Date());*/
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
			System.out.println("*************PersonalInfo************");
			System.out.println("Old QUOTE NO:"+prevQuoteNo);
			System.out.println("Old Customer Id:"+olsCustomerId);
			System.out.println("New Customer Id:"+customerId);
//			System.out.println("Old Reference No:"+refNo);
//			System.out.println("QUOTE NO:"+quoteNo);
//			System.out.println("New Customer Id:"+newCustId);
//			System.out.println("Reference No:"+newRequestNo);
			System.out.println("PreQuoteNo:"+prevPolicyNo);
			System.out.println("OriginalPoicyNo:"+req.getPolicyNo());
			System.out.println("Policy No:"+req.getPolicyNo()+"-"+count);
			System.out.println("**********************************************");
			//res.setSuccessId(customerId);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return res;
	}
	
	// Cover Document upload Details Enst Copy Quote
	public CopyQuoteSuccessRes coverDocumentUploadDetailsEndoCopyquote(CopyQuoteReq req, String refNo,
			String quoteNo, String customerId, String loginId, String prevPolicyNo, String prevQuoteNo,
			Integer count) {
		CopyQuoteSuccessRes res = new CopyQuoteSuccessRes();
		DocumentTransactionDetails savedata = new DocumentTransactionDetails();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			EndtTypeMaster entMaster =ratingutil.getEndtMasterData(req.getInsuranceId(),req.getProductId(),req.getEndtTypeId()); /*endtTypeRepo.findByCompanyIdAndProductIdAndStatusAndEndtTypeIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqual(
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
		public List<Tuple> getTravelReportDetails(GetallPolicyReportsReq req) {
			List<Tuple> list = new ArrayList<Tuple>();
			
			try {
				
				 DatabaseMetaData metaData = jasperConfiguration.getDataSourceForJasper().getConnection().getMetaData();
		         String databaseProductName = metaData.getDatabaseProductName();
		         // Check if the database product name contains the word "Oracle"
		            isOracle = databaseProductName.contains("Oracle");

		            // Check if the database product name contains the word "MySQL"
		            isMySQL = databaseProductName.contains("MySQL");
		    
		            String dateFormat = "";
		            if(isOracle) {
		            	
		            	dateFormat = "dd-MM-yy";
		        	 
		         	} else if (isMySQL) {
		        	 
		         		dateFormat = "yyyy-MM-dd";
		         	}
		            DateFormat dateForm = new SimpleDateFormat(dateFormat);  
		            
				
				CriteriaBuilder cb = em.getCriteriaBuilder(); 
				CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class); 
				
				Root<HomePositionMaster> hpm = query.from(HomePositionMaster.class);
				Root<PersonalInfo> pif = query.from(PersonalInfo.class);
			
				//broker name
				Subquery<String> brokerName = query.subquery(String.class); 
				Root<LoginUserInfo> lui = brokerName.from(LoginUserInfo.class);
				brokerName.select(lui.get("userName"));
				Predicate b1 = cb.equal( lui.get("loginId"), hpm.get("loginId"));
				brokerName.where(b1);
				
				//User type
				Subquery<String> userType = query.subquery(String.class); 
				Root<LoginMaster> lm = userType.from(LoginMaster.class);
				userType.select(lm.get("userType"));
				Predicate a1 = cb.equal( lm.get("loginId"), req.getLoginId());
				userType.where(a1);
				
				//SubUser type
				Subquery<String> subUserType = query.subquery(String.class); 
				Root<LoginMaster> lms = subUserType.from(LoginMaster.class);
				subUserType.select(lms.get("subUserType"));
				Predicate c1 = cb.equal( lms.get("loginId"), req.getLoginId());
				subUserType.where(c1);
				
				//paymentId
				Subquery<String> paymentId = query.subquery(String.class); 
				Root<PaymentInfo> pi = paymentId.from(PaymentInfo.class);
				paymentId.select(pi.get("paymentId"));
				Predicate d1 = cb.equal( pi.get("quoteNo"), hpm.get("quoteNo"));
				Predicate d2 = cb.equal( pi.get("paymentStatus"), "ACCEPTED");
				paymentId.where(d1,d2);
				
				//CurrencyId
				Subquery<String> currencyId = query.subquery(String.class); 
				Root<InsuranceCompanyMaster> icm = currencyId.from(InsuranceCompanyMaster.class);
				currencyId.select(icm.get("currencyId"));
				Predicate e1 = cb.equal( icm.get("companyId"), hpm.get("companyId"));
				currencyId.where(e1);
				
				//overallPremiumLc
				Subquery<Double> overallPremiumLc = query.subquery(Double.class); 
				Root<TravelPassengerDetails> tpdlc = overallPremiumLc.from(TravelPassengerDetails.class);
				overallPremiumLc.select(cb.sum(tpdlc.get("overallPremiumLc")));
				Predicate j1 = cb.equal( tpdlc.get("quoteNo"), hpm.get("quoteNo"));
				overallPremiumLc.where(j1);
				
				//overallPremiumFc
				Subquery<Double> overallPremiumFc = query.subquery(Double.class); 
				Root<TravelPassengerDetails> tpdfc = overallPremiumFc.from(TravelPassengerDetails.class);
				overallPremiumFc.select(cb.sum(tpdfc.get("overallPremiumFc")));
				Predicate k1 = cb.equal( tpdfc.get("quoteNo"), hpm.get("quoteNo"));
				overallPremiumFc.where(k1);
				
				Expression<Object> premium = cb.selectCase().when(hpm.get("currency").in(currencyId==null?null:currencyId), overallPremiumLc)
						.otherwise(overallPremiumFc);
				
				//policyTypeDesc
				Subquery<String> policyTypeDesc = query.subquery(String.class); 
				Root<TravelPassengerDetails> tpd = policyTypeDesc.from(TravelPassengerDetails.class);
				policyTypeDesc.select(tpd.get("travelCoverDesc")).distinct(true);
				Predicate i1 = cb.equal( tpd.get("quoteNo"), hpm.get("quoteNo"));
				policyTypeDesc.where(i1);
				
				//count
				Subquery<Long> count = query.subquery(Long.class); 
				Root<TravelPassengerDetails> c = count.from(TravelPassengerDetails.class);
				count.select(cb.count(c));
				Predicate l1 = cb.equal( c.get("quoteNo"), hpm.get("quoteNo"));
				count.where(l1);
				
				query.multiselect(
						hpm.get("loginId").alias("loginId"),
						hpm.get("quoteNo").alias("quoteNo"),	
						hpm.get("policyNo").alias("policyNo"),	
						cb.upper(cb.concat(cb.concat(pif.get("titleDesc"),  "."), pif.get("clientName"))).alias("customerName"),
						policyTypeDesc.alias("policyTypeDesc"),
						cb.function("ROUND", Double.class, premium, cb.literal(2)).alias("premium"),   //round 2
						count.alias("passengerCount"), //travel only
						hpm.get("inceptionDate").alias("startDate"),
						hpm.get("expiryDate").alias("endDate"),
						hpm.get("effectiveDate").alias("issueDate"),
						cb.upper(hpm.get("branchName")).alias("branchName"),
						cb.upper(brokerName).alias("brokerName"),
						userType.alias("userType"),
						subUserType.alias("subUserType"),
						hpm.get("currency").alias("currency"),
						hpm.get("paymentType").alias("paymentType"),
						paymentId.alias("paymentId"),
						hpm.get("debitNoteNo").alias("debitNoteNo"),
						cb.function("ROUND", BigDecimal.class, hpm.get("commissionPercentage"), cb.literal(1)).alias("commissionPercentage"), 
						cb.function("ROUND", BigDecimal.class, hpm.get("commission"), cb.literal(2)).alias("commissionAmount")
						
						); 
				//agencyCode
				Subquery<String> agencyCode = query.subquery(String.class); 
				Root<LoginMaster> log = agencyCode.from(LoginMaster.class);
				agencyCode.select(log.get("agencyCode")).distinct(true);
				
				Subquery<String> agency = query.subquery(String.class); 
				Root<LoginMaster> ag1 = agency.from(LoginMaster.class);
				agency.select(ag1.get("agencyCode"));
				Predicate h2 =  cb.equal(ag1.get("loginId"), req.getLoginId());
				agency.where(h2);
				
				Predicate f1 =  log.get("oaCode").in(agency==null?null:agency);
				agencyCode.where(f1);
				
				//agencyCode
				Subquery<String> agencyCode1 = query.subquery(String.class); 
				Root<LoginMaster> ag = agencyCode1.from(LoginMaster.class);
				agencyCode1.select(ag.get("agencyCode"));
				Predicate g2 =  cb.equal(ag.get("loginId"), req.getLoginId());
				agencyCode1.where(g2);
				
				List<Predicate> predicates = new ArrayList<Predicate>();
				predicates.add(cb.equal(hpm.get("customerId"), pif.get("customerId")));
				predicates.add(cb.equal(hpm.get("productId"), req.getProductId()));
				predicates.add(cb.equal(hpm.get("companyId"), req.getInsuranceId()));
				predicates.add(cb.equal(hpm.get("branchCode"), req.getBranchCode()));
				predicates.add(cb.equal(hpm.get("status"), "P"));
				predicates.add(cb.isNotNull(hpm.get("policyNo")));
				
				predicates.add(cb.between(hpm.get("inceptionDate"),dateForm.parse(dateForm.format(req.getStartDate())), dateForm.parse(dateForm.format(req.getEndDate()))));		
				
				Predicate ex1 = cb.equal(cb.selectCase().when(subUserType.in("both"), "1"), "1") ;
				Predicate ex2 = cb.equal(cb.selectCase().when(subUserType.in("low","high"), hpm.get("applicationId")), req.getLoginId());
				Predicate ex3 = cb.selectCase().when(cb.equal(userType, "Broker"), hpm.get("agencyCode")).in(agencyCode)	;		
				Predicate ex4 = hpm.get("agencyCode").in(agencyCode1);

				predicates.add(cb.or(ex1,ex2,ex3,ex4));
				
				
				query.where(predicates.toArray(new Predicate[0]));
			
				TypedQuery<Tuple> result = em.createQuery(query);
				list = result.getResultList();
				
				
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Log Details" + e.getMessage());
				return null;
			}
			return list;
		}
		public boolean isOracle() {
	        return isOracle;
	    }

	    public boolean isMySQL() {
	        return isMySQL;
	    }

		@Override
		public GetMotorProtfolioPendingRes getTravelProtfolioPending(ExistingQuoteReq req,
				List<String> branches, Date startDate, int limit, int offset, String status) {
			
			GetMotorProtfolioPendingRes resp = new GetMotorProtfolioPendingRes();
			List<PortfolioPendingGridCriteriaRes> portfolio = new ArrayList<PortfolioPendingGridCriteriaRes>();
			try {
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<PortfolioPendingGridCriteriaRes> query = cb.createQuery(PortfolioPendingGridCriteriaRes.class);

				Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
				Root<EserviceTravelDetails> m = query.from(EserviceTravelDetails.class);
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
						cb.max(m.get("travelStartDate")).alias("inceptionDate"),
						cb.max(m.get("travelEndDate")).alias("expiryDate"),
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
				if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
					n5 = cb.equal(m.get("loginId"), req.getLoginId());
					Expression<String> e0 = m.get("brokerBranchCode");
					n6 = e0.in(branches);
					query.where(n1, n2, n3, n4, n5, n6,n7,n8).groupBy((m.get("originalPolicyNo")),m.get("endtStatus")) ;

				} else {
					Predicate n10 = cb.equal(m.get("applicationId"), req.getApplicationId());
					if(StringUtils.isNotBlank(req.getBdmCode())){
						n5 = cb.equal(m.get("bdmCode"), req.getBdmCode());
					}else {
						n5 = cb.equal(m.get("loginId"), req.getLoginId());
					}
					Expression<String> e0 = m.get("branchCode");
					n6 = e0.in(branches);
					query.where(n1, n2, n3, n4, n5, n6,n7,n8, n10).groupBy((m.get("originalPolicyNo")),m.get("endtStatus")) ;

				}

			
				// Get Result
				TypedQuery<PortfolioPendingGridCriteriaRes> result = em.createQuery(query);
				result.setFirstResult(limit * offset);
				result.setMaxResults(offset);
				portfolio = result.getResultList();
				portfolio = portfolio.stream().filter(o -> !o.getIdsCount().equals(0L))
						.collect(Collectors.toList());
				
				resp.setPending(portfolio);
				resp.setCount(totalProtfolioPending(req,branches,startDate, limit,offset, status));
			
				
				
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
				Root<EserviceTravelDetails> m = query.from(EserviceTravelDetails.class);
		//		Root<HomePositionMaster> h = query.from(HomePositionMaster.class);
				
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
				if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
					Expression<String> e0 = m.get("brokerBranchCode");
					n6 = e0.in(branches);
					query.where(n1, n2, n3, n4, n5, n6).groupBy((m.get("originalPolicyNo")),m.get("endtStatus")) ;

				} else {
					Predicate n10 = cb.equal(m.get("applicationId"), req.getApplicationId());
					if(StringUtils.isNotBlank(req.getBdmCode())){
						n5 = cb.equal(m.get("bdmCode"), req.getBdmCode());
					}else {
						n5 = cb.equal(m.get("loginId"), req.getLoginId());
					}
					Expression<String> e0 = m.get("branchCode");
					n6 = e0.in(branches);
					query.where(n1, n2, n3, n4, n5, n6, n10).groupBy((m.get("originalPolicyNo")),m.get("endtStatus")) ;

				}

			
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
		public synchronized GetTravelAdminReferalPendingDetailsRes getTravelAdminReferalPendingDetails( RevertGridReq req, int limit,
				int offset , String status) {
			
			GetTravelAdminReferalPendingDetailsRes resp = new GetTravelAdminReferalPendingDetailsRes();
			List<ReferalGridCriteriaAdminRes> referrals = new ArrayList<ReferalGridCriteriaAdminRes>();
			try {
				resp.setCount(0l);
				
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<ReferalGridCriteriaAdminRes> query = cb.createQuery(ReferalGridCriteriaAdminRes.class);

				Root<EserviceTravelDetails> m = query.from(EserviceTravelDetails.class);
				Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
				
				query.multiselect( 
						c.get("customerReferenceNo").alias("customerReferenceNo"),
						c.get("idNumber").alias("idNumber"),
						c.get("clientName").alias("clientName"),
						// Vehicle Info
						m.get("companyId").alias("companyId"),
						m.get("productId").alias("productId"),
						m.get("branchCode").alias("branchCode"),
						m.get("requestReferenceNo").alias("requestReferenceNo") , 
						m.get("quoteNo").alias("quoteNo") ,
						m.get("customerId").alias("customerId") ,
						m.get("travelStartDate").alias("policyStartDate"),
						m.get("travelEndDate").alias("policyEndDate"), m.get("rejectReason").alias("rejectReason")
						,
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
				
			
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(m.get("updatedDate")));
				
				Predicate n1 = cb.equal(  c.get("customerReferenceNo"),  m.get("customerReferenceNo"));
				Predicate n2 = cb.equal(  m.get("companyId"),  req.getInsuranceId()) ;
				Predicate n3 = cb.equal(  m.get("productId"),  req.getProductId());
				Predicate n4 = cb.equal(m.get("status"),status );
		
				Predicate n14 = null;
				if(req.getType().equalsIgnoreCase("Q"))
						n14 = cb.isNull(m.get("endorsementTypeDesc")); 
				else if (req.getType().equalsIgnoreCase("E"))
						n14 = cb.isNotNull(m.get("endorsementTypeDesc")); 
				
				query.where(n1, n2, n3, n4, n14).orderBy(orderList);
			
				// Get Result
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

				Root<EserviceTravelDetails> m = query.from(EserviceTravelDetails.class);
				Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
				
				query.multiselect(cb.count(m));

				Predicate n1 = cb.equal(  c.get("customerReferenceNo"),  m.get("customerReferenceNo"));
				Predicate n2 = cb.equal(  m.get("companyId"),  req.getInsuranceId()) ;
				Predicate n3 = cb.equal(  m.get("productId"),  req.getProductId());
				Predicate n4 = cb.equal(m.get("status"),status );
		
				
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
		public synchronized List<ReferalGridCriteriaAdminRes> getTravelAdminReferalPendingDetailsCount( RevertGridReq req,String status) {
			List<ReferalGridCriteriaAdminRes> referrals = new ArrayList<ReferalGridCriteriaAdminRes>();
			try {
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<ReferalGridCriteriaAdminRes> query = cb.createQuery(ReferalGridCriteriaAdminRes.class);

				// Find All
				Root<EserviceTravelDetails> m = query.from(EserviceTravelDetails.class);
				Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
				
				
				// Select
				query.multiselect(  cb.max(m.get("totalPassengers")).as(Long.class).alias("idsCount"),
						// Customer Info
						cb.max(c.get("customerReferenceNo")).alias("customerReferenceNo"),
						cb.max( c.get("idNumber")).alias("idNumber"),
						cb.max(c.get("clientName")).alias("clientName"),
						// Vehicle Info
						cb.max(m.get("companyId")).alias("companyId"),
						cb.max(m.get("productId")).alias("productId"),
						cb.max(m.get("branchCode")).alias("branchCode"),
						cb.max( m.get("requestReferenceNo")).alias("requestReferenceNo") , 
						cb.selectCase().when(m.get("quoteNo").isNotNull(), m.get("quoteNo")).otherwise( m.get("quoteNo")).alias("quoteNo") ,
						cb.selectCase().when(m.get("customerId").isNotNull(), m.get("customerId")).otherwise( m.get("customerId")).alias("customerId") ,
						cb.max(m.get("travelStartDate")).alias("policyStartDate"),
						cb.max(m.get("travelEndDate")).alias("policyEndDate") , cb.max(m.get("rejectReason")).alias("rejectReason")
						,
						cb.max(m.get("adminRemarks")).alias("adminRemarks"),
						cb.max(m.get("status")).alias("status"),
						cb.max(m.get("entryDate")).alias("entryDate"),
						cb.max(m.get("referalRemarks")).alias("referalRemarks"),
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
				Predicate n1 = cb.equal(  c.get("customerReferenceNo"),  m.get("customerReferenceNo"));
				Predicate n2 = cb.equal(  m.get("companyId"),  req.getInsuranceId()) ;
				Predicate n3 = cb.equal(  m.get("productId"),  req.getProductId());
				Predicate n4 = cb.equal(m.get("status"),status );
				
				String branchCode ="";
				if (StringUtils.isNotBlank(req.getBranchCode()) && !"99999".equals(req.getBranchCode())) {
					branchCode = req.getBranchCode();
					Predicate n6 =cb.equal(m.get("branchCode"),branchCode);
					query.where(n1, n2, n3, n4, n6).orderBy(orderList)
					.groupBy(m.get("quoteNo"), m.get("customerId"),m.get("updatedDate"));
					
				} else {
					query.where(n1, n2, n3, n4).orderBy(orderList).groupBy(m.get("quoteNo"), m.get("customerId"),
							m.get("updatedDate"));
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
		public List<GetExistingBrokerListRes> getTravelProtfolioDropdownPending(ExistingBrokerUserListReq req,
				Date today) {
			List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
			List<Tuple> list = new ArrayList<Tuple>();
			try {

				if (!("issuer".equalsIgnoreCase(req.getUserType()))) {
					CriteriaBuilder cb = em.getCriteriaBuilder();
					CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

					Root<EserviceTravelDetails> m = query.from(EserviceTravelDetails.class);
					Root<LoginUserInfo> us = query.from(LoginUserInfo.class);
					query.multiselect( m.get("loginId").alias("code"),us.get("userName").alias("codeDesc"),
					m.get("sourceType").alias("type"));

					// Find All
					Subquery<String> agencyCode = query.subquery(String.class);
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
						n12 = cb.equal(m.get("brokerCode"), agencyCode);
					} else if ("User".equalsIgnoreCase(req.getUserType())) {
						n12 = cb.equal(m.get("agencyCode"), agencyCode);
					}
					Predicate n13 = cb.isNotNull(m.get("sourceType"));
					Predicate n14 = cb.isNotNull(m.get("loginId"));
					Predicate us1 = cb.equal(us.get("loginId"), m.get("loginId"));
					query.where( n2,n3, n4, n5, n12, n13, n14, us1);

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
					resList = getPortfolioPendingIssuerTravel(req, today);
				}

			} catch (Exception e) {
				e.printStackTrace();
				log.info("Log Details" + e.getMessage());
				return null;
			}
			return resList;

		}

		private List<GetExistingBrokerListRes> getPortfolioPendingIssuerTravel(ExistingBrokerUserListReq req,
				Date today) {
			List<Tuple> list = new ArrayList<Tuple>();
			List<Tuple> list1 = new ArrayList<Tuple>();
			List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
			try {
				{
					CriteriaBuilder cb = em.getCriteriaBuilder();
					CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

					Root<EserviceTravelDetails> m = query.from(EserviceTravelDetails.class);

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

					Root<EserviceTravelDetails> m1 = query1.from(EserviceTravelDetails.class);
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
					query1.where(n1, n2, n3, n4, n5, n6, us1);

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

		@Override  //dropdown
		public List<GetExistingBrokerListRes> getBrokerUserListLapsedTravel(ExistingBrokerUserListReq req, Date today,Date before30) {
			
			
			List<Tuple> list = new ArrayList<Tuple>();
			List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
			try {
				if(!("issuer".equalsIgnoreCase(req.getUserType()))){		
					
					CriteriaBuilder cb = em.getCriteriaBuilder();
					CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);
	
					Root<EserviceTravelDetails> m = query.from(EserviceTravelDetails.class);
					Root<LoginUserInfo> us = query.from(LoginUserInfo.class);
					query.multiselect(
							m.get("loginId").alias("code"),
							us.get("userName").alias("codeDesc"),
							m.get("sourceType")
							.alias("type"));
	
					// Find All
					Subquery<Long> agencyCode = query.subquery(Long.class);
					Root<LoginMaster> ocpm1 = agencyCode.from(LoginMaster.class);
					agencyCode.select(ocpm1.get("agencyCode"));
					Predicate a1 = cb.equal(ocpm1.get("loginId"), req.getLoginId());
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
				 
				 Root<EserviceTravelDetails> m = query.from(EserviceTravelDetails.class); 
				 
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
				predics.add(cb.lessThanOrEqualTo(m.get("updatedDate"), before30)); 	//lapsed
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
//						 res.setType(data.get("type")==null?"":	data.get("type").toString());
						 String type = data.get("type") == null ? "" : data.get("type").toString();
							type = "Premia " + type;
							res.setType(type);
						 resList.add(res);
					
					 }
				 }	
				 
				 CriteriaBuilder cb1 = em.getCriteriaBuilder();
				 CriteriaQuery<Tuple> query1 = cb1.createQuery(Tuple.class);
				 
				 Root<EserviceTravelDetails> m1 = query1.from(EserviceTravelDetails.class); 
				 Root<LoginUserInfo> us = query1.from(LoginUserInfo.class);
				 query1.multiselect( 
						 m1.get("loginId").alias("code"),
						 us.get("userName").alias("codeDesc"),
						 m1.get("sourceType").alias("type"));
				 
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
		public List<GetExistingBrokerListRes> getBrokerUserListTravelRejected(ExistingBrokerUserListReq req, Date today,
				Date before30) { //Issuer
			
			List<Tuple> list = new ArrayList<Tuple>();	
			List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
			try {
				if(!("issuer".equalsIgnoreCase(req.getUserType()))){		
					CriteriaBuilder cb = em.getCriteriaBuilder();
					CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

					Root<EserviceTravelDetails> m = query.from(EserviceTravelDetails.class);
					Root<LoginUserInfo> us = query.from(LoginUserInfo.class);
					query.multiselect(
							m.get("loginId").alias("code"),
							us.get("userName").alias("codeDesc"),
							m.get("sourceType").alias("type"));

					// Find All
					Subquery<Long> agencyCode = query.subquery(Long.class);
					Root<LoginMaster> ocpm1 = agencyCode.from(LoginMaster.class);
					agencyCode.select(ocpm1.get("agencyCode"));
					Predicate a1 = cb.equal(ocpm1.get("loginId"), req.getLoginId());
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
				 
				 Root<EserviceTravelDetails> m = query.from(EserviceTravelDetails.class); 
				 
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
//						 res.setType(data.get("type")==null?"":	data.get("type").toString());
						 String type = data.get("type") == null ? "" : data.get("type").toString();
							type = "Premia " + type;
							res.setType(type);
						 resList.add(res);
					
					 }
				 }	
				 
				 CriteriaBuilder cb1 = em.getCriteriaBuilder();
				 CriteriaQuery<Tuple> query1 = cb1.createQuery(Tuple.class);
				 
				 Root<EserviceTravelDetails> m1 = query1.from(EserviceTravelDetails.class); 
				 Root<LoginUserInfo> us = query1.from(LoginUserInfo.class);
				 query1.multiselect( 
						 m1.get("loginId").alias("code"),
						 us.get("userName").alias("codeDesc"),
						 m1.get("sourceType").alias("type"));
				 
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
		public List<GetExistingBrokerListRes> getTravelReferalDropdown(ExistingBrokerUserListReq req,
				Date today,String status) {
			List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
			List<Tuple> list = new ArrayList<Tuple>();
			try {

				if (!("issuer".equalsIgnoreCase(req.getUserType()))) {
					CriteriaBuilder cb = em.getCriteriaBuilder();
					CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

					Root<EserviceTravelDetails> m = query.from(EserviceTravelDetails.class);
					Root<LoginUserInfo> us = query.from(LoginUserInfo.class);
					query.multiselect( 
							m.get("loginId").alias("code"),
							us.get("userName").alias("codeDesc"),
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
						n12 = cb.equal(m.get("brokerCode"), agencyCode.as(String.class));
					} else if ("User".equalsIgnoreCase(req.getUserType())) {
						n12 = cb.equal(m.get("agencyCode"), agencyCode);
					}
					
					Predicate n15 = null;
					if(req.getType().equalsIgnoreCase("Q"))
						n15 = cb.isNull(m.get("endorsementTypeDesc")); 
					else if (req.getType().equalsIgnoreCase("E"))
						n15 = cb.isNotNull(m.get("endorsementTypeDesc"));
					
					Predicate n13 = cb.isNotNull(m.get("sourceType"));
					Predicate n14 = cb.isNotNull(m.get("loginId"));
					Predicate us1 = cb.equal(us.get("loginId"), m.get("loginId"));
					query.where(n1, n3, n4, n5, n12, n13, n14,us1,n15);

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
					resList = getReferalIssuerTravel(req, today,status);
				}

			} catch (Exception e) {
				e.printStackTrace();
				log.info("Log Details" + e.getMessage());
				return null;
			}
			return resList;

		}

		private List<GetExistingBrokerListRes> getReferalIssuerTravel(ExistingBrokerUserListReq req,
				Date today,String status) {
			List<Tuple> list = new ArrayList<Tuple>();
			List<Tuple> list1 = new ArrayList<Tuple>();
			List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
			try {
				{
					CriteriaBuilder cb = em.getCriteriaBuilder();
					CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

					Root<EserviceTravelDetails> m = query.from(EserviceTravelDetails.class);

					query.multiselect(m.get("bdmCode").alias("code"), m.get("customerName").alias("codeDesc"),
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
//							res.setType(data.get("type") == null ? "" : data.get("type").toString());
							String type = data.get("type") == null ? "" : data.get("type").toString();
							type = "Premia " + type;
							res.setType(type);
							resList.add(res);

						}
					}
				}
				{
					CriteriaBuilder cb1 = em.getCriteriaBuilder();
					CriteriaQuery<Tuple> query1 = cb1.createQuery(Tuple.class);

					Root<EserviceTravelDetails> m1 = query1.from(EserviceTravelDetails.class);

					Root<LoginUserInfo> us = query1.from(LoginUserInfo.class);
					query1.multiselect( 
							m1.get("loginId").alias("code"),
							us.get("userName").alias("codeDesc"),
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
					
					query1.where(n1, n2, n3, n4, n5, n6, us1, n15);

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
		public List<GetExistingBrokerListRes> getAdminTravelRPDropdown(ExistingBrokerUserListReq req,
				Date today) {
			List<Tuple> list = new ArrayList<Tuple>();
			List<Tuple> list1 = new ArrayList<Tuple>();
			List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
			try {
				{
					CriteriaBuilder cb = em.getCriteriaBuilder();
					CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

					Root<EserviceTravelDetails> m = query.from(EserviceTravelDetails.class);

					query.multiselect(m.get("bdmCode").alias("code"), m.get("customerName").alias("codeDesc"),
							m.get("sourceType").alias("type"));

					// Uw Condition

					Subquery<Long> uwData = query.subquery(Long.class);
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
					query.where(n2, n3,n4,n5,n6, n8,n15);

					TypedQuery<Tuple> typedQuery = em.createQuery(query);
					list = typedQuery.getResultList();
					list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.get("code"))))
							.collect(Collectors.toList());
					if (list != null && list.size() > 0) {

						for (Tuple data : list) {
							GetExistingBrokerListRes res = new GetExistingBrokerListRes();
							res.setCode(data.get("code") == null ? "" : data.get("code").toString());
							res.setCodeDesc(data.get("codeDesc") == null ? "" : data.get("codeDesc").toString());
//							res.setType(data.get("type") == null ? "" : data.get("type").toString());
							String type = data.get("type") == null ? "" : data.get("type").toString();
							type = "Premia " + type;
							res.setType(type);
							resList.add(res);

						}
					}
				}
				{
					CriteriaBuilder cb1 = em.getCriteriaBuilder();
					CriteriaQuery<Tuple> query1 = cb1.createQuery(Tuple.class);

					Root<EserviceTravelDetails> m1 = query1.from(EserviceTravelDetails.class);
					Root<LoginUserInfo> us = query1.from(LoginUserInfo.class);
					query1.multiselect( m1.get("loginId").alias("code"),us.get("userName").alias("codeDesc"),
					m1.get("sourceType").alias("type"));

					// Uw Condition

					Subquery<Long> uwData = query1.subquery(Long.class);
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
					
					query1.where(n2, n3,n4,n5,n6,n8, us1, n15);

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
		public List<GetExistingBrokerListRes> getTravelAdminReferalDropdown(ExistingBrokerUserListReq req,
				Date today,String status) {
			List<Tuple> list = new ArrayList<Tuple>();
			List<Tuple> list1 = new ArrayList<Tuple>();
			List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
			try {
				{
					CriteriaBuilder cb = em.getCriteriaBuilder();
					CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

					Root<EserviceTravelDetails> m = query.from(EserviceTravelDetails.class);

					query.multiselect(m.get("bdmCode").alias("code"), m.get("customerName").alias("codeDesc"),
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
//							res.setType(data.get("type") == null ? "" : data.get("type").toString());
							String type = data.get("type") == null ? "" : data.get("type").toString();
							type = "Premia " + type;
							res.setType(type);
							resList.add(res);

						}
					}
				}
				{
					CriteriaBuilder cb1 = em.getCriteriaBuilder();
					CriteriaQuery<Tuple> query1 = cb1.createQuery(Tuple.class);

					Root<EserviceTravelDetails> m1 = query1.from(EserviceTravelDetails.class);

					Root<LoginUserInfo> us = query1.from(LoginUserInfo.class);
					query1.multiselect( 
							m1.get("loginId").alias("code"),
							us.get("userName").alias("codeDesc"),
							m1.get("sourceType").alias("type"));

					
					Predicate n1 = cb1.equal(m1.get("adminLoginId"), req.getApplicationId());
				//	Predicate n2 = cb1.isNotNull(m1.get("applicationId"));
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
					
					query1.where(n1,n3,n4,n5,n8,  us1, n15);

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
