package com.maan.eway.common.controller;


import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.admin.res.PortfolioGridCriteriaRes;
import com.maan.eway.bean.EndtTypeMaster;
import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.EserviceTravelDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.InsuranceCompanyMaster;
import com.maan.eway.bean.LoginMaster;
import com.maan.eway.bean.LoginUserInfo;
import com.maan.eway.calculator.util.RatingFactorsUtil;
import com.maan.eway.common.req.DashBoardGetReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.DasboardCountRes;
import com.maan.eway.common.res.DashBoardChart;
import com.maan.eway.common.res.DashBoardGetRes;
import com.maan.eway.notification.bean.NotifTransactionDetails;
import com.maan.eway.notification.repository.NotifTransactionDetailsRepository;
import com.maan.eway.repository.EndtTypeMasterRepository;
import com.maan.eway.repository.LoginUserInfoRepository;
import com.maan.eway.req.calcengine.CalcEngine;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import java.text.SimpleDateFormat;

@Service
public class DashBoardServiceV1 {

	@PersistenceContext
	private EntityManager entityManager;
	
	@Autowired
	private RatingFactorsUtil ratingutil;
	
	@Autowired
	private NotifTransactionDetailsRepository notifyrepo;
	
	@Autowired
	private EndtTypeMasterRepository endtTypeMasterRepo;
	
	@Autowired
	private LoginUserInfoRepository loginUserInfoRepo;
	
	
	//SELECT TYPE,SUM(COUNT) AS COUNT,SUM(PREMIUM) AS PREMIUM,CURRENCY_CODE FROM( SELECT (CASE WHEN (HPM.STATUS ='Y' AND RENEWAL_DATE_YN IS NULL) THEN 'QUOTE' WHEN (HPM.STATUS ='Y' AND RENEWAL_DATE_YN='Y') THEN 'RENEWAL QUOTE' END) AS TYPE, COUNT(*) AS COUNT, ROUND(SUM(OVERALL_PREMIUM_LC),0) AS PREMIUM, MAX(CPM.CURRENCY_ID) AS CURRENCY_CODE FROM ESERVICE_BUILDING_DETAILS HPM,EWAY_INSURANCE_COMPANY_MASTER CPM WHERE HPM.COMPANY_ID=? AND HPM.PRODUCT_ID=? AND HPM.STATUS IN ('Y') AND (CASE WHEN 'Issuer'='Issuer' THEN HPM.APPLICATION_ID ELSE HPM.LOGIN_ID END) IN () AND HPM.entry_date >= ? AND HPM.ENTRY_DATE <=? AND CPM.COMPANY_ID=HPM.COMPANY_ID AND CPM.AMEND_ID=(SELECT MAX(AMEND_ID) FROM EWAY_INSURANCE_COMPANY_MASTER WHERE CPM.COMPANY_ID=COMPANY_ID) GROUP BY HPM.STATUS,HPM.RENEWAL_DATE_YN UNION ALL SELECT (CASE WHEN (HPM.STATUS ='Y' AND RENEWAL_DATE_YN IS NULL) THEN 'QUOTE' WHEN (HPM.STATUS ='Y' AND RENEWAL_DATE_YN='Y') THEN 'RENEWAL QUOTE' END) AS TYPE, COUNT(*) AS COUNT, ROUND(SUM(OVERALL_PREMIUM_LC),0) AS PREMIUM, MAX(CPM.CURRENCY_ID) AS CURRENCY_CODE FROM ESERVICE_COMMON_DETAILS HPM,EWAY_INSURANCE_COMPANY_MASTER CPM WHERE HPM.COMPANY_ID=? AND HPM.PRODUCT_ID=? AND HPM.STATUS IN ('Y') AND CPM.COMPANY_ID=HPM.COMPANY_ID and (CASE WHEN 'Issuer'='Issuer' THEN HPM.APPLICATION_ID ELSE HPM.LOGIN_ID END) IN () AND HPM.entry_date >= ? AND HPM.ENTRY_DATE <=? AND CPM.AMEND_ID=(SELECT MAX(AMEND_ID) FROM EWAY_INSURANCE_COMPANY_MASTER WHERE CPM.COMPANY_ID=COMPANY_ID) GROUP BY HPM.STATUS,HPM.RENEWAL_DATE_YN)X GROUP BY TYPE,CURRENCY_CODE
	
	public CommonRes getallCount(DashBoardGetReq req) {
		if("19".equals(req.getProductId())) {
			return getDashBoardCorporatePlus(req);
		}else {
			CalcEngine engine=new CalcEngine();
			engine.setInsuranceId(req.getInsuranceId());
			engine.setProductId(req.getProductId());
			List<Tuple> product = ratingutil.collectProductType(engine);
			if ("0".equals(req.getProductId())) {
				List<DasboardCountRes> allResults = new ArrayList<>();
				Set<String> executedMotorYn = new HashSet<>();
				for (Tuple tuple : product) {
					String oneProduct = tuple.get("motorYn") == null ? "M"
							: tuple.get("motorYn").toString().toUpperCase();
					if (executedMotorYn.contains(oneProduct)) {
						continue;
					}
					Object tupleProductId = tuple.get(1);
					if (tupleProductId != null) {
						req.setProductId(tupleProductId.toString());
					}
					CommonRes dashboardResponse = null;
					if ("M".equalsIgnoreCase(oneProduct)) {
						dashboardResponse = getDashBoard(req, EserviceMotorDetails.class);
					} else if ("H".equalsIgnoreCase(oneProduct)) {
						dashboardResponse = getDashBoard(req, EserviceTravelDetails.class);
					} else if ("A".equalsIgnoreCase(oneProduct)) {
						dashboardResponse = getDashBoard(req, EserviceBuildingDetails.class);
					} else if ("L".equalsIgnoreCase(oneProduct)) {
						dashboardResponse = getDashBoard(req, EserviceCommonDetails.class);
					}
					if (dashboardResponse != null && dashboardResponse.getCommonResponse() instanceof List) {
						List<?> items = (List<?>) dashboardResponse.getCommonResponse();
						for (Object obj : items) {
							if (obj instanceof DasboardCountRes) {
								allResults.add((DasboardCountRes) obj);
							}
						}
					}
					executedMotorYn.add(oneProduct);
				}
				Map<String, DasboardCountRes> typeMap = new LinkedHashMap<>();
				for (DasboardCountRes item : allResults) {
					String type = item.getType();
					if (typeMap.containsKey(type)) {
						DasboardCountRes existing = typeMap.get(type);

						int existingCount = Integer.parseInt(existing.getCount());
						int newCount = Integer.parseInt(item.getCount());
						existing.setCount(String.valueOf(existingCount + newCount));
						BigDecimal mergedPremium = existing.getPremium().add(item.getPremium());
						existing.setPremium(mergedPremium);
						if (existing.getCurrencyCode() == null || "None".equalsIgnoreCase(existing.getCurrencyCode())) {
							existing.setCurrencyCode(item.getCurrencyCode());
						}

					} else {
						DasboardCountRes newItem = new DasboardCountRes();
						newItem.setType(item.getType());
						newItem.setCount(item.getCount());
						newItem.setPremium(item.getPremium());
						newItem.setCurrencyCode(item.getCurrencyCode());
						newItem.setCodeDescLocal(item.getCodeDescLocal());
						newItem.setPosition(item.getPosition());
						newItem.setStatus(item.getStatus());
						typeMap.put(type, newItem);
					}
				}
				List<DasboardCountRes> finalResults = new ArrayList<>(typeMap.values());
				CommonRes res = new CommonRes();
				res.setMessage("Success");
				res.setIsError(false);
				res.setErroCode(0);
				res.setCommonResponse(finalResults);
				return res;

			}else {
			String oneProduct=product.get(0).get("motorYn")==null?"M":product.get(0).get("motorYn").toString();
			if("M".equals(oneProduct)) {
				return getDashBoard(req,EserviceMotorDetails.class);	
			} else if (oneProduct.equals("H")) {
				return getDashBoard(req,EserviceTravelDetails.class);
			}else if (oneProduct.equalsIgnoreCase("A")) {
				return getDashBoard(req,EserviceBuildingDetails.class);
			}else if (oneProduct.equalsIgnoreCase("L")) {
				return getDashBoard(req,EserviceCommonDetails.class);
			}
			}
		}
		return null;
		
	}

	private CommonRes getDashBoardCorporatePlus(DashBoardGetReq req) {

		List<DasboardCountRes> total=new ArrayList<DasboardCountRes>();
		Date date1 = new Date();
		Calendar cal = new GregorianCalendar();
		cal.setTime(date1);
		cal.add(Calendar.DATE, -30);cal.set(Calendar.HOUR_OF_DAY, 23);cal.set(Calendar.MINUTE, 59);
		Date startDate = cal.getTime();
		
		
		List<String> loginIds=new ArrayList<String>();
		if("Broker".equals(req.getUserType())) {
			loginIds.addAll(findByCompanyAndLoginId(req.getInsuranceId(),req.getLoginId()));
		}else {
			loginIds.add(req.getLoginId());
		}
		try { 
			List<Map<String, Object>> results = ratingutil.executeCorporatePlusQuery(req,loginIds,startDate,new Date());
			for (Map<String,Object> objects : results) {
				DasboardCountRes res=new DasboardCountRes();
			  
				res.setType(objects.get("TYPE").toString());
				res.setCount(objects.get("COUNT").toString());
				res.setPremium(new BigDecimal(objects.get("PREMIUM")==null?"0":objects.get("PREMIUM").toString()));
				res.setCurrencyCode(objects.get("CURRENCY_CODE").toString());
				total.add(res);
			}
			
			List<String> list=new ArrayList<String>();
			list.add("QUOTE");
			list.add("RENEWAL QUOTE");
			list.add("POLICY");
			list.add("RENEWAL POLICY");
			for(String type:list) {
				long count = total.stream().filter(t-> type.equals(t.getType())).count();
				if(count==0) {
					DasboardCountRes res=new DasboardCountRes();				
					res.setType(type);
					res.setCount("0".toString());
					res.setPremium(new BigDecimal("0"));
					res.setCurrencyCode("None");
					total.add(res);
				}
			}
			
			CommonRes res=new CommonRes();
			res.setCommonResponse(total);
			return res;
		}catch (Exception e) {
			e.printStackTrace();
		}
		 
		return null;
		
	
	}

	private CommonRes getDashBoard(DashBoardGetReq req,Class tablename) {
		List<DasboardCountRes> total=new ArrayList<DasboardCountRes>();
		Date today = new Date();
		Calendar cal = new GregorianCalendar();
		cal.setTime(today);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 1);
		today = cal.getTime();
		cal.set(Calendar.HOUR_OF_DAY, 1);
		cal.set(Calendar.MINUTE, 1);
		cal.add(Calendar.DAY_OF_MONTH, -90);
		Date before30 = cal.getTime();
		
		List<String> loginIds=new ArrayList<String>();
		if("Broker".equals(req.getUserType())) {
			loginIds.addAll(findByCompanyAndLoginId(req.getInsuranceId(),req.getLoginId()));
		}else {
			loginIds.add(req.getLoginId());
		}
		try {
			
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			
			CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
			Root<?> hpm = cq.from(tablename);
			Root<InsuranceCompanyMaster> cpm = cq.from(InsuranceCompanyMaster.class);

			// Create expressions for case statement
			Expression<Object> quoteCase = cb.selectCase().when(cb.and(cb.equal(hpm.get("status"), "Y"), cb.isNull(hpm.get("renewalDateYn"))), "QUOTE")
					.when(cb.and(cb.equal(hpm.get("status"), "Y"), cb.equal(hpm.get("renewalDateYn"), "Y")), "RENEWAL QUOTE")
					.otherwise("");
			Expression<Object> caseExpression;

			if ("100046".equals(req.getInsuranceId())
					|| "100047".equals(req.getInsuranceId())
					|| "100048".equals(req.getInsuranceId())
					|| "100049".equals(req.getInsuranceId())
					|| "100050".equals(req.getInsuranceId())) {
				if ("Issuer".equalsIgnoreCase(req.getUserType())) {

					caseExpression = null;

				} else {

					caseExpression = hpm.get("loginId");
				}

			} else {


				caseExpression = cb.selectCase()
						.when(
								cb.equal(cb.literal("Issuer"), req.getUserType()),
								hpm.get("applicationId")
						)
						.otherwise(hpm.get("loginId"));
			}

			// Create subquery for MAX(AMEND_ID)
			Subquery<Integer> subquery = cq.subquery(Integer.class);
			Root<InsuranceCompanyMaster> cpmSub = subquery.from(InsuranceCompanyMaster.class);
			subquery.select(cb.max(cpmSub.get("amendId")))
			.where(cb.equal(cpmSub.get("companyId"), hpm.get("companyId")));

			// Construct the main query
			cq.multiselect(quoteCase.alias("Desc"), cb.count(hpm).alias("Count"), cb.sum(hpm.get("overallPremiumLc")).alias("TotalPremium"), cb.max(cpm.get("currencyId")).alias("Currency"));
			
			List<Predicate> predicate = new ArrayList<Predicate>();
			predicate.add(cb.equal(hpm.get("companyId"), req.getInsuranceId()));
			predicate.add(cb.equal(hpm.get("productId"), req.getProductId()));
			predicate.add(cb.equal(hpm.get("status"), "Y"));
			predicate.add(cb.equal(cpm.get("companyId"), hpm.get("companyId")));
			predicate.add(cb.between(hpm.get("updatedDate"), before30,today ));
			if (caseExpression != null) {
				predicate.add(cb.in(caseExpression).value(loginIds));
			}
			predicate.add(cb.equal(cpm.get("amendId"), subquery));
			if("5".equals(req.getProductId() )) {
				// Risk Max Filter
				Subquery<Long> riskId = cq.subquery(Long.class);
				Root<EserviceMotorDetails> ocp = riskId.from(EserviceMotorDetails.class);
				riskId.select(cb.max(ocp.get("riskId")));
				Predicate a3 = cb.equal(ocp.get("requestReferenceNo"), hpm.get("requestReferenceNo"));
				Predicate a4 = cb.equal(ocp.get("customerReferenceNo"), hpm.get("customerReferenceNo"));
				riskId.where(a3,a4);
				
				predicate.add(cb.equal(hpm.get("riskId"),  riskId ));
			}
			cq.where( predicate.toArray(new Predicate[0]))
			.groupBy(hpm.get("status"), hpm.get("renewalDateYn"))
			.orderBy(cb.asc(cb.count(hpm)));
				
			//  AND (CASE WHEN 'Issuer'='Issuer' THEN HPM.APPLICATION_ID ELSE HPM.LOGIN_ID END) IN ()

			// Execute the query
			List<Object[]> results = entityManager.createQuery(cq).getResultList();
			for (int i = 0; i < results.size(); i++) {
				DasboardCountRes res=new DasboardCountRes();
				Object[] objects = results.get(i);
				res.setType(objects[0].toString());
				res.setCount(objects[1].toString());
				res.setPremium(new BigDecimal(objects[2]==null?"0":objects[2].toString()));
				res.setCurrencyCode(objects[3].toString());
				total.add(res);
			}
			
			 
			//return res;
		}catch (Exception e) {
			e.printStackTrace();
		}
		try {
			
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
			Root<HomePositionMaster> hpm = cq.from(HomePositionMaster.class);
			Root<InsuranceCompanyMaster> cpm = cq.from(InsuranceCompanyMaster.class);

			// Create expressions for case statement
			Expression<Object> quoteCase = cb.selectCase().when(cb.and(cb.equal(hpm.get("status"), "P"), cb.isNull(hpm.get("renewalDateYn"))), "POLICY")
					.when(cb.and(cb.equal(hpm.get("status"), "P"), cb.equal(hpm.get("renewalDateYn"), "Y")), "RENEWAL POLICY")
					.otherwise("");

//			Expression<Object> caseExpression = cb.selectCase()
//				    .when(cb.equal(cb.literal("Issuer"), req.getUserType()), hpm.get("applicationId"))
//				    .otherwise(hpm.get("loginId"));


			Expression<Object> caseExpression = null;

			if ("100046".equals(req.getInsuranceId())
					|| "100047".equals(req.getInsuranceId())
					|| "100048".equals(req.getInsuranceId())
					|| "100049".equals(req.getInsuranceId())
					|| "100050".equals(req.getInsuranceId())) {
				if (!"Issuer".equalsIgnoreCase(req.getUserType())) {

					caseExpression = hpm.get("loginId");
				}

			} else {
				caseExpression = cb.selectCase()
						.when(
								cb.equal(cb.literal("Issuer"), req.getUserType()),
								hpm.get("applicationId")
						)
						.otherwise(hpm.get("loginId"));
			}

			// Create subquery for MAX(AMEND_ID)
			Subquery<Integer> subquery = cq.subquery(Integer.class);
			Root<InsuranceCompanyMaster> cpmSub = subquery.from(InsuranceCompanyMaster.class);
			subquery.select(cb.max(cpmSub.get("amendId")))
			.where(cb.equal(cpmSub.get("companyId"), cpm.get("companyId")));

			// Construct the main query
			
			Date todayDate = new Date();
			cal.setTime(todayDate);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			todayDate = cal.getTime();
			
			CriteriaQuery<PortfolioGridCriteriaRes> query = cb.createQuery(PortfolioGridCriteriaRes.class);
			
			// Endt Count Max Filter
			Subquery<Long> endtCount = query.subquery(Long.class);
			Root<HomePositionMaster> ocpm1 = endtCount.from(HomePositionMaster.class);
			endtCount.select(cb.max(ocpm1.get("endtCount")));
			Predicate a1 = cb.equal(ocpm1.get("originalPolicyNo"), hpm.get("originalPolicyNo"));
			//Predicate a2 = cb.equal(ocpm1.get("status"),m.get("status"));
			endtCount.where(a1);
			
			Predicate endtTypeIdNotEqual = cb.notEqual(hpm.get("endtTypeId"), "842"); // policy cancellation
			Predicate endtTypeIdIsNull = cb.isNull(hpm.get("endtTypeId"));     
			Predicate endtTypeIdCondition = cb.or(endtTypeIdNotEqual, endtTypeIdIsNull);
			
			cq.multiselect(quoteCase.alias("Desc"), cb.count(hpm).alias("Count"), cb.sum(hpm.get("overallPremiumLc")).alias("TotalPremium"), cb.max(cpm.get("currencyId")).alias("Currency"))
			.where(cb.equal(hpm.get("companyId"), req.getInsuranceId()),
					cb.equal(hpm.get("productId"),  req.getProductId()),
					cb.equal(hpm.get("status"), "P"),
					cb.equal(cpm.get("companyId"), hpm.get("companyId")),
					cb.greaterThanOrEqualTo(hpm.get("expiryDate"), todayDate),
			         cb.lessThanOrEqualTo(hpm.get("entryDate"), todayDate),
			         cb.equal(hpm.get("integrationStatus"), "S"),
			         
//			         cb.notEqual(hpm.get("endtTypeId"),"842"),
//			         cb.isNull(hpm.get("endtTypeId")),
			         endtTypeIdCondition,

					caseExpression != null
							? cb.in(caseExpression).value(loginIds)
							: cb.conjunction(),
			         cb.equal(hpm.get("endtCount"), endtCount),
					cb.equal(cpm.get("amendId"), subquery))
			.groupBy(hpm.get("status"), hpm.get("renewalDateYn"))
			.orderBy(cb.asc(cb.count(hpm)));

			// Execute the query
			List<Object[]> results = entityManager.createQuery(cq).getResultList();
			for (int i = 0; i < results.size(); i++) {
				DasboardCountRes res=new DasboardCountRes();
				Object[] objects = results.get(i);
				res.setType(objects[0].toString());
				res.setCount(objects[1].toString());
				res.setPremium(new BigDecimal(objects[2]==null?"0":objects[2].toString()));
				res.setCurrencyCode(objects[3].toString());
				total.add(res);
			}
			
			List<String> list=new ArrayList<String>();
			list.add("QUOTE");
			list.add("RENEWAL QUOTE");
			list.add("POLICY");
			list.add("RENEWAL POLICY");
			for(String type:list) {
				long count = total.stream().filter(t-> type.equals(t.getType())).count();
				if(count==0) {
					DasboardCountRes res=new DasboardCountRes();				
					res.setType(type);
					res.setCount("0".toString());
					res.setPremium(new BigDecimal("0"));
					res.setCurrencyCode("None");
					total.add(res);
				}
			}
			Map<String,String> map=new HashMap<String,String>();
			map.put("QUOTE","CITAÇÃO");
			map.put("RENEWAL QUOTE","CITAÇÃO DE RENOVAÇÃO");
			map.put("POLICY","POLÍTICA");
			map.put("RENEWAL POLICY","POLÍTICA DE RENOVAÇÃO");
		    for (DasboardCountRes res : total) {
		         String codeDescLocal = map.get(res.getType());
		         res.setCodeDescLocal(codeDescLocal);
		    }
			CommonRes res=new CommonRes();
			res.setCommonResponse(total);
			return res;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
		
	}

	private List<String> findByCompanyAndLoginId(String insuranceId, String loginId) {
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<String> cq = cb.createQuery(String.class);
			Root<LoginMaster> root = cq.from(LoginMaster.class);
			
            Subquery<Integer> subquery = cq.subquery(Integer.class);
            Root<LoginMaster> subRoot = subquery.from(LoginMaster.class);
            subquery.select(subRoot.get("oaCode"))
                    .where(cb.equal(subRoot.get("loginId"), loginId),
                    		cb.equal(subRoot.get("companyId"), insuranceId));

            cq.multiselect(root.get("loginId")).where( cb.and(
            		cb.equal(root.get("companyId"), insuranceId),
            		cb.equal(root.get("oaCode"), subquery)));

            List<String> results = entityManager.createQuery(cq).getResultList();
            
            List<String> result=new ArrayList<String>();
            
            for (String objects : results) {
            	 
				result.add((String) objects);				
			}
            return result;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	public CommonRes getChartModel(DashBoardGetReq req,Class tableName) {

		try {
			// Assuming you have an EntityManager instance named em
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			CriteriaQuery<Tuple> cq = cb.createTupleQuery();
			Root<EserviceMotorDetails> hpm = cq.from(tableName);
			//Join<EserviceMotorDetails, InsuranceCompanyMaster> cpm =hpm.join("companyId");
			Root<InsuranceCompanyMaster> cpm=cq.from(InsuranceCompanyMaster.class);
					 
			// Define the expressions for the select clause
			Expression<Date> date = cb.<Date>selectCase().when(cb.equal(hpm.get("status"), "Y"), hpm.get("entryDate")).otherwise(hpm.get("updatedDate"));
			Expression<Long> quoteCount = cb.sum(cb.<Long>selectCase()
					.when(cb.equal(hpm.get("status"), "Y"), cb.literal(1L))
					.otherwise(cb.nullLiteral(Long.class)));
			Expression<Long> policyCount = cb.sum(cb.<Long>selectCase()
					.when(cb.equal(hpm.get("status"), "P"), cb.literal(1L))
					.otherwise(cb.nullLiteral(Long.class)));
			Expression<BigDecimal> quotePremium = cb.sum(cb.<BigDecimal>selectCase()
					.when(cb.equal(hpm.get("status"), "Y"), hpm.get("overallPremiumLc"))
					.otherwise(cb.nullLiteral(BigDecimal.class)));
			Expression<BigDecimal> policyPremium = cb.sum(cb.<BigDecimal>selectCase()
					.when(cb.equal(hpm.get("status"), "P"), hpm.get("overallPremiumLc"))
					.otherwise(cb.nullLiteral(BigDecimal.class)));
			Expression<Number> currencyCode = cb.max(cpm.get("currencyId"));
			// Add the expressions to the select clause with aliases
			cq.multiselect(
					date.alias("date"),
					quoteCount.alias("quoteCount"),
					policyCount.alias("policyCount"),
					quotePremium.alias("quotePremium"),
					policyPremium.alias("policyPremium"),
					currencyCode.alias("currencyCode")
					);
			// Define the predicates for the where clause
			Predicate companyIdPredicate = cb.equal(hpm.get("companyId"), req.getInsuranceId());
			Predicate productIdPredicate = cb.equal(hpm.get("productId"), req.getProductId());
			Predicate statusPredicate = hpm.get("status").in("P", "Y");
			//cb.equal(statusPredicate, companyIdPredicate);
			Predicate companyIdJoin = cb.equal(cpm.get("companyId"), hpm.get("companyId"));
			Predicate branchCodePredicate = cb.equal(hpm.get("branchCode"), req.getBranchCode());
			Expression<Date> dateAsLocalDate = cb.function("DATE", Date.class, date);
			 
			/*Date date1 = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(date1);
			cal.add(Calendar.DATE, -90);cal.set(Calendar.HOUR_OF_DAY, 23);cal.set(Calendar.MINUTE, 59);
			Date startDate = cal.getTime();
			
			Predicate datePredicate = cb.between(dateAsLocalDate, cb.literal(startDate), cb.literal(date1));
			*/
			
			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
			sdf.setLenient(false);

			Date startDate = req.getStartDate();
			Date endDate = req.getEndDate();

			Calendar startCal = Calendar.getInstance();
			startCal.setTime(startDate);
			startCal.set(Calendar.HOUR_OF_DAY, 0);
			startCal.set(Calendar.MINUTE, 0);
			startCal.set(Calendar.SECOND, 0);
			startCal.set(Calendar.MILLISECOND, 0);
			startDate = startCal.getTime();

			Calendar endCal = Calendar.getInstance();
			endCal.setTime(endDate);
			endCal.set(Calendar.HOUR_OF_DAY, 23);
			endCal.set(Calendar.MINUTE, 59);
			endCal.set(Calendar.SECOND, 59);
			endCal.set(Calendar.MILLISECOND, 999);
			endDate = endCal.getTime();

			Predicate datePredicate = cb.between(
			    dateAsLocalDate,
			    cb.literal(startDate),
			    cb.literal(endDate)
			);
			
			Subquery<Long> sub = cq.subquery(Long.class); // <-- call subquery on cq, not cb
			sub.select(cb.max(cpm.get("amendId")));
			sub.from(InsuranceCompanyMaster.class);
			sub.where(cb.equal(cpm.get("companyId"), hpm.get("companyId")));
			
			Predicate amendIdPredicate = cb.equal(cpm.get("amendId"), sub);
			
//			Predicate loginIdPredicate = cb.equal(cb.<String>selectCase()
//					.when(cb.equal(cb.literal("Issuer"), req.getUserType()), hpm.get("applicationId"))
//					.otherwise(hpm.get("loginId")), req.getLoginId());
			// Combine the predicates with logical and

			Predicate loginIdPredicate = null;

			if ("100046".equals(req.getInsuranceId())
					|| "100047".equals(req.getInsuranceId())
					|| "100048".equals(req.getInsuranceId())
					|| "100049".equals(req.getInsuranceId())
					|| "100050".equals(req.getInsuranceId())) {
				if (!"Issuer".equalsIgnoreCase(req.getUserType())) {
					loginIdPredicate = cb.equal(hpm.get("loginId"), req.getLoginId());
				}

			} else {

				loginIdPredicate = cb.equal(cb.<String>selectCase().when(cb.equal(cb.literal("Issuer"), req.getUserType()), hpm.get("applicationId"))
								.otherwise(hpm.get("loginId")), req.getLoginId());
			}
			cq.where(companyIdPredicate, productIdPredicate,branchCodePredicate, statusPredicate, datePredicate, amendIdPredicate,loginIdPredicate != null ? loginIdPredicate : cb.conjunction(),companyIdJoin);
			// Define the group by clause
			cq.groupBy(date);
			// Create a typed query and get the result list
			TypedQuery<Tuple> query = entityManager.createQuery(cq);
			List<Tuple> results = query.getResultList();

			
			System.out.println( "out put "+results);
			List<DashBoardChart> total=new ArrayList<DashBoardChart>();
			for (int i = 0; i < results.size(); i++) {
				Tuple objects = results.get(i);
				DashBoardChart res=DashBoardChart.builder()
						.date(objects.get(0).toString())
						.quoteCount(new BigDecimal(objects.get(1)==null?"0":objects.get(1).toString()))
						.policyCount(new BigDecimal(objects.get(2)==null?"0":objects.get(2).toString()))
						.quotePremium(new BigDecimal(objects.get(3)==null?"0":objects.get(3).toString()))
						.policyPremium(new BigDecimal(objects.get(4)==null?"0":objects.get(4).toString()))
						.currency(objects.get(5)==null?"0":objects.get(5).toString())
						.build();
				
				 
				total.add(res);
			}
			CommonRes res=new CommonRes();
			res.setCommonResponse(total);
			return res;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	
	}
	public CommonRes getChartModel(DashBoardGetReq req) {
		try {

			if ("19".equals(req.getProductId())) {
				return getChartModelCorporatePlus(req);
			} else {
				CalcEngine engine = new CalcEngine();
				engine.setInsuranceId(req.getInsuranceId());
				engine.setProductId(req.getProductId());
				List<Tuple> product = ratingutil.collectProductType(engine);
				if (req.getProductId().equals("0")) {

					List<Object> allResults = new ArrayList<>();
					Set<String> executedMotorYn = new HashSet<>();
					for (Tuple tuple : product) {
						String oneProduct = tuple.get("motorYn") == null ? "M" : tuple.get("motorYn").toString();
						if (executedMotorYn.contains(oneProduct)) {
							continue;
						}
						Object tupleProductId = tuple.get(1);
						if (tupleProductId != null) {
							req.setProductId(tupleProductId.toString());
						}
						executedMotorYn.add(oneProduct);
						CommonRes dashboardResponse = null;
						if ("M".equals(oneProduct)) {
							dashboardResponse = getChartModel(req, EserviceMotorDetails.class);
						} else if (oneProduct.equals("H")) {
							dashboardResponse = getChartModel(req, EserviceTravelDetails.class);
						} else if (oneProduct.equalsIgnoreCase("A")) {
							dashboardResponse = getChartModel(req, EserviceBuildingDetails.class);
						} else if (oneProduct.equalsIgnoreCase("L")) {
							dashboardResponse = getChartModel(req, EserviceCommonDetails.class);
						}
						if (dashboardResponse != null && dashboardResponse.getCommonResponse() instanceof List) {
							List<?> items = (List<?>) dashboardResponse.getCommonResponse();
							allResults.addAll(items);
						}
					}
					List<Object> filteredResults = allResults.stream().filter(Objects::nonNull)
							.collect(Collectors.toList());
					CommonRes res = new CommonRes();
					res.setMessage("Success");
					res.setIsError(false);
					res.setErroCode(0);
					res.setCommonResponse(filteredResults);
					return res;
				} else {
					String oneProduct = product.get(0).get("motorYn") == null ? "M"
							: product.get(0).get("motorYn").toString();
					if ("M".equals(oneProduct)) {
						return getChartModel(req, EserviceMotorDetails.class);
					} else if (oneProduct.equals("H")) {
						return getChartModel(req, EserviceTravelDetails.class);
					} else if (oneProduct.equalsIgnoreCase("A")) {
						return getChartModel(req, EserviceBuildingDetails.class);
					} else if (oneProduct.equalsIgnoreCase("L")) {
						return getChartModel(req, EserviceCommonDetails.class);
					}
				}
			}
			return null;

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private CommonRes getChartModelCorporatePlus(DashBoardGetReq req) {

		List<DashBoardChart> total=new ArrayList<DashBoardChart>();
		Date date1 = new Date();
		Calendar cal = new GregorianCalendar();
		cal.setTime(date1);
		cal.add(Calendar.DATE, -30);cal.set(Calendar.HOUR_OF_DAY, 23);cal.set(Calendar.MINUTE, 59);
		Date startDate = cal.getTime();
		
		
		List<String> loginIds=new ArrayList<String>();
		if("Broker".equals(req.getUserType())) {
			loginIds.addAll(findByCompanyAndLoginId(req.getInsuranceId(),req.getLoginId()));
		}else {
			loginIds.add(req.getLoginId());
		}
		try { 
			List<Map<String, Object>> results = ratingutil.executeChartCorporatePlusQuery(req,loginIds,startDate,new Date());
			for (Map<String,Object> objects : results) {							
				DashBoardChart res=DashBoardChart.builder()
						.date(objects.get("DATE").toString())
						.quoteCount(new BigDecimal(objects.get("QUOTE_COUNT")==null?"0":objects.get("QUOTE_COUNT").toString()))
						.policyCount(new BigDecimal(objects.get("POLICY_COUNT")==null?"0":objects.get("POLICY_COUNT").toString()))
						.quotePremium(new BigDecimal(objects.get("QUOTE_PREMIUM")==null?"0":objects.get("QUOTE_PREMIUM").toString()))
						.policyPremium(new BigDecimal(objects.get("POLICY_PREMIUM")==null?"0":objects.get("POLICY_PREMIUM").toString()))
						.currency(objects.get("CURRENCY_CODE")==null?"0":objects.get("CURRENCY_CODE").toString())
						.build();				
				 
				total.add(res);
			}
			CommonRes res=new CommonRes();
			res.setCommonResponse(total);
			return res;
			
		}catch (Exception e) {
			e.printStackTrace();
		}
		 
		return null;
		
	
	}

	public CommonRes getChartModelV2(DashBoardGetReq req) {
		Class<EserviceMotorDetails> tableName=EserviceMotorDetails.class;

		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
			 //CriteriaQuery<Object[]> cq = cb.createQuery(Object[].class);
			 CriteriaQuery<Tuple> cq = cb.createTupleQuery();
			 
			    Root<EserviceMotorDetails> motorDetailsRoot = cq.from(EserviceMotorDetails.class);
			    Root<InsuranceCompanyMaster> cpm=cq.from(InsuranceCompanyMaster.class);
			    //Join<EserviceMotorDetails, InsuranceCompanyMaster> companyJoin = motorDetailsRoot.join("companyId");
			    Predicate companyIdJoin = cb.equal(motorDetailsRoot.get("companyId"), cpm.get("companyId"));
			    Expression<Date> dateAsLocalDate = cb.function("DATE", Date.class, motorDetailsRoot.get("entryDate"));
/*
 * 
 * Expression<BigDecimal> policyPremium = cb.sum(cb.<BigDecimal>selectCase()
					.when(cb.equal(hpm.get("status"), "P"), hpm.get("overallPremiumLc"))
					.otherwise(cb.nullLiteral(BigDecimal.class)));
 */
			    cq.multiselect(
			    		dateAsLocalDate.alias("date"),
			            cb.sum(cb.<Integer>selectCase()
			                    .when(cb.and(cb.equal(motorDetailsRoot.get("status"), "Y"), cb.isNull(motorDetailsRoot.get("renewalDateYn"))), 1)
			                    .otherwise(0)).alias("quoteCount"),
			            cb.sum(cb.<Integer>selectCase()
			                    .when(cb.and(cb.equal(motorDetailsRoot.get("status"), "Y"), cb.equal(motorDetailsRoot.get("renewalDateYn"), "Y")), 1)
			                    .otherwise(0)).alias("renewalQuoteCount"),
			            cb.<BigDecimal>selectCase()
			                    .when(cb.and(cb.equal(motorDetailsRoot.get("status"), "Y"), cb.isNull(motorDetailsRoot.get("renewalDateYn"))),
			                            cb.sum(motorDetailsRoot.get("overallPremiumLc")))
			                    .otherwise(cb.nullLiteral(BigDecimal.class)).alias("quotePremium"),
			            cb.<BigDecimal>selectCase()
			                    .when(cb.and(cb.equal(motorDetailsRoot.get("status"), "Y"), cb.equal(motorDetailsRoot.get("renewalDateYn"), "Y")),
			                            cb.sum(motorDetailsRoot.get("overallPremiumLc")))
			                    .otherwise(cb.nullLiteral(BigDecimal.class)).alias("renewalQuotePremium"),
			            cb.max(cpm.get("currencyId")).alias("currencyCode")
			    );

			    Predicate companyIdPredicate = cb.equal(motorDetailsRoot.get("companyId"), req.getInsuranceId());
			    Predicate productIdPredicate = cb.equal(motorDetailsRoot.get("productId"), req.getProductId());
			    Predicate statusPredicate = motorDetailsRoot.get("status").in("Y");
			   // Predicate datePredicate = cb.between(motorDetailsRoot.get("entryDate"), java.sql.Date.valueOf("2024-01-01"), java.sql.Date.valueOf("2024-03-05"));
			   
			    Date date1 = new Date();
				Calendar cal = new GregorianCalendar();
				cal.setTime(date1);
				cal.add(Calendar.DATE, -30);cal.set(Calendar.HOUR_OF_DAY, 23);cal.set(Calendar.MINUTE, 59);
				Date startDate = cal.getTime();
				
				Predicate datePredicate = cb.between(dateAsLocalDate, cb.literal(req.getStartDate()), cb.literal(req.getEndDate()));
				
			 /*   Subquery<Long> sub = cq.subquery(Long.class); // <-- call subquery on cq, not cb
				sub.select(cb.max(cpm.get("amendId")));
				sub.from(InsuranceCompanyMaster.class);
				sub.where(cb.equal(cpm.get("companyId"), hpm.get("companyId")));
				
				Predicate amendIdPredicate = cb.equal(cpm.get("amendId"), sub);
				*/
				Subquery<Long> sub = cq.subquery(Long.class);
				sub.select(cb.max(cpm.get("amendId")));
				sub.from(InsuranceCompanyMaster.class);
			    sub.where(cb.equal(cpm.get("companyId"), motorDetailsRoot.get("companyId")));
			    Predicate amendIdPredicate = cb.equal(cpm.get("amendId"),sub);
			   
			   
			    Predicate applicationIdPredicate = cb.equal(cb.<String>selectCase()
						.when(cb.equal(cb.literal("Issuer"), req.getUserType()), motorDetailsRoot.get("applicationId"))
						.otherwise(motorDetailsRoot.get("loginId")), req.getLoginId());

			    cq.where(companyIdPredicate, productIdPredicate, statusPredicate, datePredicate, amendIdPredicate, applicationIdPredicate,companyIdJoin);

			    cq.groupBy(motorDetailsRoot.get("status"), motorDetailsRoot.get("renewalDateYn"), dateAsLocalDate);
			    cq.orderBy(cb.desc(dateAsLocalDate));

			    List<Tuple> results = entityManager.createQuery(cq).getResultList();
			    
			    System.out.println( "out put "+results);
				List<DashBoardChart> total=new ArrayList<DashBoardChart>();
				for (int i = 0; i < results.size(); i++) {
					Tuple objects = results.get(i);
					DashBoardChart res=DashBoardChart.builder()
							.date(objects.get(0).toString())
							.quoteCount(new BigDecimal(objects.get(1)==null?"0":objects.get(1).toString()))
							.policyCount(new BigDecimal(objects.get(2)==null?"0":objects.get(2).toString()))
							.quotePremium(new BigDecimal(objects.get(3)==null?"0":objects.get(3).toString()))
							.policyPremium(new BigDecimal(objects.get(4)==null?"0":objects.get(4).toString()))
							.currency(objects.get(5)==null?"0":objects.get(5).toString())
							.build();
					
					 
					total.add(res);
				}
				CommonRes res=new CommonRes();
				res.setCommonResponse(total);
				return res;
		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;	
	}
	
	public CommonRes getChartModelV3(DashBoardGetReq req) {
		List<DashBoardGetRes> res = new ArrayList<>();
		CommonRes res1 = new CommonRes();
		try {
		String loginid=req.getLoginId();
	
		if(loginid!=null && req.getStartDate()!=null && req.getEndDate()!=null)
		{
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();
	        CriteriaQuery<NotifTransactionDetails> cq = cb.createQuery(NotifTransactionDetails.class);
	        Root<NotifTransactionDetails> root = cq.from(NotifTransactionDetails.class);
	       
            LoginUserInfo userDetails = loginUserInfoRepo.findByLoginId(loginid);
            
            
	        Predicate predicateUwLoginId = cb.equal(root.get("brokerMailId"), userDetails.getUserMail());
	        Predicate predicateEmail = cb.equal(root.get("brokerName"), userDetails.getUserName());
	        Predicate predicateProduct = cb.equal(root.get("productid"), Integer.valueOf(req.getProductId()));
	        Predicate predicatePushDate = cb.greaterThanOrEqualTo(root.get("notifcationPushDate"), req.getStartDate());
	        Predicate predicateEndDate = cb.lessThanOrEqualTo(root.get("notifcationEndDate"), req.getEndDate());
             Predicate finalPredicate = cb.and(predicateUwLoginId,predicateEmail, predicateProduct, predicatePushDate, predicateEndDate);
            cq.where(finalPredicate);
	        cq.orderBy(cb.desc(root.get("entryDate")));
	        
	        List<NotifTransactionDetails> data1=entityManager.createQuery(cq).getResultList();
	        for(NotifTransactionDetails data:data1)
	        {
	        	DashBoardGetRes d= new DashBoardGetRes();
	        	d.setEmail(data.getCustomerMailid());
	        	d.setStatusmessage(data.getStatusMessage());
	        	d.setMobileCode(String.valueOf(data.getBrokerPhoneCode()));
	        	d.setPhoneno(String.valueOf(data.getBrokerPhoneNo()));
	        	d.setStartDate(data.getNotifcationPushDate());
	        	d.setEndDate(data.getNotifcationEndDate());
	        	res.add(d);	
	        }
	        res1.setCommonResponse(res);
		}
		}catch(Exception Ex)
		{
		System.out.println("*********Exception in getcharmodelv3************");
		Ex.printStackTrace();
		}
		return res1;
	}
	

	public CommonRes getgetEndormentV1(DashBoardGetReq req) {
		try {
			CriteriaBuilder cb = entityManager.getCriteriaBuilder();			 
			 CriteriaQuery<Tuple> cq = cb.createTupleQuery();
			 Root<HomePositionMaster> hm = cq.from(HomePositionMaster.class);
			 
			 cq.multiselect(hm.get("endtTypeId").alias("ENDT_TYPEID"),hm.get("endtTypeDesc").alias("Endorsement Desc"), cb.count(hm).alias("Count")
					 );
			 Expression<Date> dateAsLocalDate = cb.function("DATE", Date.class, hm.get("entryDate"));
			 
			 Predicate datePredicate = cb.between(dateAsLocalDate, cb.literal(req.getStartDate()), cb.literal(req.getEndDate()));

			Predicate applicationIdPredicate = null;

			if ("100046".equals(req.getInsuranceId())
					|| "100047".equals(req.getInsuranceId())
					|| "100048".equals(req.getInsuranceId())
					|| "100049".equals(req.getInsuranceId())
					|| "100050".equals(req.getInsuranceId())) {

				if (!"Issuer".equalsIgnoreCase(req.getUserType())) {

					applicationIdPredicate =
							cb.equal(hm.get("loginId"), req.getLoginId());
				}

			} else {

				applicationIdPredicate = cb.equal(cb.<String>selectCase()
						.when(cb.equal(cb.literal("Issuer"), req.getUserType()), hm.get("applicationId"))
						.otherwise(hm.get("loginId")), req.getLoginId());
			}
			 Predicate companyIdPredicate = cb.equal(hm.get("companyId"), req.getInsuranceId());
			 Predicate productIdPredicate = cb.equal(hm.get("productId"), req.getProductId());
			 Predicate statusPredicate = hm.get("status").in("P","E");
			 Predicate endttypeIdPredicate = cb.or((hm.get("endtTypeId").isNotNull()), cb.equal(hm.get("endtTypeId"),""));
			 Predicate endttypeDescPredicate = cb.or((hm.get("endtTypeDesc").isNotNull()), cb.equal(hm.get("endtTypeDesc"),""));
			 
			 cq.where(datePredicate, applicationIdPredicate != null ? applicationIdPredicate: cb.conjunction(),companyIdPredicate,productIdPredicate,statusPredicate,endttypeIdPredicate,endttypeDescPredicate);
			 cq.groupBy(hm.get("endtTypeId"),hm.get("endtTypeDesc"));
			 cq.orderBy(cb.asc(cb.count(hm)));
			 List<Tuple> results = entityManager.createQuery(cq).getResultList();
			 List<Map<String,Object>> rspone=new ArrayList<Map<String,Object>>();
			 for (int i = 0; i < results.size(); i++) {
				 Tuple tuple = results.get(i);
				 List<EndtTypeMaster> endtTypeMaster = endtTypeMasterRepo.findByEndtTypeAndCompanyId(tuple.get(1).toString() , req.getInsuranceId());
				 Map<String,Object> a=new HashMap<String, Object>();
				 a.put("EndtTypeId", tuple.get(0));
				 a.put("EndorsementDesc", tuple.get(1));
				 a.put("CodeDescLocal",(endtTypeMaster!=null && endtTypeMaster.size()>0) ? endtTypeMaster.get(0).getEndtTypeLocal() : " ");
				 a.put("Count", tuple.get(2));
				 rspone.add(a);
			 }
			CommonRes res=new CommonRes();
			res.setCommonResponse(rspone);
			return res;
		}catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}

}
