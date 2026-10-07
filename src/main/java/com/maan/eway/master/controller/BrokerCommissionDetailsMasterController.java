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
import com.maan.eway.error.Error;
import com.maan.eway.master.req.BrokerBackdaysGetReq;
import com.maan.eway.master.req.BrokerCommissionDetailsMasterChangeStatusReq;
import com.maan.eway.master.req.BrokerCommissionDetailsMasterGetReq;
import com.maan.eway.master.req.BrokerCommissionDetailsMasterGetallReq;
import com.maan.eway.master.req.BrokerCommissionDetailsMasterSaveReq;
import com.maan.eway.master.res.BrokerCommRes;
import com.maan.eway.master.res.BrokerCommissionDetailsMasterGetRes;
import com.maan.eway.master.service.BrokerCommissionDetailsMasterService;
import com.maan.eway.res.SuccessRes;
import com.maan.eway.service.PrintReqService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
@RestController
@Api(tags="MASTER : Broker Commission Details Master", description="API's")
@RequestMapping("/master")
public class BrokerCommissionDetailsMasterController {

@Autowired
private BrokerCommissionDetailsMasterService service;

@Autowired
private PrintReqService reqPrinter;

//Save
@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_ADMIN')")
@PostMapping("/insertbrokercommission")
@ApiOperation(value = "This Method is to save Broker Commission")
public ResponseEntity<CommonRes> saveBrokerCommission(@RequestBody BrokerCommissionDetailsMasterSaveReq req) {
	CommonRes data = new CommonRes();
	reqPrinter.reqPrint(req);

	List<Error> validation = service.validateBrokerCommission(req);
//validation
	if (validation != null && validation.size() != 0) {
		data.setCommonResponse(null);
		data.setIsError(true);
		data.setErrorMessage(validation);
		data.setMessage("Failed");
		return new ResponseEntity<CommonRes>(data, HttpStatus.OK);
	} else {
		// save
		SuccessRes res = service.saveBrokerCommission(req);
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



@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_ADMIN')")
@PostMapping("/getbrokercommission")
@ApiOperation(value = "This Method is to Get Broker Commission")
public ResponseEntity<CommonRes> getBrokerCommission(@RequestBody BrokerCommissionDetailsMasterGetReq req) {
	CommonRes data = new CommonRes();
	reqPrinter.reqPrint(req);
		BrokerCommissionDetailsMasterGetRes res = service.getBrokerCommission(req);
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


@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_ADMIN')")
@PostMapping("/getallbrokercommission")
@ApiOperation(value = "This Method is to Getall Broker Commission")
public ResponseEntity<CommonRes> getallBrokerCommission(@RequestBody BrokerCommissionDetailsMasterGetallReq req) {
	CommonRes data = new CommonRes();
	reqPrinter.reqPrint(req);
		List<BrokerCommissionDetailsMasterGetRes> res = service.getallBrokerCommission(req);
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

@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_ADMIN')")
@PostMapping("/productunoptedcommission")
@ApiOperation(value = "This Method is to Getall Broker Commission")
public ResponseEntity<CommonRes> getUnOptedBrokerCommission(@RequestBody BrokerCommissionDetailsMasterGetallReq req) {
	CommonRes data = new CommonRes();
	reqPrinter.reqPrint(req);
		List<BrokerCommissionDetailsMasterGetRes> res = service.getUnOptedBrokerCommission(req);
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

@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_ADMIN')")
@PostMapping("/getactivebrokercommission")
@ApiOperation(value = "This Method is to Getall Broker Commission")
public ResponseEntity<CommonRes> getactiveBrokerCommission(@RequestBody BrokerCommissionDetailsMasterGetallReq req) {
	CommonRes data = new CommonRes();
	reqPrinter.reqPrint(req);
		List<BrokerCommissionDetailsMasterGetRes> res = service.getactiveBrokerCommission(req);
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


@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_ADMIN')")
@PostMapping("/changestatusbrokercommission")
@ApiOperation(value = "This Method is to Change Status Broker Commission")
public ResponseEntity<CommonRes> changeStatusBrokerCommission(@RequestBody BrokerCommissionDetailsMasterChangeStatusReq req) {
	CommonRes data = new CommonRes();
	reqPrinter.reqPrint(req);
		SuccessRes res = service.changeStatusBrokerCommission(req);
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


@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_APPROVER','ROLE_USER')")
@PostMapping("/brokerbackdays")
@ApiOperation(value = "This Method is to Broker Backdays")
public ResponseEntity<CommonRes> getallBrokerCommission(@RequestBody BrokerBackdaysGetReq req) {
	CommonRes data = new CommonRes();
	reqPrinter.reqPrint(req);
	BrokerCommRes res = service.getBackDays(req);
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





