/**
 * @author : Ashok Kumar S 
 * @since  : 13-03-2025
 */
package com.maan.eway.common.res;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ExcessRes {
	
	@JsonProperty("CoverId")
	@JsonFormat(shape = Shape.STRING)
	private Integer coverId;
	
	@JsonProperty("Id")
	@JsonFormat(shape = Shape.STRING)
	private Integer id;
	
	@JsonProperty("SubId")	//sub id desc represents excess description.
	@JsonFormat(shape = Shape.STRING)
	private Integer subId;

	@JsonProperty("SubIdDesc")
	private String subIdDesc;		
	
	@JsonProperty("ExcessPercentage")
	@JsonFormat(shape = Shape.STRING)
	private Integer excessPercentage;
	
	@JsonProperty("ExcessAmount")
	@JsonFormat(shape = Shape.STRING)
	private Double excessAmount;	
		
	@JsonProperty("Currency")
	private String Currency;
	
	@JsonProperty("SectionId")
	private String sectionId;
	
	@JsonProperty("TypeId")
	private String typeId;
	
	@JsonProperty("CoverDesc")
	private String coverDesc;
	
}
