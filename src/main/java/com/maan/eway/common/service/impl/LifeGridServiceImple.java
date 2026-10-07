package com.maan.eway.common.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maan.eway.admin.res.MotorGridCriteriaRes;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.EserviceLifeDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.LoginMaster;
import com.maan.eway.bean.LoginUserInfo;
import com.maan.eway.bean.UWReferralDetails;
import com.maan.eway.common.req.ExistingBrokerUserListReq;
import com.maan.eway.common.req.ExistingQuoteReq;
import com.maan.eway.common.res.GetExistingBrokerListRes;
import com.maan.eway.common.res.GetMotorProtfolioPendingRes;
import com.maan.eway.common.res.GetMotorReferalDetailsRes;
import com.maan.eway.common.res.GetRejectedQuoteDetailsRes;
import com.maan.eway.common.res.PortfolioPendingGridCriteriaRes;
import com.maan.eway.common.res.QuoteCriteriaRes;
import com.maan.eway.common.res.QuoteCriteriaResponse;
import com.maan.eway.common.res.RejectCriteriaRes;
import com.maan.eway.common.service.LifeGridService;

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
public class LifeGridServiceImple implements LifeGridService {
	
	@PersistenceContext
	private EntityManager em;

	private Logger log = LogManager.getLogger(LifeGridServiceImple.class);

	@Override
	public QuoteCriteriaResponse getLifeExistingQuoteDetails(ExistingQuoteReq req, Date startDate, Date endDate, int limit,
			int offset) {
		QuoteCriteriaResponse resp = new QuoteCriteriaResponse();
		List<QuoteCriteriaRes> existingQuotes = new ArrayList<QuoteCriteriaRes>();
		
		try {
			
			// Get Datas
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<QuoteCriteriaRes> query = cb.createQuery(QuoteCriteriaRes.class);

			// Find All
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
			Root<EserviceLifeDetails> m = query.from(EserviceLifeDetails.class);
			
			//overallPremiumLc
			Subquery<Long> overallPremiumLc = query.subquery(Long.class);
			Root<EserviceLifeDetails> ocpm1 = overallPremiumLc.from(EserviceLifeDetails.class);
			overallPremiumLc.select(cb.sum(ocpm1.get("overallPremiumLc")));
			Predicate a1 = cb.equal(ocpm1.get("requestReferenceNo"), m.get("requestReferenceNo"));
			overallPremiumLc.where(a1);
			
			//overallPremiumFc
			Subquery<Long> overallPremiumFc = query.subquery(Long.class);
			Root<EserviceLifeDetails> oc = overallPremiumFc.from(EserviceLifeDetails.class);
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
					m.get("customerCode").alias("customerId"),
					m.get("policyStartDate").alias("policyStartDate"), m.get("policyEndDate").alias("policyEndDate"),

					overallPremiumLc.as(BigDecimal.class).alias("overallPremiumLc"), 
					overallPremiumFc.as(BigDecimal.class).alias("overallPremiumFc"),
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
				
				n11 = cb.equal(m.get("loginId"), req.getLoginId());
			}

			Predicate n8 = null;
			if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
				
				n8 = cb.equal(m.get("brokerBranchCode"), req.getBrokerBranchCode());
			} else {
			
				n8 = cb.equal(m.get("branchCode"), req.getBranchCode());
			}
			// Risk Max Filter
			Subquery<Long> riskId = query.subquery(Long.class);
			Root<EserviceLifeDetails> ocp = riskId.from(EserviceLifeDetails.class);
			riskId.select(cb.max(ocp.get("riskId")));
			Predicate a3 = cb.equal(ocp.get("requestReferenceNo"), m.get("requestReferenceNo"));
			riskId.where(a3);
			
			Predicate n10 = cb.equal(m.get("riskId"),  riskId );
		
			query.where(n1, n2, n3, n4, n5, n6, n7,n11, n8,n9,n10).orderBy(orderList);	
		
			// Get Result
			TypedQuery<QuoteCriteriaRes> result = em.createQuery(query);
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			existingQuotes = result.getResultList();
		
			resp.setQuoteRes(existingQuotes);
			resp.setTotalCount(totalcountexisting(req, startDate,endDate, "Y"));
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resp;
	}

	private Long totalcountexisting(ExistingQuoteReq req, Date startDate, Date endDate, String string) {
		Long count = 0l;
		try {
			
			
			
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> query = cb.createQuery(Long.class);
	
			// Find All
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
			Root<EserviceLifeDetails> m = query.from(EserviceLifeDetails.class);
			
			query.multiselect(cb.count(m));			
	
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
				
				n11 = cb.equal(m.get("loginId"), req.getLoginId());
			}
	
			Predicate n8 = null;
			if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
				
				n8 = cb.equal(m.get("brokerBranchCode"), req.getBrokerBranchCode());
			} else {
			
				n8 = cb.equal(m.get("branchCode"), req.getBranchCode());
			}
			// Risk Max Filter
			Subquery<Long> riskId = query.subquery(Long.class);
			Root<EserviceLifeDetails> ocp = riskId.from(EserviceLifeDetails.class);
			riskId.select(cb.max(ocp.get("riskId")));
			Predicate a3 = cb.equal(ocp.get("requestReferenceNo"), m.get("requestReferenceNo"));
			riskId.where(a3);
			
			Predicate n10 = cb.equal(m.get("riskId"),  riskId );
		
			query.where(n1, n2, n3, n4, n5, n6, n7,n11, n8,n9,n10).orderBy(orderList);	
			
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
	public List<GetExistingBrokerListRes> getLifeExistingDropdown(ExistingBrokerUserListReq req, Date today,
			Date before30) {

		List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
		List<Tuple> list = new ArrayList<Tuple>();
		try {
		if(!("issuer".equalsIgnoreCase(req.getUserType()))){		
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

			Root<EserviceLifeDetails> m = query.from(EserviceLifeDetails.class);
			Root<LoginUserInfo> us = query.from(LoginUserInfo.class);
			query.multiselect(m.get("loginId").alias("code"),us.get("userName").alias("codeDesc"),
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
			predics1.add(cb.equal(us.get("loginId"), m.get("loginId")));
			if ("Broker".equalsIgnoreCase(req.getUserType())) {
				predics1.add(cb.equal(m.get("brokerCode"), agencyCode.as(String.class)));
			} else if ("User".equalsIgnoreCase(req.getUserType())) {
				predics1.add(cb.equal(m.get("agencyCode"), agencyCode));
			}
			predics1.add(cb.isNotNull(m.get("sourceType")));
			predics1.add(cb.isNotNull(m.get("loginId")));
			query.where(predics1.toArray(new Predicate[0]));

			TypedQuery<Tuple> typedQuery1 = em.createQuery(query);
			list = typedQuery1.getResultList();
			if (list != null && list.size() > 0) {
				list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.get("code"))))	.collect(Collectors.toList());
				
			for (Tuple data : list) {
					GetExistingBrokerListRes res = new GetExistingBrokerListRes();
					res.setCode(data.get("code") == null ? "" : data.get("code").toString());
					res.setCodeDesc(data.get("codeDesc") == null ? "" : data.get("codeDesc").toString());
					res.setType(data.get("type") == null ? "" : data.get("type").toString().toLowerCase().replaceAll("premia ", ""));
					resList.add(res);
				
						}
			}
			}else {
				
				resList = getExistingIssuerLife(req,today,before30); //Issuer
			}
	} catch (Exception e) {
		e.printStackTrace();
		log.info("Log Details" + e.getMessage());
		return null;
	}
	return resList;

	}
	private List<GetExistingBrokerListRes> getExistingIssuerLife(ExistingBrokerUserListReq req, Date today,
			Date before30) {
		List<Tuple> list = new ArrayList<Tuple>();
		List<Tuple> list1 = new ArrayList<Tuple>();
		List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
		try {
			{CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

			Root<EserviceLifeDetails> m = query.from(EserviceLifeDetails.class);

			query.multiselect(m.get("bdmCode").alias("code"), m.get("customerName").alias("codeDesc"),
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
					res.setType(data.get("type") == null ? "" : data.get("type").toString());
					resList.add(res);

				}
			}
			}
			{CriteriaBuilder cb1 = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> query1 = cb1.createQuery(Tuple.class);

			Root<EserviceLifeDetails> m1 = query1.from(EserviceLifeDetails.class);
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
			predics1.add(cb1.equal(us.get("loginId"),m1.get("loginId")));
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

	private static <T> java.util.function.Predicate<T> distinctByKey(
			java.util.function.Function<? super T, ?> keyExtractor) {
		Map<Object, Boolean> seen = new ConcurrentHashMap<>();
		return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

	@Override
	public List<GetExistingBrokerListRes> getBrokerUserListLapsedLife(ExistingBrokerUserListReq req, Date today,
			Date before30) {
		List<Tuple> list = new ArrayList<Tuple>();
		List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
		try {
			if(!("issuer".equalsIgnoreCase(req.getUserType()))){		
				
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

				Root<EserviceLifeDetails> m = query.from(EserviceLifeDetails.class);
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
				
				list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.get("code"))))	.collect(Collectors.toList());
			}else {
				resList = getBrokerListLapsedLifeIssuer(req, today,  before30) ;  //Issuer
				
				
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

	private List<GetExistingBrokerListRes> getBrokerListLapsedLifeIssuer(ExistingBrokerUserListReq req, Date today,
			Date before30) {
		List<Tuple> list = new ArrayList<Tuple>();
		List<Tuple> list1 = new ArrayList<Tuple>();
		List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
		try {
			{ CriteriaBuilder cb = em.getCriteriaBuilder();
			 CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);
			 
			 Root<EserviceLifeDetails> m = query.from(EserviceLifeDetails.class); 
			 
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
			 
			 Root<EserviceLifeDetails> m1 = query1.from(EserviceLifeDetails.class); 
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
			predics1 .add(cb1.equal(us.get("loginId"), m1.get("loginId")));
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
	public QuoteCriteriaResponse getLifeLapsedQuoteDetails(ExistingQuoteReq req, Date before30, int limit, int offset) {
		QuoteCriteriaResponse resp = new QuoteCriteriaResponse();
		List<QuoteCriteriaRes> lapsedQuotes = new ArrayList<QuoteCriteriaRes>();
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<QuoteCriteriaRes> query = cb.createQuery(QuoteCriteriaRes.class);

			// Find All
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
			Root<EserviceLifeDetails> m = query.from(EserviceLifeDetails.class);
			
			//overallPremiumLc
			Subquery<Long> overallPremiumLc = query.subquery(Long.class);
			Root<EserviceLifeDetails> ocpm1 = overallPremiumLc.from(EserviceLifeDetails.class);
			overallPremiumLc.select(cb.sum(ocpm1.get("overallPremiumLc")));
			Predicate a1 = cb.equal(ocpm1.get("requestReferenceNo"), m.get("requestReferenceNo"));
			overallPremiumLc.where(a1);
			
			//overallPremiumFc
			Subquery<Long> overallPremiumFc = query.subquery(Long.class);
			Root<EserviceLifeDetails> oc = overallPremiumFc.from(EserviceLifeDetails.class);
			overallPremiumFc.select(cb.sum(oc.get("overallPremiumFc")));
			Predicate a2 = cb.equal(oc.get("requestReferenceNo"), m.get("requestReferenceNo"));
			overallPremiumFc.where(a2);
		
			// Select
			query.multiselect(
					
					// Customer Info
					c.get("customerReferenceNo").alias("customerReferenceNo"),
					c.get("idNumber").alias("idNumber"),
					c.get("clientName").alias("clientName"),
					// Vehicle Info
					m.get("companyId").alias("companyId"), 
					m.get("productId").alias("productId"),
					 m.get("productDesc").alias("productName"),
					 
					m.get("branchCode").alias("branchCode"),
					m.get("requestReferenceNo").alias("requestReferenceNo"),
					m.get("quoteNo").alias("quoteNo"),
					
					m.get("customerCode").alias("customerId"),
					m.get("policyStartDate").alias("policyStartDate"),
					m.get("policyEndDate").alias("policyEndDate"),

					overallPremiumLc.as(BigDecimal.class).alias("overallPremiumLc"), 
					overallPremiumFc.as(BigDecimal.class).alias("overallPremiumFc"),
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
				
				n8 = cb.equal(m.get("brokerBranchCode"), req.getBrokerBranchCode());
			} else {
			
				n8 = cb.equal(m.get("branchCode"), req.getBranchCode());
			}
			// Risk Max Filter
			Subquery<Long> riskId = query.subquery(Long.class);
			Root<EserviceLifeDetails> ocp = riskId.from(EserviceLifeDetails.class);
			riskId.select(cb.max(ocp.get("riskId")));
			Predicate a3 = cb.equal(ocp.get("requestReferenceNo"), m.get("requestReferenceNo"));
			riskId.where(a3);
			
			Predicate n10 = cb.equal(m.get("riskId"),  riskId );

			query.where(n1, n2, n3, n4, n5,  n7, n8,n9,n10,n11).orderBy(orderList);
			

			TypedQuery<QuoteCriteriaRes> result = em.createQuery(query);
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			lapsedQuotes = result.getResultList();
		
			resp.setQuoteRes(lapsedQuotes);
			resp.setTotalCount(totalcountlapsedQuotes(req, before30));
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resp;
	}

	private Long totalcountlapsedQuotes(ExistingQuoteReq req, Date before30) {

		Long count = 0l;
		try {
			
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> query = cb.createQuery(Long.class);

			// Find All
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
			Root<EserviceLifeDetails> m = query.from(EserviceLifeDetails.class);

			query.multiselect(cb.count(m));

			Predicate n1 = cb.equal(c.get("customerReferenceNo"), m.get("customerReferenceNo"));
			Predicate n2 = cb.equal(m.get("companyId"), req.getInsuranceId());
			Predicate n3 = cb.equal(m.get("productId"), req.getProductId());
			Predicate n4 = cb.equal(m.get("status"), "Y");
			Predicate n5 = cb.lessThanOrEqualTo(m.get("updatedDate"), before30);
			Predicate n9 = cb.isNull(m.get("endorsementType"));
			Predicate n7 = null;
			Predicate n11 = null;
			
			if (req.getApplicationId().equalsIgnoreCase("1")) {
				n7 = cb.equal(m.get("loginId"), req.getLoginId());
				n11 = cb.equal(m.get("applicationId"), req.getApplicationId());
				
			} else {
				if(StringUtils.isNotBlank(req.getBdmCode())){
					n7 = cb.equal(m.get("applicationId"), req.getApplicationId());
					n11 = cb.equal(m.get("bdmCode"), req.getBdmCode());
				}else {
					n7 = cb.equal(m.get("applicationId"), req.getApplicationId());
					n11 = cb.equal(m.get("loginId"), req.getLoginId());
				}
			}

			Predicate n8 = null;
			if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
				
				n8 = cb.equal(m.get("brokerBranchCode"), req.getBrokerBranchCode());
			} else {
			
				n8 = cb.equal(m.get("branchCode"), req.getBranchCode());
			}
			// Risk Max Filter
			Subquery<Long> riskId = query.subquery(Long.class);
			Root<EserviceLifeDetails> ocp = riskId.from(EserviceLifeDetails.class);
			riskId.select(cb.max(ocp.get("riskId")));
			Predicate a3 = cb.equal(ocp.get("requestReferenceNo"), m.get("requestReferenceNo"));
			riskId.where(a3);
			
			Predicate n10 = cb.equal(m.get("riskId"),  riskId );

			query.where(n1, n2, n3, n4, n5,  n7, n8,n9,n10,n11);

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
	public GetRejectedQuoteDetailsRes getLifeRejectedQuoteDetails(ExistingQuoteReq req, Date startDate ,Date  endDate , int limit,int offset) {

		GetRejectedQuoteDetailsRes resp = new GetRejectedQuoteDetailsRes();
		List<RejectCriteriaRes> rejectedQuotes = new ArrayList<RejectCriteriaRes>();
		try {
			
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<RejectCriteriaRes> query = cb.createQuery(RejectCriteriaRes.class);

			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
			Root<EserviceLifeDetails> m = query.from(EserviceLifeDetails.class);
			
			//overallPremiumLc
			Subquery<Long> overallPremiumLc = query.subquery(Long.class);
			Root<EserviceLifeDetails> ocpm1 = overallPremiumLc.from(EserviceLifeDetails.class);
			overallPremiumLc.select(cb.sum(ocpm1.get("overallPremiumLc")));
			Predicate a1 = cb.equal(ocpm1.get("requestReferenceNo"), m.get("requestReferenceNo"));
			overallPremiumLc.where(a1);
			
			//overallPremiumFc
			Subquery<Long> overallPremiumFc = query.subquery(Long.class);
			Root<EserviceLifeDetails> oc = overallPremiumFc.from(EserviceLifeDetails.class);
			overallPremiumFc.select(cb.sum(oc.get("overallPremiumFc")));
			Predicate a2 = cb.equal(oc.get("requestReferenceNo"), m.get("requestReferenceNo"));
			overallPremiumFc.where(a2);
		

			// Select
			query.multiselect(
					
					c.get("customerReferenceNo").alias("customerReferenceNo"), 
					c.get("idNumber").alias("idNumber"),
					c.get("clientName").alias("clientName"),
					// Vehicle Info
					m.get("companyId").alias("companyId"),
					m.get("productId").alias("productId"),
					m.get("branchCode").alias("branchCode"),
					m.get("requestReferenceNo").alias("requestReferenceNo"),
					m.get("quoteNo").alias("quoteNo"),
					m.get("customerCode").alias("customerId"),
					m.get("policyStartDate").alias("policyStartDate"),
					m.get("policyEndDate").alias("policyEndDate"),
					m.get("rejectReason").alias("rejectReason"),

					overallPremiumLc.as(BigDecimal.class).alias("overallPremiumLc"), 
					overallPremiumFc.as(BigDecimal.class).alias("overallPremiumFc"),
					m.get("currency").alias("currency"));




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
			

			
			n7 = cb.equal(m.get("applicationId"), req.getApplicationId());
			if(StringUtils.isNotBlank(req.getBdmCode())){
				n11 = cb.equal(m.get("bdmCode"), req.getBdmCode());
			}else {
				n11 = cb.equal(m.get("loginId"), req.getLoginId());
			}
			
			

			Predicate n8 = null;
			if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
				
				n8 = cb.equal(m.get("brokerBranchCode"), req.getBrokerBranchCode());
			} else {
			
				n8 = cb.equal(m.get("branchCode"), req.getBranchCode());
			}
			// Risk Max Filter
			Subquery<Long> riskId = query.subquery(Long.class);
			Root<EserviceLifeDetails> ocp = riskId.from(EserviceLifeDetails.class);
			riskId.select(cb.max(ocp.get("riskId")));
			Predicate a3 = cb.equal(ocp.get("requestReferenceNo"), m.get("requestReferenceNo"));
			riskId.where(a3);
			
			Predicate n10 = cb.equal(m.get("riskId"),  riskId );
			query.where(n1, n2, n3, n4, n5, n6, n7, n8,n9,n10,n11).orderBy(orderList);

			TypedQuery<RejectCriteriaRes> result = em.createQuery(query);
			result.setFirstResult(limit * offset); 
			result.setMaxResults(offset);
			rejectedQuotes = result.getResultList();
			
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
	public List<GetExistingBrokerListRes> getBrokerUserListLifeRejected(ExistingBrokerUserListReq req, Date today,
			Date before30) {

		List<Tuple> list = new ArrayList<Tuple>();
		List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
		try {
			if (!("issuer".equalsIgnoreCase(req.getUserType()))) {
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

				Root<EserviceLifeDetails> m = query.from(EserviceLifeDetails.class);
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
				list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.get("code"))))	.collect(Collectors.toList());
			} else {

				resList = getBrokerListRejectedLifeIssuer(req, today, before30); // Issuer

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

	private List<GetExistingBrokerListRes> getBrokerListRejectedLifeIssuer(ExistingBrokerUserListReq req, Date today,
			Date before30) {
		
		List<Tuple> list = new ArrayList<Tuple>();
		List<Tuple> list1 = new ArrayList<Tuple>();
		List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
		try {
			 CriteriaBuilder cb = em.getCriteriaBuilder();
			 CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);
			 
			 Root<EserviceLifeDetails> m = query.from(EserviceLifeDetails.class); 
			 
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
			 
			 Root<EserviceLifeDetails> m1 = query1.from(EserviceLifeDetails.class); 
			 Root<LoginUserInfo> us = query1.from(LoginUserInfo.class); 
			 query1.multiselect(
					 m1.get("loginId").alias("code"),
					 us.get("userName").alias("codeDesc"),
					 m1.get("sourceType").alias("type")
					 ).distinct(true) ;
			 
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
	public List<GetExistingBrokerListRes> getLifeProtfolioDropdownPending(ExistingBrokerUserListReq req, Date today) {

		List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
		List<Tuple> list = new ArrayList<Tuple>();
		try {

			if (!("issuer".equalsIgnoreCase(req.getUserType()))) {
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

				Root<EserviceLifeDetails> m = query.from(EserviceLifeDetails.class);
				Root<LoginUserInfo> us = query.from(LoginUserInfo.class);
				query.multiselect( m.get("loginId").alias("code"),us.get("userName").alias("codeDesc"),
						m.get("sourceType").alias("type"));

				// Find All
				Subquery<Long> agencyCode = query.subquery(Long.class);
				Root<LoginMaster> ocpm1 = agencyCode.from(LoginMaster.class);
				agencyCode.select(ocpm1.get("agencyCode"));
				Predicate a1 = cb.equal(ocpm1.get("loginId"), req.getLoginId());
				agencyCode.where(a1);

//				Predicate n1 = cb.equal(m.get("applicationId"), req.getApplicationId());
				Predicate n2 = cb.isNotNull(m.get("applicationId"));
				Predicate n3 = cb.equal(m.get("companyId"), req.getCompanyId());
				Predicate n4 = cb.equal(m.get("productId"), req.getProductId());
				Predicate n5 = cb.equal(m.get("endtStatus"), "P");
				Predicate n12 = null;
				if ("Broker".equalsIgnoreCase(req.getUserType())) {
					n12 = cb.equal(m.get("brokerCode"), agencyCode.as(String.class));
				} else if ("User".equalsIgnoreCase(req.getUserType())) {
					n12 = cb.equal(m.get("agencyCode"), agencyCode);
				}
				Predicate n13 = cb.isNotNull(m.get("sourceType"));
				Predicate n14 = cb.isNotNull(m.get("loginId"));
				
				Predicate us1 = cb.equal(us.get("loginId"), m.get("loginId"));
				query.where(n2,n3, n4, n5, n12, n13, n14,us1);

				TypedQuery<Tuple> typedQuery1 = em.createQuery(query);
				list = typedQuery1.getResultList();
				list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.get("code"))))	.collect(Collectors.toList());
					
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
				resList = getPortfolioPendingIssuerLife(req, today);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resList;

	
	}

	private List<GetExistingBrokerListRes> getPortfolioPendingIssuerLife(ExistingBrokerUserListReq req, Date today) {

		List<Tuple> list = new ArrayList<Tuple>();
		List<Tuple> list1 = new ArrayList<Tuple>();
		List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
		try {
			{
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

				Root<EserviceLifeDetails> m = query.from(EserviceLifeDetails.class);

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

				Root<EserviceLifeDetails> m1 = query1.from(EserviceLifeDetails.class);
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
	public GetMotorProtfolioPendingRes getLifeProtfolioPending(ExistingQuoteReq req, List<String> branches, Date startDate,
			int limit, int offset, String status) {

		
		GetMotorProtfolioPendingRes resp = new GetMotorProtfolioPendingRes();
		List<PortfolioPendingGridCriteriaRes> portfolio = new ArrayList<PortfolioPendingGridCriteriaRes>();
		try {
			
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<PortfolioPendingGridCriteriaRes> query = cb.createQuery(PortfolioPendingGridCriteriaRes.class);

			// Find All
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
			Root<EserviceLifeDetails> m = query.from(EserviceLifeDetails.class);
			Root<HomePositionMaster> h = query.from(HomePositionMaster.class);
			
			Subquery<Long> endtPre = query.subquery(Long.class);
			Root<HomePositionMaster> h1 = endtPre.from(HomePositionMaster.class);
			endtPre.select(cb.sum(h1.get("endtPremium"))) ;
			Predicate pm1 = cb.equal(h1.get("endtStatus"), m.get("endtStatus"));
			Predicate pm2 = cb.like(h1.get("originalPolicyNo"), m.get("originalPolicyNo"));
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
					cb.selectCase().when(cb.max(m.get("customerCode")).isNotNull(), cb.max(m.get("customerCode")))
							.otherwise(cb.max(m.get("customerCode"))).alias("customerId"),
					cb.max(m.get("policyStartDate")).alias("inceptionDate"),
					cb.max(m.get("policyEndDate")).alias("expiryDate"),
					cb.sum(m.get("overallPremiumLc")).as(BigDecimal.class).alias("overallPremiumLc"), 
					cb.sum(m.get("overallPremiumFc")).as(BigDecimal.class).alias("overallPremiumFc"),
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

			Predicate n7 = cb.greaterThanOrEqualTo(h.get("expiryDate"), startDate);
			Predicate n8 = cb.lessThanOrEqualTo(h.get("entryDate"), startDate);

			Predicate n5 = null;
			Predicate n6 = null;
			Predicate n9 = null;
		
			if (req.getApplicationId().equalsIgnoreCase("1")) {
				n5 = cb.equal(m.get("loginId"), req.getLoginId());
//				n9 = cb.equal(m.get("applicationId"), req.getApplicationId());
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
			
//			if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
//				Expression<String> e0 = m.get("brokerBranchCode");
//				n6 = e0.in(branches);
//			} else {
//				Expression<String> e0 = m.get("branchCode");
//				n6 = e0.in(branches);
//			}
//			query.where(n1, n2, n3, n4, n5, n6,n7,n8,n9).groupBy((m.get("originalPolicyNo")),m.get("endtStatus"));
//			
			
			// Get Result
			TypedQuery<PortfolioPendingGridCriteriaRes> result = em.createQuery(query);
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			portfolio = result.getResultList();
			
			resp.setPending(portfolio);
			resp.setCount(totalProtfolioPending( req,  branches,startDate,  limit,  offset,  status));
					
			
			
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
			Root<EserviceLifeDetails> m = query.from(EserviceLifeDetails.class);
	
			query.multiselect(cb.count(m));
		
			Predicate n1 = cb.equal(c.get("customerReferenceNo"), m.get("customerReferenceNo"));
			Predicate n2 = cb.equal(m.get("companyId"), req.getInsuranceId());
			Predicate n3 = cb.equal(m.get("productId"), req.getProductId());
			Predicate n4 = cb.equal(m.get("endtStatus"), status); 
			

			Predicate n5 = null;
			Predicate n6 = null;
			Predicate n9 = null;
			if (req.getApplicationId().equalsIgnoreCase("1")) {
				n5 = cb.equal(m.get("loginId"), req.getLoginId());
//				n9 = cb.equal(m.get("applicationId"), req.getApplicationId());
				Expression<String> e0 = m.get("brokerBranchCode");
				n6 = e0.in(branches);
				query.where(n1, n2, n3, n4,  n6, n9).groupBy((m.get("originalPolicyNo")),m.get("endtStatus"));
				
			} else {
				n5 = cb.equal(m.get("applicationId"), req.getApplicationId());
				if(StringUtils.isNotBlank(req.getBdmCode())){
					n9 = cb.equal(m.get("bdmCode"), req.getBdmCode());
				}else {
					n9 = cb.equal(m.get("loginId"), req.getLoginId());
				}
				Expression<String> e0 = m.get("branchCode");
				n6 = e0.in(branches);
				query.where(n1, n2, n3, n4, n5, n6, n9).groupBy((m.get("originalPolicyNo")),m.get("endtStatus"));
				
			}
			
//			if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
//				Expression<String> e0 = m.get("brokerBranchCode");
//				n6 = e0.in(branches);
//			} else {
//				Expression<String> e0 = m.get("branchCode");
//				n6 = e0.in(branches);
//			}
//			query.where(n1, n2, n3, n4, n5, n6, n9).groupBy((m.get("originalPolicyNo")),m.get("endtStatus"));
//			
			
			// Get Result
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
	public List<GetExistingBrokerListRes> getLifeRPDropdown(ExistingBrokerUserListReq req, Date today) {

		List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
		List<Tuple> list = new ArrayList<Tuple>();
		try {

			if (!("issuer".equalsIgnoreCase(req.getUserType()))) {
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

				Root<EserviceLifeDetails> m = query.from(EserviceLifeDetails.class);
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
				Predicate n5 = cb.equal(m.get("status"), "RP");
				Predicate n12 = null;
				if ("Broker".equalsIgnoreCase(req.getUserType())) {
					n12 = cb.equal(m.get("brokerCode"), agencyCode.as(String.class));
				} else if ("User".equalsIgnoreCase(req.getUserType())) {
					n12 = cb.equal(m.get("agencyCode"), agencyCode);
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
				list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.get("code")))).collect(Collectors.toList());
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
				resList = getReferalPendingIssuerLife(req, today);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resList;

	
	}

	private List<GetExistingBrokerListRes> getReferalPendingIssuerLife(ExistingBrokerUserListReq req, Date today) {

		List<Tuple> list = new ArrayList<Tuple>();
		List<Tuple> list1 = new ArrayList<Tuple>();
		List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
		try {
			{
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

				Root<EserviceLifeDetails> m = query.from(EserviceLifeDetails.class);

				query.multiselect(m.get("bdmCode").alias("code"), m.get("customerName").alias("codeDesc"),
						m.get("sourceType").alias("type"));
				Predicate n1 = cb.equal(m.get("applicationId"), req.getApplicationId());
				Predicate n2 = cb.isNotNull(m.get("applicationId"));
				Predicate n3 = cb.equal(m.get("companyId"), req.getCompanyId());
				Predicate n4 = cb.equal(m.get("productId"), req.getProductId());
				Predicate n5 = cb.equal(m.get("status"), "RP");
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

				Root<EserviceLifeDetails> m1 = query1.from(EserviceLifeDetails.class);
				Root<LoginUserInfo> us = query1.from(LoginUserInfo.class);
				query1.multiselect( m1.get("loginId").alias("code"),us.get("userName").alias("codeDesc"),
						m1.get("sourceType").alias("type"));
				
				Predicate n1 = cb1.equal(m1.get("applicationId"), req.getApplicationId());
				Predicate n2 = cb1.isNotNull(m1.get("applicationId"));
				Predicate n3 = cb1.equal(m1.get("companyId"), req.getCompanyId());
				Predicate n4 = cb1.equal(m1.get("productId"), req.getProductId());
				Predicate n5 = cb1.equal(m1.get("status"), "RP");
				Predicate n6 = cb1.isNull(m1.get("bdmCode"));
				Predicate n15 = null;
				if(req.getType().equalsIgnoreCase("Q"))
					n15 = cb1.isNull(m1.get("endorsementTypeDesc")); 
				else if (req.getType().equalsIgnoreCase("E"))
					n15 = cb1.isNotNull(m1.get("endorsementTypeDesc"));
				
				Predicate us1 = cb1.equal(us.get("loginId"), m1.get("loginId"));
				query1.where(n1, n2, n3, n4, n5, n6,n15,us1);

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
	public GetMotorReferalDetailsRes getLifeReferalDetails(ExistingQuoteReq req, int limit, int offset, String status) {
		GetMotorReferalDetailsRes resp = new GetMotorReferalDetailsRes();
		List<MotorGridCriteriaRes> referrals = new ArrayList<MotorGridCriteriaRes>();
	
		try {
			resp.setTotalCount(0);
			
			
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

			// Find All
			Root<EserviceLifeDetails> m = query.from(EserviceLifeDetails.class);
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);

			// Select
			query.multiselect(
					// Customer Info
					c.get("customerReferenceNo").alias("customerReferenceNo"), c.get("idNumber").alias("idNumber"),
					c.get("clientName").alias("clientName"),c.get("titleDesc").alias("titleDesc"),
					c.get("middleName").alias("middleName"),c.get("lastName").alias("lastName"),
					// Vehicle Info
					m.get("companyId").alias("companyId"), m.get("productId").alias("productId"),
					m.get("branchCode").alias("branchCode"), m.get("requestReferenceNo").alias("requestReferenceNo"),
					m.get("quoteNo")
							.alias("quoteNo"),
					m.get("customerCode").alias("customerId"),
					m.get("policyStartDate").alias("policyStartDate"), m.get("policyEndDate").alias("policyEndDate"),
					m.get("rejectReason").alias("rejectReason"),
					m.get("adminRemarks").alias("adminRemarks"),
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
			Root<EserviceLifeDetails> ocpm1 = riskId.from(EserviceLifeDetails.class);
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
				if(StringUtils.isNotBlank(req.getBdmCode())){
					n5 = cb.equal(m.get("applicationId"), req.getApplicationId());
					n9 = cb.equal(m.get("bdmCode"), req.getBdmCode());
				}else {
					n5 = cb.equal(m.get("applicationId"), req.getApplicationId());
					n9 = cb.equal(m.get("loginId"), req.getLoginId());
				}
				
			}
			Predicate n6 = null;
			if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
			
				n6 = cb.equal(m.get("brokerBranchCode"),req.getBrokerBranchCode());
			} else {
				
				n6 = cb.equal(m.get("branchCode"), req.getBranchCode());
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
				query.where(n1, n2, n3, n4, n5, n6, n7, n8, n9).orderBy(orderList);
			} else {
				query.where(n1, n2, n3, n4, n5, n6, n7, n8, n9, nLead).orderBy(orderList);
			}
			
			//query.where(n1, n2, n3, n4, n5, n6,n7, n8,n9).orderBy(orderList);
				
			// Get Result
			TypedQuery<Tuple> result = em.createQuery(query);
			result.setFirstResult(limit * offset); //limit 0, 
			result.setMaxResults(offset); //offset 1000
			List<Tuple> referralsList = result.getResultList();
		
			
			for (  Tuple r :referralsList   ) {
				MotorGridCriteriaRes res = new MotorGridCriteriaRes();
		
				res.setAdminRemarks(r.get("adminRemarks")==null ? "" : (String) r.get("adminRemarks"));
				res.setBranchCode(r.get("branchCode")==null ? "" : (String) r.get("branchCode"));
				res.setClientName((r.get("titleDesc") == null ? "" : (String) r.get("titleDesc")) + " "
						+ (r.get("clientName") == null ? "" : (String) r.get("clientName")) + " "
						+ (r.get("lastName") == null ? "" : (String) r.get("lastName")));
				res.setCompanyId(r.get("companyId")==null ? "" : (String) r.get("companyId"));
				res.setCustomerId(r.get("customerId")==null ? "" : (String) r.get("customerId"));
				res.setCustomerReferenceNo(r.get("customerReferenceNo")==null ? "" : (String) r.get("customerReferenceNo"));
				res.setIdNumber(r.get("idNumber")==null ? "" : (String) r.get("idNumber"));
				res.setPolicyEndDate(r.get("policyEndDate")==null ? null : (Date) r.get("policyEndDate"));
				res.setPolicyStartDate(r.get("policyStartDate")==null ? null : (Date) r.get("policyStartDate"));
				res.setProductId(r.get("productId")==null ? "" : (String) r.get("productId"));
				res.setQuoteNo(r.get("quoteNo")==null ? "" : (String) r.get("quoteNo"));
				res.setRejectReason(r.get("rejectReason")==null ? "" : (String) r.get("rejectReason"));
				res.setRequestReferenceNo(r.get("requestReferenceNo")==null ? "" : (String) r.get("requestReferenceNo"));
				res.setEndorsementDate(r.get("endorsementDate")==null ? null : (Date) r.get("endorsementDate"));
				res.setEndorsementEffdate(r.get("endorsementEffdate")==null ? null : (Date) r.get("endorsementEffdate"));
				res.setEndorsementRemarks(r.get("endorsementRemarks")==null ? "" : r.get("endorsementRemarks").toString());
				res.setEndorsementType(r.get("endorsementType")==null ? "" : r.get("endorsementType").toString());
				res.setEndorsementTypeDesc(r.get("endorsementTypeDesc")==null ? "" : r.get("endorsementTypeDesc").toString());

				res.setEndtCategDesc(r.get("endtCategDesc")==null ? "" : r.get("endtCategDesc").toString());
				res.setEndtCount(r.get("endtCount")==null ? BigDecimal.ZERO : new BigDecimal(r.get("endorsementType").toString()));
				res.setEndtPremium(r.get("endtPremium")==null ? null : Double.valueOf(r.get("endtPremium").toString()));
				res.setEndtPrevPolicyNo(r.get("endtPrevPolicyNo")==null ? "" : r.get("endtPrevPolicyNo").toString());
				res.setEndtPrevQuoteNo(r.get("endtPrevQuoteNo")==null ? "" : r.get("endtPrevQuoteNo").toString());
				res.setEndtStatus(r.get("endtStatus")==null ? "" : r.get("endtStatus").toString());
				res.setOriginalPolicyNo(r.get("originalPolicyNo")==null ? "" : r.get("originalPolicyNo").toString());
				
				referrals.add(res);
			}
			
			
	
			resp.setMotorGridCriteriaResRes(referrals);
			
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

			// Find All
			Root<EserviceLifeDetails> m = query.from(EserviceLifeDetails.class);
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);

			query.multiselect(cb.count(m));

			Subquery<Long> riskId = query.subquery(Long.class);
			Root<EserviceLifeDetails> ocpm1 = riskId.from(EserviceLifeDetails.class);
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
				if(StringUtils.isNotBlank(req.getBdmCode())){
					n5 = cb.equal(m.get("applicationId"), req.getApplicationId());
					n9 = cb.equal(m.get("bdmCode"), req.getBdmCode());
				}else {
					n5 = cb.equal(m.get("applicationId"), req.getApplicationId());
					n9 = cb.equal(m.get("loginId"), req.getLoginId());
				}
			}
			Predicate n6 = null;
			if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
			
				n6 = cb.equal(m.get("brokerBranchCode"), req.getBrokerBranchCode());
			} else {
				
				n6 = cb.equal(m.get("branchCode"), req.getBranchCode());
			}
			
			Predicate n8 = null;
			if(req.getType().equalsIgnoreCase("Q"))
				n8 = cb.isNull(m.get("endorsementTypeDesc")); 
			else if (req.getType().equalsIgnoreCase("E"))
				n8 = cb.isNotNull(m.get("endorsementTypeDesc")); 
			
			Predicate nLeadCount =null;
			if(req.getLeadSeqNo() !=null) {
	         nLeadCount = cb.equal(c.get("leadSeqNo"), req.getLeadSeqNo());
			}
			if(req.getLeadSeqNo() ==null) {
				query.where(n1, n2, n3, n4, n5, n6, n7, n8, n9);
			}else {
				query.where(n1, n2, n3, n4, n5, n6, n7, n8, n9,nLeadCount);
			}
			//query.where(n1, n2, n3, n4, n5, n6, n7, n8, n9);
			
				
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
	public List<GetExistingBrokerListRes> getLifeRADropdown(ExistingBrokerUserListReq req, Date today) {

		List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
		List<Tuple> list = new ArrayList<Tuple>();
		try {

			if (!("issuer".equalsIgnoreCase(req.getUserType()))) {
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

				Root<EserviceLifeDetails> m = query.from(EserviceLifeDetails.class);
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
				Predicate n5 = cb.equal(m.get("status"), "RA");
				Predicate n12 = null;
				if ("Broker".equalsIgnoreCase(req.getUserType())) {
					n12 = cb.equal(m.get("brokerCode"), agencyCode.as(String.class));
				} else if ("User".equalsIgnoreCase(req.getUserType())) {
					n12 = cb.equal(m.get("agencyCode"), agencyCode);
				}
				Predicate n13 = cb.isNotNull(m.get("sourceType"));
				Predicate n14 = cb.isNotNull(m.get("loginId"));
				Predicate us1 = cb.equal(us.get("loginId"), m.get("loginId"));
				
				Predicate n15 = null;
				if(req.getType().equalsIgnoreCase("Q"))
					n15 = cb.isNull(m.get("endorsementTypeDesc")); 
				else if (req.getType().equalsIgnoreCase("E"))
					n15 = cb.isNotNull(m.get("endorsementTypeDesc"));
				
				query.where(n1, n3, n4, n5, n12, n13, n14,us1,n15);

				TypedQuery<Tuple> typedQuery1 = em.createQuery(query);
				list = typedQuery1.getResultList();
				list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.get("code"))))	.collect(Collectors.toList());
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
				resList = getReferalApprovedIssuerLife(req, today);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resList;

	
	}

	private List<GetExistingBrokerListRes> getReferalApprovedIssuerLife(ExistingBrokerUserListReq req, Date today) {

		List<Tuple> list = new ArrayList<Tuple>();
		List<Tuple> list1 = new ArrayList<Tuple>();
		List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
		try {
			{
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

				Root<EserviceLifeDetails> m = query.from(EserviceLifeDetails.class);

				query.multiselect(m.get("bdmCode").alias("code"), m.get("customerName").alias("codeDesc"),
						m.get("sourceType").alias("type"));
				Predicate n1 = cb.equal(m.get("applicationId"), req.getApplicationId());
				Predicate n2 = cb.isNotNull(m.get("applicationId"));
				Predicate n3 = cb.equal(m.get("companyId"), req.getCompanyId());
				Predicate n4 = cb.equal(m.get("productId"), req.getProductId());
				Predicate n5 = cb.equal(m.get("status"), "RA");
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

				Root<EserviceLifeDetails> m1 = query1.from(EserviceLifeDetails.class);
				Root<LoginUserInfo> us = query1.from(LoginUserInfo.class);
				query1.multiselect( m1.get("loginId").alias("code"),us.get("userName").alias("codeDesc"),
						m1.get("sourceType").alias("type"));
				Predicate n1 = cb1.equal(m1.get("applicationId"), req.getApplicationId());
				Predicate n2 = cb1.isNotNull(m1.get("applicationId"));
				Predicate n3 = cb1.equal(m1.get("companyId"), req.getCompanyId());
				Predicate n4 = cb1.equal(m1.get("productId"), req.getProductId());
				Predicate n5 = cb1.equal(m1.get("status"), "RA");
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
	public List<GetExistingBrokerListRes> getLifeRRDropdown(ExistingBrokerUserListReq req, Date today) {

		List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
		List<Tuple> list = new ArrayList<Tuple>();
		try {

			if (!("issuer".equalsIgnoreCase(req.getUserType()))) {
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

				Root<EserviceLifeDetails> m = query.from(EserviceLifeDetails.class);
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
				Predicate n5 = cb.equal(m.get("status"), "RR");
				Predicate n12 = null;
				if ("Broker".equalsIgnoreCase(req.getUserType())) {
					n12 = cb.equal(m.get("brokerCode"), agencyCode.as(String.class));
				} else if ("User".equalsIgnoreCase(req.getUserType())) {
					n12 = cb.equal(m.get("agencyCode"), agencyCode);
				}
				Predicate n13 = cb.isNotNull(m.get("sourceType"));
				Predicate n14 = cb.isNotNull(m.get("loginId"));

				Predicate us1 = cb.equal(us.get("loginId"), m.get("loginId"));
				
				Predicate n15 = null;
				if(req.getType().equalsIgnoreCase("Q"))
					n15 = cb.isNull(m.get("endorsementTypeDesc")); 
				else if (req.getType().equalsIgnoreCase("E"))
					n15 = cb.isNotNull(m.get("endorsementTypeDesc"));
				
				query.where(n1, n3, n4, n5, n12, n13, n14,us1,n15);

				TypedQuery<Tuple> typedQuery1 = em.createQuery(query);
				list = typedQuery1.getResultList();
				list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.get("code"))))	.collect(Collectors.toList());
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
				resList = getReferalRejectIssuerLife(req, today);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resList;

	
	}

	private List<GetExistingBrokerListRes> getReferalRejectIssuerLife(ExistingBrokerUserListReq req, Date today) {

		List<Tuple> list = new ArrayList<Tuple>();
		List<Tuple> list1 = new ArrayList<Tuple>();
		List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
		try {
			{
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

				Root<EserviceLifeDetails> m = query.from(EserviceLifeDetails.class);

				query.multiselect(m.get("bdmCode").alias("code"), m.get("customerName").alias("codeDesc"),
						m.get("sourceType").alias("type"));
				Predicate n1 = cb.equal(m.get("applicationId"), req.getApplicationId());
				Predicate n2 = cb.isNotNull(m.get("applicationId"));
				Predicate n3 = cb.equal(m.get("companyId"), req.getCompanyId());
				Predicate n4 = cb.equal(m.get("productId"), req.getProductId());
				Predicate n5 = cb.equal(m.get("status"), "RR");
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

				Root<EserviceLifeDetails> m1 = query1.from(EserviceLifeDetails.class);
				Root<LoginUserInfo> us = query1.from(LoginUserInfo.class);
				query1.multiselect( m1.get("loginId").alias("code"),us.get("userName").alias("codeDesc"),
						m1.get("sourceType").alias("type"));
				Predicate n1 = cb1.equal(m1.get("applicationId"), req.getApplicationId());
				Predicate n2 = cb1.isNotNull(m1.get("applicationId"));
				Predicate n3 = cb1.equal(m1.get("companyId"), req.getCompanyId());
				Predicate n4 = cb1.equal(m1.get("productId"), req.getProductId());
				Predicate n5 = cb1.equal(m1.get("status"), "RR");
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
	public List<GetExistingBrokerListRes> getLifeREDropdown(ExistingBrokerUserListReq req, Date today) {

		List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
		List<Tuple> list = new ArrayList<Tuple>();
		try {

			if (!("issuer".equalsIgnoreCase(req.getUserType()))) {
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

				Root<EserviceLifeDetails> m = query.from(EserviceLifeDetails.class);
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
				Predicate n5 = cb.equal(m.get("status"), "RE");
				Predicate n12 = null;
				if ("Broker".equalsIgnoreCase(req.getUserType())) {
					n12 = cb.equal(m.get("brokerCode"), agencyCode.as(String.class));
				} else if ("User".equalsIgnoreCase(req.getUserType())) {
					n12 = cb.equal(m.get("agencyCode"), agencyCode);
				}
				Predicate n13 = cb.isNotNull(m.get("sourceType"));
				Predicate n14 = cb.isNotNull(m.get("loginId"));
				Predicate us1 = cb.equal(us.get("loginId"), m.get("loginId"));
				
				Predicate n15 = null;
				if(req.getType().equalsIgnoreCase("Q"))
					n15 = cb.isNull(m.get("endorsementTypeDesc")); 
				else if (req.getType().equalsIgnoreCase("E"))
					n15 = cb.isNotNull(m.get("endorsementTypeDesc"));
				
				query.where(n1, n3, n4, n5, n12, n13, n14,us1,n15);

				TypedQuery<Tuple> typedQuery1 = em.createQuery(query);
				list = typedQuery1.getResultList();
				list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.get("code"))))	.collect(Collectors.toList());
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
				resList = getReferalRequoteIssuerMotor(req, today);
			}

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resList;

	}

	
	private List<GetExistingBrokerListRes> getReferalRequoteIssuerMotor(ExistingBrokerUserListReq req, Date today) {

		List<Tuple> list = new ArrayList<Tuple>();
		List<Tuple> list1 = new ArrayList<Tuple>();
		List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
		try {
			{
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

				Root<EserviceLifeDetails> m = query.from(EserviceLifeDetails.class);

				query.multiselect(m.get("bdmCode").alias("code"), m.get("customerName").alias("codeDesc"),
						m.get("sourceType").alias("type"));
				Predicate n1 = cb.equal(m.get("applicationId"), req.getApplicationId());
				Predicate n2 = cb.isNotNull(m.get("applicationId"));
				Predicate n3 = cb.equal(m.get("companyId"), req.getCompanyId());
				Predicate n4 = cb.equal(m.get("productId"), req.getProductId());
				Predicate n5 = cb.equal(m.get("status"), "RE");
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

				Root<EserviceLifeDetails> m1 = query1.from(EserviceLifeDetails.class);
				Root<LoginUserInfo> us = query1.from(LoginUserInfo.class);
				query1.multiselect( m1.get("loginId").alias("code"),us.get("userName").alias("codeDesc"),
						m1.get("sourceType").alias("type"));
				Predicate n1 = cb1.equal(m1.get("applicationId"), req.getApplicationId());
				Predicate n2 = cb1.isNotNull(m1.get("applicationId"));
				Predicate n3 = cb1.equal(m1.get("companyId"), req.getCompanyId());
				Predicate n4 = cb1.equal(m1.get("productId"), req.getProductId());
				Predicate n5 = cb1.equal(m1.get("status"), "RE");
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
	public List<GetExistingBrokerListRes> getAdminLifeRPropdown(ExistingBrokerUserListReq req, Date today) {

		List<Tuple> list = new ArrayList<Tuple>();
		List<Tuple> list1 = new ArrayList<Tuple>();
		List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
		try {
			{
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

				Root<EserviceLifeDetails> m = query.from(EserviceLifeDetails.class);

				query.multiselect(m.get("loginId").alias("code"), m.get("customerName").alias("codeDesc"),
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

				Root<EserviceLifeDetails> m1 = query1.from(EserviceLifeDetails.class);
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
				
				query1.where(n2,n3,n4,n5,n6,n8,us1, n15 );

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
	public List<GetExistingBrokerListRes> getLifeAdminReferalDropdown(ExistingBrokerUserListReq req, Date today,String status) {

		List<Tuple> list = new ArrayList<Tuple>();
		List<Tuple> list1 = new ArrayList<Tuple>();
		List<GetExistingBrokerListRes> resList = new ArrayList<GetExistingBrokerListRes>();
		try {
			{
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

				Root<EserviceLifeDetails> m = query.from(EserviceLifeDetails.class);

				query.multiselect(m.get("loginId").alias("code"), m.get("customerName").alias("codeDesc"),
						m.get("sourceType").alias("type"));
				
				Predicate n1 = cb.equal(m.get("adminLoginId"), req.getApplicationId());
				//Predicate n2 = cb.isNotNull(m.get("applicationId"));
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

				Root<EserviceLifeDetails> m1 = query1.from(EserviceLifeDetails.class);
				Root<LoginUserInfo> us = query1.from(LoginUserInfo.class);
				query1.multiselect( m1.get("loginId").alias("code"),us.get("userName").alias("codeDesc"),
						m1.get("sourceType").alias("type"));

				
				Predicate n1 = cb1.equal(m1.get("adminLoginId"), req.getApplicationId());
//				Predicate n2 = cb1.isNotNull(m1.get("applicationId"));
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

	@Override
	public GetMotorReferalDetailsRes getLifeAdminReferalDetails(ExistingQuoteReq req, int limit, int offset,
			String status) {

		
		GetMotorReferalDetailsRes resp = new GetMotorReferalDetailsRes();
		List<MotorGridCriteriaRes> referrals = new ArrayList<MotorGridCriteriaRes>();
		try {
			resp.setTotalCount(0);
		
			
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

			Root<EserviceLifeDetails> m = query.from(EserviceLifeDetails.class);
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
					m.get("customerCode").alias("customerId"),
					m.get("policyStartDate").alias("policyStartDate"), m.get("policyEndDate").alias("policyEndDate"),
					m.get("rejectReason").alias("rejectReason"),
					m.get("adminRemarks").alias("adminRemarks"),
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
			Root<EserviceLifeDetails> ocpm2 = riskId.from(EserviceLifeDetails.class);
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
			//Predicate n4 = m.get("status").in( new ArrayList<String>(Arrays.asList("D",status) ));
			Predicate n4 = cb.equal(m.get("status"), status);
			Predicate n6 = cb.equal(m.get("branchCode"), req.getBranchCode());
			Predicate n7 = cb.equal(m.get("riskId"), riskId);
			Predicate n15 = null;
			Predicate n16 = null;
			n15 = cb.equal(m.get("adminLoginId"), req.getApplicationId());
			if(StringUtils.isNotBlank(req.getBdmCode())){
				n16 = cb.equal(m.get("bdmCode"), req.getBdmCode());
			}else {
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
				query.where(n7,n1, n2, n3, n4, n6,n8,n9,n10,n11,n12,n13,n14,n16).orderBy(orderList);
						
			} else {
				query.where(n7,n1, n2, n3, n4, n6,n14,n16,n15).orderBy(orderList);
						
			}
			
			// Get Result
			TypedQuery<Tuple> result = em.createQuery(query);
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			List<Tuple> referralsList = result.getResultList();
			
			for ( Tuple r :referralsList   ) {
				MotorGridCriteriaRes res = new MotorGridCriteriaRes();
			//	res.setIdsCount(r.get("idsCount")==null ? null : (Long) r.get("idsCount"));
				res.setAdminRemarks(r.get("adminRemarks")==null ? "" : (String) r.get("adminRemarks"));
				res.setBranchCode(r.get("branchCode")==null ? "" : (String) r.get("branchCode"));
				res.setClientName(r.get("clientName")==null ? "" : (String) r.get("clientName"));
				res.setCompanyId(r.get("companyId")==null ? "" : (String) r.get("companyId"));
				res.setCustomerId(r.get("customerId")==null ? "" : (String) r.get("customerId"));
				res.setCustomerReferenceNo(r.get("customerReferenceNo")==null ? "" : (String) r.get("customerReferenceNo"));
				res.setIdNumber(r.get("idNumber")==null ? "" : (String) r.get("idNumber"));
				res.setPolicyEndDate(r.get("policyEndDate")==null ? null : (Date) r.get("policyEndDate"));
				res.setPolicyStartDate(r.get("policyStartDate")==null ? null : (Date) r.get("policyStartDate"));
				res.setProductId(r.get("productId")==null ? "" : (String) r.get("productId"));
				res.setQuoteNo(r.get("quoteNo")==null ? "" : (String) r.get("quoteNo"));
				res.setRejectReason(r.get("rejectReason")==null ? "" : (String) r.get("rejectReason"));
				res.setRequestReferenceNo(r.get("requestReferenceNo")==null ? "" : (String) r.get("requestReferenceNo"));
				res.setEndorsementDate(r.get("endorsementDate")==null ? null : (Date) r.get("endorsementDate"));
				res.setEndorsementEffdate(r.get("endorsementEffdate")==null ? null : (Date) r.get("endorsementEffdate"));
				res.setEndorsementRemarks(r.get("endorsementRemarks")==null ? "" : r.get("endorsementRemarks").toString());
				res.setEndorsementType(r.get("endorsementType")==null ? "" : r.get("endorsementType").toString());
				res.setEndorsementTypeDesc(r.get("endorsementTypeDesc")==null ? "" : r.get("endorsementTypeDesc").toString());
				res.setEndtCategDesc(r.get("endtCategDesc")==null ? "" : r.get("endtCategDesc").toString());
				res.setEndtCount(r.get("endtCount")==null ? BigDecimal.ZERO : new BigDecimal(r.get("endorsementType").toString()));
				res.setEndtPremium(r.get("endtPremium")==null ? null : Double.valueOf(r.get("endtPremium").toString()));
				res.setEndtPrevPolicyNo(r.get("endtPrevPolicyNo")==null ? "" : r.get("endtPrevPolicyNo").toString());
				res.setEndtPrevQuoteNo(r.get("endtPrevQuoteNo")==null ? "" : r.get("endtPrevQuoteNo").toString());
				res.setEndtStatus(r.get("endtStatus")==null ? "" : r.get("endtStatus").toString());
				res.setOriginalPolicyNo(r.get("originalPolicyNo")==null ? "" : r.get("originalPolicyNo").toString());
				referrals.add(res);
			}
		
			
			resp.setMotorGridCriteriaResRes(referrals);
			
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

	private int totalend(ExistingQuoteReq req, String status) {

		int count = 0;
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> query = cb.createQuery(Long.class);

			Root<EserviceLifeDetails> m = query.from(EserviceLifeDetails.class);
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
			
			query.multiselect(cb.count(m));

			//Riskid
			Subquery<Long> riskId = query.subquery(Long.class);
			Root<EserviceLifeDetails> ocpm2 = riskId.from(EserviceLifeDetails.class);
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
			
			//Predicate n4 = m.get("status").in( new ArrayList<String>(Arrays.asList("D",status) ));
			Predicate n4 = cb.equal(m.get("status"), status);
			Predicate n6 = cb.equal(m.get("branchCode"), req.getBranchCode());
			Predicate n7 = cb.equal(m.get("riskId"), riskId);
			Predicate n14 = cb.isNotNull(m.get("endorsementTypeDesc")); 
			Predicate n15 = null;
			Predicate n16 = null;
			n15 = cb.equal(m.get("adminLoginId"), req.getApplicationId());
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
				query.where(n7,n1, n2, n3, n4, n6,n8,n9,n10,n11,n12,n13,n14,n16).orderBy(orderList);
						
			} else {
				query.where(n7,n1, n2, n3, n4, n6,n14,n15,n16).orderBy(orderList);
						
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

	private int totalquote(ExistingQuoteReq req, String status) {

		int count = 0;
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> query = cb.createQuery(Long.class);

			Root<EserviceLifeDetails> m = query.from(EserviceLifeDetails.class);
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
			
			query.multiselect(cb.count(m));

			//Riskid
			Subquery<Long> riskId = query.subquery(Long.class);
			Root<EserviceLifeDetails> ocpm2 = riskId.from(EserviceLifeDetails.class);
			riskId.select(cb.max(ocpm2.get("riskId")));
			Predicate a3 = cb.equal(m.get("requestReferenceNo"), ocpm2.get("requestReferenceNo"));
			riskId.where(a3);
			
		

			// Where
			Predicate n1 = cb.equal(c.get("customerReferenceNo"), m.get("customerReferenceNo"));
			Predicate n2 = cb.equal(m.get("companyId"), req.getInsuranceId());
			Predicate n3 = cb.equal(m.get("productId"), req.getProductId());
			
			//Predicate n4 = m.get("status").in( new ArrayList<String>(Arrays.asList("D",status) ));
			Predicate n4 = cb.equal(m.get("status"), status);
			Predicate n6 = cb.equal(m.get("branchCode"), req.getBranchCode());
			Predicate n7 = cb.equal(m.get("riskId"), riskId);
			Predicate n14 = cb.isNull(m.get("endorsementTypeDesc")); 

			Predicate n15 = null;
			Predicate n16 = null;
			n15 = cb.equal(m.get("adminLoginId"), req.getApplicationId());
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
						
			} else {
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

	}
