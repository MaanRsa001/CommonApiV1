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
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.maan.eway.bean.MotorBodyTypeMaster;
import com.maan.eway.master.req.BodyTypeChangeStatusReq;
import com.maan.eway.master.req.BodyTypeDropDownReq;
import com.maan.eway.master.req.MotorBodySaveReq;
import com.maan.eway.master.req.MotorBodyTypeGetAllReq;
import com.maan.eway.master.req.MotorBodyTypeGetReq;
import com.maan.eway.master.res.MotorBodyTypeGetRes;
import com.maan.eway.master.service.MotorBodyTypeMasterService;
import com.maan.eway.repository.MotorBodyTypeMasterRepository;
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
@Transactional
public class MotorBodyTypeMasterServiceImpl implements MotorBodyTypeMasterService {

	@Autowired
	private MotorBodyTypeMasterRepository repo;

	@PersistenceContext
	private EntityManager em;

	Gson json = new Gson();

	private Logger log = LogManager.getLogger(MotorBodyTypeMasterServiceImpl.class);

	@Override
	public List<String> validateMakeMotor(MotorBodySaveReq req) {

		List<String> errorList = new ArrayList<String>();

		try {

			if (StringUtils.isBlank(req.getBodyNameEn())) {
				//errorList.add(new Error("01", "Body Name En", "Please Enter Body Name En "));
				errorList.add("1315");
			}
			else if (req.getBodyNameEn().length()>100) {
				//errorList.add(new Error("01", "Body Name En", "Please Enter Body Name En within 100 Characters "));
				errorList.add("1316");
			}else if (StringUtils.isBlank(req.getBodyId()) &&  StringUtils.isNotBlank(req.getCompanyId()) && StringUtils.isNotBlank(req.getBranchCode())) {
				List<MotorBodyTypeMaster> motorBodyList = getBodyNameExistDetails(req.getBodyNameEn() , req.getCompanyId() , req.getBranchCode());
				if (motorBodyList.size()>0 ) {
					//errorList.add(new Error("01", "BodyNameEn", "This Body Name Already Exist "));
					errorList.add("1317");
				}
			}else if (StringUtils.isNotBlank(req.getBodyId()) &&  StringUtils.isNotBlank(req.getCompanyId()) && StringUtils.isNotBlank(req.getBranchCode())) {
				List<MotorBodyTypeMaster> motorBodyList = getBodyNameExistDetails(req.getBodyNameEn() , req.getCompanyId() , req.getBranchCode());
				
				if (motorBodyList.size()>0 &&  (! req.getBodyId().equalsIgnoreCase(motorBodyList.get(0).getBodyId().toString())) ) {
					//errorList.add(new Error("01", "BodyNameEn", "This Body Name Already Exist "));
					errorList.add("1317");
				}
			}	
			
			// Date Validation
			Calendar cal = new GregorianCalendar();
			Date today = new Date();
			cal.setTime(today);
			cal.add(Calendar.DAY_OF_MONTH, -1);
			today = cal.getTime();
			if (req.getEffectiveDateStart() == null || StringUtils.isBlank(req.getEffectiveDateStart().toString())) {
				//errorList.add(new Error("02", "EffectiveDateStart", "Please Enter Effective Date Start"));
				errorList.add("1261");

			} else if (req.getEffectiveDateStart().before(today)) {
				//errorList.add(new Error("02", "EffectiveDateStart", "Please Enter Effective Date Start as Future Date"));
						
				errorList.add("1262");
			}
			// Status Validation
			if (StringUtils.isBlank(req.getStatus())) {
				//errorList.add(new Error("05", "Status", "Please Enter Status"));
				errorList.add("1263");
			} else if (req.getStatus().length() > 1) {
				//errorList.add(new Error("05", "Status", "Enter Status in One Character Only"));
				errorList.add("1264");
			} else if(!("Y".equalsIgnoreCase(req.getStatus())||"N".equalsIgnoreCase(req.getStatus())||"R".equalsIgnoreCase(req.getStatus())|| "P".equalsIgnoreCase(req.getStatus()))) {
				//errorList.add(new Error("05", "Status", "Please Select Valid Status - Active or Deactive or Pending or Referral "));
				errorList.add("1265");
			}
			if (StringUtils.isBlank(req.getCompanyId())) {
				//errorList.add(new Error("04", "CompanyId", "Please Enter CompanyId"));
				errorList.add("1255");
			} 
//			else if (req.getCompanyId().length() > 20) {
//				//errorList.add(new Error("04", "CompanyId", "CompanyId 20 Character Only"));
//				errorList.add("0000");
//			}
			if (StringUtils.isBlank(req.getSeatingCapacity())) {
				//errorList.add(new Error("05", "SeatingCapacity", "Please Enter Seating Capacity"));
				errorList.add("1318");
			} else if (!req.getSeatingCapacity().matches("[0-9.]+")) {
			//	errorList.add(new Error("05", "SeatingCapacity", "Please Enter Valid Seating Capacity"));
				errorList.add("1319");
			}else if (Integer.valueOf(req.getSeatingCapacity())<0) {
				//errorList.add(new Error("05", "SeatingCapacity", "Please Enter Seating Capacity correct Value"));
				errorList.add("1320");
			}
			if (StringUtils.isBlank(req.getCylinders())) {
			//	errorList.add(new Error("06", "Cylinders", "Please Enter Cylinders"));
				errorList.add("1321");
			} else if (!req.getCylinders().matches("[0-9.]+")) {
				//errorList.add(new Error("06", "Cylinders", "Please Enter Valid Cylinders"));
				errorList.add("1322");
			}else if (Integer.valueOf(req.getCylinders())<0) {
				//errorList.add(new Error("05", "Cylinders", "Please Enter  Cylinders correct Value"));
				errorList.add("1323");
			}
			if (StringUtils.isBlank(req.getTonnage())) {
			//	errorList.add(new Error("07", "Tonnage", "Please Enter Tonnage"));
				errorList.add("1324");
			} else if (!req.getTonnage().matches("[0-9.]+")) {
				//errorList.add(new Error("06", "Tonnage", "Please Enter Valid Tonnage"));
				errorList.add("1325");
			}else if (Double.valueOf(req.getTonnage())<0) {
				//errorList.add(new Error("05", "Tonnage", "Please Enter  Tonnage correct Value"));
				errorList.add("1326");
			}else if (req.getTonnage().contains(".")) {
			    errorList.add("1325"); // Add a new error code for non-integer values
			}
			
//			if (StringUtils.isBlank(req.getBranchCode())) {
//				errorList.add(new Error("02", "BranchCode", "Please Select BranchCode"));
//			}
//			
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return errorList;
	}

		public List<MotorBodyTypeMaster> getBodyNameExistDetails(String name , String InsuranceId , String branchCode) {
			List<MotorBodyTypeMaster> list = new ArrayList<MotorBodyTypeMaster>();
			try {
				Date today = new Date();
				// Find Latest Record
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<MotorBodyTypeMaster> query = cb.createQuery(MotorBodyTypeMaster.class);

				// Find All
				Root<MotorBodyTypeMaster> b = query.from(MotorBodyTypeMaster.class);

				// Select
				query.select(b);

				// Effective Date Max Filter
				Subquery<Long> amendId = query.subquery(Long.class);
				Root<MotorBodyTypeMaster> ocpm1 = amendId.from(MotorBodyTypeMaster.class);
				amendId.select(cb.max(ocpm1.get("amendId")));
				Predicate a1 = cb.equal(ocpm1.get("bodyId"), b.get("bodyId"));
				Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
				Predicate a3 = cb.equal(ocpm1.get("branchCode"), b.get("branchCode"));
				Predicate a4 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
				Predicate a5 = cb.greaterThanOrEqualTo(ocpm1.get("effectiveDateEnd"), today);
				amendId.where(a1,a2,a3,a4,a5);

				Predicate n1 = cb.equal(b.get("amendId"), amendId);
				Predicate n2 = cb.equal(cb.lower( b.get("bodyNameEn")), name.toLowerCase());
				Predicate n3 = cb.equal(b.get("companyId"),InsuranceId);
				Predicate n4 = cb.equal(b.get("branchCode"), branchCode);
				Predicate n5 = cb.equal(b.get("branchCode"), "99999");
				Predicate n6 = cb.or(n4,n5);
				query.where(n1,n2,n3,n6);
				
				// Get Result
				TypedQuery<MotorBodyTypeMaster> result = em.createQuery(query);
				list = result.getResultList();		
			
			} catch (Exception e) {
				e.printStackTrace();
				log.info(e.getMessage());

			}
			return list;
		}

	
		
	@Override
	public SuccessRes saveMakeMotor(MotorBodySaveReq req) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY");
		SuccessRes res = new SuccessRes();
		MotorBodyTypeMaster saveData = new MotorBodyTypeMaster();
		List<MotorBodyTypeMaster> list = new ArrayList<MotorBodyTypeMaster>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();

		try {
			Integer amendId=0;
			Date startDate = req.getEffectiveDateStart() ;
			String  end = "31/12/2050";
			Date endDate = sdf.parse(end);
			long MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;
			Date oldEndDate = new Date(req.getEffectiveDateStart().getTime() - MILLIS_IN_A_DAY);
			Date entryDate = null ;
			String createdBy = "" ;
			String bodyId = "";

			if (StringUtils.isBlank(req.getBodyId()) || req.getBodyId()==null) {
				// Save
				// Long totalCount = repo.count();
				Long totalCount =  getMasterTableCount( req.getCompanyId() , req.getBranchCode());
				bodyId = Long.valueOf(totalCount + 1).toString();
				entryDate = new Date();
				createdBy = req.getCreatedBy();
				res.setResponse("Saved Successfully ");
				res.setSuccessId(bodyId);

			} else {
				// Update
				// Get Less than Equal Today Record
				// Criteria
				bodyId = req.getBodyId();
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<MotorBodyTypeMaster> query = cb.createQuery(MotorBodyTypeMaster.class);

				// Find All
				Root<MotorBodyTypeMaster> b = query.from(MotorBodyTypeMaster.class);

				// Select
				query.select(b);

				// Effective Date Max Filter
		/*		Subquery<Long> effectiveDate = query.subquery(Long.class);
				Root<MotorBodyTypeMaster> ocpm1 = effectiveDate.from(MotorBodyTypeMaster.class);
				effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
				Predicate a1 = cb.equal(ocpm1.get("bodyId"), b.get("bodyId"));
				Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), startDate);
				Predicate a3 = cb.equal(ocpm1.get("sectionId"), b.get("sectionId"));
				effectiveDate.where(a1, a2,a3);
*/
				// Order By
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(b.get("effectiveDateStart")));
				
				
				// Where
			//	Predicate n1 = cb.equal(b.get("status"), "Y");
				//Predicate n2 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
				Predicate n1 = cb.equal(b.get("companyId"), req.getCompanyId());
				Predicate n2 = cb.equal(b.get("branchCode"), req.getBranchCode());
				Predicate n3 = cb.equal(b.get("bodyId"), req.getBodyId());
				Predicate n4 = cb.equal(b.get("sectionId"), req.getSectionId());
				query.where(n1, n2, n3 , n4).orderBy(orderList);;
				
				// Get Result
				TypedQuery<MotorBodyTypeMaster> result = em.createQuery(query);
				int limit = 0 , offset = 2 ;
				result.setFirstResult(limit * offset);
				result.setMaxResults(offset);
				list = result.getResultList();

				if(list.size()>0) {
					Date beforeOneDay = new Date(new Date().getTime() - MILLIS_IN_A_DAY);
				
					if ( list.get(0).getEffectiveDateStart().before(beforeOneDay)  ) {
						amendId = list.get(0).getAmendId() + 1 ;
						entryDate = new Date() ;
						createdBy = req.getCreatedBy();
						MotorBodyTypeMaster lastRecord = list.get(0);
							lastRecord.setEffectiveDateEnd(oldEndDate);
							repo.saveAndFlush(lastRecord);
						
					} else {
						amendId = list.get(0).getAmendId() ;
						entryDate = list.get(0).getEntryDate() ;
						createdBy = list.get(0).getCreatedBy();
						saveData = list.get(0) ;
						if (list.size()>1 ) {
							MotorBodyTypeMaster lastRecord = list.get(1);
							lastRecord.setEffectiveDateEnd(oldEndDate);
							repo.saveAndFlush(lastRecord);
						}
					
				    }
				}
			
				res.setResponse("Updated Successfully ");
				res.setSuccessId(bodyId);

			}

			dozerMapper.map(req, saveData);
			saveData.setBodyId(Integer.valueOf(bodyId));
			saveData.setBodyNameEn(req.getBodyNameEn());
			saveData.setEffectiveDateStart(startDate);
			saveData.setEffectiveDateEnd(endDate);
			saveData.setCreatedBy(createdBy);
			saveData.setStatus(req.getStatus());
			saveData.setCompanyId(req.getCompanyId());
			saveData.setEntryDate(entryDate);
			saveData.setAmendId(amendId);
			saveData.setUpdatedDate(new Date());
			saveData.setBranchCode(StringUtils.isBlank(req.getBranchCode())?  "99999" :req.getBranchCode()  );
			saveData.setUpdatedBy(req.getCreatedBy());
			saveData.setCyclinders(Integer.valueOf(req.getCylinders()));
			saveData.setSeatingCapacity(Integer.valueOf(req.getSeatingCapacity()));
			saveData.setBodyNameLocal(req.getCodeDescLocal());
			saveData.setBodyType(req.getBodyType());
			repo.saveAndFlush(saveData);

//			if (list.size() > 0) {
//				// Update Old Record
//				MotorBodyTypeMaster lastRecord = list.get(0);
//				lastRecord.setEffectiveDateEnd(oldEndDate);
//				repo.saveAndFlush(lastRecord);
//			}

			log.info("Saved Details is ---> " + json.toJson(saveData));

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return res;
	}

	public Long getMasterTableCount(String companyId , String branchCode) {

		Long data = 0L;
		try {

			List<Long> list = new ArrayList<Long>();
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Long> query = cb.createQuery(Long.class);

			// Find All
			Root<MotorBodyTypeMaster> b = query.from(MotorBodyTypeMaster.class);

			// Select
			query.multiselect(cb.count(b));

			// Effective Date Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<MotorBodyTypeMaster> ocpm1 = effectiveDate.from(MotorBodyTypeMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(ocpm1.get("bodyId"), b.get("bodyId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("branchCode"), b.get("branchCode"));
			effectiveDate.where(a1,a2,a3);
			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(b.get("bodyId")));
		

			Predicate n1 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
			Predicate n2 = cb.equal(b.get("companyId"), companyId);
			Predicate n3 = cb.equal(b.get("branchCode"), branchCode);
			Predicate n4 = cb.equal(b.get("branchCode"), "99999");
			Predicate n5 = cb.or(n3,n4);
			query.where(n1,n2,n5).orderBy(orderList);
			
			// Get Result
			TypedQuery<Long> result = em.createQuery(query);
			list = result.getResultList();
			data = list.get(0);

		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());

		}
		return data;
	}
	

	@Override
	public MotorBodyTypeGetRes getMotorBody(MotorBodyTypeGetReq req) {
		MotorBodyTypeGetRes res = new MotorBodyTypeGetRes();
		DozerBeanMapper mapper = new DozerBeanMapper();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<MotorBodyTypeMaster> query = cb.createQuery(MotorBodyTypeMaster.class);
			List<MotorBodyTypeMaster> list = new ArrayList<MotorBodyTypeMaster>();

			// Find All
			Root<MotorBodyTypeMaster> c = query.from(MotorBodyTypeMaster.class);

			// Select
			query.select(c);

			// AmendId Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<MotorBodyTypeMaster> ocpm1 = amendId.from(MotorBodyTypeMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(c.get("bodyId"), ocpm1.get("bodyId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), c.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("branchCode"),c.get("branchCode"));

			amendId.where(a1,a2,a3);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("branchCode")));

			// Where
			Predicate n1 = cb.equal(c.get("amendId"), amendId);
			Predicate n2 = cb.equal(c.get("companyId"), req.getInsuranceId());
			Predicate n3 = cb.equal(c.get("branchCode"), req.getBranchCode());
			Predicate n4 = cb.equal(c.get("branchCode"), "99999");
			Predicate n5 = cb.equal(c.get("bodyId"), req.getBodyId());
			Predicate n6 = cb.or(n3,n4);
			query.where(n1,n2,n5,n6).orderBy(orderList);
		
			// Get Result
			TypedQuery<MotorBodyTypeMaster> result = em.createQuery(query);
			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getBodyId()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(MotorBodyTypeMaster :: getBodyNameEn ));
			
			res = mapper.map(list.get(0), MotorBodyTypeGetRes.class);
			res.setBodyId(list.get(0).getBodyId());
			res.setEntryDate(list.get(0).getEntryDate());
			res.setEffectiveDateStart(list.get(0).getEffectiveDateStart());
			res.setEffectiveDateEnd(list.get(0).getEffectiveDateEnd());
			res.setCylinders(list.get(0).getCyclinders()==null?"":list.get(0).getCyclinders().toString());
			res.setSeatingCapacity(list.get(0).getSeatingCapacity()==null?"":list.get(0).getSeatingCapacity().toString());
			res.setCodeDescLocal(list.get(0).getBodyNameLocal());

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return res;
	}
	private static <T> java.util.function.Predicate<T> distinctByKey(java.util.function.Function<? super T, ?> keyExtractor) {
	    Map<Object, Boolean> seen = new ConcurrentHashMap<>();
	    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}
	@Override
	public List<MotorBodyTypeGetRes> getallMotorBody(MotorBodyTypeGetAllReq req) {
		List<MotorBodyTypeGetRes> resList = new ArrayList<MotorBodyTypeGetRes>();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			List<MotorBodyTypeMaster> list = new ArrayList<MotorBodyTypeMaster>();
			// Pagination
			
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<MotorBodyTypeMaster> query = cb.createQuery(MotorBodyTypeMaster.class);

			// Find All
			Root<MotorBodyTypeMaster> b = query.from(MotorBodyTypeMaster.class);

			// Select
			query.select(b);

			// AmendId Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<MotorBodyTypeMaster> ocpm1 = amendId.from(MotorBodyTypeMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("bodyId"), b.get("bodyId"));
			Predicate a2 = cb.equal(ocpm1.get("sectionId"), b.get("sectionId"));
			Predicate a3 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a4 = cb.equal(ocpm1.get("branchCode"),b.get("branchCode"));
			amendId.where(a1,a2,a3,a4);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("branchCode")));

			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("companyId"), req.getInsuranceId());
			Predicate n3 = cb.equal(b.get("branchCode"), req.getBranchCode());
//			Predicate n4 = cb.equal(b.get("branchCode"), "99999");
//			Predicate n5 = cb.or(n3,n4);
			Predicate n6 = cb.equal(b.get("sectionId"), req.getSectionId());
			Predicate n7 = cb.equal(b.get("sectionId"), "99999");
			Predicate n8 = cb.or(n6,n7);
			query.where(n1,n2,n3,n8).orderBy(orderList);
			
			// Get Result
			TypedQuery<MotorBodyTypeMaster> result = em.createQuery(query);
		
			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getBodyId()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(MotorBodyTypeMaster :: getBodyNameEn ));
			
			// Map
			for (MotorBodyTypeMaster data : list) {
				MotorBodyTypeGetRes res = new MotorBodyTypeGetRes();

				res = mapper.map(data, MotorBodyTypeGetRes.class);
				res.setTonnage(data.getTonnage());
				res.setRemarks(data.getRemarks());;
				res.setBodyId(data.getBodyId());
				res.setCylinders(data.getCyclinders()==null?"": data.getCyclinders().toString());
				res.setSeatingCapacity(data.getSeatingCapacity()==null?"":data.getSeatingCapacity().toString());
				res.setCodeDescLocal(data.getBodyNameLocal());
				res.setBodyType(data.getBodyType());
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
	public List<MotorBodyTypeGetRes> getactiveMotorBody(MotorBodyTypeGetAllReq req) {
		List<MotorBodyTypeGetRes> resList = new ArrayList<MotorBodyTypeGetRes>();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			List<MotorBodyTypeMaster> list = new ArrayList<MotorBodyTypeMaster>();
			// Pagination
			
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<MotorBodyTypeMaster> query = cb.createQuery(MotorBodyTypeMaster.class);

			// Find All
			Root<MotorBodyTypeMaster> b = query.from(MotorBodyTypeMaster.class);

			// Select
			query.select(b);

			// AmendId Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<MotorBodyTypeMaster> ocpm1 = amendId.from(MotorBodyTypeMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("bodyId"), b.get("bodyId"));
			Predicate a2 = cb.equal(ocpm1.get("sectionId"), b.get("sectionId"));
			Predicate a3 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a4 = cb.equal(ocpm1.get("branchCode"), b.get("branchCode"));
			amendId.where(a1, a2,a3,a4);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("branchCode")));

			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("companyId"), req.getInsuranceId());
			Predicate n3 = cb.equal(b.get("branchCode"), req.getBranchCode());
			Predicate n4 = cb.equal(b.get("status"), "Y");
			Predicate n5 = cb.equal(b.get("branchCode"), "99999");
			Predicate n6 = cb.or(n3,n5);
			Predicate n7 = cb.equal(b.get("sectionId"), req.getSectionId());
			query.where(n1,n2,n4,n6,n7).orderBy(orderList);
			
			// Get Result
			TypedQuery<MotorBodyTypeMaster> result = em.createQuery(query);
			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getBodyId()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(MotorBodyTypeMaster :: getBodyNameEn ));
			
			// Map
			for (MotorBodyTypeMaster data : list) {
				MotorBodyTypeGetRes res = new MotorBodyTypeGetRes();

				res = mapper.map(data, MotorBodyTypeGetRes.class);
				res.setBodyId(data.getBodyId());
				res.setCylinders(data.getCyclinders().toString());
				res.setTonnage(data.getTonnage());
				res.setSeatingCapacity(data.getSeatingCapacity().toString());
				res.setRemarks(data.getRemarks());
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
	public SuccessRes changeStatusOfBodyType(BodyTypeChangeStatusReq req) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/YYYY");
		SuccessRes res = new SuccessRes();
		MotorBodyTypeMaster saveData = new MotorBodyTypeMaster();
		List<MotorBodyTypeMaster> list = new ArrayList<MotorBodyTypeMaster>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();

		try {
			Integer amendId = 0;
			Date startDate = req.getEffectiveDateStart();
			String end = "31/12/2022";
			Date endDate = sdf.parse(end);
			long MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;
			Date oldEndDate = new Date(req.getEffectiveDateStart().getTime() - MILLIS_IN_A_DAY);
			Date entryDate = null;
			String createdBy = "";
			String bodyId = "";

			// Update
			// Get Less than Equal Today Record
			// Criteria
			bodyId = req.getBodyId();
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<MotorBodyTypeMaster> query = cb.createQuery(MotorBodyTypeMaster.class);

			// Find All
			Root<MotorBodyTypeMaster> b = query.from(MotorBodyTypeMaster.class);

			// Select
			query.select(b);

			// Orderby
			Subquery<Long> amendId2 = query.subquery(Long.class);
			Root<MotorBodyTypeMaster> ocpm1 = amendId2.from(MotorBodyTypeMaster.class);
			amendId2.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("bodyId"), b.get("bodyId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("branchCode"), b.get("branchCode"));
			amendId2.where(a1, a2, a3);
			// Orderby
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("branchCode")));
			// Where
			Predicate n1 = cb.equal(b.get("bodyId"), req.getBodyId());
			Predicate n2 = cb.equal(b.get("companyId"), req.getInsuranceId());
			Predicate n3 = cb.equal(b.get("branchCode"), req.getBranchCode());
			Predicate n4 = cb.equal(b.get("branchCode"), "99999");
			Predicate n5 = cb.or(n3, n4);
			Predicate n6 = cb.equal(b.get("amendId"), amendId2);

			query.where(n1, n2, n5, n6).orderBy(orderList);
			// Get Result
			TypedQuery<MotorBodyTypeMaster> result = em.createQuery(query);
			int limit = 0, offset = 2;
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			list = result.getResultList();

			if (req.getBranchCode().equalsIgnoreCase(list.get(0).getBranchCode()) && list.size() > 0) {
				Date beforeOneDay = new Date(new Date().getTime() - MILLIS_IN_A_DAY);

				if (list.get(0).getEffectiveDateStart().before(beforeOneDay)) {
					amendId = list.get(0).getAmendId() + 1;
					entryDate = new Date();
					createdBy = req.getCreatedBy();
					MotorBodyTypeMaster lastRecord = list.get(0);
					lastRecord.setEffectiveDateEnd(oldEndDate);
					repo.saveAndFlush(lastRecord);

				} else {
					amendId = list.get(0).getAmendId();
					entryDate = list.get(0).getEntryDate();
					createdBy = list.get(0).getCreatedBy();
					saveData = list.get(0);
					if (req.getBranchCode().equalsIgnoreCase(list.get(0).getBranchCode()) && list.size() > 1) {
						MotorBodyTypeMaster lastRecord = list.get(1);
						lastRecord.setEffectiveDateEnd(oldEndDate);
						repo.saveAndFlush(lastRecord);
					}

				}
			}

			res.setResponse("Updated Successfully ");
			res.setSuccessId(bodyId);

			dozerMapper.map(list.get(0), saveData);
			saveData.setBodyId(Integer.valueOf(bodyId));

			saveData.setEffectiveDateStart(startDate);
			saveData.setEffectiveDateEnd(endDate);
			saveData.setCreatedBy(createdBy);
			saveData.setStatus(req.getStatus());
			saveData.setCompanyId(req.getInsuranceId());
			saveData.setEntryDate(entryDate);

			saveData.setAmendId(amendId);
			saveData.setUpdatedDate(new Date());
			saveData.setUpdatedBy(req.getCreatedBy());
			saveData.setCyclinders(list.get(0).getCyclinders());
			repo.saveAndFlush(saveData);
			res.setResponse("Status Changed");
			res.setSuccessId(req.getBodyId());
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " + e.getMessage());
			return null;
		}
		return res;
	}

	@Override
	public List<DropDownRes> getBodyTypeMasterDropdown(BodyTypeDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
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
			CriteriaQuery<MotorBodyTypeMaster> query = cb.createQuery(MotorBodyTypeMaster.class);
			List<MotorBodyTypeMaster> list = new ArrayList<MotorBodyTypeMaster>();
			// Find All
			Root<MotorBodyTypeMaster> c = query.from(MotorBodyTypeMaster.class);
			// Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("branchCode")));

			// Effective Date Start Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<MotorBodyTypeMaster> ocpm1 = effectiveDate.from(MotorBodyTypeMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(c.get("bodyId"), ocpm1.get("bodyId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a3 = cb.equal(c.get("companyId"), ocpm1.get("companyId"));
			Predicate a4 = cb.equal(c.get("branchCode"), ocpm1.get("branchCode"));
			Predicate a5 = cb.equal(c.get("sectionId"), ocpm1.get("sectionId"));
			effectiveDate.where(a1, a2,a3,a4,a5);
			// Effective Date End Max Filter
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<MotorBodyTypeMaster> ocpm2 = effectiveDate2.from(MotorBodyTypeMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate a6 = cb.equal(c.get("bodyId"), ocpm2.get("bodyId"));
			Predicate a7 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			Predicate a8 = cb.equal(c.get("companyId"), ocpm2.get("companyId"));
			Predicate a9 = cb.equal(c.get("branchCode"), ocpm2.get("branchCode"));
			Predicate a10 = cb.equal(c.get("sectionId"),  ocpm2.get("sectionId"));
			
			effectiveDate2.where(a6,a7,a8,a9,a10);
			// Where
			Predicate n1 = cb.equal(c.get("status"), "Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
		//	Predicate n4 = cb.equal(c.get("sectionId"), req.getSectionId());
			Predicate n8 = cb.equal(c.get("companyId"), req.getInsuranceId());
			Predicate n5 = cb.equal(c.get("branchCode"), req.getBranchCode());
			Predicate n6 = cb.equal(c.get("branchCode"), "99999");
			Predicate n7 = cb.or(n5,n6);
			Predicate n12 = cb.equal(c.get("status"),"R");
			Predicate n13 = cb.or(n1,n12);
			if (StringUtils.isNotBlank(req.getBodyType()) ) {
				Predicate n14 = cb.equal(c.get("bodyType"), req.getBodyType() );
				query.where(n13,n2,n3,n7,n8,n14).orderBy(orderList);
			} else {
				query.where(n13,n2,n3,n7,n8).orderBy(orderList);	
			}
			
			// Get Result
			TypedQuery<MotorBodyTypeMaster> result = em.createQuery(query);
			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getBodyId()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(MotorBodyTypeMaster :: getBodyNameEn ));
			List<DropDownRes> totalList = new ArrayList<DropDownRes>();
			
			for (MotorBodyTypeMaster data : list) {
				// Response
				DropDownRes res = new DropDownRes();
				res.setCode(data.getBodyId().toString());
				res.setCodeDesc(data.getBodyNameEn());
				res.setStatus(data.getStatus());
				res.setBodyType(data.getBodyType());
				totalList.add(res);
			}
			
			// Induvidual 
			List<String> induvidualIds = new ArrayList<String>();  
			induvidualIds.add("1");
			induvidualIds.add("2");
			induvidualIds.add("3");
			induvidualIds.add("4");
			induvidualIds.add("5");
			List<DropDownRes> induvidualList = totalList.stream().filter( o -> induvidualIds.contains(o.getCode())  ).collect(Collectors.toList());
			induvidualList.sort(Comparator.comparing( DropDownRes :: getCode));
			resList.addAll(induvidualList);
			
			// Commercial
			List<DropDownRes> commercialList = totalList.stream().filter( o -> ! induvidualIds.contains(o.getCode())  ).collect(Collectors.toList());
			commercialList.sort(Comparator.comparing( DropDownRes :: getCodeDesc));
			resList.addAll(commercialList);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return resList;
	}

	@Override
	public List<DropDownRes> getInduvidualBodyTypeMasterDropdown(BodyTypeDropDownReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
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
			CriteriaQuery<MotorBodyTypeMaster> query = cb.createQuery(MotorBodyTypeMaster.class);
			List<MotorBodyTypeMaster> list = new ArrayList<MotorBodyTypeMaster>();
			// Find All
			Root<MotorBodyTypeMaster> c = query.from(MotorBodyTypeMaster.class);
			// Select
			query.select(c);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("bodyNameEn")));

			// Effective Date Start Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<MotorBodyTypeMaster> ocpm1 = effectiveDate.from(MotorBodyTypeMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(c.get("bodyId"), ocpm1.get("bodyId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a3 = cb.equal(c.get("companyId"), ocpm1.get("companyId"));
			Predicate a4 = cb.equal(c.get("branchCode"), ocpm1.get("branchCode"));
			Predicate a5 = cb.equal(c.get("sectionId"), ocpm1.get("sectionId"));
			effectiveDate.where(a1, a2,a3,a4,a5);
			// Effective Date End Max Filter
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<MotorBodyTypeMaster> ocpm2 = effectiveDate2.from(MotorBodyTypeMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate a6 = cb.equal(c.get("bodyId"), ocpm2.get("bodyId"));
			Predicate a7 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			Predicate a8 = cb.equal(c.get("companyId"), ocpm2.get("companyId"));
			Predicate a9 = cb.equal(c.get("branchCode"), ocpm2.get("branchCode"));
			Predicate a10 = cb.equal(c.get("sectionId"),  ocpm2.get("sectionId"));
			
			effectiveDate2.where(a6,a7,a8,a9,a10);
			// Where
			Predicate n1 = cb.equal(c.get("status"), "Y");
			Predicate n2 = cb.equal(c.get("effectiveDateStart"), effectiveDate);
			Predicate n3 = cb.equal(c.get("effectiveDateEnd"), effectiveDate2);
			Predicate n4 = cb.equal(c.get("companyId"), req.getInsuranceId());
			Predicate n5 = cb.equal(c.get("branchCode"), req.getBranchCode());
			Predicate n6 = cb.equal(c.get("branchCode"), "99999");
			Predicate n7 = cb.or(n5,n6);
			if (StringUtils.isNotBlank(req.getBodyType()) ) {
				Predicate n8 = cb.equal(c.get("bodyType"), req.getBodyType() );
				query.where(n1,n2,n3,n4,n7,n8).orderBy(orderList);
			} else {
				query.where(n1,n2,n3,n4,n7).orderBy(orderList);	
			}
			
			// Get Result
			TypedQuery<MotorBodyTypeMaster> result = em.createQuery(query);
			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getBodyId()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(MotorBodyTypeMaster :: getBodyNameEn ));
			
			List<DropDownRes> totalList = new ArrayList<DropDownRes>();
			
			for (MotorBodyTypeMaster data : list) {
				// Response
				DropDownRes res = new DropDownRes();
				res.setCode(data.getBodyId().toString());
				res.setCodeDesc(data.getBodyNameEn());
				res.setStatus(data.getStatus());
				res.setBodyType(data.getBodyType());
				res.setCodeDescLocal(data.getBodyNameLocal());
				
				totalList.add(res);
			}
			resList.addAll(totalList);
			// Induvidual 
	/*		List<String> induvidualIds = new ArrayList<String>();  
			induvidualIds.add("1");
			induvidualIds.add("2");
			induvidualIds.add("3");
			induvidualIds.add("4");
			induvidualIds.add("5");
			List<DropDownRes> induvidualList = totalList.stream().filter( o -> induvidualIds.contains(o.getCode())  ).collect(Collectors.toList());
			induvidualList.sort(Comparator.comparing( DropDownRes :: getCode));
			resList.addAll(induvidualList);
			
			// Commercial
			List<DropDownRes> commercialList = totalList.stream().filter( o -> ! induvidualIds.contains(o.getCode())  ).collect(Collectors.toList());
			commercialList.sort(Comparator.comparing( DropDownRes :: getCodeDesc));
			resList.addAll(commercialList);
		*/	
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return resList;
	}
	
}
