package com.maan.eway.promoCode.res;

import java.time.LocalDateTime;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Response DTO for one row from tbl_promo_code_agent_mapping.
 */
@Getter
@Setter
@NoArgsConstructor
public class PromoCodeAgentMappingRes {

    @JsonProperty("AgentMapId")
    private Long agentMapId;

    @JsonProperty("MappingId")
    private Long mappingId;

    @JsonProperty("PromoCode")
    private String promoCode;

    @JsonProperty("CompanyId")
    private Integer companyId;

    @JsonProperty("UserName")
    private String userName;

    @JsonProperty("AgencyCode")
    private String agencyCode;

    @JsonProperty("ProductId")
    private Integer productId;

    @JsonProperty("SectionId")
    private Integer sectionId;

    @JsonProperty("TypeOfBusiness")
    private String typeOfBusiness;

    @JsonProperty("OaCode")
    private String oaCode;

    @JsonProperty("UserType")
    private String userType;

    @JsonProperty("SubUserType")
    private String subUserType;

    @JsonProperty("AmendId")
    private Integer amendId;

    @JsonProperty("EffectiveStartDate")
    private LocalDateTime effectiveStartDate;

    @JsonProperty("EffectiveEndDate")
    private LocalDateTime effectiveEndDate;

    @JsonProperty("Status")
    private String status;

    @JsonProperty("UpdatedDate")
    private LocalDateTime updatedDate;

    @JsonProperty("CreatedBy")
    private String createdBy;

    @JsonProperty("EntryDate")
    private LocalDateTime entryDate;
}