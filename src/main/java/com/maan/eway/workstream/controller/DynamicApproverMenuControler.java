/**
 * @author : Ashok Kumar S 
 * @since  : 26-02-2025
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
import com.maan.eway.workstream.request.ApproverGetReq;
import com.maan.eway.workstream.response.ListItemValueRes;
import com.maan.eway.workstream.serviceimpl.DynamicApproverMenuService;

@RestController
@RequestMapping("/approver")
public class DynamicApproverMenuControler {
	private DynamicApproverMenuService dynamicApproverMenuService;
	
	@Autowired
	public DynamicApproverMenuControler(DynamicApproverMenuService dynamicApproverMenuService) {
		this.dynamicApproverMenuService = dynamicApproverMenuService;
	}


	/**
	 * Retrieves a dynamic approver menu based on the provided request parameters.
	 * <p>
	 * This API validates the request parameters and fetches the approver menu dynamically.  
	 * If validation fails, an error response is returned. 
	 * If any exception occurred null is returned
	 * Otherwise, the retrieved menu data is returned.
	 * </p>
	 *
	 * @param req the {@link ApproverGetReq} containing the request parameters.
	 * @return a {@link ResponseEntity} containing a {@link CommonRes} object with either the menu data or an error message.
	 */
	@PostMapping("/menubasedonlevel")
	public ResponseEntity<CommonRes> getDynamicApproverMenu(@RequestBody ApproverGetReq req){
		CommonRes response = new CommonRes();

		// Validate request parameters
		List<Error> errors = dynamicApproverMenuService.validateParametersOfApproverGetReq(req);
		if(!errors.isEmpty()) {
			response.setMessage("Validation failed.");
			response.setIsError(true);
			response.setErrorMessage(errors);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
		
	    // Fetch approver menu
		List<ListItemValueRes> approverMenu = dynamicApproverMenuService.getDynamicApproverMenu(req);
		if(approverMenu == null) {
			return new ResponseEntity<>(null, HttpStatus.OK);
		}
		
	    // Return successful response
		response.setMessage("Data retrieved successfully.");
		response.setIsError(false);
		response.setCommonResponse(approverMenu);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
	

}
