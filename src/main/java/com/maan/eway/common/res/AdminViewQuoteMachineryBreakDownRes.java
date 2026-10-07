package com.maan.eway.common.res;

import java.math.BigDecimal;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AdminViewQuoteMachineryBreakDownRes {

	@JsonProperty("PowerPlantSi")
	private BigDecimal powerPlantSi;
	
	@JsonProperty("ElecMachinesSi")
	private BigDecimal elecMachinesSi;
	
	@JsonProperty("EquipmentSi")
	private BigDecimal equipmentSi;
	
	@JsonProperty("GeneralMachineSi")
	private BigDecimal generalMachineSi;
	
	@JsonProperty("ManuUnitsSi")
	private BigDecimal manuUnitsSi;
	
	@JsonProperty("BoilerPlantsSi")
	private BigDecimal boilerPlantsSi;
	
	@JsonProperty("MachineEquipSi")
	private BigDecimal machineEquipSi;
	
	@JsonProperty("MachinerySi")
	private BigDecimal machinerySi;
	
	@JsonProperty("SumInsured")
	private BigDecimal sumInsured;
	
}
