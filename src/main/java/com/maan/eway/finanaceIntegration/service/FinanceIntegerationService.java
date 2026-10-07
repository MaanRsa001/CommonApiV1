package com.maan.eway.finanaceIntegration.service;

import org.springframework.http.ResponseEntity;

import com.maan.eway.finanaceIntegration.req.CustomerCreationReq;
import com.maan.eway.finanaceIntegration.req.FinanceIntegerationBean;
import com.maan.eway.finanaceIntegration.req.FinanceReq;
import com.maan.eway.integration.res.PremiaResponse;

public interface FinanceIntegerationService {


	PremiaResponse customerEntityCreation(String sysId, FinanceReq req);

	ResponseEntity<?> createFreshPolicy(FinanceIntegerationBean integrationBean);

	ResponseEntity<?> financeIntegrationCustomerCreation(CustomerCreationReq customerCreationReq);

	ResponseEntity<?> financeIntegrationUpdateCreation(CustomerCreationReq customerCreationReq);

}
