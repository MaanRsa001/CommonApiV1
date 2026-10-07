package com.maan.eway.thirdparty.response;

import java.util.List;

public class GenericResponse {
	public List<AvailablePlan> getAvailablePlan() {
		return availablePlan;
	}
	public void setAvailablePlan(List<AvailablePlan> availablePlan) {
		this.availablePlan = availablePlan;
	}
	 
	public List<UpsellPlan> getAvailableUpsellPlans() {
		return availableUpsellPlans;
	}
	public void setAvailableUpsellPlans(List<UpsellPlan> availableUpsellPlans) {
		this.availableUpsellPlans = availableUpsellPlans;
	}

	List<AvailablePlan> availablePlan;
	List<UpsellPlan> availableUpsellPlans;
}
