package com.maan.eway.finanaceIntegration.res;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class WntAcntDtlRes {

    private Long adSysId;
    private Long adAhSysId;

    private String adCompCode;
    private String adDivnCode;
    private String adDeptCode;
    private String adLobCode;
    private String adProdCode;
    private String adBusType;

    private Integer adUwSysId;
    private Integer adPolIdx;
    private String adPolNo;
    private String adEndNo;
    private String adEndCode;
    private String adEndDesc;

    private Date adPolFmDt;
    private Date adPolToDt;

    private String adClaimNo;
    private String adFacPlmtNo;
    private String adTtyCode;

    private String adCustCode;
    private String adAcntType;
    private String adAcntSubType;
    private String adTranCode;

    private Integer adDocNo;
    private Date adDocDt;
    private Integer adCompEntryYn;

    private String adCurrencyCode;
    private BigDecimal adExchangeRate;
    private Integer adSrNo;

    private String adDrcrFlag;
    private Double adAmountFc;
    private Double adAmountLc;

    private String adNarration;

    private String adMainAcCode;
    private String adSubAcCode;
    private Integer adInstNo;
    private Date adDueDt;
    private String adInstruType;

    private String adInsuredCode;
    private String adInsuredName;
    private String adSrcCode;
    private String adSrcName;
    private String adChannelCode;
    private String adChannelDesc;
    private String adBrkRiskNoteNo;

    private String uprProcessId;
    private String ibnrProcessId;
    private String oslrProcessId;
    private String ttyProcessId;
    private String xolProcessId;
    private String takafulProcessId;

    private String adCrUid;
    private Date adCrDt;
    private String adUpdUid;
    private Date adUpdDt;

    private Double adFinAmountFc;
    private Double adFinAmountLc;
    private String adFinDocNo;
    private String adFinTranRefNo;

    private String businessTransactionCode;
    private String businessTransactionStatus;
    private String businessTransactionType;
    private String choice;
    private String businessArea;

    private Date documentDate;
    private Date dueDate;
    private String functionalArea;
    private String category;

    private Date checkDate;
    private String checkNo;
    private String currency;
    private String flag;
    private String gl;
    private String payeeName;
    private String paymentMode;
    private String reference;

    private String segment1;
    private String segment2;
    private String segment3;
    private String segment4;

    private String subGl;
    private Double transactionValue;
    private String transferReference;
    private String memo;

    private Integer sequenceId;
    private Long lineId;
}