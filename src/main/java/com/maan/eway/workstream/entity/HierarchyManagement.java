/**
 * @author : Ashok Kumar S 
 * @since  : 23-12-2024
 */
package com.maan.eway.workstream.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "workflow_hierarchy_management")
@IdClass(HierarchyManagementId.class)
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
@Builder
public class HierarchyManagement {

	@Id
	@Column(name = "COMPANY_ID")
	private Integer companyId;
	
	@Id
	@Column(name = "PRODUCT_ID")
	private Integer productId;
	
	@Id
	@Column(name = "HIERARCHY_VALUE")
	private Integer hierarchyValue;
	
	@Column(name = "HIERARCHY_LEVEL")
	private String hierarchyLevel;
	
	@Column(name = "CAN_FINALIZE")
	private Boolean canFinalize;
	
	@Column(name = "CAN_ESCALATE")
	private Boolean canEscalate;
	
}
