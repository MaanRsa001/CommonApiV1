/**
 * @author : Ashok Kumar S 
 * @since  : 23-12-2024
 */
package com.maan.eway.workstream.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.maan.eway.workstream.entity.WorkflowTracking;
import com.maan.eway.workstream.entity.WorkflowTrackingPK;


public interface WorkflowTrackingRepository extends JpaRepository<WorkflowTracking, WorkflowTrackingPK>{
	
	public WorkflowTracking findTopByCompanyIdAndProductIdAndProposalIdOrderByWorkflowIdDesc(
			Integer companyId, Integer productId, Long proposalId);
	
	public List<WorkflowTracking> findAllByCompanyIdAndProductIdAndProposalIdAndHierarchyValue(
			Integer companyId, Integer productId, Long proposalId, Integer hierarchyValue);
	
	public List<WorkflowTracking> findAllByCompanyIdAndProductIdAndProposalId(
			Integer companyId, Integer productId, Long proposalId);
	
	public List<WorkflowTracking> findAllByCompanyIdAndProductIdAndLoginId(Integer companyId, Integer productId, String loginId);
	
	public WorkflowTracking findTopByCompanyIdAndProductIdAndProposalIdOrderByActionTakenOnDesc(Integer companyId, 
			Integer productId, Long proposalId);	
	
}
