package com.maan.eway.promoCode.req;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PromoCodeAgentMappingListReq {

    @JsonProperty("productId")
    private Integer productId;

    @JsonProperty("companyId")
    private Integer companyId;

    @JsonProperty("sectionId")
    private Integer sectionId;
    
    @JsonProperty("promoCode")
    private String promoCode;
    
    @JsonProperty("type")
    private String type;
    
    @JsonProperty("discType")
    private String discType;
    
    @JsonProperty("businessType")
    private String businessType;
    
    @JsonProperty("channelType")
    private String channelType;

}