package com.maan.eway.finanaceIntegration.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CoverReq {
    private Long minProductId;
    private Long maxProductId;
    private Long companyId;
}
