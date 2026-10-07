package com.maan.eway.common.req;

import java.math.BigDecimal;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.res.calc.Cover;

import lombok.Data;

@Data
public class ViewPolicyCalc {

	@JsonProperty("VehicleId")
	private String vehicleId ;
	
	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;

	@JsonProperty("MSRefNo")
	private String msrefno;
	
	@JsonProperty("CdRefNo")
	private String cdRefNo;

	@JsonProperty("VdRefNo")
	private String vdRefNo;
	
	@JsonProperty("PdRefNo")
	private String pdRefNo;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	
	 
	@JsonProperty("InsuranceId")
	private String insuranceId;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("SectionId")
	private String sectionId;
	
	@JsonProperty("BuildingSumInsured")
	private String buildingSumInsured;
	
	@JsonProperty("NoOfVehicles")
	private Integer noOfVehicles;
	
	@JsonProperty("ClaimRatio")
	private BigDecimal claimRatio;
	
//	@JsonProperty("GroupCount")
//	private String groupCount;
//	
//	@JsonProperty("Havepromocode")
//	private String     havepromocode ;
//	
//	@JsonProperty("Promocode")
//	private String     promocode ;

    
//	@JsonProperty("Status")
//    private String     status ;
    
	@JsonProperty("Currency")
    private String    currency ;

	@JsonProperty("ExchangeRate")
    private BigDecimal     exchangeRate ;
		
	
	@JsonProperty("CoverList")
	private List<Cover> coverList ;
	
//	@JsonProperty("UWReferral")
//	private List<UWReferrals> uwList;
//	
//	@JsonProperty("MasterReferral")
//	private List<MasterReferal> referals;
	
	}
