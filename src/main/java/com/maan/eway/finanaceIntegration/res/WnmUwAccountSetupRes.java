package com.maan.eway.finanaceIntegration.res;

import lombok.Data;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;

@Data
public class WnmUwAccountSetupRes {

    private Long pasSysId;

    private String acntType;
    private String acntTypeDesc;

    private String acntSubType;
    private String acntSubTypeDesc;

    private String adCustRvPvYn;
    private String adAutomatchYn;

    private String companyCodeFm;
    private String companyCodeTo;

    private String divisionCodeFm;
    private String divisionCodeTo;

    private String departmentCodeFm;
    private String departmentCodeTo;

    private String bussinessTypeFm;
    private String bussinessTypeTo;

    private String srcTypeFm;
    private String srcTypeTo;

    private String productCodeFm;
    private String productCodeTo;

    private String sectionCodeFm;
    private String sectionCodeTo;

    private String coverCodeFm;
    private String coverCodeTo;

    private String chargeCodeFm;
    private String chargeCodeTo;

    private String vatCodeFm;
    private String vatCodeTo;

    private String srcCodeFm;
    private String srcCodeTo;

    private String custCatg;
    private String custCatgDesc;

    private String instruCodeFm;
    private String instruCodeTo;

    private String mainAcntCode;
    private String subAcntCode;

    private String acntCompCode;
    private String acntDivnCode;
    private String acntDeptCode;

    private String acntNarration;

    private String replaceText1;
    private String replaceText2;
    private String replaceText3;
    private String replaceText4;
    private String replaceText5;
    private String replaceText6;
    private String replaceText7;
    private String replaceText8;
    private String replaceText9;
    private String replaceText10;

    private String drcrFlag;
    private String transactionCode;

    private Integer compEntryYn;
    private Integer postOnlineYn;

    @Temporal(TemporalType.DATE)
    private Date effectiveFm;

    @Temporal(TemporalType.DATE)
    private Date effectiveTo;

    private String createdBy;

    @Temporal(TemporalType.DATE)
    private Date createdDate;

    private String updatedBy;

    @Temporal(TemporalType.DATE)
    private Date updatedDate;

    private String activeStatusYn;
    private String companyId;
}
