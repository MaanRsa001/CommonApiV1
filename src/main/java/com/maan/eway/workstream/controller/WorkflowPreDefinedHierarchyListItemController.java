/**
 * @author : Ashok Kumar S 
 * @since  : 09-01-2025
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
import com.maan.eway.workstream.request.PreDefinedHierarchyGetReq;
import com.maan.eway.workstream.response.ListItemValueRes;
import com.maan.eway.workstream.serviceimpl.WorkflowPreDefinedHierarchyListItemServiceImpl;

@RestController
@RequestMapping("/listitem")
public class WorkflowPreDefinedHierarchyListItemController {
	
	private WorkflowPreDefinedHierarchyListItemServiceImpl preDefinedHierarchyService;
	
	@Autowired
	public WorkflowPreDefinedHierarchyListItemController(
			WorkflowPreDefinedHierarchyListItemServiceImpl preDefinedHierarchyService) {
		this.preDefinedHierarchyService = preDefinedHierarchyService;
	}
	
	/**
	 * Retrieves the pre-defined workflow hierarchy based on the provided request parameters.
	 *
	 * @param req the request object containing companyId, branchCode.
	 * @return a ResponseEntity containing the result of the operation:
	 *         - HTTP Status 200 OK if the hierarchy data is successfully retrieved, with the data in the response body.
	 *         - HTTP Status 422 Unprocessable Entity if validation fails, with error details in the response body.
	 *         - HTTP Status 400 Bad Request exception occurred while getting data
	 */
	@PostMapping("/workflowpredefinedhierarchy")
	public ResponseEntity<CommonRes> getPreDefinedHierarchy (@RequestBody PreDefinedHierarchyGetReq req){
		CommonRes response = new CommonRes();
		List<Error> errors = preDefinedHierarchyService.validateParametersOfPreDefinedHierarchyReq(req);
	
		if(!errors.isEmpty()) {
			response.setMessage("failed");
			response.setIsError(true);
			response.setErrorMessage(errors);
			
			return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);		
		}
		
		List<ListItemValueRes> hierarchy = preDefinedHierarchyService.getPreDefinedHierarchy(req);
		if(hierarchy == null) {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
		
		response.setMessage("success");
		response.setIsError(false);
		response.setCommonResponse(hierarchy);		
		return new ResponseEntity<>(response, HttpStatus.OK);		
	}

}
