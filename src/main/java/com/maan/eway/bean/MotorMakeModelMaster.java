package com.maan.eway.bean;

import java.io.Serializable;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@IdClass(MotorMakeModelMasterId.class)
@Table(name = "eway_motor_makemodel_master")
public class MotorMakeModelMaster implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	// --- ENTITY PRIMARY KEY

	@Id
	@Column(name = "MAKE_ID", nullable = false)
	private Integer makeId;

	@Id
	@Column(name = "MODEL_ID", nullable = false)
	private Integer modelId;
	
	@Id
	@Column(name = "BODY_ID", nullable = false)
	private Integer bodyId;
	
	@Id
	@Column(name = "COMPANY_ID", nullable = false)
	private String companyId;
	
	@Id
	@Column(name = "BRANCH_CODE", nullable = false)
	private String branchCode;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "EFFECTIVE_DATE_START", nullable = false)
	private Date effectiveDateStart;

	@Id
	@Column(name = "AMEND_ID", nullable = false)
	private Integer amendId;

	
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "EFFECTIVE_DATE_END", nullable = false)
	private Date effectiveDateEnd;

	
	
	// --- ENTITY DATA FIELDS
	@Column(name = "VEHICLEMODELCODE")
	private Integer vehiclemodelcode;

	@Column(name = "STATUS", length = 10)
	private String status;

	@Temporal(TemporalType.DATE)
	@Column(name = "ENTRY_DATE")
	private Date entryDate;

	@Column(name = "REMARKS", length = 100)
	private String remarks;
	
	@Column(name = "MAKE_NAME_EN", length = 1000)
	private String makeNameEn;
	
	@Column(name = "MODEL_NAME_EN", length = 1000)
	private String modelNameEn;
	
	@Column(name = "BODY_NAME_EN", length = 1000)
	private String bodyNameEn;

	@Column(name = "VEH_CLASS")
	private Integer vehClass;
	
	@Column(name = "VEH_CLASS_EN", length =1000)
	private String vehClassEn;
	
	
	@Column(name = "VEH_MANF_COUNTRY")
	private String vehManfCountry;
	
	@Column(name = "VEH_MANF_COUNTRY_EN", length=1000)
	private String vehManfCountryEn;
	

	@Column(name = "VEH_MANF_REGION")
	private Integer vehManfRegion;
	
	@Column(name = "VEH_MANF_REGION_EN", length=1000)
	private String vehManfRegionEn;
	
	
	
	@Column(name = "VEH_CC")
	private Integer vehCc;
	
	@Column(name = "VEH_WEIGHT")
	private Integer vehWeight;
	
	@Column(name = "VEH_FUELTYPE")
	private Integer vehFueltype;
	
	
	@Column(name = "CORE_MAKE_ID", length = 100)
	private String coreMakeId;
	
	@Column(name = "CORE_MODEL_ID", length = 100)
	private String coreModelId;
	
	@Column(name = "CORE_BODY_ID", length = 100)
	private String coreBOdyId;
	
	@Column(name = "CORE_REF_NO", length = 100)
	private String coreRefNo;
	
	@Column(name = "VEHICLE_TYPE_AR", length = 100)
	private String vehicleTypeAr;
	
	@Column(name = "MAKE_NAME_AR", length = 1000)
	private String makeNameAr;
	
	@Column(name = "MODEL_NAME_AR", length = 1000)
	private String modelNameAr;
	
	@Column(name = "OTHR_MAKE_ID_1", length = 1000)
	private String othrMakeId1;
	
	@Column(name = "OTHR_MODEL_ID_1", length = 1000)
	private String othrModelId1;
	
	@Column(name = "OTHR_BODY_ID_1", length = 1000)
	private String othrBodyId1;
	

	@Column(name = "OTHR_MAKE_ID_2", length = 1000)
	private String othrMakeId2;
	
	@Column(name = "OTHR_MODEL_ID_2", length = 1000)
	private String othrModelId2;
	
	@Column(name = "OTHR_BODY_ID_2", length = 1000)
	private String othrBodyId2;

	@Column(name="REF_NO")
	private Integer refNo;
	

	@Column(name="BATCH_ID")
	private Integer batchId;

	@Column(name = "ENTRY_MODE", length = 100)
	private String entryMode;

	@Column(name = "UPLOADED_BY", length = 100)
	private String uploadedBy;

	@Column(name="PREMIA_CODE")
	private Integer premiaCode;

	@Column(name="MODEL_ID_OLD")
	private Integer modelIdOld;

	@Column(name = "CORE_APP_CODE", length = 100)
	private String coreAppCode;

	@Column(name = "REGULATORY_CODE", length = 100)
	private String regulatoryCode;
	
	@Column(name="TPLRATE")
	private Integer tplrate;

	@Column(name="BASERATE")
	private Integer baserate;

	@Column(name="NETRATE")
	private Integer netrate;

	@Column(name = "OBSOLETE_FLAG", length = 100)
	private String obsoleteFlag;

	@Column(name="ROP_BODYID")
	private Integer ropBodyid;
	
	@Column(name = "CREATED_BY", length = 100)
	private String createdBy;
	
	@Column(name = "UPDATED_BY", length = 100)
	private String updatedBy;
	

	@Temporal(TemporalType.DATE)
	@Column(name = "UPDATED_DATE")
	private Date updatedDate;
	
	@Column(name = "VEHICLE_VALUE")
	private Double vehicleValue;
	
	@Column(name = "EXCESS")
	private Double excess;
	
	@Column(name = "YOUNG_DRIVER_EXCESS")
	private Double youngDriverExcess;
	
	@Column(name = "LOSS_OF_USE_DAYS")
	private Integer lossOfUseDays;
	
	@Column(name = "LOSS_OF_USE_VALUE")
	private Double lossOfUseValue;
	
	@Column(name = "MODEL_NAME_LOCAL", length = 100)
	private String modelNameLocal;
	
	@Column(name = "VEH_CLASS_LOCAL", length = 100)
	private String vehClassLocal;
	
	@Column(name = "VEH_MANF_COUNTRY_LOCAL", length = 100)
	private String vehManfCountryLocal;
	
	@Column(name = "VEH_MANF_REGION_LOCAL", length = 100)
	private String vehManfRegionLocal;
	
	@Column(name = "MAKE_NAME_LOCAL", length = 100)
	private String makeNameLocal;
	
	

}