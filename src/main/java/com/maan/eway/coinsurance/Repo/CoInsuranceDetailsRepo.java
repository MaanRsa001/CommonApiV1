package com.maan.eway.coinsurance.Repo;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.coinsurance.bean.CoInsuranceDetails;
import com.maan.eway.coinsurance.bean.CoInsuranceDetailsId;

public interface CoInsuranceDetailsRepo extends JpaRepository<CoInsuranceDetails, CoInsuranceDetailsId> ,JpaSpecificationExecutor<CoInsuranceDetails>{
	
	 List<CoInsuranceDetails> findByQuoteNo(String quoteNo);

	 CoInsuranceDetails findByQuoteNoAndSlNo(String quoteNo, Integer slNo);


	

}
