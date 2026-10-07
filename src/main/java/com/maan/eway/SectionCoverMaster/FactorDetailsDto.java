package com.maan.eway.SectionCoverMaster;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class FactorDetailsDto {

    @JsonProperty("FactorId")
    private String factorId;

    @JsonProperty("FactorName")
    private String factorName;

    @JsonProperty("RatingFields")
    private List<RatingFieldDto> ratingFields;
}