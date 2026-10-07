package com.maan.eway.master.req;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class EmiTransactionDetailsSaveReq implements Serializable {

    private static final long serialVersionUID = 1L;
    
	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;

	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("InsuranceId")
	private String companyId;

	@JsonProperty("PremiumWithTax")
	private String premiumWithTax;

	@JsonProperty("InstallmentPeriod")
	private String installmentPeriod;
	
	@JsonProperty("PaymentDetails")
	private String paymentDetails;

	@JsonProperty("Remarks")
	private String remarks;

	@JsonProperty("Status")
	private String status;

	@JsonProperty("CreatedBy")
	private String createdBy;
	
	@JsonProperty("PolicyType")
	private String policyType;

	@JsonProperty("EmiYn")
	private String emiYn;
	
	@JsonProperty("EndtCategDesc")
    private String     endtCategDesc ;

    @JsonProperty("EndorsementRemarks")
    private String     endorsementRemarks ;

   @JsonFormat(pattern = "dd/MM/yyyy")
    @JsonProperty("EndorsementEffdate")
    private Date       endorsementEffdate ;

    @JsonProperty("EndtPrevPolicyNo")
    private String     endtPrevPolicyNo ;

    @JsonProperty("EndtPrevQuoteNo")
    private String     endtPrevQuoteNo ;
    
    @JsonProperty("EndtStatus")
    private String     endtStatus ;

    @JsonProperty("EndtTypeId")
    private String     endtTypeId ;

    @JsonProperty("EndtCount")
    private Integer    endtCount ;
    
   @JsonFormat(pattern = "dd/MM/yyyy")
    @JsonProperty("EndtDate")
    private Date       endtDate ;
    
    @JsonProperty("EndtBy")
    private String    endtBy ;

    @JsonProperty("IsChargRefund")
    private String isChargRefund;

    @JsonProperty("EndtTypeDesc")
    private String endtTypeDesc;

    @JsonProperty("EndtPremiumTax")
    private String endtPremiumTax;
    
    @JsonProperty("PolicyNo")
    private String     policyNo ;

    @JsonProperty("OriginalPolicyNo")
    private String     originalPolicyNo ;
    
    @JsonProperty("EndtPremium")
    private String       endtPremium ;
    
    @JsonProperty("EndtPremiumLc")
    private String       endtPremiumLc ;
    
    @JsonProperty("IsFinacialEndt")
    private String     isFinacialEndt ;
    
    @JsonProperty("EndtCommission")
    private String endtCommission ;
    
    @JsonProperty("InstallmentTypeId")
    private String installmentTypeId;
    
    @JsonProperty("UserOption")
	private String userOption;
	
	@JsonProperty("UserCustomizedEmiReq")
	private UserCustomizedEmiReq userCustomizedEmiReq;
}
