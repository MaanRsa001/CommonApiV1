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
@Entity
@DynamicInsert
@DynamicUpdate
@Builder

@Table(name="engineering_info")
@IdClass(EngineerInfoId.class)
public class EngineerInfo implements Serializable {

	private static final long serialVersionUID = 1L;
    @Id
	@Column(name="Product_Id")
	private Integer productid;
	
    @Id
    @Column(name="Request_Reference_no")
 	private String requestReferenceNo;
    
    @Id
    @Column(name="Location_id")
	private Integer locationId;
    
    @Id
    @Column(name="SECTION_ID")
	private String sectionId;
    
	@Column(name="Annual_Open")
	private String annualOpen;

	@Column(name="Principal_owner")
	private String principalOwner;

	@Column(name="DESCRIPTION")
	private String description;


	@Column(name="Quote_No")
	private String quoteNo;

	@Column(name="Period_of_activity")
	private String periodOfActivity;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="Start_date")
	private Date startDate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="end_date")
	private Date endDate;

	@Column(name="year_of_manufacture")
	private String yearOfManufacture;

	@Column(name="engine_number")
	private String engineNumber;

	@Column(name="serial_number")
	private String serialNumber;

	@Column(name="ownership_type_id")
	private Integer ownershipTypeId;

	@Column(name="ownership_desc")
	private String ownershipDesc;

	@Column(name="basis_of_valuation_id")
	private Integer basisOfValuationId;

	@Column(name="basic_of_valuation_desc")
	private String basisOfValuationDesc;


	@Column(name="construction_type")
	private String constructionType;

	@Column(name="Location_name")
	private String locationName;

	@Column(name="Manufacture")
	private String manufacture;
	
	@Column(name = "PARAM1")
	private String param1;
	
	@Column(name = "PARAM2")
	private String param2;
	
	@Column(name = "PARAM3")
	private String param3;
	
	@Column(name = "PARAM4")
	private String param4;
	
	@Column(name = "PARAM5")
	private String param5;

}
