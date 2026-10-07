package com.maan.eway.master.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class CoverSubCoverMasterGetAllReq {

	@JsonProperty("Limit")
    private String limit;
    
    @JsonProperty("Offset")
    private String offset;
    
    @JsonProperty("InsuranceId")
    private String companyId;
    
    @JsonProperty("ProductId")
    private String productId;
    
    @JsonProperty("SectionId")
    private String sectionId;
    
    @JsonProperty("CoverId")
    private String CoverId;
    
    @JsonFormat(pattern ="dd/MM/yyyy")
    @JsonProperty("EffectiveDateStart")
    private Date effectiveDateStart;
}
