package com.maan.eway.common.service.impl;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;

import com.google.gson.Gson;
import com.maan.eway.admin.res.PortfolioAdminSearchRes;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.common.req.SearchBrokerPolicyReq;
import com.maan.eway.common.res.PortfolioSearchDataRes;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

public class PortFolioSearchThreadCall implements Callable<Object> {

private Logger log = LogManager.getLogger(getClass());
	
	Gson json = new Gson();
	private String type;
	private SearchBrokerPolicyReq request ;
	private EntityManager em;
	
	public PortFolioSearchThreadCall(String type , SearchBrokerPolicyReq request , EntityManager em ) {
		this.type = type;
		this.request = request;
		this.em=em;
		
	} 
	
	
	@Override
	public  Map<String, Object>  call() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		try {

			type = StringUtils.isBlank(type) ? "" : type;

			log.info("Thread_OneTime--> type: " + type);

			if (type.equalsIgnoreCase("getProtfolioSearchDataCount")) {

				map.put("getProtfolioSearchDataCount", getProtfolioSearchDataCount(request));

			} else if (type.equalsIgnoreCase("getProtfolioSearchData")) {

				map.put("getProtfolioSearchData", getProtfolioSearchData(request));

			}
			
			

		} catch (Exception e) {
			log.error(e);
		}
		return map;
	}
	

	public synchronized List<PortfolioSearchDataRes> getProtfolioSearchData(SearchBrokerPolicyReq req) {
		List<PortfolioSearchDataRes> portfolioResList = new ArrayList<PortfolioSearchDataRes>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			Date startDate = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(startDate);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			startDate = cal.getTime();

			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());
			String policyNo = req.getPolicyNo();
//			String loginId = "";
//			if (req.getApplicationId().equalsIgnoreCase("1")) {
//				loginId = req.getLoginId();
//			} else {
//				loginId = req.getApplicationId();
//			}
			// Branch Res
			List<String> branches = new ArrayList<String>();
			branches.add(req.getBranchCode());
			
			if( StringUtils.isNotBlank(policyNo) && policyNo.length() > 4 ) {
				List<PortfolioAdminSearchRes> portFolioList = new ArrayList<PortfolioAdminSearchRes>();
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<PortfolioAdminSearchRes> query = cb.createQuery(PortfolioAdminSearchRes.class);

				// Find All
				Root<HomePositionMaster> m = query.from(HomePositionMaster.class);
				Root<PersonalInfo> c = query.from(PersonalInfo.class);
				
				// Select
				query.multiselect(
						c.get("customerReferenceNo").alias("customerReferenceNo"),
						c.get("idNumber").alias("idNumber"),
						c.get("clientName").alias("clientName"),
						c.get("mobileNo1").alias("mobileNo1"),
						c.get("isTaxExempted").alias("isTaxExempted"),
						c.get("taxExemptedId").alias("taxExemptedId"),
						// Vehicle Info
						m.get("companyId").alias("companyId"), 
						m.get("productId").alias("productId"),
						m.get("branchCode").alias("branchCode"), 
						m.get("requestReferenceNo").alias("requestReferenceNo"),
						m.get("quoteNo").alias("quoteNo"),
						m.get("customerId").alias("customerId"),
						m.get("inceptionDate").alias("inceptionDate"),
						m.get("expiryDate").alias("expiryDate"),
						m.get("overallPremiumLc").alias("overallPremiumLc"),
						m.get("overallPremiumFc").alias("overallPremiumFc"),
						m.get("policyNo").alias("policyNo"),
						m.get("debitAcNo").alias("debitAcNo"),
						m.get("debitTo").alias("debitTo"),
						m.get("debitToId").alias("debitToId"),
						m.get("debitNoteNo").alias("debitNoteNo"),
						m.get("debitNoteDate").alias("debitNoteDate"),
						m.get("creditTo").alias("creditTo"),
						m.get("creditToId").alias("creditToId"),
						m.get("creditNo").alias("creditNo"),
						m.get("creditDate").alias("creditDate"),
						m.get("emiYn").alias("emiYn"),
						m.get("installmentPeriod").alias("installmentPeriod"),
						m.get("noOfInstallment").alias("noOfInstallment"),
						m.get("paymentStatus").alias("paymentStatus"),
						m.get("effectiveDate").alias("effectiveDate"),
						m.get("currency").alias("currency"),
						m.get("originalPolicyNo").alias("originalPolicyNo"),
						m.get("applicationId").alias("applicationId")
						);

				// Order By
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(m.get("entryDate")));
					
				// Endt Count Max Filter
				Subquery<Long> endtCount = query.subquery(Long.class);
				Root<HomePositionMaster> ocpm1 = endtCount.from(HomePositionMaster.class);
				endtCount.select(cb.max(ocpm1.get("endtCount")));
				Predicate a1 = cb.equal(ocpm1.get("originalPolicyNo"), m.get("originalPolicyNo"));
				Predicate a2 = cb.equal(ocpm1.get("status"),m.get("status"));
				endtCount.where(a1,a2);
				
				 
				// Where
				Predicate n1 = cb.equal(c.get("customerId"), m.get("customerId"));
				Predicate n2 = cb.equal(m.get("companyId"), req.getInsuranceId());
				Predicate n3 = cb.equal(m.get("productId"), req.getProductId());
				Predicate n4 = cb.equal(m.get("status"), "P");
				Predicate n9 = cb.equal(m.get("integrationStatus"), "S");
				Predicate n7 = cb.greaterThanOrEqualTo(m.get("expiryDate"), startDate);
				Predicate n8 = cb.lessThanOrEqualTo(m.get("entryDate"), startDate);
				//Predicate n10 = cb.equal(m.get("endtCount"), endtCount);
				Predicate n11 = cb.notEqual(m.get("endtTypeId"),"842");
				Predicate n12 = cb.isNull(m.get("endtTypeId"));
				Predicate n13 = cb.or(n11,n12);
			//	Predicate n5 = cb.equal(m.get("applicationId"), "1");
				Expression<String> e0 = m.get("branchCode");
				Predicate n6 = e0.in(branches);
				//Predicate n14 = cb.like(cb.lower(m.get("policyNo")), "%" + policyNo + "%");
				//Predicate n14 = cb.equal(m.get("policyNo"),  policyNo );
				Predicate n14 = cb.like(cb.lower(m.get("policyNo")), "%" + policyNo + "%");
				query.where(n1, n2, n3, n4, n6,n7,n8,n9,n13,n14).orderBy(orderList);

				// Get Result
				TypedQuery<PortfolioAdminSearchRes> result = em.createQuery(query);
				result.setFirstResult(limit * offset);
				result.setMaxResults(offset);
				portFolioList = result.getResultList();
				
				for ( PortfolioAdminSearchRes portfolio : portFolioList ) {
					PortfolioSearchDataRes portfolioRes = new PortfolioSearchDataRes();
					portfolioRes = dozerMapper.map(portfolio, PortfolioSearchDataRes.class);
					
					portfolioResList.add(portfolioRes);
				}
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return portfolioResList;
	}

	public synchronized Long getProtfolioSearchDataCount(SearchBrokerPolicyReq req) {
		Long portfolioCount = 0L ;
		try {
			Date startDate = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(startDate);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			startDate = cal.getTime();

			String policyNo = req.getPolicyNo();
//			String loginId = "";
//			if (req.getApplicationId().equalsIgnoreCase("1")) {
//				loginId = req.getLoginId();
//			} else {
//				loginId = req.getApplicationId();
//			}
			// Branch Res
			List<String> branches = new ArrayList<String>();
			branches.add(req.getBranchCode());
			
			if( StringUtils.isNotBlank(policyNo) && policyNo.length() > 4 ) {
				
				List<Long> portFolioList = new ArrayList<Long>();
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Long> query = cb.createQuery(Long.class);

				// Find All
				Root<HomePositionMaster> m = query.from(HomePositionMaster.class);
				Root<PersonalInfo> c = query.from(PersonalInfo.class);

				// Select
				query.multiselect(cb.count(m) );

				// Order By
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(m.get("entryDate")));
					
				// Endt Count Max Filter
				Subquery<Long> endtCount = query.subquery(Long.class);
				Root<HomePositionMaster> ocpm1 = endtCount.from(HomePositionMaster.class);
				endtCount.select(cb.max(ocpm1.get("endtCount")));
				Predicate a1 = cb.equal(ocpm1.get("originalPolicyNo"), m.get("originalPolicyNo"));
				Predicate a2 = cb.equal(ocpm1.get("status"),m.get("status"));
				endtCount.where(a1,a2);
				
				 
				// Where
				Predicate n1 = cb.equal(c.get("customerId"), m.get("customerId"));
				Predicate n2 = cb.equal(m.get("companyId"), req.getInsuranceId());
				Predicate n3 = cb.equal(m.get("productId"), req.getProductId());
				Predicate n4 = cb.equal(m.get("status"), "P");
				Predicate n9 = cb.equal(m.get("integrationStatus"), "S");
				Predicate n7 = cb.greaterThanOrEqualTo(m.get("expiryDate"), startDate);
				Predicate n8 = cb.lessThanOrEqualTo(m.get("entryDate"), startDate);
				//Predicate n10 = cb.equal(m.get("endtCount"), endtCount);
				Predicate n11 = cb.notEqual(m.get("endtTypeId"),"842");
				Predicate n12 = cb.isNull(m.get("endtTypeId"));
				Predicate n13 = cb.or(n11,n12);
			//	Predicate n5 = cb.equal(m.get("applicationId"), "1");
				Expression<String> e0 = m.get("branchCode");
				Predicate n6 = e0.in(branches);
				//Predicate n14 = cb.like(cb.lower(m.get("policyNo")), "%" + policyNo + "%");
//				Predicate n14 = cb.equal(m.get("policyNo"),  policyNo );
				Predicate n14 = cb.like(cb.lower(m.get("policyNo")), "%" + policyNo + "%");
				query.where(n1, n2, n3, n4, n6,n7,n8,n9,n13,n14).orderBy(orderList);

				// Get Result
				TypedQuery<Long> result = em.createQuery(query);
				portFolioList = result.getResultList();
				portfolioCount = portFolioList.size() > 0 ? portFolioList.get(0) : 0L ;			
			
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return portfolioCount;
	}	    
}
