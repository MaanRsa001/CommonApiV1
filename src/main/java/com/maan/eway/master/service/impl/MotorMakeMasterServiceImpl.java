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
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.maan.eway.bean.MotorMakeMaster;
import com.maan.eway.bean.MotorMakeModelMaster;
import com.maan.eway.master.req.MotorMakeChangeStatusReq;
import com.maan.eway.master.req.MotorMakeGetAllReq;
import com.maan.eway.master.req.MotorMakeGetReq;
import com.maan.eway.master.req.MotorMakeSaveReq;
import com.maan.eway.master.res.MotorMakeGetRes;
import com.maan.eway.master.service.MotorMakeMasterService;
import com.maan.eway.repository.MotorMakeMasterRepository;
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
public class MotorMakeMasterServiceImpl implements MotorMakeMasterService {

	@Autowired
	private MotorMakeMasterRepository repo;

	@PersistenceContext
	private EntityManager em;

	Gson json = new Gson();

	private Logger log = LogManager.getLogger(MotorMakeMasterServiceImpl.class);

	@Override
	public List<String> validateMakeMotor(MotorMakeSaveReq req) {

		List<String> errorList = new ArrayList<String>();

		try {

			if (StringUtils.isBlank(req.getMakeNameEn())) {
				//errorList.add(new Error("01", "Make Name En", "Please Enter Make Name En "));
				errorList.add("1375");
			}
			else if (req.getMakeNameEn().length()>100) {
				//errorList.add(new Error("01", "Make Name En", "Please Enter Make Name En within 100 Characters "));
				errorList.add("1376");
			}else if (StringUtils.isBlank(req.getMakeId()) &&  StringUtils.isNotBlank(req.getInsuranceId()) && StringUtils.isNotBlank(req.getBranchCode())) {
				List<MotorMakeMaster> makeList = getMakeNameEnExistDetails(req.getMakeNameEn() , req.getInsuranceId() , req.getBranchCode());
				if (makeList.size()>0 ) {
					//errorList.add(new Error("01", "Make Name En", "This Make Name Already Exist "));
					errorList.add("1377");
				}
			}else if (StringUtils.isNotBlank(req.getMakeId()) &&  StringUtils.isNotBlank(req.getInsuranceId()) && StringUtils.isNotBlank(req.getBranchCode())) {
				List<MotorMakeMaster> makeList = getMakeNameEnExistDetails(req.getMakeNameEn() , req.getInsuranceId() , req.getBranchCode());
				
				if (makeList.size()>0 &&  (! req.getMakeId().equalsIgnoreCase(makeList.get(0).getMakeId().toString())) ) {
					//errorList.add(new Error("01", "Make Name En", "This Make Name Already Exist "));
					errorList.add("1377");
				}
				
			}
			
			// Date Validation 
			Calendar cal = new GregorianCalendar();
			Date today = new Date();
			cal.setTime(today);cal.add(Calendar.DAY_OF_MONTH, -1);cal.set(Calendar.HOUR_OF_DAY, 23);cal.set(Calendar.MINUTE, 50);
			today = cal.getTime();
			if (req.getEffectiveDateStart() == null || StringUtils.isBlank(req.getEffectiveDateStart().toString())) {
				//errorList.add(new Error("02", "EffectiveDateStart", "Please Enter Effective Date Start"));
				errorList.add("1261");

			} else if (req.getEffectiveDateStart().before(today)) {
			//	errorList.add(new Error("02", "EffectiveDateStart", "Please Enter Effective Date Start as Future Date"));
				errorList.add("1262");
			}
			// Status Validation
			if (StringUtils.isBlank(req.getStatus())) {
				//errorList.add(new Error("03", "Status", "Please Enter Status"));
				errorList.add("1263");
			} else if (req.getStatus().length() > 1) {
				//errorList.add(new Error("03", "Status", "Enter Status in One Character Only"));
				errorList.add("1264");
			} else if(!("Y".equalsIgnoreCase(req.getStatus())||"N".equalsIgnoreCase(req.getStatus())||"R".equalsIgnoreCase(req.getStatus())|| "P".equalsIgnoreCase(req.getStatus()))) {
				//errorList.add(new Error("03", "Status", "Please Select Valid Status - Active or Deactive or Pending or Referral "));
				errorList.add("1265");
			}
			if (StringUtils.isNotBlank(req.getColorDesc()) && req.getColorDesc().length() > 100) {
				//errorList.add(new Error("04", "Color Desc", "Please Enter Color Desc within 100 Characters "));
				errorList.add("1378");
			}

			if (StringUtils.isBlank(req.getInsuranceId())) {
				//errorList.add(new Error("05", "InsuranceId", "Please Enter InsuranceId"));
				errorList.add("1255");
			}

			if (StringUtils.isBlank(req.getBranchCode())) {
				//errorList.add(new Error("06", "BranchCode", "Please Select BranchCode"));
				errorList.add("1256");
			}
			 
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return errorList;
	}
	public List<MotorMakeMaster> getMakeNameEnExistDetails(String makeNameEn , String InsuranceId , String branchCode) {
		List<MotorMakeMaster> list = new ArrayList<MotorMakeMaster>();
		try {
			Date today = new Date();
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<MotorMakeMaster> query = cb.createQuery(MotorMakeMaster.class);

			// Find All
			Root<MotorMakeMaster> b = query.from(MotorMakeMaster.class);

			// Select
			query.select(b);

			// AmendId Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<MotorMakeMaster> ocpm1 = amendId.from(MotorMakeMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("makeId"), b.get("makeId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("branchCode"), b.get("branchCode"));
			Predicate a4 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a5 = cb.greaterThanOrEqualTo(ocpm1.get("effectiveDateEnd"), today);
			amendId.where(a1,a2,a3,a4,a5);

			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(cb.lower( b.get("makeNameEn")), makeNameEn.toLowerCase());
			Predicate n3 = cb.equal(b.get("companyId"),InsuranceId);
			Predicate n4 = cb.equal(b.get("branchCode"), branchCode);
			Predicate n5 = cb.equal(b.get("branchCode"), "99999");
			Predicate n6 = cb.or(n4,n5);
			query.where(n1,n2,n3,n6);
			
			// Get Result
			TypedQuery<MotorMakeMaster> result = em.createQuery(query);
			list = result.getResultList();		
		
		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());

		}
		return list;
	}

	@Override
	public SuccessRes saveMakeMotor(MotorMakeSaveReq req) {
		SimpleDateFormat sdformat = new SimpleDateFormat("dd/MM/YYYY");
		SuccessRes res = new SuccessRes();
		MotorMakeMaster saveData = new MotorMakeMaster();
		List<MotorMakeMaster> list = new ArrayList<MotorMakeMaster>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();

		try {
			Integer amendId=0;
			Date startDate = req.getEffectiveDateStart() ;
			String end = "31/12/2050";
			Date endDate = sdformat.parse(end);
			long MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;
			Date oldEndDate = new Date(req.getEffectiveDateStart().getTime() - MILLIS_IN_A_DAY);
			Date entryDate = null ;
			String createdBy = "" ;
			
			String makeId = "";

			if (StringUtils.isBlank(req.getMakeId())) {
				// Save
				// Long totalCount = repo.count();
				Integer totalCount = getMasterTableCount(req.getInsuranceId() , req.getBranchCode());
				makeId = Integer.valueOf(totalCount + 1).toString();
				entryDate = new Date();
				createdBy = req.getCreatedBy();
				res.setResponse("Saved Successfully ");
				res.setSuccessId(makeId);

			} else {
				// Update
				// Get Less than Equal Today Record
				// Criteria
				makeId = req.getMakeId().toString();
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<MotorMakeMaster> query = cb.createQuery(MotorMakeMaster.class);

				// Find All
				Root<MotorMakeMaster> b = query.from(MotorMakeMaster.class);

				// Select
				query.select(b);

//				// Effective Date Max Filter
//				Subquery<Long> effectiveDate = query.subquery(Long.class);
//				Root<MotorMakeMaster> ocpm1 = effectiveDate.from(MotorMakeMaster.class);
//				effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
//				Predicate a1 = cb.equal(ocpm1.get("makeId"), b.get("makeId"));
//				Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), startDate);
//				
//				effectiveDate.where(a1, a2);
//
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.desc(b.get("effectiveDateStart")));
				

				// Where
				//Predicate n1 = cb.equal(b.get("status"), "Y");
				//Predicate n2 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
				Predicate n3 = cb.equal(b.get("makeId"), req.getMakeId());
				Predicate n4 = cb.equal(b.get("companyId"), req.getInsuranceId());
				Predicate n5 = cb.equal(b.get("branchCode"), req.getBranchCode());
				
				query.where(n5,n3,n4).orderBy(orderList);
				

								
				// Get Result
				TypedQuery<MotorMakeMaster> result = em.createQuery(query);
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
							MotorMakeMaster lastRecord = list.get(0);
							lastRecord.setEffectiveDateEnd(oldEndDate);
							repo.saveAndFlush(lastRecord);
						
					} else {
						amendId = list.get(0).getAmendId() ;
						entryDate = list.get(0).getEntryDate() ;
						createdBy = list.get(0).getCreatedBy();
						saveData = list.get(0) ;
						if (list.size()>1 ) {
							MotorMakeMaster lastRecord = list.get(1);
							lastRecord.setEffectiveDateEnd(oldEndDate);
							repo.saveAndFlush(lastRecord);
						}
					
				    }
				}
				
				res.setResponse("Updated Successfully ");
				res.setSuccessId(makeId);

			}

			dozerMapper.map(req, saveData);
			saveData.setMakeId(Integer.valueOf(makeId));
			saveData.setMakeNameEn(req.getMakeNameEn());
			saveData.setEffectiveDateStart(startDate);
			saveData.setEffectiveDateEnd(endDate);
			saveData.setEntryDate(entryDate);
			saveData.setCreatedBy(createdBy);
			saveData.setStatus(req.getStatus());
			saveData.setUpdatedDate(new Date());
			saveData.setUpdatedBy(req.getCreatedBy());
			saveData.setCompanyId(req.getInsuranceId());
			saveData.setAmendId(amendId);
			saveData.setMakeNameLocal(req.getCodeDescLocal());
			repo.saveAndFlush(saveData);

			

			log.info("Saved Details is ---> " + json.toJson(saveData));

		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return res;
	}

	public Integer getMasterTableCount(String companyId , String branchCode) {

		Integer data = 0;
		try {

			List<MotorMakeMaster> list = new ArrayList<MotorMakeMaster>();
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<MotorMakeMaster> query = cb.createQuery(MotorMakeMaster.class);

			// Find All
			Root<MotorMakeMaster> b = query.from(MotorMakeMaster.class);

			// Select
			query.select(b);

			// Effective Date Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<MotorMakeMaster> ocpm1 = effectiveDate.from(MotorMakeMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(ocpm1.get("makeId"), b.get("makeId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("branchCode"), b.get("branchCode"));
			effectiveDate.where(a1,a2,a3);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(b.get("makeId")));
			
			Predicate n1 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
			Predicate n2 = cb.equal(b.get("companyId"), companyId);
			Predicate n3 = cb.equal(b.get("branchCode"), branchCode);
			Predicate n4 = cb.equal(b.get("branchCode"), "99999");
			Predicate n5 = cb.or(n3,n4);
			query.where(n1,n2,n5).orderBy(orderList);
			
			// Get Result
			TypedQuery<MotorMakeMaster> result = em.createQuery(query);
			int limit = 0 , offset = 1 ;
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			list = result.getResultList();
			data = list.size() > 0 ? list.get(0).getMakeId() : 0 ;
		

		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());

		}
		return data;
	}
	
	@Override
	public MotorMakeGetRes getMakeId(MotorMakeGetReq req) {
		MotorMakeGetRes res = new MotorMakeGetRes();
		ModelMapper mapper = new ModelMapper();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

		try {
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<MotorMakeMaster> query = cb.createQuery(MotorMakeMaster.class);
			List<MotorMakeMaster> list = new ArrayList<MotorMakeMaster>();

			// Find All
			Root<MotorMakeMaster> c = query.from(MotorMakeMaster.class);

			// Select
			query.select(c);

			// AmendId Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<MotorMakeMaster> ocpm1 = amendId.from(MotorMakeMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("makeId"), c.get("makeId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), c.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("branchCode"), c.get("branchCode"));
			amendId.where(a1, a2, a3);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("branchCode")));

			// Where
			Predicate n6 = cb.equal(c.get("amendId"), amendId);
			Predicate n1 = cb.equal(c.get("companyId"), req.getInsuranceId());
			Predicate n2 = cb.equal(c.get("branchCode"), req.getBranchCode());
			Predicate n3 = cb.equal(c.get("makeId"), req.getMakeId());
			Predicate n4 = cb.equal(c.get("branchCode"), "99999");
			Predicate n5 = cb.or(n2,n4);
			query.where(n1,n3,n5,n6).orderBy(orderList);
			
			// Get Result
			TypedQuery<MotorMakeMaster> result = em.createQuery(query);
			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getMakeId()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(MotorMakeMaster :: getMakeNameEn ));
			
			res = mapper.map(list.get(0), MotorMakeGetRes.class);
			res.setMakeId(list.get(0).getMakeId());
			res.setEntryDate(list.get(0).getEntryDate());
			res.setEffectiveDateStart(list.get(0).getEffectiveDateStart());
			res.setEffectiveDateEnd(list.get(0).getEffectiveDateEnd());
			res.setBranchCode(list.get(0).getBranchCode()==null?"":list.get(0).getBranchCode());
			res.setCodeDescLocal(list.get(0).getMakeNameLocal());
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
	public List<MotorMakeGetRes> getallMotorMake(MotorMakeGetAllReq req) {
		List<MotorMakeGetRes> resList = new ArrayList<MotorMakeGetRes>();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			List<MotorMakeMaster> list = new ArrayList<MotorMakeMaster>();
			
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<MotorMakeMaster> query = cb.createQuery(MotorMakeMaster.class);

			// Find All
			Root<MotorMakeMaster> b = query.from(MotorMakeMaster.class);

			// Select
			query.select(b);

			// AmendId Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<MotorMakeMaster> ocpm1 = amendId.from(MotorMakeMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("makeId"), b.get("makeId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("branchCode"), b.get("branchCode"));
			amendId.where(a1, a2, a3);
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("branchCode")));

			Predicate n6 = cb.equal(b.get("amendId"), amendId);
			Predicate n1 = cb.equal(b.get("companyId"), req.getInsuranceId());
			Predicate n2 = cb.equal(b.get("branchCode"), req.getBranchCode());
		//	Predicate n4 = cb.equal(b.get("branchCode"), "99999");
		//	Predicate n5 = cb.or(n2,n4);
			query.where(n1,n2,n6).orderBy(orderList);

			// Get Result
			TypedQuery<MotorMakeMaster> result = em.createQuery(query);
			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getMakeId()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(MotorMakeMaster :: getMakeNameEn ));


			// Map
			for (MotorMakeMaster data : list) {
				MotorMakeGetRes res = new MotorMakeGetRes();

				res = mapper.map(data, MotorMakeGetRes.class);
				res.setMakeId(data.getMakeId());
				res.setCodeDescLocal(data.getMakeNameLocal());
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
	public List<MotorMakeGetRes> getactiveMotorMake(MotorMakeGetAllReq req) {
		List<MotorMakeGetRes> resList = new ArrayList<MotorMakeGetRes>();
		
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			List<MotorMakeMaster> list = new ArrayList<MotorMakeMaster>();
		
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<MotorMakeMaster> query = cb.createQuery(MotorMakeMaster.class);

			// Find All
			Root<MotorMakeMaster> b = query.from(MotorMakeMaster.class);

			// Select
			query.select(b);

			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<MotorMakeMaster> ocpm1 = amendId.from(MotorMakeMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("makeId"), b.get("makeId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("branchCode"),b.get("branchCode"));

			amendId.where(a1, a2,a3);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("branchCode")));

			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("companyId"), req.getInsuranceId());
			Predicate n3 = cb.equal(b.get("branchCode"), req.getBranchCode());
			Predicate n4 = cb.equal(b.get("status"), "Y");
			//Predicate n5 = cb.equal(b.get("branchCode"), "99999");
			//Predicate n6 = cb.or(n3,n5);
			query.where(n1,n2,n4,n3).orderBy(orderList);
			
			// Get Result
			TypedQuery<MotorMakeMaster> result = em.createQuery(query);
			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getMakeId()))).collect(Collectors.toList());
			//list.sort(Comparator.comparing(MotorMakeMaster :: getMakeNameEn ));
			// Map
			for (MotorMakeMaster data : list) {
				MotorMakeGetRes res = new MotorMakeGetRes();

				res = mapper.map(data, MotorMakeGetRes.class);
			

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
	public List<DropDownRes> getMotorMakeDropdown(MotorMakeGetAllReq req) {
		List<DropDownRes> resList = new ArrayList<DropDownRes>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
//			cal.set(Calendar.HOUR_OF_DAY, 1);;
//			cal.set(Calendar.MINUTE, 1);
//			today = cal.getTime();
//			cal.set(Calendar.HOUR_OF_DAY, 23);
//			cal.set(Calendar.MINUTE, 59);
//			Date todayEnd = cal.getTime();
			today = cal.getTime();
			Date todayEnd = cal.getTime();
			
			List<String> induvidualIds = new ArrayList<String>();  
			induvidualIds.add("1");
			induvidualIds.add("2");
			induvidualIds.add("3");
			induvidualIds.add("4");
			induvidualIds.add("5");
			
			if(StringUtils.isNotBlank(req.getBodyId() ) &&  induvidualIds.contains(req.getBodyId()) ) {
				// Criteria
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<MotorMakeModelMaster> query=  cb.createQuery(MotorMakeModelMaster.class);
				List<MotorMakeModelMaster> list = new ArrayList<MotorMakeModelMaster>();
				// Find All
				Root<MotorMakeModelMaster> c = query.from(MotorMakeModelMaster.class);
				//Select
				query.select(c);
				
				
				// Effective Date Start Max Filter
				Subquery<Date> effectiveDate = query.subquery(Date.class);
				Root<MotorMakeModelMaster> ocpm1 = effectiveDate.from(MotorMakeModelMaster.class);
				effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
				Predicate a1 = cb.equal(c.get("makeId"),ocpm1.get("makeId"));
				Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
				Predicate a5 = cb.equal(c.get("companyId"),ocpm1.get("companyId"));
				Predicate a6 = cb.equal(c.get("branchCode"),ocpm1.get("branchCode"));
				Predicate a9 = cb.equal(c.get("bodyId"),ocpm1.get("bodyId"));
				Predicate a11 = cb.equal(c.get("modelId"),ocpm1.get("modelId"));
				effectiveDate.where(a1,a2,a5,a6,a9,a11);
				// Effective Date End Max Filter
				Subquery<Date> effectiveDate2 = query.subquery(Date.class);
				Root<MotorMakeModelMaster> ocpm2 = effectiveDate2.from(MotorMakeModelMaster.class);
				effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
				Predicate a3 = cb.equal(c.get("makeId"),ocpm2.get("makeId"));
				Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
				Predicate a7 = cb.equal(c.get("companyId"),ocpm2.get("companyId"));
				Predicate a8 = cb.equal(c.get("branchCode"),ocpm2.get("branchCode"));
				Predicate a10 = cb.equal(c.get("bodyId"),ocpm2.get("bodyId"));
				Predicate a12 = cb.equal(c.get("modelId"),ocpm2.get("modelId"));
				effectiveDate2.where(a3,a4,a7,a8,a10,a12);
				
				// Make EffectiveDate condition
				// Effective Date Start Max Filter
				Subquery<Long> makeId = query.subquery(Long.class);
				Root<MotorMakeMaster> m = makeId.from(MotorMakeMaster.class);
				
				Subquery<Date> effectiveDate3 = makeId.subquery(Date.class);
				Root<MotorMakeMaster> ocpm3 = effectiveDate3.from(MotorMakeMaster.class);
				effectiveDate3.select(cb.greatest(ocpm3.get("effectiveDateStart").as(Date.class)));
				Predicate a13 = cb.equal(m.get("makeId"),ocpm3.get("makeId"));
				Predicate a14 = cb.lessThanOrEqualTo(ocpm3.get("effectiveDateStart"), today);
				Predicate a15 = cb.equal(m.get("companyId"),ocpm3.get("companyId"));
				Predicate a16 = cb.equal(m.get("branchCode"),ocpm3.get("branchCode"));
				effectiveDate3.where(a13,a14,a15,a16);
				// Effective Date End Max Filter
				Subquery<Date> effectiveDate4 = makeId.subquery(Date.class);
				Root<MotorMakeMaster> ocpm4 = effectiveDate4.from(MotorMakeMaster.class);
				effectiveDate4.select(cb.greatest(ocpm4.get("effectiveDateEnd").as(Date.class)));
				Predicate a17 = cb.equal(m.get("makeId"),ocpm4.get("makeId"));
				Predicate a18 = cb.greaterThanOrEqualTo(ocpm4.get("effectiveDateEnd"), todayEnd);
				Predicate a19 = cb.equal(m.get("companyId"),ocpm4.get("companyId"));
				Predicate a20 = cb.equal(m.get("branchCode"),ocpm4.get("branchCode"));
				effectiveDate4.where(a17,a18,a19,a20);
				
				makeId.select(cb.max(m.get("makeId")));
				Predicate m1 = cb.equal(m.get("effectiveDateStart"),effectiveDate3);
				Predicate m2 = cb.equal(m.get("effectiveDateEnd"),effectiveDate4);
				Predicate m3 = cb.equal(m.get("makeId"),c.get("makeId"));
				Predicate m4 = cb.equal(m.get("companyId"),c.get("companyId"));
				Predicate m5 = cb.equal(m.get("branchCode"),c.get("branchCode"));
				Predicate m6 = cb.equal(m.get("status"),"Y");
				Predicate m7 = cb.equal(m.get("status"),"R");
				Predicate m8 = cb.or(m6,m7);
				makeId.where(m1,m2,m3,m4,m5,m8);
				
				// Order By
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.asc(c.get("makeNameEn")));
				
				// Where
				Predicate n1 = cb.equal(c.get("status"),"Y");
				Predicate n8 = cb.equal(c.get("status"),"R");
				Predicate n9 = cb.or(n1,n8);
				Predicate n2 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
				Predicate n3 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);
				Predicate n4 = cb.equal(c.get("companyId"), req.getInsuranceId());
				Predicate n5 = cb.equal(c.get("branchCode"), req.getBranchCode());
				Predicate n6 = cb.equal(c.get("branchCode"), "99999");
				Predicate n7 = cb.or(n5,n6);
			//	Predicate n10 = cb.equal(c.get("bodyId"), req.getBodyId());
				Predicate n11 = cb.equal(c.get("makeId"),makeId);
				
				//query.where(n9,n2,n3,n4,n7,n10,n11).orderBy(orderList);
				query.where(n9,n2,n3,n4,n7,n11).orderBy(orderList);

				
				// Get Result
				TypedQuery<MotorMakeModelMaster> result = em.createQuery(query);
				list = result.getResultList();
				list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getMakeId()))).collect(Collectors.toList());
				list.stream().filter( o -> o.getMakeNameEn()!=null ).collect(Collectors.toList()).sort(Comparator.comparing(MotorMakeModelMaster :: getMakeNameEn ));
				
				for (MotorMakeModelMaster data : list) {  
					// Response 
					DropDownRes res = new DropDownRes();
					res.setCode(data.getMakeId().toString());
					res.setCodeDesc(data.getMakeNameEn());
					res.setCodeDescLocal(data.getMakeNameLocal());
					res.setStatus(data.getStatus());
					resList.add(res);
				}
				
			} else {
				// Criteria
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<MotorMakeMaster> query=  cb.createQuery(MotorMakeMaster.class);
				List<MotorMakeMaster> list = new ArrayList<MotorMakeMaster>();
				// Find All
				Root<MotorMakeMaster> c = query.from(MotorMakeMaster.class);
				//Select
				query.select(c);
				
				
				// Effective Date Start Max Filter
				Subquery<Date> effectiveDate = query.subquery(Date.class);
				Root<MotorMakeMaster> ocpm1 = effectiveDate.from(MotorMakeMaster.class);
				effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
				Predicate a1 = cb.equal(c.get("makeId"),ocpm1.get("makeId"));
				Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
				Predicate a5 = cb.equal(c.get("companyId"),ocpm1.get("companyId"));
				Predicate a6 = cb.equal(c.get("branchCode"),ocpm1.get("branchCode"));
				effectiveDate.where(a1,a2,a5,a6);
				// Effective Date End Max Filter
				Subquery<Date> effectiveDate2 = query.subquery(Date.class);
				Root<MotorMakeMaster> ocpm2 = effectiveDate2.from(MotorMakeMaster.class);
				effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
				Predicate a3 = cb.equal(c.get("makeId"),ocpm2.get("makeId"));
				Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
				Predicate a7 = cb.equal(c.get("companyId"),ocpm2.get("companyId"));
				Predicate a8 = cb.equal(c.get("branchCode"),ocpm2.get("branchCode"));
				effectiveDate2.where(a3,a4,a7,a8);
				
				// Order By
				List<Order> orderList = new ArrayList<Order>();
				orderList.add(cb.asc(c.get("branchCode")));
				
				// Where
				Predicate n1 = cb.equal(c.get("status"),"Y");
				Predicate n8 = cb.equal(c.get("status"),"R");
				Predicate n9 = cb.or(n1,n8);
				Predicate n2 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
				Predicate n3 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);
				Predicate n4 = cb.equal(c.get("companyId"), req.getInsuranceId());
				Predicate n5 = cb.equal(c.get("branchCode"), req.getBranchCode());
				Predicate n6 = cb.equal(c.get("branchCode"), "99999");
				Predicate n7 = cb.or(n5,n6);
				query.where(n9,n2,n3,n4,n7).orderBy(orderList);
					
				// Get Result
				TypedQuery<MotorMakeMaster> result = em.createQuery(query);
				list = result.getResultList();
				list.stream().filter( o -> o.getMakeNameEn()!=null ).collect(Collectors.toList()).sort(Comparator.comparing(MotorMakeMaster :: getMakeNameEn ));
				for (MotorMakeMaster data : list) {
					// Response 
					DropDownRes res = new DropDownRes();
					res.setCode(data.getMakeId().toString());
					res.setCodeDesc(data.getMakeNameEn());
					res.setCodeDescLocal(data.getMakeNameLocal());
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

	@Override
	public SuccessRes changeStatusOfMotorMake(MotorMakeChangeStatusReq req) {
		SimpleDateFormat sdformat = new SimpleDateFormat("dd/MM/YYYY");
		SuccessRes res = new SuccessRes();
		MotorMakeMaster saveData = new MotorMakeMaster();
		List<MotorMakeMaster> list = new ArrayList<MotorMakeMaster>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();

		try {
			Integer amendId=0;
			Date startDate = req.getEffectiveDateStart() ;
			String end = "31/12/2050";
			Date endDate = sdformat.parse(end);
			long MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;
			Date oldEndDate = new Date(req.getEffectiveDateStart().getTime() - MILLIS_IN_A_DAY);
			Date entryDate = null;
			String createdBy = "";

			String makeId = "";

			// Update
			// Get Less than Equal Today Record
			// Criteria
			makeId = req.getMakeId().toString();
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<MotorMakeMaster> query = cb.createQuery(MotorMakeMaster.class);

			// Find All
			Root<MotorMakeMaster> b = query.from(MotorMakeMaster.class);

			// Select
			query.select(b);

//						//Orderby
			Subquery<Long> amendId2 = query.subquery(Long.class);
			Root<MotorMakeMaster> ocpm1 = amendId2.from(MotorMakeMaster.class);
			amendId2.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("makeId"), b.get("makeId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("branchCode"), b.get("branchCode"));
			amendId2.where(a1, a2, a3);
			// Orderby
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("branchCode")));
			// Where
			Predicate n1 = cb.equal(b.get("makeId"), req.getMakeId());
			Predicate n2 = cb.equal(b.get("companyId"), req.getInsuranceId());
			Predicate n3 = cb.equal(b.get("branchCode"), req.getBranchCode());
			Predicate n4 = cb.equal(b.get("branchCode"), "99999");
			Predicate n5 = cb.or(n3, n4);
			Predicate n6 = cb.equal(b.get("amendId"), amendId2);

			query.where(n1, n2, n5, n6).orderBy(orderList);

			// Get Result
			TypedQuery<MotorMakeMaster> result = em.createQuery(query);
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
					MotorMakeMaster lastRecord = list.get(0);
					lastRecord.setEffectiveDateEnd(oldEndDate);
					repo.saveAndFlush(lastRecord);

				} else {
					amendId = list.get(0).getAmendId();
					entryDate = list.get(0).getEntryDate();
					createdBy = list.get(0).getCreatedBy();
					saveData = list.get(0);
					if (req.getBranchCode().equalsIgnoreCase(list.get(0).getBranchCode()) && list.size() > 1) {
						MotorMakeMaster lastRecord = list.get(1);
						lastRecord.setEffectiveDateEnd(oldEndDate);
						repo.saveAndFlush(lastRecord);
					}

				}
			}

			res.setResponse("Updated Successfully ");
			res.setSuccessId(makeId);

			dozerMapper.map(list.get(0), saveData);
			saveData.setMakeId(Integer.valueOf(makeId));
			saveData.setEffectiveDateStart(startDate);
			saveData.setEffectiveDateEnd(endDate);
			saveData.setEntryDate(entryDate);
			saveData.setCreatedBy(createdBy);
			saveData.setStatus(req.getStatus());
			saveData.setUpdatedDate(new Date());
			saveData.setUpdatedBy(req.getCreatedBy());
			saveData.setCompanyId(req.getInsuranceId());
			saveData.setAmendId(amendId);
			repo.saveAndFlush(saveData);
			

			log.info("Saved Details is ---> " + json.toJson(saveData));
			// Perform Update
			res.setResponse("Status Changed");
			res.setSuccessId(req.getMakeId());
		}
		catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " + e.getMessage());
			return null;
			}
		return res;
	}



	
}
