package com.maan.eway.mtpintegration.mtppayment.controller;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonObject;
import com.maan.eway.bean.PaymentVendorMaster;
import com.maan.eway.mtpintegration.mtppayment.dto.MtpRehitReq;
import com.maan.eway.mtpintegration.mtppayment.entity.MtpPaymentTracking;
import com.maan.eway.mtpintegration.mtppayment.repository.MtpPaymentLogRepository;
import com.maan.eway.mtpintegration.mtppayment.repository.MtpPaymentTrackingRepository;
import com.maan.eway.mtpintegration.mtppayment.service.MtpPaymentStickerService;

import lombok.extern.slf4j.Slf4j;

//MtpStickerController.java
@RestController
@RequestMapping("/v1/mtp")
@Slf4j
public class MtpPaymentStickerController {

 @Autowired private MtpPaymentStickerService mtpStickerService;
 @Autowired private MtpPaymentTrackingRepository trackingRepo;
 @Autowired private MtpPaymentLogRepository logRepo;

 // List pending/failed/exhausted MTP payments
 @GetMapping("/pending-list")
 public ResponseEntity<Object> getPendingList(
         @RequestParam String companyId,
         @RequestParam(required = false) String status) {
     List<String> statuses = StringUtils.isNotBlank(status)
         ? Arrays.asList(status)
         : Arrays.asList("PENDING", "FAILED", "EXHAUSTED");
     List<MtpPaymentTracking> list = trackingRepo
         .findByCompanyIdAndStatusInOrderByEntryDateDesc(companyId, statuses);
     return ResponseEntity.ok(list);
 }

 // Manual rehit — optionally change mobile number
 @PostMapping("/rehit")
 public ResponseEntity<Object> rehit(@RequestBody MtpRehitReq req) {
     try {
         MtpPaymentTracking tracking = trackingRepo
             .findByQuoteNoAndStatusIn(req.getQuoteNo(),
                 Arrays.asList("PENDING", "EXHAUSTED", "FAILED"))
             .orElse(null);

         if (tracking == null) {
             return ResponseEntity.ok()
                 .body("{\"result\":\"ERROR\",\"message\":\"No active MTP tracking found\"}");
         }

         if (StringUtils.isNotBlank(req.getMobileNo())) {
             tracking.setEmployeeMobile(req.getMobileNo());
         }
         if (StringUtils.isNotBlank(req.getEmployeeName())) {
             tracking.setEmployeeName(req.getEmployeeName());
         }
         // Reset to PENDING so scheduler doesn't conflict
         tracking.setStatus("PENDING");
         tracking.setUpdatedDate(new Date());
         trackingRepo.save(tracking);

         PaymentVendorMaster vendor = mtpStickerService.getMtpVendor(tracking.getCompanyId());
         if (vendor == null) {
             return ResponseEntity.ok()
                 .body("{\"result\":\"ERROR\",\"message\":\"MTP vendor not found\"}");
         }

         // Trigger immediately, no scheduler involvement
         String paymentRequestId = mtpStickerService.triggerMtpPaymentAttempt(
             tracking, vendor, "MANUAL_REHIT");

         if (paymentRequestId != null) {
             return ResponseEntity.ok(
                 "{\"result\":\"SUCCESS\",\"paymentRequestId\":\"" + paymentRequestId + "\"}");
         } else {
             return ResponseEntity.ok(
                 "{\"result\":\"ERROR\",\"message\":\"MTP payment initiation failed\"}");
         }
     } catch (Exception e) {
         log.error("MTP rehit error for quoteNo {}: {}", req.getQuoteNo(), e.getMessage(), e);
         return ResponseEntity.status(500).body("{\"result\":\"ERROR\"}");
     }
 }

 // Manual status check for a specific quote
 @PostMapping("/check-status")
 public ResponseEntity<Object> checkStatus(@RequestBody MtpRehitReq req) {
     try {
         MtpPaymentTracking tracking = trackingRepo
             .findByQuoteNoAndStatusIn(req.getQuoteNo(),
                 Arrays.asList("PENDING", "EXHAUSTED"))
             .orElse(null);
         if (tracking == null) {
             return ResponseEntity.badRequest()
                 .body("{\"result\":\"ERROR\",\"message\":\"No tracking row found\"}");
         }
         PaymentVendorMaster vendor = mtpStickerService.getMtpVendor(tracking.getCompanyId());
         boolean success = mtpStickerService.checkMtpPaymentStatus(
             tracking, vendor, "MANUAL_REHIT");
         return ResponseEntity.ok(
             "{\"result\":\"" + (success ? "SUCCESS" : "PENDING") + "\"}");
     } catch (Exception e) {
         return ResponseEntity.status(500).body("{\"result\":\"ERROR\"}");
     }
 }

 // View all logs for a quote
 @GetMapping("/logs/{quoteNo}")
 public ResponseEntity<Object> getLogs(@PathVariable String quoteNo) {
     MtpPaymentTracking tracking = trackingRepo
         .findByQuoteNoAndStatusIn(quoteNo,
             Arrays.asList("PENDING","SUCCESS","EXHAUSTED","FAILED"))
         .orElse(null);
     if (tracking == null) return ResponseEntity.ok("[]");
     return ResponseEntity.ok(logRepo.findByTrackingIdOrderByEntryDateAsc(tracking.getId()));
 }
 
 @GetMapping("/download-sticker/{quoteNo}")
 public ResponseEntity<Object> downloadStickerByQuoteNo(
         @PathVariable("quoteNo") String quoteNo) {
     JsonObject response = mtpStickerService.downloadStickerByQuoteNo(quoteNo);
     if ("ERROR".equals(response.get("result").getAsString())) {
         return ResponseEntity.status(HttpStatus.OK).body(response.toString());
     }
     return ResponseEntity.ok(response.toString());
 }
 
}
