package com.maan.eway.common.controller;

import java.util.Base64;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maan.eway.bean.HomePositionMaster;
import com.maan.eway.common.res.CommonRes;
import com.maan.eway.common.res.PdfResult;
import com.maan.eway.repository.HomePositionMasterRepository;
import com.maan.eway.viewAll.dto.viewAllReq;
import com.maan.eway.viewAll.service.ViewAllWithLableService;

@RestController
@RequestMapping("/policy")
public class QRController {

	@Autowired
	private HomePositionMasterRepository homeRepo;
	
	@Autowired
	private ViewAllWithLableService service;
	
	@GetMapping("/verify/{token}")
	public ResponseEntity<?> verifyPolicy(@PathVariable String token) {
		
		Optional<HomePositionMaster> policy = homeRepo.findByQrValidationCode(token);
		
		if (policy.isEmpty()) {

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body("Invalid QR Code");
        }
		
		HomePositionMaster details = policy.get();
		
		viewAllReq req = new viewAllReq();
		
		req.setQuoteNo(details.getQuoteNo());
		
		CommonRes data = service.omanRecordForall(req);
		
		if (data == null || data.getCommonResponse() == null) {
	        return ResponseEntity
	                .status(HttpStatus.NOT_FOUND)
	                .body("PDF not available");
	    }

		try {
			ObjectMapper objectMapper = new ObjectMapper();

	        PdfResult pdfResult = objectMapper.convertValue(
	                data.getCommonResponse(),
	                PdfResult.class
	        );
	        
	        if (pdfResult == null || pdfResult.getBase64() == null
	                || pdfResult.getBase64().isBlank()) {

	            return ResponseEntity
	                    .status(HttpStatus.NOT_FOUND)
	                    .body("PDF Base64 data not available");
	        }
	        
	        String base64 = pdfResult.getBase64();
	        
	        if (base64.contains(",")) {
	            base64 = base64.substring(base64.indexOf(",") + 1);
	        }
	        
	        byte[] pdfBytes = Base64.getDecoder().decode(base64);
	        
	        return ResponseEntity.ok()
	                .header(
	                        HttpHeaders.CONTENT_DISPOSITION,
	                        "inline; filename=\"policy-"
	                                + details.getQuoteNo()
	                                + ".pdf\""
	                )
	                .contentType(MediaType.APPLICATION_PDF)
	                .contentLength(pdfBytes.length)
	                .body(pdfBytes);
		} catch (IllegalArgumentException e) {

			e.printStackTrace();
	        return ResponseEntity
	                .status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("Invalid PDF Base64 data");

	    }catch (Exception e) {
	    	e.printStackTrace();
	        return ResponseEntity
	                .status(HttpStatus.INTERNAL_SERVER_ERROR)
	                .body("Unable to generate PDF");
	    }
				
	}
	
}
