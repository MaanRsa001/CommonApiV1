package com.maan.eway.common.service.impl;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.maan.eway.bean.ClaimIntimation;
import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.MotorDataDetails;
import com.maan.eway.common.req.ClaimIntimationGetAllREq;
import com.maan.eway.common.req.ClaimIntimationGetReq;
import com.maan.eway.common.req.ClaimIntimationReq;
import com.maan.eway.common.req.ClaimIntimationUpdateReq;
import com.maan.eway.common.res.ProductRes;
import com.maan.eway.common.res.SuccessRes;
import com.maan.eway.common.service.ClaimIntimationService;
import com.maan.eway.repository.ClaimIntimationRepository;
import com.maan.eway.repository.CompanyProductMasterRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

@Service
public class ClaimIntimationServiceImpl implements ClaimIntimationService{
    @Autowired
    ClaimIntimationRepository claimIntimationRepo;
    
    @Autowired
	private CompanyProductMasterRepository productRepo;
	
	@PersistenceContext
	private EntityManager em;

	Gson json = new Gson();

	private Logger log = LogManager.getLogger(ClaimIntimationServiceImpl.class);
	
	//************************************************INSERT CLAIM INTIMATION DETAILS******************************************************\\
		@Override
		public List<String> validateClaimIntimation(ClaimIntimationReq req) {
			List<String> errorList = new ArrayList<String>();
			try {
				Calendar cal = new GregorianCalendar();
				Date today = new Date();
				cal.setTime(new Date() );
				Date inceptionDate=null;
				Date expiryDate=null;
				if(StringUtils.isBlank(req.getCompanyId())) {
					errorList.add("1255");
				}else if(StringUtils.isBlank(req.getProductId())) {
					errorList.add("1313");
				}else {
					List<HomePositionMaster> hpm=new ArrayList<>();
					
					CriteriaBuilder cb = em.getCriteriaBuilder();
			        CriteriaQuery<HomePositionMaster> query = cb.createQuery(HomePositionMaster.class);
			        Root<HomePositionMaster> root = query.from(HomePositionMaster.class);

			        Predicate n1 = cb.equal(root.get("companyId"), req.getCompanyId());
			        Predicate n2 = cb.equal(root.get("productId"), req.getProductId());
			        Predicate n3 = cb.equal(root.get("policyNo"), req.getPolicyNumber());

			        query.select(root).where(cb.and(n1,n2,n3));

			        TypedQuery<HomePositionMaster> r = em.createQuery(query);
			        hpm=r.getResultList();
			        if(!(hpm.size()>0)) {
			        	errorList.add("2334");
			        }else {
			        	HomePositionMaster h=hpm.get(0);
			        	inceptionDate=h.getInceptionDate();
			        	expiryDate=h.getExpiryDate();
			        	if(h.getExpiryDate().before(today)) {
			        		errorList.add("2347");
			        	}else if(!h.getStatus().equalsIgnoreCase("P")) {
			        		errorList.add("2334");
			        	}else if(h.getInceptionDate().before(today)){
			        		
			        	}else {
			        		List<MotorDataDetails> mdd=new ArrayList<>();
			        		CriteriaBuilder c = em.getCriteriaBuilder();
			                CriteriaQuery<MotorDataDetails> q = c.createQuery(MotorDataDetails.class);
			                Root<MotorDataDetails> roo = q.from(MotorDataDetails.class);

			                Predicate n4 = cb.equal(root.get("registrationNumber"), req.getVehicleRegistrationNumber());
			                Predicate n5 = cb.equal(root.get("companyId"), req.getCompanyId());
			                Predicate n6 = cb.equal(root.get("productId"), req.getProductId());

			                query.select(root).where(cb.and(n4,n5,n6));

			                TypedQuery<MotorDataDetails> typedQuery = em.createQuery(q);
			                 mdd=typedQuery.getResultList();
			                 if(!(mdd.size()>0)) {
			                	errorList.add("2335");
			                 }
			        	}
			        	
			        }
				}
				 if (StringUtils.isBlank(req.getPlaceOfLoss())) {
			            errorList.add("2336");
			        }
				 if (StringUtils.isBlank(req.getNatureOfDamage())) {
			            errorList.add("2337");
			        }
				 if (req.getReserveAmount() <= 0) {
			            errorList.add("9");
			        }
				 if (StringUtils.isBlank(req.getCurrentLocationOfVehicle())) {
			            errorList.add("2338");
			        }
//				 if (req.getEffectiveStartDate() == null) {
//			            errorList.add("2034");
//			        } else if (!req.getEffectiveStartDate().isEqual(LocalDate.now())) {
//			            errorList.add("2035");
//			        }
				// Validate dateOfLoss - should be today or the previous day
			        if (req.getDateOfLoss() == null) {
			            errorList.add("2339");
			        } else if (!(req.getDateOfLoss().isEqual(LocalDate.now()) || req.getDateOfLoss().isBefore(LocalDate.now()))) {
			            errorList.add("2340");
			        }else if (inceptionDate != null && expiryDate != null) {System.out.println(inceptionDate);
			            LocalDate in = inceptionDate.toInstant()
			                    .atZone(ZoneId.systemDefault()).toLocalDate();System.out.println(in);
			                LocalDate ex = expiryDate.toInstant()
			                    .atZone(ZoneId.systemDefault()).toLocalDate();

			                // Compare LocalDate (req.getDateOfLoss()) with converted LocalDate
			                if (req.getDateOfLoss().isBefore(in) || req.getDateOfLoss().isAfter(ex)) {
			                    errorList.add("2341");
			                }
			        }
			        // Validate dateOfNotification - should be today or the previous day but not before dateOfLoss
			        if (req.getDateOfNotification() == null) {
			            errorList.add("2342");
			        } else if (req.getDateOfNotification().isAfter(LocalDate.now())) {
			            errorList.add("2343");
			        } else if (req.getDateOfNotification().isBefore(req.getDateOfLoss())) {
			            errorList.add("2344");
			        }

				
				
			} catch (Exception e) {
				log.error(e);
				e.printStackTrace();
			//	errorList.add(new Error("10", "Common Error", e.getMessage()));
			}
			return errorList;
		}
	@Override
	public SuccessRes insertClaimIntimation(ClaimIntimationReq req) {
		SimpleDateFormat sdformat = new SimpleDateFormat("dd/MM/yyyy");
		SuccessRes res = new SuccessRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper(); 
		try {
			LocalDate effectiveStartDate = LocalDate.now();
			LocalDate effectiveEndDate = LocalDate.of(2049, 12, 31);
			ClaimIntimation saveData=new ClaimIntimation();
			
			Long count=claimIntimationRepo.count();
			Integer intCount = count.intValue();  // Convert Long to Integer
			if(intCount==0) {
//				saveData.setClaimReferenceNo(intCount+1);
			}else {
//			saveData.setClaimReferenceNo(intCount+1);
			}
			//save details
//			 saveData.setEffectiveEndDate(effectiveEndDate);
//			saveData.setCompanyId(req.getCompanyId());
//			saveData.setProductId(req.getProductId());
//			saveData.setPolicyNumber(req.getPolicyNumber());
//			saveData.setAmendId(0);
//			saveData.setVehicleRegistrationNumber(req.getVehicleRegistrationNumber());
//			saveData.setDateOfLoss(req.getDateOfLoss());
//			saveData.setPlaceOfLoss(req.getPlaceOfLoss());
//			saveData.setDateOfNotification(req.getDateOfNotification());
//			saveData.setNatureOfDamage(req.getNatureOfDamage());
//			saveData.setReserveAmount(req.getReserveAmount());
//			saveData.setCurrentLocationOfVehicle(req.getCurrentLocationOfVehicle());
//			saveData.setStatus(req.getStatus());
//			saveData.setEffectiveStartDate(effectiveStartDate);
//			saveData.setEffectiveEndDate(effectiveEndDate);
//			saveData.setCreatedBy(req.getCreatedBy());
			claimIntimationRepo.saveAndFlush(saveData);
			
			res.setResponse("Insert Claim Intimation Details success");
//			res.setSuccessId(saveData.getClaimReferenceNo().toString());
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return res;
	}
	
	@Override
	public SuccessRes updateClaimIntimation(ClaimIntimationUpdateReq req) {
		SimpleDateFormat sdformat = new SimpleDateFormat("dd/MM/yyyy");
		SuccessRes res = new SuccessRes();
		DozerBeanMapper dozerMapper = new DozerBeanMapper(); 
		try {
			Calendar cal = new GregorianCalendar();
			 LocalDate today = LocalDate.now();
			// LocalDate yesterday = LocalDate.now().minusDays(1);
			cal.setTime(new Date() );
			ClaimIntimation cl=new ClaimIntimation();
			 CriteriaBuilder cb = em.getCriteriaBuilder();
		        CriteriaQuery<ClaimIntimation> query = cb.createQuery(ClaimIntimation.class);
		        Root<ClaimIntimation> root = query.from(ClaimIntimation.class);

		        // Fetch records matching the claimReferenceNo
		        query.select(root)
		             .where(cb.equal(root.get("claimReferenceNo"), req.getClaimReferenceNo()));

		        List<ClaimIntimation> resultList = em.createQuery(query).getResultList();

		        // Get the object with the maximum amendId
//		        cl= resultList.stream()
//		                         .max(Comparator.comparing(ClaimIntimation::getAmendId)).get();
//
//		      LocalDate effectiveStartDate= cl.getEffectiveStartDate();
//		      LocalDate oldeffectiveEndDate= cl.getEffectiveEndDate();
//		      effectiveStartDate=today;
//		      if(req.getEffectiveStartDate()!=null) {
//		    	  oldeffectiveEndDate=req.getEffectiveStartDate();
//		      }else {
//		    	  oldeffectiveEndDate=today;
//		      }
//
//		      LocalDate effectiveEndDate = LocalDate.of(2049, 12, 31);
//
//		      cl.setEffectiveEndDate(oldeffectiveEndDate);
//
//		      ClaimIntimation saveData=new ClaimIntimation();
//		      saveData.setAmendId(cl.getAmendId()+1);
//		      saveData.setClaimReferenceNo(cl.getClaimReferenceNo());
//		      saveData.setCompanyId(cl.getCompanyId());
//		      saveData.setProductId(cl.getProductId());
//		      saveData.setPolicyNumber(cl.getPolicyNumber());
//		      saveData.setVehicleRegistrationNumber(cl.getVehicleRegistrationNumber());
//		      saveData.setDateOfLoss(req.getDateOfLoss() != null ? req.getDateOfLoss() : cl.getDateOfLoss());
//		      saveData.setPlaceOfLoss(StringUtils.isBlank(req.getPlaceOfLoss()) ? cl.getPlaceOfLoss() : req.getPlaceOfLoss());
//		      saveData.setDateOfNotification(req.getDateOfNotification() != null ? req.getDateOfNotification() : cl.getDateOfNotification());
//		      saveData.setNatureOfDamage(StringUtils.isBlank(req.getNatureOfDamage()) ? cl.getNatureOfDamage() : req.getNatureOfDamage());
//		      saveData.setReserveAmount(req.getReserveAmount() != null ? req.getReserveAmount() : cl.getReserveAmount());
//		      saveData.setCurrentLocationOfVehicle(StringUtils.isBlank(req.getCurrentLocationOfVehicle()) ? cl.getCurrentLocationOfVehicle() : req.getCurrentLocationOfVehicle());
//		      saveData.setStatus(StringUtils.isBlank(req.getStatus()) ? cl.getStatus() : req.getStatus());
//		      saveData.setEffectiveStartDate(effectiveStartDate);
//		      saveData.setEffectiveEndDate(effectiveEndDate);
//		      saveData.setCreatedBy(cl.getCreatedBy());
//              claimIntimationRepo.saveAndFlush(saveData);
		    
              res.setResponse("Update Claim Intimation Details successfully");
//  			res.setSuccessId(saveData.getClaimReferenceNo().toString());
		}catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --->" + e.getMessage());
			return null;
		}
		return res;

	}
	@Override
	public List<String> validateUpdateClaimIntimation(ClaimIntimationUpdateReq req) {
		List<String> errorList = new ArrayList<String>();
		try {
			Calendar cal = new GregorianCalendar();
			Date today = new Date();
			cal.setTime(new Date() );
			Date inceptionDate=null;
			Date expiryDate=null;
			if(req.getClaimReferenceNo()==null) {
				errorList.add("2345");
			}else {
			 CriteriaBuilder cb = em.getCriteriaBuilder();
		        CriteriaQuery<Long> query = cb.createQuery(Long.class);
		        Root<ClaimIntimation> root = query.from(ClaimIntimation.class);

		        // Count the number of records where claimReferenceNo matches
		        query.select(cb.count(root))
		             .where(cb.equal(root.get("claimReferenceNo"), req.getClaimReferenceNo()));

		        Long count = em.createQuery(query).getSingleResult();

		        if(count<1) {
		        	errorList.add("2346");
		        }else {
		        	CriteriaQuery<ClaimIntimation> q = cb.createQuery(ClaimIntimation.class);
		        	Root<ClaimIntimation> r = q.from(ClaimIntimation.class);

		        	// Subquery to get the max amendId for the given claimReferenceNo
		        	Subquery<Integer> subquery = q.subquery(Integer.class);
		        	Root<ClaimIntimation> subRoot = subquery.from(ClaimIntimation.class);
		        	subquery.select(cb.max(subRoot.get("amendId")))
		        	        .where(cb.equal(subRoot.get("claimReferenceNo"), req.getClaimReferenceNo()));

		        	// Main query to get the ClaimIntimation with the max amendId
		        	q.select(r)
		        	     .where(cb.equal(r.get("claimReferenceNo"), req.getClaimReferenceNo()),
		        	            cb.equal(r.get("amendId"), subquery));

		        	ClaimIntimation ci = em.createQuery(q).getSingleResult();
		        	
		        	
		        	
					List<HomePositionMaster> hpm=new ArrayList<>();
			        CriteriaQuery<HomePositionMaster> que = cb.createQuery(HomePositionMaster.class);
			        Root<HomePositionMaster> roo = que.from(HomePositionMaster.class);

			        Predicate n1 = cb.equal(roo.get("companyId"), ci.getCompanyId());
			        Predicate n2 = cb.equal(roo.get("productId"), ci.getProductId());
//			        Predicate n3 = cb.equal(roo.get("policyNo"), ci.getPolicyNumber());

//			        que.select(roo).where(cb.and(n1,n2,n3));

			        TypedQuery<HomePositionMaster> tq = em.createQuery(que);
			        hpm=tq.getResultList();
			        if(!(hpm.size()>0)) {
			        	errorList.add("");
			        }else {
			        	HomePositionMaster h=hpm.get(0);
			        	inceptionDate=h.getInceptionDate();
			        	expiryDate=h.getExpiryDate();
			        	if(h.getExpiryDate().before(today)) {
			        		errorList.add("");
			        	}else if(!h.getStatus().equalsIgnoreCase("P")) {
			        		errorList.add("");
			        	}else if(h.getInceptionDate().before(today)){
			        		
			        	}else {
			        		List<MotorDataDetails> mdd=new ArrayList<>();
			        		CriteriaBuilder c = em.getCriteriaBuilder();
			                CriteriaQuery<MotorDataDetails> qu = c.createQuery(MotorDataDetails.class);
			                Root<MotorDataDetails> ro = qu.from(MotorDataDetails.class);

			                Predicate n4 = cb.equal(ro.get("registrationNumber"), req.getVehicleRegistrationNumber());
			                Predicate n5 = cb.equal(ro.get("companyId"), req.getCompanyId());
			                Predicate n6 = cb.equal(ro.get("productId"), req.getProductId());

			                qu.select(ro).where(cb.and(n4,n5,n6));

			                TypedQuery<MotorDataDetails> typedQuery = em.createQuery(qu);
			                 mdd=typedQuery.getResultList();
			                 if(!(mdd.size()>0)) {
			                	errorList.add("");
			                 }
			        	}
			        }
		        if (req.getDateOfLoss() != null) {
		        	if (!(req.getDateOfLoss().isEqual(LocalDate.now()) || req.getDateOfLoss().isBefore(LocalDate.now()))) {
			            errorList.add("2340");
			        }else if (inceptionDate != null && expiryDate != null) {System.out.println(inceptionDate);
		            LocalDate in = inceptionDate.toInstant()
		                    .atZone(ZoneId.systemDefault()).toLocalDate();System.out.println(in);
		                LocalDate ex = expiryDate.toInstant()
		                    .atZone(ZoneId.systemDefault()).toLocalDate();

		                // Compare LocalDate (req.getDateOfLoss()) with converted LocalDate
		                if (req.getDateOfLoss().isBefore(in) || req.getDateOfLoss().isAfter(ex)) {
		                    errorList.add("2341");
		                }
		        }
		        	// Validate dateOfNotification - should be today or the previous day but not before dateOfLoss
		        	if (req.getDateOfNotification() != null) {
			        	if (req.getDateOfNotification().isBefore(req.getDateOfLoss())) {
				            errorList.add("2344");
				        }else if (req.getDateOfNotification().isAfter(LocalDate.now())) {
				            errorList.add("2343");
				        }
			        } 
		           
		        }else {
		        	if (req.getDateOfNotification() != null) {
			        	if (req.getDateOfNotification().isBefore(req.getDateOfLoss())) {
				            errorList.add("2344");
				        }else if (req.getDateOfNotification().isAfter(LocalDate.now())) {
				            errorList.add("2343");
				        }
			        } 
		        }
		        
		        if (req.getEffectiveStartDate() == null) {
		            errorList.add("2034");
		        } else if (!req.getEffectiveStartDate().isEqual(LocalDate.now())) {
		            errorList.add("2035");
		        }
//		        else if(ci.getEffectiveStartDate()==LocalDate.now()) {
//		        	errorList.add("");
//		        }
		        
		        
		        
		      }
		        
			}
						
		} catch (Exception e) {
			log.error(e);
			e.printStackTrace();
		//	errorList.add(new Error("10", "Common Error", e.getMessage()));
		}
		return errorList;
	}
	@Override
	public List<ClaimIntimation> getallClaimIntimation(ClaimIntimationGetAllREq req) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
	    CriteriaQuery<ClaimIntimation> cq = cb.createQuery(ClaimIntimation.class);
	    Root<ClaimIntimation> root = cq.from(ClaimIntimation.class);

	    // Get today's date
	    LocalDate today = LocalDate.now();

	    // Subquery to get max amendId for each productId and companyId
	    Subquery<Integer> subquery = cq.subquery(Integer.class);
	    Root<ClaimIntimation> subRoot = subquery.from(ClaimIntimation.class);
	    subquery.select(cb.max(subRoot.get("amendId")))
	            .where(
	                cb.equal(subRoot.get("companyId"), root.get("companyId")),
	                cb.equal(subRoot.get("productId"), root.get("productId")),
	                cb.equal(subRoot.get("claimReferenceNo"), root.get("claimReferenceNo"))
	            );

	    // Main query conditions
	    Predicate productPredicate = cb.equal(root.get("productId"), req.getProductId());
	    Predicate companyPredicate = cb.equal(root.get("companyId"), req.getCompanyId());
	    Predicate amendIdPredicate = cb.equal(root.get("amendId"), subquery);
	    Predicate effectiveStartPredicate = cb.lessThanOrEqualTo(root.get("effectiveStartDate"), today);
	    Predicate effectiveEndPredicate = cb.greaterThanOrEqualTo(root.get("effectiveEndDate"), today);
	    

	    // Combine predicates
	    cq.select(root).where(
	        cb.and(productPredicate, companyPredicate, amendIdPredicate, effectiveStartPredicate, effectiveEndPredicate)
	    );
	    List<ClaimIntimation> list=em.createQuery(cq).getResultList();
	    return list;
	}
	@Override
	public ClaimIntimation getByClaimReferenceNo(ClaimIntimationGetReq req) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
	    CriteriaQuery<ClaimIntimation> cq = cb.createQuery(ClaimIntimation.class);
	    Root<ClaimIntimation> root = cq.from(ClaimIntimation.class);

	    // Get today's date
	    LocalDate today = LocalDate.now();

	    // Subquery to get max amendId for each claimReferenceNo
	    Subquery<Integer> subquery = cq.subquery(Integer.class);
	    Root<ClaimIntimation> subRoot = subquery.from(ClaimIntimation.class);
	    subquery.select(cb.max(subRoot.get("amendId")))
	            .where(
	                cb.equal(subRoot.get("companyId"), root.get("companyId")),
	                cb.equal(subRoot.get("productId"), root.get("productId")),
	                cb.equal(subRoot.get("claimReferenceNo"), root.get("claimReferenceNo"))
	            );

	    // Main query conditions
	    Predicate claimRefPredicate = cb.equal(root.get("claimReferenceNo"), req.getClaimReferenceNo());
	    Predicate amendIdPredicate = cb.equal(root.get("amendId"), subquery);
	    Predicate effectiveStartPredicate = cb.lessThanOrEqualTo(root.get("effectiveStartDate"), today);
	    Predicate effectiveEndPredicate = cb.greaterThanOrEqualTo(root.get("effectiveEndDate"), today);

	    // Combine predicates
	    cq.select(root).where(
	        cb.and(claimRefPredicate, amendIdPredicate, effectiveStartPredicate, effectiveEndPredicate)
	    );

	    ClaimIntimation claimIntimation=em.createQuery(cq).getResultList().get(0);
	    
	    return claimIntimation;
	}
	
	@Override
	public List<ProductRes> getProductByClaimNo(String companyId) {
		List<CompanyProductMaster> products = productRepo.findByCompanyIdAndStatus(companyId, "Y");

		  return products.stream()
                  .map(p -> new ProductRes(p.getProductId(), p.getProductName()))
                  .collect(Collectors.toList());
}
	
}
