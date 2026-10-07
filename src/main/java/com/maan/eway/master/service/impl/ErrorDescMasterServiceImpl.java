package com.maan.eway.master.service.impl;

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
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.ErrorDescMaster;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.common.service.impl.GenerateSeqNoServiceImpl;
import com.maan.eway.master.req.ErrorDescMasterGetReq;
import com.maan.eway.master.req.ErrorDescMasterSaveReq;
import com.maan.eway.master.req.ErrorMasterGetAllReq;
import com.maan.eway.master.res.ErrorDescMasterRes;
import com.maan.eway.master.service.ErrorDescMasterService;
import com.maan.eway.repository.ErrorDescMasterRepository;
import com.maan.eway.res.SuccessRes;

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
public class ErrorDescMasterServiceImpl implements ErrorDescMasterService {
	
	@PersistenceContext
	private EntityManager em;

	@Autowired
	private GenerateSeqNoServiceImpl seqNo;
	
	@Autowired
	private ErrorDescMasterRepository repo;

	
	@Override
	public List<String> validateErrorDesc(ErrorDescMasterSaveReq req) {
		List<String> errorList = new ArrayList<String>();
	//	DozerBeanMapper dozerMapper = new DozerBeanMapper();
		
		try
		{
//			if (StringUtils.isBlank(req.getErrorCode()) ) {
//				errorList.add(new Error("01", "Error Code", "Please Select Error Code "));
//			}
			
			if (StringUtils.isBlank(req.getErrorDesc()) ) {
			//	errorList.add(new Error("01", "Error Desc", "Please Enter Error Desc"));
				errorList.add("1813");
				
			} else if (req.getErrorDesc().length() > 200 ) {
			//	errorList.add(new Error("01", "Error Desc", "Error Desc Must be under 200 Charecters Allowed"));
				errorList.add("1814");
			}
			
			if (StringUtils.isBlank(req.getCreatedBy()) ) {
			//	errorList.add(new Error("01", "CreatedBy", "Please Enter CreatedBy"));
				errorList.add("2039");
			}
			
			if (StringUtils.isBlank(req.getErrorField()) ) {
				//	errorList.add(new Error("01", "ErrorField", "Please Enter ErrorField"));
					errorList.add("2102");
					
				}
			
			// Date Validation
			Calendar cal = new GregorianCalendar();
			Date today = new Date();
			cal.setTime(today);
			cal.add(Calendar.DAY_OF_MONTH, -1);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 50);
			today = cal.getTime();
			if (req.getEffectiveDateStart() == null) {
			//	errorList.add(new Error("04", "EffectiveDateStart", "Please Enter Effective Date Start "));
				errorList.add("2034");

			} else if (req.getEffectiveDateStart().before(today)) {
//				errorList.add(new Error("04", "EffectiveDateStart", "Please Enter Effective Date Start as Future Date"));
					
				errorList.add("2035");
			}
						
			if (StringUtils.isBlank(req.getProductId()) ) {
//				errorList.add(new Error("01", "Product Id", "Please Select Product Id"));
				errorList.add("2100");
			}
			
			if (StringUtils.isBlank(req.getInsuranceId()) ) {
//				errorList.add(new Error("01", "Insurance Id", "Please Select Insurance Id"));
				errorList.add("2101");
			}
		
			if (StringUtils.isNotBlank(req.getLanguage()) ) {
				
				if (StringUtils.isBlank(req.getLocalLanguageErrDesc()) ) {
//					errorList.add(new Error("01", "Local Language Error Desc", "Please Enter Local Language Error Description"));
					errorList.add("1815");
				}
				if (StringUtils.isBlank(req.getLocalLanguageErrField()) ) {
//					errorList.add(new Error("01", "Local Language Error Field", "Please Enter Local Language Error Field"));
					errorList.add("1816");
				}
			}	
			
		}catch(Exception e)
		{
			e.printStackTrace();
			return null;
		}
		
		return errorList;
	}

	@Override
	public SuccessRes inserterrordesc(ErrorDescMasterSaveReq req) {
		
		
		SimpleDateFormat sdformat = new SimpleDateFormat("dd/MM/YYYY");
		SuccessRes res = new SuccessRes();
		
		try
		{
		
		   ErrorDescMaster errordesc = new ErrorDescMaster();
		
		   List<ErrorDescMaster> errordescs = new ArrayList<ErrorDescMaster>();
		
		   DozerBeanMapper dozerMapper = new DozerBeanMapper();
		
		   Integer amendId = 0 ;
		
			Date startDate = req.getEffectiveDateStart() ;
			String end = "31/12/2050";
			Date endDate = sdformat.parse(end);
			long MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;
			Date oldEndDate = new Date(req.getEffectiveDateStart().getTime() - MILLIS_IN_A_DAY);
			Date entryDate = new Date() ;
			String createdBy = req.getCreatedBy() ;
			
			String errorCode = "";
			
			if(StringUtils.isBlank(req.getErrorCode()))
			{
				errorCode = seqNo.generateErrorCode();
				res.setResponse("Saved Successfully ");
				res.setSuccessId(errorCode);
			}
			else
			{
				errorCode = req.getErrorCode();
				
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<ErrorDescMaster> query = cb.createQuery(ErrorDescMaster.class);

				Root<ErrorDescMaster> b = query.from(ErrorDescMaster.class);
				
				query.select(b);
				
				// Order By
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(b.get("amendId")));
				
				Predicate n3 = cb.equal(b.get("errorCode"), req.getErrorCode());
				Predicate n4 = cb.equal(b.get("productId"), req.getProductId());
				Predicate n6 = cb.equal(b.get("companyId"), req.getInsuranceId());
				Predicate n7 = cb.equal(b.get("moduleId"), req.getModuleId());
				Predicate n8 = cb.equal(b.get("branchCode"), StringUtils.isBlank(req.getBranchCode()) ? "99999" : req.getBranchCode());
				query.where( n3, n4,n6,n7,n8).orderBy(orderList);
				
				TypedQuery<ErrorDescMaster> result = em.createQuery(query);
				int limit = 0 , offset = 2 ;
				result.setFirstResult(limit * offset);
				result.setMaxResults(offset);
				errordescs = result.getResultList();
				
				if (errordescs.size() > 0) {
					Date beforeOneDay = new Date(new Date().getTime() - MILLIS_IN_A_DAY);
					
					if ( errordescs.get(0).getEffectiveDateStart().before(beforeOneDay)  ) {
						amendId = errordescs.get(0).getAmendId() + 1 ;
						entryDate = new Date() ;
						createdBy = req.getCreatedBy();
						ErrorDescMaster lastRecord = errordescs.get(0);
							lastRecord.setEffectiveDateEnd(oldEndDate);
							repo.saveAndFlush(lastRecord);
						
					} else {
						amendId = errordescs.get(0).getAmendId() ;
						entryDate = errordescs.get(0).getEntryDate() ;
						createdBy = errordescs.get(0).getCreatedBy();
						errordesc = errordescs.get(0) ;
						if (errordescs.size()>1 ) {
							ErrorDescMaster lastRecord = errordescs.get(1);
							lastRecord.setEffectiveDateEnd(oldEndDate);
							repo.saveAndFlush(lastRecord);
						}
					
				    }
				}
				
				res.setResponse("Updated Successfully ");
				res.setSuccessId(errorCode);
				
			}
			
			dozerMapper.map(req, errordesc);
			errordesc.setErrorCode(errorCode);
			errordesc.setEntryDate(entryDate);
			errordesc.setCompanyId(req.getInsuranceId());
			errordesc.setBranchCode(StringUtils.isBlank(req.getBranchCode()) ? "99999" : req.getBranchCode());
			errordesc.setEffectiveDateStart(startDate);
			errordesc.setEffectiveDateEnd(endDate);
			errordesc.setStatus(req.getStatus());
			errordesc.setRemarks(req.getRemarks());
			errordesc.setProductId(Integer.valueOf(req.getProductId()));
			errordesc.setErrorDesc(req.getErrorDesc());
			errordesc.setAmendId(amendId);
			errordesc.setCreatedBy(createdBy);
			errordesc.setUpdatedBy(req.getCreatedBy());
			errordesc.setUpdatedDate(new Date());
			String moduleName =  getListItem (req.getInsuranceId() , "99999" ,"ERROR_MODULES",req.getModuleId() );  
			errordesc.setModuleName(moduleName);
			
			errordesc.setLanguage(req.getLanguage()==null?"":req.getLanguage());
			errordesc.setLocalLangErrorField(req.getLocalLanguageErrField()==null?"":req.getLocalLanguageErrField());
			errordesc.setLocalLanguageDesc(req.getLocalLanguageErrDesc()==null?"":req.getLocalLanguageErrDesc());
			
			repo.saveAndFlush(errordesc);
		
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return res;
	}

	public synchronized String getListItem(String insuranceId , String branchCode, String itemType, String itemCode) {
		String itemDesc = "" ;
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
			Predicate b3 = cb.equal(c.get("branchCode"),ocpm1.get("branchCode"));
			Predicate b4 = cb.equal(c.get("companyId"),ocpm1.get("companyId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1,a2,b3,b4);
			// Effective Date End Max Filter
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<ListItemValue> ocpm2 = effectiveDate2.from(ListItemValue.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate a3 = cb.equal(c.get("itemId"),ocpm2.get("itemId"));
			Predicate b1 = cb.equal(c.get("branchCode"),ocpm2.get("branchCode"));
			Predicate b2 = cb.equal(c.get("companyId"),ocpm2.get("companyId"));
			Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			effectiveDate2.where(a3,a4,b1,b2);
						
			// Where
			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n12 = cb.equal(c.get("status"),"R");
			Predicate n13 = cb.or(n1,n12);
			Predicate n2 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);	
			Predicate n4 = cb.equal(c.get("companyId"), insuranceId);
			Predicate n5 = cb.equal(c.get("companyId"), "99999");
			Predicate n6 = cb.equal(c.get("branchCode"), branchCode);
			Predicate n7 = cb.equal(c.get("branchCode"), "99999");
			Predicate n8 = cb.or(n4,n5);
			Predicate n9 = cb.or(n6,n7);
			Predicate n10 = cb.equal(c.get("itemType"),itemType );
			Predicate n11 = cb.equal(c.get("itemCode"), itemCode);
			
			query.where(n13,n2,n3,n9,n10,n8,n11).orderBy(orderList);
			
		
			// Get Result
			TypedQuery<ListItemValue> result = em.createQuery(query);
			list = result.getResultList();
			
			itemDesc = list.size() > 0 ? list.get(0).getItemValue() : "" ; 
		} catch (Exception e) {
			e.printStackTrace();
			//log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return itemDesc ;
	}
	
	@Override
	public List<ErrorDescMasterRes> getallErrorDetails(ErrorMasterGetAllReq req) {
		
		
		List<ErrorDescMasterRes> resList = new ArrayList<ErrorDescMasterRes>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		
		try {
			List<ErrorDescMaster> list = new ArrayList<ErrorDescMaster>();

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ErrorDescMaster> query = cb.createQuery(ErrorDescMaster.class);
			
			Root<ErrorDescMaster> b = query.from(ErrorDescMaster.class);
		
			
			query.select(b);
		
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<ErrorDescMaster> ocpm1 = amendId.from(ErrorDescMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("errorCode"), b.get("errorCode"));
			Predicate a3 = cb.equal(ocpm1.get("productId"),b.get("productId"));
			Predicate a4 = cb.equal(ocpm1.get("companyId"),b.get("companyId"));
			Predicate a5 = cb.equal(ocpm1.get("moduleId"), b.get("moduleId"));
			Predicate a6 = cb.equal(ocpm1.get("branchCode"), b.get("branchCode"));
			amendId.where(a1,a3,a4,a5,a6);

			
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(b.get("errorCode")));
			
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n3 = cb.equal(b.get("productId"), req.getProductId());
			Predicate n4 = cb.equal(b.get("companyId"), req.getInsuranceId());
			Predicate n5 = cb.equal(b.get("moduleId"), req.getModuleId());
			Predicate n6 = cb.equal(b.get("branchCode"), StringUtils.isBlank(req.getBranchCode()) ? "99999" : req.getBranchCode());
			query.where(n1,n3,n4,n5,n6).orderBy(orderList);

			TypedQuery<ErrorDescMaster> result = em.createQuery(query);
			list = result.getResultList();
			
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getErrorCode()))).collect(Collectors.toList());
		//	list.sort(Comparator.comparing(ErrorDescMaster :: getErrorCode ));
	
			for (ErrorDescMaster data : list) {
				ErrorDescMasterRes res = new ErrorDescMasterRes();

				res = dozerMapper.map(data, ErrorDescMasterRes.class);
				res.setErrorCode(data.getErrorCode().toString());
				resList.add(res);
			}
			
			

		} catch (Exception e) {
			e.printStackTrace();
			//log.info(e.getMessage());
			return null;

		}
		return resList;
	}
	
	private static <T> java.util.function.Predicate<T> distinctByKey(java.util.function.Function<? super T, ?> keyExtractor) {
	    Map<Object, Boolean> seen = new ConcurrentHashMap<>();
	    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

	@Override
	public ErrorDescMasterRes getbyerrorcodeDetails(ErrorDescMasterGetReq req) {
		//List<ErrorDescMasterRes> resList = new ArrayList<ErrorDescMasterRes>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		
		ErrorDescMasterRes res = new ErrorDescMasterRes();
		
		try {
			List<ErrorDescMaster> list = new ArrayList<ErrorDescMaster>();

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ErrorDescMaster> query = cb.createQuery(ErrorDescMaster.class);
			
			Root<ErrorDescMaster> b = query.from(ErrorDescMaster.class);
		
			
			query.select(b);
		
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<ErrorDescMaster> ocpm1 = amendId.from(ErrorDescMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("errorCode"), b.get("errorCode"));
			Predicate a3 = cb.equal(ocpm1.get("productId"),b.get("productId"));
			Predicate a4 = cb.equal(ocpm1.get("companyId"),b.get("companyId"));
			Predicate a5 = cb.equal(ocpm1.get("moduleId"), b.get("moduleId"));
			Predicate a6 = cb.equal(ocpm1.get("branchCode"), b.get("branchCode"));
			amendId.where(a1,a3,a4,a5,a6);

			
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(b.get("amendId")));
			
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("errorCode"), req.getErrorCode());
			Predicate n3 = cb.equal(b.get("productId"), req.getProductId());
			Predicate n4 = cb.equal(b.get("companyId"), req.getInsuranceId());
			Predicate n5 = cb.equal(b.get("moduleId"), req.getModuleId());
			Predicate n6 = cb.equal(b.get("branchCode"), StringUtils.isBlank(req.getBranchCode()) ? "99999" : req.getBranchCode());
			query.where(n1,n2,n3,n4,n5,n6).orderBy(orderList);
			
			TypedQuery<ErrorDescMaster> result = em.createQuery(query);
			list = result.getResultList();
			
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getErrorCode()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(ErrorDescMaster :: getErrorCode ));
	
			res = dozerMapper.map(list.get(0), ErrorDescMasterRes.class);
			res.setErrorCode(list.get(0).getErrorCode().toString());
			res.setEntryDate(list.get(0).getEntryDate());
			res.setEffectiveDateStart(list.get(0).getEffectiveDateStart());
			res.setEffectiveDateEnd(list.get(0).getEffectiveDateEnd());
			res.setInsuranceId(list.get(0).getCompanyId());

		} catch (Exception e) {
			e.printStackTrace();
			
			return null;

		}
		return res;
	}

}
