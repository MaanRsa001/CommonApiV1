package com.maan.eway.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.ExclusionMaster;
import com.maan.eway.bean.ExclusionMasterId;


public interface ExclusionMasterRepository extends JpaRepository<ExclusionMaster, ExclusionMasterId>, JpaSpecificationExecutor<ExclusionMaster>{

	
	
	 
	
	ExclusionMaster findTopByExclusionIdOrderByAmendIdDesc(Integer integer);

	
	List<ExclusionMaster> findByCompanyIdAndBranchCodeAndProductIdAndSectionIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqualAndStatusOrderByAmendIdDesc(
			String companyId, String branchCode, String productId, String sectionId, Date date, Date date2, String string);


	List<ExclusionMaster> findByCompanyIdAndBranchCodeAndProductIdAndSectionIdAndTypeIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqualAndStatusOrderByAmendIdDesc(
			String companyId, String branchCode, String productId, String sectionId, String string, Date date, Date date2, String string2);


	List<ExclusionMaster> findAllByCompanyIdAndBranchCodeAndProductIdAndSectionIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqualAndStatus(
			String companyId, String branchCode, String productId, String sectionId, Date today, Date todayEnd,
			String string);


	List<ExclusionMaster> findAllByCompanyIdAndBranchCodeAndProductIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqualAndStatus(
			String companyId, String string, String productId, Date today, Date todayEnd, String string2);
	 List<ExclusionMaster> findExclusionByProductIdAndCompanyIdAndSectionId(
	            String productId,
	            String companyId,
	            String sectionId
	    );
	 
	 List<ExclusionMaster> findByCompanyIdAndProductIdAndSectionIdAndCoverIdAndBranchCodeAndTypeIdAndStatus(
		        String companyId, String productId, String sectionId,
		        Integer coverId, String branchCode, String typeId, String status);


	List<ExclusionMaster> findByCompanyIdAndProductIdAndBranchCodeAndTypeIdAndStatus(String companyId, String productId,
			String branchCode, String string, String string2);
	
	List<ExclusionMaster> findByCompanyIdAndProductIdAndBranchCodeAndSectionIdInAndTypeIdAndStatus(
	        String companyId, String productId, String branchCode,
	        List<String> sectionIds, String typeId, String status);

}
