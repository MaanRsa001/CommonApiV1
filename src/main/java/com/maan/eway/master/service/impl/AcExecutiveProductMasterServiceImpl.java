package com.maan.eway.master.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Map;
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
import com.maan.eway.bean.AcExecutiveMaster;
import com.maan.eway.bean.AcExecutiveProductMaster;
import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.error.Error;
import com.maan.eway.master.req.AcExecutiveGetReq;
import com.maan.eway.master.req.AcExecutiveGetallReq;
import com.maan.eway.master.req.AcExecutiveNonSelectedReq;
import com.maan.eway.master.req.AcExecutiveProductDropDownReq;
import com.maan.eway.master.req.AcExecutiveSaveReq;
import com.maan.eway.master.req.AcExecutiveUpdateReq;
import com.maan.eway.master.res.AcExecutiveGetRes;
import com.maan.eway.master.res.AcExecutiveProductDropdownRes;
import com.maan.eway.master.res.CompanyProductMasterRes;
import com.maan.eway.master.service.AcExecutiveProductMasterService;
import com.maan.eway.repository.AcExecutiveProductMasterRepository;
import com.maan.eway.res.SuccessRes;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.Tuple;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

@Service
@Transactional
public class AcExecutiveProductMasterServiceImpl implements AcExecutiveProductMasterService {

	Gson json = new Gson();

	private Logger log = LogManager.getLogger(AcExecutiveProductMasterServiceImpl.class);

	@Autowired
	private AcExecutiveProductMasterRepository repo;

	@PersistenceContext
	private EntityManager em;

	@Override
	public List<Error> validateacexecutive(AcExecutiveSaveReq req) {
		List<Error> errors = new ArrayList<Error>();
		try {

			if (StringUtils.isBlank(req.getAcExecutiveId())) {
				errors.add(new Error("01", "AcExecutiveId", "Please Enter AcExecutiveId"));
			}
			if (StringUtils.isBlank(req.getAcExecutiveName())) {
				errors.add(new Error("02", "AcExecutiveName", "Please Enter AcExecutiveName"));
			} else if (req.getAcExecutiveName().length() > 100) {
				errors.add(
						new Error("02", "AcExecutiveName", "Please Enter AcExecutiveName  within 100 Characters Only"));
			}
			if (StringUtils.isBlank(req.getAgencyCode())) {
				errors.add(new Error("03", "AgencyCode", "Please Enter AgencyCode"));
			} else if (req.getAgencyCode().length() > 20) {
				errors.add(new Error("03", "AgencyCode", "Please Enter AgencyCode  within 20 Characters Only"));
			}
			if (StringUtils.isBlank(req.getBankCode())) {
				errors.add(new Error("04", "BankCode", "Please Enter BankCode"));
			} else if (req.getBankCode().length() > 20) {
				errors.add(new Error("04", "BankCode", "Please Enter BankCode within 20 Characters Only"));
			}
			if (StringUtils.isBlank(req.getCompanyId())) {
				errors.add(new Error("05", "CompanyId", "Please Enter CompanyId"));
			} else if (req.getCompanyId().length() > 20) {
				errors.add(new Error("05", "CompanyId", "Please Enter CompanyId within 20 Characters Only"));
			}
			if (StringUtils.isBlank(req.getCreatedBy())) {
				errors.add(new Error("06", "CreatedBy", "Please Enter CreatedBy"));
			} else if (req.getCreatedBy().length() > 100) {
				errors.add(new Error("06", "CreatedBy", "Please Enter CreatedBy within 100 Characters Only"));
			}
			if (StringUtils.isBlank(req.getOaCode())) {
				errors.add(new Error("07", "OaCode", "Please Enter OaCode"));
			} else if (req.getOaCode().length() > 20) {
				errors.add(new Error("07", "OaCode", "Please Enter OaCode within 20 Characters Only"));
			}

			if (req.getRemarks().length() > 100) {
				errors.add(new Error("08", "Remarks", "Please Enter Remarks within 100 Characters Only"));
			}
			if (StringUtils.isBlank(req.getSubUserType())) {
				errors.add(new Error("09", "SubUserType", "Please Enter SubUserType"));
			} else if (req.getSubUserType().length() > 20) {
				errors.add(new Error("09", "SubUserType", "Please Enter SubUserType within 20 Characters Only"));
			}
			if (StringUtils.isBlank(req.getUserType())) {
				errors.add(new Error("10", "UserType", "Please Enter UserType"));
			} else if (req.getSubUserType().length() > 20) {
				errors.add(new Error("10", "UserType", "Please Enter UserType within 20 Characters Only"));
			}
			for (String data : req.getProductId()) {
				if (StringUtils.isBlank(data)) {
					errors.add(new Error("11", "Product Id", "Please Enter Product Id"));
				}
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
				errors.add(new Error("12", "EffectiveDateStart", "Please Enter Effective Date Start "));

			} else if (req.getEffectiveDateStart().before(today)) {
				errors.add(new Error("12", "EffectiveDateStart", "Please Enter Effective Date Start as Future Date"));
			}

		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
			errors.add(new Error("11", "Common Error", e.getMessage()));

		}
		return errors;
	}

	@Override
	public SuccessRes saveacexecutive(AcExecutiveSaveReq req) {
		SuccessRes res = new SuccessRes();
		SimpleDateFormat sdformat = new SimpleDateFormat("dd/MM/yyyy");
		DozerBeanMapper dozerMapper = new DozerBeanMapper();

		try {
			Integer amendId = 0;
			Date startDate = req.getEffectiveDateStart();
			String end = "31/12/2050";
			Date endDate = sdformat.parse(end);
			long MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;

			for (String data : req.getProductId()) {
				AcExecutiveProductMaster savedata = new AcExecutiveProductMaster();

				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<CompanyProductMaster> query = cb.createQuery(CompanyProductMaster.class);
				List<CompanyProductMaster> list = new ArrayList<CompanyProductMaster>();

				// Find All
				Root<CompanyProductMaster> c = query.from(CompanyProductMaster.class);

				// AmendId Start Max Filter
				Subquery<Long> amendId1 = query.subquery(Long.class);
				Root<CompanyProductMaster> ocpm1 = amendId1.from(CompanyProductMaster.class);
				amendId1.select(cb.max(ocpm1.get("amendId")));
				Predicate a1 = cb.equal(c.get("companyId"), ocpm1.get("companyId"));
				Predicate a2 = cb.equal(c.get("productId"), ocpm1.get("productId"));
				amendId1.where(a1, a2);

				Predicate n1 = cb.equal(c.get("productId"), data);
				Predicate n2 = cb.equal(c.get("companyId"), req.getCompanyId());
				Predicate n3 = cb.equal(c.get("status"), "Y");

				query.where(n1, n2, n3);

				TypedQuery<CompanyProductMaster> result = em.createQuery(query);
				list = result.getResultList();

				if (list.size() > 0) {

					savedata = dozerMapper.map(req, AcExecutiveProductMaster.class);
					CompanyProductMaster data1 = list.get(0);
					savedata.setProductName(data1.getProductName());
					savedata.setCoreAppCode(data1.getCoreAppCode());
					savedata.setRegulatoryCode(data1.getRegulatoryCode());
					savedata.setProductId(Integer.valueOf(data));
					savedata.setSumInsuredStart(0.0);
					savedata.setSumInsuredEnd(99999.0);
					savedata.setExistCustCommPercent(0.0);
					savedata.setNewCustCommPercent(0.0);
					savedata.setRenewalCustCommPercent(0.0);
					savedata.setAmendId(amendId);
					savedata.setEntryDate(new Date());
					savedata.setCreatedBy(req.getCreatedBy());
					savedata.setUpdatedDate(new Date());
					savedata.setUpdatedBy(req.getCreatedBy());
					savedata.setEffectiveDateEnd(endDate);
					savedata.setEffectiveDateStart(startDate);
					savedata.setAgencyCode(req.getAgencyCode());
					savedata.setOaCode(req.getOaCode());
					savedata.setBankCode(req.getAgencyCode());
					savedata.setStatus("Y");
					repo.saveAndFlush(savedata);
				}

			}
			res.setResponse("Inserted SuccessfullY");
			res.setSuccessId(req.getAcExecutiveId());
		}

		catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return res;
	}

	@Override
	public SuccessRes updateacexecutive(AcExecutiveUpdateReq req) {
		SuccessRes res = new SuccessRes();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			Date entryDate = null;
			String createdBy = "";
			Date startDate = req.getEffectiveDateStart();
			String end = "31/12/2050";
			Date endDate = sdf.parse(end);
			long MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24;
			Date oldEndDate = new Date(req.getEffectiveDateStart().getTime() - MILLIS_IN_A_DAY);
			Integer amendId = 0;
			AcExecutiveProductMaster saveData = new AcExecutiveProductMaster();
			List<AcExecutiveProductMaster> list = new ArrayList<AcExecutiveProductMaster>();

			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<AcExecutiveProductMaster> query = cb.createQuery(AcExecutiveProductMaster.class);

			// Find All
			Root<AcExecutiveProductMaster> b = query.from(AcExecutiveProductMaster.class);

			// Select
			query.select(b);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("effectiveDateStart")));

			// AmendId Max Filter
			Subquery<Long> amendId1 = query.subquery(Long.class);
			Root<AcExecutiveProductMaster> ocpm1 = amendId1.from(AcExecutiveProductMaster.class);
			amendId1.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a2 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			Predicate a3 = cb.equal(ocpm1.get("acExecutiveId"), b.get("acExecutiveId"));

			amendId1.where(a1, a2, a3);

			// Where
			Predicate n1 = cb.equal(b.get("status"), "Y");
			Predicate n2 = cb.equal(b.get("productId"), req.getProductId());
			Predicate n3 = cb.equal(b.get("companyId"), req.getCompanyId());
			Predicate n4 = cb.equal(b.get("acExecutiveId"), req.getAcExecutiveId());

			query.where(n1, n2, n3, n4);

			// Get Result
			TypedQuery<AcExecutiveProductMaster> result = em.createQuery(query);
			list = result.getResultList();
			if (list.size() > 0) {
				Date beforeOneDay = new Date(new Date().getTime() - MILLIS_IN_A_DAY);
				if (list.get(0).getEffectiveDateStart().before(beforeOneDay)) {
					amendId = list.get(0).getAmendId() + 1;
					entryDate = new Date();
					createdBy = req.getCreatedBy();
					AcExecutiveProductMaster lastRecord = list.get(0);
					lastRecord.setEffectiveDateEnd(oldEndDate);
					repo.saveAndFlush(lastRecord);

				} else {
					amendId = list.get(0).getAmendId();
					entryDate = list.get(0).getEntryDate();
					createdBy = list.get(0).getCreatedBy();
					saveData = list.get(0);
					if (list.size() > 1) {
						AcExecutiveProductMaster lastRecord = list.get(1);
						lastRecord.setEffectiveDateEnd(oldEndDate);
						repo.saveAndFlush(lastRecord);
					}
				}
			}
			saveData = dozerMapper.map(req, AcExecutiveProductMaster.class);
			saveData.setAmendId(amendId);
			saveData.setEffectiveDateEnd(endDate);
			saveData.setStatus("Y");
			saveData.setEntryDate(new Date());
			saveData.setCreatedBy(req.getCreatedBy());
			saveData.setUpdatedDate(new Date());
			saveData.setUpdatedBy(req.getCreatedBy());

			repo.saveAndFlush(saveData);
			res.setSuccessId(req.getProductId());
			res.setResponse("Updated Successful");
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return res;
	}

	@Override
	public AcExecutiveGetRes getacexecutive(AcExecutiveGetReq req) {
		AcExecutiveGetRes res = new AcExecutiveGetRes();
		ModelMapper mapper = new ModelMapper();
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
			CriteriaQuery<AcExecutiveProductMaster> query = cb.createQuery(AcExecutiveProductMaster.class);
			List<AcExecutiveProductMaster> list = new ArrayList<AcExecutiveProductMaster>();

			// Find All
			Root<AcExecutiveProductMaster> c = query.from(AcExecutiveProductMaster.class);

			// Select
			query.select(c);

			// amendId Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<AcExecutiveProductMaster> ocpm1 = amendId.from(AcExecutiveProductMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(c.get("acExecutiveId"), ocpm1.get("acExecutiveId"));
			Predicate a2 = cb.equal(c.get("companyId"), ocpm1.get("companyId"));
			Predicate a3 = cb.equal(c.get("productId"), ocpm1.get("productId"));
			amendId.where(a1, a2, a3);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("productId")));

			// Where

			Predicate n1 = cb.equal(c.get("amendId"), amendId);
			Predicate n2 = cb.equal(c.get("productId"), req.getProductId());
			Predicate n3 = cb.equal(c.get("companyId"), req.getCompanyId());
			Predicate n4 = cb.equal(c.get("acExecutiveId"), req.getAcExecutiveId());
			query.where(n1, n2, n3, n4).orderBy(orderList);

			// Get Result
			TypedQuery<AcExecutiveProductMaster> result = em.createQuery(query);
			list = result.getResultList();
			res = mapper.map(list.get(0), AcExecutiveGetRes.class);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return res;
	}

	@Override
	public List<AcExecutiveGetRes> getallacexecutive(AcExecutiveGetallReq req) {
		List<AcExecutiveGetRes> resList = new ArrayList<AcExecutiveGetRes>();
		ModelMapper mapper = new ModelMapper();
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
			CriteriaQuery<AcExecutiveProductMaster> query = cb.createQuery(AcExecutiveProductMaster.class);
			List<AcExecutiveProductMaster> list = new ArrayList<AcExecutiveProductMaster>();

			// Find All
			Root<AcExecutiveProductMaster> c = query.from(AcExecutiveProductMaster.class);

			// Select
			query.select(c);

			// amendId Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<AcExecutiveProductMaster> ocpm1 = amendId.from(AcExecutiveProductMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(c.get("acExecutiveId"), ocpm1.get("acExecutiveId"));
			Predicate a2 = cb.equal(c.get("companyId"), ocpm1.get("companyId"));
			amendId.where(a1, a2);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("productId")));

			// Where

			Predicate n1 = cb.equal(c.get("amendId"), amendId);
			Predicate n2 = cb.equal(c.get("companyId"), req.getCompanyId());
			Predicate n3 = cb.equal(c.get("acExecutiveId"), req.getAcExecutiveId());
			query.where(n1, n2, n3).orderBy(orderList);

			// Get Result
			TypedQuery<AcExecutiveProductMaster> result = em.createQuery(query);
			list = result.getResultList();
			for (AcExecutiveProductMaster data : list) {
				AcExecutiveGetRes res = new AcExecutiveGetRes();
				res = mapper.map(data, AcExecutiveGetRes.class);
				res.setProductId(data.getProductId().toString());
				res.setRenewCustCommPercent(data.getRenewalCustCommPercent().toString());
				resList.add(res);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}

	@Override
	public List<CompanyProductMasterRes> getallNonSelectedProducts(AcExecutiveNonSelectedReq req) {
		List<CompanyProductMasterRes> resList = new ArrayList<CompanyProductMasterRes>();
		ModelMapper mapper = new ModelMapper();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			List<CompanyProductMaster> productList = new ArrayList<CompanyProductMaster>();

			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<CompanyProductMaster> query = cb.createQuery(CompanyProductMaster.class);

			// Find All
			Root<CompanyProductMaster> b = query.from(CompanyProductMaster.class);

			// Select
			query.select(b);

			// AmendId Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<CompanyProductMaster> ocpm1 = amendId.from(CompanyProductMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			amendId.where(a1, a2);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(b.get("effectiveDateStart")));

			// Amend Id Date Max Filter
			Subquery<Long> cover = query.subquery(Long.class);
			Root<AcExecutiveProductMaster> ps = cover.from(AcExecutiveProductMaster.class);
			Subquery<Long> amendId2 = query.subquery(Long.class);
			Root<AcExecutiveProductMaster> ocpm2 = amendId2.from(AcExecutiveProductMaster.class);
			amendId2.select(cb.max(ocpm2.get("amendId")));
			Predicate eff1 = cb.equal(ocpm2.get("acExecutiveId"), ps.get("acExecutiveId"));
			Predicate eff2 = cb.equal(ocpm2.get("productId"), ps.get("productId"));
			Predicate eff3 = cb.equal(ocpm2.get("companyId"), ps.get("companyId"));
			amendId2.where(eff1, eff2, eff3);

			// Product Filter
			cover.select(ps.get("productId"));
			Predicate ps1 = cb.equal(ps.get("companyId"), req.getCompanyId());
			Predicate ps2 = cb.equal(ps.get("amendId"), amendId2);
			cover.where(ps1, ps2);

			// Where
			Expression<String> e0 = b.get("productId");

			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n3 = cb.equal(b.get("status"), "Y");
			Predicate n4 = e0.in(cover).not();
			query.where(n1, n3, n4).orderBy(orderList);

			// Get Result
			TypedQuery<CompanyProductMaster> result = em.createQuery(query);
			productList = result.getResultList();
			Map<Integer, List<CompanyProductMaster>> groupByProductId = productList.stream()
					.collect(Collectors.groupingBy(w -> w.getProductId()));

			// Map
			for (Integer data : groupByProductId.keySet()) {
				CompanyProductMaster coverData = groupByProductId.get(data).get(0);
				CompanyProductMasterRes res = new CompanyProductMasterRes();
				res = mapper.map(coverData, CompanyProductMasterRes.class);
				res.setProductId(coverData.getProductId().toString());
				resList.add(res);
			}
			resList.sort(Comparator.comparing(CompanyProductMasterRes::getProductName));

		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
			return null;

		}
		return resList;
	}

	@Override
	public List<Error> validateupdateacexecutive(AcExecutiveUpdateReq req) {
		List<Error> errorList = new ArrayList<Error>();

		try {

			if (StringUtils.isBlank(req.getSumInsuredStart())) {
				errorList.add(new Error("01", "SumInsuredStart", "Please Enter SumInsuredStart   "));
			} else if (!req.getSumInsuredStart().matches("[0-9.]+")) {
				errorList.add(new Error("01", "SumInsuredStart", "Please Enter Valid Number In SumInsuredStart   "));
			} else if (StringUtils.isBlank(req.getSumInsuredEnd())) {
				errorList.add(new Error("01", "SumInsuredEnd", "Please Enter SumInsuredEnd"));
			} else if (!req.getSumInsuredEnd().matches("[0-9.]+")) {
				errorList.add(new Error("01", "SumInsuredEnd", "Please Enter Valid Number In SumInsuredEnd "));
			} else if (Double.valueOf(req.getSumInsuredStart()) > Double.valueOf(req.getSumInsuredEnd())) {
				errorList.add(new Error("01", "SumInsuredEnd", "SumInsuredEnd must be greater than SumInsuredStart "));
			}
			if (StringUtils.isBlank(req.getExistCustCommPercent())) {
				errorList.add(new Error("02", "ExistCustCommPercent", "Please Enter ExistCustCommPercent "));
			} else if (!req.getExistCustCommPercent().matches("[0-9.]+")) {
				errorList.add(
						new Error("02", "ExistCustCommPercent", "Please Enter Valid Number In ExistCustCommPercent  "));
			}
			if (StringUtils.isBlank(req.getNewCustCommPercent())) {
				errorList.add(new Error("03", "NewCustCommPercent", "Please Enter NewCustCommPercent "));
			} else if (!req.getNewCustCommPercent().matches("[0-9.]+")) {
				errorList.add(
						new Error("03", "NewCustCommPercent", "Please Enter Valid Number In NewCustCommPercent  "));
			}

			if (StringUtils.isBlank(req.getRenewCustCommPercent())) {
				errorList.add(new Error("04", "RenewCustCommPercent", "Please Enter RenewCustCommPercent "));
			} else if (!req.getRenewCustCommPercent().matches("[0-9.]+")) {
				errorList.add(
						new Error("04", "RenewCustCommPercent", "Please Enter Valid Number In RenewCustCommPercent  "));
			}
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
			errorList.add(new Error("11", "Common Error", e.getMessage()));

		}
		return errorList;
	}

	@Override
	public List<AcExecutiveProductDropdownRes> dropdownacexecutive(AcExecutiveProductDropDownReq req) {
		List<AcExecutiveProductDropdownRes> resList = new ArrayList<AcExecutiveProductDropdownRes>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			Date todayEnd = cal.getTime();
			// Criteria
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<Tuple> query=  cb.createQuery(Tuple.class);
			
			List<Tuple> list = new ArrayList<Tuple>();

		
			// Find All
			Root<AcExecutiveProductMaster> c = query.from(AcExecutiveProductMaster.class);
			
			Subquery<Long> acExecutive = query.subquery(Long.class);
			Root<AcExecutiveMaster> ac = acExecutive.from(AcExecutiveMaster.class);
			//  Effective Date Start Max Filter
			Subquery<Date> effectiveDate3 = query.subquery(Date.class);
			Root<AcExecutiveMaster> ocpm3 = effectiveDate3.from(AcExecutiveMaster.class);
			effectiveDate3.select(cb.greatest(ocpm3.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(ac.get("acExecutiveId"),ocpm3.get("acExecutiveId"));
			Predicate a2 = cb.equal(ac.get("companyId"),ocpm3.get("companyId"));
			Predicate a3 = cb.equal(ac.get("oaCode"),ocpm3.get("oaCode"));
			Predicate a4 = cb.equal(ac.get("bankCode"),ocpm3.get("bankCode"));
			Predicate a5 = cb.lessThanOrEqualTo(ocpm3.get("effectiveDateStart"), today);
			
			effectiveDate3.where(a1,a2,a3,a4,a5);
			
			//  Effective Date End Max Filter
			Subquery<Date> effectiveDate4 = query.subquery(Date.class);
			Root<AcExecutiveMaster> ocpm4 = effectiveDate4.from(AcExecutiveMaster.class);
			effectiveDate4.select(cb.greatest(ocpm4.get("effectiveDateEnd").as(Date.class)));
			Predicate a11 = cb.equal(ac.get("acExecutiveId"),ocpm4.get("acExecutiveId"));
			Predicate a12 = cb.equal(ac.get("companyId"),ocpm4.get("companyId"));
			Predicate a13 = cb.equal(ac.get("oaCode"),ocpm4.get("oaCode"));
			Predicate a14 = cb.equal(ac.get("bankCode"),ocpm4.get("bankCode"));
			Predicate a15 = cb.greaterThanOrEqualTo(ocpm4.get("effectiveDateEnd"), todayEnd);
			
			effectiveDate4.where(a11,a12,a13,a14,a15);

			// Where
			acExecutive.select(ac.get("acExecutiveName"));
			Predicate n1 = cb.equal(ac.get("status"),"Y");
			Predicate n2 = cb.equal(ac.get("effectiveDateStart") ,effectiveDate3 );
			Predicate n3 = cb.equal(ac.get("effectiveDateEnd"),effectiveDate4);	
			Predicate n4 = cb.equal(ac.get("oaCode"),req.getOaCode());
			Predicate n5 = cb.equal(ac.get("oaCode"),"99999");
			Predicate n6 = cb.or(n4,n5);
			Predicate n7 = cb.equal(ac.get("branchCode"),req.getBranchCode());
			Predicate n8 = cb.equal(ac.get("branchCode"),"99999");
			Predicate n9 = cb.or(n7,n8);
			Predicate n10 = cb.equal(ac.get("companyId"),req.getCompanyId());
			//Predicate n11 = cb.equal(ac.get("companyId"),"99999");
			//Predicate n12 = cb.or(n10,n11);		
		//	Predicate n13 = cb.equal(ac.get("bankCode"),req.getBankCode());
		//	Predicate n14 = cb.equal(ac.get("bankCode"),"None");
		//	Predicate n15 = cb.or(n13,n14);					
			Predicate n16 = cb.equal(c.get("acExecutiveId"),ac.get("acExecutiveId"));
			acExecutive.where(n1,n2,n3,n6,n9,n10,n16);

			query.multiselect(acExecutive.alias("acExecutiveName") ,   c.get("acExecutiveId").alias("acExecutiveId"),
					c.get("bankCode").alias("bankCode") , c.get("bankName").alias("bankName")
					);
			
			
			// Effective Date Start Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<AcExecutiveProductMaster> ocpm1 = effectiveDate.from(AcExecutiveProductMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a21 = cb.equal(c.get("acExecutiveId"),ocpm1.get("acExecutiveId"));
			Predicate a22 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			effectiveDate.where(a21,a22);
			// Effective Date End Max Filter
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<AcExecutiveProductMaster> ocpm2 = effectiveDate2.from(AcExecutiveProductMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate a23 = cb.equal(c.get("acExecutiveId"),ocpm2.get("acExecutiveId"));
			Predicate a24 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			effectiveDate2.where(a23,a24);

			// Where
			Predicate n28 = cb.equal(c.get("status"),"Y");
			Predicate n29 = cb.equal(c.get("status"),"R");
			Predicate n30 = cb.or(n28,n29);
			Predicate n22 = cb.equal(c.get("oaCode"),req.getOaCode());
			Predicate n23 = cb.equal(c.get("companyId"),req.getCompanyId());
			Predicate n24 = cb.equal(c.get("productId"),req.getProductId());
		//	Predicate n25 = cb.equal(c.get("bankCode"),req.getBankCode());
			Predicate n26 = cb.equal(c.get("effectiveDateStart"),effectiveDate);
			Predicate n27 = cb.equal(c.get("effectiveDateEnd"),effectiveDate2);	

		//	Predicate n27 = cb.equal(c.get("branchCode"),req.getBranchCode());

			query.where(n30,n22,n23,n24,n26,n27);
			// Get Result
			TypedQuery<Tuple> result = em.createQuery(query);
			list = result.getResultList(); 
		
			if(list.size()>0 && list!=null) {
			for (Tuple data : list) {
				// Response 
				AcExecutiveProductDropdownRes res = new AcExecutiveProductDropdownRes();
				res.setAcExecutiveId(data.get("acExecutiveId")==null?"":data.get("acExecutiveId").toString());
				res.setAcExecutiveName(data.get("acExecutiveName")==null?"":data.get("acExecutiveName").toString());
				res.setBankCode(data.get("bankCode")==null?"":data.get("bankCode").toString());		
				res.setBankName(data.get("bankName")==null?"":data.get("bankName").toString());
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
