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

import com.maan.eway.common.req.NewQuoteReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.QuoteUpdateRes;
import com.maan.eway.common.service.NotificationThreadService;
import com.maan.eway.service.PrintReqService;

import io.swagger.annotations.Api;

@RestController
@RequestMapping("/noti")
@Api(tags = "NOTIFICATION CALL", description = "API's")
public class NoificationThreadController {

	@Autowired
	private PrintReqService reqPrinter;

	@Autowired
	private NotificationThreadService entityService;
	
	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/issuernotification")
	public ResponseEntity<CommonRes> getPaymentSucessStatus(@RequestBody NewQuoteReq req) {
		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();
		QuoteUpdateRes res = entityService.getUpdateReferral(req);
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
