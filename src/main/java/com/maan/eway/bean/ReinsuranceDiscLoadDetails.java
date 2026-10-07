package com.maan.eway.bean;

import java.math.BigDecimal;
import java.util.Date;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@IdClass(ReinsuranceDiscLoadDetailsId.class)
@Table(name = "reinsurance_disc_load_details")
public class ReinsuranceDiscLoadDetails {
	
	@Id
	@Column(name = "quote_no")
	private String quoteno;
	
	@Column(name = "uw_sys_id")
	private Integer uwSysId;
	
	@Column(name = "pol_idx")
	private Integer polIdx;
	
	@Id
	@Column(name = "company_id")
	private String companyId;
	
	@Id
	@Column(name = "product_id")
	private String productId;
	
	@Column(name = "product_code")
	private String productCode;
	
	@Column(name = "section_id")
	private String sectionId;
	
	@Column(name = "section_code")
	private String sectionCode;
	
	@Column(name = "risk_id")
	private String riskId;
	
	@Column(name = "cover_id")
	private String coverId;
	
	@Column(name = "cover_code")
	private String coverCode;
	
	@Id
	@Column(name = "disc_load_id")
	private String discLoadId;
	
	@Column(name = "disc_load_code")
	private String discLoadCode;
	
	@Column(name = "disc_load_name")
	private String discLoadName;
	
	@Column(name = "disc_load_type")
	private String discLoadType;
	
	@Column(name = "disc_load_fc")
	private BigDecimal discLoadFc;
	
	@Column(name = "disc_load_lc")
	private BigDecimal discLoadLc;
	
	@Column(name = "cover_rec_type")
	private String coverRecType;
	
	@Column(name = "status")
	private String status;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "created_date")
	private Date createdDate;
	
	@Column(name = "created_by")
	private String createdBy;
	
	@Column(name = "premia_status")
	private String premiaStatus;
	
	@Column(name = "premia_response")
	private String premiaResponse;
	
	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "updated_date")
	private Date updatedDate;
	
	@Column(name = "updated_by")
	private String updatedBy;

}
