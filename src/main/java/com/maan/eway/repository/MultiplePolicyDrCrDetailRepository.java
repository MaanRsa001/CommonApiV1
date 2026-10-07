package com.maan.eway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import com.maan.eway.bean.MultiplePolicyDrCrDetail;
import com.maan.eway.bean.MultiplePolicyDrCrDetailId;

import jakarta.transaction.Transactional;

public interface MultiplePolicyDrCrDetailRepository extends JpaRepository<MultiplePolicyDrCrDetail, MultiplePolicyDrCrDetailId> {

	List<MultiplePolicyDrCrDetail> findByQuoteNoAndRiskId(String quoteNo, int vehicleId);

	List<MultiplePolicyDrCrDetail> findByQuoteNo(String quoteNo);

	@Modifying
	@Transactional
	Integer deleteByQuoteNo(String quoteNo);

	@Modifying
	@Transactional
	@Query(nativeQuery=true,value="UPDATE multiple_policy_drcr_detail SET STATUS='E', ERROR_DESC=?2 WHERE quote_no=?1 ")
	Integer updateDrCrErrorStatus(String quoteNo, String errorDesc);
	List<MultiplePolicyDrCrDetail> findByQuoteNoAndStatusIgnoreCase(String quoteNo, String string);

	@Modifying
	@Transactional
	@Query(nativeQuery=true,value="UPDATE multiple_policy_drcr_detail SET STATUS='E', ERROR_DESC=?2 WHERE quote_no=?1 AND risk_id=?3")
	Integer updateDrCrErrorStatus(String quoteNo, String errorMesaage, String string);

	Long countByCompanyIdAndProductIdAndQuoteNo(String companyId, String errorDesc, String quoteNo);

	@Modifying
	@Transactional
	@Query(nativeQuery=true,value="UPDATE section_data_details SET COMMISSION_AMOUNT=?3, COMMISSION_PERCENTAGE=?4 WHERE quote_no=?1 AND risk_id=?2")
	Integer updateBrokerCommissionByRiskId(String quoteNo, Integer riskId, String string, String string2);

}
