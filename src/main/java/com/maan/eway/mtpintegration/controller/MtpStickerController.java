package com.maan.eway.mtpintegration.controller;


/*
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.mtpintegration.service.MtpStickerService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/mtp")
//@RequiredArgsConstructor
public class MtpStickerController {

    private final MtpStickerService stickerService;
    
    public MtpStickerController (MtpStickerService mtpStickerService) {
    	this.stickerService=mtpStickerService;
    }
    
    
    @GetMapping("/download-sticker/{stickerReference}/{vehicleNumber}")
    public ResponseEntity<byte[]> downloadSticker(
            @PathVariable String stickerReference,
            @PathVariable String vehicleNumber) {
    
  
          ResponseEntity<byte[]> response = stickerService.downloadSticker(stickerReference, vehicleNumber);

        String fileName = "sticker-" + vehicleNumber + ".pdf";
        
        
        
        
        
        return ResponseEntity.status(response.getStatusCode())
                .headers(response.getHeaders())
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=" + fileName)
                .header(HttpHeaders.CONTENT_TYPE, "application/pdf")

                .body(response.getBody());
    }
}*/



import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.gson.JsonObject;
import com.maan.eway.mtpintegration.dto.MtpListRequest;
import com.maan.eway.mtpintegration.mtppayment.dto.MtpRehitReq;
import com.maan.eway.mtpintegration.mtppayment.service.MtpPaymentStickerService;
import com.maan.eway.mtpintegration.service.MtpStickerService;

@RestController
@RequestMapping("/api/mtp")
public class MtpStickerController {

    private static final Logger log = LoggerFactory.getLogger(MtpStickerController.class);

    private final MtpStickerService stickerService;
    
    private final MtpPaymentStickerService mtpStickerService;

    public MtpStickerController(MtpStickerService stickerService, MtpPaymentStickerService mtpStickerService) {
        this.stickerService = stickerService;
        this.mtpStickerService=mtpStickerService;
    }

    @GetMapping("/Rest/stk/{stickerReference}/{vehicleNumber}")
    public ResponseEntity<byte[]> downloadSticker(@PathVariable String stickerReference,
                                                  @PathVariable String vehicleNumber) {
        log.info("Sticker download request. stickerReference={}, vehicleNumber={}", stickerReference, vehicleNumber);

        ResponseEntity<byte[]> response = stickerService.downloadSticker(stickerReference, vehicleNumber);
        String fileName = "sticker-" + vehicleNumber + ".pdf";

        return ResponseEntity.status(response.getStatusCode())
                .headers(response.getHeaders())
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .contentType(MediaType.APPLICATION_PDF)
                .body(response.getBody());
    }
    
	 
	 // ── 1. Pending/Failed/Exhausted list for grid ─────────────────────────────────
    @PostMapping("/list")
    public ResponseEntity<Object> getMtpList(@RequestBody MtpListRequest request) {
        try {
            JsonObject response = mtpStickerService.getMtpList(
                    request.getCompanyId(),
                    request.getStatus());

            return ResponseEntity.ok(response.toString());

        } catch (Exception e) {
            log.error("MTP list error: {}", e.getMessage(), e);

            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("{\"result\":\"ERROR\",\"message\":\"" + e.getMessage() + "\"}");
        }
    }
	
	 // ── 2. Manual check status for a specific quote ───────────────────────────────
	 @PostMapping("/v1/mtp/check-status")
	 public ResponseEntity<Object> checkStatus(@RequestBody MtpRehitReq req) {
	     try {
	         JsonObject response = mtpStickerService.manualCheckStatus(req.getQuoteNo());
	         return ResponseEntity.ok(response.toString());
	     } catch (Exception e) {
	         log.error("MTP check status error for quoteNo {}: {}", req.getQuoteNo(), e.getMessage(), e);
	         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                 .body("{\"result\":\"ERROR\",\"message\":\"" + e.getMessage() + "\"}");
	     }
	 }
	
	 // ── 3. Manual rehit — retrigger payment with same or new mobile ───────────────
	 @PostMapping("/rehit")
	 public ResponseEntity<Object> rehit(@RequestBody MtpRehitReq req) {
	     try {
	         JsonObject response = mtpStickerService.manualRehit(req);
	         return ResponseEntity.ok(response.toString());
	     } catch (Exception e) {
	         log.error("MTP rehit error for quoteNo {}: {}", req.getQuoteNo(), e.getMessage(), e);
	         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                 .body("{\"result\":\"ERROR\",\"message\":\"" + e.getMessage() + "\"}");
	     }
	 }
	
	 // ── 4. View all logs for a quote ──────────────────────────────────────────────
	 @GetMapping("/v1/mtp/logs/{quoteNo}")
	 public ResponseEntity<Object> getLogs(@PathVariable String quoteNo) {
	     try {
	         JsonObject response = mtpStickerService.getMtpLogs(quoteNo);
	         return ResponseEntity.ok(response.toString());
	     } catch (Exception e) {
	         log.error("MTP logs error for quoteNo {}: {}", quoteNo, e.getMessage(), e);
	         return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
	                 .body("{\"result\":\"ERROR\",\"message\":\"" + e.getMessage() + "\"}");
	     }
 }
    
}
