/**
 * @author : Ashok Kumar S 
 * @since  : 23-12-2024
 */
package com.maan.eway.workstream.entity;

import java.math.BigDecimal;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "login_product_master")
@IdClass(ApproverPK.class)
@NoArgsConstructor
@Setter
@Getter
public class Approver {
	
	@Id
	@Column(name = "COMPANY_ID", nullable = false)
	private Integer companyId;
	
	@Id
	@Column(name = "PRODUCT_ID", nullable = false)
	private Integer productId;
	
	@Id
	@Column(name = "LOGIN_ID", nullable = false)
	private String loginId;
	
	@Id
	@Column(name = "AMEND_ID", nullable = false)
	private Integer amendId;
	
	@Column(name = "HIERARCHY_LEVEL")
	private String hierarchyLevel;
	
	@Column(name = "HIERARCHY_VALUE")
	private Integer hierarchyValue;

	@Column(name = "CAN_Finalize")
	private Boolean canFinalize;
	
	@Column(name = "CAN_ESCALATE")
	private Boolean canEscalate;
	
	@Column(name = "SUM_INSURED_START")
	private BigDecimal sumInsuredStart;
	
	@Column(name = "SUM_INSURED_END")
	private BigDecimal sumInsuredEnd;
			
}
