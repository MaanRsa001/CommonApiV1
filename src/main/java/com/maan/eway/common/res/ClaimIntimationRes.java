package com.maan.eway.common.res;

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
public class ClaimIntimationRes {
	 @JsonProperty("ClaimReferenceNo")
	    @JsonFormat(shape = Shape.STRING)
	    private Integer claimReferenceNo;

	    @JsonProperty("PolicyNumber")
	    private String policyNumber; // To self-populate

	    @JsonProperty("VehicleRegistrationNumber")
	    private String vehicleRegistrationNumber;

	    @JsonProperty("DateOfLoss")
	    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd")
	    private LocalDate dateOfLoss;

	    @JsonProperty("PlaceOfLoss")
	    private String placeOfLoss;

	    @JsonProperty("DateOfNotification")
	    @JsonFormat(shape = Shape.STRING, pattern = "yyyy-MM-dd")
	    private LocalDate dateOfNotification; // Also called Intimation date

	    @JsonProperty("NatureOfDamage")
	    private String natureOfDamage;

	    @JsonProperty("ReserveAmount")
	    @JsonFormat(shape = Shape.STRING)
	    private double reserveAmount;

	    @JsonProperty("CurrentLocationOfVehicle")
	    private String currentLocationOfVehicle;

	    @JsonProperty("Status")
	    private String status;

	    @JsonProperty("EffectiveStartDate")
	    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	    private LocalDate effectiveStartDate;

	    @JsonProperty("EffectiveEndDate")
	    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	    private LocalDate effectiveEndDate;

	    @JsonProperty("CreatedBy")
	    private String createdBy;
}
