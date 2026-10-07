package com.maan.eway.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.ProductMaster;
import com.maan.eway.master.req.GetPolicyDetailsReq;
import com.maan.eway.master.req.GetQuoteCountReq;
import com.maan.eway.master.req.GetQuoteDetailsReq;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.res.GetPolicyDetailsRes;
import com.maan.eway.res.GetQuoteCountRes;
import com.maan.eway.res.GetQuoteDetailsRes;
import com.maan.eway.service.HomePositionMasterService;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
import jakarta.transaction.Transactional;

@Service
@Transactional
public class HomePositionMasterServiceImpl implements HomePositionMasterService {

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private HomePositionMasterRepository repository;

	@Autowired
	private BasicValidationService basicvalidateService;

	Gson json = new Gson();

	private Logger log=LogManager.getLogger(HomePositionMasterServiceImpl.class);

	@Override
	public List<GetQuoteCountRes> getCustomerQuoteCount(GetQuoteCountReq req) {
		 List<GetQuoteCountRes>  resList = new  ArrayList<GetQuoteCountRes>();
		try {
			Date today = new Date(); 
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<QuoteCountCriteriaRes> query = cb.createQuery(QuoteCountCriteriaRes.class);
			List<QuoteCountCriteriaRes> list = new ArrayList<QuoteCountCriteriaRes>();
			
			// Find All
			Root<HomePositionMaster> h = query.from(HomePositionMaster.class);
			
			// Select Product Name SubQuery for Effective Date Max Filter 
			Subquery<Date> pmEff = query.subquery(Date.class);
			Root<ProductMaster> pm = pmEff.from(ProductMaster.class);
			Subquery<Long> product = query.subquery(Long.class);
			Root<ProductMaster> p = product.from(ProductMaster.class);
			
			pmEff.select( cb.greatest(pm.get("effectiveDateStart").as(Date.class)) );
			Predicate p1 = cb.equal(p.get("companyId"), pm.get("companyId"));
			Predicate p2 = cb.equal(p.get("productId"), pm.get("productId"));
			Predicate p3 = cb.lessThanOrEqualTo(pm.get("effectiveDateStart") , today);
			pmEff.where(p1,p2,p3);
			
			product.select( p.get("productName")) ;
			Predicate pm1 = cb.equal(p.get("companyId"), h.get("companyId"));
			Predicate pm2 = cb.equal(p.get("productId"), h.get("productId"));
			Predicate pm3   = cb.equal(p.get("effectiveDateStart"),pmEff);
			product.where(pm1,pm2,pm3);
			
			// Select
			query.multiselect( h.get("productId").alias("productId") ,   product.alias("productName") ,
					cb.count(h.get("quoteNo")).alias("quoteCount")  , cb.count( h.get("policyNo")).alias("policyCount") ,
					cb.sum(h.get("premium"))  , h.get("overallPremium") );

			// Order By
			List<Order> orderList = new ArrayList<Order>();
		    orderList.add(cb.asc(h.get("productId")));
			
			// Where
			Predicate n1 = cb.equal(h.get("customerId"),req.getCustomerId());
			Predicate n2 = cb.equal(h.get("companyId"),req.getInsuranceId());
			Predicate n3 = cb.equal(h.get("loginId"),req.getCreatedBy());
			query.where(n1,n2,n3).groupBy( h.get("productId")) .orderBy(orderList);

			// Get Result
			TypedQuery<QuoteCountCriteriaRes> result = em.createQuery(query);
			list = result.getResultList();
			
			for (QuoteCountCriteriaRes data : list) {
				GetQuoteCountRes res = new GetQuoteCountRes();
				
				res.setOverallPremium(data.getOverallPremium()==null?"0":data.getOverallPremium().toString());
				res.setPolicyCount(data.getPolicyCount()==null?"0":data.getPolicyCount().toString());
				res.setPremium(data.getPremium()==null?"0":data.getPremium().toString());
				res.setProductId(data.getProductId()==null?"":data.getProductId().toString());
				res.setProductName(StringUtils.isBlank(data.getProductName())?"":data.getProductName());
				res.setQuoteCount(data.getQuoteCount()==null?"0":data.getQuoteCount().toString());
				resList.add(res);
				
			}
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return resList;
	}

	@Override
	public List<GetQuoteDetailsRes> getCustomerQuoteDetails(GetQuoteDetailsReq req) {
		 List<GetQuoteDetailsRes>  resList = new  ArrayList<GetQuoteDetailsRes>();
		 DozerBeanMapper dozerMapper = new DozerBeanMapper(); 
			try {
			//	List<HomePositionMaster> list = repository.findByCustomerIdAndProductIdAndLoginIdOrderByUpdatedDateAsc(Integer.valueOf(req.getCustomerId()) ,Integer.valueOf(req.getProductId()) , req.getCreatedBy());
				
				// Criteria
			    CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<HomePositionMaster> query = cb.createQuery(HomePositionMaster.class);
				List<HomePositionMaster> list = new ArrayList<HomePositionMaster>();
				
				// Find All
				Root<HomePositionMaster> h = query.from(HomePositionMaster.class);
				
				// Select
				query.select( h );

				// Order By
				List<Order> orderList = new ArrayList<Order>();
			    orderList.add(cb.desc(h.get("updatedDate")));
				
				// Where
				Predicate n1 = cb.equal(h.get("customerId"),req.getCustomerId());
				Predicate n2 = cb.equal(h.get("productId"),req.getProductId());
				Predicate n3 = cb.equal(h.get("loginId"),req.getCreatedBy());
				query.where(n1,n2,n3).orderBy(orderList);
				
				// Get Result
				TypedQuery<HomePositionMaster> result = em.createQuery(query);
				list = result.getResultList(); 
				
				for (HomePositionMaster data : list) {
					GetQuoteDetailsRes res = new GetQuoteDetailsRes();
					
					res = dozerMapper.map(data, GetQuoteDetailsRes.class ); 
					resList.add(res);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is --->" + e.getMessage());
				return null;
			}
			return resList;
		}

	@Override
	public List<GetPolicyDetailsRes> getCustomerPolicyDetails(GetPolicyDetailsReq req) {
		List<GetPolicyDetailsRes>  resList = new  ArrayList<GetPolicyDetailsRes>();
		 DozerBeanMapper dozerMapper = new DozerBeanMapper(); 
			try {
			//	List<HomePositionMaster> list = repository.findByCustomerIdAndProductIdAndLoginIdOrderByUpdatedDateAsc(Integer.valueOf(req.getCustomerId()) ,Integer.valueOf(req.getProductId()) , req.getCreatedBy());
				
				// Criteria
			    CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<HomePositionMaster> query = cb.createQuery(HomePositionMaster.class);
				List<HomePositionMaster> list = new ArrayList<HomePositionMaster>();
				
				// Find All
				Root<HomePositionMaster> h = query.from(HomePositionMaster.class);
				
				// Select
				query.select( h );

				// Order By
				List<Order> orderList = new ArrayList<Order>();
			    orderList.add(cb.desc(h.get("updatedDate")));
				
				// Where
				Predicate n1 = cb.equal(h.get("customerId"),req.getCustomerId());
				Predicate n2 = cb.equal(h.get("productId"),req.getProductId());
				Predicate n3 = cb.equal(h.get("loginId"),req.getCreatedBy());
				Predicate n4 = cb.isNotNull(h.get("policyNo"));
				query.where(n1,n2,n3,n4).orderBy(orderList);
				
				// Get Result
				TypedQuery<HomePositionMaster> result = em.createQuery(query);
				list = result.getResultList(); 
				
				for (HomePositionMaster data : list) {
					GetPolicyDetailsRes res = new GetPolicyDetailsRes();
					
					res = dozerMapper.map(data, GetPolicyDetailsRes.class ); 
					resList.add(res);
				}
				
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is --->" + e.getMessage());
				return null;
			}
			return resList;
		}
	
}
