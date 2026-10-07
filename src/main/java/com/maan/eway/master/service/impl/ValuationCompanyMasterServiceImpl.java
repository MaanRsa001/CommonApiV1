package com.maan.eway.master.service.impl;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
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

import com.google.gson.Gson;
import com.maan.eway.bean.ValuationCompanyMaster;
import com.maan.eway.common.res.SuccessRes;
import com.maan.eway.master.req.ValuationCompanyChangeStatusReq;
import com.maan.eway.master.req.ValuationCompanyMasterDropdownReq;
import com.maan.eway.master.req.ValuationCompanyMasterGetReq;
import com.maan.eway.master.req.ValuationCompanyMasterGetallReq;
import com.maan.eway.master.req.ValuationCompanyMasterSaveReq;
import com.maan.eway.master.res.ValuationCompanyMasterRes;
import com.maan.eway.master.service.ValuationCompanyMasterService;
import com.maan.eway.repository.ValuartionCompanyMasterRepository;
import com.maan.eway.res.DropDownRes;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

@Service
public class ValuationCompanyMasterServiceImpl implements ValuationCompanyMasterService{
	
	@Autowired
	private ValuartionCompanyMasterRepository valRepo;
	
	@PersistenceContext
	private EntityManager em;
	
	Gson json = new Gson();
	
	private Logger log = LogManager.getLogger(ValuationCompanyMasterServiceImpl.class);

	@Override
	public List<String> validateValuationCompany(ValuationCompanyMasterSaveReq req) {
		//List<Error> errorList = new ArrayList<Error>();
		List<String> errorList = new ArrayList<String>();
		try {
			if(StringUtils.isBlank(req.getValCompanyName())) {
				//errorList.add(new Error("", "ValCompanyName", "Please enter Valuation Company Name"));
				errorList.add("Please enter Valuation Company Name");
			}else if(req.getValCompanyName().length()>500) {
				//errorList.add(new Error("", "ValCompanyName", "Please enter Valuation Company Name 500 charecters"));
				errorList.add("Please enter Valuation Company Name 500 charecters");
			}
			if(StringUtils.isBlank(req.getAuthApi())) {
				//errorList.add(new Error("", "AuthApi", "Please enter Auth Api"));
				errorList.add("Please enter Auth Api");
			}else if(req.getAuthApi().length()>500) {
				//errorList.add(new Error("", "AuthApi", "Please enter VAuth Api 500 charecters"));
				errorList.add("Please enter Auth Api");
			}
			if(StringUtils.isBlank(req.getCompanyId())) {
				//errorList.add(new Error("", "CompanyId", "Please enter Company Id"));
				errorList.add("Please enter Company Id");
			}
			if(StringUtils.isBlank(req.getBranchCode())) {
				//errorList.add(new Error("", "BranchCode", "Please enter Branch Code"));
				errorList.add("Please enter Branch Code");
			}
	//		if(StringUtils.isBlank(req.getValCompanyCode())) {
				//errorList.add(new Error("", "ValCompanyCode", "Please enter Val Company Code"));
	//			errorList.add("Please enter Val Company Code");
	//		}
			if(StringUtils.isBlank(req.getRemarks())) {
				//errorList.add(new Error("", "Remarks", "Please enter the Remarks"));
				errorList.add("Please enter the Remarks");
			}else if(req.getRemarks().length()>200) {
				//errorList.add(new Error("","Remarks", "Please Enter Remarks within 200 Characters"));
				errorList.add("Please Enter Remarks within 200 Characters");
			}
			
			//Date Validation
			
			Calendar cal = new GregorianCalendar();
			Date today = new Date();
			cal.setTime(today);
			cal.add(Calendar.DAY_OF_MONTH,-1);
			today = cal.getTime();
			
			if(req.getEffectiveDateStart()==null || StringUtils.isBlank(req.getEffectiveDateStart().toString())) {
				//errorList.add(new Error("","EffectiveDateStart", "Please Enter Effective Start Date"));
				errorList.add("Please Enter Effective Start Date");
			}else if(req.getEffectiveDateStart().before(today)) {
				//errorList.add(new Error("", "EffectiveDateStart", "Please Enter Effective Date Start as Future Date"));
				errorList.add("Please Enter Effective Date Start as Future Date");
			}
		}
		catch(Exception ex) {
			log.error(ex);
			ex.printStackTrace();
		}
		return errorList;
	}

	@Override
	public SuccessRes saveValuationCompany(ValuationCompanyMasterSaveReq req) {
		
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		SuccessRes res = new SuccessRes();
		ValuationCompanyMaster saveData = new ValuationCompanyMaster();
		List<ValuationCompanyMaster> list  = new ArrayList<ValuationCompanyMaster>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			Integer amendId =0;
			Date startDate = req.getEffectiveDateStart();
			String end = "31/12/2037";
			Date endDate = sdf.parse(end);
			long MILLS_IN_A_DAY = 1000*60*60*24;
			Date oldEndDate = new Date(req.getEffectiveDateStart().getTime()- MILLS_IN_A_DAY);
			Date entryDate = null;
			String createdBy ="";
			Integer valCompanyCode = 0;
			Long sNo = (long) 0;
			
			if(StringUtils.isBlank(req.getSNo())) {
				Integer totalCount = getMasterTableCount(req.getCompanyId(),req.getBranchCode());
				Long sNoCount = valRepo.count();
				valCompanyCode = totalCount+1;
				sNo = sNoCount + 1;
				entryDate = new Date();
				createdBy = req.getCreatedBy();
				res.setResponse("Saved Successfully");
				res.setSuccessId(valCompanyCode.toString());
			}
			else {
				valCompanyCode = Integer.valueOf(req.getSNo());
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<ValuationCompanyMaster> query = cb.createQuery(ValuationCompanyMaster.class);
				
				//FindAll
				Root<ValuationCompanyMaster> b = query.from(ValuationCompanyMaster.class);
				
				//select
				query.select(b);
				
				//orderBy
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(b.get("effectiveDateStart")));
				
				//where
				Predicate n1 = cb.equal(b.get("valCompanyCode"), req.getValCompanyCode());
				Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
				Predicate n3 = cb.equal(b.get("branchCode"), req.getBranchCode());
				
				query.where(n1,n2,n3).orderBy(orderList);
				
				//get Result
				TypedQuery<ValuationCompanyMaster> result = em.createQuery(query);
				int limit=0, offset=2;
				result.setFirstResult(limit * offset);
				result.setMaxResults(offset);
				list = result.getResultList();
				
				if(list.size() > 0) {
					Date beforeOneDay = new Date(new Date().getTime()-MILLS_IN_A_DAY);
					if(list.get(0).getEffectiveDateStart().before(beforeOneDay)) {
						amendId = list.get(0).getAmendId()+1;
						entryDate = new Date();
						createdBy = req.getCreatedBy();
						ValuationCompanyMaster lastRecord = list.get(0);
						lastRecord.setEffectiveDateEnd(oldEndDate);
						valRepo.saveAndFlush(lastRecord);
					}
					else {
						amendId = list.get(0).getAmendId();
						createdBy = list.get(0).getCreatedBy();
						entryDate = list.get(0).getEntryDate();
						saveData = list.get(0);
						if(list.size() > 1) {
							ValuationCompanyMaster lastRecord = list.get(1);
							lastRecord.setEffectiveDateEnd(oldEndDate);
							valRepo.saveAndFlush(lastRecord);
						}
					}
				}
				res.setResponse("Updated Successfully");
				res.setSuccessId(valCompanyCode.toString());
			}
			
			dozerMapper.map(req, saveData);
			
			saveData.setSNo(sNo);
			saveData.setValCompanyCode(valCompanyCode.toString());
			saveData.setEffectiveDateStart(startDate);
			saveData.setEffectiveDateEnd(endDate);
			saveData.setCreatedBy(createdBy);
			saveData.setEntryDate(entryDate);
			saveData.setUpdatedBy(req.getCreatedBy());
			saveData.setUpdatedDate(new Date());
			saveData.setAmendId(amendId);
			saveData.setBranchCode(req.getBranchCode()==null?"99999":req.getBranchCode());
			valRepo.saveAndFlush(saveData);
			log.info("Saved Details is --> " + json.toJson(saveData));
		}
		catch(Exception ex) {
			ex.printStackTrace();
			log.info("Exception is --> " + ex.getMessage());
			return null;
		}
		return res;
	}

	private Integer getMasterTableCount(String companyId, String branchCode) {
		
		Integer data = 0;
		try {
			List<ValuationCompanyMaster> list = new ArrayList<ValuationCompanyMaster>();
			
			//Find Last Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ValuationCompanyMaster> query = cb.createQuery(ValuationCompanyMaster.class);
			
			//Find All
			Root<ValuationCompanyMaster> b = query.from(ValuationCompanyMaster.class);
			
			//Select
			query.select(b);
			
			//Effective Date Maximum Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<ValuationCompanyMaster> ocpm1 = effectiveDate.from(ValuationCompanyMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			//Predicate a1 = cb.equal(ocpm1.get("valCompanyCode"), b.get("valCompanyCode"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("branchCode"), b.get("branchCode"));
			
			effectiveDate.where(a2,a3);
			
			//Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(b.get("valCompanyCode")));
			
			Predicate n1 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
			Predicate n2 = cb.equal(b.get("companyId"), companyId);
			
			query.where(n1,n2).orderBy(orderList);
			
			//Get Result
			TypedQuery<ValuationCompanyMaster> result = em.createQuery(query);
			int limit = 0, offset = 1;
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			list = result.getResultList();
			data = list.size() > 0 ? Integer.valueOf(list.get(0).getValCompanyCode()) : 0 ;
		}
		catch(Exception ex) {
			ex.printStackTrace();
			log.info(ex.getMessage());
		}
		return data;
	}

	@Override
	public List<ValuationCompanyMasterRes> getActiveValuationCompany(ValuationCompanyMasterGetallReq req) {
		
		List<ValuationCompanyMasterRes> resList = new ArrayList<ValuationCompanyMasterRes>();
		DozerBeanMapper mapper = new DozerBeanMapper();
		
		try {
			List<ValuationCompanyMaster> list = new ArrayList<ValuationCompanyMaster>();
			
			//Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ValuationCompanyMaster> query = cb.createQuery(ValuationCompanyMaster.class);
			
			//Find All
			Root<ValuationCompanyMaster> b = query.from(ValuationCompanyMaster.class);
			
			//select
			query.select(b);
			
			//AmendId max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<ValuationCompanyMaster> ocpm1 = amendId.from(ValuationCompanyMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("valCompanyCode"), b.get("valCompanyCode"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("branchCode") , b.get("branchCode"));
			
			amendId.where(a1,a2,a3);
			
			//OrderBy
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("branchCode")));
			
			//Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
			Predicate n3 = cb.equal(b.get("branchCode"), req.getBranchCode());
			Predicate n4 = cb.equal(b.get("status"), "Y");
			Predicate n5 = cb.equal(b.get("branchCode"), "99999");
			Predicate n6 = cb.or(n3,n5);
			
			query.where(n1,n2,n4,n6).orderBy(orderList);
			
			//Get Result
			TypedQuery<ValuationCompanyMaster> result = em.createQuery(query);
			list = result.getResultList();
			list = list.stream().filter(distinctKey(o -> Arrays.asList(o.getValCompanyCode()))).collect(Collectors.toList());
			
			//Map
			for(ValuationCompanyMaster data : list) {
				ValuationCompanyMasterRes res = new ValuationCompanyMasterRes();
				
				res = mapper.map(data, ValuationCompanyMasterRes.class);
				resList.add(res);
			}
		}
		catch(Exception ex) {
			ex.printStackTrace();
			log.info("Exception is ---> " + ex.getMessage());
			return null;	
		}
		
		return resList;
	}

	private static<T> java.util.function.Predicate<T> distinctKey(java.util.function.Function<? super T, ?> keyExtractor) {
		Map<Object, Boolean> seen = new ConcurrentHashMap<>();
	    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

	@Override
	public ValuationCompanyMasterRes getByValuationCompanyCode(ValuationCompanyMasterGetReq req) {
		
		ValuationCompanyMasterRes res = new ValuationCompanyMasterRes();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			
			List<ValuationCompanyMaster> list = new ArrayList<ValuationCompanyMaster>();
			
			//Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ValuationCompanyMaster> query = cb.createQuery(ValuationCompanyMaster.class);
			
			//Find All
			Root<ValuationCompanyMaster> b = query.from(ValuationCompanyMaster.class);
			
			//Select
			query.select(b);
			
			//AmendId Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<ValuationCompanyMaster> ocpm1 = amendId.from(ValuationCompanyMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("valCompanyCode"), b.get("valCompanyCode"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("branchCode"), b.get("branchCode"));
			
			amendId.where(a1,a2,a3);
			
			//OrderBy
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("branchCode")));
			
			//where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
			Predicate n3 = cb.equal(b.get("valCompanyCode"), req.getValCompanyCode());
			Predicate n4 = cb.equal(b.get("branchCode"), req.getBranchCode());
			
			query.where(n1,n2,n3,n4).orderBy(orderList);
			
			//Get Result
			TypedQuery<ValuationCompanyMaster> result = em.createQuery(query);
			list = result.getResultList();
			list = list.stream().filter(distinctKey(o -> Arrays.asList(o.getValCompanyCode()))).collect(Collectors.toList());
			
			res = mapper.map(list.get(0), ValuationCompanyMasterRes.class);
			res.setValCompanyCode(list.get(0).getValCompanyCode());
			res.setEntryDate(list.get(0).getEntryDate());
			res.setEffectiveDateStart(list.get(0).getEffectiveDateStart());
			res.setEffectiveDateEnd(list.get(0).getEffectiveDateEnd());	
		}
		catch(Exception ex) {
			ex.printStackTrace();
			log.info("Exception is ---> " + ex.getMessage());
		}
		
		return res;
	}

	@Override
	public SuccessRes changeStatusOfCompany(ValuationCompanyChangeStatusReq req) {

		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		SuccessRes res = new SuccessRes();
		ValuationCompanyMaster saveData = new ValuationCompanyMaster();
		
		List<ValuationCompanyMaster> list = new ArrayList<ValuationCompanyMaster>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			Integer amendId =0;
			Date startDate = req.getEffectiveDateStart();
			String end = "31/12/2037";
			Date endDate = sdf.parse(end);
			long MILLS_IN_A_DAY = 1000*60*60*24;
			Date oldEndDate = new Date(req.getEffectiveDateStart().getTime()- MILLS_IN_A_DAY);
			Date entryDate = null;
			String createdBy ="";
			Integer valCompanyCode = 0;
			
			valCompanyCode = Integer.valueOf(req.getValCompanyCode());
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ValuationCompanyMaster> query = cb.createQuery(ValuationCompanyMaster.class);
			
			//Find All
			Root<ValuationCompanyMaster> b = query.from(ValuationCompanyMaster.class);
			
			//Select
			query.select(b);
			
			//order by
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(b.get("effectiveDateStart")));
			
			//where
			Predicate n1 = cb.equal(b.get("valCompanyCode"), req.getValCompanyCode());
			Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
			Predicate n3 = cb.equal(b.get("branchCode"), req.getBranchCode());
			
			query.where(n1,n2,n3).orderBy(orderList);
			
			//GetResult
			TypedQuery<ValuationCompanyMaster> result = em.createQuery(query);
			int limit = 0, offset = 2;
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			list = result.getResultList();
			
			if(req.getBranchCode().equalsIgnoreCase(list.get(0).getBranchCode()) && list.size() > 0) {
				Date beforeOneDay = new Date(new Date().getTime() - MILLS_IN_A_DAY);
				if(list.get(0).getEffectiveDateStart().before(beforeOneDay)) {
					amendId = list.get(0).getAmendId()+1;
					entryDate = new Date();
					createdBy = req.getCreatedBy();
					ValuationCompanyMaster listRecord = list.get(0);
					listRecord.setEffectiveDateEnd(oldEndDate);
					valRepo.saveAndFlush(listRecord);
				}
				else {
					amendId = list.get(0).getAmendId();
					entryDate = list.get(0).getEntryDate();
					createdBy = list.get(0).getCreatedBy();
					saveData = list.get(0);
					if(req.getBranchCode().equalsIgnoreCase(list.get(0).getBranchCode()) && list.size() > 1) {
						ValuationCompanyMaster listRecord = list.get(1);
						listRecord.setEffectiveDateEnd(oldEndDate);
						valRepo.saveAndFlush(listRecord);
					}
				}
			}
			res.setResponse("Updated Successfully");
			res.setSuccessId(valCompanyCode.toString());
			
			dozerMapper.map(list.get(0), saveData);
			
			saveData.setAmendId(amendId);
			saveData.setEffectiveDateStart(startDate);
			saveData.setEffectiveDateEnd(oldEndDate);
			saveData.setCreatedBy(createdBy);
			saveData.setEntryDate(entryDate);
			saveData.setUpdatedBy(req.getCreatedBy());
			saveData.setUpdatedDate(new Date());
			saveData.setStatus(req.getStatus());
			saveData.setValCompanyCode(valCompanyCode.toString());
			saveData.setCompanyId(list.get(0).getCompanyId());
			saveData.setBranchCode(req.getBranchCode() == null ? "99999" : req.getBranchCode());
			
			valRepo.saveAndFlush(saveData);
			log.info("Saved Details is --> " + json.toJson(saveData));
			res.setResponse("Status Changed");
			res.setSuccessId(req.getValCompanyCode());
		}
		catch(Exception ex) {
			ex.printStackTrace();
			log.info("Exception is --> " + ex.getMessage());
			return null;
		}
		return res;
	}

	@Override
	public List<DropDownRes> getValuationCompanyMasterDropdown(ValuationCompanyMasterDropdownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		
		try {
			
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			today = cal.getTime();
			Date todayEnd = cal.getTime();
			
			//Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ValuationCompanyMaster> query = cb.createQuery(ValuationCompanyMaster.class);
			List<ValuationCompanyMaster> list = new ArrayList<ValuationCompanyMaster>();
			
			//Find All
			Root<ValuationCompanyMaster> b = query.from(ValuationCompanyMaster.class);
			
			//select
			query.select(b);
			
			//Order by
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("valCompanyCode")));
			
			//Effective Date start max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<ValuationCompanyMaster> ocpm1 = effectiveDate.from(ValuationCompanyMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(b.get("valCompanyCode"), ocpm1.get("valCompanyCode"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a3 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a4 = cb.equal(ocpm1.get("branchCode"), b.get("branchCode"));
			
			effectiveDate.where(a1,a2,a3,a4);
			
			//Effective Date end max Filter
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<ValuationCompanyMaster> ocpm2 = effectiveDate2.from(ValuationCompanyMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate c1 = cb.equal(b.get("valCompanyCode"), ocpm2.get("valCompanyCode"));
			Predicate c2 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			Predicate c3 = cb.equal(ocpm2.get("companyId"), b.get("companyId"));
			Predicate c4 = cb.equal(ocpm2.get("branchCode"), b.get("branchCode"));
			
			effectiveDate2.where(c1,c2,c3,c4);
			
			//where
			Predicate n1 = cb.equal(b.get("status"), "Y");
			Predicate n2 = cb.equal(b.get("status"), "R");
			Predicate n3 = cb.or(n1,n2);
			Predicate n4 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
			Predicate n5 = cb.equal(b.get("effectiveDateEnd"), effectiveDate2);
			Predicate n6 = cb.equal(b.get("companyId"), req.getCompanyId());
			Predicate n7 = cb.equal(b.get("branchCode"), req.getBranchCode());
			Predicate n8 = cb.equal(b.get("branchCode"), "99999");
			Predicate n9 = cb.or(n7,n8);
			
			query.where(n3,n4,n5,n6,n9).orderBy(orderList);
			
			//GetResult
			TypedQuery<ValuationCompanyMaster> result = em.createQuery(query);
			list = result.getResultList();
			
			//Response
			for(ValuationCompanyMaster data : list) {
				DropDownRes res = new DropDownRes();
				res.setCode(data.getValCompanyCode());
				res.setCodeDesc(data.getValCompanyName());
				res.setStatus(data.getStatus());
				resList.add(res);
			} 			
		}
		catch(Exception ex) {
			ex.printStackTrace();
			log.info("Exception is --->"+ex.getMessage());
			return null;
		}
		return resList;
	}

	

	

}
