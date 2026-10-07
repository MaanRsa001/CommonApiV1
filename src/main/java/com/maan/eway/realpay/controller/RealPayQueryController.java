package com.maan.eway.realpay.controller;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.maan.eway.realpay.dto.UnifiedClientCreationResponseDTO;
import com.maan.eway.realpay.service.RealPayQueryService;

import jakarta.validation.constraints.NotBlank;

@RestController
@RequestMapping("/api/realpay")
@Validated
public class RealPayQueryController {
    
    private static final Logger logger = LoggerFactory.getLogger(RealPayQueryController.class);
    
    @Autowired
    private RealPayQueryService realPayQueryService;
    
    @GetMapping("/quoteNo/{quoteNo}")
    public ResponseEntity<UnifiedClientCreationResponseDTO> getClientDetailsByQuoteNo(
            @PathVariable @NotBlank(message = "Quote number is required") String quoteNo) {
        
        logger.info("Fetching client details for quote: {}", quoteNo);
        
        try {
            UnifiedClientCreationResponseDTO response = realPayQueryService.getClientDetailsByQuoteNo(quoteNo);
            
            if ("NOT_FOUND".equals(response.getStatus().getClientStatus()) &&
                "NOT_FOUND".equals(response.getStatus().getContractStatus()) &&
                "NOT_FOUND".equals(response.getStatus().getInstallmentsStatus())) {
                logger.warn("No data found for quote: {}", quoteNo);
                
                return ResponseEntity.ok(response);            }
            
            logger.info("Successfully retrieved data for quote: {}", quoteNo);
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            logger.error("Error fetching client details for quote: {}", quoteNo, e);
            return createErrorResponse("Error retrieving data: " + e.getMessage());
        }
    }
    
    @GetMapping("/quoteNo")
    public ResponseEntity<UnifiedClientCreationResponseDTO> getClientDetailsByQuoteNoParam(
            @RequestParam @NotBlank(message = "Quote number is required") String quoteNo) {
        return getClientDetailsByQuoteNo(quoteNo);
    }
    
    private ResponseEntity<UnifiedClientCreationResponseDTO> createErrorResponse(String errorMessage) {
        UnifiedClientCreationResponseDTO errorResponse = new UnifiedClientCreationResponseDTO();
        UnifiedClientCreationResponseDTO.ClientCreationStatus status = 
            new UnifiedClientCreationResponseDTO.ClientCreationStatus();
        
        status.setClientStatus("ERROR");
        status.setContractStatus("ERROR");
        status.setInstallmentsStatus("ERROR");
        
        errorResponse.setStatus(status);
        errorResponse.setMessage(errorMessage);
        errorResponse.setTimestamp(LocalDateTime.now());
        
        return ResponseEntity.internalServerError().body(errorResponse);
    }
}