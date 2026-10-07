package com.maan.eway.reinsurance.req;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SectionCoverReq {

    @JsonProperty("CompanyId")
    private String companyId;
}
