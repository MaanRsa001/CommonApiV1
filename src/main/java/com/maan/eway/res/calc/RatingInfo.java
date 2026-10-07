package com.maan.eway.res.calc;

import java.io.Serializable;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class RatingInfo implements Serializable {
	 /**
	 * 
	 */
	private static final long serialVersionUID = 3301222282893059909L;
	@JsonProperty("FactortypeId") 
	    public String factortypeId;
	    @JsonProperty("FactortypeName") 
	    public String factortypeName;
	    @JsonProperty("FactorRangeYn") 
	    public String factorRangeYn;
	    @JsonProperty("RangeFromCol") 
	    public String rangeFromCol;
	    @JsonProperty("RangeToCol") 
	    public String rangeToCol;
	    @JsonProperty("DiscretCol") 
	    public String discretCol;
	    @JsonProperty("ProductId") 
	    public String productId;
	    @JsonProperty("CompanyId") 
	    public String companyId;
	    @JsonProperty("RatingFieldId") 
	    public String ratingFieldId;
	    @JsonProperty("RatingField") 
	    public String ratingField;
	    @JsonProperty("InputTableName") 
	    public String inputTableName;
	    @JsonProperty("InputColumName") 
	    public String inputColumName;
	    
	    @JsonProperty("InputColumValue") 
	    public String inputColumValue;
}
