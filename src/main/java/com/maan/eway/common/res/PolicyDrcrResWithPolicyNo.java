package com.maan.eway.common.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.bean.PolicyDrcrDetail;

import lombok.Data;
@Data
public class PolicyDrcrResWithPolicyNo {
	
	
	   @JsonProperty("CreditNo")
	   private List<PolicyDrcrDetail> policyDetails;
	   
	   @JsonProperty("PolicyNo")
	   private String  policyNo;
	   
	

}
