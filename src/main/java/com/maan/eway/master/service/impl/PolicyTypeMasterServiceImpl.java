package com.maan.eway.master.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.maan.eway.bean.PolicyTypeMaster;
import com.maan.eway.common.service.impl.DropDownServiceImpl;
import com.maan.eway.error.Error;
import com.maan.eway.master.req.PolicyTypeMasterGetAllReq;
import com.maan.eway.master.req.PolicyTypeMasterGetReq;
import com.maan.eway.master.req.PolicyTypeMasterSaveReq;
import com.maan.eway.master.res.PolicyTypeMasterGetRes;
import com.maan.eway.master.service.PolicyTypeMasterService;
import com.maan.eway.repository.PolicyTypeMasterRepository;
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
import jakarta.transaction.Transactional;

@Service
@Transactional
public class PolicyTypeMasterServiceImpl implements PolicyTypeMasterService {

	@Autowired
	private PolicyTypeMasterRepository repo;
	
	@PersistenceContext
	private EntityManager em;

	Gson json = new Gson();
	
	private Logger log = LogManager.getLogger(PolicyTypeMasterServiceImpl.class);
	
	@Override
	public List<com.maan.eway.error.Error> validatePolicyType(PolicyTypeMasterSaveReq req) {
		List<Error> error = new ArrayList<Error>();
		try {
			if (StringUtils.isBlank(req.getPolicyTypeName())) {
				error.add(new Error("01", "Policy Type Name", "Please Enter Policy Type Name "));
			} else if (req.getPolicyTypeName().length() > 100) {
				error.add(new Error("01", "Policy Type Name", "Please Enter Policy Type Name within 100 Characters"));
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
				error.add(new Error("02", "EffectiveDateStart", "Please Enter Effective Date Start "));

			} 
			else if (req.getEffectiveDateStart().before(today)) {
				error
						.add(new Error("02", "EffectiveDateStart", "Please Enter Effective Date Start as Future Date"));
			} else if (req.getEffectiveDateEnd() == null) {
				error.add(new Error("03", "EffectiveDateEnd", "Please Enter Effective Date End "));

			} else if (req.getEffectiveDateEnd().before(req.getEffectiveDateStart())
					|| req.getEffectiveDateEnd().equals(req.getEffectiveDateStart())) {
				error.add(new Error("03", "EffectiveDateStart",
						"Please Enter Effective Date End  is After Effective Date Start"));

		}
			if (req.getRemarks().length() > 100) {
				error.add(new Error("04", "Remarks", "Please Enter Remarks within 100 Characters"));
			} 	
		}
		catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details"+e.getMessage());;
			return null;
		}
		return error;
	}

	@Override
	public SuccessRes insertPolicyType(PolicyTypeMasterSaveReq req) {
	SimpleDateFormat sdformat = new SimpleDateFormat("dd/MM/yyyy");
	SuccessRes res = new SuccessRes();
	PolicyTypeMaster saveData = new PolicyTypeMaster();
	List<PolicyTypeMaster> list = new ArrayList<PolicyTypeMaster>();
	DozerBeanMapper dozermapper = new DozerBeanMapper();
	try {
	Calendar cal = new GregorianCalendar();
	cal.setTime(req.getEffectiveDateStart());
	cal.set(Calendar.HOUR_OF_DAY, 23);
	cal.set(Calendar.MINUTE, 59);
	Date startDate = cal.getTime();
	Date today = new Date();
	cal.setTime(req.getEffectiveDateStart());
	cal.set(Calendar.HOUR_OF_DAY,today.getHours());
	cal.set(Calendar.MINUTE, today.getMinutes());
	Date oldEndDate = cal.getTime();
	cal.setTime(req.getEffectiveDateStart());
	cal.set(Calendar.HOUR_OF_DAY, today.getHours());
	cal.set(Calendar.MINUTE, today.getMinutes());
	Date effDate = cal.getTime();
	Date endDate = req.getEffectiveDateEnd();
	String policyId="";
	Integer amendId = 0;

	if(StringUtils.isBlank(req.getPolicyTypeId())) {
		//save
		//Long totalCount = getMasterTableCount();
		Long totalCount=repo.count()+1;
		policyId = Long.valueOf(totalCount+1).toString();
		res.setResponse("Saved Successfully");
		res.setSuccessId(policyId);
		}
	else {
		// Update
		policyId = req.getPolicyTypeId().toString();
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PolicyTypeMaster> query = cb.createQuery(PolicyTypeMaster.class);
		//Find All
		Root<PolicyTypeMaster> b = query.from(PolicyTypeMaster.class);
		//Select
		query.select(b);
		//Effective Date Max Filter
		Subquery<Date> effectiveDate = query.subquery(Date.class);
		Root<PolicyTypeMaster> ocpm1 = effectiveDate.from(PolicyTypeMaster.class);
		effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
		Predicate a1 = cb.equal(ocpm1.get("policyTypeId"), b.get("policyTypeId"));
		Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), startDate);
		effectiveDate.where(a1,a2);
		//where
		Predicate n1 = cb.equal(b.get("status"),"Y");
		Predicate n2 = cb.equal(b.get("effectiveDateStart"),effectiveDate);
		Predicate n3 = cb.equal(b.get("policyTypeId"),req.getPolicyTypeId());
		query.where(n1,n2,n3);
		// Get Result
		TypedQuery<PolicyTypeMaster> result = em.createQuery(query);
		list = result.getResultList();
		if(list.size()>0) {
			repo.delete(list.get(0));
			// Amend Id

			if (list.get(0).getEffectiveDateStart().before(startDate)) {
				String startDatewithoutTime = sdformat.format(startDate);
				String oldDatewithoutTime = sdformat.format(list.get(0).getEffectiveDateStart());

				if (startDatewithoutTime.equalsIgnoreCase(oldDatewithoutTime))
					
				{
					amendId = list.get(0).getAmendId() + 1;
				}
			}

		}
		res.setResponse("Updated Successfully");
		res.setSuccessId(policyId);		
	}
	dozermapper.map(req,saveData);
	saveData.setAmendId(amendId);
	saveData.setEffectiveDateStart(endDate);
	saveData.setEffectiveDateStart(req.getEffectiveDateStart());
	saveData.setStatus("Y");
	saveData.setEntryDate(new Date());
	saveData.setPolicyTypeId(Integer.valueOf(policyId));
	repo.saveAndFlush(saveData);
	if(list.size()>0) {
		// Update Old Record
		PolicyTypeMaster lastRecord = list.get(0);
		lastRecord.setEffectiveDateEnd(oldEndDate);
		String startDatewithoutTime = sdformat.format(startDate);
		String oldDatewithoutTime = sdformat.format(list.get(0).getEffectiveDateStart());

		if (startDatewithoutTime.equalsIgnoreCase(oldDatewithoutTime)) {
			lastRecord.setStatus("N");
		}

		repo.saveAndFlush(lastRecord);;
	}
	log.info("Saved Details is --->"+ json.toJson(saveData));
	}
	catch(Exception e) {
		e.printStackTrace();
		log.info("Log Details"+ e.getMessage());
		return null;
	}
	return res;
	}
	
	public Long getMasterTableCount() {
		Long data =0L;
		try {
			List<Long> list = new ArrayList<Long>();
			// Find Latest Record 
			CriteriaBuilder cb= em.getCriteriaBuilder();
		CriteriaQuery<Long>	query = cb.createQuery(Long.class);
		//Find All
		Root<PolicyTypeMaster> b = query.from(PolicyTypeMaster.class);
		//Select
		query.multiselect(cb.count(b));
		// Effective Date Max Filter
		Subquery<Date> effectiveDate = query.subquery(Date.class);
		Root<PolicyTypeMaster> ocpm1 = effectiveDate.from(PolicyTypeMaster.class);
		effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
		Predicate a1 = cb.equal(ocpm1.get("policyTypeId"),b.get("policyTypeId"));
		effectiveDate.where(a1);
		Predicate n1 = cb.equal(b.get("effectiveDateStart"),effectiveDate);
		query.where(n1);
		// Get Result
		TypedQuery<Long> result = em.createQuery(query);
		list = result.getResultList();
		data = list.get(0);
		}
		catch(Exception e) {
			e.printStackTrace();
			log.info("Log Details"+ e.getMessage());
		}
		return data;
	}
	
	
	@Override
	public PolicyTypeMasterGetRes getPolicyType(PolicyTypeMasterGetReq req) {
		PolicyTypeMasterGetRes res = new PolicyTypeMasterGetRes();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();

			List<PolicyTypeMaster> list = new ArrayList<PolicyTypeMaster>();

			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<PolicyTypeMaster> query = cb.createQuery(PolicyTypeMaster.class);

			// Find All
			Root<PolicyTypeMaster> b = query.from(PolicyTypeMaster.class);

			// Select
			query.select(b);

			// Effective Date Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<PolicyTypeMaster> ocpm1 = effectiveDate.from(PolicyTypeMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(ocpm1.get("policyTypeId"), b.get("policyTypeId"));
			Predicate a2 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			Predicate a3 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
//			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);

			effectiveDate.where(a1,a2,a3);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("policyTypeId")));

			// Where
			Predicate n1 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
			Predicate n2 = cb.equal(b.get("policyTypeId"), req.getPolicyTypeId());
			Predicate n3 = cb.equal(b.get("productId"), req.getProductId());
			Predicate n4 = cb.equal(b.get("companyId"), req.getCompanyId());
			query.where(n1, n2,n3,n4).orderBy(orderList);

			// Get Result
			TypedQuery<PolicyTypeMaster> result = em.createQuery(query);

			list = result.getResultList();
			if (!list.isEmpty())
			{
			res = mapper.map(list.get(0), PolicyTypeMasterGetRes.class);
			res.setPolicyTypeId(list.get(0).getPolicyTypeId().toString());
			res.setEntryDate(list.get(0).getEntryDate());
			res.setEffectiveDateStart(list.get(0).getEffectiveDateStart());
			res.setEffectiveDateEnd(list.get(0).getEffectiveDateEnd());
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return res;
	}
	
	
	


	@Override
	public List<PolicyTypeMasterGetRes> getallPolicyType(PolicyTypeMasterGetAllReq req) {
		List<PolicyTypeMasterGetRes> resList = new ArrayList<PolicyTypeMasterGetRes>();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();

			List<PolicyTypeMaster> list = new ArrayList<PolicyTypeMaster>();
			// Pagination
			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<PolicyTypeMaster> query = cb.createQuery(PolicyTypeMaster.class);

			// Find All
			Root<PolicyTypeMaster> b = query.from(PolicyTypeMaster.class);

			// Select
			query.select(b);

			// Effective Date Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<PolicyTypeMaster> ocpm1 = effectiveDate.from(PolicyTypeMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(ocpm1.get("policyTypeId"), b.get("policyTypeId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);

			effectiveDate.where(a1, a2);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("policyTypeId")));

			// Where
			Predicate n1 = cb.equal(b.get("effectiveDateStart"), effectiveDate);

			query.where(n1).orderBy(orderList);

			// Get Result
			TypedQuery<PolicyTypeMaster> result = em.createQuery(query);
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			list = result.getResultList();

			// Map
			for (PolicyTypeMaster data : list) {
				PolicyTypeMasterGetRes res = new PolicyTypeMasterGetRes();

				res = mapper.map(data, PolicyTypeMasterGetRes.class);
				res.setPolicyTypeId(data.getPolicyTypeId().toString());
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
	public List<PolicyTypeMasterGetRes> getallactivePolicyType(PolicyTypeMasterGetAllReq req) {
		List<PolicyTypeMasterGetRes> resList = new ArrayList<PolicyTypeMasterGetRes>();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();

			List<PolicyTypeMaster> list = new ArrayList<PolicyTypeMaster>();
			// Pagination
			int limit = StringUtils.isBlank(req.getLimit()) ? 0 : Integer.valueOf(req.getLimit());
			int offset = StringUtils.isBlank(req.getOffset()) ? 100 : Integer.valueOf(req.getOffset());

			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<PolicyTypeMaster> query = cb.createQuery(PolicyTypeMaster.class);

			// Find All
			Root<PolicyTypeMaster> b = query.from(PolicyTypeMaster.class);

			// Select
			query.select(b);

			// Effective Date Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<PolicyTypeMaster> ocpm1 = effectiveDate.from(PolicyTypeMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(ocpm1.get("policyTypeId"), b.get("policyTypeId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);

			effectiveDate.where(a1, a2);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("policyTypeId")));

			// Where
			Predicate n1 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
			Predicate n2 = cb.equal(b.get("status"), "Y");

			query.where(n1,n2).orderBy(orderList);

			// Get Result
			TypedQuery<PolicyTypeMaster> result = em.createQuery(query);
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			list = result.getResultList();

			// Map
			for (PolicyTypeMaster data : list) {
				PolicyTypeMasterGetRes res = new PolicyTypeMasterGetRes();

				res = mapper.map(data, PolicyTypeMasterGetRes.class);
				res.setPolicyTypeId(data.getPolicyTypeId().toString());
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
	public List<DropDownRes> getPolicyTypeMasterDropdown() {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);;
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			Date todayEnd = cal.getTime();
			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<PolicyTypeMaster> query=  cb.createQuery(PolicyTypeMaster.class);
			List<PolicyTypeMaster> list = new ArrayList<PolicyTypeMaster>();
			// Find All
			Root<PolicyTypeMaster> c = query.from(PolicyTypeMaster.class);
			//Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("policyTypeId")));
			
			// Effective Date Start Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<PolicyTypeMaster> ocpm1 = effectiveDate.from(PolicyTypeMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(c.get("policyTypeId"),ocpm1.get("policyTypeId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a1,a2);
			// Effective Date End Max Filter
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<PolicyTypeMaster> ocpm2 = effectiveDate2.from(PolicyTypeMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate a3 = cb.equal(c.get("policyTypeId"),ocpm2.get("policyTypeId"));
			Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			effectiveDate2.where(a3,a4);
			// Where
			Predicate n1 = cb.equal(c.get("status"),"Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);	
			query.where(n1,n2,n3).orderBy(orderList);
			// Get Result
			TypedQuery<PolicyTypeMaster> result = em.createQuery(query);
			list = result.getResultList();
			for (PolicyTypeMaster data : list) {
				// Response 
				DropDownRes res = new DropDownRes();
				res.setCode(data.getPolicyTypeId().toString());
				res.setCodeDesc(data.getPolicyTypeName());
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



}
