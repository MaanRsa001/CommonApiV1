package com.maan.eway.common.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.req.ClaimHistoryInfoGetReq;
import com.maan.eway.common.req.ClaimHistoryInfoSaveReq;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.service.impl.ClaimHistoryInfoServiceImpl;
import com.maan.eway.error.Error;

@RestController
@RequestMapping("/api")
public class ClaimHistoryInfoController {
	@Autowired
	private ClaimHistoryInfoServiceImpl claimHistoryService;
	
	@PostMapping("/saveclaimhistoryinfo")
	public ResponseEntity<CommonRes> saveUpdateClaimHistoryInfo(@RequestBody ClaimHistoryInfoSaveReq req){		
		List<Error> errorList = claimHistoryService.validateClaimHistoryInfo(req);
		if(errorList != null && !errorList.isEmpty()) {
			CommonRes response = new CommonRes();
			response.setMessage("Failed");
			response.setIsError(true);
			response.setErrorMessage(errorList);						
			return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);		
		}		
		else {
			CommonRes response = claimHistoryService.saveUpdateClaimHistoryInfo(req);
			if(response == null) {return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);}
			else {return new ResponseEntity<>(response, HttpStatus.OK);}
		}
	}
	
	
	@PostMapping("/getclaimhistoryinfo")
	public ResponseEntity<CommonRes> getClaimHistoryInfo(@RequestBody ClaimHistoryInfoGetReq req){
		CommonRes response = claimHistoryService.getClaimHistoryInfo(req);
		if(response == null) {return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);}
		else {return new ResponseEntity<>(response, HttpStatus.OK);}
	}
	
	

}
