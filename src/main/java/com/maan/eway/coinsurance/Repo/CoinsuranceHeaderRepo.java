package com.maan.eway.coinsurance.Repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maan.eway.coinsurance.bean.CoInsuranceHeader;
@Repository
public interface CoinsuranceHeaderRepo extends JpaRepository<CoInsuranceHeader, String> {

	CoInsuranceHeader findByQuoteNo(String quoteNo);
	
	

}
