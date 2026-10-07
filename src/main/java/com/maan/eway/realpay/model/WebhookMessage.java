package com.maan.eway.realpay.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "webhook_message")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class WebhookMessage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "webhook_id", nullable = false, unique = true, length = 50)
    private String webhookId;

    @Column(name = "callback_type", length = 50)
    private String callbackType;

    @Column(name = "beneficiary_user", length = 100)
    private String beneficiaryUser;

    @Column(name = "instalment_reference_number", length = 100)
    private String instalmentReferenceNumber;

    @Column(name = "contract_number", length = 100)
    private String contractNumber;

    @Column(name = "raw_payload", columnDefinition = "TEXT")
    private String rawPayload;

    @Column(name = "received_date")
    private LocalDateTime receivedDate;

    @Column(name = "processed_status", length = 50)
    private String processedStatus;

    @Column(name = "processed_message", length = 1000)
    private String processedMessage;

    @Column(name = "processed_date")
    private LocalDateTime processedDate;
}
