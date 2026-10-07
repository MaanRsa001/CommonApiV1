package com.maan.eway.claim;

import java.util.List;

import com.maan.eway.claimintimation.dto.ClaimCoInsurancedetailsData;
import com.maan.eway.claimintimation.dto.ClaimRequest;
import com.maan.eway.common.res.ViewQuoteRes;

public interface ClaimDetailsService {

	List<PolicyDetailsResponseDto> policydetailsbyregno(PolicyDetailsReq req);

	ViewQuoteRes claimViewQuoteDetails(PolicyDetailsReq req);

	List<ClaimCoverdetailsData> getCoverList(PolicyDetailsReq req);

	PolicyDetailsResponseDto policydetails(PolicyDetailsReq req);

	List<NetRes> getsumInsured(NetReq req);

	ClaimCoInsurancedetailsData getCoinsurancedetailsist(ClaimRequest req);

	PolicyInfoDetailsDto customerDetails(PolicyDetailsReq req);

	NonMotorRes nonMotorDetails(PolicyDetailsReq req);

	List<NonMotorPolicyDetailsResponseDto> nonMotorpolicydetails(PolicyDetailsReq req);

	List<NonMotorPolicyDetailsListRes> nonMotorPolicyDetailsList(NonMotorPolicyDetailsListReq req);
	
	CaimPolicydetailsRes caimPolicydetailsRes(PolicyDetailsReq req);

}
