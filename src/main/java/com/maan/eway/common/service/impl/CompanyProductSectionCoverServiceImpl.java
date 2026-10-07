package com.maan.eway.common.service.impl;

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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.maan.eway.bean.FactorRateRequestDetails;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.ProductSectionMaster;
import com.maan.eway.bean.SectionCoverMaster;
import com.maan.eway.bean.SectionDataDetails;
import com.maan.eway.common.req.GetSectionReq;
import com.maan.eway.common.res.DropdownResponse;
import com.maan.eway.common.res.GetSectionRes;
import com.maan.eway.common.service.CompanyProductSectionCoverService;
import com.maan.eway.repository.FactorRateRequestDetailsRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.PolicyCoverDataRepository;
import com.maan.eway.repository.SectionDataDetailsRepository;

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
public class CompanyProductSectionCoverServiceImpl implements CompanyProductSectionCoverService {

	@Autowired
	private HomePositionMasterRepository homeRepo;

	@Autowired
	private SectionDataDetailsRepository secRepo;
	
	@Autowired
	private FactorRateRequestDetailsRepository factorRepo;
	
	@Autowired
	private PolicyCoverDataRepository policyRepo;

	@PersistenceContext
	private EntityManager em;

	private Logger log = LogManager.getLogger(CompanyProductSectionCoverServiceImpl.class);

	private static <T> java.util.function.Predicate<T> distinctByKey(
			java.util.function.Function<? super T, ?> keyExtractor) {
		Map<Object, Boolean> seen = new ConcurrentHashMap<>();
		return t -> seen.putIfAbsent(keyExtractor.apply(t), Boolean.TRUE) == null;
	}

	@Override
	public GetSectionRes getOptedAndUnoptedSection(GetSectionReq req) {
		GetSectionRes res = new GetSectionRes();
		try {

			
			List<ProductSectionMaster> getSectionList = getallSectionDetails(req.getProductId(), req.getCompanyId());
			HomePositionMaster byOriginal = homeRepo.findLatestPolicyByOriginalPolicyNo(req.getPrevPolicyNo());
			HomePositionMaster homeData = homeRepo.findByPolicyNo(byOriginal.getPolicyNo());
			List<FactorRateRequestDetails> factorList=factorRepo.findByRequestReferenceNoAndSectionIdNotAndLocationId(homeData.getRequestReferenceNo(),99999,Integer.valueOf(req.getLocationId()));
			List<FactorRateRequestDetails> uniqueSecId = factorList.stream().filter(distinctByKey(o -> Arrays.asList(o.getSectionId())))
					.collect(Collectors.toList());
			uniqueSecId.sort(Comparator.comparing(FactorRateRequestDetails::getSectionId));
			List<FactorRateRequestDetails> filteredSectionList = null;
			
			
			List<SectionDataDetails> secList = secRepo
					.findByPolicyNoAndStatusNotAndLocationIdOrderByLocationIdAsc(byOriginal.getPolicyNo(),"D",Integer.valueOf(req.getLocationId()));
			List<SectionDataDetails> optedSec = secList.stream().filter(distinctByKey(o -> Arrays.asList(o.getSectionId()))).collect(Collectors.toList());
			if (secList != null && uniqueSecId != null) {
				filteredSectionList = uniqueSecId.stream()
						.filter(m -> secList.stream()
								.noneMatch(risk -> m.getSectionId().equals(Integer.valueOf(risk.getSectionId())))) 
						.collect(Collectors.toList());
			}
			
			
			List<DropdownResponse> unOpdropList = new ArrayList<DropdownResponse>();
			for (FactorRateRequestDetails data : filteredSectionList) {
				DropdownResponse dropres = new DropdownResponse();
				List<ProductSectionMaster> p=getSectionList.stream().filter(o->o.getSectionId().equals(data.getSectionId())).collect(Collectors.toList());
				dropres.setLocationId(data.getLocationId().toString());
				dropres.setCode(data.getSectionId().toString());
				dropres.setCodeDesc(StringUtils.isBlank(p.get(0).getSectionName()) ? "" : p.get(0).getSectionName());
				unOpdropList.add(dropres);
			}
			List<DropdownResponse> opdropList = new ArrayList<DropdownResponse>();
			for (SectionDataDetails data : optedSec) {
				DropdownResponse dropres = new DropdownResponse();
				dropres.setLocationId(data.getLocationId().toString());
				dropres.setCode(data.getSectionId().toString());
				dropres.setCodeDesc(StringUtils.isBlank(data.getSectionDesc()) ? "" : data.getSectionDesc());
				opdropList.add(dropres);

			}
			res.setOptedSectionList(opdropList);
			res.setUnOptedSectionList(unOpdropList);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return res;
	}

	public List<ProductSectionMaster> getallSectionDetails(String productId, String companyId) {
		List<ProductSectionMaster> sectionList = new ArrayList<ProductSectionMaster>();
		try {
			// Find Latest Record
			CriteriaBuilder cb = em.getCriteriaBuilder();
			CriteriaQuery<ProductSectionMaster> query = cb.createQuery(ProductSectionMaster.class);

			// Find All
			Root<ProductSectionMaster> b = query.from(ProductSectionMaster.class);

			// Select
			query.select(b);

			// Amend ID Max Filter
			Subquery<Long> amendId = query.subquery(Long.class);
			Root<ProductSectionMaster> ocpm1 = amendId.from(ProductSectionMaster.class);
			amendId.select(cb.max(ocpm1.get("amendId")));
			Predicate a1 = cb.equal(ocpm1.get("sectionId"), b.get("sectionId"));
			Predicate a2 = cb.equal(b.get("productId"), ocpm1.get("productId"));
			Predicate a3 = cb.equal(b.get("companyId"), ocpm1.get("companyId"));
			// Predicate a4 = cb.lessThanOrEqualTo(b.get("effectiveDateStart"), today);
			amendId.where(a1, a2, a3);

			// Order By
			List<Order> orderList = new ArrayList<Order>();
			orderList.add(cb.desc(b.get("effectiveDateStart")));

			// Where
			Predicate n1 = cb.equal(b.get("amendId"), amendId);
			Predicate n2 = cb.equal(b.get("productId"), productId);
			Predicate n6 = cb.equal(b.get("companyId"), companyId);
			Predicate n7 = cb.equal(b.get("status"), "Y");
			query.where(n1, n2, n6,n7).orderBy(orderList);

			// Get Result
			TypedQuery<ProductSectionMaster> result = em.createQuery(query);
			sectionList = result.getResultList();
			sectionList = sectionList.stream().filter(distinctByKey(o -> Arrays.asList(o.getSectionId())))
					.collect(Collectors.toList());
			sectionList.sort(Comparator.comparing(ProductSectionMaster::getSectionName));

		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
			return null;

		}
		return sectionList;
	}

	@Override
	public GetSectionRes getOptedAndUnoptedSectionCover(GetSectionReq req) {
		GetSectionRes res = new GetSectionRes();
		try {

			List<SectionCoverMaster> getSectionCoverList=getallSectionCoverDetails( req.getProductId(),req.getSectionId(),req.getCompanyId());
			List<SectionCoverMaster> filteredSectionList = null;
			List<SectionDataDetails> secList = secRepo
					.findByPolicyNoAndSectionIdAndLocationIdOrderByLocationIdAsc(req.getPrevPolicyNo(),req.getSectionId(),Integer.valueOf(req.getLocationId())); 
			List<SectionDataDetails> optedSec = secList.stream().filter(distinctByKey(o -> Arrays.asList(o.getCoverId()))).collect(Collectors.toList());
			if (secList != null && getSectionCoverList != null) {
				filteredSectionList = getSectionCoverList.stream()
						.filter(m -> secList.stream()
								.noneMatch(risk -> m.getSectionId().equals(Integer.valueOf(risk.getCoverId())))) 
						.collect(Collectors.toList());
			}
			
			List<DropdownResponse> unOpdropList = new ArrayList<DropdownResponse>();
			for (SectionCoverMaster data : filteredSectionList) {
				DropdownResponse dropres = new DropdownResponse();
				dropres.setLocationId(req.getLocationId().toString());
				dropres.setCode(data.getCoverId().toString());
				dropres.setCodeDesc(StringUtils.isBlank(data.getCoverDesc()) ? "" : data.getCoverDesc());
				unOpdropList.add(dropres);
			}
			List<DropdownResponse> opdropList = new ArrayList<DropdownResponse>();
			for (SectionDataDetails data : optedSec) {
				DropdownResponse dropres = new DropdownResponse();
				String coverName = getSectionCoverList.stream().filter( o -> o.getCoverId().equals(data.getCoverId())).collect(Collectors.toList()).get(0).getCoverDesc();
				dropres.setLocationId(data.getLocationId().toString());
				dropres.setCode(data.getCoverId().toString());
				dropres.setCodeDesc(StringUtils.isBlank(coverName) ? "" : coverName);
				opdropList.add(dropres);

			}
			res.setOptedSectionList(opdropList);
			res.setUnOptedSectionList(unOpdropList);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return res;

	}
	
	
	public List<SectionCoverMaster> getallSectionCoverDetails(String product,String section,String company) {
		List<SectionCoverMaster> resList = new ArrayList<SectionCoverMaster>();
		try {
			Date today  =  new Date();
			Calendar cal = new GregorianCalendar();
			cal.setTime(today);
			cal.set(Calendar.HOUR_OF_DAY, 23);
			cal.set(Calendar.MINUTE, 50);
			today = cal.getTime();

			List<SectionCoverMaster> list = new ArrayList<SectionCoverMaster>();
						
							
			// Find Latest Record
			CriteriaBuilder cb2 = em.getCriteriaBuilder();
			CriteriaQuery<SectionCoverMaster> query2 = cb2.createQuery(SectionCoverMaster.class);

			// Find All
			Root<SectionCoverMaster> b2 = query2.from(SectionCoverMaster.class);

			// Amed Id
			
			Subquery<Long> amendId2 = query2.subquery(Long.class);
			Root<SectionCoverMaster> ocpm2 = amendId2.from(SectionCoverMaster.class);
			amendId2.select(cb2.max(ocpm2.get("amendId")));
			Predicate a7 = cb2.equal(ocpm2.get("coverId"), b2.get("coverId"));
			Predicate a12 = cb2.equal(ocpm2.get("subCoverId"), b2.get("subCoverId"));
			Predicate a8 = cb2.equal(ocpm2.get("sectionId"), b2.get("sectionId"));
			Predicate a9 = cb2.equal(ocpm2.get("productId"), b2.get("productId"));
			Predicate a10 = cb2.equal(ocpm2.get("companyId"), b2.get("companyId"));
			Predicate a13 = cb2.equal(ocpm2.get("agencyCode"), b2.get("agencyCode"));
			Predicate a14 = cb2.equal(ocpm2.get("branchCode"), b2.get("branchCode"));
			amendId2.where(a7,a8,a9,a10,a12,a13,a14);
					query2.select(b2);

			// Order By
			List<Order> orderList2 = new ArrayList<Order>();
			orderList2.add(cb2.asc(b2.get("coverName")));

			
			// Where
			Predicate n4 = cb2.equal(b2.get("amendId"),amendId2);
			Predicate n6 = cb2.equal(b2.get("subCoverId"),"0");
			Predicate n7 = cb2.equal(b2.get("productId"), product);
			Predicate n14 = cb2.equal(b2.get("companyId"), company);
			Predicate n15 = cb2.equal(b2.get("sectionId"), section);
			Predicate n9 = cb2.equal(b2.get("agencyCode"), "99999");
			Predicate n12 = cb2.equal(b2.get("branchCode"), "99999");
			Predicate n13 = cb2.equal(b2.get("status"), "Y");
			query2.where(n4,n6,n7,n14,n15,n9,n12,n13).orderBy(orderList2);

			// Get Result
			TypedQuery<SectionCoverMaster> result2 = em.createQuery(query2);
			list = result2.getResultList();
			list.sort( Comparator.comparing(SectionCoverMaster :: getAgencyCode  ).thenComparing(SectionCoverMaster :: getBranchCode  ) );;
			list = list.stream().filter(distinctByKey(o -> Arrays.asList(o.getCoverId() ,o.getSubCoverId() ))).collect(Collectors.toList());

		} catch (Exception e) {
			e.printStackTrace();
			log.info(e.getMessage());
			return null;

		}
		return resList;
	}
	
	@Override
	public GetSectionRes getOptedLocationId(GetSectionReq req) {
		GetSectionRes res = new GetSectionRes();
		try {
			List<HomePositionMaster> homeList = homeRepo.findByOriginalPolicyNoOrderByEndtCountDesc(req.getPrevPolicyNo());
			if(homeList!=null && !homeList.isEmpty() )
			{
				req.setPrevPolicyNo(homeList.get(0).getPolicyNo());
			}
			List<SectionDataDetails> secList = secRepo
					.findByPolicyNoAndStatusNotOrderByLocationIdAsc(req.getPrevPolicyNo(),"D");
			
			List<SectionDataDetails> optLoc = secList.stream().filter(distinctByKey(o -> Arrays.asList(o.getLocationId()))).collect(Collectors.toList());
			List<DropdownResponse> opdropList = new ArrayList<DropdownResponse>();
			for (SectionDataDetails data : optLoc) {
				DropdownResponse dropres = new DropdownResponse();
				dropres.setCode(data.getLocationId().toString());
				dropres.setCodeDesc(data.getLocationName());
				opdropList.add(dropres);
			}
			res.setOptedSectionList(opdropList);
			res.setUnOptedSectionList(null);
		} catch (Exception e) {
			e.printStackTrace();
			log.info("Log Details" + e.getMessage());
			return null;
		}
		return res;
	}


}
