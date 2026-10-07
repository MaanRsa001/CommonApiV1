package com.maan.eway.master.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class BrokerCommRes {

	@JsonProperty("BackDays")
	private String backDays;
}
