package com.maan.eway.mtpintegration.mtppayment.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.google.gson.JsonObject;
import com.maan.eway.bean.PaymentDetail;
import com.maan.eway.bean.PaymentInfo;
import com.maan.eway.bean.PaymentVendorMaster;
import com.maan.eway.mtpintegration.mtppayment.entity.MtpPaymentLog;
import com.maan.eway.mtpintegration.mtppayment.entity.MtpPaymentTracking;
import com.maan.eway.mtpintegration.mtppayment.repository.MtpPaymentLogRepository;
import com.maan.eway.mtpintegration.mtppayment.repository.MtpPaymentTrackingRepository;
import com.maan.eway.payment.service.SelcomPaymentService;
import com.maan.eway.repository.PaymentDetailRepository;
import com.maan.eway.repository.PaymentInfoRepository;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class MtpStickerScheduler {

    @Autowired 
    private MtpPaymentTrackingRepository trackingRepo;
    @Autowired 
    private MtpPaymentStickerService mtpStickerService;
    
    @Autowired
	private SelcomPaymentService selcomService;
    
    @Autowired
	private PaymentDetailRepository paymentdetailrepo;
	
	@Autowired
	private PaymentInfoRepository paymentinforepo;
	
	@Autowired
	private MtpPaymentLogRepository logRepo;

    public void processPendingMtpPayments() {
        List<MtpPaymentTracking> dueRows = trackingRepo.findDueForProcessing(new Date());
        if (dueRows.isEmpty()) return;

        for (MtpPaymentTracking tracking : dueRows) {
            try {
                PaymentVendorMaster vendor = mtpStickerService.getMtpVendor(tracking.getCompanyId());
                if (vendor == null) continue;

                // ── Always check status of last attempt first ─────────────────
                boolean success = mtpStickerService.checkMtpPaymentStatus(
                    tracking, vendor, "SCHEDULER");

                if (success) {
                    // Status is COMPLETED — tracking already marked SUCCESS inside service
                    continue;
                }

                // ── Not success yet — check if we can retry ───────────────────
                if (tracking.getCurrentAttempt() >= tracking.getMaxAttempts()) {
                    log.warn("MTP exhausted for quoteNo {}", tracking.getQuoteNo());
                    tracking.setStatus("EXHAUSTED");
                    tracking.setUpdatedDate(new Date());
                    trackingRepo.save(tracking);
                    continue;
                }

                // ── Trigger next payment attempt via createOrderForPayment ────
                // Build a fresh PaymentDetail for the retry
                PaymentDetail retryPd = new PaymentDetail();
                retryPd.setQuoteNo(tracking.getQuoteNo());
                retryPd.setCompanyId(tracking.getCompanyId());
                retryPd.setPaymentType("6");
                retryPd.setReqBillToPhone(tracking.getEmployeeMobile());
                retryPd.setCustomerName(tracking.getEmployeeName());
                retryPd.setEntryDate(new Date());
                retryPd.setCreatedBy("SYSTEM");
                retryPd.setUpdatedBy("SYSTEM");

                JsonObject retryResponse = selcomService.createOrderForPayment(retryPd);

                int nextAttempt = tracking.getCurrentAttempt() + 1;

                if (retryResponse != null
                        && "SUCCESS".equalsIgnoreCase(retryResponse.get("result").getAsString())) {

                    JsonObject retryData = retryResponse.get("data")
                        .getAsJsonArray().get(0).getAsJsonObject();
                    String newRequestId = retryData.has("paymentRequestId")
                        ? retryData.get("paymentRequestId").getAsString() : "";
                    String newChannel   = retryData.has("paymentChannel")
                        ? retryData.get("paymentChannel").getAsString() : "";

                    // Insert new PaymentDetail row for this retry attempt
                    PaymentInfo mtpInfo = paymentinforepo
                        .findByQuoteNoAndPayments(tracking.getQuoteNo(), "MTP");
                    if (mtpInfo != null) {
                        String retryRef = "MTP-" + tracking.getQuoteNo()
                            + "-A" + nextAttempt + "-" + System.currentTimeMillis();
                        retryPd.setPaymentId(mtpInfo.getPaymentId());
                        retryPd.setMerchantReference(retryRef);
                        retryPd.setReference(newRequestId);
                        retryPd.setChannel(newChannel);
                        retryPd.setPayments("MTP");
                        retryPd.setPaymentStatus("PENDING");
                        paymentdetailrepo.saveAndFlush(retryPd);
                    }

                    // Log the retry attempt
                    MtpPaymentLog retryLog = new MtpPaymentLog();
                    retryLog.setTrackingId(tracking.getId());
                    retryLog.setQuoteNo(tracking.getQuoteNo());
                    retryLog.setAttemptNo(nextAttempt);
                    retryLog.setSource("SCHEDULER");
                    retryLog.setPaymentRequestId(newRequestId);
                    retryLog.setPaymentChannel(newChannel);
                    retryLog.setResponseJson(retryResponse.toString());
                    retryLog.setRequestTime(new Date());
                    retryLog.setResponseTime(new Date());
                    retryLog.setStatus("INITIATED");
                    retryLog.setEntryDate(new Date());
                    logRepo.save(retryLog);
                }

                // Update tracking — advance attempt count and set next due time
                tracking.setCurrentAttempt(nextAttempt);
                tracking.setUpdatedDate(new Date());
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.MINUTE, tracking.getIntervalMinutes());
                tracking.setNextAttemptDue(cal.getTime());
                trackingRepo.save(tracking);

            } catch (Exception e) {
                log.error("MTP scheduler error for quoteNo {}: {}",
                    tracking.getQuoteNo(), e.getMessage(), e);
            }
        }
    }
}