/**
 * @author : Ashok Kumar S 
 * @since  : 23-12-2024
 */
package com.maan.eway.workstream.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.maan.eway.workstream.entity.Approver;
import com.maan.eway.workstream.entity.ApproverPK;


public interface ApproverRepository extends JpaRepository<Approver, ApproverPK>{
	
	public Approver findTopByCompanyIdAndProductIdAndLoginIdOrderByAmendIdDesc(Integer companyId, 
			Integer productId, String loginId);
			

	public List<Approver> findAllByHierarchyLevel(String hierarchyLevel);
	
	public Optional<Approver> findByHierarchyLevel(String hierarchyLevel);
	
}
