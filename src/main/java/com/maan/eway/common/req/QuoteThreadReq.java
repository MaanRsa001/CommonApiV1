package com.maan.eway.common.req;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.bean.CompanyProductMaster;
import com.maan.eway.bean.EserviceBuildingDetails;
import com.maan.eway.bean.EserviceCommonDetails;
import com.maan.eway.bean.EserviceCustomerDetails;
import com.maan.eway.bean.EserviceMotorDetails;
import com.maan.eway.bean.EserviceSectionDetails;
import com.maan.eway.bean.EserviceTravelDetails;
import com.maan.eway.bean.FactorRateRequestDetails;

import lombok.Data;

@Data
public class QuoteThreadReq {

	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo ;
	
	@JsonProperty("QuoteNo")
	private String    quoteNo ;

	@JsonProperty("EndtPrevQuoteNo")
	private String    endtPrevQuoteNo ;
	
	 @JsonProperty("CustomerId")
	 private String    customerId ;
	 
	 @JsonProperty("ProductId")
	 private String    productId ;
	 
	 @JsonProperty("SectionId")
	 private String    sectionId ;

	 @JsonProperty("InsuranceId")
	 private String    insuranceId ;

		
	@JsonProperty("VehicleId")
	private Integer vehicleId ;
	
	@JsonProperty("LocationId")
	private Integer locationId ;

	@JsonProperty("CreatedBy")
	private String createdBy;
	
	@JsonProperty("AdminLoginId")
	private String adminLoginId;
	
	@JsonProperty("GroupId")
	private Integer groupId;

	@JsonProperty("GroupCount")
	private Integer groupCount;


	@JsonProperty("PolicyStartDate")
	private Date policyStartDate;
	
	@JsonProperty("PolicyEndDate")
	private Date policyEndDate;
	
	@JsonProperty("EffectiveDate")
	private Date effetiveDate;
	
	@JsonProperty("NoOfDays")
	private String noOfDays;
	
	@JsonProperty("EndtType")
	private String endtType;
	
	@JsonProperty("EndtCount")
	private String endtCount;
	
	@JsonProperty("EndtFields")
	private String endtFields;
	
	@JsonProperty("MotorYn")
	private String motorYn;
	
	@JsonProperty("IndividualId")
	private Integer individualId ;
	
	@JsonProperty("Vehicles")
	private List<VehicleIdsReq> VehicleIdsList;
	
	private List<VehicleNeedToRemove> vehicleNeedberemove; 
	
	private String originalPolicyNo;


	@JsonProperty("CommissionModifyYn")
	private String commissionModifyYn;
	
	@JsonProperty("CommissionPercent")
	private String commissionPercent;
	
	@JsonProperty("IsFinYn")
	private String isFinYn;
	// newly added
	@JsonProperty("SumInsured")
	private BigDecimal sumInsured;
	
	@JsonProperty("WhatsappPolicy")
	private String whatsappPolicy;
	
	private List<EserviceMotorDetails> motorDetailsList;

	public List<EserviceMotorDetails> getMotorDetailsList() {
	    return motorDetailsList;
	}

	public void setMotorDetailsList(List<EserviceMotorDetails> motorDetailsList) {
	    this.motorDetailsList = motorDetailsList;
	}
	
	
    private List<EserviceCommonDetails> humanDatas ;
	
	private List<EserviceBuildingDetails> buildDatas;
	
	private EserviceTravelDetails travelDatas;
	
	private CompanyProductMaster product;
	
	private List<EserviceSectionDetails> getSectionDatas;
	
	private EserviceCustomerDetails custData;
	
	private String companyid;
	
	private List<FactorRateRequestDetails> covers;
}
