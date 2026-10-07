package com.maan.eway.common.req;

import java.util.List;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
 
@Data
public class CoverTCReq {
 
    @JsonProperty("CoverId")
    private String coverId;
 
    @JsonProperty("TermsAndConditionReq")
    private List<TermsAndConditionListReq> termsAndConditionReq;
 
    @JsonProperty("ExcessReq")
    private List<ExcessReq> excessReq;
}
