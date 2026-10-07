package com.maan.eway.admin.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class GetMotorAdminReferalPendingDetailsRes {

	@JsonProperty("Count")
	private Long   count ;
	

    @JsonProperty("MotorGridRefettalAdminRes")
    private List<MotorGridCriteriaAdminRes>   motorGridCriteriaAdminRes ;
}
