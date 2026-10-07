package com.maan.eway.master.service.impl;

import java.math.BigDecimal;
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
import com.maan.eway.bean.BrokerCommissionDetails;
import com.maan.eway.bean.LoginMaster;
import com.maan.eway.bean.LoginProductMaster;
import com.maan.eway.bean.PolicyTypeMaster;
import com.maan.eway.error.Error;
import com.maan.eway.master.req.BrokerBackdaysGetReq;
import com.maan.eway.master.req.BrokerCommissionDetailsMasterChangeStatusReq;
import com.maan.eway.master.req.BrokerCommissionDetailsMasterGetReq;
import com.maan.eway.master.req.BrokerCommissionDetailsMasterGetallReq;
import com.maan.eway.master.req.BrokerCommissionDetailsMasterSaveReq;
import com.maan.eway.master.res.BrokerCommRes;
import com.maan.eway.master.res.BrokerCommissionDetailsMasterGetRes;
import com.maan.eway.master.service.BrokerCommissionDetailsMasterService;
import com.maan.eway.repository.BrokerCommissionDetailsRepository;
import com.maan.eway.repository.ListItemValueRepository;
import com.maan.eway.repository.LoginMasterRepository;
import com.maan.eway.repository.LoginProductMasterRepository;
import com.maan.eway.repository.PolicyTypeMasterRepository;
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
public class BrokerCommissionDetailsMasterServiceImpl implements BrokerCommissionDetailsMasterService {

	@PersistenceContext 
	private EntityManager em;
	
	@Autowired
	private BrokerCommissionDetailsRepository repo;
	
	@Autowired
	private ListItemValueRepository listrepo;

	@Autowired
	private PolicyTypeMasterRepository policyRepo;
	
	@Autowired
	private LoginMasterRepository loginRepo;

	@Autowired
	private LoginProductMasterRepository loginProductMasterRepository;
	
	Gson json = new Gson();
	
	private Logger log = LogManager.getLogger(BrokerCommissionDetailsMasterServiceImpl.class);


	@Override
	public List<Error> validateBrokerCommission(BrokerCommissionDetailsMasterSaveReq req) {
		// TODO Auto-generated method stub
		List<Error> errorList = new ArrayList<Error>();

		try {
			if (StringUtils.isBlank(req.getProductId())) {
				errorList.add(new Error("01", "ProductId", "Please Enter ProductId"));
			}
			
			if (StringUtils.isBlank(req.getCompanyId())) {
				errorList.add(new Error("02", "CompanyId", "Please Enter CompanyId"));
			}
						
			if (StringUtils.isBlank(req.getRemarks())) {
				errorList.add(new Error("03", "Remarks", "Please Enter Remarks "));
			}else if (req.getRemarks().length() > 100){
				errorList.add(new Error("03","Remarks", "Please Enter Remarks within 100 Characters")); 
			}
			
			// Date Validation 
			Calendar cal = new GregorianCalendar();
			Date today = new Date();
			cal.setTime(today);cal.add(Calendar.DAY_OF_MONTH, -1);;
			today = cal.getTime();
			if (req.getEffectiveDateStart() == null || StringUtils.isBlank(req.getEffectiveDateStart().toString())) {
				errorList.add(new Error("04", "EffectiveDateStart", "Please Enter Effective Date Start"));

			} else if (req.getEffectiveDateStart().before(today)) {
				errorList.add(new Error("04", "EffectiveDateStart", "Please Enter Effective Date Start as Future Date"));
			}
			//Status Validation
			if (StringUtils.isBlank(req.getStatus())) {
				errorList.add(new Error("05", "Status", "Please Select Status  "));
			} else if (req.getStatus().length() > 1) {
				errorList.add(new Error("05", "Status", "Please Select Valid Status - One Character Only Allwed"));
			}else if(!("Y".equalsIgnoreCase(req.getStatus())||"N".equalsIgnoreCase(req.getStatus())||"R".equalsIgnoreCase(req.getStatus())|| "P".equalsIgnoreCase(req.getStatus()))) {
				errorList.add(new Error("05", "Status", "Please Select Valid Status - Active or Deactive or Pending or Referral "));
			}
			if (StringUtils.isBlank(req.getCreatedBy())) {
				errorList.add(new Error("06", "CreatedBy", "Please Enter CreatedBy"));
			}else if (req.getCreatedBy().length() > 100){
				errorList.add(new Error("06","CreatedBy", "Please Enter CreatedBy within 100 Characters")); 
			}
			
			if (StringUtils.isBlank(req.getAgencyCode())) {
				errorList.add(new Error("07", "AgencyCode", "Please Enter AgencyCode"));
			}else if (req.getAgencyCode().length() > 100){
				errorList.add(new Error("07","AgencyCode", "Please Enter AgencyCode within 100 Characters")); 
			}
			
			if (StringUtils.isBlank(req.getOaCode())) {
				errorList.add(new Error("08", "OaCode", "Please Enter OaCode"));
			}else if (req.getOaCode().length() > 100){
				errorList.add(new Error("08","OaCode", "Please Enter OaCode within 100 Characters")); 
			}
			
			if (StringUtils.isBlank(req.getLoginId())) {
				errorList.add(new Error("09", "LoginId", "Please Enter LoginId"));
			}else if (req.getLoginId().length() > 100){
				errorList.add(new Error("09","LoginId", "Please Enter LoginId within 100 Characters")); 
			}
			
			if (StringUtils.isBlank(req.getPolicyType())) {
				errorList.add(new Error("10", "PolicyType", "Please Enter PolicyType"));
			}else if (req.getPolicyType().length() > 100){
				errorList.add(new Error("10","PolicyType", "Please Enter PolicyType within 100 Characters")); 
			}
			
			if (StringUtils.isBlank(req.getSuminsuredStart())) {
				errorList.add(new Error("11", "SuminsuredStart", "Please Enter SuminsuredStart"));
			}
			if ((StringUtils.isNotBlank(req.getSuminsuredStart()))
					&& !req.getSuminsuredStart().matches("[0-9]+")){
				errorList.add(new Error("11","SuminsuredStart", "Please Enter SuminsuredStart in correct format")); 
				
			}
			if (StringUtils.isBlank(req.getSuminsuredEnd())) {
				errorList.add(new Error("12", "SuminsuredEnd", "Please Enter SuminsuredEnd"));
			}
			if ((StringUtils.isNotBlank(req.getSuminsuredEnd()))
					&& !req.getSuminsuredEnd().matches("[0-9]+")){
				errorList.add(new Error("12","SuminsuredEnd", "Please Enter SuminsuredEnd in correct format")); 
				
			}
			if ((StringUtils.isNotBlank(req.getSuminsuredEnd()))
					&& (Double.valueOf(req.getSuminsuredEnd()))<0  ){
				errorList.add(new Error("12","SuminsuredEnd", "Please Enter SuminsuredEnd above zero")); 
				
			}
			if (StringUtils.isBlank(req.getCommissionPercentage())) {
				errorList.add(new Error("13", "Commission Percentage", "Please Enter Commission Percentage"));
			}
			if ((StringUtils.isNotBlank(req.getCommissionPercentage()))
					&& !req.getCommissionPercentage().matches("[0-9.]+")){
				errorList.add(new Error("13","CommissionPercentage", "Please Enter CommissionPercentage in correct format")); 
				
			}
			if ((StringUtils.isNotBlank(req.getCommissionPercentage()))
					&& (Double.valueOf(req.getCommissionPercentage()))<0  ){
				errorList.add(new Error("13","CommissionPercentage", "Please Enter CommissionPercentage above zero")); 
				
			}
			if (StringUtils.isBlank(req.getCheckerYn())) {
				errorList.add(new Error("14", "CheckerYn", "Please Select CheckerYn"));
			}
			if((StringUtils.isNotBlank(req.getSuminsuredStart()))&&  (StringUtils.isNotBlank(req.getSuminsuredEnd()))) {
				Double start = Double.valueOf(req.getSuminsuredStart());
				Double end = Double.valueOf(req.getSuminsuredEnd());					
				if(start>end) {
					errorList.add(new Error("15", "Sum Insured", "Sum Insured Start Should not be greater than Sum insured End"));					
				}
			}
			
			if(StringUtils.isNotBlank(req.getCommissionPercentage())) {
				Double a = Double.valueOf(req.getCommissionPercentage());
				if(a>20) {
					errorList.add(new Error("16", "Commission Percentage", "Commission Percentage Start Should be greater than 20 %"));
					}
				}
			if (StringUtils.isNotBlank(req.getSuminsuredStart())) {
					Double a= Double.valueOf(req.getSuminsuredStart());
					if(a<=0.0) {
				//errorList.add(new Error("11","SuminsuredStart", "Please Enter SuminsuredStart above zero")); 
					}
			}
			if (StringUtils.isNotBlank(req.getSuminsuredEnd())) {
				Double a= Double.valueOf(req.getSuminsuredEnd());
				if(a<=0.0) {
			errorList.add(new Error("12","SuminsuredEnd", "Please Enter SuminsuredEnd above zero")); 
				}
		}
			if((StringUtils.isNotBlank(req.getCompanyId())) && (StringUtils.isNotBlank(req.getProductId()))
					&&(StringUtils.isNotBlank(req.getPolicyType()))&&(StringUtils.isNotBlank(req.getLoginId()))){
				String policytype = policyName(req.getCompanyId(),req.getProductId(),req.getPolicyType());	
				
				if (StringUtils.isBlank(req.getId()) &&  StringUtils.isNotBlank(req.getCompanyId())&& StringUtils.isNotBlank(req.getProductId())&& StringUtils.isNotBlank(req.getPolicyType())&& StringUtils.isNotBlank(req.getLoginId())) {
					List<BrokerCommissionDetails> policylist = getPolicyName(policytype , req.getCompanyId() , req.getProductId(), req.getLoginId());
					if (policylist.size()>0 ) {
						errorList.add(new Error("13", "Policy Type", "This Policy Type Already Exist "));
					}
				}else if (StringUtils.isNotBlank(req.getId()) &&  StringUtils.isNotBlank(req.getCompanyId()) && StringUtils.isNotBlank(req.getProductId())&& StringUtils.isNotBlank(req.getPolicyType())&& StringUtils.isNotBlank(req.getLoginId())) {
					List<BrokerCommissionDetails> policyList = getPolicyName(policytype , req.getCompanyId() , req.getProductId(),req.getLoginId());
					
					if (policyList.size()>0 &&  (! req.getId().equalsIgnoreCase(policyList.get(0).getId().toString())) ) {
						errorList.add(new Error("13", "Policy Type", "This Policy Type Already Exist "));
					}
			}
		}
			
		}catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return errorList;
	}

	
	

	private List<BrokerCommissionDetails> getPolicyName(String policytype, String companyId, String productId, String loginId) {
		// TODO Auto-generated method stub
		List<BrokerCommissionDetails> list = new ArrayList<BrokerCommissionDetails>();
		try {
			Date today = new Date();
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<BrokerCommissionDetails> query = cb.createQuery(BrokerCommissionDetails.class);

			// Find All
			Root<BrokerCommissionDetails> b = query.from(BrokerCommissionDetails.class);

			// Select
			query.select(b);

			// Effective Date Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<BrokerCommissionDetails> ocpm1 = amendId.from(BrokerCommissionDetails.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("id"), b.get("id"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			Predicate a4 = cb.equal(ocpm1.get("policyType"), b.get("policyType"));
			Predicate a5 = cb.equal(ocpm1.get("loginId"), b.get("loginId"));
			
			amendId.where(a1,a2,a3,a4,a5);

			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(cb.lower( b.get("policyTypeDesc")), policytype.toLowerCase());
			Predicate n3 = cb.equal(b.get("companyId"),companyId);
			Predicate n4 = cb.equal(b.get("productId"),productId);
			Predicate n5 = cb.equal(b.get("loginId"),loginId);
			
			query.where(n1,n2,n3,n4,n5);
			
			// Get Result
			TypedQuery<BrokerCommissionDetails> result = em.createQuery(query);
			list = result.getResultList();		
		
		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());

		}
		return list;
	}





	@Override
	public SuccessRes saveBrokerCommission(BrokerCommissionDetailsMasterSaveReq req) {
		// TODO Auto-generated method stub
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		SuccessRes res = new SuccessRes();
		BrokerCommissionDetails saveData = new BrokerCommissionDetails();
		List<BrokerCommissionDetails> list  = new ArrayList<BrokerCommissionDetails>();
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
			Integer id = 0;
			if(StringUtils.isBlank(req.getId())) {
				Integer totalCount = getMasterTableCount(req.getCompanyId(),req.getProductId());
				id = totalCount+1;
				entryDate = new Date();
				createdBy = req.getCreatedBy();
				res.setResponse("Saved Successfully");
				res.setSuccessId(id.toString());
			}
			
			else {
				id = Integer.valueOf(req.getId());
				CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<BrokerCommissionDetails> query = cb.createQuery(BrokerCommissionDetails.class);
			//Findall
			Root<BrokerCommissionDetails> b = query.from(BrokerCommissionDetails.class);
			//select
			query.select(b);
			//Orderby
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(b.get("effectiveDateStart")));
			//Where
			Predicate n1 = cb.equal(b.get("loginId"),req.getLoginId());
			Predicate n2 = cb.equal(b.get("companyId"),req.getCompanyId());
			Predicate n3 = cb.equal(b.get("productId"),req.getProductId());
			Predicate n4 = cb.equal(b.get("oaCode"),req.getOaCode());
			Predicate n5 = cb.equal(b.get("agencyCode"),req.getAgencyCode());
			Predicate n6 = cb.equal(b.get("policyType"),req.getPolicyType());

			query.where(n1,n2,n3,n4,n5,n6).orderBy(orderList);
			
			// Get Result
			TypedQuery<BrokerCommissionDetails> result = em.createQuery(query);
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
					BrokerCommissionDetails lastRecord = list.get(0);
					lastRecord.setEffectiveDateEnd(oldEndDate);
					repo.saveAndFlush(lastRecord);
				}
				else {
					amendId = list.get(0).getAmendId();
					entryDate = list.get(0).getEntryDate();
					createdBy = list.get(0).getCreatedBy();
					saveData = list.get(0);
					if(list.size()>1) {
						BrokerCommissionDetails lastRecord = list.get(1);	
						lastRecord.setEffectiveDateEnd(oldEndDate);
						repo.saveAndFlush(lastRecord);
					}
				}
			}
			res.setResponse("Updated Successfully");
			res.setSuccessId(req.getId());

			}
	
		String policytype = policyName(req.getCompanyId(),req.getProductId(),req.getPolicyType());	
			
		dozerMapper.map(req, saveData);
		saveData.setEffectiveDateStart(StartDate);
		saveData.setEffectiveDateEnd(endDate);
		saveData.setCreatedBy(req.getCreatedBy());
		saveData.setEntryDate(new Date());
		saveData.setUpdatedBy(req.getCreatedBy());
		saveData.setUpdatedDate(new Date());
		saveData.setAmendId(amendId);
		saveData.setSuminsuredStart(new BigDecimal(req.getSuminsuredStart()));
		saveData.setSuminsuredEnd(new BigDecimal(req.getSuminsuredEnd()));
		saveData.setCommissionPercentage(Double.valueOf(req.getCommissionPercentage()));
		saveData.setId(id);
		saveData.setPolicyTypeDesc(policytype);
		repo.save(saveData);
		
		}
		catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		
		return res;

	}

	private String policyName(String companyId, String productId, String policyTypeId) {
		// TODO Auto-generated method stub
		String data="";
		try {
		List<PolicyTypeMaster> list = new ArrayList<PolicyTypeMaster>();
		// Find Latest Record
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<PolicyTypeMaster> query = cb.createQuery(PolicyTypeMaster.class);
		//Find all
		Root<PolicyTypeMaster> b = query.from(PolicyTypeMaster.class);
		// Select
		query.select(b);
		// Effective Date Max Filter
		Subquery<Date> effectiveDate = query.subquery(Date.class);
		Root<PolicyTypeMaster> ocpm1 = effectiveDate.from(PolicyTypeMaster.class);
		effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
		Predicate a1 = cb.equal(ocpm1.get("policyTypeId"),b.get("policyTypeId"));
		Predicate a2 = cb.equal(ocpm1.get("companyId"),b.get("companyId"));
		Predicate a3 = cb.equal(ocpm1.get("productId"),b.get("productId"));

		effectiveDate.where(a1,a2,a3);
	
		//OrderBy
		List<Order> orderList = new ArrayList<Order>();
		orderList.add(cb.desc(b.get("amendId")));
		
		Predicate n1 = cb.equal(b.get("effectiveDateStart"),effectiveDate);
		Predicate n2 = cb.equal(b.get("companyId"),companyId);
		Predicate n3 = cb.equal(b.get("productId"),productId);
		Predicate n4 = cb.equal(b.get("policyTypeId"),policyTypeId);
		
		query.where(n1,n2,n3,n4).orderBy(orderList);
		
		
		
		// Get Result
		TypedQuery<PolicyTypeMaster> result = em.createQuery(query);
		int limit = 0 , offset = 1 ;
		result.setFirstResult(limit * offset);
		result.setMaxResults(offset);
		list = result.getResultList();
		data = list.size() > 0 ? list.get(0).getPolicyTypeName() : "" ;
	}
	catch(Exception e) {
		e.printStackTrace();
		log.info(e.getMessage());
	}
	return data;
}




	private Integer getMasterTableCount(String companyId, String productId) {
		// TODO Auto-generated method stub
		Integer data =0;
		try {
			List<BrokerCommissionDetails> list = new ArrayList<BrokerCommissionDetails>();
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<BrokerCommissionDetails> query = cb.createQuery(BrokerCommissionDetails.class);
			//Find all
			Root<BrokerCommissionDetails> b = query.from(BrokerCommissionDetails.class);
			// Select
			query.select(b);
			// Effective Date Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<BrokerCommissionDetails> ocpm1 = effectiveDate.from(BrokerCommissionDetails.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(ocpm1.get("id"),b.get("id"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"),b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("productId"),b.get("productId"));

			effectiveDate.where(a1,a2,a3);
		
			//OrderBy
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(b.get("id")));
			
			Predicate n1 = cb.equal(b.get("effectiveDateStart"),effectiveDate);
			Predicate n2 = cb.equal(b.get("companyId"),companyId);
			Predicate n3 = cb.equal(b.get("productId"),productId);
			query.where(n1,n2,n3).orderBy(orderList);
			
			
			
			// Get Result
			TypedQuery<BrokerCommissionDetails> result = em.createQuery(query);
			int limit = 0 , offset = 1 ;
			result.setFirstResult(limit * offset);
			result.setMaxResults(offset);
			list = result.getResultList();
			data = list.size() > 0 ? list.get(0).getId() : 0 ;
		}
		catch(Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
		}
		return data;
	}




	@Override
	public BrokerCommissionDetailsMasterGetRes getBrokerCommission(BrokerCommissionDetailsMasterGetReq req) {
		// TODO Auto-generated method stub
		DozerBeanMapper mapper = new DozerBeanMapper();
		BrokerCommissionDetailsMasterGetRes res = new BrokerCommissionDetailsMasterGetRes();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();

			List<BrokerCommissionDetails> list = new ArrayList<BrokerCommissionDetails>();
		
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<BrokerCommissionDetails> query = cb.createQuery(BrokerCommissionDetails.class);

			// Find All
			Root<BrokerCommissionDetails> b = query.from(BrokerCommissionDetails.class);

			// Select
			query.select(b);

			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<BrokerCommissionDetails> ocpm1 = amendId.from(BrokerCommissionDetails.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("agencyCode"), b.get("agencyCode"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			Predicate a4 = cb.equal(ocpm1.get("oaCode"), b.get("oaCode"));
			Predicate a5 = cb.equal(ocpm1.get("loginId"), b.get("loginId"));
			Predicate a6 = cb.equal(ocpm1.get("policyType"), b.get("policyType"));
			Predicate a7 = cb.equal(ocpm1.get("id"), b.get("id"));

			amendId.where(a1, a2,a3,a4,a5,a6,a7);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("policyType")));

			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
			Predicate n3 = cb.equal(b.get("productId"),req.getProductId());
			Predicate n4 = cb.equal(b.get("oaCode"),req.getOaCode());
			Predicate n5 = cb.equal(b.get("loginId"),req.getLoginId());
			Predicate n6 = cb.equal(b.get("policyType"),req.getPolicyType());
			Predicate n7 = cb.equal(b.get("id"),req.getId());
			
			query.where(n1,n2,n3,n4,n5,n6,n7).orderBy(orderList);
			
			// Get Result
			TypedQuery<BrokerCommissionDetails> result = em.createQuery(query);

			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getPolicyType()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(BrokerCommissionDetails :: getPolicyType ));
		
			if(list!=null && list.size()>0) {
			res = mapper.map(list.get(0), BrokerCommissionDetailsMasterGetRes.class);
     		res.setAmendId(list.get(0).getAmendId().toString());
			res.setEntryDate(list.get(0).getEntryDate());
			res.setEffectiveDateStart(list.get(0).getEffectiveDateStart());
			res.setEffectiveDateEnd(list.get(0).getEffectiveDateEnd());
			res.setProductId(list.get(0).getProductId().toString());
			res.setStatus(list.get(0).getStatus());
			res.setCompanyId(list.get(0).getCompanyId());
			res.setRemarks(list.get(0).getRemarks());
			res.setCreatedBy(list.get(0).getCreatedBy());
			res.setUpdatedBy(list.get(0).getUpdatedBy());	
			res.setCommissionPercentage(list.get(0).getCommissionPercentage().toString());
			res.setSuminsuredStart(list.get(0).getSuminsuredStart().toString());
			res.setSuminsuredEnd(list.get(0).getSuminsuredEnd().toString());
			res.setId(list.get(0).getId().toString());
			res.setPolicyTypeDesc(list.get(0).getPolicyTypeDesc());
			}
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
	public List<BrokerCommissionDetailsMasterGetRes> getallBrokerCommission(
			BrokerCommissionDetailsMasterGetallReq req) {
		// TODO Auto-generated method stub
		DozerBeanMapper mapper = new DozerBeanMapper();
		List<BrokerCommissionDetailsMasterGetRes> resList = new ArrayList<BrokerCommissionDetailsMasterGetRes>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();

			List<BrokerCommissionDetails> list = new ArrayList<BrokerCommissionDetails>();
		
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<BrokerCommissionDetails> query = cb.createQuery(BrokerCommissionDetails.class);

			// Find All
			Root<BrokerCommissionDetails> b = query.from(BrokerCommissionDetails.class);

			// Select
			query.select(b);

			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<BrokerCommissionDetails> ocpm1 = amendId.from(BrokerCommissionDetails.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("agencyCode"), b.get("agencyCode"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			Predicate a4 = cb.equal(ocpm1.get("oaCode"), b.get("oaCode"));
			Predicate a5 = cb.equal(ocpm1.get("loginId"), b.get("loginId"));
			Predicate a6 = cb.equal(ocpm1.get("policyType"), b.get("policyType"));

			amendId.where(a1, a2,a3,a4,a5,a6);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("policyType")));

			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
			Predicate n3 = cb.equal(b.get("productId"),req.getProductId());
			Predicate n4 = cb.equal(b.get("oaCode"),req.getOaCode());
			Predicate n5 = cb.equal(b.get("loginId"),req.getLoginId());
			
			query.where(n1,n2,n3,n4,n5).orderBy(orderList);
			
			// Get Result
			TypedQuery<BrokerCommissionDetails> result = em.createQuery(query);

			
			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getPolicyType()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(BrokerCommissionDetails :: getPolicyType ));
			if(list!=null && list.size()>0) {
			for(BrokerCommissionDetails data : list) {
			BrokerCommissionDetailsMasterGetRes res = new BrokerCommissionDetailsMasterGetRes();
         	res = mapper.map(data, BrokerCommissionDetailsMasterGetRes.class);
     		res.setAmendId(data.getAmendId().toString());
			res.setEntryDate(data.getEntryDate());
			res.setEffectiveDateStart(data.getEffectiveDateStart());
			res.setEffectiveDateEnd(data.getEffectiveDateEnd());
			res.setProductId(data.getProductId().toString());
			res.setStatus(data.getStatus());
			res.setCompanyId(data.getCompanyId());
			res.setRemarks(data.getRemarks());
			res.setCreatedBy(data.getCreatedBy());
			res.setUpdatedBy(data.getUpdatedBy());
			res.setCommissionPercentage(data.getCommissionPercentage().toString());
			res.setSuminsuredStart(data.getSuminsuredStart().toString());
			res.setSuminsuredEnd(data.getSuminsuredEnd().toString());
			res.setId(data.getId().toString());
			res.setPolicyTypeDesc(data.getPolicyTypeDesc());
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
	public List<BrokerCommissionDetailsMasterGetRes> getactiveBrokerCommission(
			BrokerCommissionDetailsMasterGetallReq req) {
		// TODO Auto-generated method stub
		DozerBeanMapper mapper = new DozerBeanMapper();
		List<BrokerCommissionDetailsMasterGetRes> resList = new ArrayList<BrokerCommissionDetailsMasterGetRes>();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();

			List<BrokerCommissionDetails> list = new ArrayList<BrokerCommissionDetails>();
		
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<BrokerCommissionDetails> query = cb.createQuery(BrokerCommissionDetails.class);

			// Find All
			Root<BrokerCommissionDetails> b = query.from(BrokerCommissionDetails.class);

			// Select
			query.select(b);

			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<BrokerCommissionDetails> ocpm1 = amendId.from(BrokerCommissionDetails.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("agencyCode"), b.get("agencyCode"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			Predicate a4 = cb.equal(ocpm1.get("oaCode"), b.get("oaCode"));
			Predicate a5 = cb.equal(ocpm1.get("loginId"), b.get("loginId"));
			Predicate a6 = cb.equal(ocpm1.get("policyType"), b.get("policyType"));

			amendId.where(a1, a2,a3,a4,a5,a6);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("policyType")));

			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
			Predicate n3 = cb.equal(b.get("productId"),req.getProductId());
			Predicate n4 = cb.equal(b.get("oaCode"),req.getOaCode());
			Predicate n5 = cb.equal(b.get("loginId"),req.getLoginId());
			Predicate n6 = cb.equal(b.get("status"),"Y");
			
			query.where(n1,n2,n3,n4,n5,n6).orderBy(orderList);
			
			// Get Result
			TypedQuery<BrokerCommissionDetails> result = em.createQuery(query);

			
			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getPolicyType()))).collect(Collectors.toList());
			list.sort(Comparator.comparing(BrokerCommissionDetails :: getPolicyType ));
			if(list!=null && list.size()>0) {
			for(BrokerCommissionDetails data : list) {
			BrokerCommissionDetailsMasterGetRes res = new BrokerCommissionDetailsMasterGetRes();
         	res = mapper.map(data, BrokerCommissionDetailsMasterGetRes.class);
     		res.setAmendId(data.getAmendId().toString());
			res.setEntryDate(data.getEntryDate());
			res.setEffectiveDateStart(data.getEffectiveDateStart());
			res.setEffectiveDateEnd(data.getEffectiveDateEnd());
			res.setProductId(data.getProductId().toString());
			res.setStatus(data.getStatus());
			res.setCompanyId(data.getCompanyId());
			res.setRemarks(data.getRemarks());
			res.setCreatedBy(data.getCreatedBy());
			res.setUpdatedBy(data.getUpdatedBy());
			res.setCommissionPercentage(data.getCommissionPercentage().toString());
			res.setSuminsuredStart(data.getSuminsuredStart().toString());
			res.setSuminsuredEnd(data.getSuminsuredEnd().toString());
			res.setPolicyTypeDesc(data.getPolicyTypeDesc());
			res.setId(data.getId().toString());
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
	public SuccessRes changeStatusBrokerCommission(BrokerCommissionDetailsMasterChangeStatusReq req) {
		// TODO Auto-generated method stub
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		SuccessRes res = new SuccessRes();
		BrokerCommissionDetails saveData = new BrokerCommissionDetails();
		List<BrokerCommissionDetails> list  = new ArrayList<BrokerCommissionDetails>();
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
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<BrokerCommissionDetails> query = cb.createQuery(BrokerCommissionDetails.class);
			//Findall
			Root<BrokerCommissionDetails> b = query.from(BrokerCommissionDetails.class);
			//select
			query.select(b);
			//Orderby
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(b.get("effectiveDateStart")));
			//Where
			Predicate n1 = cb.equal(b.get("loginId"),req.getLoginId());
			Predicate n2 = cb.equal(b.get("companyId"),req.getCompanyId());
			Predicate n3 = cb.equal(b.get("productId"),req.getProductId());
			Predicate n4 = cb.equal(b.get("oaCode"),req.getOaCode());
			Predicate n5 = cb.equal(b.get("agencyCode"),req.getAgencyCode());
			Predicate n6 = cb.equal(b.get("policyType"),req.getPolicyType());
			Predicate n7 = cb.equal(b.get("id"),req.getId());

			query.where(n1,n2,n3,n4,n5,n6,n7).orderBy(orderList);
			
			// Get Result
			TypedQuery<BrokerCommissionDetails> result = em.createQuery(query);
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
					BrokerCommissionDetails lastRecord = list.get(0);
					lastRecord.setEffectiveDateEnd(oldEndDate);
					repo.saveAndFlush(lastRecord);
				}
				else {
					amendId = list.get(0).getAmendId();
					entryDate = list.get(0).getEntryDate();
					createdBy = list.get(0).getCreatedBy();
					saveData = list.get(0);
					if(list.size()>1) {
						BrokerCommissionDetails lastRecord = list.get(1);	
						lastRecord.setEffectiveDateEnd(oldEndDate);
						repo.saveAndFlush(lastRecord);
					}
				}
			}
		
	
		dozerMapper.map(list.get(0), saveData);
		saveData.setEffectiveDateStart(StartDate);
		saveData.setEffectiveDateEnd(endDate);
		saveData.setCreatedBy(req.getCreatedBy());
		saveData.setEntryDate(new Date());
		saveData.setUpdatedBy(req.getCreatedBy());
		saveData.setUpdatedDate(new Date());
		saveData.setAmendId(amendId);
		saveData.setSuminsuredStart(list.get(0).getSuminsuredStart());
		saveData.setSuminsuredEnd(list.get(0).getSuminsuredEnd());
		saveData.setCommissionPercentage(Double.valueOf(list.get(0).getCommissionPercentage()));
		saveData.setStatus(req.getStatus());
		saveData.setId(Integer.valueOf(req.getId()));
		repo.save(saveData);
		res.setResponse("Status Changed Successfully");
		res.setSuccessId(req.getPolicyType());

		}
		catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		
		return res;

	}




	@Override
	public BrokerCommRes getBackDays(BrokerBackdaysGetReq req) {
		BrokerCommRes brokerRes  = new BrokerCommRes() ;
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
			 String companyId = req.getCompanyId();
	            String loginId = req.getLoginId();
	            String productId = req.getProductId();
			   LoginMaster login = loginRepo.findByCompanyIdAndLoginId(companyId,loginId);
	            String userType = login != null ? login.getUserType() : null;

            if (!"BROKER".equalsIgnoreCase(userType)) {

                List<LoginProductMaster> result = loginProductMasterRepository
                        .findByCompanyIdAndLoginIdAndProductIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqualOrderByEffectiveDateStartAsc(
                                companyId,
                                loginId,
                                Integer.valueOf(productId),
                                today,
                                today
                        );
                Integer   backDays = result.isEmpty() ? 14 : result.get(0).getBackDays();
                brokerRes.setBackDays(backDays.toString() );
                return brokerRes;
            }

			List<BrokerCommissionDetails> list = new ArrayList<BrokerCommissionDetails>();
		
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<BrokerCommissionDetails> query = cb.createQuery(BrokerCommissionDetails.class);

			// Find All
			Root<BrokerCommissionDetails> b = query.from(BrokerCommissionDetails.class);

			// Select
			query.select(b);

			// Effective Date Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<BrokerCommissionDetails> ocpm1 = effectiveDate.from(BrokerCommissionDetails.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			jakarta.persistence.criteria.Predicate a1 = cb.equal(b.get("companyId"), ocpm1.get("companyId"));
			jakarta.persistence.criteria.Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			jakarta.persistence.criteria.Predicate a3 = cb.equal(b.get("loginId"), ocpm1.get("loginId"));
			jakarta.persistence.criteria.Predicate a4 = cb.equal(b.get("productId"), ocpm1.get("productId"));
//			jakarta.persistence.criteria.Predicate a11 = cb.equal(b.get("policyType"), ocpm1.get("policyType"));
//			jakarta.persistence.criteria.Predicate a12 = cb.equal(b.get("id"), ocpm1.get("id"));
			effectiveDate.where(a1, a2, a3,a4);
			
			// Effective Date End Max Filter
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<BrokerCommissionDetails> ocpm2 = effectiveDate2.from(BrokerCommissionDetails.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			jakarta.persistence.criteria.Predicate a6 = cb.equal(b.get("companyId"), ocpm2.get("companyId"));
			jakarta.persistence.criteria.Predicate a8 = cb.equal(b.get("productId"), ocpm2.get("productId"));
			jakarta.persistence.criteria.Predicate a9 = cb.equal(b.get("loginId"), ocpm2.get("loginId"));
			jakarta.persistence.criteria.Predicate a10 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
//			jakarta.persistence.criteria.Predicate a13 = cb.equal(b.get("policyType"), ocpm2.get("policyType"));
//			jakarta.persistence.criteria.Predicate a14 = cb.equal(b.get("id"), ocpm2.get("id"));
			effectiveDate2.where(a6,  a8, a9, a10);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("policyType")));

			// Where
			Predicate n1 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
			Predicate n2 = cb.equal(b.get("effectiveDateEnd"), effectiveDate2);
			Predicate n3 = cb.equal(b.get("companyId"), req.getCompanyId());
			Predicate n4 = cb.equal(b.get("productId"), req.getProductId());
			Predicate n5 = cb.equal(b.get("loginId"),  req.getLoginId());
//			Predicate n6 = cb.equal(b.get("policyType"),"99999");
//			Predicate n7 = cb.equal(b.get("id"),"99999");
			query.where(n1,n2,n3,n4,n5).orderBy(orderList);
			
			// Get Result
			TypedQuery<BrokerCommissionDetails> result = em.createQuery(query);

			list = result.getResultList();
			Integer backDays = list.size() > 0 ? (list.get(0).getBackDays() !=null ? list.get(0).getBackDays() : 14)  : 14;
			brokerRes.setBackDays(backDays.toString() );
			} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return brokerRes;
	}



	@Override
	public List<BrokerCommissionDetailsMasterGetRes> getUnOptedBrokerCommission(
			BrokerCommissionDetailsMasterGetallReq req) {
		List<BrokerCommissionDetailsMasterGetRes> resList = new ArrayList<BrokerCommissionDetailsMasterGetRes>() ;
		DozerBeanMapper mapper = new DozerBeanMapper(); 
		try {
			List<BrokerCommissionDetails> companyComList = getCompanyCommissionList(req.getCompanyId() , req.getProductId());
			
			List<PolicyTypeMaster> policyTypes = getPolicyTypeList(req.getCompanyId() , req.getProductId());
			
			for (PolicyTypeMaster pol : policyTypes) {
				List<BrokerCommissionDetails> filterAlreadyOpt = companyComList.stream().filter(o -> o.getProductId().equalsIgnoreCase(pol.getProductId().toString()) 
						&& o.getPolicyType().equalsIgnoreCase(pol.getPolicyTypeId().toString()) ).collect(Collectors.toList());
				if(filterAlreadyOpt.size() <=0 ) {
					BrokerCommissionDetailsMasterGetRes res = new BrokerCommissionDetailsMasterGetRes();
		         	res = mapper.map(pol, BrokerCommissionDetailsMasterGetRes.class);
		     		res.setAmendId(pol.getAmendId().toString());
					res.setEntryDate(pol.getEntryDate());
					res.setEffectiveDateStart(pol.getEffectiveDateStart());
					res.setEffectiveDateEnd(pol.getEffectiveDateEnd());
					res.setProductId(pol.getProductId().toString());
					res.setStatus(pol.getStatus());
					res.setCompanyId(pol.getCompanyId());
					res.setRemarks(pol.getRemarks());
					res.setCreatedBy(pol.getCreatedBy());
					res.setUpdatedBy(pol.getUpdatedBy());
					res.setCommissionPercentage(null);
					res.setSuminsuredStart("0");
					res.setSuminsuredEnd("0");
					res.setPolicyType(pol.getPolicyTypeId().toString());
					res.setPolicyTypeDesc(pol.getPolicyTypeName());
					res.setId(pol.getPolicyTypeId().toString());
					res.setLoginId("99999");
					res.setAgencyCode("99999");
					res.setOaCode("99999");
					res.setCheckerYn("N");
					resList.add(res);
				} else {
					BrokerCommissionDetails comm = filterAlreadyOpt.get(0);
					BrokerCommissionDetailsMasterGetRes res = new BrokerCommissionDetailsMasterGetRes();
		         	res = mapper.map(comm, BrokerCommissionDetailsMasterGetRes.class);
		         	res.setAmendId(comm.getAmendId().toString());
					res.setEntryDate(comm.getEntryDate());
					res.setEffectiveDateStart(comm.getEffectiveDateStart());
					res.setEffectiveDateEnd(comm.getEffectiveDateEnd());
					res.setProductId(comm.getProductId().toString());
					res.setStatus(comm.getStatus());
					res.setCompanyId(comm.getCompanyId());
					res.setRemarks(comm.getRemarks());
					res.setCreatedBy(comm.getCreatedBy());
					res.setUpdatedBy(comm.getUpdatedBy());
					res.setCommissionPercentage(comm.getCommissionPercentage().toString());
					res.setSuminsuredStart(comm.getSuminsuredStart().toString());
					res.setSuminsuredEnd(comm.getSuminsuredEnd().toString());
					res.setPolicyTypeDesc(comm.getPolicyTypeDesc());
					res.setId(comm.getId().toString());
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

	
	public List<BrokerCommissionDetails> getCompanyCommissionList(String insuranceId , String productId ) {
		List<BrokerCommissionDetails> brokerComlist = new ArrayList<BrokerCommissionDetails>();
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
			
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<BrokerCommissionDetails> query = cb.createQuery(BrokerCommissionDetails.class);
	
			// Find All
			Root<BrokerCommissionDetails> b = query.from(BrokerCommissionDetails.class);
	
			// Select
			query.select(b);
	
			// Effective Date Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<BrokerCommissionDetails> ocpm1 = effectiveDate.from(BrokerCommissionDetails.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.lessThanOrEqualTo(b.get("effectiveDateStart"),today);
			Predicate a7 = cb.equal(ocpm1.get("loginId"), b.get("loginId"));
			effectiveDate.where(a1,a2,a3,a7);
	
			// Effective Date End
			Subquery<Date> effectiveDate5 = query.subquery(Date.class);
			Root<BrokerCommissionDetails> ocpm5 = effectiveDate5.from(BrokerCommissionDetails.class);
			effectiveDate5.select(cb.greatest(ocpm5.get("effectiveDateEnd").as(Date.class)));
			Predicate a4 = cb.equal(b.get("productId"),ocpm5.get("productId") );
			Predicate a5 = cb.equal(ocpm5.get("companyId"), b.get("companyId"));
			Predicate a6 = cb.greaterThanOrEqualTo(ocpm5.get("effectiveDateEnd"), todayEnd);
			Predicate a8 = cb.equal(ocpm5.get("loginId"), b.get("loginId"));
			effectiveDate5.where(a4,a5,a6,a8);
			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("productId")));
			
			Predicate n1 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
			Predicate n2 = cb.equal(b.get("effectiveDateEnd"), effectiveDate5);
			Predicate n3 = cb.equal(b.get("status"), "Y");
			Predicate n4 = cb.equal(b.get("companyId"),insuranceId);
			Predicate n5 = cb.equal(b.get("loginId"),"99999" );
			Predicate n6 = cb.equal(b.get("productId"),productId );
			query.where(n1,n2,n3,n4,n5,n6).orderBy(orderList);
	
			// Get Result
			TypedQuery<BrokerCommissionDetails> result = em.createQuery(query);
			brokerComlist = result.getResultList();
			
	
		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
			return null;
	
		}
		return brokerComlist;
	}
	
	public List<PolicyTypeMaster> getPolicyTypeList(String insuranceId , String productId) {
		List<PolicyTypeMaster> policyTypes = new ArrayList<PolicyTypeMaster>();
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
			Predicate a1 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.lessThanOrEqualTo(b.get("effectiveDateStart"),today);
			Predicate a7 = cb.equal(ocpm1.get("policyTypeId"), b.get("policyTypeId"));
			effectiveDate.where(a1,a2,a3,a7);
	
			// Effective Date End
			Subquery<Date> effectiveDate5 = query.subquery(Date.class);
			Root<PolicyTypeMaster> ocpm5 = effectiveDate5.from(PolicyTypeMaster.class);
			effectiveDate5.select(cb.greatest(ocpm5.get("effectiveDateEnd").as(Date.class)));
			Predicate a4 = cb.equal(b.get("productId"),ocpm5.get("productId") );
			Predicate a5 = cb.equal(ocpm5.get("companyId"), b.get("companyId"));
			Predicate a6 = cb.greaterThanOrEqualTo(ocpm5.get("effectiveDateEnd"), todayEnd);
			Predicate a8 = cb.equal(ocpm5.get("policyTypeId"), b.get("policyTypeId"));
			effectiveDate5.where(a4,a5,a6,a8);
			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("productId")));
			
			Predicate n1 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
			Predicate n2 = cb.equal(b.get("effectiveDateEnd"), effectiveDate5);
			Predicate n3 = cb.equal(b.get("status"), "Y");
			Predicate n4 = cb.equal(b.get("companyId"), insuranceId);
			Predicate n5 = cb.equal(b.get("productId"),productId );
			query.where(n1,n2,n3,n4,n5).orderBy(orderList);
	
			// Get Result
			TypedQuery<PolicyTypeMaster> result = em.createQuery(query);
			policyTypes = result.getResultList();
			
	
		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
			return null;
	
		}
		return policyTypes;
	}
}