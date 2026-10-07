/**
 * @author : Ashok Kumar S 
 * @since  : 23-12-2024
 */
package com.maan.eway.workstream.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maan.eway.workstream.entity.HierarchyManagement;
import com.maan.eway.workstream.entity.HierarchyManagementId;


@Repository
public interface HierarchyManagementRepository extends JpaRepository<HierarchyManagement, HierarchyManagementId>{
	
	public List<HierarchyManagement> findAllByCompanyIdAndProductId(Integer companyId, Integer productId);

	public List<HierarchyManagement> findByCompanyIdAndProductId(Integer companyId, Integer valueOf);

}
