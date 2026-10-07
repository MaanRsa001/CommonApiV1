/**@note   : Excess related portions only added by
 * @author : Ashok Kumar S 
 * @since  : 13-03-2025
 */
package com.maan.eway.common.service.impl;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dozer.DozerBeanMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.maan.eway.bean.BranchMaster;
import com.maan.eway.bean.ClausesMaster;
import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.ExcessMaster;
import com.maan.eway.bean.ExclusionMaster;
import com.maan.eway.bean.InsuranceCompanyMaster;
import com.maan.eway.bean.ListItemValue;
import com.maan.eway.bean.ProductMaster;
import com.maan.eway.bean.SectionMaster;
import com.maan.eway.bean.TermsAndCondition;
import com.maan.eway.bean.WarrantyMaster;
import com.maan.eway.common.req.AutoInsertTermsReq;
import com.maan.eway.common.req.CoverTCReq;
import com.maan.eway.common.req.ExcessReq;
import com.maan.eway.common.req.LocationTCReq;
import com.maan.eway.common.req.SectionDataRes;
import com.maan.eway.common.req.SectionTCReq;
import com.maan.eway.common.req.TermsAndConditionGetBySubIdReq;
import com.maan.eway.common.req.TermsAndConditionGetReq;
import com.maan.eway.common.req.TermsAndConditionInsertReq;
import com.maan.eway.common.req.TermsAndConditionListReq;
import com.maan.eway.common.req.TermsAndConditionReq;
import com.maan.eway.common.res.ClausesRes;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.ExcessRes;
import com.maan.eway.common.res.ExclusionRes;
import com.maan.eway.common.res.TermsAndConditionGetBySubIdRes;
import com.maan.eway.common.res.TermsAndConditionGetRes;
import com.maan.eway.common.res.TermsAndConditionListRes;
import com.maan.eway.common.res.TermsAndConditionRes;
import com.maan.eway.common.res.WarrantyRes;
import com.maan.eway.common.service.TermsAndConditionService;
import com.maan.eway.error.Error;
import com.maan.eway.repository.BranchMasterRepository;
import com.maan.eway.repository.ClausesMasterRepository;
import com.maan.eway.repository.EServiceMotorDetailsRepository;
import com.maan.eway.repository.EserviceBuildingDetailsRepository;
import com.maan.eway.repository.EserviceCommonDetailsRepository;
import com.maan.eway.repository.ExclusionMasterRepository;
import com.maan.eway.repository.InsuranceCompanyMasterRepository;
import com.maan.eway.repository.ListItemValueRepository;
import com.maan.eway.repository.PolicyCoverDataRepository;
import com.maan.eway.repository.ProductMasterRepository;
import com.maan.eway.repository.SectionMasterRepository;
import com.maan.eway.repository.TermsAndConditionRepository;
import com.maan.eway.repository.WarRateMasterRepository;
import com.maan.eway.repository.WarrantyMasterRepository;
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
public class TermsAndConditionServiceImpl implements TermsAndConditionService {

	private Logger log = LogManager.getLogger(TermsAndConditionServiceImpl.class);

	@PersistenceContext
	private EntityManager em;

	@Autowired
	private WarrantyMasterRepository warrantyRepo;

	@Autowired
	private WarRateMasterRepository warRepo;

	@Autowired
	private ExclusionMasterRepository exclusionRepo;

	@Autowired
	private ClausesMasterRepository clausesRepo;

	@Autowired
	private InsuranceCompanyMasterRepository inuranceRepo;

	@Autowired
	private BranchMasterRepository branchRepo;

	@Autowired
	private ProductMasterRepository productRepo;

	@Autowired
	private SectionMasterRepository sectionRepo;

	@Autowired
	private TermsAndConditionRepository termsRepo;

	@Autowired
	private ListItemValueRepository listRepo;
	
	@Autowired
	private EserviceBuildingDetailsRepository buildRepo;
	
	@Autowired
	private EserviceCommonDetailsRepository commonRepo;
	
	@Autowired
	private EServiceMotorDetailsRepository EServiceMotorDetailsRepo;
	
	@Autowired
	private EntityManager entityManager;
	
	@Autowired
	private PolicyCoverDataRepository policyCoverRepo;
	
	@Autowired
	private ModelMapper mapper;
	
	private static final Integer ID_FOR_EXCESS = 1;
	private static final String DESC_FOR_EXCESS = "Excess";

	
	@Override
	public TermsAndConditionRes viewTermsAndCondition(TermsAndConditionReq req) {
		TermsAndConditionRes res = new TermsAndConditionRes();

		try {
			
			List<WarrantyRes> warrantyresList = new ArrayList<WarrantyRes>();
			List<ExclusionRes> exclusionresList = new ArrayList<ExclusionRes>();
			List<ClausesRes> clausesresList = new ArrayList<ClausesRes>();

			String refNO = req.getRequestReferenceNo() ;
			
			List<TermsAndCondition> datas1 = termsRepo
					.findByCompanyIdAndBranchCodeAndProductIdAndSectionIdAndLocationIdAndRequestReferenceNoOrderBySnoAsc(req.getCompanyId(),
							req.getBranchCode(), req.getProductId(), req.getSectionId(),req.getLocationId(), refNO );
			
			List<TermsAndCondition> filterWarrantyList = datas1.stream().filter( o -> o.getId().equals(4) ).collect(Collectors.toList());
			List<TermsAndCondition> filterClausesList = datas1.stream().filter( o -> o.getId().equals(6) ).collect(Collectors.toList());
			List<TermsAndCondition> filterExclusionList = datas1.stream().filter( o -> o.getId().equals(7) ).collect(Collectors.toList());
			
			// Warranty 
			if (filterWarrantyList.size() > 0) {
				for (TermsAndCondition data : filterWarrantyList) {
						WarrantyRes warrantyres = new WarrantyRes();
						warrantyres.setId(data.getId().toString());
						warrantyres.setSubId(data.getSubId().toString());
						warrantyres.setSubIdDesc(data.getSubIdDesc());
						warrantyres.setDocRefNo(data.getDocRefNo());
						warrantyres.setDocumentId("16");
						warrantyres.setCoverId(data.getCoverId());
						warrantyres.setTypeId(data.getTypeId() );
						warrantyres.setSectionId(data.getSectionId() != null ? data.getSectionId() : ""  );
						warrantyresList.add(warrantyres);
						res.setWarrantyRes(warrantyresList);
					 
				}
				
			} 
			
			// Clauses
			if (filterClausesList.size() > 0) {
				for (TermsAndCondition data : filterClausesList) {
						ClausesRes clausesres = new ClausesRes();
						clausesres.setId(data.getId().toString());
						clausesres.setSubId(data.getSubId().toString());
						clausesres.setSubIdDesc(data.getSubIdDesc());
						clausesres.setDocRefNo(data.getDocRefNo());
						clausesres.setDocumentId("18");
						clausesres.setTypeId(data.getTypeId());
						clausesres.setCoverId(data.getCoverId());
						clausesres.setSectionId(data.getSectionId() != null ?  data.getSectionId() :  "" );
						clausesresList.add(clausesres);
						res.setClausesRes(clausesresList);
					 
				}
			}
				
			// Exclusion
			if (filterExclusionList.size() > 0) {
				for (TermsAndCondition data : filterExclusionList) {
						ExclusionRes exclusionres = new ExclusionRes();
						exclusionres.setId(data.getId().toString());
	
						exclusionres.setSubId(data.getSubId().toString());
						exclusionres.setSubIdDesc(data.getSubIdDesc());
						exclusionres.setDocRefNo(data.getDocRefNo());
						exclusionres.setDocumentId("19");
						exclusionres.setTypeId(data.getTypeId());
						exclusionres.setCoverId(data.getCoverId());
						exclusionres.setSectionId(data.getSectionId() != null ? data.getSectionId() : "" );
						exclusionresList.add(exclusionres);
						res.setExclusionRes(exclusionresList);
					 
				}
				
			}
			
		// Retrieves the list of excess details that are present in the terms and conditions.
			List<ExcessRes> excessInTC = retrieveExcessDetailsPresentInTermsAndCondition(req);
			res.setExcessRes(excessInTC);
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " + e.getMessage());
			return null;
		}
		return res;
	}

	
	public List<ClausesMaster> getClausesMaster(TermsAndConditionReq req) {
		List<ClausesMaster> list = new ArrayList<ClausesMaster>();

		try {
			Date today  = new Date();
			Calendar cal = new GregorianCalendar(); 
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today   = cal.getTime();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			Date todayEnd   = cal.getTime();

			
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ClausesMaster> query = cb.createQuery(ClausesMaster.class);

			// Find All
			Root<ClausesMaster> b = query.from(ClausesMaster.class);

			// Select
			query.select(b);

			// Effective Date Start Max Filter
			Subquery<Date> effectiveDate = query.subquery(Date.class);
			Root<ClausesMaster> ocpm1 = effectiveDate.from(ClausesMaster.class);
			effectiveDate.select(cb.greatest(ocpm1.get("effectiveDateStart").as(Date.class)));
			Predicate a1 = cb.equal(b.get("clausesId"), ocpm1.get("clausesId"));
			Predicate a2 = cb.lessThanOrEqualTo(ocpm1.get("effectiveDateStart"), today);
			Predicate a3 = cb.equal(ocpm1.get("companyId"), b.get("companyId"));
			Predicate a4 = cb.equal(ocpm1.get("branchCode"), b.get("branchCode"));
			Predicate a5 = cb.equal(ocpm1.get("productId"), b.get("productId"));
			Predicate a6 = cb.equal(ocpm1.get("sectionId"), b.get("sectionId"));
			Predicate a20 = cb.equal(ocpm1.get("coverId"), b.get("coverId"));
			effectiveDate.where(a1, a2, a3, a4, a5, a6,a20);
			// Effective Date End Max Filter
			Subquery<Date> effectiveDate2 = query.subquery(Date.class);
			Root<ClausesMaster> ocpm2 = effectiveDate2.from(ClausesMaster.class);
			effectiveDate2.select(cb.greatest(ocpm2.get("effectiveDateEnd").as(Date.class)));
			Predicate a7 = cb.equal(b.get("clausesId"), ocpm2.get("clausesId"));
			Predicate a8 = cb.greaterThanOrEqualTo(ocpm2.get("effectiveDateEnd"), todayEnd);
			Predicate a9 = cb.equal(ocpm2.get("companyId"), b.get("companyId"));
			Predicate a10 = cb.equal(ocpm2.get("branchCode"), b.get("branchCode"));
			Predicate a11 = cb.equal(ocpm2.get("productId"), b.get("productId"));
			Predicate a12 = cb.equal(ocpm2.get("sectionId"), b.get("sectionId"));
			Predicate a21 = cb.equal(ocpm2.get("coverId"), b.get("coverId"));
			effectiveDate2.where(a7, a8, a9, a10, a11, a12,a21);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.asc(b.get("sectionId")));

			// Where
			Predicate n2 = cb.equal(b.get("companyId"), req.getCompanyId());
			Predicate n3 = cb.equal(b.get("branchCode"), req.getBranchCode());
			Predicate n4 = cb.equal(b.get("branchCode"), "99999");
			Predicate n5 = cb.or(n3, n4);
			Predicate n6 = cb.equal(b.get("productId"), req.getProductId());
			Predicate n9 = cb.equal(b.get("sectionId"), req.getSectionId());
			Predicate n10 = cb.equal(b.get("sectionId"), "99999");
			Predicate n11 = cb.or(n9, n10);
			Predicate n1 = cb.equal(b.get("effectiveDateStart"), effectiveDate);
			Predicate n12 = cb.equal(b.get("effectiveDateEnd"), effectiveDate2);
			Predicate n13 = cb.equal(b.get("status"), "Y");
			Predicate n14 = cb.equal(b.get("status"), "R");
			Predicate n15 = cb.or(n13, n14);
			
			Predicate n23 = null;

			if(req.getCoverId() != null)
			{
				Predicate n49 = cb.equal(b.get("coverId"), req.getCoverId());

				query.where(n1, n12, n2, n5, n6, n11, n15, n49)
						.orderBy(orderList);
			}
			else
			{
				query.where(n1, n12, n2, n5, n6, n11, n15).orderBy(orderList);
			}
			
			// Get Result
			TypedQuery<ClausesMaster> result = em.createQuery(query);
			list = result.getResultList();
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getClausesId())))
					.filter(distinctByKey(o -> Arrays.asList(o.getClausesDescription())))					
					.collect(Collectors.toList());
			list.sort(Comparator.comparing(ClausesMaster::getClausesDescription));
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " + e.getMessage());
			return null;
		}
		return list;
	}
	
	public List<ExclusionMaster> getExclusionMaster(TermsAndConditionReq req) {
		List<ExclusionMaster> list2 = new ArrayList<ExclusionMaster>();


		try {
			Date today  = new Date();
			Calendar cal = new GregorianCalendar(); 
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today   = cal.getTime();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			Date todayEnd   = cal.getTime();
			
			// Find Latest Record
			CriteriaBuilder cb1 = em.getCriteriaBuilder();
			CriteriaQuery<ExclusionMaster> query2 = cb1.createQuery(ExclusionMaster.class);

			// Find All
			Root<ExclusionMaster> b2 = query2.from(ExclusionMaster.class);

			// Select
			query2.select(b2);

			// Effective Date Start Max Filter
			Subquery<Date> effectiveDate3 = query2.subquery(Date.class);
			Root<ExclusionMaster> ocpm3 = effectiveDate3.from(ExclusionMaster.class);
			effectiveDate3.select(cb1.greatest(ocpm3.get("effectiveDateStart").as(Date.class)));
			Predicate a21 = cb1.equal(b2.get("exclusionId"), ocpm3.get("exclusionId"));
			Predicate a22 = cb1.lessThanOrEqualTo(ocpm3.get("effectiveDateStart"), today);
			Predicate a23 = cb1.equal(ocpm3.get("companyId"), b2.get("companyId"));
			Predicate a24 = cb1.equal(ocpm3.get("branchCode"), b2.get("branchCode"));
			Predicate a25 = cb1.equal(ocpm3.get("productId"), b2.get("productId"));
			Predicate a26 = cb1.equal(ocpm3.get("sectionId"), b2.get("sectionId"));
			Predicate a40 = cb1.equal(ocpm3.get("coverId"), b2.get("coverId"));

			effectiveDate3.where(a21, a22, a23, a24, a25, a26,a40);
			// Effective Date End Max Filter
			Subquery<Date> effectiveDate4 = query2.subquery(Date.class);
			Root<ExclusionMaster> ocpm4 = effectiveDate4.from(ExclusionMaster.class);
			effectiveDate4.select(cb1.greatest(ocpm4.get("effectiveDateEnd").as(Date.class)));
			Predicate a27 = cb1.equal(b2.get("exclusionId"), ocpm4.get("exclusionId"));
			Predicate a28 = cb1.greaterThanOrEqualTo(ocpm4.get("effectiveDateEnd"), todayEnd);
			Predicate a29 = cb1.equal(ocpm4.get("companyId"), b2.get("companyId"));
			Predicate a30 = cb1.equal(ocpm4.get("branchCode"), b2.get("branchCode"));
			Predicate a31 = cb1.equal(ocpm4.get("productId"), b2.get("productId"));
			Predicate a32 = cb1.equal(ocpm4.get("sectionId"), b2.get("sectionId"));
			Predicate a41 = cb1.equal(ocpm4.get("coverId"), b2.get("coverId"));
			
			effectiveDate4.where(a27, a28, a29, a30, a31, a32,a41);
			// Order By
			List<Order> orderList2 = new ArrayList<Order>();
			orderList2.add(cb1.asc(b2.get("sectionId")));

			// Where
			Predicate n22 = cb1.equal(b2.get("companyId"), req.getCompanyId());
			Predicate n23 = cb1.equal(b2.get("branchCode"), req.getBranchCode());
			Predicate n24 = cb1.equal(b2.get("branchCode"), "99999");
			Predicate n25 = cb1.or(n23, n24);
			Predicate n26 = cb1.equal(b2.get("productId"), req.getProductId());
			Predicate n27 = cb1.equal(b2.get("sectionId"), req.getSectionId());
			Predicate n28 = cb1.equal(b2.get("sectionId"), "99999");
			Predicate n29 = cb1.or(n27, n28);
			Predicate n21 = cb1.equal(b2.get("effectiveDateStart"), effectiveDate3);
			Predicate n30 = cb1.equal(b2.get("effectiveDateEnd"), effectiveDate4);
			Predicate n31 = cb1.equal(b2.get("status"), "Y");
			Predicate n32 = cb1.equal(b2.get("status"), "R");
			Predicate n33 = cb1.or(n31, n32);
			Predicate n43=null;
			if(req.getCoverId()!=null)
			{
//				n43 =  cb1.equal(b2.get("coverId"), req.getCoverId());
//				Predicate n48 = cb1.equal(b2.get("coverId"), "99999");
//				Predicate n49=cb1.or(n43,n48);
				Predicate n49 = cb1.equal(b2.get("coverId"), req.getCoverId());
				query2.where(n21, n22, n25, n26, n29, n30, n33,n49).orderBy(orderList2);
			}
			else
			{
				query2.where(n21, n22, n25, n26, n29, n30, n33).orderBy(orderList2);
			}
			// Get Result
			TypedQuery<ExclusionMaster> result2 = em.createQuery(query2);
			list2 = result2.getResultList();
			list2 = list2.stream().filter(distinctByKey(o -> Arrays.asList(o.getExclusionId())))
					.filter(distinctByKey(o -> Arrays.asList(o.getExclusionDescription())))					
					.collect(Collectors.toList());
			list2.sort(Comparator.comparing(ExclusionMaster::getExclusionDescription));
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " + e.getMessage());
			return null;
		}
		return list2;
	}
	
	public List<WarrantyMaster> getWarrantiesMaster(TermsAndConditionReq req) {
		List<WarrantyMaster> list3 = new ArrayList<WarrantyMaster>();
		try {
			Date today  = new Date();
			Calendar cal = new GregorianCalendar(); 
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today   = cal.getTime();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			Date todayEnd   = cal.getTime();

			// Find Latest Record
			CriteriaBuilder cb3 = em.getCriteriaBuilder();
			CriteriaQuery<WarrantyMaster> query3 = cb3.createQuery(WarrantyMaster.class);

			// Find All
			Root<WarrantyMaster> b3 = query3.from(WarrantyMaster.class);

			// Select
			query3.select(b3);

			// Effective Date Start Max Filter
			Subquery<Date> effectiveDate5 = query3.subquery(Date.class);
			Root<WarrantyMaster> ocpm5 = effectiveDate5.from(WarrantyMaster.class);
			effectiveDate5.select(cb3.greatest(ocpm5.get("effectiveDateStart").as(Date.class)));
			Predicate a33 = cb3.equal(b3.get("warrantyId"), ocpm5.get("warrantyId"));
			Predicate a34 = cb3.lessThanOrEqualTo(ocpm5.get("effectiveDateStart"), today);
			Predicate a35 = cb3.equal(ocpm5.get("companyId"), b3.get("companyId"));
			Predicate a36 = cb3.equal(ocpm5.get("branchCode"), b3.get("branchCode"));
			Predicate a37 = cb3.equal(ocpm5.get("productId"), b3.get("productId"));
			Predicate a38 = cb3.equal(ocpm5.get("sectionId"), b3.get("sectionId"));
			Predicate a45 = cb3.equal(ocpm5.get("coverId"), b3.get("coverId"));

			effectiveDate5.where(a33, a34, a35, a36, a37, a38,a45);
			// Effective Date End Max Filter
			Subquery<Date> effectiveDate6 = query3.subquery(Date.class);
			Root<WarrantyMaster> ocpm6 = effectiveDate6.from(WarrantyMaster.class);
			effectiveDate6.select(cb3.greatest(ocpm6.get("effectiveDateEnd").as(Date.class)));
			Predicate a39 = cb3.equal(b3.get("warrantyId"), ocpm6.get("warrantyId"));
			Predicate a40 = cb3.greaterThanOrEqualTo(ocpm6.get("effectiveDateEnd"), todayEnd);
			Predicate a41 = cb3.equal(ocpm6.get("companyId"), b3.get("companyId"));
			Predicate a42 = cb3.equal(ocpm6.get("branchCode"), b3.get("branchCode"));
			Predicate a43 = cb3.equal(ocpm6.get("productId"), b3.get("productId"));
			Predicate a44 = cb3.equal(ocpm6.get("sectionId"), b3.get("sectionId"));
			Predicate a46 = cb3.equal(ocpm6.get("coverId"), b3.get("coverId"));
			effectiveDate6.where(a39, a40, a41, a42, a43, a44,a46);

			// Order By
			List<Order> orderList3 = new ArrayList<Order>();
			orderList3.add(cb3.asc(b3.get("sectionId")));

			// Where
			Predicate n40 = cb3.equal(b3.get("effectiveDateStart"), effectiveDate5);
			Predicate n43 = cb3.equal(b3.get("companyId"), req.getCompanyId());
			Predicate n44 = cb3.equal(b3.get("branchCode"), req.getBranchCode());
			Predicate n34 = cb3.equal(b3.get("branchCode"), "99999");
			Predicate n35 = cb3.or(n44, n34);
			Predicate n36 = cb3.equal(b3.get("productId"), req.getProductId());
			Predicate n37 = cb3.equal(b3.get("sectionId"), req.getSectionId());
			Predicate n38 = cb3.equal(b3.get("sectionId"), "99999");
			Predicate n39 = cb3.or(n37, n38);
			Predicate n41 = cb3.equal(b3.get("effectiveDateEnd"), effectiveDate6);
			Predicate n42 = cb3.equal(b3.get("status"), "Y");
			Predicate n45 = cb3.equal(b3.get("status"), "R");
			Predicate n46 = cb3.or(n42, n45);
			Predicate a47 =null;
			if(req.getCoverId()!=null)
			{
//				 a47 = cb3.equal(b3.get("coverId"), req.getCoverId());
//				 Predicate n48 = cb3.equal(b3.get("coverId"), "99999");
//				Predicate n49=cb3.or(a47,n48);
				Predicate n49 = cb3.equal(b3.get("coverId"), req.getCoverId());
				 query3.where(n43, n35, n36, n39, n40, n41, n46,n49).orderBy(orderList3);
			}
			else
			{
				query3.where(n43, n35, n36, n39, n40, n41, n46).orderBy(orderList3);
			}
			
	
			

			// Get Result
			TypedQuery<WarrantyMaster> result3 = em.createQuery(query3);
			list3 = result3.getResultList();
			list3 = list3.stream().filter(distinctByKey(o -> Arrays.asList(o.getWarrantyId())))
					.filter(distinctByKey(o -> Arrays.asList(o.getWarrantyDescription())))					
					.collect(Collectors.toList());
			list3.sort(Comparator.comparing(WarrantyMaster::getWarrantyDescription));
			
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " + e.getMessage());
			return null;
		}
		return list3;
	}
	
	private static <T> java.util.function.Predicate<T> distinctByKey(
			java.util.function.Function<? super T, ?> keyExtractor) {
		Map<Object, Boolean> seen = new ConcurrentHashMap<>();
		return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

	@Override
	public List<Error> validateTermsAndCondition(TermsAndConditionInsertReq req) {
		List<Error> errorList = new ArrayList<Error>();

		try {

			if (StringUtils.isBlank(req.getCompanyId())) {
				errorList.add(new Error("02", "CompanyId", "Please Enter CompanyId"));
			}

			if (StringUtils.isBlank(req.getBranchCode())) {
				errorList.add(new Error("02", "BranchCode", "Please Select BranchCode"));
			}
			if (StringUtils.isBlank(req.getProductId())) {
				errorList.add(new Error("03", "ProductId", "Please Select ProductId"));
			}

			if (StringUtils.isBlank(req.getSectionId())) {
				errorList.add(new Error("04", "SectionId", "Please Select SectionId"));
			}

			if (StringUtils.isBlank(req.getRequestReferenceNo())) {
				errorList.add(new Error("05", "RequestReferenceNo", "Please Enter RequestReferenceNo"));
			}
			if (StringUtils.isBlank(req.getRiskId())) {
				errorList.add(new Error("06", "RiskId", "Please Enter RiskId"));
			}
			
			List<TermsAndConditionListReq> req1 = req.getTermsAndConditionReq();
			
			if(req1.size()>0 ) {
				for (TermsAndConditionListReq re : req1) {
					
					if(re.getId().equalsIgnoreCase("6") && StringUtils.isBlank(re.getSubIdDesc()))
						errorList.add(new Error("06", "Description", "Please Enter Clauses Description"));
					
					if(re.getId().equalsIgnoreCase("7") && StringUtils.isBlank(re.getSubIdDesc()))
						errorList.add(new Error("06", "Description", "Please Enter Exclusion Description"));
					
					if(re.getId().equalsIgnoreCase("4") && StringUtils.isBlank(re.getSubIdDesc()))
						errorList.add(new Error("06", "Description", "Please Enter Warrranty Description"));
				}
			}
			
			if(req.getExcessReq() != null && !req.getExcessReq().isEmpty()) {
				int rowNum = 1 ;
				
				for(ExcessReq ex : req.getExcessReq()) {
					
					if(StringUtils.isBlank(ex.getSubIdDesc())) {
						errorList.add(new Error("11", "SubIdDesc", "Sub ID Desc or Excess description is required for row no. :" + rowNum));
					}
					
					if(ex.getExcessAmount() == null) {
						errorList.add(new Error("12", "ExcessAmount", "Excess amount is required for row no. :" + rowNum));
					}
					
					if(ex.getExcessPercentage() == null) {
						errorList.add(new Error("13", "ExcessPercentage", "Excess percentage is required for row no. :" + rowNum));
					}
					
					if(StringUtils.isBlank(ex.getCurrency())) {
						errorList.add(new Error("14", "Currency", "Currency is required for row no. :" + rowNum));
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
	public SuccessRes insertTermsAndCondition(TermsAndConditionInsertReq req) {
	
    	SuccessRes res = new SuccessRes();
		SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
		DozerBeanMapper dozermapper = new DozerBeanMapper();
       try {
			// delete 
            Integer idValue = Integer.valueOf(req.getTermsAndConditionReq().size() >0  ?  req.getTermsAndConditionReq().get(0).getId() : "0" );
			
			List<TermsAndCondition> data = termsRepo.findByRequestReferenceNoAndRiskIdAndProductIdAndSectionIdAndId(req.getRequestReferenceNo(),
					req.getRiskId(), req.getProductId(), req.getSectionId(),  idValue);

			if (data.size() > 0 && data != null) {
				termsRepo.deleteAll(data);
			}
			
			if( req.getTermsAndConditionReq()!=null && req.getTermsAndConditionReq().size() > 0   ) {
				Long count = termsRepo.count();
				Integer count1 = count.intValue();
				Integer a = 1000;
				TermsAndCondition saveData = new TermsAndCondition();
				List<TermsAndCondition> savelist  = new ArrayList<>();
				List<InsuranceCompanyMaster> insurance = inuranceRepo
						.findTopByCompanyIdOrderByAmendIdDesc(req.getCompanyId());
				List<BranchMaster> branch = branchRepo.findTopByCompanyIdAndBranchCodeOrderByAmendIdDesc(req.getCompanyId(),
						req.getBranchCode());
				List<ProductMaster> product = productRepo
						.findTopByProductIdOrderByAmendIdDesc(Integer.valueOf(req.getProductId()));
				List<SectionMaster> section = sectionRepo
						.findTopBySectionIdOrderByAmendIdDesc(Integer.valueOf(req.getSectionId()));

				
				saveData.setCompanyId(req.getCompanyId());
				saveData.setBranchCode(req.getBranchCode());
				saveData.setProductId(req.getProductId());
				saveData.setSectionId(req.getSectionId());
				saveData.setCompanyName(insurance.get(0).getCompanyName());
				saveData.setBranchName(branch.get(0).getBranchName());
				saveData.setProductName(product.get(0).getProductName());
				saveData.setSectionName(section.size() > 0 ? section.get(0).getSectionName() : "All") ;
				saveData.setEntryDate(new Date());
				saveData.setStatus("Y");
				saveData.setCreatedBy(req.getCreatedBy());
				saveData.setUpdatedBy(req.getCreatedBy());
				saveData.setUpdatedDate(new Date());
				saveData.setQuoteNo(req.getQuoteNo());
				saveData.setRiskId(req.getRiskId());
				saveData.setAmendId(0);
				saveData.setRequestReferenceNo(req.getRequestReferenceNo());
				
				// Remove D type 
			    // Insert O TYpe
				
			for(TermsAndConditionListReq req1: req.getTermsAndConditionReq())
			{
				//if(req1.getTypeId().equalsIgnoreCase("O"))
				{
                    ListItemValue id = listRepo.findByItemTypeAndItemCode("TERMS_AND_CONDITION", req1.getId());
    				TermsAndCondition saveDatas = new TermsAndCondition();
    				saveDatas = dozermapper.map(saveData,TermsAndCondition.class );
    				saveDatas.setSno(count1 + 1);
    				saveDatas.setId(Integer.valueOf(req1.getId()));
    				saveDatas.setIdDesc(id.getItemValue());
    				saveDatas.setDocRefNo(req1.getDocRefNo());
					
    				saveDatas.setTypeId(req1.getTypeId());

					if (StringUtils.isNotBlank(req1.getSubId())) {
						saveDatas.setSubId(Integer.valueOf(req1.getSubId()));
						saveDatas.setSubIdDesc(req1.getSubIdDesc());
					}

					else {
						saveDatas.setSubId(a++);
						saveDatas.setSubIdDesc(req1.getSubIdDesc());
					}
					
					count1++;
					savelist.add(saveDatas);
				}
			}
			
		//Saves excess-related terms and conditions 
			List<TermsAndCondition> excessTC = toSaveExcessTermsAndConditions(req, saveData);
			savelist.addAll(excessTC);
			
			if(savelist!=null &&!savelist.isEmpty())
			{
			termsRepo.saveAllAndFlush(savelist);
			
			
			}
			res.setResponse("Saved Successful"); res.setSuccessId(req.getQuoteNo()); 
			}
		}catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " + e.getMessage());
			return null;
			
			}
    	
    	return res;
    	
    
	}

	@Override
	public TermsAndConditionGetRes getTermsAndCondition(TermsAndConditionGetReq req) {
		TermsAndConditionGetRes res = new TermsAndConditionGetRes();
		DozerBeanMapper dozermapper = new DozerBeanMapper();
		try {
			TermsAndCondition savedata = new TermsAndCondition();
			List<TermsAndCondition> datas = new ArrayList<TermsAndCondition>();
			if (req.getQuoteNo() == null && StringUtils.isNotBlank(req.getQuoteNo())) {
				datas = termsRepo.findByCompanyIdAndBranchCodeAndProductIdAndSectionIdAndRiskIdAndQuoteNoAndId(
						req.getCompanyId(), req.getBranchCode(), req.getProductId(), req.getSectionId(),
						req.getRiskId(), req.getQuoteNo(), Integer.valueOf(req.getId()));
			}

			else {

				datas = termsRepo
						.findByCompanyIdAndBranchCodeAndProductIdAndSectionIdAndRiskIdAndRequestReferenceNoAndId(
								req.getCompanyId(), req.getBranchCode(), req.getProductId(), req.getSectionId(),
								req.getRiskId(), req.getRequestReferenceNo(), Integer.valueOf(req.getId()));

			}

			if (datas.size() > 0 && datas != null) {
				res = dozermapper.map(datas.get(0), TermsAndConditionGetRes.class);
				List<TermsAndConditionListRes> resList = new ArrayList<TermsAndConditionListRes>();
				for (TermsAndCondition data : datas) {
					TermsAndConditionListRes res1 = new TermsAndConditionListRes();
					res1.setSubId(data.getSubId().toString());
					res1.setSubIdDesc(data.getSubIdDesc().toString());
					resList.add(res1);
				}
				res.setTermsAndConditionlistRes(resList);
			}
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " + e.getMessage());
			return null;
		}
		return res;
	}

	@Override
	public TermsAndConditionGetBySubIdRes getTermsAndConditionSubId(TermsAndConditionGetBySubIdReq req) {
		TermsAndConditionGetBySubIdRes res = new TermsAndConditionGetBySubIdRes();
		DozerBeanMapper dozermapper = new DozerBeanMapper();
		try {
			TermsAndCondition savedata = new TermsAndCondition();
			TermsAndCondition data = new TermsAndCondition();
			if (req.getQuoteNo() == null && StringUtils.isNotBlank(req.getQuoteNo())) {
				data = termsRepo.findByCompanyIdAndBranchCodeAndProductIdAndSectionIdAndRiskIdAndQuoteNoAndIdAndSubId(
						req.getCompanyId(), req.getBranchCode(), req.getProductId(), req.getSectionId(),
						req.getRiskId(), req.getQuoteNo(), Integer.valueOf(req.getId()),
						Integer.valueOf(req.getSubId()));
			} else {
				data = termsRepo
						.findByCompanyIdAndBranchCodeAndProductIdAndSectionIdAndRiskIdAndRequestReferenceNoAndIdAndSubId(
								req.getCompanyId(), req.getBranchCode(), req.getProductId(), req.getSectionId(),
								req.getRiskId(), req.getRequestReferenceNo(), Integer.valueOf(req.getId()),
								Integer.valueOf(req.getSubId()));

			}
			res = dozermapper.map(data, TermsAndConditionGetBySubIdRes.class);
			res.setId(data.getId().toString());
			res.setSubId(data.getSubId().toString());
			res.setEntryDate(data.getEntryDate());
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " + e.getMessage());
			return null;
		}
		return res;
	}
	
	
	@Override
	public ResponseEntity<CommonRes> fetchTermsAndCondition(TermsAndConditionReq req){
		
		
		CommonRes data = new CommonRes();		
		TermsAndConditionRes res = new TermsAndConditionRes();

		try {
			
			if(req == null ||  StringUtils.isBlank(req.getCompanyId()) ||  StringUtils.isBlank(req.getProductId()) 
					||  StringUtils.isBlank(req.getSectionId()) ||   StringUtils.isBlank(req.getRequestReferenceNo())  ||   StringUtils.isBlank(req.getBranchCode())  ) {
				
				return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
			}
			
			
			List<WarrantyRes> warrantyresList = new ArrayList<WarrantyRes>();
			List<ExclusionRes> exclusionresList = new ArrayList<ExclusionRes>();
			List<ClausesRes> clausesresList = new ArrayList<ClausesRes>();
			 

			Date today  = new Date();
			Calendar cal = new GregorianCalendar(); 
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today   = cal.getTime();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			Date todayEnd   = cal.getTime();

			  
			// Warranty 
						 			 
			 List<ClausesMaster>  list = null;
			 List<ExclusionMaster> list2 = null ;
			 List<WarrantyMaster> list3 = null ;
			 
				
				List<TermsAndCondition> datas1 = termsRepo
						.findByCompanyIdAndBranchCodeAndProductIdAndSectionIdAndRequestReferenceNoOrderBySnoAsc(req.getCompanyId(),
								req.getBranchCode(), req.getProductId(), req.getSectionId(), req.getRequestReferenceNo() );
				
				if(null != datas1 &&  !datas1.isEmpty()) {
				
					
					List<TermsAndCondition> filterWarrantyList = datas1.stream().filter( o -> o.getId().equals(4) ).collect(Collectors.toList());
					List<TermsAndCondition> filterClausesList = datas1.stream().filter( o -> o.getId().equals(6) ).collect(Collectors.toList());
					List<TermsAndCondition> filterExclusionList = datas1.stream().filter( o -> o.getId().equals(7) ).collect(Collectors.toList());
	
								
					if (null != filterWarrantyList && !filterWarrantyList.isEmpty() ) {
						
						for (TermsAndCondition warranties : filterWarrantyList) {
							WarrantyRes warrantyres = new WarrantyRes();
							warrantyres.setId(null != warranties.getId() ? warranties.getId().toString() : "");
							warrantyres.setSubId(null != warranties.getSubId() ? warranties.getSubId().toString() : "");
							warrantyres.setSubIdDesc(warranties.getSubIdDesc());
							warrantyres.setDocRefNo(warranties.getDocRefNo());
							warrantyres.setDocumentId("16");
							warrantyres.setTypeId(warranties.getTypeId());
							warrantyres.setSectionId(warranties.getSectionId() != null ? warranties.getSectionId() : ""  );
							warrantyres.setCoverId(null);

							warrantyresList.add(warrantyres);
							
						}
						
						res.setWarrantyRes(warrantyresList);
					}
				
				
					if (null != filterClausesList && !filterClausesList.isEmpty() ) {
					
					for (TermsAndCondition clauses : filterClausesList) {
						ClausesRes clausesres = new ClausesRes();
						clausesres.setId(clauses.getId() != null ? clauses.getId().toString() : "" );

						clausesres.setSubId(null != clauses.getId() ? clauses.getId().toString() : "");
						clausesres.setSubIdDesc(clauses.getSubIdDesc());
						clausesres.setDocRefNo(clauses.getDocRefNo());
						clausesres.setDocumentId("18");
						clausesres.setSectionId(clauses.getSectionId() != null ?  clauses.getSectionId() :  "" );
						clausesres.setTypeId(clauses.getTypeId());
						clausesres.setCoverId(null );
						clausesresList.add(clausesres);
						
					
					
				}res.setClausesRes(clausesresList);
				
					}
				
					if (null != filterExclusionList && !filterExclusionList.isEmpty() ) {
						
						for (TermsAndCondition exclusions : filterExclusionList) {
							ExclusionRes exclusionres = new ExclusionRes();
							exclusionres.setId(exclusions.getId() != null ? exclusions.getId().toString() : "" );
							exclusionres.setSubId(exclusions.getSubId() != null ?  exclusions.getSubId().toString() : "");
							exclusionres.setSubIdDesc(exclusions.getSubIdDesc());
							exclusionres.setDocRefNo(exclusions.getDocRefNo());
							exclusionres.setDocumentId("19");
							exclusionres.setTypeId(exclusions.getTypeId());
							exclusionres.setSectionId(exclusions.getSectionId() != null ? exclusions.getSectionId() : "" );
							exclusionres.setCoverId(null);
							exclusionresList.add(exclusionres);
							

						
					}
						
						res.setExclusionRes(exclusionresList);
				}
		
			
				}else {
			
			   if(null != req && null != req.getCoverIds() && !req.getCoverIds().isEmpty() ) {
				   
				  // warranty and Exclusion Not based On cover Ids 
					   
				   if("99999".equals( req.getSectionId())  ) {             // Hold 				 
						
					 }
				   
				   
				   list3 = warrantyRepo
							.findAllByCompanyIdAndBranchCodeAndProductIdAndSectionIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqualAndStatus(
									req.getCompanyId(), "99999", req.getProductId(), req.getSectionId(),
									today, todayEnd, "Y");

					list = clausesRepo
							.findAllByCompanyIdAndBranchCodeAndProductIdAndSectionIdAndCoverIdInAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqualAndStatus(
									req.getCompanyId(),  "99999", req.getProductId(), req.getSectionId(), req.getCoverIds() , 
									today, todayEnd, "Y");

					list2 = exclusionRepo
							.findAllByCompanyIdAndBranchCodeAndProductIdAndSectionIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqualAndStatus(
									req.getCompanyId(),  "99999", req.getProductId(), req.getSectionId(),
									today, todayEnd, "Y");
				
					   
				 
				   
				} else {
					
					
					
					 if("99999".equals( req.getSectionId())  ) {
						 
						 list3 = warrantyRepo
									.findAllByCompanyIdAndBranchCodeAndProductIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqualAndStatus(
											req.getCompanyId(),  "99999", req.getProductId(), 
											today, todayEnd, "Y");
							/*
							 * list = clausesRepo
							 * .findAllByCompanyIdAndBranchCodeAndProductIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqualAndStatus(
							 * req.getCompanyId(), "99999", req.getProductId(), today, todayEnd, "Y");
							 */
							list=clausesRepo.findAllByCompanyIdAndBranchCodeAndProductIdAndSectionIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqualAndStatus
									(req.getCompanyId(),  "99999", req.getProductId(), req.getSectionId(), today, todayEnd, "Y");

							list2 = exclusionRepo
									.findAllByCompanyIdAndBranchCodeAndProductIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqualAndStatus(
											req.getCompanyId(),  "99999", req.getProductId(), 
											today, todayEnd, "Y"); 
							
					 }else {

					list3 = warrantyRepo
							.findAllByCompanyIdAndBranchCodeAndProductIdAndSectionIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqualAndStatus(
									req.getCompanyId(),  "99999", req.getProductId(), req.getSectionId(),
									today, todayEnd, "Y");

					list = clausesRepo
							.findAllByCompanyIdAndBranchCodeAndProductIdAndSectionIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqualAndStatus(
									req.getCompanyId(),  "99999", req.getProductId(), req.getSectionId(),
									today, todayEnd, "Y");

					list2 = exclusionRepo
							.findAllByCompanyIdAndBranchCodeAndProductIdAndSectionIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqualAndStatus(
									req.getCompanyId(),  "99999", req.getProductId(), req.getSectionId(),
									today, todayEnd, "Y");
					
					 }

				}
						
							if (null != list3 && !list3.isEmpty() ) {
								
								for (WarrantyMaster warranties : list3) {
									WarrantyRes warrantyres = new WarrantyRes();
									warrantyres.setId("4");

									warrantyres.setSubId(warranties.getWarrantyId().toString());
									warrantyres.setSubIdDesc(warranties.getWarrantyDescription());
									warrantyres.setDocRefNo(warranties.getDocRefNo());
									warrantyres.setDocumentId("16");
									warrantyres.setTypeId(warranties.getTypeId());
									warrantyres.setSectionId(warranties.getSectionId() != null ? warranties.getSectionId() : ""  );
									warrantyres.setCoverId(null);

									warrantyresList.add(warrantyres);
									
								}
								
								res.setWarrantyRes(warrantyresList);
							}
						
						
							if (null != list && !list.isEmpty() ) {
							
							for (ClausesMaster clauses : list) {
								ClausesRes clausesres = new ClausesRes();
								clausesres.setId("6");

								clausesres.setSubId(clauses.getClausesId().toString());
								clausesres.setSubIdDesc(clauses.getClausesDescription());
								clausesres.setDocRefNo(clauses.getDocRefNo());
								clausesres.setDocumentId("18");
								clausesres.setSectionId(clauses.getSectionId() != null ?  clauses.getSectionId() :  "" );
								clausesres.setTypeId(clauses.getTypeId());
								clausesres.setCoverId(clauses.getCoverId() != null ? clauses.getCoverId().toString() : "" );
								clausesresList.add(clausesres);
								
							
							
						}res.setClausesRes(clausesresList);
						
							}
						
							if (null != list2 && !list2.isEmpty() ) {
								
								for (ExclusionMaster exclusions : list2) {
									ExclusionRes exclusionres = new ExclusionRes();
									exclusionres.setId("7");

									exclusionres.setSubId(exclusions.getExclusionId().toString());
									exclusionres.setSubIdDesc(exclusions.getExclusionDescription());
									exclusionres.setDocRefNo(exclusions.getDocRefNo());
									exclusionres.setDocumentId("19");
									exclusionres.setTypeId(exclusions.getTypeId());
									exclusionres.setSectionId(exclusions.getSectionId() != null ? exclusions.getSectionId() : "" );
									exclusionres.setCoverId(null);
									exclusionresList.add(exclusionres);
									

								
							}
								
								res.setExclusionRes(exclusionresList);
						}
							
				}
				
			//	Retrieves the excess details, If excess details are present in the Terms and Conditions, they are returned.
			//	Otherwise, active excess master records are returned.			
				List<ExcessRes> excessDetails = getExcessDetails(req);	
				res.setExcessRes(excessDetails);
				
							data.setCommonResponse(res);
							data.setErrorMessage(Collections.emptyList());
							data.setIsError(false);
							data.setMessage("Success");
							
							return new ResponseEntity<CommonRes> (data, HttpStatus.CREATED);

					} catch (Exception e) {
			
			log.error("Exception Occurs When User Fetch The Records From Terms and Conditions Master Table **** "  + e.getMessage() );
			e.printStackTrace();
		//	thorw new 			
			return new ResponseEntity<> (null, HttpStatus.INTERNAL_SERVER_ERROR);
		}
		
	}
	
	
	@Override
	public ResponseEntity<CommonRes> fetchSectionsBasedOnRisk(String requestReferenceNo  , Integer riskId){
		

		
		CommonRes res = new CommonRes();
		List<SectionDataRes> sectionDataList = new ArrayList<>();
		
		try {
			
			List<EserviceBuildingDetails> list = buildRepo
					.findAllByRequestReferenceNoAndLocationId(requestReferenceNo, riskId);
			

			if (riskId != null && riskId == 1) {
							
				List<EserviceCommonDetails> commonList = commonRepo.findByRequestReferenceNo(requestReferenceNo);
			
				if (null != commonList && !commonList.isEmpty()) {
					
					Set<String> sectionList = new HashSet<String>();

					DozerBeanMapper mapper = new DozerBeanMapper();					

					for (EserviceCommonDetails data : commonList) {
												
						if (StringUtils.isNotBlank(data.getSectionId())) {
							
							
							
						     if ((data.getSectionId().equals("35") && data.getSumInsured() == null)
									|| (data.getSectionId().equals("36") && data.getSumInsured() == null)) {

								continue;
							}
						
						
					if (sectionList.add(data.getSectionId())) {

						SectionDataRes secRes = new SectionDataRes();

						mapper.map(data, secRes);
						secRes.setSectionName(data.getSectionName() != null ? data.getSectionName() : "" );

						sectionDataList.add(secRes);
					}
				}
			}

		}
			}
			
			

			if (null != list && !list.isEmpty()) {

				DozerBeanMapper mapper = new DozerBeanMapper();
				
				Set<String> sectionList = new HashSet<String>();

				for (EserviceBuildingDetails data : list) {

					if (StringUtils.isNotBlank(data.getSectionId())) {
						

						if (data.getSectionId().equals("0")) {
							continue;
						} else if ((data.getSectionId().equals("1") && data.getSumInsured() == null)
								|| (data.getSectionId().equals("3") && data.getSumInsured() == null)
								|| data.getSectionId().equals("47") && data.getSumInsured() == null) {

							continue;

						}
					
					if (sectionList.add(data.getSectionId())) {

					SectionDataRes secRes = new SectionDataRes();

					mapper.map(data, secRes);
					secRes.setSectionName(data.getSectionDesc() != null ? data.getSectionDesc() : "" );

					sectionDataList.add(secRes);

				}
					}
			}
				
				res.setCommonResponse(sectionDataList);
				res.setErrorMessage(null);
				res.setIsError(false);
				res.setMessage("Success");
				
				return new ResponseEntity<CommonRes>(res, HttpStatus.CREATED );

			}else {
				EserviceMotorDetails data = EServiceMotorDetailsRepo.findByRequestReferenceNoAndRiskId(requestReferenceNo, riskId);
				
				if(data != null) {
					SectionDataRes secRes = new SectionDataRes();
					secRes.setCompanyId(data.getCompanyId());
					secRes.setProductId(data.getProductId());
					secRes.setRequestReferenceNo(requestReferenceNo);
					secRes.setRiskId(String.valueOf(riskId));
					secRes.setSectionId(data.getSectionId());
					secRes.setSectionName(data.getSectionName());
					
					sectionDataList.add(secRes);
					
					res.setCommonResponse(sectionDataList);
					res.setErrorMessage(null);
					res.setIsError(false);
					res.setMessage("Success");
					
					return new ResponseEntity<CommonRes>(res, HttpStatus.CREATED );
				}
				
			}
			res.setCommonResponse(sectionDataList);
			res.setErrorMessage(null);
			res.setIsError(false);
			res.setMessage("Failure-Data Not found In Table");
		
		
			return new ResponseEntity<CommonRes>(res, HttpStatus.OK );

		
		}catch(Exception e) {
			
			log.error("Exception Occurs When Fetch Section Data Based On Section Id *******" +  e.getMessage() );
			e.printStackTrace();
		//	 throw new UnexpectedException("");
			return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
	
	
	/**
	 * Retrieves excess details that are present in the Terms and Conditions 
	 * based on the provided request parameters.
	 *
	 * This method fetches Terms and Conditions records from the repository 
	 * and filters them based on the predefined excess identifier {@code ID_FOR_EXCESS}.
	 * The filtered records are then mapped to {@link ExcessRes} objects and returned.
	 *
	 * @param req The request object containing company, branch, product, 
	 *            section identifiers, and request reference number.
	 * @return A list of {@link ExcessRes} objects representing the excess details 
	 *         found in the Terms and Conditions.
	 */
	private List<ExcessRes> retrieveExcessDetailsPresentInTermsAndCondition(TermsAndConditionReq req){
		List<ExcessRes> excessList = new ArrayList<>();
		
	    // Fetch Terms and Conditions records from the repository
		List<TermsAndCondition> termsAndConditions = termsRepo
				.findByCompanyIdAndBranchCodeAndProductIdAndSectionIdAndRequestReferenceNoOrderBySnoAsc(req.getCompanyId(),
						req.getBranchCode(), req.getProductId(), req.getSectionId(), req.getRequestReferenceNo());
		
		if(!termsAndConditions.isEmpty()) {			
	        // Filter records that match the excess identifier (ID_FOR_EXCESS)
			List<TermsAndCondition> filteredTC = termsAndConditions
						.stream()
						.filter(tc -> ID_FOR_EXCESS.equals(tc.getId()))
						.toList();
			
			filteredTC.forEach(tc -> {
				ExcessRes excess =  new ExcessRes();
				
				excess.setCoverId(null);
				
				excess.setId(tc.getId());
				excess.setSubId(tc.getSubId());
								
				excess.setSubIdDesc(tc.getSubIdDesc());
				excess.setExcessAmount(tc.getExcessAmount());
				excess.setExcessPercentage(tc.getExcessPercentage());
				excess.setCurrency(tc.getCurrency());				
				
				excessList.add(excess);				
			});
								
		}
		return excessList;	
	}
	
	
	/**
	 * Retrieves the excess details based on the provided {@link TermsAndConditionReq} request.
	 * If excess details are present in the Terms and Conditions, they are returned.
	 * Otherwise, active excess master records are retrieved and transformed into {@link ExcessRes} objects.
	 *
	 * @param req The request object containing company, product, and section identifiers.
	 * @return A list of {@link ExcessRes} objects representing the excess details.
	 * @throws Exception If an error occurs while retrieving excess details.
	 */
	private List<ExcessRes> getExcessDetails(TermsAndConditionReq req) throws Exception {
		List<ExcessRes> excessList = new ArrayList<>();
		
	    // Retrieve excess details that are present in the Terms and Conditions
		List<ExcessRes> excessInTC = retrieveExcessDetailsPresentInTermsAndCondition(req);
		if(!excessInTC.isEmpty()) {
			excessList.addAll(excessInTC);
		}

		// If no excess details exist in Terms and Conditions, retrieve from the master table
		else {
			List<ExcessMaster> allExcess = retrieveAllActiveExcessMaster(
					req.getCompanyId(), req.getProductId(), req.getSectionId());
			
			allExcess.forEach(ex -> {
				ExcessRes excess =  new ExcessRes();
								
				excess.setCoverId(Integer.valueOf(ex.getCoverId()));
				
				excess.setId(ID_FOR_EXCESS);
				excess.setSubId(ex.getExcessId());
				
				excess.setSubIdDesc(ex.getExcessDescription());				
				excess.setExcessAmount(ex.getExcessAmount());
				excess.setExcessPercentage(ex.getExcessPercentage());
				excess.setCurrency(ex.getCurrency());
				
				excessList.add(excess);				
			});
		
		}
		
		return excessList;
	}
	
	
	/**
	 * Retrieves a list of active {@link ExcessMaster} records based on the given company, product, and section IDs.
	 * The method ensures only the latest amendment (highest amendId) is considered, 
	 * and filters records based on active status and validity period.
	 *
	 * @param companyId The ID of the company.
	 * @param productId The ID of the product.
	 * @param sectionId The ID of the section.
	 * @return A list of active {@link ExcessMaster} entities matching the criteria.
	 * @throws Exception If an error occurs during query execution.
	 */
	private List<ExcessMaster> retrieveAllActiveExcessMaster(
			String companyId, String productId, String sectionId) throws Exception {
		
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<ExcessMaster> query = cb.createQuery(ExcessMaster.class);
		
		Root<ExcessMaster> excessRoot = query.from(ExcessMaster.class);
		
	    // Subquery to find the maximum amendment ID
		Subquery<Integer> maxAmendId = query.subquery(Integer.class);	
		Root<ExcessMaster> subRoot = maxAmendId.from(ExcessMaster.class);
		
		maxAmendId.select(cb.max(subRoot.get("amendId")))
			.where(
					cb.equal(excessRoot.get("companyId"), subRoot.get("companyId")),
					cb.equal(excessRoot.get("productId"), subRoot.get("productId")),
					cb.equal(excessRoot.get("sectionId"), subRoot.get("sectionId")),
					cb.equal(excessRoot.get("coverId"), subRoot.get("coverId")),
					cb.equal(excessRoot.get("excessId"), subRoot.get("excessId"))					
			);
		
		// Define filters for the query
		Predicate [] filters = new Predicate[] {
				cb.equal(excessRoot.get("companyId"), companyId),
				cb.equal(excessRoot.get("productId"), productId),
				cb.equal(excessRoot.get("sectionId"), sectionId),
				cb.equal(excessRoot.get("status"), "Y"), 				//"Y" means active
				cb.equal(excessRoot.get("amendId"), maxAmendId),
				cb.lessThanOrEqualTo(excessRoot.get("effectiveDateStart"), LocalDateTime.now()),
				cb.greaterThanOrEqualTo(excessRoot.get("effectiveDateEnd"), LocalDateTime.now())
		};
				
	    // Build and execute the query
		query.select(excessRoot)
			.where(cb.and(filters))
			.orderBy(cb.asc(excessRoot.get("excessDescription")));
		
		return entityManager.createQuery(query).getResultList();		
	}

	
	/**
	 * Saves excess-related terms and conditions by mapping input request details 
	 * into {@link TermsAndCondition} entities.
	 * 
	 * This method iterates through the list of excess details from the request, 
	 * maps them to TermsAndCondition objects, assigns IDs, and prepares them 
	 * for persistence.
	 * 
	 * @param req The request object containing excess details to be saved.
	 * @param basicDetails The base details used for mapping the TermsAndCondition entries.
	 * @return A list of {@link TermsAndCondition} objects representing to be saved excess terms and conditions.
	 */
	private List<TermsAndCondition> toSaveExcessTermsAndConditions(
			TermsAndConditionInsertReq req, TermsAndCondition basicDetails) {

		List<TermsAndCondition> termsAndConditions = new ArrayList<>();

		List<ExcessReq> allExcessSaveReq = req.getExcessReq();

		int subIdStart = 1001;
		int srNoStart = 1;
		if (allExcessSaveReq != null && !allExcessSaveReq.isEmpty()) {
			for (ExcessReq excess : allExcessSaveReq) {

				TermsAndCondition tc = mapper.map(basicDetails, TermsAndCondition.class);
				tc.setSno(srNoStart++);

				tc.setId(ID_FOR_EXCESS);
				tc.setIdDesc(DESC_FOR_EXCESS);

				Integer subId = excess.getSubId() != null ? excess.getSubId() : subIdStart++;
				tc.setSubId(subId);

				tc.setSubIdDesc(excess.getSubIdDesc());
				tc.setExcessAmount(excess.getExcessAmount());
				tc.setExcessPercentage(excess.getExcessPercentage());
				tc.setCurrency(excess.getCurrency());

				termsAndConditions.add(tc);
			}
		}

		return termsAndConditions;
	}
	
	@Override
	public TermsAndConditionRes getMasterData(TermsAndConditionReq req) {
		TermsAndConditionRes res = new TermsAndConditionRes();

		try {
			
			List<WarrantyRes> warrantyresList = new ArrayList<WarrantyRes>();
			List<ExclusionRes> exclusionresList = new ArrayList<ExclusionRes>();
			List<ClausesRes> clausesresList = new ArrayList<ClausesRes>();
			List<ExcessRes> excessresList = new ArrayList<ExcessRes>();


			String refNO = req.getRequestReferenceNo() ;
			
		
				List<WarrantyMaster> list3 = getWarrantiesMaster(req);
				if (list3.size() > 0  ) {
					
					for (WarrantyMaster warranties : list3) {
						WarrantyRes warrantyres = new WarrantyRes();
						warrantyres.setId("4");

						warrantyres.setSubId(warranties.getWarrantyId().toString());
						warrantyres.setSubIdDesc(warranties.getWarrantyDescription());
						warrantyres.setDocRefNo(warranties.getDocRefNo());
						warrantyres.setDocumentId("16");
						warrantyres.setTypeId(warranties.getTypeId());
						warrantyres.setSectionId(warranties.getSectionId() != null ? warranties.getSectionId() : ""  );
						warrantyres.setCoverId(warranties.getCoverId()!= null ? warranties.getCoverId().toString() : ""  );
						warrantyres.setCoverDesc(StringUtils.isNotBlank(warranties.getCoverDesc()) ?warranties.getCoverDesc() : ""  );
						warrantyresList.add(warrantyres);
						res.setWarrantyRes(warrantyresList);
					}
				}
			
			
			// Clauses
		
				List<ClausesMaster>  list = getClausesMaster(req);
				for (ClausesMaster clauses : list) {
					ClausesRes clausesres = new ClausesRes();
					clausesres.setId("6");

					clausesres.setSubId(clauses.getClausesId().toString());
					clausesres.setSubIdDesc(clauses.getClausesDescription());
					clausesres.setDocRefNo(clauses.getDocRefNo());
					clausesres.setDocumentId("18");
					clausesres.setSectionId(clauses.getSectionId() != null ?  clauses.getSectionId() :  "" );
					clausesres.setTypeId(clauses.getTypeId());
					clausesres.setCoverId(clauses.getCoverId()!= null ? clauses.getCoverId().toString() : ""  );
					clausesres.setCoverDesc(StringUtils.isNotBlank(clauses.getCoverDesc()) ?clauses.getCoverDesc() : ""  );
					clausesresList.add(clausesres);
					res.setClausesRes(clausesresList);

				}

				List<ExclusionMaster> list2 = getExclusionMaster(req);
				if (list2.size() > 0  ) {
					
					for (ExclusionMaster exclusions : list2) {
						ExclusionRes exclusionres = new ExclusionRes();
						exclusionres.setId("7");

						exclusionres.setSubId(exclusions.getExclusionId().toString());
						exclusionres.setSubIdDesc(exclusions.getExclusionDescription());
						exclusionres.setDocRefNo(exclusions.getDocRefNo());
						exclusionres.setDocumentId("19");
						exclusionres.setTypeId(exclusions.getTypeId());
						exclusionres.setSectionId(exclusions.getSectionId() != null ? exclusions.getSectionId() : "" );
						exclusionres.setCoverId(exclusions.getCoverId()!= null ? exclusions.getCoverId().toString() : ""  );
						exclusionres.setCoverDesc(StringUtils.isNotBlank(exclusions.getCoverDesc()) ?exclusions.getCoverDesc() : ""  );
						exclusionresList.add(exclusionres);
						res.setExclusionRes(exclusionresList);

					}
				}
			
				List<ExcessMaster> list4 = getExcessMasters(req);
				if (list4.size() > 0  ) {
					
					for (ExcessMaster exclusions : list4) {
						ExcessRes exclusionres = new ExcessRes();
						exclusionres.setId(1);

						exclusionres.setSubId(exclusions.getExcessId());
						exclusionres.setSubIdDesc(exclusions.getExcessDescription());
						exclusionres.setExcessPercentage(exclusions.getExcessPercentage());;
						exclusionres.setExcessAmount(exclusions.getExcessAmount());
						exclusionres.setTypeId(exclusions.getTypeId());
						exclusionres.setSectionId(exclusions.getSectionId() != null ? exclusions.getSectionId() : "" );
						exclusionres.setCoverId(exclusions.getCoverId()!= null ? Integer.valueOf(exclusions.getCoverId()) : null  );
						exclusionres.setCoverDesc(StringUtils.isNotBlank(exclusions.getCoverName()) ?exclusions.getCoverName() : ""  );
						excessresList.add(exclusionres);
						res.setExcessRes(excessresList);
					}
				}
			
		// Retrieves the list of excess details that are present in the terms and conditions.
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " + e.getMessage());
			return null;
		}
		return res;
	 
	}
	
	public List<ExcessMaster> getExcessMasters(TermsAndConditionReq req) {
		List<ExcessMaster> list2 = new ArrayList<ExcessMaster>();


		try {
			Date today  = new Date();
			Calendar cal = new GregorianCalendar(); 
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 1);
			today   = cal.getTime();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 1);
			cal.set(Calendar.MINUTE, 1);
			Date todayEnd   = cal.getTime();
			
			// Find Latest Record
			CriteriaBuilder cb1 = em.getCriteriaBuilder();
			CriteriaQuery<ExcessMaster> query2 = cb1.createQuery(ExcessMaster.class);

			// Find All
			Root<ExcessMaster> b2 = query2.from(ExcessMaster.class);

			// Select
			query2.select(b2);

			// Effective Date Start Max Filter
			Subquery<Date> effectiveDate3 = query2.subquery(Date.class);
			Root<ExcessMaster> ocpm3 = effectiveDate3.from(ExcessMaster.class);
			effectiveDate3.select(cb1.greatest(ocpm3.get("effectiveDateStart").as(Date.class)));
			Predicate a21 = cb1.equal(b2.get("excessId"), ocpm3.get("excessId"));
			Predicate a22 = cb1.lessThanOrEqualTo(ocpm3.get("effectiveDateStart"), today);
			Predicate a23 = cb1.equal(ocpm3.get("companyId"), b2.get("companyId"));
			Predicate a24 = cb1.equal(ocpm3.get("branchCode"), b2.get("branchCode"));
			Predicate a25 = cb1.equal(ocpm3.get("productId"), b2.get("productId"));
			Predicate a26 = cb1.equal(ocpm3.get("sectionId"), b2.get("sectionId"));
			Predicate a40 = cb1.equal(ocpm3.get("coverId"), b2.get("coverId"));
			effectiveDate3.where(a21, a22, a23, a24, a25, a26,a40);
			// Effective Date End Max Filter
			Subquery<Date> effectiveDate4 = query2.subquery(Date.class);
			Root<ExcessMaster> ocpm4 = effectiveDate4.from(ExcessMaster.class);
			effectiveDate4.select(cb1.greatest(ocpm4.get("effectiveDateEnd").as(Date.class)));
			Predicate a27 = cb1.equal(b2.get("excessId"), ocpm4.get("excessId"));
			Predicate a28 = cb1.greaterThanOrEqualTo(ocpm4.get("effectiveDateEnd"), todayEnd);
			Predicate a29 = cb1.equal(ocpm4.get("companyId"), b2.get("companyId"));
			Predicate a30 = cb1.equal(ocpm4.get("branchCode"), b2.get("branchCode"));
			Predicate a31 = cb1.equal(ocpm4.get("productId"), b2.get("productId"));
			Predicate a32 = cb1.equal(ocpm4.get("sectionId"), b2.get("sectionId"));
			Predicate a41 = cb1.equal(ocpm4.get("coverId"), b2.get("coverId"));

			effectiveDate4.where(a27, a28, a29, a30, a31, a32,a41);
			// Order By
			List<Order> orderList2 = new ArrayList<Order>();
			orderList2.add(cb1.asc(b2.get("sectionId")));

			// Where
			Predicate n22 = cb1.equal(b2.get("companyId"), req.getCompanyId());
			Predicate n23 = cb1.equal(b2.get("branchCode"), req.getBranchCode());
			Predicate n24 = cb1.equal(b2.get("branchCode"), "99999");
			Predicate n25 = cb1.or(n23, n24);
			Predicate n26 = cb1.equal(b2.get("productId"), req.getProductId());
			Predicate n27 = cb1.equal(b2.get("sectionId"), req.getSectionId());
			Predicate n28 = cb1.equal(b2.get("sectionId"), "99999");
			Predicate n29 = cb1.or(n27, n28);
			Predicate n21 = cb1.equal(b2.get("effectiveDateStart"), effectiveDate3);
			Predicate n30 = cb1.equal(b2.get("effectiveDateEnd"), effectiveDate4);
			Predicate n31 = cb1.equal(b2.get("status"), "Y");
			Predicate n32 = cb1.equal(b2.get("status"), "R");
			Predicate n33 = cb1.or(n31, n32);
			Predicate n43 = null;
			if(req.getCoverId()!=null)
			{
				n43 =  cb1.equal(b2.get("coverId"), req.getCoverId());
				Predicate n44 = cb1.equal(b2.get("coverId"), "99999");
				Predicate n45=cb1.or(n43,n44);
				query2.where(n21, n22, n25, n26, n29, n30, n33,n45).orderBy(orderList2);
			}
			else
			{
				query2.where(n21, n22, n25, n26, n29, n30, n33).orderBy(orderList2);
			}
	
			

			// Get Result
			TypedQuery<ExcessMaster> result2 = em.createQuery(query2);
			list2 = result2.getResultList();
			list2 = list2.stream().filter(distinctByKey(o -> Arrays.asList(o.getExcessId())))
					.filter(distinctByKey(o -> Arrays.asList(o.getExcessDescription())))					
					.collect(Collectors.toList());
			list2.sort(Comparator.comparing(ExcessMaster::getExcessDescription));
			
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Exception is --> " + e.getMessage());
			return null;
		}
		return list2;
	}
	@Override
	public SuccessRes insertTermsAndConditionWithLocation(TermsAndConditionInsertReq req) {
	 
	    SuccessRes res = new SuccessRes();
	    DozerBeanMapper dozermapper = new DozerBeanMapper();
	 
	    try {
	        List<InsuranceCompanyMaster> insurance =
	                inuranceRepo.findTopByCompanyIdOrderByAmendIdDesc(req.getCompanyId());
	        List<BranchMaster> branch =
	                branchRepo.findTopByCompanyIdAndBranchCodeOrderByAmendIdDesc(
	                        req.getCompanyId(), req.getBranchCode());
	        List<ProductMaster> product =
	                productRepo.findTopByProductIdOrderByAmendIdDesc(
	                        Integer.valueOf(req.getProductId()));
	 
	        String companyName = insurance.get(0).getCompanyName();
	        String branchName  = branch.get(0).getBranchName();
	        String productName = product.get(0).getProductName();

	        List<TermsAndCondition> existingRecords =
	                termsRepo.findByRequestReferenceNo(req.getRequestReferenceNo());
	        Set<String> existingKeys = existingRecords.stream()
	                .map(t -> buildDupKey(t.getId(), t.getSubId(),
	                        t.getLocationId(), t.getSectionId(), t.getCoverId()))
	                .collect(Collectors.toSet());
	 
	        int snoCounter = (int) termsRepo.count();
	        int subIdAuto  = 1000;
	 
	        List<TermsAndCondition> saveList = new ArrayList<>();
	 
	        if (req.getLocationList() == null || req.getLocationList().isEmpty()) {
	            res.setResponse("No location data provided");
	            res.setSuccessId(req.getQuoteNo());
	            return res;
	        }
	 
	        for (LocationTCReq location : req.getLocationList()) {
	 
	            String locationId = StringUtils.trimToEmpty(location.getLocationId());
	            if (StringUtils.isBlank(locationId)) continue;
	 
	            if (location.getSectionList() == null
	                    || location.getSectionList().isEmpty()) continue;
	 
	            for (SectionTCReq section : location.getSectionList()) {
	 
	                String sectionId = StringUtils.trimToEmpty(section.getSectionId());
	                if (StringUtils.isBlank(sectionId)) continue;
	 
	                List<SectionMaster> sectionMasters =
	                        sectionRepo.findTopBySectionIdOrderByAmendIdDesc(
	                                Integer.valueOf(sectionId));
	                String sectionName = sectionMasters.size() > 0
	                        ? sectionMasters.get(0).getSectionName() : "All";
	 
	                if (section.getCoverList() == null
	                        || section.getCoverList().isEmpty()) continue;
	 
	                for (CoverTCReq cover : section.getCoverList()) {
	 
	                    String coverId = StringUtils.trimToEmpty(cover.getCoverId());
	                    if (StringUtils.isBlank(coverId)) continue;
	 
	                    
	                    TermsAndCondition header = TermsAndCondition.builder()
	                            .companyId(req.getCompanyId())
	                            .branchCode(req.getBranchCode())
	                            .productId(req.getProductId())
	                            .locationId(locationId)
	                            .sectionId(sectionId)
	                            .coverId(coverId)
	                            .companyName(companyName)
	                            .branchName(branchName)
	                            .productName(productName)
	                            .sectionName(sectionName)
	                            .entryDate(new Date())
	                            .status("Y")
	                            .createdBy(req.getCreatedBy())
	                            .updatedBy(req.getCreatedBy())
	                            .updatedDate(new Date())
	                            .quoteNo(req.getQuoteNo())
	                            .riskId(locationId)
	                            .amendId(0)
	                            .requestReferenceNo(req.getRequestReferenceNo())
	                            .build();
	
	 
	                    if (cover.getTermsAndConditionReq() != null
	                            && !cover.getTermsAndConditionReq().isEmpty()) {
	 
	                        
	                        Set<Integer> typeIdsInRequest = cover.getTermsAndConditionReq()
	                                .stream()
	                                .filter(item -> StringUtils.isNotBlank(item.getId()))
	                                .map(item -> Integer.valueOf(item.getId()))
	                                .collect(Collectors.toSet());
	 
	                        for (Integer typeId : typeIdsInRequest) {
	                            List<TermsAndCondition> toDelete =
	                                    termsRepo.findByRequestReferenceNoAndLocationIdAndSectionIdAndCoverIdAndId(
	                                            req.getRequestReferenceNo(),
	                                            locationId, sectionId, coverId,
	                                            typeId);  
	                            if (toDelete != null && !toDelete.isEmpty()) {
	                                termsRepo.deleteAll(toDelete);
	                                toDelete.forEach(t -> existingKeys.remove(
	                                        buildDupKey(t.getId(), t.getSubId(),
	                                                t.getLocationId(), t.getSectionId(),
	                                                t.getCoverId())));
	                            }
	                        }
	 
	                        
	                        for (TermsAndConditionListReq item : cover.getTermsAndConditionReq()) {
	 
	                            if (StringUtils.isBlank(item.getId())) continue;
	 
	                            Integer resolvedSubId = StringUtils.isNotBlank(item.getSubId())
	                                    ? Integer.valueOf(item.getSubId()) : subIdAuto++;
	 
	                            String dupKey = buildDupKey(Integer.valueOf(item.getId()),
	                                    resolvedSubId, locationId, sectionId, coverId);
	                            if (existingKeys.contains(dupKey)) continue;
	 
	                            ListItemValue listItem = listRepo
	                                    .findByItemTypeAndItemCode("TERMS_AND_CONDITION", item.getId());
	 
	                            TermsAndCondition tc = dozermapper.map(header, TermsAndCondition.class);
	                            tc.setSno(++snoCounter);
	                            tc.setId(Integer.valueOf(item.getId()));
	                            tc.setIdDesc(listItem != null ? listItem.getItemValue() : "");
	                            tc.setSubId(resolvedSubId);
	                            tc.setSubIdDesc(item.getSubIdDesc());
	                            tc.setDocRefNo(item.getDocRefNo());
	                            tc.setTypeId(item.getTypeId()); 
	 
	                            saveList.add(tc);
	                            existingKeys.add(dupKey);
	                        }
	                    }
	 
	                    
	                    if (cover.getExcessReq() != null
	                            && !cover.getExcessReq().isEmpty()) {
	 
	                        
	                        ListItemValue excessItem = listRepo
	                                .findByItemTypeAndItemCode("TERMS_AND_CONDITION", "EXCESS");
	                        Integer excessTypeId = excessItem != null
	                                ? Integer.valueOf(excessItem.getItemValue()) : null;
	 
	                        if (excessTypeId != null) {
	                           
	                            List<TermsAndCondition> toDelete =
	                                    termsRepo.findByRequestReferenceNoAndLocationIdAndSectionIdAndCoverIdAndId(
	                                            req.getRequestReferenceNo(),
	                                            locationId, sectionId, coverId,
	                                            excessTypeId);  // ← only excess rows
	                            if (toDelete != null && !toDelete.isEmpty()) {
	                                termsRepo.deleteAll(toDelete);
	                                toDelete.forEach(t -> existingKeys.remove(
	                                        buildDupKey(t.getId(), t.getSubId(),
	                                                t.getLocationId(), t.getSectionId(),
	                                                t.getCoverId())));
	                            }
	                        }
	 
	                        int excessSubIdAuto = 1001;
	 
	                        for (ExcessReq excess : cover.getExcessReq()) {
	 
	                            Integer resolvedSubId = excess.getSubId() != null
	                                    ? excess.getSubId() : excessSubIdAuto++;
	 
	                            String dupKey = buildDupKey(excessTypeId, resolvedSubId,
	                                    locationId, sectionId, coverId);
	                            if (existingKeys.contains(dupKey)) continue;
	 
	                            TermsAndCondition tc = dozermapper.map(header, TermsAndCondition.class);
	                            tc.setSno(++snoCounter);
	                            tc.setId(excessTypeId);          
	                            tc.setIdDesc(excessItem.getItemValue());
	                            tc.setSubId(resolvedSubId);
	                            tc.setSubIdDesc(excess.getSubIdDesc());
	                            tc.setExcessAmount(excess.getExcessAmount());
	                            tc.setExcessPercentage(excess.getExcessPercentage());
	                            tc.setCurrency(excess.getCurrency());
	                            tc.setTypeId("O");
	 
	                            saveList.add(tc);
	                            existingKeys.add(dupKey);
	                        }
	                    }
	 
	                } 
	            } 
	        } 
	 
	        if (!saveList.isEmpty()) {
	            termsRepo.saveAllAndFlush(saveList);
	        }
	 
	        res.setResponse("Saved Successful");
	        res.setSuccessId(req.getQuoteNo());
	 
	    } catch (Exception e) {
	        e.printStackTrace();
	        log.info("Exception in insertTermsAndConditionWithLocation --> " + e.getMessage());
	        return null;
	    }
	 
	    return res;
	}
	
	@Override
	public void autoInsertDefaultTermsAfterBuyPolicy(AutoInsertTermsReq dto) {

		try {
			String requestRefNo = dto.getRequestReferenceNo();
			String companyId    = dto.getCompanyId();
			String branchCode   = dto.getBranchCode();
			String productId    = dto.getProductId();
			String quoteNo      = dto.getQuoteNo();
			String createdBy    = dto.getCreatedBy();

	        // ── 1. Fetch T&C type IDs ──────────────────────────────────────────
	        ListItemValue warrantyItem  = listRepo.findByItemTypeAndItemCode("TERMS_AND_CONDITION", "4");
	        ListItemValue clausesItem   = listRepo.findByItemTypeAndItemCode("TERMS_AND_CONDITION", "6");
	        ListItemValue exclusionItem = listRepo.findByItemTypeAndItemCode("TERMS_AND_CONDITION", "7");

			if (warrantyItem == null || clausesItem == null || exclusionItem == null) {
				log.error("T&C config missing for company={} product={}", companyId, productId);
				return;
			}

			Integer warrantyId   = Integer.valueOf(warrantyItem.getItemCode());
			Integer clausesId    = Integer.valueOf(clausesItem.getItemCode());
			Integer exclusionId  = Integer.valueOf(exclusionItem.getItemCode());

			String warrantyDesc  = warrantyItem.getItemValue();
			String clausesDesc   = clausesItem.getItemValue();
			String exclusionDesc = exclusionItem.getItemValue();

	        // ── 2. Fetch opted cover data ──────────────────────────────────────
	        List<Object[]> distinctCovers =
	                policyCoverRepo.findDistinctLocationSectionCover(
	                        quoteNo, companyId, productId, requestRefNo);

			if (distinctCovers == null || distinctCovers.isEmpty()) return;

	        Set<String> validCoverIds      = new HashSet<>();
	        Set<String> locationSectionSet = new LinkedHashSet<>();
	        Set<String> validLocSecCover   = new HashSet<>();
	        Set<String> validLocSection    = new HashSet<>();

			for (Object[] row : distinctCovers) {
				String loc = String.valueOf(row[0]);
				String sec = String.valueOf(row[1]);
				String cov = String.valueOf(row[2]);

				validCoverIds.add(cov);
				locationSectionSet.add(loc + "_" + sec);
				validLocSecCover.add(loc + "||" + sec + "||" + cov);
				validLocSection.add(loc + "||" + sec);
			}

	        // ── 3. Load typeId=D rows for delete check ─────────────────────────
	        List<TermsAndCondition> existingForDelete =
	                termsRepo.findByRequestReferenceNoAndTypeId(requestRefNo, "D");

			List<TermsAndCondition> toDelete = new ArrayList<>();

	        for (TermsAndCondition tc : existingForDelete) {
	            String loc = tc.getLocationId();
	            String sec = tc.getSectionId();
	            String cov = tc.getCoverId();

	            if ("99999".equals(cov)) {
	                // Delete 99999 rows only if this location+section no longer opted
	                if (!validLocSection.contains(loc + "||" + sec)) {
	                    toDelete.add(tc);
	                }
	            } else {
	                // Delete cover-specific rows if location+section+cover no longer opted
	                if (!validLocSecCover.contains(loc + "||" + sec + "||" + cov)) {
	                    toDelete.add(tc);
	                }
	            }
	        }

			if (!toDelete.isEmpty()) {
				termsRepo.deleteAll(toDelete);
			}

	        // ── 4. Re-fetch ALL rows fresh after delete ────────────────────────
	        // Must re-fetch — deleted rows must be gone, manual typeId=A rows must be included
	        List<TermsAndCondition> existingAfterDelete =
	                termsRepo.findByRequestReferenceNo(requestRefNo);

	        Set<String> existingKeys = existingAfterDelete.stream()
	                .map(t -> buildDupKey(
	                        t.getId(), t.getSubId(),
	                        t.getLocationId(), t.getSectionId(), t.getCoverId()))
	                .collect(Collectors.toSet());

	        int sno = existingAfterDelete.size();

	        // ── 5. Fetch master data — scoped to opted sectionIds ──────────────
	        List<String> optedSectionIds = distinctCovers.stream()
	                .map(row -> String.valueOf(row[1]))
	                .distinct()
	                .collect(Collectors.toList());

	        List<WarrantyMaster> warranties =
	                warrantyRepo.findByCompanyIdAndProductIdAndBranchCodeAndSectionIdInAndTypeIdAndStatus(
	                        companyId, productId, "99999", optedSectionIds, "D", "Y");

	        List<ClausesMaster> clauses =
	                clausesRepo.findByCompanyIdAndProductIdAndBranchCodeAndSectionIdInAndTypeIdAndStatus(
	                        companyId, productId, "99999", optedSectionIds, "D", "Y");

	        List<ExclusionMaster> exclusions =
	                exclusionRepo.findByCompanyIdAndProductIdAndBranchCodeAndSectionIdInAndTypeIdAndStatus(
	                        companyId, productId, "99999", optedSectionIds, "D", "Y");

			List<TermsAndCondition> saveList = new ArrayList<>();

	        // ── 6. Global insert: coverId=99999, once per locationId+sectionId ─
	        sno = insertGlobalTerms(
	                locationSectionSet, existingKeys, saveList,
	                warranties, clauses, exclusions,
	                warrantyId, clausesId, exclusionId,
	                warrantyDesc, clausesDesc, exclusionDesc,
	                companyId, productId, branchCode,
	                requestRefNo, quoteNo, createdBy, sno);

	        // ── 7. Cover-specific insert ───────────────────────────────────────
	        insertCoverTerms(
	                distinctCovers, validCoverIds, existingKeys, saveList,
	                warranties, clauses, exclusions,
	                warrantyId, clausesId, exclusionId,
	                warrantyDesc, clausesDesc, exclusionDesc,
	                companyId, productId, branchCode,
	                requestRefNo, quoteNo, createdBy, sno);

			if (!saveList.isEmpty()) {
				termsRepo.saveAll(saveList);
			}

		} catch (Exception e) {
			log.error("Error in autoInsertDefaultTerms", e);
		}
	}


	private int insertGlobalTerms(
	        Set<String>             locationSectionSet,
	        Set<String>             existingKeys,
	        List<TermsAndCondition> saveList,
	        List<WarrantyMaster>    warranties,
	        List<ClausesMaster>     clauses,
	        List<ExclusionMaster>   exclusions,
	        Integer warrantyId,   Integer clausesId,   Integer exclusionId,
	        String  warrantyDesc, String  clausesDesc, String  exclusionDesc,
	        String companyId, String productId, String branchCode,
	        String requestRefNo, String quoteNo, String createdBy,
	        int snoStart) {

	    final String GLOBAL = "99999";
	    int sno = snoStart;

	    for (String locSec : locationSectionSet) {

	        String[] split    = locSec.split("_");
	        String locationId = split[0];
	        String sectionId  = split[1];

	        TermsAndCondition header = buildHeader(
	                companyId, productId, branchCode,
	                locationId, sectionId, GLOBAL,
	                requestRefNo, quoteNo, createdBy);

	        for (WarrantyMaster wm : warranties) {
	            if (!GLOBAL.equals(String.valueOf(wm.getCoverId()))) continue;

	            String key = buildDupKey(warrantyId, wm.getWarrantyId(),
	                    locationId, sectionId, GLOBAL);
	            if (existingKeys.contains(key)) continue;

	            saveList.add(buildTC(header, ++sno, warrantyId, warrantyDesc,
	                    wm.getWarrantyId(), wm.getWarrantyDescription(), wm.getDocRefNo()));
	            existingKeys.add(key);
	        }

	        for (ClausesMaster cm : clauses) {
	            if (!GLOBAL.equals(String.valueOf(cm.getCoverId()))) continue;

	            String key = buildDupKey(clausesId, cm.getClausesId(),
	                    locationId, sectionId, GLOBAL);
	            if (existingKeys.contains(key)) continue;

	            saveList.add(buildTC(header, ++sno, clausesId, clausesDesc,
	                    cm.getClausesId(), cm.getClausesDescription(), cm.getDocRefNo()));
	            existingKeys.add(key);
	        }

	        for (ExclusionMaster ex : exclusions) {
	            if (!GLOBAL.equals(String.valueOf(ex.getCoverId()))) continue;

	            String key = buildDupKey(exclusionId, ex.getExclusionId(),
	                    locationId, sectionId, GLOBAL);
	            if (existingKeys.contains(key)) continue;

	            saveList.add(buildTC(header, ++sno, exclusionId, exclusionDesc,
	                    ex.getExclusionId(), ex.getExclusionDescription(), ex.getDocRefNo()));
	            existingKeys.add(key);
	        }
	    }

	    return sno;
	}

	
	private int insertCoverTerms(
	        List<Object[]>          distinctCovers,
	        Set<String>             validCoverIds,
	        Set<String>             existingKeys,
	        List<TermsAndCondition> saveList,
	        List<WarrantyMaster>    warranties,
	        List<ClausesMaster>     clauses,
	        List<ExclusionMaster>   exclusions,
	        Integer warrantyId,   Integer clausesId,   Integer exclusionId,
	        String  warrantyDesc, String  clausesDesc, String  exclusionDesc,
	        String companyId, String productId, String branchCode,
	        String requestRefNo, String quoteNo, String createdBy,
	        int snoStart) {

	    final String GLOBAL = "99999";
	    int sno = snoStart;

	    for (Object[] row : distinctCovers) {

	        String locationId = String.valueOf(row[0]);
	        String sectionId  = String.valueOf(row[1]);
	        String coverId    = String.valueOf(row[2]);

	        if (!validCoverIds.contains(coverId)) continue;

	        TermsAndCondition header = buildHeader(
	                companyId, productId, branchCode,
	                locationId, sectionId, coverId,
	                requestRefNo, quoteNo, createdBy);

	        for (WarrantyMaster wm : warranties) {
	            String wmCover = String.valueOf(wm.getCoverId());

	            if (GLOBAL.equals(wmCover)) continue;
	            if (!coverId.equals(wmCover)) continue;

	            String key = buildDupKey(warrantyId, wm.getWarrantyId(),
	                    locationId, sectionId, coverId);
	            if (existingKeys.contains(key)) continue;

	            saveList.add(buildTC(header, ++sno, warrantyId, warrantyDesc,
	                    wm.getWarrantyId(), wm.getWarrantyDescription(), wm.getDocRefNo()));
	            existingKeys.add(key);
	        }

	        for (ClausesMaster cm : clauses) {
	            String cmCover = String.valueOf(cm.getCoverId());

	            if (GLOBAL.equals(cmCover)) continue;
	            if (!coverId.equals(cmCover)) continue;

	            String key = buildDupKey(clausesId, cm.getClausesId(),
	                    locationId, sectionId, coverId);
	            if (existingKeys.contains(key)) continue;

	            saveList.add(buildTC(header, ++sno, clausesId, clausesDesc,
	                    cm.getClausesId(), cm.getClausesDescription(), cm.getDocRefNo()));
	            existingKeys.add(key);
	        }

	        for (ExclusionMaster ex : exclusions) {
	            String exCover = String.valueOf(ex.getCoverId());

	            if (GLOBAL.equals(exCover)) continue;
	            if (!coverId.equals(exCover)) continue;

	            String key = buildDupKey(exclusionId, ex.getExclusionId(),
	                    locationId, sectionId, coverId);
	            if (existingKeys.contains(key)) continue;

	            saveList.add(buildTC(header, ++sno, exclusionId, exclusionDesc,
	                    ex.getExclusionId(), ex.getExclusionDescription(), ex.getDocRefNo()));
	            existingKeys.add(key);
	        }
	    }

	    return sno;
	}

	private TermsAndCondition buildHeader(
	        String companyId, String productId, String branchCode,
	        String locationId, String sectionId, String coverId,
	        String requestRefNo, String quoteNo, String createdBy) {

	    return TermsAndCondition.builder()
	            .companyId(companyId)
	            .productId(productId)
	            .branchCode(branchCode)
	            .locationId(locationId)
	            .sectionId(sectionId)
	            .coverId(coverId)
	            .requestReferenceNo(requestRefNo)
	            .quoteNo(quoteNo)
	            .createdBy(createdBy)
	            .updatedBy(createdBy)
	            .entryDate(new Date())
	            .updatedDate(new Date())
	            .status("Y")
	            .typeId("D")
	            .amendId(0)
	            .riskId(locationId)
	            .build();
	}

	private TermsAndCondition buildTC(
	        TermsAndCondition header, int sno,
	        Integer id,    String idDesc,
	        Integer subId, String subDesc,
	        String docRefNo) {

	    TermsAndCondition tc = new TermsAndCondition();
	    BeanUtils.copyProperties(header, tc);

	    tc.setSno(sno);
	    tc.setId(id);
	    tc.setIdDesc(idDesc);
	    tc.setSubId(subId);
	    tc.setSubIdDesc(subDesc);
	    tc.setDocRefNo(docRefNo);

	    return tc;
	}
	
	private String buildDupKey(Integer id, Integer subId,
	        String locationId, String sectionId, String coverId) {
	    return id + "_" + subId + "_"
	            + StringUtils.defaultString(locationId) + "_"
	            + StringUtils.defaultString(sectionId)  + "_"
	            + StringUtils.defaultString(coverId);
	}
	
	@Override
	public List<Error> validateTermsAndConditionAutoInsert(AutoInsertTermsReq req) {

	    List<Error> errorList = new ArrayList<>();

	    if (StringUtils.isBlank(req.getCompanyId())) {
	        errorList.add(new Error("01", "CompanyId", "Please Enter CompanyId"));
	    }

	    if (StringUtils.isBlank(req.getBranchCode())) {
	        errorList.add(new Error("02", "BranchCode", "Please Enter BranchCode"));
	    }

	    if (StringUtils.isBlank(req.getProductId())) {
	        errorList.add(new Error("03", "ProductId", "Please Enter ProductId"));
	    }

	    if (StringUtils.isBlank(req.getRequestReferenceNo())) {
	        errorList.add(new Error("04", "RequestReferenceNo", "Please Enter RequestReferenceNo"));
	    }

	    if (StringUtils.isBlank(req.getQuoteNo())) {
	        errorList.add(new Error("05", "QuoteNo", "Please Enter QuoteNo"));
	    }

	    if (StringUtils.isBlank(req.getCreatedBy())) {
	        errorList.add(new Error("06", "CreatedBy", "Please Enter CreatedBy"));
	    }

	    return errorList;
	}
	
}
