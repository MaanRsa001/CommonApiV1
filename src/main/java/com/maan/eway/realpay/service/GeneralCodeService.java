package com.maan.eway.realpay.service;

import java.util.Collections;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.realpay.util.ApiEndPoints;
import com.maan.eway.realpay.util.RestApiCaller;

@Service
public class GeneralCodeService {
    @Autowired
    public RestApiCaller restApiCaller;

    @Value("${client.merchant}")
    private String merchant;

    @Value("${realpay.product.merchant.mapping:}")
    private String merchantMappingConf;

    @Autowired
    private ClientService clientService; 

    private CommonRes parseResponseToCommonRes(ResponseEntity<String> responseEntity) {
        String responseBody = responseEntity.getBody();
        ObjectMapper mapper = new ObjectMapper();
        CommonRes commonRes = new CommonRes();
        
        try {
            Object jsonObj = mapper.readValue(responseBody, Object.class);
            commonRes.setCommonResponse(jsonObj);
            commonRes.setIsError(false);
            commonRes.setErrorMessage(Collections.emptyList());
            commonRes.setMessage("Success");
        } catch (Exception e) {
            commonRes.setIsError(true);
            commonRes.setMessage("Error parsing response");
        }
        return commonRes;
    }

    // EXISTING METHODS - Updated to accept product parameter and use dynamic merchant
    public CommonRes getFrequencyCodes(String product) {
        String merchantId = clientService.getMerchantByProduct(product);
        String url = ApiEndPoints.General.frequencyCodes + "?Version=v1&BeneficiaryUser=" + merchantId;
        return parseResponseToCommonRes(restApiCaller.callGetExternal(url));
    }

    public CommonRes getProducts(String product) {
        String merchantId = clientService.getMerchantByProduct(product);
        String url = ApiEndPoints.General.products + "?Version=v1&BeneficiaryUser=" + merchantId;
        return parseResponseToCommonRes(restApiCaller.callGetExternal(url));
    }

    public CommonRes getBanks(String product) {
        String merchantId = clientService.getMerchantByProduct(product);
        String url = ApiEndPoints.General.banks + "/" + product + "?Version=v1&BeneficiaryUser=" + merchantId;
        return parseResponseToCommonRes(restApiCaller.callGetExternal(url));
    }

    public CommonRes getTracking(String product) {
        String merchantId = clientService.getMerchantByProduct(product);
        String url = ApiEndPoints.General.tracking + "/" + product + "?Version=v1&BeneficiaryUser=" + merchantId;
        return parseResponseToCommonRes(restApiCaller.callGetExternal(url));
    }

    public CommonRes getCollection(String product) {
        String merchantId = clientService.getMerchantByProduct(product);
        String url = ApiEndPoints.General.collectionStatuses + "?Version=v1&BeneficiaryUser=" + merchantId;
        return parseResponseToCommonRes(restApiCaller.callGetExternal(url));
    }

    public CommonRes getBankResponses(String product) {
        String merchantId = clientService.getMerchantByProduct(product);
        String url = ApiEndPoints.General.bankResponses + "/" + product + "?Version=v1&BeneficiaryUser=" + merchantId;
        return parseResponseToCommonRes(restApiCaller.callGetExternal(url));
    }

    public CommonRes getEmployeeGroups(String product) {
        String merchantId = clientService.getMerchantByProduct(product);
        String url = ApiEndPoints.General.employeeGroups + "?Version=v1&BeneficiaryUser=" + merchantId;
        return parseResponseToCommonRes(restApiCaller.callGetExternal(url));
    }

    public CommonRes getTransactionTypes(String product) {
        String merchantId = clientService.getMerchantByProduct(product);
        String url = ApiEndPoints.General.transactionTypes + "/" + product + "?Version=v1&BeneficiaryUser=" + merchantId;
        return parseResponseToCommonRes(restApiCaller.callGetExternal(url));
    }

    public CommonRes getBeneficiaryUsers() {
        String url = ApiEndPoints.General.beneficiaryUsers + "?Version=v1";
        return parseResponseToCommonRes(restApiCaller.callGetExternal(url));
    }
}