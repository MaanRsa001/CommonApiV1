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

import com.google.gson.Gson;
import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.EserviceTravelDetails;
import com.maan.eway.bean.LoginMaster;
import com.maan.eway.bean.LoginUserInfo;
import com.maan.eway.common.req.PortFolioDashBoardReq;
import com.maan.eway.common.res.PortfolioAdminPendingRes;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

public class PortFolioFetchThreadCall implements Callable<Object>  {
 
	private Logger log = LogManager.getLogger(getClass());
	
	Gson json = new Gson();
	private String type;
	private PortFolioDashBoardReq request ;
	private EntityManager em;
	
	public PortFolioFetchThreadCall(String type , PortFolioDashBoardReq request , EntityManager em ) {
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

			if (type.equalsIgnoreCase("getPortFolioMotorPendings")) {

				map.put("getPortFolioMotorPendings", getPortFolioMotorPendings(request));

			} else if (type.equalsIgnoreCase("getPortFolioTravelPendings")) {

				map.put("getPortFolioTravelPendings", getPortFolioTravelPendings(request));

			} else if (type.equalsIgnoreCase("getPortFolioBuildingPendings")) {

				map.put("getPortFolioBuildingPendings", getPortFolioBuildingPendings(request));

			} else if (type.equalsIgnoreCase("getPortFolioHumanPendings")) {

				map.put("getPortFolioHumanPendings", getPortFolioHumanPendings(request));

			}
			
			

		} catch (Exception e) {
			log.error(e);
		}
		return map;
	}
	
	
	public synchronized List<PortfolioAdminPendingRes> getPortFolioMotorPendings(PortFolioDashBoardReq req) {
		List<PortfolioAdminPendingRes> list = new ArrayList<PortfolioAdminPendingRes>();
		try {
			Calendar cal = new GregorianCalendar();

			Date startDate = req.getStartDate();
			cal.setTime(startDate);
			cal.set(Calendar.HOUR_OF_DAY, 1);
			startDate = cal.getTime();

			Date endDate = req.getEndDate();
			cal.setTime(endDate);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			endDate = cal.getTime();
			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<PortfolioAdminPendingRes> query = cb.createQuery(PortfolioAdminPendingRes.class);
			
			// Find All
			Root<EserviceMotorDetails> h = query.from(EserviceMotorDetails.class);
			Root<LoginMaster> l = query.from(LoginMaster.class);
			Root<LoginUserInfo> u = query.from(LoginUserInfo.class);
			
			// Select
			query.multiselect(  cb.countDistinct(h.get("requestReferenceNo") ).alias("count")  ,
								cb.sum(h.get("overallPremiumLc")).alias("overallPremiumLc") ,
								cb.sum(h.get("overallPremiumFc")).alias("overallPremiumFc") ,
								h.get("productId").as(Integer.class).alias("productId") ,
								h.get("productName").alias("productName") ,
								l.get("agencyCode").as(Integer.class).alias("oaCode") ,
								u.get("userName").alias("brokerName") ,
								l.get("userType").alias("userType") ,
								l.get("subUserType").alias("subUserType"),
								l.get("loginId").alias("loginId"));
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(h.get("productName")));

			// Broker condition
			Subquery<Long> loginId = query.subquery(Long.class);
			Root<LoginMaster> ocpm1 = loginId.from(LoginMaster.class);
			loginId.select(ocpm1.get("loginId"));
			Predicate a1 = cb.equal(ocpm1.get("companyId") , h.get("companyId") );
			Predicate a2 = cb.equal(ocpm1.get("loginId") , h.get("loginId") );
			Predicate a3 = cb.equal(ocpm1.get("oaCode") , l.get("agencyCode") );
			loginId.where(a1, a2 ,a3);
			
			// Where
			List<Predicate> predicate = new ArrayList<Predicate>();
			predicate.add(cb.equal(h.get("loginId"), loginId ));
			predicate.add(cb.greaterThanOrEqualTo(h.get("updatedDate"), startDate));
			predicate.add(cb.lessThanOrEqualTo(h.get("updatedDate"), endDate));
			predicate.add(cb.equal(h.get("companyId"), req.getInsuranceId()));
			predicate.add(cb.equal(l.get("userType"), "Broker"));
			predicate.add(cb.equal(u.get("loginId"), l.get("loginId")));
			if(StringUtils.isNotBlank(req.getLoginId())  )  {
				predicate.add(cb.equal(l.get("loginId"), req.getLoginId()));
			}
			
			// Business Type Condition
			String businessType = StringUtils.isBlank(req.getBusinessType()) ? "" : req.getBusinessType() ;  
				
			// Pending Quote Condition 
			if("Q".equalsIgnoreCase(businessType) ) {
				// Status Not
				Expression<String> e0 = h.get("status");
				List<String> statusNot = new ArrayList<String>();
				statusNot.add("P");
				statusNot.add("D");
				statusNot.add("E");
			//	statusNot.add("N");
				predicate.add(e0.in(statusNot).not() );
				
				// Endt Status Not
//				Expression<String> e1 = h.get("endtStatus");
//				List<String> endtStatusNot = new ArrayList<String>();
//				endtStatusNot.add("C");
//				endtStatusNot.add("P");
//				predicate.add(e1.in(endtStatusNot).not() );
			} 
			
			// Product  & Branch Condition
			if(StringUtils.isNotBlank(req.getProductId())  ) 
				predicate.add(cb.equal(h.get("productId"), req.getProductId()));
			if(StringUtils.isNotBlank(req.getBranchCode()) &&  (!"99999".equalsIgnoreCase(req.getBranchCode())) )  
				predicate.add(cb.equal(h.get("branchCode"), req.getBranchCode()));
			
			
			query.where(predicate.toArray(new Predicate[0])).groupBy(h.get("productId") ,
					h.get("productName") ,l.get("agencyCode"),u.get("userName") ,
					l.get("userType"),l.get("subUserType") ,l.get("loginId") ) 
			.orderBy(orderList);
			
			// Get Result
			TypedQuery<PortfolioAdminPendingRes> result = em.createQuery(query);
			list = result.getResultList();
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			
		}
		return list ;
	}
	
	public synchronized List<PortfolioAdminPendingRes> getPortFolioBuildingPendings(PortFolioDashBoardReq req) {
		List<PortfolioAdminPendingRes> list = new ArrayList<PortfolioAdminPendingRes>();
		try {
			Calendar cal = new GregorianCalendar();

			Date startDate = req.getStartDate();
			cal.setTime(startDate);
			cal.set(Calendar.HOUR_OF_DAY, 1);
			startDate = cal.getTime();

			Date endDate = req.getEndDate();
			cal.setTime(endDate);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			endDate = cal.getTime();
			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<PortfolioAdminPendingRes> query = cb.createQuery(PortfolioAdminPendingRes.class);
			
			// Find All
			Root<EserviceBuildingDetails> h = query.from(EserviceBuildingDetails.class);
			Root<LoginMaster> l = query.from(LoginMaster.class);
			Root<LoginUserInfo> u = query.from(LoginUserInfo.class);
			
			// Select
			query.multiselect(  cb.countDistinct(h.get("requestReferenceNo") ).alias("count")  ,
								cb.sum(h.get("overallPremiumLc")).alias("overallPremiumLc") ,
								cb.sum(h.get("overallPremiumFc")).alias("overallPremiumFc") ,
								h.get("productId").as(Integer.class).alias("productId") ,
								h.get("productDesc").alias("productName") ,
								l.get("agencyCode").as(Integer.class).alias("oaCode") ,
								u.get("userName").alias("brokerName") ,
								l.get("userType").alias("userType") ,
								l.get("subUserType").alias("subUserType"),
								l.get("loginId").alias("loginId"));
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(h.get("productDesc")));

			// Broker condition
			Subquery<Long> loginId = query.subquery(Long.class);
			Root<LoginMaster> ocpm1 = loginId.from(LoginMaster.class);
			loginId.select(ocpm1.get("loginId"));
			Predicate a1 = cb.equal(ocpm1.get("companyId") , h.get("companyId") );
			Predicate a2 = cb.equal(ocpm1.get("loginId") , h.get("loginId") );
			Predicate a3 = cb.equal(ocpm1.get("oaCode") , l.get("agencyCode") );
			loginId.where(a1, a2 ,a3);
			
			// Where
			List<Predicate> predicate = new ArrayList<Predicate>();
			predicate.add(cb.equal(h.get("loginId"), loginId ));
			predicate.add(cb.greaterThanOrEqualTo(h.get("updatedDate"), startDate));
			predicate.add(cb.lessThanOrEqualTo(h.get("updatedDate"), endDate));
			predicate.add(cb.equal(h.get("companyId"), req.getInsuranceId()));
			predicate.add(cb.equal(l.get("userType"), "Broker"));
			predicate.add(cb.equal(u.get("loginId"), l.get("loginId")));
			if(StringUtils.isNotBlank(req.getLoginId())  )  {
				predicate.add(cb.equal(l.get("loginId"), req.getLoginId()));
			}
			
			// Business Type Condition
			String businessType = StringUtils.isBlank(req.getBusinessType()) ? "" : req.getBusinessType() ;  
				
			// Pending Quote Condition 
			if("Q".equalsIgnoreCase(businessType) ) {
				// Status Not
				Expression<String> e0 = h.get("status");
				List<String> statusNot = new ArrayList<String>();
				statusNot.add("P");
				statusNot.add("E");
		//		statusNot.add("D");
		//		statusNot.add("N");
				predicate.add(e0.in(statusNot).not() );
				
				// Endt Status Not
//				Expression<String> e1 = h.get("endtStatus");
//				List<String> endtStatusNot = new ArrayList<String>();
//				endtStatusNot.add("C");
//				predicate.add(e1.in(endtStatusNot).not() );
			} 
			
			// Product  & Branch Condition
			if(StringUtils.isNotBlank(req.getProductId())  ) 
				predicate.add(cb.equal(h.get("productId"), req.getProductId()));
			if(StringUtils.isNotBlank(req.getBranchCode()) &&  (!"99999".equalsIgnoreCase(req.getBranchCode())) )  
				predicate.add(cb.equal(h.get("branchCode"), req.getBranchCode()));
			
			
			query.where(predicate.toArray(new Predicate[0])).groupBy(h.get("productId") ,
					h.get("productDesc") ,l.get("agencyCode"),u.get("userName") ,
					l.get("userType"),l.get("subUserType") ,l.get("loginId") ) 
			.orderBy(orderList);
			
			// Get Result
			TypedQuery<PortfolioAdminPendingRes> result = em.createQuery(query);
			list = result.getResultList();
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			
		}
		return list ;
	}
	
	public synchronized List<PortfolioAdminPendingRes> getPortFolioTravelPendings(PortFolioDashBoardReq req) {
		List<PortfolioAdminPendingRes> list = new ArrayList<PortfolioAdminPendingRes>();
		try {
			Calendar cal = new GregorianCalendar();

			Date startDate = req.getStartDate();
			cal.setTime(startDate);
			cal.set(Calendar.HOUR_OF_DAY, 1);
			startDate = cal.getTime();

			Date endDate = req.getEndDate();
			cal.setTime(endDate);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			endDate = cal.getTime();
			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<PortfolioAdminPendingRes> query = cb.createQuery(PortfolioAdminPendingRes.class);
			
			// Find All
			Root<EserviceTravelDetails> h = query.from(EserviceTravelDetails.class);
			Root<LoginMaster> l = query.from(LoginMaster.class);
			Root<LoginUserInfo> u = query.from(LoginUserInfo.class);
			
			// Select
			query.multiselect(  cb.countDistinct(h.get("requestReferenceNo") ).alias("count")  ,
								cb.sum(h.get("overallPremiumLc")).alias("overallPremiumLc") ,
								cb.sum(h.get("overallPremiumFc")).alias("overallPremiumFc") ,
								h.get("productId").as(Integer.class).alias("productId") ,
								h.get("productName").alias("productName") ,
								l.get("agencyCode").as(Integer.class).alias("oaCode") ,
								u.get("userName").alias("brokerName") ,
								l.get("userType").alias("userType") ,
								l.get("subUserType").alias("subUserType"),
								l.get("loginId").alias("loginId"));
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(h.get("productName")));

			// Broker condition
			Subquery<Long> loginId = query.subquery(Long.class);
			Root<LoginMaster> ocpm1 = loginId.from(LoginMaster.class);
			loginId.select(ocpm1.get("loginId"));
			Predicate a1 = cb.equal(ocpm1.get("companyId") , h.get("companyId") );
			Predicate a2 = cb.equal(ocpm1.get("loginId") , h.get("loginId") );
			Predicate a3 = cb.equal(ocpm1.get("oaCode") , l.get("agencyCode") );
			loginId.where(a1, a2 ,a3);
			
			// Where
			List<Predicate> predicate = new ArrayList<Predicate>();
			predicate.add(cb.equal(h.get("loginId"), loginId ));
			predicate.add(cb.greaterThanOrEqualTo(h.get("updatedDate"), startDate));
			predicate.add(cb.lessThanOrEqualTo(h.get("updatedDate"), endDate));
			predicate.add(cb.equal(h.get("companyId"), req.getInsuranceId()));
			predicate.add(cb.equal(l.get("userType"), "Broker"));
			predicate.add(cb.equal(u.get("loginId"), l.get("loginId")));
			if(StringUtils.isNotBlank(req.getLoginId())  )  {
				predicate.add(cb.equal(l.get("loginId"), req.getLoginId()));
			}
			
			// Business Type Condition
			String businessType = StringUtils.isBlank(req.getBusinessType()) ? "" : req.getBusinessType() ;  
				
			// Pending Quote Condition 
			if("Q".equalsIgnoreCase(businessType) ) {
				// Status Not
				Expression<String> e0 = h.get("status");
				List<String> statusNot = new ArrayList<String>();
				statusNot.add("P");
				statusNot.add("E");
			//	statusNot.add("D");
			//	statusNot.add("N");
				predicate.add(e0.in(statusNot).not() );
				
				// Endt Status Not
//				Expression<String> e1 = h.get("endtStatus");
//				List<String> endtStatusNot = new ArrayList<String>();
//				endtStatusNot.add("C");
//				predicate.add(e1.in(endtStatusNot).not() );
			} 
			
			// Product  & Branch Condition
			if(StringUtils.isNotBlank(req.getProductId())  ) 
				predicate.add(cb.equal(h.get("productId"), req.getProductId()));
			if(StringUtils.isNotBlank(req.getBranchCode()) &&  (!"99999".equalsIgnoreCase(req.getBranchCode())) )  
				predicate.add(cb.equal(h.get("branchCode"), req.getBranchCode()));
			
			
			query.where(predicate.toArray(new Predicate[0])).groupBy(h.get("productId") ,
					h.get("productName") ,l.get("agencyCode"),u.get("userName") ,
					l.get("userType"),l.get("subUserType") ,l.get("loginId") ) 
			.orderBy(orderList);
			
			// Get Result
			TypedQuery<PortfolioAdminPendingRes> result = em.createQuery(query);
			list = result.getResultList();
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			
		}
		return list ;
	}
	
	
	public synchronized List<PortfolioAdminPendingRes> getPortFolioHumanPendings(PortFolioDashBoardReq req) {
		List<PortfolioAdminPendingRes> list = new ArrayList<PortfolioAdminPendingRes>();
		try {
			Calendar cal = new GregorianCalendar();

			Date startDate = req.getStartDate();
			cal.setTime(startDate);
			cal.set(Calendar.HOUR_OF_DAY, 1);
			startDate = cal.getTime();

			Date endDate = req.getEndDate();
			cal.setTime(endDate);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			endDate = cal.getTime();
			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<PortfolioAdminPendingRes> query = cb.createQuery(PortfolioAdminPendingRes.class);
			
			// Find All
			Root<EserviceCommonDetails> h = query.from(EserviceCommonDetails.class);
			Root<LoginMaster> l = query.from(LoginMaster.class);
			Root<LoginUserInfo> u = query.from(LoginUserInfo.class);
			
			// Select
			query.multiselect(  cb.countDistinct(h.get("requestReferenceNo") ).alias("count")  ,
								cb.sum(h.get("overallPremiumLc")).alias("overallPremiumLc") ,
								cb.sum(h.get("overallPremiumFc")).alias("overallPremiumFc") ,
								h.get("productId").as(Integer.class).alias("productId") ,
								h.get("productDesc").alias("productName") ,
								l.get("agencyCode").as(Integer.class).alias("oaCode") ,
								u.get("userName").alias("brokerName") ,
								l.get("userType").alias("userType") ,
								l.get("subUserType").alias("subUserType"),
								l.get("loginId").alias("loginId"));
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(h.get("productDesc")));

			// Broker condition
			Subquery<Long> loginId = query.subquery(Long.class);
			Root<LoginMaster> ocpm1 = loginId.from(LoginMaster.class);
			loginId.select(ocpm1.get("loginId"));
			Predicate a1 = cb.equal(ocpm1.get("companyId") , h.get("companyId") );
			Predicate a2 = cb.equal(ocpm1.get("loginId") , h.get("loginId") );
			Predicate a3 = cb.equal(ocpm1.get("oaCode") , l.get("agencyCode") );
			loginId.where(a1, a2 ,a3);
			
			// Where
			List<Predicate> predicate = new ArrayList<Predicate>();
			predicate.add(cb.equal(h.get("loginId"), loginId ));
			predicate.add(cb.greaterThanOrEqualTo(h.get("updatedDate"), startDate));
			predicate.add(cb.lessThanOrEqualTo(h.get("updatedDate"), endDate));
			predicate.add(cb.equal(h.get("companyId"), req.getInsuranceId()));
			predicate.add(cb.equal(l.get("userType"), "Broker"));
			predicate.add(cb.equal(u.get("loginId"), l.get("loginId")));
			if(StringUtils.isNotBlank(req.getLoginId())  )  {
				predicate.add(cb.equal(l.get("loginId"), req.getLoginId()));
			}
			
			// Business Type Condition
			String businessType = StringUtils.isBlank(req.getBusinessType()) ? "" : req.getBusinessType() ;  
				
			// Pending Quote Condition 
			if("Q".equalsIgnoreCase(businessType) ) {
				// Status Not
				Expression<String> e0 = h.get("status");
				List<String> statusNot = new ArrayList<String>();
				statusNot.add("P");
				statusNot.add("D");
				statusNot.add("E");
			//	statusNot.add("N");
				predicate.add(e0.in(statusNot).not() );
				
//				// Endt Status Not
//				Expression<String> e1 = h.get("endtStatus");
//				List<String> endtStatusNot = new ArrayList<String>();
//				endtStatusNot.add("C");
//				predicate.add(e1.in(endtStatusNot).not() );
			} 
			
			// Product  & Branch Condition
			if(StringUtils.isNotBlank(req.getProductId())  ) 
				predicate.add(cb.equal(h.get("productId"), req.getProductId()));
			if(StringUtils.isNotBlank(req.getBranchCode()) &&  (!"99999".equalsIgnoreCase(req.getBranchCode())) )  
				predicate.add(cb.equal(h.get("branchCode"), req.getBranchCode()));
			
			
			query.where(predicate.toArray(new Predicate[0])).groupBy(h.get("productId") ,
					h.get("productDesc") ,l.get("agencyCode"),u.get("userName") ,
					l.get("userType"),l.get("subUserType") ,l.get("loginId") ) 
			.orderBy(orderList);
			
			// Get Result
			TypedQuery<PortfolioAdminPendingRes> result = em.createQuery(query);
			list = result.getResultList();
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			
		}
		return list ;
	}
}
