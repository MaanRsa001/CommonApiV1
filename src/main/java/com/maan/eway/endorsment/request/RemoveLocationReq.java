package com.maan.eway.endorsment.request;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
@Data
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class RemoveLocationReq {

	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;

	@JsonProperty("CompanyId")
	private String companyid;
	
	@JsonProperty("LocationId")
	private String locationId;
	
	@JsonProperty("Productid")
	private String productid;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDate")
    private Date   effectiveDate ;
	
	  @JsonProperty("EndorsementDetails") 
	  private NonMotEndtReq     nonMotEndtReq ;  
}
