package com.maan.eway.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.maan.eway.bean.ReinsuranceDiscLoadDetails;
import com.maan.eway.bean.ReinsuranceDiscLoadDetailsId;

public interface ReinsuranceDiscLoadDetailsRepository
		extends JpaRepository<ReinsuranceDiscLoadDetails, ReinsuranceDiscLoadDetailsId>,
		JpaSpecificationExecutor<ReinsuranceDiscLoadDetails> {

	List<ReinsuranceDiscLoadDetails> findByQuoteno(String quoteNo);

}
