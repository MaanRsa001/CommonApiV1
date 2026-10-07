package com.maan.eway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.maan.eway.bean.ExcessMaster;
import com.maan.eway.bean.ExcessMasterId;

public interface ExcessMasterRepository extends JpaRepository<ExcessMaster, ExcessMasterId> {
    
    ExcessMaster findTopByCompanyIdAndProductIdAndSectionIdOrderByExcessIdDesc(
    		String companyId, String productId, String sectionId);

    ExcessMaster findTopByCompanyIdAndProductIdAndSectionIdAndExcessIdOrderByAmendIdDesc(
    		String companyId, String productId, String sectionId, Integer excessId);
    
    List<ExcessMaster> findAllByCompanyIdAndProductIdAndSectionIdOrderByExcessId(
    		String companyId, String productId, String sectionId);
    

    List<ExcessMaster> findAllByCompanyIdAndProductIdOrderByExcessId(
    		String companyId, String productId);

	List<ExcessMaster> findByCompanyIdAndProductIdAndSectionIdAndCoverIdOrderByAmendIdDesc(String companyId,
			String productId, String sectionId, String coverId);

	List<ExcessMaster> findByProductIdAndCompanyIdAndSectionId(String string, String companyId, String sectionId);
	
	List<ExcessMaster> findByCompanyIdAndProductIdAndSectionIdAndCoverIdAndStatus(String companyId,
			String productId, String sectionId, String coverId, String status);
    
}
