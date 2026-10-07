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

import com.fasterxml.jackson.databind.JsonNode;
import com.maan.eway.realpay.dto.ReceiptEntryReqDto;

import java.util.Collections;

/**
 * Service responsible for calling the Finance RealPay Receipt Entry API.
 * Endpoint: POST /financeRealpay/recieptEntry
 */
@Service
public class ReceiptEntryService {

    private static final Logger logger = LoggerFactory.getLogger(ReceiptEntryService.class);

    @Value("${finace.recieptEntry}")
    private String recieptEntryUrl;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private TokenGeneratorForEway tokenGeneratorForEway;


    /**
     * Calls the finance receipt entry API using the given DTO.
     *
     * @param req DTO containing all required fields for the receipt entry request
     * @return ResponseEntity containing the API JSON response, or null on failure
     */
    public void callRecieptEntry(ReceiptEntryReqDto req) {

        logger.info("Calling Receipt Entry API - PolicyNo: {}, QuoteNo: {}, Instalment: {}, PaymentId: {}",
                req.getPolicyNo(), req.getQuoteNo(), req.getInstalment(), req.getPaymentId());

        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
            headers.setBearerAuth(tokenGeneratorForEway.getToken());

            HttpEntity<ReceiptEntryReqDto> httpEntity = new HttpEntity<>(req, headers);

            logger.debug("Receipt Entry Request URL : {}", recieptEntryUrl);
            logger.debug("Receipt Entry Request Body: {}", req);

            ResponseEntity<JsonNode> response = restTemplate.postForEntity(
                    recieptEntryUrl, httpEntity, JsonNode.class);

            logger.info("Receipt Entry API response - Status: {}, Body: {}",
                    response.getStatusCode(), response.getBody());

        } catch (Exception e) {
            logger.error("Error calling Receipt Entry API for PolicyNo: {}, QuoteNo: {} - {}",
                    req.getPolicyNo(), req.getQuoteNo(), e.getMessage(), e);
        }
    }
}

