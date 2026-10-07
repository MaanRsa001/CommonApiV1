package com.maan.eway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.SurvivalBenefitMaster;
import com.maan.eway.bean.SurvivalBenefitMasterId;

public interface SurvivalBenefitMasterRepository extends JpaRepository< SurvivalBenefitMaster,  SurvivalBenefitMasterId>,  JpaSpecificationExecutor<SurvivalBenefitMaster> {

	List<SurvivalBenefitMaster> findByPolicyTermsAndProductIdAndSectionIdAndCompanyId(Integer valueOf, Integer valueOf2,
			Integer valueOf3, String companyId);

}
