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
import com.maan.eway.common.service.impl.FetchErrorDescServiceImpl;
import com.maan.eway.error.Error;
import com.maan.eway.master.req.PaymentMasterChangeStatusReq;
import com.maan.eway.master.req.PaymentMasterDropdownReq;
import com.maan.eway.master.req.PaymentMasterGetReq;
import com.maan.eway.master.req.PaymentMasterGetallReq;
import com.maan.eway.master.req.PaymentMasterSaveReq;
import com.maan.eway.master.res.PaymentMasterDropDownRes;
import com.maan.eway.master.res.PaymentMasterRes;
import com.maan.eway.master.service.PaymentMasterService;
import com.maan.eway.res.SuccessRes;
import com.maan.eway.service.PrintReqService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
@RestController
@Api(tags="MASTER : Payment MASTER", description="API's")
@RequestMapping("/master")
public class PaymentMasterController {

@Autowired
private PaymentMasterService service;

@Autowired
private PrintReqService reqPrinter;

@Autowired
private FetchErrorDescServiceImpl errorDescService ;

//Save
@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
@PostMapping("/insertpayment")
@ApiOperation(value = "This Method is to save Payment Master")
public ResponseEntity<CommonRes> savePaymentMaster(@RequestBody List<PaymentMasterSaveReq> req) {
	
	reqPrinter.reqPrint(req);
	CommonRes data = new CommonRes();
	List<String> validationCodes =  service.validatePaymentMaster(req);
	List<Error> validation = null;
	if(validationCodes!=null && validationCodes.size() > 0 ) {
		CommonErrorModuleReq comErrDescReq = new CommonErrorModuleReq();
		comErrDescReq.setBranchCode(req.get(0).getBranchCode());
		comErrDescReq.setInsuranceId(req.get(0).getCompanyId());
		comErrDescReq.setProductId("99999");
		comErrDescReq.setModuleId("31");
		comErrDescReq.setModuleName("MASTERS");
		
		validation = errorDescService.getErrorDesc(validationCodes, comErrDescReq);
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
		SuccessRes res = service.savePaymentMaster(req);
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

//  Get All Payment Master
@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
@PostMapping("/getallpayment")
@ApiOperation("This method is getall Payment")
public ResponseEntity<CommonRes> getallPayment(@RequestBody PaymentMasterGetallReq req) {
	CommonRes data = new CommonRes();
	reqPrinter.reqPrint(req);

	List<PaymentMasterRes> res = service.getallPayment(req);
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

//  Get Active Payment Master
@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
@PostMapping("/getactivepayment")
@ApiOperation("This method is get Active Payment")
public ResponseEntity<CommonRes> getActivePayment(@RequestBody PaymentMasterGetallReq req) {
	CommonRes data = new CommonRes();
	reqPrinter.reqPrint(req);

	List<PaymentMasterRes> res = service.getActivePayment(req);
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

// Get By Payment Id
@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
@PostMapping("/getbypaymentid")
@ApiOperation("This Method is to get by Payment id")
public ResponseEntity<CommonRes> getByPaymentId(@RequestBody PaymentMasterGetReq req) {
	CommonRes data = new CommonRes();
	PaymentMasterRes res = service.getByPaymentId(req);
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
@PostMapping("/payment/changestatus")
@ApiOperation(value = "This method is get Payment Change Status")
public ResponseEntity<CommonRes> changeStatusOfPayment(@RequestBody PaymentMasterChangeStatusReq req) {

	CommonRes data = new CommonRes();
	// Change Status
	SuccessRes res = service.changeStatusOfPayment(req);
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

//Payment Master Drop Down Type
@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
@PostMapping(value="/dropdown/paymenttypes",produces = "application/json")
@ApiOperation(value = "This method is get Payment Master Drop Down")

public ResponseEntity<CommonRes> getPaymentMasterDropdown(@RequestBody PaymentMasterDropdownReq req) {

	CommonRes data = new CommonRes();

	// Save
	List<PaymentMasterDropDownRes> res = service.getPaymentMasterDropdown(req);
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
