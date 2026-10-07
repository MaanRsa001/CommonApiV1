package com.maan.eway.mtpintegration.mtppayment.service;

import java.time.LocalDateTime;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonObject;
import com.maan.eway.bean.ApiIntegMaster;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.MotorDataDetails;
import com.maan.eway.mtpintegration.entity.MtpApiAudit;
import com.maan.eway.mtpintegration.mtppayment.entity.MtpConfigMaster;
import com.maan.eway.mtpintegration.mtppayment.repository.MtpConfigMasterRepository;
import com.maan.eway.mtpintegration.repository.MtpApiAuditRepository;
import com.maan.eway.repository.ApiIntegMasterRepository;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.MotorDataDetailsRepository;

@Service
public class MtpPaymentStatusPollingService {

    private static final Logger log = LoggerFactory.getLogger(MtpPaymentStatusPollingService.class);

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private MtpConfigMasterRepository configRepo;

    @Autowired
    private MtpApiAuditRepository auditRepo;
    
    @Autowired
    private ApiIntegMasterRepository apiIntegMasterRepo;
    
    @Autowired
    private HomePositionMasterRepository homerepo;
    
    @Autowired
    private MotorDataDetailsRepository motorDataDetailsRepo;
    
    

    @Value("${eway.common.api.base-url}")
    private String commonApiBase;

    /**
     * Entry point - fire and forget. Kicks off the first poll attempt
     * asynchronously so the caller (create-order-minim success path) is
     * not blocked.
     */
    @Async("mtpPollingExecutor")
    public void startPolling(String companyId, String quoteNo, String token) {

        MtpConfigMaster config = configRepo.findByCompanyIdAndStatus(companyId,"Y").orElse(null);

        if (config == null || !"Y".equalsIgnoreCase(config.getStatus())) {
            log.warn("No active mtp_config_master entry for companyId={}, skipping status polling for quoteNo={}",
                    companyId, quoteNo);
            return;
        }

        int maxAttempts = config.getMaxAttempts() != null ? config.getMaxAttempts() : 3;
        int intervalSeconds = config.getIntervalMinutes() != null ? config.getIntervalMinutes() : 60;

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token);

        pollLoop(quoteNo, headers, 1, maxAttempts, intervalSeconds);
    }

    /**
     * Recursive polling loop, running entirely on the async thread. Sleeps
     * intervalSeconds before each attempt (including the first), matching
     * "wait N secs, then check" behavior.
     */
    private void pollLoop(String quoteNo, HttpHeaders headers, int attemptNumber,
                           int maxAttempts, int intervalSeconds) {

        try {
            Thread.sleep(intervalSeconds * 1000L);
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            log.error("Polling thread interrupted for quoteNo={}", quoteNo);
            return;
        }

        String url = commonApiBase + "/selcom/v1/checkout/order-status/" + quoteNo;
        String rawBody = null;
        Map<String, Object> responseMap = null;
        String status;
        String policyNo = null;

        try {
            HttpEntity<Void> entity = new HttpEntity<>(headers);
            ResponseEntity<String> resp = restTemplate.exchange(url, HttpMethod.POST, entity, String.class);
            rawBody = resp.getBody();

            if (rawBody != null && !rawBody.isBlank()) {
                responseMap = objectMapper.readValue(rawBody, new TypeReference<Map<String, Object>>() {});
            }

            String result = responseMap != null ? String.valueOf(responseMap.get("result")) : null;
            boolean isCompleted = "COMPLETED".equalsIgnoreCase(result);

            if (isCompleted && responseMap.get("policyNo") != null) {
                policyNo = String.valueOf(responseMap.get("policyNo"));
            }

            status = isCompleted ? "SUCCESS" : "PENDING";

            saveAudit("ORDER_STATUS", quoteNo, null, rawBody, status);

            if (isCompleted) {
                log.info("Order status COMPLETED for quoteNo={} on attempt={} policyNo={}",
                        quoteNo, attemptNumber, policyNo);
                
                
                
                return; // stop polling - success
            }

            if ("FAIL".equalsIgnoreCase(result)) {
                log.info("Order status FAIL for quoteNo={} on attempt={}, stopping polling", quoteNo, attemptNumber);
                saveAudit("ORDER_STATUS", quoteNo, null, rawBody, "FAILED");
                return; // stop polling - explicit failure
            }

            if (attemptNumber >= maxAttempts) {
                log.warn("Order status polling exhausted maxAttempts={} for quoteNo={}, giving up", maxAttempts, quoteNo);
                saveAudit("ORDER_STATUS", quoteNo, null, rawBody, "EXHAUSTED");
                return;
            }

            // continue polling
            pollLoop(quoteNo, headers, attemptNumber + 1, maxAttempts, intervalSeconds);

        } catch (Exception e) {
            log.error("Order status polling failed for quoteNo={} attempt={} : {}", quoteNo, attemptNumber, e.getMessage(), e);
            saveAudit("ORDER_STATUS", quoteNo, null, e.getMessage(), "ERROR");

            if (attemptNumber < maxAttempts) {
                pollLoop(quoteNo, headers, attemptNumber + 1, maxAttempts, intervalSeconds);
            }
        }
    }

    private void saveAudit(String apiName, String quoteNo, String requestData, String responseData, String status) {
        try {
            MtpApiAudit audit = MtpApiAudit.builder()
                    .apiName(apiName)
                    .quoteNo(quoteNo)
                    .requestData(requestData)
                    .responseData(responseData)
                    .status(status)
                    .createdAt(LocalDateTime.now())
                    .build();
            auditRepo.save(audit);
        } catch (Exception e) {
            log.error("Failed to save MTP API audit for quoteNo={} apiName={} : {}", quoteNo, apiName, e.getMessage(), e);
        }
    }
    
    private void sendMtpWhatsApp(String quoteNo, String waId, boolean isSuccess) {
        try {
            // ── Fetch WhatsApp config from ApiIntegMaster ─────────────────────
            ApiIntegMaster whatsappConfig = apiIntegMasterRepo
                .findByCompanyIdAndProductIdAndApiTypeAndStatus(
                    "100019", 125, "WHATSAPP_MTP", "Y")
                .orElse(null);

            if (whatsappConfig == null) {
                log.error("WhatsApp config not found in ApiIntegMaster for WHATSAPP_MTP quoteNo={}", quoteNo);
                return;
            }

            String whatsappUrl   = whatsappConfig.getApiUrl();   // URL from API_URL column
            String whatsappToken = whatsappConfig.getApiDesc();  // Token from API_DESC column

            if (StringUtils.isBlank(whatsappUrl) || StringUtils.isBlank(whatsappToken)) {
                log.error("WhatsApp URL or token is blank in ApiIntegMaster for quoteNo={}", quoteNo);
                return;
            }

            // ── Fetch data from HomePositionMaster ────────────────────────────
            HomePositionMaster homePos = homerepo.findByQuoteNo(quoteNo);
            if (homePos == null) {
                log.error("WhatsApp: HomePositionMaster not found for quoteNo={}", quoteNo);
                return;
            }

            // ── Only send if homePos status is 'P' ────────────────────────────
            if (!"P".equalsIgnoreCase(homePos.getStatus())) {
                log.info("WhatsApp: HomePositionMaster status is not P for quoteNo={}, skipping", quoteNo);
                return;
            }

            String policyNo      = StringUtils.isNotBlank(homePos.getPolicyNo())      ? homePos.getPolicyNo()      : "";
            String stickerNumber = StringUtils.isNotBlank(homePos.getStickerNumber()) ? homePos.getStickerNumber() : "";
            String customerName  = StringUtils.isNotBlank(homePos.getCustomerName())  ? homePos.getCustomerName()  : "Customer";
            String startDate     = homePos.getInceptionDate() != null ? homePos.getInceptionDate().toString() : "";
            String endDate       = homePos.getExpiryDate()    != null ? homePos.getExpiryDate().toString()    : "";

            // ── Get registration number from MotorDataDetails ─────────────────
            String regNo = "";
            MotorDataDetails motorData = motorDataDetailsRepo
                .findTopByQuoteNoOrderByEntryDateDesc(quoteNo);
            if (motorData != null && StringUtils.isNotBlank(motorData.getRegistrationNumber())) {
                regNo = motorData.getRegistrationNumber();
            }

            String messageBody;
            if (isSuccess) {
                messageBody = "Dear " + customerName + ",\n\n"
                    + "Thank You For Purchasing Your Motor Insurance Policy "
                    + "through Our WhatsApp Chatbot!\n\n"
                    + "*Your Policy Information:*\n\n"
                    + "- Policy No: "        + policyNo      + "\n"
                    + "- Sticker No: "       + stickerNumber + "\n"
                    + "- Registration No: "  + regNo         + "\n"
                    + "- Start Date: "       + startDate     + "\n"
                    + "- End Date: "         + endDate;
            } else {
                messageBody = "Dear " + customerName + ",\n\n"
                    + "We were unable to process your Motor Insurance sticker at this time.\n\n"
                    + "Please kindly contact our underwriter for further assistance.\n\n"
                    + "Thank you for your patience.";
            }

            // ── Build request JSON ────────────────────────────────────────────
            JsonObject textObj = new JsonObject();
            textObj.addProperty("preview_url", false);
            textObj.addProperty("body", messageBody);

            JsonObject requestJson = new JsonObject();
            requestJson.addProperty("messaging_product", "whatsapp");
            requestJson.addProperty("recipient_type",    "individual");
            requestJson.addProperty("to",                waId);
            requestJson.addProperty("type",              "text");
            requestJson.add("text", textObj);

            // ── Call WhatsApp API ─────────────────────────────────────────────
            try (CloseableHttpClient client = HttpClients.createDefault()) {
                HttpPost httpPost = new HttpPost(whatsappUrl);
                httpPost.setHeader("Authorization", "Bearer " + whatsappToken);
                httpPost.setHeader("Content-Type",  "application/json");
                httpPost.setEntity(new StringEntity(requestJson.toString(), "UTF-8"));

                try (CloseableHttpResponse httpResponse = client.execute(httpPost)) {
                    String responseString = EntityUtils.toString(httpResponse.getEntity());
                    int    statusCode     = httpResponse.getStatusLine().getStatusCode();
                    log.info("WhatsApp API response for quoteNo={} statusCode={} body={}",
                        quoteNo, statusCode, responseString);

                    saveAudit("WHATSAPP_NOTIFY", quoteNo, requestJson.toString(),
                        responseString, statusCode == 200 ? "SUCCESS" : "FAILED");
                }
            }

        } catch (Exception e) {
            log.error("WhatsApp send failed for quoteNo={} waId={}: {}", quoteNo, waId, e.getMessage(), e);
        }
    }
    
}