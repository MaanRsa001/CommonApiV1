package com.maan.eway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.maan.eway.bean.IntegrationMappingDetailsMaster;
import com.maan.eway.bean.IntegrationMappingDetailsMasterId;

public interface IntegrationMappingDetailsRepository extends JpaRepository<IntegrationMappingDetailsMaster, IntegrationMappingDetailsMasterId>{
	
	public IntegrationMappingDetailsMaster findTopByOrderByIntegrationIdDesc();
	
	public List<IntegrationMappingDetailsMaster> findAllByIntegrationId(Long integrationId);
	
	public List<IntegrationMappingDetailsMaster> findAllByCompanyId(Integer companyId);
	
	public List<IntegrationMappingDetailsMaster> findAllByCompanyIdAndProductIdAndSectionIdAndPolicyTypeId(Integer companyId, 
			Integer productId, Integer sectionId, Integer policyTypeId);

}
