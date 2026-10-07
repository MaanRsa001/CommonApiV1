
package com.maan.eway.master.controller;
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

import com.maan.eway.common.req.CommonErrorModuleReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.DropdownCommonRes;
import com.maan.eway.common.service.impl.FetchErrorDescServiceImpl;
import com.maan.eway.error.Error;
import com.maan.eway.master.req.ColorChangeStatusReq;
import com.maan.eway.master.req.MotorColorGetAllReq;
import com.maan.eway.master.req.MotorColorGetReq;
import com.maan.eway.master.req.MotorColorSaveReq;
import com.maan.eway.master.res.MotorColorGetRes;
import com.maan.eway.master.service.MotorColorMasterService;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;
import com.maan.eway.service.PrintReqService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


@RestController
@Api(tags = "MASTER :Motor Color Master ", description = "API's")
@RequestMapping("/master")
public class MotorColorMasterController {

	@Autowired
	private MotorColorMasterService service;
	@Autowired
	private PrintReqService reqPrinter;
	
	
	@Autowired
	private FetchErrorDescServiceImpl errorDescService ;
	
	// Insert
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/savemotorcolor")
	@ApiOperation(value = "This method is Save Make Motor ")

	public ResponseEntity<CommonRes> saveColor(@RequestBody MotorColorSaveReq req) {
		
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<String> validationCodes = service.validateColorMotor(req);
		List<Error> validation = null;
		if(validationCodes!=null && validationCodes.size() > 0 ) {
			CommonErrorModuleReq comErrDescReq = new CommonErrorModuleReq();
			comErrDescReq.setBranchCode(req.getBranchCode());
			comErrDescReq.setInsuranceId(req.getInsuranceId());
			comErrDescReq.setProductId("99999");
			comErrDescReq.setModuleId("31");
			comErrDescReq.setModuleName("MASTERS");
			
			validation = errorDescService.getErrorDesc(validationCodes ,comErrDescReq);
		}
	
		
		//// validation
		if (validation != null && validation.size() != 0) {
			data.setCommonResponse(null);
			data.setIsError(true);
			data.setErrorMessage(validation);
			data.setMessage("Failed");
			return new ResponseEntity<CommonRes>(data, HttpStatus.OK);

		} else {
			/////// save

			SuccessRes res = service.saveColor(req);
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

	// Get By Make Id
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/getmotorcolor")
	@ApiOperation(value = "This method is get by Motor Color")

	public ResponseEntity<CommonRes> getMotorColor(@RequestBody MotorColorGetReq req) {
		CommonRes data = new CommonRes();

		MotorColorGetRes res = service.getMotorColor(req);
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

	// Get All
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/getallmotorcolor")
	@ApiOperation(value = "This method is Get all Motor Color ")

	public ResponseEntity<CommonRes> getallMotorColor(@RequestBody MotorColorGetAllReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();

		// Get All
		List<MotorColorGetRes> res = service.getallMotorColor(req);
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

	
	// Get All
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
		@PostMapping("/getactivemotorcolor")
		@ApiOperation(value = "This method is Get Active Motor Color ")

		public ResponseEntity<CommonRes> getactiveMotorColor(@RequestBody MotorColorGetAllReq req) {
			reqPrinter.reqPrint(req);
			CommonRes data = new CommonRes();

			// Get All
			List<MotorColorGetRes> res = service.getactiveMotorColor(req);
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
		@PostMapping("/color/changestatus")
		@ApiOperation(value = "This method is get Color Change Status")
		public ResponseEntity<CommonRes> changeStatusOfColor(@RequestBody ColorChangeStatusReq req) {

			CommonRes data = new CommonRes();
			// Change Status
			SuccessRes res = service.changeStatusOfColor(req);
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
	

		// Color Master Drop Down Type
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
		@PostMapping(value="/dropdown/color",produces = "application/json")
		@ApiOperation(value = "This method is get Color Master Drop Down")

		public ResponseEntity<DropdownCommonRes> getColorMasterDropdown(@RequestBody MotorColorGetAllReq req) {

			DropdownCommonRes data = new DropdownCommonRes();

			// Save
			List<DropDownRes> res = service.getColorMasterDropdown(req);
			data.setCommonResponse(res);
			data.setIsError(false);
			data.setErrorMessage(Collections.emptyList());
			data.setMessage("Success");

			if (res != null) {
				return new ResponseEntity<DropdownCommonRes>(data, HttpStatus.CREATED);
			} else {
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}

		}



	 }
