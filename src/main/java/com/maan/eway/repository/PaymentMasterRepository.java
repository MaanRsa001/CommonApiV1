package com.maan.eway.repository;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.PaymentMaster;
import com.maan.eway.bean.PaymentMasterId;

public interface PaymentMasterRepository  extends JpaRepository<PaymentMaster,PaymentMasterId>, JpaSpecificationExecutor<PaymentMaster>{




	List<PaymentMaster> findByCompanyIdAndBranchCodeAndUserTypeAndSubUserTypeOrderByEntryDateDesc(String companyId,
			String branchCode, String userType, String subUserType);

	List<PaymentMaster> findByCompanyIdAndBranchCodeAndUserTypeAndSubUserTypeAndEffectiveDateStartOrderByEntryDateDesc(
			String companyId, String branchCode, String userType, String subUserType, Date effectiveDateStart);

	List<PaymentMaster> findByCompanyIdAndBranchCodeAndUserTypeAndSubUserTypeAndAgencyCodeAndEffectiveDateStartOrderByEntryDateDesc(
			String companyId, String branchCode, String userType, String subUserType, String agencyCode,
			Date effectiveDateStart);
	
	List<PaymentMaster> findByCompanyIdAndBranchCodeAndAgencyCodeAndEffectiveDateStartOrderByEntryDateDesc(
			String companyId, String branchCode,  String agencyCode,
			Date effectiveDateStart);

}
