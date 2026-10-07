package com.maan.eway.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.WarrantyMaster;
import com.maan.eway.bean.WarrantyMasterId;


public interface WarrantyMasterRepository  extends JpaRepository<WarrantyMaster,WarrantyMasterId>, JpaSpecificationExecutor<WarrantyMaster>{

	
//	List<WarrantyMaster> findByCompanyIdAndBranchCodeAndProductIdAndSectionIdOrderByAmendIdDesc(String companyId,
//			String branchCode, String productId, String sectionId);

	WarrantyMaster findTopByWarrantyIdOrderByAmendIdDesc(Integer integer);

	List<WarrantyMaster> findByCompanyIdAndBranchCodeAndProductIdAndSectionIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqualAndStatusOrderByAmendIdDesc(
			String companyId, String branchCode, String productId, String sectionId, Date date, Date date2, String string);

	List<WarrantyMaster> findByCompanyIdAndBranchCodeAndProductIdAndSectionIdAndTypeIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqualAndStatusOrderByAmendIdDesc(
			String companyId, String branchCode, String productId, String sectionId, String string, Date date, Date date2, String string2);


	List<WarrantyMaster> findAllByCompanyIdAndBranchCodeAndProductIdAndSectionIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqualAndStatus(
			String companyId, String branchCode, String productId, String sectionId, Date today, Date todayEnd,
			String string);

	List<WarrantyMaster> findAllByCompanyIdAndBranchCodeAndProductIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqualAndStatus(
			String companyId, String string, String productId, Date today, Date todayEnd, String string2);

	List<WarrantyMaster> findByCompanyIdAndProductIdAndSectionIdAndBranchCodeAndTypeIdAndStatus(
	        String companyId, String productId, String sectionId,
	        String branchCode, String typeId, String status);

	List<WarrantyMaster> findByCompanyIdAndProductIdAndBranchCodeAndTypeIdAndStatus(String companyId, String productId,
			String branchCode, String string, String string2);
	
	List<WarrantyMaster> findByCompanyIdAndProductIdAndBranchCodeAndSectionIdInAndTypeIdAndStatus(
	        String companyId, String productId, String branchCode,
	        List<String> sectionIds, String typeId, String status);
	
}
