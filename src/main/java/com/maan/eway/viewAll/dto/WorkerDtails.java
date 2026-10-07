package com.maan.eway.viewAll.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class WorkerDtails {
	@JsonProperty("IdOrVisa")
	private String idOrVisa;
	@JsonProperty("EmployeeName")
	private String employeeName;
	@JsonProperty("Nationality")
	private String nationality;
	@JsonProperty("PassportNo")
    private String passportNo;
	@JsonProperty("PassportExpiryDate")
    private String passportExpiryDate;
	@JsonProperty("VisaNo")
    private String visaNo;
	@JsonProperty("VisaExpiryDate")
    private String visaExpiryDate;
	@JsonProperty("Occupation")
	 private String occupation;
	@JsonProperty("TypeOfContractor")
	private String typeOfContractor;
	@JsonProperty("OccupationDesc")
	 private String occupationDesc;
}
