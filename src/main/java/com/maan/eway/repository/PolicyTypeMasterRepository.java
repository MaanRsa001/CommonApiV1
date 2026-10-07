package com.maan.eway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.PolicyTypeMaster;
import com.maan.eway.bean.PolicyTypeMasterId;

public interface PolicyTypeMasterRepository extends JpaRepository<PolicyTypeMaster,PolicyTypeMasterId>, JpaSpecificationExecutor<PolicyTypeMaster>{

	List<PolicyTypeMaster> findByCompanyIdAndCoreAppCodeOrderByAmendIdDesc(String companyId, String typeOfCover);

}
