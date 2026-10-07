package com.maan.eway.preinspection;

import org.springframework.data.jpa.repository.JpaRepository;

import com.maan.eway.bean.PreinspectionUploadDetails;
import com.maan.eway.bean.PreinspectionUploadDetailsId;

public interface PreinspectionUploadDetailsRepo extends JpaRepository<PreinspectionUploadDetails, PreinspectionUploadDetailsId>{

	PreinspectionUploadDetails findByRegistrationNoAndChassisNo(String registrationNo, String chassisNo);

	PreinspectionUploadDetails findByQuoteNo(String quoteNo);

	PreinspectionUploadDetails findByRegistrationNo(String registrationNo);

}
