package com.maan.eway.common.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.gson.Gson;
import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.EserviceTravelDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.common.req.ExistingQuoteReq;
import com.maan.eway.common.res.LoginQuoteCriteriaResponse;

import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

public class ViewLoginExistingQuoteThreadCall implements Callable<Object>  {
 
	private Logger log = LogManager.getLogger(getClass());
	
	Gson json = new Gson();
	private String type;
	private ExistingQuoteReq request ;
	private EntityManager em;

	private Date startDate;

	private Date endDate;
	
	public ViewLoginExistingQuoteThreadCall(String type , ExistingQuoteReq request , EntityManager em ,Date startDate,Date endDate ) {
		this.type = type;
		this.request = request;
		this.em=em;
		this.startDate=startDate;
		this.endDate=endDate;
		
	} 
	
	
	@Override
	public  Map<String, Object>  call() throws Exception {
		Map<String, Object> map = new HashMap<String, Object>();
		try {

			type = StringUtils.isBlank(type) ? "" : type;

			log.info("Thread_OneTime--> type: " + type);

			if (type.equalsIgnoreCase("getMotorExistingQuote")) {
				System.out.println("Calling Motor Existing Quote ");
				map.put("getMotorExistingQuote", getMotorExistingQuote(request,startDate,endDate));

			}
			else if (type.equalsIgnoreCase("getPortFolioTravelPendings")) {

				map.put("getTravelExistingQuote", getTravelExistingQuote(request,startDate,endDate));

			} else if (type.equalsIgnoreCase("getAssetExistingQuote")) {

				map.put("getAssetExistingQuote", getBuildingExistingQuote(request,startDate,endDate));

			} else if (type.equalsIgnoreCase("getHummanExistingQuote")) {

				map.put("getHummanExistingQuote", getCommonExistingQuote(request,startDate,endDate));

			}
			
			

		} catch (Exception e) {
			log.error(e);
		}
		return map;
	}

	public synchronized  List<LoginQuoteCriteriaResponse> getMotorExistingQuote(ExistingQuoteReq req,Date startDate,Date endDate) {
			
		List<LoginQuoteCriteriaResponse> resp = new ArrayList<LoginQuoteCriteriaResponse>();
		List<EserviceMotorDetails> list = new ArrayList<EserviceMotorDetails>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try {
			System.out.println("motor Existing ");
			// Get Datas
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<EserviceMotorDetails> query = cb.createQuery(EserviceMotorDetails.class);

			// Find All
			Root<EserviceMotorDetails> m = query.from(EserviceMotorDetails.class);
			
			// Select
			query.select(m);
			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(m.get("entryDate")));

			// Where
			List<Predicate> predicate = new ArrayList<Predicate>();
			
			predicate.add(cb.equal(m.get("companyId"), req.getInsuranceId()));
			predicate.add(cb.equal(m.get("status"), "Y"));
			predicate.add(cb.lessThanOrEqualTo(m.get("updatedDate"), endDate));
			predicate.add(cb.greaterThanOrEqualTo(m.get("updatedDate"), startDate));
			predicate.add(cb.isNull(m.get("endorsementType")));
			if(StringUtils.isNotBlank(req.getLoginId())  )  {
				predicate.add(cb.equal(m.get("loginId"), req.getLoginId()));
			}
			// Product  & Branch Condition
			if(StringUtils.isNotBlank(req.getProductId())  ) {
				predicate.add(cb.equal(m.get("productId"), req.getProductId()));
			}
			if(StringUtils.isNotBlank(req.getBranchCode()) &&  (!"99999".equalsIgnoreCase(req.getBranchCode())) ) {  
				predicate.add(cb.equal(m.get("branchCode"), req.getBranchCode()));
			}
			// Risk Max Filter
			Subquery<Long> riskId = query.subquery(Long.class);
			Root<EserviceMotorDetails> ocp = riskId.from(EserviceMotorDetails.class);
			riskId.select(cb.max(ocp.get("riskId")));
			Predicate a3 = cb.equal(ocp.get("requestReferenceNo"), m.get("requestReferenceNo"));
			riskId.where(a3);
			
			predicate.add(cb.equal(m.get("riskId"),  riskId ));
		
			query.where(predicate.toArray(new Predicate[0])).orderBy(orderList);	
		
			// Get Result
			TypedQuery<EserviceMotorDetails> result = em.createQuery(query);
			list = result.getResultList();
			
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getEntryDate()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(EserviceMotorDetails :: getEntryDate ).reversed());
			if(list!=null &&list.size()>0) {
//			List<HomePositionMaster> policyList= getProtfolioActive(req,endDate,"P") ;
			Date entryDate=list.get(0).getEntryDate();	
			String productName=list.get(0).getProductName();
			String productId=list.get(0).getProductId();
			LoginQuoteCriteriaResponse resp1 = new LoginQuoteCriteriaResponse();
//			resp1.setLastPolicyDate(sdf.format(policyList.get(0).getInceptionDate()));
			resp1.setLastQuoteDate(sdf.format(entryDate));
			resp1.setProductId(productId);
			resp1.setProductName(productName);
			resp1.setQuoteCount(totalcountexisting(req, startDate,endDate, "Y"));
//			resp1.setPolicyCount(totalcountportfolioactive(req, startDate, "P"));
			resp1.setEndtsCount(null);
//			resp1.setTotalCommission(null);
//			resp1.setPremium(null);	
			resp.add(resp1);
			}
			} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resp;
	}
	private static <T> java.util.function.Predicate<T> distinctByKey(java.util.function.Function<? super T, ?> keyExtractor) {
	    Map<Object, Boolean> seen = new ConcurrentHashMap<>();
	    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

	private Long totalcountexisting(ExistingQuoteReq req, Date startDate, Date endDate, String status) {
		Long count = 0l;
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> query = cb.createQuery(Long.class);

			// Find All
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
			Root<EserviceMotorDetails> m = query.from(EserviceMotorDetails.class);
			
			// Select
			query.multiselect(cb.count(m));			
			

			List<Predicate> predicate = new ArrayList<Predicate>();
			predicate.add(cb.equal(c.get("customerReferenceNo"), m.get("customerReferenceNo")));
			predicate.add(cb.equal(m.get("companyId"), req.getInsuranceId()));
			predicate.add(cb.equal(m.get("status"), status));
			predicate.add(cb.lessThanOrEqualTo(m.get("updatedDate"), endDate));
			predicate.add(cb.greaterThanOrEqualTo(m.get("updatedDate"), startDate));
			predicate.add(cb.isNull(m.get("endorsementType")));

			if(StringUtils.isNotBlank(req.getLoginId())  )  {
				predicate.add(cb.equal(m.get("loginId"), req.getLoginId()));
			}
			// Product  & Branch Condition
			if(StringUtils.isNotBlank(req.getProductId())  ) {
				predicate.add(cb.equal(m.get("productId"), req.getProductId()));
			}
			if(StringUtils.isNotBlank(req.getBranchCode()) &&  (!"99999".equalsIgnoreCase(req.getBranchCode())) ) {  
				predicate.add(cb.equal(m.get("branchCode"), req.getBranchCode()));
			}
			// Risk Max Filter
			Subquery<Long> riskId = query.subquery(Long.class);
			Root<EserviceMotorDetails> ocp = riskId.from(EserviceMotorDetails.class);
			riskId.select(cb.max(ocp.get("riskId")));
			Predicate a3 = cb.equal(ocp.get("requestReferenceNo"), m.get("requestReferenceNo"));
			riskId.where(a3);
			
			predicate.add(cb.equal(m.get("riskId"),  riskId ));
			
			query.where(predicate.toArray(new Predicate[0]));

		
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
	public List<HomePositionMaster> getProtfolioActive(ExistingQuoteReq req, Date startDate,String status) {
		List<HomePositionMaster> list = new ArrayList<HomePositionMaster>();
		try {
			
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<HomePositionMaster> query = cb.createQuery(HomePositionMaster.class);
			// Find All
			Root<HomePositionMaster> m = query.from(HomePositionMaster.class);
			Root<PersonalInfo> c = query.from(PersonalInfo.class);

//			// Endt Count Max Filter
//			Subquery<Long> endtCount2 = query.subquery(Long.class);
//			Root<HomePositionMaster> ocpm3 = endtCount2.from(HomePositionMaster.class);
//			endtCount2.select(cb.min(ocpm3.get("endtCount")));
//			Predicate a5 = cb.equal(ocpm3.get("originalPolicyNo"), m.get("originalPolicyNo"));
//			Predicate a6 = cb.equal(ocpm3.get("status"),m.get("status"));
//			endtCount2.where(a5,a6);
//			
			// Quote No Filter
//			Subquery<String> quoteNo = query.subquery(String.class);
//			Root<HomePositionMaster> ocpm2 = quoteNo.from(HomePositionMaster.class);
//			quoteNo.select(ocpm2.get("quoteNo"));
//			Predicate a3 = cb.equal(ocpm2.get("originalPolicyNo"), m.get("originalPolicyNo"));
//			Predicate a4 = cb.equal(ocpm2.get("status"),m.get("status"));
//			Predicate a7 = cb.equal(ocpm2.get("endtCount"), endtCount2);  
//			quoteNo.where(a3,a4,a7);
			
			
//			 Select
			query.select(m);
//			query.multiselect(
//					m.get("inceptionDate").alias("inceptionDate"),
//					m.get("expiryDate").alias("expiryDate"),
//					m.get("overallPremiumLc").alias("overallPremiumLc"),
//					m.get("overallPremiumFc").alias("overallPremiumFc")
//					);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(m.get("entryDate")));
				
			// Endt Count Max Filter
			Subquery<Long> endtCount = query.subquery(Long.class);
			Root<HomePositionMaster> ocpm1 = endtCount.from(HomePositionMaster.class);
			endtCount.select(cb.max(ocpm1.get("endtCount")));
			Predicate a1 = cb.equal(ocpm1.get("originalPolicyNo"), m.get("originalPolicyNo"));
			//Predicate a2 = cb.equal(ocpm1.get("status"),m.get("status"));
			endtCount.where(a1);
			
			// Where
			Predicate n1 = cb.equal(c.get("customerId"), m.get("customerId"));
			Predicate n2 = cb.equal(m.get("companyId"), req.getInsuranceId());
			Predicate n4 = cb.equal(m.get("status"), status);
			Predicate n9 = cb.equal(m.get("integrationStatus"), "S");  //policy convert
			Predicate n7 =cb.greaterThanOrEqualTo(m.get("updatedDate"), startDate);
			Predicate n8 =cb.lessThanOrEqualTo(m.get("updatedDate"), endDate);
			Predicate n10 = cb.equal(m.get("endtCount"), endtCount);   //
			Predicate n11 = cb.notEqual(m.get("endtTypeId"),"842");   //policy calcellation
			Predicate n12 = cb.isNull(m.get("endtTypeId"));     
			Predicate n13 = cb.or(n11,n12);     
			
			Predicate n5 = null;
			Predicate n14 = null;
			Predicate n3 = null;

			if(StringUtils.isNotBlank(req.getLoginId())  )  {
				n5 =cb.equal(m.get("loginId"), req.getLoginId());
			}
			if (StringUtils.isNotBlank(req.getProductId())) {
				n14=cb.equal(m.get("productId"), req.getProductId());
			}
			if (StringUtils.isNotBlank(req.getBranchCode()) && (!"99999".equalsIgnoreCase(req.getBranchCode()))) {
				n3=cb.equal(m.get("branchCode"), req.getBranchCode());
			}
		
			query.where(n1, n2, n3, n4, n5,n7,n8,n9,n10,n13,n14).orderBy(orderList);

			// Get Result
			TypedQuery<HomePositionMaster> result = em.createQuery(query);
			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getInceptionDate()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(HomePositionMaster :: getInceptionDate ).reversed());
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return list;
	}

	private Long totalcountportfolioactive(ExistingQuoteReq req, Date startDate, String status) {
		Long count = 0l;
		try {	
			
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> query = cb.createQuery(Long.class);
		
			// Find All
			Root<HomePositionMaster> m = query.from(HomePositionMaster.class);
			Root<PersonalInfo> c = query.from(PersonalInfo.class);
		
			// Select
			query.multiselect(cb.count(m));
		
			// Endt Count Max Filter
			Subquery<Long> endtCount = query.subquery(Long.class);
			Root<HomePositionMaster> ocpm1 = endtCount.from(HomePositionMaster.class);
			endtCount.select(cb.max(ocpm1.get("endtCount")));
			Predicate a1 = cb.equal(ocpm1.get("originalPolicyNo"), m.get("originalPolicyNo"));
		//	Predicate a2 = cb.equal(ocpm1.get("status"),m.get("status"));
			endtCount.where(a1);
			
			// Where
			Predicate n1 = cb.equal(c.get("customerId"), m.get("customerId"));
			Predicate n2 = cb.equal(m.get("companyId"), req.getInsuranceId());
			Predicate n3 = cb.equal(m.get("productId"), req.getProductId());
			Predicate n4 = cb.equal(m.get("status"), status);
			Predicate n9 = cb.equal(m.get("integrationStatus"), "S");  //policy convert
			Predicate n7 = cb.greaterThanOrEqualTo(m.get("expiryDate"), startDate);
			Predicate n8 = cb.lessThanOrEqualTo(m.get("entryDate"), startDate);
			Predicate n10 = cb.equal(m.get("endtCount"), endtCount);   //
			Predicate n11 = cb.notEqual(m.get("endtTypeId"),"842");   //
			Predicate n12 = cb.isNull(m.get("endtTypeId"));     
			Predicate n13 = cb.or(n11,n12);
		
			Predicate n5 = null;
			Predicate n14 = null;
			Predicate n6 = null;
			if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
				n14 = cb.equal(m.get("loginId"), req.getLoginId());
				n6 =cb.equal(m.get("brokerBranchCode"), req.getBrokerBranchCode());
				query.where(n1, n2, n3, n4, n6,n7,n8,n9,n10,n13,n14);
			} else {
				if(StringUtils.isNotBlank(req.getBdmCode())){
					n5 = cb.equal(m.get("applicationId"), req.getApplicationId());
					n14 = cb.equal(m.get("bdmCode"), req.getBdmCode());
					n6 =cb.equal(m.get("branchCode"), req.getBranchCode());
					query.where(n1, n2, n3, n4, n5, n6,n7,n8,n9,n10,n13,n14);
				}else {
					n5 = cb.equal(m.get("applicationId"), req.getApplicationId());
					n14 = cb.equal(m.get("loginId"), req.getLoginId());
					n6 =cb.equal(m.get("branchCode"), req.getBranchCode());
					query.where(n1, n2, n3, n4, n5, n6,n7,n8,n9,n10,n13,n14);
				}
			}

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
	public  synchronized  List<LoginQuoteCriteriaResponse> getTravelExistingQuote(ExistingQuoteReq req,Date startDate,Date endDate) {
		List<LoginQuoteCriteriaResponse> resp = new ArrayList<LoginQuoteCriteriaResponse>();
		List<EserviceTravelDetails> list = new ArrayList<EserviceTravelDetails>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try {
			
			// Get Datas
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<EserviceTravelDetails> query = cb.createQuery(EserviceTravelDetails.class);

			// Find All
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
			Root<EserviceTravelDetails> m = query.from(EserviceTravelDetails.class);
			
			
			
			query.select(m);

			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(m.get("entryDate")));
			
			
		    // Where	
			List<Predicate> predicate = new ArrayList<Predicate>();
			
			predicate.add(cb.equal(m.get("companyId"), req.getInsuranceId()));
			predicate.add(cb.equal(m.get("status"), "Y"));
			predicate.add(cb.lessThanOrEqualTo(m.get("updatedDate"), endDate));
			predicate.add(cb.greaterThanOrEqualTo(m.get("updatedDate"), startDate));
			predicate.add(cb.isNull(m.get("endorsementType")));
			if(StringUtils.isNotBlank(req.getLoginId())  )  {
				predicate.add(cb.equal(m.get("loginId"), req.getLoginId()));
			}
			// Product  & Branch Condition
			if(StringUtils.isNotBlank(req.getProductId())  ) {
				predicate.add(cb.equal(m.get("productId"), req.getProductId()));
			}
			if(StringUtils.isNotBlank(req.getBranchCode()) &&  (!"99999".equalsIgnoreCase(req.getBranchCode())) ) {  
				predicate.add(cb.equal(m.get("branchCode"), req.getBranchCode()));
			}
			query.where(predicate.toArray(new Predicate[0])).orderBy(orderList);
				
			TypedQuery<EserviceTravelDetails> result = em.createQuery(query);
			list = result.getResultList();

			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getEntryDate()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(EserviceTravelDetails :: getEntryDate ).reversed());
			if(list!=null &&list.size()>0) {
//			List<HomePositionMaster> policyList= getProtfolioActive(req,endDate,"P") ;
			Date entryDate=list.get(0).getEntryDate();	
			String productName=list.get(0).getProductName();
			String productId=list.get(0).getProductId();
			LoginQuoteCriteriaResponse resp1 = new LoginQuoteCriteriaResponse();
//			resp1.setLastPolicyDate(sdf.format(policyList.get(0).getInceptionDate()));
			resp1.setLastQuoteDate(sdf.format(entryDate));
			resp1.setProductId(productId);
			resp1.setProductName(productName);
			resp1.setQuoteCount(totalcountexist(req, startDate,endDate, "Y"));
//			resp1.setPolicyCount(totalcountportfolioactive(req, startDate, "P"));
			resp1.setEndtsCount(null);
//			resp1.setTotalCommission(null);
//			resp1.setPremium(null);	
			resp.add(resp1);
			}
			
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
			
			List<Predicate> predicate = new ArrayList<Predicate>();
			predicate.add(cb.equal(c.get("customerReferenceNo"), m.get("customerReferenceNo")));
			predicate.add(cb.equal(m.get("companyId"), req.getInsuranceId()));
			predicate.add(cb.equal(m.get("status"), status));
			predicate.add(cb.lessThanOrEqualTo(m.get("updatedDate"), endDate));
			predicate.add(cb.greaterThanOrEqualTo(m.get("updatedDate"), startDate));
			predicate.add(cb.isNull(m.get("endorsementType")));

			if(StringUtils.isNotBlank(req.getLoginId())  )  {
				predicate.add(cb.equal(m.get("loginId"), req.getLoginId()));
			}
			// Product  & Branch Condition
			if(StringUtils.isNotBlank(req.getProductId())  ) {
				predicate.add(cb.equal(m.get("productId"), req.getProductId()));
			}
			if(StringUtils.isNotBlank(req.getBranchCode()) &&  (!"99999".equalsIgnoreCase(req.getBranchCode())) ) {  
				predicate.add(cb.equal(m.get("branchCode"), req.getBranchCode()));
			}


			query.where(predicate.toArray(new Predicate[0]));
			
		
			
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

public synchronized  List<LoginQuoteCriteriaResponse> getBuildingExistingQuote(ExistingQuoteReq req,Date startDate,Date endDate) {
		
	List<LoginQuoteCriteriaResponse> resp = new ArrayList<LoginQuoteCriteriaResponse>();
	List<EserviceBuildingDetails> list = new ArrayList<EserviceBuildingDetails>();
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try {
			String empty="";
			// Get Datas
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<EserviceBuildingDetails> query = cb.createQuery(EserviceBuildingDetails.class);

			// Find All
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
			Root<EserviceBuildingDetails> m = query.from(EserviceBuildingDetails.class);


			// Select
			query.select(m);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(m.get("entryDate")));

			// Where
			List<Predicate> predicate = new ArrayList<Predicate>();
			
			predicate.add(cb.equal(m.get("companyId"), req.getInsuranceId()));
			predicate.add(cb.equal(m.get("status"), "Y"));
			predicate.add(cb.lessThanOrEqualTo(m.get("updatedDate"), endDate));
			predicate.add(cb.greaterThanOrEqualTo(m.get("updatedDate"), startDate));
			predicate.add(cb.isNull(m.get("endorsementType")));
			if(StringUtils.isNotBlank(req.getLoginId())  )  {
				predicate.add(cb.equal(m.get("loginId"), req.getLoginId()));
			}
			// Product  & Branch Condition
			if(StringUtils.isNotBlank(req.getProductId())  ) {
				predicate.add(cb.equal(m.get("productId"), req.getProductId()));
			}
			if(StringUtils.isNotBlank(req.getBranchCode()) &&  (!"99999".equalsIgnoreCase(req.getBranchCode())) ) {  
				predicate.add(cb.equal(m.get("branchCode"), req.getBranchCode()));
			}
			predicate.add(cb.equal(  m.get("sectionId"),  "0"));
			query.where(predicate.toArray(new Predicate[0])).orderBy(orderList);


			// Get Result
			TypedQuery<EserviceBuildingDetails> result = em.createQuery(query);
			list = result.getResultList();
			
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getEntryDate()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(EserviceBuildingDetails :: getEntryDate ).reversed());
			if(list!=null &&list.size()>0) {
//			List<HomePositionMaster> policyList= getProtfolioActive(req,endDate,"P") ;
			Date entryDate=list.get(0).getEntryDate();	
			String productName=list.get(0).getProductDesc();
			String productId=list.get(0).getProductId();
			LoginQuoteCriteriaResponse resp1 = new LoginQuoteCriteriaResponse();
//			resp1.setLastPolicyDate(sdf.format(policyList.get(0).getInceptionDate()));
			resp1.setLastQuoteDate(sdf.format(entryDate));
			resp1.setProductId(productId);
			resp1.setProductName(productName);
			resp1.setQuoteCount(totalcountbuildingexisting(req, startDate,endDate, "Y"));
//			resp1.setPolicyCount(totalcountportfolioactive(req, startDate, "P"));
			resp1.setEndtsCount(null);
//			resp1.setTotalCommission(null);
//			resp1.setPremium(null);	
			resp.add(resp1);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resp;
	}

	private Long totalcountbuildingexisting(ExistingQuoteReq req, Date startDate, Date endDate, String status) {
		Long count = 0l;
		try {
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> query = cb.createQuery(Long.class);

			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
			Root<EserviceBuildingDetails> m = query.from(EserviceBuildingDetails.class);

			query.multiselect(cb.count(m));
					

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(m.get("updatedDate")));

			// Where
			List<Predicate> predicate = new ArrayList<Predicate>();
			predicate.add(cb.equal(c.get("customerReferenceNo"), m.get("customerReferenceNo")));
			predicate.add(cb.equal(m.get("companyId"), req.getInsuranceId()));
			predicate.add(cb.equal(m.get("status"), status));
			predicate.add(cb.lessThanOrEqualTo(m.get("updatedDate"), endDate));
			predicate.add(cb.greaterThanOrEqualTo(m.get("updatedDate"), startDate));
			predicate.add(cb.isNull(m.get("endorsementType")));

			if(StringUtils.isNotBlank(req.getLoginId())  )  {
				predicate.add(cb.equal(m.get("loginId"), req.getLoginId()));
			}
			// Product  & Branch Condition
			if(StringUtils.isNotBlank(req.getProductId())  ) {
				predicate.add(cb.equal(m.get("productId"), req.getProductId()));
			}
			if(StringUtils.isNotBlank(req.getBranchCode()) &&  (!"99999".equalsIgnoreCase(req.getBranchCode())) ) {  
				predicate.add(cb.equal(m.get("branchCode"), req.getBranchCode()));
			}
			predicate.add(cb.equal(  m.get("sectionId"),  "0"));
			query.where(predicate.toArray(new Predicate[0]));
			
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
	public synchronized  List<LoginQuoteCriteriaResponse> getCommonExistingQuote(ExistingQuoteReq req,Date startDate,Date endDate) {
		
		List<LoginQuoteCriteriaResponse> resp = new ArrayList<LoginQuoteCriteriaResponse>();
		List<EserviceCommonDetails> list = new ArrayList<EserviceCommonDetails>();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		try {

			// Get Datas
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<EserviceCommonDetails> query = cb.createQuery(EserviceCommonDetails.class);

			// Find All
			Root<EserviceCustomerDetails> c = query.from(EserviceCustomerDetails.class);
			Root<EserviceCommonDetails> m = query.from(EserviceCommonDetails.class);

//			//overallPremiumLc
//			Subquery<Long> overallPremiumLc = query.subquery(Long.class);
//			Root<EserviceCommonDetails> ocpm1 = overallPremiumLc.from(EserviceCommonDetails.class);
//			overallPremiumLc.select(cb.sum(ocpm1.get("overallPremiumLc")));
//			Predicate a1 = cb.equal(ocpm1.get("requestReferenceNo"), m.get("requestReferenceNo"));
//			overallPremiumLc.where(a1);
//			
//			//overallPremiumFc
//			Subquery<Long> overallPremiumFc = query.subquery(Long.class);
//			Root<EserviceCommonDetails> oc = overallPremiumFc.from(EserviceCommonDetails.class);
//			overallPremiumFc.select(cb.sum(oc.get("overallPremiumFc")));
//			Predicate a2 = cb.equal(oc.get("requestReferenceNo"), m.get("requestReferenceNo"));
//			overallPremiumFc.where(a2);
		
			// Select
			query.select(m);
			

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(m.get("updatedDate")));

			// Where
			List<Predicate> predicate = new ArrayList<Predicate>();
			
			predicate.add(cb.equal(m.get("companyId"), req.getInsuranceId()));
			predicate.add(cb.equal(m.get("status"), "Y"));
			predicate.add(cb.lessThanOrEqualTo(m.get("updatedDate"), endDate));
			predicate.add(cb.greaterThanOrEqualTo(m.get("updatedDate"), startDate));
			predicate.add(cb.isNull(m.get("endorsementType")));
			if(StringUtils.isNotBlank(req.getLoginId())  )  {
				predicate.add(cb.equal(m.get("loginId"), req.getLoginId()));
			}
			// Product  & Branch Condition
			if(StringUtils.isNotBlank(req.getProductId())  ) {
				predicate.add(cb.equal(m.get("productId"), req.getProductId()));
			}
			if(StringUtils.isNotBlank(req.getBranchCode()) &&  (!"99999".equalsIgnoreCase(req.getBranchCode())) ) {  
				predicate.add(cb.equal(m.get("branchCode"), req.getBranchCode()));
			}
			
//			Risk Max Filter
			Subquery<Long> riskId = query.subquery(Long.class);
			Root<EserviceCommonDetails> ocp = riskId.from(EserviceCommonDetails.class);
			riskId.select(cb.max(ocp.get("riskId")));
			Predicate a3 = cb.equal(ocp.get("requestReferenceNo"), m.get("requestReferenceNo"));
			riskId.where(a3);

			predicate.add(cb.equal(m.get("riskId"), riskId));
			query.where(predicate.toArray(new Predicate[0])).orderBy(orderList);			
			 
			
			// Get Result
			TypedQuery<EserviceCommonDetails> result = em.createQuery(query);
			list = result.getResultList();
			
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getEntryDate()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(EserviceCommonDetails :: getEntryDate ));
			if(list!=null &&list.size()>0) {
//			List<HomePositionMaster> policyList= getProtfolioActive(req,endDate,"P") ;
			Date entryDate=list.get(0).getEntryDate();	
			String productName=list.get(0).getProductDesc();
			String productId=list.get(0).getProductId();
			LoginQuoteCriteriaResponse resp1 = new LoginQuoteCriteriaResponse();
//			resp1.setLastPolicyDate(sdf.format(policyList.get(0).getInceptionDate()));
			resp1.setLastQuoteDate(sdf.format(entryDate));
			resp1.setProductId(productId);
			resp1.setProductName(productName);
			resp1.setQuoteCount(totalcommoncountexisting(req, startDate,endDate, "Y"));
//			resp1.setPolicyCount(totalcountportfolioactive(req, startDate, "P"));
			resp1.setEndtsCount(null);
//			resp1.setTotalCommission(null);
//			resp1.setPremium(null);	
			resp.add(resp1);
			}
		
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return resp;
	}

	private Long totalcommoncountexisting(ExistingQuoteReq req, Date startDate, Date endDate, String status) {
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
			query.multiselect( cb.count(m));
			
			// Where
			List<Predicate> predicate = new ArrayList<Predicate>();
			predicate.add(cb.equal(c.get("customerReferenceNo"), m.get("customerReferenceNo")));
			predicate.add(cb.equal(m.get("companyId"), req.getInsuranceId()));
			predicate.add(cb.equal(m.get("status"), status));
			predicate.add(cb.lessThanOrEqualTo(m.get("updatedDate"), endDate));
			predicate.add(cb.greaterThanOrEqualTo(m.get("updatedDate"), startDate));
			predicate.add(cb.isNull(m.get("endorsementType")));

			if(StringUtils.isNotBlank(req.getLoginId())  )  {
				predicate.add(cb.equal(m.get("loginId"), req.getLoginId()));
			}
			// Product  & Branch Condition
			if(StringUtils.isNotBlank(req.getProductId())  ) {
				predicate.add(cb.equal(m.get("productId"), req.getProductId()));
			}
			if(StringUtils.isNotBlank(req.getBranchCode()) &&  (!"99999".equalsIgnoreCase(req.getBranchCode())) ) {  
				predicate.add(cb.equal(m.get("branchCode"), req.getBranchCode()));
			}
			// Risk Max Filter
			Subquery<Long> riskId = query.subquery(Long.class);
			Root<EserviceCommonDetails> ocp = riskId.from(EserviceCommonDetails.class);
			riskId.select(cb.max(ocp.get("riskId")));
			Predicate a3 = cb.equal(ocp.get("requestReferenceNo"), m.get("requestReferenceNo"));
			riskId.where(a3);

			predicate.add(cb.equal(m.get("riskId"), riskId));

			query.where(predicate.toArray(new Predicate[0]));

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
	}
