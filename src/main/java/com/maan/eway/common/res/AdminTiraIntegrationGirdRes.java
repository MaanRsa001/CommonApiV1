package com.maan.eway.common.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AdminTiraIntegrationGirdRes {

	@JsonProperty("QuoteNo")
	private String   quoteNo;
	

	@JsonProperty("PolicyNo")
	private String  policyNo;
	
	@JsonProperty("RequestReferenceNo")
	private String   requestReferenceNo;
	
	@JsonProperty("CustomerId")
	private String   customerId;
	
	@JsonProperty("ClientName")
	private String clientName;
	
	@JsonProperty("CompanyId")
	private String   companyId;
	
	@JsonProperty("BranchCode")
	private String   branchCode;
	
	@JsonProperty("ProductId")
	private String   productId;
	
	@JsonProperty("LoginId")
	private String   loginId;
	
	@JsonProperty("ApplicationId")
	private String   applicationId;
	
	@JsonProperty("AgencyCode")
	private String   agencyCode;
	
	@JsonProperty("BrokerCode")
	private String   brokerCode;
	
//	@JsonFormat( pattern = "dd/MM/yyyy")
//	@JsonProperty("EffectiveDate")
//	private Date effectiveDate;
	
//	@JsonFormat( pattern = "dd/MM/yyyy")
	@JsonProperty("ExpiryDate")
	private String expiryDate;
	
	@JsonProperty("Status")
	private String status ;
	
//	@JsonFormat( pattern = "dd/MM/yyyy")
//	@JsonProperty("EntryDate")
//	private Date entryDate ;
	
//	@JsonFormat( pattern = "dd/MM/yyyy")
	@JsonProperty("InceptionDate")
	private String   inceptionDate;
	
	// No OF Vehicles
	@JsonProperty("NoOfVehicles")
	private String noOfVehicles ;
	
	@JsonProperty("OverallPremiumFc")
	private String   overAllPremiumFc ;

	@JsonProperty("OverallPremiumLc")
	private String   overAllPremiumLc ;

	@JsonProperty("ProductName")
    private String    productName ;
	
	@JsonProperty("UserType")
	private String userType;
	
	@JsonProperty("BdmCode")
	private String bdmCode;
	
	@JsonProperty("SourceType")
	private String sourceType;
	
	@JsonProperty("CustomerCode")
	private String customerCode;
	
	@JsonProperty("BranchName")
	private String branchName;
	
	@JsonProperty("DebitNoteNo")
	private String     debitNoteNo ;
	
	@JsonProperty("CreditNo")
	private String     creditNo ;
	
	@JsonProperty("StickerNumber")
	private String stickerNumber;
	
	@JsonProperty("TiraCoverNoteNo")
	private String tiraCoverNoteNo;

	@JsonProperty("TiraRequestId")
	private String tiraRequestId;

	@JsonProperty("TiraResponseId")
	private String tiraResponseId;

	@JsonProperty("ResponseStatusCode")
	private String responseStatusCode;

	@JsonProperty("ResponseStatusDesc")
	private String responseStatusDesc;
		

    @JsonProperty("CustomerReferenceNo")
    private String   customerReferenceNo ;
}
