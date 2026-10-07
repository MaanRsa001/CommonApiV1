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
@NoArgsConstructor
@AllArgsConstructor
public class SearchLoading implements Serializable {
	 	@JsonProperty("LoadingId") 
	    private String loadingId;
	    @JsonProperty("LoadingDesc") 
	    private String loadingDesc;
	    @JsonProperty("LoadingRate") 
	    private String loadingRate;
	    @JsonProperty("LoadingAmount") 
	    private BigDecimal loadingAmount;
	  
}
