/**
 * @author : Ashok Kumar S 
 * @since  : 13-03-2025
 */
package com.maan.eway.common.req;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Getter
@Setter
public class ExcessReq {
			
	@JsonProperty("Id")
	@JsonFormat(shape = Shape.STRING)
	private Integer id;
	
	@JsonProperty("SubId") 	//sub id desc represents excess description.
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

}
