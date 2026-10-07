package com.maan.eway.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.maan.eway.bean.ClausesMaster;
import com.maan.eway.bean.ClausesMasterId;


public interface ClausesMasterRepository  extends JpaRepository<ClausesMaster,ClausesMasterId>, JpaSpecificationExecutor<ClausesMaster>{

	
	
	 
	ClausesMaster findTopByClausesIdOrderByAmendIdDesc(Integer integer);

	
	List<ClausesMaster> findByCompanyIdAndBranchCodeAndProductIdAndSectionIdAndEffectiveDateStartLessThanAndEffectiveDateEndGreaterThanEqualAndStatusOrderByAmendIdDesc(
			String companyId, String branchCode, String productId, String sectionId, Date date, Date date2, String string);


	List<ClausesMaster> findByCompanyIdAndBranchCodeAndProductIdAndSectionIdAndTypeIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqualAndStatusOrderByAmendIdDesc(
			String companyId, String branchCode, String productId, String sectionId, String string, Date date, Date date2, String string2);


	List<ClausesMaster> findByCompanyIdAndBranchCodeOrBranchCodeAndProductIdAndSectionIdOrSectionIdAndTypeIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqualAndStatusOrderByAmendIdDesc(
			String companyId, String branchCode, String string, String productId, String sectionId,String string3, String termsId,
			Date date, Date date2, String string2);


	List<ClausesMaster> findAllByCompanyIdAndBranchCodeAndProductIdAndSectionIdAndCoverIdInAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqualAndStatus(
			String companyId, String branchCode, String productId, String sectionId, List<String> coverIds, Date today,
			Date todayEnd, String string);


	List<ClausesMaster> findAllByCompanyIdAndBranchCodeAndProductIdAndSectionIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqualAndStatus(
			String companyId, String branchCode, String productId, String sectionId, Date today, Date todayEnd,
			String string);


	List<ClausesMaster> findAllByCompanyIdAndBranchCodeAndProductIdAndEffectiveDateStartLessThanEqualAndEffectiveDateEndGreaterThanEqualAndStatus(
			String companyId, String string, String productId, Date today, Date todayEnd, String string2);
	
    List<ClausesMaster> findClausesByProductIdAndCompanyIdAndSectionId(String productId, String companyId, String sectionId);

    List<ClausesMaster> findByCompanyIdAndProductIdAndSectionIdAndBranchCodeAndTypeIdAndStatus(
            String companyId, String productId, String sectionId,
            String branchCode, String typeId, String status);


	List<ClausesMaster> findByCompanyIdAndProductIdAndBranchCodeAndTypeIdAndStatus(String companyId, String productId,
			String branchCode, String string, String string2);
	
	List<ClausesMaster> findByCompanyIdAndProductIdAndBranchCodeAndSectionIdInAndTypeIdAndStatus(
	        String companyId, String productId, String branchCode,
	        List<String> sectionIds, String typeId, String status);
}
