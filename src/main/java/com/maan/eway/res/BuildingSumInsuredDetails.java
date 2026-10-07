package com.maan.eway.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BuildingSumInsuredDetails {

	
	@JsonProperty("RiskId")
	private String riskId;
	
	@JsonProperty("BuildingSuminsured")
    private String     buildingSuminsured ;
	
	@JsonProperty("AllriskSumInsured")
    private String     allriskSuminsured ;
	
	@JsonProperty("SumInsured")
    private String     sumInsured ;
	
	@JsonProperty("EmpLiabilitySi")
    private String     empLiabilitySi ;
	
	@JsonProperty("FidEmpSi")
    private String     fidEmpSi;
	
	
	
	@JsonProperty("WaterTankSi")
    private String    waterTankSi;

	@JsonProperty("ArchitectsSi")
    private String    architectsSi;

	@JsonProperty("LossOfRentSi")
    private String    lossOfRentSi;


	@JsonProperty("TypeOfProperty")
    private String    typeOfProperty;
	
	@JsonProperty("JewellerySi")
    private String    jewellerySi;

	@JsonProperty("PaitingsSi")
    private String    paitingsSi;

	@JsonProperty("CarpetsSi")
    private String    carpetsSi;
	
	
//	@JsonProperty("OccupationDetails")
//    private List<OccupationReqClass>     occupationDetails ;
	
	
	@JsonProperty("LiabilitySi")
    private String     liabilitySi;
	
	@JsonProperty("OccupationType")
    private String    occupationType;
	
	@JsonProperty("OccupationTypeDesc")
    private String    occupationTypeDesc;

	@JsonProperty("LiabilityOccupationId") 
    private String     liabilityOccupationId ;
   
   @JsonProperty("LiabilityOccupationDesc") 
    private String     liabilityOccupationDesc;
   
	@JsonProperty("PersonalAccSuminsured")
    private String    personalAccSuminsured;
	
	@JsonProperty("PersonalIntermediarySuminsured")
    private String     personalIntermediarySuminsured ;

	@JsonProperty("Count")
    private String    count;
//	@JsonProperty("PaTotaldisabilitySumInsured")
//    private String     paTotaldisabilitySumInsured ;
//	
//	@JsonProperty("PaPermanentdisablementSuminsured")
//    private String     paPermanentdisablementSuminsured ;
//	
//	@JsonProperty("PaMedicalSuminsured")
//    private String     paMedicalSuminsured ;
	@JsonProperty("ContentSuminsured")
    private String     contentSuminsured ;

	
	@JsonProperty("SectionId")
	private List<String> sectionId;
	
	
	
	@JsonProperty("ElecEquipSuminsured")
    private String     elecEquipSuminsured ;
	

	@JsonProperty("GoodsSinglecarrySuminsured")
    private String   goodsSinglecarrySuminsured ;

	@JsonProperty("GoodsTurnoverSuminsured")
    private String   goodsTurnoverSuminsured ;
	
	@JsonProperty("CurrencyId")
    private String   currencyId ;
	
	   
    @JsonProperty("StockInTradeSi")
    private String stockInTradeSi ;
   
    @JsonProperty("GoodsSi")
    private String goodsSi;
   
    @JsonProperty("FurnitureSi")
    private String furnitureSi;
   
    @JsonProperty("ApplianceSi")
    private String applianceSi;
   
    @JsonProperty("CashValueablesSi")
    private String cashValueablesSi;
    
 
    
    @JsonProperty("RevenueFromStamps")
    private String revenueFromStamps;
  
    
	@JsonProperty("MiningPlantSi")
	private String miningPlantSi;
	
	@JsonProperty("NonminingPlantSi")
	private String nonminingPlantSi;
	
	@JsonProperty("GensetsSi")
	private String gensetsSi;
	
	@JsonProperty("EquipmentSi")
	private String equipmentSi;
	
	@JsonProperty("MachinerySi")
	private String machinerySi;
	
	@JsonProperty("PlantAllriskSi")
	private String plantAllriskSi;
	
	@JsonProperty("MoneySafeLimit")
    private String moneySafeLimit    ;
	
	@JsonProperty("MoneyOutofSafe")
    private String moneyOutofSafe    ;
	
	@JsonProperty("MoneyDirectorResidence")
    private String moneyDirectorResidence    ;
	
	@JsonProperty("MoneyCollector")
    private String moneyCollector    ;
	
	@JsonProperty("MoneyAnnualEstimate")
    private String moneyAnnualEstimate    ;
	
	@JsonProperty("MoneyMajorLoss")
    private String moneyMajorLoss;
	
	@JsonProperty("FirePlantSi")
    private String firePlantSi  ;
    @JsonProperty("FireEquipSi")
    private String fireEquipSi  ;
    
}
