/**
 * @author : Ashok Kumar S 
 * @since  : 23-12-2024
 */
package com.maan.eway.workstream.service;

import java.util.List;
import java.util.Optional;

import com.maan.eway.error.Error;
import com.maan.eway.workstream.request.ApproverGetReq;
import com.maan.eway.workstream.response.ApproverRes;


public interface ApproverService {	
	
	public List<Error> validateParametersOfApproverGetReq(ApproverGetReq req);
	
	public Optional<ApproverRes> getApprover(Integer companyId, Integer productId, String loginId);
	
	public boolean verifyAuthorityToFinalize(Integer companyId, Integer productId, String loginId);
	
	public boolean verifyAuthorityToEscalate(Integer companyId, Integer productId, String loginId);
	
}
