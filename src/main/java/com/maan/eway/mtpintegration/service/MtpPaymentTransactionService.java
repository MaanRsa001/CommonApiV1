package com.maan.eway.mtpintegration.service;

/*

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.maan.eway.mtpintegration.dto.InitiatePaymentRequest;
import com.maan.eway.mtpintegration.dto.InitiatePaymentResponse;
import com.maan.eway.mtpintegration.dto.PaymentStatusRequest;
import com.maan.eway.mtpintegration.dto.PaymentStatusResponse;
import com.maan.eway.mtpintegration.entity.MtpPaymentTransaction;
import com.maan.eway.mtpintegration.repository.MtpPaymentTransactionRepository;

@Service
public class MtpPaymentTransactionService {

    private final MtpPaymentTransactionRepository repository;

    public MtpPaymentTransactionService(
            MtpPaymentTransactionRepository repository) {
        this.repository = repository;
    }

    
    public void saveInitiated(
            InitiatePaymentRequest request,
            InitiatePaymentResponse response) {
        try {

            MtpPaymentTransaction transaction = repository
                    .findByNumberPlateAndAssessmentTypeAndMsisdnAndPartnerIdentifier(
                            request.getNumberPlate(),
                            request.getAssessmentType(),
                            request.getMsisdn(),
                            request.getPartnerIdentifier())
                    .orElse(new MtpPaymentTransaction());

            transaction.setPaymentRequestId(
                    response.getPaymentRequestId() == null
                            ? null
                            : String.valueOf(response.getPaymentRequestId()));

            transaction.setPaymentChannel(response.getPaymentChannel());
            transaction.setNumberPlate(request.getNumberPlate());
            transaction.setMsisdn(request.getMsisdn());
            transaction.setAssessmentType(request.getAssessmentType());
            transaction.setPartnerIdentifier(request.getPartnerIdentifier());

            transaction.setReturnCode(response.getReturnCode());
            transaction.setReturnMessage(response.getReturnMessage());

            transaction.setStatus(
                    response.getReturnCode() != null && response.getReturnCode() == 0
                            ? "INITIATED"
                            : "FAILED");

            if (transaction.getCreatedAt() == null) {
                transaction.setCreatedAt(LocalDateTime.now());
            }

            transaction.setUpdatedAt(LocalDateTime.now());

            repository.save(transaction);

        } catch (Exception ignored) {
            // Transaction tracking failure should not stop API response.
        }
    }
    
    
    

    public void updateStatus(
            PaymentStatusRequest request,
            PaymentStatusResponse response) {
        try {
            MtpPaymentTransaction transaction = repository
                    .findTopByPaymentRequestIdOrderByIdDesc(
                            request.getPaymentRequestId())
                    .orElseGet(() -> {
                        MtpPaymentTransaction tx = new MtpPaymentTransaction();
                        tx.setPaymentRequestId(request.getPaymentRequestId());
                        tx.setCreatedAt(LocalDateTime.now());
                        return tx;
                    });

            transaction.setPaymentChannel(request.getPaymentChannel());

            transaction.setStatus(
                    response.getPaymentStatus() != null
                            ? response.getPaymentStatus()
                            : response.getReturnMessage());

            transaction.setReturnCode(response.getReturnCode());
            transaction.setReturnMessage(response.getReturnMessage());

            transaction.setUpdatedAt(LocalDateTime.now());

            repository.save(transaction);

        } catch (Exception ignored) {
            // Transaction tracking failure should not stop API response.
        }
    }
}*/

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maan.eway.mtpintegration.dto.InitiatePaymentRequest;
import com.maan.eway.mtpintegration.dto.InitiatePaymentResponse;
import com.maan.eway.mtpintegration.dto.PaymentStatusRequest;
import com.maan.eway.mtpintegration.dto.PaymentStatusResponse;
import com.maan.eway.mtpintegration.entity.MtpPaymentTransaction;
import com.maan.eway.mtpintegration.repository.MtpPaymentTransactionRepository;

@Service
public class MtpPaymentTransactionService {

    private static final Logger log = LoggerFactory.getLogger(MtpPaymentTransactionService.class);

    private final MtpPaymentTransactionRepository repository;
    private final ObjectMapper objectMapper;

    public MtpPaymentTransactionService(MtpPaymentTransactionRepository repository,
                                        ObjectMapper objectMapper) {
        this.repository = repository;
        this.objectMapper = objectMapper;
    }

    public MtpPaymentTransaction saveInitiated(InitiatePaymentRequest request,
                                               InitiatePaymentResponse response) {
        try {
            MtpPaymentTransaction transaction = repository
                    .findByNumberPlateAndAssessmentTypeAndMsisdnAndPartnerIdentifier(
                            safe(request.getNumberPlate()),
                            safe(request.getAssessmentType()),
                            safe(request.getMsisdn()),
                            safe(request.getPartnerIdentifier()))
                    .orElseGet(MtpPaymentTransaction::new);

            if (response != null && response.getPaymentRequestId() != null) {
                transaction.setPaymentRequestId(String.valueOf(response.getPaymentRequestId()));
            }
            transaction.setPaymentChannel(response == null ? null : response.getPaymentChannel());
            transaction.setNumberPlate(safe(request.getNumberPlate()));
            transaction.setMsisdn(safe(request.getMsisdn()));
            transaction.setAssessmentType(safe(request.getAssessmentType()));
            transaction.setPartnerIdentifier(safe(request.getPartnerIdentifier()));
            transaction.setReturnCode(response == null ? null : response.getReturnCode());
            transaction.setReturnMessage(response == null ? "No response from MTP initiate payment API" : response.getReturnMessage());
            transaction.setStatus(isSuccessCode(response == null ? null : response.getReturnCode()) ? "INITIATED" : "FAILED");
            transaction.setInitRequestJson(toJson(request));
            transaction.setInitResponseJson(toJson(response));

            if (transaction.getCreatedAt() == null) {
                transaction.setCreatedAt(LocalDateTime.now());
            }
            transaction.setUpdatedAt(LocalDateTime.now());

            return repository.save(transaction);
        } catch (Exception ex) {
            log.error("Unable to save MTP initiate payment transaction. error={}", ex.getMessage(), ex);
            return null;
        }
    }

    public MtpPaymentTransaction updateStatus(PaymentStatusRequest request,
                                              PaymentStatusResponse response) {
        try {
            MtpPaymentTransaction transaction = repository
                    .findTopByPaymentRequestIdOrderByIdDesc(safe(request.getPaymentRequestId()))
                    .orElseGet(() -> {
                        MtpPaymentTransaction tx = new MtpPaymentTransaction();
                        tx.setPaymentRequestId(safe(request.getPaymentRequestId()));
                        tx.setCreatedAt(LocalDateTime.now());
                        return tx;
                    });

            transaction.setPaymentChannel(safe(request.getPaymentChannel()));
            transaction.setReturnCode(response == null ? null : response.getReturnCode());
            transaction.setReturnMessage(response == null ? "No response from MTP payment status API" : response.getReturnMessage());
            transaction.setStatus(resolveStatus(response));

            if (response != null) {
                transaction.setStickerReference(response.getStickerReference());
                transaction.setTransactionId(response.getTransactionId());
                transaction.setDownloadLink(response.getDownloadLink());
            }
            transaction.setStatusRequestJson(toJson(request));
            transaction.setStatusResponseJson(toJson(response));
            transaction.setUpdatedAt(LocalDateTime.now());

            return repository.save(transaction);
        } catch (Exception ex) {
            log.error("Unable to update MTP payment transaction status. error={}", ex.getMessage(), ex);
            return null;
        }
    }

    public boolean isStickerDownloadAllowed(String stickerReference, String numberPlate) {
        return repository.findTopByStickerReferenceAndNumberPlateOrderByIdDesc(stickerReference, numberPlate)
                .map(tx -> "COMPLETED".equalsIgnoreCase(tx.getStatus()))
                .orElse(false);
    }

    private String resolveStatus(PaymentStatusResponse response) {
        if (response == null) {
            return "PENDING";
        }
        if (response.getPaymentStatus() != null && !response.getPaymentStatus().trim().isEmpty()) {
            return response.getPaymentStatus().trim().toUpperCase();
        }
        if (isSuccessCode(response.getReturnCode()) && response.getDownloadLink() != null) {
            return "COMPLETED";
        }
        if (response.getReturnCode() != null && response.getReturnCode() != 0) {
            return "FAILED";
        }
        return "PENDING";
    }

    private boolean isSuccessCode(Integer code) {
        return code != null && code == 0;
    }

    private String safe(String value) {
        return value == null ? null : value.trim();
    }

    private String toJson(Object value) {
        try {
            return value == null ? null : objectMapper.writeValueAsString(value);
        } catch (Exception ex) {
            return String.valueOf(value);
        }
    }
}

