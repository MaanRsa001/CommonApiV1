package com.maan.eway.viewAll.dto;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FieldQueryTableQueryDto {

    private BigDecimal queryId;
    private String queryName;
    private String sqlQuery;
    private String productYn;
    private String pdfYn;
}