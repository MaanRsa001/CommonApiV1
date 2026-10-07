package com.maan.eway.integration.controller;

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
import com.maan.eway.integration.req.ValuationDetailsReq;
import com.maan.eway.integration.req.ValuationListReq;
import com.maan.eway.integration.req.ValuationReq;
import com.maan.eway.integration.req.ValuationStatusReq;
import com.maan.eway.integration.res.PremiaResponse;
import com.maan.eway.integration.res.ValuationListRes;
import com.maan.eway.integration.service.ValuationService;
import com.maan.eway.service.PrintReqService;

import io.swagger.annotations.Api;
 

@RestController
@RequestMapping("/api/valuation")
@Api(tags = "Valuation Controller :  ", description = "API's")
public class ValuationController {

	@Autowired
	private ValuationService service;
	
	@Autowired
	private PrintReqService reqPrinter;
	
	@PostMapping("/push")
	public ResponseEntity<CommonRes> pushValuation(@RequestBody ValuationReq req){

		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();

		PremiaResponse res = service.pushValuation(req);
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
	@PostMapping("/getStatus")
	public ResponseEntity<CommonRes> getStatus(@RequestBody ValuationStatusReq req){

		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();

		PremiaResponse res = service.getStatus(req);
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
	@PostMapping("/getDetails")
	public ResponseEntity<CommonRes> getDetails(@RequestBody ValuationDetailsReq req){

		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();

		PremiaResponse res = service.getDetails(req);
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
	@PostMapping("/getList")
	public ResponseEntity<CommonRes> getValuationList(@RequestBody ValuationListReq req){

		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();

		List<ValuationListRes> res = service.getValuationList(req);
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
