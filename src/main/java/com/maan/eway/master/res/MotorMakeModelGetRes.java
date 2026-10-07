package com.maan.eway.master.res;



import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MotorMakeModelGetRes {

	@JsonProperty("MakeId")
	private String makeId;
	
	@JsonProperty("ModelId")
	private String modelId;
	
	@JsonProperty("BodyId")
	private String bodyId;
	

	@JsonProperty("BranchCode")
	private String branchCode;

	@JsonProperty("VehicleModelCode")
	private String vehicleModelCode;
	

	@JsonProperty("Status")
	private String status;

	@JsonProperty("MakeNameEn")
	private String makeNameEn;
	
	@JsonProperty("ModelNameEn")
	private String modelNameEn;
	
	@JsonProperty("BodyNameEn")
	private String bodyNameEn;

	@JsonProperty("VehClass")
	private String vehClass;
	
	@JsonProperty("VehClassEn")
	private String vehClassEn;
	
	@JsonProperty("VehManfCountry")
	private String vehManfCountry;

	@JsonProperty("VehManfCountryEn")
	private String vehManfCountryEn;

	@JsonProperty("VehManfRegion")
	private String vehManfRegion;

	@JsonProperty("VehManfRegionEn")
	private String vehManfRegionEn;

	
	@JsonProperty("VehCc")
	private String vehCc;
	
	@JsonProperty("VehWeight")
	private String vehWeight;
	

	@JsonProperty("VehFueltype")
	private String vehFueltype;
	
	
	@JsonProperty("CoreMakeId")
	private String coreMakeId;
	
	@JsonProperty("CoreModelId")
	private String coreModelId;
	

	@JsonProperty("CoreBodyId")
	private String coreBodyId;
	
	@JsonProperty("CoreRefNo")
	private String coreRefNo;
	
	@JsonProperty("VehicleTypeAr")
	private String vehicleTypeAr;
	
	
	@JsonProperty("OtherMakeId1")
	private String otherMakeId1;
	
	@JsonProperty("OtherModelId1")
	private String otherModelId1;
	
	@JsonProperty("OtherBodyId1")
	private String otherBodyId1;
	
	@JsonProperty("OtherMakeId2")
	private String otherMakeId2;
	
	@JsonProperty("OtherModelId2")
	private String otherModelId2;
	
	@JsonProperty("OtherBodyId2")
	private String otherBodyId2;
	
	@JsonProperty("RefNo")
	private String refNo;
	
	@JsonProperty("BatchId")
	private String BatchId;
	
	@JsonProperty("EntryMode")
	private String entryMode;
	
	@JsonProperty("UploadedBy")
	private String uploadedBy;
	
	@JsonProperty("PrimaCode")
	private String primaCode;
	
	@JsonProperty("ModelIdOld")
	private String modelIdOld;
	
	@JsonProperty("CoreAppCode")
	private String coreAppCode;
	
	
	@JsonProperty("RegulatoryCode")
	private String regulatoryCode;
	
	@JsonProperty("TplRate")
	private String tplRate;
	

	@JsonProperty("BaseRate")
	private String baseRate;
	

	@JsonProperty("NetRate")
	private String netRate;
	
	@JsonFormat(pattern="dd/MM/YYYY")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
	
	@JsonFormat(pattern="dd/MM/YYYY")
	@JsonProperty("EffectiveDateEnd")
	private Date effectiveDateEnd;
	
	@JsonProperty("Remarks")
	private String remarks;
	

	@JsonProperty("ObsoleteFlag")
	private String obsoleteFlag;
	

	@JsonProperty("RopBodyId")
	private String ropBodyid;

	@JsonFormat(pattern="dd/MM/YYYY")
	@JsonProperty("EntryDate")
	private Date entryDate;
	
	@JsonProperty("CodeDescLocal")
	private String codeDescLocal;
		
}
