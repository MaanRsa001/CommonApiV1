package com.maan.eway.integration.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class YiPolicyDetailsGetRes {
	
	private static final long serialVersionUID = 1L;
    
	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;

	@JsonProperty("ServiceId")
    private String serviceId ;

	@JsonProperty("ServiceAction")
    private String     serviceAction ;

	@JsonProperty("QuotationPolicyNo")
    private String     quotationPolicyNo ;

	@JsonProperty("IndexNo")
    private String     indexNo ;

	@JsonProperty("DivisionCode")
    private String     divisionCode ;

	@JsonProperty("Product")
    private String     product ;

	@JsonProperty("PolicyType")
	private String     policyType ;

	@JsonProperty("BusinessType")
    private String     businessType ;

	@JsonProperty("Customer")
    private String     customer ;

	@JsonProperty("SourceType")
    private String     sourceType ;

	@JsonProperty("SourceCode")
    private String     sourceCode ;

	@JsonProperty("Department")
    private String     department ;

	@JsonFormat(pattern = "dd/MM/yyyy")
    @JsonProperty("PolicyIssueDt")
    private String       policyIssueDt ;
	
	@JsonFormat(pattern = "dd/MM/yyyy")
    @JsonProperty("PeriodFrom")
    private String       periodFrom ;

	@JsonFormat(pattern = "dd/MM/yyyy")
    @JsonProperty("PeriodTo")
    private String       periodTo ;

    @JsonProperty("DurType")
    private String     durType ;

    @JsonProperty("CivilId")
    private String     civilId ;

    @JsonProperty("ContactEmailId")
    private String     contactEmailId ;

    @JsonProperty("ContactNumber")
    private String     contactNumber ;

    @JsonProperty("ContactPerName")
    private String     contactPerName ;

    @JsonProperty("CoverNoteNo")
    private String     coverNoteNo ;

    @JsonProperty("PremCurr")
    private String     premCurr ;

    @JsonProperty("QuoteReceivedDt")
    private String       quoteReceivedDt ;

    @JsonProperty("RaAllocationAt")
    private String     raAllocationAt ;

    @JsonProperty("RaApplYn")
    private String     raApplYn ;

    @JsonProperty("RenewalRecordYn")
    private String     renewalRecordYn ;

    @JsonProperty("SiCurr")
    private String     siCurr ;

    @JsonProperty("SicCode")
    private String     sicCode ;

    @JsonProperty("SicGroup")
    private String     sicGroup ;

    @JsonProperty("TerrAcceptedYn")
    private String     terrAcceptedYn ;

    @JsonProperty("Territoryjurisdiction")
    private String     territoryjurisdiction ;

    @JsonProperty("TerrorismForAllRisk")
    private String     terrorismForAllRisk ;

    @JsonProperty("CnIssueDate")
    private Date       cnIssueDate ;

    @JsonProperty("Donotrenewyn")
    private String     donotrenewyn ;

    @JsonProperty("AdviceDate")
    private String     adviceDate ;

    @JsonProperty("BookId")
    private String bookId ;

    @JsonProperty("brokerRefNo")
    private String     brokerRefNo ;

    @JsonProperty("BrokingSlip")
    private String     brokingSlip ;

    @JsonProperty("AddressLine1")
    private String     addressLine1 ;

    @JsonProperty("AddressLine2")
    private String     addressLine2 ;

    @JsonProperty("AddressLine3")
    private String     addressLine3 ;

    @JsonProperty("State")
    private String     state ;

    @JsonProperty("City")
    private String     city ;

    @JsonProperty("Country")
    private String     country ;

    @JsonProperty("CustRef")
    private String     custRef ;

    @JsonProperty("IssuedAt")
    private String     issuedAt ;

    @JsonProperty("Method")
    private String     method ;

    @JsonProperty("ModeOfPayment")
    private String     modeOfPayment ;

    @JsonProperty("FacYn")
    private String     facYn ;

    @JsonProperty("HypothecationYn")
    private String     hypothecationYn ;

    @JsonProperty("InstallmentYn")
    private String     installmentYn ;

    @JsonProperty("PolicyPeriod")
    private String policyPeriod ;

    @JsonProperty("PreInspReqYn")
    private String     preInspReqYn ;

    @JsonProperty("PremCalcType")
    private String     premCalcType ;

    @JsonProperty("InsuredFirstname")
    private String     insuredFirstname ;

    @JsonProperty("InsuredMiddlename")
    private String     insuredMiddlename ;

    @JsonProperty("InsuredLastname")
    private String     insuredLastname ;

    @JsonProperty("InsuredPhoneNumber")
    private String     insuredPhoneNumber ;

    @JsonProperty("InsuredEmailid")
    private String     insuredEmailid ;
    
    @JsonProperty("Insured")
    private String insured;

    @JsonProperty("InsuredOccupation")
    private String     insuredOccupation ;

    @JsonFormat(pattern = "dd/MM/yyyy")
    @JsonProperty("RequestTime")
    private Date       requestTime ;
    
    @JsonFormat(pattern = "dd/MM/yyyy")
    @JsonProperty("ResponseTime")
    private Date       responseTime ;

    @JsonProperty("Status")
    private String     status ;

    @JsonProperty("PWsResponseType")
    private String     pWsResponseType ;

    @JsonProperty("PWsError")
    private String     pWsError ;

    @JsonProperty("CurrentStatus")
    private String     currentStatus ;

    @JsonProperty("ClaimIntmDays")
    private String     claimIntmDays ;

    @JsonProperty("CnNoAsPolicyNoYn")
    private String     cnNoAsPolicyNoYn ;

    @JsonProperty("IntegStatus")
    private String     integStatus ;

    @JsonProperty("QuotationType")
    private String     quotationType ;

    @JsonProperty("OldPolicyNo")
    private String     oldPolicyNo ;

    @JsonFormat(pattern = "dd/MM/yyyy")
    @JsonProperty("QuotationIssueDt")
    private String       quotationIssueDt ;

    @JsonProperty("ValidityPeriod")
    private String     validityPeriod ;

    @JsonProperty("UwId")
    private String     uwId ;

    @JsonProperty("PremWarrentyYn")
    private String     premWarrentyYn ;

    @JsonProperty("excessOfLossIndicator")
    private String     excessOfLossIndicator ;

    @JsonProperty("IndividualAcNo")
    private String     individualAcNo ;

    @JsonProperty("SecCode")
    private String     secCode ;

    @JsonProperty("PolFleetYn")
    private String     polFleetYn ;

    @JsonProperty("PayMode")
    private String     payMode ;

    @JsonProperty("BankName")
    private String     bankName ;

    @JsonProperty("BranchName")
    private String     branchName ;

    @JsonFormat(pattern = "dd/MM/yyyy")
    @JsonProperty("DateOfCollection")
    private String       dateOfCollection ;

    @JsonProperty("ChequeNo")
    private String     chequeNo ;

    @JsonProperty("ChequeDate")
    private String       chequeDate ;

    @JsonProperty("CreditCardNo")
    private String     creditCardNo ;

    @JsonProperty("CardType")
    private String cardType;
    
    @JsonProperty("AuthorizationCode")
    private String     authorizationCode ;

    @JsonProperty("TransactionId")
    private String     transactionId ;

    @JsonProperty("TransactionDate")
    private String       transactionDate ;

    @JsonProperty("RequestUserId")
    private String     requestUserId ;

    @JsonProperty("NetPremium")
    private String     netPremium ;

    @JsonProperty("InsuredWilayat")
    private String     insuredWilayat ;

    @JsonProperty("InsuredVisaStatus")
    private String     insuredVisaStatus ;

    @JsonProperty("Promocode")
    private String     promocode ;
    
    @JsonProperty("CustomerCode")
    private String customerCode;
    
    @JsonProperty("IntegrationStatus")
    private String integrationStatus;

}
