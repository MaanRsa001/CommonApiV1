package com.maan.eway.mtpintegration.dto;


import lombok.Data;

@Data
public class CommonPartnerResponse {
    private Integer returnCode;
    private String returnMessage;
	public Integer getReturnCode() {
		return returnCode;
	}
	public void setReturnCode(Integer returnCode) {
		this.returnCode = returnCode;
	}
	public String getReturnMessage() {
		return returnMessage;
	}
	public void setReturnMessage(String returnMessage) {
		this.returnMessage = returnMessage;
	}
	
}
