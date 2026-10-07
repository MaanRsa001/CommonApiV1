package com.maan.eway.integration.service.impl;


import com.maan.eway.integration.req.PremiaRequest;
import com.maan.eway.integration.res.PremiaResponse;
import com.maan.eway.integration.service.IntegrationService;
import java.util.Iterator;
import java.util.List;

public class PushIntegrationThread
    implements Runnable
{

    private IntegrationService intService;
    private List<String> quoteNo;
    private List<String> premiaIds;

    public PushIntegrationThread(IntegrationService intService, List<String> quoteNo, List<String> premiaIds)
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