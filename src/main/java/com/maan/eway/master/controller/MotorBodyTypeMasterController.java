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
import com.maan.eway.master.req.BodyTypeChangeStatusReq;
import com.maan.eway.master.req.BodyTypeDropDownReq;
import com.maan.eway.master.req.MotorBodySaveReq;
import com.maan.eway.master.req.MotorBodyTypeGetAllReq;
import com.maan.eway.master.req.MotorBodyTypeGetReq;
import com.maan.eway.master.res.MotorBodyTypeGetRes;
import com.maan.eway.master.service.MotorBodyTypeMasterService;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;
import com.maan.eway.service.PrintReqService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(tags = "MASTER : Motor Body Type Master ", description = "API's")
@RequestMapping("/master")
public class MotorBodyTypeMasterController {

	@Autowired
	private MotorBodyTypeMasterService service;
	@Autowired
	private PrintReqService reqPrinter;
	
	
	@Autowired
	private FetchErrorDescServiceImpl errorDescService ;
	
	// Insert
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/savemotorbodytype")
	@ApiOperation(value = "This method is Save Make Motor ")

	public ResponseEntity<CommonRes> saveMakeMotor(@RequestBody MotorBodySaveReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<String> validationCodes = service.validateMakeMotor(req);
		List<Error> validation = null;
		if(validationCodes!=null && validationCodes.size() > 0 ) {
			CommonErrorModuleReq comErrDescReq = new CommonErrorModuleReq();
			comErrDescReq.setBranchCode(req.getBranchCode());
			comErrDescReq.setInsuranceId(req.getCompanyId());
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
	@PostMapping("/getmotorbody")
	@ApiOperation(value = "This method is get by Make Id ")

	public ResponseEntity<CommonRes> getMotorBody(@RequestBody MotorBodyTypeGetReq req) {
		CommonRes data = new CommonRes();

		MotorBodyTypeGetRes res = service.getMotorBody(req);
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
	@PostMapping("/getallmotorbody")
	@ApiOperation(value = "This method is Get all Motor Make ")

	public ResponseEntity<CommonRes> getallMotorBody(@RequestBody MotorBodyTypeGetAllReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();

		// Get All
		List<MotorBodyTypeGetRes> res = service.getallMotorBody(req);
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

	
	// Get All Active
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
		@PostMapping("/getactivemotorbody")
		@ApiOperation(value = "This method is Get Active Motor Body ")

		public ResponseEntity<CommonRes> getactiveMotorBody(@RequestBody MotorBodyTypeGetAllReq req) {
			reqPrinter.reqPrint(req);
			CommonRes data = new CommonRes();

			// Get All
			List<MotorBodyTypeGetRes> res = service.getactiveMotorBody(req);
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
		@PostMapping("/bodytype/changestatus")
		@ApiOperation(value = "This method is get Body Type Change Status")
		public ResponseEntity<CommonRes> changeStatusOfBodyType(@RequestBody BodyTypeChangeStatusReq req) {

			CommonRes data = new CommonRes();
			// Change Status
			SuccessRes res = service.changeStatusOfBodyType(req);
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
	

		// Body Type Master Drop Down Type
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
		@PostMapping(value="/dropdown/bodytype",produces = "application/json")
		@ApiOperation(value = "This method is get Body Type Drop Down")

		public ResponseEntity<DropdownCommonRes> getBodyTypeMasterDropdown(@RequestBody BodyTypeDropDownReq req) {

			DropdownCommonRes data = new DropdownCommonRes();

			// Save
			List<DropDownRes> res = service.getBodyTypeMasterDropdown(req);
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
		
		// Body Type Master Drop Down Type
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
		@PostMapping("/dropdown/induvidual/bodytype")
		@ApiOperation(value = "This method is get Body Type Drop Down")

		public ResponseEntity<DropdownCommonRes> getInduvidualBodyTypeMasterDropdown(@RequestBody BodyTypeDropDownReq req) {

			DropdownCommonRes data = new DropdownCommonRes();

			// Save
			List<DropDownRes> res = service.getInduvidualBodyTypeMasterDropdown(req);
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