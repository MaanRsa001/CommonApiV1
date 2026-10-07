package com.maan.eway.bean.mpesa;

import java.util.UUID;

public class MpesaRequest {
	
	private String input_TransactionReference = UUID.randomUUID().toString().replace("-", "").substring(0, 6);
	
	private String input_CustomerMSISDN;
	
	private String input_Amount;
	
	private String input_ThirdPartyReference = UUID.randomUUID().toString().replace("-", "").substring(0, 6);
	
	private String input_ServiceProviderCode;

	public String getInput_TransactionReference() {
		return input_TransactionReference;
	}

	public void setInput_TransactionReference(String input_TransactionReference) {
		this.input_TransactionReference = input_TransactionReference;
	}

	public String getInput_CustomerMSISDN() {
		return input_CustomerMSISDN;
	}

	public void setInput_CustomerMSISDN(String input_CustomerMSISDN) {
		this.input_CustomerMSISDN = input_CustomerMSISDN;
	}

	public String getInput_Amount() {
		return input_Amount;
	}

	public void setInput_Amount(String input_Amount) {
		this.input_Amount = input_Amount;
	}

	public String getInput_ThirdPartyReference() {
		return input_ThirdPartyReference;
	}

	public void setInput_ThirdPartyReference(String input_ThirdPartyReference) {
		this.input_ThirdPartyReference = input_ThirdPartyReference;
	}

	public String getInput_ServiceProviderCode() {
		return input_ServiceProviderCode;
	}

	public void setInput_ServiceProviderCode(String input_ServiceProviderCode) {
		this.input_ServiceProviderCode = input_ServiceProviderCode;
	}

}
