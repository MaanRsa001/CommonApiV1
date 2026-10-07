package com.maan.eway.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class MotorCoverNoteRefReqAck {
	@JsonProperty("AcknowledgementId")
	public Double AcknowledgementId;
	@JsonProperty("RequestId")
	public String RequestId;
	@JsonProperty("AcknowledgementStatusCode")
	public String AcknowledgementStatusCode;
	@JsonProperty("AcknowledgementStatusDesc")
	public String AcknowledgementStatusDesc;
}
