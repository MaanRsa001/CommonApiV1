package com.maan.eway.bean;

import java.io.Serializable;
import java.math.BigDecimal;
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
@Table(name = "aviation_info")
@IdClass(AviationInfoId.class)
public class AviationInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    // 🔹 Composite Primary Key
    @Id
    @Column(name = "Product_id")
    private Integer productId;

    @Id
    @Column(name = "Location_id")
    private Integer locationId;

    @Id
    @Column(name = "Section_id")
    private Integer sectionId;

    @Id
    @Column(name = "Risk_id")
    private Integer riskId;

    @Id
    @Column(name = "Request_reference_no")
    private String requestReferenceNo;

    @Column(name = "Quote_no")
    private String quoteNo;

    @Column(name = "Location_name")
    private String locationName;

    @Column(name = "Owner_Name")
    private String ownerName;

    @Column(name = "Aircraft_Name")
    private String aircraftName;

    @Column(name = "Type")
    private String type;

    @Column(name = "Registration_Number")
    private String registrationNumber;

    @Column(name = "Manufacture_Year")
    private String manufactureYear;

    @Column(name = "Make")
    private String make;

    @Column(name = "Model")
    private String model;

    @Column(name = "Usage_Desc")
    private String usageDesc;

    @Column(name = "Geographical_Limit")
    private String geographicalLimit;

    @Column(name = "Engine_Type")
    private String engineType;

    @Column(name = "No_of_Engines")
    private Integer noOfEngines;

    @Column(name = "Weight")
    private String weight;

    @Column(name = "Night_Flight")
    private String nightFlight;

    @Column(name = "Start_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date startDate;

	@Column(name = "End_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date endDate;
	
	@Column(name = "Entry_date")
	@Temporal(TemporalType.TIMESTAMP)
	private Date entryDate;
}