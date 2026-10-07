package com.maan.eway.SectionCoverMaster;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SectionRequestDto {

    @JsonProperty("InsuranceId")
    private String insuranceId;

    @JsonProperty("ProductId")
    private String productId;
}