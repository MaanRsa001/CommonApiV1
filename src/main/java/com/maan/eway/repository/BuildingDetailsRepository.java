package com.maan.eway.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;

import com.maan.eway.bean.BuildingDetails;
import com.maan.eway.bean.BuildingDetailsId;

public interface BuildingDetailsRepository extends JpaRepository<BuildingDetails, BuildingDetailsId>, JpaSpecificationExecutor<BuildingDetails> {

	BuildingDetails findByRequestReferenceNoAndRiskIdAndSectionId(String requestReferenceNo, Integer valueOf,
			String sectionId);

	BuildingDetails findByRequestReferenceNoAndRiskId(String requestReferenceNo, Integer valueOf);

	List<BuildingDetails> findByRequestReferenceNo(String requestReferenceNo);

	List<BuildingDetails> findByQuoteNoOrderByRiskIdAsc(String quoteNo);

	Long countByQuoteNo(String newQuoteNo);

	List<BuildingDetails> findByQuoteNo(String quoteNo);

	List<BuildingDetails> findByRequestReferenceNoOrderByRiskIdAsc(String requestReferenceNo);

	@Query(value = "SELECT   PD.section_id,   PD.COVER_DESC AS SECTION_DESC,   BD.LOCATION_NAME,   NULL AS DESCRIPTION,   PD.RATE,   PD.SUM_INSURED,   PD.PREMIUM_EXCLUDED_TAX_LC AS PREMIUM FROM   POLICY_COVER_DATA PD   INNER JOIN building_details BD ON PD.VEHICLE_ID = BD.RISK_ID   AND PD.Request_reference_no = BD.Request_reference_no WHERE   PD.TAX_ID = '0'   AND PD.DISC_LOAD_ID = '0'   AND PD.SUB_COVER_ID = '0'   AND PD.SECTION_ID NOT IN ('47', '3')   AND PD.QUOTE_NO = ?1 UNION ALL SELECT   PD.section_id,   cr.SECTION_DESC,   cr.location_name,   cr.content_risk_desc AS DESCRIPTION,   pd.rate,   cr.sum_insured,   PD.PREMIUM_EXCLUDED_TAX_LC AS PREMIUM FROM   CONTENT_AND_RISK cr CROSS   JOIN policy_cover_data pd WHERE   cr.QUOTE_NO = ?1   AND cr.risk_id = pd.vehicle_id   AND cr.section_id = pd.section_id   AND cr.request_reference_no = pd.request_reference_no   AND pd.tax_id = '0'   AND pd.disc_load_id = '0'",nativeQuery = true)
	List<Map<String, Object>> getSectionDetails(String quoteNo);
	


}
