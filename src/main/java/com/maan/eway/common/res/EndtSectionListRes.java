package com.maan.eway.common.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class EndtSectionListRes {

	@JsonProperty("OptedSections")
	private List<EndtSectionsRes> optedSections ;
	
	@JsonProperty("NonOptedSections")
	private List<EndtSectionsRes> nonoptedSections ;
	
//	@JsonProperty("EndtSections")
//	private List<EndtSectionsRes> endtSections ;
}
