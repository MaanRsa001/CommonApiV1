package com.maan.eway.mtpintegration.dto;



import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class ValidateVrnRequest {
    @NotBlank(message = "numberPlate is required")
    private String numberPlate;

    @NotBlank(message = "assessmentType is required")
    @Pattern(regexp = "M|E", message = "assessmentType allowed values: M or E")
    private String assessmentType;

	public String getNumberPlate() {
		return numberPlate;
	}

	public void setNumberPlate(String numberPlate) {
		this.numberPlate = numberPlate;
	}

	public String getAssessmentType() {
		return assessmentType;
	}

	public void setAssessmentType(String assessmentType) {
		this.assessmentType = assessmentType;
	}
}
