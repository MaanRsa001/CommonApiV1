package com.maan.eway.SectionCoverMaster;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SectionResponseDto {

    @JsonProperty("InsuranceId")
    private String insuranceId;

    @JsonProperty("ProductId")
    private String productId;

    @JsonProperty("ProductName")
    private String productName;

    @JsonProperty("SectionList")
    private List<SectionDto> sectionList;
}