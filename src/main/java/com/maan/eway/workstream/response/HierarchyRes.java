/**
 * @author : Ashok Kumar S 
 * @since  : 23-12-2024
 */
package com.maan.eway.workstream.response;

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
public class HierarchyRes {
	
	@JsonProperty("HierarchyValue")
	@JsonFormat(shape = Shape.STRING)
	private Integer hierarchyValue;
	
	@JsonProperty("HierarchyLevel")
	private String hierarchyLevel;

	@JsonProperty("CanFinalize")
	private Boolean canFinalize;
	
	@JsonProperty("CanEscalate")
	private Boolean canEscalate;
	
}
