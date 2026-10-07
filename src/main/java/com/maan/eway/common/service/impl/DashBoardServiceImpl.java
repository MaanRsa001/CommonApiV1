package com.maan.eway.common.service.impl;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
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

import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.LoginMaster;
import com.maan.eway.bean.PaymentDetail;
import com.maan.eway.bean.PersonalInfo;
import com.maan.eway.common.req.DashBoardGetReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.DasboardCountRes;
import com.maan.eway.common.res.DasboardListRes;
import com.maan.eway.common.res.DasboardPolicyListRes;
import com.maan.eway.common.res.DasboardRecentCusListRes;
import com.maan.eway.common.res.DasboardReferalPendingRes;
import com.maan.eway.common.service.DashBoardService;
import com.maan.eway.repository.EServiceMotorDetailsRepository;
import com.maan.eway.repository.EserviceBuildingDetailsRepository;
import com.maan.eway.repository.EserviceCommonDetailsRepository;
import com.maan.eway.repository.EserviceCustomerDetailsRepository;
import com.maan.eway.repository.EserviceTravelDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.LoginBranchMasterRepository;
import com.maan.eway.repository.LoginMasterRepository;

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
public class DashBoardServiceImpl implements DashBoardService {



	@Autowired
	private EServiceMotorDetailsRepository repo;

	@Autowired
	private EserviceCustomerDetailsRepository custRepo;

	@Autowired
	private EserviceCommonDetailsRepository commonRepo;

	@Autowired
	private LoginBranchMasterRepository loginBranchRepo;
	
	@Autowired
	private LoginMasterRepository loginRepo;
	
	@Autowired
	private EserviceTravelDetailsRepository travelRepo;

	@Autowired
	private EserviceBuildingDetailsRepository buildingRepo;

	@Autowired
	private HomePositionMasterRepository homeRepo;

	@PersistenceContext
	private EntityManager em;
	
	

	private Logger log = LogManager.getLogger(DashBoardServiceImpl.class);

	@Override
	public List<DasboardCountRes> getallCount(DashBoardGetReq req){
		List<DasboardCountRes> reslist = new ArrayList<DasboardCountRes>();
		List<Map<String, Object>> list1 = new ArrayList<Map<String, Object>>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		List<Tuple> list = new ArrayList<Tuple>();
		 
		try {
			
			Date startDate=req.getStartDate();
			Date endDate = req.getEndDate();
			/*if (StringUtils.isBlank(req.getStartDate()) && StringUtils.isBlank(req.getEndDate())) {
				Date date1 = new Date();
				Calendar cal = new GregorianCalendar();
				cal.setTime(date1);
				cal.add(Calendar.DATE, -30);cal.set(Calendar.HOUR_OF_DAY, 23);cal.set(Calendar.MINUTE, 59);
				startDate = cal.getTime();
				cal.setTime(date1);
				cal.add(Calendar.DAY_OF_MONTH, 0);cal.set(Calendar.HOUR_OF_DAY, 23);cal.set(Calendar.MINUTE, 59);
				endDate = cal.getTime();
				
			} else {
				Date startDate1 = sdf.parse(req.getStartDate());
				Date endDate1=sdf.parse(req.getEndDate());
				Calendar cal = new GregorianCalendar();
				cal.setTime(startDate1);
				//cal.add(Calendar.HOUR , -1);
				cal.add(Calendar.DAY_OF_MONTH, -1);cal.set(Calendar.HOUR_OF_DAY, 23);cal.set(Calendar.MINUTE, 59);
				startDate = cal.getTime() ;
				cal.setTime(endDate1);
			//	cal.add(Calendar.HOUR , +23);
				cal.add(Calendar.DAY_OF_MONTH, 0);cal.set(Calendar.HOUR_OF_DAY,23 );cal.set(Calendar.MINUTE, 59);
				endDate = cal.getTime() ;
			}*/
/*			  CriteriaBuilder criteriaBuilder = em.getCriteriaBuilder();
		        CriteriaQuery<DasboardCountRes> criteriaQuery = criteriaBuilder.createQuery(DasboardCountRes.class);
		        Root<HomePositionMaster> root = criteriaQuery.from(HomePositionMaster.class);

		        criteriaQuery.multiselect(root.get("status").alias("status"), criteriaBuilder.count(root).alias("count"));
		        criteriaQuery.groupBy(root.get("status"));

		        Subquery<String> subUserTypeQuery = criteriaQuery.subquery(String.class);
		        Root<LoginMaster> ewayLoginRoot = subUserTypeQuery.from(LoginMaster.class);
		        subUserTypeQuery.select(ewayLoginRoot.get("subUserType"));
		        subUserTypeQuery.where(criteriaBuilder.equal(ewayLoginRoot.get("loginId"), req.getLoginId()));

		        Subquery<String> agencyCodeQuery = criteriaQuery.subquery(String.class);
		        Root<LoginMaster> agencyLoginRoot = agencyCodeQuery.from(LoginMaster.class);
		        agencyCodeQuery.select(agencyLoginRoot.get("agencyCode"));
		        agencyCodeQuery.where(
		                criteriaBuilder.equal(agencyLoginRoot.get("loginId"), req.getLoginId()),
		                criteriaBuilder.in(agencyLoginRoot.get("oaCode")).value(subUserTypeQuery)
		        );

		        Predicate statusPredicate = root.get("status").in("Y", "P", "RA");
		        Predicate inceptionDatePredicate = criteriaBuilder.between(root.get("inceptionDate"), startDate, endDate);
		        Predicate companyIdPredicate = criteriaBuilder.equal(root.get("companyId"), req.getInsuranceId());
		        Predicate branchCodePredicate = criteriaBuilder.equal(root.get("branchCode"), req.getBranchCode());
		        Predicate productIdPredicate = criteriaBuilder.equal(root.get("productId"),req.getProductId());
		        Predicate userTypePredicate = criteriaBuilder.or(
		                criteriaBuilder.and(
		                        criteriaBuilder.equal(subUserTypeQuery, "both"),
		                        criteriaBuilder.equal(criteriaBuilder.literal("1"), "1")
		                ),
		                criteriaBuilder.and(
		                        criteriaBuilder.or(
		                                criteriaBuilder.equal(ewayLoginRoot.get("subUserType"), "low"),
		                                criteriaBuilder.equal(ewayLoginRoot.get("subUserType"), "high")
		                        ),
		                        criteriaBuilder.equal(root.get("applicationId"), req.getLoginId())
		                ),
		                criteriaBuilder.and(
		                        criteriaBuilder.equal(ewayLoginRoot.get("userType"), "Broker"),
		                        criteriaBuilder.equal(root.get("agencyCode"), ewayLoginRoot.get("agencyCode"))
		                ),
		                criteriaBuilder.equal(root.get("agencyCode"), agencyCodeQuery),
		                criteriaBuilder.in(root.get("agencyCode")).value(agencyCodeQuery)
		        );

		        criteriaQuery.where(
		                statusPredicate,
		                inceptionDatePredicate,
		                companyIdPredicate,
		                branchCodePredicate,
		                productIdPredicate,
		                userTypePredicate
		        );
		    	TypedQuery<DasboardCountRes> result = em.createQuery(criteriaQuery);
				reslist = result.getResultList();
	*/		
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

			// Find All
			Root<HomePositionMaster> m = query.from(HomePositionMaster.class);
			
			  Subquery<String> subUserTypeQuery = query.subquery(String.class);
		        Root<LoginMaster> ewayLoginRoot = subUserTypeQuery.from(LoginMaster.class);
		        subUserTypeQuery.select(ewayLoginRoot.get("subUserType"));
		        subUserTypeQuery.where(cb.equal(ewayLoginRoot.get("loginId"), req.getLoginId()));

		        Subquery<String> agencyCodeQuery = query.subquery(String.class);
		        Root<LoginMaster> agencyLoginRoot = agencyCodeQuery.from(LoginMaster.class);
		        agencyCodeQuery.select(agencyLoginRoot.get("agencyCode"));
		        agencyCodeQuery.where(
		                cb.equal(agencyLoginRoot.get("loginId"), req.getLoginId()),
		                cb.in(agencyLoginRoot.get("oaCode")).value(subUserTypeQuery)
		        );
		        Subquery<Long> endtCount = query.subquery(Long.class);
				Root<HomePositionMaster> ocpm1 = endtCount.from(HomePositionMaster.class);
				endtCount.select(cb.max(ocpm1.get("endtCount")));
				Predicate a1 = cb.equal(ocpm1.get("originalPolicyNo"), m.get("originalPolicyNo"));
				//Predicate a2 = cb.equal(ocpm1.get("status"), m.get("status"));
				endtCount.where(a1);
			// Select
			query.multiselect(cb.max(m.get("status")).alias("status"),cb.count(m).alias("count"));			
			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(m.get("status")));
		
			// Where
			Predicate n1 = m.get("status").in("Y", "P", "RA");
			Predicate n2 = cb.between(m.get("inceptionDate"), startDate, endDate);
			Predicate n3 = cb.equal(m.get("companyId"), req.getInsuranceId());
			Predicate n4 = cb.equal(m.get("branchCode"), req.getBranchCode());
			Predicate n5 = cb.equal(m.get("productId"), req.getProductId());
			Predicate n12 = cb.equal(m.get("endtCount"), endtCount);
			Predicate n6=null;
			Predicate n7=null;
			Predicate n8=null;
			Predicate n9=null;
			Predicate n10=null;
			Expression<String> e0 = subUserTypeQuery;
	
		
			if(StringUtils.isNotBlank(req.getLoginId()) && "issuer".equalsIgnoreCase(req.getUserType()) && "both".equalsIgnoreCase(req.getSubUserType())){
				 n7 = e0.in("both");
				 query.where(n1,n2,n3,n4,n5,n7,n12).orderBy(orderList).groupBy(m.get("status"));
			}else if(StringUtils.isNotBlank(req.getLoginId()) && "issuer".equalsIgnoreCase(req.getUserType()) && ("low".equalsIgnoreCase(req.getSubUserType())||"high".equalsIgnoreCase(req.getSubUserType()))){
				n8=cb.equal(subUserTypeQuery,"high");
				n9=cb.equal(subUserTypeQuery,"low");
				n6 = cb.or(n8,n9);
//				n7 = e0.in(n6);
				n10=cb.equal(m.get("applicationId"), req.getLoginId());
				query.where(n1,n2,n3,n4,n5,n6,n12,n10).orderBy(orderList).groupBy(m.get("status"));
			}else if(StringUtils.isNotBlank(req.getLoginId()) && "broker".equalsIgnoreCase(req.getUserType()) && "b2b".equalsIgnoreCase(req.getSubUserType())){
//				n6=cb.equal(m.get("userType"), "Broker");
				n7=cb.equal(m.get("agencyCode"), agencyCodeQuery);
				query.where(n1,n2,n3,n4,n5,n7,n12).orderBy(orderList).groupBy(m.get("status"));
			}else {
				n6=cb.equal(m.get("loginId"), req.getLoginId());
				n7=cb.equal(m.get("agencyCode"), agencyCodeQuery);
				query.where(n1,n2,n3,n4,n5,n6,n7,n12).orderBy(orderList).groupBy(m.get("status"));
			}
			
			// Get Result
			TypedQuery<Tuple> result = em.createQuery(query);
			list = result.getResultList();
		
			for (Tuple map : list) {
				DasboardCountRes res = new DasboardCountRes();
				res.setStatus(map.get("status") == null ? "" : String.valueOf(map.get("status")));
				res.setCount(map.get("count") == null ? "" : String.valueOf(map.get("count")));
				reslist.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return reslist;
	}

	
	

	private static <T> java.util.function.Predicate<T> distinctByKey(
			java.util.function.Function<? super T, ?> keyExtractor) {
		Map<Object, Boolean> seen = new ConcurrentHashMap<>();
		return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}




	@Override
	public DasboardListRes getallList(DashBoardGetReq req) {
		//List<DasboardListRes> reslist = new ArrayList<DasboardListRes>();
		DasboardListRes reslist = new DasboardListRes();
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		List<Tuple> list = new ArrayList<Tuple>();
		 
		try {

			Date startDate = req.getStartDate();
			Date endDate = req.getEndDate();
		/*	if (StringUtils.isBlank(req.getStartDate()) && StringUtils.isBlank(req.getEndDate())) {
				Date date1 = new Date();
				Calendar cal = new GregorianCalendar();
				cal.setTime(date1);
				cal.add(Calendar.DATE, -30);
				cal.set(Calendar.HOUR_OF_DAY, 23);
				cal.set(Calendar.MINUTE, 59);
				startDate = cal.getTime();
				cal.setTime(date1);
				cal.add(Calendar.DAY_OF_MONTH, 0);
				cal.set(Calendar.HOUR_OF_DAY, 23);
				cal.set(Calendar.MINUTE, 59);
				endDate = cal.getTime();

			} else {
				Date startDate1 = sdf.parse(req.getStartDate());
				Date endDate1 = sdf.parse(req.getEndDate());
				Calendar cal = new GregorianCalendar();
				cal.setTime(startDate1);
				// cal.add(Calendar.HOUR , -1);
				cal.add(Calendar.DAY_OF_MONTH, -1);
				cal.set(Calendar.HOUR_OF_DAY, 23);
				cal.set(Calendar.MINUTE, 59);
				startDate = cal.getTime();
				cal.setTime(endDate1);
				// cal.add(Calendar.HOUR , +23);
				cal.add(Calendar.DAY_OF_MONTH, 0);
				cal.set(Calendar.HOUR_OF_DAY, 23);
				cal.set(Calendar.MINUTE, 59);
				endDate = cal.getTime();
			}*/

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

			// Find All
			Root<HomePositionMaster> m = query.from(HomePositionMaster.class);

			Subquery<String> subUserTypeQuery = query.subquery(String.class);
			Root<LoginMaster> ewayLoginRoot = subUserTypeQuery.from(LoginMaster.class);
			subUserTypeQuery.select(ewayLoginRoot.get("subUserType"));
			subUserTypeQuery.where(cb.equal(ewayLoginRoot.get("loginId"), req.getLoginId()));

			Subquery<String> agencyCodeQuery = query.subquery(String.class);
			Root<LoginMaster> agencyLoginRoot = agencyCodeQuery.from(LoginMaster.class);
			agencyCodeQuery.select(agencyLoginRoot.get("agencyCode"));
			agencyCodeQuery.where(cb.equal(agencyLoginRoot.get("loginId"), req.getLoginId()),
					cb.in(agencyLoginRoot.get("oaCode")).value(subUserTypeQuery));

			Subquery<Long> endtCount = query.subquery(Long.class);
			Root<HomePositionMaster> ocpm1 = endtCount.from(HomePositionMaster.class);
			endtCount.select(cb.max(ocpm1.get("endtCount")));
			Predicate a1 = cb.equal(ocpm1.get("originalPolicyNo"), m.get("originalPolicyNo"));
			//Predicate a2 = cb.equal(ocpm1.get("status"), m.get("status"));
			endtCount.where(a1);
			// Select
			query.multiselect(m.get("originalPolicyNo").alias("originalPolicyNo"),
					m.get("requestReferenceNo").alias("requestReferenceNo"), m.get("quoteNo").alias("quoteNo"),
					m.get("customerName").alias("customerName"), m.get("inceptionDate").alias("inceptionDate"),
					m.get("expiryDate").alias("expiryDate"),
					m.get("status").alias("status")/* ,cb.count(m).alias("count") */);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(m.get("status")));

			// Where
			Predicate n1 = m.get("status").in("Y", "P", "RA","RP");
			Predicate n2 = cb.between(m.get("inceptionDate"), startDate, endDate);
			Predicate n12 = cb.equal(m.get("endtCount"), endtCount);
			Predicate n3 = cb.equal(m.get("companyId"), req.getInsuranceId());
			Predicate n4 = cb.equal(m.get("branchCode"), req.getBranchCode());
			Predicate n5 = cb.equal(m.get("productId"), req.getProductId());
			Predicate n6 = null;
			Predicate n7 = null;
			Predicate n8 = null;
			Predicate n9 = null;
			Expression<String> e0 = subUserTypeQuery;
		
			if(StringUtils.isNotBlank(req.getLoginId()) && "issuer".equalsIgnoreCase(req.getUserType()) && "both".equalsIgnoreCase(req.getSubUserType())){
				 n7 = e0.in("both");
				 query.where(n1,n2,n3,n4,n5,n7,n12).orderBy(orderList);
			}else if(StringUtils.isNotBlank(req.getLoginId()) && "issuer".equalsIgnoreCase(req.getUserType()) && ("low".equalsIgnoreCase(req.getSubUserType())||"high".equalsIgnoreCase(req.getSubUserType()))){
				n8=cb.equal(subUserTypeQuery,"high");
				n9=cb.equal(subUserTypeQuery,"both");
				n7 = cb.or(n8,n9);
				query.where(n1,n2,n3,n4,n5,n7,n12).orderBy(orderList);
			}else if(StringUtils.isNotBlank(req.getLoginId()) && "broker".equalsIgnoreCase(req.getUserType()) && "b2b".equalsIgnoreCase(req.getSubUserType())){
				n7=cb.equal(m.get("agencyCode"), agencyCodeQuery);
				query.where(n1,n2,n3,n4,n5,n7,n12).orderBy(orderList);
			}else {
				n6=cb.equal(m.get("loginId"), req.getLoginId());
				n7=cb.equal(m.get("agencyCode"), agencyCodeQuery);
				query.where(n1,n2,n3,n4,n5,n6,n7,n12).orderBy(orderList);
			}
			
			// Get Result
			TypedQuery<Tuple> result = em.createQuery(query);
			list = result.getResultList();
			if(list!=null && list.size()>0) {
			List<DasboardPolicyListRes> quoteResList = new ArrayList<DasboardPolicyListRes>();
			List<DasboardPolicyListRes> raResList = new ArrayList<DasboardPolicyListRes>();
			List<DasboardPolicyListRes> policyResList = new ArrayList<DasboardPolicyListRes>();
			List<Tuple> fiterStatusY = list.stream().filter( o -> o.get("status").equals("Y")   ).collect(Collectors.toList());
			if (fiterStatusY.size() > 0 && fiterStatusY != null) {
				for (Tuple map : fiterStatusY) {
				DasboardPolicyListRes res = new DasboardPolicyListRes();
				res.setPolicyNo(map.get("originalPolicyNo")==null?"":map.get("originalPolicyNo").toString());
				res.setCustomerName(map.get("customerName")==null?"":map.get("customerName").toString());
				res.setRequestReferencNo(map.get("requestReferenceNo")==null?"":map.get("requestReferenceNo").toString());
				res.setQuoteNo(map.get("quoteNo") == null ? "" : map.get("quoteNo").toString());
				
				String policyStartDate = map.get("inceptionDate") == null ? null: dateFormat.format(map.get("inceptionDate"));
				res.setPolicyStartDate(policyStartDate);
				
				String policyEndDate = map.get("expiryDate") == null ? null: dateFormat.format(map.get("expiryDate"));
				res.setPolicyEndDate(policyEndDate);
			
				res.setStatus(map.get("status") == null ? "" : String.valueOf(map.get("status")));
				//res.setCount(map.get("count") == null ? "" : String.valueOf(map.get("count")));
				quoteResList.add(res);
			}
		}

			List<Tuple> fiterStatusRP = list.stream().filter( o -> o.get("status").equals("RP")   ).collect(Collectors.toList());
			if(fiterStatusRP.size()>0 && fiterStatusRP!=null) {
			for (Tuple map : fiterStatusRP) {
				DasboardPolicyListRes res = new DasboardPolicyListRes();
				res.setPolicyNo(map.get("originalPolicyNo")==null?"":map.get("originalPolicyNo").toString());
				res.setRequestReferencNo(map.get("requestReferenceNo")==null?"":map.get("requestReferenceNo").toString());
				res.setQuoteNo(map.get("quoteNo") == null ? "" : map.get("quoteNo").toString());
				res.setCustomerName(map.get("customerName")==null?"":map.get("customerName").toString());
				String policyStartDate = map.get("inceptionDate") == null ? null: dateFormat.format(map.get("inceptionDate"));
				res.setPolicyStartDate(policyStartDate);
				
				String policyEndDate = map.get("expiryDate") == null ? null: dateFormat.format(map.get("expiryDate"));
				res.setPolicyEndDate(policyEndDate);
			
				res.setStatus(map.get("status") == null ? "" : String.valueOf(map.get("status")));
				//res.setCount(map.get("count") == null ? "" : String.valueOf(map.get("count")));
				raResList.add(res);
			}
		}
			List<Tuple> fiterStatusP = list.stream().filter( o -> o.get("status").equals("P")   ).collect(Collectors.toList());
			if(fiterStatusP.size()>0 && fiterStatusP!=null) {
			for (Tuple map : fiterStatusP) {
				DasboardPolicyListRes res = new DasboardPolicyListRes();
				res.setPolicyNo(map.get("originalPolicyNo")==null?"":map.get("originalPolicyNo").toString());
				res.setRequestReferencNo(map.get("requestReferenceNo")==null?"":map.get("requestReferenceNo").toString());
				res.setQuoteNo(map.get("quoteNo") == null ? "" : map.get("quoteNo").toString());
				res.setCustomerName(map.get("customerName")==null?"":map.get("customerName").toString());
				String policyStartDate = map.get("inceptionDate") == null ? null: dateFormat.format(map.get("inceptionDate"));
				res.setPolicyStartDate(policyStartDate);
				
				String policyEndDate = map.get("expiryDate") == null ? null: dateFormat.format(map.get("expiryDate"));
				res.setPolicyEndDate(policyEndDate);
			
				res.setStatus(map.get("status") == null ? "" : String.valueOf(map.get("status")));
				policyResList.add(res);
			}
		}
		reslist.setPolicyList(policyResList);
		reslist.setQuoteList(quoteResList);
		reslist.setRaList(raResList);

	}

} catch (Exception e) {
	e.printStackTrace();
	log.info("Log Details" + e.getMessage());
	return null;
}
return reslist;
}




	@Override
	public List<DasboardReferalPendingRes> getallReferalPendingbyLogin(DashBoardGetReq req) {
		List<DasboardReferalPendingRes> reslist = new ArrayList<DasboardReferalPendingRes>();
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		List<Tuple> list = new ArrayList<Tuple>();
		 
		try {
			
			Date startDate=req.getStartDate();
			Date endDate = req.getEndDate();
			/*if (StringUtils.isBlank(req.getStartDate()) && StringUtils.isBlank(req.getEndDate())) {
				Date date1 = new Date();
				Calendar cal = new GregorianCalendar();
				cal.setTime(date1);
				cal.add(Calendar.DATE, -30);cal.set(Calendar.HOUR_OF_DAY, 23);cal.set(Calendar.MINUTE, 59);
				startDate = cal.getTime();
				cal.setTime(date1);
				cal.add(Calendar.DAY_OF_MONTH, 0);cal.set(Calendar.HOUR_OF_DAY, 23);cal.set(Calendar.MINUTE, 59);
				endDate = cal.getTime();
				
			} else {
				Date startDate1 = sdf.parse(req.getStartDate());
				Date endDate1=sdf.parse(req.getEndDate());
				Calendar cal = new GregorianCalendar();
				cal.setTime(startDate1);
				//cal.add(Calendar.HOUR , -1);
				cal.add(Calendar.DAY_OF_MONTH, -1);cal.set(Calendar.HOUR_OF_DAY, 23);cal.set(Calendar.MINUTE, 59);
				startDate = cal.getTime() ;
				cal.setTime(endDate1);
			//	cal.add(Calendar.HOUR , +23);
				cal.add(Calendar.DAY_OF_MONTH, 0);cal.set(Calendar.HOUR_OF_DAY,23 );cal.set(Calendar.MINUTE, 59);
				endDate = cal.getTime() ;
			}*/
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

			// Find All
			Root<HomePositionMaster> m = query.from(HomePositionMaster.class);
			
			  Subquery<String> subUserTypeQuery = query.subquery(String.class);
		        Root<LoginMaster> ewayLoginRoot = subUserTypeQuery.from(LoginMaster.class);
		        subUserTypeQuery.select(ewayLoginRoot.get("subUserType"));
		        subUserTypeQuery.where(cb.equal(ewayLoginRoot.get("loginId"), req.getLoginId()));

		        Subquery<String> agencyCodeQuery = query.subquery(String.class);
		        Root<LoginMaster> agencyLoginRoot = agencyCodeQuery.from(LoginMaster.class);
		        agencyCodeQuery.select(agencyLoginRoot.get("agencyCode"));
		        agencyCodeQuery.where(
		                cb.equal(agencyLoginRoot.get("loginId"), req.getLoginId()),
		                cb.in(agencyLoginRoot.get("oaCode")).value(subUserTypeQuery)
		        );
		      
			// Select
			query.multiselect(	m.get("requestReferenceNo").alias("requestReferenceNo"),
					m.get("customerName").alias("customerName"), m.get("inceptionDate").alias("inceptionDate"),
					m.get("expiryDate").alias("expiryDate"),
					m.get("status").alias("status"));			
			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(m.get("status")));
		
			// Where
			Predicate n1 = m.get("status").in("RP");
			Predicate n2 = cb.between(m.get("inceptionDate"), startDate, endDate);
			Predicate n3 = cb.equal(m.get("companyId"), req.getInsuranceId());
			Predicate n4 = cb.equal(m.get("branchCode"), req.getBranchCode());
			Predicate n5 = cb.equal(m.get("productId"), req.getProductId());
			Predicate n6=null;
			Predicate n7=null;
			Predicate n8=null;
			Predicate n9=null;
			Expression<String> e0 = subUserTypeQuery;
	
		
			if(StringUtils.isNotBlank(req.getLoginId()) && "issuer".equalsIgnoreCase(req.getUserType()) && "both".equalsIgnoreCase(req.getSubUserType())){
				 n7 = e0.in("both");
				 query.where(n1,n2,n3,n4,n5,n7).orderBy(orderList);
			}else if(StringUtils.isNotBlank(req.getLoginId()) && "issuer".equalsIgnoreCase(req.getUserType()) && ("low".equalsIgnoreCase(req.getSubUserType())||"high".equalsIgnoreCase(req.getSubUserType()))){
				n8=cb.equal(subUserTypeQuery,"high");
				n9=cb.equal(subUserTypeQuery,"both");
				n7 = cb.or(n8,n9);
				query.where(n1,n2,n3,n4,n5,n7).orderBy(orderList);
			}else if(StringUtils.isNotBlank(req.getLoginId()) && "broker".equalsIgnoreCase(req.getUserType()) && "b2b".equalsIgnoreCase(req.getSubUserType())){
//				n6=cb.equal(m.get("userType"), "Broker");
				n7=cb.equal(m.get("agencyCode"), agencyCodeQuery);
				query.where(n1,n2,n3,n4,n5,n7).orderBy(orderList);
			}else {
				n6=cb.equal(m.get("loginId"), req.getLoginId());
				n7=cb.equal(m.get("agencyCode"), agencyCodeQuery);
				query.where(n1,n2,n3,n4,n5,n6,n7).orderBy(orderList);
			}
			
			// Get Result
			TypedQuery<Tuple> result = em.createQuery(query);
			list = result.getResultList();
		
			if(list.size()>0 && list!=null) {
			for (Tuple map : list) {
				DasboardReferalPendingRes res = new DasboardReferalPendingRes();
				
				res.setRequestReferencNo(map.get("requestReferenceNo")==null?"":map.get("requestReferenceNo").toString());
				res.setCustomerName(map.get("customerName")==null?"":map.get("customerName").toString());
				String policyStartDate = map.get("inceptionDate") == null ? null: dateFormat.format(map.get("inceptionDate"));
				res.setPolicyStartDate(policyStartDate);
				
				String policyEndDate = map.get("expiryDate") == null ? null: dateFormat.format(map.get("expiryDate"));
				res.setPolicyEndDate(policyEndDate);
			
				res.setStatus(map.get("status") == null ? "" : String.valueOf(map.get("status")));

//				res.setStatus(map.get("status") == null ? "" : String.valueOf(map.get("status")));
				reslist.add(res);
			}
		}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return reslist;

	}


	@Override
	public List<DasboardRecentCusListRes> getRecentCustomerList(DashBoardGetReq req) {
		List<DasboardRecentCusListRes> reslist = new ArrayList<DasboardRecentCusListRes>();
		DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy hh:mm:ss");
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		List<Tuple> list = new ArrayList<Tuple>();

		try {

			Date startDate = req.getStartDate();
			Date endDate = req.getEndDate();
			/*if (StringUtils.isBlank(req.getStartDate()) && StringUtils.isBlank(req.getEndDate())) {
				Date date1 = new Date();
				Calendar cal = new GregorianCalendar();
				cal.setTime(date1);
				cal.add(Calendar.DATE, -30);
				cal.set(Calendar.HOUR_OF_DAY, 23);
				cal.set(Calendar.MINUTE, 59);
				startDate = cal.getTime();
				cal.setTime(date1);
				cal.add(Calendar.DAY_OF_MONTH, 0);
				cal.set(Calendar.HOUR_OF_DAY, 23);
				cal.set(Calendar.MINUTE, 59);
				endDate = cal.getTime();

			} else {
				Date startDate1 = sdf.parse(req.getStartDate());
				Date endDate1 = sdf.parse(req.getEndDate());
				Calendar cal = new GregorianCalendar();
				cal.setTime(startDate1);
				// cal.add(Calendar.HOUR , -1);
				cal.add(Calendar.DAY_OF_MONTH, -1);
				cal.set(Calendar.HOUR_OF_DAY, 23);
				cal.set(Calendar.MINUTE, 59);
				startDate = cal.getTime();
				cal.setTime(endDate1);
				// cal.add(Calendar.HOUR , +23);
				cal.add(Calendar.DAY_OF_MONTH, 0);
				cal.set(Calendar.HOUR_OF_DAY, 23);
				cal.set(Calendar.MINUTE, 59);
				endDate = cal.getTime();
			}*/
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);

			// Find All
			Root<PersonalInfo> c = query.from(PersonalInfo.class);
			Root<HomePositionMaster> m = query.from(HomePositionMaster.class);
		

			// Select
			query.multiselect( // Customer Info
					c.get("customerReferenceNo").alias("customerReferenceNo"), 
					cb.max(c.get("titleDesc")).alias("titleDesc"),
					cb.max(c.get("clientName")).alias("clientName"), cb.max(c.get("mobileNo1")).alias("mobileNo1"),
					cb.max(c.get("email1")).alias("email1"), cb.max(c.get("genderDesc")).alias("genderDesc"),
					cb.max(c.get("occupationDesc")).alias("occupationDesc"), cb.max(c.get("entryDate")).alias("entryDate"),
					cb.max(c.get("companyId")).alias("companyId"), cb.max(c.get("branchCode")).alias("branchCode"));

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("clientName")));

			
			// Filter
			Subquery<String> notIn = query.subquery(String.class);
			Root<PersonalInfo> ocpm1 = notIn.from(PersonalInfo.class);
			notIn.select(ocpm1.get("customerReferenceNo"));
			Predicate a1 = cb.equal(ocpm1.get("companyId"), req.getInsuranceId());
			Predicate a2 = cb.equal(ocpm1.get("status"),"P");
			Predicate a3 = cb.equal(ocpm1.get("branchCode"), req.getBranchCode());
			Predicate a4 = cb.between(c.get("entryDate"), startDate, endDate);
			notIn.where(a1,a2,a3,a4);
			
//			Expression<String> e0 = notIn;
			
			// Where
			Predicate n1 = cb.equal(c.get("customerId"), m.get("customerId"));
			Predicate n2 = cb.between(c.get("entryDate"), startDate, endDate);
			Predicate n3 = cb.equal(c.get("companyId"), req.getInsuranceId());
			Predicate n4 = cb.equal(c.get("branchCode"), req.getBranchCode());
			Predicate n5 = cb.equal(m.get("productId"), req.getProductId());
			Predicate n6 = cb.equal(c.get("status"), "P");
//			Predicate n7 = e0.in(notIn);
			Predicate n7 = cb.not(c.get("customerReferenceNo").in(notIn));

			query.where(n1, n2, n3, n4, n5, n6,n7).orderBy(orderList).groupBy(c.get("clientName"),
					c.get("customerReferenceNo"));

			// Get Result
			TypedQuery<Tuple> result = em.createQuery(query);
			list = result.getResultList();

			if (list.size() > 0 && list != null) {
				for (Tuple map : list) {
					DasboardRecentCusListRes res = new DasboardRecentCusListRes();

					res.setCustomerReferenceNo(map.get("customerReferenceNo") == null ? "" : map.get("customerReferenceNo").toString());
					res.setTitleDesc(map.get("titleDesc") == null ? "" : map.get("titleDesc").toString());
					res.setClientName(map.get("clientName") == null ? "" : String.valueOf(map.get("clientName")));
					res.setCompanyId(map.get("companyId") == null ? "" : String.valueOf(map.get("companyId")));
					res.setBranchCode(map.get("branchCode") == null ? "" : String.valueOf(map.get("branchCode")));
					res.setMobileNo1(map.get("mobileNo1") == null ? "" : String.valueOf(map.get("mobileNo1")));
					res.setEmail1  (map.get("email1") == null ? "" : String.valueOf(map.get("email1")));
					res.setOccupationDesc(map.get("occupationDesc") == null ? "" : String.valueOf(map.get("occupationDesc")));
					res.setGenderDesc(map.get("genderDesc") == null ? "" : String.valueOf(map.get("genderDesc")));
					String entryDate = map.get("entryDate") == null ? null : dateFormat.format(map.get("entryDate"));
					res.setEntryDate(entryDate);
					reslist.add(res);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return reslist;
	}




	@Override
	public CommonRes getPaymentDetailsByLoginId(String loginId) {
		log.info("welcome to getPaymentDetailsByLoginId || "+loginId);
		try {
			LoginMaster lmData = loginRepo.findByLoginId(loginId);
			if(lmData!=null) {
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<Tuple> cq = cb.createQuery(Tuple.class);
				Root<PaymentDetail> pdRoot = cq.from(PaymentDetail.class);
				Root<HomePositionMaster> hpmRoot = cq.from(HomePositionMaster.class);
				
				if("Broker".equalsIgnoreCase(lmData.getUserType())) {
					
				}else if("User".equalsIgnoreCase(lmData.getUserType())) {
					
				}else if("Issuer".equalsIgnoreCase(lmData.getUserType())) {
					
				}
				
				cq.multiselect(pdRoot.get("quoteNo").alias("quoteNo"),pdRoot.get("customerName").alias("customerName"),
						pdRoot.get("premiumFc").alias("premiumFc"),pdRoot.get("paymentStatus").alias("paymentStatus"));
				
				
			}
			
			
			log.info("whoo whoo PaymentDetailsByLoginId Method Completed!");
		}catch(Exception e) {
			log.info("Exception in getPaymentDetailsByLoginId ----> "+e);
			e.printStackTrace();
		}
		return null;
	}

}
