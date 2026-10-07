/**
 * @author : Ashok Kumar S 
 * @since  : 23-12-2024
 */
package com.maan.eway.workstream.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.error.Error;
import com.maan.eway.workstream.request.WorkflowGetByLevelReq;
import com.maan.eway.workstream.request.WorkflowGetReq;
import com.maan.eway.workstream.response.WorkflowTrackingRes;
import com.maan.eway.workstream.serviceimpl.WorkflowTrackingServiceImpl;

@RestController
@RequestMapping("/workflow")
public class WorkflowTrackingController {
	
	private WorkflowTrackingServiceImpl workflowService;
	
	@Autowired
	public WorkflowTrackingController(WorkflowTrackingServiceImpl workflowService) {
		this.workflowService = workflowService;
	}

	/**
	 * This endpoint retrieves all workflows associated with a given proposal ID, company ID, product ID, and hierarchy level.
	 * It validates the incoming request parameters and returns a response with the corresponding workflow data filtered by the specified hierarchy level.
	 *
	 * @param req the request object containing the company ID, product ID, proposal ID, and hierarchy value
	 * @return a ResponseEntity containing the status and response details of the workflow retrieval
	 *         - HTTP Status 200 OK if the request is successful, with the workflows data in the response body
	 *         - HTTP Status 400 Bad Request if exception occurred while getting workflow data
	 *         - HTTP Status 422 Unprocessable Entity if validation fails, with error details in the response body
	 */
	@PostMapping("/fetchallbyproposalidandlevel")
	public ResponseEntity<?> getAllWorkflowByProposalIdAndLevel(@RequestBody WorkflowGetByLevelReq req){
		CommonRes response = new CommonRes();
		List<Error> errors = workflowService.validateParamatersOfWorkflowGetByLevelReq(req);
		if(!errors.isEmpty()) {
			response.setMessage("failed");
			response.setIsError(true);
			response.setErrorMessage(errors);
			return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
		}

		List<WorkflowTrackingRes> allWorkflows = workflowService.getAllWorkflowByProposalIdAndLevel(req.getCompanyId(),
				req.getProductId(), req.getProposalId(), req.getHierarchyValue());
				
		if(allWorkflows == null) {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
		
		response.setMessage("success");
		response.setIsError(false);
		response.setCommonResponse(allWorkflows);
		
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	

	/**
	 * This endpoint retrieves all workflows associated with a given proposal ID, company ID, and product ID.
	 * It validates the incoming request parameters and returns a response with the corresponding workflow data.
	 *
	 * @param req the request object containing the company ID, product ID, and proposal ID
	 * @return a ResponseEntity containing the status and response details of the workflow retrieval
	 *         - HTTP Status 200 OK if the request is successful, with the workflows data in the response body
	 *         - HTTP Status 400 Bad Request if exception occurred while getting workflow data
	 *         - HTTP Status 422 Unprocessable Entity if validation fails, with error details in the response body
	 */
	@PostMapping("/fetchallbyproposalid")
	public ResponseEntity<?> getAllWorkflowByProposalId(@RequestBody WorkflowGetReq req){
		CommonRes response = new CommonRes();
		List<Error> errors = workflowService.validateParamatersOfWorkflowGetReq(req);
		if(!errors.isEmpty()) {
			response.setMessage("failed");
			response.setIsError(true);
			response.setErrorMessage(errors);
			return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
		}

		List<WorkflowTrackingRes> allWorkflows = workflowService.getAllWorkflowByProposalId(req.getCompanyId(),
				req.getProductId(), req.getProposalId());
		
		if(allWorkflows == null) {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
		
		response.setMessage("success");
		response.setIsError(false);
		response.setCommonResponse(allWorkflows);
		
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
