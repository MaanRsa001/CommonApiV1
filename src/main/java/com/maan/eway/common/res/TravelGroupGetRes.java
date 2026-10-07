package com.maan.eway.common.res;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class TravelGroupGetRes {


	@JsonProperty("GroupId")
    private String    groupId     ;
//	@JsonProperty("GroupDesc")
//    private String    groupDesc     ;
	@JsonProperty("GroupMembers")
    private String    groupMembers    ;
	
	@JsonProperty("TravelId")
    private String    travelId;
}
