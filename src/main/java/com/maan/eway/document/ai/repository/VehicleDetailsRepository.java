package com.maan.eway.document.ai.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.maan.eway.document.ai.bean.VehicleDetails;

public interface VehicleDetailsRepository extends JpaRepository<VehicleDetails, Long>{

	List<VehicleDetails> findByQuoteNo(String quoteNo);

	VehicleDetails findByVehicleImageId(Long id);

	

}
