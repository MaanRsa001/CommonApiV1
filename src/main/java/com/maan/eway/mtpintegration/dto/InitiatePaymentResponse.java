package com.maan.eway.mtpintegration.dto;


import lombok.Data;

@Data
public class InitiatePaymentResponse {
    private String paymentChannel;
    private Long paymentRequestId;
    private Integer returnCode;
    private String returnMessage;
	public String getPaymentChannel() {
		return paymentChannel;
	}
	public void setPaymentChannel(String paymentChannel) {
		this.paymentChannel = paymentChannel;
	}
	public Long getPaymentRequestId() {
		return paymentRequestId;
	}
	public void setPaymentRequestId(Long paymentRequestId) {
		this.paymentRequestId = paymentRequestId;
	}
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
