package com.maan.eway.common.res;

import java.io.Serializable;
import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SearchTax implements Serializable  {
	@JsonProperty("TaxId") 
    public String taxId;
    @JsonProperty("TaxRate") 
    public Double taxRate;
    @JsonProperty("TaxAmount") 
    public BigDecimal taxAmount;
    @JsonProperty("TaxDesc") 
    public String taxDesc;
    
}
