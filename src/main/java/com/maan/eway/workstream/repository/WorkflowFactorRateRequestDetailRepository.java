/**
 * @author : Ashok Kumar S 
 * @since  : 09-01-2025
 */
package com.maan.eway.workstream.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maan.eway.workstream.entity.WorkflowFactorRateRequestDetail;
import com.maan.eway.workstream.entity.WorkflowFactorRateRequestDetailPK;

@Repository
public interface WorkflowFactorRateRequestDetailRepository extends 
			JpaRepository<WorkflowFactorRateRequestDetail, WorkflowFactorRateRequestDetailPK>{
	
}
