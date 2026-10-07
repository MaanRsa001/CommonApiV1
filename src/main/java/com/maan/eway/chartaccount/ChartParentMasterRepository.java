package com.maan.eway.chartaccount;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;


@Repository
public interface ChartParentMasterRepository extends JpaRepository<ChartParentMaster, ChartParentMasterId> {

	List<ChartParentMaster> findByChatParentIdCompanyIdAndStatusIgnoreCase(Integer companyId, String status);

	List<ChartParentMaster> findByChatParentIdCompanyIdAndStatusIgnoreCaseOrderByDisplayOrderAsc(Integer companyId,
			String status);
	
	List<ChartParentMaster> findByChatParentIdCompanyId(Integer companyId);
	
	@Transactional
	@Modifying
	@Query(nativeQuery=true,value="DELETE FROM policy_drcr_detail WHERE quote_no=?1")
	Integer deleteDrCrDataByQuoteNo(String quoteNo);
	
	@Transactional
	@Modifying
	@Query(nativeQuery=true,value="DELETE FROM multiple_policy_drcr_detail WHERE quote_no=?1")
	Integer deleteMultipleDrCrDataByQuoteNo(String quoteNo);

}
