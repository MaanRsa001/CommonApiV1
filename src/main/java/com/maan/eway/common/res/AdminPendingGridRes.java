package com.maan.eway.common.res;


import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class AdminPendingGridRes {

	
	@JsonProperty("Count")
	private Long   count ;
	

    @JsonProperty("PendingGrid")
    private List<AdminPendingGridListRes>   adminPendingGridListRes ;
    
   
}
