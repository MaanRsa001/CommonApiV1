package com.maan.eway.common.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.endorsment.request.EndorsementEmiRes;
import com.maan.eway.master.res.EmiDisplayListRes;

import lombok.Data;

@Data
public class EndorsementEmiDetailsRes {

	@JsonProperty("EndorseEmiInfos")
	private EndorsementEmiRes endtEmiRes;

	@JsonProperty("EmiStatus")
	private String emiStatus;

	@JsonProperty("PendingEmiList")
	private List<EmiDisplayListRes> emiPremiumRes;

}
