package com.maan.eway.finanaceIntegration.req;

import lombok.Data;

@Data
public class SectionReq {
    private Long minProductId;
    private Long maxProductId;
    private Long companyId;

}
