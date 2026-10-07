package com.maan.eway.mtpintegration.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MtpPolicyReq {

	@NotBlank(message = "vrn is required")
	private String vrn;
	
	@NotBlank(message = "partnerIdentifier is required")
	private String partnerIdentifier;
	
}
