package com.maan.eway.master.res;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Data;

@Data
public class PremiaConfigMasterRes implements Serializable {

    private static final long serialVersionUID = 1L;

    @JsonProperty("PremiaId")
    private String premiaId ;
    
	@JsonProperty("PremiaTableName")
    private String premiaTableName  ;
    
	@JsonProperty("InsuranceId")
	private String companyId;
	
	@JsonProperty("BranchCode")
	private String branchCode;
	
	@JsonProperty("ProductId")
	private String productId;
	
	@JsonProperty("SectionId")
	private String sectionId;
	
	@JsonProperty("Status")
	private String status;
	
	@JsonProperty("EntityName")
	private String entityName;
	
	
	@JsonProperty("SourceTableName")
	private List<String> sourceTableName;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDateStart")
	private Date effectiveDateStart;
	
	@JsonProperty("CreatedBy")
	private String createdBy;
	
	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EffectiveDateEnd")
	private Date effectiveDateEnd;
	
	@JsonProperty("UpdatedBy")
	private String updatedBy;

	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("EntryDate")
	private Date entryDate;

	@JsonFormat(pattern="dd/MM/yyyy")
	@JsonProperty("UpdatedDate")
	private Date updatedDate;

	@JsonProperty("Remarks")
	private String remarks;

	@JsonProperty("AmendId")
	private String amendId;

	@JsonProperty("QueryKey")
	private String queryKey;
	
}
