package com.maan.eway.bean;

import java.util.Date;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@DynamicInsert
@DynamicUpdate
@Builder
@IdClass(DamagePartsDetailsId.class)
@Table(name = "damage_parts_details")
public class DamagePartsDetails {
	
	@Id
	@Column(name = "DAMAGE_ID")
	private Long damageId;
	
	@Id
	@Column(name = "UNIQUE_ID")
	private String uniqueId;
	
	@Id
	@Column(name = "DOCUMENT_ID")
	private Integer documentId;
	
	@Id
	@Column(name = "QUOTE_NO")
	private String quoteNo;
	
	@Column(name = "NAME")
	private String name;
	
	@Column(name = "MATERIAL_TYPE")
	private String materialType;
	
	@Column(name = "DAMAGE_TYPE")
	private String damageType;
	
	@Column(name = "DAMAGE_PERCENTAGE")
	private Integer damagePercentage;
	
	@Column(name = "RECOMMENDATION")
	private String recommendation;
	
	@Column(name = "REPAIR_COST_USD")
	private Double repairCostUSD;
	
	@Column(name = "REPAIR_COST_INR_MIN")
	private Long repairCostInrMIN;
	
	@Column(name = "REPAIR_COST_INR_MAX")
	private Long repairCostInrMAX;
	
	@Column(name = "ENTRY_DATE")
	private Date ENTRY_DATE;
	
	@Column(name = "REMARKS")
	private String remark;
	
	@Column(name = "DOCUMENT_REF")
	private Integer documentRef;
	
}

