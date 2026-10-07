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
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.LoginMaster;
import com.maan.eway.bean.PaymentMaster;
import com.maan.eway.master.req.PaymentMasterChangeStatusReq;
import com.maan.eway.master.req.PaymentMasterDropdownReq;
import com.maan.eway.master.req.PaymentMasterGetReq;
import com.maan.eway.master.req.PaymentMasterGetallReq;
import com.maan.eway.master.req.PaymentMasterSaveReq;
import com.maan.eway.master.res.PaymentMasterDropDownRes;
import com.maan.eway.master.res.PaymentMasterRes;
import com.maan.eway.master.service.PaymentMasterService;
import com.maan.eway.repository.LoginMasterRepository;
import com.maan.eway.repository.PaymentMasterRepository;
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
public class PaymentMasterServiceImpl implements PaymentMasterService {

	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private PaymentMasterRepository repo;

	@Autowired
	private LoginMasterRepository loginRepo ;

	
	Gson json = new Gson();
	
	private Logger log = LogManager.getLogger(PaymentMasterServiceImpl.class);
	
	@Override
	public List<String> validatePaymentMaster(List<PaymentMasterSaveReq> reqlist) {
		List<String> errorList = new ArrayList<String>();
		try {

			for (PaymentMasterSaveReq req : reqlist) {
				if (StringUtils.isBlank(req.getCompanyId())) {
					// errorList.add(new Error("02", "CompanyId", "Please Enter CompanyId"));
					errorList.add("1255");
				}

				if (StringUtils.isBlank(req.getBranchCode())) {
					// errorList.add(new Error("02", "BranchCode", "Please Select BranchCode"));
					errorList.add("1256");
				}
				if (StringUtils.isBlank(req.getProductId())) {
					// errorList.add(new Error("02", "ProductId", "Please Select ProductId"));
					errorList.add("1313");
				}

				// Date Validation
				Calendar cal = new GregorianCalendar();
				Date today = new Date();
				cal.setTime(today);
				cal.add(Calendar.DAY_OF_MONTH, -1);
				;
				today = cal.getTime();
				if (req.getEffectiveDateStart() == null
						|| StringUtils.isBlank(req.getEffectiveDateStart().toString())) {
					// errorList.add(new Error("05", "EffectiveDateStart", "Please Enter Effective
					// Date Start"));
					errorList.add("1261");

				} else if (req.getEffectiveDateStart().before(today)) {
					// errorList.add(new Error("05", "EffectiveDateStart", "Please Enter Effective
					// Date Start as Future Date"));
					errorList.add("1262");
				}
				// Status Validation
				if (StringUtils.isBlank(req.getStatus())) {
					// errorList.add(new Error("05", "Status", "Please Select Status "));
					errorList.add("1263");
				} else if (req.getStatus().length() > 1) {
					// errorList.add(new Error("05", "Status", "Please Select Valid Status - One
					// Character Only Allwed"));
					errorList.add("1264");
				} else if (!("Y".equalsIgnoreCase(req.getStatus()) || "N".equalsIgnoreCase(req.getStatus())
						|| "R".equalsIgnoreCase(req.getStatus()) || "P".equalsIgnoreCase(req.getStatus()))) {
					// errorList.add(new Error("05", "Status", "Please Select Valid Status - Active
					// or Deactive or Pending or Referral "));
					errorList.add("1265");
				}

				if (StringUtils.isBlank(req.getCreatedBy())) {
					// errorList.add(new Error("09", "CreatedBy", "Please Select CreatedBy"));
					errorList.add("1270");
				} else if (req.getCreatedBy().length() > 100) {
					// errorList.add(new Error("09","CreatedBy", "Please Enter CreatedBy within 100
					// Characters"));
					errorList.add("1271");
				}

				String agencyCode = StringUtils.isNotBlank(req.getAgencyCode()) ? req.getAgencyCode() : "99999";
				List<PaymentMaster> datas = repo
						.findByCompanyIdAndBranchCodeAndAgencyCodeAndEffectiveDateStartOrderByEntryDateDesc(
								req.getCompanyId(), req.getBranchCode(),	agencyCode, req.getEffectiveDateStart());
				if (datas != null && datas.size() > 0) {
					if (StringUtils.isBlank(req.getPaymentMasterId())) {
						if (datas != null && datas.size() > 0) {

							if ((datas.get(0).getBranchCode().equalsIgnoreCase(req.getBranchCode()))
									&& (datas.get(0).getCashYn().toLowerCase()
											.equalsIgnoreCase(req.getCashYn().toLowerCase()))
									&& (datas.get(0).getChequeYn().toLowerCase()
											.equalsIgnoreCase(req.getChequeYn().toLowerCase()))
									&& (datas.get(0).getCompanyId().equalsIgnoreCase(req.getCompanyId().toLowerCase()))
									&& (datas.get(0).getCreditYn().toLowerCase()
											.equalsIgnoreCase(req.getCreditYn().toLowerCase()))
									&& (datas.get(0).getStatus().toLowerCase()
											.equalsIgnoreCase(req.getStatus().toLowerCase()))
//									&& (datas.get(0).getSubUserType().toLowerCase()
//											.equalsIgnoreCase(req.getSubUserType().toLowerCase()))
//									&& (datas.get(0).getUserType().toLowerCase()
//											.equalsIgnoreCase(req.getUserType().toLowerCase()))
									&& (datas.get(0).getCashYn().toLowerCase()
											.equalsIgnoreCase(req.getCashYn().toLowerCase()))

							)

							{
								// errorList.add(new Error("10","Duplicate Data", "Already Data Available for
								// the same, It is a Duplicate Data"));
								errorList.add("1676");
							}
						}
					}
				}
			}
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return errorList;
	}
	
	@Override
	public SuccessRes savePaymentMaster(List<PaymentMasterSaveReq> reqList) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		SuccessRes res = new SuccessRes();
		PaymentMaster saveData = new PaymentMaster();
		List<PaymentMaster> list = new ArrayList<PaymentMaster>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			for (PaymentMasterSaveReq req : reqList) {
				Integer amendId = 0;
				Date startDate = req.getEffectiveDateStart();
				String end = "31/12/2050";
				Date endDate = sdf.parse(end);
				long MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;
				Date oldEndDate = new Date(req.getEffectiveDateStart().getTime() - MILLIS_IN_A_DAY);
				Date entryDate = null;
				String createdBy = "";
				Integer paymentId = 0;
				if (StringUtils.isBlank(req.getPaymentMasterId())) {
					Integer totalCount = getMasterTableCount(req.getCompanyId(), req.getBranchCode(),
							req.getAgencyCode());
					paymentId = totalCount + 1;
					entryDate = new Date();
					createdBy = req.getCreatedBy();
					res.setResponse("Saved Successfully");
					res.setSuccessId(paymentId.toString());
				} else {
					paymentId = Integer.valueOf(req.getPaymentMasterId());
					CriteriaBuilder cb = em.getCriteriaBuilder();
					CriteriaQuery<PaymentMaster> query = cb.createQuery(PaymentMaster.class);
					// Findall
					Root<PaymentMaster> b = query.from(PaymentMaster.class);
					// select
					query.select(b);
					// Orderby
					List<Order> orderList = new ArrayList<Order>();
					orderList.add(cb.desc(b.get("amendId")));
					// Where
					Predicate n1 = cb.equal(b.get("paymentMasterId"), req.getPaymentMasterId());
					Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
					Predicate n3 = cb.equal(b.get("branchCode"), req.getBranchCode());
					Predicate n4 = cb.equal(b.get("productId"), req.getProductId());
					String agencyCode = StringUtils.isNotBlank(req.getAgencyCode()) ? req.getAgencyCode() : "99999";
					Predicate n13 = cb.equal(b.get("agencyCode"), agencyCode);
					query.where(n1, n2, n3, n4, n13).orderBy(orderList);

					// Get Result
					TypedQuery<PaymentMaster> result = em.createQuery(query);
					int limit = 0, offset = 2;
					result.setFirstResult(limit * offset);
					result.setMaxResults(offset);
					list = result.getResultList();

					if (list.size() > 0) {
						Date beforeOneDay = new Date(new Date().getTime());

						if (list.get(0).getEffectiveDateStart().before(beforeOneDay)
						        && list.get(0).getEffectiveDateEnd().after(beforeOneDay)) {
							amendId = list.get(0).getAmendId() + 1;
							entryDate = new Date();
							createdBy = req.getCreatedBy();
							PaymentMaster lastRecord = list.get(0);
							lastRecord.setEffectiveDateEnd(oldEndDate);
							repo.saveAndFlush(lastRecord);

						} else {
							amendId = list.get(0).getAmendId();
							entryDate = list.get(0).getEntryDate();
							createdBy = list.get(0).getCreatedBy();
							// saveData = list.get(0) ;
							if (list.size() > 1) {
								PaymentMaster lastRecord = list.get(1);
								lastRecord.setEffectiveDateEnd(oldEndDate);
								repo.saveAndFlush(lastRecord);
							}

						}
					}
					res.setResponse("Updated Successfully");
					res.setSuccessId(paymentId.toString());
				}

				LoginMaster loginMaster = loginRepo
						.findByAgencyCode(StringUtils.isBlank(req.getAgencyCode()) ? "99999" : req.getAgencyCode());
				dozerMapper.map(req, saveData);
				saveData.setUserType(loginMaster != null ? loginMaster.getUserType() : "99999");
				saveData.setSubUserType(loginMaster != null ? loginMaster.getSubUserType() : "99999");
				saveData.setPaymentMasterId(paymentId);
				saveData.setEffectiveDateStart(startDate);
				saveData.setEffectiveDateEnd(endDate);
				saveData.setCreatedBy(createdBy);
				saveData.setEntryDate(entryDate);
				saveData.setUpdatedBy(req.getCreatedBy());
				saveData.setUpdatedDate(new Date());
				saveData.setAmendId(amendId);
				saveData.setBranchCode(req.getBranchCode() == null ? "99999" : req.getBranchCode());
				saveData.setCompanyId(req.getCompanyId() == null ? "99999" : req.getCompanyId());
				saveData.setOaCode(StringUtils.isBlank(req.getOaCode()) ? "99999" : req.getOaCode());
				saveData.setAgencyCode(StringUtils.isBlank(req.getAgencyCode()) ? "99999" : req.getAgencyCode());
				repo.saveAndFlush(saveData);
			}
			log.info("Saved Details is --> " + json.toJson(saveData));
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " + e.getMessage());
			return null;
		}
		return res;
	}
	
	
public Integer getMasterTableCount(String companyId, String branchCode , String agencyCode)	{

	Integer data =0;
	try {
		List<PaymentMaster> list = new ArrayList<PaymentMaster>();
		// Find Latest Record
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PaymentMaster> query = cb.createQuery(PaymentMaster.class);
		//Find all
		Root<PaymentMaster> b = query.from(PaymentMaster.class);
		// Select
		query.select(b);
		// Effective Date Max Filter
		Subquery<Date> effectiveDate = query.subquery(Date.class);
		Root<PaymentMaster> ocpm1 = effectiveDate.from(PaymentMaster.class);
		effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
		Predicate a1 = cb.equal(ocpm1.get("paymentMasterId"),b.get("paymentMasterId"));
		Predicate a2 = cb.equal(ocpm1.get("companyId"),b.get("companyId"));
		Predicate a3 = cb.equal(ocpm1.get("branchCode"),b.get("branchCode"));
		Predicate a4 = cb.equal(ocpm1.get("agencyCode"),b.get("agencyCode"));
		
		effectiveDate.where(a1,a2,a3,a4);
	
		//OrderBy
		List<Order> orderList = new ArrayList<Order>();
		orderList.add(cb.desc(b.get("paymentMasterId")));
		
		Predicate n1 = cb.equal(b.get("effectiveDateStart"),effectiveDate);
		Predicate n2 = cb.equal(b.get("companyId"),companyId);
		Predicate n3 = cb.equal(b.get("branchCode"), branchCode);
		Predicate n4 = cb.equal(b.get("branchCode"), "99999");
		Predicate n5 = cb.or(n3,n4);
		Predicate n6 = cb.equal(b.get("companyId"),"99999");
		Predicate n7 = cb.or(n2,n6);
		agencyCode = StringUtils.isNotBlank(agencyCode)  ? agencyCode : "99999"   ;
		Predicate n13 = cb.equal(b.get("agencyCode"),agencyCode);
		query.where(n1,n7,n5,n13).orderBy(orderList);
		
		
		
		// Get Result
		TypedQuery<PaymentMaster> result = em.createQuery(query);
		int limit = 0 , offset = 1 ;
		result.setFirstResult(limit * offset);
		result.setMaxResults(offset);
		list = result.getResultList();
		data = list.size() > 0 ? list.get(0).getPaymentMasterId() : 0 ;
	}
	catch(Exception e) {
		e.printStackTrace();
		log.info(e.getMessage());
	}
	return data;
}

@Override
public List<PaymentMasterRes> getallPayment(PaymentMasterGetallReq req) {
	List<PaymentMasterRes> resList = new ArrayList<PaymentMasterRes>();
	DozerBeanMapper mapper = new DozerBeanMapper();
	try {
		List<PaymentMaster> list = new ArrayList<PaymentMaster>();
	
		// Find Latest Record
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PaymentMaster> query = cb.createQuery(PaymentMaster.class);

		// Find All
		Root<PaymentMaster> b = query.from(PaymentMaster.class);

		// Select
		query.select(b);

		// Amend ID Max Filter
		Subquery<Long> amendId = query.subquery(Long.class);
		Root<PaymentMaster> ocpm1 = amendId.from(PaymentMaster.class);
		amendId.select(cb.max(ocpm1.get("amendId")));
		Predicate a1 = cb.equal(ocpm1.get("paymentMasterId"), b.get("paymentMasterId"));
		Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
		Predicate a3 = cb.equal(ocpm1.get("branchCode"),b.get("branchCode"));
		Predicate a4 = cb.equal(ocpm1.get("productId"),b.get("productId"));
		Predicate a5 = cb.equal(ocpm1.get("agencyCode"),b.get("agencyCode"));
		amendId.where(a1, a2,a3,a4,a5);

		// Order By
		
		// Where
		Predicate n1 = cb.equal(b.get("amendId"), amendId);
		Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
		Predicate n3 = cb.equal(b.get("branchCode"), req.getBranchCode());
		Predicate n4 = cb.equal(b.get("branchCode"), "99999");
		Predicate n5 = cb.or(n3,n4);

		
		Predicate n13=null;
		if(StringUtils.isNotBlank(req.getAgencyCode())) {
		    n13 = cb.equal(b.get("agencyCode"),req.getAgencyCode());
		}else {
			n13 = cb.equal(b.get("agencyCode"),"99999");
		}
//		Predicate n15 = cb.or(n13, n14);
		if(StringUtils.isNotBlank(req.getAgencyCode())) {
			Predicate n16 = cb.equal(b.get("userType"),req.getUserType());
			Predicate n17 = cb.equal(b.get("subUserType"),req.getSubUserType());
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(b.get("amendId")));
			if (req.getProductId() != null && req.getProductId().isEmpty()) {
				Predicate n6 =  cb.equal(b.get("productId"), req.getProductId());
				query.where(n1,n2,n5,n6,n13,n16,n17).orderBy(orderList);
			} else {
				query.where(n1,n2,n5,n13,n16,n17).orderBy(orderList);
			}
			
		} else {
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("branchCode")));

			if (req.getProductId() != null && req.getProductId().isEmpty()) {
				Predicate n6 =  cb.equal(b.get("productId"), req.getProductId());
				query.where(n1,n2,n5,n6,n13).orderBy(orderList);
			} else {
				query.where(n1,n2,n5,n13).orderBy(orderList);
			}
				
		}
		
		
		// Get Result
		TypedQuery<PaymentMaster> result = em.createQuery(query);
		list = result.getResultList();
		list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getPaymentMasterId()))).collect(Collectors.toList());
		list.sort(Comparator.comparing(PaymentMaster :: getPaymentMasterId ));
		
		// Map
		for (PaymentMaster data : list) {
			PaymentMasterRes res = new PaymentMasterRes();

			res = mapper.map(data, PaymentMasterRes.class);

			resList.add(res);
		}

	} catch (Exception e) {
		e.printStackTrace();
		log.info(e.getMessage());
		return null;

	}
	return resList;
}
private static <T> java.util.function.Predicate<T> distinctByKey(java.util.function.Function<? super T, ?> keyExtractor) {
    Map<Object, Boolean> seen = new ConcurrentHashMap<>();
    return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
}
@Override
public List<PaymentMasterRes> getActivePayment(PaymentMasterGetallReq req) {
	List<PaymentMasterRes> resList = new ArrayList<PaymentMasterRes>();
	DozerBeanMapper mapper = new DozerBeanMapper();
	try {
		List<PaymentMaster> list = new ArrayList<PaymentMaster>();
	
		// Find Latest Record
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PaymentMaster> query = cb.createQuery(PaymentMaster.class);

		// Find All
		Root<PaymentMaster> b = query.from(PaymentMaster.class);

		// Select
		query.select(b);

		// Amend ID Max Filter
		Subquery<Long> amendId = query.subquery(Long.class);
		Root<PaymentMaster> ocpm1 = amendId.from(PaymentMaster.class);
		amendId.select(cb.max(ocpm1.get("amendId")));
		Predicate a1 = cb.equal(ocpm1.get("paymentMasterId"), b.get("paymentMasterId"));
		Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
		Predicate a3 = cb.equal(ocpm1.get("branchCode"),b.get("branchCode"));
		Predicate a4 = cb.equal(ocpm1.get("productId"),b.get("productId"));
		Predicate a5 = cb.equal(ocpm1.get("agencyCode"),b.get("agencyCode"));
		amendId.where(a1, a2,a3,a4,a5);

		// Order By
		List<Order> orderList = new ArrayList<Order>();
		orderList.add(cb.asc(b.get("branchCode")));

		// Where
		Predicate n1 = cb.equal(b.get("amendId"), amendId);
		Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
		Predicate n4 = cb.equal(b.get("status"), "Y");
		Predicate n5 = cb.equal(b.get("branchCode"), req.getBranchCode());
		Predicate n6 = cb.equal(b.get("branchCode"), "99999");
		Predicate n7 = cb.or(n5,n6);
		Predicate n8 =  cb.equal(b.get("productId"), req.getProductId());
		String agencyCode = StringUtils.isNotBlank(req.getAgencyCode())  ? req.getAgencyCode() : "99999"   ;
		Predicate n13 = cb.equal(b.get("agencyCode"),agencyCode);
		query.where(n1,n2,n4,n7,n8,n13).orderBy(orderList);

		
		query.where(n1,n8,n4,n6).orderBy(orderList);
		
		// Get Result
		TypedQuery<PaymentMaster> result = em.createQuery(query);
		list = result.getResultList();
		list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getPaymentMasterId()))).collect(Collectors.toList());
		list.sort(Comparator.comparing(PaymentMaster :: getPaymentMasterId ));
		
		// Map
		for (PaymentMaster data : list) {
			PaymentMasterRes res = new PaymentMasterRes();

			res = mapper.map(data, PaymentMasterRes.class);

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
public PaymentMasterRes getByPaymentId(PaymentMasterGetReq req) {
	PaymentMasterRes res = new PaymentMasterRes();
	DozerBeanMapper mapper = new DozerBeanMapper();
	try {
		Date today = new Date();
		Calendar cal = new GregorianCalendar();
		cal.setTime(today);
		cal.set(Calendar.HOUR_OF_DAY, 23);
		cal.set(Calendar.MINUTE, 1);
		today = cal.getTime();

		List<PaymentMaster> list = new ArrayList<PaymentMaster>();
	
		// Find Latest Record
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PaymentMaster> query = cb.createQuery(PaymentMaster.class);

		// Find All
		Root<PaymentMaster> b = query.from(PaymentMaster.class);

		// Select
		query.select(b);

		// Amend ID Max Filter
		Subquery<Long> amendId = query.subquery(Long.class);
		Root<PaymentMaster> ocpm1 = amendId.from(PaymentMaster.class);
		amendId.select(cb.max(ocpm1.get("amendId")));
		Predicate a1 = cb.equal(ocpm1.get("paymentMasterId"), b.get("paymentMasterId"));
		Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
		Predicate a3 = cb.equal(ocpm1.get("branchCode"),b.get("branchCode"));
		Predicate a4 = cb.equal(ocpm1.get("productId"),b.get("productId"));
		Predicate a5 = cb.equal(ocpm1.get("agencyCode"),b.get("agencyCode"));
		amendId.where(a1, a2,a3,a4,a5);

		// Order By
		List<Order> orderList = new ArrayList<Order>();
		orderList.add(cb.asc(b.get("branchCode")));

		// Where
		Predicate n1 = cb.equal(b.get("amendId"), amendId);
		Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
		Predicate n3 = cb.equal(b.get("branchCode"), req.getBranchCode());
		Predicate n4 = cb.equal(b.get("branchCode"), "99999");
		Predicate n5 = cb.or(n3,n4);
		Predicate n6 =  cb.equal(b.get("productId"), req.getProductId());
		Predicate n7 = cb.equal(b.get("paymentMasterId"), req.getPaymentMasterId());
		Predicate n13 = cb.equal(b.get("agencyCode"),req.getAgencyCode());
		Predicate n14 = cb.equal(b.get("agencyCode"), "99999");
		Predicate n15 = cb.or(n13,n14);
		query.where(n1,n2,n5,n6,n7,n15).orderBy(orderList);
		
		
		// Get Result
		TypedQuery<PaymentMaster> result = em.createQuery(query);

		list = result.getResultList();
		list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getPaymentMasterId()))).collect(Collectors.toList());
		list.sort(Comparator.comparing(PaymentMaster :: getPaymentMasterId ));
		if (list.size() > 0) {
			res = mapper.map(list.get(0), PaymentMasterRes.class);
			res.setPaymentMasterId(list.get(0).getPaymentMasterId().toString());
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
public SuccessRes changeStatusOfPayment(PaymentMasterChangeStatusReq req) {
	SuccessRes res = new SuccessRes();
	DozerBeanMapper dozerMapper = new DozerBeanMapper();
	try {
		List<PaymentMaster> list = new ArrayList<PaymentMaster>();
		
		// Find Latest Record
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PaymentMaster> query = cb.createQuery(PaymentMaster.class);
		// Find all
		Root<PaymentMaster> b = query.from(PaymentMaster.class);
		//Select
		query.select(b);

		// Amend ID Max Filter
		Subquery<Long> amendId = query.subquery(Long.class);
		Root<PaymentMaster> ocpm1 = amendId.from(PaymentMaster.class);
		amendId.select(cb.max(ocpm1.get("amendId")));
		Predicate a1 = cb.equal(ocpm1.get("paymentMasterId"), b.get("paymentMasterId"));
		Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
		Predicate a3 = cb.equal(ocpm1.get("branchCode"),b.get("branchCode"));
		Predicate a4 = cb.equal(ocpm1.get("productId"),b.get("productId"));
		Predicate a5 = cb.equal(ocpm1.get("agencyCode"),b.get("agencyCode"));
		amendId.where(a1, a2,a3,a4,a5);

		// Order By
		List<Order> orderList = new ArrayList<Order>();
		orderList.add(cb.asc(b.get("branchCode")));

		// Where
		Predicate n1 = cb.equal(b.get("amendId"), amendId);
		Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
		Predicate n3 = cb.equal(b.get("branchCode"), req.getBranchCode());
		Predicate n4 = cb.equal(b.get("paymentMasterId"), req.getPaymentMasterId());
		Predicate n5 = cb.equal(b.get("branchCode"), "99999");
		Predicate n6 = cb.or(n3,n5);
		Predicate n7 = cb.equal(b.get("companyId"),"99999");
		Predicate n8 = cb.or(n2,n7);
		String agencyCode = StringUtils.isNotBlank(req.getAgencyCode())  ? req.getAgencyCode() : "99999"   ;
		Predicate n13 = cb.equal(b.get("agencyCode"),agencyCode);
		
		query.where(n1,n8,n4,n6,n13).orderBy(orderList);
		
		// Get Result 
		TypedQuery<PaymentMaster> result = em.createQuery(query);
		list = result.getResultList();
		PaymentMaster updateRecord = list.get(0);
		if(  req.getBranchCode().equalsIgnoreCase(updateRecord.getBranchCode())) {
			updateRecord.setStatus(req.getStatus());
			repo.save(updateRecord);
		} else {
			PaymentMaster saveNew = new PaymentMaster();
			dozerMapper.map(updateRecord,saveNew);
			saveNew.setBranchCode(req.getBranchCode());
			saveNew.setStatus(req.getStatus());
			repo.save(saveNew);
		}
	
		// Perform Update
		res.setResponse("Status Changed");
		res.setSuccessId(req.getPaymentMasterId());
	}
	catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is --> " + e.getMessage());
		return null;
		}
	return res;
}
@Override
public List<PaymentMasterDropDownRes> getPaymentMasterDropdown(PaymentMasterDropdownReq req){
	List<PaymentMasterDropDownRes> resList = new ArrayList<PaymentMasterDropDownRes>();
	try {
		Date today = new Date();
		Calendar cal = new GregorianCalendar();
		cal.setTime(today);
		today = cal.getTime();
		Date todayEnd = cal.getTime();
		
		// Criteria
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PaymentMaster> query=  cb.createQuery(PaymentMaster.class);
		List<PaymentMaster> list = new ArrayList<PaymentMaster>();
		// Find All
		Root<PaymentMaster> c = query.from(PaymentMaster.class);
		//Select
		query.select(c);
		// Order By
		List<Order> orderList = new ArrayList<Order>();
		orderList.add(cb.desc(c.get("paymentMasterId")));
		
		// Effective Date Start Max Filter
		Subquery<Date> effectiveDate = query.subquery(Date.class);
		Root<PaymentMaster> ocpm1 = effectiveDate.from(PaymentMaster.class);
		effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
		Predicate a1 = cb.equal(c.get("paymentMasterId"),ocpm1.get("paymentMasterId"));
		Predicate a2 = cb.equal(c.get("companyId"),ocpm1.get("companyId"));
		Predicate a3 = cb.equal(c.get("branchCode"),ocpm1.get("branchCode"));
		Predicate a4 = cb.equal(c.get("userType"),ocpm1.get("userType"));
		Predicate a5 = cb.equal(c.get("subUserType"),ocpm1.get("subUserType"));
		Predicate a6 = cb.equal(c.get("productId"),ocpm1.get("productId"));
		Predicate a7 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
		Predicate a15 = cb.equal(c.get("agencyCode"),ocpm1.get("agencyCode"));
		effectiveDate.where(a1,a2,a3,a4,a5,a6,a7,a15);
		
		// Effective Date End Max Filter
		Subquery<Date> effectiveDate2 = query.subquery(Date.class);
		Root<PaymentMaster> ocpm2 = effectiveDate2.from(PaymentMaster.class);
		effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
		Predicate a8 = cb.equal(c.get("paymentMasterId"),ocpm2.get("paymentMasterId"));
		Predicate a9 = cb.equal(c.get("companyId"),ocpm2.get("companyId"));
		Predicate a10 = cb.equal(c.get("branchCode"),ocpm2.get("branchCode"));
		Predicate a11 = cb.equal(c.get("userType"),ocpm2.get("userType"));
		Predicate a12 = cb.equal(c.get("subUserType"),ocpm2.get("subUserType"));
		Predicate a13 = cb.equal(c.get("productId"),ocpm2.get("productId"));
	//	Predicate a14 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
		Predicate a16 = cb.equal(c.get("agencyCode"),ocpm2.get("agencyCode"));
	//	effectiveDate2.where(a8,a9,a10,a11,a12,a13,a14);
		effectiveDate2.where(a8,a9,a10,a11,a12,a13,a16);
		// Where
		Predicate n1 = cb.equal(c.get("status"),"Y");
		Predicate n11 = cb.equal(c.get("status"),"R");
		Predicate n12 = cb.or(n1,n11);
		Predicate n2 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
		Predicate n3 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);	
		Predicate n4 = cb.equal(c.get("companyId"),req.getCompanyId());
		Predicate n5 = cb.equal(c.get("branchCode"),req.getBranchCode());
		Predicate n6 = cb.equal(c.get("branchCode"),"99999");	
		Predicate n7 = cb.or(n5,n6);
		Predicate n8 = cb.equal(c.get("userType"),req.getUserType());
		Predicate n9 = cb.equal(cb.lower(c.get("subUserType")),req.getSubUserType().toLowerCase() );
		Predicate n10 = cb.equal(c.get("productId"),req.getProductId());
		
		String agencyCode = StringUtils.isBlank(req.getAgencyCode())? "99999" :req.getAgencyCode() ;
//		if(StringUtils.isNotBlank(req.getCreatedBy()) ) {
//			LoginMaster loginData = loginRepo.findByLoginId(req.getCreatedBy());
//			if(loginData !=null  )
//				agencyCode = loginData.getAgencyCode() ;
//		}
		  
		Predicate n13 = cb.equal(c.get("agencyCode"),agencyCode );
		Predicate n14 = cb.equal(c.get("agencyCode"),"99999" );
		Predicate n15 = cb.or(n13,n14 );
		query.where(n12,n2,n3,n4,n7,n8,n9,n10,n15).orderBy(orderList);
		
		// Get Result
		TypedQuery<PaymentMaster> result = em.createQuery(query);
		list = result.getResultList();
		
		
		
		// Payment Types
		List<ListItemValue> paymentList = getPaymentItems(req.getCompanyId() , req.getBranchCode() ,  "PAYMENT_MODE");
				
		if(list.size()>0 && paymentList.size() >0 ) {
			// Response 
			PaymentMaster paymentData = list.get(0);
			if("Y".equalsIgnoreCase(paymentData.getCashYn()) ) {
				ListItemValue cash = paymentList.stream().filter( o -> o.getItemCode().equalsIgnoreCase("1") ).findFirst().orElse(null)  ;
				PaymentMasterDropDownRes res = new PaymentMasterDropDownRes();
				if(cash!=null) {
				res.setCode(cash.getItemCode());
				res.setCodeDesc(cash.getItemValue());
				res.setCodeDescLocal(cash.getItemValueLocal());
				res.setType(cash.getParam1());
				resList.add(res);
				}
			} 
			if("Y".equalsIgnoreCase(paymentData.getCreditYn()) ) {
				ListItemValue credit = paymentList.stream().filter( o -> o.getItemCode().equalsIgnoreCase("3")).findFirst().orElse(null) ;
				
				PaymentMasterDropDownRes res = new PaymentMasterDropDownRes();
				if(credit!=null) {
				res.setCode(credit.getItemCode());
				res.setCodeDesc(credit.getItemValue());
				res.setCodeDescLocal(credit.getItemValueLocal());
				res.setType(credit.getParam1());
				resList.add(res);
				}
			}
			if("Y".equalsIgnoreCase(paymentData.getChequeYn()) ) {
				ListItemValue cheque = paymentList.stream().filter( o -> o.getItemCode().equalsIgnoreCase("2") ).findFirst().orElse(null) ;
				PaymentMasterDropDownRes res = new PaymentMasterDropDownRes();
				if(cheque!=null) {
				res.setCode(cheque.getItemCode());
				res.setCodeDesc(cheque.getItemValue());
				res.setCodeDescLocal(cheque.getItemValueLocal());
				res.setType(cheque.getParam1());
				resList.add(res);
				}
			}
			
			if("Y".equalsIgnoreCase(paymentData.getOnlineYn()) ) {
				ListItemValue online = paymentList.stream().filter( o -> o.getItemCode().equalsIgnoreCase("4") ).findFirst().orElse(null) ;
				PaymentMasterDropDownRes res = new PaymentMasterDropDownRes();
				if(online!=null) {
				res.setCode(online.getItemCode());
				res.setCodeDesc(online.getItemValue());
				res.setCodeDescLocal(online.getItemValueLocal());
				res.setType(online.getParam1());
				resList.add(res);
				}
				
			}
			if("Y".equalsIgnoreCase(paymentData.getMobilePaymentYn()) ) {
				Arrays.asList("5","6").forEach(i -> {
					List<ListItemValue> onlineList = paymentList.stream().filter( o -> o.getItemCode().equalsIgnoreCase(i) ).collect(Collectors.toList());
					if(!onlineList.isEmpty()) {
						ListItemValue online = onlineList.get(0);
						PaymentMasterDropDownRes res = new PaymentMasterDropDownRes();
						res.setCode(online.getItemCode());
						res.setCodeDesc(online.getItemValue());
						res.setCodeDescLocal(online.getItemValueLocal());
						res.setType(online.getParam1());
						resList.add(res);
					}
				});
			}
			
			if("Y".equalsIgnoreCase(paymentData.getRealpayYn()) ) {
				ListItemValue realpay = paymentList.stream().filter( o -> o.getItemCode().equalsIgnoreCase("7") ).findFirst().orElse(null)  ;
				PaymentMasterDropDownRes res = new PaymentMasterDropDownRes();
				if(realpay!=null) {
				res.setCode(realpay.getItemCode());
				res.setCodeDesc(realpay.getItemValue());
				res.setCodeDescLocal(realpay.getItemValueLocal());
				res.setType(realpay.getParam1());
				resList.add(res);
				}
			}

			if("Y".equalsIgnoreCase(paymentData.getEftYn()) ) {
				ListItemValue realpay = paymentList.stream().filter( o -> o.getItemCode().equalsIgnoreCase("8") ).findFirst().orElse(null)  ;
				PaymentMasterDropDownRes res = new PaymentMasterDropDownRes();
				if(realpay!=null) {
					res.setCode(realpay.getItemCode());
					res.setCodeDesc(realpay.getItemValue());
					res.setCodeDescLocal(realpay.getItemValueLocal());
					res.setType(realpay.getParam1());
					resList.add(res);
				}
			}
		}
		
	}	catch(Exception e) {
			e.printStackTrace();
			log.info("Exception is --->"+e.getMessage());
			return null;
	}
		return resList;
}


public synchronized List<ListItemValue> getPaymentItems(String insuranceId , String branchCode, String itemType) {
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
		Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
		Predicate a11 = cb.equal(ocpm1.get("companyId"),insuranceId);
		Predicate a12 = cb.equal(ocpm1.get("itemType"),itemType);
		effectiveDate.where(a1,a2,a11,a12);
		// Effective Date End Max Filter
		Subquery<Date> effectiveDate2 = query.subquery(Date.class);
		Root<ListItemValue> ocpm2 = effectiveDate2.from(ListItemValue.class);
		effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
		Predicate a3 = cb.equal(c.get("itemId"),ocpm2.get("itemId"));
		Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
		Predicate a13 = cb.equal(ocpm2.get("companyId"),insuranceId);
		Predicate a14 = cb.equal(ocpm2.get("itemType"),itemType);
		effectiveDate2.where(a3,a4,a13,a14);
					
		// Where
		Predicate n1 = cb.equal(c.get("status"),"Y");
		Predicate n2 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
		Predicate n3 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);	
		Predicate n4 = cb.equal(c.get("companyId"), insuranceId);
		Predicate n5 = cb.equal(c.get("companyId"), "99999");
		Predicate n6 = cb.equal(c.get("branchCode"), branchCode);
		Predicate n7 = cb.equal(c.get("branchCode"), "99999");
		Predicate n8 = cb.or(n4,n5);
		Predicate n9 = cb.or(n6,n7);
		Predicate n10 = cb.equal(c.get("itemType"),itemType );
		query.where(n1,n8,n2,n3,n9,n10).orderBy(orderList);
		// Get Result
		TypedQuery<ListItemValue> result = em.createQuery(query);
		list = result.getResultList();
		 
	} catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is ---> " + e.getMessage());
		return null;
	}
	return list ;
}
		
	

	
	
	
	
}
