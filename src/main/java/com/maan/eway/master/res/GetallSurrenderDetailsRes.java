package com.maan.eway.master.res;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.maan.eway.master.req.SurrenderReq;

import lombok.Data;

@Data
public class GetallSurrenderDetailsRes {
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;  		//doubt
	
	@JsonFormat(pattern = "dd/MM/yyyy")
	@JsonProperty("EffectiveDateEnd")
	private Date effectiveDateEnd;  
	
	@JsonProperty("PolicyTerm")
    private String     policyTerm;
	
	@JsonProperty("SurrenderList")
    private List<SurrenderReq>    surrenderList;

}
