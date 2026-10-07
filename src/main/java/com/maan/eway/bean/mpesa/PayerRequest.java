package com.maan.eway.bean.mpesa;

import lombok.Data;

@Data
public class PayerRequest {
private String partyIdType;
	
	private String partyId;

	public String getPartyIdType() {
		return partyIdType;
	}

	public void setPartyIdType(String partyIdType) {
		this.partyIdType = partyIdType;
	}

	public String getPartyId() {
		return partyId;
	}

	public void setPartyId(String partyId) {
		this.partyId = partyId;
	}
}
