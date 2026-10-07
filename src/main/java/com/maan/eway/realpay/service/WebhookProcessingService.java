package com.maan.eway.realpay.service;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.JsonNode;
// import com.maan.eway.realpay.model.WebhookMessage; // Placeholder
// import com.maan.eway.realpay.repository.WebhookMessageRepository; // Placeholder

@Service
public class WebhookProcessingService {

    private static final Logger logger = LoggerFactory.getLogger(WebhookProcessingService.class);

    // @Autowired
    // private WebhookMessageRepository webhookMessageRepository; // Uncomment when repository is defined

    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void storeWebhookMessageAsync(String webhookId, String callbackType, String beneficiaryUser,
                                         String rawPayload, JsonNode payload) {
        try {
            logger.info("WEBHOOK_STORAGE_ASYNC - ID: {}", webhookId);

            // === PLACEHOLDER START ===
            /*
            WebhookMessage webhookMessage = new WebhookMessage();
            webhookMessage.setWebhookId(webhookId);
            webhookMessage.setCallbackType(callbackType);
            webhookMessage.setBeneficiaryUser(beneficiaryUser);
            webhookMessage.setRawPayload(truncatePayload(rawPayload, 65535));
            webhookMessage.setReceivedDate(LocalDateTime.now());
            webhookMessage.setProcessedStatus("QUEUED");

            if (payload != null && payload.has("InstalmentGetResponse")) {
                JsonNode instalmentResponse = payload.get("InstalmentGetResponse");
                if (instalmentResponse.isArray() && instalmentResponse.size() > 0) {
                    JsonNode instalment = instalmentResponse.get(0);
                    if (instalment.has("InstalmentReferenceNumber")) {
                        webhookMessage.setInstalmentReferenceNumber(
                                instalment.get("InstalmentReferenceNumber").asText());
                    }
                    if (instalment.has("ContractNumber")) {
                        webhookMessage.setContractNumber(instalment.get("ContractNumber").asText());
                    }
                }
            }

            webhookMessageRepository.save(webhookMessage);
            */
            // === PLACEHOLDER END ===
            
            logger.info("WEBHOOK_STORED - ID: {}", webhookId);

        } catch (Exception e) {
            logger.error("WEBHOOK_STORAGE_ERROR - ID: {}, Error: {}", webhookId, e.getMessage(), e);
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void updateWebhookStatus(String webhookId, String status, String message) {
        try {
            // === PLACEHOLDER START ===
            /*
            WebhookMessage webhookMessage = webhookMessageRepository.findByWebhookId(webhookId);
            if (webhookMessage != null) {
                webhookMessage.setProcessedStatus(status);
                webhookMessage.setProcessedMessage(truncatePayload(message, 1000));
                webhookMessage.setProcessedDate(LocalDateTime.now());
                webhookMessageRepository.save(webhookMessage);
            }
            */
            // === PLACEHOLDER END ===
             logger.info("WEBHOOK_STATUS_UPDATE - ID: {}, Status: {}, Message: {}", webhookId, status, message);
        } catch (Exception e) {
            logger.error("WEBHOOK_STATUS_UPDATE_ERROR - ID: {}, Error: {}", webhookId, e.getMessage(), e);
        }
    }

    private String truncatePayload(String payload, int maxLength) {
        if (payload == null) {
            return null;
        }
        return payload.length() > maxLength ? payload.substring(0, maxLength) : payload;
    }
}