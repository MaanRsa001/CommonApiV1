package com.maan.eway.bean;

import jakarta.persistence.*;
import lombok.Data;

import java.util.Date;

@Entity
@Data
@Table(name = "wnt_acnt_hdr")
public class WntAcntHdr {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ah_sys_id")
    private Long ahSysId;

    @Column(name = "ah_uw_sys_id")
    private Long ahUwSysId;

    @Column(name = "ah_pol_idx")
    private Integer ahPolIdx;

    @Column(name = "ah_pol_no", length = 240)
    private String ahPolNo;

    @Column(name = "ah_end_no", length = 240)
    private String ahEndNo;

    @Column(name = "ah_clm_no", length = 240)
    private String ahClmNo;

    @Column(name = "ah_fac_place_no", length = 240)
    private String ahFacPlaceNo;

    @Column(name = "ah_tty_code", length = 100)
    private String ahTtyCode;

    @Column(name = "upr_process_id", length = 50)
    private String uprProcessId;

    @Column(name = "ibnr_process_id", length = 50)
    private String ibnrProcessId;

    @Column(name = "oslr_process_id", length = 50)
    private String oslrProcessId;

    @Column(name = "tty_process_id", length = 50)
    private String ttyProcessId;

    @Column(name = "xol_process_id", length = 50)
    private String xolProcessId;

    @Column(name = "takaful_process_id", length = 50)
    private String takafulProcessId;

    @Column(name = "ah_acnt_type", length = 50)
    private String ahAcntType;

    @Column(name = "ah_tran_code", length = 50)
    private String ahTranCode;

    @Column(name = "ah_doc_no")
    private Integer ahDocNo;

    @Temporal(TemporalType.DATE)
    @Column(name = "ah_doc_dt")
    private Date ahDocDt;

    @Column(name = "ah_no_of_rec")
    private Integer ahNoOfRec;

    @Column(name = "ah_cr_uid", length = 50)
    private String ahCrUid;

    @Temporal(TemporalType.DATE)
    @Column(name = "ah_cr_dt")
    private Date ahCrDt;

    @Column(name = "ah_upd_uid", length = 50)
    private String ahUpdUid;

    @Temporal(TemporalType.DATE)
    @Column(name = "ah_upd_dt")
    private Date ahUpdDt;

    @Column(name = "ah_req_resp_status")
    private Integer ahReqRespStatus;

    @Temporal(TemporalType.DATE)
    @Column(name = "ah_req_sent_dt")
    private Date ahReqSentDt;

    @Lob
    @Column(name = "ah_req_message")
    private String ahReqMessage;

    @Temporal(TemporalType.DATE)
    @Column(name = "ah_res_recd_dt")
    private Date ahResRecdDt;

    @Lob
    @Column(name = "ah_resp_message")
    private String ahRespMessage;

    @Column(name = "ah_fin_intg_status", length = 240)
    private String ahFinIntgStatus;

    @Column(name = "ah_fin_intg_ref_no", length = 240)
    private String ahFinIntgRefNo;

    @Column(name = "ah_matching_req_resp_status", length = 500)
    private String ahMatchingReqRespStatus;

    @Temporal(TemporalType.DATE)
    @Column(name = "ah_matching_req_sent_dt")
    private Date ahMatchingReqSentDt;

    @Column(name = "ah_matching_req_message", length = 500)
    private String ahMatchingReqMessage;

    @Temporal(TemporalType.DATE)
    @Column(name = "ah_matching_res_recd_dt")
    private Date ahMatchingResRecdDt;

    @Column(name = "ah_matching_resp_message", length = 500)
    private String ahMatchingRespMessage;

    @Column(name = "ah_matching_fin_intg_status", length = 500)
    private String ahMatchingFinIntgStatus;

    @Column(name = "ah_matching_yn")
    private Boolean ahMatchingYn;

}
