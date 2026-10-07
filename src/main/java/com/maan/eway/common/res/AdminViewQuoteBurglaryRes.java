package com.maan.eway.common.res;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AdminViewQuoteBurglaryRes {
	
	
	@JsonProperty("NatureOfTradeId")
	private Integer natureOfTradeId;
	
	@JsonProperty("NatureOfTradeDesc")
	private String natureOfTradeDesc;
	
	@JsonProperty("InsuranceForId")
	private String insuranceForId;

	@JsonProperty("InternalWallType")
	private Integer internalWallType;
	
	@JsonProperty("InternalWallTypeDesc")
	private String internalWallTypeDesc;
	
	
	@JsonProperty("OccupiedYear")
	private Integer occupiedYear;
	
	@JsonProperty("Occupion")
	private String occupion;
	
	@JsonProperty("OccupionDesc")
	private String occupionDesc;
	
	
	@JsonProperty("OccupiedYearDesc")
	private String occupiedYearDesc;
	
	@JsonProperty("BuildingBuildYear")
	private Integer buildingBuildYear;
	
	@JsonProperty("BuildingBuildYearDesc")
	private String buildingBuildYearDesc;
	
	@JsonProperty("RoofType")
	private String roofType;
	
	@JsonProperty("RoofTypeDesc")
	private String roofTypeDesc;
	
	@JsonProperty("CeilingType")
	private Integer ceilingType;
	
	@JsonProperty("CeilingTypeDesc")
	private String ceilingTypeDesc;
	
	@JsonProperty("WallType")
	private String wallType;
	
	@JsonProperty("Address")
	private String address;
	
	@JsonProperty("RegionCode")
	private String regionCode;
	
	@JsonProperty("DistrictCode")
	private String districtCode;
	
	@JsonProperty("WatchmanGuardHours")
	private Integer watchmanGuardHours;
	
	@JsonProperty("WallTypeDesc")
	private String wallTypeDesc;
	
	@JsonProperty("AccessibleWindows")
	private Integer accessibleWindows;
	
	@JsonProperty("WindowsMaterialDesc")
	private String windowsMaterialDesc;
	
	@JsonProperty("ShowWindow")
	private Integer showWindow;
	
	@JsonProperty("FrontDoors")
	private Integer frontDoors;
	
	@JsonProperty("BackDoors")
	private Integer backDoors;
	
	@JsonProperty("WindowsMaterialId")
	private Integer windowsMaterialId;
	
	@JsonProperty("DoorsMaterialId")
	private String doorsMaterialId;
	
	@JsonProperty("DoorsMaterialIdDesc")
	private String doorsMaterialIdDesc;
	
	@JsonProperty("NightLeftDoor")
	private Integer nightLeftDoor;
	
	@JsonProperty("NightLeftDoorDesc")
	private String nightLeftDoorDesc;
	
	@JsonProperty("BuildingOccupied")
	private Integer buildingOccupied;
	
	@JsonProperty("StockInTradeSi")
	private BigDecimal stockInTradeSi;
	
	@JsonProperty("GoodsSi")
	private  BigDecimal goodsSi;
	
	@JsonProperty("FurnitureSi")
	private BigDecimal furnitureSi;
	
	@JsonProperty("ApplianceSi")
	private BigDecimal applianceSi;
	
	@JsonProperty("CashValueablesSi")
	private BigDecimal cashValueablesSi;
	
	@JsonProperty("BurglarySi")
	private BigDecimal burglarySi;
	
	@JsonProperty("StockLossPercent")
	private Integer stockLossPercent;
	
	@JsonProperty("GoodsLossPercent")
	private Integer goodsLossPercent;
	
	@JsonProperty("FurnitureLossPercent")
	private Integer furnitureLossPercent;
	
	@JsonProperty("ApplianceLossPercent")
	private Integer applianceLossPercent;
	
	@JsonProperty("CashValueablesLossPercent")
	private Integer cashValueablesLossPercent;
}
