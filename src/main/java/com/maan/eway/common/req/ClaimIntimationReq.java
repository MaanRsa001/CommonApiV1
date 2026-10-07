package com.maan.eway.common.req;

import java.time.LocalDate;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ClaimIntimationReq {
	   @JsonProperty("ClaimReferenceNo")
	   @JsonFormat(shape = Shape.STRING)
	   private Integer claimReferenceNo;
	 
	    @JsonProperty("ProductId")
	    private String productId;

	    @JsonProperty("CompanyId")
	    private String companyId;
	    
	    @JsonProperty("PolicyNumber")
	    private String policyNumber; // To self-populate

	    @JsonProperty("VehicleRegistrationNumber")
	    private String vehicleRegistrationNumber;

	    @JsonProperty("DateOfLoss")
	    @JsonFormat(pattern = "dd/MM/yyyy")
	    private LocalDate dateOfLoss;

	    @JsonProperty("PlaceOfLoss")
	    private String placeOfLoss;

	    @JsonProperty("DateOfNotification")
	    @JsonFormat(pattern = "dd/MM/yyyy")
	    private LocalDate dateOfNotification; // Also called Intimation date

	    @JsonProperty("NatureOfDamage")
	    private String natureOfDamage;

	    @JsonProperty("ReserveAmount")
	    @JsonFormat(shape = Shape.STRING)
	    private Double reserveAmount;

	    @JsonProperty("CurrentLocationOfVehicle")
	    private String currentLocationOfVehicle;
	    
	    @JsonProperty("Status")
	    private String status;

	
	    @JsonProperty("CreatedBy")
	    private String createdBy;

}
