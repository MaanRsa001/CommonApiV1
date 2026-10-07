package com.maan.eway.preinspection;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface PreinspectionApiTxnRepository extends JpaRepository<PreinspectionApiTxn, Long> {

	Optional<PreinspectionApiTxn> findTopByQuoteNoOrderByCreatedDateDesc(String quoteNo);
	
}

