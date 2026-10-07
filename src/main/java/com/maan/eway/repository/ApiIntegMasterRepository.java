package com.maan.eway.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maan.eway.bean.ApiIntegMaster;
import com.maan.eway.bean.ApiIntegMasterId;

@Repository
public interface ApiIntegMasterRepository extends JpaRepository<ApiIntegMaster, ApiIntegMasterId>{

	public List<ApiIntegMaster> findAllByCompanyIdAndProductId(String companyId, Integer productId);
	
	Optional<ApiIntegMaster> findByCompanyIdAndProductIdAndApiTypeAndStatus(
            String companyId,
            Integer productId,
            String apiType,
            String status);
	
}
