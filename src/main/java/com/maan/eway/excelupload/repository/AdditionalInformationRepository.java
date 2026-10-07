package com.maan.eway.excelupload.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.maan.eway.excelupload.bean.AdditionalInformation;

public interface AdditionalInformationRepository extends JpaRepository<AdditionalInformation, Long> {

	 List<AdditionalInformation> findByQuoteNoAndRequestReferenceNo(String quoteNo, String requestReferenceNo);

	 void deleteByQuoteNoAndRequestReferenceNoAndCompanyIdAndProductIdAndSectionIdAndCoverId(
            String quoteno, String requestReferenceNo, String companyId,
             String productId, String sectionId, String coverId);

	List<AdditionalInformation> findByQuoteNoAndCompanyIdAndProductIdAndSectionIdAndCoverId(String quoteNo,
			String companyId, String productId, String valueOf, String valueOf2);

	void deleteByQuoteNoAndCompanyIdAndProductIdAndSectionIdAndCoverId(String quoteNo, String companyId,
			String productId, String sectionId, String coverId);

	void deleteByQuoteNoAndLocationIdAndSectionIdAndCoverId(String quoteNo, String locationId, String sectionId,
			String coverId);

	List<AdditionalInformation> findByQuoteNoAndCompanyIdAndProductIdAndLocationIdAndSectionIdAndCoverId(String quoteNo,
			String companyId, String productId, String locationId , String valueOf, String valueOf2);
	
	void deleteByQuoteNoAndLocationIdAndSectionIdAndCoverIdAndRiskId(
	    String quoteNo, String locationId,
	    String sectionId, String coverId, String riskId);

	List<AdditionalInformation> findByQuoteNoAndCompanyIdAndProductIdAndLocationIdAndSectionIdAndCoverIdAndRiskId(
	    String quoteNo, String companyId, String productId,
	    String locationId, String sectionId, String coverId, String riskId);

	void deleteByEndtReqRefNo(String endtReqRefNo);

	List<AdditionalInformation> findByQuoteNoAndStatus(String quoteNo, String string);

	void deleteByEndtReqRefNoAndEndtStatusNot(String endtReqRefNo, String string);

	List<AdditionalInformation> findByEndtReqRefNo(String endtReqRefNo);

	List<AdditionalInformation> findByQuoteNo(String quoteNo);

	List<AdditionalInformation> findByQuoteNoAndCompanyIdAndProductIdAndLocationIdAndSectionIdAndCoverIdAndRiskIdAndStatus(
			String quoteNo, String companyId, String productId, String valueOf, String valueOf2, String valueOf3,
			String valueOf4, String string);

	List<AdditionalInformation> findByQuoteNoAndCompanyIdAndProductIdAndLocationIdAndSectionIdAndCoverIdAndStatus(
			String quoteNo, String companyId, String productId, String valueOf, String valueOf2, String valueOf3,
			String string);
	 

}
