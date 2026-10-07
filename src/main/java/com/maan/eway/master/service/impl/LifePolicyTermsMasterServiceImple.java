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
import org.springframework.transaction.annotation.Transactional;

import com.google.gson.Gson;
import com.maan.eway.bean.LifePolicytermsMaster;
import com.maan.eway.error.Error;
import com.maan.eway.master.req.GetPolicyTermsDetailsReq;
import com.maan.eway.master.req.GetallPolicyTermsDetailsReq;
import com.maan.eway.master.req.InsertPolicyTermsReq;
import com.maan.eway.master.res.GetallPolicyTermsDetailsRes;
import com.maan.eway.master.service.LifePolicyTermsMasterService;
import com.maan.eway.notification.repository.LifePolicytermsMasterRepository;
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
public class LifePolicyTermsMasterServiceImple implements LifePolicyTermsMasterService{
	private Logger log=LogManager.getLogger(BankMasterServiceImpl.class);
	Gson json = new Gson();
	SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
	DozerBeanMapper mapper = new DozerBeanMapper();
	
	@PersistenceContext
	private EntityManager em;
	
	@Autowired
	private LifePolicytermsMasterRepository repo; 
	
	

	@Override
	public List<Error> validatePolicyTerms(InsertPolicyTermsReq req) {
		List<Error> errorList = new ArrayList<Error>();

		try {
			
			if (StringUtils.isBlank(req.getCompanyId())) {
				errorList.add(new Error("02", "CompanyId", "Please Enter CompanyId"));
			}
			if (StringUtils.isBlank(req.getProductId())) {
				errorList.add(new Error("02", "ProductId", "Please Enter ProductId"));
			}
			if (StringUtils.isBlank(req.getSectionId())) {
				errorList.add(new Error("02", "SectionId", "Please Enter SectionId"));
			}
			
			// Date Validation 
			Calendar cal = new GregorianCalendar();
			Date today = new Date();
			cal.setTime(today);cal.add(Calendar.DAY_OF_MONTH, -1);
			today = cal.getTime();
			if (req.getEffectiveDateStart() == null || StringUtils.isBlank(req.getEffectiveDateStart().toString())) {
				errorList.add(new Error("05", "EffectiveDateStart", "Please Enter Effective Date Start"));

			} else if (req.getEffectiveDateStart().before(today)) {
				errorList.add(new Error("05", "EffectiveDateStart", "Please Enter Effective Date Start as Future Date"));
			}
			
			//Status Validation
			if (StringUtils.isBlank(req.getStatus())) {
				errorList.add(new Error("05", "Status", "Please Select Status"));
			} else if (req.getStatus().length() > 1) {
				errorList.add(new Error("05", "Status", "Please Select Valid Status - One Character Only Allwed"));
			}else if(!("Y".equalsIgnoreCase(req.getStatus())||"N".equalsIgnoreCase(req.getStatus())||"R".equalsIgnoreCase(req.getStatus())|| "P".equalsIgnoreCase(req.getStatus()))) {
				errorList.add(new Error("05", "Status", "Please Select Valid Status - Active or Deactive or Pending or Referral "));
			}
			
			if (req.getPolicyTerm()==null ) {
				errorList.add(new Error("07", "PolicyTerm", "Please Enter Policy Term"));
			}else if (req.getPolicyTerm() <= 0){
				errorList.add(new Error("07","PolicyTerm", "Please Enter Policy Term Greater Than Zero")); 
			}
			else if (req.getPolicyTerm() > 100){
				errorList.add(new Error("07","PolicyTerm", "Please Enter Policy Term within 100")); 
				
			} 
			
			else if ( req.getType().equalsIgnoreCase("N")  &&  StringUtils.isNotBlank(req.getCompanyId()) && StringUtils.isNotBlank(req.getProductId()) && StringUtils.isNotBlank(req.getSectionId())) {
				List<LifePolicytermsMaster> old = repo.findByPolicyTermsAndProductIdAndSectionIdAndCompanyId(
						Integer.valueOf(req.getPolicyTerm()), Integer.valueOf(req.getProductId()),Integer.valueOf(req.getSectionId()),
						req.getCompanyId());
				if (old.size()>0 ) {
					errorList.add(new Error("01", "Policy Term", "This Policy Term Already Exist"));
				}
			
			}	
			if (StringUtils.isBlank(req.getPolicyTermDesc())) {
				errorList.add(new Error("08", "PolicyTermDesc", "Please Enter Policy Term Desc"));
			}else if (req.getPolicyTermDesc().length() > 100){
				errorList.add(new Error("08","PolicyTermDesc", "Please Enter Policy Term Desc within 100 Characters")); 
			}
			
			
			if(StringUtils.isNotBlank(req.getStatus()) && ! (req.getStatus().equalsIgnoreCase("P")) ) {
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
				
				if (StringUtils.isNotBlank(req.getRemarks()) && req.getRemarks().length() > 100){
					errorList.add(new Error("03","Remark", "Please Enter Remark within 100 Characters")); 
				}
			
			}
			
			if (StringUtils.isBlank(req.getCreatedBy())) {
				errorList.add(new Error("09", "CreatedBy", "Please Enter CreatedBy"));
			}else if (req.getCreatedBy().length() > 100){
				errorList.add(new Error("09","CreatedBy", "Please Enter CreatedBy within 100 Characters")); 
			}	
			
			
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		}
		return errorList;
	}

	@Override
	public SuccessRes insertPolicyTerms(InsertPolicyTermsReq req) {
	
		SuccessRes res = new SuccessRes();
		LifePolicytermsMaster saveData = new LifePolicytermsMaster();
		List<LifePolicytermsMaster> list  = new ArrayList<LifePolicytermsMaster>();
		
		try {
			Integer amendId = 0;
			Date StartDate = req.getEffectiveDateStart();
			String end = "31/12/2050";
			Date endDate = sdf.parse(end);
			long MILLS_IN_A_DAY = 1000*60*60*24;
			Date oldEndDate = new Date(req.getEffectiveDateStart().getTime()- MILLS_IN_A_DAY);
			Date entryDate = null;
			String createdBy ="";
			
			List<LifePolicytermsMaster> old = repo.findByPolicyTermsAndProductIdAndSectionIdAndCompanyId(
					Integer.valueOf(req.getPolicyTerm()), Integer.valueOf(req.getProductId()),Integer.valueOf(req.getSectionId()),
					req.getCompanyId());
			
			if(old.size()<=0 ) {   //Insert
				
				entryDate = new Date();
				createdBy = req.getCreatedBy();
				res.setResponse("Saved Successfully");
				
			}
			else {  //update
				
				CriteriaBuilder cb = em.getCriteriaBuilder();
				CriteriaQuery<LifePolicytermsMaster> query = cb.createQuery(LifePolicytermsMaster.class);
			
				Root<LifePolicytermsMaster> b = query.from(LifePolicytermsMaster.class);
				
				query.select(b);
			
				List<Order> orderList = new ArrayList<Order>();
			    orderList.add(cb.desc(b.get("amendId")));
				
				Predicate n1 = cb.equal(b.get("policyTerms"),req.getPolicyTerm());
				Predicate n2 = cb.equal(b.get("companyId"),req.getCompanyId());
				Predicate n3 = cb.equal(b.get("productId"),req.getProductId());
				Predicate n4 = cb.equal(b.get("sectionId"),req.getSectionId());
			
				query.where(n1,n2,n3,n4).orderBy(orderList);
				
			
				TypedQuery<LifePolicytermsMaster> result = em.createQuery(query);
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
						LifePolicytermsMaster lastRecord = list.get(0);
						lastRecord.setEffectiveDateEnd(oldEndDate);
						repo.saveAndFlush(lastRecord);
					}
					else {
						amendId = list.get(0).getAmendId();
						entryDate = list.get(0).getEntryDate();
						createdBy = list.get(0).getCreatedBy();
					
						if(list.size()>1) {
							LifePolicytermsMaster lastRecord = list.get(1);	
							lastRecord.setEffectiveDateEnd(oldEndDate);
							repo.saveAndFlush(lastRecord);
						}
					}
				}
				res.setResponse("Updated Successfully");
				
			}
			
			
			mapper.map(req, saveData);
			
			saveData.setPolicyTerms(Integer.valueOf(req.getPolicyTerm()));
			saveData.setPolicyTermsDesc(req.getPolicyTermDesc());
			saveData.setEffectiveDateStart(StartDate);
			saveData.setEffectiveDateEnd(endDate);
			saveData.setCreatedBy(createdBy);
			saveData.setEntryDate(entryDate);
			saveData.setUpdatedBy(req.getCreatedBy());
			saveData.setUpdatedDate(new Date());
			saveData.setAmendId(amendId);
			
			repo.saveAndFlush(saveData);
			res.setSuccessId(req.getPolicyTerm().toString());
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
	public List<GetallPolicyTermsDetailsRes> getallPolicyTermsDetails(GetallPolicyTermsDetailsReq req) {
		List<GetallPolicyTermsDetailsRes> resList = new ArrayList<GetallPolicyTermsDetailsRes>();
		List<LifePolicytermsMaster> list = new ArrayList<LifePolicytermsMaster>();
		try {
			
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<LifePolicytermsMaster> query = cb.createQuery(LifePolicytermsMaster.class);
		
			Root<LifePolicytermsMaster> b = query.from(LifePolicytermsMaster.class);

			query.select(b);
			
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<LifePolicytermsMaster> ocpm1 = amendId.from(LifePolicytermsMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("policyTerms"), b.get("policyTerms"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			Predicate a4 = cb.equal(ocpm1.get("sectionId"), b.get("sectionId"));

			amendId.where(a1,a2,a3,a4);
			
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("policyTerms")));

			List<Predicate> predics = new ArrayList<Predicate>();
			predics.add(cb.equal(b.get("amendId"), amendId));
			predics.add(cb.equal(b.get("companyId"), req.getCompanyId()));
			predics.add(cb.equal(b.get("productId"), req.getProductId()));
			predics.add(cb.equal(b.get("sectionId"), req.getSectionId()));
			
			query.where(predics.toArray(new Predicate[0])).orderBy(orderList);
			
			TypedQuery<LifePolicytermsMaster> result = em.createQuery(query);
			list = result.getResultList();
			
			for (LifePolicytermsMaster data : list) {
				GetallPolicyTermsDetailsRes res = new GetallPolicyTermsDetailsRes();
				res = mapper.map(data, GetallPolicyTermsDetailsRes.class);
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
	public GetallPolicyTermsDetailsRes getPolicyTermsDetails(GetPolicyTermsDetailsReq req) {
		GetallPolicyTermsDetailsRes res = new GetallPolicyTermsDetailsRes();
		DozerBeanMapper mapper = new DozerBeanMapper();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today = cal.getTime();

			List<LifePolicytermsMaster> list = new ArrayList<LifePolicytermsMaster>();
		
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<LifePolicytermsMaster> query = cb.createQuery(LifePolicytermsMaster.class);

			// Find All
			Root<LifePolicytermsMaster> b = query.from(LifePolicytermsMaster.class);

			// Select
			query.select(b);

			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<LifePolicytermsMaster> ocpm1 = amendId.from(LifePolicytermsMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("policyTerms"), b.get("policyTerms"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			Predicate a4 = cb.equal(ocpm1.get("sectionId"), b.get("sectionId"));
			amendId.where(a1,a2,a3,a4);


			List<Predicate> predics = new ArrayList<Predicate>();
			predics.add(cb.equal(b.get("amendId"), amendId));
			predics.add(cb.equal(b.get("companyId"), req.getCompanyId()));
			predics.add(cb.equal(b.get("productId"), req.getProductId()));
			predics.add(cb.equal(b.get("sectionId"), req.getSectionId()));
			predics.add(cb.equal(b.get("policyTerms"), req.getPolicyTerm()));
			
			query.where(predics.toArray(new Predicate[0]));
			
			// Get Result
			TypedQuery<LifePolicytermsMaster> result = em.createQuery(query);
			list = result.getResultList();
		
			res = mapper.map(list.get(0), GetallPolicyTermsDetailsRes.class);
		
			
			} catch (Exception e) {
				e.printStackTrace();
				log.info("Exception is ---> " + e.getMessage());
				return null;
			}
		return res;
	}

	@Override
	public List<DropDownRes> getPolicyTermsMasterDropdown(GetallPolicyTermsDetailsReq req) { // only Active
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
		
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<LifePolicytermsMaster> query=  cb.createQuery(LifePolicytermsMaster.class);
			List<LifePolicytermsMaster> list = new ArrayList<LifePolicytermsMaster>();
		
			Root<LifePolicytermsMaster> c = query.from(LifePolicytermsMaster.class);
			
			query.select(c);
			
			// Effective Date Start Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<LifePolicytermsMaster> ocpm1 = effectiveDate.from(LifePolicytermsMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 =  cb.equal(ocpm1.get("policyTerms"), c.get("policyTerms"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a5 = cb.equal(c.get("companyId"),ocpm1.get("companyId"));
			Predicate a6 = cb.equal(c.get("sectionId"),ocpm1.get("sectionId"));
			Predicate a10 = cb.equal(c.get("productId"),ocpm1.get("productId"));
			effectiveDate.where(a1,a2,a5,a6,a10);
			// Effective Date End Max Filter
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<LifePolicytermsMaster> ocpm2 = effectiveDate2.from(LifePolicytermsMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate a3 = cb.equal(ocpm2.get("policyTerms"), c.get("policyTerms"));
			Predicate a4 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			Predicate a7 = cb.equal(c.get("companyId"),ocpm2.get("companyId"));
			Predicate a8 = cb.equal(c.get("productId"),ocpm2.get("productId"));
			Predicate a9 = cb.equal(c.get("sectionId"),ocpm2.get("sectionId"));
			effectiveDate2.where(a3,a4,a7,a8,a9);
			
			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(c.get("policyTerms")));
			
			List<Predicate> predics = new ArrayList<Predicate>();
			predics.add(cb.or(cb.equal(c.get("status"),"Y"), cb.equal(c.get("status"),"R")));
			predics.add(cb.equal(c.get("effectiveDateStart"),effectiveDate));
			predics.add(cb.equal(c.get("effectiveDateEnd"),effectiveDate2));
			predics.add(cb.equal(c.get("companyId"), req.getCompanyId()));
			predics.add(cb.equal(c.get("productId"), req.getProductId()));
			predics.add(cb.equal(c.get("sectionId"), req.getSectionId()));
			query.where(predics.toArray(new Predicate[0]));
			
			TypedQuery<LifePolicytermsMaster> result = em.createQuery(query);
			list = result.getResultList(); 
		
			
			for(LifePolicytermsMaster data : list ) {
				// Response
				DropDownRes res = new DropDownRes();
				res.setCode(data.getPolicyTerms().toString());
				res.setCodeDesc(data.getPolicyTermsDesc());
				res.setCodeDescLocal(data.getPolicyTermsDescLocal());
				res.setStatus(data.getStatus());
				resList.add(res);
			}		
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is ---> " + e.getMessage());
			return null;
		}
		return resList;
	}

}
