package com.maan.eway.jasper.controller;

import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.error.Error;
import com.maan.eway.jasper.req.JasperDocumentReq;
import com.maan.eway.jasper.res.JasperDocumentRes;
import com.maan.eway.jasper.service.JasperQuoteService;
import com.maan.eway.service.PrintReqService;
import com.maan.eway.service.ValidationService;

import io.swagger.annotations.Api;


@RestController
@RequestMapping("/pdf/quote")
@Api(tags = "REPORT : Jasper Reports", description = "API's")
public class JasperQuoteController {

	
	@Autowired
	private JasperQuoteService jasper;
	@Autowired
	private  PrintReqService printReq;
	
	@Autowired
	private ValidationService servicevali;
	
	@PostMapping("/policyform") 
	private ResponseEntity<CommonRes> policyform(@RequestBody JasperDocumentReq req) {
		printReq.reqPrint(req);
		CommonRes data = new CommonRes();
		List<Error> validation =servicevali.validateMotorSchedule(req);
//		List<Error> validation =null;
		if(validation != null && !validation.isEmpty()){
			data.setCommonResponse(null);
			data.setIsError(true);
			data.setErrorMessage(validation);
			data.setMessage("Failed");
			return new ResponseEntity<CommonRes>(data, HttpStatus.OK);
		}else {
			JasperDocumentRes res = jasper.policyform(req);
			data.setCommonResponse(res);
			data.setIsError(false);
			data.setErrorMessage(Collections.emptyList());
			data.setMessage("Success");
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		}
	}
	

}
