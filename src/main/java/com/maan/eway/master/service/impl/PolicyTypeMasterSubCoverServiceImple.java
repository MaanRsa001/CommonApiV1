package com.maan.eway.master.service.impl;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maan.eway.bean.TravelPolicyType;
import com.maan.eway.error.Error;
import com.maan.eway.master.req.GetPolicyTypesubcoverReq;
import com.maan.eway.master.req.PolicyTypeMasterSubCoverSaveReq;
import com.maan.eway.master.req.PolicyTypeSubCoverMasterGetAllReq;
import com.maan.eway.master.res.PolicyTypeSubCoverMasterGetRes;
import com.maan.eway.master.service.PolicyTypeMasterSubCoverService;
import com.maan.eway.repository.TravelPolicyTypeRepository;
import com.maan.eway.res.SuccessRes;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.CriteriaUpdate;
import jakarta.persistence.criteria.Order;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

@Transactional
@Service 
public class PolicyTypeMasterSubCoverServiceImple  implements PolicyTypeMasterSubCoverService{
	
	@PersistenceContext
	private EntityManager em;
	private Logger log=LogManager.getLogger(PolicyTypeMasterSubCoverServiceImple.class);
	
	@Autowired
	private TravelPolicyTypeRepository repository;

	@Override
	public List<Error> validatePolicyTypeSubCover(PolicyTypeMasterSubCoverSaveReq req) {
		List<Error> errorList = new ArrayList<Error>();
		try {
			
			// Date Validation 
			Calendar cal = new GregorianCalendar();
			Date today = new Date();
			cal.setTime(today);cal.add(Calendar.DAY_OF_MONTH, -1);cal.set(Calendar.HOUR_OF_DAY, 23);cal.set(Calendar.MINUTE, 50);
			today = cal.getTime();
			if (req.getEffectiveDateStart() == null ) {
				errorList.add(new Error("04", "EffectiveDateStart", "Please Select Effective Date"));
	
			} else if (req.getEffectiveDateStart().before(today)) {
				errorList.add(new Error("04", "EffectiveDateStart", "Please Select Effective Date as Future Date"));
			} 
			
			if (StringUtils.isBlank(req.getCreatedBy())) {
				errorList.add(new Error("06", "CreatedBy", "Please Enter CreatedBy"));
			}else if (req.getCreatedBy().length() > 50) {
				errorList.add(new Error("06", "CreatedBy", "Please Enter CreatedBy within 100 Characters"));
			} 
						
			// Other Errors	
			if (StringUtils.isBlank(req.getCompanyId())) {
				errorList.add(new Error("01", "Insurance Id", "Please Select InsuranceId"));
				
			}
			if (StringUtils.isBlank(req.getBranchCode())) {
				errorList.add(new Error("01", "BranchCode", "Please Select BranchCode"));
				
			}
			if (StringUtils.isBlank(req.getProductId())) {
				errorList.add(new Error("02", "ProductId", "Please Enter ProductId"));
			}
			if (StringUtils.isBlank(req.getPolicyTypeId())) {
				errorList.add(new Error("02", "PolicyTypeId", "Please Select PolicyTypeId"));
			}
			if (StringUtils.isBlank(req.getPlanTypeId())) {
				errorList.add(new Error("02", "PlanTypeId", "Please Select PlanTypeId"));
			}
			
//			if (StringUtils.isBlank(req.getRemarks()) ) {
//				errorList.add(new Error("03", "Remark", "Please Enter Remark "));
//			}else if (req.getRemarks().length() > 100){
//				errorList.add(new Error("03","Remark", "Please Enter Remark within 100 Characters")); 
//			}
			
			if (StringUtils.isBlank(req.getCoverId())) {
				errorList.add(new Error("02", "CoverId", "Please Select CoverId"));
			}
			if (StringUtils.isBlank(req.getCoverStatus())) {
				errorList.add(new Error("05", "Cover Status", "Please Select Cover Status"));
			} else if (req.getCoverStatus().length() > 1) {
				errorList.add(new Error("05", "Cover Status", "Please Select Valid Cover Status - 1 Character Only Allwed"));
			}
			
			if (StringUtils.isBlank(req.getCoverDesc())) {
				errorList.add(new Error("02", "CoverDesc", "Please Enter CoverDesc in row "));
			} 
			if (StringUtils.isBlank(req.getPolicyTypeDesc())) {
				errorList.add(new Error("02", "PolicyTypeDesc", "Please Enter PolicyTypeDesc in row "));
			} 
			if (StringUtils.isBlank(req.getPlanTypeDesc())) {
				errorList.add(new Error("02", "PlanTypeDesc", "Please Enter PlanTypeDesc in row "));
			} 
			
			
			Set<String> uniqueElements = new HashSet<>();
			int row = 0 ;
			
			if(req.getTravelSubCover().size()>0) {
				
				for(PolicyTypeSubCoverMasterGetRes sub : req.getTravelSubCover()) {
							row = row + 1; 
							
							if (StringUtils.isBlank(sub.getCurrency())) {
								errorList.add(new Error("02", "Currency", "Please Enter Currency in row "+ row));
							}
							if (StringUtils.isBlank(sub.getExcessAmt())) {
								errorList.add(new Error("02", "ExcessAmt", "Please Enter ExcessAmt in row "+ row));
							}
							if (StringUtils.isBlank(sub.getSubCoverDesc())) {
								errorList.add(new Error("02", "SubCoverDesc", "Please Enter SubCoverDesc in row "+ row));
							} else if (! uniqueElements.add(sub.getSubCoverDesc())) {
									errorList.add(new Error("02", "SubCoverDesc", "SubCover '" + sub.getSubCoverDesc() + "' Already Exists in row "+ row));
							}
							
							if (StringUtils.isBlank(sub.getSumInsured())) {
								errorList.add(new Error("02", "SumInsured", "Please Enter SumInsured in row "+ row));
							}
							if (StringUtils.isBlank(sub.getStatus())) {
								errorList.add(new Error("05", "SubCover Status", "Please Select SubCover Status"));
							} else if (sub.getStatus().length() > 1) {
								errorList.add(new Error("05", "SubCover Status", "Please Select Valid SubCover Status - 1 Character Only Allwed"));
							}
							
						}
					} else {
						errorList.add(new Error("05", "SubCover", "Please Enter SubCover Details"));
			}
				
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		return errorList;
					 
		
	
	}

	@Override
	public SuccessRes insertPolicyTypeSubCover(PolicyTypeMasterSubCoverSaveReq req) {
		SuccessRes res = new SuccessRes();
		try {
			// Update Old Records
			Integer amendId = upadateOldSubCovers(req ) ; //effectivedate end
			Integer sNo = 0 ;
		
			// Insert New Records
			res = insertNewSubCovers(req , amendId , sNo);
		
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return res;
	
	}

	
	private SuccessRes insertNewSubCovers(PolicyTypeMasterSubCoverSaveReq req, Integer amendId, Integer sNo) {
		SimpleDateFormat sdformat = new SimpleDateFormat("dd/MM/YYYY");
		SuccessRes res = new SuccessRes();
		DozerBeanMapper dozerMapper = new  DozerBeanMapper();
		List<TravelPolicyType> saveList = new ArrayList<TravelPolicyType>();
		try {
			String end = "31/12/2050";
			Date endDate = sdformat.parse(end);
			String coverid = req.getCoverId();
		
			res.setResponse("Updated Successfully");
			res.setSuccessId(coverid);
		
			
			for ( PolicyTypeSubCoverMasterGetRes data :  req.getTravelSubCover() ) {
				TravelPolicyType saveData = new TravelPolicyType();
				// Save New Records
				sNo = sNo + 1 ;
				saveData = dozerMapper.map(req, TravelPolicyType.class );
				
				saveData.setEffectiveStartDate(req.getEffectiveDateStart());
				saveData.setEffectiveEndDate(endDate);
				saveData.setEntryDate(new Date());
				saveData.setAmendId(amendId);
				saveData.setSubCoverId(sNo);
				saveData.setSubCoverDesc(data.getSubCoverDesc());
				saveData.setSumInsured(data.getSumInsured());
				saveData.setExcessAmt(data.getExcessAmt());
				saveData.setStatus(data.getStatus());
				saveData.setCurrency(data.getCurrency());
				saveData.setUpdatedBy(req.getCreatedBy());
				saveData.setUpdatedDate(new Date());
				
				saveList.add(saveData);
			}
			repository.saveAllAndFlush(saveList);
			
		} catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is --->" + e.getMessage());
		return null;
	}
	return res;
	
	}

	private Integer upadateOldSubCovers(PolicyTypeMasterSubCoverSaveReq req) {
		List<TravelPolicyType> list = new ArrayList<TravelPolicyType>();
		Integer amendId = 0 ;
		try {
			long MILLIS_IN_A_DAY = 1000 * 60 * 60 * 24; //milliseconds in a day
			Date oldEndDate = new Date(req.getEffectiveDateStart().getTime() - MILLIS_IN_A_DAY);
			Date entryDate = new Date();
			
			// FInd Old Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<TravelPolicyType> query = cb.createQuery(TravelPolicyType.class);
		
			Root<TravelPolicyType> b = query.from(TravelPolicyType.class);
		
			query.select(b);
			
			// Max AmendId
			Subquery<Long> maxAmendId = query.subquery(Long.class);
			Root<TravelPolicyType> ocpm1 = maxAmendId.from(TravelPolicyType.class);
			maxAmendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("policyTypeId"),b.get("policyTypeId"));
			Predicate a4 = cb.equal(ocpm1.get("planTypeId"),b.get("planTypeId"));
			Predicate a5 = cb.equal(ocpm1.get("coverId"),b.get("coverId"));
			Predicate a7 = cb.equal(ocpm1.get("branchCode"), b.get("branchCode"));
			maxAmendId.where(a1,a2,a3,a4,a5,a7);

		
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(b.get("amendId")));
			
			
			Predicate n1 = cb.equal(b.get("companyId"), req.getCompanyId());
			Predicate n2 = cb.equal(b.get("productId"), req.getProductId());
			Predicate n3 = cb.equal(b.get("policyTypeId"), req.getPolicyTypeId());
			Predicate n4 = cb.equal(b.get("coverId"), req.getCoverId());
			Predicate n5 = cb.equal(b.get("planTypeId"), req.getPlanTypeId());
			Predicate n6 = cb.equal(b.get("branchCode"), req.getBranchCode());
			Predicate n7 = cb.notEqual(b.get("subCoverId"), "0" );
			Predicate n10 = cb.equal(b.get("amendId"), maxAmendId);
			
			query.where(n1,n2,n3,n4,n5,n6,n7,n10).orderBy(orderList);
		
			TypedQuery<TravelPolicyType> result = em.createQuery(query);
			list = result.getResultList();
			
			if(list.size()>0) {
				Date beforeOneDay = new Date(new Date().getTime() - MILLIS_IN_A_DAY);
			
				if ( list.get(0).getEffectiveStartDate().before(beforeOneDay)  ) { //old effstartdate past -- end date changed
					amendId = list.get(0).getAmendId() + 1 ;
					entryDate = new Date() ;
					
					CriteriaBuilder cb2 = em.getCriteriaBuilder();
				
					CriteriaUpdate<TravelPolicyType> update = cb2.createCriteriaUpdate(TravelPolicyType.class);
				
					Root<TravelPolicyType> m = update.from(TravelPolicyType.class);
				
					update.set("updatedBy", req.getCreatedBy());
					update.set("updatedDate", entryDate);
					update.set("effectiveEnddate", oldEndDate);
					
					n1 = cb.equal(m.get("companyId"), req.getCompanyId());
					n2 = cb.equal(m.get("productId"), req.getProductId());
					n3 = cb.equal(m.get("branchCode"), req.getBranchCode());
					n4 = cb.equal(m.get("policyTypeId"), req.getPolicyTypeId());
					n5 = cb.equal(m.get("planTypeId"), req.getPlanTypeId());
					n6 = cb.equal(m.get("coverId"), req.getCoverId());
					n7 = cb.notEqual(m.get("subCoverId"), "0" );
					n10 = cb.equal(m.get("amendId"), list.get(0).getAmendId());
					update.where(n1,n2,n3,n4,n5,n6,n7,n10);
			
					em.createQuery(update).executeUpdate();
					
				} else { 	// old effstartdate today or future-- delete
					
					amendId = list.get(0).getAmendId() ;
					repository.deleteAll(list);
			    }
			}
			
		} catch (Exception e) {
		e.printStackTrace();
		log.info("Exception is --->" + e.getMessage());
		return null;
	}
	return amendId;
	
	}

	@Override
	public List<PolicyTypeSubCoverMasterGetRes> getallPolicyTypesubcover(PolicyTypeSubCoverMasterGetAllReq req) {
		List<PolicyTypeSubCoverMasterGetRes> resList = new ArrayList<PolicyTypeSubCoverMasterGetRes>();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			today = cal.getTime();  //beginning of the date
			
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			cal.set(Calendar.MILLISECOND, 999);
			Date todayEnd = cal.getTime(); //today end
			
			List<TravelPolicyType> list = new ArrayList<TravelPolicyType>();
		
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<TravelPolicyType> query = cb.createQuery(TravelPolicyType.class);

			// Find All
			Root<TravelPolicyType> b = query.from(TravelPolicyType.class);

			// Select
			query.select(b);

			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<TravelPolicyType> ocpm1 = amendId.from(TravelPolicyType.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("policyTypeId"),b.get("policyTypeId"));
			Predicate a4 = cb.equal(ocpm1.get("planTypeId"),b.get("planTypeId"));
			Predicate a5 = cb.equal(ocpm1.get("coverId"),b.get("coverId"));
			Predicate a7 = cb.equal(ocpm1.get("branchCode"), b.get("branchCode"));
			Predicate a9 = cb.equal(ocpm1.get("coverId"),  b.get("coverId"));
	
			amendId.where(a1,a2,a3,a4,a5,a7,a9);

			Predicate n1 = cb.equal(b.get("amendId"),amendId);
			Predicate n2 = cb.equal(b.get("productId"), req.getProductId() );	
			Predicate n3 = cb.equal(b.get("companyId"), req.getCompanyId() );		
			Predicate n4 = cb.equal(b.get("branchCode"), req.getBranchCode());
			Predicate n5 = cb.equal(b.get("coverId"), req.getCoverId());
			Predicate n6 =  cb.equal(b.get("policyTypeId"), req.getPolicyTypeId()); 
			Predicate n7 =  cb.equal(b.get("planTypeId"), req.getPlanTypeId()); 
			Predicate n8 = cb.lessThanOrEqualTo(b.get("effectiveStartDate"), today);
			Predicate n9 = cb.greaterThanOrEqualTo(b.get("effectiveEndDate"), todayEnd);
			
		
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("subCoverId")));
			query.where(n1, n2,n3,n4, n5, n6, n7, n8, n9).orderBy(orderList);
			
			TypedQuery<TravelPolicyType> result = em.createQuery(query);
			list = result.getResultList();
			
			if(list.size()>0) {
				
				for(TravelPolicyType data : list) {
					PolicyTypeSubCoverMasterGetRes res = new PolicyTypeSubCoverMasterGetRes();
					res = dozerMapper.map(data,  PolicyTypeSubCoverMasterGetRes.class);
					resList.add(res);
					
				}
			}
		
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
			return null;
	
		}
		return resList;
	}

	@Override
	public PolicyTypeSubCoverMasterGetRes getPolicyTypesubcover(GetPolicyTypesubcoverReq req) {
		PolicyTypeSubCoverMasterGetRes res = new PolicyTypeSubCoverMasterGetRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper();
		try {
			Date today = new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 0);
			cal.set(Calendar.MINUTE, 0);
			cal.set(Calendar.SECOND, 0);
			cal.set(Calendar.MILLISECOND, 0);
			today = cal.getTime();  //beginning of the date
			
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 59);
			cal.set(Calendar.SECOND, 59);
			cal.set(Calendar.MILLISECOND, 999);
			Date todayEnd = cal.getTime(); //today end
			
			List<TravelPolicyType> list = new ArrayList<TravelPolicyType>();
		
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<TravelPolicyType> query = cb.createQuery(TravelPolicyType.class);

			// Find All
			Root<TravelPolicyType> b = query.from(TravelPolicyType.class);

			// Select
			query.select(b);

			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<TravelPolicyType> ocpm1 = amendId.from(TravelPolicyType.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			Predicate a2 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a3 = cb.equal(ocpm1.get("policyTypeId"),b.get("policyTypeId"));
			Predicate a4 = cb.equal(ocpm1.get("planTypeId"),b.get("planTypeId"));
			Predicate a5 = cb.equal(ocpm1.get("coverId"),b.get("coverId"));
			Predicate a7 = cb.equal(ocpm1.get("branchCode"), b.get("branchCode"));
			Predicate a9 = cb.equal(ocpm1.get("coverId"),  b.get("coverId"));
			Predicate a10 = cb.equal(ocpm1.get("subCoverId"),  b.get("subCoverId"));
	
			amendId.where(a1,a2,a3,a4,a5,a7,a9,a10);

			Predicate n1 = cb.equal(b.get("amendId"),amendId);
			Predicate n2 = cb.equal(b.get("productId"), req.getProductId() );	
			Predicate n3 = cb.equal(b.get("companyId"), req.getCompanyId() );		
			Predicate n4 = cb.equal(b.get("branchCode"), req.getBranchCode());
			Predicate n5 = cb.equal(b.get("coverId"), req.getCoverId());
			Predicate n6 =  cb.equal(b.get("policyTypeId"), req.getPolicyTypeId()); 
			Predicate n7 =  cb.equal(b.get("planTypeId"), req.getPlanTypeId()); 
			Predicate n8 = cb.lessThanOrEqualTo(b.get("effectiveStartDate"), today);
			Predicate n9 = cb.greaterThanOrEqualTo(b.get("effectiveEndDate"), todayEnd);
			Predicate n10 = cb.equal(b.get("subCoverId"), req.getSubCoverId());
		
			query.where(n1, n2,n3,n4, n5, n6, n7, n8, n9, n10);
			
			TypedQuery<TravelPolicyType> result = em.createQuery(query);
			list = result.getResultList();

			if (list.size() > 0) 
				res = dozerMapper.map(list.get(0), PolicyTypeSubCoverMasterGetRes.class);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
		}
		return res;
	
	}

}
