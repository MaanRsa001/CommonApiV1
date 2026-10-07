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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.maan.eway.bean.WarRateMaster;
import com.maan.eway.error.Error;
import com.maan.eway.master.req.NonSelectedClausesGetAllReq;
import com.maan.eway.master.req.WarRateMasterGetReq;
import com.maan.eway.master.req.WarRateMasterGetallReq;
import com.maan.eway.master.req.WarRateMasterReq;
import com.maan.eway.master.req.WarRateMasterSaveReq;
import com.maan.eway.master.req.WarrateChangeStatusReq;
import com.maan.eway.master.req.WarrateMasterDropdownReq;
import com.maan.eway.master.res.WarRateMasterRes;
import com.maan.eway.master.service.WarRateMasterService;
import com.maan.eway.repository.WarRateMasterRepository;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;
@Service
public class WarRateMasterServiceImpl implements WarRateMasterService {

	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private WarRateMasterRepository repo;

	Gson json = new Gson();
	
	private Logger log = LogManager.getLogger(WarRateMasterServiceImpl.class);
	
	public List<WarRateMaster> getWarRateDescriptionExistDetails(String WarrateDescription , String InsuranceId , String branchCode, String productId, String sectionId) {
		List<WarRateMaster> list = new ArrayList<WarRateMaster>();
		try {
			Date today = new Date();
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<WarRateMaster> query = cb.createQuery(WarRateMaster.class);

			// Find All
			Root<WarRateMaster> b = query.from(WarRateMaster.class);

			// Select
			query.select(b);

			// Effective Date Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<WarRateMaster> ocpm1 = amendId.from(WarRateMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("warRateId"), b.get("warRateId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("branchCode"), b.get("branchCode"));
			Predicate a6 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			Predicate a7 = cb.equal(ocpm1.get("sectionId"), b.get("sectionId"));

			amendId.where(a1,a2,a3,a6,a7);

			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(cb.lower( b.get("warRateDescription")), WarrateDescription.toLowerCase());
			Predicate n3 = cb.equal(b.get("companyId"),InsuranceId);
			Predicate n4 = cb.equal(b.get("branchCode"), branchCode);
			Predicate n7 = cb.equal(b.get("productId"), productId);
			Predicate n10 = cb.equal(b.get("sectionId"),sectionId);
			query.where(n1,n2,n3,n4,n7,n10);
			
			// Get Result
			TypedQuery<WarRateMaster> result = em.createQuery(query);
			list = result.getResultList();		
		
		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());

		}
		return list;
	}
	
public Integer getMasterTableCount(String companyId,  String productId, String sectionId)	{

	Integer data =0;
	try {
		List<WarRateMaster> list = new ArrayList<WarRateMaster>();
		// Find Latest Record
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<WarRateMaster> query = cb.createQuery(WarRateMaster.class);
		//Find all
		Root<WarRateMaster> b = query.from(WarRateMaster.class);
		// Select
		query.select(b);
		// Effective Date Max Filter
		Subquery<Long> amendId = query.subquery(Long.class);
		Root<WarRateMaster> ocpm1 = amendId.from(WarRateMaster.class);
		amendId.select(cb.max(ocpm1.get("amendId")));
		Predicate a1 = cb.equal(ocpm1.get("warRateId"),b.get("warRateId"));
		Predicate a2 = cb.equal(ocpm1.get("companyId"),b.get("companyId"));
		Predicate a3 = cb.equal(ocpm1.get("branchCode"),b.get("branchCode"));
		Predicate a4 = cb.equal(ocpm1.get("productId"), b.get("productId"));
		Predicate a5 = cb.equal(ocpm1.get("sectionId"), b.get("sectionId"));

		amendId.where(a1,a2,a3,a4,a5);
	
		//OrderBy
		List<Order> orderList = new ArrayList<Order>();
		orderList.add(cb.desc(b.get("warRateId")));
		
		Predicate n1 = cb.equal(b.get("amendId"),amendId);
		Predicate n2 = cb.equal(b.get("companyId"),companyId);
		Predicate n7 = cb.equal(b.get("productId"), productId);
		Predicate n10 = cb.equal(b.get("sectionId"),sectionId);
		
		query.where(n1,n2,n7,n10).orderBy(orderList);
		
		
		
		// Get Result
		TypedQuery<WarRateMaster> result = em.createQuery(query);
		int limit = 0 , offset = 1 ;
		result.setFirstResult(limit * offset);
		result.setMaxResults(offset);
		list = result.getResultList();
		data = list.size() > 0 ? list.get(0).getWarRateId() : 0 ;
	}
	catch(Exception e) {
		e.printStackTrace();
		log.info(e.getMessage());
	}
	return data;
}

private static <T> java.util.function.Predicate<T> distinctByKey(java.util.function.Function<? super T, ?> keyExtractor) {
    Map<Object, Boolean> seen = new ConcurrentHashMap<>();
    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
}


@Override
public List<Error> validateWarranty(WarRateMasterSaveReq req) {
	List<Error> errorList = new ArrayList<Error>();

	try {
	
		if (StringUtils.isBlank(req.getWarRateDesc())) {
			errorList.add(new Error("02", "WarRateDesc", "Please Enter WarRateDesc"));
		}else if (req.getWarRateDesc().length() > 100){
			errorList.add(new Error("02","WarRateDesc", "Please Enter WarRateDesc 100 Characters")); 
		}else if (StringUtils.isBlank(req.getWarRateId()) &&  StringUtils.isNotBlank(req.getCompanyId()) && StringUtils.isNotBlank(req.getBranchCode())&& StringUtils.isNotBlank(req.getProductId())&& StringUtils.isNotBlank(req.getSectionId())) {
			List<WarRateMaster> WarrateList = getWarRateDescriptionExistDetails(req.getWarRateDesc() , req.getCompanyId() , req.getBranchCode(),req.getProductId(),req.getSectionId());
			if (WarrateList.size()>0 ) {
				errorList.add(new Error("01", "WarRateDesc", "This WarRateDesc Already Exist "));
			}
		}else if (StringUtils.isNotBlank(req.getWarRateId()) &&  StringUtils.isNotBlank(req.getCompanyId()) && StringUtils.isNotBlank(req.getBranchCode())&& StringUtils.isNotBlank(req.getProductId())&& StringUtils.isNotBlank(req.getSectionId())) {
			List<WarRateMaster> WarrateList = getWarRateDescriptionExistDetails(req.getWarRateDesc() , req.getCompanyId() , req.getBranchCode(),req.getProductId(), req.getSectionId());
			
			if (WarrateList.size()>0 &&  (! req.getWarRateId().equalsIgnoreCase(WarrateList.get(0).getWarRateId().toString())) ) {
				errorList.add(new Error("01", "WarRateDesc", "This WarRateDesc Already Exist "));
			}
			
		}
		
		
		if (StringUtils.isBlank(req.getCompanyId())) {
			errorList.add(new Error("02", "CompanyId", "Please Enter CompanyId"));
		}
		
		if (StringUtils.isBlank(req.getBranchCode())) {
			errorList.add(new Error("02", "BranchCode", "Please Select BranchCode"));
		}
/*		if (StringUtils.isBlank(req.getOccupationNameAr())) {
			errorList.add(new Error("03", "OccupationNameAr", "Please Select OccupationNameAr"));
		}else if (req.getOccupationNameAr().length() > 100){
			errorList.add(new Error("03","OccupationNameAr", "Please Enter OccupationNameAr 100 Characters")); 
		} */
		
		if (StringUtils.isBlank(req.getRemarks())) {
			errorList.add(new Error("04", "Remarks", "Please Select Remarks "));
		}else if (req.getRemarks().length() > 100){
			errorList.add(new Error("04","Remarks", "Please Enter Remarks within 100 Characters")); 
		}
		
		// Date Validation 
		Calendar cal = new GregorianCalendar();
		Date today = new Date();
		cal.setTime(today);cal.add(Calendar.DAY_OF_MONTH, -1);;
		today = cal.getTime();
		if (req.getEffectiveDateStart() == null || StringUtils.isBlank(req.getEffectiveDateStart().toString())) {
			errorList.add(new Error("05", "EffectiveDateStart", "Please Enter Effective Date Start"));

		} else if (req.getEffectiveDateStart().before(today)) {
			errorList.add(new Error("05", "EffectiveDateStart", "Please Enter Effective Date Start as Future Date"));
		}
		//Status Validation
		if (StringUtils.isBlank(req.getStatus())) {
			errorList.add(new Error("05", "Status", "Please Select Status  "));
		} else if (req.getStatus().length() > 1) {
			errorList.add(new Error("05", "Status", "Please Select Valid Status - One Character Only Allwed"));
		}else if(!("Y".equalsIgnoreCase(req.getStatus())||"N".equalsIgnoreCase(req.getStatus())||"R".equalsIgnoreCase(req.getStatus())|| "P".equalsIgnoreCase(req.getStatus()))) {
			errorList.add(new Error("05", "Status", "Please Select Valid Status - Active or Deactive or Pending or Referral "));
		}

		if (StringUtils.isBlank(req.getCoreAppCode())) {
			errorList.add(new Error("07", "CoreAppCode", "Please Enter CoreAppCode"));
		}else if (req.getCoreAppCode().length() > 20){
			errorList.add(new Error("07","CoreAppCode", "Please Enter CoreAppCode within 20 Characters")); 
		}
		if (StringUtils.isBlank(req.getRegulatoryCode())) {
			errorList.add(new Error("08", "RegulatoryCode", "Please Enter RegulatoryCode"));
		}else if (req.getRegulatoryCode().length() > 20){
			errorList.add(new Error("08","RegulatoryCode", "Please Enter RegulatoryCode within 20 Characters")); 
		}
		if (StringUtils.isBlank(req.getCreatedBy())) {
			errorList.add(new Error("09", "CreatedBy", "Please Enter CreatedBy"));
		}else if (req.getCreatedBy().length() > 100){
			errorList.add(new Error("09","CreatedBy", "Please Enter CreatedBy within 100 Characters")); 
		}	
		if (StringUtils.isBlank(req.getWarRate())) {
			errorList.add(new Error("10", "WarRate", "Please Enter Warrate"));
		}
		if (StringUtils.isBlank(req.getProductId())) {
			errorList.add(new Error("11", "ProductId", "Please Enter ProductId"));
		}
		
		
		
	} catch (Exception e) {
		log.error(e);
		e.printStackTrace();
	}
	return errorList;
}
@Override
public SuccessRes saveWarRate(WarRateMasterSaveReq req) {
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	SuccessRes res = new SuccessRes();
	WarRateMaster saveData = new WarRateMaster();
	List<WarRateMaster> list  = new ArrayList<WarRateMaster>();
	DozerBeanMapper dozerMapper = new DozerBeanMapper();
	try {
		
		
		Integer amendId = 0;
		Date StartDate = req.getEffectiveDateStart();
		String end = "31/12/2050";
		Date endDate = sdf.parse(end);
		long MILLS_IN_A_DAY = 1000*60*60*24;
		Date oldEndDate = new Date(req.getEffectiveDateStart().getTime()- MILLS_IN_A_DAY);
		Date entryDate = null;
		String createdBy ="";
		Integer warrateId = 0;
		if(StringUtils.isBlank(req.getWarRateId())) {
			Integer totalCount = getMasterTableCount(req.getCompanyId(),req.getProductId(),req.getSectionId());
			warrateId = totalCount+1;
			entryDate = new Date();
			createdBy = req.getCreatedBy();
			res.setResponse("Saved Successfully");
			res.setSuccessId(warrateId.toString());
		}
		else {
			warrateId = Integer.valueOf(req.getWarRateId());
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<WarRateMaster> query = cb.createQuery(WarRateMaster.class);
			//Findall
			Root<WarRateMaster> b = query.from(WarRateMaster.class);
			//select
			query.select(b);
			//Orderby
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(b.get("effectiveDateStart")));
			//Where
			Predicate n1 = cb.equal(b.get("warRateId"),req.getWarRateId());
			Predicate n2 = cb.equal(b.get("companyId"),req.getCompanyId());
			Predicate n3 = cb.equal(b.get("branchCode"),req.getBranchCode());
			Predicate n4 = cb.equal(b.get("productId"),req.getProductId());
			Predicate n5 = cb.equal(b.get("sectionId"),req.getSectionId());
			
			query.where(n1,n2,n3,n4,n5).orderBy(orderList);
			
			// Get Result
			TypedQuery<WarRateMaster> result = em.createQuery(query);
			int limit=0, offset=2;
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			list = result.getResultList();
			if(list.size()>0) {
				Date beforeOneDay = new Date(new Date().getTime()- MILLS_IN_A_DAY);
				if(list.get(0).getEffectiveDateStart().before(beforeOneDay)) {
					amendId = list.get(0).getAmendId()+1;
					entryDate = new Date();
					createdBy = req.getCreatedBy();
					WarRateMaster lastRecord = list.get(0);
					lastRecord.setEffectiveDateEnd(oldEndDate);
					repo.saveAndFlush(lastRecord);
				}
				else {
					amendId = list.get(0).getAmendId();
					entryDate = list.get(0).getEntryDate();
					createdBy = list.get(0).getCreatedBy();
					saveData = list.get(0);
					if(list.size()>1) {
						WarRateMaster lastRecord = list.get(1);	
						lastRecord.setEffectiveDateEnd(oldEndDate);
						repo.saveAndFlush(lastRecord);
					}
				}
			}
			res.setResponse("Updated Successfully");
			res.setSuccessId(warrateId.toString());
		}
		dozerMapper.map(req, saveData);
		saveData.setWarRateId(warrateId);
		saveData.setEffectiveDateStart(StartDate);
		saveData.setEffectiveDateEnd(endDate);
		saveData.setCreatedBy(createdBy);
		saveData.setEntryDate(entryDate);
		saveData.setUpdatedBy(req.getCreatedBy());
		saveData.setUpdatedDate(new Date());
		saveData.setAmendId(amendId);
		saveData.setProductId(req.getProductId());
		repo.saveAndFlush(saveData);	
		log.info("Saved Details is --> " + json.toJson(saveData));	
		
		
	}
	catch(Exception e) {
		e.printStackTrace();
		log.info("Exception is --> " + e.getMessage());
		return null;
	}
	return res;
	
	
}
@Override
public List<WarRateMasterRes> getallWarRate(WarRateMasterGetallReq req) {
	List<WarRateMasterRes> resList = new ArrayList<WarRateMasterRes>();
	DozerBeanMapper mapper = new DozerBeanMapper();
	try {
		List<WarRateMaster> list = new ArrayList<WarRateMaster>();
	
		// Find Latest Record
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<WarRateMaster> query = cb.createQuery(WarRateMaster.class);

		// Find All
		Root<WarRateMaster> b = query.from(WarRateMaster.class);

		// Select
		query.select(b);

		// Amend ID Max Filter
		Subquery<Long> amendId = query.subquery(Long.class);
		Root<WarRateMaster> ocpm1 = amendId.from(WarRateMaster.class);
		amendId.select(cb.max(ocpm1.get("amendId")));
		Predicate a1 = cb.equal(ocpm1.get("warRateId"), b.get("warRateId"));
		Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
		Predicate a3 = cb.equal(ocpm1.get("branchCode"),b.get("branchCode"));
		Predicate a4 = cb.equal(ocpm1.get("productId"),b.get("productId"));
		Predicate a5 = cb.equal(ocpm1.get("sectionId"),b.get("sectionId"));
//		Predicate a6 = cb.equal(ocpm1.get("policyType"),b.get("policyType"));

		amendId.where(a1, a2,a3,a4,a5);

		// Order By
		List<Order> orderList = new ArrayList<Order>();
		orderList.add(cb.asc(b.get("branchCode")));

		// Where
		Predicate n1 = cb.equal(b.get("amendId"), amendId);
		Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
		Predicate n3 = cb.equal(b.get("branchCode"), req.getBranchCode());
		Predicate n6 = cb.equal(b.get("productId"), req.getProductId());
		Predicate n9 = cb.equal(b.get("sectionId"), req.getSectionId());
		query.where(n1,n2,n3,n6,n9).orderBy(orderList);
		
		// Get Result
		TypedQuery<WarRateMaster> result = em.createQuery(query);
		list = result.getResultList();
		list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getWarRateId()))).collect(Collectors.toList());
		list.sort(Comparator.comparing(WarRateMaster :: getWarRateDesc));
		
		// Map
		for (WarRateMaster data : list) {
			WarRateMasterRes res = new WarRateMasterRes();

			res = mapper.map(data, WarRateMasterRes.class);
			res.setCoreAppCode(data.getCoreAppCode());

			resList.add(res);
		}

	} catch (Exception e) {
		e.printStackTrace();
		log.info(e.getMessage());
		return null;

	}
	return resList;
}
@Override
public List<WarRateMasterRes> getActiveWarrate(WarRateMasterGetallReq req) {
	List<WarRateMasterRes> resList = new ArrayList<WarRateMasterRes>();
	DozerBeanMapper mapper = new DozerBeanMapper();
	try {
		List<WarRateMaster> list = new ArrayList<WarRateMaster>();
	
		// Find Latest Record
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<WarRateMaster> query = cb.createQuery(WarRateMaster.class);

		// Find All
		Root<WarRateMaster> b = query.from(WarRateMaster.class);

		// Select
		query.select(b);

		// Amend ID Max Filter
		Subquery<Long> amendId = query.subquery(Long.class);
		Root<WarRateMaster> ocpm1 = amendId.from(WarRateMaster.class);
		amendId.select(cb.max(ocpm1.get("amendId")));
		Predicate a1 = cb.equal(ocpm1.get("warRateId"), b.get("warRateId"));
		Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
		Predicate a3 = cb.equal(ocpm1.get("branchCode"),b.get("branchCode"));
		Predicate a4 = cb.equal(ocpm1.get("productId"),b.get("productId"));
		Predicate a5 = cb.equal(ocpm1.get("sectionId"),b.get("sectionId"));

		amendId.where(a1, a2,a3,a4,a5);

		// Order By
		List<Order> orderList = new ArrayList<Order>();
		orderList.add(cb.asc(b.get("branchCode")));

		// Where
		// Where
		Predicate n1 = cb.equal(b.get("amendId"), amendId);
		Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
		Predicate n3 = cb.equal(b.get("branchCode"), req.getBranchCode());
		Predicate n4 = cb.equal(b.get("status"), "Y");
		Predicate n7 = cb.equal(b.get("productId"), req.getProductId());
		Predicate n10 = cb.equal(b.get("sectionId"), req.getSectionId());
		
		query.where(n1,n2,n4,n3,n4,n7,n10).orderBy(orderList);

		// Get Result
		TypedQuery<WarRateMaster> result = em.createQuery(query);
		list = result.getResultList();
		list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getWarRateId()))).collect(Collectors.toList());
		list.sort(Comparator.comparing(WarRateMaster :: getWarRateDesc));
		
		// Map
		for (WarRateMaster data : list) {
			WarRateMasterRes res = new WarRateMasterRes();

			res = mapper.map(data, WarRateMasterRes.class);
			res.setCoreAppCode(data.getCoreAppCode());

			resList.add(res);
		}

	} catch (Exception e) {
		e.printStackTrace();
		log.info(e.getMessage());
		return null;

	}
	return resList;
}
@Override
public WarRateMasterRes getByWarrateId(WarRateMasterGetReq req) {
	WarRateMasterRes res = new WarRateMasterRes();
	DozerBeanMapper mapper = new DozerBeanMapper();
	try {
		Date today = new Date();
		Calendar cal = new GregorianCalendar();
		cal.setTime(today);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 1);
		today = cal.getTime();

		List<WarRateMaster> list = new ArrayList<WarRateMaster>();
	
		// Find Latest Record
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<WarRateMaster> query = cb.createQuery(WarRateMaster.class);

		// Find All
		Root<WarRateMaster> b = query.from(WarRateMaster.class);

		// Select
		query.select(b);

		// Amend ID Max Filter
		Subquery<Long> amendId = query.subquery(Long.class);
		Root<WarRateMaster> ocpm1 = amendId.from(WarRateMaster.class);
		amendId.select(cb.max(ocpm1.get("amendId")));
		Predicate a1 = cb.equal(ocpm1.get("warRateId"), b.get("warRateId"));
		Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
		Predicate a3 = cb.equal(ocpm1.get("branchCode"),b.get("branchCode"));
		Predicate a4 = cb.equal(ocpm1.get("productId"),b.get("productId"));
		Predicate a5 = cb.equal(ocpm1.get("sectionId"),b.get("sectionId"));

		amendId.where(a1, a2,a3,a4,a5);

		// Order By
		List<Order> orderList = new ArrayList<Order>();
		orderList.add(cb.asc(b.get("branchCode")));

		// Where
		Predicate n1 = cb.equal(b.get("amendId"), amendId);
		Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
		Predicate n3 = cb.equal(b.get("branchCode"), req.getBranchCode());
		Predicate n4 = cb.equal(b.get("warRateId"), req.getWarRateId());
		Predicate n8 = cb.equal(b.get("productId"), req.getProductId());
		Predicate n11 = cb.equal(b.get("sectionId"), req.getSectionId());
		
		query.where(n1,n2,n4,n3,n8,n11).orderBy(orderList);
		
		// Get Result
		TypedQuery<WarRateMaster> result = em.createQuery(query);

		list = result.getResultList();
		list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getWarRateId()))).collect(Collectors.toList());
		list.sort(Comparator.comparing(WarRateMaster :: getWarRateDesc ));
		
		res = mapper.map(list.get(0), WarRateMasterRes.class);
		res.setWarRateId(list.get(0).getWarRateId().toString());
		res.setEntryDate(list.get(0).getEntryDate());
		res.setEffectiveDateStart(list.get(0).getEffectiveDateStart());
		res.setEffectiveDateEnd(list.get(0).getEffectiveDateEnd());
		res.setCoreAppCode(list.get(0).getCoreAppCode());
		} catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is ---> " + e.getMessage());
		return null;
	}
	return res;
}
@Override
public SuccessRes changeStatusOfWarrate(WarrateChangeStatusReq req) {
	SuccessRes res = new SuccessRes();
	DozerBeanMapper dozerMapper = new DozerBeanMapper();
	try {
		List<WarRateMaster> list = new ArrayList<WarRateMaster>();
		
		// Find Latest Record
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<WarRateMaster> query = cb.createQuery(WarRateMaster.class);
		// Find all
		Root<WarRateMaster> b = query.from(WarRateMaster.class);
		//Select
		query.select(b);

		// Amend ID Max Filter
		Subquery<Long> amendId = query.subquery(Long.class);
		Root<WarRateMaster> ocpm1 = amendId.from(WarRateMaster.class);
		amendId.select(cb.max(ocpm1.get("amendId")));
		Predicate a1 = cb.equal(ocpm1.get("warRateId"), b.get("warRateId"));
		Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
		Predicate a3 = cb.equal(ocpm1.get("branchCode"),b.get("branchCode"));
		Predicate a4 = cb.equal(ocpm1.get("productId"),b.get("productId"));
		Predicate a5 = cb.equal(ocpm1.get("sectionId"),b.get("sectionId"));

		amendId.where(a1, a2,a3,a4,a5);

		// Order By
		List<Order> orderList = new ArrayList<Order>();
		orderList.add(cb.asc(b.get("branchCode")));

		// Where
				Predicate n1 = cb.equal(b.get("amendId"), amendId);
				Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
				Predicate n3 = cb.equal(b.get("branchCode"), req.getBranchCode());
				Predicate n4 = cb.equal(b.get("warRateId"), req.getWarRateId());
				Predicate n7 = cb.equal(b.get("productId"), req.getProductId());
				Predicate n10 = cb.equal(b.get("sectionId"), req.getSectionId());
				query.where(n1,n2,n4,n3,n7,n10).orderBy(orderList);
		
		// Get Result 
		TypedQuery<WarRateMaster> result = em.createQuery(query);
		list = result.getResultList();
		WarRateMaster updateRecord = list.get(0);
		if(  req.getBranchCode().equalsIgnoreCase(updateRecord.getBranchCode())) {
			updateRecord.setStatus(req.getStatus());
			repo.save(updateRecord);
		} else {
			WarRateMaster saveNew = new WarRateMaster();
			dozerMapper.map(updateRecord,saveNew);
			saveNew.setBranchCode(req.getBranchCode());
			saveNew.setStatus(req.getStatus());
			repo.save(saveNew);
		}
	
		// Perform Update
		res.setResponse("Status Changed");
		res.setSuccessId(req.getWarRateId());
	}
	catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is --> " + e.getMessage());
		return null;
		}
	return res;
}

@Override
public List<DropDownRes> getWarrateMasterDropdown(WarrateMasterDropdownReq req) {
	List<DropDownRes> resList = new ArrayList<DropDownRes>();
	try {
		Date today = new Date();
		Calendar cal = new GregorianCalendar();
		cal.setTime(today);
		today = cal.getTime();
		Date todayEnd = cal.getTime();
		
		// Criteria
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<WarRateMaster> query=  cb.createQuery(WarRateMaster.class);
		List<WarRateMaster> list = new ArrayList<WarRateMaster>();
		// Find All
		Root<WarRateMaster> c = query.from(WarRateMaster.class);
		//Select
		query.select(c);
		// Order By
		List<Order> orderList = new ArrayList<Order>();
		orderList.add(cb.asc(c.get("warRateDesc")));
		
		// Effective Date Start Max Filter
		Subquery<Date> effectiveDate = query.subquery(Date.class);
		Root<WarRateMaster> ocpm1 = effectiveDate.from(WarRateMaster.class);
		effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
		Predicate a1 = cb.equal(c.get("warRateId"),ocpm1.get("warRateId"));
		Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
		Predicate a5 = cb.equal(c.get("companyId"),ocpm1.get("companyId"));
		Predicate a6 = cb.equal(c.get("branchCode"),ocpm1.get("branchCode"));
		Predicate a7 = cb.equal(c.get("productId"),ocpm1.get("productId"));
		Predicate a8 = cb.equal(c.get("sectionId"),ocpm1.get("sectionId"));

		effectiveDate.where(a1,a2,a5,a6,a7,a8);
		// Effective Date End Max Filter
		Subquery<Date> effectiveDate2 = query.subquery(Date.class);
		Root<WarRateMaster> ocpm2 = effectiveDate2.from(WarRateMaster.class);
		effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
		Predicate a3 = cb.equal(c.get("warRateId"),ocpm2.get("warRateId"));
		Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
		Predicate a10 = cb.equal(c.get("companyId"),ocpm2.get("companyId"));
		Predicate a11 = cb.equal(c.get("branchCode"),ocpm2.get("branchCode"));
		Predicate a12 = cb.equal(c.get("productId"),ocpm2.get("productId"));
		Predicate a13 = cb.equal(c.get("sectionId"),ocpm2.get("sectionId"));

		effectiveDate2.where(a3,a4,a10,a11,a12,a13);
		// Where
		Predicate n1 = cb.equal(c.get("status"),"Y");
		Predicate n12 = cb.equal(c.get("status"),"R");
		Predicate n13 = cb.or(n1,n12);
		Predicate n2 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
		Predicate n3 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);	
		Predicate n4 = cb.equal(c.get("companyId"),req.getCompanyId());
		Predicate n5 = cb.equal(c.get("branchCode"),req.getBranchCode());
		Predicate n6 = cb.equal(c.get("branchCode"),"99999");
		Predicate n7 = cb.or(n5,n6);
		
		Predicate n8 = cb.equal(c.get("productId"), req.getProductId());
		Predicate n11 = cb.equal(c.get("sectionId"), req.getSectionId());
		query.where(n13,n2,n3,n4,n7,n8,n11).orderBy(orderList);
		// Get Result
		TypedQuery<WarRateMaster> result = em.createQuery(query);
		list = result.getResultList();
		for (WarRateMaster data : list) {
			// Response 
			DropDownRes res = new DropDownRes();
			res.setCode(data.getWarRateId().toString());
			res.setCodeDesc(data.getWarRateDesc());
			res.setCodeDescLocal(data.getWarRateDescLocal());
			res.setStatus(data.getStatus());
			resList.add(res);
		}
	}
		catch(Exception e) {
			e.printStackTrace();
			log.info("Exception is --->"+e.getMessage());
			return null;
			}
		return resList;
}

//@Override
//public List<Error> validateWarranty(WarRateMasterListSaveReq reqList) {
//	List<Error> errorList = new ArrayList<Error>();
//
//	try {
//
//		for(WarRateMasterReq req : reqList.getWarRateReq()) {
//		
//		if (StringUtils.isBlank(req.getWarRateDesc())) {
//			errorList.add(new Error("02", "WarRateDesc", "Please Enter WarRateDesc"));
//		}else if (req.getWarRateDesc().length() > 100){
//			errorList.add(new Error("02","WarRateDesc", "Please Enter WarRateDesc 100 Characters")); 
//		}else if (StringUtils.isBlank(req.getWarRateId()) &&  StringUtils.isNotBlank(req.getCompanyId()) && StringUtils.isNotBlank(req.getBranchCode())&& StringUtils.isNotBlank(req.getProductId())&& StringUtils.isNotBlank(req.getSectionId())) {
//			List<WarRateMaster> WarrateList = getWarRateDescriptionExistDetails(req.getWarRateDesc() , req.getCompanyId() , req.getBranchCode(),req.getProductId(), req.getSectionId());
//			if (WarrateList.size()>0 ) {
//				errorList.add(new Error("01", "WarRateDesc", "This WarRateDesc Already Exist "));
//			}
//		}else if (StringUtils.isNotBlank(req.getWarRateId()) &&  StringUtils.isNotBlank(req.getCompanyId()) && StringUtils.isNotBlank(req.getBranchCode())&& StringUtils.isNotBlank(req.getProductId())&& StringUtils.isNotBlank(req.getSectionId())) {
//			List<WarRateMaster> WarrateList = getWarRateDescriptionExistDetails(req.getWarRateDesc() , req.getCompanyId() , req.getBranchCode(),req.getProductId(), req.getSectionId());
//			
//			if (WarrateList.size()>0 &&  (! req.getWarRateId().equalsIgnoreCase(WarrateList.get(0).getWarRateId().toString())) ) {
//				errorList.add(new Error("01", "WarRateDesc", "This WarRateDesc Already Exist "));
//			}
//			
//		}
//		
//		
//		if (StringUtils.isBlank(req.getCompanyId())) {
//			errorList.add(new Error("02", "CompanyId", "Please Enter CompanyId"));
//		}
//		
//		if (StringUtils.isBlank(req.getBranchCode())) {
//			errorList.add(new Error("02", "BranchCode", "Please Select BranchCode"));
//		}
//		if (StringUtils.isBlank(req.getWarRate())) {
//			errorList.add(new Error("10", "WarRate", "Please Enter Warrate"));
//		}
//		if (StringUtils.isBlank(req.getProductId())) {
//			errorList.add(new Error("11", "ProductId", "Please Enter ProductId"));
//		}
//		if (StringUtils.isBlank(req.getSectionId())) {
//			errorList.add(new Error("12", "SectionId", "Please Enter SectionId"));
//		}
//				
//		}
//		if (StringUtils.isBlank(reqList.getRemarks())) {
//			errorList.add(new Error("04", "Remarks", "Please Select Remarks "));
//		}else if (reqList.getRemarks().length() > 100){
//			errorList.add(new Error("04","Remarks", "Please Enter Remarks within 100 Characters")); 
//		}
//		
//		// Date Validation 
//		Calendar cal = new GregorianCalendar();
//		Date today = new Date();
//		cal.setTime(today);cal.add(Calendar.DAY_OF_MONTH, -1);;
//		today = cal.getTime();
//		if (reqList.getEffectiveDateStart() == null || StringUtils.isBlank(reqList.getEffectiveDateStart().toString())) {
//			errorList.add(new Error("05", "EffectiveDateStart", "Please Enter Effective Date Start"));
//
//		} else if (reqList.getEffectiveDateStart().before(today)) {
//			errorList.add(new Error("05", "EffectiveDateStart", "Please Enter Effective Date Start as Future Date"));
//		}
//		//Status Validation
//		if (StringUtils.isBlank(reqList.getStatus())) {
//			errorList.add(new Error("06", "Status", "Please Enter Status"));
//		} else if (reqList.getStatus().length() > 1) {
//			errorList.add(new Error("06", "Status", "Enter Status in 1 Character Only"));
//		}else if(!("Y".equals(reqList.getStatus())||"N".equals(reqList.getStatus()) || "R".equals(reqList.getStatus()))) {
//			errorList.add(new Error("06", "Status", "Enter Status in Y or N or R Only"));
//		}
//
//		if (StringUtils.isBlank(reqList.getCoreAppCode())) {
//			errorList.add(new Error("07", "CoreAppCode", "Please Enter CoreAppCode"));
//		}else if (reqList.getCoreAppCode().length() > 20){
//			errorList.add(new Error("07","CoreAppCode", "Please Enter CoreAppCode within 20 Characters")); 
//		}
//		if (StringUtils.isBlank(reqList.getRegulatoryCode())) {
//			errorList.add(new Error("08", "RegulatoryCode", "Please Enter RegulatoryCode"));
//		}else if (reqList.getRegulatoryCode().length() > 20){
//			errorList.add(new Error("08","RegulatoryCode", "Please Enter RegulatoryCode within 20 Characters")); 
//		}
//		if (StringUtils.isBlank(reqList.getCreatedBy())) {
//			errorList.add(new Error("09", "CreatedBy", "Please Enter CreatedBy"));
//		}else if (reqList.getCreatedBy().length() > 100){
//			errorList.add(new Error("09","CreatedBy", "Please Enter CreatedBy within 100 Characters")); 
//		}	
//		if (StringUtils.isBlank(reqList.getDocRefNo())) {
//			errorList.add(new Error("15", "DocRefNo", "Please Enter DocRefNo"));
//		}
//		else if (reqList.getDocRefNo().length() > 50){
//			errorList.add(new Error("15","DocRefNo", "Please Enter DocRefNo within 50 Characters")); 
//		}	
//		
//	} catch (Exception e) {
//		log.error(e);
//		e.printStackTrace();
//	}
//	return errorList;
//}
//
//@Override
//public SuccessRes saveWarRate(WarRateMasterListSaveReq reqList) {
//	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
//	SuccessRes res = new SuccessRes();
//	WarRateMaster saveData = new WarRateMaster();
//	List<WarRateMaster> list  = new ArrayList<WarRateMaster>();
//	DozerBeanMapper dozerMapper = new DozerBeanMapper();
//	try {
//		
//		
//		Integer amendId = 0;
//		Date StartDate = reqList.getEffectiveDateStart();
//		String end = "31/12/2050";
//		Date endDate = sdf.parse(end);
//		Date MILLS_IN_A_DAY = 1000*60*60*24;
//		Date oldEndDate = new Date(reqList.getEffectiveDateStart().getTime()- MILLS_IN_A_DAY);
//		Date entryDate = null;
//		String createdBy ="";
//		createdBy = reqList.getCreatedBy();
//		
//		for(WarRateMasterReq req : reqList.getWarRateReq()) {
//		Integer warrateId = 0;
//		if(StringUtils.isBlank(req.getWarRateId())) {
//			Integer totalCount = getMasterTableCount(req.getCompanyId(),req.getBranchCode(),req.getProductId(),req.getSectionId());
//			warrateId = totalCount+1;
//			entryDate = new Date();
//			res.setResponse("Saved Successfully");
//			res.setSuccessId(warrateId.toString());
//		}
//		else {
//			warrateId = Integer.valueOf(req.getWarRateId());
//			CriteriaBuilder cb = em.getCriteriaBuilder();
//			CriteriaQuery<WarRateMaster> query = cb.createQuery(WarRateMaster.class);
//			//Findall
//			Root<WarRateMaster> b = query.from(WarRateMaster.class);
//			//select
//			query.select(b);
//			//Orderby
//			List<Order> orderList = new ArrayList<Order>();
//			orderList.add(cb.desc(b.get("effectiveDateStart")));
//			//Where
//			Predicate n1 = cb.equal(b.get("warRateId"),req.getWarRateId());
//			Predicate n2 = cb.equal(b.get("companyId"),req.getCompanyId());
//			Predicate n3 = cb.equal(b.get("branchCode"),req.getBranchCode());
//			Predicate n4 = cb.equal(b.get("productId"),req.getProductId());
//			Predicate n5 = cb.equal(b.get("sectionId"),req.getProductId());
//			
//			query.where(n1,n2,n3,n4).orderBy(orderList);
//			
//			// Get Result
//			TypedQuery<WarRateMaster> result = em.createQuery(query);
//			int limit=0, offset=2;
//			result.setFirstResult(limit * offset);
//			result.setMaxResults(offset);
//			list = result.getResultList();
//			if(list.size()>0) {
//				Date beforeOneDay = new Date(new Date().getTime()- MILLS_IN_A_DAY);
//				if(list.get(0).getEffectiveDateStart().before(beforeOneDay)) {
//					amendId = list.get(0).getAmendId()+1;
//					entryDate = new Date();
//					createdBy = reqList.getCreatedBy();
//					WarRateMaster lastRecord = list.get(0);
//					lastRecord.setEffectiveDateEnd(oldEndDate);
//					repo.saveAndFlush(lastRecord);
//				}
//				else {
//					amendId = list.get(0).getAmendId();
//					entryDate = list.get(0).getEntryDate();
//					createdBy = list.get(0).getCreatedBy();
//					saveData = list.get(0);
//					if(list.size()>1) {
//						WarRateMaster lastRecord = list.get(1);	
//						lastRecord.setEffectiveDateEnd(oldEndDate);
//						repo.saveAndFlush(lastRecord);
//					}
//				}
//			}
//			res.setResponse("Updated Successfully");
//			res.setSuccessId(warrateId.toString());
//		}
//		
//		dozerMapper.map(req, saveData);
//		saveData.setWarRateId(warrateId);
//		saveData.setEffectiveDateStart(StartDate);
//		saveData.setEffectiveDateEnd(endDate);
//		saveData.setCreatedBy(createdBy);
//		saveData.setEntryDate(entryDate);
//		saveData.setUpdatedBy(reqList.getCreatedBy());
//		saveData.setUpdatedDate(new Date());
//		saveData.setAmendId(amendId);
//		saveData.setProductId(req.getProductId());
//		saveData.setStatus(reqList.getStatus());
//		saveData.setDocRefNo(reqList.getDocRefNo());
//		saveData.setCoreAppCode(reqList.getCoreAppCode());
//		saveData.setRegulatoryCode(reqList.getRegulatoryCode());
//		repo.saveAndFlush(saveData);	
//		log.info("Saved Details is --> " + json.toJson(saveData));	
//		
//		}
//	}
//	catch(Exception e) {
//		e.printStackTrace();
//		log.info("Exception is --> " + e.getMessage());
//		return null;
//	}
//	return res;
//	
//	
//}

@Override
public List<WarRateMasterRes> getallNonSelectedWarrate(NonSelectedClausesGetAllReq req) {
	List<WarRateMasterRes> resList = new ArrayList<WarRateMasterRes>();
	DozerBeanMapper dozerMapper = new  DozerBeanMapper();
	try {
		Date today  = new Date();
		Calendar cal = new GregorianCalendar();
		cal.setTime(today);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 1);
		today = cal.getTime();
		cal.set(Calendar.HOUR_OF_DAY, 1);
		cal.set(Calendar.MINUTE, 1);
		Date todayEnd = cal.getTime();
		
		List<WarRateMaster> list = new ArrayList<WarRateMaster>();
	
		// Find Latest Record
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<WarRateMaster> query = cb.createQuery(WarRateMaster.class);

		// Find All
		Root<WarRateMaster> b = query.from(WarRateMaster.class);

		// Select
		query.select(b);

		// Effective Date Max Filter
		Subquery<Date> effectiveDate = query.subquery(Date.class);
		Root<WarRateMaster> ocpm1 = effectiveDate.from(WarRateMaster.class);
		effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
		Predicate a1 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
		Predicate a2 = cb.equal(ocpm1.get("productId"), b.get("productId"));
		Predicate a3 = cb.equal(ocpm1.get("sectionId"), b.get("sectionId"));
		Predicate a4 = cb.equal(ocpm1.get("branchCode"), b.get("branchCode"));
		Predicate a5 = cb.lessThanOrEqualTo(b.get("effectiveDateStart"),today);
		effectiveDate.where(a1,a2,a3,a4,a5);

		// Effective Date End
		Subquery<Date> effectiveDate2 = query.subquery(Date.class);
		Root<WarRateMaster> ocpm2 = effectiveDate2.from(WarRateMaster.class);
		effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
		Predicate a6 = cb.equal(ocpm2.get("companyId"), b.get("companyId"));
		Predicate a7 = cb.equal(ocpm2.get("productId"), b.get("productId"));
		Predicate a8 = cb.equal(ocpm2.get("sectionId"), b.get("sectionId"));
		Predicate a9 = cb.equal(ocpm2.get("branchCode"), b.get("branchCode"));
		Predicate a10 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
		effectiveDate2.where(a6,a7,a8,a9,a10);
		
		// Order By
		List<Order> orderList = new ArrayList<Order>();
		orderList.add(cb.asc(b.get("warRateDesc")));
		
		// Company Product Effective Date Max Filter
		Subquery<Long> clause = query.subquery(Long.class);
		Root<WarRateMaster> cs = clause.from(WarRateMaster.class);
		Subquery<Date> effectiveDate3 = query.subquery(Date.class);
		Root<WarRateMaster> ocpm3 = effectiveDate3.from(WarRateMaster.class);
		effectiveDate3.select(cb.greatest(ocpm3.get("effectiveDateStart").as(Date.class)));
		Predicate eff1 = cb.equal(ocpm3.get("companyId"), cs.get("companyId"));
		Predicate eff2 = cb.equal(ocpm3.get("productId"), cs.get("productId"));
		Predicate eff3 = cb.equal(ocpm3.get("sectionId"), cs.get("sectionId"));
		Predicate eff4 = cb.equal(ocpm3.get("branchCode"), cs.get("branchCode"));
		Predicate eff5 = cb.lessThanOrEqualTo(ocpm3.get("effectiveDateStart"),today);
		effectiveDate3.where(eff1,eff2,eff3,eff4,eff5);
		
		Subquery<Date> effectiveDate4 = query.subquery(Date.class);
		Root<WarRateMaster> ocpm4 = effectiveDate4.from(WarRateMaster.class);
		effectiveDate4.select(cb.greatest(ocpm4.get("effectiveDateEnd").as(Date.class)));
		Predicate eff6 = cb.equal(ocpm4.get("companyId"), cs.get("companyId"));
		Predicate eff7 = cb.equal(ocpm4.get("productId"), cs.get("productId"));
		Predicate eff8 = cb.equal(ocpm4.get("sectionId"), cs.get("sectionId"));
		Predicate eff9 = cb.equal(ocpm4.get("branchCode"), cs.get("branchCode"));
		Predicate eff10 = cb.lessThanOrEqualTo(ocpm4.get("effectiveDateEnd"),todayEnd);
		effectiveDate4.where(eff6,eff7,eff8,eff9,eff10);
		
		// Product Section Filter
		clause.select(cs.get("warRateId"));
		Predicate cs1 = cb.equal(cs.get("companyId"), req.getCompanyId());
		Predicate cs2 = cb.equal(cs.get("productId"), req.getProductId());
		Predicate cs3 = cb.equal(cs.get("sectionId"),req.getSectionId());
		Predicate cs6 = cb.equal(cs.get("branchCode"), req.getBranchCode());
		clause.where(cs1,cs2,cs3,cs6);
		
		// Where
		Expression<String>e0= b.get("warRateId");
		Predicate n1 = cb.equal(b.get("companyId"), req.getCompanyId());
		Predicate n2 = cb.equal(b.get("productId"), req.getProductId());
		Predicate n3 = cb.equal(b.get("sectionId"),"0");
		Predicate n4 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
		Predicate n5 = cb.equal(b.get("effectiveDateEnd"), effectiveDate2);
		Predicate n6 = cb.equal(b.get("branchCode"), req.getBranchCode());
		
		Predicate n9 = e0.in(clause).not();
		Predicate n10 = cb.equal(b.get("status"), "Y");
		query.where(n1,n2,n3,n4,n5,n6,n9,n10).orderBy(orderList);

		// Get Result
		TypedQuery<WarRateMaster> result = em.createQuery(query);
		list = result.getResultList();
		
//		// Map
		for (WarRateMaster data : list ) {
			WarRateMasterRes res = new WarRateMasterRes();

			res = dozerMapper.map(data, WarRateMasterRes.class);
			res.setProductId(data.getProductId());
			resList.add(res);
		}

	} catch (Exception e) {
		e.printStackTrace();
		log.info(e.getMessage());
		return null;

	}
	return resList;
}

@Override
public List<Error> validateWarrantyList(List<WarRateMasterReq> reqList) {
	List<Error> errorList = new ArrayList<Error>();

	try {
	
		for (WarRateMasterReq req :  reqList ) {
			
			
			
			if (StringUtils.isBlank(req.getCompanyId())) {
				errorList.add(new Error("02", "CompanyId", "Please Enter CompanyId"));
			}
			
			if (StringUtils.isBlank(req.getBranchCode())) {
				errorList.add(new Error("02", "BranchCode", "Please Select BranchCode"));
			}
	
			if (StringUtils.isBlank(req.getCreatedBy())) {
				errorList.add(new Error("09", "CreatedBy", "Please Enter CreatedBy"));
			}else if (req.getCreatedBy().length() > 100){
				errorList.add(new Error("09","CreatedBy", "Please Enter CreatedBy within 100 Characters")); 
			}	
			if (StringUtils.isBlank(req.getWarRateId())) {
				errorList.add(new Error("10", "WarRateId", "Please Enter WarrateId"));
			}
			if (StringUtils.isBlank(req.getProductId())) {
				errorList.add(new Error("11", "ProductId", "Please Enter ProductId"));
			}
		}
		
		
		
		
	} catch (Exception e) {
		log.error(e);
		e.printStackTrace();
	}
	return errorList;
}

@Override
public SuccessRes saveWarRateList(List<WarRateMasterReq> reqList) {
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	SuccessRes res = new SuccessRes();
	List<WarRateMaster> list  = new ArrayList<WarRateMaster>();
	DozerBeanMapper dozerMapper = new DozerBeanMapper();
	try {
		String end = "31/12/2050";
		List<String> warRateIds = reqList.stream().map( WarRateMasterReq :: getWarRateId ).collect(Collectors.toList()); 
	
			String createdBy ="";
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<WarRateMaster> query = cb.createQuery(WarRateMaster.class);
			//Findall
			Root<WarRateMaster> b = query.from(WarRateMaster.class);
			//select
			query.select(b);
			//Orderby
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(b.get("amendId")));
			
			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<WarRateMaster> ocpm1 = amendId.from(WarRateMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("warRateId"), b.get("warRateId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("branchCode"),b.get("branchCode"));
			Predicate a4 = cb.equal(ocpm1.get("productId"),b.get("productId"));
			Predicate a5 = cb.equal(ocpm1.get("sectionId"),b.get("sectionId"));
//			Predicate a6 = cb.equal(ocpm1.get("policyType"),b.get("policyType"));
			amendId.where(a1,a2,a3,a4,a5);
			// Where
			Expression<String> e0 = b.get("warRateId");
			
			
			//Where
			Predicate n1 = e0.in(warRateIds);
			Predicate n2 = cb.equal(b.get("companyId"),reqList.get(0).getCompanyId());
			Predicate n3 = cb.equal(b.get("branchCode"),reqList.get(0).getBranchCode());
			Predicate n4 = cb.equal(b.get("productId"),reqList.get(0).getProductId() );
			Predicate n5 = cb.equal(b.get("sectionId"),"0");
			Predicate n6 = cb.equal(b.get("amendId"),amendId);
			query.where(n1,n2,n3,n4,n5,n5,n6).orderBy(orderList);
			TypedQuery<WarRateMaster> result = em.createQuery(query);
			list = result.getResultList();
			
			for (WarRateMaster data :  list) {
				WarRateMaster save = new WarRateMaster();
				dozerMapper.map(data, save);
				save.setCreatedBy(createdBy);
				save.setEntryDate(new Date());
				save.setUpdatedBy(createdBy);
				save.setUpdatedDate(new Date());
				save.setAmendId(0);
				save.setProductId(reqList.get(0).getProductId());
				save.setSectionId(reqList.get(0).getSectionId());
				repo.saveAndFlush(save);	
				log.info("Saved Details is --> " + json.toJson(save));	
			}
			res.setResponse("Added Succesfully");
			res.setSuccessId("");
	}
	catch(Exception e) {
		e.printStackTrace();
		log.info("Exception is --> " + e.getMessage());
		return null;
	}
	return res;
	}

	


		

		
	

	
	
	
	
}
