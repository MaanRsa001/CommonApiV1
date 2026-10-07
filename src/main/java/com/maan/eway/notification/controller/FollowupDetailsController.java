package com.maan.eway.notification.controller;

import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.error.Error;
import com.maan.eway.notification.req.FollowupDetailsGetReq;
import com.maan.eway.notification.req.FollowupDetailsGetallReq;
import com.maan.eway.notification.req.FollowupDetailsSaveReq;
import com.maan.eway.notification.res.FollowUpDetailsPageRes;
import com.maan.eway.notification.res.FollowUpDetailsRes;
import com.maan.eway.notification.service.FollowupDetailsService;
import com.maan.eway.res.SuccessRes;
import com.maan.eway.service.PrintReqService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(tags = "FOLLOWUP : FollowUp Details ", description = "API's")
@RequestMapping("/api")
public class FollowupDetailsController {


	@Autowired
	private PrintReqService reqPrinter;
	
	
	@Autowired
	private FollowupDetailsService service;

	// Save list of Follow Up Details
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/savefollowup")
	@ApiOperation(value = "This method is to Save FollowUp Details")
	public ResponseEntity<CommonRes> savefollowup(@RequestBody FollowupDetailsSaveReq req) {

		reqPrinter.reqPrint("Printer Request --->" + req);
		CommonRes data = new CommonRes();

		// Validation
		List<Error> validation = service.validateFollowupDetails(req);
		if (validation != null && validation.size() != 0) {

			data.setCommonResponse(null);
			data.setIsError(true);
			data.setErrorMessage(validation);
			data.setMessage("Failed");

			return new ResponseEntity<CommonRes>(data, HttpStatus.OK);

		} else {
			// Save
			SuccessRes res = service.saveFollowupDetails(req);
			data.setCommonResponse(res);
			data.setIsError(false);
			data.setErrorMessage(Collections.emptyList());
			data.setMessage("Success");

			if (res != null) {
				return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
			} else {
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}
		}
	}
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/getfollowupdetailsid")
	@ApiOperation(value = "This method is to Get FollowUp Details")
	public ResponseEntity<CommonRes> getclientdetailsid(@RequestBody FollowupDetailsGetReq req) {

		reqPrinter.reqPrint("Printer Request --->" + req);
		CommonRes data = new CommonRes();

		// Save
		FollowUpDetailsRes res = service.getclientdetailsid(req);
		data.setCommonResponse(res);
		data.setIsError(false);
		data.setErrorMessage(Collections.emptyList());
		data.setMessage("Success");

		if (res != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}

	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/getallfollowupdetails")
	@ApiOperation(value = "This method is to Get FollowUp Details List")
	public ResponseEntity<CommonRes> getclientdetails(@RequestBody FollowupDetailsGetallReq req) {

		reqPrinter.reqPrint("Printer Request --->" + req);
		CommonRes data = new CommonRes();

		// Save
		FollowUpDetailsPageRes res = service.getfollowupDetails(req);
		data.setCommonResponse(res);
		data.setIsError(false);
		data.setErrorMessage(Collections.emptyList());
		data.setMessage("Success");

		if (res != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}

	
	
	
}
