/**
 * @author : Ashok Kumar S 
 * @since  : 28-12-2024
 */
package com.maan.eway.workstream.service;

import java.util.List;

import com.maan.eway.error.Error;
import com.maan.eway.workstream.request.ReferralActionDropDownGetReq;
import com.maan.eway.workstream.response.ReferralActionDropDownRes;

public interface ReferralActionDropDownService {
	
	public List<Error> validateReferralActionDropDownGetReq(
			ReferralActionDropDownGetReq req);
	
	public List<ReferralActionDropDownRes> getReferralActionDropDown(ReferralActionDropDownGetReq req);

}
