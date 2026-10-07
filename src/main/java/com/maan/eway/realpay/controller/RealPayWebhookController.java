package com.maan.eway.realpay.controller;

import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.*;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.maan.eway.realpay.service.*;
import jakarta.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.maan.eway.realpay.util.TokenGenerator;

@RestController
@RequestMapping("/realpay/webhook")
public class RealPayWebhookController {

    private static final Logger logger =
            LoggerFactory.getLogger(RealPayWebhookController.class);

    // UAT HMAC secret from RealPay
    @Value("${realpay.webhook.hmac.secret}")
    private String hmacSecretKey;

    @Value("${realpay.webhook.hmac.validation.enabled:true}")
    private boolean hmacValidationEnabled;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private WebhookProcessingService webhookProcessingService;

    @Autowired
    private InstalmentStatusService instalmentStatusService;

    @Autowired
    private TokenGenerator tokenGenerator;

    @Autowired
    private PremiaPushIntegration premiaPushIntegration;

    @Autowired
    private HttpServletRequest httpServletRequest;

    @Autowired
    private ReceiptGenerator receiptGenerator;

    private static final class ResponseGroups {
        static final Set<String> GROUP1_INSUFFICIENT_FUNDS_NAMIBIA =
                Set.of("TR76", "00002", "AM04", "RR10");
        static final Set<String> GROUP2_PERMANENT_CLOSURE_NAMIBIA =
                Set.of("MD07", "UN26", "00012", "AC05", "AC06",
                       "UN03", "UN06", "UN08", "UN10", "RR15", "RR06");
        static final Set<String> GROUP3_NEW_MANDATE_NAMIBIA =
                Set.of("NA04", "NA28", "NA30", "NA32", "NA34", "NA36", "MD05");
        static final Set<String> GROUP4_DISPUTE_NAMIBIA =
                Set.of("RR16");
        static final Set<String> GROUP5_TECHNICAL_ERRORS_NAMIBIA =
                Set.of("AC02", "AC03", "AC08", "AC12", "AC13", "AG08",
                       "AM09", "AM10", "AM11", "AM12", "AM13", "AM14", "AM16",
                       "AM19", "BE08", "BE09", "BE10", "BE11", "BE18", "BE22",
                       "CH04", "CL03", "CURR", "DT01", "DT03", "DU01", "DU03",
                       "DU04", "ED06", "FF01", "FF04", "FF05", "FF06", "FF08",
                       "FF10", "RC06", "RC07", "RR02", "RR07", "RR09", "SL13",
                       "TR25", "TR26", "TR27", "TR28", "TR29", "TR30", "TR31",
                       "TR32", "TR33", "TR34", "TR35", "TR36", "TR37", "TR51",
                       "TR52", "TR64", "TR66", "TR74", "TR75", "ACTC", "ACSC",
                       "ACSP", "ACWC", "ACCP", "PDNG", "PART", "RJCT",
                       "MD01", "MD02");

        static final Set<String> GROUP1_INSUFFICIENT_FUNDS_ESWATINI =
                Set.of("02");
        static final Set<String> GROUP2_PERMANENT_CLOSURE_ESWATINI =
                Set.of("45", "12", "26", "06", "50");
        static final Set<String> GROUP3_NEW_MANDATE_ESWATINI =
                Set.of();
        static final Set<String> GROUP4_DISPUTE_ESWATINI =
                Set.of();
        static final Set<String> GROUP5_TECHNICAL_ERRORS_ESWATINI =
                Set.of("39", "72", "04", "6", "34", "36", "41", "44", "46",
                       "47", "48", "55", "60", "61", "95", "64", "65", "67",
                       "71", "38", "40", "42", "43", "49", "54", "57", "58",
                       "59", "62", "68", "70", "30", "31", "33", "74", "75",
                       "76", "77");
    }

    @PostMapping("/callback")
    public ResponseEntity<Void> handleWebhookCallback(
            @RequestHeader(value = "x-hmac", required = false) String receivedHmac,
            @RequestHeader(value = "x-callback", required = false) String callbackType,
            @RequestHeader(value = "x-beneficiary-user", required = false) String beneficiaryUser,
            @RequestBody String rawPayload) {

        long startTime = System.currentTimeMillis();
        String webhookId = generateWebhookId();

        logRequestDebugInfo(webhookId, receivedHmac, callbackType, beneficiaryUser, rawPayload);

        try {
            boolean hmacValid = true;

            if (hmacValidationEnabled) {
                if (receivedHmac == null || receivedHmac.isEmpty()) {
                    logger.error("WEBHOOK_HMAC_MISSING - ID: {}", webhookId);
                    hmacValid = false;
                    webhookProcessingService.updateWebhookStatus(
                            webhookId, "REJECTED", "Missing HMAC signature");
                } else if (!validateHmacSignature(webhookId, rawPayload, receivedHmac)) {
                    logger.error("WEBHOOK_HMAC_VALIDATION_FAILED - ID: {}", webhookId);
                    hmacValid = false;
                    webhookProcessingService.updateWebhookStatus(
                            webhookId, "REJECTED", "Invalid HMAC signature");
                } else {
                    logger.info("WEBHOOK_HMAC_VALIDATED - ID: {}", webhookId);
                }
            }

            JsonNode payload;
            try {
                payload = objectMapper.readTree(rawPayload);
            } catch (Exception e) {
                logger.error("WEBHOOK_JSON_PARSE_ERROR - ID: {}, Error: {}",
                        webhookId, e.getMessage(), e);
                webhookProcessingService.updateWebhookStatus(
                        webhookId, "ERROR", "JSON parse error: " + e.getMessage());
                return ResponseEntity.ok().build();
            }

            webhookProcessingService.storeWebhookMessageAsync(
                    webhookId, callbackType, beneficiaryUser, rawPayload, payload);

            if (hmacValid) {
                String normalizedType = callbackType == null ? "" : callbackType.trim().toUpperCase();
                if ("INSTALMENT".equals(normalizedType)) {
                    processInstalmentCallback(payload, webhookId);

                    /// calling push Integeration and Reciept Generation...
                    JsonNode instalmentResponse = payload.get("InstalmentGetResponse");
                    if (instalmentResponse.isArray() && !instalmentResponse.isEmpty()) {
                        JsonNode instalment = instalmentResponse.get(0);

                        String instalmentReferenceNumber =
                                instalment.has("InstalmentReferenceNumber")
                                        ? instalment.get("InstalmentReferenceNumber").asText()
                                        : "UNKNOWN";
                        String instalmentStatus = instalment.has("InstalmentStatus")
                                ? instalment.get("InstalmentStatus").asText()
                                : "UNKNOWN";

                        if(instalmentStatus.equals("S")){
                            /// A way to commit the transaction before calling the Receipt generation API ...
                            premiaPushIntegration.callPremiaPushIntegration(instalmentReferenceNumber,true);
                            instalmentStatusService.createEntryForSuccessEmail(instalmentReferenceNumber);
                            logger.info("Triggered push integration and Receipt generation for {} at {}",instalmentReferenceNumber, LocalDateTime.now());
                        }

                    }

                } else if ("CONTRACT".equals(normalizedType)) {
                    processContractCallback(payload, webhookId);
                } else if ("MANDATE".equals(normalizedType)) {
                    processMandateCallback(payload, webhookId);
                } else {
                    logger.warn("WEBHOOK_UNKNOWN_TYPE - ID: {}, Type: {}",
                            webhookId, callbackType);
                    webhookProcessingService.updateWebhookStatus(
                            webhookId, "UNKNOWN_TYPE",
                            "Unknown callback type: " + callbackType);
                }
            } else {
                logger.warn("WEBHOOK_SKIPPED_DUE_TO_INVALID_HMAC - ID: {}", webhookId);
            }

            long duration = System.currentTimeMillis() - startTime;
            logger.info("WEBHOOK_PROCESSED - ID: {}, Duration: {} ms",
                    webhookId, duration);
            return ResponseEntity.ok().build();

        } catch (Exception e) {
            logger.error("WEBHOOK_PROCESSING_ERROR - ID: {}, Error: {}",
                    webhookId, e.getMessage(), e);
            webhookProcessingService.updateWebhookStatus(
                    webhookId, "ERROR", "Processing error: " + e.getMessage());
            return ResponseEntity.ok().build();
        }
    }

    @PostMapping("/receipt/generate")
    public void triggerReceiptGenerate(@RequestParam String instalmentReference){
         receiptGenerator.callPushIntegrationAndReciptGenerationAPI(instalmentReference);
    }

    @PostMapping("/EFT/Success")
    public ResponseEntity<?> handleEFTSuccess(@RequestParam String quoteNo,@RequestParam String companyId,
                                              @RequestParam String installmentMonth, @RequestParam BigDecimal amount) {
        return instalmentStatusService.handleEFTSuccess(quoteNo,companyId,installmentMonth,amount);
    }

    @GetMapping("/EFT/Enabled")
    public ResponseEntity<?> checkEftEnabled(@RequestParam String quoteNo,
                                              @RequestParam String installmentMonth) {
        return instalmentStatusService.checkEFTEnabled(quoteNo,installmentMonth);
    }

    private void logRequestDebugInfo(String webhookId,
                                     String receivedHmac,
                                     String callbackType,
                                     String beneficiaryUser,
                                     String rawPayload) {
        logger.info("WEBHOOK_RECEIVED - ID: {}, Type: {}, Beneficiary: {}",
                webhookId, callbackType, beneficiaryUser);

        StringBuilder headerBuilder = new StringBuilder();
        Enumeration<String> headerNames = httpServletRequest.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String name = headerNames.nextElement();
            String value = httpServletRequest.getHeader(name);
            headerBuilder.append(name).append(": ").append(value).append(" | ");
        }
        logger.info("WEBHOOK_HEADERS - ID: {}, Headers: {}",
                webhookId, headerBuilder.toString());

        logger.info("WEBHOOK_META - ID: {}, Method: {}, URI: {}, RemoteAddr: {}",
                webhookId,
                httpServletRequest.getMethod(),
                httpServletRequest.getRequestURI(),
                httpServletRequest.getRemoteAddr());

        logger.info("WEBHOOK_RAW_PAYLOAD_LENGTH - ID: {}, Length: {}",
                webhookId, rawPayload != null ? rawPayload.length() : 0);

        if (rawPayload != null && rawPayload.length() <= 4000) {
            logger.info("WEBHOOK_RAW_PAYLOAD - ID: {}, Body: {}",
                    webhookId, rawPayload);
        }

        logger.info("WEBHOOK_RECEIVED_HMAC - ID: {}, x-hmac: {}",
                webhookId, receivedHmac);
    }

    private boolean validateHmacSignature(String webhookId,
                                          String payload,
                                          String receivedHmac) {
        try {
            if (hmacSecretKey == null || hmacSecretKey.isEmpty()) {
                logger.error("HMAC_SECRET_KEY_NOT_CONFIGURED - ID: {}", webhookId);
                return false;
            }

            logger.info("HMAC_INPUT_DEBUG - ID: {}, PayloadLength: {}",
                    webhookId, payload != null ? payload.length() : 0);

            String calculatedHmac = calculateHmacMd5(payload, hmacSecretKey);

            logger.info("HMAC_VALUES - ID: {}, Calculated: {}, Received: {}",
                    webhookId, calculatedHmac, receivedHmac);

            boolean isValid = equalsConstantTime(
                    calculatedHmac.toUpperCase(),
                    receivedHmac.trim().toUpperCase());

            if (!isValid) {
                byte[] payloadBytes = payload.getBytes(StandardCharsets.UTF_8);
                logger.info("HMAC_PAYLOAD_BYTES_HEX - ID: {}, Bytes: {}",
                        webhookId, bytesToHex(payloadBytes));
            }

            return isValid;
        } catch (Exception e) {
            logger.error("HMAC_CALCULATION_ERROR - ID: {}, Error: {}",
                    webhookId, e.getMessage(), e);
            return false;
        }
    }

    private String calculateHmacMd5(String data, String key) throws Exception {
        Mac mac = Mac.getInstance("HmacMD5");
        SecretKeySpec secretKeySpec =
                new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacMD5");
        mac.init(secretKeySpec);
        byte[] hmacBytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return bytesToHex(hmacBytes);
    }

    private String bytesToHex(byte[] bytes) {
        StringBuilder sb = new StringBuilder(bytes.length * 2);
        for (byte b : bytes) {
            sb.append(String.format("%02X", b));
        }
        return sb.toString();
    }

    private boolean equalsConstantTime(String a, String b) {
        if (a == null || b == null) return false;
        if (a.length() != b.length()) return false;
        int result = 0;
        for (int i = 0; i < a.length(); i++) {
            result |= a.charAt(i) ^ b.charAt(i);
        }
        return result == 0;
    }

    private void processInstalmentCallback(JsonNode payload, String webhookId) {
        try {
            logger.info("WEBHOOK_INSTALMENT_PROCESSING - ID: {}", webhookId);

            if (payload.has("InstalmentGetResponse")) {
                JsonNode instalmentResponse = payload.get("InstalmentGetResponse");
                if (instalmentResponse.isArray() && instalmentResponse.size() > 0) {
                    JsonNode instalment = instalmentResponse.get(0);

                    String instalmentReferenceNumber =
                            instalment.has("InstalmentReferenceNumber")
                                    ? instalment.get("InstalmentReferenceNumber").asText()
                                    : "UNKNOWN";

                    String responseCode =
                            instalment.has("ResponseCode")
                                    ? instalment.get("ResponseCode").asText()
                                    : null;

                    String product = detectProductFromResponseCode(responseCode);

                    logger.info("WEBHOOK_INSTALMENT_DETAILS - ID: {}, Ref: {}, Product: {}, ResponseCode: {}",
                            webhookId, instalmentReferenceNumber, product, responseCode);

                    String token = getAuthToken();
                    if (token == null) {
                        logger.error("WEBHOOK_AUTH_TOKEN_FAILED - ID: {}", webhookId);
                        webhookProcessingService.updateWebhookStatus(
                                webhookId, "ERROR", "Failed to generate token");
                        return;
                    }

                    instalmentStatusService
                            .processSingleInstalmentFromWebhook(instalment, token, product);

                    webhookProcessingService.updateWebhookStatus(
                            webhookId, "PROCESSED",
                            "Successfully processed instalment " + instalmentReferenceNumber +
                                    " Product: " + product);
                } else {
                    webhookProcessingService.updateWebhookStatus(
                            webhookId, "FAILED", "Empty InstalmentGetResponse array");
                }
            } else {
                webhookProcessingService.updateWebhookStatus(
                        webhookId, "FAILED", "Missing InstalmentGetResponse");
            }
        } catch (Exception e) {
            logger.error("WEBHOOK_INSTALMENT_PROCESSING_ERROR - ID: {}, Error: {}",
                    webhookId, e.getMessage(), e);
            webhookProcessingService.updateWebhookStatus(
                    webhookId, "ERROR", e.getMessage());
        }
    }

    private void processContractCallback(JsonNode payload, String webhookId) {
        try {
            logger.info("WEBHOOK_CONTRACT_PROCESSING - ID: {}", webhookId);

            if (payload.has("ContractGetResponse")) {
                JsonNode contractResponse = payload.get("ContractGetResponse");
                if (contractResponse.isArray() && contractResponse.size() > 0) {
                    JsonNode contract = contractResponse.get(0);

                    String contractNumber =
                            contract.has("ContractNumber")
                                    ? contract.get("ContractNumber").asText()
                                    : "UNKNOWN";

                    logger.info("WEBHOOK_CONTRACT_DETAILS - ID: {}, Contract: {}",
                            webhookId, contractNumber);

                    String token = getAuthToken();
                    if (token == null) {
                        logger.error("WEBHOOK_AUTH_TOKEN_FAILED - ID: {}", webhookId);
                        webhookProcessingService.updateWebhookStatus(
                                webhookId, "ERROR", "Failed to generate token");
                        return;
                    }

                    if (contract.has("ContractInstalments")) {
                        JsonNode instalments = contract.get("ContractInstalments");
                        if (instalments.isArray()) {
                            int processedCount = 0;
                            for (JsonNode instalment : instalments) {
                                String responseCode =
                                        instalment.has("ResponseCode")
                                                ? instalment.get("ResponseCode").asText()
                                                : null;
                                String product = detectProductFromResponseCode(responseCode);
                                instalmentStatusService
                                        .processSingleInstalmentFromWebhook(instalment, token, product);
                                processedCount++;
                            }
                            webhookProcessingService.updateWebhookStatus(
                                    webhookId, "PROCESSED",
                                    "Processed " + processedCount +
                                            " instalments from contract " + contractNumber);
                        } else {
                            webhookProcessingService.updateWebhookStatus(
                                    webhookId, "FAILED", "ContractInstalments not array");
                        }
                    } else {
                        webhookProcessingService.updateWebhookStatus(
                                webhookId, "FAILED",
                                "Missing ContractInstalments in contract " + contractNumber);
                    }
                } else {
                    webhookProcessingService.updateWebhookStatus(
                            webhookId, "FAILED", "Empty ContractGetResponse array");
                }
            } else {
                webhookProcessingService.updateWebhookStatus(
                        webhookId, "FAILED", "Missing ContractGetResponse");
            }
        } catch (Exception e) {
            logger.error("WEBHOOK_CONTRACT_PROCESSING_ERROR - ID: {}, Error: {}",
                    webhookId, e.getMessage(), e);
            webhookProcessingService.updateWebhookStatus(
                    webhookId, "ERROR", e.getMessage());
        }
    }

    private void processMandateCallback(JsonNode payload, String webhookId) {
        logger.info("WEBHOOK_MANDATE_PROCESSING - ID: {}", webhookId);
        webhookProcessingService.updateWebhookStatus(
                webhookId, "STORED", "Mandate callback stored");
    }

    private String detectProductFromResponseCode(String responseCode) {
        if (responseCode == null || responseCode.isEmpty()) {
            return "FNBENDO";
        }
        if (isNamibiaResponseCode(responseCode)) {
            return "FNBENDO";
        }
        if (isEswatiniResponseCode(responseCode)) {
            return "REALTIME";
        }
        return "FNBENDO";
    }

    private boolean isNamibiaResponseCode(String responseCode) {
        return ResponseGroups.GROUP1_INSUFFICIENT_FUNDS_NAMIBIA.contains(responseCode)
                || ResponseGroups.GROUP2_PERMANENT_CLOSURE_NAMIBIA.contains(responseCode)
                || ResponseGroups.GROUP3_NEW_MANDATE_NAMIBIA.contains(responseCode)
                || ResponseGroups.GROUP4_DISPUTE_NAMIBIA.contains(responseCode)
                || ResponseGroups.GROUP5_TECHNICAL_ERRORS_NAMIBIA.contains(responseCode);
    }

    private boolean isEswatiniResponseCode(String responseCode) {
        return ResponseGroups.GROUP1_INSUFFICIENT_FUNDS_ESWATINI.contains(responseCode)
                || ResponseGroups.GROUP2_PERMANENT_CLOSURE_ESWATINI.contains(responseCode)
                || ResponseGroups.GROUP3_NEW_MANDATE_ESWATINI.contains(responseCode)
                || ResponseGroups.GROUP4_DISPUTE_ESWATINI.contains(responseCode)
                || ResponseGroups.GROUP5_TECHNICAL_ERRORS_ESWATINI.contains(responseCode);
    }

    public String getAuthToken() {
        try {
            String token = tokenGenerator.getToken();
            if (token != null && !token.isEmpty()) {
                if (!token.startsWith("Bearer ")) {
                    token = "Bearer " + token;
                }
                return token;
            }
            return null;
        } catch (Exception e) {
            logger.error("Failed to generate authentication token: {}", e.getMessage(), e);
            return null;
        }
    }

    private String generateWebhookId() {
        return "WH-" + UUID.randomUUID();
    }

}
