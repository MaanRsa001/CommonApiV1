package com.maan.eway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.maan.eway.bean.UwQuestionsOptionsMaster;
import com.maan.eway.bean.UwQuestionsOptionsMasterId;

public interface UwQuestionsOptionsMasterRepository  extends JpaRepository<UwQuestionsOptionsMaster,UwQuestionsOptionsMasterId >  {

	

	List<UwQuestionsOptionsMaster> findByCompanyIdAndBranchCodeOrBranchCodeAndProductId(String companyId,
			String branchCode, String string, Integer valueOf);

}
