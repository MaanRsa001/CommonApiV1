package com.maan.eway.bean;

import jakarta.persistence.*;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Date;

@Entity
@Data
@Table(name = "wnt_acnt_dtl")
public class WntAcntDtl {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ad_sys_id")
    private Long adSysId;

    @Column(name = "ad_ah_sys_id")
    private Long adAhSysId;

    @Column(name = "ad_comp_code")
    private String adCompCode;

    @Column(name = "ad_divn_code")
    private String adDivnCode;

    @Column(name = "ad_dept_code")
    private String adDeptCode;

    @Column(name = "ad_lob_code")
    private String adLobCode;

    @Column(name = "ad_prod_code")
    private String adProdCode;

    @Column(name = "ad_bus_type")
    private String adBusType;

    @Column(name = "ad_uw_sys_id")
    private Integer adUwSysId;

    @Column(name = "ad_pol_idx")
    private Integer adPolIdx;

    @Column(name = "ad_pol_no")
    private String adPolNo;

    @Column(name = "ad_end_no")
    private String adEndNo;

    @Column(name = "ad_end_code")
    private String adEndCode;

    @Column(name = "ad_end_desc")
    private String adEndDesc;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ad_pol_fm_dt")
    private Date adPolFmDt;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ad_pol_to_dt")
    private Date adPolToDt;

    @Column(name = "ad_claim_no")
    private String adClaimNo;

    @Column(name = "ad_fac_plmt_no")
    private String adFacPlmtNo;

    @Column(name = "ad_tty_code")
    private String adTtyCode;

    @Column(name = "ad_cust_code")
    private String adCustCode;

    @Column(name = "ad_acnt_type")
    private String adAcntType;

    @Column(name = "ad_acnt_sub_type")
    private String adAcntSubType;

    @Column(name = "ad_tran_code")
    private String adTranCode;

    @Column(name = "ad_doc_no")
    private Integer adDocNo;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ad_doc_dt")
    private Date adDocDt;

    @Column(name = "ad_comp_entry_yn")
    private Integer adCompEntryYn;

    @Column(name = "ad_currency_code")
    private String adCurrencyCode;

    @Column(name = "ad_exchange_rate")
    private BigDecimal adExchangeRate;

    @Column(name = "ad_sr_no")
    private Integer adSrNo;

    @Column(name = "ad_drcr_flag")
    private String adDrcrFlag;

    @Column(name = "ad_amount_fc")
    private Double adAmountFc;

    @Column(name = "ad_amount_lc")
    private Double adAmountLc;

    @Column(name = "ad_narration", length = 2000)
    private String adNarration;

    @Column(name = "ad_main_ac_code")
    private String adMainAcCode;

    @Column(name = "ad_sub_ac_code")
    private String adSubAcCode;

    @Column(name = "ad_inst_no")
    private Integer adInstNo;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ad_due_dt")
    private Date adDueDt;

    @Column(name = "ad_instru_type")
    private String adInstruType;

    @Column(name = "ad_insured_code")
    private String adInsuredCode;

    @Column(name = "ad_insured_name", length = 500)
    private String adInsuredName;

    @Column(name = "ad_src_code")
    private String adSrcCode;

    @Column(name = "ad_src_name", length = 500)
    private String adSrcName;

    @Column(name = "ad_channel_code")
    private String adChannelCode;

    @Column(name = "ad_channel_desc", length = 500)
    private String adChannelDesc;

    @Column(name = "ad_brk_risk_note_no", length = 200)
    private String adBrkRiskNoteNo;

    @Column(name = "upr_process_id")
    private String uprProcessId;

    @Column(name = "ibnr_process_id")
    private String ibnrProcessId;

    @Column(name = "oslr_process_id")
    private String oslrProcessId;

    @Column(name = "tty_process_id")
    private String ttyProcessId;

    @Column(name = "xol_process_id")
    private String xolProcessId;

    @Column(name = "takaful_process_id")
    private String takafulProcessId;

    @Column(name = "ad_cr_uid")
    private String adCrUid;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ad_cr_dt")
    private Date adCrDt;

    @Column(name = "ad_upd_uid")
    private String adUpdUid;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "ad_upd_dt")
    private Date adUpdDt;

    @Column(name = "ad_fin_amount_fc")
    private Double adFinAmountFc;

    @Column(name = "ad_fin_amount_lc")
    private Double adFinAmountLc;

    @Column(name = "ad_fin_doc_no", length = 100)
    private String adFinDocNo;

    @Column(name = "ad_fin_tran_ref_no", length = 240)
    private String adFinTranRefNo;

    @Column(name = "businesstransactioncode")
    private String businessTransactionCode;

    @Column(name = "businesstransactionstatus")
    private String businessTransactionStatus;

    @Column(name = "businesstransactiontype")
    private String businessTransactionType;

    @Column(name = "choice")
    private String choice;

    @Column(name = "businessarea")
    private String businessArea;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "documentdate")
    private Date documentDate;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "duedate")
    private Date dueDate;

    @Column(name = "functionalarea")
    private String functionalArea;

    @Column(name = "category")
    private String category;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "checkdate")
    private Date checkDate;

    @Column(name = "checkno")
    private String checkNo;

    @Column(name = "currency")
    private String currency;

    @Column(name = "flag")
    private String flag;

    @Column(name = "gl")
    private String gl;

    @Column(name = "payeename")
    private String payeeName;

    @Column(name = "paymentmode")
    private String paymentMode;

    @Column(name = "reference", columnDefinition = "TEXT")
    private String reference;

    @Column(name = "segment1")
    private String segment1;

    @Column(name = "segment2")
    private String segment2;

    @Column(name = "segment3")
    private String segment3;

    @Column(name = "segment4")
    private String segment4;

    @Column(name = "subgl")
    private String subGl;

    @Column(name = "transactionvalue")
    private Double transactionValue;

    @Column(name = "transferreference", columnDefinition = "TEXT")
    private String transferReference;

    @Column(name = "memo", columnDefinition = "TEXT")
    private String memo;

    @Column(name = "sequenceid")
    private Integer sequenceId;

    @Column(name = "lineid")
    private Long lineId;
}