package com.maan.eway.reinsurance.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import com.maan.eway.bean.PersonalInfo;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.reinsurance.req.RIGridBrokerReq;
import com.maan.eway.reinsurance.req.RiGridReq;
import com.maan.eway.reinsurance.res.RIBrokerRes;
import com.maan.eway.reinsurance.service.ReinsuranceGridService;
import com.maan.eway.renewal.res.RiGridRes;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

@Service
public class ReinsuranceGridServiceImpl implements ReinsuranceGridService{

	private Logger log = LogManager.getLogger(ReinsuranceGridServiceImpl.class);
	
	@PersistenceContext
	private EntityManager em; 
	
	
	@Override
	public CommonRes getReferralPendinglist(RiGridReq req, String status) {
		List<RiGridRes> referrals = new ArrayList<RiGridRes>();
		CommonRes res = new CommonRes();
		int limit=0;
		int offset=0;
		if(StringUtils.isNotBlank(req.getLimit()) && StringUtils.isNotBlank(req.getOffset())){
	     limit = Integer.valueOf(req.getLimit());
		 offset = Integer.valueOf(req.getOffset());
	    }
		try {
            CriteriaBuilder cb = em.getCriteriaBuilder();
            CriteriaQuery<RiGridRes> query = cb.createQuery(RiGridRes.class);
            Root<HomePositionMaster> h = query.from(HomePositionMaster.class);
            Root<PersonalInfo> p = query.from(PersonalInfo.class);

			List<Predicate> list = new ArrayList<>();

            query.multiselect(h.get("customerName").alias("clientName"),
                    h.get("companyId").alias("companyId"),
                    h.get("quoteNo").alias("quoteNo"),
                    h.get("branchCode").alias("branchCode"),
                    h.get("productId").alias("productId"),
                    h.get("requestReferenceNo").alias("requestReferenceNo"),
                    h.get("customerId").alias("customerId"),
                    h.get("inceptionDate").alias("policyStartDate"),
                    h.get("expiryDate").alias("policyEndDate"),
                    h.get("remarks").alias("remarks"),
                    h.get("referalRemarks").alias("referalRemarks"),
                    h.get("endorsementRemarks").alias("endorsementRemarks"),
                    h.get("endtTypeId").alias("endorsementType"),
                    h.get("endorsementEffdate").alias("endorsementEffdate"),
                    h.get("originalPolicyNo").alias("originalPolicyNo"),
                    h.get("endtPrevPolicyNo").alias("endtPrevPolicyNo"),
                    h.get("endtPrevQuoteNo").alias("endtPrevQuoteNo"),
					p.get("customerReferenceNo").alias("customerReferenceNo"),
					h.get("endtCount").alias("endtCount"),
                    h.get("endtStatus").alias("endtStatus"),
                    h.get("endtCategDesc").alias("endtCategDesc"),
                    h.get("endtPremium").alias("endtPremium"));
            List<Order> orderList = new ArrayList<Order>();
            orderList.add(cb.desc(h.get("inceptionDate")));
//            list = new ArrayList<Predicate>();
			list.add(cb.equal(h.get("customerId"), p.get("customerId")));
            list.add(cb.equal(h.get("companyId"), req.getCompanyid()));
            list.add(cb.equal(h.get("productId"), Integer.valueOf(req.getProductId())));
            list.add(cb.equal(h.get("riStatus"), status));
            list.add(h.get("status").in("Y", "E", "RA"));
            list.add(cb.equal(h.get("applicationId"), req.getApplicationId()));
            if (StringUtils.isNotBlank(req.getBdmCode())) {
                list.add(cb.equal(h.get("bdmCode"), req.getBdmCode()));
            }
            if (StringUtils.isNotBlank(req.getLoginId())) {
                list.add(cb.equal(h.get("loginId"), req.getLoginId()));
            }
            if (req.getUserType().equalsIgnoreCase("Broker") || req.getUserType().equalsIgnoreCase("User")) {
                list.add(cb.equal(h.get("brokerBranchCode"), req.getBrokerBranchCode()));
            } else {

                list.add(cb.equal(h.get("branchCode"), req.getBranchCode()));
            }

//			if (req.getType().equalsIgnoreCase("Q")) {
//				list.add(cb.isNull(h.get("endtCategDesc")));
//			} else if (req.getType().equalsIgnoreCase("E")) {
//				list.add(cb.isNotNull(h.get("endtCategDesc")));
//			}

            query.where(cb.and(list.toArray(new Predicate[0])));
            TypedQuery<RiGridRes> result = em.createQuery(query);
            result.setFirstResult(limit * offset);
            result.setMaxResults(offset);
            referrals = result.getResultList();

            if (referrals != null && referrals.size() > 0) {
                referrals = referrals.stream().filter(distinctByKey(o -> Arrays.asList(o.getRequestReferenceNo())))
                        .collect(Collectors.toList());
                res.setCommonResponse(referrals);
                res.setMessage("Success");
                res.setErroCode(0);
                res.setIsError(false);

            } else {
                referrals = null;
                res.setCommonResponse(referrals);
                res.setMessage("failed => data Null");
                res.setErroCode(1);
                res.setIsError(true);
            }

        } catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details => getReferralPendinglist" + e.getMessage());
			res.setCommonResponse(null);
			res.setMessage("Error in getReferralPendinglist");
			res.setErroCode(1);
			res.setIsError(true);
		}
		return res;
	}
	
	private static <T> java.util.function.Predicate<T> distinctByKey(
			java.util.function.Function<? super T, ?> keyExtractor) {
		Map<Object, Boolean> seen = new ConcurrentHashMap<>();
		return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

	@Override
	public CommonRes getBrokerlist(RIGridBrokerReq req, String status){
		CommonRes rescom = new CommonRes();
		List<Tuple> tuple = new ArrayList<Tuple>();
		List<RIBrokerRes> resList = new ArrayList<RIBrokerRes>();
	try
	{
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<Tuple> query = cb.createQuery(Tuple.class);
		Root<HomePositionMaster> h = query.from(HomePositionMaster.class);
		
		query.multiselect(h.get("loginId").alias("code"),
				h.get("bdmName").alias("codeDesc"),
				h.get("sourceType").alias("type"));
		List<Predicate> list = new ArrayList<Predicate>();
		list.add(cb.equal(h.get("applicationId"), req.getApplicationId()));
		list.add(cb.equal(h.get("productId"),Integer.valueOf(req.getProductId())));
		list.add(cb.equal(h.get("riStatus"), status));
		list.add(cb.equal(h.get("companyId"), req.getCompanyId()));
		if (StringUtils.isNotBlank(req.getLoginId())) {
			list.add(cb.equal(h.get("loginId"), req.getLoginId()));
		}
//		if (req.getType().equalsIgnoreCase("Q")) {
//			list.add(cb.isNull(h.get("endtCategDesc")));
//		} else if (req.getType().equalsIgnoreCase("E")) {
//			list.add(cb.isNotNull(h.get("endtCategDesc")));
//		}
		query.where(cb.and(list.toArray(new Predicate[0])));
		TypedQuery<Tuple> typedQuery = em.createQuery(query);
		tuple = typedQuery.getResultList();
		tuple = tuple.stream().filter(distinctByKey(o -> Arrays.asList(o.get("code"))))
				.collect(Collectors.toList());
		
		if (tuple != null && tuple.size() > 0) {

			for (Tuple data : tuple) {
				RIBrokerRes res = new RIBrokerRes();
				res.setCode(data.get("code") == null ? "" : data.get("code").toString());
				res.setCodeDesc(data.get("codeDesc") == null ? "" : data.get("codeDesc").toString());
				res.setType(data.get("type") == null ? "" : data.get("type").toString());
				resList.add(res);

			}
		}
		rescom.setCommonResponse(resList);
		rescom.setMessage("Success");
		rescom.setErroCode(0);
		rescom.setIsError(false);
		
	}
	catch(Exception e)
	{
		e.printStackTrace();
		log.info("Log Details => getReferralPendinglist" + e.getMessage());
		rescom.setCommonResponse(null);
		rescom.setMessage("failed");
		rescom.setErroCode(0);
		rescom.setIsError(false);
	}
		return rescom;
	}
	

}
