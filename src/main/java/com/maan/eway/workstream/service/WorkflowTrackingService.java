/**
 * @author : Ashok Kumar S 
 * @since  : 23-12-2024
 */
package com.maan.eway.workstream.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import com.maan.eway.workstream.entity.WorkflowTracking;
import com.maan.eway.workstream.request.QuoteProposalActionReq;
import com.maan.eway.workstream.response.WorkflowTrackingRes;

public interface WorkflowTrackingService {
	
	public WorkflowTracking createNewEntryInWorkflow(QuoteProposalActionReq req);	
		
	public List<WorkflowTrackingRes> getAllWorkflowByProposalIdAndLevel(
			Integer companyId, Integer productId, Long proposalId, Integer hierarchyValue);
	
/*	public List<WorkflowTrackingRes> getAllWorkflowByApprover(
			Integer companyId, Integer productId, String loginId); */
	
	public WorkflowTrackingRes latestUpdateOnProposal(
			Integer companyId, Integer productId, Long proposalId); 
	
	public Map<Long, LocalDateTime> findActionTimeOfNonIncoming(
			Integer companyId, Integer productId, String loginId, String action);
	
	public Map<Long, LocalDateTime> findActionTimeOfEscalatedWorkflowBecomeIncoming(
			Integer companyId, Integer productId);
	
	public Map<Long, LocalDateTime> findActionTimeOfRevokedWorkflowBecomeIncoming(
			Integer companyId, Integer productId, Integer hierarchyLevel);
	
	public Map<Long, LocalDateTime> findActionTimeOfApprovedWorkflowBecomeIncoming(
			Integer companyId, Integer productId, Integer hierarchyLevel);
	
}
