package com.maan.eway.finanaceIntegration.res;

import lombok.Data;

import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.util.Date;

@Data
public class WnmGeneralMappingRes {

    private Long gmSysId;

    private String gmMapType;
    private String gmMapTypeDesc;

    private String gmTable;
    private String gmColumn;
    private String gmColumnType;
    private String gmColumnDesc;

    private String gmValueType;
    private String gmSqlStmt;

    private String gmCrUid;

    @Temporal(TemporalType.DATE)
    private Date gmCrDt;

    private String gmUpdUid;

    @Temporal(TemporalType.DATE)
    private Date gmUpdDt;

    private String companyId;
}
