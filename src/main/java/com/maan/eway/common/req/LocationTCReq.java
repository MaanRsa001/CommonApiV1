package com.maan.eway.common.req;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
 
@Data
public class LocationTCReq {
 
    @JsonProperty("LocationId")
    private String locationId;   
 
    @JsonProperty("RiskId")
    private String riskId;       
 
    @JsonProperty("SectionList")
    private List<SectionTCReq> sectionList;
}
