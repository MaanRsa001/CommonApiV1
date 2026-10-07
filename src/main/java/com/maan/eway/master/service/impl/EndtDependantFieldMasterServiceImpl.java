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
import com.maan.eway.bean.EndtDependantFieldMaster;
import com.maan.eway.master.req.EndtDependantFieldChangeStatusReq;
import com.maan.eway.master.req.EndtDependantFieldMasterSaveReq;
import com.maan.eway.master.req.EndtDependantFieldsGetallReq;
import com.maan.eway.master.req.EndtDependantMasterGetReq;
import com.maan.eway.master.res.EndtDependantFieldsGetRes;
import com.maan.eway.master.service.EndtDependantFieldMasterService;
import com.maan.eway.repository.EndtDependantFieldsMasterRepository;
import com.maan.eway.res.DropDownRes;
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

@Service
public class EndtDependantFieldMasterServiceImpl implements EndtDependantFieldMasterService{

	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private EndtDependantFieldsMasterRepository repo; 
	
	Gson json = new Gson();
	
	private Logger log = LogManager.getLogger(EndtDependantFieldMasterServiceImpl.class); 
	
	@Override
	public List<String> validateDependantField(EndtDependantFieldMasterSaveReq req) {
		// TODO Auto-generated method stub
		List<String> errorList = new ArrayList<String>();

		try {
			if (StringUtils.isBlank(req.getProductId())) {
//				errorList.add(new Error("01", "ProductId", "Please Enter ProductId"));
				errorList.add("1313");
			}
			
			if (StringUtils.isBlank(req.getCompanyId())) {
//				errorList.add(new Error("02", "CompanyId", "Please Enter CompanyId"));
				errorList.add("1255");
			}
			if (StringUtils.isBlank(req.getRemarks())) {
//				errorList.add(new Error("04", "Remarks", "Please Enter Remarks "));
				errorList.add("1259");
			}else if (req.getRemarks().length() > 100){
//				errorList.add(new Error("04","Remarks", "Please Enter Remarks within 100 Characters"));
				errorList.add("1260");
			}

			if (StringUtils.isBlank(req.getDependantFieldName())) {
//				errorList.add(new Error("11", "DependantFieldName", "Please Enter DependantFieldName"));
				errorList.add("1921");
			}else if (req.getDependantFieldName().length() > 100){
//				errorList.add(new Error("11","DependantFieldName", "Please Enter DependantFieldName within 100 Characters")); 
				errorList.add("1922");
			}
			else if (StringUtils.isBlank(req.getDependantFieldId()) &&  StringUtils.isNotBlank(req.getCompanyId()) && StringUtils.isNotBlank(req.getProductId())) {
				List<EndtDependantFieldMaster> dependentList = getDependantFieldNameExistDetails(req.getDependantFieldName() , req.getCompanyId(), req.getProductId());
				if (dependentList.size()>0 ) {
//					errorList.add(new Error("01", "DependantFieldName", "This DependantFieldName Already Exist "));
					errorList.add("1923");
				}
			}else if (StringUtils.isNotBlank(req.getDependantFieldId()) &&  StringUtils.isNotBlank(req.getCompanyId()) && StringUtils.isNotBlank(req.getProductId())) {
				List<EndtDependantFieldMaster> dependentList = getDependantFieldNameExistDetails(req.getDependantFieldName() , req.getCompanyId() , req.getProductId());
				
				if (dependentList.size()>0 &&  (! req.getDependantFieldId().equalsIgnoreCase(dependentList.get(0).getDependantFieldId().toString())) ) {
//					errorList.add(new Error("01", "DependantFieldName", "This DependantFieldName Already Exist "));
					errorList.add("1923");
				}
				
			}
			// Date Validation 
			Calendar cal = new GregorianCalendar();
			Date today = new Date();
			cal.setTime(today);cal.add(Calendar.DAY_OF_MONTH, -1);;
			today = cal.getTime();
			if (req.getEffectiveDateStart() == null || StringUtils.isBlank(req.getEffectiveDateStart().toString())) {
//				errorList.add(new Error("05", "EffectiveDateStart", "Please Enter Effective Date Start"));
				errorList.add("1261");

			} else if (req.getEffectiveDateStart().before(today)) {
//				errorList.add(new Error("05", "EffectiveDateStart", "Please Enter Effective Date Start as Future Date"));
				errorList.add("1262");
			}
			//Status Validation
			if (StringUtils.isBlank(req.getStatus())) {
//				errorList.add(new Error("05", "Status", "Please Select Status  "));
				errorList.add("1263");
			} else if (req.getStatus().length() > 1) {
//				errorList.add(new Error("05", "Status", "Please Select Valid Status - One Character Only Allwed"));
				errorList.add("1264");
			}else if(!("Y".equalsIgnoreCase(req.getStatus())||"N".equalsIgnoreCase(req.getStatus())||"R".equalsIgnoreCase(req.getStatus())|| "P".equalsIgnoreCase(req.getStatus()))) {
//				errorList.add(new Error("05", "Status", "Please Select Valid Status - Active or Deactive or Pending or Referral "));
				errorList.add("1265");
			}
			
			if (StringUtils.isBlank(req.getCoreAppCode())) {
//				errorList.add(new Error("07", "CoreAppCode", "Please Enter CoreAppCode"));
				errorList.add("1266");
			}else if (req.getCoreAppCode().length() > 20){
//				errorList.add(new Error("07","CoreAppCode", "Please Enter CoreAppCode within 20 Characters")); 
				errorList.add("1267");
			}
			if (StringUtils.isBlank(req.getRegulatoryCode())) {
//				errorList.add(new Error("20", "RegulatoryCode", "Please Enter RegulatoryCode"));
				errorList.add("1268");
			}else if (req.getRegulatoryCode().length() > 10){
//				errorList.add(new Error("20","RegulatoryCode", "Please Enter RegulatoryCode within 10 Characters")); 
				errorList.add("1269");
			}
			
			if (StringUtils.isBlank(req.getCreatedBy())) {
//				errorList.add(new Error("09", "CreatedBy", "Please Enter CreatedBy"));
				errorList.add("1270");
			}else if (req.getCreatedBy().length() > 100){
//				errorList.add(new Error("09","CreatedBy", "Please Enter CreatedBy within 100 Characters")); 
				errorList.add("1271");
			}

		
			
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return errorList;
	}

	private List<EndtDependantFieldMaster> getDependantFieldNameExistDetails(String dependantFieldName,
			String companyId, String productId) {
		// TODO Auto-generated method stub
		List<EndtDependantFieldMaster> list = new ArrayList<EndtDependantFieldMaster>();
		try {
			Date today = new Date();
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<EndtDependantFieldMaster> query = cb.createQuery(EndtDependantFieldMaster.class);

			// Find All
			Root<EndtDependantFieldMaster> b = query.from(EndtDependantFieldMaster.class);

			// Select
			query.select(b);

			// Effective Date Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<EndtDependantFieldMaster> ocpm1 = amendId.from(EndtDependantFieldMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("dependantFieldId"), b.get("dependantFieldId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			
			amendId.where(a1,a2,a3);

			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(cb.lower( b.get("dependantFieldName")), dependantFieldName.toLowerCase());
			Predicate n3 = cb.equal(b.get("companyId"),companyId);
			Predicate n4 = cb.equal(b.get("productId"),productId);
			
			query.where(n1,n2,n3,n4);
			
			// Get Result
			TypedQuery<EndtDependantFieldMaster> result = em.createQuery(query);
			list = result.getResultList();		
		
		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());

		}
		return list;
	}
	

	@Override
	public SuccessRes saveDependantField(EndtDependantFieldMasterSaveReq req) {
		// TODO Auto-generated method stub
		SuccessRes res = new SuccessRes();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		EndtDependantFieldMaster savedata = new EndtDependantFieldMaster();
		List<EndtDependantFieldMaster> list = new ArrayList<EndtDependantFieldMaster>();
		DozerBeanMapper dozermapper = new DozerBeanMapper();
		try {
			Integer amendId=0;
			Date startDate = req.getEffectiveDateStart();
			String end = "31/12/2050";
			Date endDate = sdf.parse(end);
			long MILLS_IN_A_DAY = 1000*60*60*24;
			Date oldEndDate = new Date(req.getEffectiveDateStart().getTime()- MILLS_IN_A_DAY);
			Date entryDate = null;
			String createdBy ="";
			Integer dependantFieldId = 0;

			if(StringUtils.isBlank(req.getDependantFieldId()))
			{
				Integer totalcount = getMasterTableCount(req.getCompanyId(), req.getProductId());
				dependantFieldId = totalcount+1;
				res.setResponse("Saved Successful");
				res.setSuccessId(dependantFieldId.toString());
			}
			else {
				dependantFieldId = Integer.valueOf(req.getDependantFieldId());
				CriteriaBuilder cb= em.getCriteriaBuilder();
				CriteriaQuery<EndtDependantFieldMaster> query= cb.createQuery(EndtDependantFieldMaster.class);
				//Findall				
				Root<EndtDependantFieldMaster> b = query.from(EndtDependantFieldMaster.class);
				//Select 
				query.select(b);
				//OrderBy
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(b.get("effectiveDateStart")));
				
				//Where 
				Predicate n1 = cb.equal(b.get("dependantFieldId"),req.getDependantFieldId());
				Predicate n2 = cb.equal(b.get("companyId"),req.getCompanyId());
				Predicate n3 = cb.equal(b.get("productId"),req.getProductId());
				
				query.where(n1,n2,n3).orderBy(orderList);
				
				// Get Result
				TypedQuery<EndtDependantFieldMaster> result = em.createQuery(query);
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
						EndtDependantFieldMaster lastRecord = list.get(0);
						lastRecord.setEffectiveDateEnd(oldEndDate);
						repo.saveAndFlush(lastRecord);
					}
					else {
						amendId = list.get(0).getAmendId();
						entryDate = list.get(0).getEntryDate();
						createdBy = list.get(0).getCreatedBy();
						savedata = list.get(0);
						if(list.size()>1) {
							EndtDependantFieldMaster lastRecord = list.get(1);	
							lastRecord.setEffectiveDateEnd(oldEndDate);
							repo.saveAndFlush(lastRecord);
						}
					}
				}
				res.setResponse("Updated Successfully");
				res.setSuccessId(dependantFieldId.toString());
			}
		
			dozermapper.map(req, savedata);
			savedata.setDependantFieldId(dependantFieldId);
			savedata.setEffectiveDateStart(startDate);
			savedata.setEffectiveDateEnd(endDate);
			savedata.setCreatedBy(req.getCreatedBy());
			savedata.setEntryDate(new Date());
			savedata.setUpdatedBy(req.getCreatedBy());
			savedata.setUpdatedDate(new Date());
			savedata.setAmendId(amendId);
			repo.save(savedata);
			
		}
		
		catch(Exception e) {
			e.printStackTrace();
			log.info("Exception is -->" +e.getMessage());
			return null;
		}
		
	return res;
	}

	private Integer getMasterTableCount(String companyId, String productId) {
		// TODO Auto-generated method stub
		Integer data =0;
		try {
			List<EndtDependantFieldMaster> list = new ArrayList<EndtDependantFieldMaster>();
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<EndtDependantFieldMaster> query = cb.createQuery(EndtDependantFieldMaster.class);
			//Find all
			Root<EndtDependantFieldMaster> b = query.from(EndtDependantFieldMaster.class);
			// Select
			query.select(b);
			// Effective Date Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<EndtDependantFieldMaster> ocpm1 = effectiveDate.from(EndtDependantFieldMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(ocpm1.get("companyId"),b.get("companyId"));
			Predicate a2 = cb.equal(ocpm1.get("productId"),b.get("productId"));
			Predicate a3 = cb.equal(ocpm1.get("dependantFieldId"),b.get("dependantFieldId"));

			effectiveDate.where(a1,a2,a3);
		
			//OrderBy
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(b.get("dependantFieldId")));
			
			Predicate n1 = cb.equal(b.get("effectiveDateStart"),effectiveDate);
			Predicate n2 = cb.equal(b.get("companyId"),companyId);
			Predicate n3 = cb.equal(b.get("productId"),productId);
			query.where(n1,n2,n3).orderBy(orderList);
			
			
			
			// Get Result
			TypedQuery<EndtDependantFieldMaster> result = em.createQuery(query);
			int limit = 0 , offset = 1 ;
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			list = result.getResultList();
			data = list.size() > 0 ? list.get(0).getDependantFieldId() : 0 ;
		}
		catch(Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
		}
		return data;

	}

	@Override
	public List<EndtDependantFieldsGetRes> getallDependantField(EndtDependantFieldsGetallReq req) {
		List<EndtDependantFieldsGetRes> resList = new ArrayList<EndtDependantFieldsGetRes>();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			today = cal.getTime();
			Date todayEnd = cal.getTime();
			
			List<EndtDependantFieldMaster> list = new ArrayList<EndtDependantFieldMaster>();
		
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<EndtDependantFieldMaster> query = cb.createQuery(EndtDependantFieldMaster.class);

			// Find All
			Root<EndtDependantFieldMaster> b = query.from(EndtDependantFieldMaster.class);

			// Select
			query.select(b);

			
			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<EndtDependantFieldMaster> ocpm1 = amendId.from(EndtDependantFieldMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a2 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			Predicate a3 = cb.equal(ocpm1.get("dependantFieldId"),b.get("dependantFieldId"));

			amendId.where(a1, a2,a3);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("dependantFieldId")));

			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
			Predicate n3 = cb.equal(b.get("productId"),req.getProductId());
			
			query.where(n1,n2,n3).orderBy(orderList);
			
			
			/*
			// Effective Date Start Max Filter
			Subquery<Long> effectiveDate = query.subquery(Long.class);
			Root<EndtDependantFieldMaster> ocpm1 = effectiveDate.from(EndtDependantFieldMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(b.get("dependantFieldId"),ocpm1.get("dependantFieldId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a3 = cb.equal(b.get("companyId"),ocpm1.get("companyId"));
			Predicate a4 = cb.equal(b.get("productId"),ocpm1.get("productId"));

			effectiveDate.where(a1,a2,a3,a4);
			// Effective Date End Max Filter
			Subquery<Long> effectiveDate2 = query.subquery(Long.class);
			Root<EndtDependantFieldMaster> ocpm2 = effectiveDate2.from(EndtDependantFieldMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate a5 = cb.equal(b.get("dependantFieldId"),ocpm2.get("dependantFieldId"));
			Predicate a6 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			Predicate a7 = cb.equal(b.get("companyId"),ocpm2.get("companyId"));
			Predicate a8 = cb.equal(b.get("productId"),ocpm2.get("productId"));
	
			effectiveDate2.where(a5,a6,a7,a8);

			
			
			// Order By
						List<Order> orderList = new ArrayList<Order>();
						orderList.add(cb.asc(b.get("dependantFieldId")));

						// Where
						Predicate n1 = cb.equal(b.get("status"),"Y");
						Predicate n2 = cb.equal(b.get("status"),"R");
						Predicate n3 = cb.or(n1,n2);
						Predicate n4 = cb.equal(b.get("effectiveDateStart"),effectiveDate);
						Predicate n5 = cb.equal(b.get("effectiveDateEnd"),effectiveDate2);	
						Predicate n6 = cb.equal(b.get("companyId"), req.getCompanyId());
						Predicate n7 = cb.equal(b.get("productId"),req.getProductId());
						
						query.where(n3,n4,n5,n6,n7).orderBy(orderList);	
			*/
			
			
			// Get Result
			TypedQuery<EndtDependantFieldMaster> result = em.createQuery(query);

			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getDependantFieldId()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(EndtDependantFieldMaster :: getDependantFieldId ));
			if(list.size()>0 && list!=null) {
			for(EndtDependantFieldMaster data : list) {
				EndtDependantFieldsGetRes res = new EndtDependantFieldsGetRes(); 	
			res=mapper.map(data, EndtDependantFieldsGetRes.class);
			res.setAmendId(data.getAmendId().toString());
			res.setEntryDate(data.getEntryDate());
			res.setEffectiveDateStart(data.getEffectiveDateStart());
			res.setEffectiveDateEnd(data.getEffectiveDateEnd());
			res.setCoreAppCode(data.getCoreAppCode());
			res.setDependantFieldId(data.getDependantFieldId().toString());
			res.setUpdatedDate(data.getUpdatedDate());
			res.setStatus(data.getStatus());
			res.setRemarks(data.getRemarks());
			res.setCreatedBy(data.getCreatedBy());
			res.setUpdatedBy(data.getUpdatedBy());
			res.setRegulatoryCode(data.getRegulatoryCode());
			resList.add(res);
			}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}

	private static <T> java.util.function.Predicate<T> distinctByKey(java.util.function.Function<? super T, ?> keyExtractor) {
	    Map<Object, Boolean> seen = new ConcurrentHashMap<>();
	    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}
	@Override
	public List<EndtDependantFieldsGetRes> getActiveDependantField(EndtDependantFieldsGetallReq req) {
		// TODO Auto-generated method stub
		List<EndtDependantFieldsGetRes> resList = new ArrayList<EndtDependantFieldsGetRes>();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();

			List<EndtDependantFieldMaster> list = new ArrayList<EndtDependantFieldMaster>();
		
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<EndtDependantFieldMaster> query = cb.createQuery(EndtDependantFieldMaster.class);

			// Find All
			Root<EndtDependantFieldMaster> b = query.from(EndtDependantFieldMaster.class);

			// Select
			query.select(b);

			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<EndtDependantFieldMaster> ocpm1 = amendId.from(EndtDependantFieldMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a2 = cb.equal(ocpm1.get("productId"), b.get("productId"));
		
			amendId.where(a1, a2);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("dependantFieldId")));

			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
			Predicate n3 = cb.equal(b.get("productId"),req.getProductId());
			Predicate n4 = cb.equal(b.get("status"),"Y");
					
			query.where(n1,n2,n3,n4).orderBy(orderList);
			
			// Get Result
			TypedQuery<EndtDependantFieldMaster> result = em.createQuery(query);

			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getDependantFieldId()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(EndtDependantFieldMaster :: getDependantFieldId ));
			
			if(list!=null && list.size()>0) {
			for(EndtDependantFieldMaster data : list) {
				EndtDependantFieldsGetRes res = new EndtDependantFieldsGetRes(); 	
			res=mapper.map(data, EndtDependantFieldsGetRes.class);
			res.setAmendId(data.getAmendId().toString());
			res.setEntryDate(data.getEntryDate());
			res.setEffectiveDateStart(data.getEffectiveDateStart());
			res.setEffectiveDateEnd(data.getEffectiveDateEnd());
			res.setCoreAppCode(data.getCoreAppCode());
			res.setDependantFieldId(data.getDependantFieldId().toString());
			res.setUpdatedDate(data.getUpdatedDate());
			res.setStatus(data.getStatus());
			res.setRemarks(data.getRemarks());
			res.setCreatedBy(data.getCreatedBy());
			res.setUpdatedBy(data.getUpdatedBy());
			res.setRegulatoryCode(data.getRegulatoryCode());
			resList.add(res);
			}
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}
	@Override
	public EndtDependantFieldsGetRes getByDependantFieldId(EndtDependantMasterGetReq req) {
		// TODO Auto-generated method stub
		EndtDependantFieldsGetRes res = new EndtDependantFieldsGetRes();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();

			List<EndtDependantFieldMaster> list = new ArrayList<EndtDependantFieldMaster>();
		
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<EndtDependantFieldMaster> query = cb.createQuery(EndtDependantFieldMaster.class);

			// Find All
			Root<EndtDependantFieldMaster> b = query.from(EndtDependantFieldMaster.class);

			// Select
			query.select(b);

			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<EndtDependantFieldMaster> ocpm1 = amendId.from(EndtDependantFieldMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a2 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			Predicate a3 = cb.equal(ocpm1.get("dependantFieldId"), b.get("dependantFieldId"));
			
			amendId.where(a1, a2,a3);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("dependantFieldId")));

			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
			Predicate n3 = cb.equal(b.get("productId"),req.getProductId());
			Predicate n4 = cb.equal(b.get("dependantFieldId"),req.getDependantFieldId());
					
			query.where(n1,n2,n3,n4).orderBy(orderList);
			
			// Get Result
			TypedQuery<EndtDependantFieldMaster> result = em.createQuery(query);

			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getDependantFieldId()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(EndtDependantFieldMaster :: getDependantFieldId ));
			
			if(list.size()>0 && list!=null) {
			res=mapper.map(list.get(0), EndtDependantFieldsGetRes.class);
			res.setAmendId(list.get(0).getAmendId().toString());
			res.setEntryDate(list.get(0).getEntryDate());
			res.setEffectiveDateStart(list.get(0).getEffectiveDateStart());
			res.setEffectiveDateEnd(list.get(0).getEffectiveDateEnd());
			res.setCoreAppCode(list.get(0).getCoreAppCode());
			res.setDependantFieldId(list.get(0).getDependantFieldId().toString());
			res.setUpdatedDate(list.get(0).getUpdatedDate());
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return res;
	}

	@Override
	public SuccessRes changeStatusOfDependantField(EndtDependantFieldChangeStatusReq req) {
		// TODO Auto-generated method stub
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		SuccessRes res = new SuccessRes();
		EndtDependantFieldMaster saveData = new EndtDependantFieldMaster();
		List<EndtDependantFieldMaster> list  = new ArrayList<EndtDependantFieldMaster>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			Integer amendId = 0;
			Date StartDate = req.getEffectiveDateStart();
			String end = "31/12/2050";
			Date endDate = sdf.parse(end);
			long MILLS_IN_A_DAY = 1000*60*60*24;
			Date oldEndDate = new Date(req.getEffectiveDateStart().getTime()- MILLS_IN_A_DAY);
			Date entryDate = null;
			String createdBy = "";
			Integer dependantFieldId = 0;

			dependantFieldId = Integer.valueOf(req.getDependantFieldId());
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<EndtDependantFieldMaster> query = cb.createQuery(EndtDependantFieldMaster.class);
			// Findall
			Root<EndtDependantFieldMaster> b = query.from(EndtDependantFieldMaster.class);
			// select
			query.select(b);
			// Orderby
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(b.get("effectiveDateStart")));
			// Where
			Predicate n1 = cb.equal(b.get("dependantFieldId"), req.getDependantFieldId());
			Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
			Predicate n3 = cb.equal(b.get("productId"), req.getProductId());
			query.where(n1, n2, n3).orderBy(orderList);

			// Get Result
			TypedQuery<EndtDependantFieldMaster> result = em.createQuery(query);
			int limit = 0, offset = 2;
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			list = result.getResultList();
				Date beforeOneDay = new Date(new Date().getTime() - MILLS_IN_A_DAY);
				if (list.get(0).getEffectiveDateStart().before(beforeOneDay)) {
					amendId = list.get(0).getAmendId() + 1;
					entryDate = new Date();
					createdBy = req.getCreatedBy();
					EndtDependantFieldMaster lastRecord = list.get(0);
					lastRecord.setEffectiveDateEnd(oldEndDate);
					repo.saveAndFlush(lastRecord);
				} else {
					amendId = list.get(0).getAmendId();
					entryDate = list.get(0).getEntryDate();
					createdBy = list.get(0).getCreatedBy();
					saveData = list.get(0);
			//			EndtTypeMaster lastRecord = list.get(1);
			//			lastRecord.setEffectiveDateEnd(oldEndDate);
			//			repo.saveAndFlush(lastRecord);
					
				}
			
			res.setResponse("Updated Successfully");
			res.setSuccessId(dependantFieldId.toString());

			dozerMapper.map(list.get(0), saveData);
			
			saveData.setDependantFieldId(dependantFieldId);
			saveData.setEffectiveDateStart(StartDate);
			saveData.setEffectiveDateEnd(endDate);
			saveData.setCreatedBy(createdBy);
			saveData.setEntryDate(entryDate);
			saveData.setUpdatedBy(req.getCreatedBy());
			saveData.setUpdatedDate(new Date());
			saveData.setAmendId(amendId);
			saveData.setStatus(req.getStatus());
			saveData.setCompanyId(list.get(0).getCompanyId());
			saveData.setProductId(req.getProductId()==null?"99999":(req.getProductId()));
			repo.saveAndFlush(saveData);	
			log.info("Saved Details is --> " + json.toJson(saveData));	
			res.setResponse("Status Changed");
			res.setSuccessId(req.getDependantFieldId());
		}
		catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " + e.getMessage());
			return null;
			}
		return res;
	}

	@Override
	public List<DropDownRes> getDependantMasterDropdown(EndtDependantFieldsGetallReq req) {
		// TODO Auto-generated method stub
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			today = cal.getTime();
			Date todayEnd = cal.getTime();
			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<EndtDependantFieldMaster> query=  cb.createQuery(EndtDependantFieldMaster.class);
			List<EndtDependantFieldMaster> list = new ArrayList<EndtDependantFieldMaster>();
			// Find All
			Root<EndtDependantFieldMaster> b = query.from(EndtDependantFieldMaster.class);
			//Select
			query.select(b);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("dependantFieldName")));
			
			// Effective Date Start Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<EndtDependantFieldMaster> ocpm1 = effectiveDate.from(EndtDependantFieldMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(b.get("dependantFieldId"),ocpm1.get("dependantFieldId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a3 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a4 = cb.equal(ocpm1.get("productId"), b.get("productId"));

			effectiveDate.where(a1,a2,a3,a4);
			// Effective Date End Max Filter
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<EndtDependantFieldMaster> ocpm2 = effectiveDate2.from(EndtDependantFieldMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate a7 = cb.equal(b.get("dependantFieldId"),ocpm2.get("dependantFieldId"));
			Predicate a8 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			Predicate a9 = cb.equal(ocpm2.get("companyId"), b.get("companyId"));
			Predicate a10 = cb.equal(ocpm2.get("productId"), b.get("productId"));

			effectiveDate2.where(a7,a8,a9,a10);
			// Where
			Predicate n1 = cb.equal(b.get("status"),"Y");
			Predicate n12 = cb.equal(b.get("status"),"R");
			Predicate n13 = cb.or(n1,n12);
			Predicate n2 = cb.equal(b.get("effectiveDateStart"),effectiveDate);
			Predicate n3 = cb.equal(b.get("effectiveDateEnd"),effectiveDate2);	
			Predicate n4 = cb.equal(b.get("companyId"),req.getCompanyId());	
			Predicate n5 = cb.equal(b.get("productId"),req.getProductId());
			query.where(n13,n2,n3,n4,n5).orderBy(orderList);
			// Get Result
			TypedQuery<EndtDependantFieldMaster> result = em.createQuery(query);
			list = result.getResultList();
			if(list.size()>0 && list!=null) {
			for (EndtDependantFieldMaster data : list) {
				// Response 
				DropDownRes res = new DropDownRes();
				res.setCode(data.getDependantFieldId().toString());
				res.setCodeDesc(data.getDependantFieldName());
				res.setCodeDescLocal(data.getDependentFieldNameLocal());
				res.setStatus(data.getStatus());
				resList.add(res);
			}
			}
		}
			catch(Exception e) {
				e.printStackTrace();
				log.info("Exception is --->"+e.getMessage());
				return null;
				}
			return resList;
	}

}
