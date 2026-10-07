package com.maan.eway.service.impl;

import org.springframework.stereotype.Service;

import com.maan.eway.bean.ApiIntegMaster;
import com.maan.eway.repository.ApiIntegMasterRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ApiIntegrationService {

    private final ApiIntegMasterRepository repository;

    public String getApiUrl(String companyId,
                            Integer productId,
                            String apiType) {

        return repository
                .findByCompanyIdAndProductIdAndApiTypeAndStatus(
                        companyId,
                        productId,
                        apiType,
                        "Y")
                .map(ApiIntegMaster::getApiUrl)
                .orElseThrow(() ->
                        new RuntimeException("API URL not configured : " + apiType));
    }
}
