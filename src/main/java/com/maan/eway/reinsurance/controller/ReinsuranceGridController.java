package com.maan.eway.reinsurance.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.reinsurance.req.RIGridBrokerReq;
import com.maan.eway.reinsurance.req.RiGridReq;
import com.maan.eway.reinsurance.service.ReinsuranceGridService;

@RestController
@RequestMapping("/grid")
public class ReinsuranceGridController {
	
	@Autowired
	ReinsuranceGridService gridService;
	
	@PostMapping("/getReferralPending")
	public ResponseEntity<CommonRes> getReferralPendingListRP(@RequestBody RiGridReq req) {
		CommonRes data =  gridService.getReferralPendinglist(req,"RP");
		if (data != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	@PostMapping("/getReferralApproved")
	public ResponseEntity<CommonRes> getReferralAppRA(@RequestBody RiGridReq req) {
		CommonRes data =  gridService.getReferralPendinglist(req,"RA");
		if (data != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	@PostMapping("/getReferralReject")
	public ResponseEntity<CommonRes> getReferralRR(@RequestBody RiGridReq req) {
		CommonRes data =  gridService.getReferralPendinglist(req,"RR");
		if (data != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/getBrokerPending")
	public ResponseEntity<CommonRes> getBrokerPendinglist(@RequestBody RIGridBrokerReq req) {
		CommonRes data =  gridService.getBrokerlist(req,"RP");
		if (data != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/getBrokerApproved")
	public ResponseEntity<CommonRes> getBrokerRAlist(@RequestBody RIGridBrokerReq req) {
		CommonRes data =  gridService.getBrokerlist(req,"RA");
		if (data != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/getBrokerReject")
	public ResponseEntity<CommonRes> getBrokerRRlist(@RequestBody RIGridBrokerReq req) {
		CommonRes data =  gridService.getBrokerlist(req,"RR");
		if (data != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
}
