package com.maan.eway.common.req;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
 
@Data
public class SectionTCReq {
 
    @JsonProperty("SectionId")
    private String sectionId;
 
    @JsonProperty("CoverList")
    private List<CoverTCReq> coverList;
}
