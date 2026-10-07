package com.maan.eway.res;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import jakarta.xml.bind.annotation.XmlRootElement;
import lombok.Data;
import lombok.ToString;
@Data
@ToString
@XmlRootElement
@JsonDeserialize
public class TitleType {

	@JsonProperty("Individual") 
	private  List<DropDownRes> individual;
	
	@JsonProperty("Corporate") 
	private  List<DropDownRes> corporate;
	
}
