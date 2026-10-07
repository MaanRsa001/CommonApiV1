package com.maan.eway.integration.controller;

import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.SectionDataDetails;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.service.QuoteThreadService;
import com.maan.eway.finanaceIntegration.req.CustomerCreationReq;
import com.maan.eway.finanaceIntegration.req.FinanceIntegerationBean;
import com.maan.eway.finanaceIntegration.service.impl.FinanceIntegrationServiceImpl;
import com.maan.eway.integration.req.PremiaListRequest;
import com.maan.eway.integration.req.PremiaRequest;
import com.maan.eway.integration.res.PremiaResponse;
import com.maan.eway.integration.service.IntegrationService;
import com.maan.eway.integration.service.PhoenixIntegrationService;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.SectionDataDetailsRepository;
import com.maan.eway.service.PrintReqService;

import io.swagger.annotations.Api;

@RestController
@RequestMapping("/push/integration")
@Api(tags = "Integeraion Controller : Premia Integration ", description = "API's")
public class IntegrationController {

	@Autowired
	private PhoenixIntegrationService phoenixservice;

	@Autowired
	private PrintReqService reqPrinter;

	@Autowired
	private FinanceIntegrationServiceImpl financeService;
	
	@Autowired
	private SectionDataDetailsRepository sectionDataDetailsRepos;

	@Autowired
	private HomePositionMasterRepository homeRepo;

	@Autowired
	private IntegrationService service;

	@Autowired
	@Lazy
	private QuoteThreadService quoteThreadService;

	@PostMapping("/quote")
	public ResponseEntity<CommonRes> pushPremiaIntegeration(@RequestBody PremiaRequest req) {
		CommonRes data = new CommonRes();
		reqPrinter.reqPrint(req);
		HomePositionMaster home = null;
		String migrateYN = null;
		if (StringUtils.isBlank(req.getQuoteNo()) || req.getQuoteNo() == null) {
			home = homeRepo.findByPolicyNo(req.getPolicyNo());
			req.setQuoteNo(home.getQuoteNo());
		} else {
			home = homeRepo.findByQuoteNo(req.getQuoteNo());
		}
		// Referral-approved quotes never get uw_sys_id / ah_pol_idx, and finance
		// (wnt_acnt_hdr) needs both.
		// Runs in its own transaction, so the push below sees the ids.
		if (home.getUwsysId() == null) {
			try {
				quoteThreadService.QuoteGenerateUwsysIdAndPolIdxID(null, home.getQuoteNo());
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		Set<String> specialCompanyIds = Set.of("100046", "100047", "100048", "100049", "100050");
		if (specialCompanyIds.contains(home.getCompanyId())) {
			List<SectionDataDetails> sectionDataDetailsList = sectionDataDetailsRepos
					.findByQuoteNoOrderByRiskIdAsc(home.getQuoteNo());

			if (sectionDataDetailsList != null && !sectionDataDetailsList.isEmpty()) {
				migrateYN = sectionDataDetailsList.get(0).getMigratedPolicyYn();
			}
			PremiaResponse res = null;
			if (migrateYN != null && migrateYN.equalsIgnoreCase("Y")) {
				reqPrinter.reqPrint(req);
				CustomerCreationReq custReq = new CustomerCreationReq();
				custReq.setQuoteNo(req.getQuoteNo());
				financeService.financeIntegrationCustomerCreation(custReq);

				phoenixservice.pushPremiaIntegration(req);

				FinanceIntegerationBean account = new FinanceIntegerationBean();
				account.setQuoteNo(req.getQuoteNo());
				draftAccountEntryRequest(account);
				financeService.createFreshPolicy(account);
			} else {
				reqPrinter.reqPrint(req);
				CustomerCreationReq custReq = new CustomerCreationReq();
				custReq.setQuoteNo(req.getQuoteNo());
				financeService.financeIntegrationCustomerCreation(custReq);

				phoenixservice.pushPremiaIntegration(req);

				FinanceIntegerationBean account = new FinanceIntegerationBean();
				account.setQuoteNo(req.getQuoteNo());
				draftAccountEntryRequest(account);
				financeService.createFreshPolicy(account);
			}

			data.setCommonResponse(res);
			data.setIsError(false);
			data.setErrorMessage(Collections.emptyList());
			data.setMessage("Success");

			return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);

		} else {
			CustomerCreationReq custReq = new CustomerCreationReq();
			custReq.setQuoteNo(req.getQuoteNo());
			financeService.financeIntegrationCustomerCreation(custReq);
			PremiaResponse res = service.pushPremiaIntegration(req);
			data.setCommonResponse(res);
			data.setIsError(false);
			data.setErrorMessage(Collections.emptyList());
			data.setMessage("Success");

			FinanceIntegerationBean account = new FinanceIntegerationBean();
			account.setQuoteNo(req.getQuoteNo());
			draftAccountEntryRequest(account);
			financeService.createFreshPolicy(account);
			if (res != null) {
				return new ResponseEntity<CommonRes>(data, HttpStatus.CREATED);
			} else {
				return new ResponseEntity<>(null, HttpStatus.BAD_REQUEST);
			}
		}
	}

	@PostMapping("/hitByQuoteNo")
	public ResponseEntity<CommonRes> hitByQuoteNo(@RequestBody PremiaListRequest req) {

		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();

		PremiaResponse res = phoenixservice.hitByQuoteNo(req);
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

	@PostMapping("/marine")
	public ResponseEntity<CommonRes> pushPremiaMarineIntegeration(@RequestBody PremiaRequest req) {

		reqPrinter.reqPrint(req);
		CommonRes data = new CommonRes();

		PremiaResponse res = phoenixservice.pushPremiaMarineIntegeration(req);
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

	private void draftAccountEntryRequest(FinanceIntegerationBean account) {
		financeService.draftAccountEntryRequest(account);

	}

}
