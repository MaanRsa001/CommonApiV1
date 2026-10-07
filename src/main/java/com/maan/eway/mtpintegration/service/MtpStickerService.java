package com.maan.eway.mtpintegration.service;
/*
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;

@Service
public class MtpStickerService {

    private static final Logger log =
            LoggerFactory.getLogger(MtpStickerService.class);

    private final MtpAuthService authService;
    private final MtpExternalApiClient externalApiClient;
    private final MtpAuditService auditService;
    private final MtpErrorLogService errorLogService;
    private final MtpStickerDeliveryService stickerDeliveryService;

    public MtpStickerService(
            MtpAuthService authService,
            MtpExternalApiClient externalApiClient,
            MtpAuditService auditService,
            MtpErrorLogService errorLogService,
            MtpStickerDeliveryService stickerDeliveryService) {

        this.authService = authService;
        this.externalApiClient = externalApiClient;
        this.auditService = auditService;
        this.errorLogService = errorLogService;
        this.stickerDeliveryService = stickerDeliveryService;
    }

    public ResponseEntity<byte[]> downloadSticker(
            String stickerReference,
            String vehicleNumber) {

        String apiName = "DOWNLOAD_STICKER";
        String path = "/Rest/stk/" + stickerReference + "/" + vehicleNumber;

        try {
            log.info("Downloading sticker. stickerReference={}, vehicleNumber={}",
                    stickerReference, vehicleNumber);

            ResponseEntity<byte[]> response =
                    externalApiClient.getFileWithoutToken(path);

            String status = response.getStatusCode().is2xxSuccessful()
                    ? "SUCCESS"
                    : "FAILED";

            stickerDeliveryService.saveDownloadAttempt(
                    stickerReference,
                    vehicleNumber,
                    status);

            auditService.save(
                    apiName,
                    "stickerReference=" + stickerReference + ", vehicleNumber=" + vehicleNumber,
                    "HTTP " + response.getStatusCode(),
                    status);

            return response;

        } catch (RestClientResponseException ex) {

            stickerDeliveryService.saveDownloadAttempt(
                    stickerReference,
                    vehicleNumber,
                    "FAILED");

            auditService.save(
                    apiName,
                    "stickerReference=" + stickerReference + ", vehicleNumber=" + vehicleNumber,
                    ex.getResponseBodyAsString(),
                    "FAILED");

            errorLogService.save(apiName, ex);
            throw ex;

        } catch (RuntimeException ex) {

            stickerDeliveryService.saveDownloadAttempt(
                    stickerReference,
                    vehicleNumber,
                    "FAILED");

            auditService.save(
                    apiName,
                    "stickerReference=" + stickerReference + ", vehicleNumber=" + vehicleNumber,
                    ex.toString(),
                    "FAILED");

            errorLogService.save(apiName, ex);
            throw ex;
        }
    }
}*/
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientResponseException;

@Service
public class MtpStickerService {

    private static final Logger log = LoggerFactory.getLogger(MtpStickerService.class);

    private final MtpExternalApiClient externalApiClient;
    private final MtpAuditService auditService;
    private final MtpErrorLogService errorLogService;
    private final MtpStickerDeliveryService stickerDeliveryService;
    private final MtpPaymentTransactionService paymentTransactionService;

    public MtpStickerService(MtpExternalApiClient externalApiClient,
                             MtpAuditService auditService,
                             MtpErrorLogService errorLogService,
                             MtpStickerDeliveryService stickerDeliveryService,
                             MtpPaymentTransactionService paymentTransactionService) {
        this.externalApiClient = externalApiClient;
        this.auditService = auditService;
        this.errorLogService = errorLogService;
        this.stickerDeliveryService = stickerDeliveryService;
        this.paymentTransactionService = paymentTransactionService;
    }

    public ResponseEntity<byte[]> downloadSticker(String stickerReference, String vehicleNumber) {
        String apiName = "DOWNLOAD_STICKER";
        String path = "/Rest/stk/" + stickerReference + "/" + vehicleNumber;

        if (!paymentTransactionService.isStickerDownloadAllowed(stickerReference, vehicleNumber)) {
            throw new IllegalStateException("Sticker download allowed only after paymentStatus = COMPLETED");
        }

        try {
            log.info("Downloading sticker without token. stickerReference={}, vehicleNumber={}", stickerReference, vehicleNumber);
            ResponseEntity<byte[]> response = externalApiClient.getFileWithoutToken(path);
            String status = response.getStatusCode().is2xxSuccessful() ? "COMPLETED" : "FAILED";

            stickerDeliveryService.saveDownloadAttempt(stickerReference, vehicleNumber, status);
            auditService.save(apiName,
                    "stickerReference=" + stickerReference + ", vehicleNumber=" + vehicleNumber,
                    "HTTP " + response.getStatusCode(),
                    status);
            return response;

        } catch (RestClientResponseException ex) {
            stickerDeliveryService.saveDownloadAttempt(stickerReference, vehicleNumber, "FAILED");
            auditService.save(apiName,
                    "stickerReference=" + stickerReference + ", vehicleNumber=" + vehicleNumber,
                    ex.getResponseBodyAsString(),
                    "FAILED");
            errorLogService.save(apiName, ex);
            throw ex;

        } catch (RuntimeException ex) {
            stickerDeliveryService.saveDownloadAttempt(stickerReference, vehicleNumber, "FAILED");
            auditService.save(apiName,
                    "stickerReference=" + stickerReference + ", vehicleNumber=" + vehicleNumber,
                    ex.toString(),
                    "FAILED");
            errorLogService.save(apiName, ex);
            throw ex;
        }
    }
}

