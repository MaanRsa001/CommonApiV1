package com.maan.eway.master.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class CityMasterDropDownRes {
	
	@JsonProperty("Code")
	@JsonFormat(shape = Shape.STRING)
	private Integer code;
	
	@JsonProperty("CodeDesc")
	private String codeDesc;

}
