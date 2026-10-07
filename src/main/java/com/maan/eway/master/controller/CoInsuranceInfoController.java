package com.maan.eway.master.controller;

import java.util.Collections;
import java.util.List;

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
import com.maan.eway.req.CoInsuranceDetails;
import com.maan.eway.service.CoInsuranceInfoService;



@RestController

@RequestMapping("/CoInsurance")
public class CoInsuranceInfoController  {
	
	
	@Autowired
	private CoInsuranceInfoService service;
	
	
	

	
	
	@PostMapping("/save")
	public ResponseEntity<CommonRes> CoInsuarancesave(@RequestBody CoInsuranceDetails req) {
	    CommonRes data = new CommonRes();
	    
//	    List<Error> validations = null;
	    List<Error> validation = service.validatecoinsurancedetails(req);
	    
	  
	    if (validation != null && validation.size() != 0) {
			data.setCommonResponse(null);
			data.setIsError(true);
			data.setErrorMessage(validation);
			data.setMessage("Failed");
			return new ResponseEntity<CommonRes>(data, HttpStatus.OK);
			}
	    else {
	    CommonRes res = service.CoInsuranceInfosaveupdate(req);
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
	
	@GetMapping("/deleteByQuoteNo/{QuoteNo}")
		public CommonRes CoInsuaranceDelete(@PathVariable("QuoteNo") String  QuoteNo) {
			return service.CoInsuranceInfodelete(QuoteNo);
		}
		
	@GetMapping("/getAllByByQuoteNo/{QuoteNo}")
		public CommonRes getAllByQuoteNo(@PathVariable("QuoteNo") String  QuoteNo) {
		 return service.getAllByQuoteNo(QuoteNo);

		}
	

}
