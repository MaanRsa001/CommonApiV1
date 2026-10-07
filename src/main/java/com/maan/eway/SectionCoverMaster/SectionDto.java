package com.maan.eway.SectionCoverMaster;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class SectionDto {

    @JsonProperty("SectionId")
    private String sectionId;

    @JsonProperty("SectionListName")
    private String sectionListName;

    @JsonProperty("CoverList")
    private List<CoverDto> coverList;
}