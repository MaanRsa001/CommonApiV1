 package com.maan.eway.SectionCoverMaster;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class CoverDto {

    @JsonProperty("CoverId")
    private String coverId;

    @JsonProperty("CoverName")
    private String coverName;

    @JsonProperty("CoverageType")
    private String coverageType;

    @JsonProperty("CalculationType")
    private String calculationType;

    @JsonProperty("CoverBasedOn")
    private String coverBasedOn;

    @JsonProperty("FactorDetails")
    private FactorDetailsDto factorDetails;
}