package com.maan.eway.renewal.res;

import java.sql.Timestamp;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class RenewalTrackAgentResByProduct {

	@JsonProperty("SourceCode")
	private String sourceCode;
	@JsonProperty("SourceName")
	private String sourceName;
	@JsonProperty("SourceCount")
	private String sourceCount;
	@JsonProperty("TotalPremium")
	private String totalPremium;
	@JsonProperty("PolicyDetails")
	private List<PolicyDetail> policyDetails;

	@Data
	public static class PolicyDetail {
		@JsonProperty("CustomerCode")
		private String customerCode;
		@JsonProperty("CustomerName")
		private String customerName;
		@JsonProperty("PolicyEndDate")
		private String policyEndDate;
		@JsonProperty("TotalPremium")
		private Double totalPremium;
		@JsonProperty("Status")
		private String status;
		@JsonProperty("BranchCode")
		private String branchCode;
		@JsonProperty("BranchName")
		private String branchName;
		@JsonProperty("ProductCode")
		private String productCode;
		@JsonProperty("ProductName")
		private String productName;
		@JsonProperty("SourceCode")
		private String sourceCode;
		@JsonProperty("SourceName")
		private String sourceName;

		public PolicyDetail(String sourceCode, String sourceName, String productCode, String productName,
				String branchCode, String branchName, String customerCode, String customerName, Timestamp expiryDate,
				String status, Double premium) {
			this.sourceCode = sourceCode;
			this.sourceName = sourceName;
			this.productCode = productCode;
			this.productName = productName;
			this.branchCode = branchCode;
			this.branchName = branchName;
			this.customerName = customerName;
			this.policyEndDate = (expiryDate != null) ? expiryDate.toLocalDateTime().toLocalDate().toString() : null;
			this.status = status;
			this.totalPremium = premium;
			this.customerCode = customerCode;
		}

	}

}
