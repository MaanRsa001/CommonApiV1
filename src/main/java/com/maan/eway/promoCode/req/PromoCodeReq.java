package com.maan.eway.promoCode.req;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PromoCodeReq {

    @JsonProperty("companyId")
    private Integer companyId;

    @JsonProperty("promoCode")
    private String promoCode;

    @JsonProperty("promoDesc")
    private String promoDesc;

    @JsonProperty("branchId")
    private Integer branchId;

    @JsonProperty("status")
    private String status;

    @JsonProperty("discountType")
    private String discountType;

    @JsonProperty("coreAppCode")
    private String coreAppCode;

    @JsonProperty("createdBy")
    private String createdBy;

    @JsonProperty("remarks")
    private String remarks;

    @JsonProperty("effectiveStartDate")
    private String effectiveStartDate;

    @JsonProperty("effectiveEndDate")
    private String effectiveEndDate;

    @JsonProperty("promocodedetails")
    private List<PromoCodeDetailReq> promocodedetails;
}
