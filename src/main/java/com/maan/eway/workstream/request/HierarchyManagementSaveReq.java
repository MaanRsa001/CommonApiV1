/**
 * @author : Ashok Kumar S 
 * @since  : 23-12-2024
 */
package com.maan.eway.workstream.request;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@Setter
@Getter
public class HierarchyManagementSaveReq {
	
	@JsonProperty("CompanyId")
	@JsonFormat(shape = Shape.STRING)
	private Integer companyId;
	
	@JsonProperty("ProductId")
	@JsonFormat(shape = Shape.STRING)
	private Integer productId;
		
	@JsonProperty("Hierarchies")
	private List<Hierarchy> hierarchies;
	
	@JsonProperty("HierarchyYN")
	private String hierarchyYN;

}
