package com.maan.eway.req.referal;

import java.util.List;
import java.util.Map;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ReferralRequest {

	private String apiLink;
	
	private String primaryId;
	
	private String primaryKey;
	
	private String primaryTable;

	private String apiRequest;
	
	private List<Map<String,String>> mp;
	
	private String tokenl;
	
	
}
