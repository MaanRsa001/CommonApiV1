package com.maan.eway.excess.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.excess.req.ExcessInsertDetailsReq;
import com.maan.eway.excess.req.ExcessTransactionReq;
import com.maan.eway.excess.service.ExcessTransactionDetailsService;

@RestController
@RequestMapping("/excess")
public class ExcessTransactionDetailsController {

	@Autowired
	private ExcessTransactionDetailsService excessService;

	@PostMapping("/gettransactiondetails")
	public ResponseEntity<CommonRes> getExcessTransactionDetails(@RequestBody ExcessTransactionReq req) {
		CommonRes response = excessService.getExcessTransactionDetails(req);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/inserttransactiondetails")
	public ResponseEntity<CommonRes> insertExcessTransactionDetails(@RequestBody ExcessTransactionReq req) {
		CommonRes response = excessService.insertExessTransactionDetails(req);
		return ResponseEntity.ok(response);
	}
	
	@PostMapping("/insertExcessTransactionDetails")
	public ResponseEntity<CommonRes> insertExcess(@RequestBody ExcessInsertDetailsReq req) {
		CommonRes response = excessService.insertExess(req);
		return ResponseEntity.ok(response);
	}
}
