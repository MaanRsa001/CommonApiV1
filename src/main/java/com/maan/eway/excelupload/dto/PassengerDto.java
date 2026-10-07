package com.maan.eway.excelupload.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PassengerDto {
	@JsonProperty("PassengerFirstName")
	private String passengerFirstName;

	@JsonProperty("PassengerLastName")
	private String passengerLastName;

	@JsonProperty("GenderId")
	private String genderId;

	@JsonProperty("RelationId")
	private String relationId;

	@JsonProperty("Dob")
	private String dob;

	@JsonProperty("Nationality")
	private String nationality;

	@JsonProperty("PassportNo")
	private String passportNo;     // resolved from Gender + Relationship desc
}
