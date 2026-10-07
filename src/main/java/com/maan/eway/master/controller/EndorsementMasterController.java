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
import com.maan.eway.master.req.EndorsementChangeStatusReq;
import com.maan.eway.master.req.EndorsementMasterDropdownReq;
import com.maan.eway.master.req.EndorsementMasterGetReq;
import com.maan.eway.master.req.EndorsementMasterGetallReq;
import com.maan.eway.master.req.EndorsementMasterSaveReq;
import com.maan.eway.master.res.EndorsementMasterGetallRes;
import com.maan.eway.master.res.EndorsementMasterRes;
import com.maan.eway.master.res.GetallEndorsementRes;
import com.maan.eway.master.service.EndorsementMasterService;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;
import com.maan.eway.service.PrintReqService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
@RestController
@Api(tags="MASTER : Endorsement MASTER", description="API's")
@RequestMapping("/master")
public class EndorsementMasterController {

@Autowired
private EndorsementMasterService service;

@Autowired
private PrintReqService reqPrinter;

@Autowired
private FetchErrorDescServiceImpl errorDescService ;

//Save
@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_ADMIN')")
@PostMapping("/insertendorsement")
@ApiOperation(value = "This Method is to save Endorsement Master")
public ResponseEntity<CommonRes> saveEndorsement(@RequestBody EndorsementMasterSaveReq req) {
	reqPrinter.reqPrint(req);
	CommonRes data = new CommonRes();
	List<String> validationCodes = service.validateEndorsement(req);
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
	
	//validation
	if (validation != null && validation.size() != 0) {
		data.setCommonResponse(null);
		data.setIsError(true);
		data.setErrorMessage(validation);
		data.setMessage("Failed");
		return new ResponseEntity<CommonRes>(data, HttpStatus.OK);
	} else {
		// save
		SuccessRes res = service.saveEndorsement(req);
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

//  Get All Endorsement Master
@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_ADMIN')")
@PostMapping("/getallendorsement")
@ApiOperation("This method is getall Endorsement")
public ResponseEntity<CommonRes> getallEndorsement(@RequestBody EndorsementMasterGetallReq req) {
	CommonRes data = new CommonRes();
	reqPrinter.reqPrint(req);

	List<EndorsementMasterGetallRes> res = service.getallEndorsement(req);
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

//  Get Active Endorsement Master
//@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_ADMIN')")
@PostMapping("/getactiveendorsement")
@ApiOperation("This method is get Active Endorsement")
public ResponseEntity<CommonRes> getActiveEndorsement(@RequestBody EndorsementMasterGetallReq req) {
	CommonRes data = new CommonRes();
	reqPrinter.reqPrint(req);

	List<EndorsementMasterGetallRes> res = service.getActiveEndorsement(req);
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
@PostMapping("/getbyendorsementid")
@ApiOperation("This Method is to get by Endorsement id")
public ResponseEntity<CommonRes> getByEndorsementId(@RequestBody EndorsementMasterGetReq req) {
	CommonRes data = new CommonRes();
	EndorsementMasterRes res = service.getByEndorsementId(req);
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
@PostMapping("/endorsement/changestatus")
@ApiOperation(value = "This method is get Endorsement Change Status")
public ResponseEntity<CommonRes> changeStatusOfEndorsement(@RequestBody EndorsementChangeStatusReq req) {

	CommonRes data = new CommonRes();
	// Change Status
	SuccessRes res = service.changeStatusOfEndorsement(req);
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
@PostMapping(value="/dropdown/endorsement",produces = "application/json")
@ApiOperation(value = "This method is get Endorsement Master Drop Down")

public ResponseEntity<DropdownCommonRes> getEndorsementMasterDropdown(@RequestBody EndorsementMasterDropdownReq req) {

	DropdownCommonRes data = new DropdownCommonRes();

	// Save
	List<DropDownRes> res = service.getEndorsementMasterDropdown(req);
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



//Get All Endorsement Master
@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_ADMIN')")
@PostMapping("/getallbrokerendorsement")
@ApiOperation("This method is getall Endorsement")
public ResponseEntity<CommonRes> getallBrokerEndorsement(@RequestBody EndorsementMasterGetallReq req) {
CommonRes data = new CommonRes();
reqPrinter.reqPrint(req);

List<EndorsementMasterGetallRes> res = service.getallBrokerEndorsement(req);
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

//Get All Endorsement Master
@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_ADMIN')")
@PostMapping("/getallendorsementgrid")
@ApiOperation("This method is getall Endorsement")
public ResponseEntity<CommonRes> getallEndorsementGrid(@RequestBody EndorsementMasterGetallReq req) {
CommonRes data = new CommonRes();
reqPrinter.reqPrint(req);

List<GetallEndorsementRes> res = service.getallEndorsementGrid(req);
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
