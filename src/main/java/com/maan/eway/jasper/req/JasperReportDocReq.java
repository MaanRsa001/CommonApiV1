package com.maan.eway.jasper.req;



import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class JasperReportDocReq {

	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("StartDate")
	private Date startDate;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EndDate")
	private Date endDate;
	
	@JsonProperty("InsuranceId")
	private String insuranceId;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("LoginId")
	private String loginId;
	
	@JsonProperty("ImageUrl")
	private String pvImagePath;
	
	@JsonProperty("PolicyNo")
	private String pvPolicyNo;
	
	
}
