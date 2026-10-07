package com.maan.eway.realpay.service;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.maan.eway.realpay.dto.EndorsementResBean;
import com.maan.eway.realpay.dto.GetPolicyDetailsReqDto;

import java.util.Collections;

/**
 * Service responsible for calling the Finance getPolicyDetails API.
 * Endpoint: POST /finance/getPolicyDetails
 */
@Service
public class PolicyDetailsFromFinance {

    private static final Logger logger = LoggerFactory.getLogger(PolicyDetailsFromFinance.class);

    @Value("${finace.getPolicyDetails}")
    private String getPolicyDetailsUrl;

    @Autowired
    private RestTemplate restTemplate;
    @Autowired
    private TokenGeneratorForEway tokenGeneratorForEway;


    /**
     * Calls the finance getPolicyDetails API using the given DTO.
     *
     * @param req DTO containing PolicyNo, QuoteNo, InsuranceId and ProductId
     * @return EndorsementResBean parsed from the API response, or null on failure
     */
    public EndorsementResBean callGetPolicyDetails(GetPolicyDetailsReqDto req) {

        logger.info("Calling GetPolicyDetails API - PolicyNo: {}, QuoteNo: {}, InsuranceId: {}, ProductId: {}",
                req.getPolicyNo(), req.getQuoteNo(), req.getInsuranceId(), req.getProductId());

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.setBearerAuth(tokenGeneratorForEway.getToken());

            HttpEntity<GetPolicyDetailsReqDto> httpEntity = new HttpEntity<>(req, headers);

            logger.info("GetPolicyDetails Request URL : {}", getPolicyDetailsUrl);
            logger.info("GetPolicyDetails Request Body: {}", req);

            ResponseEntity<EndorsementResBean> response = restTemplate.postForEntity(
                    getPolicyDetailsUrl, httpEntity, EndorsementResBean.class);

            logger.info("GetPolicyDetails API response - Status: {}, Body: {}",
                    response.getStatusCode(), response.getBody());

            return response.getBody();

        } catch (Exception e) {
            logger.error("Error calling GetPolicyDetails API for PolicyNo: {}, QuoteNo: {} - {}",
                    req.getPolicyNo(), req.getQuoteNo(), e.getMessage(), e);
            return null;
        }
    }
}
