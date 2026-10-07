package com.maan.eway.bean;

import jakarta.persistence.*;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "yi_content_upload")
public class YiContentUpload {

    @Id
    @Column(name = "cont_sr_no")
    private Long contSrNo;

    @Column(name = "company_id", length = 50)
    private String companyId;

    @Column(name = "product_id", length = 50)
    private String productId;

    @Column(name = "section_id", length = 100, nullable = false)
    private String sectionId;

    @Column(name = "risk_id", nullable = false)
    private Integer riskId;

    @Column(name = "end_no_idx", length = 1000)
    private String endNoIdx;

    @Column(name = "item_description", length = 255)
    private String itemDescription;

    @Column(name = "amount_fc", length = 100)
    private String amountFc;

    @Column(name = "amount_lc", length = 100)
    private String amountLc;

    @Column(name = "quotation_policy_no", length = 50)
    private String quotationPolicyNo;

    @Column(name = "request_time")
    private LocalDateTime requestTime;

    @Column(name = "response_time")
    private LocalDateTime responseTime;

    @Column(name = "status", length = 10)
    private String status;

    @Column(name = "p_ws_response_type", length = 100)
    private String pWsResponseType;

    @Column(name = "p_ws_error", length = 1000)
    private String pWsError;

}

