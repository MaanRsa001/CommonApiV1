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
@Builder
@ToString
@DynamicInsert
@DynamicUpdate
@IdClass(ErrorDescMasterId.class)
@Entity
@Table(name ="error_desc_master")
public class ErrorDescMaster implements Serializable{
	
	private static final long serialVersionUID = 1L;
	
	@Id
	@Column(name="ERROR_CODE")
	private String errorCode;
	
	@Id
	@Column(name="COMPANY_ID")
	private String companyId;
	
	@Id
	@Column(name="BRANCH_CODE")
	private String branchCode;
	
	@Id
	@Column(name="PRODUCT_ID")
	private Integer productId;
	
	@Id
	@Column(name="MODULE_ID")
	private Integer moduleId;
	
	@Column(name="MODULE_NAME")
	private String moduleName;
	
	@Id
	@Column(name="AMEND_ID")
	private Integer amendId;
	
	@Column(name="ERROR_FIELD")
	private String errorField;
	
	@Column(name="ERROR_DESC")
	private String errorDesc;
	
	@Column(name="EFFECTIVE_DATE_END")
	private Date effectiveDateEnd;
	
	@Column(name="EFFECTIVE_DATE_START")
	private Date effectiveDateStart;
	
	@Column(name="STATUS")
	private String status;
	
	@Column(name="REMARKS")
	private String remarks;
	
	@Column(name="ENTRY_DATE")
	private Date entryDate;
	

	
	@Column(name="CREATED_BY")
	private String createdBy;
	
	@Column(name="UPDATED_BY")
	private String updatedBy;
	
	@Column(name="UPDATED_DATE")
	private Date updatedDate;
	
	@Column(name="LOCAL_LANGUAGE_DESC")
	private String localLanguageDesc;
	
	@Column(name="LANGUAGE")
	private String language;
	
	@Column(name="LOCAL_LANG_ERROR_FIELD")
	private String localLangErrorField;
	
}
