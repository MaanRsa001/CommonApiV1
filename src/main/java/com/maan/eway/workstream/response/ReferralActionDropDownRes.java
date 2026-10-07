/**
 * @author : Ashok Kumar S 
 * @since  : 28-12-2024
 */
package com.maan.eway.workstream.response;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@EqualsAndHashCode
public class ReferralActionDropDownRes {
	
	@JsonProperty("ItemCode")
	private String itemCode;
	
	@JsonProperty("ItemValue")
	private String itemValue;
	
}
