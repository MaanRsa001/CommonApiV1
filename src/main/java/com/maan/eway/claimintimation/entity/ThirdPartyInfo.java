package com.maan.eway.claimintimation.entity;

import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "third_party_info")
@Getter
@Setter
@NoArgsConstructor
public class ThirdPartyInfo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "INTIMATION_NO")
	private String intimationNo;

	@Column(name = "REQUEST_REFERENCE_NO")
	private String requestReferenceNo;

	@Column(name = "DRIVER_NAME")
	private String driverName;

	@Column(name = "LICENSE_NO")
	private String licenseNo;

	@Column(name = "NATIONALITY")
	private String nationality;

	@Column(name = "MOBILE_NO")
	private Long mobileNo;

	@Column(name = "DRIVER_LIABILITY")
	private String driverLiability;

	@Column(name = "VEHICLE_NUMBER")
	private String vehicleNumber;

	@Column(name = "MAKE")
	private String make;

	@Column(name = "MODEL")
	private String model;

	@Column(name = "PLATE_NO")
	private Integer plateNo;

	@Column(name = "THIRD_PARTY_REFERENCE")
	private String thirdPartyReference;

	@Column(name = "THIRD_PARTY_TYPE")
	private String thirdPartyType;

	@Column(name = "ENTRY_DATE")
	private Date entryDate;
}
