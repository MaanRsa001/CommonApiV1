package com.maan.eway.promoCode.req;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class PromoCodeHeaderListReq {

	@JsonProperty("companyId")
	private Integer companyId;
}