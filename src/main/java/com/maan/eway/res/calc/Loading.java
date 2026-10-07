package com.maan.eway.res.calc;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Loading implements Serializable {
	@JsonProperty("LoadingId") 
	private String loadingId;
	@JsonProperty("LoadingDesc") 
	private String loadingDesc;
	@JsonProperty("LoadingRate") 
	private String loadingRate;
	@JsonProperty("LoadingAmount") 
	private BigDecimal loadingAmount;
	@JsonProperty("LoadingCalcType") 
	private String loadingCalcType;
	@JsonProperty("LoadingForId") 
	private String loadingforId;
	@JsonProperty("SubCoverId") 
	public String subCoverId;
	@JsonProperty("MaxLoadingAmount") 
	public BigDecimal maxAmount;
	@JsonProperty("FactorTypeId")
	private String factorTypeId;

	@JsonProperty("CoverAgeType")
    private String coverAgeType;
	@JsonProperty("RegulatoryCode")
	private String  regulatoryCode ;
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDate")
	private Date   effectiveDate ;

	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("PolicyEndDate")
	private Date   policyEndDate ;
	@JsonProperty("MinRate") 
	public Double minrate;
	// only for ui
	@JsonProperty("ActualRate") 
	public Double actualrate;


}
