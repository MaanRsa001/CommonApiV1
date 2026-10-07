package com.maan.eway.realpay.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.maan.eway.realpay.model.WebhookMessage;

@Repository
public interface WebhookMessageRepository extends JpaRepository<WebhookMessage, Long> {
    
    WebhookMessage findByWebhookId(String webhookId);
}
