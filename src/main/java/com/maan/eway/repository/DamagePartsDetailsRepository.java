package com.maan.eway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.DamagePartsDetails;
import com.maan.eway.bean.DamagePartsDetailsId;

public interface DamagePartsDetailsRepository extends JpaRepository<DamagePartsDetails,DamagePartsDetailsId > , JpaSpecificationExecutor<DamagePartsDetails>{
	
	List<DamagePartsDetails> findByQuoteNoAndUniqueId(String quoteNo, String uniqueId);

}
