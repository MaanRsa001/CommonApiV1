package com.maan.eway.bean;

import java.util.Date;

import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.DynamicUpdate;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@NoArgsConstructor
@AllArgsConstructor
@ToString
@Entity
@DynamicInsert
@DynamicUpdate
@Data
@Table(name="eway_module_master")
public class ModuleMaster {

	@Id
	@Column(name = "MODULE_ID", nullable = false)
	private Integer moduleId;

	@Column(name = "USERTYPE", length = 100, nullable = false)
	private String usertype;

	@Column(name = "COMPANY_ID", length = 50, nullable = false)
	private String companyId;

	@Column(name = "MODULE_NAME", length = 300)
	private String moduleName;

	@Column(name = "BRANCH_CODE", length = 10)
	private String branchCode;

	@Column(name = "STATUS", nullable = false, length = 1)
	private String status;

	@Column(name = "DISPLAY_ORDER")
	private Integer displayOrder;

	@Column(name = "DISPLAY_YN")
	private String displayYn;

	@Column(name = "CREATED_BY")
	private String createdBy;

	@Column(name = "ENTRY_DATE")
	private Date entryDate;

	@Column(name = "MODULE_NAME_LOCAL")
	private String moduleNameLocal;

	@Column(name = "MODULE_REMARKS")
	private String moduleRemarks;
	
	@Column(name = "MODULE_URL")
	private String moduleURL;

	@Column(name = "MODULE_LOGO")
	private String moduleLOGO;
}
