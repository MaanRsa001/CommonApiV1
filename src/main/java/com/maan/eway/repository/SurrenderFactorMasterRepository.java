package com.maan.eway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.SurrenderFactorMaster;
import com.maan.eway.bean.SurrenderFactorMasterId;

public interface SurrenderFactorMasterRepository extends JpaRepository<SurrenderFactorMaster,SurrenderFactorMasterId > , JpaSpecificationExecutor<SurrenderFactorMaster> {

	List<SurrenderFactorMaster> findByPolicyTermsAndProductIdAndSectionIdAndCompanyId(Integer valueOf, Integer valueOf2,
			Integer valueOf3, String companyId);

}
