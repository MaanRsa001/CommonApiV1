package com.maan.eway.viewAll.dto;

import java.io.Serializable;
import java.math.BigDecimal;

import groovy.transform.ToString;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class RiskFieldFlowDto implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer companyId;
    private String companyName;
    private Integer productId;
    private String productName;
    private Integer keyId;
    private Integer sectionId;
    private String sectionName;
    private Integer coverId;
    private String coverName;
    private String jsonKey;
    private String status;
    private BigDecimal queryId;
    private String queryCol;
    private Integer orderBy;
    private String amount;
    private String datatype;
}