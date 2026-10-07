package com.maan.eway.overalldiscount;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class MsPolicySaveReq {
	
	@JsonProperty("RequestReferenceNo")
	 private String     requestReferenceNo ;
	 
	 @JsonProperty("PdRefno")
    private Long       pdRefno ;

	 @JsonProperty("CdRefno")
    private Long       cdRefno ;

   @JsonProperty("NoOfVehicles")
   private Integer    noOfVehicles ;

   @JsonProperty("GroupCount")
   private Integer groupCount;
   
   
   @JsonProperty("Currency")
   private String    currency ;

   @JsonProperty("ExchangeRate")
   private BigDecimal     exchangeRate ;
   
   @JsonProperty("EndtTypeId")
   private Integer    endtTypeId ;
  
	 @JsonProperty("EndtCategoryId")
   private String    endtCategoryId ;
	 
   @JsonProperty("Havepromocode")
   private String     havepromocode ;

   @JsonProperty("Promocode")
   private String     promocode ;
 
   @JsonProperty("Status")
   private String status;  
   
   @JsonProperty("PeriodOfInsurance")
   private String     periodOfInsurance ;
   
   @JsonProperty("BuildingSumInsured")
   private BigDecimal     BuildingSumInsured ;
   
   @JsonProperty("FactorUse")
   private String factorUse;

}
