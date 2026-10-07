package com.maan.eway.common.res;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CustomerQuoteAndPolicyDetail {
	List<CustomerDetailsResponse> quoteNos;
	List<CustomerDetailsResponse> policyNos;
	List<ClaimDetailResponse> claimNos;
}