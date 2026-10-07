package com.maan.eway.integration.service;

import com.maan.eway.integration.req.PremiaListRequest;
import com.maan.eway.integration.req.PremiaRequest;
import com.maan.eway.integration.res.PremiaResponse;

public interface IntegrationService {
	PremiaResponse pushPremiaIntegration(PremiaRequest request);

	PremiaResponse hitByQuoteNo(PremiaListRequest req);

	PremiaResponse pushPremiaMarineIntegeration(PremiaRequest req);
}
