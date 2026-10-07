package com.maan.eway.creditAmount.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.creditAmount.Res.BrokerCreditDetailsRes;
import com.maan.eway.creditAmount.service.BrokerCreditService;

import io.swagger.annotations.Api;

@RestController
@RequestMapping("/credit")
@Api(tags = "CREDIT UTILIZED AND BALANCE DETAILS", description = "API's")

public class BrokerCreditController {
	
	 @Autowired
	    private BrokerCreditService brokerCreditService;
	 
	 	
	 	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	    @GetMapping("/details/{brokerId}")
	    public BrokerCreditDetailsRes getBrokerCreditDetails(@PathVariable String brokerId) {
	        return brokerCreditService.getBrokerCreditSummary(brokerId);
	    }
	    
	 	@PreAuthorize("hasAnyRole('ROLE_APPROVER','ROLE_USER','ROLE_ADMIN')")
	    @GetMapping("/balance/{quoteNo}")
	    public BrokerCreditDetailsRes getBrokerBalnceDetails(@PathVariable String quoteNo) {
	 		return brokerCreditService.getBrokerCreditBalance(quoteNo);
	    }

}
