package com.maan.eway.mtpintegration.dto;


import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class InitiatePaymentRequest {
    @NotBlank(message = "numberPlate is required")
    private String numberPlate;

    @NotBlank(message = "msisdn is required")
    private String msisdn;

    public String getMsisdn() {
		return msisdn;
	}

	public void setMsisdn(String msisdn) {
		this.msisdn = msisdn;
	}

	public String getAssessmentType() {
		return assessmentType;
	}

	public void setAssessmentType(String assessmentType) {
		this.assessmentType = assessmentType;
	}

	public String getPartnerIdentifier() {
		return partnerIdentifier;
	}

	public void setPartnerIdentifier(String partnerIdentifier) {
		this.partnerIdentifier = partnerIdentifier;
	}

	@NotBlank(message = "assessmentType is required")
    @Pattern(regexp = "M|E", message = "assessmentType allowed values: M or E")
    private String assessmentType;

    @NotBlank(message = "partnerIdentifier is required")
    private String partnerIdentifier;
    
    public String getNumberPlate() {
        return numberPlate;
    }

    public void setNumberPlate(String numberPlate) {
        this.numberPlate = numberPlate;
    }
    
}
