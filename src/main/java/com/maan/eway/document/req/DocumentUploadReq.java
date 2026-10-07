package com.maan.eway.document.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class DocumentUploadReq {

	@JsonProperty("QuoteNo")
	private String quoteNo;

	@JsonProperty("Id")
	private String id ;
	
	@JsonProperty("IdType")
	private String idType ;
	
	@JsonProperty("SectionId")
	private String sectionId;
	
	@JsonProperty("InsuranceId")
	private String insuranceId;
	
	@JsonProperty("DocumentId")
	private String documentId ;
	
	@JsonProperty("ExistingUniqueId")
	private String existingUniqueId;  
	
	
	@JsonProperty("RiskId")
	private String riskId;
	
	@JsonProperty("LocationId")
	private String locationId;
	
	@JsonProperty("LocationName")
	private String locationName;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("FileName")
	private String fileName;

	@JsonProperty("OriginalFileName")
	private String originalFileName;

	@JsonProperty("UploadedBy")
	private String uploadedBy;
	
	@JsonProperty("TermsAndCondtionYn")
	private String termsAndCondtionYn;

    @JsonProperty("EndorsementDate") //EndorsementDate
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date       endorsementDate ;
    @JsonProperty("EndorsementRemarks") // EndorsementRemarks
    private String     endorsementRemarks ;    
    @JsonProperty("EndorsementEffectiveDate") // EndorsementEffectiveDate
    @JsonFormat(pattern = "dd/MM/yyyy")
    private Date       endorsementEffdate ;
    @JsonProperty("OrginalPolicyNo") // OrginalPolicyNo
    private String     originalPolicyNo ;
    @JsonProperty("EndtPrevPolicyNo") // EndtPrevPolicyNo
    private String     endtPrevPolicyNo ;
    @JsonProperty("EndtPrevQuoteNo") // EndtPrevQuoteNo
    private String     endtPrevQuoteNo ;
    @JsonProperty("EndtCount")  // EndtCount
    private String endtCount ;
    @JsonProperty("EndtStatus") //EndtStatus
    private String     endtStatus ;   
    @JsonProperty("IsFinanceEndt") //IsFinanceEndt
    private String     isFinaceYn ;  
    @JsonProperty("EndtCategoryDesc") //EndtCategoryDesc
    private String     endtCategDesc ;
    @JsonProperty("EndorsementType") //EndorsementType
    private String    endorsementType ;

    @JsonProperty("EndorsementTypeDesc") // EndorsementTypeDesc
    private String     endorsementTypeDesc ;

	@JsonProperty("EmiYn")
	private String emiYn;

	@JsonProperty("InstallmentPeriod")
	private String installmentPeriod;

	@JsonProperty("NoOfInstallment")
	private String noOfInstallment;

	@JsonProperty("VerifiedYn")
	private String verifiedYn;

}
