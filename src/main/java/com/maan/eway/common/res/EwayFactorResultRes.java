package com.maan.eway.common.res;

import java.util.Date;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class EwayFactorResultRes {

	@JsonProperty("RequestReferenceNo")
    private String     requestReferenceNo ;

	@JsonProperty("VehicleId")
    private Integer    vehicleId ;

	@JsonProperty("overId")
    private Integer    coverId ;

	@JsonProperty("InsuranceId")
    private String     companyId ;

	@JsonProperty("ProductId")
    private Integer    productId ;

	@JsonProperty("SectionId")
    private Integer    sectionId ;

	@JsonProperty("CdRefno")
    private String     cdRefno ;

	@JsonProperty("VdRefno")
    private String     vdRefno ;

	@JsonProperty("MsRefno")
    private String     msRefno ;

	@JsonProperty("CoverName")
    private String     coverName ;

	@JsonProperty("RiskPremiumAmt")
    private Double     riskPremiumAmt ;

	@JsonProperty("RiskPremiumRate")
    private Double     riskPremiumRate ;

	@JsonProperty("TargetLossRatio")
    private Double     targetLossRatio ;

	@JsonProperty("MinRate")
    private Double     minRate ;

	@JsonProperty("MinPremium")
    private Double     minPremium ;

	@JsonProperty("FinalPremiumAmtExclTax")
    private Double     finalPremiumAmtExclTax ;

	@JsonProperty("FinalPremiumRateExclTax")
    private Double     finalPremiumRateExclTax ;

	@JsonProperty("ProRataPremiumAmtExclTax")
    private Double     proRataPremiumAmtExclTax ;

//	@JsonProperty("Status")
//	private String     status ;

	@JsonFormat(pattern = "dd/MM/yyy")
    @JsonProperty("EntryDate")
    private Date       entryDate ;

    @JsonProperty("CreatedBy")
    private String     createdBy ;
    
    
    @JsonProperty("MinRateDiscount")
    private Double     appliedDiscountRate ;

    @JsonProperty("MinRateWithLoading")
    private Double     minRateLoad ;

    @JsonProperty("MinPremiumWithLoading")
    private Double     minPremiumLoad ;
    
    private Map<String, Double> minLoadingBreakdown;
	
    
}
