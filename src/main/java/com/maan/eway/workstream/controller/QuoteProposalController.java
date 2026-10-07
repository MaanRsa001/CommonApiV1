/**
 * @author : Ashok Kumar S 
 * @since  : 23-12-2024
 */
package com.maan.eway.workstream.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.error.Error;
import com.maan.eway.workstream.request.ApproverGetReq;
import com.maan.eway.workstream.request.QuoteProposalActionReq;
import com.maan.eway.workstream.response.QuoteProposalRes;
import com.maan.eway.workstream.service.ApproverService;
import com.maan.eway.workstream.serviceimpl.QuoteProposalServiceImpl;


@RestController
@RequestMapping("/approver")
public class QuoteProposalController {
	private static final String ACTION_ASSIGNED = "AS";
	private static final String ACTION_APPROVED = "AP";
	private static final String ACTION_ESCALATED = "ES";
	private static final String ACTION_REJECTED = "RJ";
	private static final String ACTION_REVOKED = "RV";
	
	private QuoteProposalServiceImpl proposalService;
	private ApproverService approverService;

	@Autowired
	public QuoteProposalController(QuoteProposalServiceImpl proposalService, ApproverService approverService) {
		this.proposalService = proposalService;
		this.approverService = approverService;
	}

	/**
	 * This endpoint retrieves all incoming proposals for an approver based on the provided request parameters.
	 * It validates the incoming request and returns a response with the corresponding incoming proposals.
	 * 
	 * @param req the request object containing companyId, productId, loginId.
	 * @return a ResponseEntity containing the status and response details of the proposal retrieval
	 *         - HTTP Status 200 OK if proposals are successfully retrieved, with the list of incoming proposals
	 *         - HTTP Status 400 Bad Request if exception occurred while getting proposals
     *         - HTTP Status 422 Unprocessable Entity if validation fails, with error details in the response body

	 */
	@PostMapping("/incomingforeach")
	public ResponseEntity<?> incomingProposalsForEach(@RequestBody ApproverGetReq req){
		CommonRes response = new CommonRes();
		
		List<Error> errors = proposalService.validateParametersOfApproverGetReq(req);
		if(!errors.isEmpty()) {
			
			response.setMessage("failed");
			response.setIsError(true);
			response.setErrorMessage(errors);			
			return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
		}
		
		List<QuoteProposalRes> incomingProposals = proposalService.allIncomingProposals(req);
		if(incomingProposals == null) {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
		
		response.setMessage("success");
		response.setIsError(false);
		response.setCommonResponse(incomingProposals);
		
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	/**
	 * This endpoint retrieves all proposals assigned to each approver based on the provided request parameters.
	 * 
	 * @param req the request object containing companyId, productId, loginId
	 * @return a ResponseEntity containing the result of the operation:
	 *         - HTTP Status 200 OK if proposals are successfully retrieved, with the list of assigned proposals
	 *         - HTTP Status 422 Unprocessable Entity if validation fails, with error details in the response body
	 *         - HTTP Status 400 Bad Request if exception occurred while getting proposals
	 */
	@PostMapping("/assignedforeach")
	public ResponseEntity<?> assignedForEach(@RequestBody ApproverGetReq req){
		CommonRes response = new CommonRes();
		
		List<Error> errors = proposalService.validateParametersOfApproverGetReq(req);
		if(!errors.isEmpty()) {
			response.setMessage("failed");
			response.setIsError(true);
			response.setErrorMessage(errors);
						
			return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
		}
		List<QuoteProposalRes> assignedProposals = proposalService.assignedForEach(req);
		if(assignedProposals == null) {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
		response.setMessage("success");
		response.setIsError(false);
		response.setCommonResponse(assignedProposals);
		
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	
	/**
	 * This endpoint allows an approver to take specific actions on a proposal.
	 * Actions include assigning, approving, revoking, rejecting, or escalating a proposal.
	 * 
	 * <p>The method performs validation on the incoming request and ensures the approver has the appropriate authority 
	 * to take the specified action. The action is recorded if valid. Relevant success or error responses are returned 
	 * based on the outcome of the process.</p>
	 * 
	 * @param req the request object containing the details for taking action on a proposal:
	 *            - `companyId`: The ID of the company
	 *            - `productId`: The ID of the product
	 *            - `proposalId`: The ID of the proposal
	 *            - `loginId`: The login ID of the approver
	 *            - `actionTaken`: The action to be performed (e.g., ASSIGNED, APPROVED, REVOKED, REJECTED, ESCALATED)
	 * @return a ResponseEntity containing the result of the action:
	 *         - HTTP Status 200 OK if the action is successfully performed, with relevant success details
	 *         - HTTP Status 422 Unprocessable Entity if validation fails, with error details in the response body
	 *         - HTTP Status 400 Bad Request if none of the specified criteria are met
	 */
	@PostMapping("/takeaction")
	public ResponseEntity<?> takeActionOnProposal(@RequestBody QuoteProposalActionReq req){
		CommonRes response = new CommonRes();
		List<Error> errors = proposalService.validateParametersOfProposalActionReq(req);
		if(!errors.isEmpty()) {
			response.setMessage("failed");
			response.setIsError(true);
			response.setErrorMessage(errors);
			return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
		}
		
		if(ACTION_ASSIGNED.equals(req.getActionTaken())) {
			//Verify This Proposal is Incoming to Himself
			boolean verifyAuthority = proposalService.verifyTheProposalIsIncomingForApprover(req);
			if(!verifyAuthority) {
				response.setMessage("failed");
				response.setIsError(true);
				response.setErrorMessage(
						List.of(new Error("1","Message","This Quotation is Assigned By Someone"))
						);
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
			
			//Action on Proposal
			boolean actionAssigned = proposalService.recordingActionOnProposal(req);
			if(actionAssigned) {
				response.setMessage("success");
				response.setIsError(false);				
				response.setCommonResponse(
						Map.of("Message", "Quotation is ASSIGNED by " + req.getLoginId())
						);				
				return new ResponseEntity<>(response, HttpStatus.OK);
			}
		}
		else {
			//Verify This Proposal is Assigned to himself
			boolean belongsToHim = proposalService.verifyProposalAssignedToHimSelf(req.getCompanyId(), 
					req.getProductId(), req.getProposalId(), req.getLoginId());
			if(!belongsToHim) {
				response.setMessage("failed");
				response.setIsError(true);
				response.setErrorMessage(
						List.of(new Error("1","Message","This Quotation Is Not Assigned To Yourself"))
						);

				return new ResponseEntity<>(response, HttpStatus.OK);
			}			

			
			//Action on proposal
			if(ACTION_REVOKED.equals(req.getActionTaken())) {
				boolean actionRevoked = proposalService.recordingActionOnProposal(req);
				if(actionRevoked) {
					response.setMessage("success");
					response.setIsError(false);				
					response.setCommonResponse(
							Map.of("Message", "Quotation is REVOKED by " + req.getLoginId())
							);					
					return new ResponseEntity<>(response, HttpStatus.OK);
				}
			}
			if(ACTION_APPROVED.equals(req.getActionTaken())) {
				boolean actionApproved = proposalService.recordingActionOnProposal(req);
				if(actionApproved) {
					response.setMessage("success");
					response.setIsError(false);				
					response.setCommonResponse(
							Map.of("Message", "Quotation is APPROVED by " + req.getLoginId())
							);					
					return new ResponseEntity<>(response, HttpStatus.OK);
				}
			}
			if(ACTION_REJECTED.equals(req.getActionTaken())) {
				boolean actionRejected = proposalService.recordingActionOnProposal(req);
				if(actionRejected) {
					response.setMessage("success");
					response.setIsError(false);				
					response.setCommonResponse(
							Map.of("Message", "Quotation is REJECTED by " + req.getLoginId())
							);					
					return new ResponseEntity<>(response, HttpStatus.OK);
				}
			}
			if(ACTION_ESCALATED.equals(req.getActionTaken())) {
		        // Verify if the user has authority to Escalate the proposal
				boolean authorityToEscalate = approverService.verifyAuthorityToEscalate(req.getCompanyId(), 
						req.getProductId(), req.getLoginId());
				if(!authorityToEscalate) {
					response.setMessage("failed");
					response.setIsError(true);
					response.setErrorMessage(
							List.of(new Error("1","Message","You Don't Have Access To Escalate"))
							);
					return new ResponseEntity<>(response, HttpStatus.OK);
				}
				
				boolean actionEscalated = proposalService.recordingActionOnProposal(req);
				if(actionEscalated) {
					response.setMessage("success");
					response.setIsError(false);				
					response.setCommonResponse(
							Map.of("Message", "Quotation is ESCALATED by " + req.getLoginId())
							);					
					return new ResponseEntity<>(response, HttpStatus.OK);
				}
			}
		}
		
		//None of above criteria met
		return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
	}

	
	/**
	 * This endpoint retrieves all proposals that have been approved for a given approver based on the provided request parameters.
	 * It validates the incoming request and returns a response containing the approved proposals.
	 * 
	 * @param req the request object containing companyId, productId, loginId
	 * @return a ResponseEntity containing the status and response details of the proposal retrieval
	 *         - HTTP Status 200 OK if the request is successful, with the approved proposals data in the response body
	 *         - HTTP Status 400 Bad Request if exception occurred while getting proposals.
	 *         - HTTP Status 422 Unprocessable Entity if validation fails, with error details in the response body
	 */
	@PostMapping("/approvedforeach")
	public ResponseEntity<?> approvedForEach(@RequestBody ApproverGetReq req){
		CommonRes response = new CommonRes();
		
		List<Error> errors = proposalService.validateParametersOfApproverGetReq(req);
		if(!errors.isEmpty()) {
			
			response.setMessage("failed");
			response.setIsError(true);
			response.setErrorMessage(errors);
			return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
		}
		List<QuoteProposalRes> approvedProposals = proposalService.actionTakenByEachExceptAssigned(
				req.getCompanyId(), req.getProductId(), req.getLoginId(), ACTION_APPROVED);
		if(approvedProposals == null) {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
		
		response.setMessage("success");
		response.setIsError(false);
		response.setCommonResponse(approvedProposals);
		
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	
	/**
	 * This endpoint retrieves all proposals that have been rejected for a given approver based on the provided request parameters.
	 * It validates the incoming request and returns a response containing the rejected proposals.
	 * 
	 * @param req the request object containing companyId, productId, loginId
	 * @return a ResponseEntity containing the status and response details of the proposal retrieval
	 *         - HTTP Status 200 OK if the request is successful, with the rejected proposals data in the response body
	 *         - HTTP Status 400 Bad Request if exception occurred while getting proposals.
	 *         - HTTP Status 422 Unprocessable Entity if validation fails, with error details in the response body
	 */
	@PostMapping("/rejectedforeach")
	public ResponseEntity<?> rejectedForEach(@RequestBody ApproverGetReq req){
		CommonRes response = new CommonRes();
		
		List<Error> errors = proposalService.validateParametersOfApproverGetReq(req);
		if(!errors.isEmpty()) {
			
			response.setMessage("failed");
			response.setIsError(true);
			response.setErrorMessage(errors);
			return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
		}
		List<QuoteProposalRes> rejectedProposals = proposalService.actionTakenByEachExceptAssigned(
				req.getCompanyId(), req.getProductId(), req.getLoginId(), ACTION_REJECTED);
		if(rejectedProposals == null) {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
		
		response.setMessage("success");
		response.setIsError(false);
		response.setCommonResponse(rejectedProposals);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	
	
	/**
	 * This endpoint retrieves all proposals that have been revoked for a given approver based on the provided request parameters.
	 * It validates the incoming request and returns a response containing the revoked proposals.
	 * 
	 * @param req the request object containing companyId, productId, loginId
	 * @return a ResponseEntity containing the status and response details of the proposal retrieval
	 *         - HTTP Status 200 OK if the request is successful, with the revoked proposals data in the response body
	 *         - HTTP Status 400 Bad Request if exception occurred while getting proposals
	 *         - HTTP Status 422 Unprocessable Entity if validation fails, with error details in the response body
	 */
	@PostMapping("/revokedforeach")
	public ResponseEntity<?> revokedForEach(@RequestBody ApproverGetReq req){
		CommonRes response = new CommonRes();
		
		List<Error> errors = proposalService.validateParametersOfApproverGetReq(req);
		if(!errors.isEmpty()) {
			
			response.setMessage("failed");
			response.setIsError(true);
			response.setErrorMessage(errors);
			return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
		}
		List<QuoteProposalRes> revokedProposals = proposalService.actionTakenByEachExceptAssigned(
				req.getCompanyId(), req.getProductId(), req.getLoginId(), ACTION_REVOKED);
		if(revokedProposals == null) {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
		
		response.setMessage("success");
		response.setIsError(false);
		response.setCommonResponse(revokedProposals);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	
	/**
	 * This endpoint retrieves all proposals that have been escalated for a given approver based on the provided request parameters.
	 * It validates the incoming request and returns a response containing the escalated proposals.
	 * 
	 * @param req the request object containing companyId, productId, loginId
	 * @return a ResponseEntity containing the status and response details of the proposal retrieval
	 *         - HTTP Status 200 OK if the request is successful, with the escalated proposals data in the response body
	 *         - HTTP Status 400 Bad Request if exception occurred while getting proposals
	 *         - HTTP Status 422 Unprocessable Entity if validation fails, with error details in the response body
	 */
	@PostMapping("/escalatedforeach")
	public ResponseEntity<?> escalatedForEach(@RequestBody ApproverGetReq req){
		CommonRes response = new CommonRes();
		
		List<Error> errors = proposalService.validateParametersOfApproverGetReq(req);
		if(!errors.isEmpty()) {
			
			response.setMessage("failed");
			response.setIsError(true);
			response.setErrorMessage(errors);			
			return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
		}

		// Fetch proposals escalated for each approver
		List<QuoteProposalRes> escalatedProposals = proposalService.actionTakenByEachExceptAssigned(
				req.getCompanyId(), req.getProductId(), req.getLoginId(), ACTION_ESCALATED);
		
		if(escalatedProposals == null) {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
		
		response.setMessage("success");
		response.setIsError(false);
		response.setCommonResponse(escalatedProposals);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	

}
