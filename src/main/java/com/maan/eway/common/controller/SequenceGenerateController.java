package com.maan.eway.common.controller;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.req.SequenceGenerateReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.SequenceGenerateRes;
import com.maan.eway.common.service.SequenceGenerateService;
import com.maan.eway.common.service.impl.FetchErrorDescServiceImpl;
import com.maan.eway.service.PrintReqService;

@RestController
@RequestMapping("/api")
public class SequenceGenerateController {

	@Autowired 
	private SequenceGenerateService entityService ; 
	
	@Autowired
	private PrintReqService reqPrinter;
	
	@Autowired
	private FetchErrorDescServiceImpl errorDescService ;


	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping(value="/generatesequence",produces = "application/json")
	public ResponseEntity<CommonRes> generateSequence(@RequestBody  SequenceGenerateReq req) {

		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		
		/////// save
		SequenceGenerateRes res = entityService.generateSequence(req);
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
