package com.maan.eway.creditAmount.service;

import com.maan.eway.creditAmount.Res.BrokerCreditDetailsRes;

public interface BrokerCreditService  {
	
	 BrokerCreditDetailsRes getBrokerCreditSummary(String brokerId);
	 
	 BrokerCreditDetailsRes getBrokerCreditBalance(String cbcNo);
	
}
