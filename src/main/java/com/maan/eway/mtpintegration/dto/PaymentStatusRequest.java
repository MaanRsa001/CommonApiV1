package com.maan.eway.mtpintegration.dto;


import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class PaymentStatusRequest {
    @NotBlank(message = "paymentRequestId is required")
    private String paymentRequestId;

    @NotBlank(message = "paymentChannel is required")
    private String paymentChannel;

	public String getPaymentRequestId() {
		return paymentRequestId;
	}

	public void setPaymentRequestId(String paymentRequestId) {
		this.paymentRequestId = paymentRequestId;
	}

	public String getPaymentChannel() {
		return paymentChannel;
	}

	public void setPaymentChannel(String paymentChannel) {
		this.paymentChannel = paymentChannel;
	}
}
