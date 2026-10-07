package com.maan.eway.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.maan.eway.bean.CustomerSequence;
import com.maan.eway.bean.CustomerSequenceId;

public interface CustomerSequenceRepository extends JpaRepository<CustomerSequence, CustomerSequenceId> {

	@Query(value = "SELECT sequence_no FROM customersequence WHERE company_id = :companyId ORDER BY sequence_no DESC LIMIT 1", nativeQuery = true)
	String findLatestSequenceByCompanyId(@Param("companyId") String companyId);
	
	@Query(value = "SELECT sequence_no FROM customersequence ORDER BY sequence_no DESC LIMIT 1", nativeQuery = true)
	String findLatestSequence();
	  @Modifying
	    @Transactional
	    @Query(value = "DELETE FROM customersequence WHERE company_id = :companyId", nativeQuery = true)
	    void deleteByCompanyId(@Param("companyId") String companyId);

}
