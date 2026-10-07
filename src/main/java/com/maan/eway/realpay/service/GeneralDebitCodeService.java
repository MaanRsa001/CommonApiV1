package com.maan.eway.realpay.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.maan.eway.realpay.util.ApiEndPoints;
import com.maan.eway.realpay.util.RestApiCaller;

@Service
public class GeneralDebitCodeService {
    @Autowired
    public RestApiCaller restApiCaller;

    @Value("${client.merchant}")
    private String merchant;

    @Autowired
    private ClientService clientService; // Inject ClientService

    // EXISTING METHODS - Updated to use dynamic merchant
    public String getDebitSequenceTypes(String product) {
        String merchantId = clientService.getMerchantByProduct(product);
        String url = ApiEndPoints.General.debitSequenceTypes+"/"+product+"?Version=v1&BeneficiaryUser="+merchantId;
        return restApiCaller.callGetExternal(url).getBody();
    }

    public String getMandateStatuses(String product) {
        String merchantId = clientService.getMerchantByProduct(product);
        String url = ApiEndPoints.General.mandateStatuses+"/"+product+"?Version=v1&BeneficiaryUser="+merchantId;
        return restApiCaller.callGetExternal(url).getBody();
    }

    public String getAdjustmentCategories(String product) {
        String merchantId = clientService.getMerchantByProduct(product);
        String url = ApiEndPoints.General.adjustmentCategories+"/"+product+"?Version=v1&BeneficiaryUser="+merchantId;
        return restApiCaller.callGetExternal(url).getBody();
    }

    public String getMandateReasonCodes(String product) {
        String merchantId = clientService.getMerchantByProduct(product);
        String url = ApiEndPoints.General.mandateReasonCodes+"/"+product+"?Version=v1&BeneficiaryUser="+merchantId;
        return restApiCaller.callGetExternal(url).getBody();
    }

    public String getMandateHistoryActions(String product) {
        String merchantId = clientService.getMerchantByProduct(product);
        String url = ApiEndPoints.General.mandateHistoryActions+"/"+product+"?Version=v1&BeneficiaryUser="+merchantId;
        return restApiCaller.callGetExternal(url).getBody();
    }

    public String getPayoutCancelCodes(String product) {
        String merchantId = clientService.getMerchantByProduct(product);
        String url = ApiEndPoints.General.payoutCancelCodes+"/"+product+"?Version=v1&BeneficiaryUser="+merchantId;
        return restApiCaller.callGetExternal(url).getBody();
    }
}