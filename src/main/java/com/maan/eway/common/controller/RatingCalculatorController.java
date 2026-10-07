package com.maan.eway.common.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.common.req.EserviceMotorDetailsSaveRes;
import com.maan.eway.common.service.SeqQuotenoService;
import com.maan.eway.req.calcengine.CalcCommission;
import com.maan.eway.req.calcengine.CalcEngine;
import com.maan.eway.req.calcengine.ReferralApi;
import com.maan.eway.res.calc.AdminReferral;
import com.maan.eway.res.calc.DebitAndCredit;
import com.maan.eway.res.referal.MasterReferal;
import com.maan.eway.service.CalculatorEngine;
import com.maan.eway.service.impl.referal.ReferalServiceImpl;

import io.swagger.annotations.ApiOperation;

@RestController
@RequestMapping("/calculator")
public class RatingCalculatorController {
	@Autowired
	private CalculatorEngine service;
	
	@Autowired
	private ReferalServiceImpl rservice;
	
	@Autowired
	private SeqQuotenoService seq;
	/*@PostMapping("/loadsection")
	@ApiOperation("This Method is to get by id")
	public void loadSection(@RequestBody CalcEngine request) {
		service.LoadSection(request);
	}
	*/
	/*@PostMapping("/loadfactorrates")visionmotor@123#
	@ApiOperation("This Method is to get by id")
	public void LoadFactorRates(@RequestBody CalcEngine request) {
		service.LoadFactorRates(request,"5");
	}*/
//@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/calc")
	@ApiOperation("This Method is to get by id")
	public EserviceMotorDetailsSaveRes calc(@RequestBody CalcEngine request,@RequestHeader("Authorization") String tokens) {
		EserviceMotorDetailsSaveRes response = service.calculator(request,tokens.replaceAll("Bearer ", "").split(",")[0]); 
		return response;
	}
@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/referalcalc")
	@ApiOperation("This Method is to get by id")
	public EserviceMotorDetailsSaveRes referalcalc(@RequestBody CalcEngine request,@RequestHeader("Authorization") String tokens) {
		EserviceMotorDetailsSaveRes response = service.referalCalculator(request,tokens.replaceAll("Bearer ", "").split(",")[0]); 
		return response;
	}
	
	@PostMapping("/masterreferral")
	@ApiOperation("This Method is to get by id")
	public List<MasterReferal> masterreferral(@RequestBody CalcEngine request ,@RequestHeader("Authorization") String tokens ) {
		List<MasterReferal> response=null;
		try {
			System.out.println("T"+tokens);
			response = rservice.masterreferral(request,tokens.replaceAll("Bearer ", "").split(",")[0]);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} 
		return response;
	}
@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/commissionCalc")
	@ApiOperation("This Method is to get by id")
	public List<DebitAndCredit> commissionCalc(@RequestBody CalcCommission request ,@RequestHeader("Authorization") String tokens ) {
		List<DebitAndCredit> response=null;
		try {
			
			response = service.commissionCalc(request);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return response;
	}

    @PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/sequence")
	@ApiOperation("This Method is to get by id")
	public String getsequence() {
		return seq.create();
	}
	 
    @PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/referallist")
	@ApiOperation("This Method is to get by id")
	public List<AdminReferral> getReferalList(@RequestBody ReferralApi request) {

    	List<AdminReferral> response=null;
		try {
			
			response = service.getReferalList(request);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return response;
	
	}


    @PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/policy/calc")
	@ApiOperation("This Method is to get by id")
	public EserviceMotorDetailsSaveRes policyCalc(@RequestBody CalcEngine request,@RequestHeader("Authorization") String tokens) {
		EserviceMotorDetailsSaveRes response = service.policyCalculator(request,tokens.replaceAll("Bearer ", "").split(",")[0]); 
		return response;
	}
    
    @PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	@PostMapping("/calc/call")
	@ApiOperation("This Method is to get by id")
	public List<EserviceMotorDetailsSaveRes> getcalc(@RequestBody CalcEngine request,@RequestHeader("Authorization") String tokens) {
		List<EserviceMotorDetailsSaveRes> response = service.getCalc(request,tokens.replaceAll("Bearer ", "").split(",")[0]); 
		return response;
	}
    
}
