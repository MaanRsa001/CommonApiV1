package com.maan.eway.finanaceIntegration.controller;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.finanaceIntegration.req.CustomerCreationReq;
import com.maan.eway.finanaceIntegration.req.FinanceIntegerationBean;
import com.maan.eway.finanaceIntegration.req.FinanceReq;
import com.maan.eway.finanaceIntegration.service.FinanceIntegerationService;
import com.maan.eway.integration.res.PremiaResponse;

import io.swagger.annotations.Api;

@RestController
@RequestMapping("/push/financeIntegration")
@Api(tags = "financeintegration Controller : financeintegration Integration ", description = "API's")
public class FinanceController {

	@Autowired
	private FinanceIntegerationService financeService;

	/********
	 * SysId
	 *********/
	@PostMapping("/customerEntity/{sysId}")
	public ResponseEntity<CommonRes> customerEntity(@PathVariable String sysId, @RequestBody FinanceReq req) {

		// reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();

		PremiaResponse res = financeService.customerEntityCreation(sysId, req);
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

	@PostMapping("/financeFreshPolicyAccountGeneration")
	public ResponseEntity<?> createHdrForFreshPolicy1(@RequestBody FinanceIntegerationBean integerationBean) {
		ResponseEntity<?> hdrForFreshPolicy = financeService.createFreshPolicy(integerationBean);
		return hdrForFreshPolicy;
	}

	@PostMapping("/financeIntegrationCustomerCreation")
	public ResponseEntity<?> financeIntegrationCustomerCreation(@RequestBody CustomerCreationReq customerCreationReq) {
		ResponseEntity<?> hdrForFreshPolicy = financeService.financeIntegrationCustomerCreation(customerCreationReq);
		return hdrForFreshPolicy;
	}
	
	@PostMapping("/financeIntegrationUpdateCreation")
	public ResponseEntity<?> financeIntegrationUpdateCreation(@RequestBody CustomerCreationReq customerCreationReq) {
		ResponseEntity<?> hdrForFreshPolicy = financeService.financeIntegrationUpdateCreation(customerCreationReq);
		return hdrForFreshPolicy;
	}
}
