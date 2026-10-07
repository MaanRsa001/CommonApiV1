package com.maan.eway.thirdparty.Mapfre.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
public class MapfreIntegrationLogId {
	
	
	private String quoteNo;

    private String requestRefNo;

}
