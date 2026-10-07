package com.maan.eway.common.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ErrorGroupRes {
	
	@JsonProperty("CompanyId")
	private String companyId ;
	
	@JsonProperty("ProductId")
	private Integer productId ;

	@JsonProperty("ModuleId")
	private Integer moduleId;
	
	@JsonProperty("ModuleName")
	private String moduleName;
	
	@JsonProperty("ErrorDescList")
	private List<ErrorDescListRes> errorDescList;
	
	
	
	
}
