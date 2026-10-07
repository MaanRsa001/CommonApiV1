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
import com.maan.eway.master.req.EndtDependantFieldChangeStatusReq;
import com.maan.eway.master.req.EndtDependantFieldMasterSaveReq;
import com.maan.eway.master.req.EndtDependantFieldsGetallReq;
import com.maan.eway.master.req.EndtDependantMasterGetReq;
import com.maan.eway.master.res.EndtDependantFieldsGetRes;
import com.maan.eway.master.service.EndtDependantFieldMasterService;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;
import com.maan.eway.service.PrintReqService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(tags = "MASTER :ENDT DEPENDANT FIELDS MASTER", description = "API'S")
@RequestMapping("/master")
public class EndtDependantFieldsMasterController {


	@Autowired
	private PrintReqService reqPrinter;

	@Autowired
	private EndtDependantFieldMasterService service;
	

	@Autowired
	private FetchErrorDescServiceImpl errorDescService ;

	// Save

	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_ADMIN')")
	@PostMapping("/savedependant")
	@ApiOperation(value = "This Method is to save Endorsement Dependant Fields")
	public ResponseEntity<CommonRes> saveDependantField(@RequestBody EndtDependantFieldMasterSaveReq req) {

		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		List<String> validationCodes = service.validateDependantField(req);
		List<Error> validation = null;
		if(validationCodes!=null && validationCodes.size() > 0 ) {
			CommonErrorModuleReq comErrDescReq = new CommonErrorModuleReq();
			comErrDescReq.setBranchCode("99999");
			comErrDescReq.setInsuranceId(req.getCompanyId());
			comErrDescReq.setProductId("99999");
			comErrDescReq.setModuleId("31");
			comErrDescReq.setModuleName("MASTERS");
			
			validation = errorDescService.getErrorDesc(validationCodes ,comErrDescReq);
		}

		// Validation
		if (validation != null && validation.size() != 0) {
			data.setCommonResponse(null);
			data.setErrorMessage(validation);
			data.setIsError(true);
			data.setMessage("Failed");
			return new ResponseEntity<CommonRes>(data, HttpStatus.OK);
		}

		// save
		else {
			SuccessRes res = service.saveDependantField(req);
			data.setCommonResponse(res);
			data.setErrorMessage(Collections.emptyList());
			data.setIsError(false);
			data.setMessage("Success");
			if (res != null) {
				return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
			} else {
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}
		}

	}

//  Get All Endorsement Dependant Field Master
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_ADMIN')")
	@PostMapping("/getalldependant")
	@ApiOperation("This method is getall Endorsement Dependant Fields")
	public ResponseEntity<CommonRes> getallDependantField(@RequestBody EndtDependantFieldsGetallReq req) {
		CommonRes data = new CommonRes();
		reqPrinter.reqPrint(req);

		List<EndtDependantFieldsGetRes> res = service.getallDependantField(req);
		data.setCommonResponse(res);
		data.setErrorMessage(Collections.emptyList());
		data.setIsError(false);
		data.setMessage("Success");

		if (res != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}

//  Get Active Endorsement Dependant Field  Master
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_ADMIN')")
	@PostMapping("/getactivedependant")
	@ApiOperation("This method is get Active Endorsement Dependant Fields")
	public ResponseEntity<CommonRes> getActiveDependantField(@RequestBody EndtDependantFieldsGetallReq req) {
		CommonRes data = new CommonRes();
		reqPrinter.reqPrint(req);

		List<EndtDependantFieldsGetRes> res = service.getActiveDependantField(req);
		data.setCommonResponse(res);
		data.setErrorMessage(Collections.emptyList());
		data.setIsError(false);
		data.setMessage("Success");

		if (res != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}

// Get By Endorsement Id
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_ADMIN')")
	@PostMapping("/getbydependantid")
	@ApiOperation("This Method is to get by Endorsement Dependant Field id")
	public ResponseEntity<CommonRes> getByDependantFieldId(@RequestBody EndtDependantMasterGetReq req) {
		CommonRes data = new CommonRes();
		EndtDependantFieldsGetRes res = service.getByDependantFieldId(req);
		data.setCommonResponse(res);
		data.setErrorMessage(Collections.emptyList());
		data.setIsError(false);
		data.setMessage("Success");

		if (res != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);

		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}

	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_ADMIN')")
	@PostMapping("/dependant/changestatus")
	@ApiOperation(value = "This method is get Endorsement Change Status")
	public ResponseEntity<CommonRes> changeStatusOfDependantField(@RequestBody EndtDependantFieldChangeStatusReq req) {

		CommonRes data = new CommonRes();
		// Change Status
		SuccessRes res = service.changeStatusOfDependantField(req);
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

//Endorsement Master Drop Down Type
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping(value = "/dropdown/dependant", produces = "application/json")
	@ApiOperation(value = "This method is get Endorsement Dependant Field Master Drop Down")

	public ResponseEntity<DropdownCommonRes> getDependantMasterDropdown(
			@RequestBody EndtDependantFieldsGetallReq req) {

		DropdownCommonRes data = new DropdownCommonRes();

		// Save
		List<DropDownRes> res = service.getDependantMasterDropdown(req);
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