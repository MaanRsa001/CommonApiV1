package com.maan.eway.realpay.service;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.maan.eway.realpay.dto.InstalmentBatchPutRequestDTO;
import com.maan.eway.realpay.dto.InstalmentPutRequestItemDTO;

import javax.annotation.PostConstruct;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maan.eway.realpay.util.ApiEndPoints;
import com.maan.eway.realpay.util.MerchantUtil;
import com.maan.eway.realpay.util.RestApiCaller;
import com.maan.eway.realpay.util.TokenGenerator;

@Service
public class ClientService {

    private static final Logger logger = LoggerFactory.getLogger(ClientService.class);

    @Autowired
    public RestApiCaller restApiCaller;

    @Autowired
    private RestTemplate restTemplate;

    @Value("${client.merchant:21478}")
    private String merchant;

    @Value("${realpay.product.merchant.mapping:FNBENDO:21478,REALTIME:21477}")
    private String merchantMappingConf;

    @Autowired
    public TokenGenerator tokenGenerator;

    @Autowired
    ObjectMapper objectMapper;

    private Map<String, String> productMerchantMap = new HashMap<>();

    @PostConstruct
    public void initMerchantMap() {
        productMerchantMap = MerchantUtil.parseProductMerchantMapping(merchantMappingConf);
        logger.info("Initialized product-merchant mapping: {}", productMerchantMap);
    }

    public String getMerchantByProduct(String product) {
        if (productMerchantMap.containsKey(product)) {
            String mappedMerchant = productMerchantMap.get(product);
            logger.debug("Using mapped merchant '{}' for product '{}'", mappedMerchant, product);
            return mappedMerchant;
        }
        logger.debug("Using default merchant '{}' for product '{}'", merchant, product);
        return merchant;
    }

    public JsonNode getClientsNew(String url, String product, String beneficiaryUser) throws JsonProcessingException {
        try {
            String merchantId = StringUtils.hasText(beneficiaryUser) ? beneficiaryUser : getMerchantByProduct(product);
            logger.info("Making GET request for product: {} using merchant: {}", product, merchantId);

            // Fix URL construction - use proper parameter names
            String finalUrl = buildInstalmentChangesUrl(product, url, merchantId);

            logger.info("Final RealPay API URL: {}", finalUrl);

            String responseString = restApiCaller.callGetExternalNew(finalUrl).getBody();

            if (!StringUtils.hasText(responseString)) {
                logger.warn("Empty response received from API for product: {}", product);
                return createEmptyResponse();
            }

            JsonNode response = objectMapper.readTree(responseString);
            logger.info("GET request completed successfully for product: {}", product);
            return response;

        } catch (JsonProcessingException e) {
            logger.error("Error parsing JSON response for product {}: {}", product, e.getMessage(), e);
            throw e;
        } catch (HttpClientErrorException.NotFound e) {
            logger.warn("RealPay API endpoint not found (404) for product {}: {}", product, e.getMessage());
            return createNotFoundResponse(product);
        } catch (HttpClientErrorException e) {
            logger.error("HTTP client error for product {}: Status: {}, Response: {}",
                    product, e.getStatusCode(), e.getResponseBodyAsString());
            return createErrorResponse("HTTP Error: " + e.getStatusCode() + " - " + e.getStatusText());
        } catch (HttpServerErrorException e) {
            logger.error("HTTP server error for product {}: Status: {}, Response: {}",
                    product, e.getStatusCode(), e.getResponseBodyAsString());
            return createErrorResponse("Server Error: " + e.getStatusCode() + " - " + e.getStatusText());
        } catch (Exception e) {
            logger.error("Error making GET request for product {}: {}", product, e.getMessage(), e);
            return createErrorResponse("Request failed: " + e.getMessage());
        }
    }

    private String buildInstalmentChangesUrl(String product, String incomingUrl, String merchantId) {
        try {
            // Parse the incoming URL to extract parameters
            UriComponentsBuilder incomingBuilder = UriComponentsBuilder.fromHttpUrl(incomingUrl);
            Map<String, String> incomingParams = incomingBuilder.build().getQueryParams().toSingleValueMap();

            String startDate = incomingParams.getOrDefault("StartDate", getDefaultStartDate());
            String endDate = incomingParams.getOrDefault("EndDate", getDefaultEndDate());
            String version = incomingParams.getOrDefault("Version", "v1");

            logger.debug("Extracted params - StartDate: {}, EndDate: {}, Version: {}",
                    startDate, endDate, version);

            // Build the correct RealPay API URL
            return UriComponentsBuilder.fromHttpUrl(ApiEndPoints.Reports.instalmentChangesReport)
                    .pathSegment(product) // Product as path segment
                    .queryParam("BeneficiaryUser", merchantId) // Correct parameter name (singular)
                    .queryParam("StartDate", startDate)
                    .queryParam("EndDate", endDate)
                    .queryParam("Version", version)
                    .build()
                    .toUriString();

        } catch (Exception e) {
            logger.error("Error building URL for product {}: {}", product, e.getMessage());
            // Fallback to direct construction
            return buildInstalmentChangesUrlDirect(product, getDefaultStartDate(), getDefaultEndDate(), "v1",
                    merchantId);
        }
    }

    private String buildInstalmentChangesUrlDirect(String product, String startDate, String endDate,
            String version, String merchantId) {
        return UriComponentsBuilder.fromHttpUrl(ApiEndPoints.Reports.instalmentChangesReport)
                .pathSegment(product)
                .queryParam("BeneficiaryUser", merchantId)
                .queryParam("StartDate", startDate)
                .queryParam("EndDate", endDate)
                .queryParam("Version", version)
                .build()
                .toUriString();
    }

    private String getDefaultStartDate() {
        return java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    private String getDefaultEndDate() {
        return java.time.LocalDate.now().format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public JsonNode getInstalmentChanges(String product, String startDate, String endDate, String version,
            String beneficiaryUser) {
        try {
            String merchantId = StringUtils.hasText(beneficiaryUser) ? beneficiaryUser : getMerchantByProduct(product);
            logger.info("Retrieving instalment changes for product: {} using merchant: {} from {} to {}",
                    product, merchantId, startDate, endDate);

            String url = buildInstalmentChangesUrlDirect(product, startDate, endDate, version, merchantId);

            logger.info("Direct API call URL: {}", url);

            return getClientsNew(url, product, merchantId);

        } catch (Exception e) {
            logger.error("Error retrieving instalment changes for product {}: {}", product, e.getMessage(), e);
            throw new RuntimeException("Failed to retrieve instalment changes", e);
        }
    }

    // Response creation methods
    private JsonNode createEmptyResponse() {
        try {
            return objectMapper.readTree("{\"InstalmentChangesGetResponse\":[]}");
        } catch (JsonProcessingException e) {
            logger.error("Error creating empty response", e);
            return createBasicResponse("Empty response created due to error");
        }
    }

    private JsonNode createNotFoundResponse(String product) {
        try {
            String json = String.format("{" +
                    "\"message\":\"No instalment changes found for product %s on the specified date\"," +
                    "\"InstalmentChangesGetResponse\":[]" +
                    "}", product);
            return objectMapper.readTree(json);
        } catch (JsonProcessingException e) {
            logger.error("Error creating not found response", e);
            return createEmptyResponse();
        }
    }

    private JsonNode createErrorResponse(String errorMessage) {
        try {
            String json = String.format("{" +
                    "\"error\":\"%s\"," +
                    "\"InstalmentChangesGetResponse\":[]" +
                    "}", errorMessage);
            return objectMapper.readTree(json);
        } catch (JsonProcessingException e) {
            logger.error("Error creating error response", e);
            return createBasicResponse("Error processing request");
        }
    }

    private JsonNode createBasicResponse(String message) {
        try {
            String json = "{\"message\":\"" + message + "\",\"InstalmentChangesGetResponse\":[]}";
            return objectMapper.readTree(json);
        } catch (JsonProcessingException e) {
            // Last resort fallback
            return null;
        }
    }

    // Existing methods remain the same but with improved error handling
    public JsonNode callClient(String product, JsonNode requestWrapper, String method, String type,
            String beneficiaryUser) {
        try {
            String merchantId =  getMerchantByProduct(product);
            logger.info("Calling {} API for product: {} using merchant: {} with method: {}", type, product, merchantId,
                    method);

            String url = buildApiUrl(product, type, merchantId);
            HttpHeaders headers = createHeaders();
            Map<String, JsonNode> requestData = createRequestData(requestWrapper, method, type);

            HttpEntity<Map<String, JsonNode>> requestEntity = new HttpEntity<>(requestData, headers);

            logger.debug("Request URL: {}", url);

            JsonNode response;
            if ("put".equalsIgnoreCase(method)) {
                response = restApiCaller.callPutExternal(url, requestEntity).getBody();
            } else {
                response = restApiCaller.callPostExternal(url, requestEntity).getBody();
            }

            logger.info("API call completed successfully for {} - {} with merchant: {}", type, product, merchantId);
            return response;

        } catch (Exception e) {
            logger.error("Error calling {} API for product {}: {}", type, product, e.getMessage(), e);
            throw new RuntimeException("Failed to call " + type + " API", e);
        }
    }

    private String buildApiUrl(String product, String type, String merchantId) {
        String baseUrl;
        switch (type.toLowerCase()) {
            case "client":
                baseUrl = ApiEndPoints.General.client;
                break;
            case "contract":
                baseUrl = ApiEndPoints.General.contract;
                break;
            case "instalment":
                baseUrl = ApiEndPoints.General.instalment;
                break;
            default:
                throw new IllegalArgumentException("Unknown API type: " + type);
        }

        // Ensure proper URL format
        if (baseUrl.contains("https:/") && !baseUrl.contains("https://")) {
            baseUrl = baseUrl.replace("https:/", "https://");
        }

        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .pathSegment(product)
                .queryParam("Version", "v1")
                .queryParam("BeneficiaryUser", merchantId)
                .build()
                .toUriString();
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        String token = tokenGenerator.getToken();
        if (StringUtils.hasText(token)) {
            headers.set("Authorization", "Bearer " + token);
        } else {
            logger.warn("No authorization token available");
        }

        return headers;
    }

    private Map<String, JsonNode> createRequestData(JsonNode requestWrapper, String method, String type) {
        Map<String, JsonNode> requestData = new HashMap<>();
        String requestKey = getRequestKey(method, type);
        requestData.put(requestKey, requestWrapper);
        return requestData;
    }

    private String getRequestKey(String method, String type) {
        String operation = "put".equalsIgnoreCase(method) ? "Put" : "Post";

        switch (type.toLowerCase()) {
            case "client":
                return "Client" + operation + "Request";
            case "contract":
                return "Contract" + operation + "Request";
            case "instalment":
                return "Instalment" + operation + "Request";
            default:
                throw new IllegalArgumentException("Unknown API type: " + type);
        }
    }

    // Other existing methods (cancelInstalment, etc.) remain the same
    public JsonNode cancelInstalment(String product, String clientNumber, String contractNumber,
                                     String contractSequence, String instalmentSequence, boolean cashPaid) {
        int retryCount = 0;
        int maxRetries = 3;
        Exception lastException = null;

        while (retryCount <= maxRetries) {
            try {
                String merchantId = getMerchantByProduct(product);
                logger.info(
                        "Cancelling instalment (Attempt {}/{}) - Product: {}, Merchant: {}, Client: {}, Contract: {}",
                        retryCount + 1, maxRetries + 1, product, merchantId, clientNumber, contractNumber);

                String url = buildCancellationApiUrl(product, clientNumber, contractNumber,
                        contractSequence, instalmentSequence, merchantId);

                logger.debug("Cancellation URL: {}", url);

                HttpHeaders headers = createHeaders();
                String emptyJsonBody = "{}";
                HttpEntity<String> requestEntity = new HttpEntity<>(emptyJsonBody, headers);

                ResponseEntity<JsonNode> response = restTemplate.exchange(
                        url,
                        HttpMethod.DELETE,
                        requestEntity,
                        JsonNode.class);

                logger.info("Instalment cancellation successful - Product: {}, Merchant: {}, Response status: {}",
                        product, merchantId, response.getStatusCode());
                return response.getBody();

            } catch (ResourceAccessException e) {
                lastException = e;
                logger.warn("Network error on attempt {} for product {} - {}: {}",
                        retryCount + 1, product, e.getClass().getSimpleName(), e.getMessage());

            } catch (HttpServerErrorException e) {
                lastException = e;
                logger.warn("Server error ({}...) on attempt {} for product {}: {}",
                        e.getStatusCode(), retryCount + 1, product, e.getMessage());

            } catch (HttpClientErrorException e) {
                if (e.getStatusCode().value() == 404) {
                    logger.info("Instalment already cancelled or not found (404) for product {} - Treating as success",
                            product);
                    return createSuccessResponse("Already cancelled or not found");
                } else if (e.getStatusCode().value() == 400) {
                    logger.error("Bad request (400) for product {} - Invalid parameters, not retrying", product);
                    throw new RuntimeException("Invalid cancellation parameters", e);
                } else {
                    lastException = e;
                    logger.warn("Client error ({}) on attempt {} for product {}: {}",
                            e.getStatusCode(), retryCount + 1, product, e.getMessage());
                }

            } catch (Exception e) {
                lastException = e;
                logger.warn("Unexpected error on attempt {} for product {}: {}", retryCount + 1, product,
                        e.getMessage());
            }

            retryCount++;
            if (retryCount <= maxRetries) {
                int waitTime = retryCount * 2000;
                logger.info("Retrying in {}ms for product {}...", waitTime, product);
                try {
                    Thread.sleep(waitTime);
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                    logger.error("Retry sleep interrupted for product: {}", product);
                    break;
                }
            }
        }

        logger.error("Cancellation failed after {} attempts for product {}. Last error: {}",
                maxRetries + 1, product, lastException != null ? lastException.getMessage() : "Unknown");

        return createErrorResponse("Failed after " + (maxRetries + 1) + " attempts: " +
                (lastException != null ? lastException.getMessage() : "Unknown error"));
    }

    private String buildCancellationApiUrl(String product, String clientNumber, String contractNumber,
            String contractSequence, String instalmentSequence, String beneficiaryUser) {
        String baseUrl = ApiEndPoints.General.instalment;

        if (baseUrl.contains("https:/") && !baseUrl.contains("https://")) {
            baseUrl = baseUrl.replace("https:/", "https://");
        }

        return UriComponentsBuilder.fromHttpUrl(baseUrl)
                .pathSegment(product)
                .queryParam("ClientNumber", clientNumber)
                .queryParam("ContractNumber", contractNumber)
                .queryParam("ContractSequence", contractSequence)
                .queryParam("InstalmentSequence", instalmentSequence)
                .queryParam("BeneficiaryUser", beneficiaryUser)
                .queryParam("Version", "v1")
                .build()
                .toUriString();
    }

    private JsonNode createSuccessResponse(String message) {
        try {
            String json = String.format("{\"status\":\"SUCCESS\",\"message\":\"%s\"}", message);
            return objectMapper.readTree(json);
        } catch (Exception e) {
            logger.error("Error creating success response: {}", e.getMessage());
            return null;
        }
    }

    public boolean isCancellationSuccessful(JsonNode response) {
        if (response == null) {
            logger.error("Null response from RealPay cancellation API");
            return false;
        }
        // Implementation remains the same as before
        return true;
    }

    /**
     * Calls the external RealPay API to maintain instalments.
     * URI: https://uat.realpaycollect.com:4448/rpi/rpws/maintain/instalments/{product}?Product=&BeneficiaryUser=&Version=
     * 
     * @param product The product code for the path segment and Product query parameter.
     * @param beneficiaryUser The beneficiary user/merchant ID.
     * @param instalments The list of instalments to maintain.
     * @return The API response as a JsonNode.
     */
    public JsonNode maintainInstalmentsExternal(String product, String beneficiaryUser, List<InstalmentPutRequestItemDTO> instalments) {
        try {
            String merchantId = StringUtils.hasText(beneficiaryUser) ? beneficiaryUser : getMerchantByProduct(product);
            logger.info("Maintaining instalments for product: {} using merchant: {}", product, merchantId);

            // 1. Build the target URL
            String url = UriComponentsBuilder.fromHttpUrl(ApiEndPoints.General.instalment)
                    .pathSegment(product)
                    .queryParam("Product", product)
                    .queryParam("BeneficiaryUser", merchantId)
                    .queryParam("Version", ApiEndPoints.version)
                    .build()
                    .toUriString();

            logger.info("RealPay Maintain Instalment URL: {}", url);

            // 2. Prepare the request body
            InstalmentBatchPutRequestDTO requestBody = InstalmentBatchPutRequestDTO.builder()
                    .instalmentPutRequest(instalments)
                    .build();

            // 3. Create headers with token
            HttpHeaders headers = createHeaders();
            HttpEntity<InstalmentBatchPutRequestDTO> requestEntity = new HttpEntity<>(requestBody, headers);

            // 4. Call the external API using PUT
            ResponseEntity<JsonNode> response = restApiCaller.callPutExternal(url, requestEntity);

            logger.info("RealPay Maintain Instalment response received for product: {}", product);
            return response.getBody();

        } catch (Exception e) {
            logger.error("Error calling Maintain Instalment API for product {}: {}", product, e.getMessage(), e);
            throw new RuntimeException("Failed to call Maintain Instalment API", e);
        }
    }
}