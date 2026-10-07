package com.maan.eway.mtpintegration.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maan.eway.mtpintegration.entity.MtpApiAudit;
import com.maan.eway.mtpintegration.repository.MtpApiAuditRepository;

@Service
public class MtpAuditService {

    private final MtpApiAuditRepository auditRepository;
    private final ObjectMapper objectMapper;

    public MtpAuditService(
            MtpApiAuditRepository auditRepository,
            ObjectMapper objectMapper) {

        this.auditRepository = auditRepository;
        this.objectMapper = objectMapper;
    }

    public void save(
            String apiName,
            Object request,
            Object response,
            String status) {

        try {

            MtpApiAudit audit = new MtpApiAudit();

            audit.setApiName(apiName);
            audit.setRequestData(toJson(request));
            audit.setResponseData(toJson(response));
            audit.setStatus(status);
            audit.setCreatedAt(LocalDateTime.now());

            auditRepository.save(audit);

        } catch (Exception ignored) {
            // Audit failure should not stop business API flow.
        }
    }

    private String toJson(Object value) {

        try {

            if (value == null) {
                return null;
            }

            if (value instanceof String) {
                return (String) value;
            }

            return objectMapper.writeValueAsString(value);

        } catch (Exception ex) {

            return String.valueOf(value);
        }
    }
}