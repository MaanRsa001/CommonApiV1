package com.maan.eway.jasper.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class CreditDataSetTwo {

	@JsonProperty("CoreAppCode")
	private String coreAppCode;
	
}
