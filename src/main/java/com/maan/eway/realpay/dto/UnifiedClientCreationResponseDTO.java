package com.maan.eway.realpay.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class UnifiedClientCreationResponseDTO {
	
    private ClientCreationStatus status;
    
    private ClientAccountDetailsDTO clientDetails;
    private ContractDTO contractDetails;
    private List<InstalmentDTO> installmentDetails;
    
    private String message;
    private LocalDateTime timestamp;
    private String firstInstalmentPaid;
    
    @Data
    public static class ClientCreationStatus {
    	
        private String clientStatus;
        private String contractStatus;
        private String installmentsStatus;
        
        private String clientReference;
        private String contractReference;
        private String installmentReference;  
			
    }
}
