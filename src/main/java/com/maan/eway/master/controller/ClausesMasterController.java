package com.maan.eway.master.controller;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maan.eway.common.req.CommonErrorModuleReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.DropdownCommonRes;
import com.maan.eway.common.service.impl.FetchErrorDescServiceImpl;
import com.maan.eway.error.Error;
import com.maan.eway.master.req.ClausesChangeStatusReq;
import com.maan.eway.master.req.ClausesMasterDropdownReq;
import com.maan.eway.master.req.ClausesMasterGetReq;
import com.maan.eway.master.req.ClausesMasterGetallReq;
import com.maan.eway.master.req.ClausesMasterReq;
import com.maan.eway.master.req.ClausesMasterSaveReq;
import com.maan.eway.master.req.NonSelectedClausesGetAllReq;
import com.maan.eway.master.res.ClausesMasterRes;
import com.maan.eway.master.service.ClausesMasterService;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;
import com.maan.eway.service.PrintReqService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
@RestController
@Api(tags="MASTER : Clauses MASTER", description="API's")
@RequestMapping("/master")
public class ClausesMasterController {

@Autowired
private ClausesMasterService service;

@Autowired
private PrintReqService reqPrinter;

@Autowired
private FetchErrorDescServiceImpl errorDescService ;

//Save
@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
@PostMapping("/insertclauses")
@ApiOperation(value = "This Method is to save Waranty Master")
public ResponseEntity<CommonRes> saveClauses(@RequestParam(value= "documentFile", required = false) MultipartFile documentFile,
			@RequestParam("Req") String jsonReq) throws IOException {
	
	ClausesMasterSaveReq req = new ObjectMapper().readValue(jsonReq, ClausesMasterSaveReq.class);
	
	reqPrinter.reqPrint(req);
	CommonRes data = new CommonRes();
	List<String> validationCodes = service.validateClauses(req);
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

	//validation
	if (validation != null && validation.size() != 0) {
		data.setCommonResponse(null);
		data.setIsError(true);
		data.setErrorMessage(validation);
		data.setMessage("Failed");
		return new ResponseEntity<CommonRes>(data, HttpStatus.OK);
	} else {
		// save
		SuccessRes res = service.saveClauses(req,documentFile);
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

//  Get All Clauses Master
@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
@PostMapping("/getallclauses")
@ApiOperation("This method is getall Clauses")
public ResponseEntity<CommonRes> getallClauses(@RequestBody ClausesMasterGetallReq req) {
	CommonRes data = new CommonRes();
	reqPrinter.reqPrint(req);

	List<ClausesMasterRes> res = service.getallClauses(req);
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

//  Get Active Clauses Master
@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
@PostMapping("/getactiveclauses")
@ApiOperation("This method is get Active Clauses")
public ResponseEntity<CommonRes> getActiveClauses(@RequestBody ClausesMasterGetallReq req) {
	CommonRes data = new CommonRes();
	reqPrinter.reqPrint(req);

	List<ClausesMasterRes> res = service.getActiveClauses(req);
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

// Get By Clauses Id
@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
@PostMapping("/getbyclausesid")
@ApiOperation("This Method is to get by Clauses id")
public ResponseEntity<CommonRes> getByClausesId(@RequestBody ClausesMasterGetReq req) {
	CommonRes data = new CommonRes();
	ClausesMasterRes res = service.getByClausesId(req);
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
@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
@PostMapping("/clauses/changestatus")
@ApiOperation(value = "This method is get Clauses Change Status")
public ResponseEntity<CommonRes> changeStatusOfClauses(@RequestBody ClausesChangeStatusReq req) {

	CommonRes data = new CommonRes();
	// Change Status
	SuccessRes res = service.changeStatusOfClauses(req);
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

//Clauses Master Drop Down Type
@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
@PostMapping(value="/dropdown/clauses",produces = "application/json")
@ApiOperation(value = "This method is get Clauses Master Drop Down")

public ResponseEntity<DropdownCommonRes> getClausesMasterDropdown(@RequestBody ClausesMasterDropdownReq req) {

	DropdownCommonRes data = new DropdownCommonRes();

	// Save
	List<DropDownRes> res = service.getClausesMasterDropdown(req);
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

@PostMapping("/getallnonselectedclauses")
@ApiOperation("This method is getall Company Product Master")
public ResponseEntity<CommonRes> getallNonSelectedCompanyProducts(@RequestBody NonSelectedClausesGetAllReq req)
{
	CommonRes data = new CommonRes();
	reqPrinter.reqPrint(req);
	
	List<ClausesMasterRes> res = service.getallNonSelectedWars(req);
	data.setCommonResponse(res);
	data.setErrorMessage(Collections.emptyList());
	data.setIsError(false);
	data.setMessage("Success");
	
	if(res!= null) {
		return new ResponseEntity<CommonRes> (data, HttpStatus.CREATED);
	}
	else {
		return new ResponseEntity<> (null, HttpStatus.BAD_REQUEST);
	}
}


//List Save

@PostMapping("/insertclauseslist")
@ApiOperation(value = "This Method is to save Waranty Master List")
public ResponseEntity<CommonRes> saveClausesList(@RequestBody  List<ClausesMasterReq> req) {
	CommonRes data = new CommonRes();
	reqPrinter.reqPrint(req);

	List<Error> validation = service.validateClausesList(req);
//validation
	if (validation != null && validation.size() != 0) {
		data.setCommonResponse(null);
		data.setIsError(true);
		data.setErrorMessage(validation);
		data.setMessage("Failed");
		return new ResponseEntity<CommonRes>(data, HttpStatus.OK);
	} else {
		// save
		SuccessRes res = service.saveClausesList(req);
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









}
