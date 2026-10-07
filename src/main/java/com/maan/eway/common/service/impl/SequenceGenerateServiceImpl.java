package com.maan.eway.common.service.impl;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.hibernate.query.sql.internal.NativeQueryImpl;
import org.hibernate.transform.AliasToEntityMapResultTransformer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maan.eway.bean.ProductSequenceCondition;
import com.maan.eway.bean.ProductSequenceMaster;
import com.maan.eway.common.req.SequenceGenerateReq;
import com.maan.eway.common.res.SequenceGenerateRes;
import com.maan.eway.common.service.SequenceGenerateService;
import com.maan.eway.repository.ProductSequenceMasterRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Query;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

@Service
@Transactional
public class SequenceGenerateServiceImpl implements SequenceGenerateService {
 
	private Logger log = LogManager.getLogger(SequenceGenerateServiceImpl.class);

	@Autowired
	private ProductSequenceMasterRepository  prodSeqRepo ;
	
	@PersistenceContext
	private EntityManager em;


	@Override
    @Transactional

	public  SequenceGenerateRes generateSequence(SequenceGenerateReq req) {
		SequenceGenerateRes res = new SequenceGenerateRes();
	try {
		// Sequence Master 
		ProductSequenceMaster seqMaster = getProductSequenceMaster(req);
		
		if(seqMaster !=null ) {
			
			//String.format("%07d",entity.getPolicyno());
			String seqNo = String.format("%0"+ seqMaster.getCurrentSequenceNo().length() +"d", Long.valueOf(seqMaster.getCurrentSequenceNo())+1);  
			seqMaster.setCurrentSequenceNo(seqNo);
			
			String seqCharacter = seqMaster.getSequenceCharacter();
			
			String coniditionValue  ="";
			 String month = LocalDate.now().format(DateTimeFormatter.ofPattern("MM"));
			System.out.println(month);
			if( "Y".equalsIgnoreCase(seqMaster.getQueryYn()) ) {
				coniditionValue = frameQueryConditions(req , seqMaster) ;
			}
			String generatedSequence  = null;
			if(req.getType().equalsIgnoreCase("5") && req.getInsuranceId().equalsIgnoreCase("100020") ) {
				 generatedSequence = seqCharacter + coniditionValue 
						+ ( StringUtils.isNotBlank(seqMaster.getSequenceNoApplyYn()) &&"N".equalsIgnoreCase(seqMaster.getSequenceNoApplyYn()) ?"" :  seqNo )+"/"+month ;
			} else {
				 generatedSequence = seqCharacter + coniditionValue 
						+ ( StringUtils.isNotBlank(seqMaster.getSequenceNoApplyYn()) &&"N".equalsIgnoreCase(seqMaster.getSequenceNoApplyYn()) ?"" :  seqNo ) ;
			}
			
			
			seqMaster.setCurrentGeneratedSequence(generatedSequence);
			prodSeqRepo.save(seqMaster);
			res.setGeneratedValue(generatedSequence);			
		}
		
		
	} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			
		}
		return res;
	}
	
	public  String frameQueryConditions(SequenceGenerateReq req , ProductSequenceMaster seqMaster ) {
		String conditionsValue = "";
	try {
		// Sequence Conditions
		List<ProductSequenceCondition> seqConditions =  getProductSequenceCondtions(req , seqMaster.getProductId()  ,seqMaster.getSequenceId() ) ;
		
		StringBuffer str = new StringBuffer(); 
		for (ProductSequenceCondition seqCond : seqConditions ) {
			
			// Seq Conditions
			if (StringUtils.isNotBlank(seqCond.getQuery() ) && seqCond.getQuery().contains("?") && req.getParams()!=null && req.getParams().size() > 0 ) {
				List<Map<String, Object>> dataFromQuery = new ArrayList<Map<String, Object>>();
				
				try {
					String query = seqCond.getQuery();
					List<String> params = req.getParams();
					dataFromQuery =  getListFromQueryWithoutKey(query, params);
					
					Map<String, Object> data =  dataFromQuery.get(0) ;
					//String key = (String) data.values().iterator().next();
					String value =String.valueOf((Object) data.values().iterator().next());
					str.append(value==null ? ""  : value );
					
				} catch (Exception e ) {
					e.printStackTrace();
					log.info("Exception is ---> " + e.getMessage());
				}
				
			} else {
				//str.append(seqCond.getQuery()==null?"":seqCond.getQuery());
			}
			
			// Join with
			str.append(seqCond.getJoinWith()==null?"":seqCond.getJoinWith());
			
		}
		conditionsValue = str.toString() ; 
	
		
	} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			
		}
		return conditionsValue;
	}
	
	public List<Map<String, Object>> getListFromQueryWithoutKey(String query, List<String> params) {
		if(query!=null) {
			 Query nativequery = em.createNativeQuery(query);		
			for(int i=0;i<params.size();i++) {
				nativequery.setParameter(i+1, params.get(i));
			}			
		 
			nativequery.unwrap(NativeQueryImpl.class).setResultTransformer(AliasToEntityMapResultTransformer.INSTANCE);
			List<Map<String,Object>> list = nativequery.getResultList();
			return list;
		}
		return null;
	}

	
	public ProductSequenceMaster getProductSequenceMaster(SequenceGenerateReq req) {
		ProductSequenceMaster seqMaster = new ProductSequenceMaster();
		try {
//			Date today = new Date();
//			Calendar cal = new GregorianCalendar();
//			cal.setTime(today);
//			cal.set(Calendar.HOUR_OF_DAY, 23);
//			;
//			cal.set(Calendar.MINUTE, 1);
//			today = cal.getTime();
//			cal.set(Calendar.HOUR_OF_DAY, 1);
//			cal.set(Calendar.MINUTE, 1);
//			Date todayEnd = cal.getTime();

//			// Criteria
//			CriteriaBuilder cb = em.getCriteriaBuilder();
//			CriteriaQuery<ProductSequenceMaster> query = cb.createQuery(ProductSequenceMaster.class);
//			List<ProductSequenceMaster> list = new ArrayList<ProductSequenceMaster>();
//			// Find All
//			Root<ProductSequenceMaster> c = query.from(ProductSequenceMaster.class);
//			// Select
//			query.select(c);
//			// Order By
//			List<Order> orderList = new ArrayList<Order>();
//			orderList.add(cb.asc(c.get("productId")));
//
//			// Effective Date Start Max Filter
//			Subquery<Date> effectiveDate = query.subquery(Date.class);
//			Root<ProductSequenceMaster> ocpm1 = effectiveDate.from(ProductSequenceMaster.class);
//			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
//			Predicate a1 = cb.equal(c.get("sequenceId"), ocpm1.get("sequenceId"));
//			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
//			Predicate a3 = cb.equal(c.get("companyId"), ocpm1.get("companyId"));
//			Predicate a4 = cb.equal(c.get("branchCode"), ocpm1.get("branchCode"));
//			Predicate a6 = cb.equal(c.get("productId"), ocpm1.get("productId"));
//			effectiveDate.where(a1, a2,a3,a4,a6);
//			
//			// Effective Date End Max Filter
//			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
//			Root<ProductSequenceMaster> ocpm2 = effectiveDate2.from(ProductSequenceMaster.class);
//			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
//			Predicate a7 = cb.equal(c.get("sequenceId"), ocpm2.get("sequenceId"));
//			Predicate a8 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
//			Predicate a9 = cb.equal(c.get("companyId"), ocpm2.get("companyId"));
//			Predicate a10 = cb.equal(c.get("branchCode"), ocpm2.get("branchCode"));
//			Predicate a12 = cb.equal(c.get("productId"),  ocpm2.get("productId"));
//			effectiveDate2.where(a7,a8,a9,a10,a12);
//			
//			// Where
//			Predicate n1 = cb.equal(c.get("status"),"Y");
//			Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
//			Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
//			Predicate n4 = cb.equal(c.get("companyId"), req.getInsuranceId());
//			Predicate n5 = cb.equal(c.get("branchCode"), "99999");
//			Predicate n6 = cb.equal(c.get("type"), req.getType() );
//			Predicate n7 = cb.equal(c.get("productId"), req.getProductId());
//			Predicate n8 = cb.equal(c.get("productId"), "99999");
//			Predicate n9 = cb.or(n7,n8) ;
//			
//			query.where(n1,n2,n3,n4,n5,n6,n9).orderBy(orderList);
//			
//			// Get Result
//			TypedQuery<ProductSequenceMaster> result = em.createQuery(query);
//			list = result.getResultList();
//			seqMaster = list.get(0);
			  Date today = new Date();
	            Calendar cal = Calendar.getInstance();
	            cal.setTime(today);
	            cal.set(Calendar.HOUR_OF_DAY, 23);
	            cal.set(Calendar.MINUTE, 59);
	            Date todayStart = cal.getTime();

	            cal.set(Calendar.HOUR_OF_DAY, 0);
	            cal.set(Calendar.MINUTE, 0);
	            Date todayEnd = cal.getTime();
			  List<ProductSequenceMaster> list = prodSeqRepo.findForUpdate(
	                    req.getInsuranceId(),
	                    req.getType(),
	                    req.getProductId(),
	                    todayStart,
	                    todayEnd
	            );

	            return list.isEmpty() ? null : list.get(0);
		} catch(Exception e) {
				e.printStackTrace();
				log.info("Exception is --->"+e.getMessage());
				return null;
				}
			//return seqMaster;
		}
	
	public List<ProductSequenceCondition> getProductSequenceCondtions(SequenceGenerateReq req ,Integer productId , Integer sequenceId ) {
		List<ProductSequenceCondition> seqConditions = new ArrayList<ProductSequenceCondition>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			;
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			Date todayEnd = cal.getTime();

			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ProductSequenceCondition> query = cb.createQuery(ProductSequenceCondition.class);
			// Find All
			Root<ProductSequenceCondition> c = query.from(ProductSequenceCondition.class);
			// Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("queryId")));

			// Effective Date Start Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<ProductSequenceCondition> ocpm1 = effectiveDate.from(ProductSequenceCondition.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(c.get("sequenceId"), ocpm1.get("sequenceId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a3 = cb.equal(c.get("companyId"), ocpm1.get("companyId"));
			Predicate a4 = cb.equal(c.get("branchCode"), ocpm1.get("branchCode"));
			Predicate a6 = cb.equal(c.get("productId"), ocpm1.get("productId"));
			Predicate a13 = cb.equal(c.get("queryId"), ocpm1.get("queryId"));
			effectiveDate.where(a1, a2,a3,a4,a6 , a13);
			
			// Effective Date End Max Filter
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<ProductSequenceCondition> ocpm2 = effectiveDate2.from(ProductSequenceCondition.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate a7 = cb.equal(c.get("sequenceId"), ocpm2.get("sequenceId"));
			Predicate a8 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			Predicate a9 = cb.equal(c.get("companyId"), ocpm2.get("companyId"));
			Predicate a10 = cb.equal(c.get("branchCode"), ocpm2.get("branchCode"));
			Predicate a12 = cb.equal(c.get("productId"),  ocpm2.get("productId"));
			Predicate a14 = cb.equal(c.get("queryId"),  ocpm2.get("queryId"));
			effectiveDate2.where(a7,a8,a9,a10,a12,a14);
			
			// Where
			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
			Predicate n4 = cb.equal(c.get("companyId"), req.getInsuranceId());
			Predicate n5 = cb.equal(c.get("branchCode"), "99999");
			Predicate n6 = cb.equal(c.get("productId"), productId);
			Predicate n7 = cb.equal(c.get("sequenceId"), sequenceId);
			
			query.where(n1,n2,n3,n4,n5,n6,n7).orderBy(orderList);
			
			// Get Result
			TypedQuery<ProductSequenceCondition> result = em.createQuery(query);
			seqConditions = result.getResultList();
		
			
		} catch(Exception e) {
				e.printStackTrace();
				log.info("Exception is --->"+e.getMessage());
				return null;
				}
			return seqConditions;
		}

}
