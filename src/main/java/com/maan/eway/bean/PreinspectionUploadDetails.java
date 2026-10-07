package com.maan.eway.bean;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

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

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@IdClass(PreinspectionUploadDetailsId.class)
@Table(name = "WH_PREINSPECTION_UPLOAD_DETAIL")
public class PreinspectionUploadDetails implements Serializable{/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	//--- ENTITY PRIMARY KEY 
		@Id
		@Column(name="SNO")
		private Long sNo;
		
		@Column(name="QUOTE_NO")
		private String quoteNo;
		
		@Id
		@Column(name="REGISTRATION_NO")
		private String registrationNo;
		
		@Id
		@Column(name="CHASSIS_NO")
		private String chassisNo;
		
		@Id
		@Column(name="COMPANY_ID")
		private Long companyId;
		
		//--- ENTITY DATA FIELDS 
		
		@Column(name="CUSTOMER_NAME")
		private String customerName;

		
		@Column(name="MOBILE_NO")
		private Long mobileNo;
		
		@Column(name="POLICY_START_DATE")
		private Date policyStartDate;
		
		@Column(name="POLICY_END_DATE")
		private Date policyEndDate;
		
		@Column(name="PRODUCT_ID")
		private Long productId;
		
		@Column(name="PRODUCT_NAME")
		private String productName;
		
		@Column(name="SECTION_ID")
		private Long sectionId;
		
		@Column(name="SECTION_NAME")
		private String sectionName;
		
		
		@Column(name="ENTRY_DATE")
		private Date entryDate;
		
		@Column(name="COMPLETION_DATE")
		private Date completionDate;
		
		@Column(name="PREMIUM")
		private BigDecimal premium;
		
		@Column(name="STATUS")
		private String status;
		
		@Column(name="REFERENCE_NO")
		private Long referenceNo;

}
