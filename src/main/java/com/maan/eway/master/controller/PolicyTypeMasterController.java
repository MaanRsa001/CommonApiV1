package com.maan.eway.master.controller;

import java.util.Collections;
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
import com.maan.eway.master.req.PolicyTypeMasterGetAllReq;
import com.maan.eway.master.req.PolicyTypeMasterGetReq;
import com.maan.eway.master.req.PolicyTypeMasterSaveReq;
import com.maan.eway.master.res.PolicyTypeMasterGetRes;
import com.maan.eway.master.service.PolicyTypeMasterService;
import com.maan.eway.res.SuccessRes;
import com.maan.eway.service.PrintReqService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(tags="MASTER : Policy Type Master", description = "API's")
@RequestMapping("/master")
public class PolicyTypeMasterController {

	@Autowired
	private PolicyTypeMasterService service;
	
	@Autowired
	private PrintReqService reqPrinter;
	
	@PostMapping("/insertpolicytype")
	@ApiOperation("This method is to save Policy Type Master")
	public ResponseEntity<CommonRes> insertPolicyType(@RequestBody PolicyTypeMasterSaveReq req){
		CommonRes data = new CommonRes();
		reqPrinter.reqPrint(req);
		
		List<Error> validation = service.validatePolicyType(req);
		//Validation
		if(validation!=null && validation.size()!=0) {
		data.setCommonResponse(null);
		data.setErrorMessage(validation);
		data.setMessage("Failed");
		return new ResponseEntity<CommonRes>(data, HttpStatus.OK);

		}
		else {
		SuccessRes res = service.insertPolicyType(req);
		data.setCommonResponse(res);
		data.setErrorMessage(Collections.emptyList());
		data.setIsError(false);
		data.setMessage("Success");
		
		if(res!=null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		}
		else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
		
		}
	}
	
	@PostMapping("/getpolicytype")
	@ApiOperation("This method is to get policy type")
	public ResponseEntity<CommonRes> getPolicyType(@RequestBody PolicyTypeMasterGetReq req){
		CommonRes data = new CommonRes();
		reqPrinter.reqPrint(req);
		PolicyTypeMasterGetRes res = service.getPolicyType(req);
		data.setCommonResponse(res);
		data.setErrorMessage(Collections.emptyList());
		data.setIsError(false);
		data.setMessage("Success");
		if(res!=null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		}
		else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	

	@PostMapping("/getallpolicytype")
	@ApiOperation("This method is to get all Policy Type Master")
	public ResponseEntity<CommonRes>getallPolicyType(@RequestBody PolicyTypeMasterGetAllReq req){
		CommonRes data = new CommonRes();
		reqPrinter.reqPrint(req);
		List<PolicyTypeMasterGetRes> res = service.getallPolicyType(req);
		data.setCommonResponse(res);
		data.setErrorMessage(Collections.emptyList());
		data.setIsError(false);
		data.setMessage("Success");
		if(res!=null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		}
		else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	
	@PostMapping("/getactivepolicytype")
	@ApiOperation("This method is to get all Active Policy Type Master")
	public ResponseEntity<CommonRes>getallactivePolicyType(@RequestBody PolicyTypeMasterGetAllReq req)
	{
		CommonRes data = new CommonRes();
		reqPrinter.reqPrint(req);
		List<PolicyTypeMasterGetRes> res = service.getallactivePolicyType(req);
		data.setCommonResponse(res);
		data.setErrorMessage(Collections.emptyList());
		data.setIsError(false);
		data.setMessage("Success");
		if(res!=null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		}
		else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	
	// Policy Type Master Drop Down Type
//	@GetMapping("/dropdown/policytype")
//	@ApiOperation(value = "This method is get Policy Type Master Drop Down")
//
//	public ResponseEntity<CommonRes> getPolicyTypeMasterDropdown() {
//
//		CommonRes data = new CommonRes();
//
//		// Save
//		List<DropDownRes> res = service.getPolicyTypeMasterDropdown();
//		data.setCommonResponse(res);
//		data.setIsError(false);
//		data.setErrorMessage(Collections.emptyList());
//		data.setMessage("Success");
//
//		if (res != null) {
//			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
//		} else {
//			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
//		}
//
//	}


}
