package com.maan.eway.req;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.req.calcengine.CalcEngine;

import lombok.Data;

@Data
public class CalcEngineBatch extends CalcEngine{

	 @JsonProperty("CoverIds")
	    private List<String> coverIds;

	    @JsonProperty("VdRefNos")
	    private List<String> vdRefNos;

	    @JsonProperty("CdRefNos")
	    private List<String> cdRefNos;

	    @JsonProperty("MSRefNos")
	    private List<String> msRefNos;

	    @JsonProperty("DdRefNos")
	    private List<String> ddRefNos;
}
