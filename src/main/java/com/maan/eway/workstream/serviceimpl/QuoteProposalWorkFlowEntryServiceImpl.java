package com.maan.eway.workstream.serviceimpl;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.maan.eway.common.service.impl.QuoteThreadServiceImpl;
import com.maan.eway.workstream.entity.QuoteProposal;
import com.maan.eway.workstream.entity.WorkflowFactorRateRequestDetail;
import com.maan.eway.workstream.entity.WorkflowTracking;
import com.maan.eway.workstream.repository.QuoteProposalRepository;
import com.maan.eway.workstream.request.QuoteProposalSaveReq;

import jakarta.transaction.Transactional;

@Service
public class QuoteProposalWorkFlowEntryServiceImpl {
	
	private static final String STATUS_PROCESSING = "PR";
	
	@Autowired
	private QuoteProposalRepository proposalRepo;
	@Autowired
	private WorkflowTrackingServiceImpl workflowService;

	@Autowired
	private WorkflowFactorRateRequestDetailServiceImpl workflowFactorService; 

	private ModelMapper mapper;
	
	private Logger log = LogManager.getLogger(QuoteThreadServiceImpl.class);

	
	/**
	 * Creates a new quote proposal and logs the first workflow entry for the proposal.
	 * This method performs the following steps:
	 * <ol>
	 *     <li>Creates a new proposal based on the provided {@link QuoteProposalSaveReq} object.</li>
	 *     <li>If the proposal is successfully created, logs the first entry in the workflow using the 
	 *     {@link WorkflowService#firstTimeEntryInWorkflow(QuoteProposal)} method.</li>
	 * </ol>
	 * 
	 * This method is annotated with {@link Transactional} to ensure that the database operations are 
	 * rolled back if a {@link RuntimeException} occurs during the execution.
	 *
	 * @param req the {@link QuoteProposalSaveReq} object containing the details for creating a new proposal.
	 *            The request must contain the necessary information such as company ID, product ID, 
	 *            customer reference number, request reference number, and quote number.
	 * @throws RuntimeException if an unexpected error occurs during the process, which triggers a rollback.
	 * 
	 * @see WorkflowService#firstTimeEntryInWorkflow(QuoteProposal)
	 */
	@Transactional(rollbackOn = RuntimeException.class)
	public void createQuoteProposalAndFirstWorkflowEntry(QuoteProposalSaveReq req) {
		try {
			//Create new quote proposal
			QuoteProposal savedProposal = createNewProposal(req);
			 if(savedProposal == null) {
		        	throw new RuntimeException("Failed to create New Quote Proposal");
		        }
			 
	        // Create a record in WorkflowTracking
			WorkflowTracking workflowTracking = workflowService.firstTimeEntryInWorkflow(savedProposal);
			if(workflowTracking == null) {
	        	throw new RuntimeException("Failed to create NewQuote workflow entry");
			}
			
	        // create entries in workflow factor rate request for each action
	        List<WorkflowFactorRateRequestDetail> workflowFactorRateList = workflowFactorService.createEntryInWorkflowFactorRateForEachTaken(
	        		savedProposal.getRequestReferenceNo(), savedProposal.getProposalId(), workflowTracking.getWorkflowId());
	        if(workflowFactorRateList == null) {
	        	throw new RuntimeException("Failed to create entries in workflow factor rate request details");
	        }

		} catch (Exception e) {
			log.error("Exception : {}",e.getMessage(), e);
		}		
	}
	
	public QuoteProposal createNewProposal(QuoteProposalSaveReq req) {
		try {
			Optional<QuoteProposal> optProposal = proposalRepo
					.findByCompanyIdAndProductIdAndCustomerReferenceNoAndRequestReferenceNoAndQuoteNo(
					req.getCompanyId(), req.getProductId(), req.getCustomerReferenceNo(),
					req.getRequestReferenceNo(), req.getQuoteNo());
			
			if(optProposal.isEmpty()) {				
				QuoteProposal newProposal = mapper.map(req, QuoteProposal.class);
				newProposal.setProposalId(proposalIdGenerator(req.getCompanyId(), req.getProductId()));
				newProposal.setProposalStatus(STATUS_PROCESSING);
				newProposal.setCreatedOn(LocalDateTime.now());
				
				return proposalRepo.saveAndFlush(newProposal);
			}
			return null;
		} catch (Exception e) {
			log.error("Exception : {}", e.getMessage(), e);
			return null;
		}
		
	}
	
	private Long proposalIdGenerator(Integer companyId, Integer productId) {
		QuoteProposal maxIdProposal = proposalRepo.findTopByCompanyIdAndProductIdOrderByProposalIdDesc(
				companyId, productId);
		
		if(maxIdProposal != null) {
			long newId = maxIdProposal.getProposalId()+1L; 
			return newId;
		}
		else {return 1L;}
	}

}
