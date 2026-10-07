package com.maan.eway.finanaceIntegration.res;

import lombok.Data;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;

@Data
public class WnmAccountingEntryMappingRes {

    private Long aemSysId;

    private String acntType;
    private String acntTypeDesc;

    private String acntSubType;
    private String acntSubTypeDesc;

    private String acntTable;
    private String acntColumn;
    private String acntColumnType;
    private String acntColumnDesc;

    private String acntValueType;
    private String acntSqlStmt;

    private String acntCrUid;

    @Temporal(TemporalType.DATE)
    private Date acntCrDt;

    private String acntUpdUid;

    @Temporal(TemporalType.DATE)
    private Date acntUpdDt;

    private String companyId;
}
