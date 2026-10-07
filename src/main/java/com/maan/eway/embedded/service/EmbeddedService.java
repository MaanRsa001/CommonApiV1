package com.maan.eway.embedded.service;

import com.maan.eway.common.res.CommonRes;
import com.maan.eway.embedded.request.ClaimDetailsReq;
import com.maan.eway.embedded.request.Inalipa;
import com.maan.eway.embedded.response.InalipaDetailsRes;
import com.maan.eway.embedded.response.ResponseForInalipa;

public interface EmbeddedService {
	
	public ResponseForInalipa createPolicy(String loginId, Inalipa request);
	public String createSchedule(String loginId, String encodedPolicyNo);
	public InalipaDetailsRes getClaimDetails(ClaimDetailsReq req);
	public CommonRes sendSms(String policyNumber);
	public String getPolicySchedule(String quote);
	

}
