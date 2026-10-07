package com.maan.eway.common.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AdminViewQuoteCommonRes {
	
	@JsonProperty("RequestReferenceNo")
	private String requestReferenceNo;
	
	@JsonProperty("QuoteNo")
	private String quoteNo;
	
	@JsonProperty("ContentRisk")
	private AdminViewQuoteContentRes contentRisk;
	
	@JsonProperty("AllRisk")
	private AdminViewQuoteAllRiskRes allRisk;
	
	@JsonProperty("FirePerilsRisk")
	private AdminViewQuoteFirePerilsRes firePerilsRisk;
	
	@JsonProperty("MachineryBreakDownRisk")
	private AdminViewQuoteMachineryBreakDownRes machineryBreakDownRisk;
	
	@JsonProperty("FidelityRisk")
	private AdminViewQuoteFidelityRes fidelityRisk;
	
	@JsonProperty("MoneyRisk")
	private AdminViewQuoteMoneyRes moneyRisk;
	
	@JsonProperty("BurglaryRisk")
	private AdminViewQuoteBurglaryRes burglaryRisk;
	
	@JsonProperty("BusinessRisk")
	private AdminViewQuoteBusinessRiskRes businessRisk;
	
	@JsonProperty("PersonalAccident")
	private AdminViewQuotePersonalAccidentRes personalAccident;
	
	@JsonProperty("PersonalLiability")
	private AdminViewQuotePersonalAccidentRes personalLiability;
	
	@JsonProperty("EmpLiability")
	private AdminViewQuoteEmpLiabilityRes empLiability;
	
	@JsonProperty("ElecEquipRisk")
	private AdminViewQuoteElecEquipmentRes elecEquipRisk;
	
	@JsonProperty("PlateGlassRisk")
	private AdminViewQuotePlateGlassRes plateGlassRisk;

	@JsonProperty("PublicLiabilityRisk")
	private AdminViewQuotePubLiabilityRes publicLiabilityRisk;
	
	@JsonProperty("BusinessInterruptionRisk")
	private AdminViewQuoteBusinessInterruptionRes businessInterruptionRisk;
	
	@JsonProperty("GoodsInTransitRisk")
	private AdminViewQuoteGoodsInTransitRes GoodsInTransitRisk;
	
	
}
