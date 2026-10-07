/**
 * @author : Ashok Kumar S 
 * @since  : 23-12-2024
 */
package com.maan.eway.workstream.service;

import java.util.List;
import java.util.Optional;

import com.maan.eway.common.res.QuoteUpdateRes;
import com.maan.eway.error.Error;
import com.maan.eway.workstream.entity.QuoteProposal;
import com.maan.eway.workstream.request.ApproverGetReq;
import com.maan.eway.workstream.request.QuoteProposalActionReq;
import com.maan.eway.workstream.request.QuoteProposalSaveReq;
import com.maan.eway.workstream.response.QuoteProposalRes;

public interface QuoteProposalService {
	
	public List<Error> validateParametersOfApproverGetReq(ApproverGetReq req);
	
	public List<Error> validateParametersOfProposalActionReq(QuoteProposalActionReq req);
	
	public List<QuoteProposalRes> allIncomingProposals(ApproverGetReq req);
	
	public List<QuoteProposalRes> assignedForEach(ApproverGetReq req);
	
	public List<QuoteProposalRes> actionTakenByEachExceptAssigned(
			Integer companyId, Integer productId, String loginId, String action);
	
	public boolean verifyTheProposalIsIncomingForApprover(QuoteProposalActionReq req);
	
	public boolean verifyProposalAssignedToHimSelf(Integer companyId, 
			Integer productId, Long proposalId, String loginId);
	
	public boolean recordingActionOnProposal(QuoteProposalActionReq req);
	
	public void createQuoteProposalAndFirstWorkflowEntry(QuoteProposalSaveReq req);
	
	public QuoteProposal createNewProposal(QuoteProposalSaveReq req);
	
	public QuoteProposal setStatusOfProposal(QuoteProposalActionReq req);
	
	public QuoteUpdateRes finalUpdateOfReferralStatus(QuoteProposalActionReq req);
	
	public QuoteProposal updateLastActionOnProposal(QuoteProposalActionReq req);
	
	public Optional<QuoteProposalRes> getProposal(Integer companyId, Integer productId, Long proposalId);
		
}
