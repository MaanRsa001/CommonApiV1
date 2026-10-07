package com.maan.eway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.ReInsuranceCoverDetailsId;
import com.maan.eway.bean.ReinsuranceCoverDetails;

public interface ReinsuranceCoverDetailsRepository extends JpaRepository<ReinsuranceCoverDetails,ReInsuranceCoverDetailsId > , JpaSpecificationExecutor<ReinsuranceCoverDetails> {
	
	void deleteByQuoteno(String quoteno);
	
	List<ReinsuranceCoverDetails> findByQuoteno(String quoteno);
	
	List<ReinsuranceCoverDetails> findByQuotenoAndSectionId(String quoteno, String sectonId);
	
	List<ReinsuranceCoverDetails> findByQuotenoAndSectionIdAndRiskIdAndProductId(String quoteno, String sectonId, Integer riskId, String productId);

}
