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

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.DropdownCommonRes;
import com.maan.eway.error.Error;
import com.maan.eway.master.req.NonSelectedClausesGetAllReq;
import com.maan.eway.master.req.WarRateMasterGetReq;
import com.maan.eway.master.req.WarRateMasterGetallReq;
import com.maan.eway.master.req.WarRateMasterReq;
import com.maan.eway.master.req.WarRateMasterSaveReq;
import com.maan.eway.master.req.WarrateChangeStatusReq;
import com.maan.eway.master.req.WarrateMasterDropdownReq;
import com.maan.eway.master.res.WarRateMasterRes;
import com.maan.eway.master.service.WarRateMasterService;
import com.maan.eway.res.DropDownRes;
import com.maan.eway.res.SuccessRes;
import com.maan.eway.service.PrintReqService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
@RestController
@Api(tags="MASTER : War Rate MASTER", description="API's")
@RequestMapping("/master")
public class WarRateMasterController {

@Autowired
private WarRateMasterService  service;

@Autowired
private PrintReqService reqPrinter;

//Save
@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER')")
@PostMapping("/insertwarrate")
@ApiOperation(value="This Method is to save War Rate Master")
public ResponseEntity<CommonRes> saveWarRate(@RequestBody WarRateMasterSaveReq req){
	CommonRes data = new CommonRes();
	reqPrinter.reqPrint(req);
	
List<Error> validation = service.validateWarranty(req);
//validation
if(validation !=null && validation.size()!=0) {
	data.setCommonResponse(null);
	data.setIsError(true);
	data.setErrorMessage(validation);
	data.setMessage("Failed");
	return new ResponseEntity<CommonRes>(data,HttpStatus.OK);
} else {
	//save
	SuccessRes res = service.saveWarRate(req);
	data.setCommonResponse(res);
	data.setIsError(false);
	data.setErrorMessage(Collections.emptyList());
	data.setMessage("Success");
	
	if(res!=null) {
		return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
	}
	else {
		return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
	}
}
}

//  Get All War Rate Master
@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER')")
@PostMapping("/getallwarrate")
@ApiOperation("This method is getall Warrate")
public ResponseEntity<CommonRes> getallWarRate(@RequestBody WarRateMasterGetallReq req)
{
	CommonRes data = new CommonRes();
	reqPrinter.reqPrint(req);
	
	List<WarRateMasterRes> res =service.getallWarRate(req);
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

//  Get Active War RAte Master
@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER')")
	@PostMapping("/getactivewarrate")
	@ApiOperation("This method is get Active War Rate")
	public ResponseEntity<CommonRes> getActiveWarrate(@RequestBody WarRateMasterGetallReq req)
	{
		CommonRes data = new CommonRes();
		reqPrinter.reqPrint(req);
		
		List<WarRateMasterRes> res = service.getActiveWarrate(req);
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

// Get By War Rate Id
@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER')")
@PostMapping("/getbywarrateid")
@ApiOperation("This Method is to get by War Rate id")
public ResponseEntity<CommonRes> getByWarrateId(@RequestBody WarRateMasterGetReq req)
{
CommonRes data = new CommonRes();
WarRateMasterRes res = service.getByWarrateId(req);
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
	
@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER')")
@PostMapping("/warrate/changestatus")
@ApiOperation(value = "This method is get Warrate Change Status")
public ResponseEntity<CommonRes> changeStatusOfWarrate(@RequestBody WarrateChangeStatusReq req) {

	CommonRes data = new CommonRes();
	// Change Status
	SuccessRes res = service.changeStatusOfWarrate(req);
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
//War Rate Master Drop Down Type
@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping(value="/dropdown/warrate",produces = "application/json")
	@ApiOperation(value = "This method is get War Rate Master Drop Down")

	public ResponseEntity<DropdownCommonRes> getWarrateMasterDropdown(@RequestBody WarrateMasterDropdownReq req) {

		DropdownCommonRes data = new DropdownCommonRes();

		// Save
		List<DropDownRes> res = service.getWarrateMasterDropdown(req);
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

	
	
	//Save
@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER')")
	@PostMapping("/insertwarratelist")
	@ApiOperation(value="This Method is to save War Rate Master List")
	public ResponseEntity<CommonRes> saveWarRate(@RequestBody  List<WarRateMasterReq> req){
		CommonRes data = new CommonRes();
		reqPrinter.reqPrint(req);
		
	List<Error> validation = service.validateWarrantyList(req);
	//validation
	if(validation !=null && validation.size()!=0) {
		data.setCommonResponse(null);
		data.setIsError(true);
		data.setErrorMessage(validation);
		data.setMessage("Failed");
		return new ResponseEntity<CommonRes>(data,HttpStatus.OK);
	} else {
		//save
		SuccessRes res = service.saveWarRateList(req);
		data.setCommonResponse(res);
		data.setIsError(false);
		data.setErrorMessage(Collections.emptyList());
		data.setMessage("Success");
		
		if(res!=null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		}
		else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	}

	@PostMapping("/getallnonselectedwarrate")
	@ApiOperation("This method is getall Warrate Master")
	public ResponseEntity<CommonRes> getallNonSelectedWarrate(@RequestBody NonSelectedClausesGetAllReq req)
	{
		CommonRes data = new CommonRes();
		reqPrinter.reqPrint(req);
		
		List<WarRateMasterRes> res = service.getallNonSelectedWarrate(req);
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
	

	
}
