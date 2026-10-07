package com.maan.eway.claim;

import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
@Data
public class NonMotEndtRes {
	
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
    @JsonProperty("PolicyNo") 
    private String     policyNo ;



}
