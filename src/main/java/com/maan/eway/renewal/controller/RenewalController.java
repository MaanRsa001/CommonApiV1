package com.maan.eway.renewal.controller;

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
import com.maan.eway.renewal.req.PullrenewalReq;
import com.maan.eway.renewal.req.RenewalCopyQuoteReq;
import com.maan.eway.renewal.req.RenewalPendingRequest;
import com.maan.eway.renewal.req.RenewalSearchReq;
import com.maan.eway.renewal.req.RenewalStatusDetailReq;
import com.maan.eway.renewal.req.RenewalTransDetailReq;
import com.maan.eway.renewal.req.RenewalTransactionReq;
import com.maan.eway.renewal.res.RenewPremiaPolicyRes;
import com.maan.eway.renewal.service.RenewalService;
import com.maan.eway.res.CopyQuoteSuccessRes;

import io.swagger.annotations.Api;

@RestController
@Api(tags = "RENEWAL : renewal ", description = "API's")
@RequestMapping("/post/renewal")
public class RenewalController {
	
	@Autowired
	private RenewalService renewalservice;
	
	@PostMapping("/pullPremiarenewal")
	public ResponseEntity<CommonRes> pullPremiarenewal() {
	 	CommonRes data = renewalservice.pullPremiarenewal();
	 	if (data != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/pullrenewal")
	public ResponseEntity<CommonRes> pullrenewal(@RequestBody PullrenewalReq request) {
	 	CommonRes data = renewalservice.pullrenewal(request);
	 	if (data != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/renewalCopyQuote")
	public ResponseEntity<CopyQuoteSuccessRes> renewalCopyQuote(@RequestBody RenewalCopyQuoteReq request) {
		CopyQuoteSuccessRes data = renewalservice.renewalCopyQuote(request);
	 	if (data != null) {
			return new ResponseEntity<CopyQuoteSuccessRes>(data, HttpStatus.CREATED);
	 	} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/pending")
	public ResponseEntity<CommonRes> getRenewalPending(@RequestBody RenewalPendingRequest request) {
	 	CommonRes data = renewalservice.getRenewalPending(request);
	 	if (data != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}

	@PostMapping("/expired")
	public ResponseEntity<CommonRes> getRenewalExpired(@RequestBody RenewalPendingRequest request) {
	 	CommonRes data = renewalservice.getRenewalExpired(request);
	 	if (data != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/completed")
	public ResponseEntity<CommonRes> getRenewalCompleted(@RequestBody RenewalPendingRequest request) {
	 	CommonRes data = renewalservice.getRenewalCompleted(request);
	 	if (data != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/transaction")
	public ResponseEntity<CommonRes> getRenewalTransaction(@RequestBody RenewalTransactionReq request) {
	 	CommonRes data = renewalservice.getRenewalTransaction(request);
	 	if (data != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/transaction/success")
	public ResponseEntity<CommonRes> getRenewalTransactionSuccess(@RequestBody RenewalTransDetailReq request) {
	 	CommonRes data = renewalservice.getRenewalTransactionSuccess(request);
	 	if (data != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/transaction/converted")
	public ResponseEntity<CommonRes> getRenewalTransactionCoverted(@RequestBody RenewalTransDetailReq request) {
	 	CommonRes data = renewalservice.getRenewalTransactionConverted(request);
	 	if (data != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/transaction/pending")
	public ResponseEntity<CommonRes> getRenewalTransactionPending(@RequestBody RenewalTransDetailReq request) {
	 	CommonRes data = renewalservice.getRenewalTransactionPending(request);
	 	if (data != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	
	@PostMapping("/search/policydetails")
	public ResponseEntity<CommonRes> searchPolicyDetails(@RequestBody RenewalSearchReq req){
		
		CommonRes data = new CommonRes();
		
		List<RenewPremiaPolicyRes> res = renewalservice.searchRenewPremiaPolicy(req);
		
		data.setCommonResponse(res);
		data.setErrorMessage(Collections.EMPTY_LIST);
		data.setIsError(false);
		data.setMessage("Success");
		
		if(res != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		}
		else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	@PostMapping("/StatusList")
	public ResponseEntity<CommonRes> getRenewalStatusList(@RequestBody RenewalTransDetailReq request) {
	 	CommonRes data = renewalservice.getRenewalStatusList(request);
	 	if (data != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
	@PostMapping("/StatusDetaillist")
	public ResponseEntity<CommonRes> getRenewalStatusDetailList(@RequestBody RenewalStatusDetailReq request) {
	 	CommonRes data = renewalservice.getRenewalStatusDetailList(request);
	 	if (data != null) {
			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
		} else {
			return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
		}
	}
}
