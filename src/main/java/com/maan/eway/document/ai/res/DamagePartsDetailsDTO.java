package com.maan.eway.document.ai.res;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;


@Data
public class DamagePartsDetailsDTO {
	
	@JsonProperty("damageId")
	   private Long damageId;
	@JsonProperty("uniqueId")
	    private Integer uniqueId;
	@JsonProperty("documentId")
	    private Integer documentId;
	@JsonProperty("quoteNo")
	    private String quoteNo;
	@JsonProperty("name")
	    private String name;
	@JsonProperty("materialType")
	    private String materialType;
	@JsonProperty("damageType")
	    private String damageType;
	@JsonProperty("damagePercentage")
	    private Long damagePercentage;
	@JsonProperty("recommendation")
	    private String recommendation;
	@JsonProperty("repairCostUSD")
	    private Long repairCostUSD;
	@JsonProperty("repairCostInrMIN")
	    private Long repairCostInrMIN;
	@JsonProperty("repairCostInrMAX")
	    private Long repairCostInrMAX;
	@JsonProperty("remark")
	    private String remark;
	@JsonProperty("documentRef")
	    private String documentRef;
	@JsonProperty("entry_DATE")
	    private Date entry_DATE;

}
