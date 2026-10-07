/**
 * @author : Ashok Kumar S 
 * @since  : 09-01-2025
 */
package com.maan.eway.workstream.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ListItemValueRes {
	
	@JsonProperty("ItemCode")
	private String itemCode;
	
	@JsonProperty("ItemValue")
	private String itemValue;

}
