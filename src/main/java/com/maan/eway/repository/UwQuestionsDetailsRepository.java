package com.maan.eway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.UwQuestionsDetails;
import com.maan.eway.bean.UwQuestionsDetailsId;
public interface UwQuestionsDetailsRepository  extends JpaRepository<UwQuestionsDetails,UwQuestionsDetailsId > , JpaSpecificationExecutor<UwQuestionsDetails> {


	
	List<UwQuestionsDetails> findByCompanyIdAndProductIdAndRequestReferenceNoAndVehicleId(String insuranceId,
			Integer productId, String requestReferenceNo, Integer vehicleId);

	List<UwQuestionsDetails> findByRequestReferenceNo(String requestReferenceNo);

	List<UwQuestionsDetails> findByRequestReferenceNoAndVehicleId(String refNo, Integer vehId);


	List<UwQuestionsDetails> findByCompanyIdAndProductIdAndRequestReferenceNoAndBranchCodeAndQuestionCategoryAndVehicleIdNotAndIdTypeAndIdNumber(
			String companyId, Integer valueOf, String requestReferenceNo, String branchCode, String questionCategory,
			Integer valueOf2, String idType, String idNumber);

	List<UwQuestionsDetails> findByCompanyIdAndProductIdAndRequestReferenceNoAndSectionId(String companyId, Integer valueOf,
			String requestReferenceNo,String Sectionid);
	
	List<UwQuestionsDetails> findByCompanyIdAndProductIdAndRequestReferenceNo(String companyId, Integer valueOf,
			String requestReferenceNo);



}
