package com.maan.eway.common.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PolicyTypeDuplicateCheckReq {

	private String productId;
	private String policyType;
}
