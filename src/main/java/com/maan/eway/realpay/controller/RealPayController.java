package com.maan.eway.realpay.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maan.eway.realpay.service.ClientService;
import com.maan.eway.realpay.service.InstalmentStatusService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/realpay")
public class RealPayController {

    private static final Logger logger = LoggerFactory.getLogger(RealPayController.class);

    @Autowired
    private ClientService clientService;

    @Autowired
    private InstalmentStatusService instalmentStatusService;

    @Autowired
    private ObjectMapper objectMapper;

    @GetMapping("/instalmentChangesReport")
    public ResponseEntity<?> getInstalmentChangesReport(
            @RequestParam String product,
            @RequestParam String startDate,
            @RequestParam String endDate,
            @RequestParam(defaultValue = "v1") String version,
            @RequestParam(required = false, name = "x-beneficiary-user") String beneficiaryUser,
            @RequestHeader("Authorization") String token) {

        logger.info(
                "Processing instalment changes report for product: {}, dates: {} to {}, version: {}, beneficiaryUser: {}",
                product, startDate, endDate, version, beneficiaryUser);

        try {
            // Call the service method with correct parameters
            JsonNode response = clientService.getInstalmentChanges(product, startDate, endDate, version,
                    beneficiaryUser);

            // Log the response for debugging
            if (response != null) {
                logger.debug("RealPay API response for product {}: {}", product, response.toString());

                if (response.has("InstalmentChangesGetResponse")) {
                    // Process the response asynchronously if needed
                    JsonNode changesResponse = response.get("InstalmentChangesGetResponse");
                    if (changesResponse != null && changesResponse.size() > 0) {
                        logger.info("Found {} instalment changes for product {}", changesResponse.size(), product);
                        instalmentStatusService.processInstalmentChangesAsync(changesResponse, token, product);
                    } else {
                        logger.info("No instalment changes found for product {}", product);
                    }
                }
            }

            return ResponseEntity.ok().body(response);

        } catch (Exception e) {
            logger.error("Error processing instalment changes report for product {}: {}", product, e.getMessage(), e);
            return ResponseEntity.internalServerError().body(createErrorResponse(e.getMessage()));
        }
    }

    private JsonNode createErrorResponse(String message) {
        try {
            String json = String.format("{" +
                    "\"Message\":\"Unexpected Error Occurred\"," +
                    "\"IsError\":true," +
                    "\"ErrorMessage\":[{\"Code\":\"INTERNAL_ERROR\",\"Message\":\"%s\"}]," +
                    "\"Result\":null," +
                    "\"ErrorCode\":500" +
                    "}", message);
            return objectMapper.readTree(json);
        } catch (JsonProcessingException e) {
            logger.error("Error creating error response JSON", e);
            // Return a simple error response as fallback
            try {
                return objectMapper.readTree("{\"error\":\"Internal Server Error\"}");
            } catch (JsonProcessingException ex) {
                return null;
            }
        }
    }
}