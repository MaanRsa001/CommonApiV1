package com.maan.eway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.ReInsuranceRiskDetails;
import com.maan.eway.bean.ReInsuranceRiskDetailsId;

public interface ReInsuranceRiskDetailsRepositiory extends JpaRepository<ReInsuranceRiskDetails,ReInsuranceRiskDetailsId > , JpaSpecificationExecutor<ReInsuranceRiskDetails> {
	
	void deleteByQuoteno(String quoteno);
	
	List<ReInsuranceRiskDetails	> findByQuoteno(String quoteno);

}
