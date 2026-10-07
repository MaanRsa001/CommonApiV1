/**
 * @author : Ashok Kumar S 
 * @since  : 28-12-2024
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
import com.maan.eway.workstream.request.ReferralActionDropDownGetReq;
import com.maan.eway.workstream.response.ReferralActionDropDownRes;
import com.maan.eway.workstream.serviceimpl.ReferralActionDropDownServiceImpl;

@RestController
@RequestMapping("/dropdown")
public class ReferralActionDropDownController {
	
	private ReferralActionDropDownServiceImpl actionDropDownService;
	
	@Autowired
	public ReferralActionDropDownController(ReferralActionDropDownServiceImpl actionDropDownService) {
		this.actionDropDownService = actionDropDownService;
	}
	
	
	/**
	 * Retrieves the referral action dropdown options based on the provided request parameters.
	 * 
	 * @param req the request object containing companyId, branchCode.
	 * @return a ResponseEntity containing the result of the operation:
	 *         - HTTP Status 200 OK if the dropdown data is successfully retrieved, with the data in the response body.
	 *         - HTTP Status 422 Unprocessable Entity if validation fails, with error details in the response body.
	 *         - HTTP Status 400 Bad Request exception occurred while getting data
	 */
	@PostMapping("/referralactiondropdown")
	public ResponseEntity<CommonRes> getReferralActionDropDown(@RequestBody ReferralActionDropDownGetReq req){
		CommonRes response = new CommonRes();
		List<Error> errors = actionDropDownService.validateReferralActionDropDownGetReq(req);
	
		if(!errors.isEmpty()) {
			response.setMessage("failed");
			response.setIsError(true);
			response.setErrorMessage(errors);
			
			return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);		
		}
		
		List<ReferralActionDropDownRes> dropDown = actionDropDownService.getReferralActionDropDown(req);
		
		if(dropDown == null) {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
		
		response.setMessage("success");
		response.setIsError(false);
		response.setCommonResponse(dropDown);		
		return new ResponseEntity<>(response, HttpStatus.OK);		
	}

}
