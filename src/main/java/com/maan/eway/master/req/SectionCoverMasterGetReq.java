package com.maan.eway.master.req;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class SectionCoverMasterGetReq {
	
	@JsonProperty("CoverId")
    private String     coverId     ;
	
	@JsonProperty("SectionId")
    private String     sectionId     ;
	
	@JsonProperty("InsuranceId")
    private String     companyId     ;

	@JsonProperty("ProductId")
    private String     productId     ;
	
	@JsonFormat(pattern ="dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
}
