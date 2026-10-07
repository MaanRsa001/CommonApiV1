package com.maan.eway.integration.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetAllPolicy {
private static final long serialVersionUID = 1L;

@JsonFormat(pattern = "dd/MM/yyyy")
@JsonProperty("StartDate")
private Date startDate;

@JsonFormat(pattern = "dd/MM/yyyy")
@JsonProperty("EndDate")
private Date endDate;

@JsonProperty("InsuranceId")
private String companyId;
@JsonProperty("ProductId")
private String productId;

@JsonProperty("Limit")
private String limit;
@JsonProperty("Offset")
private String offset;
}
