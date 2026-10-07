/**
 * @author : Ashok Kumar S 
 * @since  : 28-12-2024
 */
package com.maan.eway.workstream.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class ReferralActionDropDownGetReq {
	
	@JsonProperty("CompanyId")
	private String companyId;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	//For Additional Filtering
	@JsonProperty("ProductId")
	@JsonFormat(shape = Shape.STRING)
	private Integer productId;
	
	@JsonProperty("LoginId")
	private String loginId;
			
}
