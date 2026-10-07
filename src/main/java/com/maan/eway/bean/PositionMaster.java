package com.maan.eway.bean;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name="POSITION_MASTER")
public class PositionMaster {

	@Id
	@Column(name="APPLICATION_NO")
	private Long applicationno;
	@Column(name="CERTIFICATE_NO")
	private String certificateNo;
	@Column(name="TRAN_ID")
	private String tranId;
	@Column(name="ENDT_TYPE")
	private String endttype;
	@Column(name="DISCOUNT_PREMIUM")
	private Double discountpremium;
	@Column(name="QUOTE_NO")
	private Long quoteno;
	@Column(name="CUSTOMER_ID")
    private Long customerid;
    @Column(name="APPLICATION_ID")
    private String applicationid;
    @Column(name="LOGIN_ID")
    private String loginid;
    @Column(name="PRODUCT_ID")
    private Long productid;
    @Column(name="COMPANY_ID")
    private Long companyid;
    @Column(name="POLICY_NO")
    private String policyno;
    @Column(name="POLICY_TERM")
    private String policyterm;
    @Column(name="PREMIUM")
    private Double premium;
    @Column(name="AMEND_ID")
    private Long amendid;
    @Column(name="INTEGRATION_ERROR")
    private String integrationError;  
    @Column(name="INCEPTION_DATE")
    private Date inceptiondate;
    @Column(name="EXPIRY_DATE")
    private Date expirydate;
    @Column(name="EFFECTIVE_DATE")
    private Date effectivedate;
    @Column(name="ENTRY_DATE")
    private Date entrydate;
    @Column(name="REMARKS")
    private String remarks;
    @Column(name="STATUS")
    private String status;
    @Column(name="OPEN_COVER_NO")
    private String opencoverno;
    @Column(name="EXCESS_PREMIUM")
    private Double excesspremium;
    @Column(name="MISSIPPI_OPENCOVER_NO")
    private String missippiopencoverno;
    @Column(name="BUSINESS_TYPE")
    private Double businesstype;
    @Column(name="MODIFY_CLAUSE_ID")
	private String modifyclauseid;
	@Column(name="MODIFY_EXCLUSION_ID")
	private String modifyexclusionid;
	@Column(name="MODIFY_WARRANTY_ID")
	private String modifywarrantyid;
	@Column(name="MODIFY_EXCLAUSE_ID")
	private String modifyexclauseid;
	@Column(name="PDF_PRE_SHOW_STATUS")
	private String pdfpreshowstatus;
	@Column(name="EDIT_CLAUSES_YN")
	private String editclausesyn;
	@Column(name="FINALIZE_YN")
	private String finalizeyn;
	@Column(name="PDF_MODIFY_CLAUSE")
	private String pdfmodifyclause;
	@Column(name="PDF_MODIFY_EXTRACLAUSES")
	private String pdfmodifyextraclauses;
	@Column(name="PDF_MODIFY_WARRANTY")
	private String pdfmodifywarranty;
	@Column(name="PDF_MODIFY_EXCLUSION")
	private String pdfmodifyexclusion;
	@Column(name="SUBJECTIVITY_DESC")
	private String subjectivitydesc;
	@Column(name="EDIT_SUB_YN")
	private String editsubyn;
	@Column(name="MODE_OF_PAYMENT")
	private String modeofpayment;
	@Column(name="PRE_RECEIPT_NO")
	private String prereceiptno;
	@Column(name="REFERRAL_UPDATE")
	private Date referralupdate;
	@Column(name="REFERRAL_APP_UPDATE")
	private Date referralappupdate;
	@Column(name="ENDT_REMARKS")
	private String endtremarks;
	@Column(name="ADMIN_REMARKS")
	private String adminremarks;
	@Column(name="BROKER_REMARKS")
	private String brokerremarks;
	@Column(name="APPROVED_BY")
	private String approvedby;
	@Column(name="LAPSED_REMARKS")
	private String lapsedremarks;
	@Column(name="LAPSED_DATE")
	private Date lapseddate;
	@Column(name="LAPSED_UPDATED_BY")
	private String lapsedupdatedby;
	@Column(name="VERIFICATION_CODE")
	private Long verificationcode;
	@Column(name="INTEGRATION_STATUS")
	private String integrationstatus;
	@Column(name="CORE_INTG_STATUS")
	private String coreintgstatus;
	@Column(name="ENDT_STATUS")
	private String endtstatus;
	@Column(name="CBC_NO")
	private String cbcno;
	@Column(name="BRANCH_CODE")
	private String branchCode;
	@Column(name="INTG_TAXINVOICEDOCID")
	private String intgTaxInvoice;
	@Column(name="INTG_DRCRDOCID")
	private String integdrcr;
	@Column(name="INTG_SYSID")
	private String intgSysId;
	@Column(name="CORE_INTEG_ERROR")
	private String integError;
	@Column(name="INTEG_RESPONSE_TIME")
	private Date integResTime;
	@Column(name="COMMISSION")
	private Double commission;
	@Column(name="CANCEL_REMARKS")
	private String cancelRemarks;
	@Column(name="ORIGINAL_POLICY_NO")
	private String originalPolicyNo;
	@Column(name="PDF_BROKER_STATUS")
	private String pdfBrokerStatus;	
	@Column(name="PDF_BANKER_STATUS")
	private String pdfBankerStatus;	
	@Column(name="PDF_GENERATE_STATUS")
	private String pdfgenerateStatus;
	@Column(name="PDF_BANKER_ASSURED_STATUS")
	private String pdfbankassuredStatus;
	@Column(name="PDF_CURRENCY_STATUS")
	private String pdfcurrencyStatus;
	@Column(name="PDF_STAMP_STATUS")
	private String pdfstampStatus;
	@Column(name="CERT_CLAUSES_YN")
	private String certclausesYN;
	@Column(name="ENDT_PREM_YN")
	private String endtPremYN;
	@Column(name="ENDT_CLAUSES_YN")
	private String endtClausesYN;
	@Column(name="PDF_EXCESS_STATUS")
	private String pdfexcessStatus;
	@Column(name="BASIS_VAL")
	private String basisVal;
	@Column(name="PAYMENT_MODE")
	private String paymentMode;
	@Column(name="DEBIT_NOTE_DATE")
	private Date debitnoteDate;
	@Column(name="MISSIPPI_TRANFER_DATE")
	private Date missippitranDate;
	@Column(name="CREDIT_NOTE_DATE")
	private Date creditNoteDate;
	@Column(name="DEBIT_NOTE_NO")
	private String debitNoteNo;
	@Column(name="CREDIT_NOTE_NO")
	private String creditNoteNo;
	@Column(name="DEBIT_TO")
	private String debitTo;
	@Column(name="CREDIT_TO")
	private String creditTo;
	@Column(name="DEBIT_TO_ID")
	private String debitToId;
	@Column(name="CREDIT_TO_ID")
	private String crediToId;
	@Column(name="OPEN_COVER_INT_STATUS")
	private String opencoverintstatus;
	@Column(name="FREIGHT_STATUS")
	private String freightStatus;
	@Column(name="PRO_COMMISSION")
	private String proCommission;
	@Column(name="POLICY_EFFECTIVE_DATE")
	private Date policyEffectiveDate;
	@Column(name="PDF_COMMISSION_STATUS")
	private String pdfCommissionStatus;
	@Column(name="NOTE_TYPE")
	private String noteType;
	@Column(name="OC_MUL_CURR_ID")
	private String ocMulCurrId;
	@Column(name="DEBIT_CUST_ID")
	private String debitCustId;
	@Column(name="OC_MUL_CURR_VALUE")
	private String ocMulCurrValue;
	@Column(name="TINY_URL")
	private String tinyUrl;
	@Column(name="VALIDITY_DATE")
	private Date validityDate;
	@Column(name="ADMIN_REFERRAL_REMARKS")
	private String adminreferralremarks;
	@Column(name="PDF_MODIFY_BACKDATE")
	private String pdfModifyBackdate;
	@Column(name="PDF_BACKDATE_ID")
	private String pdfbackdateid;
	@Column(name="QUOTE_REF_NO")
	private String quoteRefNo;
	@Column(name="STAMP_DUTY_YN")
	private String stampDutyYN;
	@Column(name="COMMISSION_FC")
	private Double commissionFc;
	@Column(name="PREMIUM_FC")
	private Double premiumFc;
	@Column(name="PDF_COUNT")
	private Integer pdfCount;
	@Column(name="WHT_PERCENT")
	private Double whtPercent;
	@Column(name="IRALEVY_PERCENT")
	private Double iraLevyPercent;
	@Column(name="WHT_AMOUNT")
	private Double whtAmount;
	@Column(name="WHT_AMOUNT_FC")
	private Double whtAmountFc;
	@Column(name="IRALEVY_AMOUNT")
	private Double iraLevyAmount;
	@Column(name="IRALEVY_AMOUNT_FC")
	private Double iraLevyAmountFc;
	@Column(name="INTEG_TRANSACTION_NO")
	private String integTransactionNo;
	@Column(name="INTEG_CERTIFICATE_NO")
	private String integCertificateNo;
	
}
