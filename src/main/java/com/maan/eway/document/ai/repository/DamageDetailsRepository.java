package com.maan.eway.document.ai.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.maan.eway.document.ai.bean.DamageDetails;

public interface DamageDetailsRepository extends JpaRepository<DamageDetails, Long>{

	List<DamageDetails> findByQuoteNo(String quoteNo);

	List<DamageDetails> findByVehicleDetailsId(Long id);

}
