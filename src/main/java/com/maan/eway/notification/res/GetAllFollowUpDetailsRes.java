package com.maan.eway.notification.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;
@Data
public class GetAllFollowUpDetailsRes {

	

	private static final long serialVersionUID = 1L;

	@JsonProperty("Concluded")
	private List<FollowUpDetailsRes> Concluded;
	
	@JsonProperty("NotConcluded")
	private List<FollowUpDetailsRes> NotConcluded;    
}
