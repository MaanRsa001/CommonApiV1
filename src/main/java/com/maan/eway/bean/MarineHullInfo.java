package com.maan.eway.bean;

import java.io.Serializable;
import java.util.Date;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
@Builder
@Entity
@DynamicInsert
@DynamicUpdate
@Table(name = "marine_hull_info")
@IdClass(MarineHullInfoId.class)
public class MarineHullInfo implements Serializable {

	private static final long serialVersionUID = 1L;

	// 🔹 Composite Primary Key
	@Id
	@Column(name = "Product_Id")
	private Integer productId;

	@Id
	@Column(name = "SECTION_ID")
	private Integer sectionId;

	@Id
	@Column(name = "Request_Reference_no")
	private String requestReferenceNo;

	@Id
	@Column(name = "COVER_ID")
	private Integer coverId;
	
	@Column(name = "Quote_No")
	private String quoteNo;

	// 🔹 Other Columns
	@Column(name = "Location_id")
	private Integer locationId;

	@Column(name = "location_name")
	private String locationName;

	@Column(name = "Start_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date startDate;

	@Column(name = "end_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date endDate;

	@Column(name = "Vesse_Id")
	private String vesseId;

	@Column(name = "Vessel_name")
	private String vesselName;

	@Column(name = "Vessel_Registration")
	private String vesselRegistration;

	@Column(name = "Manufucturing_Year")
	private String manufacturingYear;

	@Column(name = "Max_Passanger")
	private String maxPassenger;

	@Column(name = "Max_crew")
	private String maxCrew;

	@Column(name = "max")
	private String max;
	
	@Column(name = "Vessel_Usage")
	private String vesselUsage;
	
	@Column(name = "Vessel_Type")
	private String vesselType;
	
	@Column(name = "Vessel_Value")
	private String vesselValue;
	
	@Column(name = "Max_Cargo_Capacity")
	private String maxCargoCapacity;
	
	@Column(name = "Territorial_Limits")
	private String territorialLimits;
	
	@Column(name = "Dimension")
	private String dimension;
	
	@Column(name = "Engine_Type")
	private String engineType;
	
	@Column(name = "Construction_Material")
	private String constructionMaterial;
	
	@Column(name = "Passenger_Liability")
	private String passengerLiability;
	
	@Column(name = "risk_id")
	private Integer riskId;
	
}
