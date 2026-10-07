package com.maan.eway.integration.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.maan.eway.integration.req.PremiaRequest;
import com.maan.eway.integration.res.PremiaResponse;
import com.maan.eway.integration.service.PhoenixIntegrationService;

public class PushPremiaThread implements Runnable {

	private PhoenixIntegrationService intService;

	private String quoteNo;
	private String companyId;
	private String policyNo;

	public PushPremiaThread(String policyNo, String quoteNo, String companyId) {
		this.policyNo = policyNo;
		this.quoteNo = quoteNo;
		this.companyId = companyId;
	}

	@Override
	public void run() {
		PremiaResponse response = new PremiaResponse();
		PremiaRequest request = new PremiaRequest();
		List<String> premiaIds = new ArrayList<>();
		premiaIds.add("1");
		premiaIds.add("2");
		premiaIds.add("3");
		premiaIds.add("4");
		premiaIds.add("5");
		premiaIds.add("6");
		premiaIds.add("7");
		premiaIds.add("8");
		premiaIds.add("9");
		premiaIds.add("10");
		premiaIds.add("11");

		request.setQuoteNo(quoteNo);
		request.setPolicyNo(policyNo);
		request.setCompanyId(companyId);
		request.setPremiaIds(premiaIds);
		System.out.println((new StringBuilder("PremiaRequest ")).append(request).toString());
		response = intService.pushPremiaIntegration(request);
		System.out.println(response);

	}

}
