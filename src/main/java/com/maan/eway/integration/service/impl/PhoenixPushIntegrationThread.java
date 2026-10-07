package com.maan.eway.integration.service.impl;

import java.util.List;

import com.maan.eway.integration.req.PremiaRequest;
import com.maan.eway.integration.res.PremiaResponse;
import com.maan.eway.integration.service.PhoenixIntegrationService;

public class PhoenixPushIntegrationThread
    implements Runnable
{

    private PhoenixIntegrationService intService;
    private List<String> quoteNo;
    private List<String> premiaIds;

    public PhoenixPushIntegrationThread(PhoenixIntegrationService intService, List<String> quoteNo, List<String> premiaIds)
    {
        this.intService = intService;
        this.quoteNo = quoteNo;
        this.premiaIds = premiaIds;
    }

    public void run()
    {
        PremiaResponse response = new PremiaResponse();
        for(String q:quoteNo)
        {
            
            PremiaRequest request = new PremiaRequest();
            request.setQuoteNo(q);
            request.setPremiaIds(premiaIds);
            System.out.println((new StringBuilder("PremiaRequest ")).append(request).toString());
            response = intService.pushPremiaIntegration(request);
            System.out.println(response);
        }

    }
}