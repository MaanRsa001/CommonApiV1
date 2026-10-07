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
import com.maan.eway.master.req.MotorMakeChangeStatusReq;
import com.maan.eway.master.req.MotorMakeGetAllReq;
import com.maan.eway.master.req.MotorMakeGetReq;
import com.maan.eway.master.req.MotorMakeSaveReq;
import com.maan.eway.master.res.MotorMakeGetRes;
import com.maan.eway.master.service.MotorMakeMasterService;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;
import com.maan.eway.service.PrintReqService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;


@RestController
@Api(tags = "MASTER : Motor Make Master ", description = "API's")
@RequestMapping("/master")
public class MotorMakeMasterController {

	@Autowired
	private MotorMakeMasterService service;
	@Autowired
	private PrintReqService reqPrinter;
	

	@Autowired
	private FetchErrorDescServiceImpl errorDescService ;
	
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/savemakemotor")
	@ApiOperation(value = "This method is Save Make Motor ")

	public ResponseEntity<CommonRes> saveMakeMotor(@RequestBody MotorMakeSaveReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<String> validationCodes = service.validateMakeMotor(req);
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

			SuccessRes res = service.saveMakeMotor(req);
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
	@PostMapping("/getmakeid")
	@ApiOperation(value = "This method is get by Make Id ")

	public ResponseEntity<CommonRes> getMakeId(@RequestBody MotorMakeGetReq req) {
		CommonRes data = new CommonRes();

		MotorMakeGetRes res = service.getMakeId(req);
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
	@PostMapping("/getallmotormake")
	@ApiOperation(value = "This method is Get all Motor Make ")

	public ResponseEntity<CommonRes> getallMotorMake(@RequestBody MotorMakeGetAllReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();

		// Get All
		List<MotorMakeGetRes> res = service.getallMotorMake(req);
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
		@PostMapping("/getactivemotormake")
		@ApiOperation(value = "This method is Get Active Motor Make ")

		public ResponseEntity<CommonRes> getactiveMotorMake(@RequestBody MotorMakeGetAllReq req) {
			reqPrinter.reqPrint(req);
			CommonRes data = new CommonRes();

			// Get All
			List<MotorMakeGetRes> res = service.getactiveMotorMake(req);
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
		
		// Motor Make Drop Down Type
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
		@PostMapping(value="/dropdown/motormake",produces = "application/json")
		@ApiOperation(value = "This method is get Motor Make Master Drop Down")

		public ResponseEntity<DropdownCommonRes> getMotorMakeDropdown(@RequestBody MotorMakeGetAllReq req) {

			DropdownCommonRes data = new DropdownCommonRes();

			// Save
			List<DropDownRes> res = service.getMotorMakeDropdown(req);
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
		
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
		@PostMapping("/motormake/changestatus")
		@ApiOperation(value = "This method is Motor Make Change Status")
		public ResponseEntity<CommonRes> changeStatusOfMotorMake(@RequestBody MotorMakeChangeStatusReq req) {

			CommonRes data = new CommonRes();
			// Change Status
			SuccessRes res = service.changeStatusOfMotorMake(req);
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