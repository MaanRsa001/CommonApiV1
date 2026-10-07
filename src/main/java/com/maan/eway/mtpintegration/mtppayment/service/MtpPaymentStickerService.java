package com.maan.eway.mtpintegration.mtppayment.service;


import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.bean.MotorDataDetails;
import com.maan.eway.bean.PaymentDetail;
import com.maan.eway.bean.PaymentInfo;
import com.maan.eway.bean.PaymentVendorMaster;
import com.maan.eway.mtpintegration.mtppayment.dto.MtpRehitReq;
import com.maan.eway.mtpintegration.mtppayment.entity.MtpConfigMaster;
import com.maan.eway.mtpintegration.mtppayment.entity.MtpPaymentLog;
import com.maan.eway.mtpintegration.mtppayment.entity.MtpPaymentTracking;
import com.maan.eway.mtpintegration.mtppayment.repository.MtpConfigMasterRepository;
import com.maan.eway.mtpintegration.mtppayment.repository.MtpPaymentLogRepository;
import com.maan.eway.mtpintegration.mtppayment.repository.MtpPaymentTrackingRepository;
import com.maan.eway.payment.service.SelcomPaymentService;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.repository.MotorDataDetailsRepository;
import com.maan.eway.repository.PaymentDetailRepository;
import com.maan.eway.repository.PaymentInfoRepository;
import com.maan.eway.repository.PaymentVendorMasterRepository;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class MtpPaymentStickerService {

    @Autowired private MtpConfigMasterRepository mtpConfigRepo;
    @Autowired private MtpPaymentTrackingRepository trackingRepo;
    @Autowired private MtpPaymentLogRepository logRepo;
    @Autowired private PaymentDetailRepository paymentDetailRepo;
    @Autowired private PaymentInfoRepository paymentinforepo;
    @Autowired private HomePositionMasterRepository homerepo;
    @Autowired private MotorDataDetailsRepository motorDataDetailsRepo;
    @Autowired private PaymentVendorMasterRepository paymentVendorRepo;
//    @Autowired private SelcomPaymentService selcomService;

    @Value("${mtp.base.url}")
    private String mtpBaseUrl;

    @Value("${mtp.sticker.download.path}")
    private String mtpStickerDownloadPath;
    
    @Value("${mtp.status.check.interval.seconds:20}")
    private int statusCheckIntervalSeconds;

    @Value("${mtp.status.check.max.wait.minutes:2}")
    private int statusCheckMaxWaitMinutes;

    // ── Called from savePaymentDetails after policy is generated ─────────────
    public void triggerMtpStickerPayment(String quoteNo, String policyNo,
                                          String companyId, String employeeName,
                                          String employeeMobile) {
        try {
            Optional<MtpPaymentTracking> existing = trackingRepo
                .findByQuoteNoAndStatusIn(quoteNo, 
                    Arrays.asList("PENDING", "SUCCESS"));
            if (existing.isPresent()) {
                log.info("MTP sticker tracking already exists for quoteNo {}, skipping", quoteNo);
                return;
            }

            MtpConfigMaster config = mtpConfigRepo
                .findByCompanyIdAndStatus(companyId, "Y")
                .orElse(null);
            int maxAttempts    = config != null ? config.getMaxAttempts()    : 3;
            int intervalMinutes = config != null ? config.getIntervalMinutes() : 2;

            // Create tracking row
            MtpPaymentTracking tracking = new MtpPaymentTracking();
            tracking.setQuoteNo(quoteNo);
            tracking.setPolicyNo(policyNo);
            tracking.setCompanyId(companyId);
            tracking.setEmployeeName(employeeName);
            tracking.setEmployeeMobile(employeeMobile);
            tracking.setCurrentAttempt(0);
            tracking.setMaxAttempts(maxAttempts);
            tracking.setIntervalMinutes(intervalMinutes);
            tracking.setStatus("PENDING");
            tracking.setNextAttemptDue(new Date()); // due immediately
            tracking.setEntryDate(new Date());
            tracking.setUpdatedDate(new Date());
            trackingRepo.save(tracking);

            log.info("MTP sticker tracking created for quoteNo {} policyNo {}", quoteNo, policyNo);
        } catch (Exception e) {
            log.error("Failed to create MTP sticker tracking for quoteNo {}: {}", quoteNo, e.getMessage(), e);
        }
    }

    // ── Core: trigger one MTP payment attempt ────────────────────────────────
    public String triggerMtpPaymentAttempt(MtpPaymentTracking tracking,
                                            PaymentVendorMaster vendor,
                                            String source) {
        String quoteNo = tracking.getQuoteNo();
        MtpPaymentLog mtpLog = new MtpPaymentLog();
        mtpLog.setTrackingId(tracking.getId());
        mtpLog.setQuoteNo(quoteNo);
        mtpLog.setAttemptNo(tracking.getCurrentAttempt() + 1);
        mtpLog.setSource(source);
        mtpLog.setEntryDate(new Date());
        mtpLog.setRequestTime(new Date());

        try {
            // Fetch vehicle reg no from MotorDataDetails
            MotorDataDetails motorData = motorDataDetailsRepo
                .findTopByQuoteNoOrderByEntryDateDesc(quoteNo);
            String numberPlate = (motorData != null
                && StringUtils.isNotBlank(motorData.getRegistrationNumber()))
                    ? motorData.getRegistrationNumber() : "";

            // Get OAuth token
            String accessToken = getMtpAccessToken(
            		vendor.getCheckStatusUrl().trim(),   
    	            vendor.getVendorCode(),          
    	            vendor.getApiSecretKey() 
            );
            if (accessToken == null) {
                mtpLog.setStatus("FAILED");
                mtpLog.setResponseJson("{\"error\":\"OAuth token fetch failed\"}");
                mtpLog.setResponseTime(new Date());
                logRepo.save(mtpLog);
                return null;
            }

            // Build request — no premium, just vehicle + mobile + quoteNo
            JsonObject requestPayload = new JsonObject();
            requestPayload.addProperty("numberPlate",       numberPlate);
            requestPayload.addProperty("msisdn",            tracking.getEmployeeMobile());
            requestPayload.addProperty("assessmentType",    "M"); // product 5 + policyType 1 always M
            requestPayload.addProperty("partnerIdentifier", quoteNo);

            mtpLog.setRequestJson(requestPayload.toString());

            String paymentUrl = vendor.getPaymentUrlLink().trim();
            try (CloseableHttpClient client = HttpClients.createDefault()) {
                HttpPost httpPost = new HttpPost(paymentUrl);
                httpPost.setHeader("Authorization", "Bearer " + accessToken);
                httpPost.setHeader("Content-Type", "application/json");
                httpPost.setEntity(new StringEntity(requestPayload.toString(), "UTF-8"));

                try (CloseableHttpResponse httpResponse = client.execute(httpPost)) {
                    String responseString = EntityUtils.toString(httpResponse.getEntity());
                    mtpLog.setResponseJson(responseString);
                    mtpLog.setResponseTime(new Date());

                    
                    JsonObject responseJson = JsonParser.parseString(responseString).getAsJsonObject();
                    int returnCode = responseJson.has("returnCode")
                        ? responseJson.get("returnCode").getAsInt() : -1;

                    if (returnCode == 0) {
                        String paymentRequestId = responseJson.get("paymentRequestId").getAsString();
                        String paymentChannel   = responseJson.has("paymentChannel")
                            ? responseJson.get("paymentChannel").getAsString() : "";

                        mtpLog.setPaymentRequestId(paymentRequestId);
                        mtpLog.setPaymentChannel(paymentChannel);
                        mtpLog.setStatus("INITIATED");

                        // Insert PaymentDetail row for this attempt
                        savePaymentDetailForMtpAttempt(tracking, paymentRequestId,
                            paymentChannel, mtpLog.getAttemptNo());

                        log.info("MTP sticker payment initiated for quoteNo {} attempt {} requestId {}",
                            quoteNo, mtpLog.getAttemptNo(), paymentRequestId);

                        logRepo.save(mtpLog);
                        return paymentRequestId;
                    } else {
                        String errorMsg = responseJson.has("returnMessage")
                            ? responseJson.get("returnMessage").getAsString() : "Unknown";
                        log.error("MTP sticker payment failed for quoteNo {}: {}", quoteNo, errorMsg);
                        mtpLog.setStatus("FAILED");
                        logRepo.save(mtpLog);
                        return null;
                    }
                }
            }
        } catch (Exception e) {
            log.error("MTP sticker payment attempt exception for quoteNo {}: {}", quoteNo, e.getMessage(), e);
            mtpLog.setStatus("FAILED");
            mtpLog.setResponseJson("{\"error\":\"" + e.getMessage() + "\"}");
            mtpLog.setResponseTime(new Date());
            logRepo.save(mtpLog);
            return null;
        }
    }

	
	public boolean checkMtpPaymentStatus(MtpPaymentTracking tracking, PaymentVendorMaster vendor, String source) {
		String quoteNo = tracking.getQuoteNo();
		MtpPaymentLog statusLog = new MtpPaymentLog();
		statusLog.setTrackingId(tracking.getId());
		statusLog.setQuoteNo(quoteNo);
		statusLog.setAttemptNo(tracking.getCurrentAttempt());
		statusLog.setSource(source + "_STATUS_CHECK");
		statusLog.setEntryDate(new Date());
		statusLog.setRequestTime(new Date());

		try {

			PaymentDetail mtpPaymentDetail = paymentDetailRepo.findTopByQuoteNoAndPaymentsOrderByEntryDateDesc(quoteNo,
					"MTP");

			if (mtpPaymentDetail == null || StringUtils.isBlank(mtpPaymentDetail.getReference())) {
				log.error("MTP status check: no paymentRequestId found for quoteNo {}", quoteNo);
				return false;
			}

			String paymentRequestId = mtpPaymentDetail.getReference() ; //"9392" 
			String paymentChannel = mtpPaymentDetail.getChannel();

// ── Get OAuth token ───────────────────────────────────────────────
			String accessToken = getMtpAccessToken(vendor.getCheckStatusUrl().trim(), vendor.getVendorCode().trim(),
					vendor.getApiSecretKey().trim());
			if (accessToken == null)
				return false;

// ── POST payment-status ───────────────────────────────────────────
			String statusUrl = mtpBaseUrl.trim() + "/Rest/partners/payment-status";
			JsonObject requestPayload = new JsonObject();
			requestPayload.addProperty("paymentRequestId", paymentRequestId);
			requestPayload.addProperty("paymentChannel", StringUtils.isBlank(paymentChannel) ? "" : paymentChannel);

			statusLog.setRequestJson(requestPayload.toString());
			statusLog.setPaymentRequestId(paymentRequestId);

			try (CloseableHttpClient client = HttpClients.createDefault()) {
				HttpPost httpPost = new HttpPost(statusUrl);
				httpPost.setHeader("Authorization", "Bearer " + accessToken);
				httpPost.setHeader("Content-Type", "application/json");
				httpPost.setEntity(new StringEntity(requestPayload.toString(), "UTF-8"));

				try (CloseableHttpResponse httpResponse = client.execute(httpPost)) {
					String responseString = EntityUtils.toString(httpResponse.getEntity());
					log.info("MTP payment-status response for quoteNo {}: {}", quoteNo, responseString);

					statusLog.setResponseJson(responseString);
					statusLog.setResponseTime(new Date());

					JsonObject responseJson = JsonParser.parseString(responseString).getAsJsonObject();
					String paymentStatus = responseJson.has("paymentStatus")
							? responseJson.get("paymentStatus").getAsString()
							: "FAILED";
					String returnMessage = responseJson.has("returnMessage")
							? responseJson.get("returnMessage").getAsString()
							: "";

					statusLog.setStatus(paymentStatus);
					logRepo.save(statusLog);

					if ("COMPLETED".equals(paymentStatus) || "APPROVED".equals(paymentStatus)) {

						mtpPaymentDetail.setPaymentStatus("ACCEPTED");
						mtpPaymentDetail.setResponseMessage(returnMessage);
						mtpPaymentDetail.setResponseTime(new Date());

						if (responseJson.has("transactionId") && !responseJson.get("transactionId").isJsonNull()) {
							String transactionId = responseJson.get("transactionId").getAsString();
							mtpPaymentDetail.setAuthTransRefNo(transactionId);
							mtpPaymentDetail.setMsisdn(transactionId);
						}
						if (responseJson.has("stickerReference")
								&& !responseJson.get("stickerReference").isJsonNull()) {
							mtpPaymentDetail.setAccountNumber(responseJson.get("stickerReference").getAsString());
						}
						paymentDetailRepo.saveAndFlush(mtpPaymentDetail);
						log.info("MTP PaymentDetail updated to ACCEPTED for quoteNo {}", quoteNo);

// ── Update PaymentInfo to ACCEPTED ────────────────────
						PaymentInfo mtpPaymentInfo = paymentinforepo.findByQuoteNoAndPayments(quoteNo, "MTP");
						if (mtpPaymentInfo != null) {
							mtpPaymentInfo.setPaymentStatus("ACCEPTED");
							mtpPaymentInfo.setUpdatedDate(new Date());
							mtpPaymentInfo.setUpdatedBy("SYSTEM");
							paymentinforepo.saveAndFlush(mtpPaymentInfo);
							log.info("MTP PaymentInfo updated to ACCEPTED for quoteNo {}", quoteNo);
						}

// ── Save sticker + download file ──────────────────────
						String stickerReference = responseJson.has("stickerReference")
								? responseJson.get("stickerReference").getAsString()
								: "";
						String downloadLink = responseJson.has("downloadLink")
								? responseJson.get("downloadLink").getAsString()
								: "";
						downloadLink = "https://uiapaymentstest.servicecops.com/test/Rest/stk/"
			                    + "697563771" + "/" + "UBP373R";

						saveStickerDetails(quoteNo, stickerReference, downloadLink);

// ── Mark tracking SUCCESS ─────────────────────────────
						tracking.setStatus("SUCCESS");
						tracking.setUpdatedDate(new Date());
						trackingRepo.save(tracking);

						log.info("MTP sticker SUCCESS for quoteNo {} stickerRef {}", quoteNo, stickerReference);
						return true;
					}

					log.info("MTP status {} for quoteNo {}", paymentStatus, quoteNo);
					return false;
				}
			}

		} catch (Exception e) {
			log.error("MTP status check exception for quoteNo {}: {}", quoteNo, e.getMessage(), e);
			statusLog.setStatus("ERROR");
			statusLog.setResponseJson("{\"error\":\"" + e.getMessage() + "\"}");
			statusLog.setResponseTime(new Date());
			logRepo.save(statusLog);
			return false;
		}
	}

    // ── Save sticker reference + download file → HomePositionMaster ──────────
    private void saveStickerDetails(String quoteNo, String stickerReference, String downloadLink) {
        try {
            HomePositionMaster homePos = homerepo.findByQuoteNo(quoteNo);
            if (homePos == null) return;

            if (StringUtils.isNotBlank(stickerReference)) {
                homePos.setStickerNumber(stickerReference);
            }
            if (StringUtils.isNotBlank(downloadLink)) {
                MotorDataDetails motorData = motorDataDetailsRepo
                    .findTopByQuoteNoOrderByEntryDateDesc(quoteNo);
                String vehicleRegNo = (motorData != null
                    && StringUtils.isNotBlank(motorData.getRegistrationNumber()))
                        ? motorData.getRegistrationNumber() : "UNKNOWN";

                String filePath = downloadAndSaveMtpSticker(
                    stickerReference, vehicleRegNo, downloadLink);
                if (StringUtils.isNotBlank(filePath)) {
                    homePos.setOrangeCardurl(filePath);
                }
            }
            homerepo.save(homePos);
        } catch (Exception e) {
            log.error("Failed to save sticker details for quoteNo {}: {}", quoteNo, e.getMessage(), e);
        }
    }

    private String downloadAndSaveMtpSticker(String stickerReference,
                                              String vehicleRegNo,
                                              String downloadLink) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(downloadLink.trim());
            try (CloseableHttpResponse response = client.execute(httpGet)) {
                if (response.getStatusLine().getStatusCode() != 200) {
                    log.error("MTP sticker download HTTP {} for ref {}", 
                        response.getStatusLine().getStatusCode(), stickerReference);
                    return null;
                }
                org.apache.http.HttpEntity entity = response.getEntity();
                byte[] bytes = EntityUtils.toByteArray(entity);
                String contentType = entity.getContentType() != null
                    ? entity.getContentType().getValue() : "application/pdf";
                String ext = contentType.contains("pdf") ? ".pdf"
                    : contentType.contains("png") ? ".png"
                    : contentType.contains("jpeg") ? ".jpg" : ".pdf";

                String fileName = vehicleRegNo + "_" + stickerReference + ext;
                File dir = new File(mtpStickerDownloadPath);
                if (!dir.exists()) dir.mkdirs();
                File file = new File(dir, fileName);
                try (FileOutputStream fos = new FileOutputStream(file)) {
                    fos.write(bytes);
                }
                return file.getAbsolutePath();
            }
        } catch (Exception e) {
            log.error("MTP sticker download exception for ref {}: {}", stickerReference, e.getMessage(), e);
            return null;
        }
    }

    // ── Insert PaymentDetail for each MTP attempt ────────────────────────────
    private void savePaymentDetailForMtpAttempt(MtpPaymentTracking tracking,
                                                 String paymentRequestId,
                                                 String paymentChannel,
                                                 int attemptNo) {
        try {
            // Fetch latest PaymentInfo for this quoteNo with paymentType=6
            PaymentInfo mtpPaymentInfo = paymentinforepo
                .findByQuoteNoAndPayments(tracking.getQuoteNo(), "MTP");

            if (mtpPaymentInfo == null) {
                log.error("MTP PaymentInfo (type=6) not found for quoteNo {}", tracking.getQuoteNo());
                return;
            }

            PaymentDetail pd = new PaymentDetail();
            pd.setQuoteNo(tracking.getQuoteNo());
            pd.setPaymentId(mtpPaymentInfo.getPaymentId());
            pd.setMerchantReference("MTP-" + tracking.getQuoteNo() + "-A" + attemptNo
                + "-" + System.currentTimeMillis());
            pd.setPaymentType("6");
            pd.setPaymentTypedesc("MTP Sticker Payment");
            pd.setPaymentStatus("PENDING");
            pd.setCompanyId(tracking.getCompanyId());
            pd.setReference(paymentRequestId);
            pd.setChannel(paymentChannel);
            pd.setReqBillToPhone(tracking.getEmployeeMobile());
            pd.setCustomerName(tracking.getEmployeeName());
            pd.setEntryDate(new Date());
            pd.setUpdatedDate(new Date());
            pd.setCreatedBy("SYSTEM");
            pd.setUpdatedBy("SYSTEM");
            paymentDetailRepo.saveAndFlush(pd);
        } catch (Exception e) {
            log.error("Failed to save PaymentDetail for MTP attempt quoteNo {}: {}",
                tracking.getQuoteNo(), e.getMessage(), e);
        }
    }

    // ── OAuth2 helper ─────────────────────────────────────────────────────────
    public String getMtpAccessToken(String tokenUrl, String clientId, String clientSecret) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpPost post = new HttpPost(tokenUrl);
            post.setHeader("Content-Type", "application/x-www-form-urlencoded");
            List<org.apache.http.NameValuePair> params = new ArrayList<>();
            params.add(new org.apache.http.message.BasicNameValuePair("grant_type", "client_credentials"));
            params.add(new org.apache.http.message.BasicNameValuePair("client_id", clientId));
            params.add(new org.apache.http.message.BasicNameValuePair("client_secret", clientSecret));
            post.setEntity(new org.apache.http.client.entity.UrlEncodedFormEntity(params, "UTF-8"));
            try (CloseableHttpResponse response = client.execute(post)) {
                String body = EntityUtils.toString(response.getEntity());
                JsonObject tokenJson = JsonParser.parseString(body).getAsJsonObject();
                if (tokenJson.has("access_token")) {
                    return tokenJson.get("access_token").getAsString();
                }
                log.error("MTP token missing access_token: {}", body);
                return null;
            }
        } catch (Exception e) {
            log.error("MTP OAuth token fetch failed: {}", e.getMessage(), e);
            return null;
        }
    }

    // ── Fetch MTP vendor master ───────────────────────────────────────────────
    public PaymentVendorMaster getMtpVendor(String companyId) {
        List<PaymentVendorMaster> vendors = paymentVendorRepo
            .findByCompanyIdAndStatusAndVendorIdOrderByAmendIdDesc(companyId, "Y", "5");
        return vendors.stream()
            .filter(v -> "mtp".equals(v.getVendorName()))
            .findFirst().orElse(null);
    }
    
    @Async("mtpTaskExecutor")
    public void pollMtpStatusAndRetry(MtpPaymentTracking tracking, PaymentVendorMaster vendor) {
        String quoteNo = tracking.getQuoteNo();
        log.info("MTP async polling started for quoteNo {} attempt {}", 
            quoteNo, tracking.getCurrentAttempt());

        try {
            long intervalMs = statusCheckIntervalSeconds * 1000L;
            int  maxAttempts = tracking.getMaxAttempts();

            for (int attempt = 1; attempt <= maxAttempts; attempt++) {
                log.info("MTP status check attempt {}/{} for quoteNo {}", 
                    attempt, maxAttempts, quoteNo);

                boolean success = checkMtpPaymentStatus(tracking, vendor, "ASYNC_POLL");

                if (success) {
                    log.info("MTP payment SUCCESS on attempt {}/{} for quoteNo {}", 
                        attempt, maxAttempts, quoteNo);
                    return;
                }

                if (attempt < maxAttempts) {
                    log.info("MTP status PENDING — waiting {}s before next check for quoteNo {}",
                        statusCheckIntervalSeconds, quoteNo);
                    try {
                        Thread.sleep(intervalMs);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        log.warn("MTP polling interrupted for quoteNo {}", quoteNo);
                        return;
                    }
                }
            }

            // All attempts exhausted without success
            log.warn("MTP max status check attempts {} exhausted for quoteNo {}", 
                maxAttempts, quoteNo);
            tracking = trackingRepo.findById(tracking.getId()).orElse(tracking);
            tracking.setStatus("EXHAUSTED");
            tracking.setUpdatedDate(new Date());
            trackingRepo.save(tracking);

        } catch (Exception e) {
            log.error("MTP async polling exception for quoteNo {}: {}", quoteNo, e.getMessage(), e);
        }
    }

    // ── Poll status every N seconds until SUCCESS or timeout ─────────────────────
    private boolean pollUntilSuccessOrTimeout(MtpPaymentTracking tracking,
                                               PaymentVendorMaster vendor) {
        long maxWaitMs   = statusCheckMaxWaitMinutes * 60 * 1000L;
        long intervalMs  = statusCheckIntervalSeconds * 1000L;
        long startTime   = System.currentTimeMillis();

        while ((System.currentTimeMillis() - startTime) < maxWaitMs) {
            try {
                log.info("MTP status check for quoteNo {} elapsed {}s",
                    tracking.getQuoteNo(),
                    (System.currentTimeMillis() - startTime) / 1000);

                boolean success = checkMtpPaymentStatus(tracking, vendor, "ASYNC_POLL");
                if (success) return true;

                // Wait before next check
                Thread.sleep(intervalMs);

            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                log.warn("MTP polling interrupted for quoteNo {}", tracking.getQuoteNo());
                return false;
            } catch (Exception e) {
                log.error("MTP poll iteration error for quoteNo {}: {}", 
                    tracking.getQuoteNo(), e.getMessage(), e);
            }
        }

        log.info("MTP polling window ({}min) expired for quoteNo {} attempt {}",
            statusCheckMaxWaitMinutes, tracking.getQuoteNo(), tracking.getCurrentAttempt());
        return false;
    }

    // ── Build a PaymentDetail for retry attempts ──────────────────────────────────
    private PaymentDetail buildRetryPaymentDetail(MtpPaymentTracking tracking) {
        PaymentDetail pd = paymentDetailRepo.findByQuoteNoAndPayments(tracking.getQuoteNo(), "MTP" );
        return pd;
    }
    
    public JsonObject downloadStickerByQuoteNo(String quoteNo) {
        JsonObject response = new JsonObject();
        try {
            
            HomePositionMaster homePos = homerepo.findByQuoteNo(quoteNo);
//            if (homePos == null || StringUtils.isBlank(homePos.getStickerNumber())) {
//                log.error("Sticker number not found in HomePositionMaster for quoteNo {}", quoteNo);
//                response.addProperty("result",           "ERROR");
//                response.addProperty("error",            true);
//                response.addProperty("errorDescription", "Sticker not found");
//                return response;
//            }
            List<PaymentDetail> payments = paymentDetailRepo.findByQuoteNoOrderByEntryDateDesc(quoteNo);
            PaymentDetail pm= payments.get(0);
//            String stickerReference = homePos.getStickerNumber();
            
            String stickerReference = pm.getAccountNumber() ;
            String url=pm.getResSignature();

            MotorDataDetails motorData = motorDataDetailsRepo
                    .findTopByQuoteNoOrderByEntryDateDesc(quoteNo);
//            if (motorData == null || StringUtils.isBlank(motorData.getRegistrationNumber())) {
//                log.error("Vehicle registration number not found for quoteNo {}", quoteNo);
//                response.addProperty("result",           "ERROR");
//                response.addProperty("error",            true);
//                response.addProperty("errorDescription", "Sticker not found");
//                return response;
//            }
            String vehicleRegNo = motorData.getRegistrationNumber();

            // ──Build static download URL ─────────────────────────────────────
            String downloadLink = "https://uiapaymentstest.servicecops.com/test/Rest/stk/"
                    + stickerReference + "/" + vehicleRegNo;
           
            
            log.info("MTP sticker download URL for quoteNo {}: {}", quoteNo, url);
            
            String stickerLink = url == null ? downloadLink : url;
            
            log.info("MTP sticker download URL for quoteNo {}: {}", quoteNo, stickerLink);

            // Fetch sticker bytes
            byte[] stickerBytes = fetchStickerBytes(stickerLink);
            if (stickerBytes == null) {
                log.error("MTP sticker fetch returned null for quoteNo {}", quoteNo);
                response.addProperty("result",           "ERROR");
                response.addProperty("error",            true);
                response.addProperty("errorDescription", "Sticker not found");
                return response;
            }

            // ── Detect content type and encode to base64 ──────────────────────
            String contentType = detectContentType(stickerBytes);
            String base64Data  = Base64.getEncoder().encodeToString(stickerBytes);
            String base64Url   = "data:" + contentType + ";base64," + base64Data;

            // ── Save to disk ──────────────────────────────────────────────────
            String fileName  = vehicleRegNo + "_" + stickerReference + getExtension(contentType);
            File dir         = new File(mtpStickerDownloadPath);
            if (!dir.exists()) dir.mkdirs();
            File stickerFile = new File(dir, fileName);
            try (FileOutputStream fos = new FileOutputStream(stickerFile)) {
                fos.write(stickerBytes);
            }
            log.info("MTP sticker saved at {} for quoteNo {}", stickerFile.getAbsolutePath(), quoteNo);

            // ── Update orangeCardUrl in HomePositionMaster ────────────────────
            homePos.setOrangeCardurl(stickerFile.getAbsolutePath());
            homePos.setStickerNumber(stickerReference);
      
            homerepo.save(homePos);

            // ── Build success response ────────────────────────────────────────
            response.addProperty("result",           "SUCCESS");
            response.addProperty("error",            false);
            response.addProperty("quoteNo",          quoteNo);
            response.addProperty("stickerReference", stickerReference);
            response.addProperty("vehicleRegNo",     vehicleRegNo);
            response.addProperty("contentType",      contentType);
            response.addProperty("filePath",         stickerFile.getAbsolutePath());
            response.addProperty("base64Url",        base64Url);

        } catch (Exception e) {
            log.error("MTP sticker download exception for quoteNo {}: {}", quoteNo, e.getMessage(), e);
            response.addProperty("result",           "ERROR");
            response.addProperty("error",            true);
            response.addProperty("errorDescription", "Sticker not found");
        }
        return response;
    }

    // ── Fetch raw bytes from URL ──────────────────────────────────────────────────
    private byte[] fetchStickerBytes(String downloadLink) {
        try (CloseableHttpClient client = HttpClients.createDefault()) {
            HttpGet httpGet = new HttpGet(downloadLink.trim());
            try (CloseableHttpResponse httpResponse = client.execute(httpGet)) {
                int statusCode = httpResponse.getStatusLine().getStatusCode();
                if (statusCode != 200) {
                    log.error("MTP sticker HTTP {} for URL {}", statusCode, downloadLink);
                    return null;
                }
                return EntityUtils.toByteArray(httpResponse.getEntity());
            }
        } catch (Exception e) {
            log.error("MTP sticker fetch exception: {}", e.getMessage(), e);
            return null;
        }
    }

    // ── Detect content type from magic bytes ─────────────────────────────────────
    private String detectContentType(byte[] bytes) {
        if (bytes == null || bytes.length < 4) return "application/pdf";
        if (bytes[0] == 0x25 && bytes[1] == 0x50
                && bytes[2] == 0x44 && bytes[3] == 0x46) return "application/pdf";
        if (bytes[0] == (byte) 0x89 && bytes[1] == 0x50
                && bytes[2] == 0x4E && bytes[3] == 0x47) return "image/png";
        if (bytes[0] == (byte) 0xFF && bytes[1] == (byte) 0xD8) return "image/jpeg";
        return "application/pdf";
    }

    private String getExtension(String contentType) {
        if ("image/png".equals(contentType))  return ".png";
        if ("image/jpeg".equals(contentType)) return ".jpg";
        return ".pdf";
    }
    
    
 // ── 1. Grid list ──────────────────────────────────────────────────────────────
    public JsonObject getMtpList(String companyId, String status) {
        JsonObject response = new JsonObject();
        try {
            List<String> statuses = StringUtils.isNotBlank(status)
                ? Arrays.asList(status)
                : Arrays.asList("PENDING", "EXHAUSTED", "SUCCESS");

            List<MtpPaymentTracking> trackingList = trackingRepo
                .findByCompanyIdAndStatusInOrderByEntryDateDesc(companyId, statuses);

            JsonArray dataArray = new JsonArray();
            for (MtpPaymentTracking t : trackingList) {

                // Fetch latest PaymentDetail for this quote (MTP row)
                PaymentDetail mtpPd = paymentDetailRepo
                    .findTopByQuoteNoAndPaymentsOrderByEntryDateDesc(t.getQuoteNo(), "MTP");

                // Fetch HomePositionMaster for policyNo and sticker info
                HomePositionMaster homePos = homerepo.findByQuoteNo(t.getQuoteNo());

                JsonObject row = new JsonObject();
                row.addProperty("trackingId",       t.getId());
                row.addProperty("quoteNo",          t.getQuoteNo());
                row.addProperty("policyNo",         t.getPolicyNo() != null ? t.getPolicyNo() : "");
                row.addProperty("companyId",        t.getCompanyId());
                row.addProperty("employeeName",     t.getEmployeeName() != null ? t.getEmployeeName() : "");
                row.addProperty("employeeMobile",   t.getEmployeeMobile() != null ? t.getEmployeeMobile() : "");
                row.addProperty("currentAttempt",   t.getCurrentAttempt());
                row.addProperty("maxAttempts",      t.getMaxAttempts());
                row.addProperty("status",           t.getStatus());
                row.addProperty("entryDate",        t.getEntryDate() != null ? t.getEntryDate().toString() : "");
                row.addProperty("updatedDate",      t.getUpdatedDate() != null ? t.getUpdatedDate().toString() : "");

                // PaymentDetail fields
                if (mtpPd != null) {
                    row.addProperty("paymentStatus",    mtpPd.getPaymentStatus() != null ? mtpPd.getPaymentStatus() : "");
                    row.addProperty("paymentRequestId", mtpPd.getReference() != null ? mtpPd.getReference() : "");
                    row.addProperty("paymentChannel",   mtpPd.getChannel() != null ? mtpPd.getChannel() : "");
                    row.addProperty("merchantReference",mtpPd.getMerchantReference() != null ? mtpPd.getMerchantReference() : "");
                    row.addProperty("transactionId",    mtpPd.getAuthTransRefNo() != null ? mtpPd.getAuthTransRefNo() : "");
                }

                // Sticker fields
                if (homePos != null) {
                    row.addProperty("stickerNumber",  homePos.getStickerNumber() != null ? homePos.getStickerNumber() : "");
                    row.addProperty("orangeCardUrl",  homePos.getOrangeCardurl() != null ? homePos.getOrangeCardurl() : "");
                }

                dataArray.add(row);
            }

            response.addProperty("result", "SUCCESS");
            response.addProperty("count",  dataArray.size());
            response.add("data", dataArray);

        } catch (Exception e) {
            log.error("getMtpList error: {}", e.getMessage(), e);
            response.addProperty("result",  "ERROR");
            response.addProperty("message", e.getMessage());
        }
        return response;
    }

    // ── 2. Manual check status ────────────────────────────────────────────────────
    public JsonObject manualCheckStatus(String quoteNo) {
        JsonObject response = new JsonObject();
        try {
            MtpPaymentTracking tracking = trackingRepo
                .findByQuoteNoAndStatusIn(quoteNo,
                    Arrays.asList("PENDING", "EXHAUSTED", "SUCCESS"))
                .orElse(null);

            if (tracking == null) {
                response.addProperty("result",  "ERROR");
                response.addProperty("message", "No MTP tracking found for quoteNo: " + quoteNo);
                return response;
            }

            if ("SUCCESS".equals(tracking.getStatus())) {
                response.addProperty("result",  "SUCCESS");
                response.addProperty("message", "Payment already completed");
                response.addProperty("status",  "SUCCESS");
                return response;
            }

            PaymentVendorMaster vendor = getMtpVendor(tracking.getCompanyId());
            if (vendor == null) {
                response.addProperty("result",  "ERROR");
                response.addProperty("message", "MTP vendor not found for companyId: " + tracking.getCompanyId());
                return response;
            }

            boolean success = checkMtpPaymentStatus(tracking, vendor, "MANUAL_CHECK");

            response.addProperty("result",  success ? "SUCCESS" : "PENDING");
            response.addProperty("status",  success ? "SUCCESS" : "PENDING");
            response.addProperty("message", success
                ? "Payment completed successfully"
                : "Payment still pending");
            response.addProperty("quoteNo", quoteNo);

        } catch (Exception e) {
            log.error("manualCheckStatus error for quoteNo {}: {}", quoteNo, e.getMessage(), e);
            response.addProperty("result",  "ERROR");
            response.addProperty("message", e.getMessage());
        }
        return response;
    }

    // ── 3. Manual rehit ───────────────────────────────────────────────────────────
    public JsonObject manualRehit(MtpRehitReq req) {
        JsonObject response = new JsonObject();
        try {
            String quoteNo = req.getQuoteNo();

            MtpPaymentTracking tracking = trackingRepo
                .findByQuoteNoAndStatusIn(quoteNo,
                    Arrays.asList("PENDING", "EXHAUSTED", "SUCCESS"))
                .orElse(null);

            if (tracking == null) {
                response.addProperty("result",  "ERROR");
                response.addProperty("message", "No MTP tracking found for quoteNo: " + quoteNo);
                return response;
            }

            // Update mobile/name if provided
            if (StringUtils.isNotBlank(req.getMobileNo())) {
                tracking.setEmployeeMobile(req.getMobileNo());
            }
            if (StringUtils.isNotBlank(req.getEmployeeName())) {
                tracking.setEmployeeName(req.getEmployeeName());
            }

            PaymentVendorMaster vendor = getMtpVendor(tracking.getCompanyId());
            if (vendor == null) {
                response.addProperty("result",  "ERROR");
                response.addProperty("message", "MTP vendor not found");
                return response;
            }

            // Build PaymentDetail for rehit
            PaymentDetail rehitPd = buildRetryPaymentDetail(tracking);

            // Trigger payment
            JsonObject mtpResponse = createOrderForPaymentMTP(rehitPd);

            if (mtpResponse == null
                    || !"SUCCESS".equalsIgnoreCase(mtpResponse.get("result").getAsString())) {
                log.error("MTP rehit payment failed for quoteNo {}: {}", quoteNo, mtpResponse);
                response.addProperty("result",  "ERROR");
                response.addProperty("message", "MTP payment initiation failed");
                return response;
            }

            // Extract response fields
            JsonObject dataObj      = mtpResponse.get("data").getAsJsonArray().get(0).getAsJsonObject();
            String paymentRequestId = dataObj.has("paymentRequestId")
                ? dataObj.get("paymentRequestId").getAsString() : "";
            String paymentChannel   = dataObj.has("paymentChannel")
                ? dataObj.get("paymentChannel").getAsString() : "";

            // Insert new PaymentDetail row for this rehit
            PaymentInfo mtpInfo = paymentinforepo.findByQuoteNoAndPayments(quoteNo, "MTP");
            if (mtpInfo != null) {
                int nextAttempt = tracking.getCurrentAttempt() + 1;
                rehitPd.setPaymentId(mtpInfo.getPaymentId());
                rehitPd.setMerchantReference("MTP-" + quoteNo + "-REHIT-" + System.currentTimeMillis());
                rehitPd.setReference(paymentRequestId);
                rehitPd.setChannel(paymentChannel);
                rehitPd.setPayments("MTP");
                rehitPd.setPaymentStatus("PENDING");
                paymentDetailRepo.saveAndFlush(rehitPd);

                // Log rehit attempt
                MtpPaymentLog rehitLog = new MtpPaymentLog();
                rehitLog.setTrackingId(tracking.getId());
                rehitLog.setQuoteNo(quoteNo);
                rehitLog.setAttemptNo(nextAttempt);
                rehitLog.setSource("MANUAL_REHIT");
                rehitLog.setPaymentRequestId(paymentRequestId);
                rehitLog.setPaymentChannel(paymentChannel);
                rehitLog.setRequestJson(rehitPd.toString());
                rehitLog.setResponseJson(mtpResponse.toString());
                rehitLog.setRequestTime(new Date());
                rehitLog.setResponseTime(new Date());
                rehitLog.setStatus("INITIATED");
                rehitLog.setEntryDate(new Date());
                logRepo.save(rehitLog);

                // Update tracking
                tracking.setCurrentAttempt(nextAttempt);
                tracking.setStatus("PENDING");
                tracking.setUpdatedDate(new Date());
                trackingRepo.save(tracking);
            }

            // Spawn async status check for this rehit
            pollMtpStatusAndRetry(tracking, vendor);

            response.addProperty("result",          "SUCCESS");
            response.addProperty("message",         "MTP payment retriggered successfully");
            response.addProperty("quoteNo",         quoteNo);
            response.addProperty("paymentRequestId",paymentRequestId);
            response.addProperty("paymentChannel",  paymentChannel);

        } catch (Exception e) {
            log.error("manualRehit error for quoteNo {}: {}", req.getQuoteNo(), e.getMessage(), e);
            response.addProperty("result",  "ERROR");
            response.addProperty("message", e.getMessage());
        }
        return response;
    }

    // ── 4. Get logs for a quote ───────────────────────────────────────────────────
    public JsonObject getMtpLogs(String quoteNo) {
        JsonObject response = new JsonObject();
        try {
            MtpPaymentTracking tracking = trackingRepo
                .findByQuoteNoAndStatusIn(quoteNo,
                    Arrays.asList("PENDING", "SUCCESS", "EXHAUSTED", "FAILED"))
                .orElse(null);

            if (tracking == null) {
                response.addProperty("result",  "ERROR");
                response.addProperty("message", "No MTP tracking found for quoteNo: " + quoteNo);
                return response;
            }

            List<MtpPaymentLog> logs = logRepo
                .findByTrackingIdOrderByEntryDateAsc(tracking.getId());

            JsonArray logArray = new JsonArray();
            for (MtpPaymentLog l : logs) {
                JsonObject logObj = new JsonObject();
                logObj.addProperty("logId",            l.getId());
                logObj.addProperty("attemptNo",        l.getAttemptNo() != null ? l.getAttemptNo() : 0);
                logObj.addProperty("source",           l.getSource() != null ? l.getSource() : "");
                logObj.addProperty("paymentRequestId", l.getPaymentRequestId() != null ? l.getPaymentRequestId() : "");
                logObj.addProperty("paymentChannel",   l.getPaymentChannel() != null ? l.getPaymentChannel() : "");
                logObj.addProperty("status",           l.getStatus() != null ? l.getStatus() : "");
                logObj.addProperty("requestJson",      l.getRequestJson() != null ? l.getRequestJson() : "");
                logObj.addProperty("responseJson",     l.getResponseJson() != null ? l.getResponseJson() : "");
                logObj.addProperty("requestTime",      l.getRequestTime() != null ? l.getRequestTime().toString() : "");
                logObj.addProperty("responseTime",     l.getResponseTime() != null ? l.getResponseTime().toString() : "");
                logObj.addProperty("entryDate",        l.getEntryDate() != null ? l.getEntryDate().toString() : "");
                logArray.add(logObj);
            }

            response.addProperty("result",   "SUCCESS");
            response.addProperty("quoteNo",  quoteNo);
            response.addProperty("trackingStatus", tracking.getStatus());
            response.addProperty("currentAttempt", tracking.getCurrentAttempt());
            response.addProperty("maxAttempts",    tracking.getMaxAttempts());
            response.add("logs", logArray);

        } catch (Exception e) {
            log.error("getMtpLogs error for quoteNo {}: {}", quoteNo, e.getMessage(), e);
            response.addProperty("result",  "ERROR");
            response.addProperty("message", e.getMessage());
        }
        return response;
    }
    
    public JsonObject createOrderForPaymentMTP(PaymentDetail payment) {
		try {

			if(payment!=null ) {
				PaymentInfo paymentInfo = paymentinforepo.findByQuoteNoAndPaymentId(payment.getQuoteNo(), payment.getPaymentId());

				String userytype="b2b";
				if(paymentInfo.getSubUserType().equalsIgnoreCase("b2c")) {
					userytype="b2c";
				}

				List<PaymentVendorMaster> paymentId= paymentVendorRepo.findByCompanyIdAndStatusAndVendorIdAndUserTypeAndProductIdOrderByAmendIdDesc(payment.getCompanyId(),"Y","1",userytype,paymentInfo.getProductId());
				PaymentVendorMaster vendor =null;
				if((paymentInfo.getProductId().equals(5) && payment.getCompanyId().equals("100019") &&  payment.getPaymentType().equals("6")) ||
						(paymentInfo.getProductId().equals(125) && payment.getCompanyId().equals("100019"))) {
					vendor =getMtpVendor(payment.getCompanyId());	
				}
				
				else if(paymentId!=null && paymentId.size()>0) {						 
					vendor = paymentId.get(0);
				}else {
					paymentId=paymentVendorRepo.findByCompanyIdAndStatusAndVendorIdAndUserTypeAndProductIdOrderByAmendIdDesc(payment.getCompanyId(),"Y","1",userytype,99999);
					vendor = paymentId.get(0);
				}
				
				JsonObject response = null;
				
				if ("mtp".equals(vendor.getVendorName())) {
				    return mtpForMot(vendor, payment, paymentInfo);
				}
				else {
					return null;
				}
				
			}

		}catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
    
    private JsonObject mtpForMot(PaymentVendorMaster vendor, PaymentDetail payment, PaymentInfo paymentInfo) {
	    JsonObject jsonResponse = new JsonObject();
	    try {
	        
	        String quoteNo = paymentInfo.getQuoteNo();
	        MotorDataDetails motorData = motorDataDetailsRepo.findTopByQuoteNoOrderByEntryDateDesc(quoteNo);

	        if (motorData == null) {
	            log.error("MTP: No motor data found for quoteNo {}", quoteNo);
	            jsonResponse.addProperty("result", "ERROR");
	            jsonResponse.addProperty("message", "Motor data not found for quote: " + quoteNo);
	            return jsonResponse;
	        }

	        String numberPlate   = motorData.getRegistrationNumber();
	        String insuranceType = motorData.getInsuranceType(); 
	        String assessmentType = "1".equals(insuranceType) ? "M" : "E";

	        String accessToken = getMtpAccessToken(
	        		vendor.getCheckStatusUrl().trim(),   
	            vendor.getVendorCode(),          
	            vendor.getApiSecretKey()    
	        );
	        if (accessToken == null) {
	            jsonResponse.addProperty("result", "ERROR");
	            jsonResponse.addProperty("message", "MTP OAuth token fetch failed");
	            return jsonResponse;
	        }

	        
	        JsonObject requestPayload = new JsonObject();
	        requestPayload.addProperty("numberPlate",        numberPlate);
	        requestPayload.addProperty("msisdn",             "0"+payment.getReqBillToPhone());
	        requestPayload.addProperty("assessmentType",     assessmentType);
	        requestPayload.addProperty("partnerIdentifier",  "ALLIANCE");  

	        
	        try (CloseableHttpClient client = HttpClients.createDefault()) {
	        	String paymentUrl = vendor.getPaymentUrlLink().trim();
	            HttpPost httpPost = new HttpPost(paymentUrl);
	            httpPost.setHeader("Authorization", "Bearer " + accessToken);
	            httpPost.setHeader("Content-Type", "application/json");
	            httpPost.setEntity(new StringEntity(requestPayload.toString(), "UTF-8"));

	            try (CloseableHttpResponse httpResponse = client.execute(httpPost)) {
	                String responseString = EntityUtils.toString(httpResponse.getEntity());
	                log.info("MTP initiate-payment response for quoteNo {}: {}", quoteNo, responseString);

	                JsonObject responseJson = JsonParser.parseString(responseString).getAsJsonObject();
	                int returnCode = responseJson.has("returnCode")
	                        ? responseJson.get("returnCode").getAsInt() : -1;

	                if (returnCode == 0) {
	                    String mtpPaymentRequestId = responseJson.get("paymentRequestId").getAsString();
	                    String paymentChannel      = responseJson.has("paymentChannel")
	                            ? responseJson.get("paymentChannel").getAsString() : "";
	                    String returnMessage       = responseJson.get("returnMessage").getAsString();

//	                    payment.setReference(mtpPaymentRequestId);
//	                    payment.setChannel(paymentChannel);
//	                    paymentinforepo.save(paymentInfo);
	                    

	                    log.info("MTP paymentRequestId {} and channel {} saved to paymentInfo for quoteNo {}",
	                            mtpPaymentRequestId, paymentChannel, quoteNo);

	                   
	                    jsonResponse.addProperty("result", "SUCCESS");
	                    jsonResponse.addProperty("message", returnMessage);

	                    JsonObject innerResponse = new JsonObject();
	                    innerResponse.addProperty("payment_gateway_url", "");  
	                    innerResponse.addProperty("paymentRequestId", mtpPaymentRequestId);
	                    innerResponse.addProperty("paymentChannel", paymentChannel);
	                    innerResponse.addProperty("returnMessage", returnMessage);
	                    
	                    payment.setReference(mtpPaymentRequestId);
	                    payment.setChannel(paymentChannel);
	                    payment.setResponseMessage(innerResponse.toString() );
	                    JsonArray dataArray = new JsonArray();
	                    dataArray.add(innerResponse);
	                    jsonResponse.add("data", dataArray);

	                } else {
	                    String errorMsg = responseJson.has("returnMessage")
	                            ? responseJson.get("returnMessage").getAsString()
	                            : "Unknown error from MTP";
	                    log.error("MTP payment failed for quoteNo {}: returnCode={}, message={}",
	                            quoteNo, returnCode, errorMsg);
	                    jsonResponse.addProperty("result", "ERROR");
	                    jsonResponse.addProperty("message", errorMsg);
	                    jsonResponse.addProperty("returnCode", returnCode);
	                    payment.setResponseMessage(errorMsg);
	                }
	            }
	        }

	    } catch (Exception e) {
	        log.error("MTP payment exception for quoteNo {}: {}", paymentInfo.getQuoteNo(), e.getMessage(), e);
	        jsonResponse.addProperty("result", "ERROR");
	        jsonResponse.addProperty("message", "MTP payment initiation failed: " + e.getMessage());
	    }
	    return jsonResponse;
	}
    
}
