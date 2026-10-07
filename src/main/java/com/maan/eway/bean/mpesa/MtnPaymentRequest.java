package com.maan.eway.bean.mpesa;

import lombok.Data;

@Data
public class MtnPaymentRequest {
private String amount;
	
	private String currency;
	
	private String externalId;
	
	private PayerRequest payer;
	
	private String payerMessage;
	
	private String payeeNote;

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getExternalId() {
		return externalId;
	}

	public void setExternalId(String externalId) {
		this.externalId = externalId;
	}

	public PayerRequest getPayer() {
		return payer;
	}

	public void setPayer(PayerRequest payer) {
		this.payer = payer;
	}

	public String getPayerMessage() {
		return payerMessage;
	}

	public void setPayerMessage(String payerMessage) {
		this.payerMessage = payerMessage;
	}

	public String getPayeeNote() {
		return payeeNote;
	}

	public void setPayeeNote(String payeeNote) {
		this.payeeNote = payeeNote;
	}

	@Override
	public String toString() {
		return "MtnPaymentRequest [amount=" + amount + ", currency=" + currency + ", externalId=" + externalId
				+ ", payer=" + payer + ", payerMessage=" + payerMessage + ", payeeNote=" + payeeNote + "]";
	}
}
