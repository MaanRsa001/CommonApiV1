package com.maan.eway.document.res;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ClientDocListRes {

	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;

	@JsonProperty("UniqueId")
	private String uniqueId;
	
	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("Id")
	private String id;

	@JsonProperty("FilePathOriginal")
	private String filePathOriginal;

	@JsonProperty("DocumentId")
	private String documentId;
	
	@JsonProperty("DocumentName")
	private String documentName;
	
	@JsonProperty("ProductName")
	private String producttName;
	
	@JsonProperty("DocumentDesc")
	private String documentDesc;
	
	@JsonProperty("DocApplicableId")
	private String docApplicableId;
	
	@JsonProperty("DocApplicable")
	private String docApplicable;
	
	@JsonProperty("DocumentType")
	private String documentType;
	
	@JsonProperty("DocumentTypeDesc")
	private String documentTypeDesc;

	
    @JsonProperty("CreatedBy")
	private String createdby ;
	
	@JsonProperty("Status")
	private String status;
	
	@JsonFormat(pattern = "dd/MM/YYYY")
	@JsonProperty("UploadedTime")
	private Date uploadedTime;
	
	@JsonProperty("IdType")
	private String idType ;
	
	@JsonProperty("SectionId")
	private String sectionId;
	
	@JsonProperty("InsuranceId")
	private String insuranceId;
	
	@JsonProperty("SectionName")
	private String sectionName;
	
	@JsonProperty("CompanyName")
	private String companyName;
	
	@JsonProperty("RiskId")
	private String riskId;
	
	@JsonProperty("LocationId")
	private String locationId;
	
	@JsonProperty("LocationName")
	private String locationName;
	
	@JsonProperty("ProductType")
	private String productType;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("FileName")
	private String fileName;

	@JsonProperty("OriginalFileName")
	private String originalFileName;

	@JsonProperty("UploadedBy")
	private String uploadedBy;
	

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
    private BigDecimal endtCount ;
    @JsonProperty("EndtStatus") //EndtStatus
    private String     endtStatus ;   
    @JsonProperty("IsFinanceEndt") //IsFinanceEndt
    private String     isFinaceYn ;  
    @JsonProperty("EndtCategoryDesc") //EndtCategoryDesc
    private String     endtCategDesc ;
    @JsonProperty("EndorsementType") //EndorsementType
    private Integer    endorsementType ;

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
