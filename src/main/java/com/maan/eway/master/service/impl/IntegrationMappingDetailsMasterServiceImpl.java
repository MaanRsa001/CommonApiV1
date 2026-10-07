package com.maan.eway.master.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.IntegrationMappingDetailsMaster;
import com.maan.eway.bean.PolicyTypeMaster;
import com.maan.eway.bean.ProductSectionMaster;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.error.Error;
import com.maan.eway.master.req.IntegrationMappingDetailsMasterGetAllReq;
import com.maan.eway.master.req.IntegrationMappingDetailsMasterGetReq;
import com.maan.eway.master.req.IntegrationMappingDetailsMasterSaveReq;
import com.maan.eway.master.res.IntegrationMappingDetailsMasterRes;
import com.maan.eway.master.service.IntegrationMappingDetailsMasterService;
import com.maan.eway.repository.IntegrationMappingDetailsRepository;

import jakarta.persistence.EntityManager;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.persistence.criteria.Subquery;

@Service
public class IntegrationMappingDetailsMasterServiceImpl implements IntegrationMappingDetailsMasterService {
	
	private final Logger log = LogManager.getLogger(IntegrationMappingDetailsMasterServiceImpl.class);	
	@Autowired
	private IntegrationMappingDetailsRepository integrMappingRepo;
	@Autowired
	private EntityManager entityManager;
	
	@Override
	public List<String> validateIntegrationMappingDetails(IntegrationMappingDetailsMasterSaveReq req) {
		List<String> errorCodes = new ArrayList<>();

	//--Primary Key Validation
		if(req.getCompanyId() == null) {
			errorCodes.add("5003");
		}
		if(req.getSectionId() == null) {
			errorCodes.add("5004");
		}
		if(req.getProductId() == null) {
			errorCodes.add("5005");
		}
//		if(req.getPolicyTypeId() == null) {
//			errorCodes.add("5006");
//		}
		
	//--Data Fields Validation
		if(req.getCoreSectionCode() == null) {
			errorCodes.add("5007");
		}
		if(StringUtils.isBlank(req.getCoreSectionDesc())) {
			errorCodes.add("5008");
		}
		if(req.getCoreProductCode() == null) {
			errorCodes.add("5009");
		}
		if(StringUtils.isBlank(req.getCoreProductDesc())) {
			errorCodes.add("5010");
		}
		if(StringUtils.isBlank(req.getStatus())) {
			errorCodes.add("5011");
		}
		else if(! (req.getStatus().equals("Y") || req.getStatus().equals("N") 
				|| req.getStatus().equals("P")) ){
			errorCodes.add("5012");
		}
		if(req.getEffectiveDateStart() == null) {
			errorCodes.add("5013");
		}
		else if(req.getEffectiveDateStart().isBefore(LocalDate.now())) {
			errorCodes.add("5014");
		}
		
		return errorCodes;
	}

	@Override
	public CommonRes saveIntegrationMappingDetails(IntegrationMappingDetailsMasterSaveReq req) {
		try {	
			ModelMapper mapper = new ModelMapper();
			CommonRes response = new CommonRes();
			
			List<IntegrationMappingDetailsMaster> existingDetails = integrMappingRepo.findAllByCompanyIdAndProductIdAndSectionIdAndPolicyTypeId(req.getCompanyId(), 
					req.getProductId(), req.getSectionId(), req.getPolicyTypeId());
			if(existingDetails != null && ! existingDetails.isEmpty()) {
				response.setMessage("Failed");
				response.setIsError(true);
				response.setErrorMessage(List.of(new Error("409", "Status", "The Integration Mapping Detail, You Are Trying To Create Already Exists.")));
			}
			else {
				IntegrationMappingDetailsMaster toSaveMapping = mapper.map(req, IntegrationMappingDetailsMaster.class);

				toSaveMapping.setProductName(retriveProductNameFromCompanyProductMaster(req.getCompanyId(), req.getProductId()));
				toSaveMapping.setSectionName(retriveSectionNameFromProductSectionMaster(req.getCompanyId(), req.getProductId(), req.getSectionId()));
				toSaveMapping.setPolicyType(retrivePolicyTypeNameFromPolicyTypeMaster(req.getCompanyId(), req.getProductId(), req.getPolicyTypeId()));
			//Integration Id	
				IntegrationMappingDetailsMaster topByIntegrationIdDesc = integrMappingRepo.findTopByOrderByIntegrationIdDesc();
				if(topByIntegrationIdDesc != null) {
					long maxIntegrId = topByIntegrationIdDesc.getIntegrationId();
					toSaveMapping.setIntegrationId(++maxIntegrId);
				}
				else {toSaveMapping.setIntegrationId(1L);}
			//Other Parameters	
				toSaveMapping.setAmendId(0);
				toSaveMapping.setEntryDate(LocalDate.now());
				toSaveMapping.setEffectiveDateEnd(LocalDate.of(2050, 12, 31));				
											
				integrMappingRepo.saveAndFlush(toSaveMapping);
				response.setMessage("Success");
				response.setIsError(false);
				response.setCommonResponse(Map.entry("Status", "Integration Mapping Details Saved Successfully"));
				}						
			return response;
		} catch (Exception e) {
			log.error(e.getMessage());
			return null;
		}
	}

	@Override
	public CommonRes updateIntegrationMappingDetails(IntegrationMappingDetailsMasterSaveReq req) {
		try {
			ModelMapper mapper = new ModelMapper();
			CommonRes response = new CommonRes();
			
			List<IntegrationMappingDetailsMaster> allByIntegrationIdList = integrMappingRepo.findAllByIntegrationId(req.getIntegrationId());
			if(allByIntegrationIdList != null && !allByIntegrationIdList.isEmpty()) {
				Optional<IntegrationMappingDetailsMaster> optionalMapping = allByIntegrationIdList.stream()
														.sorted(Comparator.comparing(IntegrationMappingDetailsMaster::getAmendId).reversed())
														.findFirst();
								
				IntegrationMappingDetailsMaster oldMapping = optionalMapping.get();
				List<IntegrationMappingDetailsMaster> saveList = new ArrayList<>();
				
		//Modify Old Record Effective End Date
				oldMapping.setUpdatedDate(LocalDate.now());
				oldMapping.setUpdatedBy(req.getCreatedBy());
				oldMapping.setEffectiveDateEnd(req.getEffectiveDateStart().minusDays(1));
				saveList.add(oldMapping);
				
		//Create New Record for Update
				IntegrationMappingDetailsMaster toUpdateMapping = mapper.map(req, IntegrationMappingDetailsMaster.class);
				
				toUpdateMapping.setProductName(retriveProductNameFromCompanyProductMaster(req.getCompanyId(), req.getProductId()));		
				toUpdateMapping.setSectionName(retriveSectionNameFromProductSectionMaster(req.getCompanyId(), req.getProductId(), req.getSectionId()));
				toUpdateMapping.setPolicyType(retrivePolicyTypeNameFromPolicyTypeMaster(req.getCompanyId(), req.getProductId(), req.getPolicyTypeId()));
				
				toUpdateMapping.setIntegrationId(oldMapping.getIntegrationId());		
				Integer amendId = oldMapping.getAmendId();
				toUpdateMapping.setAmendId(amendId+1);
				
				toUpdateMapping.setEntryDate(LocalDate.now());
				toUpdateMapping.setEffectiveDateEnd(LocalDate.of(2050, 12, 31));
				saveList.add(toUpdateMapping);
								
				integrMappingRepo.saveAll(saveList);
				
				response.setMessage("Success");
				response.setIsError(false);
				response.setCommonResponse(Map.entry("Status", "Integration Mapping Detail Updated Successfully"));
			}								
			else {
				response.setMessage("Failed");
				response.setIsError(true);
				response.setErrorMessage(List.of(new Error("404", "Status", "The Integration Mapping Detail To Be Updated Was Not Found.")));
			}
		return response;
		} catch (Exception e) {
			log.error(e.getMessage());
			return null;
		}
	}

	
		
	@Override
	public CommonRes getIntegrationMappingDetails(IntegrationMappingDetailsMasterGetReq req) {
		try {
			ModelMapper mapper = new ModelMapper();
			CommonRes response = new CommonRes();			
						
			List<IntegrationMappingDetailsMaster> allByIntegrationIdList = integrMappingRepo.findAllByIntegrationId(req.getIntegrationId());
			if(allByIntegrationIdList != null && !allByIntegrationIdList.isEmpty()) {
				IntegrationMappingDetailsMaster imd = allByIntegrationIdList.stream()
														.sorted(Comparator.comparing(IntegrationMappingDetailsMaster::getAmendId).reversed())
														.findFirst().get();
							
				IntegrationMappingDetailsMasterRes mappingDetailsResponse = mapper.map(imd, IntegrationMappingDetailsMasterRes.class);				
				
				response.setMessage("Success");
				response.setIsError(false);
				response.setCommonResponse(mappingDetailsResponse);
			}
			else{
				response.setMessage("Failed");
				response.setIsError(true);
				response.setErrorMessage(List.of(new Error("404", "Status", "Integration Mapping Detail Is Not Found")));
			}
			return response;
		} catch (Exception e) {
			log.error(e.getMessage());
			return null;
		}
	}
	

	@Override
	public CommonRes getAllIntegrationMappingDetails(IntegrationMappingDetailsMasterGetAllReq req) {
		try {
			ModelMapper mapper = new ModelMapper();
			CommonRes response = new CommonRes();
			List<IntegrationMappingDetailsMaster> recordsOfEachGroup = selectTopRecordsOfEachGroup(req.getCompanyId(), req.getProductId(), 
														req.getSectionId(), req.getPolicyTypeId());
	
			List<IntegrationMappingDetailsMasterRes> allMappingDetails = new ArrayList<>();
			if(recordsOfEachGroup != null && !recordsOfEachGroup.isEmpty()) {
				for(IntegrationMappingDetailsMaster mappingDetail : recordsOfEachGroup) {
					IntegrationMappingDetailsMasterRes mappingResponse = mapper.map(mappingDetail, IntegrationMappingDetailsMasterRes.class);
					allMappingDetails.add(mappingResponse);				
				}				
			}			
			response.setMessage("Success");
			response.setIsError(false);
			response.setCommonResponse(allMappingDetails);
			return response;
		} catch (Exception e) {
			log.error(e.getMessage());
			return null;
		}
	}

	
	
	@Override	
	public String retriveProductNameFromCompanyProductMaster(Integer companyId, Integer productId){
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<CompanyProductMaster> cq = cb.createQuery(CompanyProductMaster.class);
		Root<CompanyProductMaster> cpm = cq.from(CompanyProductMaster.class);
		
		cq.select(cpm).where(
				cb.equal(cpm.get("companyId"), String.valueOf(companyId)),
				cb.equal(cpm.get("productId"), productId)
				);
		cq.orderBy(cb.desc(cpm.get("amendId")));
		
		List<CompanyProductMaster> resultList = entityManager.createQuery(cq).getResultList();		
		if(resultList != null && !resultList.isEmpty()) {
			return resultList.get(0).getProductName();
		}
		else {return null;}
	}
	
	@Override	
	public String retriveSectionNameFromProductSectionMaster(Integer companyId, Integer productId, Integer sectionId){
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<ProductSectionMaster> cq = cb.createQuery(ProductSectionMaster.class);
		Root<ProductSectionMaster> root = cq.from(ProductSectionMaster.class);
		
		cq.select(root).where(
				cb.equal(root.get("companyId"), String.valueOf(companyId)),
				cb.equal(root.get("sectionId"), sectionId),
				cb.equal(root.get("productId"), productId)
				);
		cq.orderBy(cb.desc(root.get("amendId")));
		
		List<ProductSectionMaster> resultList = entityManager.createQuery(cq).getResultList();		
		if(resultList != null && !resultList.isEmpty()) {
			return resultList.get(0).getSectionName();
		}
		else {return null;}
	}
	
	@Override	
	public String retrivePolicyTypeNameFromPolicyTypeMaster(Integer companyId, Integer productId, Integer policyTypeId){
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<PolicyTypeMaster> cq = cb.createQuery(PolicyTypeMaster.class);
		Root<PolicyTypeMaster> root = cq.from(PolicyTypeMaster.class);
		
		cq.select(root).where(
				cb.equal(root.get("companyId"), String.valueOf(companyId)),
				cb.equal(root.get("productId"), productId),
				cb.equal(root.get("policyTypeId"), policyTypeId)						
				);
		cq.orderBy(cb.desc(root.get("amendId")));
		
		List<PolicyTypeMaster> resultList = entityManager.createQuery(cq).getResultList();		
		if(resultList != null && !resultList.isEmpty()) {
			return resultList.get(0).getPolicyTypeName();
		}
		else {return null;}
	}
	
	@Override
	public List<IntegrationMappingDetailsMaster> selectTopRecordsOfEachGroup(Integer companyId, Integer productId,  Integer sectionId, Integer policyTypeId){
		
		CriteriaBuilder cb = entityManager.getCriteriaBuilder();
		CriteriaQuery<IntegrationMappingDetailsMaster> cq = cb.createQuery(IntegrationMappingDetailsMaster.class);
		Root<IntegrationMappingDetailsMaster> root = cq.from(IntegrationMappingDetailsMaster.class);

	// Subquery to get the maximum amend_id for each group
		Subquery<Integer> subquery = cq.subquery(Integer.class);
		Root<IntegrationMappingDetailsMaster> subRoot = subquery.from(IntegrationMappingDetailsMaster.class);
		
		subquery.select(cb.max(subRoot.get("amendId")));
		subquery.where(
		    cb.equal(root.get("companyId"), subRoot.get("companyId")),
		    cb.equal(root.get("sectionId"), subRoot.get("sectionId")),
		    cb.equal(root.get("productId"), subRoot.get("productId")),
		    cb.equal(root.get("policyTypeId"), subRoot.get("policyTypeId"))
		);

		Predicate companyPre = cb.equal(root.get("companyId"), companyId);
		Predicate productPre = cb.equal(root.get("productId"), productId);
		Predicate sectionPre = cb.equal(root.get("sectionId"), sectionId);
		Predicate policyPre = cb.equal(root.get("policyTypeId"), policyTypeId);
		Predicate amendPre = cb.equal(root.get("amendId"), subquery);
		
	// Main query: Select records where amend_id matches the subquery's max
		if(companyId != null && productId != null && sectionId != null && policyTypeId != null) {
			cq.select(root).where(companyPre, productPre, sectionPre, policyPre, amendPre);
		}
		else if(companyId != null && productId != null && sectionId != null) {
			cq.select(root).where(companyPre, productPre, sectionPre, amendPre);
		}
		else if(companyId != null && productId != null) {			
			cq.select(root).where(companyPre, productPre, amendPre);
		}		
		else {
			cq.select(root).where(companyPre, amendPre);			
		}
		
	// Order the result
		cq.orderBy(
		    cb.asc(root.get("companyId")),
		    cb.asc(root.get("sectionId")),
		    cb.asc(root.get("productId")),
		    cb.asc(root.get("policyTypeId"))
		);
		
		return entityManager.createQuery(cq).getResultList();
	}

	


}
