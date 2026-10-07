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

import com.maan.eway.common.req.GetSectionReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.GetSectionRes;
import com.maan.eway.common.service.CompanyProductSectionCoverService;
import com.maan.eway.service.PrintReqService;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/api")
@Api(tags = "Common  : Section Info ", description = "API's")
public class CompanyProductSectionCoverController {
	@Autowired
	private PrintReqService reqPrinter;
	
	@Autowired
	private CompanyProductSectionCoverService service;

	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_APPROVER','ROLE_USER')")
	@PostMapping("/getOptedAndUnoptedSection")
	@ApiOperation("This method is Get Opted And Unopted Section")
	public ResponseEntity<CommonRes> getOptedAndUnoptedSection(@RequestBody GetSectionReq req) {
		CommonRes data = new CommonRes();
		reqPrinter.reqPrint(req);

		GetSectionRes res = service.getOptedAndUnoptedSection(req);
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

	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_APPROVER','ROLE_USER')")
	@PostMapping("/getOptedAndUnoptedSectionCover")
	@ApiOperation("This method is Get Opted And Unopted Section Cover")
	public ResponseEntity<CommonRes> getOptedAndUnoptedSectionCover(@RequestBody GetSectionReq req) {
		CommonRes data = new CommonRes();
		reqPrinter.reqPrint(req);

		GetSectionRes res = service.getOptedAndUnoptedSectionCover(req);
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
	
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_APPROVER','ROLE_USER')")
	@PostMapping("/getOptedLocationId")
	@ApiOperation("This method is Get Opted Location")
	public ResponseEntity<CommonRes> getOptedLoctionId(@RequestBody GetSectionReq req) {
		CommonRes data = new CommonRes();
		reqPrinter.reqPrint(req);

		GetSectionRes res = service.getOptedLocationId(req);
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
