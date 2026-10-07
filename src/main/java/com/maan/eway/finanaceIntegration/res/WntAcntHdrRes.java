package com.maan.eway.finanaceIntegration.res;

import lombok.Data;
import java.util.Date;

@Data
public class WntAcntHdrRes {

    private Long ahSysId;
    private Long ahUwSysId;
    private Integer ahPolIdx;
    private String ahPolNo;
    private String ahEndNo;
    private String ahClmNo;
    private String ahFacPlaceNo;
    private String ahTtyCode;

    private String uprProcessId;
    private String ibnrProcessId;
    private String oslrProcessId;
    private String ttyProcessId;
    private String xolProcessId;
    private String takafulProcessId;

    private String ahAcntType;
    private String ahTranCode;

    private Integer ahDocNo;
    private Date ahDocDt;
    private Integer ahNoOfRec;

    private String ahCrUid;
    private Date ahCrDt;
    private String ahUpdUid;
    private Date ahUpdDt;

    private Integer ahReqRespStatus;
    private Date ahReqSentDt;
    private String ahReqMessage;

    private Date ahResRecdDt;
    private String ahRespMessage;

    private String ahFinIntgStatus;
    private String ahFinIntgRefNo;

    private String ahMatchingReqRespStatus;
    private Date ahMatchingReqSentDt;
    private String ahMatchingReqMessage;

    private Date ahMatchingResRecdDt;
    private String ahMatchingRespMessage;
    private String ahMatchingFinIntgStatus;

    private Boolean ahMatchingYn;
}